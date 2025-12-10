package com.zcpbysj.campusidletrade_server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实名认证实体
 */
@Data
@TableName("user_verifications")
public class UserVerification {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 身份证正面照片
     */
    private String idCardFront;

    /**
     * 身份证反面照片
     */
    private String idCardBack;

    /**
     * 学生证照片
     */
    private String studentCard;

    /**
     * 状态: 0待审核 1已通过 2已拒绝
     */
    private Integer status;

    /**
     * 审核备注
     */
    private String remark;

    /**
     * 审核人ID
     */
    private Long reviewerId;

    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
