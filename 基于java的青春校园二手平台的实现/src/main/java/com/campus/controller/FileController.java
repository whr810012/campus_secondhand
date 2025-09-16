package com.campus.controller;

import com.campus.common.Result;
import com.campus.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件上传控制器
 * 
 * @author Campus Team
 */
@RestController
@RequestMapping("/file")
@CrossOrigin
public class FileController {
    
    @Autowired
    private FileUtil fileUtil;
    
    /**
     * 上传单个文件
     */
    @PostMapping("/upload")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            
            String fileUrl = fileUtil.uploadFile(file);
            return Result.success("文件上传成功", fileUrl);
        } catch (Exception e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 上传图片
     */
    @PostMapping("/upload-image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("图片不能为空");
            }
            
            String imageUrl = fileUtil.uploadImage(file);
            return Result.success("图片上传成功", imageUrl);
        } catch (Exception e) {
            return Result.error("图片上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量上传文件
     */
    @PostMapping("/upload-batch")
    public Result<List<String>> uploadFiles(@RequestParam("files") MultipartFile[] files) {
        try {
            if (files == null || files.length == 0) {
                return Result.error("文件不能为空");
            }
            
            List<String> fileUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String fileUrl = fileUtil.uploadFile(file);
                    fileUrls.add(fileUrl);
                }
            }
            
            return Result.success("文件批量上传成功", fileUrls);
        } catch (Exception e) {
            return Result.error("文件批量上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量上传图片
     */
    @PostMapping("/upload-images")
    public Result<List<String>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        try {
            if (files == null || files.length == 0) {
                return Result.error("图片不能为空");
            }
            
            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String imageUrl = fileUtil.uploadImage(file);
                    imageUrls.add(imageUrl);
                }
            }
            
            return Result.success("图片批量上传成功", imageUrls);
        } catch (Exception e) {
            return Result.error("图片批量上传失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除文件
     */
    @DeleteMapping("/delete")
    public Result<Void> deleteFile(@RequestParam String filePath) {
        try {
            boolean deleted = fileUtil.deleteFile(filePath);
            if (deleted) {
                return Result.success("文件删除成功", null);
            } else {
                return Result.error("文件删除失败");
            }
        } catch (Exception e) {
            return Result.error("文件删除失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量删除文件
     */
    @DeleteMapping("/delete-batch")
    public Result<Void> deleteFiles(@RequestBody Map<String, List<String>> params) {
        try {
            List<String> filePaths = params.get("filePaths");
            if (filePaths == null || filePaths.isEmpty()) {
                return Result.error("文件路径不能为空");
            }
            
            int successCount = 0;
            for (String filePath : filePaths) {
                if (fileUtil.deleteFile(filePath)) {
                    successCount++;
                }
            }
            
            return Result.success("批量删除完成，成功删除 " + successCount + " 个文件", null);
        } catch (Exception e) {
            return Result.error("批量删除失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查文件是否存在
     */
    @GetMapping("/exists")
    public Result<Boolean> fileExists(@RequestParam String filePath) {
        try {
            boolean exists = fileUtil.fileExists(filePath);
            return Result.success(exists);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取文件信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getFileInfo(@RequestParam String filePath) {
        try {
            Map<String, Object> fileInfo = new HashMap<>();
            fileInfo.put("exists", fileUtil.fileExists(filePath));
            fileInfo.put("size", fileUtil.getFileSize(filePath));
            fileInfo.put("sizeFormatted", fileUtil.formatFileSize(fileUtil.getFileSize(filePath)));
            fileInfo.put("extension", fileUtil.getFileExtension(filePath));
            
            return Result.success(fileInfo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取支持的文件类型
     */
    @GetMapping("/supported-types")
    public Result<Map<String, List<String>>> getSupportedFileTypes() {
        try {
            Map<String, List<String>> supportedTypes = new HashMap<>();
            
            // 图片类型
            List<String> imageTypes = List.of("jpg", "jpeg", "png", "gif", "bmp", "webp");
            supportedTypes.put("images", imageTypes);
            
            // 文档类型
            List<String> documentTypes = List.of("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt");
            supportedTypes.put("documents", documentTypes);
            
            // 视频类型
            List<String> videoTypes = List.of("mp4", "avi", "mov", "wmv", "flv", "mkv");
            supportedTypes.put("videos", videoTypes);
            
            // 音频类型
            List<String> audioTypes = List.of("mp3", "wav", "flac", "aac", "ogg");
            supportedTypes.put("audios", audioTypes);
            
            return Result.success(supportedTypes);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}