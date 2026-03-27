package com.youlai.boot.platform.album.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.core.exception.BusinessException;
import com.youlai.boot.core.web.ResultCode;
import com.youlai.boot.platform.album.mapper.PhotoReviewMapper;
import com.youlai.boot.platform.album.model.dto.PhotoReviewAnalysisDTO;
import com.youlai.boot.platform.album.model.entity.PhotoReview;
import com.youlai.boot.platform.album.model.query.PhotoReviewPageQuery;
import com.youlai.boot.platform.album.model.vo.ExifInfoVO;
import com.youlai.boot.platform.album.model.vo.PhotoReviewDetailVO;
import com.youlai.boot.platform.album.model.vo.PhotoReviewPageVO;
import com.youlai.boot.platform.album.model.vo.PhotoReviewUploadVO;
import com.youlai.boot.platform.album.service.AiImageAnalysisService;
import com.youlai.boot.platform.album.service.ExifParseService;
import com.youlai.boot.platform.album.service.PhotoReviewService;
import com.youlai.boot.platform.file.model.FileInfo;
import com.youlai.boot.platform.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 照片点评服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoReviewServiceImpl implements PhotoReviewService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");

    private final FileService fileService;
    private final ExifParseService exifParseService;
    private final AiImageAnalysisService aiImageAnalysisService;
    private final PhotoReviewMapper photoReviewMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PhotoReviewUploadVO uploadPhotoReview(MultipartFile file) {
        validateParams(file);

        // 1. 上传文件
        FileInfo fileInfo = fileService.uploadFile(file);

        // 2. 解析 EXIF
        ExifInfoVO exifInfo = exifParseService.parseExif(file);

        // 3. 调用 AI 摄影点评
        byte[] fileBytes;
        try {
            fileBytes = file.getBytes();
        } catch (Exception e) {
            log.warn("读取文件字节失败，AI 点评将仅使用 URL: {}", e.getMessage());
            fileBytes = null;
        }
        String mimeType = file.getContentType();
        PhotoReviewAnalysisDTO reviewResult = aiImageAnalysisService.analyzePhotoReview(fileInfo.getUrl(), fileBytes, mimeType);

        // 4. 入库
        LocalDateTime reviewTime = LocalDateTime.now();
        PhotoReview review = new PhotoReview();
        review.setOriginalName(file.getOriginalFilename());
        review.setFilePath(fileInfo.getPath());
        review.setFileUrl(fileInfo.getUrl());
        review.setFileSize(file.getSize());
        review.setExifInfo(exifInfo);
        review.setReviewSummary(StrUtil.isBlank(reviewResult.getSummary()) ? null : reviewResult.getSummary().trim());
        review.setReviewAdvantages(StrUtil.isBlank(reviewResult.getAdvantages()) ? null : reviewResult.getAdvantages().trim());
        review.setReviewDisadvantages(StrUtil.isBlank(reviewResult.getDisadvantages()) ? null : reviewResult.getDisadvantages().trim());
        review.setReviewRating(StrUtil.isBlank(reviewResult.getRating()) ? "有待改进" : reviewResult.getRating().trim());
        review.setReviewTime(reviewTime);
        photoReviewMapper.insert(review);

        return buildUploadVO(review);
    }

    @Override
    public IPage<PhotoReviewPageVO> getPhotoReviewPage(PhotoReviewPageQuery queryParams) {
        Page<PhotoReviewPageVO> page = new Page<>(queryParams.getPageNum(), queryParams.getPageSize());
        return photoReviewMapper.getPhotoReviewPage(page, queryParams);
    }

    @Override
    public PhotoReviewDetailVO getPhotoReviewDetail(Long id) {
        PhotoReviewDetailVO detail = photoReviewMapper.getPhotoReviewDetail(id);
        if (detail == null) {
            throw new BusinessException(ResultCode.USER_RESOURCE_NOT_FOUND, "照片点评记录不存在");
        }
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePhotoReview(Long id) {
        PhotoReview review = photoReviewMapper.selectById(id);
        if (review == null) {
            throw new BusinessException(ResultCode.USER_RESOURCE_NOT_FOUND, "照片点评记录不存在");
        }
        return photoReviewMapper.deleteById(id) > 0;
    }

    private void validateParams(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.REQUEST_REQUIRED_PARAMETER_IS_EMPTY, "照片文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (StrUtil.isBlank(originalFilename)) {
            throw new BusinessException(ResultCode.UPLOAD_FILE_TYPE_MISMATCH, "不支持的文件类型");
        }

        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex < 0 || dotIndex == originalFilename.length() - 1) {
            throw new BusinessException(ResultCode.UPLOAD_FILE_TYPE_MISMATCH, "仅支持 jpg/png/webp 格式");
        }
        String ext = originalFilename.substring(dotIndex + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new BusinessException(ResultCode.UPLOAD_FILE_TYPE_MISMATCH, "仅支持 jpg/png/webp 格式");
        }
    }

    private PhotoReviewUploadVO buildUploadVO(PhotoReview review) {
        PhotoReviewUploadVO vo = new PhotoReviewUploadVO();
        vo.setId(review.getId());
        vo.setOriginalName(review.getOriginalName());
        vo.setFileUrl(review.getFileUrl());
        vo.setFileSize(review.getFileSize());
        vo.setReviewSummary(review.getReviewSummary());
        vo.setReviewAdvantages(review.getReviewAdvantages());
        vo.setReviewDisadvantages(review.getReviewDisadvantages());
        vo.setReviewRating(review.getReviewRating());
        vo.setReviewTime(review.getReviewTime());
        return vo;
    }
}
