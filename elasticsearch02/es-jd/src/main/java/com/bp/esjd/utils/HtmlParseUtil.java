package com.bp.esjd.utils;

import com.bp.esjd.entity.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Component
public class HtmlParseUtil {
//    public static void main(String[] args) throws IOException {
//        new HtmlParseUtil().parseJD("心理学").forEach(System.out::println);
//    }

    public List<Content> parseJD(String keywords) throws IOException {
        //转换编码格式
        String encode = URLEncoder.encode(keywords, "utf-8");
        //获取请求 https://search.jd.com/Search?keyword=java
        //前提:需要联网   ajax不能获取
        String url = "https://search.jd.com/Search?keyword=" + encode;
        //解析网页(jsoup返回的document就是浏览器的document对象)

        Document document = Jsoup.parse(new URL(url), 30000);
        //所有在js中使用的方法这里都能用
        Element element = document.getElementById("J_goodsList");
        //获取所有的<li>
        List<Content> goodList = new ArrayList<>();
        Elements li = element.getElementsByTag("li");
        for (Element e : li) {
            String img = e.getElementsByTag("img").eq(0).attr("src");
            String price = e.getElementsByClass("p-price").eq(0).text();
            String title = e.getElementsByClass("p-name").eq(0).text();
            Content content = new Content();
            content.setImg(img);
            content.setPrice(price);
            content.setTitle(title);
            goodList.add(content);
        }
        return goodList;
    }

}
