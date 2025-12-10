package com.zcpbysj.campusidletrade_server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报修表
 */
@Data
@TableName("repairs")
public class Repair {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 报修单号
     */
    private String repairNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 报修地点
     */
    private String location;

    /**
     * 问题类型
     */
    private String issueType;

    /**
     * 问题描述
     */
    private String description;

    /**
     * 图片列表 (JSON格式)
     */
    private String images;

    /**
     * 状态: received/assigned/in_progress/completed
     */
    private String status;

    /**
     * 维修人员ID
     */
    private Long technicianId;

    /**
     * 维修人员姓名 (非数据库字段)
     */
    @TableField(exist = false)
    private String technicianName;

    /**
     * 评分 1-5
     */
    private Integer rating;

    /**
     * 反馈
     */
    private String feedback;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 完成时间
     */
    private LocalDateTime completedAt;
}
