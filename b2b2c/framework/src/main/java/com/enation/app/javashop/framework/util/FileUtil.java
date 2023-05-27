package com.enation.app.javashop.framework.util;

import com.enation.app.javashop.framework.logs.Logger;
import com.enation.app.javashop.framework.logs.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 文件工具类
 *
 * @author chopper
 * @version v1.0
 * @Description:
 * @since v7.0
 * 2018/6/28 下午5:10
 */
public class FileUtil {

    private final static Logger logger = LoggerFactory.getLogger(FileUtil.class);


    private FileUtil() {
    }

    /**
     * 是否是允许上传的图片
     *
     * @param ex 文件后缀
     * @return
     */
    public static boolean isAllowUpImg(String ex) {
        String allowTYpe = "gif,jpg,png,jpeg,mp4,pdf";
        if (!"".equals(ex.trim()) && ex.length() > 0) {
            return allowTYpe.toUpperCase().indexOf(ex.toUpperCase()) >= 0;
        } else {
            return false;
        }
    }

    /**
     * 读取一个文件
     *
     * @param resource 类路径
     * @return
     */
    public static String readFile(String resource) {

        String stripped = resource.startsWith("/") ? resource.substring(1)
                : resource;

        InputStream stream = null;
        ClassLoader classLoader = Thread.currentThread()
                .getContextClassLoader();
        if (classLoader != null) {
            stream = classLoader.getResourceAsStream(stripped);

        }
        String content = readStreamToString(stream);

        return content;

    }

    /**
     * 将流转换成一个字符串
     *
     * @param stream 流
     * @return
     */
    public static String readStreamToString(InputStream stream) {
        StringBuffer fileContentsb = new StringBuffer();
        String fileContent = "";

        try {
            InputStreamReader read = new InputStreamReader(stream, "utf-8");
            BufferedReader reader = new BufferedReader(read);
            String line;
            while ((line = reader.readLine()) != null) {
                fileContentsb.append(line + "\n");
            }
            read.close();
            read = null;
            reader.close();
            read = null;
            fileContent = fileContentsb.toString();
        } catch (Exception ex) {
            fileContent = "";
        }
        return fileContent;
    }


    /**
     * 获取文件类型
     *
     * @param fileType 文件后缀
     * @return
     */
    public static String contentType(String fileType) {
        fileType = fileType.toLowerCase();
        String contentType = "";
        switch (fileType) {
            case "bmp":
                contentType = "image/bmp";
                break;
            case "gif":
                contentType = "image/gif";
                break;
            case "png":
            case "jpeg":
            case "jpg":
                contentType = "image/jpeg";
                break;
            case "html":
                contentType = "text/html";
                break;
            case "txt":
                contentType = "text/plain";
                break;
            case "vsd":
                contentType = "application/vnd.visio";
                break;
            case "ppt":
            case "pptx":
                contentType = "application/vnd.ms-powerpoint";
                break;
            case "doc":
            case "docx":
                contentType = "application/msword";
                break;
            case "xml":
                contentType = "text/xml";
                break;
            case "mp4":
                contentType = "video/mp4";
                break;
            case "pdf":
                contentType = "application/pdf";
                break;
            default:
                contentType = "application/octet-stream";
                break;
        }
        return contentType;
    }
}
