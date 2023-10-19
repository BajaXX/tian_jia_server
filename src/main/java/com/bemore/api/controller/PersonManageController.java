package com.bemore.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bemore.api.dao.TPersonDao;
import com.bemore.api.entity.Person;
import com.bemore.api.entity.SupportContract;
import com.bemore.api.entity.TPerson;
import com.bemore.api.entity.request.SearchParam;
import com.bemore.api.service.PersonManageService;
import com.bemore.api.util.GsonUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author yujiangjun
 */
@RestController
@RequestMapping("/personManage")
public class PersonManageController extends BaseController {

    private final PersonManageService personManageService;

    @Autowired
    private TPersonDao tpersonDao;

    public PersonManageController(PersonManageService personManageService) {
        this.personManageService = personManageService;
    }

    @PostMapping("/update")
    public HashMap update(@RequestBody TPerson person) {
        personManageService.updateById(person);
        return success(null);
    }

    @GetMapping("/getPerson")
    public HashMap getOnePersonById(@RequestParam String id) {
        TPerson byId = personManageService.getById(id);
        return success(byId);
    }

    @PostMapping("/save")
    public HashMap add(@RequestBody TPerson person) {
        QueryWrapper<TPerson> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(StringUtils.hasLength(person.getIdcard()), TPerson::getIdcard, person.getIdcard());
        List<TPerson> list = personManageService.list(queryWrapper);
        if (!CollectionUtils.isEmpty(list)) return error(400, "身份证号重复，保存失败,请核对后再添加");
        personManageService.save(person);
        return success(null);
    }

    @GetMapping("/delete")
    public HashMap delete(Integer uuid) {
        personManageService.removeById(uuid);
        return success(null);
    }

    @GetMapping("/getList")
    public HashMap getList(TPerson person) {
        if (person != null) {
            String idCard = person.getIdcard();
            person.setIdcard(null);
            QueryWrapper<TPerson> queryWrapper = new QueryWrapper<>(person);
            queryWrapper.like(!StringUtils.isEmpty(idCard), "idcard", idCard);
            return success(personManageService.list(queryWrapper));
        }
        return success(personManageService.list(new QueryWrapper<>(person)));
    }

    @PostMapping("/getAllPersonList")
    public String getAllPersonList(@RequestBody SearchParam param) {
        List<TPerson> personList = new ArrayList<>();

        if (Objects.isNull(param)) {
            personList = tpersonDao.findAllTPerson(0, 10);
        }else{
            personList = tpersonDao.searchAllTPerson("%"+param.getKeyword()+"%",param.getPage()* param.getSize(), param.getSize());
        }
        return GsonUtil.build(personList);
    }

}
