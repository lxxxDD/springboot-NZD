package com.zcpbysj.campusidletrade_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcpbysj.campusidletrade_server.entity.MarketItem;
import com.zcpbysj.campusidletrade_server.entity.dto.market.CreateMarketItemDTO;
import com.zcpbysj.campusidletrade_server.entity.dto.market.UpdateMarketItemDTO;
import com.zcpbysj.campusidletrade_server.entity.vo.market.MarketItemListVO;
import com.zcpbysj.campusidletrade_server.entity.vo.market.MarketItemVO;
import com.zcpbysj.campusidletrade_server.entity.vo.common.PageVO;

import java.util.List;

/**
 * 商品服务接口
 */
public interface IMarketItemService extends IService<MarketItem> {
    
    /**
     * 获取商品列表
     */
    PageVO<MarketItemListVO> getItems(Integer page, Integer size, String category, String keyword, String sort);
    
    /**
     * 获取商品详情
     */
    MarketItemVO getItemDetail(Long id, Long userId);
    
    /**
     * 发布商品
     */
    Long createItem(Long sellerId, CreateMarketItemDTO dto);
    
    /**
     * 更新商品
     */
    void updateItem(Long id, Long sellerId, UpdateMarketItemDTO dto);
    
    /**
     * 更新商品状态
     */
    void updateItemStatus(Long id, Long sellerId, String status);
    
    /**
     * 删除商品
     */
    void deleteItem(Long id, Long sellerId);
    
    /**
     * 获取我的商品
     */
    List<MarketItemListVO> getMyItems(Long sellerId, String status);
    
    /**
     * 收藏/取消收藏
     */
    Boolean toggleFavorite(Long itemId, Long userId);
    
    /**
     * 增加浏览量
     */
    void incrementViewCount(Long id);
}
