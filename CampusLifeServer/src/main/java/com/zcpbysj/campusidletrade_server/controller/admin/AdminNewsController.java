package com.zcpbysj.campusidletrade_server.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zcpbysj.campusidletrade_server.common.Result;
import com.zcpbysj.campusidletrade_server.entity.News;
import com.zcpbysj.campusidletrade_server.service.INewsService;
import com.zcpbysj.campusidletrade_server.service.ISystemLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/news")
@RequiredArgsConstructor
public class AdminNewsController {

    private final INewsService newsService;
    private final ISystemLogService systemLogService;

    // 新闻列表
    @GetMapping
    public Result<Page<News>> getNewsList(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size,
                                          @RequestParam(required = false) String title) {
        Page<News> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<>();
        if (title != null && !title.isEmpty()) {
            wrapper.like(News::getTitle, title);
        }
        wrapper.orderByDesc(News::getPublishTime);
        return Result.success(newsService.page(pageParam, wrapper));
    }

    // 发布/更新新闻
    @PostMapping
    public Result<?> saveNews(@RequestBody News news) {
        if (news.getId() == null) {
            news.setCreateTime(LocalDateTime.now());
            news.setPublishTime(LocalDateTime.now());
            newsService.save(news);
            systemLogService.addLog("发布新闻: " + news.getTitle(), "success", "新闻管理");
        } else {
            news.setUpdateTime(LocalDateTime.now());
            newsService.updateById(news);
            systemLogService.addLog("更新新闻: " + news.getTitle(), "info", "新闻管理");
        }
        return Result.success();
    }

    // 获取新闻详情
    @GetMapping("/{id}")
    public Result<News> getNewsById(@PathVariable Long id) {
        News news = newsService.getById(id);
        if (news == null) {
            return Result.fail("新闻不存在");
        }
        return Result.success(news);
    }

    // 更新新闻状态
    @PostMapping("/{id}/status")
    public Result<?> updateNewsStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        News news = newsService.getById(id);
        if (news == null) {
            return Result.fail("新闻不存在");
        }
        news.setStatus(status);
        newsService.updateById(news);
        systemLogService.addLog("新闻状态变更: " + status, "info", "新闻管理");
        return Result.success("状态更新成功");
    }

    // 删除新闻
    @DeleteMapping("/{id}")
    public Result<?> deleteNews(@PathVariable Long id) {
        newsService.removeById(id);
        systemLogService.addLog("删除新闻 #" + id, "warning", "新闻管理");
        return Result.success("删除成功");
    }
}
