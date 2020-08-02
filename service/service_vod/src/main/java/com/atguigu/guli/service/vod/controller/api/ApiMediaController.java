package com.atguigu.guli.service.vod.controller.api;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.common.base.util.ExceptionUtils;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.vod.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "阿里云视频点播")
@Slf4j
@RestController
@RequestMapping("/api/vod/media")
public class ApiMediaController {

    @Autowired
    private VideoService videoService;

    @ApiOperation("获取播放凭证")
    @GetMapping("get-play-auth/{videoSourceId}")
    public R getPlayAuth(
            @ApiParam(value = "阿里云视频id", required = true)
            @PathVariable("videoSourceId") String videoSourceId
    ){
        try {
            String playAuth = videoService.getPlayAuth(videoSourceId);
            return R.ok().data("playAuth", playAuth).message("获取播放凭证成功");
        } catch (Exception e) {
            ExceptionUtils.getMessage(e);
            log.error("获取播放凭证失败");
            throw new GuliException(ResultCodeEnum.FETCH_PLAYAUTH_ERROR);
        }
    }
}
