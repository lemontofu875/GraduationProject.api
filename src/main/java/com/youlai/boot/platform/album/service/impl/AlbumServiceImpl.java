package com.youlai.boot.platform.album.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youlai.boot.core.exception.BusinessException;
import com.youlai.boot.core.web.ResultCode;
import com.youlai.boot.platform.album.model.entity.Album;
import com.youlai.boot.platform.album.model.entity.Photo;
import com.youlai.boot.platform.album.model.form.AlbumForm;
import com.youlai.boot.platform.album.model.vo.AlbumOptionVO;
import com.youlai.boot.platform.album.model.vo.AlbumVO;
import com.youlai.boot.platform.album.mapper.AlbumMapper;
import com.youlai.boot.platform.album.mapper.PhotoMapper;
import com.youlai.boot.platform.album.service.AlbumService;
import com.youlai.boot.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 相册服务实现
 *
 * @author youlai
 */
@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumMapper albumMapper;
    private final PhotoMapper photoMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AlbumVO createAlbum(AlbumForm form) {
        Album entity = new Album();
        entity.setName(form.getName().trim());
        Long userId = SecurityUtils.getUserId();
        entity.setUserId(userId);

        albumMapper.insert(entity);
        return toVO(entity);
    }

    @Override
    public List<AlbumOptionVO> listAlbumOptions() {
        List<Album> list = albumMapper.selectList(null);
        return list.stream()
                .map(a -> new AlbumOptionVO(a.getId(), a.getName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AlbumVO updateAlbum(Long id, AlbumForm form) {
        Album album = albumMapper.selectById(id);
        if (album == null) {
            throw new BusinessException(ResultCode.USER_RESOURCE_NOT_FOUND, "相册不存在");
        }
        album.setName(form.getName().trim());
        albumMapper.updateById(album);
        return toVO(album);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAlbum(Long id) {
        Album album = albumMapper.selectById(id);
        if (album == null) {
            throw new BusinessException(ResultCode.USER_RESOURCE_NOT_FOUND, "相册不存在");
        }
        long count = photoMapper.selectCount(new LambdaQueryWrapper<Photo>().eq(Photo::getAlbumId, id));
        if (count > 0) {
            throw new BusinessException(ResultCode.USER_OPERATION_EXCEPTION, "相册下存在照片，无法删除");
        }
        return albumMapper.deleteById(id) > 0;
    }

    private AlbumVO toVO(Album entity) {
        AlbumVO vo = new AlbumVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setUserId(entity.getUserId());
        vo.setCoverUrl(entity.getCoverUrl());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
