package com.soft2242.shop.mapper;

import com.soft2242.shop.entity.Goods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.soft2242.shop.vo.UserOrderGoodsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author ycshang
 * @since 2023-11-07
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    /**
     * 根据订单id 查询商品信息列表
     * 
     * @param id
     * @return
     */
    List<UserOrderGoodsVO> getGoodsListByOrderId(@Param("id") Integer id);
}
