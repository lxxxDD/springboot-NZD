package com.zcpbysj.campusidletrade_server.entity.vo.order;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单明细VO
 */
@Data
public class OrderItemVO {
    
    private Long id;
    
    /**
     * 商品ID
     */
    private Long itemId;
    
    /**
     * 商品名称
     */
    private String itemName;
    
    /**
     * 商品图片
     */
    private String itemImage;
    
    /**
     * 单价
     */
    private BigDecimal price;
    
    /**
     * 数量
     */
    private Integer quantity;
}
