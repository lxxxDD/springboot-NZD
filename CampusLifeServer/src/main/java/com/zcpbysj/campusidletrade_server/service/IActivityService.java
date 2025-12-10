package com.zcpbysj.campusidletrade_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcpbysj.campusidletrade_server.entity.Activity;
import com.zcpbysj.campusidletrade_server.entity.vo.activity.ActivityVO;
import com.zcpbysj.campusidletrade_server.entity.vo.common.PageVO;

import java.util.List;

/**
 * 活动服务接口
 */
public interface IActivityService extends IService<Activity> {
    
    /**
     * 获取活动列表
     */
    PageVO<ActivityVO> getActivities(String status, Integer page, Integer size, Long userId);
    
    /**
     * 获取活动详情
     */
    ActivityVO getActivityDetail(Long id, Long userId);
    
    /**
     * 报名活动
     */
    void registerActivity(Long activityId, Long userId);
    
    /**
     * 取消报名
     */
    void cancelRegistration(Long activityId, Long userId);
    
    /**
     * 获取我的活动
     */
    List<ActivityVO> getMyActivities(Long userId);
}
