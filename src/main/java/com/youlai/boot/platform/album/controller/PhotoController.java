package com.youlai.boot.platform.album.controller;

import com.youlai.boot.core.web.Result;
import com.youlai.boot.platform.album.model.vo.PhotoUploadVO;
import com.youlai.boot.platform.album.service.PhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 照片上传接口（智能相册核心）
 *
 * @author youlai
 */
@Tag(name = "智能相册-照片接口")
@RestController
@RequestMapping("/api/v1/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping("/upload")
    @Operation(summary = "照片上传", description = "上传照片、解析EXIF、调用大模型分析内容、存储到数据库")
    public Result<PhotoUploadVO> uploadPhoto(
            @Parameter(name = "file", description = "照片文件，支持 jpg/png/webp 格式", required = true, schema = @Schema(type = "string", format = "binary"))
            @RequestPart("file") MultipartFile file,
            @Parameter(name = "albumId", description = "所属相册ID", required = true, in = ParameterIn.QUERY)
            @RequestParam("albumId") Long albumId,
            @Parameter(name = "description", description = "照片备注，用户自定义", required = false, in = ParameterIn.QUERY)
            @RequestParam(value = "description", required = false) String description
    ) {
        PhotoUploadVO vo = photoService.uploadPhoto(file, albumId, description);
        return Result.success(vo, "上传成功");
    }
}
