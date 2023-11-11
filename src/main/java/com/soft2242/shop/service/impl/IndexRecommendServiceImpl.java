package com.soft2242.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.soft2242.shop.convert.IndexRecommendConvert;
import com.soft2242.shop.entity.IndexRecommend;
import com.soft2242.shop.mapper.IndexRecommendMapper;
import com.soft2242.shop.service.IndexRecommendService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft2242.shop.vo.IndexRecommendVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ycshang
 * @since 2023-11-07
 */
@Service
public class IndexRecommendServiceImpl extends ServiceImpl<IndexRecommendMapper, IndexRecommend>
    implements IndexRecommendService {

    /**
     * 首页-热门推荐
     *
     * @return
     */
    @Override
    public List<IndexRecommendVO> getList() {
        LambdaQueryWrapper<IndexRecommend> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(IndexRecommend::getCreateTime);
        List<IndexRecommend> list = baseMapper.selectList(wrapper);
        List<IndexRecommendVO> results = IndexRecommendConvert.INSTANCE.convertToUserVoList(list);
        return results;
    }
}
