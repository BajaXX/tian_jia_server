package com.bemore.api.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.bemore.api.entity.MeetingRoom;

public interface MeetingRoomDao extends JpaRepository<MeetingRoom, String>, JpaSpecificationExecutor<MeetingRoom>  {
		
}
