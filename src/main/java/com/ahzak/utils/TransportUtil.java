package com.ahzak.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 文件下载
 *
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/12 9:18
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class TransportUtil {

    /**
     * 从url中下载文件到指定的路径
     *
     * @param urlStr url
     * @param savePath 文件保存路径
     * @param overwrite 是否覆盖原文件
     * @return java.io.File 已下载完成的文件
     * @author Zhu Kaixiao
     * @date 2019/7/12 9:46
     **/
    public static File downLoadFromUrl(String urlStr, String savePath, boolean overwrite) throws IOException {
        File file = new File(savePath);

        if (!overwrite && file.exists()) {
            throw new IllegalArgumentException(String.format("文件已存在: [%s]", file.getCanonicalPath()));
        }

        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3 * 1000);
        //防止屏蔽程序抓取而返回403错误
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        try (InputStream inputStream = conn.getInputStream();
             FileOutputStream fos = new FileOutputStream(file)) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
        }

        System.out.println("info:" + url + " download success");
        return file;
    }


    /**
     * see {@link TransportUtil#downLoadFromUrl(String, String, boolean)}
     *
     * @return java.io.File
     * @author Zhu Kaixiao
     * @date 2019/7/12 9:48
     **/
    public static File downLoadFromUrl(String urlStr, String savePath) throws IOException {
        return downLoadFromUrl(urlStr, savePath, true);
    }

}
