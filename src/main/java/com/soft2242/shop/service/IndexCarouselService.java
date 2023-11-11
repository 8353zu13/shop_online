package com.soft2242.shop.service;

import com.soft2242.shop.entity.IndexCarousel;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ycshang
 * @since 2023-11-07
 */
public interface IndexCarouselService extends IService<IndexCarousel> {

    /**
     * 首页-广告区域
     * @param distributionSite
     * @return
     */
    List<IndexCarousel> getList(Integer distributionSite);
}
