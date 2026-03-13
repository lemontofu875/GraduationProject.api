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
     * @param imageUrl    图片 URL（若为 localhost 等内网地址，云端无法访问，需传 imageBytes 用 base64）
     * @param imageBytes  图片字节（可选）；当 URL 为本地/内网时传入，将以 base64 发送给大模型
     * @param mimeType    图片 MIME 类型（如 image/jpeg），用于 base64 时
     * @return 分析结果，失败时返回空描述
     */
    AiImageAnalysisDTO analyzeImage(String imageUrl, byte[] imageBytes, String mimeType);
}
