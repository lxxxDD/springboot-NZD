package com.zcpbysj.campusidletrade_server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcpbysj.campusidletrade_server.entity.Technician;
import com.zcpbysj.campusidletrade_server.mapper.TechnicianMapper;
import com.zcpbysj.campusidletrade_server.service.TechnicianService;
import org.springframework.stereotype.Service;

@Service
public class TechnicianServiceImpl extends ServiceImpl<TechnicianMapper, Technician> implements TechnicianService {
}
