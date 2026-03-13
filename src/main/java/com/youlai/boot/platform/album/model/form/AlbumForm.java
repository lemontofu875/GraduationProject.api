package com.youlai.boot.platform.album.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 相册表单（新建/编辑）
 *
 * @author youlai
 */
@Data
@Schema(description = "相册表单")
public class AlbumForm {

    @Schema(description = "相册名称", example = "旅行相册", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "相册名称不能为空")
    @Size(max = 100, message = "相册名称长度不能超过100")
    private String name;
}
