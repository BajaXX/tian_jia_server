package com.bemore.api.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;
@Entity
@TableName("t_enterprise_addrs")
@Data
public class EnterpriseAddr {
    @Id
    private Integer id;
    private String addr;
    private Timestamp createTime;

}
