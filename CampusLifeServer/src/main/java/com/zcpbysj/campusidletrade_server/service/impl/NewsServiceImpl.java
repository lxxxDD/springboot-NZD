package com.zcpbysj.campusidletrade_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcpbysj.campusidletrade_server.entity.News;
import com.zcpbysj.campusidletrade_server.entity.vo.news.NewsVO;
import com.zcpbysj.campusidletrade_server.entity.vo.common.PageVO;
import com.zcpbysj.campusidletrade_server.mapper.NewsMapper;
import com.zcpbysj.campusidletrade_server.service.INewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 新闻服务实现
 */
@Service
@RequiredArgsConstructor
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements INewsService {

    private final NewsMapper newsMapper;

    // 分类名称映射
    private static final Map<String, String> CATEGORY_NAME_MAP = new HashMap<>();
    static {
        CATEGORY_NAME_MAP.put("notice", "通知公告");
        CATEGORY_NAME_MAP.put("academic", "学术动态");
        CATEGORY_NAME_MAP.put("campus", "校园生活");
        CATEGORY_NAME_MAP.put("activity", "活动资讯");
    }

    @Override
    public PageVO<NewsVO> getNewsList(String category, Integer page, Integer size) {
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<>();
        
        // 只查询已发布的新闻
        wrapper.eq(News::getStatus, "published");
        
        // 分类筛选
        if (category != null && !category.isEmpty()) {
            wrapper.eq(News::getCategory, category);
        }
        
        // 按发布时间倒序
        wrapper.orderByDesc(News::getPublishTime);
        
        // 分页查询
        Page<News> pageResult = newsMapper.selectPage(new Page<>(page, size), wrapper);
        
        // 转换为 VO
        List<NewsVO> voList = pageResult.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        PageVO<NewsVO> pageVO = new PageVO<>();
        pageVO.setList(voList);
        pageVO.setTotal(pageResult.getTotal());
        pageVO.setHasMore(pageResult.getCurrent() < pageResult.getPages());
        
        return pageVO;
    }

    @Override
    public NewsVO getNewsDetail(Long id) {
        News news = newsMapper.selectById(id);
        if (news == null) {
            throw new RuntimeException("新闻不存在");
        }
        
        // 增加浏览次数
        news.setViewCount(news.getViewCount() == null ? 1 : news.getViewCount() + 1);
        newsMapper.updateById(news);
        
        return convertToVO(news);
    }

    private NewsVO convertToVO(News news) {
        NewsVO vo = new NewsVO();
        BeanUtils.copyProperties(news, vo);
        vo.setCategoryName(CATEGORY_NAME_MAP.getOrDefault(news.getCategory(), "其他"));
        return vo;
    }
}
