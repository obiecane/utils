package com.ahzak.utils;

import cn.hutool.core.io.IoUtil;
import com.jeecms.jspgouexclusive.exception.GlobalException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;

/**
 * 文件工具类
 *
 * @author pss
 * @version 1.0
 * @date 2019/10/28
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Slf4j
public class FileUtil extends cn.hutool.core.io.FileUtil {


    static final String specialS1 = "\0";
    static final String specialS2 = "/";
    static final String specialS3 = "\\";
    /**
     * doc的后缀
     */
    public static final String[] DOC_EXT = new String[]{"doc", "docx", "wps", "txt", "pdf"};
    /**
     * excel后缀
     */
    public static final String[] EXCEL_EXT = new String[]{"xlsx", "xlsm", "xltx", "xltm", "xlsb", "xlam"};
    /**
     * ppt后缀
     */
    public static final String[] PPT_EXT = new String[]{"ppt", "pptx", "pptm", "ppsx", "potx", "potm"};
    /**
     * 视频后缀
     */
    public static final String[] VIDEO_EXT = new String[]{"avi", "asf", "wmv", "avs", "flv", "mkv", "mov", "3gp",
            "mp4", "mpg", "mpeg", "dat", "ogm", "vob", "rmvb", "rm", "ts", "ifo"};
    /**
     * 音频后缀
     */
    public static final String[] AUDIO_EXT = new String[]{"wav", "aac", "mp3", "aif", "au", "ram", "wma", "amr"};
    /**
     * 压缩包后缀
     */
    public static final String[] ZIP_EXT = new String[]{"zip", "rar", "tar", "gzip"};

    private static Map<String, String> fileTypeMap = new HashMap<String, String>();
    private static Map<String, List<String>> whiteList = new HashMap<String, List<String>>();
    private static List<String> blackList = new ArrayList<String>();

    static {
        // 初始化文件类型信息
        initFileTypeHeadInfos();
    }


    /**
     * 根据文件大小转换为B、KB、MB、GB单位字符串显示
     *
     * @param fileSize 文件的大小,单位: byte
     * @return 返回 转换后带有单位的字符串
     */
    public static String humanSize(long fileSize) {
        String strFileSize;
        if (fileSize < 1024) {
            strFileSize = fileSize + "B";
        } else if (fileSize < 1024 * 1024) {
            strFileSize = Math.ceil(fileSize / 1024.0 * 100) / 100 + "KB";
        } else if (fileSize < 1024 * 1024 * 1024) {
            strFileSize = Math.ceil(fileSize / (1024 * 1024.0) * 100) / 100 + "MB";
        } else {
            strFileSize = Math.ceil(fileSize / (1024 * 1024 * 1024.0) * 100) / 100 + "GB";
        }
        return strFileSize;
    }

    public static String humanSize(File file) {
        return humanSize(size(file));
    }


    /**
     * 根据文件头特征获取文件的真实类型
     *
     * @param file 文件对象
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2019/11/23 14:30
     */
    public static String getRealType(File file) {
        BufferedInputStream inputStream = getInputStream(file);
        String fileHeaderCode = getFileHeaderCode(inputStream);
        String fileSuffix = getFileSuffix(fileHeaderCode);
        IoUtil.close(inputStream);
        return fileSuffix;
    }


    /**
     * 以指定的后缀名创建临时文件<br>
     * 创建后的文件名为 [Random].[suffix]
     * 创建的文件在 System.getProperty("java.io.tmpdir") 文件夹下
     *
     * @param suffix
     * @return java.io.File
     * @author Zhu Kaixiao
     * @date 2019/11/23 14:35
     */
    public static File createTempFile(String suffix) {
        File tempFile = createTempFileIn(System.getProperty("java.io.tmpdir"), suffix);
        return tempFile;
    }

    /**
     * 在指定目录下创建临时文件
     *
     * @param dir
     * @return java.io.File
     * @author Zhu Kaixiao
     * @date 2019/11/23 15:15
     */
    public static File createTempFileIn(String dir) {
        File tempFile = createTempFile("tempfile", null, new File(dir), true);
        return tempFile;
    }

    /**
     * 在指定目录下以指定后缀名创建临时文件
     *
     * @param dir
     * @param suffix
     * @return java.io.File
     * @author Zhu Kaixiao
     * @date 2019/11/23 15:17
     */
    public static File createTempFileIn(String dir, String suffix) {
        if (!suffix.startsWith(".")) {
            suffix = "." + suffix;
        }
        File tempFile = createTempFile("tempfile", suffix, new File(dir), true);
        return tempFile;
    }

    /**
     * 创建临时文件<br>
     * 创建后的文件名为 [Random].tmp
     * 创建的文件在 System.getProperty("java.io.tmpdir") 文件夹下
     *
     * @return java.io.File
     * @author Zhu Kaixiao
     * @date 2019/11/23 14:35
     */
    public static File createTempFile() {
        File tempFile = createTempFileIn(System.getProperty("java.io.tmpdir"), ".tmp");
        return tempFile;
    }




    /**
     * 是否是文档
     *
     * @param ext 后缀格式
     */
    public static boolean isDoc(String ext) {
        ext = ext.toLowerCase(Locale.ENGLISH);
        for (String s : DOC_EXT) {
            if (s.equalsIgnoreCase(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否Excel格式
     *
     * @param ext 格式
     * @Title: isValidExcelExt
     * @return: boolean true 是Excel
     */
    public static boolean isExcel(String ext) {
        ext = ext.toLowerCase(Locale.ENGLISH);
        for (String s : EXCEL_EXT) {
            if (s.equalsIgnoreCase(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否ppt格式
     *
     * @param ext 格式
     * @Title: isValidPptExt
     * @return: boolean true 是ppt
     */
    public static boolean isPpt(String ext) {
        ext = ext.toLowerCase(Locale.ENGLISH);
        for (String s : PPT_EXT) {
            if (s.equalsIgnoreCase(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否视频格式
     *
     * @param ext 格式
     * @Title: isValidVideoExt
     * @return: boolean true 是视频
     */
    public static boolean isVideo(String ext) {
        ext = ext.toLowerCase(Locale.ENGLISH);
        for (String s : VIDEO_EXT) {
            if (s.equalsIgnoreCase(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否音频格式
     *
     * @param ext 格式
     * @Title: isValidAudioExt
     * @return: boolean true 是音频
     */
    public static boolean isAudio(String ext) {
        ext = ext.toLowerCase(Locale.ENGLISH);
        for (String s : AUDIO_EXT) {
            if (s.equalsIgnoreCase(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否压缩包格式
     *
     * @param ext 格式
     * @Title: isValidZipExt
     * @return: boolean true 是压缩包
     */
    public static boolean isZip(String ext) {
        ext = ext.toLowerCase(Locale.ENGLISH);
        for (String s : ZIP_EXT) {
            if (s.equalsIgnoreCase(ext)) {
                return true;
            }
        }
        return false;
    }


//    /**
//     * 判断该文件是否允许上传！
//     *
//     * @param inputStream 检查输入流上传的文件是否合法
//     * @Title: checkFileIsValid
//     * @return: Boolean
//     */
//    public static Boolean checkFileIsValid(InputStream inputStream) {
//        String suffix = null;
//        String fileHeaderCode = null;
//        Boolean flag = false;
//        if (inputStream == null) {
//            return false;
//        }
//        fileHeaderCode = getFileHeaderCode(inputStream);
//        suffix = getFileSuffix(fileHeaderCode);
//        boolean isInWhite = false;
//        if (StringUtils.isNotBlank(suffix)) {
//            suffix = suffix.toLowerCase(Locale.ENGLISH);
//            isInWhite = whiteList.containsKey(suffix);
//        }
//        /** 没在白名单文件头中定义的则检查黑名单 可能是txt html等 */
//        if (isInWhite) {
//            if (codeIsBelongToExt(suffix, fileHeaderCode)) {
//                flag = true;
//            } else {
//                flag = false;
//            }
//        } else {
//            for (String string : blackList) {
//                if (fileHeaderCode.toLowerCase(Locale.ENGLISH)
//                        .startsWith(string.toLowerCase(Locale.ENGLISH))
//                        || string.toLowerCase(Locale.ENGLISH)
//                        .startsWith(fileHeaderCode.toLowerCase(Locale.ENGLISH))) {
//                    flag = false;
//                } else {
//                    flag = true;
//                }
//            }
//        }
//        return flag;
//    }
//
//    /**
//     * 将InputStream文件流转换File
//     *
//     * @Title: convertInputStreamToFile
//     * @param: @param
//     * input 文件流
//     * @param: @param
//     * filePath 文件路径及名称
//     * @param: @return
//     * @return: File
//     */
//    public static File convertInputStreamToFile(InputStream input, String filePath) {
//        BufferedOutputStream bos = null;
//        FileOutputStream fileOutputStream = null;
//        try {
//            filePath = java.text.Normalizer.normalize(filePath, java.text.Normalizer.Form.NFKD);
//            File file = new File(filePath);
//            bos = new BufferedOutputStream(fileOutputStream = new FileOutputStream(file));
//            byte[] buf = new byte[1024];
//            int length;
//            while ((length = input.read(buf)) != -1) {
//                bos.write(buf, 0, length);
//            }
//            input.close();
//            bos.close();
//            return file;
//        } catch (IOException e) {
//            log.debug("文件流转换出错{}", e.getMessage());
//            return null;
//        } finally {
//            try {
//                if (fileOutputStream != null) {
//                    fileOutputStream.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            try {
//                if (bos != null) {
//                    bos.close();
//                }
//            } catch (Exception e2) {
//            }
//        }
//    }

    /**
     * 输入流转字节输出流
     *
     * @param inputStream InputStream
     * @Title: convertInputStreamToByte
     * @return: ByteArrayOutputStream
     */
    public static ByteArrayOutputStream convertInputStreamToByte(InputStream inputStream) {
        try {
            ByteArrayOutputStream outByte = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > -1) {
                outByte.write(buffer, 0, len);
            }
            outByte.flush();
            return outByte;
        } catch (Exception e) {
            log.debug("文件流转换出错{}", e.getMessage());
            return null;
        }
    }

    /**
     * 是否非法文件名或者路径
     *
     * @param basePath 限定路径（选择文件或者路径只能是在这下面） 可为空，为空则不验证限定路径
     * @param filename 文件名或者路径 为空串则返回true 不可为空 为空则抛出异常 非法请求
     * @throws GlobalException GlobalException
     * @Title: isValidFilename
     */
    public static void isValidFilename(String basePath, String filename) throws GlobalException {
        if (StringUtils.isBlank(filename)) {
            throw new GlobalException("filename or path is valid!");
        }
        if (filename.contains("../") || filename.contains("..\\") || (filename.indexOf(specialS1) != -1)) {
            throw new GlobalException("filename or path is valid!");
        } else {
            if (StringUtils.isNoneBlank(basePath)) {
                if (!filename.startsWith(basePath)) {
                    throw new GlobalException("filename or path is valid!");
                }
            }
        }
    }

    /**
     * 格式化文件名或者路徑名
     *
     * @param filename 文件名或者路径名
     * @Title: normalizeFilename
     * @return: String
     */
    public static String normalizeFilename(String filename) {
        if (StringUtils.isBlank(filename)) {
            return filename;
        }
        return java.text.Normalizer.normalize(filename, java.text.Normalizer.Form.NFKD);
    }

    private static void initFileTypeHeadInfos() {
        List<String> jpgHeadList = new ArrayList<String>();
        jpgHeadList.add("ffd8ff");
        List<String> pngHeadList = new ArrayList<String>();
        pngHeadList.add("89504e");
        List<String> gifHeadList = new ArrayList<String>();
        gifHeadList.add("47494638");
        List<String> bmpHeadList = new ArrayList<String>();
        bmpHeadList.add("424d");
        List<String> aviHeadList = new ArrayList<String>();
        aviHeadList.add("41564920");
        aviHeadList.add("52494646");
        List<String> wmvHeadList = new ArrayList<String>();
        wmvHeadList.add("3026b2758e66cf11a6d9");
        List<String> mp4HeadList = new ArrayList<String>();
        mp4HeadList.add("000000");
        List<String> pdfHeadList = new ArrayList<String>();
        pdfHeadList.add("255044462d312e");
        List<String> docHeadList = new ArrayList<String>();
        docHeadList.add("d0cf11e0a1b11ae10000");
        List<String> docxHeadList = new ArrayList<String>();
        docxHeadList.add("504b03041400");
        List<String> htmlHeadList = new ArrayList<String>();
        htmlHeadList.add("68746D6C3E");
        htmlHeadList.add("3c21444f435459504520");
        List<String> flvHeadList = new ArrayList<String>();
        flvHeadList.add("464c56");
        List<String> tifHeadList = new ArrayList<String>();
        tifHeadList.add("49492A00");
        List<String> rarHeadList = new ArrayList<String>();
        rarHeadList.add("52617221");
        rarHeadList.add("1f8b0800");
        rarHeadList.add("504b0304140000000800");

        whiteList.put("jpg", jpgHeadList);
        whiteList.put("jpeg", jpgHeadList);
        whiteList.put("png", pngHeadList);
        whiteList.put("gif", gifHeadList);
        whiteList.put("bmp", bmpHeadList);
        whiteList.put("avi", aviHeadList);
        whiteList.put("wmv", wmvHeadList);
        whiteList.put("mp4", mp4HeadList);
        whiteList.put("pdf", pdfHeadList);
        whiteList.put("doc", docHeadList);
        whiteList.put("xls", docHeadList);
        whiteList.put("ppt", docHeadList);
        whiteList.put("flv", flvHeadList);
        whiteList.put("docx", docxHeadList);
        whiteList.put("xlsx", docxHeadList);
        whiteList.put("pptx", docxHeadList);
        whiteList.put("html", htmlHeadList);
        whiteList.put("tif", tifHeadList);
        whiteList.put("rar", rarHeadList);
        // txt 文件头过于多了
        whiteList.put("txt", null);
        whiteList.put("3gp", null);
        blackList.add("4d5a90");

        fileTypeMap.put("ffd8ff", "jpg");
        fileTypeMap.put("89504e", "png");
        fileTypeMap.put("47494638", "gif");
        fileTypeMap.put("424d", "bmp");
        fileTypeMap.put("41564920", "avi");
        fileTypeMap.put("52494646", "avi");
        fileTypeMap.put("3026b2758e66cf11a6d9", "wmv");
        fileTypeMap.put("000000", "mp4");
        fileTypeMap.put("255044462d312e", "pdf");
        fileTypeMap.put("d0cf11e0a1b11ae10000", "doc");
        // docx|xlsx|pptx
        fileTypeMap.put("504b03041400", "docx");
        fileTypeMap.put("68746D6C3E", "html");
        fileTypeMap.put("3c21444f435459504520", "html");
        fileTypeMap.put("3c21646f637479706520", "htm");
        fileTypeMap.put("49492a", "tif");
        fileTypeMap.put("414331", "dwg");
        fileTypeMap.put("48544d", "css");
        fileTypeMap.put("696b2e", "js");
        fileTypeMap.put("7b5c72", "rtf");
        fileTypeMap.put("384250", "psd");
        fileTypeMap.put("46726f", "eml");
        fileTypeMap.put("537461", "mdb");
        fileTypeMap.put("252150", "ps");
        fileTypeMap.put("2e524d", "rmvb");
        // flv、f4v
        fileTypeMap.put("464c56", "flv");
        fileTypeMap.put("494433", "mp3");
        fileTypeMap.put("000001", "mpg");
        fileTypeMap.put("52494646e27807005741", "wav");
        // MIDI (mid)
        fileTypeMap.put("4d546864000000060001", "mid");
        fileTypeMap.put("504b0304140000000800", "zip");
        fileTypeMap.put("52617221", "rar");
        fileTypeMap.put("235468", "ini");
        fileTypeMap.put("504b03040a0000000000", "jar");
        fileTypeMap.put("4d5a90", "exe");
        fileTypeMap.put("3c2540", "jsp");
        fileTypeMap.put("4d616e", "mf");
        fileTypeMap.put("3C3F786D6C", "xml");
        fileTypeMap.put("494e53", "sql");
        fileTypeMap.put("706163", "java");
        fileTypeMap.put("406563", "bat");
        fileTypeMap.put("1f8b0800000000000000", "gz");
        fileTypeMap.put("6c6f67", "properties");
        fileTypeMap.put("cafeba", "class");
        fileTypeMap.put("495453", "chm");
        fileTypeMap.put("04000000010000001300", "mxp");
        fileTypeMap.put("6431303a637265617465", "torrent");
        fileTypeMap.put("6D6F6F76", "mov");
        fileTypeMap.put("FF575043", "wpd");
        fileTypeMap.put("CFAD12FEC5FD746F", "dbx");
        fileTypeMap.put("2142444E", "pst");
        fileTypeMap.put("AC9EBD8F", "qdf");
        fileTypeMap.put("E3828596", "pwl");
        fileTypeMap.put("2E7261FD", "ram");
        fileTypeMap.put("49492A00", "tif");
    }

    /**
     * 二进制数组转十六进制
     *
     * @param src 文件数组
     * @return
     */
    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (null == src || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 获取文件真实类型
     *
     * @param fileHeaderCode 文件头
     * @return
     */
    private static String getFileSuffix(String fileHeaderCode) {
        String res = null;
        Iterator<String> keyIter = fileTypeMap.keySet().iterator();
        while (keyIter.hasNext()) {
            String key = keyIter.next();
            if (key.toLowerCase(Locale.ENGLISH).startsWith(fileHeaderCode.toLowerCase(Locale.ENGLISH))
                    || fileHeaderCode.toLowerCase(Locale.ENGLISH).startsWith(key)) {
                res = fileTypeMap.get(key);
                break;
            }
        }
        return res;
    }

    /**
     * 获取文件头
     *
     * @param file 輸入流
     * @Title: getFileHeaderCode
     * @return: String
     */
    private static String getFileHeaderCode(InputStream file) {
        byte[] b = new byte[10];
        String fileCode = "";
        try {
            file.read(b, 0, b.length);
            fileCode = bytesToHexString(b);
        } catch (IOException e) {
            log.error("IOException:", e);
        }
        return fileCode;
    }


    /**
     * 修改文件的后缀为该文件的真实类型
     * @param file
     * @return java.io.File
     * @author Zhu Kaixiao
     * @date 2019/11/23 15:03
     */
    public static File renameToRealType(File file) {
        String newSuffix = "." + FileUtil.getRealType(file);
        String canonicalPath = FileUtil.getCanonicalPath(file);
        if (canonicalPath.endsWith(newSuffix)) {
            return file;
        }
        String newPath = StringUtils.substringBeforeLast(canonicalPath, ".") + newSuffix;
        rename(file, newPath, false, true);
        return new File(newPath);
    }


}
