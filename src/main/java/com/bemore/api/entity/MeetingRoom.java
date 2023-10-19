package com.bemore.api.entity;

import javax.persistence.*;

import com.baomidou.mybatisplus.annotation.TableName;
import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "t_meeting_room")
public class MeetingRoom {

	@Id
	@GeneratedValue(generator="idGenerator")
	@GenericGenerator(name="idGenerator", strategy="uuid")
	private String id;
	private String name;
	private String address;
	private Integer size;
	private Integer network;
	private String resource;
	private Integer status;
	private String roomPic;

}
