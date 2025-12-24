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
        
        // 【商品锁定】检查是否有未支付的订单占用此商品
        LambdaQueryWrapper<OrderItem> existingWrapper = new LambdaQueryWrapper<>();
        existingWrapper.eq(OrderItem::getItemId, dto.getItemId());
        List<OrderItem> existingOrderItems = orderItemMapper.selectList(existingWrapper);
        for (OrderItem oi : existingOrderItems) {
            Order existingOrder = getById(oi.getOrderId());
            if (existingOrder != null && "pending".equals(existingOrder.getStatus())) {
                throw new RuntimeException("该商品有其他用户正在下单中，请稍后再试");
            }
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
        
        // 扣除买家余额（担保交易：钱暂时冻结，确认收货后再打给卖家）
        BigDecimal newBuyerBalance = buyer.getBalance().subtract(order.getTotalAmount());
        buyer.setBalance(newBuyerBalance);
        userMapper.updateById(buyer);
        
        // 记录买家交易（备注为冻结中）
        Transaction buyerTx = new Transaction();
        buyerTx.setUserId(userId);
        buyerTx.setType("payment");
        buyerTx.setAmount(order.getTotalAmount().negate());
        buyerTx.setBalanceAfter(newBuyerBalance);
        buyerTx.setOrderId(order.getId());
        buyerTx.setDescription("购买商品（担保中）");
        transactionMapper.insert(buyerTx);
        
        // 【重要】支付后不立即给卖家打款，等确认收货后再打款
        // 更新订单状态为已支付，等待卖家发货
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
    @Transactional
    public void updateOrderStatus(Long id, Long userId, String status) {
        Order order = getById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        String currentStatus = order.getStatus();
        
        // 验证状态转换是否合理
        if ("shipping".equals(status)) {
            // 发货：只有卖家可以操作，且订单必须是已支付状态
            if (!order.getSellerId().equals(userId)) {
                throw new RuntimeException("只有卖家可以确认发货");
            }
            if (!"paid".equals(currentStatus)) {
                throw new RuntimeException("只有已支付的订单才能发货");
            }
        } else if ("completed".equals(status)) {
            // 确认收货：只有买家可以操作，且订单必须是配送中状态
            if (!order.getBuyerId().equals(userId)) {
                throw new RuntimeException("只有买家可以确认收货");
            }
            if (!"shipping".equals(currentStatus)) {
                throw new RuntimeException("只有配送中的订单才能确认收货");
            }
            
            // 【担保交易】确认收货后，将款项打给卖家
            User seller = userMapper.selectById(order.getSellerId());
            BigDecimal newSellerBalance = seller.getBalance().add(order.getTotalAmount());
            seller.setBalance(newSellerBalance);
            userMapper.updateById(seller);
            
            // 记录卖家收入
            Transaction sellerTx = new Transaction();
            sellerTx.setUserId(order.getSellerId());
            sellerTx.setType("income");
            sellerTx.setAmount(order.getTotalAmount());
            sellerTx.setBalanceAfter(newSellerBalance);
            sellerTx.setOrderId(order.getId());
            sellerTx.setDescription("商品售出（买家已确认收货）");
            transactionMapper.insert(sellerTx);
            
            order.setCompletedAt(LocalDateTime.now());
        } else {
            throw new RuntimeException("不支持的状态操作");
        }
        
        order.setStatus(status);
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
        
        String currentStatus = order.getStatus();
        
        // 待支付订单可以直接取消
        if ("pending".equals(currentStatus)) {
            order.setStatus("cancelled");
            order.setRemark(reason);
            updateById(order);
            return;
        }
        
        // 已支付但未发货的订单可以申请退款
        if ("paid".equals(currentStatus)) {
            // 退款给买家
            User buyer = userMapper.selectById(userId);
            BigDecimal newBuyerBalance = buyer.getBalance().add(order.getTotalAmount());
            buyer.setBalance(newBuyerBalance);
            userMapper.updateById(buyer);
            
            // 记录退款交易
            Transaction refundTx = new Transaction();
            refundTx.setUserId(userId);
            refundTx.setType("refund");
            refundTx.setAmount(order.getTotalAmount());
            refundTx.setBalanceAfter(newBuyerBalance);
            refundTx.setOrderId(order.getId());
            refundTx.setDescription("订单退款");
            transactionMapper.insert(refundTx);
            
            // 恢复商品状态
            List<OrderItem> orderItems = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId())
            );
            for (OrderItem oi : orderItems) {
                MarketItem item = marketItemMapper.selectById(oi.getItemId());
                if (item != null && "sold".equals(item.getStatus())) {
                    item.setStatus("active");
                    marketItemMapper.updateById(item);
                }
            }
            
            order.setStatus("refunded");
            order.setRemark(reason);
            updateById(order);
            return;
        }
        
        throw new RuntimeException("当前订单状态不支持取消/退款");
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
