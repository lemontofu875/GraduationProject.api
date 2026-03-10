package com.youlai.boot.platform.album.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * EXIF 信息 VO（光圈、快门、ISO、拍摄时间、分辨率等）
 *
 * @author youlai
 */
@Data
@Schema(description = "EXIF信息")
public class ExifInfoVO {

    @Schema(description = "光圈")
    private String aperture;

    @Schema(description = "快门速度")
    private String shutterSpeed;

    @Schema(description = "ISO")
    private Integer iso;

    @Schema(description = "拍摄时间")
    private String shootTime;

    @Schema(description = "分辨率")
    private String resolution;
}
