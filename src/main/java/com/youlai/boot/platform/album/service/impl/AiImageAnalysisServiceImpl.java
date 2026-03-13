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

import java.util.Base64;
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
            "scene(场景分类，如：风景、人像、美食、建筑、动物等)。只返回JSON，不要其他文字注意用中文。";

    private static final String USER_PROMPT = "请分析这张图片，返回JSON格式：{\"description\":\"...\",\"tags\":\"...\",\"scene\":\"...\"}";

    @Override
    public AiImageAnalysisDTO analyzeImage(String imageUrl, byte[] imageBytes, String mimeType) {
        AiImageAnalysisDTO result = new AiImageAnalysisDTO();
        if (StrUtil.isBlank(apiKey)) {
            log.warn("AI 分析未执行：未配置 album.ai.api-key 或环境变量 ALBUM_AI_API_KEY");
            return result;
        }
        // 本地/内网 URL 云端无法访问，必须用 base64
        boolean useBase64 = isLocalOrPrivateUrl(imageUrl) && imageBytes != null && imageBytes.length > 0;
        if (!useBase64 && StrUtil.isBlank(imageUrl)) {
            log.warn("AI 分析未执行：imageUrl 为空且无 imageBytes");
            return result;
        }
        if (useBase64) {
            log.info("检测到本地/内网图片地址，将使用 base64 发送图片给大模型");
        }
        try {
            String responseBody = callChatCompletions(imageUrl, imageBytes, mimeType);
            if (StrUtil.isNotBlank(responseBody)) {
                parseAndFill(result, responseBody);
            } else {
                log.warn("AI 返回内容为空，请检查接口与模型是否支持视觉能力");
            }
        } catch (Exception e) {
            log.error("AI 图片分析失败: {}", e.getMessage(), e);
        }
        return result;
    }

    /** 判断是否为本地或内网地址（云端无法访问） */
    private boolean isLocalOrPrivateUrl(String url) {
        if (StrUtil.isBlank(url)) return true;
        String lower = url.toLowerCase();
        return lower.contains("localhost") || lower.contains("127.0.0.1") || lower.startsWith("http://192.168.") || lower.startsWith("http://10.");
    }

    @SuppressWarnings("unchecked")
    private String callChatCompletions(String imageUrl, byte[] imageBytes, String mimeType) {
        String imageInput;
        if (imageBytes != null && imageBytes.length > 0 && isLocalOrPrivateUrl(imageUrl)) {
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            String type = StrUtil.isNotBlank(mimeType) ? mimeType : "image/jpeg";
            imageInput = "data:" + type + ";base64," + base64;
        } else {
            imageInput = imageUrl;
        }

        Map<String, Object> userContent = Map.of(
                "type", "text",
                "text", USER_PROMPT
        );
        Map<String, Object> imageContent = Map.of(
                "type", "image_url",
                "image_url", Map.of("url", imageInput)
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

        Map<?, ?> bodyMap = response.getBody();
        if (bodyMap == null) return null;
        // 部分接口错误时仍返回 200，在 body 里放 error
        if (bodyMap.containsKey("error")) {
            Object err = bodyMap.get("error");
            log.warn("AI 接口返回错误: {}", err);
            return null;
        }
        List<?> choices = (List<?>) bodyMap.get("choices");
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
