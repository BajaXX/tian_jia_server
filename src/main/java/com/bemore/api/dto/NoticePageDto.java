package com.bemore.api.dto;

import com.bemore.api.entity.MeetingRoom;
import com.bemore.api.entity.Notice;
import com.bemore.api.entity.request.NoticeParam;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NoticePageDto {

	private List<NoticeParam> notices;
	private long totalSize;
	private int currentPage;
	private int totalPage;
	
}
