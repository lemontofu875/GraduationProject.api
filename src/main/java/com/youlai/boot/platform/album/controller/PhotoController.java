package com.youlai.boot.platform.album.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlai.boot.core.web.PageResult;
import com.youlai.boot.core.web.Result;
import com.youlai.boot.platform.album.model.query.PhotoPageQuery;
import com.youlai.boot.platform.album.model.vo.PhotoPageVO;
import com.youlai.boot.platform.album.model.vo.PhotoUploadVO;
import com.youlai.boot.platform.album.service.PhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
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

    @GetMapping("/page")
    @Operation(summary = "照片分页列表", description = "分页返回照片及完整信息，支持按拍摄时间、AI描述/标签/场景、用户备注、是否收藏、相册名称筛选；不传筛选项时返回所有照片")
    public PageResult<PhotoPageVO> getPhotoPage(PhotoPageQuery queryParams) {
        IPage<PhotoPageVO> result = photoService.getPhotoPage(queryParams);
        return PageResult.success(result);
    }

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

    @PostMapping("/{photoId}/favorite")
    @Operation(summary = "设置照片收藏状态", description = "根据照片ID设置是否收藏")
    public Result<Void> updateFavorite(
            @Parameter(name = "photoId", description = "照片ID", required = true, in = ParameterIn.PATH)
            @PathVariable("photoId") Long photoId,
            @Parameter(name = "isFavorite", description = "是否收藏 true=收藏 false=取消收藏", required = true, in = ParameterIn.QUERY)
            @RequestParam("isFavorite") Boolean isFavorite
    ) {
        photoService.updateFavorite(photoId, isFavorite);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除照片", description = "根据照片ID删除照片")
    public Result<?> deletePhoto(
            @Parameter(name = "id", description = "照片ID", required = true, in = ParameterIn.PATH)
            @PathVariable("id") Long id
    ) {
        boolean ok = photoService.deletePhoto(id);
        return Result.judge(ok);
    }
}
