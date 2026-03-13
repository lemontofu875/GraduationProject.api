package com.youlai.boot.platform.album.controller;

import com.youlai.boot.core.web.Result;
import com.youlai.boot.platform.album.model.form.AlbumForm;
import com.youlai.boot.platform.album.model.vo.AlbumOptionVO;
import com.youlai.boot.platform.album.model.vo.AlbumVO;
import com.youlai.boot.platform.album.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 相册接口
 *
 * @author youlai
 */
@Tag(name = "智能相册-相册接口")
@RestController
@RequestMapping("/api/v1/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping
    @Operation(summary = "新建相册")
    public Result<AlbumVO> createAlbum(@Valid @RequestBody AlbumForm form) {
        AlbumVO vo = albumService.createAlbum(form);
        return Result.success(vo, "创建成功");
    }

    @GetMapping("/options")
    @Operation(summary = "相册下拉列表", description = "返回所有相册的 id 与名称，供前端下拉框使用")
    public Result<List<AlbumOptionVO>> listAlbumOptions() {
        return Result.success(albumService.listAlbumOptions());
    }
}
