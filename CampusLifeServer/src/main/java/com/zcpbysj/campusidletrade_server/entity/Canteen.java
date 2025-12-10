package com.zcpbysj.campusidletrade_server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 食堂实体
 */
@Data
@TableName("canteen")
public class Canteen {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 食堂名称
     */
    private String name;

    /**
     * 食堂位置
     */
    private String location;

    /**
     * 食堂图片
     */
    private String image;

    /**
     * 营业开始时间
     */
    private LocalTime openTime;

    /**
     * 营业结束时间
     */
    private LocalTime closeTime;

    /**
     * 当前排队人数
     */
    private Integer queueCount;

    /**
     * 总座位数
     */
    private Integer totalSeats;

    /**
     * 剩余座位数
     */
    private Integer availableSeats;

    /**
     * 状态：open-营业中, closed-已打烊, maintenance-维护中
     */
    private String status;

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
