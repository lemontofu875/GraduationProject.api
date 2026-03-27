package com.youlai.boot.platform.album.model.dto;

import lombok.Data;

/**
 * AI 照片点评结果 DTO
 */
@Data
public class PhotoReviewAnalysisDTO {

    /** 照片概述 */
    private String summary;

    /** 优点 */
    private String advantages;

    /** 缺点 */
    private String disadvantages;

    /** 照片评级：杰出、优秀、良好、有待改进、烂片一张 */
    private String rating;
}
