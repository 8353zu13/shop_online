package com.soft2242.shop.service;

import com.soft2242.shop.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.soft2242.shop.query.UserLoginQuery;
import com.soft2242.shop.vo.LoginResultVO;
import com.soft2242.shop.vo.UserVO;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author ycshang
 * @since 2023-11-07
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     * 
     * @param query
     * @return
     */
    LoginResultVO login(UserLoginQuery query);
}
