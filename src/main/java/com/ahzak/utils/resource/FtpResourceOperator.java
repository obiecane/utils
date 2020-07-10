package com.ahzak.utils.resource;

import cn.hutool.extra.ftp.Ftp;
import com.google.common.collect.Lists;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/7/7 18:21
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
class FtpResourceOperator extends AbstractResourceOperator {

    @Override
    public List<UploadResult> upload(Collection<Resource> resources) throws IOException {
        List<UploadResult> results = Lists.newArrayListWithCapacity(resources.size());

        try (Ftp ftp = openFtp()) {
            String homePath = ftp.pwd();
            for (Resource resource : resources) {
                UploadContext context = UploadContext.wrap(resource);
                context.attach("ftp", ftp);
                prepareStore(context);

                try (InputStream in = resource.getInputStream()) {
                    ftp.upload(getFtpConfig().getDirPrefix() + context.getDir(), context.getFilename(), in);
                    ftp.cd(homePath);
                }
                results.add(UploadResult.of(context));
            }

        }
        return results;
    }

    @Override
    public UploadResult upload(Resource resource) throws IOException {
        return upload(Collections.singletonList(resource)).get(0);
    }

    @Override
    public List<Boolean> delete(Collection<String> paths) throws IOException {
        List<Boolean> booleans = Lists.newArrayListWithCapacity(paths.size());
        try (Ftp ftp = openFtp()) {
            String homePath = ftp.pwd();
            for (String path : paths) {
                booleans.add(ftp.delFile(homePath + getFtpConfig().getDirPrefix() + clearPath(path)));
                ftp.cd(homePath);
            }
        }
        return booleans;
    }

    @Override
    public boolean delete(String path) throws IOException {
        return delete(Collections.singletonList(path)).get(0);
    }

    private Ftp openFtp() {
        Ftp ftp = new Ftp(getFtpConfig().getHost(), getFtpConfig().getPort(), getFtpConfig().getUsername(), getFtpConfig().getPassword());
        ftp.init();
        return ftp;
    }

    @Override
    protected boolean exist(UploadContext context) {
        Ftp ftp = context.getAttach("ftp");
        return ftp.existFile(getFtpConfig().getDirPrefix() + context.getDir() + context.getFilename());
    }

    private Config.FtpConfig getFtpConfig() {
        return Config.getInstance().getFtp();
    }


    private String getHomePath(Ftp ftp) {

        String homePath = ftp.pwd() + "/";
        if (getFtpConfig().getDirPrefix().startsWith(homePath)) {
            homePath = "";
        }
        return homePath;
    }

}
