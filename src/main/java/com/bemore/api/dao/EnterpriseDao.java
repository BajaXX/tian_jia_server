package com.bemore.api.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.bemore.api.entity.Enterprise;

public interface EnterpriseDao extends JpaRepository<Enterprise, String>, JpaSpecificationExecutor<Enterprise> {

    int countAllByProcess(Integer process);
    Enterprise findByName(String name);

}
