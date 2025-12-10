package com.zcpbysj.campusidletrade_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcpbysj.campusidletrade_server.entity.Admin;

public interface IAdminService extends IService<Admin> {
    Admin login(String username, String password);
}
