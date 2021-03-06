package com.baidu.shop.response;

import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.status.HTTPStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @ClassName EsResponse
 * @Description: TODO
 * @Author jlz
 * @Date 2020-09-21 14:53
 * @Version V1.0
 **/
@Data
@NoArgsConstructor
public class EsResponse extends Result<List<GoodsDoc>> {

    private Long total;

    private Long totalPage;

    private List<BrandEntity> brandList;

    private List<CategoryEntity> categoryList;

    private Map<String, List<String>> paramAndValueMap;

    public EsResponse(Long total, Long totalPage, List<BrandEntity> brandList, List<CategoryEntity> categoryList , List<GoodsDoc> goodsDocs,  Map<String, List<String>> paramAndValueMap) {
        super(HTTPStatus.OK, HTTPStatus.OK+"", goodsDocs);
        this.total = total;
        this.totalPage = totalPage;
        this.brandList = brandList;
        this.categoryList = categoryList;
        this.paramAndValueMap = paramAndValueMap;
    }

}
