package com.zcpbysj.campusidletrade_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcpbysj.campusidletrade_server.entity.*;
import com.zcpbysj.campusidletrade_server.entity.dto.food.CreateFoodOrderDTO;
import com.zcpbysj.campusidletrade_server.entity.vo.food.FoodOrderVO;
import com.zcpbysj.campusidletrade_server.entity.vo.common.PageVO;
import com.zcpbysj.campusidletrade_server.mapper.FoodOrderItemMapper;
import com.zcpbysj.campusidletrade_server.mapper.FoodOrderMapper;
import com.zcpbysj.campusidletrade_server.mapper.CanteenMapper;
import com.zcpbysj.campusidletrade_server.mapper.FoodItemMapper;
import com.zcpbysj.campusidletrade_server.mapper.UserMapper;
import com.zcpbysj.campusidletrade_server.mapper.TransactionRecordMapper;
import com.zcpbysj.campusidletrade_server.service.IFoodOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 食堂订单服务实现类
 */
@Service
@RequiredArgsConstructor
public class FoodOrderServiceImpl extends ServiceImpl<FoodOrderMapper, FoodOrder> implements IFoodOrderService {

    private final FoodOrderMapper foodOrderMapper;
    private final FoodOrderItemMapper foodOrderItemMapper;
    private final CanteenMapper canteenMapper;
    private final FoodItemMapper foodItemMapper;
    private final UserMapper userMapper;
    private final TransactionRecordMapper transactionRecordMapper;

    @Override
    @Transactional
    public FoodOrderVO createOrder(Long userId, CreateFoodOrderDTO dto) {
        // 获取食堂信息
        Canteen canteen = canteenMapper.selectById(dto.getCanteenId());
        if (canteen == null) {
            throw new RuntimeException("食堂不存在");
        }
        
        // 检查食堂是否营业
        if (!"open".equals(canteen.getStatus())) {
            throw new RuntimeException("该食堂已打烊，暂时无法点餐");
        }

        // 计算总金额并创建订单项
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<FoodOrderItem> orderItems = new ArrayList<>();

        for (CreateFoodOrderDTO.FoodOrderItemDTO itemDTO : dto.getItems()) {
            FoodItem foodItem = foodItemMapper.selectById(itemDTO.getFoodItemId());
            if (foodItem == null) {
                throw new RuntimeException("菜品不存在: " + itemDTO.getFoodItemId());
            }
            if (!"available".equals(foodItem.getStatus())) {
                throw new RuntimeException("菜品已下架: " + foodItem.getName());
            }

            BigDecimal subtotal = foodItem.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            FoodOrderItem orderItem = new FoodOrderItem();
            orderItem.setFoodItemId(foodItem.getId());
            orderItem.setFoodName(foodItem.getName());
            orderItem.setFoodImage(foodItem.getImage());
            orderItem.setPrice(foodItem.getPrice());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setSubtotal(subtotal);
            orderItems.add(orderItem);
        }

        // 创建订单
        FoodOrder order = new FoodOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setCanteenId(dto.getCanteenId());
        order.setCanteenName(canteen.getName());
        order.setTotalAmount(totalAmount);
        order.setStatus("pending"); // 待支付
        order.setRemark(dto.getRemark());
        foodOrderMapper.insert(order);

        // 保存订单项
        for (FoodOrderItem item : orderItems) {
            item.setOrderId(order.getId());
            foodOrderItemMapper.insert(item);
        }

        return convertToVO(order, orderItems);
    }

    @Override
    @Transactional
    public Boolean payOrder(Long orderId, Long userId, String paymentMethod) {
        FoodOrder order = foodOrderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }
        if (!"pending".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确，无法支付");
        }

        // 检查用户余额
        User user = userMapper.selectById(userId);
        if (user.getBalance().compareTo(order.getTotalAmount()) < 0) {
            throw new RuntimeException("余额不足");
        }

        // 扣除用户余额
        user.setBalance(user.getBalance().subtract(order.getTotalAmount()));
        userMapper.updateById(user);

