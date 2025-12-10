package com.zcpbysj.campusidletrade_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcpbysj.campusidletrade_server.entity.Canteen;
import com.zcpbysj.campusidletrade_server.entity.vo.food.CanteenVO;

import java.util.List;

/**
 * 食堂服务接口
 */
public interface ICanteenService extends IService<Canteen> {

    /**
     * 获取所有食堂列表
     */
    List<CanteenVO> listAllCanteens();

    /**
     * 根据ID获取食堂详情
     */
    CanteenVO getCanteenById(Long id);

    /**
     * 获取营业中的食堂
     */
    List<CanteenVO> listOpenCanteens();

    /**
     * 更新排队人数
     */
    boolean updateQueueCount(Long id, Integer queueCount);

    /**
     * 更新座位信息
     */
    boolean updateSeats(Long id, Integer availableSeats);
}
