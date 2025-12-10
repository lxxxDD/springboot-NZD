package com.zcpbysj.campusidletrade_server.entity.vo.order;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单VO
 */
@Data
public class OrderVO {
    
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 买家ID
     */
    private Long buyerId;
    
    /**
     * 买家名称
     */
    private String buyerName;

    /**
     * 买家头像
     */
    private String buyerAvatar;
    
    /**
     * 卖家ID
     */
    private Long sellerId;
    
    /**
     * 卖家名称
     */
    private String sellerName;

    /**
     * 卖家头像
     */
    private String sellerAvatar;
    
    /**
     * 订单类型
     */
    private String orderType;
    
    /**
     * 订单金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 支付方式
     */
    private String paymentMethod;
    
    /**
     * 状态
     */
    private String status;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 订单明细
     */
    private List<OrderItemVO> items;
    
    /**
     * 支付时间
     */
    private LocalDateTime paidAt;
    
    /**
     * 完成时间
     */
    private LocalDateTime completedAt;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
