package com.youlai.boot.platform.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.platform.album.model.entity.RecyclePhoto;
import com.youlai.boot.platform.album.model.query.RecyclePhotoPageQuery;
import com.youlai.boot.platform.album.model.vo.RecyclePhotoDetailVO;
import com.youlai.boot.platform.album.model.vo.RecyclePhotoPageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 回收站照片 Mapper
 *
 * @author youlai
 */
@Mapper
public interface RecyclePhotoMapper extends BaseMapper<RecyclePhoto> {

    /**
     * 回收站照片分页
     *
     * @param page        分页对象
     * @param queryParams 查询参数
     * @return 分页结果
     */
    Page<RecyclePhotoPageVO> getRecyclePhotoPage(Page<RecyclePhotoPageVO> page, @Param("queryParams") RecyclePhotoPageQuery queryParams);

    /**
     * 回收站照片详情
     *
     * @param id 回收站记录ID
     * @return 详情
     */
    RecyclePhotoDetailVO getRecyclePhotoDetail(@Param("id") Long id);
}
