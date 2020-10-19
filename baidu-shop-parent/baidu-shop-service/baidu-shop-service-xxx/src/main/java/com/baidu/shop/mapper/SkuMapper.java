package com.baidu.shop.mapper;

import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.entity.SkuEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SkuMapper extends Mapper<SkuEntity>, DeleteByIdListMapper<SkuEntity,Long> {
    @Select(value = "select s.*,stock from tb_sku s , tb_stock t where s.id = t.sku_id and s.spu_id = #{spuId}")
    List<SkuDTO> getSkuAndStockBySpuId(Integer spuId);

    @Select(value = "select s.*,s.own_spec ownSpec,stock from tb_sku s , tb_stock t where s.id = t.sku_id and s.id = #{skuId}")
    SkuDTO selectBySkuId(Long skuId);
}
