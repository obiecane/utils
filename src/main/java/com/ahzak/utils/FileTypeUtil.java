package com.ahzak.utils;

import com.google.common.collect.Sets;
import org.apache.commons.io.FilenameUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/10/25 9:27
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class FileTypeUtil {

    /**
     * 文件类型 ： 1 图片
     */
    public static final int TYPE_IMAGE = 1;

    /**
     * 文件类型 ： 2 视频
     */
    public static final int TYPE_VIDEO = 2;


    /**
     * 文件类型 ： 3 音频
     */
    public static final int TYPE_AUDIO = 3;


    /**
     * 文件类型 ： 4 其他
     */
    public static final int TYPE_OTHER = 4;


    private static final Set<String> TEXT_TYPE = new HashSet(Arrays.asList("txt", "md"));

    /**
     * 音频文件常见后缀
     */
    public final static Set<String> AUDIO_SUFFIX = Sets.newHashSet("mp3", "wav", "aif", "au", "ram", "wma", "mmf", "amr", "flac", "aac");

    /**
     * 视频文件常见
     */
    public final static Set<String> VIDEO_SUFFIX = Sets.newHashSet("mp4", "m4v", "mov", "qt", "avi", "flv", "wmv", "asf", "mpeg", "mpg", "vob", "rmvb", "rm", "ts", "3gp", "asf", "wmv", "mkv", "dat");

    private final static Set<String> PIC_SUFFIX = Sets.newHashSet("webp", "bmp", "pcx", "tif", "tiff", "gif", "jpeg", "jpg", "jpe", "tga", "exif", "fpx", "svg", "psd", "cdr", "pcd", "dxf", "ufo", "eps", "ai", "png", "hdri", "raw", "wmf", "flic", "emf", "ico", "icon");


    public static boolean isImage(String suffix) {
        return PIC_SUFFIX.contains(suffix.toLowerCase());
    }

    public static boolean isVideo(String suffix) {
        return VIDEO_SUFFIX.contains(suffix.toLowerCase());
    }

    public static boolean isAudio(String suffix) {
        return AUDIO_SUFFIX.contains(suffix.toLowerCase());
    }

    /**
     * 根据文件名称或文件后缀识别文件类型(图片, 音频, 视频, 其他)
     *
     * @param filenameOrSuffix
     * @return int
     * @author Zhu Kaixiao
     * @date 2019/10/25 9:41
     */
    public static int fileType(String filenameOrSuffix) {
        String suffix = filenameOrSuffix;
        if (filenameOrSuffix.indexOf(".") > -1) {
            suffix = FilenameUtils.getExtension(filenameOrSuffix);
        }

        if (isAudio(suffix)) {
            return TYPE_AUDIO;
        } else if (isVideo(suffix)) {
            return TYPE_VIDEO;
        } else if (isImage(suffix)) {
            return TYPE_IMAGE;
        } else {
            return TYPE_OTHER;
        }
    }
}
