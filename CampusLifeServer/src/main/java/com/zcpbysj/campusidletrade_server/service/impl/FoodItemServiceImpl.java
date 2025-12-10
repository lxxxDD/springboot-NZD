package com.zcpbysj.campusidletrade_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcpbysj.campusidletrade_server.entity.Canteen;
import com.zcpbysj.campusidletrade_server.entity.FoodItem;
import com.zcpbysj.campusidletrade_server.entity.vo.food.FoodItemVO;
import com.zcpbysj.campusidletrade_server.mapper.FoodItemMapper;
import com.zcpbysj.campusidletrade_server.service.ICanteenService;
import com.zcpbysj.campusidletrade_server.service.IFoodItemService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品服务实现类
 */
@Service
public class FoodItemServiceImpl extends ServiceImpl<FoodItemMapper, FoodItem> implements IFoodItemService {

    @Autowired
    private ICanteenService canteenService;

    @Override
    public List<FoodItemVO> listByCanteenId(Long canteenId) {
        LambdaQueryWrapper<FoodItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FoodItem::getCanteenId, canteenId)
               .eq(FoodItem::getStatus, "available")
               .orderByDesc(FoodItem::getMonthlySales);
        List<FoodItem> foodItems = list(wrapper);
        return foodItems.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public FoodItemVO getFoodItemById(Long id) {
        FoodItem foodItem = getById(id);
        if (foodItem == null) {
            return null;
        }
        return convertToVO(foodItem);
    }

    @Override
    public List<FoodItemVO> listByCategory(String category) {
        LambdaQueryWrapper<FoodItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FoodItem::getCategory, category)
               .eq(FoodItem::getStatus, "available")
               .orderByDesc(FoodItem::getMonthlySales);
        List<FoodItem> foodItems = list(wrapper);
        return foodItems.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FoodItemVO> searchFoodItems(String keyword) {
        LambdaQueryWrapper<FoodItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(FoodItem::getName, keyword)
               .or()
               .like(FoodItem::getDescription, keyword)
               .eq(FoodItem::getStatus, "available");
        List<FoodItem> foodItems = list(wrapper);
        return foodItems.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FoodItemVO> listHotFoodItems(Integer limit) {
        LambdaQueryWrapper<FoodItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FoodItem::getStatus, "available")
               .orderByDesc(FoodItem::getMonthlySales)
               .last("LIMIT " + limit);
        List<FoodItem> foodItems = list(wrapper);
        return foodItems.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateStock(Long id, Integer stock) {
        FoodItem foodItem = getById(id);
        if (foodItem == null) {
            return false;
        }
        foodItem.setStock(stock);
        // 如果库存为0，自动设置为售罄
        if (stock == 0) {
            foodItem.setStatus("soldout");
        }
        return updateById(foodItem);
    }

    @Override
    public boolean updateStatus(Long id, String status) {
        FoodItem foodItem = getById(id);
        if (foodItem == null) {
            return false;
        }
        foodItem.setStatus(status);
        return updateById(foodItem);
    }

    /**
     * 将实体转换为VO
     */
    private FoodItemVO convertToVO(FoodItem foodItem) {
        FoodItemVO vo = new FoodItemVO();
        BeanUtils.copyProperties(foodItem, vo);
        // 获取食堂名称
        Canteen canteen = canteenService.getById(foodItem.getCanteenId());
        if (canteen != null) {
            vo.setCanteenName(canteen.getName());
        }
        return vo;
    }
}
