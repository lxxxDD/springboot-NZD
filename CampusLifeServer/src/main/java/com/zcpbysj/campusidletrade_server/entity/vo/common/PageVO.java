package com.zcpbysj.campusidletrade_server.entity.vo.common;

import lombok.Data;

import java.util.List;

/**
 * 分页VO
 */
@Data
public class PageVO<T> {
    
    /**
     * 数据列表
     */
    private List<T> list;
    
    /**
     * 总数
     */
    private Long total;
    
    /**
     * 当前页
     */
    private Integer page;
    
    /**
     * 每页大小
     */
    private Integer size;
    
    /**
     * 是否有更多
     */
    private Boolean hasMore;
    
    public static <T> PageVO<T> of(List<T> list, Long total, Integer page, Integer size) {
        PageVO<T> pageVO = new PageVO<>();
        pageVO.setList(list);
        pageVO.setTotal(total);
        pageVO.setPage(page);
        pageVO.setSize(size);
        pageVO.setHasMore((long) page * size < total);
        return pageVO;
    }
}
