package com.bemore.api.dao;

import com.bemore.api.entity.EnterpriseLog;
import com.bemore.api.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface NoticeDao extends JpaRepository<Notice, String>, JpaSpecificationExecutor<Notice> {


	
}
