package com.bemore.api.controller.tax;

import com.bemore.api.controller.BaseController;
import com.bemore.api.service.tax.AddEnterpriseService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * @author 余江俊
 */
@Api("新增企业明细")
@RestController
@RequestMapping("/texManage")
public class AddEnterpriseDetailController extends BaseController {

    private final AddEnterpriseService addEnterpriseService;

    public AddEnterpriseDetailController(AddEnterpriseService addEnterpriseService) {
        this.addEnterpriseService = addEnterpriseService;
    }

    @GetMapping("/query")
    public HashMap query(@RequestParam Integer year, @RequestParam Integer month){

        return success(addEnterpriseService.getAddDetail(year,month));
    }

    @GetMapping("/export")
    public void query(@RequestParam Integer year, @RequestParam Integer month, HttpServletResponse resp) throws Exception{
        addEnterpriseService.exportAddEnterpriseDetail(year,month,resp);
    }
}
