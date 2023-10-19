package com.bemore.api.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.bemore.api.entity.Tax;


public interface TaxDao extends JpaRepository<Tax, String>, JpaSpecificationExecutor<Tax> {

	List<Tax> findAllByYearAndMonthLessThanEqualAndMonthGreaterThanEqual(int year, int endMonth, int startMonth);
	
	List<Tax> findByEnterpriseName(String enterpriseName);

	List<Tax> findByEnterpriseNameAndDate(String enterpriseName, String date);

	List<Tax> findByDate(String date);

	List<Tax> findByYearAndMonth(int year, int month);
}
