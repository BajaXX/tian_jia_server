package com.bemore.api.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.bemore.api.entity.Person;

public interface PersonDao  extends JpaRepository<Person, String>, JpaSpecificationExecutor<Person>  {

	Person findByEnterpriseIdAndIsFinance(String enterpriseId,Integer isFinance);

	Person findByEnterpriseIdAndIsMaster(String enterpriseId,Integer isMaster);

	Person findByEnterpriseIdAndIsContact(String enterpriseId,Integer sContact);
	
	@Transactional
	@Modifying
	@Query(nativeQuery = true ,value = "delete from t_enterprise_person where enterprise_id = ?1")
	int deleteByEnterprise(String enterprise);
	
	List<Person> findByEnterpriseId(String enterpriseId);
	
}
