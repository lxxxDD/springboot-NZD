package com.zcpbysj.campusidletrade_server.controller;

import com.zcpbysj.campusidletrade_server.entity.vo.food.FoodItemVO;
import com.zcpbysj.campusidletrade_server.service.IFoodItemService;
import com.zcpbysj.campusidletrade_server.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品控制器
 */
@RestController
@RequestMapping("/api/food")
public class FoodItemController {

    @Autowired
    private IFoodItemService foodItemService;

    /**
     * 根据食堂ID获取菜品列表
     */
    @GetMapping("/canteen/{canteenId}")
    public Result<List<FoodItemVO>> listByCanteenId(@PathVariable Long canteenId) {
        List<FoodItemVO> foodItems = foodItemService.listByCanteenId(canteenId);
        return Result.success(foodItems);
    }

    /**
     * 根据ID获取菜品详情
     */
    @GetMapping("/{id}")
    public Result<FoodItemVO> getFoodItemById(@PathVariable Long id) {
        FoodItemVO foodItem = foodItemService.getFoodItemById(id);
        if (foodItem == null) {
            return Result.error("菜品不存在");
        }
        return Result.success(foodItem);
    }

    /**
     * 根据分类获取菜品
     */
    @GetMapping("/category/{category}")
    public Result<List<FoodItemVO>> listByCategory(@PathVariable String category) {
        List<FoodItemVO> foodItems = foodItemService.listByCategory(category);
        return Result.success(foodItems);
    }

    /**
     * 搜索菜品
     */
    @GetMapping("/search")
    public Result<List<FoodItemVO>> searchFoodItems(@RequestParam String keyword) {
        List<FoodItemVO> foodItems = foodItemService.searchFoodItems(keyword);
        return Result.success(foodItems);
    }

    /**
     * 获取热销菜品
     */
    @GetMapping("/hot")
    public Result<List<FoodItemVO>> listHotFoodItems(@RequestParam(defaultValue = "10") Integer limit) {
        List<FoodItemVO> foodItems = foodItemService.listHotFoodItems(limit);
        return Result.success(foodItems);
    }

    /**
     * 更新库存
     */
    @PutMapping("/{id}/stock")
    public Result<Boolean> updateStock(@PathVariable Long id, @RequestParam Integer stock) {
        boolean success = foodItemService.updateStock(id, stock);
        if (success) {
            return Result.success(true);
        }
        return Result.error("更新失败");
    }

    /**
     * 更新菜品状态
     */
    @PutMapping("/{id}/status")
    public Result<Boolean> updateStatus(@PathVariable Long id, @RequestParam String status) {
        boolean success = foodItemService.updateStatus(id, status);
        if (success) {
            return Result.success(true);
        }
        return Result.error("更新失败");
    }
}
