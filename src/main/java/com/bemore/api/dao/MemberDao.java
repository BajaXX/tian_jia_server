package com.bemore.api.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.bemore.api.entity.Member;

public interface MemberDao extends JpaRepository<Member, String>, JpaSpecificationExecutor<Member> {

	List<Member> findMembersByEnterpriseIdAndIsStockEquals(String enterpriseId,Integer isStock);
	
	@Transactional
	@Modifying
	@Query(nativeQuery = true ,value = "delete from t_enterprise_member where enterprise_id = ?1")
	int deleteByEnterprise(String enterprise);
	
	List<Member> findByEnterpriseId(String enterpriseId);
	
}
