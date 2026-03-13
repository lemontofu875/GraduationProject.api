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

            // 光圈 (F-Number)，EXIF 常为有理数如 "16/10" -> f/1.6
            String aperture = getTagValue(metadata, ExifSubIFDDirectory.class, ExifSubIFDDirectory.TAG_FNUMBER);
            if (aperture != null) {
                Double fNum = parseRationalOrDouble(aperture);
                if (fNum != null) {
                    exifInfo.setAperture(String.format("f/%.1f", fNum));
                } else {
                    exifInfo.setAperture(aperture);
                }
            }

            // 快门速度 (Exposure Time)，可能为 "1/200" 或小数
            String shutterSpeed = getTagValue(metadata, ExifSubIFDDirectory.class, ExifSubIFDDirectory.TAG_EXPOSURE_TIME);
            if (shutterSpeed != null) {
                String formatted = formatShutterSpeed(shutterSpeed);
                if (formatted != null) {
                    exifInfo.setShutterSpeed(formatted);
                } else {
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

    /**
     * 解析有理数 "16/10" 或小数 "1.6"，返回数值
     */
    private Double parseRationalOrDouble(String value) {
        if (value == null || value.isBlank()) return null;
        value = value.trim();
        int slash = value.indexOf('/');
        if (slash > 0) {
            try {
                double num = Double.parseDouble(value.substring(0, slash).trim());
                double den = Double.parseDouble(value.substring(slash + 1).trim());
                return den != 0 ? num / den : null;
            } catch (NumberFormatException e) {
                return null;
            }
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 格式化为快门显示：如 "1/200" -> "1/200s"，小数秒 -> "1/125s" 或 "2s"
     */
    private String formatShutterSpeed(String value) {
        Double sec = parseRationalOrDouble(value);
        if (sec == null) return null;
        if (sec < 1 && sec > 0) {
            int den = (int) Math.round(1 / sec);
            return "1/" + den + "s";
        }
        return sec + "s";
    }
}
