package com.bemore.api.entity;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "m_files")
public class Files {

	@Id
	@GeneratedValue(generator="idGenerator")
	@GenericGenerator(name="idGenerator", strategy="uuid")
	@Column(length = 36)
	private String id;	
	// 所处流程(1新开，2迁入，3变更，4注销)
	private Integer type;
	// 企业类型(1个独，2有限，3合伙)
	private Integer enterpriseType;
	// 文件名
	private String name;
	
}
