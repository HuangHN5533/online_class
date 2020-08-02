package com.atguigu.guli.service.ucenter.service.impl;

import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.common.base.util.ExceptionUtils;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.ucenter.service.GiteeService;
import com.atguigu.guli.service.ucenter.utils.UcenterGiteeProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Service
@Slf4j
public class GiteeServiceImpl implements GiteeService {

    @Autowired
    private UcenterGiteeProperties ucenterGiteeProperties;

    private static final RestTemplate rest = new RestTemplate();

    private static final String GITEE_REDIRECT_URI = "https://gitee.com/oauth/authorize?client_id={client_id}&redirect_uri={redirect_uri}&response_type=code";

    private static final String ACCESS_TOKEN_API_URI = "https://gitee.com/oauth/token?grant_type=authorization_code&code={code}&client_id={client_id}&redirect_uri={redirect_uri}&client_secret={client_secret}";

    private static final String USER_INFO_URI = "https://gitee.com/api/v5/user?access_token={access_token}";

    @Override
    public String sendRedirect() {
        String clientId = ucenterGiteeProperties.getClientId();
        String clientSecret = ucenterGiteeProperties.getClientSecret();
        String redirectUri = ucenterGiteeProperties.getRedirectUri();
        UriComponents components = UriComponentsBuilder.fromHttpUrl(GITEE_REDIRECT_URI).build();
        String uri = components.expand(clientId, redirectUri).encode().toUri().toString();
        return "redirect:" + uri;
    }

    @Override
    public String getAccessToken(String code) {
        String clientId = ucenterGiteeProperties.getClientId();
        String clientSecret = ucenterGiteeProperties.getClientSecret();
        String redirectUri = ucenterGiteeProperties.getRedirectUri();
        try {
            UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(ACCESS_TOKEN_API_URI).build();
            URI uri = uriComponents.expand(code, clientId, redirectUri, clientSecret).encode().toUri();
            RequestEntity<Void> requestEntity = RequestEntity
                    .post(uri)
                    .headers(httpHeaders -> httpHeaders.add("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.92 Safari/537.36"))
                    .build();
            ResponseEntity<String> exchange = rest.exchange(requestEntity, String.class);
            String s = exchange.getBody();
            JacksonJsonParser json = new JacksonJsonParser();
            Map<String, Object> map = json.parseMap(s);
            String accessToken = map.get("access_token").toString();
            return accessToken;
        } catch (RestClientException e) {
            log.error("获取access_token失败");
            throw new GuliException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }
    }

    @Override
    public Map<String, Object> getUserInfo(String accessToken) {
        try {
            UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(USER_INFO_URI).build();
            URI uri = uriComponents.expand(accessToken).encode().toUri();
            RequestEntity<Void> requestEntity = RequestEntity
                    .get(uri)
                    .headers(httpHeaders -> httpHeaders.add("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.92 Safari/537.36"))
                    .build();
            ResponseEntity<String> exchange = rest.exchange(requestEntity, String.class);
            String s = exchange.getBody();
            JacksonJsonParser json = new JacksonJsonParser();
            Map<String, Object> map = json.parseMap(s);
            return map;
        } catch (RestClientException e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.FETCH_USERINFO_ERROR);
        }
    }
}
