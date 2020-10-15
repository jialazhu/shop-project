package com.baidu.shop.business.impl;

import com.baidu.shop.business.OauthService;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.mapper.OauthMapper;
import com.baidu.shop.utils.BCryptUtil;
import com.baidu.shop.utils.JwtUtils;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName OauthServiceImpl
 * @Description: TODO
 * @Author jlz
 * @Date 2020-10-15 19:14
 * @Version V1.0
 **/
@Service
public class OauthServiceImpl implements OauthService {

    @Resource
    private OauthMapper oauthMapper;

    @Override
    public String login(UserEntity userEntity, JwtConfig jwtConfig) {

        String token = null;

        Example example = new Example(UserEntity.class);
        Example.Criteria criteria = example.createCriteria();
        // 判断传入的username 是不是数值类型 &&  是不是手机号格式
        if(userEntity.getUsername().matches("\\d{11}") && userEntity.getUsername().matches("[1][3578]\\d{9}")){
            criteria.andEqualTo("phone",userEntity.getUsername());
        }else{
            criteria.andEqualTo("username",userEntity.getUsername());
        }
        List<UserEntity> list = oauthMapper.selectByExample(example);
        //判断只能查询到一条数据
        if(list.size() == 1){
            UserEntity entity = list.get(0);
            //密码校验
            if(BCryptUtil.checkpw(userEntity.getPassword(),entity.getPassword())){
                //生成token
                try {
                    token = JwtUtils.generateToken(new UserInfo(entity.getId(), entity.getUsername()), jwtConfig.getPrivateKey(), jwtConfig.getExpire());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return token;
    }
}
