//package com.ahzak.utils;
//
//import com.google.common.net.HttpHeaders;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang.StringUtils;
//import org.apache.commons.lang3.ArrayUtils;
//import org.apache.commons.lang3.Validate;
//import org.springframework.util.Assert;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//import org.springframework.web.util.UrlPathHelper;
//
//import javax.servlet.ServletRequest;
//import javax.servlet.http.Cookie;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.UnsupportedEncodingException;
//import java.net.URLDecoder;
//import java.util.*;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import static com.jeecms.jspgouexclusive.constants.WebConstants.POST;
//import static com.jeecms.jspgouexclusive.constants.WebConstants.UTF8;
//
//
///**
// * HttpServletRequest帮助类
// */
//@Slf4j
//public class RequestUtils {
//
//    /**
//     * 获取访问者IP
//     * <p>
//     * 在一般情况下使用Request.getRemoteAddr()即可，但是经过nginx等反向代理软件后，这个方法会失效。
//     * <p>
//     * 本方法先从Header中获取X-Real-IP，如果不存在再从X-Forwarded-For获得第一个IP(用,分割)，
//     * 如果还不存在则调用Request .getRemoteAddr()。
//     *
//     * @param request
//     * @return
//     */
//    public static String getIpAddr(HttpServletRequest request) {
//        String ip = request.getHeader("X-Real-IP");
//        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
//            if (ip.contains("../") || ip.contains("..\\")) {
//                return "";
//            }
//            return ip;
//        }
//        ip = request.getHeader("X-Forwarded-For");
//        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
//            // 多次反向代理后会有多个IP值，第一个为真实IP。
//            int index = ip.indexOf(',');
//            if (index != -1) {
//                ip = ip.substring(0, index);
//            }
//            if (ip.contains("../") || ip.contains("..\\")) {
//                return "";
//            }
//            return ip;
//        } else {
//            ip = request.getRemoteAddr();
//            if (ip.contains("../") || ip.contains("..\\")) {
//                return "";
//            }
//            if (ip.equals("0:0:0:0:0:0:0:1")) {
//                ip = "127.0.0.1";
//            }
//            return ip;
//        }
//
//    }
//
//    /**
//     * 获得当的访问路径
//     * <p>
//     * HttpServletRequest.getRequestURL+"?"+HttpServletRequest.getQueryString
//     *
//     * @param request
//     * @return
//     */
//    public static String getLocation(HttpServletRequest request) {
//        UrlPathHelper helper = new UrlPathHelper();
//        StringBuffer buff = request.getRequestURL();
//        String uri = request.getRequestURI();
//        String origUri = helper.getOriginatingRequestUri(request);
//        buff.replace(buff.length() - uri.length(), buff.length(), origUri);
//        String queryString = helper.getOriginatingQueryString(request);
//        if (queryString != null) {
//            buff.append("?").append(queryString);
//        }
//        return buff.toString();
//    }
//
//    /**
//     * 设置让浏览器弹出下载对话框的Header.
//     *
//     * @param filename
//     *            下载后的文件名.
//     */
//    public static void setDownloadHeader(HttpServletResponse response, String filename) {
//        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
//    }
//
//
//    public static final String COMMON_PATH_SEPARATE = "-";
//    public static final int PORT_DEF = 80;
//
//    private static final String NUKNOWN = "unknown";
//    private static final String ESCAPE_AND_STR = "&amp;";
//    private static final String[] ADDR_HEADER = {"X-Forwarded-For", "Proxy-Client-IP",
//            "WL-Proxy-Client-IP", "X-Real-IP"};
//    /**
//     * 私有IP：A类  10.0.0.0-10.255.255.255  
//     *              B类  172.16.0.0-172.31.255.255  
//     *              C类  192.168.0.0-192.168.255.255           当然，还有127这个网段是环回地址
//     * localhost  
//     */
//    static List<Pattern> ipFilterRegexList = new ArrayList<>();
//
//    static {
//        Set<String> ipFilter = new HashSet<String>();
//        ipFilter.add(
//                "^10\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])"
//                        + "\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])"
//                        + "\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])$");
//        // B类地址范围: 172.16.0.0---172.31.255.255
//        ipFilter.add(
//                "^172\\.(1[6789]|2[0-9]|3[01])\\"
//                        + ".(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])\\"
//                        + ".(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])$");
//        // C类地址范围: 192.168.0.0---192.168.255.255
//        ipFilter.add(
//                "^192\\.168\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])\\"
//                        + ".(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[0-9])$");
//        ipFilter.add("127.0.0.1");
//        ipFilter.add("0.0.0.0");
//        ipFilter.add("localhost");
//        for (String tmp : ipFilter) {
//            ipFilterRegexList.add(Pattern.compile(tmp));
//        }
//    }
//
//    /**
//     * 获取当前访问URL （含协议、域名、端口号[80端口默认忽略]、项目名）
//     *
//     * @param request HttpServletRequest
//     * @Title: getServerUrl
//     * @return: String
//     */
//    public static String getServerUrl(HttpServletRequest request) {
//        // 访问协议
//        String protocol = request.getScheme();
//        // 访问域名
//        String serverName = request.getServerName();
//        // 访问端口号
//        int port = request.getServerPort();
//        // 访问项目名
//        String contextPath = request.getContextPath();
//        String url = "%s://%s%s%s";
//        String portStr = "";
//        if (port != PORT_DEF) {
//            portStr += ":" + port;
//        }
//        return String.format(url, protocol, serverName, portStr, contextPath);
//
//    }
//
//    /**
//     * 获得真实IP地址。在使用了反向代理时，直接用HttpServletRequest.getRemoteAddr()无法获取客户真实的IP地址。
//     *
//     * @param request ServletRequest
//     * @return
//     */
//    public static String getRemoteAddr(ServletRequest request) {
//        String addr = null;
//        if (request instanceof HttpServletRequest) {
//            HttpServletRequest hsr = (HttpServletRequest) request;
//            for (String header : ADDR_HEADER) {
//                if (org.apache.commons.lang3.StringUtils.isBlank(addr) || NUKNOWN.equalsIgnoreCase(addr)) {
//                    addr = hsr.getHeader(header);
//                } else {
//                    break;
//                }
//            }
//        }
//        if (org.apache.commons.lang3.StringUtils.isBlank(addr) || NUKNOWN.equalsIgnoreCase(addr)) {
//            addr = request.getRemoteAddr();
//        } else {
//            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按','分割
//            if (addr != null) {
//                int i = addr.indexOf(",");
//                if (i > 0) {
//                    addr = addr.substring(0, i);
//                }
//            }
//
//        }
//        return addr;
//    }
//
//    /**
//     * 判断IP是否内网IP
//     *
//     * @param ip IPV4 IP
//     * @Title: ipIsInner
//     * @return: boolean
//     */
//    public static boolean ipIsInner(String ip) {
//        Boolean isInnerIp = false;
//        for (Pattern tmp : ipFilterRegexList) {
//            Matcher matcher = tmp.matcher(ip);
//            if (matcher.find()) {
//                isInnerIp = true;
//                break;
//            }
//        }
//        return isInnerIp;
//    }
//
//
//    /**
//     * 获取当前会话中的HttpServletRequest
//     *
//     * @Title: getHttpServletRequest
//     * @return: HttpServletRequest
//     */
//    public static HttpServletRequest getHttpServletRequest() {
//        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        if (sra != null) {
//            return sra.getRequest();
//        }
//        return null;
//    }
//
//
//    /**
//     * 获取请求参数
//     *
//     * @param request HttpServletRequest
//     * @Title: getQueryParams
//     * @return: Map
//     */
//    public static Map<String, Object> getQueryParams(HttpServletRequest request) {
//        Map<String, String[]> map;
//        if (request.getMethod().equalsIgnoreCase(POST)) {
//            map = request.getParameterMap();
//        } else {
//            String s = request.getQueryString();
//            if (org.apache.commons.lang3.StringUtils.isBlank(s)) {
//                return new HashMap<String, Object>(5);
//            }
//            try {
//                s = URLDecoder.decode(s, UTF8);
//            } catch (UnsupportedEncodingException e) {
//                s = request.getQueryString();
//            }
//            map = parseQueryString(s);
//        }
//
//        Map<String, Object> params = new HashMap<String, Object>(map.size());
//        int len;
//        for (Map.Entry<String, String[]> entry : map.entrySet()) {
//            len = entry.getValue().length;
//            if (len == 1) {
//                params.put(entry.getKey(), entry.getValue()[0]);
//            } else if (len > 1) {
//                params.put(entry.getKey(), entry.getValue());
//            }
//        }
//        return params;
//    }
//
//    /**
//     * 获取请求参数map
//     *
//     * @param queryString queryString
//     * @Title: parseQueryString
//     * @return: Map
//     */
//    public static Map<String, String[]> parseQueryString(String queryString) {
//        if (org.apache.commons.lang3.StringUtils.isBlank(queryString)) {
//            return Collections.emptyMap();
//        }
//        Map<String, String[]> queryMap = new TreeMap<String, String[]>();
//        String[] params;
//        /** &被JsoupUtil转移 */
//        if (queryString.indexOf(ESCAPE_AND_STR) != -1) {
//            params = queryString.split(ESCAPE_AND_STR);
//        } else {
//            params = queryString.split("&");
//        }
//
//        for (String param : params) {
//            int index = param.indexOf('=');
//            if (index != -1) {
//                String name = param.substring(0, index);
//                // name为空值不保存
//                if (org.apache.commons.lang3.StringUtils.isBlank(name)) {
//                    continue;
//                }
//                String value = param.substring(index + 1);
//                try {
//                    value = URLDecoder.decode(value, "UTF-8");
//                } catch (UnsupportedEncodingException e) {
//                    log.error("never!", e);
//                }
//                if (queryMap.containsKey(name)) {
//                    String[] values = queryMap.get(name);
//                    queryMap.put(name, ArrayUtils.addAll(values, value));
//                } else {
//                    queryMap.put(name, new String[]{value});
//                }
//            }
//        }
//        return queryMap;
//    }
//
//    public static String getParam(HttpServletRequest request, Map<String, String[]> queryMap, String name) {
//        String[] values = getParamValues(request, queryMap, name);
//        return ArrayUtils.isNotEmpty(values) ? org.apache.commons.lang3.StringUtils.join(values, ',') : null;
//    }
//
//    public static String getParam(HttpServletRequest request, String name) {
//        String[] values = getParamValues(request, name);
//        return ArrayUtils.isNotEmpty(values) ? org.apache.commons.lang3.StringUtils.join(values, ',') : null;
//    }
//
//    /**
//     * 获取参数值 数组
//     *
//     * @param request  HttpServletRequest
//     * @param queryMap Map
//     * @param name     参数值名称
//     * @Title: getParamValues
//     * @return: String[]
//     */
//    public static String[] getParamValues(HttpServletRequest request, Map<String, String[]> queryMap, String name) {
//        Validate.notNull(request, "Request must not be null");
//        String[] values = queryMap.get(name);
//        if (values == null) {
//            values = request.getParameterValues(name);
//        }
//        return values;
//    }
//
//    /**
//     * 获取参数值 数组
//     *
//     * @param request HttpServletRequest
//     * @param name    参数值名称
//     * @Title: getParamValues
//     * @return: String[]
//     */
//    public static String[] getParamValues(HttpServletRequest request, String name) {
//        Validate.notNull(request, "Request must not be null");
//        String qs = request.getQueryString();
//        Map<String, String[]> queryMap = parseQueryString(qs);
//        return getParamValues(request, queryMap, name);
//    }
//
//    /**
//     * 根据参数名前缀获取值数组
//     *
//     * @param request HttpServletRequest
//     * @param prefix  参数名前缀
//     * @Title: getParamPrefix
//     * @return: String[]
//     */
//    public static String[] getParamPrefix(HttpServletRequest request, String prefix) {
//        Validate.notNull(request, "Request must not be null");
//        Map<String, String[]> params = getParamValuesMap(request, prefix);
//        List<String> values = new ArrayList<String>();
//        for (Map.Entry<String, String[]> entry : params.entrySet()) {
//            values.addAll(Arrays.asList(entry.getValue()));
//        }
//        return values.toArray(new String[values.size()]);
//    }
//
//    public static Map<String, String> getParamMap(HttpServletRequest request, String prefix) {
//        return getParamMap(request, prefix, false);
//    }
//
//    /**
//     * 根据参数名前缀 获取参数名 map
//     *
//     * @param request       HttpServletRequest
//     * @param prefix        参数名前缀
//     * @param keyWithPrefix 是否完全匹配，true指定参数名 false 则按前缀查参数
//     * @Title: getParamMap
//     * @return: Map
//     */
//    public static Map<String, String> getParamMap(HttpServletRequest request,
//                                                  String prefix, boolean keyWithPrefix) {
//        Validate.notNull(request, "Request must not be null");
//        Map<String, String> params = new LinkedHashMap<String, String>();
//        if (prefix == null) {
//            prefix = "";
//        }
//        String qs = request.getQueryString();
//        Map<String, String[]> queryMap = parseQueryString(qs);
//        int len = prefix.length();
//        Enumeration<String> paramNames = request.getParameterNames();
//        while (paramNames != null && paramNames.hasMoreElements()) {
//            String paramName = (String) paramNames.nextElement();
//            if ("".equals(prefix) || paramName.startsWith(prefix)) {
//                String name = keyWithPrefix ? paramName : paramName.substring(len);
//                String value = getParam(request, queryMap, paramName);
//                if (org.apache.commons.lang3.StringUtils.isNotBlank(value)) {
//                    params.put(name, value);
//                }
//            }
//        }
//        return params;
//    }
//
//    public static Map<String, String[]> getParamValuesMap(HttpServletRequest request, String prefix) {
//        return getParamValuesMap(request, prefix, false);
//    }
//
//    /**
//     * 获取参数值map
//     *
//     * @param request HttpServletRequest
//     * @param patters field_operate_dataType格式通用查询参数数组
//     * @Title: getParamValuesMap
//     * @return: Map
//     */
//    public static Map<String, String[]> getParamValuesMap(HttpServletRequest request, String[] patters) {
//        Map<String, String[]> params = getParamValuesMap(request, null, true);
//        Map<String, Map<String, String>> patterMap = new HashMap<String, Map<String, String>>(5);
//        if (patters != null && patters.length > 0) {
//            // 格式化参数标准数据中，将name_eq或name_eq_String 转换成以 _ 分隔成name为key ,
//            // operate、dataType、field为key的map
//            // operate(操作方式)、dataType(条件数据类型)、field(对应实体类属性名)
//            for (int i = 0; i < patters.length; i++) {
//                String[] patter = patters[i].split("_");
//                if (patter.length < 2) {
//                    throw new IllegalArgumentException(" "
//                            + "patters formate error , must include '-' ");
//                }
//                Map<String, String> map = new HashMap<String, String>(5);
//                map.put("operate", patter[1]);
//                if (patter.length > 2) {
//                    map.put("dataType", patter[2]);
//                }
//                if (patter[0].startsWith("[") && patter[0].endsWith("]")) {
//                    String[] str = patter[0].replace("[", "").replace("]", "").split(",");
//                    if (str.length < 2) {
//                        throw new IllegalArgumentException(" \"[]\"patters formate error ,"
//                                + " must include ',' ");
//                    }
//                    map.put("field", str[1]);
//                    patterMap.put(str[0], map);
//                } else {
//                    map.put("field", patter[0]);
//                    patterMap.put(patter[0], map);
//                }
//            }
//        } else {
//            return new HashMap<String, String[]>(1);
//        }
//        Map<String, String[]> comParams = new HashMap<String, String[]>(5);
//        int includeCount = 0;
//        for (Map.Entry<String, String[]> entry : params.entrySet()) {
//            // 参数
//            String key = entry.getKey();
//            // 判断参数是否在标准格式中是否存在
//            if (patterMap.containsKey(entry.getKey())) {
//                String[] value = entry.getValue();
//                Map<String, String> patterValue = patterMap.get(key);
//                String comKey = patterValue.get("operate").toUpperCase()
//                        + "_" + patterValue.get("field");
//                if (patterValue.containsKey("dataType")) {
//                    comKey += "_" + patterValue.get("dataType");
//                }
//                comParams.put(comKey, value);
//            } else {
//                includeCount++;
//            }
//        }
//        if (includeCount > 0) {
//            log.info("查询参数不符合规则，已自动过滤{}个", includeCount);
//        }
//        return comParams;
//    }
//
//    /**
//     * 获取请求参数值
//     *
//     * @param request       HttpServletRequest
//     * @param prefix        参数名前缀
//     * @param keyWithPrefix 是否完全匹配，true指定参数名 false 则按前缀查参数
//     * @Title: getParamValuesMap
//     * @return: Map
//     */
//    public static Map<String, String[]> getParamValuesMap(HttpServletRequest request, String prefix,
//                                                          boolean keyWithPrefix) {
//        Validate.notNull(request, "Request must not be null");
//        Enumeration<String> paramNames = request.getParameterNames();
//        Map<String, String[]> params = new LinkedHashMap<String, String[]>();
//        if (prefix == null) {
//            prefix = "";
//        }
//        String qs = request.getQueryString();
//        Map<String, String[]> queryMap = parseQueryString(qs);
//        int len = prefix.length();
//        while (paramNames != null && paramNames.hasMoreElements()) {
//            String paramName = (String) paramNames.nextElement();
//            if ("".equals(prefix) || paramName.startsWith(prefix)) {
//                String name = keyWithPrefix ? paramName : paramName.substring(len);
//                String[] values = getParamValues(request, queryMap, paramName);
//                if (values != null && values.length > 0) {
//                    params.put(name, values);
//                }
//            }
//        }
//        return params;
//    }
//
//    /**
//     * 设置禁止客户端缓存的Header.
//     */
//    public static void setNoCacheHeader(HttpServletResponse response) {
//        response.setDateHeader("Expires", 1L);
//        response.addHeader("Pragma", "no-cache");
//        response.setHeader("Cache-Control", "no-cache, no-store, max-age=0");
//    }
//
//    /**
//     * 获取cookie值
//     *
//     * @param request HttpServletRequest
//     * @param name    cookie名称
//     * @Title: getCookie
//     * @return: String
//     */
//    public static String getCookie(HttpServletRequest request, String name) {
//        Assert.notNull(request, "Request must not be null");
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if (name.equals(cookie.getName())) {
//                    return cookie.getValue();
//                }
//            }
//        }
//        return null;
//    }
//
//    /**
//     * 根据request参数构造签名数据
//     *
//     * @param request HttpServletRequest
//     * @Title: getSignMap
//     * @return: Map
//     */
//    public static Map<String, String> getSignMap(HttpServletRequest request) {
//        Map<String, String> param = new HashMap<String, String>();
//        Enumeration<String> penum = (Enumeration<String>) request.getParameterNames();
//        while (penum.hasMoreElements()) {
//            String pKey = (String) penum.nextElement();
//            String value = request.getParameter(pKey);
//            // sign和uploadFile不参与 值为空也不参与
//            if (!pKey.equals("sign") && !pKey.equals("uploadFile") && org.apache.commons.lang3.StringUtils.isNotBlank(value)) {
//                param.put(pKey, value);
//            }
//        }
//        return param;
//    }
//}
