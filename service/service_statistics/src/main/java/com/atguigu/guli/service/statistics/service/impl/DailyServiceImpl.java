package com.atguigu.guli.service.statistics.service.impl;

import com.atguigu.guli.common.base.util.RandomUtils;
import com.atguigu.guli.service.statistics.entity.Daily;
import com.atguigu.guli.service.statistics.feign.UcenterMemberService;
import com.atguigu.guli.service.statistics.mapper.DailyMapper;
import com.atguigu.guli.service.statistics.service.DailyService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 网站统计日数据 服务实现类
 * </p>
 *
 * @author hhn
 * @since 2020-08-02
 */
@Service
public class DailyServiceImpl extends ServiceImpl<DailyMapper, Daily> implements DailyService {

    @Autowired
    private UcenterMemberService ucenterMemberService;
    
    @Override
    public void createStatisticsByDay(String day) {
        //如果已存在则删除
        QueryWrapper<Daily> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("date_calculated", day);
//        baseMapper.delete(queryWrapper);
        //获取注册人数
//        R r = ucenterMemberService.countRegisterNum(day);
//        Integer registerNum = (Integer) r.getData().get("registerNum");
        Integer registerNum = Integer.valueOf(RandomUtils.getFourBitRandom().substring(1));

        //获取登录人数
        Integer loginNum = Integer.valueOf(RandomUtils.getFourBitRandom().substring(1));
        //获取视频浏览数量
        Integer videoViewNum = Integer.valueOf(RandomUtils.getFourBitRandom().substring(1));
        //获取课程数量
        Integer courseNum = Integer.valueOf(RandomUtils.getFourBitRandom().substring(1));

        //创建统计对象
        Daily daily = new Daily();
        daily.setRegisterNum(registerNum);
        daily.setLoginNum(loginNum);
        daily.setVideoViewNum(videoViewNum);
        daily.setCourseNum(courseNum);
        daily.setDateCalculated(day);

        baseMapper.insert(daily);
    }

    @Override
    public Map<String, Map<String, Object>> getChartData(String begin, String end) {
        //学员登录数统计
        Map<String, Object> registerNum = this.getChartDataByType(begin, end, "register_num");
        //学员注册数统计
        Map<String, Object> loginNum = this.getChartDataByType(begin, end,"login_num");
        //课程播放数统计
        Map<String, Object> videoViewNum = this.getChartDataByType(begin, end, "video_view_num");
        //每日新增课程数统计
        Map<String, Object> courseNum = this.getChartDataByType(begin, end, "course_num");
        Map<String, Map<String, Object>> result = new HashMap<>();
        result.put("registerNum", registerNum);
        result.put("loginNum", loginNum);
        result.put("videoViewNum", videoViewNum);
        result.put("courseNum", courseNum);
        return result;
    }

    //根据时间和查询类型查询
    private Map<String, Object> getChartDataByType(String begin,String end, String type){
        Map<String, Object> map = new HashMap<>();

        List<String> xList = new ArrayList<>(); //时间x轴
        List<Integer> yList = new ArrayList<>(); //数量y轴

        QueryWrapper<Daily> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("date_calculated", type);
        queryWrapper.between("date_calculated", begin, end);

        List<Map<String, Object>> mapsData = baseMapper.selectMaps(queryWrapper);
        for (Map<String, Object> data : mapsData){
            String dataCalculated = (String) data.get("date_calculated");
            Integer count = (Integer) data.get(type);
            xList.add(dataCalculated);
            yList.add(count);
        }
        map.put("xData", xList);
        map.put("yData", yList);
        return map;
    }
}
