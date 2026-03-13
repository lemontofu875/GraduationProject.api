package com.youlai.boot.platform.album.service.impl;

import com.youlai.boot.platform.album.model.entity.Album;
import com.youlai.boot.platform.album.model.form.AlbumForm;
import com.youlai.boot.platform.album.model.vo.AlbumOptionVO;
import com.youlai.boot.platform.album.model.vo.AlbumVO;
import com.youlai.boot.platform.album.mapper.AlbumMapper;
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
