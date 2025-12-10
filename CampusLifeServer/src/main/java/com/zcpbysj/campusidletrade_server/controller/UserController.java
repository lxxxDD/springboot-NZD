package com.zcpbysj.campusidletrade_server.controller;

import com.zcpbysj.campusidletrade_server.common.Result;
import com.zcpbysj.campusidletrade_server.entity.dto.user.LoginDTO;
import com.zcpbysj.campusidletrade_server.entity.dto.user.RegisterDTO;
import com.zcpbysj.campusidletrade_server.entity.dto.user.TopupDTO;
import com.zcpbysj.campusidletrade_server.entity.dto.user.UpdateProfileDTO;
import com.zcpbysj.campusidletrade_server.entity.vo.user.LoginVO;
import com.zcpbysj.campusidletrade_server.entity.vo.user.UserInfoVO;
import com.zcpbysj.campusidletrade_server.service.IUserService;
import com.zcpbysj.campusidletrade_server.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户接口
 */
@Tag(name = "用户模块")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<LoginVO> register(@RequestBody RegisterDTO dto) {
        LoginVO vo = userService.register(dto);
        return Result.success(vo);
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        LoginVO vo = userService.login(dto);
        return Result.success(vo);
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/profile")
    public Result<UserInfoVO> getProfile(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        UserInfoVO vo = userService.getProfile(userId);
        return Result.success(vo);
    }

    @Operation(summary = "更新用户信息")
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestHeader("Authorization") String token,
                                      @RequestBody UpdateProfileDTO dto) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        userService.updateProfile(userId, dto);
        return Result.success();
    }

    @Operation(summary = "获取余额")
    @GetMapping("/balance")
    public Result<Map<String, BigDecimal>> getBalance(@RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        BigDecimal balance = userService.getBalance(userId);
        Map<String, BigDecimal> data = new HashMap<>();
        data.put("balance", balance);
        return Result.success(data);
    }

    @Operation(summary = "充值")
    @PostMapping("/topup")
    public Result<Map<String, BigDecimal>> topup(@RequestHeader("Authorization") String token,
                                                  @RequestBody TopupDTO dto) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        BigDecimal newBalance = userService.topup(userId, dto);
        Map<String, BigDecimal> data = new HashMap<>();
        data.put("newBalance", newBalance);
        return Result.success(data);
    }
    @Operation(summary = "获取用户在线状态")
    @GetMapping("/status/{userId}")
    public Result<Map<String, Object>> getUserStatus(@PathVariable Long userId) {
        boolean isOnline = com.zcpbysj.campusidletrade_server.websocket.WebSocketServer.isOnline(userId);
        Map<String, Object> data = new HashMap<>();
        data.put("online", isOnline);
        return Result.success(data);
    }
}
