package com.atguigu.guli.service.trade.controller.api;


import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.common.base.util.JwtInfo;
import com.atguigu.guli.common.base.util.JwtUtils;
import com.atguigu.guli.service.trade.entity.Order;
import com.atguigu.guli.service.trade.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 订单 前端控制器
 * </p>
 *
 * @author hhn
 * @since 2020-07-31
 */
@Api(tags = "网站订单管理")
@Slf4j
@RestController
@RequestMapping("/api/trade/order")
public class ApiOrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("提交订单")
    @PostMapping("auth/save/{courseId}")
    public R save(
            @ApiParam(value = "课程id", required = true) @PathVariable("courseId") String courseId,
            HttpServletRequest request){
        //获取请求中token的用户信息
        JwtInfo jwtInfo = JwtUtils.getMemberIdByJwtToken(request);
        String memberId = jwtInfo.getId();
        String orderId = orderService.saveOrder(courseId, memberId);
        return R.ok().data("orderId", orderId);
    }

    @ApiOperation("获取订单")
    @GetMapping("auth/get/{orderId}")
    public R getOrder(
            @ApiParam(value = "订单id", required = true) @PathVariable("orderId") String orderId,
            HttpServletRequest request){
        JwtInfo jwtInfo = JwtUtils.getMemberIdByJwtToken(request);
        Order order = orderService.getByOrderId(orderId, jwtInfo.getId());
        return R.ok().data("item", order);
    }

    @ApiOperation("判断课程是否购买")
    @GetMapping("auth/is-buy/{courseId}")
    public R isBuyByCourseId(
            @ApiParam(value = "课程id", required = true) @PathVariable("courseId") String courseId,
            HttpServletRequest request){
        //获取请求中token的用户信息
        JwtInfo jwtInfo = JwtUtils.getMemberIdByJwtToken(request);
        String memberId = jwtInfo.getId();
        Boolean isBuy = orderService.isBuyByCourseId(courseId, memberId);
        return R.ok().data("isBuy", isBuy);
    }

    @ApiOperation("获取用户订单列表")
    @GetMapping("auth/list")
    public R list(HttpServletRequest request){
        //获取请求中token的用户信息
        JwtInfo jwtInfo = JwtUtils.getMemberIdByJwtToken(request);
        String memberId = jwtInfo.getId();
        List<Order> orderList = orderService.selectByMember(memberId);
        return R.ok().data("items", orderList);
    }

    @ApiOperation("删除用户订单")
    @DeleteMapping("auth/remove/{orderId}")
    public R remove(
            @ApiParam(value = "订单id", required = true) @PathVariable("orderId") String orderId,
            HttpServletRequest request){
        //获取请求中token的用户信息
        JwtInfo jwtInfo = JwtUtils.getMemberIdByJwtToken(request);
        String memberId = jwtInfo.getId();
        Boolean result = orderService.removeById(orderId,memberId);
        if (result){
            return R.ok().message("删除成功");
        }
        else {
            return R.error().message("删除失败");
        }
    }

    @ApiOperation("查询支付结果")
    @GetMapping("/query-pay-status/{orderNo}")
    public R queryPayStatus(@ApiParam(value = "订单号",required = true) @PathVariable("orderNo") String orderNo){
        boolean result = orderService.queryPayStatus(orderNo);
        if (result){
            return R.ok().message("支付成功");
        }
        return R.setResult(ResultCodeEnum.PAY_RUN);//支付中
    }
}

