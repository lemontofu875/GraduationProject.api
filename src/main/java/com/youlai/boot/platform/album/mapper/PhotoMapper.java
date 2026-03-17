package com.youlai.boot.platform.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.platform.album.model.entity.Photo;
import com.youlai.boot.platform.album.model.query.PhotoPageQuery;
import com.youlai.boot.platform.album.model.vo.PhotoPageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 照片 Mapper
 *
 * @author youlai
 */
@Mapper
public interface PhotoMapper extends BaseMapper<Photo> {

    /**
     * 分页查询照片（支持拍摄时间、AI描述/标签/场景、用户备注、是否收藏、相册名称筛选）
     *
     * @param page        分页对象
     * @param queryParams 查询条件，为空或不传筛选项时返回所有照片
     * @return 照片分页数据
     */
    Page<PhotoPageVO> getPhotoPage(Page<PhotoPageVO> page, @Param("queryParams") PhotoPageQuery queryParams);

    /**
     * 获取场景分类列表（去重）
     *
     * @return 场景分类列表
     */
    List<String> listAiScenes();
}
