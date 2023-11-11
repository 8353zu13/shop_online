package com.soft2242.shop.service.impl;

import static com.soft2242.shop.common.constant.APIConstant.*;

import com.soft2242.shop.convert.UserConvert;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.soft2242.shop.common.exception.ServerException;
import com.soft2242.shop.common.utils.GeneratorCodeUtils;
import com.soft2242.shop.common.utils.JWTUtils;

import com.soft2242.shop.entity.User;
import com.soft2242.shop.mapper.UserMapper;
import com.soft2242.shop.query.UserLoginQuery;
import com.soft2242.shop.service.RedisService;
import com.soft2242.shop.service.UserService;
import com.soft2242.shop.vo.LoginResultVO;
import com.soft2242.shop.vo.UserTokenVO;

import lombok.AllArgsConstructor;

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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final RedisService service;

    /**
     * 用户登录
     *
     * @param query
     * @return
     */
    @Override
    public LoginResultVO login(UserLoginQuery query) {
        // 1.获取openId
        String url = "http://api.weixin.qq.com/sns.jscode2session?" + "appid=" + APP_ID + "&secret=" + APP_SECRET
            + "&js_code=" + query.getCode() + "&grant_type=authorization_code";

        RestTemplate restTemplate = new RestTemplate();
        String openIdResult = restTemplate.getForObject(url, String.class);
        System.out.println(url);
        if (StringUtils.contains(openIdResult, WX_ERR_CODE)) {
            throw new ServerException(("openId获取失败") + openIdResult);
        }

        // 2.解析返回的数据
        JSONObject jsonObject = JSON.parseObject(openIdResult);
        String openId = jsonObject.getString(WX_OPENID);
        User user = baseMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getOpenId, openId));

        // 3.判断用户是否存在,如果用户不存在直接注册新用户
        if (user == null) {
            user = new User();
            String account = "用户" + GeneratorCodeUtils.generateCode();
            user.setAvatar(DEFAULT_AVATAR);
            user.setAccount(account);
            user.setNickname(account);
            user.setOpenId(openId);
            user.setMobile("''");
            baseMapper.insert(user);
        }
        LoginResultVO userVO = UserConvert.INSTANCE.convertToLoginResultVO(user);

        // 4.生成token，存入redis并设置过期时间
        UserTokenVO tokenVO = new UserTokenVO(userVO.getId());

        String token = JWTUtils.generateToken(JWT_SECRET, tokenVO.toMap());
        service.set(APP_NAME + userVO.getId(), token, APP_TOKEN_EXPIRE_TIME);
        userVO.setToken(token);
        return userVO;
    }
}
