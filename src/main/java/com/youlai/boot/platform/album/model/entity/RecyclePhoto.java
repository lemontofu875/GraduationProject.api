package com.youlai.boot.platform.album.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.youlai.boot.common.base.BaseEntity;
import com.youlai.boot.platform.album.model.vo.ExifInfoVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 回收站照片实体
 *
 * @author youlai
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "photo_recycle_bin", autoResultMap = true)
public class RecyclePhoto extends BaseEntity {

    /** 原照片ID */
    private Long photoId;

    /** 原所属相册ID */
    private Long albumId;

    /** 原始文件名 */
    private String originalName;

    /** 存储路径 */
    private String filePath;

    /** 访问URL */
    private String fileUrl;

    /** 缩略图存储路径 */
    private String thumbPath;

    /** 缩略图访问URL */
    private String thumbUrl;

    /** 文件大小(字节) */
    private Long fileSize;

    /** EXIF信息(光圈/快门/ISO/拍摄时间/分辨率等) */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private ExifInfoVO exifInfo;

    /** AI分析-图片描述 */
    private String aiDescription;

    /** AI分析-标签(逗号分隔) */
    private String aiTags;

    /** AI分析-场景分类 */
    private String aiScene;

    /** 用户备注 */
    private String description;

    /** 是否收藏 */
    private Boolean isFavorite;

    /** 删除时间 */
    private LocalDateTime deletedTime;
}
