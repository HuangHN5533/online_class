package com.atguigu.guli.service.sms.controller.api;

import com.aliyuncs.exceptions.ClientException;
import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.common.base.util.FormUtils;
import com.atguigu.guli.common.base.util.RandomUtils;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.sms.service.SmsService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Api(tags = "短信管理")
@Slf4j
@RestController
@RequestMapping("/api/sms")
@RefreshScope //动态刷新配置中心的配置
public class ApiSmsController {

    @Autowired
    private SmsService smsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("send/{mobile}")
    public R getCode(@PathVariable("mobile") String mobile) throws ClientException {
        //验证手机号是否合法
        if (StringUtils.isEmpty(mobile) || !FormUtils.isMobile(mobile)){
            log.error("手机号不合法");
            throw new GuliException(ResultCodeEnum.LOGIN_MOBILE_ERROR);
        }

        //生成验证码，六位验证码
        String checkCode = RandomUtils.getSixBitRandom();

        //发送验证码
//        smsService.send(mobile, checkCode);

        //存储验证码到redis
        //有效期5分钟
        redisTemplate.opsForValue().set(mobile, checkCode, 5, TimeUnit.MINUTES);

        return R.ok().message("短信发送成功");
    }
}
