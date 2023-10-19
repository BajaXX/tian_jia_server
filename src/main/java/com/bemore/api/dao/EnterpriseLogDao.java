package com.bemore.api.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.bemore.api.entity.EnterpriseLog;

public interface EnterpriseLogDao extends JpaRepository<EnterpriseLog, String>, JpaSpecificationExecutor<EnterpriseLog> {

	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "delete from t_enterprise_log where enterprise_id = ?1 and valid='0'")
	int deleteByEnterprise(String enterprise);
	
	@Transactional
	@Modifying
	@Query(nativeQuery = true, value = "update t_enterprise_log set valid='1' where enterprise_id = ?1")
	int validLog(String enterprise);

	EnterpriseLog findByEnterpriseId(String enterpriseId);
	
	List<EnterpriseLog> findByEnterpriseIdAndValid(String enterpriseId, String valid);
	
}
