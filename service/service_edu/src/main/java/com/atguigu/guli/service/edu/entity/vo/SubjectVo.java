package com.atguigu.guli.service.edu.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class SubjectVo implements Serializable {
    private String id;
    private String title;
    private String sort;
    private List<SubjectVo> children = new ArrayList<>();
}
