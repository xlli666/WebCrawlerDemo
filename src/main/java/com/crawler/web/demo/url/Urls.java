package com.crawler.web.demo.url;

import java.util.*;

public class Urls {
    // 已访问的URL集合，已经访问过的不能再重复，使用set来保证不重复;
    private static Set<String> visitedUrlSet = new HashSet<>();
    // 待访问的URL集合，待访问：1）规定访问顺序；2）保证不提供重复的访问地址;
    private static LinkedList<String> unVisitedUrlQueue = new LinkedList<>();
    // 记录所有url的深度进行爬取判断
    private static Map<String, Integer> allUrlDepth = new HashMap<>();

    public synchronized static void setAllUrlDepth(String url, int depth){
        if (!allUrlDepth.containsKey(url)) {
            allUrlDepth.put(url,depth);
        }
    }

    public static int getAllUrlDepth(String url){
        return allUrlDepth.get(url);
    }

    // 获得已经访问的URL数目
    public static int getVisitedUrlNum() {
        return visitedUrlSet.size();
    }

    // 添加到访问过的URL
    public static void addVisitedUrlSet(String url) {
        visitedUrlSet.add(url);
    }

    // 移除访问过的URL
    public static void removeVisitedUrlSet(String url) {
        visitedUrlSet.remove(url);
    }

    //获得待访问的URL集合
    public static LinkedList<String> getUnVisitedUrlQueue() {
        return unVisitedUrlQueue;
    }

    // 添加URL到待访问的集合中，保证每个URL只被访问一次
    public synchronized static void addUnVisitedUrlQueue(String url) {
        if (url != null && !"".equals(url.trim()) && !visitedUrlSet.contains(url) && !unVisitedUrlQueue.contains(url)) {
            unVisitedUrlQueue.add(url);
        }
    }

    //删除待访问的URL
    public synchronized static String removeHeadOfUnVisitedUrlQueue() {
        return unVisitedUrlQueue.removeFirst();
    }

    //判断待访问的URL队列是否为空
    public static boolean unVisitedUrlQueueIsNotEmpty() {
        return !unVisitedUrlQueue.isEmpty();
    }
}
