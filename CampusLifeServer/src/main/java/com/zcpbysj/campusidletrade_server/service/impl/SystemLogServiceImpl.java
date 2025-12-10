package com.zcpbysj.campusidletrade_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcpbysj.campusidletrade_server.entity.SystemLog;
import com.zcpbysj.campusidletrade_server.mapper.SystemLogMapper;
import com.zcpbysj.campusidletrade_server.service.ISystemLogService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统日志服务实现类
 */
@Service
public class SystemLogServiceImpl extends ServiceImpl<SystemLogMapper, SystemLog> implements ISystemLogService {

    @Override
    public List<SystemLog> getRecentLogs(int limit) {
        LambdaQueryWrapper<SystemLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SystemLog::getCreateTime);
        wrapper.last("LIMIT " + limit);
        return list(wrapper);
    }

    @Override
    public void addLog(String title, String type, String module) {
        SystemLog log = new SystemLog();
        log.setTitle(title);
        log.setType(type);
        log.setModule(module);
        log.setCreateTime(LocalDateTime.now());
        save(log);
    }
}
