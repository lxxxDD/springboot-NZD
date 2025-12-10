package com.zcpbysj.campusidletrade_server.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcpbysj.campusidletrade_server.common.Result;
import com.zcpbysj.campusidletrade_server.entity.UserVerification;
import com.zcpbysj.campusidletrade_server.service.IUserVerificationService;
import com.zcpbysj.campusidletrade_server.service.ISystemLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户实名认证管理控制器
 */
@RestController
@RequestMapping("/api/admin/verifications")
@RequiredArgsConstructor
public class AdminVerificationController {

    private final IUserVerificationService userVerificationService;
    private final ISystemLogService systemLogService;

    /**
     * 获取认证申请列表
     */
    @GetMapping
    public Result<Page<UserVerification>> getVerificationList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        Page<UserVerification> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<UserVerification> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(UserVerification::getStatus, status);
        }
        wrapper.orderByDesc(UserVerification::getCreateTime);
        return Result.success(userVerificationService.page(pageParam, wrapper));
    }

    /**
     * 获取认证详情
     */
    @GetMapping("/{id}")
    public Result<UserVerification> getVerificationById(@PathVariable Long id) {
        UserVerification verification = userVerificationService.getById(id);
        if (verification == null) {
            return Result.fail("认证记录不存在");
        }
        return Result.success(verification);
    }

    /**
     * 审核通过
     */
    @PostMapping("/{id}/approve")
    public Result<?> approveVerification(@PathVariable Long id, @RequestBody(required = false) Map<String, Object> body) {
        UserVerification verification = userVerificationService.getById(id);
        if (verification == null) {
            return Result.fail("认证记录不存在");
        }
        
        verification.setStatus(1); // 已通过
        verification.setReviewTime(LocalDateTime.now());
        if (body != null && body.get("remark") != null) {
            verification.setRemark(body.get("remark").toString());
        }
        
        userVerificationService.updateById(verification);
        systemLogService.addLog("审核通过认证 #" + id, "success", "认证管理");
        return Result.success("审核通过");
    }

    /**
     * 审核拒绝
     */
    @PostMapping("/{id}/reject")
    public Result<?> rejectVerification(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        UserVerification verification = userVerificationService.getById(id);
        if (verification == null) {
            return Result.fail("认证记录不存在");
        }
        
        String remark = body.get("remark") != null ? body.get("remark").toString() : "审核未通过";
        
        verification.setStatus(2); // 已拒绝
        verification.setReviewTime(LocalDateTime.now());
        verification.setRemark(remark);
        
        userVerificationService.updateById(verification);
        systemLogService.addLog("拒绝认证 #" + id, "warning", "认证管理");
        return Result.success("已拒绝");
    }

    /**
     * 删除认证记录
     */
    @DeleteMapping("/{id}")
    public Result<?> deleteVerification(@PathVariable Long id) {
        if (!userVerificationService.removeById(id)) {
            return Result.fail("删除失败");
        }
        systemLogService.addLog("删除认证记录 #" + id, "warning", "认证管理");
        return Result.success("删除成功");
    }
}
