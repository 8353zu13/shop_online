package com.soft2242.shop.service;

import com.soft2242.shop.entity.UserOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.soft2242.shop.entity.UserOrderGoods;
import com.soft2242.shop.vo.UserOrderVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ycshang
 * @since 2023-11-07
 */
public interface UserOrderService extends IService<UserOrder> {

    /**
     * 提交订单
     * 
     * @param orderVO
     * @return
     */
    Integer addGoodsOrder(UserOrderVO orderVO);

}
