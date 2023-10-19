package com.bemore.api.dao;

import com.bemore.api.entity.EnterpriseSupportLog;
import com.bemore.api.entity.response.PlatformSupportDataView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface EnterpriseSupportLogDao extends JpaRepository<EnterpriseSupportLog, String>, JpaSpecificationExecutor<EnterpriseSupportLog> {


    List<EnterpriseSupportLog> findByEnterpriseNameAndSupportContractIdAndDate(String enterpriseName, String supportContractId, int date);

    @Transactional
    void deleteEnterpriseSupportLogsByEnterpriseNameAndYearAndSupportId(@Param("enterpriseName") String enterpriseName, @Param("year") int year, @Param("supportId") String supportId);

    @Transactional
    void deleteEnterpriseSupportLogsByYear(@Param("year") int year);

    List<EnterpriseSupportLog> findByPlatformIdAndYear(@Param("platformId") String platformId, @Param("year") int year);

    List<EnterpriseSupportLog> findByPlatformIdAndYearAndDateLessThanEqual(@Param("platformId") String platformId, @Param("year") int year,int endDate);

    List<EnterpriseSupportLog> findByEnterpriseNameAndYearAndDateLessThanEqual(@Param("enterpriseName") String enterpriseName, @Param("year") int year,int endDate);

    List<EnterpriseSupportLog> findByEnterpriseNameAndSupportContractIdAndYearAndDateLessThan(@Param("enterpriseName") String enterpriseName, @Param("supportContractId") String supportContractId, @Param("year") int year, @Param("date") int date);

    List<EnterpriseSupportLog> findByPlatformIdAndDate(@Param("platformId") String platformId, @Param("date") int date);

    List<EnterpriseSupportLog> findByEnterpriseNameLikeAndDateGreaterThanEqualAndDateLessThanEqualOrderByDate(String enterpriseName, int startDate, int endDate);

    List<EnterpriseSupportLog> findByDateGreaterThanEqualAndDateLessThanEqualOrderByDate(int startDate, int endDate);

    @Query(nativeQuery = true, value = "select platform_id,platform_name,sum(platform_support_amount) as platformSupportAmount,sum(platform_month_amount) as platformMonthAmount,sum(platform_surplus) as platformSurplus,date FROM t_enterprise_support_log WHERE platform_id is not null AND platform_id!='' AND date between :startDate AND :endDate group by platform_id,date")
    List<String[]> getPlatformSupportList(int startDate, int endDate);

    @Query(nativeQuery = true, value = "select platform_id,platform_name,sum(platform_support_amount) as platformSupportAmount,sum(platform_month_amount) as platformMonthAmount,sum(platform_surplus) as platformSurplus,date FROM t_enterprise_support_log WHERE platform_id = :platformId AND date between :startDate AND :endDate group by platform_id,date")
    List<String[]> getPlatformSupportListByPlatformId(String platformId, int startDate, int endDate);


    List<EnterpriseSupportLog> findByPlatformIdAndDateGreaterThanEqualAndDateLessThanEqual(String platformId, int startDate, int endDate);


}
