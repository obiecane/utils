package com.ahzak.utils.email;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.*;
import org.apache.commons.mail.resolver.DataSourceCompositeResolver;
import org.apache.commons.mail.resolver.DataSourceFileResolver;
import org.apache.commons.mail.resolver.DataSourceUrlResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 邮件发送工具类
 * @author Zhu Kaixiao
 * @version 1.0
 * @date 2020/6/28 16:56
 * @copyright 江西金磊科技发展有限公司 All rights reserved. Notice
 * 仅限于授权后使用，禁止非授权传阅以及私自用于商业目的。
 */
@Slf4j
@Component
public class EmailUtil {

    @Autowired
    private EmailProperties _emailProperties;
    private static EmailProperties emailProperties;

    @PostConstruct
    private void init() {
        emailProperties = _emailProperties;
    }


    /**
     * 发送纯文本邮件
     *
     * @param hostName 邮箱服务器
     * @param username 用户名
     * @param password 密码
     * @param fromName 发信名
     * @param targets  收信地址
     * @param bccColl  密送地址
     * @param ccColl   抄送地址
     * @param subject  主题
     * @param content  内容
     * @author Zhu Kaixiao
     * @date 2020/6/29 11:05
     */
    public static void sendText(
            String hostName, String username, String password,
            String fromName,
            Collection<String> targets, Collection<String> bccColl, Collection<String> ccColl,
            String subject, String content
    ) throws EmailException {
        try {
            doSend(new SimpleEmail(), hostName, username, password, fromName, targets, bccColl, ccColl, subject, content, Collections.emptyList());
        } catch (IOException e) {
        }
    }

    /**
     * 发送纯文本邮件
     *
     * @param fromName 发信名
     * @param targets  收信地址
     * @param bccColl  密送地址
     * @param ccColl   抄送地址
     * @param subject  主题
     * @param content  内容
     * @author Zhu Kaixiao
     * @date 2020/6/29 11:08
     */
    public static void sendText(
            String fromName,
            Collection<String> targets, Collection<String> bccColl, Collection<String> ccColl,
            String subject, String content
    ) throws EmailException {
        sendText(emailProperties.getHostName(), emailProperties.getUsername(), emailProperties.getPassword(), fromName, targets, bccColl, ccColl, subject, content);
    }

    /**
     * 发送纯文本邮件
     *
     * @param targets 收信地址
     * @param bccColl 密送地址
     * @param ccColl  抄送地址
     * @param subject 主题
     * @param content 内容
     * @author Zhu Kaixiao
     * @date 2020/6/29 11:09
     */
    public static void sendText(
            Collection<String> targets, Collection<String> bccColl, Collection<String> ccColl,
            String subject, String content
    ) throws EmailException {
        sendText(emailProperties.getHostName(), emailProperties.getUsername(), emailProperties.getPassword(), emailProperties.getFromName(), targets, bccColl, ccColl, subject, content);
    }

    /**
     * 发送纯文本邮件
     *
     * @param targets 收信地址
     * @param subject 主题
     * @param content 内容
     * @author Zhu Kaixiao
     * @date 2020/6/29 11:09
     */
    public static void sendText(
            Collection<String> targets, String subject, String content
    ) throws EmailException {
        sendText(emailProperties.getHostName(), emailProperties.getUsername(), emailProperties.getPassword(), emailProperties.getFromName(), targets, Collections.emptyList(), Collections.emptyList(), subject, content);
    }


    /**
     * 发送html格式邮件
     *
     * @param hostName  邮箱服务器
     * @param username  用户名
     * @param password  密码
     * @param fromName  发信名
     * @param targets   收信地址
     * @param bccColl   密送地址
     * @param ccColl    抄送地址
     * @param subject   主题
     * @param content   内容
     * @param resources 附件
     * @author Zhu Kaixiao
     * @date 2020/6/29 11:09
     */
    public static void sendHtml(
            String hostName, String username, String password,
            String fromName,
            Collection<String> targets, Collection<String> bccColl, Collection<String> ccColl,
            String subject, String content, Collection<Resource> resources
    ) throws EmailException, IOException {
        doSend(new ImageHtmlEmail(), hostName, username, password, fromName, targets, bccColl, ccColl, subject, content, resources);
    }

    /**
     * 发送html格式邮件
     *
     * @param hostName 邮箱服务器
     * @param username 用户名
     * @param password 密码
     * @param fromName 发信名
     * @param targets  收信地址
     * @param bccColl  密送地址
     * @param ccColl   抄送地址
     * @param subject  主题
     * @param content  内容
     * @author Zhu Kaixiao
     * @date 2020/6/29 11:10
     */
    public static void sendHtml(
            String hostName, String username, String password,
            String fromName,
            Collection<String> targets, Collection<String> bccColl, Collection<String> ccColl,
            String subject, String content
    ) throws EmailException {
        try {
            sendHtml(hostName, username, password, fromName, targets, bccColl, ccColl, subject, content, Collections.emptyList());
        } catch (IOException e) {
        }
    }


