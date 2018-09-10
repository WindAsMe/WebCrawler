package com.company;


import com.sun.source.doctree.SeeTree;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;


/**
 * Author     : WindAsMe
 * File       : WebCrawler.java
 * Time       : Create on 18-9-10
 * Location   : ../Home/Crawler/WebCrawler.java
 * Function   : Web Crawler
 */
public class WebCrawler {

    // Save the url which is marked.
    private static Set<String> set = new HashSet<>();

    // DFS searching web
    private static void craweler(String url) {
        if (set.size() > 9 || set.contains(url))
            return;
        try {
            // Save the correlative url
            Queue<String> queue = new ArrayDeque<>();
            Document doc = Jsoup.connect(url).get();
            // mark
            set.add(url);
            System.out.println("\nTitle: " + doc.title() + " url: " + url);

            // Element a including [href] attribute
            Elements links = doc.select("a[href]");
            Elements media = doc.select("[src]");
            Elements imports = doc.select("link[href]");

            System.out.println(("\nMedia: " + media.size()));
            for (Element src : media) {
                if (src.tagName().equals("img"))
                    // Patten is like: * img: https://www.baidu.com/img/baidu_jgylogo3.gif到百度首页
                    System.out.println(" * " + src.tagName() + ": " + src.attr("abs:src") +
                            src.attr("width") + src.attr("height") +
                            trim(src.attr("alt"), 20));
                else
                    System.out.println((" * " + src.tagName() + ": " + src.attr("abs:src")));
            }

            System.out.println(("\nImports: " + imports.size()));
            for (Element link : imports)
                System.out.println((" * " + link.tagName() + ": " + link.attr("abs:href") + link.attr("rel")));

            System.out.println(("\nLinks: " + links.size()));
            for (Element link : links) {
                String s = link.attr("abs:href") + trim(link.text(), 35);
                System.out.println((" * " + s));
                // Sort the valid url
                if (s.substring(0, 4).equals("http") && )
                    queue.add(s);
            }

            // Waiting 2.5s
            Thread.sleep(2500);

            while (!queue.isEmpty())
                craweler(queue.poll());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width - 1) + ".";
        else
            return s;
    }

    public static void main(String[] args) {
        // java.util.Scanner input = new java.util.Scanner(System.in);
        // System.out.print("Enter a URL: ");
        // String url = input.nextLine();
        craweler("https://www.baidu.com");
    }


}
