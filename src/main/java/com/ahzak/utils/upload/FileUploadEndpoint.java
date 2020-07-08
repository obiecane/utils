package com.ahzak.utils.upload;

import cn.hutool.core.lang.Assert;
import com.ahzak.utils.JcResult;
import com.ahzak.utils.MultipartFileResource;
import com.ahzak.utils.exception.GlobalException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/7/8 10:05
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "jeemarket.upload", name = "strategy", havingValue = "LOCAL")
@RestController
class FileUploadEndpoint {

    @PostMapping("/res/upload")
    public JcResult<List<UploadResult>> fileUpload(@RequestParam("files") MultipartFile[] files) throws IOException {
        Assert.notEmpty(files, "请选择文件");

        List<Resource> resourceList = Arrays.stream(files)
                .peek(mf -> {
                    if (mf.isEmpty()) {
                        throw new GlobalException("上传失败");
                    }
                })
                .map(MultipartFileResource::new)
                .collect(Collectors.toList());

        List<UploadResult> uploadResults = UploadUtil.upload(resourceList);

        return JcResult.okData(uploadResults);
    }

}