    /**
     * 使用配置文件中的账户信息发送html格式邮件
     *
     * @param fromName  发信名
     * @param targets   收信地址
     * @param bccColl   密送地址
     * @param ccColl    抄送地址
     * @param subject   主题
     * @param content   内容
     * @param resources 附件
     * @author Zhu Kaixiao
     * @date 2020/6/29 11:11
     */
    public static void sendHtml(
            String fromName,
            Collection<String> targets, Collection<String> bccColl, Collection<String> ccColl,
            String subject, String content, Collection<Resource> resources
    ) throws EmailException, IOException {
        sendHtml(emailProperties.getHostName(), emailProperties.getUsername(), emailProperties.getPassword(), fromName, targets, bccColl, ccColl, subject, content, resources);
    }


    /**
     * 使用配置文件中的账户信息发送html格式邮件
     *
     * @param targets   收信地址
     * @param bccColl   密送地址
     * @param ccColl    抄送地址
     * @param subject   主题
     * @param content   内容
     * @param resources 附件
     * @author Zhu Kaixiao
     * @date 2020/6/29 11:12
     */
    public static void sendHtml(
            Collection<String> targets, Collection<String> bccColl, Collection<String> ccColl,
            String subject, String content, Collection<Resource> resources
    ) throws EmailException, IOException {
        sendHtml(emailProperties.getFromName(), targets, bccColl, ccColl, subject, content, resources);
    }

    /**
     * 使用配置文件中的账户信息发送html格式邮件
     *
     * @param targets   收信地址
     * @param ccColl    抄送地址
     * @param subject   主题
     * @param content   内容
     * @param resources 附件
     * @author Zhu Kaixiao
     * @date 2020/6/29 11:13
     */
    public static void sendHtml(
            Collection<String> targets, Collection<String> ccColl,
            String subject, String content, Collection<Resource> resources
    ) throws EmailException, IOException {
        sendHtml(emailProperties.getFromName(), targets, Collections.emptyList(), ccColl, subject, content, resources);
    }

    /**
     * 使用配置文件中的账户信息发送html格式邮件
     *
     * @param targets   收信地址
     * @param subject   主题
     * @param content   内容
     * @param resources 附件
     * @author Zhu Kaixiao
     * @date 2020/6/29 11:13
     */
    public static void sendHtml(
            Collection<String> targets,
            String subject, String content, Collection<Resource> resources
    ) throws EmailException, IOException {
        sendHtml(emailProperties.getFromName(), targets, Collections.emptyList(), Collections.emptyList(), subject, content, resources);
    }

    /**
     * 使用配置文件中的账户信息发送html格式邮件
     *
     * @param targets 收信地址
     * @param subject 主题
     * @param content 内容
     * @author Zhu Kaixiao
     * @date 2020/6/29 11:13
     */
    public static void sendHtml(
            Collection<String> targets,
            String subject, String content
    ) throws EmailException {
        try {
            sendHtml(emailProperties.getFromName(), targets, Collections.emptyList(), Collections.emptyList(), subject, content, Collections.emptyList());
        } catch (IOException e) {
        }
    }

    /**
     * 发送html格式邮件
     *
     * @param hostName 邮箱服务器
     * @param username 用户名
     * @param password 密码
     * @param fromName 发信名
     * @param targets  收信地址
     * @param subject  主题
     * @param content  内容
     * @author Zhu Kaixiao
     * @date 2020/6/29 11:14
     */
    public static void sendHtml(
            String hostName, String username, String password,
            String fromName,
            Collection<String> targets, String subject, String content
    ) throws EmailException {
        try {
            sendHtml(hostName, username, password, fromName, targets, subject, content, Collections.emptyList());
        } catch (IOException ignore) {
            // 因为没有附件 所以没有IOException
        }
    }

    /**
     * 发送html格式邮件
     *
     * @param hostName 邮箱服务器
     * @param username 用户名
     * @param password 密码
     * @param fromName 发信名
     * @param targets  收信地址
     * @param cc       抄送
     * @param subject  主题
     * @param content  内容
     * @author Zhu Kaixiao
     * @date 2020/6/29 11:18
     */
    public static void sendHtml(
            String hostName, String username, String password,
            String fromName,
            Collection<String> targets, Collection<String> cc,
            String subject, String content
    ) throws EmailException {
        try {
            sendHtml(hostName, username, password, fromName, targets, Collections.emptyList(), cc, subject, content, Collections.emptyList());
        } catch (IOException ignore) {
            // 因为没有附件 所以没有IOException
        }
    }


