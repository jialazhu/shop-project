package com.baidu.shop.service.impl;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.mapper.SpecGroupMapper;
import com.baidu.shop.mapper.SpecParamMapper;
import com.baidu.shop.service.BaseApiService;
import com.baidu.shop.service.SpecificationService;
import com.baidu.shop.utils.BeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.google.gson.JsonObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName SpecificationServiceImpl
 * @Description: TODO
 * @Author jlz
 * @Date 2020-09-03 12:00
 * @Version V1.0
 **/
@RestController
public class SpecificationServiceImpl extends BaseApiService implements SpecificationService {

    @Resource
    private SpecGroupMapper specGroupMapper;

    @Resource
    private SpecParamMapper specParamMapper;

    @Override
    public Result<List<SpecGroupEntity>> selectGroup(SpecGroupDTO specGroupDTO) {
        Example example = new Example(SpecGroupEntity.class);
        if(ObjectUtil.isNotNull(specGroupDTO.getCid())) example.createCriteria().andEqualTo("cid",specGroupDTO.getCid());
        List<SpecGroupEntity> list = specGroupMapper.selectByExample(example);
        return this.setResultSuccess(list);
    }

    @Transactional
    @Override
    public Result<JsonObject> saveGroup(SpecGroupDTO specGroupDTO) {
        SpecGroupEntity groupEntity = BeanUtil.copyProperties(specGroupDTO, SpecGroupEntity.class);
        specGroupMapper.insertSelective(groupEntity);
        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JsonObject> editGroup(SpecGroupDTO specGroupDTO) {
        SpecGroupEntity groupEntity = BeanUtil.copyProperties(specGroupDTO, SpecGroupEntity.class);
        specGroupMapper.updateByPrimaryKeySelective(groupEntity);
        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JsonObject> deleteGroup(Integer id) {
        if(ObjectUtil.isNull(id)) return this.setResultError("无效id");
        //验证组下是否还有参数数据 通过groupid 查询参数表中 groupId = id 的数据集合
        Example example = new Example(SpecParamEntity.class);
        example.createCriteria().andEqualTo("groupId", id);
        List<SpecParamEntity> groupList = specParamMapper.selectByExample(example);
        //判断list如果不为空 则不能删除 直接return
        if(!groupList.isEmpty()) return this.setResultError("规格组中包含数据.无法删除");
        //删除操作
        specGroupMapper.deleteByPrimaryKey(id);
        return this.setResultSuccess();
    }


    @Override
    public Result<List<SpecParamEntity>> selectParam(SpecParamDTO specParamDTO) {

        Example example = new Example(SpecParamEntity.class);
        Example.Criteria criteria = example.createCriteria();
        if(ObjectUtil.isNotNull(specParamDTO.getGroupId())) criteria.andEqualTo("groupId",specParamDTO.getGroupId());
        if(ObjectUtil.isNotNull(specParamDTO.getCid())) criteria.andEqualTo("cid",specParamDTO.getCid());
        if(ObjectUtil.isNotNull(specParamDTO.getSearching())) criteria.andEqualTo("searching",specParamDTO.getSearching());
        List<SpecParamEntity> list = specParamMapper.selectByExample(example);
        return this.setResultSuccess(list);
    }

    @Transactional
    @Override
    public Result<JsonObject> saveParam(SpecParamDTO specParamDTO) {
        specParamMapper.insertSelective(BeanUtil.copyProperties(specParamDTO,SpecParamEntity.class));
        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JsonObject> editParam(SpecParamDTO specParamDTO) {
        specParamMapper.updateByPrimaryKeySelective(BeanUtil.copyProperties(specParamDTO,SpecParamEntity.class));
        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JsonObject> deleteParam(Integer id) {
        if(ObjectUtil.isNull(id)) return this.setResultError("无效id");
        specParamMapper.deleteByPrimaryKey(id);
        return this.setResultSuccess();
    }
}
