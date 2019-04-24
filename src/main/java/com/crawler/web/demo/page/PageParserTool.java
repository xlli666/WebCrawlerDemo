package com.crawler.web.demo.page;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PageParserTool {
    //解析页面数据，获取相关信息

    /**
     * 通过CSS选择器来选取页面内容
     * @param page 页面数据
     * @param ccsSelector CSS样式选择
     * @return 所有标签行数据
     */
    public static Elements select(Page page, String ccsSelector) {
        return page.getDoc().select(ccsSelector);
    }

    /**
     * 通过CSS选择器来得到指定元素;
     * @param page 页面数据
     * @param ccsSelector CSS样式选择
     * @param index 索引
     * @return 指定索引标签行数据
     */
    public static Element select(Page page, String ccsSelector, int index) {
        Elements elements = select(page, ccsSelector);
        int realIndex = index;
        if (index < 0) {
            realIndex = elements.size()+index;
        }
        return elements.get(realIndex);
    }
    /**
     * 获取满足CSS选择器元素中的链接，选择器cssSelector必须定位到具体的超链接
     * 例如我们想抽取id为content的div中的所有超链接：这里就要将cssSelector定义为div[id=content]
     * 放入set中防止重复
     * @param page 页面数据
     * @param cssSelector CSS样式选择
     * @return 链接List
     */
    public static Set<String> getUrls2JSoup(Page page , String cssSelector) {
        Set<String> urls  = new HashSet<>();
        Elements elements = select(page , cssSelector);
        for (Element element : elements) {
            if (element.hasAttr("href")) {
                urls.add(element.attr("abs:href"));
            } else if (element.hasAttr("src")) {
                urls.add(element.attr("abs:src"));
            }
        }
        return urls;
    }

    /**
     * 获取网页中满足指定css选择器的所有元素的指定属性的集合
     * getAttrs2JSoup("img[src]","abs:src")可获取网页中所有图片的链接
     * @param page 页面数据
     * @param cssSelector CSS样式选择
     * @param attrName 属性名
     * @return 属性值List
     */
    public static List<String> getAttrs2JSoup(Page page , String cssSelector, String attrName) {
        List<String> result = new ArrayList<>();
        Elements elements = select(page ,cssSelector);
        for (Element element : elements) {
            if (element.hasAttr(attrName)) {
                result.add(element.attr("abs:"+attrName));
            }
        }
        return result;
    }

    /**
     * 通过正则表达式解析页面数据，获取a标签的url
     * @param page 页面内容
     * @return URL值
     */
    public static Set<String> getUrls2Pattern(Page page) {
        String regexString = "<a .*href=.+</a>";
        Set<String> urls  = new HashSet<>();
        InputStream inputStream = new ByteArrayInputStream(page.getHtml().getBytes());
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, page.getCharset()));
            // 按行读取并打印
            String line;
            // 正则表达式的匹配规则提取该网页的链接
            Pattern pattern = Pattern.compile(regexString);
            while ((line = bufferedReader.readLine()) != null) {
                // 编写正则，匹配超链接地址
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    String href = matcher.group();
                    // 找到超链接地址并截取字符串
                    // 有无引号
                    href = href.substring(href.indexOf("href="));
                    if (href.charAt(5) == '\"') {
                        href = href.substring(6);
                    } else {
                        href = href.substring(5);
                    }
                    // 截取到引号或者空格或者到">"结束
                    try {
                        href = href.substring(0, href.indexOf("\""));
                    } catch (Exception e) {
                        try {
                            href = href.substring(0, href.indexOf(" "));
                        } catch (Exception e1) {
                            href = href.substring(0, href.indexOf(">"));
                        }
                    }
                    if (href.startsWith("http:") || href.startsWith("https:")) {
                        // 输出该网页存在的链接
                        //System.out.println(href);
                        // 将url地址放到队列中
                        urls.add(href);
                    }
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }
}
