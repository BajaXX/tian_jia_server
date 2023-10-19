package com.bemore.api.controller;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bemore.api.dao.FilesDao;
import com.bemore.api.entity.Files;
import com.bemore.api.util.GsonUtil;

@RestController
@RequestMapping("/setting")
public class SettingController {

	@Autowired
	private FilesDao filesDao;
	
	@PostMapping("/newFile")
	public String newFile(@RequestBody Files files) {		
		filesDao.save(files);
		return GsonUtil.build();
	}
	
	@PostMapping("/fetchFiles")
	public String fetchFiles(@RequestParam int type, @RequestParam int enterpriseType) {
		List<Files> files = filesDao.findAll(new Specification<Files>(){
			@Override
            public Predicate toPredicate(Root<Files> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();
				
				if(type > 0){
					list.add(cb.equal(root.<Integer>get("type"), type));
				}
				if(enterpriseType > 0){
					list.add(cb.equal(root.<Integer>get("enterpriseType"), enterpriseType));
				}
				
				Predicate[] p =	new Predicate[list.size()];
				return cb.and(list.toArray(p));				
			}
		});		
		return GsonUtil.build(files);
	}
}
