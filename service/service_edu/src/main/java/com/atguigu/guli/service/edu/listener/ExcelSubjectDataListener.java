package com.atguigu.guli.service.edu.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.atguigu.guli.service.edu.entity.Subject;
import com.atguigu.guli.service.edu.entity.excel.ExcelSubjectData;
import com.atguigu.guli.service.edu.mapper.SubjectMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * excel导入监听器
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class ExcelSubjectDataListener extends AnalysisEventListener<ExcelSubjectData> {

    private SubjectMapper subjectMapper;
    /**
     * 解析每行记录
     * @param data
     * @param context
     */
    @Override
    public void invoke(ExcelSubjectData data, AnalysisContext context) {
        //读一行存一行
        log.info("解析到一条记录");
        //读取出来的数据
        String levelOneTitle = data.getLevelOneTitle();
        String levelTwoTitle = data.getLevelTwoTitle();
        String parentId = null;
        //判断一级数据是否存在
        Subject subjectLevelOne = getByTitle(levelOneTitle);
        if (subjectLevelOne == null){
            Subject subject = new Subject();
            subject.setParentId("0");
            subject.setTitle(levelOneTitle);
            //存入数据库
            subjectMapper.insert(subject);
            //获取一级类别id作为二级类别parentId
            parentId = subject.getId();
        }else {
            //获取一级类别id作为二级类别parentId
            parentId = subjectLevelOne.getId();
        }

        //判断二级数据是否存在
        Subject subjectLevelTwo = getByTitleAndParentId(levelTwoTitle, parentId);
        if (subjectLevelTwo == null){
            Subject subject = new Subject();
            subject.setTitle(levelTwoTitle);
            subject.setParentId(parentId);
            //存入数据库
            subjectMapper.insert(subject);
        }
    }

    /**
     * 收尾工作
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("全部数据解析完成");
    }

    /**
     * 根据title查找一级标题是否存在
     * @param title
     * @return
     */
    private Subject getByTitle(String title){
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title", title);
        queryWrapper.eq("parent_id", 0);
        return subjectMapper.selectOne(queryWrapper);
    }

    /**
     * 根据title跟parentId查找二级标题是否存在
     * @param title
     * @param parentId
     * @return
     */
    private Subject getByTitleAndParentId(String title, String parentId){
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title", title);
        queryWrapper.eq("parent_id", parentId);
        return subjectMapper.selectOne(queryWrapper);
    }
}
