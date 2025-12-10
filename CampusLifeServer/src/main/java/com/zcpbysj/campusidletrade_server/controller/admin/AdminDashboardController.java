package com.zcpbysj.campusidletrade_server.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zcpbysj.campusidletrade_server.common.Result;
import com.zcpbysj.campusidletrade_server.entity.FoodOrder;
import com.zcpbysj.campusidletrade_server.entity.MarketItem;
import com.zcpbysj.campusidletrade_server.entity.Repair;
import com.zcpbysj.campusidletrade_server.entity.User;
import com.zcpbysj.campusidletrade_server.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/admin/dashboard")
public class AdminDashboardController {

    @Autowired
    private IUserService userService;
    
    @Autowired
    private IRepairService repairService;
    
    @Autowired
    private IFoodOrderService foodOrderService;
    
    @Autowired
    private IMarketItemService marketItemService;

    @GetMapping("/stats")
    public Result<?> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 用户总数
        long userCount = userService.count();
        stats.put("userCount", userCount);
        
        // 待处理报修数（状态为received或assigned）
        QueryWrapper<Repair> repairWrapper = new QueryWrapper<>();
        repairWrapper.in("status", "received", "assigned", "in_progress");
        long pendingRepairs = repairService.count(repairWrapper);
        stats.put("pendingRepairs", pendingRepairs);
        
        // 今日订单数
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        QueryWrapper<FoodOrder> orderWrapper = new QueryWrapper<>();
        orderWrapper.between("create_time", todayStart, todayEnd);
        long todayOrders = foodOrderService.count(orderWrapper);
        stats.put("todayOrders", todayOrders);
        
        // 在售二手商品数
        QueryWrapper<MarketItem> marketWrapper = new QueryWrapper<>();
        marketWrapper.eq("status", "active");
        long marketItems = marketItemService.count(marketWrapper);
        stats.put("marketItems", marketItems);
        
        return Result.success(stats);
    }
    
    /**
     * 获取一周订单趋势数据（真实数据）
     */
    @GetMapping("/charts/orders-trend")
    public Result<?> getOrdersTrend() {
        Map<String, Object> result = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Long> orders = new ArrayList<>();
        
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            dates.add(date.format(formatter));
            
            // 查询当天订单数
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);
            QueryWrapper<FoodOrder> wrapper = new QueryWrapper<>();
            wrapper.between("create_time", dayStart, dayEnd);
            long count = foodOrderService.count(wrapper);
            orders.add(count);
        }
        
        result.put("dates", dates);
        result.put("orders", orders);
        return Result.success(result);
    }
    
    /**
     * 获取各模块数据占比（饼图）
     */
    @GetMapping("/charts/module-distribution")
    public Result<?> getModuleDistribution() {
        List<Map<String, Object>> data = new ArrayList<>();
        
        // 获取各模块真实数据
        long users = userService.count();
        long repairs = repairService.count();
        long orders = foodOrderService.count();
        long market = marketItemService.count();
        
        data.add(createPieData("用户", users));
        data.add(createPieData("报修", repairs));
        data.add(createPieData("订单", orders));
        data.add(createPieData("二手商品", market));
        
        return Result.success(data);
    }
    
    /**
     * 获取用户注册趋势（最近7天，真实数据）
     */
    @GetMapping("/charts/user-trend")
    public Result<?> getUserTrend() {
        Map<String, Object> result = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Long> users = new ArrayList<>();
        
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            dates.add(date.format(formatter));
            
            // 查询当天注册用户数
            LocalDateTime dayStart = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.between("create_time", dayStart, dayEnd);
            long count = userService.count(wrapper);
            users.add(count);
        }
        
        result.put("dates", dates);
        result.put("users", users);
        return Result.success(result);
    }
    
    /**
     * 获取报修状态统计（真实数据）
     */
    @GetMapping("/charts/repair-status")
    public Result<?> getRepairStatus() {
        List<Map<String, Object>> data = new ArrayList<>();
        
        // 待处理（received）
        QueryWrapper<Repair> pendingWrapper = new QueryWrapper<>();
        pendingWrapper.eq("status", "received");
        long pending = repairService.count(pendingWrapper);
        
        // 处理中（assigned + in_progress）
        QueryWrapper<Repair> processingWrapper = new QueryWrapper<>();
        processingWrapper.in("status", "assigned", "in_progress");
        long processing = repairService.count(processingWrapper);
        
        // 已完成（completed）
        QueryWrapper<Repair> completedWrapper = new QueryWrapper<>();
        completedWrapper.eq("status", "completed");
        long completed = repairService.count(completedWrapper);
        
        data.add(createPieData("待处理", pending));
        data.add(createPieData("处理中", processing));
        data.add(createPieData("已完成", completed));
        
        return Result.success(data);
    }
    
    private Map<String, Object> createPieData(String name, long value) {
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("value", value);
        return item;
    }
}
