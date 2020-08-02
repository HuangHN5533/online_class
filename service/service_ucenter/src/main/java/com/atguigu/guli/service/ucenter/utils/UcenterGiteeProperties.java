package com.atguigu.guli.service.ucenter.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "gitee.open")
public class UcenterGiteeProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
}
