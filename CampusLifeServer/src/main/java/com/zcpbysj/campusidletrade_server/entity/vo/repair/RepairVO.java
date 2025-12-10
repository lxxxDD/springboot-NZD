package com.zcpbysj.campusidletrade_server.entity.vo.repair;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 报修VO
 */
@Data
public class RepairVO {
    
    private Long id;
    
    /**
     * 报修单号
     */
    private String repairNo;
    
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
     * 图片列表
     */
    private List<String> images;
    
    /**
     * 状态
     */
    private String status;
    
    /**
     * 维修人员名称
     */
    private String technicianName;
    
    /**
     * 评分
     */
    private Integer rating;
    
    /**
     * 反馈
     */
    private String feedback;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 完成时间
     */
    private LocalDateTime completedAt;
}
