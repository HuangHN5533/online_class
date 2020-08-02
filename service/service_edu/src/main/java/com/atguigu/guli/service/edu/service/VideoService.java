package com.atguigu.guli.service.edu.service;

import com.atguigu.guli.service.edu.entity.Video;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 课程视频 服务类
 * </p>
 *
 * @author Helen
 * @since 2020-04-01
 */
public interface VideoService extends IService<Video> {

    /**
     * 根据课时id删除视频
     * @param id 课时id，video表id
     */
    void removeMediaVideoById(String id);

    /**
     * 根据章节id删除课时视频
     * @param chapterId
     */
    void removeMediaVideoByChapterId(String chapterId);

    /**
     * 根据课程id删除课时视频
     * @param courseId
     */
    void removeMediaVideoByCourseId(String courseId);
}
