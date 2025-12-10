package com.zcpbysj.campusidletrade_server.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcpbysj.campusidletrade_server.common.Result;
import com.zcpbysj.campusidletrade_server.entity.Activity;
import com.zcpbysj.campusidletrade_server.entity.dto.admin.StatusDTO;
import com.zcpbysj.campusidletrade_server.service.IActivityService;
import com.zcpbysj.campusidletrade_server.service.ISystemLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理端-活动管理")
@RestController
@RequestMapping("/api/admin/activities")
@RequiredArgsConstructor
public class AdminActivityController {

    private final IActivityService activityService;
    private final ISystemLogService systemLogService;

    @Operation(summary = "获取活动列表")
    @GetMapping
    public Result<Page<Activity>> getActivityList(@RequestParam(defaultValue = "1") Integer page,
                                                  @RequestParam(defaultValue = "10") Integer size,
                                                  @RequestParam(required = false) String status,
                                                  @RequestParam(required = false) String title) {
        Page<Activity> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Activity::getStatus, status);
        }
        if (title != null && !title.isEmpty()) {
            wrapper.like(Activity::getTitle, title);
        }
        wrapper.orderByDesc(Activity::getCreateTime);
        return Result.success(activityService.page(pageParam, wrapper));
    }

    @Operation(summary = "更新活动状态")
    @PostMapping("/{id}/status")
    public Result<?> updateActivityStatus(@PathVariable Long id, @RequestBody StatusDTO dto) {
        String status = dto.getStatus();
        Activity activity = activityService.getById(id);
        if (activity == null) {
            return Result.fail("活动不存在");
        }
        activity.setStatus(status);
        activityService.updateById(activity);
        systemLogService.addLog("活动状态变更为: " + status, "info", "活动管理");
        return Result.success();
    }

    @Operation(summary = "获取活动详情")
    @GetMapping("/{id}")
    public Result<Activity> getActivityById(@PathVariable Long id) {
        Activity activity = activityService.getById(id);
        if (activity == null) {
            return Result.fail("活动不存在");
        }
        return Result.success(activity);
    }

    @Operation(summary = "创建/更新活动")
    @PostMapping
    public Result<?> saveActivity(@RequestBody Activity activity) {
        if (activity.getId() == null) {
            activity.setCurrentParticipants(0);
            if (activity.getStatus() == null) {
                activity.setStatus("upcoming");
            }
            activityService.save(activity);
            systemLogService.addLog("创建活动: " + activity.getTitle(), "success", "活动管理");
            return Result.success("创建成功");
        } else {
            activityService.updateById(activity);
            systemLogService.addLog("更新活动: " + activity.getTitle(), "info", "活动管理");
            return Result.success("更新成功");
        }
    }

    @Operation(summary = "删除活动")
    @DeleteMapping("/{id}")
    public Result<?> deleteActivity(@PathVariable Long id) {
        activityService.removeById(id);
        systemLogService.addLog("删除活动 #" + id, "warning", "活动管理");
        return Result.success("删除成功");
    }
}
