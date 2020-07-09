package com.ahzak.utils.upload;

import cn.hutool.http.HttpUtil;
import com.ahzak.utils.EncodeUtils;
import com.ahzak.utils.FileUtil;
import com.ahzak.utils.JcResult;
import com.ahzak.utils.TokenUtil;
import com.ahzak.utils.spring.SpringMvcUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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
    public List<UploadResult> upload(Collection<Resource> resources) throws IOException {
        // 如果远端服务也在同一台机器上， 那么转为本地文件操作
        if (isSelf()) {
            return UploadStrategy.LOCAL.getUploader().upload(resources);
        }
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("multipart/form-data");
        // 设置请求的格式类型
        headers.setContentType(type);
        headers.setBearerAuth(Optional.ofNullable(SpringMvcUtil.currToken()).orElse(TokenUtil.getSystemToken()));

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
                .map(m -> new UploadResult(m.get("url"), replaceRT(m.get("path")), m.get("filename")))
                .collect(Collectors.toList());
        return results;
    }


    private String replaceRT(String str) {
        return str.substring(0, str.length() - 4) + Config.getInstance().getStrategy().getUrlParameter();
    }

    @Override
    public UploadResult upload(Resource resources) throws IOException {
        return upload(Collections.singletonList(resources)).get(0);
    }

    private Config.RemoteConfig getRemoteConfig() {
        return Config.getInstance().getRemote();
    }

    private volatile Boolean isSelf = null;
    private Lock lock = new ReentrantLock();
    private boolean isSelf() {
        if (isSelf != null) {
            return isSelf;
        }
        if (lock.tryLock()) {
            try {
                String url = String.format("http://%s:%s/res/upload/localConfig",
                        Config.getInstance().getRemote().getHost(),
                        Config.getInstance().getRemote().getPort()
                );
                String body = HttpUtil
                        .createGet(url)
                        .header("remote-auth-token", getRemoteAuth(url))
                        .execute()
                        .body();
                LocalConfigVO localConfigVO = JSONObject.parseObject(body, LocalConfigVO.class);
                String validFileFullPath = localConfigVO.getValidFileFullPath();
                // 是在同一台机器上
                if (FileUtil.exist(validFileFullPath)) {
                    isSelf = true;
                    // 覆盖本地策略的配置
                    Config.getInstance().setLocal(localConfigVO.getLocalConfig());
                    FileUtil.del(validFileFullPath);
                } else {
                    isSelf = false;
                }
            } catch (Exception ignored) {
                isSelf = false;
            } finally {
                lock.unlock();
            }
        }
        return false;
    }

    private String getRemoteAuth(String url) {
        String auth = StringUtils.reverse(EncodeUtils.paramEncrypt("tauthr", url));
        return auth;
    }
}
