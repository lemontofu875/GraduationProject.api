package com.youlai.boot.platform.album.model.query;

import com.youlai.boot.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 照片分页查询对象
 *
 * @author youlai
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "照片分页查询")
public class PhotoPageQuery extends BasePageQuery {

    @Schema(description = "拍摄时间范围 [开始时间, 结束时间]，格式 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss")
    private List<String> shootTime;

    @Schema(description = "AI分析-图片描述（模糊匹配）")
    private String aiDescription;

    @Schema(description = "AI分析-标签（多选，命中任一项即匹配）")
    private List<String> aiTags;

    @Schema(description = "AI分析-场景分类（多选，命中任一项即匹配）")
    private List<String> aiScene;

    @Schema(description = "用户备注（模糊匹配）")
    private String description;

    @Schema(description = "是否收藏")
    private Boolean isFavorite;

    @Schema(description = "相册名称（模糊匹配）")
    private String albumName;
}
