package com.bemore.api.service;

import com.bemore.api.dao.EnterpriseDao;
import com.bemore.api.entity.Enterprise;
import com.google.common.collect.Maps;
import org.apache.commons.compress.utils.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yaobo
 * @version 1.0.0
 * @date 1/3/22 6:44 PM
 * @description
 */
@Service
public class SearchService {
    private final Logger logger = LoggerFactory.getLogger(SearchService.class);

    @Autowired
    private EnterpriseDao enterpriseDao;
    public Map<String,List<Enterprise>> searchViaFile(MultipartFile file) {
        Map<String,List<Enterprise>> resultMap = Maps.newHashMap();
        List<String> targetList = new LinkedList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String tmp = "";
            while ((tmp = br.readLine()) != null) {
                targetList.add(tmp);
            }
        }catch (Exception e) {
            logger.error("ERROR-OCCURRED-WHEN-READ-FILE {}",e.toString());
        }
        String field = targetList.remove(0);
        List<Enterprise> validList = enterpriseDao.findAll((Specification<Enterprise>) (root, criteriaQuery, cb) -> {
            List<Predicate> list = Lists.newArrayList();
            CriteriaBuilder.In<String> in = cb.in(root.get(field));
            for (String name : targetList) {
                in.value(name);
            }
            list.add(in);
            Predicate[] predicates = new Predicate[list.size()];
            return cb.and(list.toArray(predicates));
        });
        resultMap.put("valid",validList);
        List<String> validNameList = validList.stream().map(Enterprise::getName).collect(Collectors.toList());
        List<Enterprise> invalidList  = targetList.stream()
                .filter(s -> !validNameList.contains(s))
                .map(s -> {
                    Enterprise enterprise = new Enterprise();
                    enterprise.setName(s);
                    return enterprise;
                })
                .collect(Collectors.toList());
        resultMap.put("invalid",invalidList);
        return resultMap;
    }
}
