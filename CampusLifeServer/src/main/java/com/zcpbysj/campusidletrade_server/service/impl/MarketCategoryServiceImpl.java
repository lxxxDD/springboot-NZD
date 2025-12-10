package com.zcpbysj.campusidletrade_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcpbysj.campusidletrade_server.entity.MarketCategory;
import com.zcpbysj.campusidletrade_server.mapper.MarketCategoryMapper;
import com.zcpbysj.campusidletrade_server.service.IMarketCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品分类服务实现
 */
@Service
public class MarketCategoryServiceImpl extends ServiceImpl<MarketCategoryMapper, MarketCategory> implements IMarketCategoryService {

    @Override
    public List<MarketCategory> getActiveCategories() {
        LambdaQueryWrapper<MarketCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MarketCategory::getStatus, 1)
               .orderByAsc(MarketCategory::getSortOrder);
        return list(wrapper);
    }
}
