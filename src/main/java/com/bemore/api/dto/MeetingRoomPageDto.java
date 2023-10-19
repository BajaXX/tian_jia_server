package com.bemore.api.dto;

import java.util.List;

import com.bemore.api.entity.MeetingRoom;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetingRoomPageDto {

	private List<MeetingRoom> meetingRooms;
	private long totalSize;
	private int currentPage;
	private int totalPage;
	
}
