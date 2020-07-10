package com.ahzak.utils.resource;

import cn.hutool.core.io.FileUtil;
import com.ahzak.utils.EncodeUtils;
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

import java.io.File;
import java.io.IOException;
import java.util.*;
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
class RemoteResourceOperator extends AbstractResourceOperator {

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
                .map(m -> new UploadResult(m.get("url"), m.get("path"), m.get("filename")))
                .collect(Collectors.toList());
        return results;
    }

    @Override
    public UploadResult upload(Resource resources) throws IOException {
        return upload(Collections.singletonList(resources)).get(0);
    }

    @Override
    public List<Boolean> delete(Collection<String> paths) throws IOException {
        if (isSelf()) {
            return UploadStrategy.LOCAL.getUploader().delete(paths);
        }
        // todo 调用删除接口
        String url = String.format("http://%s:%s/res/upload",
                Config.getInstance().getRemote().getHost(),
                Config.getInstance().getRemote().getPort()
        );
        HttpHeaders headers = new HttpHeaders();
        headers.add("remote-auth-token", getRemoteAuth(url));
        headers.setBearerAuth(Optional.ofNullable(SpringMvcUtil.currToken()).orElse(TokenUtil.getSystemToken()));
        HttpEntity<String> requestEntity = new HttpEntity<>(JSONObject.toJSONString(paths), headers);
        ResponseEntity<ArrayList> resEntity = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, ArrayList.class);
        List body = resEntity.getBody();
        return body;
    }

    @Override
    public boolean delete(String path) throws IOException {
        return delete(Collections.singletonList(path)).get(0);
    }

    @Override
    public File download(String dirPath, String path) throws IOException {
        if (isSelf()) {
            return UploadStrategy.LOCAL.getUploader().download(dirPath, path);
        }
        return super.download(dirPath, path);
    }

    private Config.RemoteConfig getRemoteConfig() {
        return Config.getInstance().getRemote();
    }

    /**
     * 替换rt后缀为远程策略的后缀
     * ps
     * 假如远程服务切换了上传策略, 那本地的remote的url前缀就要更换, 这样的话以前的rt=2就会拼接出错
     *
     * @param str
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/7/10 10:18
     */
    @Deprecated
    private String replaceRT(String str) {
        return str.substring(0, str.length() - 4) + Config.getInstance().getStrategy().getUrlParameter();
    }

    private volatile Boolean isSelf = null;
    private Lock lock = new ReentrantLock();

    private boolean isSelf() {
        if (isSelf == null) {
            if (lock.tryLock()) {
                try {
                    String url = String.format("http://%s:%s/res/upload/localConfig",
                            Config.getInstance().getRemote().getHost(),
                            Config.getInstance().getRemote().getPort()
                    );

                    HttpHeaders headers = new HttpHeaders();
                    headers.add("remote-auth-token", getRemoteAuth(url));
                    headers.setBearerAuth(Optional.ofNullable(SpringMvcUtil.currToken()).orElse(TokenUtil.getSystemToken()));
                    HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
                    ResponseEntity<LocalConfigVO> resEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, LocalConfigVO.class);

                    LocalConfigVO localConfigVO = resEntity.getBody();
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
        }

        return isSelf;
    }

    private String getRemoteAuth(String url) {
        String auth = StringUtils.reverse(EncodeUtils.encrypt("tauthr", url));
        return auth;
    }
}
