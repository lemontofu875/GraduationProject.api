package com.youlai.boot.platform.album.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 相册响应 VO
 *
 * @author youlai
 */
@Data
@Schema(description = "相册信息")
public class AlbumVO {

    @Schema(description = "相册ID")
    private Long id;

    @Schema(description = "相册名称")
    private String name;

    @Schema(description = "所属用户ID")
    private Long userId;

    @Schema(description = "封面图URL")
    private String coverUrl;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
