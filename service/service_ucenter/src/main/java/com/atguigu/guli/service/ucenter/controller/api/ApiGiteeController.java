package com.atguigu.guli.service.ucenter.controller.api;

import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.common.base.util.JwtInfo;
import com.atguigu.guli.common.base.util.JwtUtils;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.ucenter.entity.Member;
import com.atguigu.guli.service.ucenter.service.GiteeService;
import com.atguigu.guli.service.ucenter.service.MemberService;
import com.atguigu.guli.service.ucenter.utils.UcenterGiteeProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/api/ucenter/gitee")
@Slf4j
public class ApiGiteeController {

    @Autowired
    private GiteeService giteeService;

    @Autowired
    private UcenterGiteeProperties ucenterProperties;

    @Autowired
    private MemberService memberService;

    @GetMapping("login")
    public String sendRedirect(){
        //重定向到码云授权页面
        String redirectUrl = giteeService.sendRedirect();
        return redirectUrl;
    }

    //码云回调地址
    @GetMapping("callback")
    public String callback(String code){
        if(StringUtils.isEmpty(code)){
            log.error("非法回调请求");
            throw new GuliException(ResultCodeEnum.ILLEGAL_CALLBACK_REQUEST_ERROR);
        }
        //获取accessToken
        String accessToken = giteeService.getAccessToken(code);
        //使用accessToken获取用户信息
        Map<String, Object> userInfo = giteeService.getUserInfo(accessToken);
        //查找用户是否已被注册
        String id = String.valueOf(userInfo.get("id"));
        Member member = memberService.getByOpenid(id);
        if (member == null){
            //解析出userInfo中的用户个人信息
            String name = (String) userInfo.get("name");
            String avatarUrl = (String) userInfo.get("avatar_url");
            //在本地数据库中插入当前码云用户的信息（使用码云账号在本地服务器注册新用户）
            member = new Member();
            member.setOpenid(id);
            member.setNickname(name);
            member.setAvatar(avatarUrl);
            memberService.save(member);
        }
        //注册则直接使用当前用户的信息登录（生成jwt）
        JwtInfo jwtInfo = new JwtInfo();
        jwtInfo.setId(member.getId());
        jwtInfo.setNickname(member.getNickname());
        jwtInfo.setAvatar(member.getAvatar());
        String jwtToken = JwtUtils.getJwtToken(jwtInfo, 1800);

        return "redirect:http://localhost:3000?token=" + jwtToken;
    }
}
