package com.bemore.api.dto;

import com.bemore.api.entity.Enterprise;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EnterprisePageDto {

	private List<Enterprise> enterprises;
	private int totalNumber;

}
