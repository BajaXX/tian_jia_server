package com.bemore.api.dto;

import java.util.List;

import com.bemore.api.entity.Tax;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnterpriseTaxPage {

	private List<Tax> taxs;
	private long totalNumber;
	private int currentPage;
	private int totalPage;	
	
}
