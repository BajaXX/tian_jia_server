package com.bemore.api.dao;

import com.bemore.api.entity.Platforms;
import com.bemore.api.entity.PlatformsContract;
import com.bemore.api.entity.response.PlatformsContractView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface PlatformsContractDao extends JpaRepository<PlatformsContract, String>, JpaSpecificationExecutor<PlatformsContract> {

    @Query("SELECT count(1) from PlatformsContract WHERE platformId=:platformId AND cstatus=1 AND isFund=:isFund AND (:contractStart BETWEEN agreementStart AND agreementEnd OR :contractEnd BETWEEN agreementStart AND agreementEnd)")
    int getContractCountByTax(@Param("platformId") String platformId, @Param("isFund") int isFund, @Param("contractStart") int contractStart, @Param("contractEnd") int contractEnd);

    List<PlatformsContract> findAllPlatformsContractByPlatformIdOrderByCstatusAscAgreementStart(@Param("platformId") String platformId);

    List<PlatformsContract> findPlatformsContractsByPlatformIdAndIsFundAndAgreementStartLessThanEqualAndAgreementEndGreaterThanEqual(@Param("platformId") String platformId, @Param("isfund") int isfund, @Param("agreementStart") int agreementStart, @Param("agreementEnd") int agreementEnd);

    @Query("FROM PlatformsContract WHERE platformId=:platformId AND isFund= :isFund AND cstatus=1 AND :curDate BETWEEN agreementStart AND agreementEnd AND taxStart<= :tax AND taxEnd> :tax")
    PlatformsContract getPlatformsContractByPidTax(@Param("platformId") String platformId, @Param("tax") double tax, @Param("isFund") int isFund, @Param("isFund") int curDate);


    @Transactional
    @Modifying
    @Query("update PlatformsContract set cstatus=:cstatus ,cancelDate=:cancelDate WHERE id=:id")
    int updateCstatus(@Param("id") String id, @Param("cstatus") int cstatus, @Param("cancelDate") int cancelDate);


}
