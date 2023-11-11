package com.soft2242.shop.service;

import com.soft2242.shop.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.soft2242.shop.query.UserLoginQuery;
import com.soft2242.shop.vo.LoginResultVO;
import com.soft2242.shop.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * 根据用户id 获取用户信息
     * @param userId
     * @return
     */
    User getUserInfo(Integer userId);

    /**
     * 修改用户信息
     * @param userVO
     * @return
     */
    UserVO editUserInfo(UserVO userVO);

    /**
     * 修改用户头像
     * @param userId
     * @param file
     * @return
     */
    String editUserAvatar(Integer userId , MultipartFile file);
}
