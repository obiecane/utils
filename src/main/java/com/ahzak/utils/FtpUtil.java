package com.ahzak.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author Zhu Kaixiao
 * @date 2019/5/8 9:00
 **/
@Slf4j
public class FtpUtil {


    private FtpConf ftpConf;

    /**
     * 创建文件夹失败时的重试次数
     */
    private static final int MAKE_DIR_RETRY = 5;

    /**
     * 切换文件夹时失败的重试次数
     */
    private static final int CHANGE_DIR_RETRY = 5;


    /**
     * 退出登录并关闭连接
     * @param ftpClient ftp对象
     * @author Zhu Kaixiao
     * @date 2019/5/8 1:01
     **/
    private static void closeFtp(FTPClient ftpClient) {
        try {
            // 退出
            ftpClient.logout();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            // 断开连接
            ftpClient.disconnect();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 连接并登录ftp服务器
     * @param hostname 主机名
     * @param port 端口
     * @param username 用户名
     * @param password 登录密码
     * @return org.apache.commons.net.ftp.FTPClient
     * @author Zhu Kaixiao
     * @date 2019/5/8 1:00
     **/
    private static FTPClient connectFtp(String hostname, int port, String username, String password) throws IOException {
        FTPClient ftp = new FTPClient();
        // 连接ftp服务器
        ftp.connect(hostname, port);
        // 登录
        ftp.login(username, password);
        return ftp;
    }

    /**
     * 根据当前日期在ftp服务器上创建文件夹
     * @param ftpClient ftp连接对象
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2019/5/8 1:01
     **/
    private static String getWorkingDirectory(String basePath, FTPClient ftpClient) throws IOException {
        String path = basePath;

        String format = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String[] strings = format.split("-");
        for (String str : strings) {
            path += str + "/";
            boolean flag = ftpClient.changeWorkingDirectory(path);
            if (!flag) {
                retryMakeDirectory(ftpClient, path);
            }
        }
        return path;
    }

    /**
     * 批量上传文件到ftp
     * @param filePathList 文件名列表
     * @return java.lang.String 图片资源的url地址
     * @author Zhu Kaixiao
     * @date 2019/5/8 1:02
     **/
    public List<String> upload(List<String> filePathList) throws IOException {
        List<File> fileList = filePathList.stream().map(File::new).collect(Collectors.toList());
        List<String> filenameList = fileList.stream().map(File::getName).collect(Collectors.toList());
        return doUpload(filenameList, filePathList.stream().map(File::new).collect(Collectors.toList()));
    }

    public String upload(String filepath) throws IOException {
        List<String> upload = upload(Collections.singletonList(filepath));
        return upload.get(0);
    }




    /**
     * 真正实现上传的方法
     * @param filenameList 文件名列表
     * @param objList File or InputString or Function
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2019/5/8 0:57
     **/
    @SuppressWarnings("unchecked")
    private List<String> doUpload(List<String> filenameList, List<Object> objList) throws IOException {
        FTPClient ftp = connectFtp(ftpConf.getHost(), ftpConf.getPort(),
                ftpConf.getUsername(), ftpConf.getPassword());
        // 指定上传路径
        String workingDirectory = getWorkingDirectory(ftp.printWorkingDirectory() + "/", ftp);
        retryChangeWorkingDirectory(ftp, workingDirectory);
        // 指定上传文件的类型 二进制文件
        ftp.setFileType(FTP.BINARY_FILE_TYPE);

        List<String> ftpFileUrl = new ArrayList<>(filenameList.size());
        for (int i = 0; i < filenameList.size(); i++) {
            String filename = filenameList.get(i);
            Object obj = objList.get(i);
            if (obj instanceof Function) {
                OutputStream outputStream = ftp.appendFileStream(filename);
                ((Function<OutputStream, Object>) obj).apply(outputStream);
                outputStream.flush();
                outputStream.close();
            } else if (obj instanceof File) {
                InputStream local = new FileInputStream((File) obj);
                ftp.storeFile(filename, local);
            } else if (obj instanceof InputStream) {
                ftp.storeFile(filename, (InputStream) obj);
            } else {
                throw new RuntimeException("上传参数错误");
            }
            ftpFileUrl.add("ftp://" + ftpConf.getHost() + ":" + ftpConf.getPort() + workingDirectory + filename);
            log.info("[{}]上传成功, ftp url:{}", filename, ftpFileUrl.get(i));
        }

        closeFtp(ftp);

//        return ftpBean.getUrl() + "/" + workingDirectory + filename;
        return ftpFileUrl;
    }


    /**
     * 生成上传文件的名称
     * @param primitiveFileName 原来的文件名
     * @return java.lang.String
     * @date 2019/5/8 0:56
     **/
    private static String getFileName(String primitiveFileName) {
        // 使用uuid生成文件名
        String fileName = UUID.randomUUID().toString().replaceAll("-", "");
        // 获取文件后缀
        String suffix = StringUtils.substringAfterLast(primitiveFileName, ".");
        return fileName + "." + suffix;
    }

    /**
     * ftp切换工作目录, 如果切换失败则重试, 最大重试次数为 {@code CHANGE_DIR_RETRY}
     * 如果超过最大重试次数依然失败, 则抛出{@code RuntimeException}
     *
     * @param ftpClient ftp对象
     * @param dir       目录
     * @author Zhu Kaixiao
     * @date 2019/5/13 14:25
     **/
    private static void retryChangeWorkingDirectory(FTPClient ftpClient, String dir) throws IOException {
        int i = 0;
        boolean changeSuccess;
        String currDirectory = ftpClient.printWorkingDirectory() + "/";
        // 如果已经在当前目录, 直接返回
        if (currDirectory.endsWith(dir)) {
            return;
        }
        do {
            // 注意: 如果当前ftp会话的工作目录已经在当前目录, 那么changeWorkingDirectory将总是返回false
            changeSuccess = ftpClient.changeWorkingDirectory(dir);
            ++i;
        } while (!changeSuccess && i < CHANGE_DIR_RETRY);

        if (!changeSuccess) {
            throw new RuntimeException("ftp切换工作目录失败: " + dir);
        }
    }

    /**
     * ftp创建文件夹, 如果创建失败则重试, 最大重试次数为 {@code MAKE_DIR_RETRY}, 每次重试间隔为500ms
     * 如果超过最大重试次数依然失败, 则抛出{@code RuntimeException}
     *
     * @param ftpClient ftp对象
     * @param dir       待创建的文件夹
     * @author Zhu Kaixiao
     * @date 2019/5/13 14:40
     **/
    private static void retryMakeDirectory(FTPClient ftpClient, String dir) throws IOException {
        int f = 0;
        boolean makeSuccess;
        do {
            // 创建上传的路径 该方法只能创建一级目录，在这里如果/home/ftpuser存在则可创建image
            makeSuccess = ftpClient.makeDirectory(dir);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ++f;
        } while (!makeSuccess && f < MAKE_DIR_RETRY);

        // 超过最大重试次数依然创建失败, 则抛出异常
        if (!makeSuccess) {
            throw new RuntimeException("ftp创建目录失败: " + dir);
        }
    }
}
