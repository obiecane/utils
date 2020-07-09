package com.ahzak.utils.upload;

import cn.hutool.core.util.IdUtil;
import com.ahzak.utils.EncodeUtils;
import com.ahzak.utils.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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
     * 用于删除文件
     * 禁止该接口被外部调用
     */
    @DeleteMapping("/res/upload")
    public void delFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        check(request, response);

        // do delete
    }

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

    private void check(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authToken = request.getHeader("remote-auth-token");
        String requestUrl = request.getRequestURL().toString();
        try {
            String decrypt = EncodeUtils.paramDecrypt("", StringUtils.reverse(authToken));
            if (requestUrl.equalsIgnoreCase(decrypt)) {
                return;
            }
        } catch (Exception ignored) { }
        response.sendError(HttpStatus.NOT_FOUND.value());
    }
}
