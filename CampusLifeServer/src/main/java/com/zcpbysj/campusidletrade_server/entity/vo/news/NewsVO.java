package com.zcpbysj.campusidletrade_server.entity.vo.news;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 新闻视图对象
 */
@Data
public class NewsVO {

    private Long id;

    /**
     * 新闻标题
     */
    private String title;

    /**
     * 新闻摘要
     */
    private String summary;

    /**
     * 新闻内容（详情时返回）
     */
    private String content;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 新闻分类
     */
    private String category;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 作者/来源
     */
    private String author;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
