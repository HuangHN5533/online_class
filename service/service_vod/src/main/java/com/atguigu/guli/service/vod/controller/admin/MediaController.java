package com.atguigu.guli.service.vod.controller.admin;

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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Api(tags = "视频文件管理")
@Slf4j
@RestController
@RequestMapping("/admin/vod/media")
public class MediaController {

    @Autowired
    private VideoService videoService;

    @ApiOperation("上传视频文件")
    @PostMapping("upload")
    public R uploadVideo(@ApiParam(value = "视频文件", required = true) @RequestParam("file") MultipartFile file){
        if (file == null){
            return R.error().message("请选择上传的视频");
        }
        try {
            InputStream inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            String videoId = videoService.uploadVideo(inputStream, originalFilename);
            return R.ok().message("视频上传成功").data("videoId", videoId);
        } catch (IOException e) {
            log.error(ExceptionUtils.getMessage(e));
            throw  new GuliException(ResultCodeEnum.VIDEO_UPLOAD_TOMCAT_ERROR);
        }
    }

    @ApiOperation("删除视频")
    @DeleteMapping("remove/{videoId}")
    public R removeVideo(@ApiParam(value = "视频id", required = true) @PathVariable("videoId") String videoId){
        try {
            videoService.removeVideo(videoId);
            return R.ok().message("删除视频成功");
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.VIDEO_DELETE_ALIYUN_ERROR);
        }
    }

    @ApiOperation("批量删除视频")
    @DeleteMapping("remove")
    public R removeVideoList(@ApiParam(value = "视频id列表", required = true) @RequestBody List<String> videoIdList){
        try {
            videoService.removeVideoByIdList(videoIdList);
            return R.ok().message("删除视频成功");
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.VIDEO_DELETE_ALIYUN_ERROR);
        }
    }
}
