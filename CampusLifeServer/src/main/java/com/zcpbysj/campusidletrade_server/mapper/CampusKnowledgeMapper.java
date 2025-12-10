package com.zcpbysj.campusidletrade_server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zcpbysj.campusidletrade_server.entity.CampusKnowledge;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 校园知识库Mapper
 */
@Mapper
public interface CampusKnowledgeMapper extends BaseMapper<CampusKnowledge> {
    
    /**
     * 根据关键词搜索知识库
     */
    @Select("SELECT * FROM campus_knowledge WHERE status = 1 AND (question LIKE CONCAT('%', #{keyword}, '%') OR keywords LIKE CONCAT('%', #{keyword}, '%')) ORDER BY priority DESC LIMIT 5")
    List<CampusKnowledge> searchByKeywords(@Param("keyword") String keyword);
}
