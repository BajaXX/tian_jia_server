package com.bemore.api.dao;

import com.bemore.api.entity.ProjectLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author jackie
 */
public interface ProjectLogDao extends JpaRepository<ProjectLog,String>, JpaSpecificationExecutor<ProjectLog> {


    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update t_enterprise_project_log set valid='1' where enterprise_id = ?1")
    int validLog(String enterprise);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from t_enterprise_project_log where enterprise_id = ?1 and valid='0'")
    int deleteByEnterprise(String enterpriseId);

    List<ProjectLog> findByEnterpriseIdAndValid(String enterpriseId, String valid);
}
