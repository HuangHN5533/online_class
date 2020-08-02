package com.atguigu.guli.service.ucenter.service;

import java.util.Map;

/**
 * 码云授权登录接口
 */
public interface GiteeService {

    /**
     * 授权登录重定向
     * @return
     */
    String sendRedirect();

    /**
     * 获取AccessToken
     * @param code
     * @return
     */
    String getAccessToken(String code);

    /**
     * 使用AccessToken调用码云api获取用户信息
     * @param accessToken
     * @return
     */
    Map<String, Object> getUserInfo(String accessToken);
}
