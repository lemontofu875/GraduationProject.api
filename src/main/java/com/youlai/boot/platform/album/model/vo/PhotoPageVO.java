package com.youlai.boot.platform.album.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 照片分页列表项 VO（含相册名及完整照片信息）
 *
 * @author youlai
 */
@Data
@Schema(description = "照片分页列表项")
public class PhotoPageVO {

    @Schema(description = "照片ID")
    private Long id;

    @Schema(description = "所属相册ID")
    private Long albumId;

    @Schema(description = "相册名称")
    private String albumName;

    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "存储路径")
    private String filePath;

    @Schema(description = "访问URL")
    private String fileUrl;

    @Schema(description = "缩略图访问URL（优先 WebP，失败时为 JPEG；列表/宫格请用此字段；无则回退 fileUrl）")
    private String thumbUrl;

    @Schema(description = "文件大小(字节)")
    private Long fileSize;

    @Schema(description = "EXIF信息(光圈/快门/ISO/拍摄时间/分辨率等)")
    private ExifInfoVO exifInfo;

    @Schema(description = "AI分析-图片描述")
    private String aiDescription;

    @Schema(description = "AI分析-标签(逗号分隔)")
    private String aiTags;

    @Schema(description = "AI分析-场景分类")
    private String aiScene;

    @Schema(description = "用户备注")
    private String description;

    @Schema(description = "是否收藏")
    private Boolean isFavorite;

    @Schema(description = "上传/创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime uploadTime;
}
