package com.youlai.boot.platform.album.model.dto;

import lombok.Data;

/**
 * AI 图片分析结果 DTO
 *
 * @author youlai
 */
@Data
public class AiImageAnalysisDTO {

    /** 图片详细描述 */
    private String description;

    /** 标签(逗号分隔) */
    private String tags;

    /** 场景分类 */
    private String scene;
} 
