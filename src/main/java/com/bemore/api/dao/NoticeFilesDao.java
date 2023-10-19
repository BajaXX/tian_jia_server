package com.bemore.api.dao;

import com.bemore.api.entity.Notice;
import com.bemore.api.entity.NoticeFiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NoticeFilesDao extends JpaRepository<NoticeFiles, String>, JpaSpecificationExecutor<NoticeFiles> {


	
}
