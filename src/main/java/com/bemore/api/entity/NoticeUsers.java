package com.bemore.api.entity;

import com.bemore.api.util.LongToTimestampConvertor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Data
@Table(name = "t_notice_users")
public class NoticeUsers {
  @Id
  @GeneratedValue(generator = "idGenerator")
  @GenericGenerator(name = "idGenerator", strategy = "uuid")
  private String id;
//  private String nid;
  private String userId;
  @Convert(converter = LongToTimestampConvertor.class)
  @Column(name = "issue_time")
  private long issueTime;
  private Integer mstatus;

  @JsonIgnoreProperties(value = { "notice" })
  @ManyToOne(cascade={CascadeType.MERGE,CascadeType.REFRESH},optional=false,fetch = FetchType.LAZY)//可选属性optional=false,表示author不能为空。删除文章，不影响用户
  @JoinColumn(name="nid",referencedColumnName="id")//设置在article表中的关联字段(外键)
  private Notice notice;




}
