package com.zcpbysj.campusidletrade_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcpbysj.campusidletrade_server.entity.Canteen;
import com.zcpbysj.campusidletrade_server.entity.vo.food.CanteenVO;
import com.zcpbysj.campusidletrade_server.mapper.CanteenMapper;
import com.zcpbysj.campusidletrade_server.service.ICanteenService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 食堂服务实现类
 */
@Service
public class CanteenServiceImpl extends ServiceImpl<CanteenMapper, Canteen> implements ICanteenService {

    @Override
    public List<CanteenVO> listAllCanteens() {
        List<Canteen> canteens = list();
        return canteens.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public CanteenVO getCanteenById(Long id) {
        Canteen canteen = getById(id);
        if (canteen == null) {
            return null;
        }
        return convertToVO(canteen);
    }

    @Override
    public List<CanteenVO> listOpenCanteens() {
        LambdaQueryWrapper<Canteen> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Canteen::getStatus, "open");
        List<Canteen> canteens = list(wrapper);
        return canteens.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean updateQueueCount(Long id, Integer queueCount) {
        Canteen canteen = getById(id);
        if (canteen == null) {
            return false;
        }
        canteen.setQueueCount(queueCount);
        return updateById(canteen);
    }

    @Override
    public boolean updateSeats(Long id, Integer availableSeats) {
        Canteen canteen = getById(id);
        if (canteen == null) {
            return false;
        }
        canteen.setAvailableSeats(availableSeats);
        return updateById(canteen);
    }

    /**
     * 将实体转换为VO
     */
    private CanteenVO convertToVO(Canteen canteen) {
        CanteenVO vo = new CanteenVO();
        BeanUtils.copyProperties(canteen, vo);
        // 计算预计等待时间（每人约2分钟）
        if (canteen.getQueueCount() != null) {
            vo.setEstimatedWaitMinutes(canteen.getQueueCount() * 2);
        } else {
            vo.setEstimatedWaitMinutes(0);
        }
        return vo;
    }
}
