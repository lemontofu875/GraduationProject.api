package com.youlai.boot.platform.album.service.impl;

import cn.hutool.core.util.StrUtil;
import com.youlai.boot.core.exception.BusinessException;
import com.youlai.boot.core.web.ResultCode;
import com.youlai.boot.platform.album.model.dto.AiImageAnalysisDTO;
import com.youlai.boot.platform.album.model.entity.Album;
import com.youlai.boot.platform.album.model.entity.Photo;
import com.youlai.boot.platform.album.model.vo.ExifInfoVO;
import com.youlai.boot.platform.album.model.vo.PhotoUploadVO;
import com.youlai.boot.platform.album.service.AiImageAnalysisService;
import com.youlai.boot.platform.album.service.ExifParseService;
import com.youlai.boot.platform.album.service.PhotoService;
import com.youlai.boot.platform.album.mapper.AlbumMapper;
import com.youlai.boot.platform.album.mapper.PhotoMapper;
import com.youlai.boot.platform.file.model.FileInfo;
import com.youlai.boot.platform.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

/**
 * 照片服务实现
 *
 * @author youlai
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");

    private final FileService fileService;
    private final ExifParseService exifParseService;
    private final AiImageAnalysisService aiImageAnalysisService;
    private final AlbumMapper albumMapper;
    private final PhotoMapper photoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PhotoUploadVO uploadPhoto(MultipartFile file, Long albumId, String description) {
        // 1. 校验参数
        validateParams(file, albumId);

        // 2. 上传文件到 OSS
        FileInfo fileInfo = fileService.uploadFile(file);

        // 3. 解析 EXIF
        ExifInfoVO exifInfo = exifParseService.parseExif(file);

        // 4. 调用 AI 分析图片内容（传入文件字节：本地/内网 URL 时以 base64 发送，否则大模型无法访问）
        byte[] fileBytes;
        try {
            fileBytes = file.getBytes();
        } catch (Exception e) {
            log.warn("读取文件字节失败，AI 将仅使用 URL: {}", e.getMessage());
            fileBytes = null;
        }
        String mimeType = file.getContentType();
        AiImageAnalysisDTO aiResult = aiImageAnalysisService.analyzeImage(fileInfo.getUrl(), fileBytes, mimeType);

        // 5. 持久化到数据库
        Photo photo = new Photo();
        photo.setAlbumId(albumId);
        photo.setOriginalName(file.getOriginalFilename());
        photo.setFilePath(fileInfo.getPath());
        photo.setFileUrl(fileInfo.getUrl());
        photo.setFileSize(file.getSize());
        photo.setExifInfo(exifInfo);
        photo.setAiDescription(aiResult.getDescription());
        photo.setAiTags(aiResult.getTags());
        photo.setAiScene(aiResult.getScene());
        photo.setDescription(StrUtil.isNotBlank(description) ? description : null);
        photo.setIsFavorite(false);

        photoMapper.insert(photo);

        // 6. 构建返回 VO
        return buildPhotoUploadVO(photo);
    }

    private void validateParams(MultipartFile file, Long albumId) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.REQUEST_REQUIRED_PARAMETER_IS_EMPTY, "照片文件不能为空");
        }
        if (albumId == null) {
            throw new BusinessException(ResultCode.REQUEST_REQUIRED_PARAMETER_IS_EMPTY, "相册ID不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (StrUtil.isBlank(originalFilename)) {
            throw new BusinessException(ResultCode.UPLOAD_FILE_TYPE_MISMATCH, "不支持的文件类型");
        }
        String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new BusinessException(ResultCode.UPLOAD_FILE_TYPE_MISMATCH, "仅支持 jpg/png/webp 格式");
        }

        Album album = albumMapper.selectById(albumId);
        if (album == null) {
            throw new BusinessException(ResultCode.USER_RESOURCE_NOT_FOUND, "相册不存在");
        }
    }

    private PhotoUploadVO buildPhotoUploadVO(Photo photo) {
        PhotoUploadVO vo = new PhotoUploadVO();
        vo.setId(photo.getId());
        vo.setOriginalName(photo.getOriginalName());
        vo.setFilePath(photo.getFilePath());
        vo.setFileSize(photo.getFileSize());
        vo.setExifInfo(photo.getExifInfo());
        vo.setAiDescription(photo.getAiDescription());
        vo.setAiTags(photo.getAiTags());
        vo.setAiScene(photo.getAiScene());
        vo.setIsFavorite(photo.getIsFavorite());
        vo.setUploadTime(photo.getCreateTime());
        return vo;
    }
}
