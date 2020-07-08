package com.ahzak.utils.upload;

import com.ahzak.utils.JcResult;
import com.ahzak.utils.TokenUtil;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 调用其他服务接口, 实现上传
 *
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/7/8 9:11
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
class RemoteUploader extends AbstractUploader {

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    protected String url(String filename) {
        return null;
    }

    @Override
    public List<UploadResult> upload(Collection<Resource> resources) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("multipart/form-data");
        // 设置请求的格式类型
        headers.setContentType(type);
        headers.setBearerAuth(TokenUtil.getSystemToken());

        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        for (Resource resource : resources) {
            form.add("files", resource);
        }

        String uploadUrl = String.format("http://%s:%s/res/upload", getRemoteConfig().getHost(), getRemoteConfig().getPort());
        HttpEntity<MultiValueMap<String, Object>> files = new HttpEntity<>(form, headers);
        ResponseEntity<JcResult> responseResponseEntity = restTemplate.postForEntity(uploadUrl, files, JcResult.class);
        JcResult jcResult = responseResponseEntity.getBody();
        if (!jcResult.success()) {
            throw new IOException(jcResult.getMessage());
        }
        List<Map<String, String>> data = (List<Map<String, String>>) jcResult.getData();
        List<UploadResult> results = data.stream()
                .map(m -> new UploadResult(m.get("url"), m.get("path")))
                .collect(Collectors.toList());
        return results;
    }

    @Override
    public UploadResult upload(Resource resources) throws IOException {
        return upload(Collections.singletonList(resources)).get(0);
    }

    private Config.RemoteConfig getRemoteConfig() {
        return Config.getInstance().getRemote();
    }
}
