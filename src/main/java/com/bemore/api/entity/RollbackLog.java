package com.bemore.api.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

 /**
 * @author : http://www.chiner.pro
 * @date : 2022-1-16
 * @desc : 回溯税收数据临时记录表
 */
@TableName(value = "t_rollback_log")
@Data
@ApiModel(value = "新增有效户表")
@Entity
@Table(name = "t_rollback_log")
public class RollbackLog implements Serializable,Cloneable{
    /** 主键 */
    @Id
    @TableId(type = IdType.ASSIGN_UUID)
    @Column(length = 36)
    private String id ;
    /** 所属日期 */
    private String date ;
    /** 所属年份 */
    private Integer year ;
    /** 所属月份 */
    private Integer month ;
}