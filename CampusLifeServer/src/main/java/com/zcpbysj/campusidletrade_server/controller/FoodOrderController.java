package com.zcpbysj.campusidletrade_server.controller;

import com.zcpbysj.campusidletrade_server.common.Result;
import com.zcpbysj.campusidletrade_server.entity.dto.food.CreateFoodOrderDTO;
import com.zcpbysj.campusidletrade_server.entity.vo.food.FoodOrderVO;
import com.zcpbysj.campusidletrade_server.entity.vo.common.PageVO;
import com.zcpbysj.campusidletrade_server.service.IFoodOrderService;
import com.zcpbysj.campusidletrade_server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 食堂订单控制器
 */
@RestController
@RequestMapping("/api/food-orders")
@RequiredArgsConstructor
public class FoodOrderController {

    private final IFoodOrderService foodOrderService;
    private final JwtUtil jwtUtil;

    /**
     * 创建食堂订单
     */
    @PostMapping
    public Result<FoodOrderVO> createOrder(@RequestBody CreateFoodOrderDTO dto, HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        if (userId == null) {
            return Result.error("请先登录");
        }
        FoodOrderVO order = foodOrderService.createOrder(userId, dto);
        return Result.success(order);
    }

    /**
     * 支付食堂订单
     */
    @PostMapping("/{orderId}/pay")
    public Result<Boolean> payOrder(
            @PathVariable Long orderId,
            @RequestParam(defaultValue = "balance") String paymentMethod,
            HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        Boolean result = foodOrderService.payOrder(orderId, userId, paymentMethod);
        return Result.success(result);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public Result<FoodOrderVO> getOrderDetail(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        FoodOrderVO order = foodOrderService.getOrderDetail(orderId, userId);
        return Result.success(order);
    }

    /**
     * 获取用户的食堂订单列表
     */
    @GetMapping
    public Result<PageVO<FoodOrderVO>> getOrderList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        PageVO<FoodOrderVO> pageVO = foodOrderService.getOrderList(userId, page, size, status);
        return Result.success(pageVO);
    }

    /**
     * 取消订单
     */
    @PostMapping("/{orderId}/cancel")
    public Result<Void> cancelOrder(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        foodOrderService.cancelOrder(orderId, userId);
        return Result.success();
    }

    /**
     * 确认取餐（完成订单）
     */
    @PostMapping("/{orderId}/complete")
    public Result<Void> completeOrder(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = jwtUtil.getUserIdFromRequest(request);
        foodOrderService.completeOrder(orderId, userId);
        return Result.success();
    }
}
