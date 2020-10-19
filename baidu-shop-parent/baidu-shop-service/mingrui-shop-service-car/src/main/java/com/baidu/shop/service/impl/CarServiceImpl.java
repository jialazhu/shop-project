package com.baidu.shop.service.impl;

import com.baidu.shop.CarService;
import com.baidu.shop.base.Result;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.Car;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.service.BaseApiService;
import com.baidu.shop.utils.CookieUtils;
import com.baidu.shop.utils.JwtUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName CarServiceImpl
 * @Description: TODO
 * @Author jlz
 * @Date 2020-10-19 20:01
 * @Version V1.0
 **/
@RestController
public class CarServiceImpl extends BaseApiService implements CarService {

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private GoodsFeign goodsFeign;


    @Override
    public Result<JSONObject> addCar(Car car,String token) {

        try {
            UserInfo info = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            //通过skuId从数据库中获取当前商品的信息
            Result<SkuDTO> skuResult = goodsFeign.getSkuAndStockBySkuId(car.getSkuId());
            if(skuResult.getCode() == 200){
                SkuDTO skuDTO = skuResult.getData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.setResultSuccess();
    }
}
