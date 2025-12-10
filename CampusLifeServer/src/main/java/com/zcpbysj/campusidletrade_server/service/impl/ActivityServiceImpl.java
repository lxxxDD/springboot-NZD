package com.zcpbysj.campusidletrade_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcpbysj.campusidletrade_server.entity.Activity;
import com.zcpbysj.campusidletrade_server.entity.ActivityRegistration;
import com.zcpbysj.campusidletrade_server.entity.User;
import com.zcpbysj.campusidletrade_server.entity.vo.activity.ActivityVO;
import com.zcpbysj.campusidletrade_server.entity.vo.common.PageVO;
import com.zcpbysj.campusidletrade_server.mapper.ActivityMapper;
import com.zcpbysj.campusidletrade_server.mapper.ActivityRegistrationMapper;
import com.zcpbysj.campusidletrade_server.mapper.UserMapper;
import com.zcpbysj.campusidletrade_server.service.IActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 活动服务实现类
 */
@Service
@RequiredArgsConstructor
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements IActivityService {

    private final ActivityRegistrationMapper registrationMapper;
    private final UserMapper userMapper;

    @Override
    public PageVO<ActivityVO> getActivities(String status, Integer page, Integer size, Long userId) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(Activity::getStatus, status);
        }
        
        wrapper.orderByDesc(Activity::getStartTime);
        
        Page<Activity> pageResult = page(new Page<>(page, size), wrapper);
        
        List<ActivityVO> list = pageResult.getRecords().stream()
                .map(activity -> convertToVO(activity, userId))
                .collect(Collectors.toList());
        
        return PageVO.of(list, pageResult.getTotal(), page, size);
    }

    @Override
    public ActivityVO getActivityDetail(Long id, Long userId) {
        Activity activity = getById(id);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        return convertToVO(activity, userId);
    }

    @Override
    @Transactional
    public void registerActivity(Long activityId, Long userId) {
        Activity activity = getById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        if (!"upcoming".equals(activity.getStatus()) && !"ongoing".equals(activity.getStatus())) {
            throw new RuntimeException("活动已结束或已取消");
        }
        if (activity.getMaxParticipants() != null && 
            activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            throw new RuntimeException("活动名额已满");
        }
        
        // 检查是否已报名
        ActivityRegistration existing = registrationMapper.selectOne(
            new LambdaQueryWrapper<ActivityRegistration>()
                .eq(ActivityRegistration::getActivityId, activityId)
                .eq(ActivityRegistration::getUserId, userId)
                .eq(ActivityRegistration::getStatus, "registered")
        );
        if (existing != null) {
            throw new com.zcpbysj.campusidletrade_server.common.ServiceException(2001, "已报名此活动");
        }
        
        // 创建报名记录
        ActivityRegistration registration = new ActivityRegistration();
        registration.setActivityId(activityId);
        registration.setUserId(userId);
        registration.setStatus("registered");
        registrationMapper.insert(registration);
        
        // 更新参与人数
        update(new LambdaUpdateWrapper<Activity>()
            .eq(Activity::getId, activityId)
            .setSql("current_participants = current_participants + 1"));
    }

    @Override
    @Transactional
    public void cancelRegistration(Long activityId, Long userId) {
        ActivityRegistration registration = registrationMapper.selectOne(
            new LambdaQueryWrapper<ActivityRegistration>()
                .eq(ActivityRegistration::getActivityId, activityId)
                .eq(ActivityRegistration::getUserId, userId)
                .eq(ActivityRegistration::getStatus, "registered")
        );
        
        if (registration == null) {
            throw new RuntimeException("未报名此活动");
        }
        
        registration.setStatus("cancelled");
        registrationMapper.updateById(registration);
        
        // 更新参与人数
        update(new LambdaUpdateWrapper<Activity>()
            .eq(Activity::getId, activityId)
            .setSql("current_participants = current_participants - 1"));
    }

    @Override
    public List<ActivityVO> getMyActivities(Long userId) {
        // 查询用户报名的活动
        List<ActivityRegistration> registrations = registrationMapper.selectList(
            new LambdaQueryWrapper<ActivityRegistration>()
                .eq(ActivityRegistration::getUserId, userId)
                .eq(ActivityRegistration::getStatus, "registered")
        );
        
        List<Long> activityIds = registrations.stream()
                .map(ActivityRegistration::getActivityId)
                .collect(Collectors.toList());
        
        if (activityIds.isEmpty()) {
            return List.of();
        }
        
        return listByIds(activityIds).stream()
                .map(activity -> convertToVO(activity, userId))
                .collect(Collectors.toList());
    }
    
    private ActivityVO convertToVO(Activity activity, Long userId) {
        ActivityVO vo = new ActivityVO();
        vo.setId(activity.getId());
        vo.setTitle(activity.getTitle());
        vo.setDescription(activity.getDescription());
        vo.setCoverImage(activity.getCoverImage());
        vo.setLocation(activity.getLocation());
        vo.setStartTime(activity.getStartTime());
        vo.setEndTime(activity.getEndTime());
        vo.setRegistrationStartTime(activity.getRegistrationStartTime());
        vo.setRegistrationEndTime(activity.getRegistrationEndTime());
        vo.setServerTime(LocalDateTime.now());
        vo.setMaxParticipants(activity.getMaxParticipants());
        vo.setCurrentParticipants(activity.getCurrentParticipants());
        vo.setStatus(activity.getStatus());
        vo.setCreateTime(activity.getCreateTime());
        
        // 获取组织者信息
        if (activity.getOrganizerId() != null) {
            User organizer = userMapper.selectById(activity.getOrganizerId());
            if (organizer != null) {
                vo.setOrganizerName(organizer.getUsername());
            }
        }
        
        // 检查是否已报名
        if (userId != null) {
            ActivityRegistration registration = registrationMapper.selectOne(
                new LambdaQueryWrapper<ActivityRegistration>()
                    .eq(ActivityRegistration::getActivityId, activity.getId())
                    .eq(ActivityRegistration::getUserId, userId)
                    .eq(ActivityRegistration::getStatus, "registered")
            );
            vo.setIsRegistered(registration != null);
        } else {
            vo.setIsRegistered(false);
        }
        
        return vo;
    }
}
