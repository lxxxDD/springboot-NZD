package com.zcpbysj.campusidletrade_server.controller;

import com.zcpbysj.campusidletrade_server.common.Result;
import com.zcpbysj.campusidletrade_server.entity.dto.market.CreateMarketItemDTO;
import com.zcpbysj.campusidletrade_server.entity.dto.market.UpdateItemStatusDTO;
import com.zcpbysj.campusidletrade_server.entity.dto.market.UpdateMarketItemDTO;
import com.zcpbysj.campusidletrade_server.entity.vo.market.MarketItemListVO;
import com.zcpbysj.campusidletrade_server.entity.vo.market.MarketItemVO;
import com.zcpbysj.campusidletrade_server.entity.vo.common.PageVO;
import com.zcpbysj.campusidletrade_server.entity.MarketCategory;
import com.zcpbysj.campusidletrade_server.service.IMarketCategoryService;
import com.zcpbysj.campusidletrade_server.service.IMarketItemService;
import com.zcpbysj.campusidletrade_server.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品接口
 */
@Tag(name = "商品模块")
@RestController
@RequestMapping("/api/market")
@RequiredArgsConstructor
public class MarketController {

    private final IMarketItemService marketItemService;
    private final IMarketCategoryService marketCategoryService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "获取商品列表")
    @GetMapping("/items")
    public Result<PageVO<MarketItemListVO>> getItems(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sort) {
        PageVO<MarketItemListVO> vo = marketItemService.getItems(page, size, category, keyword, sort);
        return Result.success(vo);
    }

    @Operation(summary = "获取商品详情")
    @GetMapping("/items/{id}")
    public Result<MarketItemVO> getItemDetail(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token) {
        Long userId = null;
        if (token != null && !token.isEmpty()) {
            try {
                userId = jwtUtil.getUserIdFromToken(token);
            } catch (Exception ignored) {}
        }
        
        // 增加浏览量
        marketItemService.incrementViewCount(id);
        
        MarketItemVO vo = marketItemService.getItemDetail(id, userId);
        return Result.success(vo);
    }

    @Operation(summary = "发布商品")
    @PostMapping("/items")
    public Result<Map<String, Long>> createItem(
            @RequestHeader("Authorization") String token,
            @RequestBody CreateMarketItemDTO dto) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        Long itemId = marketItemService.createItem(userId, dto);
        Map<String, Long> data = new HashMap<>();
        data.put("itemId", itemId);
        return Result.success(data);
    }

    @Operation(summary = "更新商品")
    @PutMapping("/items/{id}")
    public Result<Void> updateItem(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestBody UpdateMarketItemDTO dto) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        marketItemService.updateItem(id, userId, dto);
        return Result.success();
    }

    @Operation(summary = "更新商品状态")
    @PutMapping("/items/{id}/status")
    public Result<Void> updateItemStatus(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestBody UpdateItemStatusDTO dto) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        marketItemService.updateItemStatus(id, userId, dto.getStatus());
        return Result.success();
    }

    @Operation(summary = "删除商品")
    @DeleteMapping("/items/{id}")
    public Result<Void> deleteItem(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        marketItemService.deleteItem(id, userId);
        return Result.success();
    }

    @Operation(summary = "获取我的商品")
    @GetMapping("/my-items")
    public Result<List<MarketItemListVO>> getMyItems(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String status) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        List<MarketItemListVO> list = marketItemService.getMyItems(userId, status);
        return Result.success(list);
    }

    @Operation(summary = "收藏/取消收藏")
    @PostMapping("/items/{id}/favorite")
    public Result<Map<String, Boolean>> toggleFavorite(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtUtil.getUserIdFromToken(token);
        Boolean isFavorite = marketItemService.toggleFavorite(id, userId);
        Map<String, Boolean> data = new HashMap<>();
        data.put("isFavorite", isFavorite);
        return Result.success(data);
    }

    @Operation(summary = "获取商品分类列表")
    @GetMapping("/categories")
    public Result<List<MarketCategory>> getCategories() {
        List<MarketCategory> categories = marketCategoryService.getActiveCategories();
        return Result.success(categories);
    }

    @Operation(summary = "获取所有分类(管理端)")
    @GetMapping("/categories/all")
    public Result<List<MarketCategory>> getAllCategories() {
        List<MarketCategory> categories = marketCategoryService.list();
        return Result.success(categories);
    }

    @Operation(summary = "创建分类")
    @PostMapping("/categories")
    public Result<MarketCategory> createCategory(@RequestBody MarketCategory category) {
        marketCategoryService.save(category);
        return Result.success(category);
    }

    @Operation(summary = "更新分类")
    @PutMapping("/categories/{id}")
    public Result<Void> updateCategory(@PathVariable Long id, @RequestBody MarketCategory category) {
        category.setId(id);
        marketCategoryService.updateById(category);
        return Result.success();
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/categories/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        marketCategoryService.removeById(id);
        return Result.success();
    }
}
