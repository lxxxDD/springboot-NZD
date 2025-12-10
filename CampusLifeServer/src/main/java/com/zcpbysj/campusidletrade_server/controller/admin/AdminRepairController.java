package com.zcpbysj.campusidletrade_server.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcpbysj.campusidletrade_server.common.Result;
import com.zcpbysj.campusidletrade_server.entity.Repair;
import com.zcpbysj.campusidletrade_server.entity.Technician;
import com.zcpbysj.campusidletrade_server.service.IRepairService;
import com.zcpbysj.campusidletrade_server.service.ISystemLogService;
import com.zcpbysj.campusidletrade_server.service.TechnicianService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/repairs")
@RequiredArgsConstructor
public class AdminRepairController {

    private final IRepairService repairService;
    private final ISystemLogService systemLogService;
    private final TechnicianService technicianService;

    // 报修列表
    @GetMapping
    public Result<Page<Repair>> getRepairList(@RequestParam(defaultValue = "1") Integer page,
                                              @RequestParam(defaultValue = "10") Integer size,
                                              @RequestParam(required = false) String status,
                                              @RequestParam(required = false) String keyword) {
        Page<Repair> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Repair> wrapper = new LambdaQueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Repair::getStatus, status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Repair::getLocation, keyword)
                .or().like(Repair::getIssueType, keyword)
                .or().like(Repair::getDescription, keyword));
        }
        wrapper.orderByDesc(Repair::getCreateTime);
        Page<Repair> result = repairService.page(pageParam, wrapper);
        
        // 填充维修人员姓名
        for (Repair repair : result.getRecords()) {
            if (repair.getTechnicianId() != null) {
                Technician tech = technicianService.getById(repair.getTechnicianId());
                if (tech != null) {
                    repair.setTechnicianName(tech.getName());
                }
            }
        }
        return Result.success(result);
    }

    // 更新报修状态 (派单/完成)
    @PostMapping("/{id}/status")
    public Result<?> updateRepairStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        Repair repair = repairService.getById(id);
        if (repair == null) {
            return Result.fail("报修单不存在");
        }
        repair.setStatus(status);
        repairService.updateById(repair);
        systemLogService.addLog("报修单状态变更为: " + status, "info", "报修管理");
        return Result.success();
    }
    
    // 获取报修详情
    @GetMapping("/{id}")
    public Result<Repair> getRepairById(@PathVariable Long id) {
        Repair repair = repairService.getById(id);
        if (repair == null) {
            return Result.fail("报修单不存在");
        }
        return Result.success(repair);
    }

    // 删除报修单
    @DeleteMapping("/{id}")
    public Result<?> deleteRepair(@PathVariable Long id) {
        repairService.removeById(id);
        systemLogService.addLog("删除报修单 #" + id, "warning", "报修管理");
        return Result.success("删除成功");
    }

    // 指派维修人员
    @PostMapping("/{id}/assign")
    public Result<?> assignRepair(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        Long technicianId = body.get("technicianId");
        Repair repair = repairService.getById(id);
        if (repair == null) {
            return Result.fail("报修单不存在");
        }
        repair.setTechnicianId(technicianId);
        repair.setStatus("in_progress");
        repairService.updateById(repair);
        systemLogService.addLog("报修单 #" + id + " 已指派维修人员 #" + technicianId, "info", "报修管理");
        return Result.success("指派成功");
    }
}
