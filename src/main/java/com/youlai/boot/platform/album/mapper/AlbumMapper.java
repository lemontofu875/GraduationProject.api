package com.youlai.boot.platform.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youlai.boot.platform.album.model.entity.Album;
import org.apache.ibatis.annotations.Mapper;

/**
 * 相册 Mapper
 *
 * @author youlai
 */
@Mapper
public interface AlbumMapper extends BaseMapper<Album> {
}
