package com.bemore.api.dao;

import com.bemore.api.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.transaction.Transactional;

/**
 * @author jackie
 */
public interface ProjectDao extends JpaRepository<Project, String>, JpaSpecificationExecutor<Project> {

    @Transactional
    int deleteByEnterpriseId(String enterpriseId);

    Project findByEnterpriseId(String enterpriseId);
}
