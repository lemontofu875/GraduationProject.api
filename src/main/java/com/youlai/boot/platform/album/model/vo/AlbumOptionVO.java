package com.youlai.boot.platform.album.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 相册下拉选项 VO（用于前端下拉框等）
 *
 * @author youlai
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "相册下拉选项")
public class AlbumOptionVO {

    @Schema(description = "相册ID")
    private Long albumId;

    @Schema(description = "相册名称")
    private String name;
}
