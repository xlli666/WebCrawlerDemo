package com.crawler.web.demo.util;

import com.crawler.web.demo.page.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileTool {

    private static Logger logger = LoggerFactory.getLogger(FileTool.class);

    private static String DIR_PATH;

    /**
     * 获取文件名称
     * @return 文件名
     */
    private static String getFileName(){
        return System.currentTimeMillis()+".txt";
    }

    /**
     * 获取文件名称
     * @param url URL地址
     * @param contentType 页面类型
     * @return 文件名
     */
    private static String getFileName(String url, String contentType){
        //去除 http:// 或 https://
        url = url.substring(7);
        //text/html 类型
        if (contentType.contains("html")) {
            url = url.replaceAll("[\\?/:*|<>\"]", "_") + ".html";
            return url;
        }
        //如 application/pdf 类型
        else {
            return url.replaceAll("[\\?/:*|<>\"]", "_") + "." +
                    contentType.substring(contentType.lastIndexOf("/") + 1);
        }
    }

    /**
     * 创建文件夹
     */
    private static void mkDir() {
        if (DIR_PATH == null) {
            DIR_PATH = Class.class.getClass().getResource("/").getPath() + "temp" + File.separator;
        }
        File fileDir = new File(DIR_PATH);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }

    }

    /**
     * 保存页面
     * @param page 获取的页面
     */
    public static void saveToLocal(Page page){
        mkDir();
        String fileName =  getFileName() ;
        String filePath = DIR_PATH + fileName ;
        byte[] data = page.getContent();
        try {
            //Files.lines(Paths.get("D:\\jd.txt"), StandardCharsets.UTF_8).forEach(System.out::println);
            DataOutputStream out = new DataOutputStream(new FileOutputStream(new File(filePath)));
            for (byte aData : data) {
                out.write(aData);
            }
            out.flush();
            out.close();
            logger.info("文件：{} 已存储在 {}", fileName, filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
