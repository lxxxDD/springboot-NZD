package com.zcpbysj.campusidletrade_server.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcpbysj.campusidletrade_server.common.Result;
import com.zcpbysj.campusidletrade_server.entity.SystemLog;
import com.zcpbysj.campusidletrade_server.service.ISystemLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统日志Controller
 */
@RestController
@RequestMapping("/api/admin/logs")
@RequiredArgsConstructor
public class AdminSystemLogController {

    private final ISystemLogService systemLogService;

    /**
     * 获取最近日志
     */
    @GetMapping("/recent")
    public Result<List<SystemLog>> getRecentLogs(@RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(systemLogService.getRecentLogs(limit));
    }

    /**
     * 分页查询日志
     */
    @GetMapping
    public Result<Page<SystemLog>> getLogList(@RequestParam(defaultValue = "1") Integer page,
                                               @RequestParam(defaultValue = "20") Integer size,
                                               @RequestParam(required = false) String level,
                                               @RequestParam(required = false) String module) {
        Page<SystemLog> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SystemLog> wrapper = new LambdaQueryWrapper<>();
        
        if (level != null && !level.isEmpty()) {
            wrapper.eq(SystemLog::getType, level);
        }
        if (module != null && !module.isEmpty()) {
            wrapper.like(SystemLog::getModule, module);
        }
        
        wrapper.orderByDesc(SystemLog::getCreateTime);
        return Result.success(systemLogService.page(pageParam, wrapper));
    }
}
