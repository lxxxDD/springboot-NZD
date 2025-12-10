package com.zcpbysj.campusidletrade_server.controller.admin;

import com.zcpbysj.campusidletrade_server.common.Result;
import com.zcpbysj.campusidletrade_server.entity.Admin;
import com.zcpbysj.campusidletrade_server.entity.Role;
import com.zcpbysj.campusidletrade_server.entity.dto.admin.AdminLoginDTO;
import com.zcpbysj.campusidletrade_server.service.IAdminService;
import com.zcpbysj.campusidletrade_server.service.IRoleService;
import com.zcpbysj.campusidletrade_server.util.JwtUtil;
import com.zcpbysj.campusidletrade_server.service.ISystemLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "管理端-认证")
@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final IAdminService adminService;
    private final IRoleService roleService;
    private final JwtUtil jwtUtil;
    private final ISystemLogService systemLogService;

    @Operation(summary = "管理员登录")
    @PostMapping("/login")
    public Result<?> login(@RequestBody AdminLoginDTO loginForm) {
        String username = loginForm.getUsername();
        String password = loginForm.getPassword();
        
        if (username == null || password == null) {
            return Result.fail("用户名和密码不能为空");
        }
        
        Admin admin = adminService.login(username, password);
        if (admin == null) {
            return Result.fail("用户名或密码错误");
        }
        
        if (admin.getStatus() == 0) {
            return Result.fail("账号已被禁用");
        }
        
        // 获取角色名称
        String roleName = "ADMIN";
        if (admin.getRoleId() != null) {
            Role role = roleService.getById(admin.getRoleId());
            if (role != null) {
                roleName = role.getName();
            }
        }
        
        // 使用JWT生成token
        String token = jwtUtil.generateAccessToken(admin.getUsername(), admin.getId(), roleName);
        
        // 返回管理员信息
        Map<String, Object> result = new HashMap<>();
        result.put("id", admin.getId());
        result.put("username", admin.getUsername());
        result.put("nickname", admin.getNickname());
        result.put("avatar", admin.getAvatar());
        result.put("roleId", admin.getRoleId());
        result.put("token", token);
        
        // 获取角色信息
        if (admin.getRoleId() != null) {
            result.put("role", roleService.getById(admin.getRoleId()));
        }
        
        systemLogService.addLog("管理员登录: " + admin.getUsername(), "success", "系统登录");
        return Result.success(result);
    }

    @Operation(summary = "获取当前管理员信息")
    @GetMapping("/info")
    public Result<?> getAdminInfo(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return Result.fail("未登录或token无效");
        }
        
        try {
            // 处理 Bearer 前缀
            String actualToken = jwtUtil.extractTokenFromHeader(token);
            
            // 验证token
            if (!jwtUtil.validateToken(actualToken)) {
                return Result.fail("token已过期或无效");
            }
            
            // 从token中获取管理员ID
            Long adminId = jwtUtil.getUserIdFromToken(actualToken);
            Admin admin = adminService.getById(adminId);
            if (admin == null) {
                return Result.fail("管理员不存在");
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("id", admin.getId());
            result.put("username", admin.getUsername());
            result.put("nickname", admin.getNickname());
            result.put("avatar", admin.getAvatar());
            result.put("email", admin.getEmail());
            result.put("phone", admin.getPhone());
            result.put("roleId", admin.getRoleId());
            
            if (admin.getRoleId() != null) {
                result.put("role", roleService.getById(admin.getRoleId()));
            }
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.fail("token解析失败");
        }
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<?> logout() {
        // JWT是无状态的，客户端删除token即可
        return Result.success("退出成功");
    }
}
