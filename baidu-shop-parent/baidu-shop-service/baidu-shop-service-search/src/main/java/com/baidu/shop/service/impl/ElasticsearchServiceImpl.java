package com.baidu.shop.service.impl;

import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.entity.SpuDetailEntity;
import com.baidu.shop.feign.BrandFeign;
import com.baidu.shop.feign.CategoryFeign;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecificationFeign;
import com.baidu.shop.repository.MrElasticsearchRepository;
import com.baidu.shop.response.EsResponse;
import com.baidu.shop.service.BaseApiService;
import com.baidu.shop.service.ElasticsearchService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.ESHighLightUtil;
import com.baidu.shop.utils.JSONUtil;
import com.baidu.shop.utils.StringUtil;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private BrandFeign brandFeign;

    @Autowired
    private CategoryFeign categoryFeign;

    /**
     * es搜索方法
     * @param search
     * @param page
     * @return
     */
    @Override
    public EsResponse search(String search, Integer page,String filter) {
        //查询
        SearchHits<GoodsDoc> searchHits = elasticsearchRestTemplate.search(this.getSearchQueryBuilder(search,page,filter).build(), GoodsDoc.class);
        //将查询到的hits中content内容的title替换成高亮title. 返回查询的数据
        List<GoodsDoc> goodsDocs = ESHighLightUtil.getHighLightHit(searchHits.getSearchHits())
                .stream().map(searchHit -> searchHit.getContent()).collect(Collectors.toList());
        //总条数 
        long total = searchHits.getTotalHits();
        long totalPage = Double.valueOf(Math.ceil(Long.valueOf(total).doubleValue() / 10)).longValue();
        //聚合
        Aggregations aggregations = searchHits.getAggregations();
        //获得brandId集合
        List<BrandEntity> brandList = this.getBrandList(aggregations);
        //获得cid3分类和热门cid的map
        Map<Integer, List<CategoryEntity>> categoryMap = this.getCategoryList(aggregations);
        // 热门cid
        Integer hotCid = 0 ;
        // 分类cid3集合
        List<CategoryEntity> categoryList = null ;
        for (Map.Entry<Integer, List<CategoryEntity>> entry : categoryMap.entrySet()) {
            hotCid = entry.getKey();
            categoryList = entry.getValue();
        }
        // 根据热点cid 返回热点分类能用于 搜索 的规格和具体范围
        HashMap<String, List<String>> paramAndValueMap = this.getHotParamAndValueByHotCid(hotCid, search);

        return new EsResponse(total,totalPage,brandList,categoryList,goodsDocs,paramAndValueMap);
    }

    /**
     * 通过热点分类id返回分类的规格和聚合的值
     * @param hotCid
     * @param search
     * @return
     */
    private HashMap<String, List<String>> getHotParamAndValueByHotCid(Integer hotCid , String search){
        //通过热门cid去查询规格
        SpecParamDTO paramDTO = new SpecParamDTO();
        paramDTO.setCid(hotCid);
        paramDTO.setSearching(true);
        // 查询出来的规格结果集
        Result<List<SpecParamEntity>> paramResult = specificationFeign.selectParam(paramDTO);
        HashMap<String, List<String>> map = new HashMap<>();
        if(paramResult.getCode() == 200){
            // 构建 es查询条件
            NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
            //需要在原搜索的基础上构建查询 所以必须要有
            searchQueryBuilder.withQuery(QueryBuilders.multiMatchQuery(search,"title","brandName","categoryName"));
            searchQueryBuilder.withPageable(PageRequest.of(0,1));
            //遍历规格集合
            paramResult.getData().stream().forEach(param->{
                //根据查询出来的规格名 聚合 搜索
                searchQueryBuilder.addAggregation(AggregationBuilders.terms(param.getName()).field("specs."+param.getName()+".keyword"));
            });
            // es查询
            SearchHits<GoodsDoc> SearchHits = elasticsearchRestTemplate.search(searchQueryBuilder.build(), GoodsDoc.class);
            // 获得聚合aggr
            Aggregations aggregations = SearchHits.getAggregations();
            //再次遍历规格集合 通过规格名去获得聚合的桶 (规格名就是聚合名)
            paramResult.getData().stream().forEach(param->{
                Terms terms = aggregations.get(param.getName());
                List<String> valueList = terms.getBuckets().stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());
                map.put(param.getName(),valueList);
            });
            return map;
        }
        return null;
    }


    /**
     * 构建es搜索查询条件
     * @param search
     * @param page
     * @return
     */
    private NativeSearchQueryBuilder getSearchQueryBuilder(String search, Integer page,String filter){

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //搜索过滤
        if(StringUtil.isNotEmpty(filter) && filter.length() > 2){
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            // json字符串转map对象
            Map<String, String> filterMap = JSONUtil.toMapValueString(filter);
            // 遍历
            filterMap.forEach((key,value) -> {
                MatchQueryBuilder matchQueryBuilder = null;
                if(key.equals("cid3") || key.equals("brandId")){
                    // 拼接过滤条件
                    matchQueryBuilder = QueryBuilders.matchQuery(key, value);
                }else{
                    matchQueryBuilder = QueryBuilders.matchQuery("specs."+key+".keyword",value);
                }
                boolQueryBuilder.must(matchQueryBuilder);
            });

            nativeSearchQueryBuilder.withFilter(boolQueryBuilder);
        }

        // 查询
        if(StringUtil.isNotEmpty(search)){
            nativeSearchQueryBuilder.withQuery(QueryBuilders.multiMatchQuery(search,"title","brandName","categoryName"));
        }
        //聚合
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("brand_agg").field("brandId"));
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("cate_agg").field("cid3"));
        //高亮
        nativeSearchQueryBuilder.withHighlightBuilder(ESHighLightUtil.getHighlightBuilder("title"));
        //分页
        nativeSearchQueryBuilder.withPageable(PageRequest.of(page-1,10));

        return nativeSearchQueryBuilder;
    }

    /**
     * 通过品牌id聚合 返回品牌集合
     * @param aggregations
     * @return
     */
    private List<BrandEntity> getBrandList(Aggregations aggregations){
        Terms brand_agg = aggregations.get("brand_agg");
        List<? extends Terms.Bucket> brandBuckets = brand_agg.getBuckets();
        List<String> brandIdList = brandBuckets.stream().map(brand -> brand.getKeyAsString()).collect(Collectors.toList());
        String brandids = String.join(",", brandIdList);
        Result<List<BrandEntity>> brandResult =  brandFeign.getBrandByIdList(brandids);
        return brandResult.getData();
    }

    /**
     * 通过分类cid3聚合 返回分类集合
     * @param aggregations
     * @return
     */
    private  Map<Integer, List<CategoryEntity>>  getCategoryList(Aggregations aggregations){
        Terms cate_agg = aggregations.get("cate_agg");
        List<? extends Terms.Bucket> catebuckets = cate_agg.getBuckets();
        // 最热的总数.
        List<Long> hotDoc = Arrays.asList(0L);
        //最热的cid
        List<Integer> hotCid = Arrays.asList(0);
        List<String> catesList = catebuckets.stream().map(cate -> {
            if(cate.getDocCount() > hotDoc.get(0)){
                hotDoc.set(0,cate.getDocCount());
                hotCid.set(0,cate.getKeyAsNumber().intValue());
            }
            return cate.getKeyAsString();
        }).collect(Collectors.toList());
        String cateids = String.join(",", catesList);
        Result<List<CategoryEntity>> categoryResult= categoryFeign.getCateByIdList(cateids);
        // 返回热门cid 和 查询的所有分类
        Map<Integer, List<CategoryEntity>> map = new HashMap<>();
        map.put(hotCid.get(0),categoryResult.getData());
        return map;
    }

    /**
     * 清空es库
     * @return
     */
    @Override
    public Result<JsonObject> cleanEsData() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        if(indexOperations.exists()){
            indexOperations.delete();
            log.info("删除成功");
        }
        return this.setResultSuccess();
    }

    /**
     * 初始化es库
     * @return
     */
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

    /**
     * 获得GoodsDOC集合
     * @return
     */
    private List<GoodsDoc> getGoodsInfo() {
        SpuDTO spuDTO = new SpuDTO();
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

    /**
     * 通过spuid查询 通过map方式返回价格集合和skus集合
     * @param spuId
     * @return
     */
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

    /**
     * 通过spuid查询 返回规格参数
     * @param spu
     * @return
     */
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
                    //判断是不是私有或公有规格
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


    /**
     * 工具方法. 将具体值转化为具体范围
     * @param value
     * @param segments
     * @param unit
     * @return
     */
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
