package com.zcpbysj.campusidletrade_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcpbysj.campusidletrade_server.entity.FoodItem;
import com.zcpbysj.campusidletrade_server.entity.vo.food.FoodItemVO;

import java.util.List;

/**
 * 菜品服务接口
 */
public interface IFoodItemService extends IService<FoodItem> {

    /**
     * 根据食堂ID获取菜品列表
     */
    List<FoodItemVO> listByCanteenId(Long canteenId);

    /**
     * 根据ID获取菜品详情
     */
    FoodItemVO getFoodItemById(Long id);

    /**
     * 根据分类获取菜品
     */
    List<FoodItemVO> listByCategory(String category);

    /**
     * 搜索菜品
     */
    List<FoodItemVO> searchFoodItems(String keyword);

    /**
     * 获取热销菜品
     */
    List<FoodItemVO> listHotFoodItems(Integer limit);

    /**
     * 更新库存
     */
    boolean updateStock(Long id, Integer stock);

    /**
     * 更新菜品状态
     */
    boolean updateStatus(Long id, String status);
}
