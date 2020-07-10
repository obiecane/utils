package com.ahzak.utils.resource;

import com.ahzak.utils.FileUtil;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 文件上传工具类
 *
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/7/7 17:21
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class ResourceUtil {


    /**
     * 上传文件
     * 上传本地文件{@link org.springframework.core.io.FileSystemResource}
     * 上传网络文件可直接用{@link org.springframework.core.io.UrlResource}, 或者自行下载到本地, 再以本地文件形式上传
     * 上传{@link org.springframework.web.multipart.MultipartFile}使用{@link com.jeecms.market.kit.MultipartFileResource}
     * 上传 二进制数据 用{@link org.springframework.core.io.ByteArrayResource}, 再用{@link com.jeecms.market.kit.RenameResource}包装一下
     * ...
     * 只要实现了{@link Resource}接口, 且{@link Resource#getFilename()}和{@link Resource#getInputStream()}两个方法能返回正确的值
     * 那么就都可以上传
     * 当getFilename()返回值为null或空白字符时, 将会自动生成一个随机文件名
     *
     * @param resources 资源集合
     * @return java.util.List<com.jeecms.market.utils.upload.UploadResult>
     * @author Zhu Kaixiao
     * @date 2020/7/8 16:26
     */
    public static List<UploadResult> upload(Collection<Resource> resources) throws IOException {

        return Config.getInstance()
                .getStrategy()
                .getUploader()
                .upload(resources);
    }

    /**
     * @param resources 资源
     * @return java.util.List<com.jeecms.market.utils.upload.UploadResult>
     * @author Zhu Kaixiao
     * @date 2020/7/8 16:35
     * @see ResourceUtil#upload(Collection)
     */
    public static List<UploadResult> upload(Resource[] resources) throws IOException {
        return upload(Arrays.asList(resources));
    }

    /**
     * @param resource 资源
     * @return com.jeecms.market.utils.resource.UploadResult
     * @author Zhu Kaixiao
     * @date 2020/7/10 9:39
     * @see ResourceUtil#upload(Collection)
     */
    public static UploadResult upload(Resource resource) throws IOException {
        return upload(Collections.singletonList(resource)).get(0);
    }

    public static boolean delete(String path) throws IOException {
        return delete(Collections.singletonList(path)).get(0);
    }

    public static List<Boolean> delete(String[] path) throws IOException {
        return delete(Arrays.asList(path));
    }

    public static List<Boolean> delete(Collection<String> paths) throws IOException {
        return Config.getInstance().getStrategy().getUploader().delete(paths);
    }

    public static File download(String path) throws IOException {
        return download(Collections.singletonList(path)).get(0);
    }

    public static List<File> download(String[] path) throws IOException {
        return download(Arrays.asList(path));
    }

    public static List<File> download(Collection<String> paths) throws IOException {
        return download(FileUtil.getTmpDirPath(), paths);
    }


    public static File download(String dirPath, String path) throws IOException {
        return download(dirPath, Collections.singletonList(path)).get(0);
    }

    public static List<File> download(String dirPath, String[] path) throws IOException {
        return download(dirPath, Arrays.asList(path));
    }

    public static List<File> download(String dirPath, Collection<String> paths) throws IOException {
        return Config.getInstance().getStrategy().getUploader().download(dirPath, paths);
    }

    /**
     * 使用当前配置的url前缀拼接资源访问url
     *
     * @param path path
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/7/8 16:25
     */
    public static String accessUrl(String path) {
        // 根据path的后缀决定使用什么前缀
        if (Config.getInstance().getUrlSpliceStrategy() == UrlSpliceStrategy.DYNAMIC) {
            for (UploadStrategy uploadStrategy : UploadStrategy.values()) {
                String rt = uploadStrategy.getUrlParameter();
                if (path.endsWith(rt)) {
                    return Optional.of(getUrlPrefix(uploadStrategy))
                            .orElse(getUrlPrefix(UploadStrategy.REMOTE)) + path.substring(0, path.length() - rt.length() - 1);
                }
            }
        }
        return getUrlPrefix() + path;
    }

    /**
     * 当前配置的资源访问url前缀
     *
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/7/8 16:25
     */
    public static String getUrlPrefix() {
        return getUrlPrefix(Config.getInstance().getStrategy());
    }

    static String getUrlPrefix(UploadStrategy uploadStrategy) {
        switch (uploadStrategy) {
            case LOCAL:
                return Config.getInstance().getLocal().getUrlPrefix();
            case REMOTE:
                return Config.getInstance().getRemote().getUrlPrefix();
            case FTP:
                return Config.getInstance().getFtp().getUrlPrefix();
            case ALI_OSS:
                return Config.getInstance().getAliOss().getUrlPrefix();
            default:
                // 不可能会到这里
                return null;
        }
    }
}
