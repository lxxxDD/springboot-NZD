package com.zcpbysj.campusidletrade_server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zcpbysj.campusidletrade_server.entity.TransactionRecord;
import com.zcpbysj.campusidletrade_server.entity.vo.common.PageVO;
import com.zcpbysj.campusidletrade_server.entity.vo.market.TransactionVO;

/**
 * 交易记录服务接口
 */
public interface ITransactionService extends IService<TransactionRecord> {
    
    /**
     * 获取用户交易记录列表
     */
    PageVO<TransactionVO> getTransactions(Long userId, Integer page, Integer size, String type);
}
