package com.soft2242.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.soft2242.shop.common.exception.ServerException;
import com.soft2242.shop.convert.UserOrderDetailConvert;
import com.soft2242.shop.entity.Goods;
import com.soft2242.shop.entity.UserOrder;
import com.soft2242.shop.entity.UserOrderGoods;
import com.soft2242.shop.entity.UserShippingAddress;
import com.soft2242.shop.enums.OrderStatusEnum;
import com.soft2242.shop.mapper.GoodsMapper;
import com.soft2242.shop.mapper.UserOrderGoodsMapper;
import com.soft2242.shop.mapper.UserOrderMapper;
import com.soft2242.shop.mapper.UserShippingAddressMapper;
import com.soft2242.shop.query.OrderGoodsQuery;
import com.soft2242.shop.service.UserOrderGoodsService;
import com.soft2242.shop.service.UserOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft2242.shop.vo.OrderDetailVO;
import com.soft2242.shop.vo.UserOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ycshang
 * @since 2023-11-07
 */
@Service
public class UserOrderServiceImpl extends ServiceImpl<UserOrderMapper, UserOrder> implements UserOrderService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private UserShippingAddressMapper userShippingAddressMapper;

    @Autowired
    private UserOrderGoodsMapper userOrderGoodsMapper;

    @Autowired
    private UserOrderGoodsService userOrderGoodsService;

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> cancelTask;

    @Async
    public void scheduleOrderCancel(UserOrder userOrder) {
        cancelTask = executorService.schedule(() -> {
            // Integer转Byte
            if (userOrder.getStatus() == OrderStatusEnum.WAITING_FOR_PAYMENT.getValue().byteValue()) {
                userOrder.setStatus(OrderStatusEnum.CANCELLED.getValue().byteValue());
                baseMapper.updateById(userOrder);
            }
        }, 30, TimeUnit.MINUTES);
    }

    public void cancelScheduledTask() {
        if (cancelTask != null && !cancelTask.isDone()) {
            // 取消定时任务
            cancelTask.cancel(true);
        }
    }

    /**
     * 提交订单
     *
     * @param orderVO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addGoodsOrder(UserOrderVO orderVO) {
        // 1.声明订单总支付费用、总运费、总购买件数
        BigDecimal totalPrice = new BigDecimal(0);
        Integer totalCount = 0;
        BigDecimal totalFreight = new BigDecimal(0);
        UserOrder userOrder = new UserOrder();
        userOrder.setUserId(orderVO.getUserId());
        userOrder.setAddressId(orderVO.getAddressId());
        // 订单编号使用uuid随机生成不重复的编号
        userOrder.setOrderNumber(UUID.randomUUID().toString());
        userOrder.setDeliveryTimeType(orderVO.getDeliveryType().byteValue());
        // 提交订单默认状态为待付款
        userOrder.setStatus(OrderStatusEnum.WAITING_FOR_PAYMENT.getValue().byteValue());
        if (orderVO.getBuyerMessage() != null) {
            userOrder.setBuyerMessage(orderVO.getBuyerMessage());
        }
        userOrder.setPayType(orderVO.getPayType().byteValue());
        userOrder.setPayChannel(orderVO.getPayChannel().byteValue());
        baseMapper.insert(userOrder);
        // 异步取消创建的订单，如果订单创建30分钟后用户没有付款，修改订单状态为取消
        scheduleOrderCancel(userOrder);
        List<UserOrderGoods> orderGoodsList = new ArrayList<>();
        // 遍历用户购买的商品列表，订单-商品表批量添加数据
        for (OrderGoodsQuery goodsVO : orderVO.getGoods()) {
            Goods goods = goodsMapper.selectById(goodsVO.getId());
            if (goodsVO.getCount() > goods.getInventory()) {
                throw new ServerException(goods.getName() + "库存数量不足");
            }
            UserOrderGoods userOrderGoods = new UserOrderGoods();
            userOrderGoods.setGoodsId(goods.getId());
            userOrderGoods.setName(goods.getName());
            userOrderGoods.setCover(goods.getCover());
            userOrderGoods.setOrderId(userOrder.getId());
            userOrderGoods.setCount(goodsVO.getCount());
            userOrderGoods.setAttrsText(goodsVO.getSkus());
            userOrderGoods.setFreight(goods.getFreight());
            userOrderGoods.setPrice(goods.getPrice());

            // 计算总付款金额，使用BigDecimal，避免精度确实
            BigDecimal freight = new BigDecimal(userOrderGoods.getFreight().toString());
            BigDecimal goodsPrice = new BigDecimal(userOrderGoods.getPrice().toString());
            BigDecimal count = new BigDecimal(userOrderGoods.getCount().toString());
            // 减库存
            goods.setInventory(goods.getInventory() - goodsVO.getCount());
            // 增加销量
            goods.setSalesCount(goodsVO.getCount());
            BigDecimal price = goodsPrice.multiply(count).add(freight);
            totalPrice = totalPrice.add(price);
            totalCount += goodsVO.getCount();
            totalFreight = totalFreight.add(freight);
            orderGoodsList.add(userOrderGoods);
            goodsMapper.updateById(goods);
        }

        userOrderGoodsService.batchUserOrderGoods(orderGoodsList);
        userOrder.setTotalPrice(totalPrice.doubleValue());
        userOrder.setTotalCount(totalCount);
        userOrder.setTotalFreight(totalFreight.doubleValue());
        baseMapper.updateById(userOrder);
        return userOrder.getId();
    }

    /**
     * 订单详情
     *
     * @param id
     * @return
     */
    @Override
    public OrderDetailVO getOrderDetail(Integer id) {
        // 1.订单详情
        UserOrder userOrder = baseMapper.selectById(id);
        if (userOrder == null) {
            throw new ServerException("订单信息不存在");
        }
        OrderDetailVO orderDetailVO = UserOrderDetailConvert.INSTANCE.convertToOrderDetailVO(userOrder);
        orderDetailVO.setTotalMoney(userOrder.getTotalPrice());

        // 2.收货人信息
        UserShippingAddress userShippingAddress = userShippingAddressMapper.selectById(userOrder.getAddressId());

        if (userShippingAddress == null) {
            throw new ServerException("收货地址信息不存在");
        }
        orderDetailVO.setReceiverContact(userShippingAddress.getReceiver());
        orderDetailVO.setReceiverMobile(userShippingAddress.getContact());
        orderDetailVO.setReceiverAddress(userShippingAddress.getAddress());

        // 3.商品集合
        List<UserOrderGoods> list = userOrderGoodsMapper
            .selectList(new LambdaQueryWrapper<UserOrderGoods>().eq(UserOrderGoods::getOrderId, id));

        orderDetailVO.setSkus(list);
        // 订单截至订单创建30分钟之后
        orderDetailVO.setPayLatestTime(userOrder.getCloseTime().plusMinutes(30));

        if (orderDetailVO.getPayLatestTime().isAfter(LocalDateTime.now())) {
            Duration duration = Duration.between(LocalDateTime.now(), orderDetailVO.getPayLatestTime());
            // 倒计时秒数
            orderDetailVO.setCountdown(duration.toMillisPart());
        }
        return orderDetailVO;
    }
}
