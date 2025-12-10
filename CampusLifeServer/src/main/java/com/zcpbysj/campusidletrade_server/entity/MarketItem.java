package com.zcpbysj.campusidletrade_server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 二手商品表
 */
@Data
@TableName("market_items")
public class MarketItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 卖家ID
     */
    private Long sellerId;

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
     * 分类名称
     */
    private String category;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 成色
     */
    private String conditionLevel;

    /**
     * 图片列表 (JSON格式)
     */
    private String images;

    /**
     * 封面图
     */
    private String coverImage;

    /**
     * 状态: active-出售中, inactive-已下架, sold-已售出, deleted-已删除, violation-违规
     */
    private String status;

    /**
     * 拒绝原因（审核拒绝时填写）
     */
    private String rejectReason;

    /**
     * 浏览量
     */
    private Integer viewCount;

    /**
     * 收藏数
     */
    private Integer favoriteCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
