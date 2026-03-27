package com.youlai.boot.platform.album.model.query;

import com.youlai.boot.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 照片点评分页查询
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "照片点评分页查询")
public class PhotoReviewPageQuery extends BasePageQuery {
}
