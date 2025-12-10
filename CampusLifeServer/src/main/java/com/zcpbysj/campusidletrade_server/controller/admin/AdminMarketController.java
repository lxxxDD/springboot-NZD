package com.zcpbysj.campusidletrade_server.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcpbysj.campusidletrade_server.common.Result;
import com.zcpbysj.campusidletrade_server.entity.MarketItem;
import com.zcpbysj.campusidletrade_server.entity.User;
import com.zcpbysj.campusidletrade_server.entity.dto.admin.StatusDTO;
import com.zcpbysj.campusidletrade_server.entity.vo.market.MarketItemVO;
import com.zcpbysj.campusidletrade_server.service.IMarketItemService;
import com.zcpbysj.campusidletrade_server.service.ISystemLogService;
import com.zcpbysj.campusidletrade_server.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "管理端-二手市场")
@RestController
@RequestMapping("/api/admin/market")
@RequiredArgsConstructor
public class AdminMarketController {

    private final IMarketItemService marketItemService;
    private final ISystemLogService systemLogService;
    private final IUserService userService;

    @Operation(summary = "获取商品列表")
    @GetMapping("/items")
    public Result<Page<MarketItemVO>> getItemList(@RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "10") Integer size,
                                                @RequestParam(required = false) String status,
                                                @RequestParam(required = false) String keyword,
                                                @RequestParam(required = false) String category) {
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
            // 获取卖家信息
            User seller = userService.getById(item.getSellerId());
            if (seller != null) {
                vo.setSellerName(seller.getUsername());
                vo.setSellerAvatar(seller.getAvatar());
            }
            return vo;
        }).collect(Collectors.toList());
        
        Page<MarketItemVO> voPage = new Page<>(page, size, resultPage.getTotal());
        voPage.setRecords(voList);
        return Result.success(voPage);
    }

    @Operation(summary = "更新商品状态")
    @PostMapping("/items/{id}/status")
    public Result<?> updateItemStatus(@PathVariable Long id, @RequestBody StatusDTO dto) {
        String status = dto.getStatus();
        String reason = dto.getReason();
        MarketItem item = marketItemService.getById(id);
        if (item == null) {
            return Result.fail("商品不存在");
        }
        item.setStatus(status);
        if ("rejected".equals(status) && reason != null) {
            item.setRejectReason(reason); // 保存拒绝原因
        }
        marketItemService.updateById(item);
        String logMsg = "商品状态变更为: " + status;
        if (reason != null) logMsg += " (原因: " + reason + ")";
        systemLogService.addLog(logMsg, "info", "二手市场");
        return Result.success();
    }

    @Operation(summary = "获取商品详情")
    @GetMapping("/items/{id}")
    public Result<MarketItemVO> getItemById(@PathVariable Long id) {
        MarketItem item = marketItemService.getById(id);
        if (item == null) {
            return Result.fail("商品不存在");
        }
        
        // 转换为VO并填充卖家信息
        MarketItemVO vo = new MarketItemVO();
        BeanUtils.copyProperties(item, vo);
        User seller = userService.getById(item.getSellerId());
        if (seller != null) {
            vo.setSellerName(seller.getUsername());
            vo.setSellerAvatar(seller.getAvatar());
        }
        
        return Result.success(vo);
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/items/{id}")
    public Result<?> deleteItem(@PathVariable Long id) {
        marketItemService.removeById(id);
        systemLogService.addLog("删除商品 #" + id, "warning", "二手市场");
        return Result.success("删除成功");
    }
}
