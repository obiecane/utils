package com.ahzak.utils.resource;


import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/7/8 17:01
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
class UploadContext {

    @Getter
    private Resource resource;

    @Getter
    @Setter
    private String filename;

    @Getter
    @Setter
    private String path;

    @Getter
    @Setter
    private String dir;

    private Map<String, Object> attachMap;

    {
        attachMap = new HashMap<>();
        attachMap.put("nameNo", 0);
    }

    void attach(String key, Object value) {
        attachMap.put(key, value);
    }

    <V> V getAttach(String key) {
        return (V) attachMap.get(key);
    }

    private UploadContext(Resource resource) {
        this.resource = resource;
        this.filename = resource.getFilename();
    }

    public static UploadContext wrap(Resource resource) {
        return new UploadContext(resource);
    }

    public String getPath() {
        return getDir() + getFilename();
    }

    public String getTypeSuffix() {
        return "?" + Config.getInstance().getStrategy().getUrlParameter();
    }

    public String getAccessUrl() {
        return ResourceUtil.getUrlPrefix() + getPath();
    }
}