        // 更新订单状态
        order.setStatus("paid");
        order.setPaymentMethod(paymentMethod);
        order.setPaidAt(LocalDateTime.now());
        order.setPickupCode(generatePickupCode());
        foodOrderMapper.updateById(order);

        // 记录交易流水 - 食堂订单收入归平台/食堂，不是卖家
        TransactionRecord record = new TransactionRecord();
        record.setUserId(userId);
        record.setAmount(order.getTotalAmount().negate()); // 负数表示支出
        record.setType("payment");
        record.setDescription("食堂订单支付 - " + order.getCanteenName());
        record.setOrderId(order.getId());
        record.setBalanceAfter(user.getBalance());
        transactionRecordMapper.insert(record);

        return true;
    }

    @Override
    public FoodOrderVO getOrderDetail(Long orderId, Long userId) {
        FoodOrder order = foodOrderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权查看此订单");
        }

        List<FoodOrderItem> items = foodOrderItemMapper.selectList(
                new LambdaQueryWrapper<FoodOrderItem>().eq(FoodOrderItem::getOrderId, orderId)
        );

        return convertToVO(order, items);
    }

    @Override
    public PageVO<FoodOrderVO> getOrderList(Long userId, Integer page, Integer size, String status) {
        Page<FoodOrder> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<FoodOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FoodOrder::getUserId, userId);
        if (status != null && !status.isEmpty()) {
            wrapper.eq(FoodOrder::getStatus, status);
        }
        wrapper.orderByDesc(FoodOrder::getCreateTime);

        Page<FoodOrder> orderPage = foodOrderMapper.selectPage(pageParam, wrapper);

        List<FoodOrderVO> voList = orderPage.getRecords().stream().map(order -> {
            List<FoodOrderItem> items = foodOrderItemMapper.selectList(
                    new LambdaQueryWrapper<FoodOrderItem>().eq(FoodOrderItem::getOrderId, order.getId())
            );
            return convertToVO(order, items);
        }).collect(Collectors.toList());

        PageVO<FoodOrderVO> pageVO = new PageVO<>();
        pageVO.setList(voList);
        pageVO.setTotal(orderPage.getTotal());
        pageVO.setHasMore(orderPage.getCurrent() < orderPage.getPages());
        return pageVO;
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        FoodOrder order = foodOrderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }
        if (!"pending".equals(order.getStatus())) {
            throw new RuntimeException("只能取消待支付的订单");
        }

        order.setStatus("cancelled");
        foodOrderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void completeOrder(Long orderId, Long userId) {
        FoodOrder order = foodOrderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }
        if (!"paid".equals(order.getStatus())) {
            throw new RuntimeException("只能完成已支付的订单");
        }

        order.setStatus("completed");
        order.setCompletedAt(LocalDateTime.now());
        foodOrderMapper.updateById(order);
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "FO" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", new Random().nextInt(10000));
    }

    /**
     * 生成取餐码
     */
    private String generatePickupCode() {
        return String.format("%04d", new Random().nextInt(10000));
    }

    /**
     * 转换为VO
     */
    private FoodOrderVO convertToVO(FoodOrder order, List<FoodOrderItem> items) {
        FoodOrderVO vo = new FoodOrderVO();
        BeanUtils.copyProperties(order, vo);
        
        // 设置状态文本
        vo.setStatusText(getStatusText(order.getStatus()));

        // 转换订单项
        List<FoodOrderVO.FoodOrderItemVO> itemVOs = items.stream().map(item -> {
            FoodOrderVO.FoodOrderItemVO itemVO = new FoodOrderVO.FoodOrderItemVO();
            BeanUtils.copyProperties(item, itemVO);
            return itemVO;
        }).collect(Collectors.toList());
        vo.setItems(itemVOs);

        return vo;
    }

    /**
     * 获取状态文本
     */
    private String getStatusText(String status) {
        switch (status) {
            case "pending": return "待支付";
            case "paid": return "已支付";
            case "completed": return "已完成";
            case "cancelled": return "已取消";
            default: return "未知";
        }
    }
}
