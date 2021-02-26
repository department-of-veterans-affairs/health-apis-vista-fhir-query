package gov.va.api.health.vistafhirquery.service.controller.observation;

import gov.va.api.health.fhir.api.FhirDateTimeParameter;
import lombok.Getter;

import java.time.Instant;
import java.util.Optional;

class StartStop {
        @Getter
        Optional<String> start;
        @Getter
        Optional<String> stop;
        private final FhirDateTimeParameter date1;
        private final FhirDateTimeParameter date2;

        StartStop(FhirDateTimeParameter d1, FhirDateTimeParameter d2) {
                date1 = d1;
                date2 = d2;

                if (date1 != null) {
                                computeStartStop();
                }
                start=Optional.empty();
                stop=Optional.empty();
        }

        private void computeStartStop() {
                switch (date1.prefix()) {
                        case EQ:eqDate1();
                                break;
                        case SA: // fall-through
                        case GT:gtDate1();
                                break;
                        case EB: // fall-through
                        case LT:ltDate1();
                                break;
                        case GE:geDate1();
                                break;
                        case LE:leDate1();
                                break;
                        case AP:
                                throw new UnsupportedOperationException("AP search prefix not implemented");
                        default:
                                throw new IllegalArgumentException("Unknown search prefix: " + date1.prefix());
                }
        }

        private void leDate1() {
                if (date2 == null) {
                        stop=toOptionalString(date1.upperBound());
                }
                else {
                        switch (date2.prefix()) {

                        }
                }
        }

        private void geDate1() {
                if (date2 == null) {
                        start=toOptionalString(date1.lowerBound());
                }
                else {
                        switch (date2.prefix()) {

                        }
                }
        }

        private void ltDate1() {
                if (date2 == null) {
                        stop=toOptionalString(date1.lowerBound());
                }
                else {
                        switch (date2.prefix()){

                        }
                }
        }

        private void gtDate1() {
                if (date2 == null) {
                        start=toOptionalString(date1.upperBound());
                }
                else {
                        switch (date2.prefix()){
                                case EQ:gtDate1EqDate2();
                                        break;
                                case SA: // fall-through
                                case GT:gtDate1GtDate2();
                                        break;
                                case EB: // fall-through
                                case LT:gtDate1LtDate2();
                                        break;
                                case GE:gtDate1GeDate2();
                                        break;
                                case LE:gtDate1LeDate2();
                                        break;
                                case AP:
                                        throw new UnsupportedOperationException("AP search prefix not implemented");
                                default:
                                        throw new IllegalArgumentException("Unknown search prefix: " + date2.prefix());
                        }
                }
        }

        private void gtDate1LeDate2() {
                if (date1.upperBound().isBefore(date2.upperBound())){
                        start=toOptionalString(date1.upperBound());
                        stop=toOptionalString(date2.upperBound());
                }
                else {
                        nope();
                }
        }

        private void gtDate1LtDate2() {
                if (date1.upperBound().isBefore(date2.lowerBound())){
                        start=toOptionalString(date1.upperBound());
                        stop=toOptionalString(date2.lowerBound());
                }
                else {
                        nope();
                }
        }

        private void gtDate1GeDate2() {
                start=toOptionalString(maxInstant(date1.upperBound(),date2.lowerBound()));
        }

        private void gtDate1GtDate2() {
                start=toOptionalString(maxInstant(date1.upperBound(),date2.upperBound()));
        }

        private Instant maxInstant(Instant a, Instant b){
               return a.isAfter(b) ? a : b;
        }

        private void gtDate1EqDate2() {
                if (date1.upperBound().isBefore(date2.lowerBound())){
                        start=toOptionalString(date2.lowerBound());
                        stop=toOptionalString(date2.upperBound());
                }
                else {
                        nope();
                }
        }

        private void eqDate1() {
                if (date2 == null) {
                        start=toOptionalString(date1.lowerBound());
                        stop=toOptionalString(date1.upperBound());
                }
                else {
                        switch (date2.prefix()){
                                case EQ:eqDate1EqDate2();
                                        break;
                                case SA: // fall-through
                                case GT:eqDate1GtDate2();
                                        break;
                                case EB: // fall-through
                                case LT:eqDate1LtDate2();
                                        break;
                                case GE:eqDate1GeDate2();
                                        break;
                                case LE:eqDate1LeDate2();
                                        break;
                                case AP:
                                        throw new UnsupportedOperationException("AP search prefix not implemented");
                                default:
                                        throw new IllegalArgumentException("Unknown search prefix: " + date2.prefix());
                        }
                }

        }

        private void eqDate1LeDate2() {
                if (!date1.lowerBound().isAfter(date2.upperBound())){
                        start=toOptionalString(date1.lowerBound());
                        stop=toOptionalString(date1.upperBound());
                }
                else {
                        nope();
                }
        }

        private void eqDate1GeDate2() {
                if (!date1.lowerBound().isBefore(date2.upperBound())){
                        start=toOptionalString(date1.lowerBound());
                        stop=toOptionalString(date1.upperBound());
                }
                else {
                        nope();
                }
        }

        private void eqDate1LtDate2() {
                if (date1.lowerBound().isBefore(date2.upperBound())){
                        start=toOptionalString(date1.lowerBound());
                        stop=toOptionalString(date1.upperBound());
                }
                else {
                        nope();
                }
        }

        private void eqDate1GtDate2() {
                if (date1.lowerBound().isAfter(date2.upperBound())){
                        start=toOptionalString(date1.lowerBound());
                        stop=toOptionalString(date1.upperBound());
                }
                else {
                 nope();
                }
        }

        private void eqDate1EqDate2() {
                nope();
        }

        //TODO: Web exception handler goodness with bad request (400)
        private void nope() {
                throw new IllegalArgumentException("Bad search combination");
        }

        private Optional<String> toOptionalString(Instant i){
                if (i==null){
                        return Optional.empty();
                }
                return Optional.of(i.toString());
        }
}
