package com.zcpbysj.campusidletrade_server.controller;

import com.zcpbysj.campusidletrade_server.entity.Technician;
import com.zcpbysj.campusidletrade_server.service.TechnicianService;
import com.zcpbysj.campusidletrade_server.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 维修人员控制器
 */
@RestController
@RequestMapping("/api/admin/technicians")
public class TechnicianController {

    @Autowired
    private TechnicianService technicianService;

    /**
     * 获取所有维修人员列表
     */
    @GetMapping
    public Result<List<Technician>> list() {
        List<Technician> list = technicianService.list();
        return Result.success(list);
    }

    /**
     * 根据ID获取维修人员
     */
    @GetMapping("/{id}")
    public Result<Technician> getById(@PathVariable Long id) {
        Technician technician = technicianService.getById(id);
        return Result.success(technician);
    }

    /**
     * 添加维修人员
     */
    @PostMapping
    public Result<?> create(@RequestBody Technician technician) {
        technicianService.save(technician);
        return Result.success("添加成功");
    }

    /**
     * 更新维修人员
     */
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Technician technician) {
        technician.setId(id);
        technicianService.updateById(technician);
        return Result.success("更新成功");
    }

    /**
     * 删除维修人员
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        technicianService.removeById(id);
        return Result.success("删除成功");
    }
}
