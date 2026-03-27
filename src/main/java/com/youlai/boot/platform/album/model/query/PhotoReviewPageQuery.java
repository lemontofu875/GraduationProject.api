package com.youlai.boot.platform.album.model.query;

import com.youlai.boot.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 照片点评分页查询
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "照片点评分页查询")
public class PhotoReviewPageQuery extends BasePageQuery {

    @Schema(description = "点评时间范围 [开始时间, 结束时间]，格式 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss")
    private List<String> reviewTime;

    @Schema(description = "照片评级：杰出、优秀、良好、有待改进、烂片一张")
    private String reviewRating;
}
