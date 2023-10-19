package com.bemore.api.entity;


import com.bemore.api.util.LongToTimestampConvertor;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "t_notice")
public class Notice {
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "uuid")
    @Column(name = "id")
    private String id;
    @Column(name = "title")
    private String title;
    @Column(name = "content")
    private String content;
    @Convert(converter = LongToTimestampConvertor.class)
    @Column(name = "issue_time")
    private long issueTime;

//    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
//    @JoinColumn(name = "nid",nullable = false,insertable = false,updatable = false)
    @OneToMany(targetEntity = NoticeUsers.class, mappedBy = "notice",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<NoticeUsers> usersList;

//    @OneToMany(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
//    @JoinColumn(name = "nid",nullable = false,insertable = false,updatable = false)
    @OneToMany(targetEntity = NoticeFiles.class,mappedBy = "notice",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<NoticeFiles> files;

}
