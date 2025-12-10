package com.zcpbysj.campusidletrade_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcpbysj.campusidletrade_server.entity.News;
import com.zcpbysj.campusidletrade_server.entity.vo.news.NewsVO;
import com.zcpbysj.campusidletrade_server.entity.vo.common.PageVO;

/**
 * 新闻服务接口
 */
public interface INewsService extends IService<News> {

    /**
     * 获取新闻列表
     */
    PageVO<NewsVO> getNewsList(String category, Integer page, Integer size);

    /**
     * 获取新闻详情
     */
    NewsVO getNewsDetail(Long id);
}
