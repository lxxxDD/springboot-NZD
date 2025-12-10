package com.zcpbysj.campusidletrade_server.entity.vo.food;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 食堂订单VO
 */
@Data
public class FoodOrderVO {
    
    private Long id;
    private String orderNo;
    private Long userId;
    private Long canteenId;
    private String canteenName;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String status;
    private String statusText;
    private String pickupCode;
    private String remark;
    private LocalDateTime paidAt;
    private LocalDateTime completedAt;
    private LocalDateTime createTime;
    
    /**
     * 订单项列表
     */
    private List<FoodOrderItemVO> items;
    
    @Data
    public static class FoodOrderItemVO {
        private Long id;
        private Long foodItemId;
        private String foodName;
        private String foodImage;
        private BigDecimal price;
        private Integer quantity;
        private BigDecimal subtotal;
    }
}