    /**
     * 发送html格式邮件
     *
     * @param hostName           邮箱服务器
     * @param username           用户名
     * @param password           密码
     * @param fromName           发信名
     * @param targets            收信地址
     * @param subject            主题
     * @param content            内容
     * @param attachmentResource 附件
     * @author Zhu Kaixiao
     * @date 2020/6/29 11:14
     */
    public static void sendHtml(
            String hostName, String username, String password,
            String fromName,
            Collection<String> targets, String subject, String content,
            Collection<Resource> attachmentResource
    ) throws EmailException, IOException {
        doSend(new ImageHtmlEmail(), hostName, username, password, fromName, targets, Collections.emptyList(), Collections.emptyList(), subject, content, attachmentResource);
    }

    /**
     * 使用配置文件中的账户信息发送html格式邮件
     *
     * @param fromName 发信名
     * @param targets  收信地址
     * @param subject  主题
     * @param content  内容
     * @author Zhu Kaixiao
     * @date 2020/6/29 11:15
     */
    public static void sendHtml(
            String fromName,
            Collection<String> targets,
            String subject, String content
    ) throws EmailException {
        sendHtml(emailProperties.getHostName(), emailProperties.getUsername(), emailProperties.getPassword(), fromName, targets, subject, content);
    }

    /**
     * 使用配置文件中的账户信息发送html格式邮件
     *
     * @param fromName 发信名
     * @param targets  收信地址
     * @param cc       抄送
     * @param subject  主题
     * @param content  内容
     * @author Zhu Kaixiao
     * @date 2020/6/29 11:17
     */
    public static void sendHtml(
            String fromName,
            Collection<String> targets,
            Collection<String> cc,
            String subject, String content
    ) throws EmailException {
        sendHtml(emailProperties.getHostName(), emailProperties.getUsername(), emailProperties.getPassword(), fromName, targets, cc, subject, content);
    }

    /**
     * 使用配置文件中的账户信息发送html格式邮件
     *
     * @param fromName 发信名
     * @param targets  收信地址
     * @param bcc      密送地址
     * @param cc       抄送地址
     * @param subject  主题
     * @param content  内容
     * @author Zhu Kaixiao
     * @date 2020/6/29 11:15
     */
    public static void sendHtml(
            String fromName,
            Collection<String> targets, Collection<String> bcc, Collection<String> cc,
            String subject, String content
    ) throws EmailException {
        sendHtml(emailProperties.getHostName(), emailProperties.getUsername(), emailProperties.getPassword(), fromName, targets, bcc, cc, subject, content);
    }


    /**
     * 使用配置文件中的账户信息发送html格式邮件
     *
     * @param fromName           发信名
     * @param targets            收信地址
     * @param subject            主题
     * @param content            内容
     * @param attachmentResource 附件
     * @author Zhu Kaixiao
     * @date 2020/6/29 11:16
     */
    public static void sendHtml(
            String fromName,
            Collection<String> targets, String subject, String content,
            Collection<Resource> attachmentResource
    ) throws EmailException, IOException {
        sendHtml(emailProperties.getHostName(), emailProperties.getUsername(), emailProperties.getPassword(), fromName, targets, subject, content, attachmentResource);
    }

    /**
     * 使用配置文件中的账户信息发送html格式邮件
     *
     * @param targets 收信地址
     * @param cc      抄送地址
     * @param subject 主题
     * @param content 内容
     * @author Zhu Kaixiao
     * @date 2020/6/29 11:16
     */
    public static void sendHtml(Collection<String> targets, Collection<String> cc, String subject, String content) throws EmailException {
        sendHtml(emailProperties.getFromName(), targets, cc, subject, content);
    }

    /**
     * 使用配置文件中的账户信息发送html格式邮件
     *
     * @param targets 收信地址
     * @param content 内容
     * @author Zhu Kaixiao
     * @date 2020/6/29 11:20
     */
    public static void sendHtml(Collection<String> targets, String content) throws EmailException {
        sendHtml(emailProperties.getFromName(), targets, "", content);
    }

    /**
     * 使用配置文件中的账户信息发送html格式邮件
     *
     * @param targets            收信地址
     * @param content            内容
     * @param attachmentResource 附件
     * @author Zhu Kaixiao
     * @date 2020/6/29 11:20
     */
    public static void sendHtml(Collection<String> targets, String content, Collection<Resource> attachmentResource) throws EmailException, IOException {
        sendHtml(emailProperties.getFromName(), targets, "", content, attachmentResource);
    }


