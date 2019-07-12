package com.ahzak.utils;

import java.io.File;
import java.util.UUID;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2019/7/12 8:42
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class Test {

    public static void main(String[] args) {
        String url1 = "https://mmbiz.qpic.cn/mmbiz_jpg/YxwiaXZWTlobub6oCGclXZ6L8HHbIUOzI6sO5ZMIumYgFC3j3AjTWsF8WkqfUP2I3ynxc66icKzxfgCuNKX2rlww/640?wx_fmt=jpeg&tp=webp&wxfrom=5&wx_lazy=1&wx_co=1";
        String url2 = "http://pic22.nipic.com/20120726/10387189_020114544000_2.jpg";
        String savePath = "F:/test/imgTest/a1/a2/a5/" + getName();
        try {
            File file = TransportUtil.downloadFromUrl(url1, savePath);
            String imageFormat = ImageUtil.realImgFormat(file);


            System.out.println(String.format("File(%s)'s format is %s", file, imageFormat));
            File newName = new File(file + "." + imageFormat);


            boolean renameSuccess = file.renameTo(newName);
            System.out.println(String.format("File(%s) rename to %s %s", file, newName, renameSuccess ? "successful" : "failed"));
            System.out.println(renameSuccess);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String getName() {
        return UUID.randomUUID().toString();
    }



}
