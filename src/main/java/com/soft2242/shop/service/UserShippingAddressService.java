package com.soft2242.shop.service;

import com.soft2242.shop.entity.UserShippingAddress;
import com.baomidou.mybatisplus.extension.service.IService;
import com.soft2242.shop.vo.AddressVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ycshang
 * @since 2023-11-07
 */
public interface UserShippingAddressService extends IService<UserShippingAddress> {

    /**
     * 添加收获地址
     * 
     * @param addressVO
     * @return
     */
    Integer saveShippingAddress(AddressVO addressVO);

    /**
     * 修改收货地址
     * 
     * @param addressVO
     * @return
     */
    Integer editShippingAddress(AddressVO addressVO);

    /**
     * 获取收获地址列表
     *
     * @return
     */
    List<AddressVO> getUserShippingAddressList(Integer userId);

    /**
     * 根据id删除收货地址
     * 
     * @param id
     * @return
     */
    void removeShippingAddress(Integer id);

    /**
     * 根据Id获取收货地址详情
     * 
     * @param id
     * @return
     */
    AddressVO getAddressInfoById(Integer id);
}
