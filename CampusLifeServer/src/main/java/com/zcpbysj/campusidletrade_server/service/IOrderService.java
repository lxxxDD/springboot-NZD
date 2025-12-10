package com.zcpbysj.campusidletrade_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcpbysj.campusidletrade_server.entity.Order;
import com.zcpbysj.campusidletrade_server.entity.dto.order.CreateOrderDTO;
import com.zcpbysj.campusidletrade_server.entity.vo.order.OrderVO;
import com.zcpbysj.campusidletrade_server.entity.vo.common.PageVO;

/**
 * 订单服务接口
 */
public interface IOrderService extends IService<Order> {
    
    /**
     * 获取订单列表
     */
    PageVO<OrderVO> getOrders(Long userId, Integer page, Integer size, String type, String status, String role);
    
    /**
     * 获取订单详情
     */
    OrderVO getOrderDetail(Long id, Long userId);
    
    /**
     * 创建订单
     */
    OrderVO createOrder(Long buyerId, CreateOrderDTO dto);
    
    /**
     * 支付订单
     */
    Boolean payOrder(Long id, Long userId, String paymentMethod);
    
    /**
     * 更新订单状态
     */
    void updateOrderStatus(Long id, Long userId, String status);
    
    /**
     * 取消订单
     */
    void cancelOrder(Long id, Long userId, String reason);
}
