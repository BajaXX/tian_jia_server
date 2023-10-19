package com.bemore.api.controller;

import com.bemore.api.service.NewEnterpriseDocGeneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class IndexController {

    @Autowired
    NewEnterpriseDocGeneService docGeneService;

    @GetMapping("/down1401")
    public void down1401(HttpServletResponse response){
        docGeneService.generate1401Doc("2c9f9ec777e40fbb0177fdb2e4cb001d","个人独资企业设立(变更)登记审核表",response);
    }
}
