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
 * 照片点评实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "photo_review", autoResultMap = true)
public class PhotoReview extends BaseEntity {

    /** 原始文件名 */
    private String originalName;

    /** 存储路径 */
    private String filePath;

    /** 访问URL */
    private String fileUrl;

    /** 文件大小(字节) */
    private Long fileSize;

    /** EXIF信息(光圈/快门/ISO/拍摄时间/分辨率等) */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private ExifInfoVO exifInfo;

    /** 照片概述 */
    private String reviewSummary;

    /** 优点 */
    private String reviewAdvantages;

    /** 缺点 */
    private String reviewDisadvantages;

    /** 照片评级：杰出、优秀、良好、有待改进、烂片一张 */
    private String reviewRating;

    /** 点评时间 */
    private LocalDateTime reviewTime;
}
