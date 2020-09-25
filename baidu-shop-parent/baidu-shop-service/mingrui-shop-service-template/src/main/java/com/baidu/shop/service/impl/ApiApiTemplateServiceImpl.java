package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.*;
import com.baidu.shop.entity.*;
import com.baidu.shop.feign.BrandFeign;
import com.baidu.shop.feign.CategoryFeign;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecificationFeign;
import com.baidu.shop.service.BaseApiService;
import com.baidu.shop.service.ApiTemplateService;
import com.baidu.shop.utils.BeanUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ApiTemplateServiceImpl
 * @Description: TODO
 * @Author jlz
 * @Date 2020-09-25 19:13
 * @Version V1.0
 **/
@RestController
public class ApiApiTemplateServiceImpl extends BaseApiService implements ApiTemplateService {

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private CategoryFeign categoryFeign;

    @Autowired
    private BrandFeign brandFeign;

    @Autowired
    private SpecificationFeign specificationFeign;

    @Autowired
    private TemplateEngine templateEngine;

    @Value(value = "${baidu.static.html.path}")
    private String htmlPath;

    /**
     * 创建html模版
     * @param spuId
     * @return
     */
    @Override
    public Result<JSONObject> createHtmlTemplate(Integer spuId) {

        Map<String, Object> map = this.getInfoBySpuId(spuId);

        //创建模板引擎上下文
        Context context = new Context();
        //将所有准备的数据放到模板中
        context.setVariables(map);
        //创建文件 param1:文件路径 param2:文件名称
        File file = new File(htmlPath, spuId + ".html");
        // 判断上级目录是否存在 不存在 创建目录
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        //构建文件输出流
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(file ,"UTF-8");
            //根据模板生成静态文件
            //param1:模板名称 params2:模板上下文[上下文中包含了需要填充的数据],文件输出流
            templateEngine.process("item",context,writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }finally {
            writer.close();
        }



        return this.setResultSuccess();
    }

    /**
     * 初始化html模版
     * @return
     */
    @Override
    public Result<JSONObject> initHtmlTemplate() {
        Result<List<SpuDTO>> listResult = goodsFeign.select(new SpuDTO());
        if(listResult.getCode() == 200){
            listResult.getData().stream().forEach(spuDTO -> {
                this.createHtmlTemplate(spuDTO.getId());
            });
        }
        return this.setResultSuccess();
    }

    /**
     * 获取页面所需数据map
     * @param spuId
     * @return
     */
    private Map<String, Object> getInfoBySpuId(Integer spuId) {
        Map<String, Object> map = null;
        // 通过spuId 获得spu数据
        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);
        Result<List<SpuDTO>> goodsResult = goodsFeign.select(spuDTO);

        if(goodsResult.getCode() == 200)
            map = this.getMap(goodsResult);

