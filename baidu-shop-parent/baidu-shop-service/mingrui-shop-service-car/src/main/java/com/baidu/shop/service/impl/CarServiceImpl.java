package com.baidu.shop.service.impl;

import com.baidu.shop.CarService;
import com.baidu.shop.base.Result;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.constant.ShopConstant;
import com.baidu.shop.dto.Car;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.SkuEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecificationFeign;
import com.baidu.shop.redis.repository.RedisRepository;
import com.baidu.shop.service.BaseApiService;
import com.baidu.shop.utils.JSONUtil;
import com.baidu.shop.utils.JwtUtils;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @ClassName CarServiceImpl
 * @Description: TODO
 * @Author jlz
 * @Date 2020-10-19 20:01
 * @Version V1.0
 **/
@RestController
@Slf4j
public class CarServiceImpl extends BaseApiService implements CarService {

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private SpecificationFeign specificationFeign;

    @Autowired
    private RedisRepository redisRepository;

    @Override
    public Result<List<Car>> deleteCar(String skuId, String token) {
        try {
            UserInfo info = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            redisRepository.delHash(ShopConstant.REDIS_CAR_PRE + info.getId(),skuId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultSuccess();
    }

    @Override
    public Result<List<Car>> updateNum(Long skuId,Integer type, String token) {

        try {
            UserInfo info = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            if(ObjectUtil.isNotNull(type)){
                Car redisCar = redisRepository.getHash(ShopConstant.REDIS_CAR_PRE + info.getId(), skuId + "", Car.class);
                redisCar.setNum(type == ShopConstant.CAR_NUM_TYPE_INCREMENT ?redisCar.getNum()+1:redisCar.getNum()-1);
                //进购物车 reids
                redisRepository.setHash(ShopConstant.REDIS_CAR_PRE+info.getId(),skuId+"",JSONUtil.toJsonString(redisCar));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.setResultSuccess();
    }

    @Override
    public Result<List<Car>> getCar(String token) {
        try {
            UserInfo info = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            Map<String, String> redisRepositoryHash = redisRepository.getHash(ShopConstant.REDIS_CAR_PRE + info.getId());
            List<Car> cars = new ArrayList<>();
            redisRepositoryHash.forEach((k,v)->{
                Car car = JSONUtil.toBean(v, Car.class);
                cars.add(car);
            });
            return this.setResultSuccess(cars);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultError("");
    }

    @Override
    public Result<JSONObject> mergeCar(String skusList, String token) {
        //skusList 是集合类型 通过alibaba.json转换
        List<Car> carList = com.alibaba.fastjson.JSONObject.parseArray(skusList, Car.class);
        carList.forEach(car -> this.addCar(car,token));
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> addCar(Car car,String token) {

        try {
            UserInfo info = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            //查询redis购物车中是否有相同的商品
            Car redisCar = redisRepository.getHash(ShopConstant.REDIS_CAR_PRE + info.getId(), car.getSkuId() + "", Car.class);
            Car saveCar = null;
            if(ObjectUtil.isNotNull(redisCar)){ //不为空说明有相同的商品 让数量相加
                redisCar.setNum(redisCar.getNum()+car.getNum());
                saveCar = redisCar;
                log.debug("相同商品,key:{},skuKey:{},num:{}",ShopConstant.REDIS_CAR_PRE + info.getId(),car.getSkuId() + "",redisCar.getNum());
            }else{ // 为空说明没有相同的商品.正常新增
                //通过skuId从数据库中获取当前商品的信息
                Result<SkuEntity> skuResult = goodsFeign.getSkuBySkuId(car.getSkuId());
                if(skuResult.getCode() == 200){
                    SkuEntity sku = skuResult.getData();
                    car.setImage(StringUtil.isNotEmpty(sku.getImages())?sku.getImages().split(",")[0]:"");
                    car.setPrice(sku.getPrice());
                    car.setTitle(sku.getTitle());
                    car.setUserId(info.getId());
                    //处理一下规格参数名
                    Map<String, String> ownSpecMap = JSONUtil.toMapValueString(sku.getOwnSpec());
                    Map<String, String> oldOwnSpecMap = new HashMap<>();
                    ownSpecMap.forEach((k,v) ->{
                        Result<SpecParamEntity> paramResult = specificationFeign.selectParamById(Integer.valueOf(k));
                        if(paramResult.getCode() == 200){
                            oldOwnSpecMap.put(paramResult.getData().getName(),v);
                        }
                    });
                    car.setOwnSpec(JSONUtil.toJsonString(oldOwnSpecMap));
                    saveCar = car;
                    log.debug("新增商品:Key:{},skuKey:{},value:{}",
                            ShopConstant.REDIS_CAR_PRE+info.getId(),car.getSkuId()+"",JSONUtil.toJsonString(car));
                }
            }
            //进购物车 reids
            redisRepository.setHash(ShopConstant.REDIS_CAR_PRE+info.getId(),saveCar.getSkuId()+"",JSONUtil.toJsonString(saveCar));
            log.debug("商品入车成功:Key:{},skuKey:{}",ShopConstant.REDIS_CAR_PRE+car.getUserId(),car.getSkuId()+"");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.setResultSuccess();
    }
}
