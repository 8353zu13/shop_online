package com.soft2242.shop.service;

import com.soft2242.shop.common.result.PageResult;
import com.soft2242.shop.entity.Goods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.soft2242.shop.query.Query;
import com.soft2242.shop.query.RecommendByTabGoodsQuery;
import com.soft2242.shop.vo.GoodsVO;
import com.soft2242.shop.vo.IndexTabRecommendVO;
import com.soft2242.shop.vo.RecommendGoodsVO;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ycshang
 * @since 2023-11-07
 */
public interface GoodsService extends IService<Goods> {

    /**
     * 热门推荐
     * 
     * @param query
     * @return
     */
    IndexTabRecommendVO getTabRecommendGoodsByTabId(RecommendByTabGoodsQuery query);

    /**
     * 首页推荐-猜你喜欢（分页）
     * 
     * @param query
     * @return
     */
    PageResult<RecommendGoodsVO> getRecommendGoodsByPage(Query query);

    /**
     * 根据id 获取商品详情
     * @param id
     * @return
     */
    GoodsVO getGoodsDetail(Integer id);
}
