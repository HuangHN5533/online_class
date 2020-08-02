package com.atguigu.guli.service.ucenter.config;

import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * 微信登录需要
 * spring-session-data-redis配置文件
 */
//@Configuration
//@EnableRedisHttpSession
public class HttpSessionConfig {

    //可选配置
//    @Bean
    public CookieSerializer cookieSerializer(){
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        //将spring session默认的cookie key从SESSION替换为原生的JSESSIONID
        serializer.setCookieName("JSESSIONID");
        //把cookiePath设置为根路径
        serializer.setCookiePath("/");
        //配置相关正则表达式，可以达到同父域下单点登录的效果
        serializer.setDomainNamePattern("^.+?\\.(\\w+\\.[a-z]+)$");
        return serializer;
    }
}
