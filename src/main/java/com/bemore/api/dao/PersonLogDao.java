package com.bemore.api.dao;

import java.util.List;

import javax.transaction.Transactional;

import com.bemore.api.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.bemore.api.entity.PersonLog;

public interface PersonLogDao extends JpaRepository<PersonLog, String>, JpaSpecificationExecutor<PersonLog> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "delete from t_enterprise_person_log where enterprise_id = ?1 and valid='0'")
    int deleteByEnterprise(String enterprise);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update t_enterprise_person_log set valid='1' where enterprise_id = ?1")
    int validLog(String enterprise);

    List<PersonLog> findByEnterpriseIdAndValid(String enterpriseId, String valid);

    List<PersonLog> findByEnterpriseId(String enterpriseId);

    PersonLog findByEnterpriseIdAndIsMasterAndValid(String enterpriseId, Integer isMaster, String valid);

}
