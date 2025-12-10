package com.zcpbysj.campusidletrade_server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcpbysj.campusidletrade_server.entity.Role;
import com.zcpbysj.campusidletrade_server.mapper.RoleMapper;
import com.zcpbysj.campusidletrade_server.service.IRoleService;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {
}
