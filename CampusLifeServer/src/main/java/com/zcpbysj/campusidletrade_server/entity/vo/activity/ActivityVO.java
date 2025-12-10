package com.zcpbysj.campusidletrade_server.entity.vo.activity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 活动VO
 */
@Data
public class ActivityVO {
    
    private Long id;
    
    /**
     * 活动标题
     */
    private String title;
    
    /**
     * 活动描述
     */
    private String description;
    
    /**
     * 封面图
     */
    private String coverImage;
    
    /**
     * 活动地点
     */
    private String location;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 报名开始时间
     */
    private LocalDateTime registrationStartTime;

    /**
     * 报名截止时间
     */
    private LocalDateTime registrationEndTime;
    
    /**
     * 最大参与人数
     */
    private Integer maxParticipants;
    
    /**
     * 当前参与人数
     */
    private Integer currentParticipants;
    
    /**
     * 组织者名称
     */
    private String organizerName;
    
    /**
     * 状态
     */
    private String status;
    
    /**
     * 当前用户是否已报名
     */
    private Boolean isRegistered;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 服务器当前时间
     */
    private LocalDateTime serverTime;
}
