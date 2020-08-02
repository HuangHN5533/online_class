package com.atguigu.guli.service.trade.service;

import com.atguigu.guli.service.trade.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author hhn
 * @since 2020-07-31
 */
public interface OrderService extends IService<Order> {

    Order getByOrderId(String orderId, String memberId);

    String saveOrder(String courseId, String memberId);

    boolean isBuyByCourseId(String courseId, String memberId);

    List<Order> selectByMember(String memberId);

    boolean removeById(String orderId, String memberId);

    Order getOrderByOrderNo(String orderNo);

    void updateOrderStatus(Map<String, String> notifyMap);

    boolean queryPayStatus(String orderNo);
}
