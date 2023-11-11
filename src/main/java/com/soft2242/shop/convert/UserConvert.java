package com.soft2242.shop.convert;

import com.soft2242.shop.entity.User;
import com.soft2242.shop.vo.LoginResultVO;
import org.apache.ibatis.annotations.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserConvert {
    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);

    LoginResultVO convertToLoginResultVO(User user);
}