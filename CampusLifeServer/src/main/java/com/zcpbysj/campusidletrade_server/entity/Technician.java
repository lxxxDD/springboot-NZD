package com.zcpbysj.campusidletrade_server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 维修人员表
 */
@Data
@TableName("technicians")
public class Technician {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 专长类型: Electric/Water/Wifi/Furniture/AC/Other
     */
    private String specialty;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 状态: active/inactive
     */
    private String status;

    /**
     * 累计评分
     */
    private Double avgRating;

    /**
     * 完成订单数
     */
    private Integer completedCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
