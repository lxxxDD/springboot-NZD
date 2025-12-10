package com.zcpbysj.campusidletrade_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcpbysj.campusidletrade_server.entity.SystemLog;

import java.util.List;

/**
 * 系统日志服务接口
 */
public interface ISystemLogService extends IService<SystemLog> {

    /**
     * 获取最近的日志
     */
    List<SystemLog> getRecentLogs(int limit);

    /**
     * 添加日志
     */
    void addLog(String title, String type, String module);
}
