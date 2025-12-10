package com.zcpbysj.campusidletrade_server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 校园知识库表
 */
@Data
@TableName("campus_knowledge")
public class CampusKnowledge {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分类: library/canteen/repair/activity/general
     */
    private String category;

    /**
     * 问题
     */
    private String question;

    /**
     * 答案
     */
    private String answer;

    /**
     * 关键词(逗号分隔)
     */
    private String keywords;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 状态: 1启用 0禁用
     */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
