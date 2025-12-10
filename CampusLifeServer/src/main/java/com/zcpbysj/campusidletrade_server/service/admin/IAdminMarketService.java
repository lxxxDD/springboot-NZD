package com.zcpbysj.campusidletrade_server.service.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcpbysj.campusidletrade_server.entity.vo.market.MarketItemVO;

/**
 * 管理端-二手市场服务接口
 */
public interface IAdminMarketService {

    /**
     * 分页获取商品列表
     */
    Page<MarketItemVO> getItemList(Integer page, Integer size, String status, String keyword, String category);

    /**
     * 更新商品状态（审核通过/拒绝/下架）
     */
    boolean updateItemStatus(Long id, String status, String reason);

    /**
     * 获取商品详情（含卖家信息）
     */
    MarketItemVO getItemDetail(Long id);

    /**
     * 删除商品
     */
    boolean deleteItem(Long id);
}
