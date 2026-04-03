package com.youlai.boot.platform.album.service.impl;

import com.youlai.boot.platform.album.service.ImageThumbnailService;
import com.youlai.boot.platform.file.model.FileInfo;
import com.youlai.boot.platform.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * 生成 WebP 缩略图并上传；若当前环境不支持 WebP 写出则回退为 JPEG
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageThumbnailServiceImpl implements ImageThumbnailService {

    private final FileService fileService;

    @Value("${album.thumb.max-edge:800}")
    private int maxEdge;

    static {
        ImageIO.scanForPlugins();
    }

    @Override
    public FileInfo uploadWebpThumbnail(byte[] originalBytes, FileInfo originalFileInfo) {
        if (originalBytes == null || originalBytes.length == 0 || originalFileInfo == null
                || originalFileInfo.getPath() == null || originalFileInfo.getPath().isEmpty()) {
            return null;
        }
        try {
            BufferedImage scaled = Thumbnails.of(new ByteArrayInputStream(originalBytes))
                    .size(maxEdge, maxEdge)
                    .keepAspectRatio(true)
                    .asBufferedImage();
            BufferedImage rgb = toRgb(scaled);
            byte[] encoded;
            String contentType;
            String ext;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (ImageIO.write(rgb, "webp", baos)) {
                encoded = baos.toByteArray();
                contentType = "image/webp";
                ext = "webp";
            } else {
                baos.reset();
                ImageIO.write(rgb, "jpg", baos);
                encoded = baos.toByteArray();
                contentType = "image/jpeg";
                ext = "jpg";
            }
            String thumbRelativePath = buildThumbPath(originalFileInfo.getPath(), ext);
            return fileService.uploadBytes(encoded, contentType, thumbRelativePath);
        } catch (Exception e) {
            log.warn("生成缩略图失败: {}", e.getMessage());
            return null;
        }
    }

    /** 由原路径推导缩略图相对路径（正斜杠） */
    private static String buildThumbPath(String originalPath, String ext) {
        String normalized = originalPath.replace('\\', '/');
        int dot = normalized.lastIndexOf('.');
        String base = dot > 0 ? normalized.substring(0, dot) : normalized;
        return base + "_thumb." + ext;
    }

    /** 统一为 RGB，避免透明通道在部分编码器下异常 */
    private static BufferedImage toRgb(BufferedImage src) {
        if (src.getType() == BufferedImage.TYPE_INT_RGB) {
            return src;
        }
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, src.getWidth(), src.getHeight());
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return out;
    }
}
