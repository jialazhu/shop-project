package com.baidu.shop.service.impl;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.service.BaseApiService;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.utils.BeanUtil;
import com.baidu.shop.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName BrandServiceImpl
 * @Description: TODO
 * @Author jlz
 * @Date 2020-08-31 14:40
 * @Version V1.0
 **/
@RestController
public class BrandServiceImpl extends BaseApiService implements BrandService {

    @Resource
    private BrandMapper brandMapper;

    @Override
    public Result<JsonObject> save(BrandDTO brandDTO) {
        brandMapper.insertSelective(BeanUtil.copyProperties(brandDTO,BrandEntity.class));
        return this.setResultSuccess();
    }

    @Override
    public Result<List<BrandEntity>> select(BrandDTO brandDTO) {
        // 分页
        PageHelper.startPage(brandDTO.getPage(),brandDTO.getRows());
        //条件查询
        Example example = new Example(BrandEntity.class);
        //排序
        if(StringUtil.isNotEmpty(brandDTO.getSort())) example.setOrderByClause(brandDTO.getOrderByClause());
        //根据name 模糊查询
        if(StringUtil.isNotEmpty(brandDTO.getName())) example.createCriteria()
                .andLike("name","%"+brandDTO.getName()+"%");
        PageInfo<BrandEntity> pageInfo = new PageInfo<>(brandMapper.selectByExample(example));
        return this.setResultSuccess(pageInfo);
    }
}
