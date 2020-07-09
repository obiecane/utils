package com.ahzak.utils.upload;

import lombok.Data;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/7/8 10:10
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Data
public class UploadResult {

    /**
     * 访问链接
     */
    private String url;

    /**
     * 路径
     */
    private String path;

    /**
     * 文件名
     */
    private String filename;

    UploadResult(String url, String path, String filename) {
        this.url = url;
        this.path = path;
        this.filename = filename;
    }

    static UploadResult of(UploadContext context) {
        // URLEncoder.QUERY.encode(wrapper.getPath(), StandardCharsets.UTF_8)
        String path;

        switch (Config.getInstance().getUrlSpliceStrategy()) {
            case FOLLOW_UPLOAD_STRATEGY:
                path = context.getPath();
                break;

            case DYNAMIC:
            default:
                path = context.getPath() + context.getTypeSuffix();
                break;
        }

        return new UploadResult(
                context.getAccessUrl(),
                path,
                context.getFilename()
        );
    }
}
