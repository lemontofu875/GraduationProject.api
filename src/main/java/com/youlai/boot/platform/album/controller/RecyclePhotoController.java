package com.youlai.boot.platform.album.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlai.boot.core.web.PageResult;
import com.youlai.boot.core.web.Result;
import com.youlai.boot.platform.album.model.query.RecyclePhotoPageQuery;
import com.youlai.boot.platform.album.model.vo.RecyclePhotoDetailVO;
import com.youlai.boot.platform.album.model.vo.RecyclePhotoPageVO;
import com.youlai.boot.platform.album.service.RecyclePhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 回收站照片接口
 *
 * @author youlai
 */
@Tag(name = "智能相册-回收站接口")
@RestController
@RequestMapping("/api/v1/photo-recycle-bin")
@RequiredArgsConstructor
public class RecyclePhotoController {

    private final RecyclePhotoService recyclePhotoService;

    @GetMapping("/page")
    @Operation(summary = "回收站照片分页", description = "分页返回回收站中的所有照片")
    public PageResult<RecyclePhotoPageVO> getRecyclePhotoPage(RecyclePhotoPageQuery queryParams) {
        IPage<RecyclePhotoPageVO> result = recyclePhotoService.getRecyclePhotoPage(queryParams);
        return PageResult.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "回收站照片详情", description = "根据回收站记录ID获取照片详情")
    public Result<RecyclePhotoDetailVO> getRecyclePhotoDetail(
            @Parameter(name = "id", description = "回收站记录ID", required = true, in = ParameterIn.PATH)
            @PathVariable("id") Long id
    ) {
        return Result.success(recyclePhotoService.getRecyclePhotoDetail(id));
    }

    @PostMapping("/{id}/restore")
    @Operation(summary = "还原照片", description = "根据回收站记录ID将照片还原到删除前相册")
    public Result<?> restorePhoto(
            @Parameter(name = "id", description = "回收站记录ID", required = true, in = ParameterIn.PATH)
            @PathVariable("id") Long id
    ) {
        String message = recyclePhotoService.restorePhoto(id);
        return Result.success(null, message);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除单张回收站照片", description = "根据回收站记录ID永久删除单张照片")
    public Result<?> deleteRecyclePhoto(
            @Parameter(name = "id", description = "回收站记录ID", required = true, in = ParameterIn.PATH)
            @PathVariable("id") Long id
    ) {
        boolean ok = recyclePhotoService.deleteRecyclePhoto(id);
        return Result.judge(ok);
    }

    @DeleteMapping("/all")
    @Operation(summary = "清空回收站", description = "永久删除回收站中所有照片")
    public Result<Integer> clearRecycleBin() {
        return Result.success(recyclePhotoService.clearRecycleBin());
    }
}
