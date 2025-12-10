package com.zcpbysj.campusidletrade_server.entity.dto.food;

import lombok.Data;

import java.util.List;

/**
 * 创建食堂订单请求DTO
 */
@Data
public class CreateFoodOrderDTO {
    
    /**
     * 食堂ID
     */
    private Long canteenId;
    
    /**
     * 订单项列表
     */
    private List<FoodOrderItemDTO> items;
    
    /**
     * 备注
     */
    private String remark;
    
    @Data
    public static class FoodOrderItemDTO {
        /**
         * 菜品ID
         */
        private Long foodItemId;
        
        /**
         * 数量
         */
        private Integer quantity;
    }
}
