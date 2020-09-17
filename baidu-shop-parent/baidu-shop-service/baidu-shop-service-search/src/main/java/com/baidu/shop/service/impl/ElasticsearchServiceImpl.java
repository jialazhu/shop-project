package com.baidu.shop.service.impl;

import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.entity.SpuDetailEntity;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecificationFeign;
import com.baidu.shop.repository.MrElasticsearchRepository;
import com.baidu.shop.service.BaseApiService;
import com.baidu.shop.service.ElasticsearchService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.JSONUtil;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ElasticsearchServiceImpl
 * @Description: TODO
 * @Author jlz
 * @Date 2020-09-16 19:54
 * @Version V1.0
 **/
@RestController
@Slf4j
public class ElasticsearchServiceImpl extends BaseApiService implements ElasticsearchService {

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private SpecificationFeign specificationFeign;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private MrElasticsearchRepository mrElasticsearchRepository;

    @Override
    public Result<JsonObject> cleanEsData() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        if(indexOperations.exists()){
            indexOperations.delete();
            log.info("删除成功");
        }
        return this.setResultSuccess();
    }

    @Override
    public Result<JsonObject> initEsData() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        if(!indexOperations.exists()){
            indexOperations.create();
            log.info("索引创建成功");
            indexOperations.createMapping();
            log.info("映射创建成功");
        }

        List<GoodsDoc> goodsInfo = this.getGoodsInfo();
//        elasticsearchRestTemplate.save(goodsInfo);
        mrElasticsearchRepository.saveAll(goodsInfo);
        return this.setResultSuccess();
    }

    private List<GoodsDoc> getGoodsInfo() {
        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setPage(1);
        spuDTO.setRows(5);
        Result<List<SpuDTO>> spuResult = goodsFeign.select(spuDTO);

        List<GoodsDoc> goodsDocs = new ArrayList<>();

        if(spuResult.getCode() == HTTPStatus.OK){
            List<SpuDTO> spuList = spuResult.getData();

            // 遍历集合
            spuList.stream().forEach(spu->{
                GoodsDoc goodsDoc = new GoodsDoc();
                goodsDoc.setId(spu.getId().longValue());
                goodsDoc.setTitle(spu.getTitle());
                goodsDoc.setSubTitle(spu.getSubTitle());
                goodsDoc.setBrandName(spu.getBrandName());
                goodsDoc.setCategoryName(spu.getCategoryName());
                goodsDoc.setBrandId(spu.getBrandId().longValue());
                goodsDoc.setCid1(spu.getCid1().longValue());
                goodsDoc.setCid2(spu.getCid2().longValue());
                goodsDoc.setCid3(spu.getCid3().longValue());
                goodsDoc.setCreateTime(spu.getCreateTime());
                Map<List<Long>, List<Map<String, Object>>> priceAndSkus = this.getPriceAndSkus(spu.getId());
                priceAndSkus.forEach((k, v) -> {
                    goodsDoc.setPrice(k);
                    goodsDoc.setSkus(JSONUtil.toJsonString(v));
                });
                goodsDoc.setSpecs(this.getSpecs(spu));
                goodsDocs.add(goodsDoc);
            });
        }
        return goodsDocs;
    }

    private Map<List<Long>,List<Map<String, Object>>> getPriceAndSkus(Integer spuId){
        //通过spuid 查询skus
        List<Map<String, Object>> skus = new ArrayList<>();
        List<Long> priceList = new ArrayList<>();
        Result<List<SkuDTO>> skusResult = goodsFeign.getSkuAndStockBySpuId(spuId);
        if(skusResult.getCode() == HTTPStatus.OK){
            List<SkuDTO> skuList = skusResult.getData();
            //遍历获得price集合和skus有用的数据
            skuList.stream().forEach(sku->{
                Map<String, Object> skusMap = new HashMap<>();
                skusMap.put("id",sku.getId());
                skusMap.put("title",sku.getTitle());
                skusMap.put("images",sku.getImages());
                skusMap.put("price",sku.getPrice());
                priceList.add(sku.getPrice());
                skus.add(skusMap);
            });
        }
        Map<List<Long>,List<Map<String, Object>>> map = new HashMap<>();
        map.put(priceList,skus);
        return map;
    }

    private Map<String, Object> getSpecs(SpuDTO spu){
        //将规格参数和规格值存放到specs
        Map<String, Object> specs = new HashMap<>();
        //通过spu.id查询spuDetail
        Result<SpuDetailEntity> spuDetailResult = goodsFeign.getSpuDetailBySpuId(spu.getId());
        if(spuDetailResult.getCode() == HTTPStatus.OK){
            SpuDetailEntity spuDetailEntity = spuDetailResult.getData();
            //将json字符串的参数值转换成json对象
            Map<String, String> genericSpec = JSONUtil.toMapValueString(spuDetailEntity.getGenericSpec());
            Map<String, List<String>> specialSpec = JSONUtil.toMapValueStrList(spuDetailEntity.getSpecialSpec());

            //通过spu.cid3查询规格参数params
            SpecParamDTO specParamDTO = new SpecParamDTO();
            specParamDTO.setCid(spu.getCid3());
            Result<List<SpecParamEntity>> paramResult = specificationFeign.selectParam(specParamDTO);
            if (paramResult.getCode() == HTTPStatus.OK) {
                List<SpecParamEntity> paramList = paramResult.getData();
                paramList.stream().forEach(param->{
                    if(param.getGeneric()){
                        //判断是不是可搜索范围 和 是否是数值类型
                        if(param.getNumeric() && param.getSearching()){
                            //修改数值的值.改为具体范围
                            specs.put(param.getName(),this.chooseSegment(genericSpec.get(param.getId().toString()),param.getSegments(),param.getUnit()));
                        }else{
                            specs.put(param.getName(),genericSpec.get(param.getId().toString()));
                        }
                    }else{
                        specs.put(param.getName(),specialSpec.get(param.getId().toString()));
                    }
                });
            }
        }
        return specs;
    }

    // 因为有些参数是数值类型能被范围搜索.效率低.此方法将数值直接更改为具体范围.方便直接查询 提高es查询效率
    private String chooseSegment(String value, String segments, String unit) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : segments.split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + unit + "以上";
                }else if(begin == 0){
                    result = segs[1] + unit + "以下";
                }else{
                    result = segment + unit;
                }
                break;
            }
        }
        return result;
    }
}
