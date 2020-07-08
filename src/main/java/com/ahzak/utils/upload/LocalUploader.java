package com.ahzak.utils.upload;

import cn.hutool.core.io.IoUtil;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件保存在本地
 *
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/7/7 17:24
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
class LocalUploader extends AbstractUploader {

    @Override
    public UploadResult upload(Resource resource) throws IOException {
        String filename = filename(resource);

        File dest = new File(getLocalConfig().getStoreLocation() + filename(resource));

        try (InputStream inputStream = resource.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(dest)) {
            IoUtil.copy(inputStream, outputStream);
        }

        return new UploadResult(url(filename), path(filename));
    }

    @Override
    protected String url(String filename) {
        return getLocalConfig().getUrlPrefix() + path(filename);
    }

    private Config.LocalConfig getLocalConfig() {
        return Config.getInstance().getLocal();
    }
}
