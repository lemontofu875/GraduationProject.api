package com.youlai.boot.platform.file.service;

import com.youlai.boot.platform.file.model.FileInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 对象存储服务接口层
 *
 * @author haoxr
 * @since 2022/11/19
 */
public interface FileService {

    /**
     * 上传文件
     * @param file 表单文件对象
     * @return 文件信息
     */
    FileInfo uploadFile(MultipartFile file);

    /**
     * 上传字节数据（如缩略图），路径规则与 {@link #uploadFile} 一致，由调用方指定相对路径（如 20240312/xxx_thumb.webp）
     *
     * @param data         文件字节
     * @param contentType  MIME 类型
     * @param relativePath 相对存储根目录的路径，使用正斜杠
     * @return 文件信息
     */
    FileInfo uploadBytes(byte[] data, String contentType, String relativePath);

    /**
     * 删除文件
     *
     * @param filePath 文件完整URL
     * @return 删除结果
     */
    boolean deleteFile(String filePath);


}
