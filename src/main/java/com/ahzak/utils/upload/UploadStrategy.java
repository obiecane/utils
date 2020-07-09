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
    LOCAL('1', new LocalUploader()),
    /**
     * 远程
     */
    REMOTE('2', new RemoteUploader()),
    /**
     * ftp
     */
    FTP('3', new FtpUploader()),
    /**
     * 阿里云对象存储
     */
    ALI_OSS('4', new AliOSSUploader());

    Uploader uploader;
    char charFlag;

    UploadStrategy(char charFlag, Uploader uploader) {
        this.charFlag = charFlag;
        this.uploader = uploader;
    }


    Uploader getUploader() {
        return uploader;
    }

    static UploadStrategy valueOf(char charFlag) {
        if (charFlag == LOCAL.charFlag) {
            return LOCAL;
        } else if (charFlag == REMOTE.charFlag) {
            return REMOTE;
        } else if (charFlag == FTP.charFlag) {
            return FTP;
        } else if (charFlag == ALI_OSS.charFlag) {
            return ALI_OSS;
        }
        return null;
    }


    String getUrlParameter() {
        return "rt=" + this.charFlag;
    }
}
