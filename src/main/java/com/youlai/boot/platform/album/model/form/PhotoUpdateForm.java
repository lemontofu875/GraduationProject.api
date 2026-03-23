package com.youlai.boot.platform.album.model.form;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 照片编辑表单（可部分更新：JSON 中未传或为 null 的字段不修改；传空字符串表示清空该字段）
 *
 * @author youlai
 */
@Data
@Schema(description = "照片编辑表单")
public class PhotoUpdateForm {

    @Schema(description = "AI分析-图片描述")
    @Size(max = 4000, message = "AI描述长度不能超过4000")
    private String aiDescription;

    @Schema(description = "AI分析-标签（英文逗号或中文逗号分隔，与存储格式一致）")
    @Size(max = 2000, message = "AI标签长度不能超过2000")
    private String aiTags;

    @Schema(description = "AI分析-场景分类")
    @Size(max = 200, message = "AI场景长度不能超过200")
    private String aiScene;

    @Schema(description = "用户备注")
    @Size(max = 2000, message = "备注长度不能超过2000")
    private String description;
}
