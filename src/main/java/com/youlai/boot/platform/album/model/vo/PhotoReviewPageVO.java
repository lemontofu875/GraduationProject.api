package com.youlai.boot.platform.album.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 照片点评分页项 VO
 */
@Data
@Schema(description = "照片点评分页项")
public class PhotoReviewPageVO {

    @Schema(description = "点评记录ID")
    private Long id;

    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "访问URL")
    private String fileUrl;

    @Schema(description = "缩略图访问URL（列表用）")
    private String thumbUrl;

    @Schema(description = "文件大小(字节)")
    private Long fileSize;

    @Schema(description = "照片概述")
    private String reviewSummary;

    @Schema(description = "照片评级：杰出、优秀、良好、有待改进、烂片一张")
    private String reviewRating;

    @Schema(description = "点评时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewTime;
}
