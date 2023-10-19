package com.bemore.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bemore.api.util.LongToTimestampConvertor;
import com.bemore.api.util.Timestamp2LongHandler;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.type.JdbcType;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Data
@ApiModel(value = "会议室预定表")
@ToString
@TableName(value = "t_meeting_room_use_log")
@Table(name = "t_meeting_room_use_log")

public class MeetingRoomUseLog {
  @Id
  @GeneratedValue(generator="idGenerator")
  @GenericGenerator(name="idGenerator", strategy="uuid")
  private String id;
  private String meetingRoomId;
  private String meetingRoomName;
  private String meetingRoomAddress;
  @Convert(converter = LongToTimestampConvertor.class)
  @Column(name = "start_time")
  private long startTime;
  @Convert(converter = LongToTimestampConvertor.class)
  @Column(name = "end_time")
  private long endTime;
  private String organizer;
  private String mobile;
  private long members;
  private long lunch;
  private long lunchType;
  private int status;




}
