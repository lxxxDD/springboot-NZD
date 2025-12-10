package com.zcpbysj.campusidletrade_server.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcpbysj.campusidletrade_server.common.Result;
import com.zcpbysj.campusidletrade_server.entity.Admin;
import com.zcpbysj.campusidletrade_server.entity.dto.admin.StatusDTO;
import com.zcpbysj.campusidletrade_server.service.IAdminService;
import com.zcpbysj.campusidletrade_server.service.ISystemLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理端-账号管理")
@RestController
@RequestMapping("/api/admin/accounts")
@RequiredArgsConstructor
public class AdminAccountController {

    private final IAdminService adminService;
    private final ISystemLogService systemLogService;

    @Operation(summary = "获取管理员列表")
    @GetMapping
    public Result<Page<Admin>> getAdminList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username) {
        Page<Admin> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            wrapper.like(Admin::getUsername, username);
        }
        wrapper.orderByDesc(Admin::getCreateTime);
        return Result.success(adminService.page(pageParam, wrapper));
    }

    @Operation(summary = "获取管理员详情")
    @GetMapping("/{id}")
    public Result<Admin> getAdminById(@PathVariable Long id) {
        Admin admin = adminService.getById(id);
        if (admin == null) {
            return Result.fail("管理员不存在");
        }
        admin.setPassword(null); // 不返回密码
        return Result.success(admin);
    }

    @Operation(summary = "创建管理员")
    @PostMapping
    public Result<?> createAdmin(@RequestBody Admin admin) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, admin.getUsername());
        if (adminService.count(wrapper) > 0) {
            return Result.fail("用户名已存在");
        }
        
        admin.setStatus(1); // 默认启用
        adminService.save(admin);
        systemLogService.addLog("创建管理员: " + admin.getUsername(), "success", "账号管理");
        return Result.success("创建成功");
    }

    @Operation(summary = "更新管理员")
    @PutMapping("/{id}")
    public Result<?> updateAdmin(@PathVariable Long id, @RequestBody Admin admin) {
        Admin existingAdmin = adminService.getById(id);
        if (existingAdmin == null) {
            return Result.fail("管理员不存在");
        }
        
        admin.setId(id);
        // 如果密码为空，保持原密码
        if (admin.getPassword() == null || admin.getPassword().isEmpty()) {
            admin.setPassword(existingAdmin.getPassword());
        }
        
        adminService.updateById(admin);
        systemLogService.addLog("更新管理员: " + admin.getUsername(), "info", "账号管理");
        return Result.success("更新成功");
    }

    @Operation(summary = "删除管理员")
    @DeleteMapping("/{id}")
    public Result<?> deleteAdmin(@PathVariable Long id) {
        if (!adminService.removeById(id)) {
            return Result.fail("删除失败");
        }
        systemLogService.addLog("删除管理员 #" + id, "warning", "账号管理");
        return Result.success("删除成功");
    }

    @Operation(summary = "更新管理员状态")
    @PostMapping("/{id}/status")
    public Result<?> updateAdminStatus(@PathVariable Long id, @RequestBody StatusDTO dto) {
        Integer status = Integer.valueOf(dto.getStatus());
        Admin admin = adminService.getById(id);
        if (admin == null) {
            return Result.fail("管理员不存在");
        }
        admin.setStatus(status);
        adminService.updateById(admin);
        systemLogService.addLog("管理员状态变更: " + (status == 1 ? "启用" : "禁用"), "info", "账号管理");
        return Result.success("状态更新成功");
    }

    @Operation(summary = "重置管理员密码")
    @PostMapping("/{id}/reset-password")
    public Result<?> resetPassword(@PathVariable Long id, @RequestBody StatusDTO dto) {
        String newPassword = dto.getReason();
        if (newPassword == null || newPassword.isEmpty()) {
            return Result.fail("新密码不能为空");
        }
        
        Admin admin = adminService.getById(id);
        if (admin == null) {
            return Result.fail("管理员不存在");
        }
        
        admin.setPassword(newPassword);
        adminService.updateById(admin);
        systemLogService.addLog("重置管理员密码: " + admin.getUsername(), "warning", "账号管理");
        return Result.success("密码重置成功");
    }
}
