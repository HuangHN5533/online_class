package com.atguigu.guli.service.edu.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 前台课程查询实体类
 * @author helen
 * @since 2020/4/27
 */
@Data
public class WebCourseQueryVo implements Serializable {

    private static final long serialVersionUID = 1L;
    private String subjectParentId;//一级类别
    private String subjectId;//二级类别
    private String buyCountSort;//购买数量排序
    private String gmtCreateSort;//创建时间排序
    private String priceSort;//价格排序

    private Integer type;
}
