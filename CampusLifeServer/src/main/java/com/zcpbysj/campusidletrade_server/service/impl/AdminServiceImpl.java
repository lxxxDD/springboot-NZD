package com.zcpbysj.campusidletrade_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcpbysj.campusidletrade_server.entity.Admin;
import com.zcpbysj.campusidletrade_server.mapper.AdminMapper;
import com.zcpbysj.campusidletrade_server.service.IAdminService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements IAdminService {

    @Override
    public Admin login(String username, String password) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, username);
        Admin admin = getOne(wrapper);
        
        // 直接明文比较
        if (admin != null && password.equals(admin.getPassword())) {
            // 更新最后登录时间
            admin.setLastLoginTime(LocalDateTime.now());
            updateById(admin);
            return admin;
        }
        return null;
    }
}
