package com.youlai.boot.platform.album.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlai.boot.platform.album.model.query.PhotoReviewPageQuery;
import com.youlai.boot.platform.album.model.vo.PhotoReviewDetailVO;
import com.youlai.boot.platform.album.model.vo.PhotoReviewPageVO;
import com.youlai.boot.platform.album.model.vo.PhotoReviewUploadVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 照片点评服务
 */
public interface PhotoReviewService {

    /**
     * 上传照片并生成 AI 点评
     *
     * @param file 照片文件
     * @return 点评结果
     */
    PhotoReviewUploadVO uploadPhotoReview(MultipartFile file);

    /**
     * 照片点评分页
     *
     * @param queryParams 查询参数
     * @return 分页结果
     */
    IPage<PhotoReviewPageVO> getPhotoReviewPage(PhotoReviewPageQuery queryParams);

    /**
     * 照片点评详情
     *
     * @param id 点评记录ID
     * @return 详情
     */
    PhotoReviewDetailVO getPhotoReviewDetail(Long id);

    /**
     * 删除单条点评记录
     *
     * @param id 点评记录ID
     * @return 是否成功
     */
    boolean deletePhotoReview(Long id);
}
