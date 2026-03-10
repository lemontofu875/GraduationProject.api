package com.youlai.boot.platform.album.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youlai.boot.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 相册实体
 *
 * @author youlai
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("album")
public class Album extends BaseEntity {

    /** 相册名称 */
    private String name;

    /** 所属用户ID */
    private Long userId;

    /** 封面图URL */
    private String coverUrl;
}
