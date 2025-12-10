package com.zcpbysj.campusidletrade_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zcpbysj.campusidletrade_server.entity.Repair;
import com.zcpbysj.campusidletrade_server.entity.Technician;
import com.zcpbysj.campusidletrade_server.entity.User;
import com.zcpbysj.campusidletrade_server.entity.dto.repair.CreateRepairDTO;
import com.zcpbysj.campusidletrade_server.entity.dto.repair.RateRepairDTO;
import com.zcpbysj.campusidletrade_server.entity.vo.repair.RepairVO;
import com.zcpbysj.campusidletrade_server.mapper.RepairMapper;
import com.zcpbysj.campusidletrade_server.mapper.UserMapper;
import com.zcpbysj.campusidletrade_server.service.IRepairService;
import com.zcpbysj.campusidletrade_server.service.TechnicianService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 报修服务实现类
 */
@Service
@RequiredArgsConstructor
public class RepairServiceImpl extends ServiceImpl<RepairMapper, Repair> implements IRepairService {

    private final UserMapper userMapper;
    private final TechnicianService technicianService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<RepairVO> getRepairs(Long userId, String status) {
        // 先查询所有记录，调试用
        List<Repair> allRepairs = list();
        System.out.println("=== 数据库所有报修记录 ===");
        for (Repair r : allRepairs) {
            System.out.println("  id=" + r.getId() + ", user_id=" + r.getUserId() + ", user_id类型=" + (r.getUserId() != null ? r.getUserId().getClass().getName() : "null"));
        }
        
        LambdaQueryWrapper<Repair> wrapper = new LambdaQueryWrapper<>();
        // 按用户ID过滤
        if (userId != null) {
            wrapper.eq(Repair::getUserId, userId);
        }
        
        if (StringUtils.hasText(status)) {
            wrapper.eq(Repair::getStatus, status);
        }
        
        wrapper.orderByDesc(Repair::getCreateTime);
        
        List<Repair> repairs = list(wrapper);
        System.out.println("=== 查询报修列表 userId=" + userId + " (类型:" + userId.getClass().getName() + "), 结果数量=" + repairs.size() + " ===");
        
        return repairs.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public RepairVO getRepairDetail(Long id, Long userId) {
        Repair repair = getById(id);
        if (repair == null) {
            throw new RuntimeException("报修单不存在");
        }
        if (!repair.getUserId().equals(userId)) {
            throw new RuntimeException("无权查看此报修单");
        }
        return convertToVO(repair);
    }

    @Override
    public Long createRepair(Long userId, CreateRepairDTO dto) {
        Repair repair = new Repair();
        repair.setRepairNo(generateRepairNo());
        repair.setUserId(userId);
        repair.setLocation(dto.getLocation());
        repair.setIssueType(dto.getIssueType());
        repair.setDescription(dto.getDescription());
        
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            try {
                repair.setImages(objectMapper.writeValueAsString(dto.getImages()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("图片处理失败");
            }
        }
        
        repair.setStatus("received");
        save(repair);
        
        return repair.getId();
    }

    @Override
    public void rateRepair(Long id, Long userId, RateRepairDTO dto) {
        Repair repair = getById(id);
        if (repair == null) {
            throw new RuntimeException("报修单不存在");
        }
        if (!repair.getUserId().equals(userId)) {
            throw new RuntimeException("无权评价此报修单");
        }
        if (!"completed".equals(repair.getStatus())) {
            throw new RuntimeException("只能评价已完成的报修单");
        }
        if (repair.getRating() != null) {
            throw new RuntimeException("已评价过此报修单");
        }
        
        repair.setRating(dto.getRating());
        repair.setFeedback(dto.getFeedback());
        updateById(repair);
    }
    
    private String generateRepairNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", new Random().nextInt(10000));
        return "R" + timestamp + random;
    }
    
    private RepairVO convertToVO(Repair repair) {
        RepairVO vo = new RepairVO();
        vo.setId(repair.getId());
        vo.setRepairNo(repair.getRepairNo());
        vo.setLocation(repair.getLocation());
        vo.setIssueType(repair.getIssueType());
        vo.setDescription(repair.getDescription());
        vo.setStatus(repair.getStatus());
        vo.setRating(repair.getRating());
        vo.setFeedback(repair.getFeedback());
        vo.setCreateTime(repair.getCreateTime());
        vo.setCompletedAt(repair.getCompletedAt());
        
        // 解析图片列表
        if (StringUtils.hasText(repair.getImages())) {
            try {
                vo.setImages(objectMapper.readValue(repair.getImages(), new TypeReference<List<String>>() {}));
            } catch (JsonProcessingException e) {
                vo.setImages(new ArrayList<>());
            }
        } else {
            vo.setImages(new ArrayList<>());
        }
        
        // 获取维修人员信息
        if (repair.getTechnicianId() != null) {
            Technician technician = technicianService.getById(repair.getTechnicianId());
            if (technician != null) {
                vo.setTechnicianName(technician.getName());
            }
        }
        
        return vo;
    }
}
