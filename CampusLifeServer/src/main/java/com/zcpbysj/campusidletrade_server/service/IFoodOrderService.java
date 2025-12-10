package com.zcpbysj.campusidletrade_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcpbysj.campusidletrade_server.entity.FoodOrder;
import com.zcpbysj.campusidletrade_server.entity.dto.food.CreateFoodOrderDTO;
import com.zcpbysj.campusidletrade_server.entity.vo.food.FoodOrderVO;
import com.zcpbysj.campusidletrade_server.entity.vo.common.PageVO;

/**
 * 食堂订单服务接口
 */
public interface IFoodOrderService extends IService<FoodOrder> {
    
    /**
     * 创建食堂订单
     */
    FoodOrderVO createOrder(Long userId, CreateFoodOrderDTO dto);
    
    /**
     * 支付食堂订单
     */
    Boolean payOrder(Long orderId, Long userId, String paymentMethod);
    
    /**
     * 获取订单详情
     */
    FoodOrderVO getOrderDetail(Long orderId, Long userId);
    
    /**
     * 获取用户的食堂订单列表
     */
    PageVO<FoodOrderVO> getOrderList(Long userId, Integer page, Integer size, String status);
    
    /**
     * 取消订单
     */
    void cancelOrder(Long orderId, Long userId);
    
    /**
     * 确认取餐（完成订单）
     */
    void completeOrder(Long orderId, Long userId);
}
