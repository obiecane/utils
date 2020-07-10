package com.ahzak.utils.resource;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.ahzak.utils.EncodeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "jeemarket.upload", name = "strategy", havingValue = "LOCAL")
@RestController
public class LocalConfigEndpoint {

    /**
     * 获取本地配置信息
     * 禁止外部调用
     */
    @GetMapping("/res/upload/localConfig")
    public LocalConfigVO getLocalConfig(HttpServletRequest request, HttpServletResponse response) throws IOException {
        check(request, response);
        Config.LocalConfig localConfig = Config.getInstance().getLocal();
        String uuid = IdUtil.fastUUID();
        String validFileFullPath = localConfig.getStoreLocation() + "/ulc/" + uuid + ".ulc";
        FileUtil.touch(validFileFullPath);
        return new LocalConfigVO(validFileFullPath, Config.getInstance().getLocal());
    }

    /**
     * 防止其他调用
     *
     * @param request
     * @param response
     * @return void
     * @author Zhu Kaixiao
     * @date 2020/7/10 8:47
     */
    static void check(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authToken = request.getHeader("remote-auth-token");
        String requestUrl = request.getRequestURL().toString();
        try {
            String decrypt = EncodeUtils.decrypt("tauthr", StringUtils.reverse(authToken));
            if (requestUrl.equalsIgnoreCase(decrypt)) {
                return;
            }
        } catch (Exception ignored) {
        }
        response.sendError(HttpStatus.NOT_FOUND.value());
    }
}
