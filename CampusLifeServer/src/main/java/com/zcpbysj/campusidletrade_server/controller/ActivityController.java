package com.zcpbysj.campusidletrade_server.controller;

import com.zcpbysj.campusidletrade_server.common.Result;
import com.zcpbysj.campusidletrade_server.entity.vo.activity.ActivityVO;
import com.zcpbysj.campusidletrade_server.entity.vo.common.PageVO;
import com.zcpbysj.campusidletrade_server.service.IActivityService;
import com.zcpbysj.campusidletrade_server.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 活动接口
 */
@Tag(name = "活动模块")
@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final IActivityService activityService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "获取活动列表")
    @GetMapping
    public Result<PageVO<ActivityVO>> getActivities(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = null;
        if (token != null && !token.isEmpty()) {
            try {
                userId = jwtUtil.getUserIdFromToken(token);
            } catch (Exception ignored) {}
        }
        PageVO<ActivityVO> vo = activityService.getActivities(status, page, size, userId);
        return Result.success(vo);
    }

    @Operation(summary = "获取活动详情")
    @GetMapping("/{id}")
    public Result<ActivityVO> getActivityDetail(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = null;
        if (token != null && !token.isEmpty()) {
            try {
                userId = jwtUtil.getUserIdFromToken(token);
            } catch (Exception ignored) {}
        }
        ActivityVO vo = activityService.getActivityDetail(id, userId);
        return Result.success(vo);
    }

    @Operation(summary = "报名活动")
    @PostMapping("/{id}/register")
    public Result<Void> registerActivity(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        activityService.registerActivity(id, userId);
        return Result.success();
    }

    @Operation(summary = "取消报名")
    @PostMapping("/{id}/cancel")
    public Result<Void> cancelRegistration(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        activityService.cancelRegistration(id, userId);
        return Result.success();
    }

    @Operation(summary = "获取我的活动")
    @GetMapping("/my")
    public Result<List<ActivityVO>> getMyActivities(
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        List<ActivityVO> list = activityService.getMyActivities(userId);
        return Result.success(list);
    }
}
