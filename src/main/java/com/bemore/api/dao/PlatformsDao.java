package com.bemore.api.dao;

import com.bemore.api.entity.Platforms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface PlatformsDao extends JpaRepository<Platforms, String>, JpaSpecificationExecutor<Platforms> {


    @Query(nativeQuery = true, value = "select * FROM t_platforms WHERE platform_name = :platformName LIMIT 1")
    Platforms findPlatformsByName(@Param("platformName") String platformName);

    @Query(nativeQuery = true, value = "select id FROM t_platforms WHERE is_base=1 LIMIT 1")
    Platforms findBasePlatform();

    @Transactional
    @Modifying
    @Query(value = "UPDATE Platforms SET isBase=1 WHERE id=:id")
    void setBasePlatform(@Param("id") String id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Platforms SET isBase=0")
    void resetBasePlatform();
}
