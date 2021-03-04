#!/usr/bin/env bash

usage() {
  cat <<EOF

$0 [options]

Generate configurations for local development.

Options
     --debug               Enable debugging
 -h, --help                Print this help and exit.
     --secrets-conf <file> The configuration file with secrets!

Secrets Configuration
 This bash file is sourced and expected to set the following variables
 - VISTALINK_URL
 - VISTALINK_ACCESS_CODE
 - VISTALINK_VERIFY_CODE

 Variables that can be used for optional configurations:
 - VISTALINK_CLIENT_KEY
 - WEB_EXCEPTION_KEY

$1
EOF
  exit 1
}

main() {
  REPO=$(cd $(dirname $0)/../.. && pwd)
  SECRETS="$REPO/secrets.conf"
  PROFILE=dev
  MARKER=$(date +%s)
  ARGS=$(getopt -n $(basename ${0}) \
      -l "debug,help,secrets-conf:" \
      -o "h" -- "$@")
  [ $? != 0 ] && usage
  eval set -- "$ARGS"
  while true
  do
    case "$1" in
      --debug) set -x ;;
      -h|--help) usage "halp! what this do?" ;;
      --secrets-conf) SECRETS="$2" ;;
      --) shift;break ;;
    esac
    shift;
  done

  echo "Loading secrets: $SECRETS"
  [ ! -f "$SECRETS" ] && usage "File not found: $SECRETS"
  . $SECRETS

  # Support values as configured in Shanktosecrets
  if [ -z "${VISTALINK_ACCESS_CODE:-}" ]; then export VISTALINK_ACCESS_CODE=${VISTA_ACCESS_CODE:-}; fi
  if [ -z "${VISTALINK_VERIFY_CODE:-}" ]; then export VISTALINK_VERIFY_CODE=${VISTA_VERIFY_CODE:-}; fi

  MISSING_SECRETS=false
  requiredParam VISTALINK_URL "$VISTALINK_URL"
  requiredParam VISTALINK_ACCESS_CODE "$VISTALINK_ACCESS_CODE"
  requiredParam VISTALINK_VERIFY_CODE "$VISTALINK_VERIFY_CODE"
  requiredParam VFQ_DB_URL "${VFQ_DB_URL}"
  requiredParam VFQ_DB_USER "${VFQ_DB_USER}"
  requiredParam VFQ_DB_PASSWORD "${VFQ_DB_PASSWORD}"
  [ -z "$VISTALINK_CLIENT_KEY" ] && VISTALINK_CLIENT_KEY="not-used"
  [ -z "$WEB_EXCEPTION_KEY" ] && WEB_EXCEPTION_KEY="-shanktopus-for-the-win-"
  [ $MISSING_SECRETS == true ] && usage "Missing configuration secrets, please update $SECRETS"

  populateConfig
}

# =====================================================================

checkForUnsetValues() {
  local project="$1"
  local profile="$2"
  local target="$REPO/$project/config/application-${profile}.properties"
  echo "checking $target"
  grep -E '(.*= *unset)' "$target"
  [ $? == 0 ] && echo "Failed to populate all unset values" && exit 1
  diff $target $target.$MARKER
  [ $? == 0 ] && rm -v $target.$MARKER
}

comment() {
  local project="$1"
  local profile="$2"
  local target="$REPO/$project/config/application-${profile}.properties"
  cat >> $target
}

addValue() {
  local project="$1"
  local profile="$2"
  local key="$3"
  local value="$4"
  local target="$REPO/$project/config/application-${profile}.properties"
  local escapedValue=$(echo $value | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')
  echo "$key=$escapedValue" >> $target
}

configValue() {
  local project="$1"
  local profile="$2"
  local key="$3"
  local value="$4"
  local target="$REPO/$project/config/application-${profile}.properties"
  local escapedValue=$(echo $value | sed -e 's/\\/\\\\/g; s/\//\\\//g; s/&/\\\&/g')
  sed -i "s/^$key=.*/$key=$escapedValue/" $target
}

makeConfig() {
  local project="$1"
  local profile="$2"
  local target="$REPO/$project/config/application-${profile}.properties"
  [ -f "$target" ] && mv -v $target $target.$MARKER
  grep -E '(.*= *unset)' "$REPO/$project/src/main/resources/application.properties" \
    > "$target"
}

populateConfig() {
  makeConfig vista-fhir-query $PROFILE
  configValue vista-fhir-query $PROFILE vistalink.api.url "$VISTALINK_URL"
  comment vista-fhir-query $PROFILE <<EOF
# To populate vistalink.api.client-key, use the VISTALINK_CLIENT_KEY value in
# the secrets files used with make-configs.sh
EOF
  configValue vista-fhir-query $PROFILE vistalink.api.client-key "$VISTALINK_CLIENT_KEY"
  configValue vista-fhir-query $PROFILE vistalink.api.access-code "$VISTALINK_ACCESS_CODE"
  configValue vista-fhir-query $PROFILE vistalink.api.verify-code "$VISTALINK_VERIFY_CODE"
  configValue vista-fhir-query $PROFILE vista-fhir-query.internal.client-keys "disabled"
  configValue vista-fhir-query $PROFILE vista-fhir-query.public-url "http://localhost:8095"
  configValue vista-fhir-query $PROFILE vista-fhir-query.public-r4-base-path "r4"
  addValue    vista-fhir-query $PROFILE vista-fhir-query.custom-r4-url-and-path.Patient "http://localhost:8090/data-query/r4"
  configValue vista-fhir-query $PROFILE vista-fhir-query.public-web-exception-key "$WEB_EXCEPTION_KEY"
  configValue vista-fhir-query $PROFILE identityservice.encodingKey fhir-query
  configValue vista-fhir-query $PROFILE identityservice.patientIdPattern "[0-9]+(V[0-9]{6})?"
  configValue vista-fhir-query $PROFILE spring.datasource.url "${VFQ_DB_URL}"
  configValue vista-fhir-query $PROFILE spring.datasource.username "${VFQ_DB_USER}"
  configValue vista-fhir-query $PROFILE spring.datasource.password "${VFQ_DB_PASSWORD}"

  addValue vista-fhir-query $PROFILE management.endpoints.web.exposure.include "health,info,i2"
  addValue vista-fhir-query $PROFILE alternate-patient-ids.enabled true
  addValue vista-fhir-query $PROFILE alternate-patient-ids.id.1011537977V693883 5000000347
  addValue vista-fhir-query $PROFILE alternate-patient-ids.id.32000225 195601
  addValue vista-fhir-query $PROFILE alternate-patient-ids.id.1017283148V813263 195604
  addValue vista-fhir-query $PROFILE alternate-patient-ids.id.5000335 195602
  addValue vista-fhir-query $PROFILE alternate-patient-ids.id.25000126 195603


  checkForUnsetValues vista-fhir-query $PROFILE
}

requiredParam() {
  local param="${1}"
  local value="${2:-}"
  if [ -z "$value" ]
  then
    usage "Missing Configuration: $param"
    MISSING_SECRETS=true
  fi
}

# =====================================================================

main "$@"
