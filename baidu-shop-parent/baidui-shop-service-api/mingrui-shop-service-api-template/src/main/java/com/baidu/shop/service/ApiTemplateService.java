package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @ClassName TemplateService
 * @Description: TODO
 * @Author jlz
 * @Date 2020-09-25 19:05
 * @Version V1.0
 **/
@Api(tags = "模版接口")
public interface ApiTemplateService {

    @ApiOperation(value = "通过spuId创建html模版")
    @GetMapping("item/createHtmlTemplate")
    Result<JSONObject> createHtmlTemplate(Integer spuId);

    @ApiOperation(value = "初始化Html模版")
    @GetMapping("item/initHtmlTemplate")
    Result<JSONObject> initHtmlTemplate();
}
