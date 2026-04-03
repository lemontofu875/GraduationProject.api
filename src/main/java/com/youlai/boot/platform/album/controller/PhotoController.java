package com.youlai.boot.platform.album.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlai.boot.core.web.PageResult;
import com.youlai.boot.core.web.Result;
import com.youlai.boot.platform.album.model.form.PhotoUpdateForm;
import com.youlai.boot.platform.album.model.query.PhotoPageQuery;
import com.youlai.boot.platform.album.model.vo.PhotoPageVO;
import com.youlai.boot.platform.album.model.vo.PhotoUploadVO;
import com.youlai.boot.platform.album.service.PhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    @Operation(summary = "照片分页列表", description = "分页返回照片信息；列表展示请使用 thumbUrl（WebP 缩略图），大图/详情使用 fileUrl；支持按拍摄时间、AI描述/标签/场景、用户备注、是否收藏、相册名称筛选；不传筛选项时返回所有照片")
    public PageResult<PhotoPageVO> getPhotoPage(PhotoPageQuery queryParams) {
        IPage<PhotoPageVO> result = photoService.getPhotoPage(queryParams);
        return PageResult.success(result);
    }

    @GetMapping("/scenes")
    @Operation(summary = "场景分类下拉列表", description = "返回已存在的 AI 场景分类列表（去重），用于筛选下拉框")
    public Result<List<String>> listAiScenes() {
        return Result.success(photoService.listAiScenes());
    }

    @GetMapping("/tags")
    @Operation(summary = "AI标签下拉列表", description = "返回已存在的 AI 标签列表（拆分、去重），用于筛选下拉框")
    public Result<List<String>> listAiTags() {
        return Result.success(photoService.listAiTags());
    }

    @PutMapping("/{id}")
    @Operation(summary = "编辑照片信息", description = "可修改 AI 描述、AI 标签、AI 场景、用户备注；未传或为 null 的字段不修改，传空字符串可清空")
    public Result<PhotoUploadVO> updatePhoto(
            @Parameter(name = "id", description = "照片ID", required = true, in = ParameterIn.PATH)
            @PathVariable("id") Long id,
            @Valid @RequestBody PhotoUpdateForm form
    ) {
        PhotoUploadVO vo = photoService.updatePhoto(id, form);
        return Result.success(vo, "修改成功");
    }

    @PostMapping("/upload")
    @Operation(summary = "照片上传", description = "上传原图、自动生成 WebP 缩略图、解析EXIF、调用大模型分析内容、存储到数据库；响应含 fileUrl（原图）与 thumbUrl（列表用）")
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
    @Operation(summary = "删除照片", description = "根据照片ID将照片放入回收站")
    public Result<?> deletePhoto(
            @Parameter(name = "id", description = "照片ID", required = true, in = ParameterIn.PATH)
            @PathVariable("id") Long id
    ) {
        boolean ok = photoService.deletePhoto(id);
        return Result.judge(ok);
    }
}
