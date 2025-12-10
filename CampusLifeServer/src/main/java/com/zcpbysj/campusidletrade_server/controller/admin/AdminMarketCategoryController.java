package com.zcpbysj.campusidletrade_server.controller.admin;

import com.zcpbysj.campusidletrade_server.entity.MarketCategory;
import com.zcpbysj.campusidletrade_server.service.IMarketCategoryService;
import com.zcpbysj.campusidletrade_server.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品分类管理控制器
 */
@RestController
@RequestMapping("/api/admin/market/categories")
public class AdminMarketCategoryController {

    @Autowired
    private IMarketCategoryService marketCategoryService;

    /**
     * 获取所有分类列表
     */
    @GetMapping
    public Result<List<MarketCategory>> list() {
        List<MarketCategory> list = marketCategoryService.list();
        return Result.success(list);
    }

    /**
     * 获取所有启用的分类
     */
    @GetMapping("/all")
    public Result<List<MarketCategory>> allActive() {
        List<MarketCategory> list = marketCategoryService.getActiveCategories();
        return Result.success(list);
    }

    /**
     * 根据ID获取分类
     */
    @GetMapping("/{id}")
    public Result<MarketCategory> getById(@PathVariable Long id) {
        MarketCategory category = marketCategoryService.getById(id);
        return Result.success(category);
    }

    /**
     * 添加分类
     */
    @PostMapping
    public Result<?> create(@RequestBody MarketCategory category) {
        marketCategoryService.save(category);
        return Result.success("添加成功");
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody MarketCategory category) {
        category.setId(id);
        marketCategoryService.updateById(category);
        return Result.success("更新成功");
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        marketCategoryService.removeById(id);
        return Result.success("删除成功");
    }
}
