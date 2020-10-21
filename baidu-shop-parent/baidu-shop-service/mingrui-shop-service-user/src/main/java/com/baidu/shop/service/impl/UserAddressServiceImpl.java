package com.baidu.shop.service.impl;

import com.baidu.shop.base.Result;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.UserAddressDTO;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.UserAddressEntity;
import com.baidu.shop.mapper.UserAddressMapper;
import com.baidu.shop.service.BaseApiService;
import com.baidu.shop.service.UserAddressService;
import com.baidu.shop.utils.BeanUtil;
import com.baidu.shop.utils.JwtUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName UserAddressServiceImpl
 * @Description: TODO
 * @Author jlz
 * @Date 2020-10-21 15:20
 * @Version V1.0
 **/
@RestController
public class UserAddressServiceImpl extends BaseApiService implements UserAddressService {

    @Autowired
    private JwtConfig jwtConfig;

    @Resource
    private UserAddressMapper userAddressMapper;

    @Transactional
    @Override
    public Result<JSONObject> saveAddr(UserAddressDTO userAddressDTO,String token) {
        try {
            UserInfo info = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            UserAddressEntity userAddressEntity = BeanUtil.copyProperties(userAddressDTO, UserAddressEntity.class);
            userAddressEntity.setUserId(info.getId());
            userAddressMapper.insertSelective(userAddressEntity);
            return this.setResultSuccess();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultError("内部错误");
    }

    @Transactional
    @Override
    public Result<JSONObject> editAddr(UserAddressDTO userAddressDTO) {
        UserAddressEntity userAddressEntity = BeanUtil.copyProperties(userAddressDTO, UserAddressEntity.class);
        userAddressMapper.updateByPrimaryKeySelective(userAddressEntity);
        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JSONObject> delAddr(Integer id) {
        userAddressMapper.deleteByPrimaryKey(id);
        return this.setResultSuccess();
    }

    @Override
    public Result<UserAddressEntity> selectAddressById(Integer id) {
        UserAddressEntity addressEntity = userAddressMapper.selectByPrimaryKey(id);
        return this.setResultSuccess(addressEntity);
    }

    @Override
    public Result<List<UserAddressEntity>> selectAddressByuserId(String token) {
        try {
            UserInfo info = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            //通过用户Id 查询地址
            Example example = new Example(UserAddressEntity.class);
            example.createCriteria().andEqualTo("userId",info.getId());
            List<UserAddressEntity> addressEntityList = userAddressMapper.selectByExample(example);
            return this.setResultSuccess(addressEntityList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultError("");
    }
}
