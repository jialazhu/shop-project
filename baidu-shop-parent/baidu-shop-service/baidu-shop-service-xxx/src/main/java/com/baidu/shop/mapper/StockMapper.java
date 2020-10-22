package com.baidu.shop.mapper;

import com.baidu.shop.entity.StockEntity;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

public interface StockMapper extends Mapper<StockEntity>, DeleteByIdListMapper<StockEntity,Long> {


    @Update(value = "update tb_stock s set s.stock = ( (select * from (select t.stock from tb_stock t where t.sku_id = #{skuId} ) a) - #{stock}) where s.sku_id = #{skuId}")
    void updateStock(Long skuId, Integer stock);
}
