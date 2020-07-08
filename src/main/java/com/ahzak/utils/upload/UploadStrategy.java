package com.ahzak.utils.upload;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/7/7 17:30
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
enum UploadStrategy {

    /**
     * 本地
     */
    LOCAL(new LocalUploader()),
    /**
     * 远程
     */
    REMOTE(new RemoteUploader()),
    /**
     * ftp
     */
    FTP(new FtpUploader()),
    /**
     * 阿里云对象存储
     */
    ALI_OSS(new AliOSSUploader());

    Uploader uploader;

    UploadStrategy(Uploader uploader) {
        this.uploader = uploader;
    }


    Uploader getUploader() {
        return uploader;
    }
}
