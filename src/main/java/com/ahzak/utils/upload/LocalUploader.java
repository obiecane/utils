package com.ahzak.utils.upload;

import cn.hutool.core.io.FileUtil;
import org.springframework.core.io.Resource;

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
        UploadContext context = UploadContext.wrap(resource);
        prepareStore(context);

        try (InputStream inputStream = resource.getInputStream()) {
            FileUtil.writeFromStream(inputStream, getLocalConfig().getStoreLocation() + context.getPath());
        }

        return UploadResult.of(context);
    }

    @Override
    protected boolean exist(UploadContext context) {
        return FileUtil.exist(getLocalConfig().getStoreLocation() + context.getPath());
    }

    private Config.LocalConfig getLocalConfig() {
        return Config.getInstance().getLocal();
    }
}
