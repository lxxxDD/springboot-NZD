package com.zcpbysj.campusidletrade_server.controller;

import com.zcpbysj.campusidletrade_server.entity.vo.food.CanteenVO;
import com.zcpbysj.campusidletrade_server.service.ICanteenService;
import com.zcpbysj.campusidletrade_server.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 食堂控制器
 */
@RestController
@RequestMapping("/api/canteen")
public class CanteenController {

    @Autowired
    private ICanteenService canteenService;

    /**
     * 获取所有食堂列表
     */
    @GetMapping("/list")
    public Result<List<CanteenVO>> listAllCanteens() {
        List<CanteenVO> canteens = canteenService.listAllCanteens();
        return Result.success(canteens);
    }

    /**
     * 获取营业中的食堂
     */
    @GetMapping("/open")
    public Result<List<CanteenVO>> listOpenCanteens() {
        List<CanteenVO> canteens = canteenService.listOpenCanteens();
        return Result.success(canteens);
    }

    /**
     * 根据ID获取食堂详情
     */
    @GetMapping("/{id}")
    public Result<CanteenVO> getCanteenById(@PathVariable Long id) {
        CanteenVO canteen = canteenService.getCanteenById(id);
        if (canteen == null) {
            return Result.error("食堂不存在");
        }
        return Result.success(canteen);
    }

    /**
     * 更新排队人数
     */
    @PutMapping("/{id}/queue")
    public Result<Boolean> updateQueueCount(@PathVariable Long id, @RequestParam Integer queueCount) {
        boolean success = canteenService.updateQueueCount(id, queueCount);
        if (success) {
            return Result.success(true);
        }
        return Result.error("更新失败");
    }

    /**
     * 更新座位信息
     */
    @PutMapping("/{id}/seats")
    public Result<Boolean> updateSeats(@PathVariable Long id, @RequestParam Integer availableSeats) {
        boolean success = canteenService.updateSeats(id, availableSeats);
        if (success) {
            return Result.success(true);
        }
        return Result.error("更新失败");
    }
}
