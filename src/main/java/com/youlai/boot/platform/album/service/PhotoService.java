package com.youlai.boot.platform.album.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlai.boot.platform.album.model.query.PhotoPageQuery;
import com.youlai.boot.platform.album.model.vo.PhotoPageVO;
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

    /**
     * 更新照片收藏状态
     *
     * @param photoId   照片ID
     * @param isFavorite 是否收藏
     */
    void updateFavorite(Long photoId, Boolean isFavorite);

    /**
     * 分页查询照片，支持多条件筛选；不传筛选项时返回所有照片
     *
     * @param queryParams 分页及筛选项：拍摄时间、AI描述/标签/场景、用户备注、是否收藏、相册名称
     * @return 照片分页列表（含相册名称及完整照片信息）
     */
    IPage<PhotoPageVO> getPhotoPage(PhotoPageQuery queryParams);

    /**
     * 删除照片
     *
     * @param id 照片ID
     * @return 是否删除成功
     */
    boolean deletePhoto(Long id);
}
