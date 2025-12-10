package com.zcpbysj.campusidletrade_server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 食堂订单表
 */
@Data
@TableName("food_order")
public class FoodOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 食堂ID
     */
    private Long canteenId;

    /**
     * 食堂名称
     */
    private String canteenName;

    /**
     * 订单金额
     */
    private BigDecimal totalAmount;

    /**
     * 支付方式: balance/wechat/alipay
     */
    private String paymentMethod;

    /**
     * 状态: pending-待支付, paid-已支付, preparing-制作中, ready-待取餐, completed-已完成, cancelled-已取消, refunded-已退款
     */
    private String status;

    /**
     * 取餐码
     */
    private String pickupCode;

    /**
     * 备注
     */
    private String remark;

    /**
     * 支付时间
     */
    private LocalDateTime paidAt;

    /**
     * 完成时间
     */
    private LocalDateTime completedAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
