package com.youlai.boot.platform.album.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youlai.boot.core.exception.BusinessException;
import com.youlai.boot.core.web.ResultCode;
import com.youlai.boot.platform.album.mapper.AlbumMapper;
import com.youlai.boot.platform.album.mapper.PhotoMapper;
import com.youlai.boot.platform.album.mapper.RecyclePhotoMapper;
import com.youlai.boot.platform.album.model.entity.Album;
import com.youlai.boot.platform.album.model.entity.Photo;
import com.youlai.boot.platform.album.model.entity.RecyclePhoto;
import com.youlai.boot.platform.album.model.query.RecyclePhotoPageQuery;
import com.youlai.boot.platform.album.model.vo.RecyclePhotoDetailVO;
import com.youlai.boot.platform.album.model.vo.RecyclePhotoPageVO;
import com.youlai.boot.platform.album.service.RecyclePhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 回收站照片服务实现
 *
 * @author youlai
 */
@Service
@RequiredArgsConstructor
public class RecyclePhotoServiceImpl implements RecyclePhotoService {

    private static final String DEFAULT_ALBUM_NAME = "默认相册";

    private final RecyclePhotoMapper recyclePhotoMapper;
    private final PhotoMapper photoMapper;
    private final AlbumMapper albumMapper;

    @Override
    public IPage<RecyclePhotoPageVO> getRecyclePhotoPage(RecyclePhotoPageQuery queryParams) {
        Page<RecyclePhotoPageVO> page = new Page<>(queryParams.getPageNum(), queryParams.getPageSize());
        return recyclePhotoMapper.getRecyclePhotoPage(page, queryParams);
    }

    @Override
    public RecyclePhotoDetailVO getRecyclePhotoDetail(Long id) {
        RecyclePhotoDetailVO detail = recyclePhotoMapper.getRecyclePhotoDetail(id);
        if (detail == null) {
            throw new BusinessException(ResultCode.USER_RESOURCE_NOT_FOUND, "回收站照片不存在");
        }
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRecyclePhoto(Long id) {
        RecyclePhoto recyclePhoto = recyclePhotoMapper.selectById(id);
        if (recyclePhoto == null) {
            throw new BusinessException(ResultCode.USER_RESOURCE_NOT_FOUND, "回收站照片不存在");
        }
        return recyclePhotoMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String restorePhoto(Long id) {
        RecyclePhoto recyclePhoto = recyclePhotoMapper.selectById(id);
        if (recyclePhoto == null) {
            throw new BusinessException(ResultCode.USER_RESOURCE_NOT_FOUND, "回收站照片不存在");
        }
        boolean originAlbumExists = albumMapper.selectById(recyclePhoto.getAlbumId()) != null;
        Long restoreAlbumId = resolveRestoreAlbumId(recyclePhoto.getAlbumId());

        Photo photo = new Photo();
        photo.setAlbumId(restoreAlbumId);
        photo.setOriginalName(recyclePhoto.getOriginalName());
        photo.setFilePath(recyclePhoto.getFilePath());
        photo.setFileUrl(recyclePhoto.getFileUrl());
        photo.setThumbPath(recyclePhoto.getThumbPath());
        photo.setThumbUrl(recyclePhoto.getThumbUrl());
        photo.setFileSize(recyclePhoto.getFileSize());
        photo.setExifInfo(recyclePhoto.getExifInfo());
        photo.setAiDescription(recyclePhoto.getAiDescription());
        photo.setAiTags(recyclePhoto.getAiTags());
        photo.setAiScene(recyclePhoto.getAiScene());
        photo.setDescription(recyclePhoto.getDescription());
        photo.setIsFavorite(recyclePhoto.getIsFavorite());
        photoMapper.insert(photo);
        recyclePhotoMapper.deleteById(id);
        if (originAlbumExists) {
            return "还原成功";
        }
        return "原相册不存在，已还原到默认相册";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int clearRecycleBin() {
        return recyclePhotoMapper.delete(new LambdaQueryWrapper<>());
    }

    /**
     * 解析还原相册ID：
     * 1. 原相册存在则还原到原相册；
     * 2. 原相册不存在则还原到“默认相册”；
     * 3. 默认相册也不存在则自动创建“默认相册”。
     */
    private Long resolveRestoreAlbumId(Long originAlbumId) {
        Album originAlbum = albumMapper.selectById(originAlbumId);
        if (originAlbum != null) {
            return originAlbum.getId();
        }
        Album defaultAlbum = albumMapper.selectOne(new LambdaQueryWrapper<Album>()
                .eq(Album::getName, DEFAULT_ALBUM_NAME)
                .last("LIMIT 1"));
        if (defaultAlbum != null) {
            return defaultAlbum.getId();
        }
        Album newDefaultAlbum = new Album();
        newDefaultAlbum.setName(DEFAULT_ALBUM_NAME);
        albumMapper.insert(newDefaultAlbum);
        return newDefaultAlbum.getId();
    }
}
