package com.company;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.*;


/**
 * Author     : WindAsMe
 * File       : WebCrawlerDFS.java
 * Time       : Create on 18-9-10
 * Location   : ../Home/Crawler/WebCrawlerDFS.java
 * Function   : Web Crawler in DFS
 */
public class WebCrawlerDFS {

    // Save the url which is marked
    private static Set<String> set = new HashSet<>();
    private static int count = 0;
    // DFS searching web
    private static void craweler(String url, Connection c) {
        if (set.size() > 9 || set.contains(url))
            return;
        try {
            // Save the correlative url
            // In this step, every url is working
            Queue<String> queue = new ArrayDeque<>();
            Document doc = Jsoup.connect(url).get();
            Statement statement = c.createStatement();
            count++;
            // mark
            set.add(url);
            System.out.println("\nTitle: " + doc.title() + " URL: " + url + " Running times: " + count);
            System.out.println("set: " + set.toString());
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
                String s = link.attr("abs:href");
                System.out.println((" * " + link.attr("abs:href") + trim(link.text(), 35)));

                // Sort the valid url
                if (validURL(s))
                    queue.add(s);
            }

            if (!c.isClosed()) {
                System.out.println("Insert INTO dfs_table (url, media, imports, links) VALUES (" + "\"" + url + "\"" + "," + media.size() + "," + imports.size() + "," + links.size() + ")");
                statement.execute("Insert INTO dfs_table (url, media, imports, links) VALUES (" + "\"" + url + "\"" + "," + media.size() + "," + imports.size() + "," + links.size() + ")");
            }

            // Waiting 2.5s
            Thread.sleep(2500);

            while (!queue.isEmpty())
                craweler(queue.poll(), c);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // trim the url
    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width - 1) + ".";
        else
            return s;
    }

    // 1. http(s) is the start
    // 2. Can be connected
    private static boolean validURL(String s) {
        // if s.length < 4, first piece is in utility
        if (s.length() > 10 && s.substring(0, 4).equals("http")) {
            try {
                URL url = new URL(s);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                System.out.println(connection.toString());
                return connection.getResponseCode() == 200;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else
            return false;
    }


    public static void main(String[] args) {
        try {
            // java.util.Scanner input = new java.util.Scanner(System.in);
            // System.out.print("Enter a URL: ");
            // String url = input.nextLine();
            String driver = "com.mysql.jdbc.Driver";
            String username = "root";
            String password = "change123";
            String database = "jdbc:mysql://localhost:3306/IRDB";
            Class.forName(driver);
            Connection c = DriverManager.getConnection(database, username, password);
            craweler("https://www.baidu.com", c);
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
