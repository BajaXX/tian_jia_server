package com.bemore.api.dto;

import com.bemore.api.entity.Person;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PersonDto extends Person {

    // 是否新股东
    private Integer isStock;
    // 是否新监事
    private Integer isSupervisor;
    // 是否新董事
    private Integer isDirector;
    // 是否老股东
    private Integer isOldStock;
    // 是否老监事
    private Integer isOldSupervisor;
    // 是否老董事
    private Integer isOldDirector;
    // 原有出资方式
    private String oldPutType;
    // 出资方式
    private String putType;
    // 原有出资额
    private String putAmount;
    // 当前出资额
    private String oldPutAmount;
    // 实际出资额
    private String realPutAmount;
    // 原出资日期
    private String putDate;
    // 出资日期
    private String oldPutDate;
    // 实际出资日期
    private String realPutDate;
    // 监事选举方式
    private String supervisorType;
    // 董事选举方式
    private String directorType;

    /**
     * 合伙性质 1普通合伙 2有限合伙
     */
    private Integer partnerType;
}
