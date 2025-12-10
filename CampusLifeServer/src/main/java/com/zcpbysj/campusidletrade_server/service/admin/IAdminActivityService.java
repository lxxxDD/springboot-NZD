package com.zcpbysj.campusidletrade_server.service.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcpbysj.campusidletrade_server.entity.Activity;

/**
 * 管理端-活动管理服务接口
 */
public interface IAdminActivityService {

    /**
     * 分页获取活动列表
     */
    Page<Activity> getActivityList(Integer page, Integer size, String status, String title);

    /**
     * 更新活动状态（审核通过/拒绝）
     */
    boolean updateActivityStatus(Long id, String status);

    /**
     * 获取活动详情
     */
    Activity getActivityDetail(Long id);

    /**
     * 保存活动（新增/更新）
     */
    boolean saveActivity(Activity activity);

    /**
     * 删除活动
     */
    boolean deleteActivity(Long id);
}
