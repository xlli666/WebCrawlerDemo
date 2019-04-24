package com.crawler.web.demo.page;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class RequestAndResponseTool {

    private static Logger logger = LoggerFactory.getLogger(RequestAndResponseTool.class);

    /**
     * 使用URLConnection方式获取页面内容
     * @param url URL地址
     * @return 页面内容Page
     */
    public static Page sendRequestAndGetResponse2Url(String url){
        Page page = null;
        try {
            URL uri = new URL(url);
            URLConnection conn = uri.openConnection();
            String contentType = conn.getContentType();
            byte[] respBody = read(conn.getInputStream());
            page = new Page(respBody, url, contentType);
        } catch (IOException e) {
            logger.error("响应处理失败：" + e.getLocalizedMessage());
        }
        return page;
    }

    /**
     * 使用HttpClient方式获取页面内容
     * @param url URL地址
     * @return 页面内容Page
     */
    public static Page sendRequestAndGetResponse2Client(String url) {
        Page page = null;
        /* 4.3以后版本设置
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://www.baidu.com");//HTTP Get请求(POST雷同)
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();//设置请求和传输超时时间
        httpGet.setConfig(requestConfig);
        httpClient.execute(httpGet);//执行请求
        */
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);//连接超时时间
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);//数据传输超时时间
        HttpGet httpGet = new HttpGet(url);
        /* 直接获取页面html数据
        ResponseHandler<String> responseHandlerString = new BasicResponseHandler(); */
        /* 通过内部处理后获取html数据 */
        ResponseHandler<Page> responseHandlerPage = new ResponseHandler<Page>(){
            @Override
            public Page handleResponse(HttpResponse httpResponse) throws IOException {
                StatusLine statusLine = httpResponse.getStatusLine();
                HttpEntity entity = httpResponse.getEntity();
                // 判断访问的状态码
                if (statusLine.getStatusCode() >= 300) {
                    logger.error("发送请求失败：" + statusLine);
                    throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
                }
                // 4.处理HTTP响应内容
                if (entity == null) {
                    logger.error("No response");
                    throw new ClientProtocolException("Response contains no content");
                }
                byte[] respBody = read(entity.getContent());
                String contentType = entity.getContentType().getValue();
                return new Page(respBody, url, contentType);
            }
        };
        // 3.执行HTTP GET请求
        try {
            /* 直接获取页面html数据
            String respBodyHtml = httpClient.execute(httpGet, responseHandlerString);
            page = new Page(respBodyHtml, url); */
            /* 通过内部处理后获取html数据 */
            page = httpClient.execute(httpGet, responseHandlerPage);
        } catch (IOException e) {
            logger.error("响应处理失败：" + e.getLocalizedMessage());
        } finally {
            // 释放连接
            httpClient.getConnectionManager().shutdown();
        }
        return page;
    }

    /**
     * 输入流转换成字节
     * @param inputStream 输入流
     * @return 字节数据
     * @throws IOException IO异常
     */
    private static byte[] read(InputStream inputStream) throws IOException{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer,0, len);
        }
        outputStream.close();
        inputStream.close();
        return outputStream.toByteArray();
    }

}
