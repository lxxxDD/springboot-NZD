package com.zcpbysj.campusidletrade_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcpbysj.campusidletrade_server.entity.User;
import com.zcpbysj.campusidletrade_server.entity.dto.user.LoginDTO;
import com.zcpbysj.campusidletrade_server.entity.dto.user.RegisterDTO;
import com.zcpbysj.campusidletrade_server.entity.dto.user.TopupDTO;
import com.zcpbysj.campusidletrade_server.entity.dto.user.UpdateProfileDTO;
import com.zcpbysj.campusidletrade_server.entity.vo.user.LoginVO;
import com.zcpbysj.campusidletrade_server.entity.vo.user.UserInfoVO;

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
