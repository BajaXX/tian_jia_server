package com.bemore.api.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Data
@Table(name = "t_notice_files")
public class NoticeFiles {
  @Id
  @GeneratedValue(generator = "idGenerator")
  @GenericGenerator(name = "idGenerator", strategy = "uuid")
  private String fileid;
//  private String nid;
  private String fileName;
  private long fileSize;
  private String fileUrl;

  @JsonIgnoreProperties(value = { "notice" })
  @ManyToOne(cascade={CascadeType.MERGE,CascadeType.REFRESH},optional=false,fetch = FetchType.LAZY)//可选属性optional=false,表示author不能为空。删除文章，不影响用户
  @JoinColumn(name="nid",referencedColumnName="id")//设置在article表中的关联字段(外键)
  private Notice notice;




}
