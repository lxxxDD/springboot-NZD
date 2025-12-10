package com.zcpbysj.campusidletrade_server.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcpbysj.campusidletrade_server.common.Result;
import com.zcpbysj.campusidletrade_server.entity.Role;
import com.zcpbysj.campusidletrade_server.service.IRoleService;
import com.zcpbysj.campusidletrade_server.service.ISystemLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
public class AdminRoleController {

    private final IRoleService roleService;
    private final ISystemLogService systemLogService;

    /**
     * 获取角色列表（分页）
     */
    @GetMapping
    public Result<Page<Role>> getRoleList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name) {
        Page<Role> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(Role::getName, name);
        }
        wrapper.orderByDesc(Role::getCreateTime);
        return Result.success(roleService.page(pageParam, wrapper));
    }

    /**
     * 获取所有角色（下拉选择用）
     */
    @GetMapping("/all")
    public Result<List<Role>> getAllRoles() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getStatus, 1);
        return Result.success(roleService.list(wrapper));
    }

    /**
     * 获取角色详情
     */
    @GetMapping("/{id}")
    public Result<Role> getRoleById(@PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role == null) {
            return Result.fail("角色不存在");
        }
        return Result.success(role);
    }

    /**
     * 创建角色
     */
    @PostMapping
    public Result<?> createRole(@RequestBody Role role) {
        // 检查角色名是否已存在
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getName, role.getName());
        if (roleService.count(wrapper) > 0) {
            return Result.fail("角色名已存在");
        }
        
        role.setStatus(1); // 默认启用
        roleService.save(role);
        systemLogService.addLog("创建角色: " + role.getName(), "success", "角色管理");
        return Result.success("创建成功");
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    public Result<?> updateRole(@PathVariable Long id, @RequestBody Role role) {
        Role existingRole = roleService.getById(id);
        if (existingRole == null) {
            return Result.fail("角色不存在");
        }
        
        role.setId(id);
        roleService.updateById(role);
        systemLogService.addLog("更新角色: " + role.getName(), "info", "角色管理");
        return Result.success("更新成功");
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteRole(@PathVariable Long id) {
        if (!roleService.removeById(id)) {
            return Result.fail("删除失败");
        }
        systemLogService.addLog("删除角色 #" + id, "warning", "角色管理");
        return Result.success("删除成功");
    }

    /**
     * 更新角色状态
     */
    @PostMapping("/{id}/status")
    public Result<?> updateRoleStatus(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Integer status = Integer.valueOf(body.get("status").toString());
        Role role = roleService.getById(id);
        if (role == null) {
            return Result.fail("角色不存在");
        }
        role.setStatus(status);
        roleService.updateById(role);
        systemLogService.addLog("角色状态变更: " + (status == 1 ? "启用" : "禁用"), "info", "角色管理");
        return Result.success("状态更新成功");
    }
}
