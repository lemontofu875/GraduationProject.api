package com.youlai.boot.platform.album.service;

import com.youlai.boot.platform.file.model.FileInfo;

/**
 * 图片缩略图（WebP）生成与上传
 */
public interface ImageThumbnailService {

    /**
     * 根据原图字节生成 WebP 缩略图并上传；失败返回 null（不影响主流程）
     *
     * @param originalBytes     原图字节
     * @param originalFileInfo  原图上传结果（用于推导缩略图路径）
     * @return 缩略图文件信息
     */
    FileInfo uploadWebpThumbnail(byte[] originalBytes, FileInfo originalFileInfo);
}
