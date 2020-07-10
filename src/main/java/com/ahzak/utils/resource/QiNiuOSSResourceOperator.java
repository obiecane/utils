package com.ahzak.utils.resource;

import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * 七牛云存储
 *
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/7/8 16:16
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
class QiNiuOSSResourceOperator extends AbstractResourceOperator {
    @Override
    public UploadResult upload(Resource resources) throws IOException {
        throw new RuntimeException("暂不支持");
    }

    @Override
    public boolean delete(String path) {
        return false;
    }
}
