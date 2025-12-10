package com.zcpbysj.campusidletrade_server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 菜品实体
 */
@Data
@TableName("food_item")
public class FoodItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属食堂ID
     */
    private Long canteenId;

    /**
     * 菜品名称
     */
    private String name;

    /**
     * 菜品描述
     */
    private String description;

    /**
     * 菜品图片
     */
    private String image;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 原价（用于显示折扣）
     */
    private BigDecimal originalPrice;

    /**
     * 分类：亚洲风味, 西式, 饮品, 小吃, 主食
     */
    private String category;

    /**
     * 标签，多个用逗号分隔：热销, 新品, 推荐, 辣
     */
    private String tags;

    /**
     * 月销量
     */
    private Integer monthlySales;

    /**
     * 评分 1-5
     */
    private BigDecimal rating;

    /**
     * 库存数量，-1表示不限
     */
    private Integer stock;

    /**
     * 状态：available-可售, soldout-售罄, offline-下架
     */
    private String status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
