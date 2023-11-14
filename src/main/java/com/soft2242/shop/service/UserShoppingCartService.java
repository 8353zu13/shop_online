package com.soft2242.shop.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.soft2242.shop.entity.UserShoppingCart;
import com.soft2242.shop.query.CartQuery;
import com.soft2242.shop.query.EditCartQuery;
import com.soft2242.shop.vo.CartGoodsVO;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ycshang
 * @since 2023-11-07
 */
public interface UserShoppingCartService extends IService<UserShoppingCart> {

    /**
     * 添加购物车
     * 
     * @param query
     * @return
     */
    CartGoodsVO addShopCart(CartQuery query);

    /**
     * 购物车列表
     * 
     * @param userId
     * @return
     */
    List<CartGoodsVO> shopCartList(Integer userId);

    /**
     * 修改购物车
     * 
     * @param query
     * @return
     */
    CartGoodsVO editCart(EditCartQuery query);

    /**
     * 删除/清空购物车单品
     * 
     * @param userId
     * @param ids
     */
    void removeCartGoods(Integer userId, List<Integer> ids);

    /**
     * 购物车全选/取消全选
     * 
     * @param selected
     * @param userId
     */
    void editCartSelected(Boolean selected, Integer userId);
}
