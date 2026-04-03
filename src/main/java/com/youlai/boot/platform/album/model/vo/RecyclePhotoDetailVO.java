package com.youlai.boot.platform.album.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 回收站照片详情 VO
 *
 * @author youlai
 */
@Data
@Schema(description = "回收站照片详情")
public class RecyclePhotoDetailVO {

    @Schema(description = "回收站记录ID")
    private Long id;

    @Schema(description = "原照片ID")
    private Long photoId;

    @Schema(description = "原所属相册ID")
    private Long albumId;

    @Schema(description = "原所属相册名称")
    private String albumName;

    @Schema(description = "原始文件名")
    private String originalName;

    @Schema(description = "存储路径")
    private String filePath;

    @Schema(description = "访问URL")
    private String fileUrl;

    @Schema(description = "缩略图访问URL")
    private String thumbUrl;

    @Schema(description = "文件大小(字节)")
    private Long fileSize;

    @Schema(description = "EXIF信息")
    private ExifInfoVO exifInfo;

    @Schema(description = "AI分析-图片描述")
    private String aiDescription;

    @Schema(description = "AI分析-标签")
    private String aiTags;

    @Schema(description = "AI分析-场景分类")
    private String aiScene;

    @Schema(description = "用户备注")
    private String description;

    @Schema(description = "是否收藏")
    private Boolean isFavorite;

    @Schema(description = "删除时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedTime;
}
