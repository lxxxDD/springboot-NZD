package com.zcpbysj.campusidletrade_server.controller;

import com.zcpbysj.campusidletrade_server.common.Result;
import com.zcpbysj.campusidletrade_server.entity.vo.common.PageVO;
import com.zcpbysj.campusidletrade_server.entity.vo.market.TransactionVO;
import com.zcpbysj.campusidletrade_server.service.ITransactionService;
import com.zcpbysj.campusidletrade_server.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 交易记录接口
 */
@Tag(name = "交易记录模块")
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final ITransactionService transactionService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "获取交易记录列表")
    @GetMapping
    public Result<PageVO<TransactionVO>> getTransactions(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        PageVO<TransactionVO> vo = transactionService.getTransactions(userId, page, size, type);
        return Result.success(vo);
    }
}
