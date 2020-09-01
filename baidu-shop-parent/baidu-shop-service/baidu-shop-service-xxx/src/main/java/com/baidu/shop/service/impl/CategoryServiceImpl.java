package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.mapper.CategoryMapper;
import com.baidu.shop.service.BaseApiService;
import com.baidu.shop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: baidu-shop-parent
 * @description:
 * @author: Mr.Zheng
 * @create: 2020-08-27 20:51
 **/
@RestController
public class CategoryServiceImpl extends BaseApiService implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public Result<List<CategoryEntity>> getCategoryByPid(Integer pid) {

        CategoryEntity categoryEntity = new CategoryEntity();

        categoryEntity.setParentId(pid);

        List<CategoryEntity> list = categoryMapper.select(categoryEntity);

        return this.setResultSuccess(list);
    }

    @Transactional
    @Override
    public Result<JSONObject> addCategory(CategoryEntity categoryEntity) {
        //新增时将添加数据的父节点数据修改为父节点
        CategoryEntity categoryEntity1 = new CategoryEntity();
        categoryEntity1.setId(categoryEntity.getParentId());
        categoryEntity1.setIsParent(1);
        categoryMapper.updateByPrimaryKeySelective(categoryEntity1);
        categoryMapper.insertSelective(categoryEntity);
        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JSONObject> editCategory(CategoryEntity categoryEntity) {
        categoryMapper.updateByPrimaryKeySelective(categoryEntity);
        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JSONObject> deleteCategory(Integer id) {
        if (null == id && id == 0) { // 为null.说明没有数据.id无效
            return this.setResultError("id无效");
        }
        //通过id查询是否有数据.
        CategoryEntity categoryEntity = categoryMapper.selectByPrimaryKey(id);

        //判断数据是否是父节点
        if(categoryEntity.getIsParent() == 1){
            return this.setResultError("不能删除父节点");
        }
        //通过查询出的数据的parentid查询当前节点 父节点 的 子节点 数量
        Example example = new Example(CategoryEntity.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("parentId",categoryEntity.getParentId());

        List<CategoryEntity> list = categoryMapper.selectByExample(example);
        //判断list的size是否为1
        if(list.size() == 1){ // 修改父节点的状态.将isParent修改为0
            CategoryEntity entity = new CategoryEntity();
            entity.setIsParent(0);
            entity.setId(categoryEntity.getParentId());
            categoryMapper.updateByPrimaryKeySelective(entity);
        }
        //删除
        categoryMapper.deleteByPrimaryKey(id);
        return this.setResultSuccess();
    }
}
