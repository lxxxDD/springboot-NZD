package com.zcpbysj.campusidletrade_server.entity.vo.food;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 菜品VO
 */
@Data
public class FoodItemVO {
    private Long id;
    private Long canteenId;
    private String canteenName;
    private String name;
    private String description;
    private String image;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String category;
    private String tags;
    private Integer monthlySales;
    private BigDecimal rating;
    private Integer stock;
    private String status;
}
