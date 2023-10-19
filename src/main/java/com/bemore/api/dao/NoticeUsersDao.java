package com.bemore.api.dao;

import com.bemore.api.entity.NoticeUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoticeUsersDao extends JpaRepository<NoticeUsers, String>, JpaSpecificationExecutor<NoticeUsers> {

    @Query(" select count(1) from NoticeUsers t where userId = :userId and mstatus=0")
    Integer countUnreadNotice(@Param("userId") String userId);

//    @Query(" select count(1) from NoticeUsers t where enterpriseName = :enterpriseName and mstatus=0")
//    List<String> getEnterpriseUserId(@Param("enterpriseName") String enterpriseName);


}
