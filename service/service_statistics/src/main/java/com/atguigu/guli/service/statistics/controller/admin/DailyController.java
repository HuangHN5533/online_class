package com.atguigu.guli.service.statistics.controller.admin;


import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.statistics.service.DailyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * 网站统计日数据 前端控制器
 * </p>
 *
 * @author hhn
 * @since 2020-08-02
 */
@Api(tags = "统计分析管理")
@RestController
@RequestMapping("/admin/statistics/daily")
public class DailyController {
    @Autowired
    private DailyService dailyService;

    @ApiOperation("生成统计记录")
    @GetMapping("create/{day}")
    public R createStatisticsByDay(
            @ApiParam(value = "统计日期", required = true)
            @PathVariable("day") String day){
        dailyService.createStatisticsByDay(day);
        return R.ok().message("统计数据生成成功");
    }

    @ApiOperation("显示统计记录")
    @GetMapping("show-chart/{begin}/{end}")
    public R showChart(@ApiParam(value = "统计日期", required = true) @PathVariable("begin") String begin,
                        @ApiParam(value = "统计日期", required = true) @PathVariable("end") String end){
        Map<String, Map<String, Object>> map = dailyService.getChartData(begin, end);
        return R.ok().data("chartData", map);
    }
}

