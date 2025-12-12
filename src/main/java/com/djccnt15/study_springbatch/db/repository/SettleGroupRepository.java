package com.djccnt15.study_springbatch.db.repository;

import com.djccnt15.study_springbatch.db.model.SettleGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;


public interface SettleGroupRepository extends JpaRepository<SettleGroupEntity, Long> {

    @Query(value = """
        SELECT new SettleGroupEntity(detail.customerId, detail.serviceId, sum(detail.count), sum(detail.fee))
        FROM SettleDetailEntity detail
        WHERE 1=1
            AND detail.targetDate between :start and :end
            AND detail.customerId = :customerId
        GROUP BY detail.customerId, detail.serviceId
        """)
    List<SettleGroupEntity> findGroupByCustomerIdAndServiceId(LocalDate start, LocalDate end, Long customerId);
}
