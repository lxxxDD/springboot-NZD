package com.zcpbysj.campusidletrade_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcpbysj.campusidletrade_server.entity.User;
import com.zcpbysj.campusidletrade_server.entity.dto.LoginDTO;
import com.zcpbysj.campusidletrade_server.entity.dto.RegisterDTO;
import com.zcpbysj.campusidletrade_server.entity.dto.TopupDTO;
import com.zcpbysj.campusidletrade_server.entity.dto.UpdateProfileDTO;
import com.zcpbysj.campusidletrade_server.entity.vo.LoginVO;
import com.zcpbysj.campusidletrade_server.entity.vo.UserInfoVO;

import java.math.BigDecimal;

/**
 * 用户服务接口
 */
public interface IUserService extends IService<User> {
    
    /**
     * 用户注册
     */
    LoginVO register(RegisterDTO dto);
    
    /**
     * 用户登录
     */
    LoginVO login(LoginDTO dto);
    
    /**
     * 获取用户信息
     */
    UserInfoVO getProfile(Long userId);
    
    /**
     * 更新用户信息
     */
    void updateProfile(Long userId, UpdateProfileDTO dto);
    
    /**
     * 获取余额
     */
    BigDecimal getBalance(Long userId);
    
    /**
     * 充值
     */
    BigDecimal topup(Long userId, TopupDTO dto);
    
    /**
     * 根据学号查找用户
     */
    User findByStudentId(String studentId);
}
