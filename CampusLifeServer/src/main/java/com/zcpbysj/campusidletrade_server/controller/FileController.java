package com.zcpbysj.campusidletrade_server.controller;

import com.zcpbysj.campusidletrade_server.common.Result;
import com.zcpbysj.campusidletrade_server.util.FileUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 文件上传接口
 */
@Tag(name = "文件上传")
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileController {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @Value("${file.access.url:http://localhost:8080/uploads/}")
    private String accessUrl;

    @Operation(summary = "上传单张图片")
    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }
        
        String url = saveFile(file);
        Map<String, String> data = new HashMap<>();
        data.put("url", url);
        return Result.success(data);
    }

    @Operation(summary = "批量上传图片")
    @PostMapping("/images")
    public Result<Map<String, List<String>>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return Result.error("请选择文件");
        }
        
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                urls.add(saveFile(file));
            }
        }
        
        Map<String, List<String>> data = new HashMap<>();
        data.put("urls", urls);
        return Result.success(data);
    }
    
    private String saveFile(MultipartFile file) {
        try {
            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString().replace("-", "") + extension;
            
            // 按日期分目录
            String dateDir = java.time.LocalDate.now().toString().replace("-", "/");
            String relativePath = dateDir + "/" + filename;
            
            // 获取绝对路径并创建目录
            File uploadDir = new File(uploadPath).getAbsoluteFile();
            File dir = new File(uploadDir, dateDir);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created && !dir.exists()) {
                    throw new RuntimeException("无法创建上传目录: " + dir.getAbsolutePath());
                }
            }
            
            // 保存文件
            File dest = new File(uploadDir, relativePath);
            file.transferTo(dest.getAbsoluteFile());
            
            return accessUrl + relativePath;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }
}
