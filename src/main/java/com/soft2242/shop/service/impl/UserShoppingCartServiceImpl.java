package com.soft2242.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.soft2242.shop.common.exception.ServerException;
import com.soft2242.shop.entity.Goods;
import com.soft2242.shop.entity.UserShoppingCart;
import com.soft2242.shop.mapper.GoodsMapper;
import com.soft2242.shop.mapper.UserShoppingCartMapper;
import com.soft2242.shop.query.CartQuery;
import com.soft2242.shop.query.EditCartQuery;
import com.soft2242.shop.service.UserShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft2242.shop.vo.CartGoodsVO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ycshang
 * @since 2023-11-07
 */
@Service
@AllArgsConstructor
public class UserShoppingCartServiceImpl extends ServiceImpl<UserShoppingCartMapper, UserShoppingCart>
    implements UserShoppingCartService {
    private final GoodsMapper goodsMapper;

    /**
     * 添加购物车
     *
     * @param query
     * @return
     */
    @Override
    public CartGoodsVO addShopCart(CartQuery query) {
        // 查询商品是否存在，以及库存是否充足
        Goods goods = goodsMapper.selectById(query.getId());
        if (goods == null) {
            throw new ServerException("商品信息不存在");
        }
        if (query.getCount() > goods.getInventory()) {
            throw new ServerException("商品库存不足");
        }
        // 插入购物车信息
        UserShoppingCart userShoppingCart = new UserShoppingCart();
        userShoppingCart.setUserId(query.getUserId());
        userShoppingCart.setGoodsId(goods.getId());
        userShoppingCart.setPrice(goods.getPrice());
        userShoppingCart.setCount(query.getCount());
        userShoppingCart.setAttrsText(query.getAttrsText());
        userShoppingCart.setSelected(false);
        baseMapper.insert(userShoppingCart);

        // 返回用户添加购车信息
        CartGoodsVO goodsVO = new CartGoodsVO();
        goodsVO.setId(userShoppingCart.getId());
        goodsVO.setName(goods.getName());
        goodsVO.setAttrsText(query.getAttrsText());
        goodsVO.setPrice(userShoppingCart.getPrice());
        goodsVO.setNowPrice(goods.getPrice());
        goodsVO.setSelected(userShoppingCart.getSelected());
        goodsVO.setStock(goods.getInventory());
        goodsVO.setCount(query.getCount());
        goodsVO.setPicture(goods.getCover());
        goodsVO.setDiscount(goods.getDiscount());
        return goodsVO;
    }

    /**
     * 购物车列表
     *
     * @param userId
     * @return
     */
    @Override
    public List<CartGoodsVO> shopCartList(Integer userId) {
        List<CartGoodsVO> list = baseMapper.getCartGoodsInfo(userId);
        return list;
    }

    /**
     * 修改购物车
     *
     * @param query
     * @return
     */
    @Override
    public CartGoodsVO editCart(EditCartQuery query) {
        UserShoppingCart userShoppingCart = baseMapper.selectById(query.getId());
        if (userShoppingCart == null) {
            throw new ServerException("购物车信息不存在");
        }

        userShoppingCart.setCount(query.getCount());
        userShoppingCart.setSelected(query.getSelected());
        baseMapper.updateById(userShoppingCart);
        // 查询购物车信息
        Goods goods = goodsMapper.selectById(userShoppingCart.getGoodsId());
        if (query.getCount() > goods.getInventory()) {
            throw new ServerException(goods.getName() + "库存数量不足");
        }
        CartGoodsVO goodsVO = new CartGoodsVO();
        goodsVO.setId(userShoppingCart.getId());
        goodsVO.setName(goods.getName());
        goodsVO.setAttrsText(userShoppingCart.getAttrsText());
        goodsVO.setPrice(userShoppingCart.getPrice());
        goodsVO.setNowPrice(goods.getPrice());
        goodsVO.setSelected(userShoppingCart.getSelected());
        goodsVO.setStock(goods.getInventory());
        goodsVO.setCount(query.getCount());
        goodsVO.setPicture(goods.getCover());
        goodsVO.setDiscount(goods.getDiscount());
        return goodsVO;
    }

    /**
     * 删除/清空购物车单品
     *
     * @param userId
     * @param ids
     */
    @Override
    public void removeCartGoods(Integer userId, List<Integer> ids) {
        // 1.查询用户购物车列表
        List<UserShoppingCart> cartList =
            baseMapper.selectList(new LambdaQueryWrapper<UserShoppingCart>().eq(UserShoppingCart::getUserId, userId));
        if (cartList.size() == 0) {
            return;
        }
        // 2.与需要删除的购物车交集
        List<UserShoppingCart> deleteCartList =
            cartList.stream().filter((item -> ids.contains(item.getId()))).collect(Collectors.toList());
        // 3.删除购物车信息
        removeBatchByIds(deleteCartList);
    }

    /**
     * 购物车全选/取消全选
     *
     * @param selected
     * @param userId
     */
    @Override
    public void editCartSelected(Boolean selected, Integer userId) {
        // 1.查询用户的购物车列表
        List<UserShoppingCart> cartList =
            baseMapper.selectList(new LambdaQueryWrapper<UserShoppingCart>().eq(UserShoppingCart::getUserId, userId));
        if (cartList.size() == 0) {
            return;
        }
        // 2.批量修改购物车选中状态
        cartList.stream().forEach((item -> item.setSelected(selected)));
        // 3.数据库数据更新
        saveOrUpdateBatch(cartList);
    }
}
