package com.baidu.shop.service.impl;

import com.baidu.shop.base.Result;
import com.baidu.shop.component.BaiduRabbitMQ;
import com.baidu.shop.constant.MqMessageConstant;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.dto.StockDTO;
import com.baidu.shop.entity.*;
import com.baidu.shop.mapper.*;
import com.baidu.shop.service.BaseApiService;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.service.GoodsService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName SpuServiceImpl
 * @Description: TODO
 * @Author jlz
 * @Date 2020-09-07 14:23
 * @Version V1.0
 **/
@RestController
public class GoodsServiceImpl extends BaseApiService implements GoodsService {

    @Resource
    private SpuMapper spuMapper;

    @Autowired
    private BrandService brandService;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SpuDetailMapper spuDetailMapper;

    @Resource
    private SkuMapper skuMapper;

    @Resource
    private StockMapper stockMapper;

    @Resource
    private BaiduRabbitMQ baiduRabbitMQ;

    /*//数据源事务注解
    @Autowired
    DataSourceTransactionManager dataSourceTransactionManager;
    //事务注解
    @Autowired
    TransactionDefinition transactionDefinition;*/

    @Override
    public Result<JsonObject> updateStock(StockDTO stockDTO) {
        stockMapper.updateStock(stockDTO.getSkuId(),stockDTO.getStock());
        return this.setResultSuccess();
    }

    @Override
    public Result<SkuEntity> getSkuBySkuId(Long skuId) {
        SkuEntity skuEntity = skuMapper.selectByPrimaryKey(skuId);
        return this.setResultSuccess(skuEntity);
    }

