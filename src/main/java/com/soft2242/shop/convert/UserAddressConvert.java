package com.soft2242.shop.convert;

import com.soft2242.shop.entity.UserShippingAddress;
import com.soft2242.shop.vo.UserAddressVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserAddressConvert {

    UserAddressConvert INSTANCE = Mappers.getMapper(UserAddressConvert.class);

    UserAddressVO convertToUserAddressVO(UserShippingAddress userShippingAddress);

    List<UserAddressVO> convertToUserAddressVOList(List<UserShippingAddress> list);
}