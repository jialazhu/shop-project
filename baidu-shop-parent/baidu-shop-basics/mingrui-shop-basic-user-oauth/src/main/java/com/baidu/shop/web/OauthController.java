package com.baidu.shop.web;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.business.OauthService;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.service.BaseApiService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.CookieUtils;
import com.baidu.shop.utils.JwtUtils;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.validate.group.MingruiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.CookieParam;

/**
 * @ClassName OauthController
 * @Description: TODO
 * @Author jlz
 * @Date 2020-10-15 19:10
 * @Version V1.0
 **/
@Api(tags = "登录接口")
@RestController
public class OauthController extends BaseApiService {

    @Autowired
    private OauthService oauthService;

    @Autowired
    private JwtConfig jwtConfig;

    @ApiOperation(value = "用户登录")
    @PostMapping("oauth/login")
    public Result<JSONObject> login(@Validated({MingruiOperation.Add.class}) @RequestBody UserEntity userEntity
            , HttpServletRequest request, HttpServletResponse response){

        String token = oauthService.login(userEntity,jwtConfig);
        // token == null 返回错误信息
        if(ObjectUtil.isNull(token)) return this.setResultError(HTTPStatus.TOKEN_VALIDATE_ERROR,"密码错误");
        // token != null 放入cookie
        CookieUtils.setCookie(request,response,jwtConfig.getCookieName(),token,jwtConfig.getCookieMaxAge(),true);

        return this.setResultSuccess();
    }

    @ApiOperation(value = "页面头部信息验证用户")
    @GetMapping("oauth/verify")
    public Result<UserEntity> verifyLogin(@CookieValue(value = "SHOP_TOKEN") String token){
        try {
            UserInfo info = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            return this.setResultSuccess(info);
        } catch (Exception e) {
            e.printStackTrace();
            return this.setResultError(HTTPStatus.USER_COOKIE_VALIDATE_ERROR,"");
        }

    }

    @ApiOperation(value = "用户退出")
    @DeleteMapping("oauth/exit")
    public Result<JSONObject> exit(HttpServletRequest request,HttpServletResponse response){

        try {
            CookieUtils.deleteCookie(request,response,jwtConfig.getCookieName());
            return this.setResultSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            return this.setResultError("");
        }

    }
}
