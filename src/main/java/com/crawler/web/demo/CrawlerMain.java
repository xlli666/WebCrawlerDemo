package com.crawler.web.demo;

import com.crawler.web.demo.page.Page;
import com.crawler.web.demo.page.PageParserTool;
import com.crawler.web.demo.page.RequestAndResponseTool;
import com.crawler.web.demo.url.Urls;
import com.crawler.web.demo.util.FileTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class CrawlerMain {

    private static Logger logger = LoggerFactory.getLogger(CrawlerMain.class);

    /* 多线程操作
    //生命对象，帮助进行线程的等待操作
    private static final Object obj = new Object();
    //记录总线程数5条
    private static int MAX_THREAD=5;
    //记录空闲的线程数
    private static int count=0;

    //private static int currDepth = 0;

    public static void crawlingDepthTh(String visitUrl, int cMaxDepth) {

        //判断当前url是否爬取过
        //先从待访问的序列中取出第一个
        //String visitUrl = Urls.removeHeadOfUnVisitedUrlQueue();
        int urlDepth = Urls.getAllUrlDepth(visitUrl);
        if (visitUrl != null && urlDepth <= cMaxDepth) {
            //根据URL得到page;
            Page page = RequestAndResponseTool.sendRequestAndGetResponse2Client(visitUrl);
            //将保存文件
            FileTool.saveToLocal(page);
            //将已经访问过的链接放入已访问的链接中；
            Urls.addVisitedUrlSet(visitUrl);
            //得到超链接
            Set<String> urls = PageParserTool.getUrls2JSoup(page, "a");
            for (String url : urls) {
                addurl(url,urlDepth);
                logger.info("新增爬取路径: " + url);
            }
            //检测线程是否执行
            logger.info("当前执行：" + Thread.currentThread().getName() + " 爬取线程处理爬取：" + visitUrl);
            logger.info("当前爬取深度" + urlDepth);
        }
        if (Urls.unVisitedUrlQueueIsNotEmpty()) {
            synchronized (obj) {
                obj.notify();
            }
        } else {
            System.out.println("end");
        }

    }

    public static synchronized void addurl(String url,int urlDepth){
        Urls.addUnVisitedUrlQueue(url);
        Urls.setAllUrlDepth(url, urlDepth + 1);
    }

    public static synchronized String geturl(){
        return Urls.removeHeadOfUnVisitedUrlQueue();
    }

    //为了保证线程的安全，就需要使用同步关键字(synchronized)，来对取得连接和放入连接操作加锁。
    public class MyThread extends Thread{
        @Override
        public void run() {
            //设定一个死循环，让线程一直存在
            while(true){
                //判断是否新链接，有则获取
                if(Urls.unVisitedUrlQueueIsNotEmpty()){
                    //调用crawlingDepth方法爬取
                    //CrawlerMain crawler = new CrawlerMain();
                    String visitUrl = geturl();
                    crawlingDepthTh(visitUrl, 1);
                } else {
                    System.out.println("当前线程准备就绪，等待连接爬取："+this.getName());
                    count++;
                    //建立一个对象，让线程进入等待状态，即wait（）
                    synchronized(obj){
                        try {
                            obj.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    count--;
                }
            }
        }
    }*/

    /**
     * 初始化URL队列
     * @param seeds 种子URL
     */
    private void initCrawlerWithSeeds(String[] seeds) {
        for (String seed : seeds) {
            Urls.addUnVisitedUrlQueue(seed);
        }
    }
    /**
     * 初始化URL队列及深度
     * @param seeds 种子URL
     * @param depth 初始深度
     */
    private void initCrawlerWithSeeds(String[] seeds, int depth) {
        for (String seed : seeds) {
            Urls.addUnVisitedUrlQueue(seed);
            Urls.setAllUrlDepth(seed, depth);
        }
    }

    /**
     * 抓取过程
     * @param seeds 抓取地址
     * @param cMaxLength 抓取地址最大数量
     */
    public void crawlingLength(String[] seeds, int cMaxLength){
        //初始化URL队列
        initCrawlerWithSeeds(seeds);
        /*定义过滤器，提取以 http://www.baidu.com 开头的链接
        UrlFilter filter = new UrlFilter() {
            @Override
            public boolean accept(String url) {
                return url.startsWith("http://www.baidu.com");
            }
        };
        if (filter.accept("")){
            return;
        }*/
        //循环条件：待抓取的链接不空且抓取的网页不多于cLength条
        while (Urls.unVisitedUrlQueueIsNotEmpty() && Urls.getVisitedUrlNum() <= cMaxLength) {
            //先从待访问的序列中取出第一个
            String visitUrl = Urls.removeHeadOfUnVisitedUrlQueue();
            if (visitUrl == null){
                continue;
            }
            //根据URL得到page;
            Page page = RequestAndResponseTool.sendRequestAndGetResponse2Client(visitUrl);
            /*对page进行处理： 访问DOM的某个标签
            Elements elements = PageParserTool.select(page,"a");
            if(!elements.isEmpty()){
                System.out.println("下面将打印所有a标签： ");
                System.out.println(elements);
            }*/
            //将保存文件
            FileTool.saveToLocal(page);
            //将已经访问过的链接放入已访问的链接中；
            Urls.addVisitedUrlSet(visitUrl);
            //得到超链接
            Set<String> urls = PageParserTool.getUrls2JSoup(page,"img");
            for (String url : urls) {
                Urls.addUnVisitedUrlQueue(url);
                logger.info("新增爬取路径: " + url);
            }
        }
    }
    /**
     * 抓取过程
     * @param seeds 抓取地址
     * @param cMaxDepth 抓取地址最大深度
     */
    public void crawlingDepth(String[] seeds, int cMaxDepth) {
        //初始化URL队列
        initCrawlerWithSeeds(seeds, 0);
        //判断当前url是否爬取过
        while (Urls.unVisitedUrlQueueIsNotEmpty()){
            //先从待访问的序列中取出第一个
            String visitUrl = Urls.removeHeadOfUnVisitedUrlQueue();
            int urlDepth = Urls.getAllUrlDepth(visitUrl);
            if (visitUrl != null && urlDepth <= cMaxDepth){
                //根据URL得到page;
                Page page = RequestAndResponseTool.sendRequestAndGetResponse2Client(visitUrl);
                //将保存文件
                FileTool.saveToLocal(page);
                //将已经访问过的链接放入已访问的链接中；
                Urls.addVisitedUrlSet(visitUrl);
                //得到超链接
                Set<String> urls = PageParserTool.getUrls2JSoup(page,"a");
                for (String url : urls) {
                    Urls.addUnVisitedUrlQueue(url);
                    Urls.setAllUrlDepth(url, urlDepth+1);
                    logger.info("新增爬取路径: " + url);
                }
                logger.info("当前URL深度：" + urlDepth);
            }
        }
    }

    public static void main(String[] args) {
        CrawlerMain crawler = new CrawlerMain();
        crawler.crawlingDepth(new String[]{"https://www.baidu.com/"}, 1);

        /*
        crawler.initCrawlerWithSeeds(new String[]{"https://www.baidu.com/"}, 0);
        for(int i=0;i<MAX_THREAD;i++) {
            new CrawlerMain().new MyThread().start();
        }*/
    }
}
