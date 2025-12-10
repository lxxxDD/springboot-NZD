package com.zcpbysj.campusidletrade_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcpbysj.campusidletrade_server.entity.Transaction;
import com.zcpbysj.campusidletrade_server.entity.User;
import com.zcpbysj.campusidletrade_server.entity.dto.user.LoginDTO;
import com.zcpbysj.campusidletrade_server.entity.dto.user.RegisterDTO;
import com.zcpbysj.campusidletrade_server.entity.dto.user.TopupDTO;
import com.zcpbysj.campusidletrade_server.entity.dto.user.UpdateProfileDTO;
import com.zcpbysj.campusidletrade_server.entity.vo.user.LoginVO;
import com.zcpbysj.campusidletrade_server.entity.vo.user.UserInfoVO;
import com.zcpbysj.campusidletrade_server.mapper.TransactionMapper;
import com.zcpbysj.campusidletrade_server.mapper.UserMapper;
import com.zcpbysj.campusidletrade_server.service.IUserService;
import com.zcpbysj.campusidletrade_server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 用户服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final TransactionMapper transactionMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public LoginVO register(RegisterDTO dto) {
        // 检查学号是否已存在
        if (findByStudentId(dto.getStudentId()) != null) {
            throw new RuntimeException("学号已被注册");
        }
        
        // 检查邮箱是否已存在
        if (dto.getEmail() != null) {
            User existEmail = getOne(new LambdaQueryWrapper<User>().eq(User::getEmail, dto.getEmail()));
            if (existEmail != null) {
                throw new RuntimeException("邮箱已被注册");
            }
        }
        
        // 创建用户
        User user = new User();
        user.setStudentId(dto.getStudentId());
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setBalance(BigDecimal.ZERO);
        user.setPoints(0);
        user.setStatus(1);
        save(user);
        
        // 注册后自动分配随机默认头像
        int avatarIndex = (int)(user.getId() % 13) + 1;
        user.setAvatar("/avatar/" + avatarIndex + ".png");
        updateById(user);
        
        // 生成token并返回
        String token = jwtUtil.generateToken(user.getId(), user.getStudentId());
        
        LoginVO vo = new LoginVO();
        vo.setUserId(user.getId());
        vo.setUserInfo(convertToUserInfoVO(user));
        vo.setToken(token);
        return vo;
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = findByStudentId(dto.getStudentId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getStudentId());
        
        LoginVO vo = new LoginVO();
        vo.setUserId(user.getId());
        vo.setUserInfo(convertToUserInfoVO(user));
        vo.setToken(token);
        return vo;
    }

    @Override
    public UserInfoVO getProfile(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return convertToUserInfoVO(user);
    }

    @Override
    public void updateProfile(Long userId, UpdateProfileDTO dto) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (dto.getUsername() != null) {
            user.setUsername(dto.getUsername());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        
        updateById(user);
    }

    @Override
    public BigDecimal getBalance(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return user.getBalance();
    }

    @Override
    @Transactional
    public BigDecimal topup(Long userId, TopupDTO dto) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("充值金额必须大于0");
        }
        
        BigDecimal newBalance = user.getBalance().add(dto.getAmount());
        user.setBalance(newBalance);
        updateById(user);
        
        // 记录交易
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setType("topup");
        transaction.setAmount(dto.getAmount());
        transaction.setBalanceAfter(newBalance);
        transaction.setDescription("钱包充值");
        transactionMapper.insert(transaction);
        
        return newBalance;
    }

    @Override
    public User findByStudentId(String studentId) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getStudentId, studentId));
    }
    
    private UserInfoVO convertToUserInfoVO(User user) {
        UserInfoVO vo = new UserInfoVO();
        vo.setId(user.getId());
        vo.setStudentId(user.getStudentId());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setBalance(user.getBalance());
        vo.setPoints(user.getPoints());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }
}
