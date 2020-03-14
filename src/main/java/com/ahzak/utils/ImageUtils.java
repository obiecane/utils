package com.ahzak.utils;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * 读取图片的分辨率
 *
 * @author: xzg
 * @date: 2019年10月24日
 * @Copyright: 江西金磊科技发展有限公司 All rights reserved.Notice
 *             仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Slf4j
public class ImageUtils {
    /**
     * 通过BufferedImage获取
     * @param in 文件流
     * @return 图片的分辨率
     * @throws IOException
     */
    public static String getResolution1(InputStream in) throws IOException {
        BufferedImage image = ImageIO.read(in);
        try {
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image.getWidth() + "*" + image.getHeight();
    }

    /**
     * 获取图片的分辨率
     *
     * @param path
     * @return
     */
    public static Dimension getImageDim(String path) {
        Dimension result = null;
        String suffix = getFileSuffix(path);
        //解码具有给定后缀的文件
        Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
        System.out.println(ImageIO.getImageReadersBySuffix(suffix));
        if (iter.hasNext()) {
            ImageReader reader = iter.next();
            try {
                ImageInputStream stream = new FileImageInputStream(new File(
                        path));
                reader.setInput(stream);
                int width = reader.getWidth(reader.getMinIndex());
                int height = reader.getHeight(reader.getMinIndex());
                result = new Dimension(width, height);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                reader.dispose();
            }
        }
        System.out.println("getImageDim:" + result);
        return result;
    }

    /**
     * 获得图片的后缀名
     * @param path
     * @return
     */
    private static String getFileSuffix(final String path) {
        String result = null;
        if (path != null) {
            result = "";
            if (path.lastIndexOf('.') != -1) {
                result = path.substring(path.lastIndexOf('.'));
                if (result.startsWith(".")) {
                    result = result.substring(1);
                }
            }
        }
        System.out.println("getFileSuffix:" + result);
        return result;
    }

    /**
     * 截取Dimension对象获得分辨率
     * @param path
     *
     * @return
     */
    public static String getResolution2(String path) {
        String s = getImageDim(path).toString();
        s = s.substring(s.indexOf("[") + 1, s.indexOf("]"));
        String w = s.substring(s.indexOf("=") + 1, s.indexOf(","));
        String h = s.substring(s.lastIndexOf("=") + 1);
        String result = w + " x " + h;
        System.out.println("getResolution:" + result);
        return result;
    }

    /**
     * 测试
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String path = "C:\\Users\\ASUS\\Desktop\\QQ图片20190701154354.jpg";
        File file = new File(path);
        String resolution1 = getResolution1(new FileInputStream(file));
        System.out.println(resolution1);
    }
}