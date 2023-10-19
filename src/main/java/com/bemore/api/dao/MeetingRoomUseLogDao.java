package com.bemore.api.dao;

import com.bemore.api.entity.MeetingRoom;
import com.bemore.api.entity.MeetingRoomUseLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MeetingRoomUseLogDao extends JpaRepository<MeetingRoomUseLog, String>, JpaSpecificationExecutor<MeetingRoomUseLog>  {
		
}
