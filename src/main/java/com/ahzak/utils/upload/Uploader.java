package com.ahzak.utils.upload;

import com.ahzak.utils.Assert;
import com.ahzak.utils.LambdaUtil;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/7/7 17:23
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
interface Uploader {


    default List<UploadResult> upload(Collection<Resource> resources) throws IOException {
        Assert.notEmpty(resources, "请选择文件");

        List<UploadResult> results = resources.stream()
                .map(r -> {
                    try {
                        return upload(r);
                    } catch (IOException e) {
                        return LambdaUtil.doThrow(e);
                    }
                })
                .collect(Collectors.toList());

        return results;
    }

    UploadResult upload(Resource resources) throws IOException;

}
