package com.zcpbysj.campusidletrade_server.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcpbysj.campusidletrade_server.common.Result;
import com.zcpbysj.campusidletrade_server.entity.FoodItem;
import com.zcpbysj.campusidletrade_server.service.IFoodItemService;
import com.zcpbysj.campusidletrade_server.service.ISystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/food-items")
public class AdminFoodItemController {

    @Autowired
    private IFoodItemService foodItemService;
    
    @Autowired
    private ISystemLogService systemLogService;

    @GetMapping
    public Result<?> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long canteenId) {
        
        Page<FoodItem> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<FoodItem> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(FoodItem::getStatus, status);
        }
        if (canteenId != null) {
            wrapper.eq(FoodItem::getCanteenId, canteenId);
        }
        wrapper.orderByDesc(FoodItem::getCreateTime);
        
        return Result.success(foodItemService.page(pageParam, wrapper));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(foodItemService.getById(id));
    }

    @PostMapping
    public Result<?> create(@RequestBody FoodItem foodItem) {
        foodItemService.save(foodItem);
        systemLogService.addLog("创建菜品: " + foodItem.getName(), "success", "菜品管理");
        return Result.success("创建成功");
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody FoodItem foodItem) {
        foodItem.setId(id);
        foodItemService.updateById(foodItem);
        systemLogService.addLog("更新菜品 #" + id, "info", "菜品管理");
        return Result.success("更新成功");
    }

    @PostMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        FoodItem item = foodItemService.getById(id);
        if (item == null) {
            return Result.error("菜品不存在");
        }
        item.setStatus(status);
        foodItemService.updateById(item);
        systemLogService.addLog("菜品状态变更: " + status, "info", "菜品管理");
        return Result.success("状态更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        foodItemService.removeById(id);
        systemLogService.addLog("删除菜品 #" + id, "warning", "菜品管理");
        return Result.success("删除成功");
    }
}
