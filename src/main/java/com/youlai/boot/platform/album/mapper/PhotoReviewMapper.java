package com.youlai.boot.platform.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.platform.album.model.entity.PhotoReview;
import com.youlai.boot.platform.album.model.query.PhotoReviewPageQuery;
import com.youlai.boot.platform.album.model.vo.PhotoReviewDetailVO;
import com.youlai.boot.platform.album.model.vo.PhotoReviewPageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 照片点评 Mapper
 */
@Mapper
public interface PhotoReviewMapper extends BaseMapper<PhotoReview> {

    /**
     * 照片点评分页
     *
     * @param page        分页对象
     * @param queryParams 查询参数
     * @return 分页结果
     */
    Page<PhotoReviewPageVO> getPhotoReviewPage(Page<PhotoReviewPageVO> page, @Param("queryParams") PhotoReviewPageQuery queryParams);

    /**
     * 照片点评详情
     *
     * @param id 点评记录ID
     * @return 详情
     */
    PhotoReviewDetailVO getPhotoReviewDetail(@Param("id") Long id);
}
