package com.ahzak.utils.upload;

import cn.hutool.extra.ftp.Ftp;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/7/7 18:21
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
class FtpUploader extends AbstractUploader {

    @Override
    public UploadResult upload(Resource resource) throws IOException {
        UploadContext context = UploadContext.wrap(resource);

        try (Ftp ftp = new Ftp(getFtpConfig().getHost(), getFtpConfig().getPort(), getFtpConfig().getUsername(), getFtpConfig().getPassword())) {
            ftp.init();
            context.attach("ftp", ftp);
            prepareStore(context);

            try (InputStream in = resource.getInputStream()) {
                ftp.upload(getFtpConfig().getDirPrefix() + context.getDir(), context.getFilename(), in);
            }
        }

        return UploadResult.of(context);
    }

    @Override
    protected boolean exist(UploadContext context) {
        Ftp ftp = context.getAttach("ftp");
        return ftp.existFile(getFtpConfig().getDirPrefix() + context.getDir() + context.getFilename());
    }

    private Config.FtpConfig getFtpConfig() {
        return Config.getInstance().getFtp();
    }

}
