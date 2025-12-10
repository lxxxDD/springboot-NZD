package com.zcpbysj.campusidletrade_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcpbysj.campusidletrade_server.entity.Repair;
import com.zcpbysj.campusidletrade_server.entity.dto.repair.CreateRepairDTO;
import com.zcpbysj.campusidletrade_server.entity.dto.repair.RateRepairDTO;
import com.zcpbysj.campusidletrade_server.entity.vo.repair.RepairVO;

import java.util.List;

/**
 * 报修服务接口
 */
public interface IRepairService extends IService<Repair> {
    
    /**
     * 获取报修列表
     */
    List<RepairVO> getRepairs(Long userId, String status);
    
    /**
     * 获取报修详情
     */
    RepairVO getRepairDetail(Long id, Long userId);
    
    /**
     * 提交报修
     */
    Long createRepair(Long userId, CreateRepairDTO dto);
    
    /**
     * 评价报修
     */
    void rateRepair(Long id, Long userId, RateRepairDTO dto);
}
