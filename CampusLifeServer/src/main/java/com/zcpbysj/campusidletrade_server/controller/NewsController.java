package com.zcpbysj.campusidletrade_server.controller;

import com.zcpbysj.campusidletrade_server.common.Result;
import com.zcpbysj.campusidletrade_server.entity.vo.news.NewsVO;
import com.zcpbysj.campusidletrade_server.entity.vo.common.PageVO;
import com.zcpbysj.campusidletrade_server.service.INewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 新闻接口
 */
@Tag(name = "新闻模块")
@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final INewsService newsService;

    @Operation(summary = "获取新闻列表")
    @GetMapping
    public Result<PageVO<NewsVO>> getNewsList(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        PageVO<NewsVO> vo = newsService.getNewsList(category, page, size);
        return Result.success(vo);
    }

    @Operation(summary = "获取新闻详情")
    @GetMapping("/{id}")
    public Result<NewsVO> getNewsDetail(@PathVariable Long id) {
        NewsVO vo = newsService.getNewsDetail(id);
        return Result.success(vo);
    }
}
