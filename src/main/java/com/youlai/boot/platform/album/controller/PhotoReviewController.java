package com.youlai.boot.platform.album.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlai.boot.core.web.PageResult;
import com.youlai.boot.core.web.Result;
import com.youlai.boot.platform.album.model.query.PhotoReviewPageQuery;
import com.youlai.boot.platform.album.model.vo.PhotoReviewDetailVO;
import com.youlai.boot.platform.album.model.vo.PhotoReviewPageVO;
import com.youlai.boot.platform.album.model.vo.PhotoReviewUploadVO;
import com.youlai.boot.platform.album.service.PhotoReviewService;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 照片点评接口
 */
@Tag(name = "智能相册-照片点评接口")
@RestController
@RequestMapping("/api/v1/photo-reviews")
@RequiredArgsConstructor
public class PhotoReviewController {

    private final PhotoReviewService photoReviewService;

    @PostMapping("/upload")
    @Operation(summary = "上传照片并生成 AI 点评", description = "上传照片、解析EXIF、调用大模型生成概述/优缺点/评级、保存点评记录")
    public Result<PhotoReviewUploadVO> uploadPhotoReview(
            @Parameter(name = "file", description = "照片文件，支持 jpg/png/webp 格式", required = true, schema = @Schema(type = "string", format = "binary"))
            @RequestPart("file") MultipartFile file
    ) {
        PhotoReviewUploadVO vo = photoReviewService.uploadPhotoReview(file);
        return Result.success(vo, "点评生成成功");
    }

    @GetMapping("/page")
    @Operation(summary = "照片点评分页", description = "分页返回照片点评记录，支持按点评时间范围、照片评级筛选")
    public PageResult<PhotoReviewPageVO> getPhotoReviewPage(PhotoReviewPageQuery queryParams) {
        IPage<PhotoReviewPageVO> result = photoReviewService.getPhotoReviewPage(queryParams);
        return PageResult.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "照片点评详情", description = "根据点评记录ID返回照片基础信息和AI点评信息")
    public Result<PhotoReviewDetailVO> getPhotoReviewDetail(
            @Parameter(name = "id", description = "点评记录ID", required = true, in = ParameterIn.PATH)
            @PathVariable("id") Long id
    ) {
        return Result.success(photoReviewService.getPhotoReviewDetail(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除单条照片点评记录", description = "根据点评记录ID删除单条照片点评记录")
    public Result<?> deletePhotoReview(
            @Parameter(name = "id", description = "点评记录ID", required = true, in = ParameterIn.PATH)
            @PathVariable("id") Long id
    ) {
        boolean ok = photoReviewService.deletePhotoReview(id);
        return Result.judge(ok);
    }
}
