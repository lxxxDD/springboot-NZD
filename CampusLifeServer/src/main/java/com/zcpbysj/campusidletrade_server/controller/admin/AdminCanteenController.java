package com.zcpbysj.campusidletrade_server.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcpbysj.campusidletrade_server.common.Result;
import com.zcpbysj.campusidletrade_server.entity.Canteen;
import com.zcpbysj.campusidletrade_server.entity.dto.admin.StatusDTO;
import com.zcpbysj.campusidletrade_server.service.ICanteenService;
import com.zcpbysj.campusidletrade_server.service.ISystemLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理端-食堂管理")
@RestController
@RequestMapping("/api/admin/canteens")
@RequiredArgsConstructor
public class AdminCanteenController {

    private final ICanteenService canteenService;
    private final ISystemLogService systemLogService;

    @Operation(summary = "获取食堂列表")
    @GetMapping
    public Result<Page<Canteen>> getCanteenList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String name) {
        Page<Canteen> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Canteen> wrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.isEmpty()) {
            wrapper.like(Canteen::getName, name);
        }
        wrapper.orderByDesc(Canteen::getCreateTime);
        return Result.success(canteenService.page(pageParam, wrapper));
    }

    @Operation(summary = "获取食堂详情")
    @GetMapping("/{id}")
    public Result<Canteen> getCanteenById(@PathVariable Long id) {
        Canteen canteen = canteenService.getById(id);
        if (canteen == null) {
            return Result.fail("食堂不存在");
        }
        return Result.success(canteen);
    }

    @Operation(summary = "创建食堂")
    @PostMapping
    public Result<?> createCanteen(@RequestBody Canteen canteen) {
        canteen.setStatus("open"); // 默认营业中
        canteenService.save(canteen);
        systemLogService.addLog("创建食堂: " + canteen.getName(), "success", "食堂管理");
        return Result.success("创建成功");
    }

    @Operation(summary = "更新食堂")
    @PutMapping("/{id}")
    public Result<?> updateCanteen(@PathVariable Long id, @RequestBody Canteen canteen) {
        Canteen existingCanteen = canteenService.getById(id);
        if (existingCanteen == null) {
            return Result.fail("食堂不存在");
        }
        
        canteen.setId(id);
        canteenService.updateById(canteen);
        systemLogService.addLog("更新食堂: " + canteen.getName(), "info", "食堂管理");
        return Result.success("更新成功");
    }

    @Operation(summary = "删除食堂")
    @DeleteMapping("/{id}")
    public Result<?> deleteCanteen(@PathVariable Long id) {
        if (!canteenService.removeById(id)) {
            return Result.fail("删除失败");
        }
        systemLogService.addLog("删除食堂 #" + id, "warning", "食堂管理");
        return Result.success("删除成功");
    }

    @Operation(summary = "更新食堂状态")
    @PostMapping("/{id}/status")
    public Result<?> updateCanteenStatus(@PathVariable Long id, @RequestBody StatusDTO dto) {
        String status = dto.getStatus();
        Canteen canteen = canteenService.getById(id);
        if (canteen == null) {
            return Result.fail("食堂不存在");
        }
        canteen.setStatus(status);
        canteenService.updateById(canteen);
        systemLogService.addLog("食堂状态变更: " + status, "info", "食堂管理");
        return Result.success("状态更新成功");
    }
}