        return map;
    }

    /**
     * 获取map需要的所有参数
     * @param goodsResult
     * @return
     */
    private Map<String, Object> getMap(Result<List<SpuDTO>> goodsResult){
        Map<String, Object> map = new HashMap<>();
        // spu数据
        SpuDTO spuData = goodsResult.getData().get(0);
        map.put("spuData",spuData);
        // spuDetail数据
        SpuDetailDTO spuDetailDTO = this.getSpuDetailDTO(spuData.getId());
        spuData.setSpuDetail(spuDetailDTO);
        map.put("spuData",spuData);
        //skus集合数据
        List<SkuDTO> skusList = this.getSkusList(spuData.getId());
        spuData.setSkus(skusList);
        map.put("spuData",spuData);
        //特有规格值数据和所有规格单位数据
        Map<String, Map<Integer, String>> specialSpecAndSpecUnit = this.getSpecialSpecAndSpecUnit(spuData);
        Map<Integer, String> specialSpecMap = specialSpecAndSpecUnit.get("specialSpecMap");
        Map<Integer, String> specUnitMap = specialSpecAndSpecUnit.get("specUnitMap");
        map.put("specialSpecMap",specialSpecMap);
        map.put("specUnitMap",specUnitMap);
        //所有规格组和规格参数数据 组和参数关系 1:多
        Map<String, Map<Integer, String>> specMap = this.getSpecMap(spuData);
        map.put("specMap",specMap);
        //品牌数据
        BrandEntity brandData = this.getBrandEntity(spuData);
        map.put("brandData",brandData);
        //分类数据
        List<CategoryEntity> categoryList = this.getCategoryList(spuData);
        map.put("categoryList",categoryList);
        return map;
    }

    /**
     * 获取spuDetail数据
     * @param spuId
     * @return
     */
    private SpuDetailDTO getSpuDetailDTO(Integer spuId){
        //通过spuId 获得spuDetail数据 放到spuData中
        Result<SpuDetailEntity> spuDetailResult = goodsFeign.getSpuDetailBySpuId(spuId);
        if(spuDetailResult.getCode() == 200){
            SpuDetailEntity spuDetailData = spuDetailResult.getData();
            SpuDetailDTO spuDetailDTO = BeanUtil.copyProperties(spuDetailData, SpuDetailDTO.class);
            return spuDetailDTO;
        }
        return null;
    }

    /**
     * 获取skus数据
     * @param spuId
     * @return
     */
    private List<SkuDTO> getSkusList(Integer spuId){
        //通过spuId 获得skus集合.放到spuData中
        Result<List<SkuDTO>> skuAndStockResult = goodsFeign.getSkuAndStockBySpuId(spuId);
        if(skuAndStockResult.getCode() == 200){
            List<SkuDTO> skusList = skuAndStockResult.getData();
            return skusList;
        }
        return null;
    }

    /**
     * 获取特有规格值 和 所有规格单位
     * @param spuData
     * @return
     */
    private Map<String,Map<Integer,String>> getSpecialSpecAndSpecUnit(SpuDTO spuData){
        Map<String,Map<Integer,String>> map = new HashMap<>();
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
        return map;
    }

    /**
     * 获取所有规格组和规格参数数据map  关系:1对多
     * @param spuData
     * @return
     */
    private Map<String, Map<Integer, String>> getSpecMap(SpuDTO spuData){

        //通过cid3 获得规格组 规格参数 放到map中
        //规格组
        SpecGroupDTO specGroupDTO = new SpecGroupDTO();
        specGroupDTO.setCid(spuData.getCid3());
        Result<List<SpecGroupEntity>> groupResult = specificationFeign.selectGroup(specGroupDTO);
        // 规格参数
        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(spuData.getCid3());
        Result<List<SpecParamEntity>> paramResult = specificationFeign.selectParam(specParamDTO);

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
            return specMap;
        }
        return null;
    }

    /**
     * 获取品牌数据
     * @param spuData
     * @return
     */
    private BrandEntity getBrandEntity(SpuDTO spuData){
        //通过查询出来的spu数据的brandId 获得brand数据
        BrandDTO brandDTO = new BrandDTO();
        brandDTO.setId(spuData.getBrandId());
        Result<PageInfo<BrandEntity>> brandResult = brandFeign.select(brandDTO);
        if(brandResult.getCode() == 200){
            BrandEntity brandData = brandResult.getData().getList().get(0);
            return brandData;
        }
        return null;
    }

    /**
     * 获取分类数据
     * @param spuData
     * @return
     */
    private List<CategoryEntity> getCategoryList(SpuDTO spuData){
        //通过查询出来的spu数据的cid1,cid2,cid3 查询category数据
        // 将cid1,cid2,cid3 转为 String的集合
        List<String> cidList = Arrays.asList(spuData.getCid1().toString(), spuData.getCid2().toString(), spuData.getCid3().toString());
        // 通过cid集合查询出分类数据
        Result<List<CategoryEntity>> categoryResult = categoryFeign.getCateByIdList(String.join(",", cidList));
        if(categoryResult.getCode() == 200){
            List<CategoryEntity> categoryList = categoryResult.getData();
            return categoryList;
        }
        return null;
    }
}
