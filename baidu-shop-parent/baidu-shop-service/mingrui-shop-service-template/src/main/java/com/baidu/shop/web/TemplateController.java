package com.baidu.shop.web;

import com.baidu.shop.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @ClassName TemplateController
 * @Description: TODO
 * @Author jlz
 * @Date 2020-09-23 19:09
 * @Version V1.0
 **/
@Controller
@RequestMapping("item")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    @GetMapping("/{spuId}.html")
    public String item(@PathVariable Integer spuId, ModelMap map){
       Map<String,Object> infoMap =  templateService.getInfoBySpuId(spuId);
       map.putAll(infoMap);
        return "item";
    }
}
