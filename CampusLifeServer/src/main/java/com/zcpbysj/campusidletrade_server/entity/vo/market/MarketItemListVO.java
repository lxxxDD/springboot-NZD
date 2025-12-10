package com.zcpbysj.campusidletrade_server.entity.vo.market;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品列表VO (简化版)
 */
@Data
public class MarketItemListVO {
    
    private Long id;
    
    /**
     * 卖家名称
     */
    private String sellerName;
    
    /**
     * 商品标题
     */
    private String title;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 封面图
     */
    private String coverImage;
    
    /**
     * 状态
     */
    private String status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 浏览量
     */
    private Integer viewCount;
}
