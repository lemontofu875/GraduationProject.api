package com.youlai.boot.platform.album.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.youlai.boot.platform.album.model.dto.AiImageAnalysisDTO;
import com.youlai.boot.platform.album.model.dto.PhotoReviewAnalysisDTO;
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
import java.util.Set;

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

    @Value("${album.ai.api-key:sk-aQivmCBTQneYzMygvIhrphKQkaCjEMFvYtzfJgpsN7xbs4Zr}")
    private String apiKey;

    @Value("${album.ai.model:gpt-4o}")
    private String model;

    private static final String SYSTEM_PROMPT = "你是一个专业的图片分析助手。请分析用户提供的图片，以JSON格式返回，包含以下字段：" +
            "description(详细描述图片内容，如风景、人物、天气、颜色、标志性建筑等；如果是cosplay照片请识别出cos的角色；如果照片里面有标志性建筑，如广州塔、黄鹤楼也请描述一下)、" +
            "tags(用逗号分隔的标签（每张照片最多返回5个，这5个都是主要标签），如：海边,沙滩,小狗,蓝天,夜景等)、" +
            "scene(场景分类，如：风景、人像、美食、建筑、动物等)。只返回JSON，不要其他文字注意用中文，。";

    private static final String USER_PROMPT = "请分析这张图片，返回JSON格式：{\"description\":\"...\",\"tags\":\"...\",\"scene\":\"...\"}";

    private static final String REVIEW_SYSTEM_PROMPT = "你是一名专业摄影点评助手。请基于照片内容进行详细点评（注意不要过于鸡蛋里挑骨头），仅返回JSON，包含字段：" +
            "summary(照片概述)、advantages(优点)、disadvantages(缺点)、rating(照片评级)。" +
            "rating 必须且只能是以下五个值之一：杰出、优秀、良好、有待改进、烂片一张。请使用中文。";

    private static final String REVIEW_USER_PROMPT = "请点评这张照片，返回JSON格式：{\"summary\":\"...\",\"advantages\":\"...\",\"disadvantages\":\"...\",\"rating\":\"杰出|优秀|良好|有待改进|烂片一张\"}";

    private static final Set<String> ALLOWED_RATINGS = Set.of("杰出", "优秀", "良好", "有待改进", "烂片一张");

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
            String responseBody = callChatCompletions(imageUrl, imageBytes, mimeType, SYSTEM_PROMPT, USER_PROMPT);
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

    @Override
    public PhotoReviewAnalysisDTO analyzePhotoReview(String imageUrl, byte[] imageBytes, String mimeType) {
        PhotoReviewAnalysisDTO result = new PhotoReviewAnalysisDTO();
        if (StrUtil.isBlank(apiKey)) {
            log.warn("AI 点评未执行：未配置 album.ai.api-key 或环境变量 ALBUM_AI_API_KEY");
            return result;
        }
        boolean useBase64 = isLocalOrPrivateUrl(imageUrl) && imageBytes != null && imageBytes.length > 0;
        if (!useBase64 && StrUtil.isBlank(imageUrl)) {
            log.warn("AI 点评未执行：imageUrl 为空且无 imageBytes");
            return result;
        }
        if (useBase64) {
            log.info("检测到本地/内网图片地址，将使用 base64 发送图片给大模型（摄影点评）");
        }
        try {
            String responseBody = callChatCompletions(imageUrl, imageBytes, mimeType, REVIEW_SYSTEM_PROMPT, REVIEW_USER_PROMPT);
            if (StrUtil.isNotBlank(responseBody)) {
                parseAndFillReview(result, responseBody);
            } else {
                log.warn("AI 点评返回内容为空，请检查接口与模型是否支持视觉能力");
            }
        } catch (Exception e) {
            log.error("AI 照片点评失败: {}", e.getMessage(), e);
        }
        result.setRating(normalizeRating(result.getRating()));
        return result;
    }

    /** 判断是否为本地或内网地址（云端无法访问） */
    private boolean isLocalOrPrivateUrl(String url) {
        if (StrUtil.isBlank(url)) return true;
        String lower = url.toLowerCase();
        return lower.contains("localhost") || lower.contains("127.0.0.1") || lower.startsWith("http://192.168.") || lower.startsWith("http://10.");
    }

    @SuppressWarnings("unchecked")
    private String callChatCompletions(String imageUrl, byte[] imageBytes, String mimeType, String systemPrompt, String userPrompt) {
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
                "text", userPrompt
        );
        Map<String, Object> imageContent = Map.of(
                "type", "image_url",
                "image_url", Map.of("url", imageInput)
        );

        Map<String, Object> body = Map.of(
                "model", model,
                "stream", false,
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
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

    private void parseAndFillReview(PhotoReviewAnalysisDTO dto, String jsonStr) {
        try {
            String clean = jsonStr.trim();
            if (clean.startsWith("```")) {
                int start = clean.indexOf("\n") + 1;
                int end = clean.lastIndexOf("```");
                clean = end > start ? clean.substring(start, end) : clean.substring(start);
            }
            JSONObject obj = JSONUtil.parseObj(clean);
            dto.setSummary(obj.getStr("summary"));
            dto.setAdvantages(obj.getStr("advantages"));
            dto.setDisadvantages(obj.getStr("disadvantages"));
            dto.setRating(obj.getStr("rating"));
        } catch (Exception e) {
            log.debug("解析 AI 点评 JSON 失败，使用原始文本: {}", e.getMessage());
            dto.setSummary(jsonStr);
        }
    }

    private String normalizeRating(String rating) {
        if (StrUtil.isBlank(rating)) {
            return "有待改进";
        }
        String trimmed = rating.trim();
        if (ALLOWED_RATINGS.contains(trimmed)) {
            return trimmed;
        }
        return "有待改进";
    }
}
