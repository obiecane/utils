package com.ahzak.utils.upload;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/7/7 17:25
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Data
@Component
@ConfigurationProperties(prefix = "jeemarket.upload")
class Config {

    private static volatile Object config;

    @Lazy
    @Autowired
    public void setConfig(Config config) {
        Config.config = config;
    }

    static Config getInstance() {
        if (config == null) {
            synchronized (Config.class) {
                if (config == null) {
                    config = new Config();
                }
            }
        }
        return (Config) config;
    }


    /**
     * 保存策略
     */
    private UploadStrategy strategy = UploadStrategy.LOCAL;


    /**
     * 本地存储配置
     */
    @NestedConfigurationProperty
    private LocalConfig local = new LocalConfig();

    /**
     * 远端存储配置
     */
    @NestedConfigurationProperty
    private RemoteConfig remote = new RemoteConfig();

    /**
     * FTP存储配置
     */
    @NestedConfigurationProperty
    private FtpConfig ftp = new FtpConfig();

    /**
     * 阿里云存储配置
     */
    @NestedConfigurationProperty
    private AliOSSConfig aliOss = new AliOSSConfig();


    private static String fixUrlPrefix(String urlPrefix) {
        if (!urlPrefix.startsWith("http://") && !urlPrefix.startsWith("https://")) {
            urlPrefix = "http://" + urlPrefix;
        }
        if (urlPrefix.endsWith("/")) {
            urlPrefix = urlPrefix.replaceAll("/+$", "");
        }
        return urlPrefix;
    }

    @Data
    static class LocalConfig {

        /**
         * 保存位置
         */
        private String storeLocation = "F:\\res";

        /**
         * url前缀
         */
        private String urlPrefix;

        public void setUrlPrefix(String urlPrefix) {
            this.urlPrefix = fixUrlPrefix(urlPrefix);
        }

        public void setStoreLocation(String storeLocation) {
            if (!storeLocation.endsWith(File.separator)) {
                storeLocation += File.separator;
            }
            this.storeLocation = storeLocation;
        }

    }

    @Data
    static class RemoteConfig {
        // ip地址
        private String host = "127.0.0.1";

        // 端口
        private int port = 80;

        /**
         * url前缀
         */
        private String urlPrefix;

        public void setUrlPrefix(String urlPrefix) {
            this.urlPrefix = fixUrlPrefix(urlPrefix);
        }
    }

    @Data
    static class FtpConfig {

        /**
         * url前缀
         */
        private String urlPrefix;

        public void setUrlPrefix(String urlPrefix) {
            this.urlPrefix = fixUrlPrefix(urlPrefix);
        }
    }

    @Data
    static class AliOSSConfig {

        /**
         * url前缀
         */
        private String urlPrefix;

        public void setUrlPrefix(String urlPrefix) {
            this.urlPrefix = fixUrlPrefix(urlPrefix);
        }
    }

}
