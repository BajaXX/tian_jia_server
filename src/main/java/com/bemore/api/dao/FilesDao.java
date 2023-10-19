package com.bemore.api.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.bemore.api.entity.Files;

public interface FilesDao extends JpaRepository<Files, String>, JpaSpecificationExecutor<Files> {

	List<Files> findByEnterpriseTypeAndType(Integer enterpriseType, Integer type);
	
}
