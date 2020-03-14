package com.ahzak.utils;

import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * 文件下载
 *
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/12 9:18
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Slf4j
public class DownloadUtil {


    /**
     * see {@link #downloadFromUrl(String, String, Transporter, boolean)}
     *
     * @return java.io.File
     * @author Zhu Kaixiao
     * @date 2019/7/12 9:48
     */
    public static File downloadFromUrl(String urlStr, String savePath, boolean overwrite) {
        return downloadFromUrl(urlStr, new File(savePath), overwrite);
    }

    public static File downloadFromUrl(String urlStr, File saveFile, boolean overwrite) {
        return downloadFromUrl(urlStr, saveFile, (in, out) -> IoUtil.copy(in, out), overwrite);
    }

    /**
     * 从url中下载文件到指定的路径
     *
     * @param urlStr      url
     * @param savePath    文件保存路径
     * @param transporter 传输器  可用于指定具体的传输过程
     * @param overwrite   是否覆盖原文件
     * @return java.io.File 已下载完成的文件
     * @author Zhu Kaixiao
     * @date 2019/7/12 9:46
     */
    public static File downloadFromUrl(String urlStr, String savePath, Transporter transporter, boolean overwrite) {
        return downloadFromUrl(urlStr, new File(savePath), transporter, overwrite);
    }

    public static File downloadFromUrl(String urlStr, File file, Transporter transporter, boolean overwrite) {
        try {
            if (!overwrite && file.exists()) {
                throw new IllegalArgumentException(String.format("文件已存在: [%s]", file.getCanonicalPath()));
            }

            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }

            URL url = new URL(encodeUrl(urlStr));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            conn.setRequestProperty("Host", url.getHost());
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");


            try (InputStream inputStream = conn.getInputStream();
                 FileOutputStream fos = new FileOutputStream(file)) {
                transporter.transport(inputStream, fos);
                fos.flush();
            }

            log.debug("{} download success", urlStr);
            return file;
        } catch (IOException e) {
            log.warn("{} download fail", urlStr);
            throw new RuntimeException(e);
        }
    }

    /**
     * see {@link #downloadFromUrl(String, String, Transporter, boolean)}
     *
     * @return java.io.File
     * @author Zhu Kaixiao
     * @date 2019/7/12 9:48
     */
    public static File downloadFromUrl(String urlStr, String savePath) {
        return downloadFromUrl(urlStr, savePath, true);
    }

    public static File downloadFromUrl(String urlStr, File saveFile) {
        return downloadFromUrl(urlStr, saveFile, true);
    }

    /**
     * 从指定url中下载文件   文件会被保存在系统临时目录
     *
     * @param urlStr
     * @return java.io.File
     * @author Zhu Kaixiao
     * @date 2019/11/23 14:52
     */
    public static File downloadFromUrl(String urlStr) {
        File tempFile = FileUtil.createTempFile();
        downloadFromUrl(urlStr, tempFile, true);
        File file = FileUtil.renameToRealType(tempFile);
        return file;
    }

    /**
     * 下载文件到指定目录, 文件名将自动创建
     *
     * @param urlStr
     * @param saveDir
     * @return java.io.File
     * @author Zhu Kaixiao
     * @date 2019/11/23 15:07
     */
    public static File downloadFromUrlToDir(String urlStr, String saveDir) {
        File tempFile = FileUtil.createTempFileIn(saveDir);
        downloadFromUrl(urlStr, tempFile, true);
        File file = FileUtil.renameToRealType(tempFile);
        return file;
    }

    public static void main(String[] args) {
        File file = downloadFromUrl("http://mmbiz.qpic.cn/mmbiz_jpg/gvCYlALaZpWo0gCDMibpujswicicMK86OmCWcTATyWNzcicd5FDL6X6aKFuM4ejXZqqpEGYlKM7iaiaBW6FiamBDIvBNw/0");
        System.out.println(FileUtil.getCanonicalPath(file));
    }

    @FunctionalInterface
    public interface Transporter {

        /**
         * 从输入流传送到输出流
         *
         * @param in  输入流
         * @param out 输出流
         */
        void transport(InputStream in, OutputStream out);
    }


    /**
     * url编码   避免url中有中文时报错
     *
     * @param urlStr url
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2019/8/5 14:57
     **/
    private static String encodeUrl(String urlStr) throws UnsupportedEncodingException {
        StringBuilder newUrlSb = new StringBuilder();
        String protocol = StringUtils.substringBefore(urlStr, "://");
        newUrlSb.append(protocol).append("://");
        String urlBody = StringUtils.substringAfter(urlStr, "://");

        String hf = urlBody;
        String param = null;
        if (urlBody.contains("?")) {
            // host and file
            hf = StringUtils.substringBefore(urlBody, "?");
            // parameter
            param = StringUtils.substringAfter(urlBody, "?");
        }

        String[] sfSp = hf.split("/+");
        for (int i = 0; i < sfSp.length; i++) {
            // host, may be have port
            if (i == 0) {
                newUrlSb.append(sfSp[i]).append("/");
            } else {
                newUrlSb.append(URLEncoder.encode(sfSp[i], "UTF-8")).append("/");
            }
        }

        newUrlSb.delete(newUrlSb.length() - 1, newUrlSb.length());

        if (param != null) {
            newUrlSb.append("?");
            for (String s : param.split("&")) {
                String[] paramSp = s.split("=");
                newUrlSb.append(URLEncoder.encode(paramSp[0], "UTF-8")).append("=").append(URLEncoder.encode(paramSp[1], "UTF-8")).append("&");
            }
            newUrlSb.delete(newUrlSb.length() - 1, newUrlSb.length());
        }

        return newUrlSb.toString();
    }

}
