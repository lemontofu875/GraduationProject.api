package com.youlai.boot.platform.album.service;

import com.youlai.boot.platform.album.model.dto.AiImageAnalysisDTO;

/**
 * AI 图片分析服务（调用大模型分析图片内容）
 *
 * @author youlai
 */
public interface AiImageAnalysisService {

    /**
     * 分析图片内容，返回描述、标签、场景
     *
     * @param imageUrl 图片可访问 URL
     * @return 分析结果，失败时返回空描述
     */
    AiImageAnalysisDTO analyzeImage(String imageUrl);
}
