package gov.va.api.health.vistafhirquery.service.controller.observation;

import gov.va.api.health.autoconfig.logging.Loggable;
import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/** Repository for interacting with the VitalVuidMapping table. */
@Loggable
@Transactional(isolation = Isolation.READ_UNCOMMITTED)
public interface VitalVuidMappingRepository
    extends JpaSpecificationExecutor<VitalVuidMappingEntity>,
        PagingAndSortingRepository<VitalVuidMappingEntity, String> {
  List<VitalVuidMappingEntity> findByCodingSystemId(Short codingSystemId);
}