    @Transactional
    @Override
    public Result<JsonObject> editSaleable(SpuDTO spuDTO) {
        spuMapper.updateByPrimaryKeySelective(BeanUtil.copyProperties(spuDTO,SpuEntity.class));
        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JsonObject> delete(Integer spuId) {

        if(ObjectUtil.isNull(spuId)) return  this.setResultError("无效id");
        spuMapper.deleteByPrimaryKey(spuId);
        spuDetailMapper.deleteByPrimaryKey(spuId);
        // 删除sku 和 stock
        this.deleteSkusAndStocks(spuId);

        // 在事务提交后执行的方法
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                //发送消息
                baiduRabbitMQ.send(spuId+"", MqMessageConstant.SPU_ROUT_KEY_DELETE);
            }
        });

        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JsonObject> edit(SpuDTO spuDTO) {

        Date date = new Date();
        //修改spu
        SpuEntity spuEntity = BeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setLastUpdateTime(date);
        spuMapper.updateByPrimaryKeySelective(spuEntity);
        //修改spuDetail
        spuDetailMapper.updateByPrimaryKeySelective(BeanUtil.copyProperties(spuDTO.getSpuDetail(), SpuDetailEntity.class));
        // 删除sku 和 stock
        this.deleteSkusAndStocks(spuDTO.getId());
        //新增 sku 和 stock
        this.addSkusAndStocks(spuDTO.getSkus(),spuDTO.getId(),date);

        // 在事务提交后执行的方法
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                //发送消息
                baiduRabbitMQ.send(spuDTO.getId()+"", MqMessageConstant.SPU_ROUT_KEY_SAVE);
            }
        });

        return this.setResultSuccess();
    }

    @Override
    public Result<List<SkuDTO>> getSkuAndStockBySpuId(Integer spuId) {
       // List<SkuDTO> list = skuMapper.getSkuAndStockBySpuId(spuId);
        Example example = new Example(SkuEntity.class);
        if(ObjectUtil.isNotNull(spuId))
            example.createCriteria().andEqualTo("spuId",spuId);
        List<SkuEntity> skuList = skuMapper.selectByExample(example);

        List<SkuDTO> SkuDTOList = skuList.stream().map(skuEntity -> {
            SkuDTO skuDTO = BeanUtil.copyProperties(skuEntity, SkuDTO.class);
            StockEntity stockEntity = stockMapper.selectByPrimaryKey(skuEntity.getId());
            skuDTO.setStock(stockEntity.getStock());
            return skuDTO;
        }).collect(Collectors.toList());

        return this.setResultSuccess(SkuDTOList);
    }

    @Override
    public Result<SpuDetailEntity> getSpuDetailBySpuId(Integer spuId) {
        SpuDetailEntity spuDetailEntity = spuDetailMapper.selectByPrimaryKey(spuId);
        return this.setResultSuccess(spuDetailEntity);
    }

    @Transactional
    @Override
    public Result<JsonObject> save(SpuDTO spuDTO) {

        // 先新增spu表 获得spuId
        Date date = new Date();
        SpuEntity   spuEntity = BeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setSaleable(1);
        spuEntity.setValid(1);
        spuEntity.setCreateTime(date);
        spuEntity.setLastUpdateTime(date);
        spuMapper.insertSelective(spuEntity);
        //新增spuDetail
        SpuDetailEntity spuDetailEntity = BeanUtil.copyProperties(spuDTO.getSpuDetail(), SpuDetailEntity.class);
        spuDetailEntity.setSpuId(spuEntity.getId());
        spuDetailMapper.insertSelective(spuDetailEntity);
        // 新增 sku 和 stock
        this.addSkusAndStocks(spuDTO.getSkus(),spuEntity.getId(),date);

        //事务提交完成后执行的方法
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                //发送消息
                baiduRabbitMQ.send(spuEntity.getId()+"", MqMessageConstant.SPU_ROUT_KEY_SAVE);
            }
        });

        return this.setResultSuccess();
    }

    @Override
    public Result<List<SpuDTO>> select(SpuDTO spuDTO) {
        // 分页
        if(ObjectUtil.isNotNull(spuDTO.getPage()) && ObjectUtil.isNotNull(spuDTO.getRows()) )
            PageHelper.startPage(spuDTO.getPage(),spuDTO.getRows());
        // 条件查询
        Example example = new Example(SpuEntity.class);
        //排序
        if(StringUtil.isNotEmpty(spuDTO.getSort())) example.setOrderByClause(spuDTO.getOrderByClause());
        // 构建查询条件
        Example.Criteria criteria = example.createCriteria();
        if(ObjectUtil.isNotNull(spuDTO.getSaleable()) && spuDTO.getSaleable() != 2)
            criteria.andEqualTo("saleable",spuDTO.getSaleable());
        if(StringUtil.isNotEmpty(spuDTO.getTitle()))
            criteria.andLike("title","%"+ spuDTO.getTitle() +"%");
        if(ObjectUtil.isNotNull(spuDTO.getId()))
            criteria.andEqualTo("id",spuDTO.getId());
        List<SpuEntity> list = spuMapper.selectByExample(example);

        List<SpuDTO> collect = this.getSelectConditionbyList(list);

        PageInfo<SpuEntity> pageInfo = new PageInfo<>(list);
        return this.setResult(HTTPStatus.OK,pageInfo.getTotal()+"",collect);
    }

    private List<SpuDTO> getSelectConditionbyList(List<SpuEntity> list){
        List<SpuDTO> collect = list.stream().map(spuEntity -> {
            SpuDTO spuDTO1 = BeanUtil.copyProperties(spuEntity, SpuDTO.class);
            // 通过查询出的spu数据brandID 查询 其所属品牌
            BrandDTO brandDTO = new BrandDTO();
            brandDTO.setId(spuDTO1.getBrandId());
            Result<PageInfo<BrandEntity>> brandList = brandService.select(brandDTO);
            if (!brandList.getData().getList().isEmpty() && brandList.getData().getList().size() == 1)
                spuDTO1.setBrandName(brandList.getData().getList().get(0).getName());

            // 通过查询出来的spu数据的分类目录cid1,cid2,cid3, 拼接查询出来的分类名
            String cateStr = categoryMapper.selectByIdList(
                    Arrays.asList(spuDTO1.getCid1(), spuDTO1.getCid2(), spuDTO1.getCid3()))
                    .stream().map(categoryEntity -> categoryEntity.getName()).collect(Collectors.joining("/"));
            spuDTO1.setCategoryName(cateStr);
            return spuDTO1;
        }).collect(Collectors.toList());

        return collect;
    }

    private void addSkusAndStocks(List<SkuDTO> skus, Integer spuId , Date date){
        skus.stream().forEach(skuDTO -> {
            // 新增sku
            SkuEntity skuEntity = BeanUtil.copyProperties(skuDTO, SkuEntity.class);
            skuEntity.setSpuId(spuId);
            skuEntity.setCreateTime(date);
            skuEntity.setLastUpdateTime(date);
            skuMapper.insertSelective(skuEntity);
            //新增stock
            StockEntity stockEntity = new StockEntity();
            stockEntity.setSkuId(skuEntity.getId());
            stockEntity.setStock(skuDTO.getStock());
            stockMapper.insertSelective(stockEntity);
        });
    }

    private void deleteSkusAndStocks(Integer spuId){
        // 通过spuId查询出所有的sku 集合
        Example example = new Example(SkuEntity.class);
        example.createCriteria().andEqualTo("spuId",spuId);
        //遍历查询出来的sku集合获得skuId集合
        List<Long> skuIdList = skuMapper.selectByExample(example)
                .stream().map(sku -> sku.getId()).collect(Collectors.toList());
        //删除sku
        if(!skuIdList.isEmpty()){
            skuMapper.deleteByIdList(skuIdList);
            //删除stock
            stockMapper.deleteByIdList(skuIdList);
        }
    }
}
