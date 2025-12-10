package com.zcpbysj.campusidletrade_server.controller;

import com.zcpbysj.campusidletrade_server.common.Result;
import com.zcpbysj.campusidletrade_server.entity.dto.order.CancelOrderDTO;
import com.zcpbysj.campusidletrade_server.entity.dto.order.CreateOrderDTO;
import com.zcpbysj.campusidletrade_server.entity.dto.order.PayOrderDTO;
import com.zcpbysj.campusidletrade_server.entity.vo.order.OrderVO;
import com.zcpbysj.campusidletrade_server.entity.vo.common.PageVO;
import com.zcpbysj.campusidletrade_server.service.IOrderService;
import com.zcpbysj.campusidletrade_server.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单接口
 */
@Tag(name = "订单模块")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "获取订单列表")
    @GetMapping
    public Result<PageVO<OrderVO>> getOrders(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        PageVO<OrderVO> vo = orderService.getOrders(userId, page, size, type, status, role);
        return Result.success(vo);
    }

    @Operation(summary = "获取订单详情")
    @GetMapping("/{id}")
    public Result<OrderVO> getOrderDetail(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        OrderVO vo = orderService.getOrderDetail(id, userId);
        return Result.success(vo);
    }

    @Operation(summary = "创建订单")
    @PostMapping
    public Result<OrderVO> createOrder(
            @RequestHeader("Authorization") String token,
            @RequestBody CreateOrderDTO dto) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        OrderVO vo = orderService.createOrder(userId, dto);
        return Result.success(vo);
    }

    @Operation(summary = "支付订单")
    @PostMapping("/{id}/pay")
    public Result<Map<String, Boolean>> payOrder(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestBody PayOrderDTO dto) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        Boolean success = orderService.payOrder(id, userId, dto.getPaymentMethod());
        Map<String, Boolean> data = new HashMap<>();
        data.put("success", success);
        return Result.success(data);
    }

    @Operation(summary = "更新订单状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateOrderStatus(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, String> body) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        orderService.updateOrderStatus(id, userId, body.get("status"));
        return Result.success();
    }

    @Operation(summary = "取消订单")
    @PostMapping("/{id}/cancel")
    public Result<Void> cancelOrder(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestBody CancelOrderDTO dto) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        orderService.cancelOrder(id, userId, dto.getReason());
        return Result.success();
    }
}
