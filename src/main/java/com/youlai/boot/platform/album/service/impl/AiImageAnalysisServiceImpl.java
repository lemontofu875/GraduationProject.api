package com.youlai.boot.platform.album.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.youlai.boot.platform.album.model.dto.AiImageAnalysisDTO;
import com.youlai.boot.platform.album.service.AiImageAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * AI 图片分析服务实现（调用 yunwu.ai / OpenAI 兼容接口）
 *
 * @author youlai
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiImageAnalysisServiceImpl implements AiImageAnalysisService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${album.ai.api-url:https://yunwu.ai/v1/chat/completions}")
    private String apiUrl;

    @Value("${album.ai.api-key:}")
    private String apiKey;

    @Value("${album.ai.model:gpt-4o}")
    private String model;

    private static final String SYSTEM_PROMPT = "你是一个专业的图片分析助手。请分析用户提供的图片，以JSON格式返回，包含以下字段：" +
            "description(详细描述图片内容，如风景、人物、天气、颜色等)、" +
            "tags(用逗号分隔的标签，如：海边,沙滩,小狗,蓝天)、" +
            "scene(场景分类，如：风景、人像、美食、建筑、动物等)。只返回JSON，不要其他文字。";

    private static final String USER_PROMPT = "请分析这张图片，返回JSON格式：{\"description\":\"...\",\"tags\":\"...\",\"scene\":\"...\"}";

    @Override
    public AiImageAnalysisDTO analyzeImage(String imageUrl) {
        AiImageAnalysisDTO result = new AiImageAnalysisDTO();
        if (StrUtil.isBlank(apiKey) || StrUtil.isBlank(imageUrl)) {
            log.debug("AI 分析跳过：apiKey 或 imageUrl 为空");
            return result;
        }
        try {
            String responseBody = callChatCompletions(imageUrl);
            if (StrUtil.isNotBlank(responseBody)) {
                parseAndFill(result, responseBody);
            }
        } catch (Exception e) {
            log.error("AI 图片分析失败: {}", e.getMessage());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private String callChatCompletions(String imageUrl) {
        Map<String, Object> userContent = Map.of(
                "type", "text",
                "text", USER_PROMPT
        );
        Map<String, Object> imageContent = Map.of(
                "type", "image_url",
                "image_url", Map.of("url", imageUrl)
        );

        Map<String, Object> body = Map.of(
                "model", model,
                "stream", false,
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", List.of(userContent, imageContent))
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Accept", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(JSONUtil.toJsonStr(body), headers);
        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);

        if (response.getBody() == null) return null;
        List<?> choices = (List<?>) response.getBody().get("choices");
        if (choices == null || choices.isEmpty()) return null;
        Map<?, ?> choice = (Map<?, ?>) choices.get(0);
        Map<?, ?> message = (Map<?, ?>) choice.get("message");
        if (message == null) return null;
        Object content = message.get("content");
        return content != null ? content.toString() : null;
    }

    private void parseAndFill(AiImageAnalysisDTO dto, String jsonStr) {
        try {
            // 清理可能的 markdown 代码块
            String clean = jsonStr.trim();
            if (clean.startsWith("```")) {
                int start = clean.indexOf("\n") + 1;
                int end = clean.lastIndexOf("```");
                clean = end > start ? clean.substring(start, end) : clean.substring(start);
            }
            JSONObject obj = JSONUtil.parseObj(clean);
            dto.setDescription(obj.getStr("description"));
            dto.setTags(obj.getStr("tags"));
            dto.setScene(obj.getStr("scene"));
        } catch (Exception e) {
            log.debug("解析 AI 返回 JSON 失败，使用原始文本: {}", e.getMessage());
            dto.setDescription(jsonStr);
        }
    }
}
