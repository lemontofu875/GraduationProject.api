package com.youlai.boot.platform.album.model.query;

import com.youlai.boot.common.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 回收站照片分页查询
 *
 * @author youlai
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "回收站照片分页查询")
public class RecyclePhotoPageQuery extends BasePageQuery {
}
