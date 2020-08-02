package com.atguigu.guli.service.edu.service.impl;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.base.dto.CourseDto;
import com.atguigu.guli.service.edu.entity.*;
import com.atguigu.guli.service.edu.entity.form.CourseInfoForm;
import com.atguigu.guli.service.edu.entity.vo.*;
import com.atguigu.guli.service.edu.feign.OssFileService;
import com.atguigu.guli.service.edu.mapper.*;
import com.atguigu.guli.service.edu.service.CourseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2020-04-01
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Autowired
    private CourseDescriptionMapper courseDescriptionMapper;

    @Autowired
    private OssFileService ossFileService;

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private ChapterMapper chapterMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CourseCollectMapper courseCollectMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String saveInfoForm(CourseInfoForm courseInfoForm) {
        //保存课程基本信息
        Course course = new Course();
        BeanUtils.copyProperties(courseInfoForm, course);
        course.setStatus(Course.COURSE_DRAFT);
        //保存成功后，mybatis会自动填上courseId
        baseMapper.insert(course);

        //保存课程简介
        CourseDescription courseDescription = new CourseDescription();
        courseDescription.setDescription(courseInfoForm.getDescription());
        courseDescription.setId(course.getId());
        courseDescriptionMapper.insert(courseDescription);

        return course.getId();
    }

    @Override
    public CourseInfoForm getCourseInfoFormById(String id) {
        //根据id获取课程
        Course course = baseMapper.selectById(id);
        if (course == null){
            return null;
        }
        //根据id获取课程简介
        CourseDescription courseDescription = courseDescriptionMapper.selectById(course.getId());

        CourseInfoForm courseInfoForm = new CourseInfoForm();
        BeanUtils.copyProperties(course, courseInfoForm);
        courseInfoForm.setDescription(courseDescription.getDescription());

        return courseInfoForm;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCourseInfoById(CourseInfoForm courseInfo) {
        //更新课程基本信息
        Course course = new Course();
        BeanUtils.copyProperties(courseInfo, course);
        baseMapper.updateById(course);

        //更新课程简介
        CourseDescription courseDescription = new CourseDescription();
        courseDescription.setDescription(courseInfo.getDescription());
        courseDescription.setId(courseInfo.getId());
        courseDescriptionMapper.updateById(courseDescription);
    }

    @Override
    public IPage<CourseVo> selectPage(Long page, Long limit, CourseQueryVo courseQueryVo) {
        QueryWrapper<CourseVo> queryWrapper = new QueryWrapper<>();

        String title = courseQueryVo.getTitle();
        String teacherId = courseQueryVo.getTeacherId();
        String subjectParentId = courseQueryVo.getSubjectParentId();
        String subjectId = courseQueryVo.getSubjectId();
        //按创建时间倒序排列
        queryWrapper.orderByDesc("c.gmt_create");

        //拼装查询条件
        if (!StringUtils.isEmpty(title)){
            queryWrapper.like("c.title", title);
        }
        if (!StringUtils.isEmpty(teacherId)){
            queryWrapper.eq("c.teacher_id", teacherId);
        }
        if (!StringUtils.isEmpty(subjectParentId)){
            queryWrapper.eq("c.subject_parent_id", subjectParentId);
        }
        if (!StringUtils.isEmpty(subjectId)){
            queryWrapper.eq("c.subject_id", subjectId);
        }
        //拼装分页
        Page<CourseVo> pageParam = new Page<>(page, limit);
        //执行分页
        //只需要在mapper层传入封装好的分页组件，sql分页分页条件组装过程由mp自动完成
        List<CourseVo> records = baseMapper.selectPageByCourseQueryVo(pageParam, queryWrapper);
        //将数据放到pageParam中
        return pageParam.setRecords(records);
    }

    @Override
    public boolean removeCoverById(String id) {
        Course course = baseMapper.selectById(id);
        if (course != null){
            String cover = course.getCover();
            if (!StringUtils.isEmpty(cover)){
                R r = ossFileService.remove(cover);
                return r.getSuccess();
            }
        }
        return false;
    }

    /**
     * 先删除子表，再删除父表
     * @param id 课程Id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeCourseById(String id) {
        //先删除video
        //根据courseId删除
        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
        videoQueryWrapper.eq("course_id", id);
        videoMapper.delete(videoQueryWrapper);

        //删除chapter
        QueryWrapper<Chapter> chapterQueryWrapper = new QueryWrapper<>();
        chapterQueryWrapper.eq("course_id", id);
        chapterMapper.delete(chapterQueryWrapper);

        //删除课程简介
        courseDescriptionMapper.deleteById(id);

        //删除评论
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("course_id", id);
        commentMapper.delete(commentQueryWrapper);

        //删除收藏
        QueryWrapper<CourseCollect> collectQueryWrapper = new QueryWrapper<>();
        collectQueryWrapper.eq("course_id", id);
        courseCollectMapper.delete(collectQueryWrapper);

        //删除课程
        return this.removeById(id);
    }

    @Override
    public CoursePublishVo getCoursePublishVoById(String id) {
        return baseMapper.selectCoursePublishVoById(id);
    }

    @Override
    public boolean publishCourseById(String id) {
        Course course = new Course();
        course.setId(id);
        course.setStatus(Course.COURSE_NORMAL);
        return this.updateById(course);
    }

    @Override
    public List<Course> webSelectList(WebCourseQueryVo webCourseQueryVo) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        //已发布课程
        queryWrapper.eq("status", Course.COURSE_NORMAL);
        //一级类别
        String subjectParentId = webCourseQueryVo.getSubjectParentId();
        //二级类别
        String subjectId = webCourseQueryVo.getSubjectId();
        //购买数量排序
        String buyCountSort = webCourseQueryVo.getBuyCountSort();
        //创建时间排序
        String gmtCreateSort = webCourseQueryVo.getGmtCreateSort();
        //价格排序
        String priceSort = webCourseQueryVo.getPriceSort();
        if (!StringUtils.isEmpty(subjectParentId)){
            queryWrapper.eq("subject_parent_id", subjectParentId);
        }
        if (!StringUtils.isEmpty(subjectId)){
            queryWrapper.eq("subject_id", subjectId);
        }
        if (!StringUtils.isEmpty(buyCountSort)){
            queryWrapper.orderByDesc("buy_count");
        }
        if (!StringUtils.isEmpty(gmtCreateSort)){
            queryWrapper.orderByDesc("gmt_create");
        }
        if (!StringUtils.isEmpty(priceSort)){
            Integer type = webCourseQueryVo.getType();
            if (type == null || type == 1){
                queryWrapper.orderByAsc("price");
            }
            else {
                queryWrapper.orderByDesc("price");
            }
        }
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 获取课程信息并更新浏览数
     * @param id 课程id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public WebCourseVo selectWebCourseVoById(String id) {
        Course course = baseMapper.selectById(id);
        //更新浏览数
        course.setViewCount(course.getViewCount() + 1);
        baseMapper.updateById(course);

        //获取课程详情
        WebCourseVo webCourseVo = baseMapper.selectWebCourseVoById(id);

        return webCourseVo;
    }

    @Cacheable(value = "index", key = "'selectHotCourses'")
    @Override
    public List<Course> selectHotCourses() {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        //根据购买量、浏览量倒序排序
        queryWrapper.orderByDesc("buy_count", "view_count");

        //在sql最后添加"limit 8"，取前八个
        queryWrapper.last("limit 8");

        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public CourseDto getCourseDtoById(String id) {
        CourseDto courseDto = baseMapper.selectCouseDtoById(id);
        return courseDto;
    }

    @Override
    public void updateBuyCountById(String courseId) {
        Course course = baseMapper.selectById(courseId);
        long butCount = course.getBuyCount() + 1;
        course.setBuyCount(butCount);
        baseMapper.updateById(course);
    }

}
