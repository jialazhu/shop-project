package com.baidu.shop.service.impl;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.*;
import com.baidu.shop.entity.*;
import com.baidu.shop.feign.BrandFeign;
import com.baidu.shop.feign.CategoryFeign;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecificationFeign;
import com.baidu.shop.service.TemplateService;
import com.baidu.shop.utils.BeanUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName TemplateServiceImpl
 * @Description: TODO
 * @Author jlz
 * @Date 2020-09-23 19:22
 * @Version V1.0
 **/
@Service
public class TemplateServiceImpl implements TemplateService {

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private CategoryFeign categoryFeign;

    @Autowired
    private BrandFeign brandFeign;

    @Autowired
    private SpecificationFeign specificationFeign;

    @Override
    public Map<String, Object> getInfoBySpuId(Integer spuId) {
        Map<String, Object> map = new HashMap<>();
        // 通过spuId 获得spu数据
        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);
        Result<List<SpuDTO>> goodsResult = goodsFeign.select(spuDTO);

        if(goodsResult.getCode() == 200){

            // spu数据
            SpuDTO spuData = goodsResult.getData().get(0);
            map.put("spuData",spuData);

            //通过spuId 获得spuDetail数据 放到spuData中
            Result<SpuDetailEntity> spuDetailResult = goodsFeign.getSpuDetailBySpuId(spuId);
            if(spuDetailResult.getCode() == 200){
                SpuDetailEntity spuDetailData = spuDetailResult.getData();
//                map.put("spuDetailData",spuDetailData);
                spuData.setSpuDetail(BeanUtil.copyProperties(spuDetailData, SpuDetailDTO.class));
                map.put("spuData",spuData);
            }

            //通过spuId 获得skus集合.放到spuData中
            Result<List<SkuDTO>> skuAndStockResult = goodsFeign.getSkuAndStockBySpuId(spuId);
            if(skuAndStockResult.getCode() == 200){
                List<SkuDTO> skusList = skuAndStockResult.getData();
                spuData.setSkus(skusList);
                map.put("spuData",spuData);
            }

            //通过cid3 获得规格组 规格参数 放到map中
            //规格组
            SpecGroupDTO specGroupDTO = new SpecGroupDTO();
            specGroupDTO.setCid(spuData.getCid3());
            Result<List<SpecGroupEntity>> groupResult = specificationFeign.selectGroup(specGroupDTO);
            // 规格参数
            SpecParamDTO specParamDTO = new SpecParamDTO();
            specParamDTO.setCid(spuData.getCid3());
            Result<List<SpecParamEntity>> paramResult = specificationFeign.selectParam(specParamDTO);

            if(paramResult.getCode() == 200){
                //特有规格参数
                Map<Integer, String> specialSpecMap = new HashMap<>();
                //规格参数的单位
                Map<Integer,String> specUnitMap = new HashMap<>();
                paramResult.getData().stream().forEach(param ->{
                    if(!param.getGeneric())  // 特有规格参数
                        specialSpecMap.put(param.getId(),param.getName());
                    specUnitMap.put(param.getId(),param.getUnit());
                });
                map.put("specialSpecMap",specialSpecMap);
                map.put("specUnitMap",specUnitMap);
            }

            if(groupResult.getCode() == 200 ){
                // key 规格组名 , value 参数map
                Map<String, Map<Integer, String>> specMap = new HashMap<>();
                groupResult.getData().stream().forEach(group->{
                    Map<Integer, String> paramMap = new HashMap<>(); // 规格参数
                    paramResult.getData().stream().forEach(param ->{
                        if(group.getId() == param.getGroupId()){ // 判断 如果是一个规格组里的参数 就添加到paramMap中.
                            paramMap.put(param.getId(),param.getName());
                        }
                    });
                    specMap.put(group.getName(),paramMap);
                });
                map.put("specMap",specMap);
            }

            //通过查询出来的spu数据的brandId 获得brand数据
            BrandDTO brandDTO = new BrandDTO();
            brandDTO.setId(spuData.getBrandId());
            Result<PageInfo<BrandEntity>> brandResult = brandFeign.select(brandDTO);
            if(brandResult.getCode() == 200){
                BrandEntity brandData = brandResult.getData().getList().get(0);
                map.put("brandData",brandData);
            }

            //通过查询出来的spu数据的cid1,cid2,cid3 查询category数据
            // 将cid1,cid2,cid3 转为 String的集合
            List<String> cidList = Arrays.asList(spuData.getCid1().toString(), spuData.getCid2().toString(), spuData.getCid3().toString());
            // 通过cid集合查询出分类数据
            Result<List<CategoryEntity>> categoryResult = categoryFeign.getCateByIdList(String.join(",", cidList));
            if(categoryResult.getCode() == 200){
                List<CategoryEntity> categoryList = categoryResult.getData();
                map.put("categoryList",categoryList);
            }
        }

        return map;
    }
}
