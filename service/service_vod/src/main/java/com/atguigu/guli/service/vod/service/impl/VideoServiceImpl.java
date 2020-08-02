package com.atguigu.guli.service.vod.service.impl;

import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthResponse;
import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.vod.service.VideoService;
import com.atguigu.guli.service.vod.util.AliyunSDKVodUtils;
import com.atguigu.guli.service.vod.util.VodProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.List;

@Service
@Slf4j
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VodProperties vodProperties;

    @Override
    public String uploadVideo(InputStream inputStream, String originalFilename) {
        String keyid = vodProperties.getKeyid();
        String keysecret = vodProperties.getKeysecret();
        String title = originalFilename.substring(0, originalFilename.lastIndexOf("."));

        UploadStreamRequest request = new UploadStreamRequest(keyid, keysecret, title, originalFilename, inputStream);

        //转码模板组
//        request.setTemplateGroupId(vodProperties.getTemplateGroupId());
        //设置工作流
//        request.setWorkflowId(vodProperties.getWorkflowId());

        UploadVideoImpl uploader = new UploadVideoImpl();
        UploadStreamResponse response = uploader.uploadStream(request);

        String videoId = response.getVideoId();
        if (StringUtils.isEmpty(videoId)){
            log.error("视频上传阿里云失败：" + response.getCode() + "-" + response.getMessage());
            throw  new GuliException(ResultCodeEnum.VIDEO_UPLOAD_ALIYUN_ERROR);
        }
        return videoId;
    }

    @Override
    public void removeVideo(String videoId) {
        String keyid = vodProperties.getKeyid();
        String keysecret = vodProperties.getKeysecret();
        DefaultAcsClient client = AliyunSDKVodUtils.initVodClient(keyid, keysecret);
        DeleteVideoRequest request = new DeleteVideoRequest();
        request.setVideoIds(videoId);
        try {
            client.getAcsResponse(request);
        } catch (ClientException e) {
            throw new GuliException(ResultCodeEnum.VIDEO_DELETE_ALIYUN_ERROR);
        }
    }

    @Override
    public void removeVideoByIdList(List<String> videoIdList) throws ClientException {
        String keyid = vodProperties.getKeyid();
        String keysecret = vodProperties.getKeysecret();
        DefaultAcsClient client = AliyunSDKVodUtils.initVodClient(keyid, keysecret);
        DeleteVideoRequest request = new DeleteVideoRequest();
        //拼接id列表
        StringBuffer videoIdString = new StringBuffer();
        int size = videoIdList.size();
        for (int i = 0; i < size; i++){
            videoIdString.append(videoIdList.get(i));
            if (i == size - 1 || i % 20 == 19){
                //删除视频
                request.setVideoIds(videoIdString.toString());
                client.getAcsResponse(request);
                //重置列表
                videoIdString = new StringBuffer();
            }
            else if (i % 20 < 19){
                videoIdString.append(",");
            }
        }
    }

    @Override
    public String getPlayAuth(String videoSourceId) throws ClientException {
        String keyid = vodProperties.getKeyid();
        String keysecret = vodProperties.getKeysecret();
        DefaultAcsClient client = AliyunSDKVodUtils.initVodClient(keyid, keysecret);

        GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
        request.setVideoId(videoSourceId);
        request.setAuthInfoTimeout(200L); //超时时间

        GetVideoPlayAuthResponse response = client.getAcsResponse(request);

        return response.getPlayAuth();
    }
}
