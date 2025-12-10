package com.zcpbysj.campusidletrade_server.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 文件工具类
 * 提供文件上传、下载、压缩、格式转换等功能
 */
public class FileUtil {

    // 支持的图片格式
    private static final Set<String> IMAGE_EXTENSIONS = Set.of(
        "jpg", "jpeg", "png", "gif", "bmp", "webp", "svg"
    );
    
    // 支持的文档格式
    private static final Set<String> DOCUMENT_EXTENSIONS = Set.of(
        "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt"
    );
    
    // 支持的视频格式
    private static final Set<String> VIDEO_EXTENSIONS = Set.of(
        "mp4", "avi", "mov", "wmv", "flv", "mkv", "webm"
    );
    
    // 支持的音频格式
    private static final Set<String> AUDIO_EXTENSIONS = Set.of(
        "mp3", "wav", "flac", "aac", "ogg", "wma"
    );

    /**
     * 获取文件扩展名
     */
    public static String getFileExtension(String filename) {
        if (StringUtil.isEmpty(filename)) return "";
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 获取不带扩展名的文件名
     */
    public static String getFileNameWithoutExtension(String filename) {
        if (StringUtil.isEmpty(filename)) return "";
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) return filename;
        
        return filename.substring(0, lastDotIndex);
    }

    /**
     * 生成唯一文件名
     */
    public static String generateUniqueFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String randomStr = StringUtil.generateRandomAlphanumeric(6);
        
