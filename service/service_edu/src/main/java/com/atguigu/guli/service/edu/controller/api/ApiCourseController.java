package com.atguigu.guli.service.edu.controller.api;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.base.dto.CourseDto;
import com.atguigu.guli.service.edu.entity.Course;
import com.atguigu.guli.service.edu.entity.vo.ChapterVo;
import com.atguigu.guli.service.edu.entity.vo.WebCourseQueryVo;
import com.atguigu.guli.service.edu.entity.vo.WebCourseVo;
import com.atguigu.guli.service.edu.service.ChapterService;
import com.atguigu.guli.service.edu.service.CourseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "前台课程")
@RestController
@RequestMapping("/api/edu/course")
public class ApiCourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private ChapterService chapterService;

    @ApiOperation("课程列表")
    @GetMapping("list")
    public R pageList(@ApiParam(value = "查询列表", required = true) WebCourseQueryVo webCourseQueryVo){
        List<Course> courseList = courseService.webSelectList(webCourseQueryVo);
        return R.ok().data("courseList", courseList);
    }

    @ApiOperation("课程详情")
    @GetMapping("get/{courseId}")
    public R course(@ApiParam(value = "课程id", required = true) @PathVariable("courseId") String id){
        //查询前台课程和讲师信息
        WebCourseVo webCourseVo = courseService.selectWebCourseVoById(id);
        //查询课程章节信息
        List<ChapterVo> chapterVoList = chapterService.nestedList(id);

        return R.ok().data("course", webCourseVo).data("chapterVoList",chapterVoList);
    }

    @ApiOperation("根据课程id查询前台课程信息")
    @GetMapping("inner/get-course-dto/{courseId}")
    public CourseDto getCourseDtoById(@ApiParam(value = "课程id", required = true) @PathVariable("courseId") String id){
        CourseDto courseDto = courseService.getCourseDtoById(id);
        return courseDto;
    }

    @ApiOperation("根据课程id更新购买数量")
    @GetMapping("inner/update-buy-count/{courseId}")
    public R updateBuyCountById(@ApiParam(value = "课程id", required = true) @PathVariable("courseId") String courseId){
        courseService.updateBuyCountById(courseId);
        return R.ok();
    }
}
