package com.zcpbysj.campusidletrade_server.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcpbysj.campusidletrade_server.common.Result;
import com.zcpbysj.campusidletrade_server.entity.FoodOrder;
import com.zcpbysj.campusidletrade_server.service.IFoodOrderService;
import com.zcpbysj.campusidletrade_server.service.ISystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/food-orders")
public class AdminFoodOrderController {

    @Autowired
    private IFoodOrderService foodOrderService;
    
    @Autowired
    private ISystemLogService systemLogService;

    @GetMapping
    public Result<?> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String orderNo) {
        
        Page<FoodOrder> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<FoodOrder> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(FoodOrder::getStatus, status);
        }
        if (StringUtils.hasText(orderNo)) {
            wrapper.like(FoodOrder::getOrderNo, orderNo);
        }
        wrapper.orderByDesc(FoodOrder::getCreateTime);
        
        return Result.success(foodOrderService.page(pageParam, wrapper));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(foodOrderService.getById(id));
    }

    @PostMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        FoodOrder order = foodOrderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        order.setStatus(status);
        foodOrderService.updateById(order);
        systemLogService.addLog("订单状态变更: " + status, "info", "订单管理");
        return Result.success("状态更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        foodOrderService.removeById(id);
        systemLogService.addLog("删除订单 #" + id, "warning", "订单管理");
        return Result.success("删除成功");
    }
}
