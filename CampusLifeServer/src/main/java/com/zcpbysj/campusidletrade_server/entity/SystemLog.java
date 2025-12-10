package com.zcpbysj.campusidletrade_server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统日志实体类
 */
@Data
@TableName("system_logs")
public class SystemLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 日志标题 */
    private String title;

    /** 日志类型: info-信息, success-成功, warning-警告, error-错误 */
    private String type;

    /** 模块名称 */
    private String module;

    /** 操作者ID */
    private Long operatorId;

    /** 操作者名称 */
    private String operatorName;

    /** IP地址 */
    private String ip;

    /** 详细内容 */
    private String content;

    /** 创建时间 */
    private LocalDateTime createTime;
}
