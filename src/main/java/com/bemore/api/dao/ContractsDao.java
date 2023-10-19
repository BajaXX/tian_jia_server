package com.bemore.api.dao;

import com.bemore.api.entity.Contracts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContractsDao extends JpaRepository<Contracts, String>, JpaSpecificationExecutor<Contracts> {



}