    private static void doSend(
            Email email,
            String hostName, String username, String password,
            String fromName,
            Collection<String> targets, Collection<String> bccColl, Collection<String> ccColl,
            String subject, String content, Collection<Resource> resources
    ) throws EmailException, IOException {
        targets = Optional.ofNullable(targets).orElse(Collections.emptyList()).stream().distinct().collect(Collectors.toList());
        ccColl = Optional.ofNullable(ccColl).orElse(Collections.emptyList());
        bccColl = Optional.ofNullable(bccColl).orElse(Collections.emptyList());
        resources = Optional.ofNullable(resources).orElse(Collections.emptyList());

        email.setHostName(hostName);
        //邮件服务器验证：用户名/密码
        email.setAuthentication(username, password);
        // 必须放在前面，否则乱码
        email.setCharset("UTF-8");

        // 添加收信人地址
        for (String target : targets) {
            email.addTo(target);
        }

        // 添加密送人地址
        for (String bcc : bccColl) {
            email.addBcc(bcc);
        }

        // 添加抄送人地址
        for (String cc : ccColl) {
            email.addCc(cc);
        }

        // 设置发信人
        email.setFrom(username, fromName);

        // 设置图片解析器, 解析邮件内容中插入的图片
        if (email instanceof ImageHtmlEmail) {
            // 解析本地图片和网络图片都有的html文件重点就是下面这两行；
            // ImageHtmlEmail通过setDataSourceResolver来识别并嵌入图片
            // 查看DataSourceResolver的继承结构发现有几个好用的子类
            DataSourceResolver[] dataSourceResolvers;
            try {
                dataSourceResolvers = new DataSourceResolver[]{
                        // 添加DataSourceFileResolver用于解析本地图片
                        new DataSourceFileResolver(),
                        // 添加DataSourceUrlResolver用于解析网络图片，注意：new URL("http://")
                        new DataSourceUrlResolver(new URL("http://"))
                };
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

            //DataSourceCompositeResolver类可以加入多个DataSourceResolver,
            //把需要的DataSourceResolver放到一个数组里传进去就可以了；
            ((ImageHtmlEmail) email).setDataSourceResolver(new DataSourceCompositeResolver(dataSourceResolvers));
        }

        // 设置附件
        if (email instanceof MultiPartEmail) {
            for (Resource resource : resources) {
                // 创建一个attachment（附件）对象
                EmailAttachment attachment = new EmailAttachment();
                //设置上传附件的地址
//        attachment.setPath("F:\\图片图标\\Connect_logo_7.png");
                attachment.setURL(resource.getURL());
                attachment.setDisposition(EmailAttachment.ATTACHMENT);
                //这个描述可以随便写
                attachment.setDescription(resource.getDescription());
                //这个名称要注意和文件格式一致,这将是接收人下载下来的文件名称
                attachment.setName(resource.getFilename());
                ((MultiPartEmail) email).attach(attachment);
            }
        }


        // 设置主题
        email.setSubject(subject);

        // 设置邮件内容
        email.setMsg(content);

        // 发送
        email.send();
    }


    public static void main(String[] args) throws Exception {
        String msg = "这是一张用于测试的图片，请查收。 <img src=\"F:\\图片图标\\2.png\"> "
                + " <img src=\"https://commons.apache.org/proper/commons-email/images/commons-logo.png\">";

        List<String> targets = Arrays.asList("690710726@qq.com", "3617246657@qq.com", "3617246657@qq.com");

        List<String> bcc = Arrays.asList("438687152@qq.com");

        List<String> cc = Arrays.asList("2498756079@qq.com");

        List<Resource> resources = new ArrayList<>();
        resources.add(new FileSystemResource("C:\\Users\\ASUS\\Desktop\\test\\2020-5.txt"));
        resources.add(new UrlResource("https://csdnimg.cn/cdn/content-toolbar/csdn-logo.png?v=20200416.1"));

        String hostName = "smtp.qq.com";
        String username = "444323306@qq.com";
        String password = "lppkefbgjjppbhbc";

        String subject = "邮件测试";

        String fromName = "金磊云平台";


        EmailProperties properties = new EmailProperties();
        properties.setFromName(fromName);
        properties.setHostName(hostName);
        properties.setPassword(password);
        properties.setUsername(username);
        emailProperties = properties;
        // -------------------------------------------------------------------------------------------------------------
        // -------------------------------------------------------------------------------------------------------------
        // -------------------------------------------------------------------------------------------------------------


//        doSend(
//                new ImageHtmlEmail(),
//                hostName, username, password,
//                fromName,
//                targets, bcc, cc,
//                subject, msg, resources
//        );
//        sendHtml("smtp.qq.com", "444323306@qq.com", "lppkefbgjjppbhbc", "金磊云平台", strings, "发信主题", msg);

        sendHtml(targets, cc, subject, msg);

    }

}
