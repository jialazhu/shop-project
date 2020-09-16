package com.baidu.shop.service.impl;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.service.BaseApiService;
import com.baidu.shop.service.ElasticsearchService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.JSONUtil;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName ElasticsearchServiceImpl
 * @Description: TODO
 * @Author jlz
 * @Date 2020-09-16 19:54
 * @Version V1.0
 **/
@RestController
public class ElasticsearchServiceImpl extends BaseApiService implements ElasticsearchService {

    @Autowired
    private GoodsFeign goodsFeign;

    @Override
    public Result<JsonObject> esGoodsInfo() {
        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setPage(1);
        spuDTO.setRows(5);
        Result<List<SpuDTO>> spuResult = goodsFeign.select(spuDTO);
        if(spuResult.getCode() == HTTPStatus.OK){
            List<SpuDTO> spuList = spuResult.getData();
            // 遍历集合通过spuid 查询skus
            spuList.stream().forEach(spu->{
                Result<List<SkuDTO>> skusResult = goodsFeign.getSkuAndStockBySpuId(spu.getId());
                if(skusResult.getCode() == HTTPStatus.OK){
                    List<SkuDTO> skuList = skusResult.getData();
                    String s = JSONUtil.toJsonString(skuList);
                    System.out.println(s);
                }
            });
        }

        return this.setResultSuccess();
    }
}
