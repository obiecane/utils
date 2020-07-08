package com.ahzak.utils.upload;


import org.springframework.core.io.Resource;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/7/7 17:56
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
abstract class AbstractUploader implements Uploader {

    protected String filename(Resource resource) {
        return System.currentTimeMillis() + resource.getFilename();
    }

    protected String path(String filename) {
        return "/" + filename;
    }

    protected abstract String url(String filename);

}
