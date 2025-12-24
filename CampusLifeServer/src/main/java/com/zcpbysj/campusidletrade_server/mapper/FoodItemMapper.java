package com.zcpbysj.campusidletrade_server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zcpbysj.campusidletrade_server.entity.FoodItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 菜品Mapper
 */
@Mapper
public interface FoodItemMapper extends BaseMapper<FoodItem> {
    
    /**
     * 乐观锁扣减库存
     * 只有当前库存等于expectedStock时才执行扣减，确保并发安全
     */
    @Update("UPDATE food_item SET stock = stock - #{quantity} WHERE id = #{id} AND stock = #{expectedStock} AND stock >= #{quantity}")
    int deductStock(@Param("id") Long id, @Param("quantity") Integer quantity, @Param("expectedStock") Integer expectedStock);
    
    /**
     * 恢复库存（订单取消时使用）
     */
    @Update("UPDATE food_item SET stock = stock + #{quantity} WHERE id = #{id} AND stock >= 0")
    int restoreStock(@Param("id") Long id, @Param("quantity") Integer quantity);
}