        return timestamp + "_" + randomStr + (StringUtil.isNotEmpty(extension) ? "." + extension : "");
    }

    /**
     * 检查文件类型
     */
    public static FileType getFileType(String filename) {
        String extension = getFileExtension(filename);
        
        if (IMAGE_EXTENSIONS.contains(extension)) {
            return FileType.IMAGE;
        } else if (DOCUMENT_EXTENSIONS.contains(extension)) {
            return FileType.DOCUMENT;
        } else if (VIDEO_EXTENSIONS.contains(extension)) {
            return FileType.VIDEO;
        } else if (AUDIO_EXTENSIONS.contains(extension)) {
            return FileType.AUDIO;
        } else {
            return FileType.OTHER;
        }
    }

    /**
     * 验证文件大小
     */
    public static boolean isValidFileSize(MultipartFile file, long maxSizeInBytes) {
        return file != null && file.getSize() <= maxSizeInBytes;
    }

    /**
     * 验证文件类型
     */
    public static boolean isValidFileType(String filename, Set<String> allowedExtensions) {
        String extension = getFileExtension(filename);
        return allowedExtensions.contains(extension);
    }

    /**
     * 保存上传的文件
     */
    public static String saveUploadedFile(MultipartFile file, String uploadDir) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        // 创建上传目录
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 生成唯一文件名
        String uniqueFileName = generateUniqueFileName(file.getOriginalFilename());
        Path filePath = uploadPath.resolve(uniqueFileName);

        // 保存文件
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFileName;
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 复制文件
     */
    public static boolean copyFile(String sourcePath, String targetPath) {
        try {
            Path source = Paths.get(sourcePath);
            Path target = Paths.get(targetPath);
            
            // 创建目标目录
            Files.createDirectories(target.getParent());
            
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 移动文件
     */
    public static boolean moveFile(String sourcePath, String targetPath) {
        try {
            Path source = Paths.get(sourcePath);
            Path target = Paths.get(targetPath);
            
            // 创建目标目录
            Files.createDirectories(target.getParent());
            
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 读取文件内容为字符串
     */
    public static String readFileToString(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readString(path);
    }

    /**
     * 写入字符串到文件
     */
    public static void writeStringToFile(String content, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        Files.writeString(path, content);
    }

    /**
     * 读取文件内容为字节数组
     */
    public static byte[] readFileToBytes(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readAllBytes(path);
    }

    /**
     * 写入字节数组到文件
     */
    public static void writeBytesToFile(byte[] bytes, String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        Files.write(path, bytes);
    }

    /**
     * 获取文件MIME类型
     */
    public static String getMimeType(String filename) {
        try {
            Path path = Paths.get(filename);
            return Files.probeContentType(path);
        } catch (IOException e) {
            // 如果无法探测，根据扩展名返回
            String extension = getFileExtension(filename);
            return getMimeTypeByExtension(extension);
        }
    }

    /**
     * 根据扩展名获取MIME类型
     */
    private static String getMimeTypeByExtension(String extension) {
        return switch (extension.toLowerCase()) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "pdf" -> "application/pdf";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls" -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "txt" -> "text/plain";
            case "mp4" -> "video/mp4";
            case "mp3" -> "audio/mpeg";
            default -> "application/octet-stream";
        };
    }

    /**
     * 压缩文件
     */
    public static void zipFiles(List<String> filePaths, String zipFilePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            
            for (String filePath : filePaths) {
                Path path = Paths.get(filePath);
                if (!Files.exists(path)) continue;
                
                String fileName = path.getFileName().toString();
                ZipEntry zipEntry = new ZipEntry(fileName);
                zos.putNextEntry(zipEntry);
                
                Files.copy(path, zos);
                zos.closeEntry();
            }
        }
    }

    /**
     * 解压文件
     */
    public static void unzipFile(String zipFilePath, String destDir) throws IOException {
        Path destPath = Paths.get(destDir);
        Files.createDirectories(destPath);
        
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry zipEntry;
            
            while ((zipEntry = zis.getNextEntry()) != null) {
                Path entryPath = destPath.resolve(zipEntry.getName());
                
                // 安全检查，防止目录遍历攻击
                if (!entryPath.startsWith(destPath)) {
                    throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
                }
                
                if (zipEntry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    Files.copy(zis, entryPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }
    }

    /**
     * 获取文件大小（格式化）
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        
        String[] units = {"KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = bytes;
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.2f %s", size, units[unitIndex]);
    }

    /**
     * 列出目录下的所有文件
     */
    public static List<FileInfo> listFiles(String dirPath, boolean recursive) throws IOException {
        List<FileInfo> fileInfos = new ArrayList<>();
        Path dir = Paths.get(dirPath);
        
        if (!Files.exists(dir) || !Files.isDirectory(dir)) {
            return fileInfos;
        }
        
        if (recursive) {
            Files.walk(dir)
                 .filter(Files::isRegularFile)
                 .forEach(path -> {
                     try {
                         fileInfos.add(createFileInfo(path));
                     } catch (IOException e) {
                         // 忽略无法访问的文件
                     }
                 });
        } else {
            Files.list(dir)
                 .filter(Files::isRegularFile)
                 .forEach(path -> {
                     try {
                         fileInfos.add(createFileInfo(path));
                     } catch (IOException e) {
                         // 忽略无法访问的文件
                     }
                 });
        }
        
        return fileInfos;
    }

    /**
     * 创建文件信息对象
     */
    private static FileInfo createFileInfo(Path path) throws IOException {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setName(path.getFileName().toString());
        fileInfo.setPath(path.toString());
        fileInfo.setSize(Files.size(path));
        fileInfo.setFormattedSize(formatFileSize(Files.size(path)));
        fileInfo.setType(getFileType(fileInfo.getName()));
        fileInfo.setMimeType(getMimeType(fileInfo.getName()));
        fileInfo.setLastModified(Files.getLastModifiedTime(path).toInstant());
        return fileInfo;
    }

    /**
     * 检查文件是否存在
     */
    public static boolean exists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    /**
     * 创建目录
     */
    public static boolean createDirectories(String dirPath) {
        try {
            Files.createDirectories(Paths.get(dirPath));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 文件类型枚举
     */
    public enum FileType {
        IMAGE("图片"),
        DOCUMENT("文档"),
        VIDEO("视频"),
        AUDIO("音频"),
        OTHER("其他");
        
        private final String description;
        
        FileType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }

    /**
     * 文件信息类
     */
    public static class FileInfo {
        private String name;
        private String path;
        private long size;
        private String formattedSize;
        private FileType type;
        private String mimeType;
        private java.time.Instant lastModified;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        
        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }
        
        public String getFormattedSize() { return formattedSize; }
        public void setFormattedSize(String formattedSize) { this.formattedSize = formattedSize; }
        
        public FileType getType() { return type; }
        public void setType(FileType type) { this.type = type; }
        
        public String getMimeType() { return mimeType; }
        public void setMimeType(String mimeType) { this.mimeType = mimeType; }
        
        public java.time.Instant getLastModified() { return lastModified; }
        public void setLastModified(java.time.Instant lastModified) { this.lastModified = lastModified; }
    }
}