package com.bemore.api.dao;

import com.bemore.api.entity.ContractRules;
import com.bemore.api.entity.Tax;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContractRulesDao extends JpaRepository<ContractRules, String>, JpaSpecificationExecutor<ContractRules> {

    List<ContractRules> findAllByContractIdOrderByTaxStart(String contractId);

    @Query("SELECT count(1) from ContractRules WHERE contractId=:contractId AND ((taxStart <= :taxStart AND taxEnd>:taxStart) OR (taxStart <= :taxEnd AND taxEnd>:taxEnd))")
    int getContractCountByTax(@Param("contractId") String contractId, @Param("taxStart") long taxStart, @Param("taxEnd") long taxEnd);


}
