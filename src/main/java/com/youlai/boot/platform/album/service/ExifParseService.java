package com.youlai.boot.platform.album.service;

import com.youlai.boot.platform.album.model.vo.ExifInfoVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * EXIF 信息解析服务
 *
 * @author youlai
 */
public interface ExifParseService {

    /**
     * 从照片中解析 EXIF 信息
     *
     * @param file 照片文件
     * @return EXIF 信息，无法解析时返回空对象
     */
    ExifInfoVO parseExif(MultipartFile file);
}
