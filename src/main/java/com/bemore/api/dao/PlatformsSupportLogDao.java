package com.bemore.api.dao;

import com.bemore.api.entity.TPlatformsSupportLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface PlatformsSupportLogDao extends JpaRepository<TPlatformsSupportLog, String>, JpaSpecificationExecutor<TPlatformsSupportLog> {


    @Query(nativeQuery = true, value = "SELECT support_areas,support_project, SUM(platform_month_amount) AS total_amount FROM t_platforms_support_log where date=:date and support_areas is not null and support_areas is not null and support_areas<>'' GROUP BY support_areas,support_project")
    List<Object[]> getSupportMonthByDate(int date);

    List<TPlatformsSupportLog> findAllByPlatformIdAndDateBetweenOrderByDate(@Param("platformsId") String platformsId, int startDate, int endDate);
    List<TPlatformsSupportLog> findAllByPlatformIdAndYearAndDateLessThanEqual(@Param("platformsId") String platformsId,@Param("year") int year, int endDate);
    List<TPlatformsSupportLog> findAllByPlatformIdAndYearAndDateLessThan(@Param("platformsId") String platformsId,@Param("year") int year, int endDate);
    List<TPlatformsSupportLog> findAllByDateBetweenOrderByDate(int startDate, int endDate);

    @Transactional
    void deleteByPlatformIdAndDateBetween(@Param("platformsId") String platformsId, int startDate, int endDate);


}
