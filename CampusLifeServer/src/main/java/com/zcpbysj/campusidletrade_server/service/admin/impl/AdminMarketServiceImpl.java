package com.zcpbysj.campusidletrade_server.service.admin.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcpbysj.campusidletrade_server.entity.MarketItem;
import com.zcpbysj.campusidletrade_server.entity.User;
import com.zcpbysj.campusidletrade_server.entity.vo.market.MarketItemVO;
import com.zcpbysj.campusidletrade_server.service.IMarketItemService;
import com.zcpbysj.campusidletrade_server.service.ISystemLogService;
import com.zcpbysj.campusidletrade_server.service.IUserService;
import com.zcpbysj.campusidletrade_server.service.admin.IAdminMarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理端-二手市场服务实现
 */
@Service
@RequiredArgsConstructor
public class AdminMarketServiceImpl implements IAdminMarketService {

    private final IMarketItemService marketItemService;
    private final IUserService userService;
    private final ISystemLogService systemLogService;

    @Override
    public Page<MarketItemVO> getItemList(Integer page, Integer size, String status, String keyword, String category) {
        Page<MarketItem> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<MarketItem> wrapper = new LambdaQueryWrapper<>();
        
        if (status != null && !status.isEmpty()) {
            wrapper.eq(MarketItem::getStatus, status);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(MarketItem::getTitle, keyword);
        }
        if (category != null && !category.isEmpty()) {
            wrapper.eq(MarketItem::getCategory, category);
        }
        wrapper.orderByDesc(MarketItem::getCreateTime);
        
        Page<MarketItem> resultPage = marketItemService.page(pageParam, wrapper);
        
        // 转换为VO并填充卖家信息
        List<MarketItemVO> voList = resultPage.getRecords().stream().map(item -> {
            MarketItemVO vo = new MarketItemVO();
            BeanUtils.copyProperties(item, vo);
            User seller = userService.getById(item.getSellerId());
            if (seller != null) {
                vo.setSellerName(seller.getUsername());
                vo.setSellerAvatar(seller.getAvatar());
            }
            return vo;
        }).collect(Collectors.toList());
        
        Page<MarketItemVO> voPage = new Page<>(page, size, resultPage.getTotal());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public boolean updateItemStatus(Long id, String status, String reason) {
        MarketItem item = marketItemService.getById(id);
        if (item == null) {
            return false;
        }
        item.setStatus(status);
        if ("rejected".equals(status) && reason != null) {
            item.setRejectReason(reason);
        }
        boolean success = marketItemService.updateById(item);
        if (success) {
            String logMsg = "商品状态变更为: " + status;
            systemLogService.addLog(logMsg, "info", "二手市场");
        }
        return success;
    }

    @Override
    public MarketItemVO getItemDetail(Long id) {
        MarketItem item = marketItemService.getById(id);
        if (item == null) {
            return null;
        }
        MarketItemVO vo = new MarketItemVO();
        BeanUtils.copyProperties(item, vo);
        User seller = userService.getById(item.getSellerId());
        if (seller != null) {
            vo.setSellerName(seller.getUsername());
            vo.setSellerAvatar(seller.getAvatar());
        }
        return vo;
    }

    @Override
    public boolean deleteItem(Long id) {
        boolean success = marketItemService.removeById(id);
        if (success) {
            systemLogService.addLog("删除商品 #" + id, "warning", "二手市场");
        }
        return success;
    }
}
