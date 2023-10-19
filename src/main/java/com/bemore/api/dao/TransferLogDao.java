package com.bemore.api.dao;

import com.bemore.api.entity.TransferLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.transaction.Transactional;
import java.util.List;


public interface TransferLogDao extends JpaRepository<TransferLog, String>, JpaSpecificationExecutor<TransferLog> {

    @Transactional
    int deleteByEnterpriseId(String enterpriseId);


    List<TransferLog> findTransferLogsByEnterpriseId(String enterpriseId);

    List<TransferLog> findTransferLogsByEnterpriseIdAndTransType(String enterpriseId,Integer transType);


}
