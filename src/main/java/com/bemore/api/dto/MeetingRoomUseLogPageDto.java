package com.bemore.api.dto;

import com.bemore.api.entity.MeetingRoom;
import com.bemore.api.entity.MeetingRoomUseLog;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MeetingRoomUseLogPageDto {

	private List<MeetingRoomUseLog> meetingRoomUseLogs;
	private long totalSize;
	private int currentPage;
	private int totalPage;
	
}
