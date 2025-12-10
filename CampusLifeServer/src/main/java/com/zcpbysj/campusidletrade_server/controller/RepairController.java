package com.zcpbysj.campusidletrade_server.controller;

import com.zcpbysj.campusidletrade_server.common.Result;
import com.zcpbysj.campusidletrade_server.entity.dto.repair.CreateRepairDTO;
import com.zcpbysj.campusidletrade_server.entity.dto.repair.RateRepairDTO;
import com.zcpbysj.campusidletrade_server.entity.vo.repair.RepairVO;
import com.zcpbysj.campusidletrade_server.service.IRepairService;
import com.zcpbysj.campusidletrade_server.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报修接口
 */
@Tag(name = "报修模块")
@RestController
@RequestMapping("/api/repairs")
@RequiredArgsConstructor
public class RepairController {

    private final IRepairService repairService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "获取报修列表")
    @GetMapping
    public Result<List<RepairVO>> getRepairs(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String status) {
        System.out.println("=== 获取报修列表 token=" + token + " ===");
        Long userId = jwtUtil.getUserIdFromToken(token);
        System.out.println("=== 解析出的 userId=" + userId + " ===");
        List<RepairVO> list = repairService.getRepairs(userId, status);
        return Result.success(list);
    }

    @Operation(summary = "获取报修详情")
    @GetMapping("/{id}")
    public Result<RepairVO> getRepairDetail(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        RepairVO vo = repairService.getRepairDetail(id, userId);
        return Result.success(vo);
    }

    @Operation(summary = "提交报修")
    @PostMapping
    public Result<Map<String, Long>> createRepair(
            @RequestHeader("Authorization") String token,
            @RequestBody CreateRepairDTO dto) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        Long repairId = repairService.createRepair(userId, dto);
        Map<String, Long> data = new HashMap<>();
        data.put("repairId", repairId);
        return Result.success(data);
    }

    @Operation(summary = "评价报修")
    @PostMapping("/{id}/rate")
    public Result<Void> rateRepair(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestBody RateRepairDTO dto) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        repairService.rateRepair(id, userId, dto);
        return Result.success();
    }
}
