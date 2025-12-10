package com.zcpbysj.campusidletrade_server.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcpbysj.campusidletrade_server.common.Result;
import com.zcpbysj.campusidletrade_server.entity.TransactionRecord;
import com.zcpbysj.campusidletrade_server.entity.User;
import com.zcpbysj.campusidletrade_server.entity.vo.admin.AdminTransactionVO;
import com.zcpbysj.campusidletrade_server.service.ITransactionService;
import com.zcpbysj.campusidletrade_server.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 财务管理控制器
 */
@RestController
@RequestMapping("/api/admin/finance")
@RequiredArgsConstructor
public class AdminFinanceController {

    private final ITransactionService transactionService;
    private final IUserService userService;

    /**
     * 获取交易记录列表（包含用户信息）
     */
    @GetMapping("/transactions")
    public Result<Map<String, Object>> getTransactionList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String type) {
        Page<TransactionRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<TransactionRecord> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(TransactionRecord::getUserId, userId);
        }
        if (type != null && !type.isEmpty()) {
            wrapper.eq(TransactionRecord::getType, type);
        }
        wrapper.orderByDesc(TransactionRecord::getCreateTime);
        Page<TransactionRecord> pageResult = transactionService.page(pageParam, wrapper);
        
        // 转换为VO，添加用户信息
        List<AdminTransactionVO> voList = pageResult.getRecords().stream()
                .map(record -> {
                    AdminTransactionVO vo = new AdminTransactionVO();
                    BeanUtils.copyProperties(record, vo);
                    // 查询用户信息
                    User user = userService.getById(record.getUserId());
                    if (user != null) {
                        vo.setUsername(user.getUsername());
                        vo.setAvatar(user.getAvatar());
                    }
                    return vo;
                })
                .collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("records", voList);
        result.put("total", pageResult.getTotal());
        return Result.success(result);
    }

    /**
     * 获取财务统计数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getFinanceStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 总交易金额
        LambdaQueryWrapper<TransactionRecord> allWrapper = new LambdaQueryWrapper<>();
        stats.put("totalTransactions", transactionService.count(allWrapper));
        
        // 充值总额
        LambdaQueryWrapper<TransactionRecord> rechargeWrapper = new LambdaQueryWrapper<>();
        rechargeWrapper.eq(TransactionRecord::getType, "recharge");
        stats.put("rechargeCount", transactionService.count(rechargeWrapper));
        
        // 消费总额
        LambdaQueryWrapper<TransactionRecord> paymentWrapper = new LambdaQueryWrapper<>();
        paymentWrapper.eq(TransactionRecord::getType, "payment");
        stats.put("paymentCount", transactionService.count(paymentWrapper));
        
        // 用户总余额（简化计算）
        stats.put("totalUserBalance", 0);
        
        return Result.success(stats);
    }

    /**
     * 为用户充值
     */
    @PostMapping("/recharge")
    public Result<?> rechargeUser(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        String remark = body.get("remark") != null ? body.get("remark").toString() : "管理员充值";
        
        User user = userService.getById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        
        // 更新用户余额
        BigDecimal newBalance = user.getBalance().add(amount);
        user.setBalance(newBalance);
        userService.updateById(user);
        
        // 创建交易记录
        TransactionRecord record = new TransactionRecord();
        record.setUserId(userId);
        record.setAmount(amount);
        record.setType("recharge");
        record.setDescription(remark);
        record.setBalanceAfter(newBalance);
        transactionService.save(record);
        
        return Result.success("充值成功");
    }

    /**
     * 获取交易详情
     */
    @GetMapping("/transactions/{id}")
    public Result<TransactionRecord> getTransactionById(@PathVariable Long id) {
        TransactionRecord record = transactionService.getById(id);
        if (record == null) {
            return Result.fail("交易记录不存在");
        }
        return Result.success(record);
    }
}
