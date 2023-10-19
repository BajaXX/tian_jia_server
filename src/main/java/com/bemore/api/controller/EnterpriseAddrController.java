package com.bemore.api.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bemore.api.dto.AddrRequest;
import com.bemore.api.entity.EnterpriseAddr;
import com.bemore.api.exception.WebException;
import com.bemore.api.service.EnterpriseAddrService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/addr")
public class EnterpriseAddrController extends BaseController {
    @Autowired
    private EnterpriseAddrService enterpriseAddrService;


    @GetMapping("/getAddrs")
    public HashMap getAddrs() {
        return success(enterpriseAddrService.list());
    }

    @GetMapping("/searchAddrs")
    public HashMap searchAddrs(AddrRequest request) {
        if (!Objects.isNull(request.getKeyWord())) {
            QueryWrapper<EnterpriseAddr> queryWrapper = new QueryWrapper<>();
            queryWrapper
                    .and(StringUtils.isNotEmpty(request.getKeyWord()), (i) -> i.like("addr", request.getKeyWord()));
            return success(enterpriseAddrService.list(queryWrapper));
        } else {
            List<EnterpriseAddr> enterpriseAddrList = enterpriseAddrService.list();
            return success(enterpriseAddrList);
        }

    }

    @PostMapping("/addAddr")
    public HashMap addAddr(@RequestBody EnterpriseAddr addr) {
        EnterpriseAddr enterpriseAddr = enterpriseAddrService.getOne(new QueryWrapper<EnterpriseAddr>().eq("addr", addr.getAddr()));
        if (!Objects.isNull(enterpriseAddr)) throw new WebException(102, "该地址已经存在");
        enterpriseAddrService.save(addr);
        return success(null);
    }

    @PostMapping("/batchAddAddr")
    public HashMap batchAddAddr(@RequestBody List<EnterpriseAddr> addrs) {
        enterpriseAddrService.saveBatch(addrs);
        return success(null);
    }

    @PostMapping("updateAddr")
    public HashMap updateAddr(@RequestBody EnterpriseAddr addr) {
        enterpriseAddrService.saveOrUpdate(addr);
        return success(null);
    }

    @GetMapping("deleteAddr")
    public HashMap deleteAddr(Integer id) {
        enterpriseAddrService.removeById(id);
        return success(null);
    }

    @PostMapping("import")
    public HashMap importAddr(MultipartFile file) throws Exception {
        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
        reader.setHeaderAlias(new HashMap<String, String>() {
            {
                put("注册地址", "addr");
                put("行业", "industry");
                put("状态", "enable");
                put("企业", "enterpriseName");
            }
        });
        List<EnterpriseAddr> addrList = reader.read(0, 1, EnterpriseAddr.class);
        enterpriseAddrService.saveBatch(addrList);
        return success(null);
    }
}
