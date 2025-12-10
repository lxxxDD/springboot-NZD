package com.zcpbysj.campusidletrade_server.service.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcpbysj.campusidletrade_server.entity.User;

/**
 * 管理端-用户管理服务接口
 */
public interface IAdminUserService {

    /**
     * 分页获取用户列表
     */
    Page<User> getUserList(Integer page, Integer size, String keyword, Integer status);

    /**
     * 更新用户状态（启用/禁用）
     */
    boolean updateUserStatus(Long id, Integer status);

    /**
     * 获取用户详情
     */
    User getUserDetail(Long id);

    /**
     * 重置用户密码
     */
    boolean resetPassword(Long id);
}
