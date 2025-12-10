package com.zcpbysj.campusidletrade_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zcpbysj.campusidletrade_server.entity.Favorite;
import com.zcpbysj.campusidletrade_server.entity.MarketItem;
import com.zcpbysj.campusidletrade_server.entity.User;
import com.zcpbysj.campusidletrade_server.entity.dto.market.CreateMarketItemDTO;
import com.zcpbysj.campusidletrade_server.entity.dto.market.UpdateMarketItemDTO;
import com.zcpbysj.campusidletrade_server.entity.vo.market.MarketItemListVO;
import com.zcpbysj.campusidletrade_server.entity.vo.market.MarketItemVO;
import com.zcpbysj.campusidletrade_server.entity.vo.common.PageVO;
import com.zcpbysj.campusidletrade_server.mapper.FavoriteMapper;
import com.zcpbysj.campusidletrade_server.mapper.MarketItemMapper;
import com.zcpbysj.campusidletrade_server.mapper.UserMapper;
import com.zcpbysj.campusidletrade_server.service.IMarketItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MarketItemServiceImpl extends ServiceImpl<MarketItemMapper, MarketItem> implements IMarketItemService {

    private final UserMapper userMapper;
    private final FavoriteMapper favoriteMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PageVO<MarketItemListVO> getItems(Integer page, Integer size, String category, String keyword, String sort) {
        LambdaQueryWrapper<MarketItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MarketItem::getStatus, "active");
        
        if (StringUtils.hasText(category)) {
            wrapper.eq(MarketItem::getCategory, category);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(MarketItem::getTitle, keyword);
        }
        
        // 排序
        if ("price_asc".equals(sort)) {
            wrapper.orderByAsc(MarketItem::getPrice);
        } else if ("price_desc".equals(sort)) {
            wrapper.orderByDesc(MarketItem::getPrice);
        } else {
            wrapper.orderByDesc(MarketItem::getCreateTime);
        }
        
        Page<MarketItem> pageResult = page(new Page<>(page, size), wrapper);
        
        List<MarketItemListVO> list = pageResult.getRecords().stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());
        
        return PageVO.of(list, pageResult.getTotal(), page, size);
    }

    @Override
    public MarketItemVO getItemDetail(Long id, Long userId) {
        MarketItem item = getById(id);
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        
        MarketItemVO vo = convertToVO(item);
        
        // 检查是否收藏
        if (userId != null) {
            Favorite favorite = favoriteMapper.selectOne(
                new LambdaQueryWrapper<Favorite>()
                    .eq(Favorite::getUserId, userId)
                    .eq(Favorite::getItemId, id)
            );
            vo.setIsFavorite(favorite != null);
        } else {
            vo.setIsFavorite(false);
        }
        
        return vo;
    }

    @Override
    @Transactional
    public Long createItem(Long sellerId, CreateMarketItemDTO dto) {
        MarketItem item = new MarketItem();
        item.setSellerId(sellerId);
        item.setTitle(dto.getTitle());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setOriginalPrice(dto.getOriginalPrice());
        item.setCategory(dto.getCategory());
        item.setConditionLevel(dto.getConditionLevel());
        
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            try {
                item.setImages(objectMapper.writeValueAsString(dto.getImages()));
                item.setCoverImage(dto.getImages().get(0));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("图片处理失败");
            }
        }
        
        item.setStatus("active");
        item.setViewCount(0);
        item.setFavoriteCount(0);
        
        save(item);
        return item.getId();
    }

    @Override
    @Transactional
    public void updateItem(Long id, Long sellerId, UpdateMarketItemDTO dto) {
        MarketItem item = getById(id);
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!item.getSellerId().equals(sellerId)) {
            throw new RuntimeException("无权修改此商品");
        }
        
        if (dto.getTitle() != null) item.setTitle(dto.getTitle());
        if (dto.getDescription() != null) item.setDescription(dto.getDescription());
        if (dto.getPrice() != null) item.setPrice(dto.getPrice());
        if (dto.getOriginalPrice() != null) item.setOriginalPrice(dto.getOriginalPrice());
        if (dto.getCategory() != null) item.setCategory(dto.getCategory());
        if (dto.getConditionLevel() != null) item.setConditionLevel(dto.getConditionLevel());
        
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            try {
                item.setImages(objectMapper.writeValueAsString(dto.getImages()));
                item.setCoverImage(dto.getImages().get(0));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("图片处理失败");
            }
        }
        
        updateById(item);
    }

    @Override
    public void updateItemStatus(Long id, Long sellerId, String status) {
        MarketItem item = getById(id);
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!item.getSellerId().equals(sellerId)) {
            throw new RuntimeException("无权修改此商品");
        }
        
        item.setStatus(status);
        updateById(item);
    }

    @Override
    public void deleteItem(Long id, Long sellerId) {
        MarketItem item = getById(id);
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!item.getSellerId().equals(sellerId)) {
            throw new RuntimeException("无权删除此商品");
        }
        
        // 删除商品图片文件
        deleteItemImages(item);
        
        item.setStatus("deleted");
        updateById(item);
    }
    
    /**
     * 删除商品关联的图片文件
     */
    private void deleteItemImages(MarketItem item) {
        try {
            // 解析图片列表
            List<String> images = new ArrayList<>();
            if (StringUtils.hasText(item.getImages())) {
                images = objectMapper.readValue(item.getImages(), new TypeReference<List<String>>() {});
            }
            
            // 添加封面图
            if (StringUtils.hasText(item.getCoverImage())) {
                images.add(item.getCoverImage());
            }
            
            // 删除每个图片文件
            for (String imageUrl : images) {
                deleteImageFile(imageUrl);
            }
        } catch (Exception e) {
            // 图片删除失败不影响商品删除
            log.warn("删除商品图片失败: {}", e.getMessage());
        }
    }
    
    /**
     * 删除单个图片文件
     */
    private void deleteImageFile(String imageUrl) {
        if (!StringUtils.hasText(imageUrl)) {
            return;
        }
        
        try {
            // 提取文件路径（去掉 URL 前缀）
            String filePath = imageUrl;
            if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                // 从完整 URL 中提取路径部分
                int uploadIndex = imageUrl.indexOf("/upload/");
                if (uploadIndex != -1) {
                    filePath = imageUrl.substring(uploadIndex);
                }
            }
            
            // 构建完整文件路径
            java.io.File file = new java.io.File("D:/AGXNZDBiShe/lgfbishe/CampusLifeServer" + filePath);
            if (file.exists() && file.isFile()) {
                boolean deleted = file.delete();
                if (deleted) {
                    log.info("成功删除图片文件: " + file.getAbsolutePath());
                } else {
                    log.warn("删除图片文件失败: " + file.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            log.error("删除图片文件异常: " + e.getMessage());
        }
    }

    @Override
    public List<MarketItemListVO> getMyItems(Long sellerId, String status) {
        LambdaQueryWrapper<MarketItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MarketItem::getSellerId, sellerId);
        wrapper.ne(MarketItem::getStatus, "deleted");
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(MarketItem::getStatus, status);
        }
        
        wrapper.orderByDesc(MarketItem::getCreateTime);
        
        return list(wrapper).stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Boolean toggleFavorite(Long itemId, Long userId) {
        // 1. 验证商品是否存在且有效
        MarketItem item = getById(itemId);
        if (item == null || "deleted".equals(item.getStatus())) {
            throw new RuntimeException("商品不存在或已删除");
        }

        Favorite existing = favoriteMapper.selectOne(
            new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getItemId, itemId)
        );
        
        if (existing != null) {
            // 取消收藏
            favoriteMapper.deleteById(existing.getId());
            // 减少收藏数
            update(new LambdaUpdateWrapper<MarketItem>()
                .eq(MarketItem::getId, itemId)
                .setSql("favorite_count = favorite_count - 1"));
            return false;
        } else {
            // 添加收藏
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setItemId(itemId);
            favoriteMapper.insert(favorite);
            // 增加收藏数
            update(new LambdaUpdateWrapper<MarketItem>()
                .eq(MarketItem::getId, itemId)
                .setSql("favorite_count = favorite_count + 1"));
            return true;
        }
    }

    @Override
    public void incrementViewCount(Long id) {
        update(new LambdaUpdateWrapper<MarketItem>()
            .eq(MarketItem::getId, id)
            .setSql("view_count = view_count + 1"));
    }
    
    private MarketItemListVO convertToListVO(MarketItem item) {
        MarketItemListVO vo = new MarketItemListVO();
        vo.setId(item.getId());
        vo.setTitle(item.getTitle());
        vo.setPrice(item.getPrice());
        vo.setCoverImage(item.getCoverImage());
        vo.setStatus(item.getStatus());
        vo.setCreateTime(item.getCreateTime());
        vo.setViewCount(item.getViewCount());
        
        User seller = userMapper.selectById(item.getSellerId());
        if (seller != null) {
            vo.setSellerName(seller.getUsername());
        }
        
        return vo;
    }
    
    private MarketItemVO convertToVO(MarketItem item) {
        MarketItemVO vo = new MarketItemVO();
        vo.setId(item.getId());
        vo.setSellerId(item.getSellerId());
        vo.setTitle(item.getTitle());
        vo.setDescription(item.getDescription());
        vo.setPrice(item.getPrice());
        vo.setOriginalPrice(item.getOriginalPrice());
        vo.setCategory(item.getCategory());
        vo.setConditionLevel(item.getConditionLevel());
        vo.setCoverImage(item.getCoverImage());
        vo.setStatus(item.getStatus());
        vo.setViewCount(item.getViewCount());
        vo.setFavoriteCount(item.getFavoriteCount());
        vo.setCreateTime(item.getCreateTime());
        
        // 设置图片（保留原始JSON字符串，前端解析）
        vo.setImages(item.getImages());
        
        // 获取卖家信息
        User seller = userMapper.selectById(item.getSellerId());
        if (seller != null) {
            vo.setSellerName(seller.getUsername());
            vo.setSellerAvatar(seller.getAvatar());
        }
        
        return vo;
    }
}
