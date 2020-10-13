package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.UserDTO;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.mapper.UserMapper;
import com.baidu.shop.service.BaseApiService;
import com.baidu.shop.service.UserService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BCryptUtil;
import com.baidu.shop.utils.BeanUtil;
import com.baidu.shop.utils.LuoSiMaoUtil;
import com.baidu.shop.utils.VerifyCode;
import com.google.common.math.DoubleMath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.List;

/**
 * @ClassName UserServiceImpl
 * @Description: TODO
 * @Author jlz
 * @Date 2020-10-13 14:56
 * @Version V1.0
 **/
@RestController
@Slf4j
public class UserServiceImpl extends BaseApiService implements UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 生成图片验证码
     * @return
     */
    @Override
    public Result<JSONObject> getVerifyCode() {
        VerifyCode vc = new VerifyCode();
        BufferedImage bi = vc.getImage();
        String path = vc.outputImage(bi, vc.getText());
        return this.setResultSuccess(vc.getText());
    }

    /**
     * 发送手机验证码
     * @param userDTO
     * @return
     */
    @Override
    public Result<JSONObject> sendValidCode(UserDTO userDTO) {
        String phone = userDTO.getPhone();
        String code = this.getCode(); // 随机6位数验证码
        log.debug("手机号:{} --> 验证码:{}",phone,code);
        //LuoSiMaoUtil.sendCode(phone,code);  //发送验证码
        return this.setResultSuccess(code);
    }



    /**
     * 验证用户名或手机号 方法
     * @param value
     * @param type
     * @return
     */
    @Override
    public Result<List<UserEntity>> checkUsernameOrPhone(String value, Integer type) {
        Example example = new Example(UserEntity.class);
        Example.Criteria criteria = example.createCriteria();
        if(type == 1){
            criteria.andEqualTo("username",value);
        }else if(type == 2){
            criteria.andEqualTo("phone",value);
        }
        List<UserEntity> userList = userMapper.selectByExample(example);

        return this.setResultSuccess(userList);
    }

    /**
     * 注册方法
     * @param userDTO
     * @return
     */
    @Override
    public Result<JSONObject> register(UserDTO userDTO) {
        UserEntity userEntity = BeanUtil.copyProperties(userDTO, UserEntity.class);
        userEntity.setCreated(new Date());
        userEntity.setPassword(BCryptUtil.hashpw(userEntity.getPassword(),BCryptUtil.gensalt()));
        userMapper.insertSelective(userEntity);
        return this.setResultSuccess();
    }


    /**
     * 随机生成6位数验证码
     * 包含数字.英文大小写
     */
    private String getCode() {
        char[] arr = {48,49,50,51,52,53,54,55,56,57,//从0到9的数字
                65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,//从A到Z的数字
                97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122//从a到z的数字
        };
        int i=1;
        String code = "";
        while(i++<=6){ //循环6次，得到6位数的验证码
            char a = arr[(int)(Math.random()*62)];
            code += a;
        }
        return code;
    }
}
