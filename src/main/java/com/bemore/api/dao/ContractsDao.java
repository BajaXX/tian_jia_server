package com.bemore.api.dao;

import com.bemore.api.entity.Contracts;
import com.bemore.api.entity.SupportContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContractsDao extends JpaRepository<Contracts, String>, JpaSpecificationExecutor<Contracts> {

    List<Contracts> findAllByContractNameLike(@Param("contractName") String contractName);

}
