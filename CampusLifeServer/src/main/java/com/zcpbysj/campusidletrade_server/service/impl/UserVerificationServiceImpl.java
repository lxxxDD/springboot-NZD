package com.zcpbysj.campusidletrade_server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcpbysj.campusidletrade_server.entity.UserVerification;
import com.zcpbysj.campusidletrade_server.mapper.UserVerificationMapper;
import com.zcpbysj.campusidletrade_server.service.IUserVerificationService;
import org.springframework.stereotype.Service;

@Service
public class UserVerificationServiceImpl extends ServiceImpl<UserVerificationMapper, UserVerification> implements IUserVerificationService {
}
