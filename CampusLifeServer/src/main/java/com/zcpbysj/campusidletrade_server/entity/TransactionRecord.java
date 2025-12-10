package com.zcpbysj.campusidletrade_server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易流水记录实体
 */
@Data
@TableName("transactions")
public class TransactionRecord {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 交易金额（正数为收入，负数为支出）
     */
    private BigDecimal amount;
    
    /**
     * 交易类型：recharge-充值, withdraw-提现, payment-二手商品支付, 
     * income-二手商品收入, food_payment-食堂订单支付
     */
    private String type;
    
    /**
     * 交易描述
     */
    private String description;
    
    /**
     * 关联订单ID
     */
    private Long orderId;
    
    /**
     * 交易后余额
     */
    private BigDecimal balanceAfter;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
