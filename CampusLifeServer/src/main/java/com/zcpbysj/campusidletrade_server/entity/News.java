package com.zcpbysj.campusidletrade_server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 校园新闻实体
 */
@Data
@TableName("news")
public class News {

    @TableId(type = IdType.AUTO)
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
     * 新闻内容
     */
    private String content;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 新闻分类：notice-通知公告, academic-学术动态, campus-校园生活, activity-活动资讯
     */
    private String category;

    /**
     * 作者/来源
     */
    private String author;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    /**
     * 状态：draft-草稿, published-已发布, archived-已归档
     */
    private String status;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
