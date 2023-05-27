package com.enation.coder.util;

import com.enation.framework.context.ThreadContextHolder;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author fk
 * @version v2.0
 * @Description: 工具类
 * @date 2018/8/6 11:37
 * @since v7.0.0
 */
public class UrlUtil {


    /**
     * 传入url 返回对应页面的html
     *
     * @param url  页面的url
     * @return 返回对应页面的html
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String getHTML(String url) throws ClientProtocolException, IOException {
        String html = "null";
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(50000) // socket超时
                .setConnectTimeout(50000) // connect超时
                .build();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        html = EntityUtils.toString(response.getEntity(), "utf-8");
        return html;
    }

    /**
     * 获取地址
     * @return
     */
    public static String getCallUrl(){

        HttpServletRequest request = ThreadContextHolder.getHttpRequest();
        String serverName = request.getServerName();
        int port = request.getServerPort();
        String portstr = "";
        if(port!=80){
            portstr = ":"+port;
        }
        String contextPath = request.getContextPath();

        return "http://"+serverName+portstr+contextPath;
    }
}
