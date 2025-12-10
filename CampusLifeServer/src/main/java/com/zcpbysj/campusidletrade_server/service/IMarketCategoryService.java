package com.zcpbysj.campusidletrade_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcpbysj.campusidletrade_server.entity.MarketCategory;

import java.util.List;

/**
 * 商品分类服务接口
 */
public interface IMarketCategoryService extends IService<MarketCategory> {
    
    /**
     * 获取所有启用的分类
     */
    List<MarketCategory> getActiveCategories();
}
