package com.youlai.boot.platform.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.boot.platform.album.model.entity.Photo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 照片 Mapper
 *
 * @author youlai
 */
@Mapper
public interface PhotoMapper extends BaseMapper<Photo> {
}
