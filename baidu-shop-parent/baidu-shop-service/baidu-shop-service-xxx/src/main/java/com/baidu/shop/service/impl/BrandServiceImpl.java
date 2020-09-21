package com.baidu.shop.service.impl;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.entity.SpuEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.mapper.CategoryBrandMapper;
import com.baidu.shop.mapper.SpuMapper;
import com.baidu.shop.service.BaseApiService;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.utils.BeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.PinyinUtil;
import com.baidu.shop.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName BrandServiceImpl
 * @Description: TODO
 * @Author jlz
 * @Date 2020-08-31 14:40
 * @Version V1.0
 **/
@RestController
public class BrandServiceImpl extends BaseApiService implements BrandService {

    @Resource
    private BrandMapper brandMapper;
    @Resource
    private CategoryBrandMapper categoryBrandMapper;
    @Resource
    private SpuMapper spuMapper;

    @Override
    public Result<List<BrandEntity>> getBrandByIdList(String brandids) {
        List<Integer> brandIdList = Arrays.asList(brandids.split(",")).stream()
                .map(brandid -> Integer.parseInt(brandid)).collect(Collectors.toList());
        List<BrandEntity> list = brandMapper.selectByIdList(brandIdList);
        return this.setResultSuccess(list);
    }

    @Override
    public Result<List<BrandEntity>> getBrandByCate(Integer cid) {
        if(ObjectUtil.isNull(cid)) return this.setResultError("cid不能为空");
        List<BrandEntity> list = brandMapper.getBrandByCate(cid);
        return this.setResultSuccess(list);
    }

    @Override
    public Result<PageInfo<BrandEntity>> select(BrandDTO brandDTO) {
        // 分页
        if(ObjectUtil.isNotNull(brandDTO.getPage()) && ObjectUtil.isNotNull(brandDTO.getRows()) )
            PageHelper.startPage(brandDTO.getPage(),brandDTO.getRows());
        //条件查询
        Example example = new Example(BrandEntity.class);
        Example.Criteria criteria = example.createCriteria();
        //排序
        if(StringUtil.isNotEmpty(brandDTO.getSort())) example.setOrderByClause(brandDTO.getOrderByClause());
        // 根据id 查询
        if(ObjectUtil.isNotNull(brandDTO.getId())) criteria.andEqualTo("id",brandDTO.getId());
        //根据name 模糊查询
        if(StringUtil.isNotEmpty(brandDTO.getName())) criteria.andLike("name","%"+brandDTO.getName()+"%");
        PageInfo<BrandEntity> pageInfo = new PageInfo<>(brandMapper.selectByExample(example));
        return this.setResultSuccess(pageInfo);
    }

    @Transactional
    @Override
    public Result<JsonObject> save(BrandDTO brandDTO) {
        // 自动识别 品牌首字母(大写字母)
        // 获得品牌名字的第一个字符.通过工具类转换成拼音.并截取第一个字母并大写
        String upperCase = PinyinUtil.getUpperCase(String.valueOf(brandDTO.getName().charAt(0)), PinyinUtil.TO_FIRST_CHAR_PINYIN);
        // bean copy
        BrandEntity brandEntity = BeanUtil.copyProperties(brandDTO, BrandEntity.class);
        // 将自动识别的首字母赋给对象因为实体类是char类型.所以需要转换.因为首字母是一位字符.所以通过charAt(0)
        brandEntity.setLetter(upperCase.charAt(0));
        // 新增
        brandMapper.insertSelective(brandEntity);
        // 新增关系
        this.insertCategoryAndBrand(brandDTO,brandEntity);
        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JsonObject> edit(BrandDTO brandDTO) {
        // 自动识别 品牌首字母(大写字母)
        // 获得品牌名字的第一个字符.通过工具类转换成拼音.并截取第一个字母并大写
        String upperCase = PinyinUtil.getUpperCase(String.valueOf(brandDTO.getName().charAt(0)), PinyinUtil.TO_FIRST_CHAR_PINYIN);
        // bean copy
        BrandEntity brandEntity = BeanUtil.copyProperties(brandDTO, BrandEntity.class);
        // 将自动识别的首字母赋给对象因为实体类是char类型.所以需要转换.因为首字母是一位字符.所以通过charAt(0)
        brandEntity.setLetter(upperCase.charAt(0));
        //修改
        brandMapper.updateByPrimaryKeySelective(brandEntity);
        //根据brandid删除关系表中的数据
        this.deleteCategoryAndBrand(brandEntity.getId());
        // 新增关系
        this.insertCategoryAndBrand(brandDTO,brandEntity);
        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<List<BrandEntity>> delete(Integer id) {
        //判断品牌是否被spu绑定 如果绑定不能删除
        Example example = new Example(SpuEntity.class);
        example.createCriteria().andEqualTo("brandId",id);
        List<SpuEntity> spuEntities = spuMapper.selectByExample(example);
        if(!spuEntities.isEmpty()) return this.setResultError("已绑定spu,不能删除");
        //删除
        brandMapper.deleteByPrimaryKey(id);
        //删除关系表
        this.deleteCategoryAndBrand(id);
        return this.setResultSuccess();
    }

    private void insertCategoryAndBrand(BrandDTO brandDTO, BrandEntity brandEntity){
        // 将关系新增到关系表中
        if( brandDTO.getCategory().contains(",")){ // 判断是否包含逗号 分隔
            // 分割获得分类的id数组 通过arrays.aslist将数组变成list通过stream流 map遍历. 返回一个经过collect转换的list
            // getCategoryBrand(brandEntity.getId(), cate) 调用封装的方法.返回一个 categoryBrandEntity 对象
            List<CategoryBrandEntity> collect = Arrays.asList(brandDTO.getCategory().split(",")).stream()
                    .map(cate -> getCategoryBrand(brandEntity.getId(), cate) ).collect(Collectors.toList());
            //批量新增操作
            categoryBrandMapper.insertList(collect);
        }else{
            // 新增操作
            categoryBrandMapper.insertSelective(getCategoryBrand(brandEntity.getId(),brandDTO.getCategory()));
        }
    }

    private CategoryBrandEntity getCategoryBrand(Integer brandId, String categoryId){
        CategoryBrandEntity categoryBrandEntity = new CategoryBrandEntity(); // new
        categoryBrandEntity.setBrandId(brandId); // 赋值品牌id brandid
        categoryBrandEntity.setCategoryId(StringUtil.toInteger(categoryId)); // 赋值分类id categoryId
        return categoryBrandEntity ;
    }

    private void deleteCategoryAndBrand(Integer id){
        Example example = new Example(CategoryBrandEntity.class);
        example.createCriteria().andEqualTo("brandId",id);
        categoryBrandMapper.deleteByExample(example);
    }


}
