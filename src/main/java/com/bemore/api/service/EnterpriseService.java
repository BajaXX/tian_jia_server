package com.bemore.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bemore.api.entity.Enterprise;
import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @ClassName EnterpriseService
 * @Description 企业信息服务层
 * @Author Louis
 * @Date 2022/04/24 23:29
 */
public interface EnterpriseService extends IService<Enterprise> {

    void updateByExcel(@NonNull MultipartFile excelFile) throws IOException;

}
