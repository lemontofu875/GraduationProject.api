package com.youlai.boot.platform.album.service.impl;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
import com.youlai.boot.platform.album.model.vo.ExifInfoVO;
import com.youlai.boot.platform.album.service.ExifParseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * EXIF 信息解析服务实现
 *
 * @author youlai
 */
@Slf4j
@Service
public class ExifParseServiceImpl implements ExifParseService {

    private static final DateTimeFormatter EXIF_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");

    @Override
    public ExifInfoVO parseExif(MultipartFile file) {
        ExifInfoVO exifInfo = new ExifInfoVO();
        try (InputStream inputStream = file.getInputStream()) {
            Metadata metadata = ImageMetadataReader.readMetadata(inputStream);

            // 光圈 (F-Number)
            String aperture = getTagValue(metadata, ExifSubIFDDirectory.class, ExifSubIFDDirectory.TAG_FNUMBER);
            if (aperture != null) {
                try {
                    double fNum = Double.parseDouble(aperture);
                    exifInfo.setAperture(String.format("f/%.1f", fNum));
                } catch (NumberFormatException e) {
                    exifInfo.setAperture(aperture);
                }
            }

            // 快门速度 (Exposure Time)
            String shutterSpeed = getTagValue(metadata, ExifSubIFDDirectory.class, ExifSubIFDDirectory.TAG_EXPOSURE_TIME);
            if (shutterSpeed != null) {
                try {
                    double sec = Double.parseDouble(shutterSpeed);
                    if (sec < 1) {
                        exifInfo.setShutterSpeed(String.format("1/%ds", (int) Math.round(1 / sec)));
                    } else {
                        exifInfo.setShutterSpeed(sec + "s");
                    }
                } catch (NumberFormatException e) {
                    exifInfo.setShutterSpeed(shutterSpeed);
                }
            }

            // ISO
            String iso = getTagValue(metadata, ExifSubIFDDirectory.class, ExifSubIFDDirectory.TAG_ISO_EQUIVALENT);
            if (iso != null) {
                try {
                    exifInfo.setIso(Integer.parseInt(iso));
                } catch (NumberFormatException ignored) {
                }
            }

            // 拍摄时间
            String dateTime = getTagValue(metadata, ExifIFD0Directory.class, ExifIFD0Directory.TAG_DATETIME);
            if (dateTime != null) {
                try {
                    LocalDateTime dt = LocalDateTime.parse(dateTime, EXIF_DATE_FORMAT);
                    exifInfo.setShootTime(dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                } catch (Exception e) {
                    exifInfo.setShootTime(dateTime);
                }
            }

            // 分辨率 (Image Width x Height)
            Integer width = getTagInt(metadata, JpegDirectory.class, JpegDirectory.TAG_IMAGE_WIDTH);
            Integer height = getTagInt(metadata, JpegDirectory.class, JpegDirectory.TAG_IMAGE_HEIGHT);
            if (width == null || height == null) {
                width = getTagInt(metadata, ExifSubIFDDirectory.class, ExifSubIFDDirectory.TAG_EXIF_IMAGE_WIDTH);
                height = getTagInt(metadata, ExifSubIFDDirectory.class, ExifSubIFDDirectory.TAG_EXIF_IMAGE_HEIGHT);
            }
            if (width != null && height != null) {
                exifInfo.setResolution(width + "x" + height);
            }

        } catch (ImageProcessingException e) {
            log.debug("无法解析 EXIF，可能不是支持的图片格式: {}", e.getMessage());
        } catch (Exception e) {
            log.warn("解析 EXIF 异常: {}", e.getMessage());
        }
        return exifInfo;
    }

    private String getTagValue(Metadata metadata, Class<? extends Directory> dirClass, int tagType) {
        Directory dir = metadata.getFirstDirectoryOfType(dirClass);
        if (dir == null) return null;
        Object value = dir.getObject(tagType);
        return value != null ? value.toString() : null;
    }

    private Integer getTagInt(Metadata metadata, Class<? extends Directory> dirClass, int tagType) {
        Directory dir = metadata.getFirstDirectoryOfType(dirClass);
        if (dir == null) return null;
        try {
            return dir.getInteger(tagType);
        } catch (Exception e) {
            return null;
        }
    }
}
