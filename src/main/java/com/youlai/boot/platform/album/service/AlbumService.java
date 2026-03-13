package com.youlai.boot.platform.album.service;

import com.youlai.boot.platform.album.model.form.AlbumForm;
import com.youlai.boot.platform.album.model.vo.AlbumOptionVO;
import com.youlai.boot.platform.album.model.vo.AlbumVO;

import java.util.List;

/**
 * 相册服务
 *
 * @author youlai
 */
public interface AlbumService {

    /**
     * 新建相册
     *
     * @param form 相册表单（名称等）
     * @return 新建的相册信息
     */
    AlbumVO createAlbum(AlbumForm form);

    /**
     * 获取所有相册列表（仅 id、名称），用于前端下拉框等
     *
     * @return 相册选项列表
     */
    List<AlbumOptionVO> listAlbumOptions();

    /**
     * 删除相册
     *
     * @param id 相册ID
     * @return 是否删除成功
     */
    boolean deleteAlbum(Long id);
}
