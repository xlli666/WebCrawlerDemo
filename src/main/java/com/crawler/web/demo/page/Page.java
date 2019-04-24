package com.crawler.web.demo.page;

import com.crawler.web.demo.util.CharsetDetector;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.UnsupportedEncodingException;

public class Page {
    private byte[] content;
    private String html;  //网页源码字符串
    private Document doc  ;//网页Dom文档
    private String charset;//字符编码
    private String url;//url路径
    private String contentType;// 内容类型

    Page(byte[] content, String url, String contentType) {
        this.content = content;
        this.url = url;
        this.contentType = contentType;
    }

    Page(String html, String url) {
        this.html = html;
        this.url = url;
    }

    public String getCharset() {
        return charset;
    }

    public String getUrl() {
        return url;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public String getHtml() {
        if (html != null) {
            return html;
        }
        if (content == null) {
            return null;
        }
        if(charset==null){
            charset = CharsetDetector.guessEncoding(content); //根据内容来猜测字符编码
        }
        try {
            html = new String(content, charset);
            return html;
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Document getDoc(){
        if (doc != null) {
            return doc;
        }
        try {
            doc = Jsoup.parse(getHtml(), url);
            return doc;
        } catch (Exception e) {
            return null;
        }
    }

}
