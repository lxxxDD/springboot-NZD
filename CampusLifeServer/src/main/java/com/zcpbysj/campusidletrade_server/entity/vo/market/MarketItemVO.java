package com.zcpbysj.campusidletrade_server.entity.vo.market;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品信息VO
 */
@Data
public class MarketItemVO {
    
    private Long id;
    
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
     * 商品标题
     */
    private String title;
    
    /**
     * 商品描述
     */
    private String description;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 原价
     */
    private BigDecimal originalPrice;
    
    /**
     * 分类
     */
    private String category;
    
    /**
     * 成色
     */
    private String conditionLevel;
    
    /**
     * 图片列表 (JSON格式字符串)
     */
    private String images;
    
    /**
     * 封面图
     */
    private String coverImage;
    
    /**
     * 状态
     */
    private String status;
    
    /**
     * 浏览量
     */
    private Integer viewCount;
    
    /**
     * 收藏数
     */
    private Integer favoriteCount;
    
    /**
     * 当前用户是否收藏
     */
    private Boolean isFavorite;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
