package com.youlai.boot.platform.album.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youlai.boot.platform.album.model.query.RecyclePhotoPageQuery;
import com.youlai.boot.platform.album.model.vo.RecyclePhotoDetailVO;
import com.youlai.boot.platform.album.model.vo.RecyclePhotoPageVO;

/**
 * 回收站照片服务
 *
 * @author youlai
 */
public interface RecyclePhotoService {

    /**
     * 回收站照片分页
     *
     * @param queryParams 查询参数
     * @return 分页数据
     */
    IPage<RecyclePhotoPageVO> getRecyclePhotoPage(RecyclePhotoPageQuery queryParams);

    /**
     * 回收站照片详情
     *
     * @param id 回收站记录ID
     * @return 详情
     */
    RecyclePhotoDetailVO getRecyclePhotoDetail(Long id);

    /**
     * 删除单张回收站照片
     *
     * @param id 回收站记录ID
     * @return 是否成功
     */
    boolean deleteRecyclePhoto(Long id);

    /**
     * 还原照片到原相册
     *
     * @param id 回收站记录ID
     * @return 还原结果提示文案
     */
    String restorePhoto(Long id);

    /**
     * 清空回收站
     *
     * @return 删除数量
     */
    int clearRecycleBin();
}
