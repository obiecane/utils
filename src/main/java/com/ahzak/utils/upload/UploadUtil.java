package com.ahzak.utils.upload;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/7/7 17:21
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
public class UploadUtil {


    public static List<UploadResult> upload(Collection<Resource> resources) throws IOException {
        return Config.getInstance()
                .getStrategy()
                .getUploader()
                .upload(resources);
    }

    public static List<UploadResult> upload(Resource[] resources) throws IOException {
        return upload(Arrays.asList(resources));
    }

}
