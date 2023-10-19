package com.bemore.api.controller;

import com.bemore.api.common.BaseResponseData;
import com.bemore.api.service.SearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author yaobo
 * @version 1.0.0
 * @date 1/3/22 6:37 PM
 * @description
 */
@RestController
@RequestMapping("search")
@Api(value = "garden search module")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/enterprise/file")
    @ApiOperation(value = "for enterprise")
    public BaseResponseData enterpriseSearchViaFile(@RequestParam("file")MultipartFile file) {
        return BaseResponseData.success(searchService.searchViaFile(file));
    }
}
