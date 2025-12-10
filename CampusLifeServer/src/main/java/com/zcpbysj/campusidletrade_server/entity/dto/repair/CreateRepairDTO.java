package com.zcpbysj.campusidletrade_server.entity.dto.repair;

import lombok.Data;

import java.util.List;

/**
 * 提交报修请求DTO
 */
@Data
public class CreateRepairDTO {
    
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
}
