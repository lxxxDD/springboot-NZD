package com.zcpbysj.campusidletrade_server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zcpbysj.campusidletrade_server.entity.TransactionRecord;
import com.zcpbysj.campusidletrade_server.entity.vo.common.PageVO;
import com.zcpbysj.campusidletrade_server.entity.vo.market.TransactionVO;
import com.zcpbysj.campusidletrade_server.mapper.TransactionRecordMapper;
import com.zcpbysj.campusidletrade_server.service.ITransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 交易记录服务实现
 */
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl extends ServiceImpl<TransactionRecordMapper, TransactionRecord> 
        implements ITransactionService {

    @Override
    public PageVO<TransactionVO> getTransactions(Long userId, Integer page, Integer size, String type) {
        // 构建查询条件
        LambdaQueryWrapper<TransactionRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TransactionRecord::getUserId, userId);
        if (type != null && !type.isEmpty()) {
            wrapper.eq(TransactionRecord::getType, type);
        }
        wrapper.orderByDesc(TransactionRecord::getCreateTime);
        
        // 分页查询
        IPage<TransactionRecord> pageResult = page(new Page<>(page, size), wrapper);
        
        // 转换为VO
        List<TransactionVO> voList = pageResult.getRecords().stream()
                .map(record -> {
                    TransactionVO vo = new TransactionVO();
                    BeanUtils.copyProperties(record, vo);
                    return vo;
                })
                .collect(Collectors.toList());
        
        // 构建分页响应
        PageVO<TransactionVO> pageVO = new PageVO<>();
        pageVO.setList(voList);
        pageVO.setTotal(pageResult.getTotal());
        pageVO.setHasMore(pageResult.getCurrent() < pageResult.getPages());
        
        return pageVO;
    }
}
