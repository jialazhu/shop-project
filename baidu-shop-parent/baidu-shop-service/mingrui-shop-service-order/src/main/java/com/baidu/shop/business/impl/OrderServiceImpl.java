package com.baidu.shop.business.impl;

import com.baidu.shop.base.Result;
import com.baidu.shop.business.OrderService;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.constant.ShopConstant;
import com.baidu.shop.dto.*;
import com.baidu.shop.entity.OrderDetailEntity;
import com.baidu.shop.entity.OrderEntity;
import com.baidu.shop.entity.OrderStatusEntity;
import com.baidu.shop.entity.UserAddressEntity;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.UserAddressFeign;
import com.baidu.shop.mapper.OrderDetailMapper;
import com.baidu.shop.mapper.OrderMapper;
import com.baidu.shop.mapper.OrderStatusMapper;
import com.baidu.shop.redis.repository.RedisRepository;
import com.baidu.shop.service.BaseApiService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BeanUtil;
import com.baidu.shop.utils.IdWorker;
import com.baidu.shop.utils.JwtUtils;
import com.baidu.shop.utils.ObjectUtil;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class OrderServiceImpl extends BaseApiService implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Resource
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private RedisRepository redisRepository;

    @Autowired
    private IdWorker idWorker;

    @Resource
    private UserAddressFeign userAddressFeign;

    @Resource
    private GoodsFeign goodsFeign;

    @Override
    public Result<JSONObject> updateOrderStatus(OrderStatusEntity orderStatusEntity) {
        orderStatusMapper.updateByPrimaryKeySelective(orderStatusEntity);
        return this.setResultSuccess();
    }

    @Override
    public Result<OrderInfo> getOrderById(String orderId) {
        OrderEntity orderEntity = orderMapper.selectByPrimaryKey(orderId);
        OrderInfo orderInfo = BeanUtil.copyProperties(orderEntity, OrderInfo.class);

        Example example = new Example(OrderDetailEntity.class);
        example.createCriteria().andEqualTo("orderId",orderId);
        List<OrderDetailEntity> orderDetailEntities = orderDetailMapper.selectByExample(example);

        orderInfo.setOrderDetailList(orderDetailEntities);

        OrderStatusEntity orderStatusEntity = orderStatusMapper.selectByPrimaryKey(orderId);
        orderInfo.setOrderStatusEntity(orderStatusEntity);

        return this.setResultSuccess(orderInfo);
    }

    @Transactional
    @Override
    public Result<Long> createOrder(OrderDTO orderDTO,String token) {
        try {
            UserInfo info = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            long orderId = idWorker.nextId();
            Date date = new Date();
            OrderEntity orderEntity = this.getOrderEntity(orderDTO, info, orderId, date);
            //获取订单总价格和订单详情集合
            Map<List<Long>, List<OrderDetailEntity>> map = this.getOrderDetailAndTotalPrice(orderDTO, info, orderId);
            Map<String, List<OrderDetailEntity>> OrderDetailEntityMap = new HashMap<>();
            map.forEach((k,v)->{
                orderEntity.setTotalPay(k.get(0)); //总价
                orderEntity.setActualPay(k.get(0)); //实际总价
                OrderDetailEntityMap.put("orderDetailEntityList",v);
            });
            List<OrderDetailEntity> orderDetailEntityList = OrderDetailEntityMap.get("orderDetailEntityList");
            //获得订单状态类
            OrderStatusEntity orderStatusEntity = this.getOrderStatus(orderId, date);
            //数据入库
            orderMapper.insertSelective(orderEntity);
            orderDetailMapper.insertList(orderDetailEntityList);
            orderStatusMapper.insertSelective(orderStatusEntity);
            //当前事务提交后执行的方法
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    Arrays.asList(orderDTO.getSkuIds().split(",")).stream().forEach(skuId->{
                        redisRepository.delHash(ShopConstant.REDIS_CAR_PRE+info.getId(),skuId);
                        //订单生成成功 减去库存.
                        orderDetailEntityList.stream().forEach(good ->{
                            StockDTO stockDTO = new StockDTO();
                            stockDTO.setSkuId(good.getSkuId());
                            stockDTO.setStock(good.getNum());
                            Result<JsonObject> result = goodsFeign.updateStock(stockDTO);
                            if(result.getCode() == 200) log.debug("订单生成成功.商品Id:{} 库存数量{} 删减失败",good.getSkuId(),good.getNum());
                        });

                        //发送延迟队列 查询订单是否支付成功
                    });
                }
            });
            return this.setResult(HTTPStatus.OK,"",orderId+""); //将订单id变为字符串.防止前台编译导致精度丢失
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultError("内部错误");
    }

    /**
     * 生成需要的订单类
     * @param orderDTO
     * @param info
     * @param orderId
     * @param date
     * @return
     */
    private OrderEntity getOrderEntity(OrderDTO orderDTO, UserInfo info, Long orderId,Date date){
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderId(orderId);
        orderEntity.setPaymentType(orderDTO.getPayType());
        orderEntity.setCreateTime(date);
        orderEntity.setUserId(info.getId()+"");
        orderEntity.setBuyerMessage("买家留言:很好");
        orderEntity.setBuyerNick(info.getUsername());
        orderEntity.setBuyerRate(1); //已评价
        orderEntity.setInvoiceType(0); // 无发票
        orderEntity.setSourceType(2); //pc端
        //获取收件人地址
        Result<UserAddressEntity> addressEntityResult = userAddressFeign.selectAddressById(orderDTO.getAddrId());
        if(addressEntityResult.getCode() ==200){
            UserAddressEntity data = addressEntityResult.getData();
            orderEntity.setReceiverState(data.getState());
            orderEntity.setReceiverCity(data.getCity());
            orderEntity.setReceiverDistrict(data.getDistrict());
            orderEntity.setReceiverAddress(data.getAddress());
            orderEntity.setReceiverMobile(data.getPhone());
            orderEntity.setReceiverZip(data.getZipCode());
            orderEntity.setReceiver(data.getName());
        }
        return orderEntity;
    }

    /**
     * 生成订单状态类
     * @param orderId
     * @param date
     * @return
     */
    private OrderStatusEntity getOrderStatus(Long orderId,Date date){
        //生成订单状态类
        OrderStatusEntity orderStatusEntity = new OrderStatusEntity();
        orderStatusEntity.setOrderId(orderId);
        orderStatusEntity.setCreateTime(date);
        orderStatusEntity.setStatus(1); //1:未付款
        return orderStatusEntity;
    }

    /**
     * 生成订单详情集合和总商品价格 返回一个map
     * @param orderDTO
     * @param info
     * @param orderId
     * @return
     */
    private Map<List<Long>,List<OrderDetailEntity>> getOrderDetailAndTotalPrice(OrderDTO orderDTO, UserInfo info, Long orderId){
        List<Long> totalPrices = Arrays.asList(0L);
        //获取订单商品
        List<OrderDetailEntity> orderDetailEntityList = Arrays.asList(orderDTO.getSkuIds().split(",")).stream().map(skuId -> {
            Car car = redisRepository.getHash(ShopConstant.REDIS_CAR_PRE + info.getId(), skuId, Car.class);
            if (ObjectUtil.isNull(car)) throw new RuntimeException("数据错误");
            //将商品信息 放入 订单详情类
            OrderDetailEntity orderDetailEntity = new OrderDetailEntity();
            orderDetailEntity.setOrderId(orderId);
            orderDetailEntity.setSkuId(car.getSkuId());
            orderDetailEntity.setNum(car.getNum());
            orderDetailEntity.setTitle(car.getTitle());
            orderDetailEntity.setOwnSpec(car.getOwnSpec());
            orderDetailEntity.setPrice(car.getPrice());
            orderDetailEntity.setImage(car.getImage());
            //计算 订单所有商品总价
            totalPrices.set(0,car.getPrice()*car.getNum()+totalPrices.get(0));
            return orderDetailEntity;
        }).collect(Collectors.toList());
        Map<List<Long>, List<OrderDetailEntity>> hashMap = new HashMap<>();
        hashMap.put(totalPrices,orderDetailEntityList);
        return hashMap;
    }
}