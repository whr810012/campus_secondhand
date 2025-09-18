package com.campus.secondhand.controller;

import com.campus.secondhand.common.Result;
import com.campus.secondhand.entity.Img;
import com.campus.secondhand.service.ImgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.Base64;

/**
 * 文件上传控制器
 *
 * @author campus-secondhand
 */
@Slf4j
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController {

    private final ImgService imgService;

    @Value("${file.upload.max-size:5242880}")
    private long maxFileSize;

    /**
     * 上传图片到数据库（base64格式）
     */
    @PostMapping("/image/database")
    public Result<Map<String, Object>> uploadImageToDatabase(@RequestParam("file") MultipartFile file,
                                                           @RequestParam(value = "userId", required = false) Long userId) {
        try {
            // 验证文件
            if (file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }

            // 验证文件大小
            if (file.getSize() > maxFileSize) {
                return Result.error("文件大小不能超过5MB");
            }

            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.error("只能上传图片文件");
            }

            // 获取文件扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 生成新文件名
            String fileName = UUID.randomUUID().toString() + extension;
            
            // 将文件转换为base64
            byte[] fileBytes = file.getBytes();
            String base64Data = Base64.getEncoder().encodeToString(fileBytes);
            
            // 保存到数据库
            Img savedImg = imgService.saveImg(
                fileName,
                base64Data,
                userId
            );
            
            if (savedImg == null) {
                return Result.error("图片保存失败");
            }

            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("id", savedImg.getId());
            result.put("name", savedImg.getName());
            result.put("base64", "data:" + contentType + ";base64," + base64Data); // 返回完整的base64数据
            result.put("userId", savedImg.getUserId());
            result.put("status", savedImg.getStatus());
            result.put("createdAt", savedImg.getCreatedAt());
            result.put("updatedAt", savedImg.getUpdatedAt());

            log.info("图片上传到数据库成功: {}", fileName);
            return Result.success(result);

        } catch (IOException e) {
            log.error("图片上传失败", e);
            return Result.error("图片上传失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("图片上传异常", e);
            return Result.error("图片上传异常");
        }
    }

    /**
     * 获取图片（base64格式）
     */
    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        try {
            Img img = imgService.getImgById(id);
            if (img == null) {
                return ResponseEntity.notFound().build();
            }

            // 将base64数据转换为字节数组
            byte[] imageBytes = Base64.getDecoder().decode(img.getBase64Data());
            
            // 根据文件名获取MIME类型
            String contentType = "image/jpeg"; // 默认JPEG
            String fileName = img.getName().toLowerCase();
            if (fileName.endsWith(".png")) {
                contentType = "image/png";
            } else if (fileName.endsWith(".gif")) {
                contentType = "image/gif";
            } else if (fileName.endsWith(".webp")) {
                contentType = "image/webp";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(imageBytes);
        } catch (Exception e) {
            log.error("获取图片失败", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 获取用户的图片列表
     */
    @GetMapping("/images/user/{userId}")
    public Result<List<Map<String, Object>>> getUserImages(@PathVariable Long userId) {
        try {
            List<Img> imgs = imgService.getImgsByUserId(userId);
            List<Map<String, Object>> result = new ArrayList<>();
            
            for (Img img : imgs) {
                Map<String, Object> imgInfo = new HashMap<>();
                imgInfo.put("id", img.getId());
                imgInfo.put("name", img.getName());
                imgInfo.put("userId", img.getUserId());
                imgInfo.put("status", img.getStatus());
                imgInfo.put("createdAt", img.getCreatedAt());
                imgInfo.put("updatedAt", img.getUpdatedAt());
                // 不返回base64Data，减少数据传输量
                result.add(imgInfo);
            }

            return Result.success(result);
        } catch (Exception e) {
            log.error("获取用户图片列表失败", e);
            return Result.error("获取用户图片列表失败");
        }
    }

    /**
     * 删除图片
     */
    @DeleteMapping("/image/{id}")
    public Result<String> deleteImage(@PathVariable Long id, 
                                     @RequestParam(value = "userId", required = false) Long userId) {
        try {
            boolean deleted = imgService.deleteImg(id, userId);
            if (deleted) {
                return Result.success("图片删除成功");
            } else {
                return Result.error("图片删除失败或无权限");
            }
        } catch (Exception e) {
            log.error("删除图片失败", e);
            return Result.error("删除图片失败");
        }
     }




}