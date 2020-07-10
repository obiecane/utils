package com.ahzak.utils.resource;


import com.ahzak.utils.DateUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;

/**
 * 操作者
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/7/7 17:56
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
abstract class AbstractResourceOperator implements ResourceOperator {

    protected String nextFilename(UploadContext context) {
        if (StringUtils.isBlank(context.getResource().getFilename())) {
            return Long.toHexString(DateUtil.currentSeconds() - 1592668800) + ".data";
        }

        String f = "%s (%s)%s%s";
        int nameNo = context.getAttach("nameNo");
        ++nameNo;
        context.attach("nameNo", nameNo);

        // 后缀
        String originFilename = context.getResource().getFilename();
        String baseName = FilenameUtils.getBaseName(originFilename);
        String extension = FilenameUtils.getExtension(originFilename);
        String dot = StringUtils.isBlank(extension) ? "" : ".";
        return String.format(f, baseName, nameNo, dot, extension);
    }

    protected String dir() {
        return DateUtil.format(LocalDate.now(), "/yyyy/MM/dd/");
    }

    /**
     * 设置保存的文件名
     *
     * @param context
     * @author Zhu Kaixiao
     * @date 2020/7/8 17:18
     */
    protected void prepareStore(UploadContext context) {
        context.setDir(dir());
        while (exist(context)) {
            context.setFilename(nextFilename(context));
        }
    }

    protected String clearPath(String path) {
        if (path.indexOf('?') != -1) {
            return path.substring(0, path.indexOf("?"));
        }
        return path;
    }

    protected boolean exist(UploadContext context) {
        return false;
    }
}
