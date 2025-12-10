package com.zcpbysj.campusidletrade_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcpbysj.campusidletrade_server.entity.*;
import com.zcpbysj.campusidletrade_server.entity.dto.order.CreateOrderDTO;
import com.zcpbysj.campusidletrade_server.entity.vo.order.OrderItemVO;
import com.zcpbysj.campusidletrade_server.entity.vo.order.OrderVO;
import com.zcpbysj.campusidletrade_server.entity.vo.common.PageVO;
import com.zcpbysj.campusidletrade_server.mapper.*;
import com.zcpbysj.campusidletrade_server.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    private final OrderItemMapper orderItemMapper;
    private final MarketItemMapper marketItemMapper;
    private final UserMapper userMapper;
    private final TransactionMapper transactionMapper;

    @Override
    public PageVO<OrderVO> getOrders(Long userId, Integer page, Integer size, String type, String status, String role) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        
        if ("buyer".equals(role)) {
            wrapper.eq(Order::getBuyerId, userId);
        } else if ("seller".equals(role)) {
            wrapper.eq(Order::getSellerId, userId);
        } else {
            wrapper.and(w -> w.eq(Order::getBuyerId, userId).or().eq(Order::getSellerId, userId));
        }
        
        if (StringUtils.hasText(type)) {
            wrapper.eq(Order::getOrderType, type);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Order::getStatus, status);
        }
        
        wrapper.orderByDesc(Order::getCreateTime);
        
        Page<Order> pageResult = page(new Page<>(page, size), wrapper);
        
        List<OrderVO> list = pageResult.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        return PageVO.of(list, pageResult.getTotal(), page, size);
    }

    @Override
    public OrderVO getOrderDetail(Long id, Long userId) {
        Order order = getById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new RuntimeException("无权查看此订单");
        }
        
        return convertToVO(order);
    }

    @Override
    @Transactional
    public OrderVO createOrder(Long buyerId, CreateOrderDTO dto) {
        MarketItem item = marketItemMapper.selectById(dto.getItemId());
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!"active".equals(item.getStatus())) {
            throw new RuntimeException("商品已下架或已售出");
        }
        if (item.getSellerId().equals(buyerId)) {
            throw new RuntimeException("不能购买自己的商品");
        }
        
        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setBuyerId(buyerId);
        order.setSellerId(item.getSellerId());
        order.setOrderType(dto.getType());
        order.setTotalAmount(item.getPrice());
        order.setStatus("pending");
        save(order);
        
        // 创建订单明细
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(order.getId());
        orderItem.setItemId(item.getId());
        orderItem.setItemName(item.getTitle());
        orderItem.setItemImage(item.getCoverImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setQuantity(1);
        orderItemMapper.insert(orderItem);
        
        return convertToVO(order);
    }

    @Override
    @Transactional
    public Boolean payOrder(Long id, Long userId, String paymentMethod) {
        Order order = getById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getBuyerId().equals(userId)) {
            throw new RuntimeException("无权支付此订单");
        }
        if (!"pending".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确");
        }
        
        // 检查余额
        User buyer = userMapper.selectById(userId);
        if (buyer.getBalance().compareTo(order.getTotalAmount()) < 0) {
            throw new RuntimeException("余额不足");
        }
        
        // 扣除买家余额
        BigDecimal newBuyerBalance = buyer.getBalance().subtract(order.getTotalAmount());
        buyer.setBalance(newBuyerBalance);
        userMapper.updateById(buyer);
        
        // 记录买家交易
        Transaction buyerTx = new Transaction();
        buyerTx.setUserId(userId);
        buyerTx.setType("payment");
        buyerTx.setAmount(order.getTotalAmount().negate());
        buyerTx.setBalanceAfter(newBuyerBalance);
        buyerTx.setOrderId(order.getId());
        buyerTx.setDescription("购买商品");
        transactionMapper.insert(buyerTx);
        
        // 增加卖家余额
        User seller = userMapper.selectById(order.getSellerId());
        BigDecimal newSellerBalance = seller.getBalance().add(order.getTotalAmount());
        seller.setBalance(newSellerBalance);
        userMapper.updateById(seller);
        
        // 记录卖家交易
        Transaction sellerTx = new Transaction();
        sellerTx.setUserId(order.getSellerId());
        sellerTx.setType("income");
        sellerTx.setAmount(order.getTotalAmount());
        sellerTx.setBalanceAfter(newSellerBalance);
        sellerTx.setOrderId(order.getId());
        sellerTx.setDescription("商品售出");
        transactionMapper.insert(sellerTx);
        
        // 更新订单状态
        order.setPaymentMethod(paymentMethod);
        order.setStatus("paid");
        order.setPaidAt(LocalDateTime.now());
        updateById(order);
        
        // 更新商品状态为已售
        List<OrderItem> orderItems = orderItemMapper.selectList(
            new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId())
        );
        for (OrderItem oi : orderItems) {
            MarketItem item = marketItemMapper.selectById(oi.getItemId());
            if (item != null) {
                item.setStatus("sold");
                marketItemMapper.updateById(item);
            }
        }
        
        return true;
    }

    @Override
    public void updateOrderStatus(Long id, Long userId, String status) {
        Order order = getById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }
        
        order.setStatus(status);
        if ("completed".equals(status)) {
            order.setCompletedAt(LocalDateTime.now());
        }
        updateById(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long id, Long userId, String reason) {
        Order order = getById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!order.getBuyerId().equals(userId)) {
            throw new RuntimeException("无权取消此订单");
        }
        if (!"pending".equals(order.getStatus())) {
            throw new RuntimeException("只能取消待支付的订单");
        }
        
        order.setStatus("cancelled");
        order.setRemark(reason);
        updateById(order);
    }
    
    private String generateOrderNo() {
        // yyyyMMddHHmmssSSS (17位) + 6位随机数
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String random = String.format("%06d", new Random().nextInt(1000000));
        return "XYT"+ timestamp + random;
    }
    
    private OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setBuyerId(order.getBuyerId());
        vo.setSellerId(order.getSellerId());
        vo.setOrderType(order.getOrderType());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setPaymentMethod(order.getPaymentMethod());
        vo.setStatus(order.getStatus());
        vo.setRemark(order.getRemark());
        vo.setPaidAt(order.getPaidAt());
        vo.setCompletedAt(order.getCompletedAt());
        vo.setCreateTime(order.getCreateTime());
        
        // 获取买家卖家信息
        User buyer = userMapper.selectById(order.getBuyerId());
        if (buyer != null) {
            vo.setBuyerName(buyer.getUsername());
            vo.setBuyerAvatar(buyer.getAvatar());
        }
        if (order.getSellerId() != null) {
            User seller = userMapper.selectById(order.getSellerId());
            if (seller != null) {
                vo.setSellerName(seller.getUsername());
                vo.setSellerAvatar(seller.getAvatar());
            }
        }
        
        // 获取订单明细
        List<OrderItem> orderItems = orderItemMapper.selectList(
            new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId())
        );
        vo.setItems(orderItems.stream().map(this::convertToItemVO).collect(Collectors.toList()));
        
        return vo;
    }
    
    private OrderItemVO convertToItemVO(OrderItem item) {
        OrderItemVO vo = new OrderItemVO();
        vo.setId(item.getId());
        vo.setItemId(item.getItemId());
        vo.setItemName(item.getItemName());
        vo.setItemImage(item.getItemImage());
        vo.setPrice(item.getPrice());
        vo.setQuantity(item.getQuantity());
        return vo;
    }
}
