package com.youlai.boot.platform.album.service;

import com.youlai.boot.platform.album.model.vo.PhotoUploadVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 照片服务
 *
 * @author youlai
 */
public interface PhotoService {

    /**
     * 上传照片：上传文件、解析 EXIF、调用 AI 分析、存入数据库
     *
     * @param file        照片文件 (jpg/png/webp)
     * @param albumId     所属相册 ID
     * @param description 用户备注（可选）
     * @return 上传结果
     */
    PhotoUploadVO uploadPhoto(MultipartFile file, Long albumId, String description);
}
