package com.bemore.api.dao;

import com.bemore.api.entity.SupportContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface SupportContractDao extends JpaRepository<SupportContract, String>, JpaSpecificationExecutor<SupportContract> {


    List<SupportContract> findAllByEnterpriseNameLikeOrderByStatusAscStartDateAsc(@Param("enterpriseName") String enterpriseName);

    @Query("from SupportContract WHERE enterpriseName=:enterpriseName AND status=1 AND :date BETWEEN startDate AND endDate")
    List<SupportContract> findByEnterpriseNameAndDate(@Param("enterpriseName") String enterpriseName, @Param("date") int date);

    @Query("SELECT DISTINCT enterpriseName from SupportContract")
    List<String> getSupportEnterpriseList();

    @Query("SELECT DISTINCT enterpriseName from SupportContract WHERE platformId=:platformId AND status=1 AND :curDate BETWEEN startDate AND endDate")
    List<String> getEnterpriseByPlatformsId(@Param("platformId") String platformId, @Param("curDate") int curDate);

    @Query("SELECT DISTINCT enterpriseName from SupportContract where enterpriseName like :keyword")
    List<String> getSupportEnterpriseByName(@Param("keyword") String keyword);


    @Query("SELECT count(1) from SupportContract WHERE enterpriseName=:enterpriseName AND status=1 AND (:startDate BETWEEN startDate AND endDate OR :endDate BETWEEN startDate AND endDate)")
    int getContractCountByDate(@Param("enterpriseName") String enterpriseName, @Param("startDate") int startDate, @Param("endDate") int endDate);


    @Transactional
    @Modifying
    @Query("update SupportContract set status=:status ,cancelDate=:cancelDate WHERE id=:id")
    int updateStatus(@Param("id") String id, @Param("status") int status, @Param("cancelDate") int cancelDate);





}
