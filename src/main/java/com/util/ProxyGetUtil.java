package com.util;

import com.common.Constant;
import com.entity.ProxyIP;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ProxyGetUtil {

    public static List<ProxyIP> getProxyIP(String url) {
        List<ProxyIP> ipList = null;
        try {
            //向ip代理地址发起get请求，获得Document对象
            Document doc = getDocument(url);

            //将得到的Document对象解析成字符串
            String ipStr = doc.body().text().trim().toString();

            //正则匹配拿到IP地址集合
            ipList = new ArrayList<ProxyIP>();
            List<String> ips = new ArrayList<String>();
            String lines[] = ipStr.split(" ");
            for (int i = 0; i < lines.length; i++) {
                Pattern ipreg = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:\\d{1,5}");

                Matcher ip = ipreg.matcher(lines[i]);
                while (ip.find()) {
                    System.out.println(ip.group());
                    ips.add(ip.group());
                }
            }

            //循环遍历得到的IP地址集合
            for (final String ip : ips) {
                log.info(ip);
                ProxyIP proxyIP = new ProxyIP();
                String[] temp = ip.split(":");
                proxyIP.setAddress(temp[0].trim());
                proxyIP.setPort(temp[1].trim());
                ipList.add(proxyIP);
            }
            log.info("一共有："+ipList.size()+"个IP");
        } catch (IOException e) {
            ipList = new ArrayList<ProxyIP>();
            try {
                HandleCrawler.setCookie();
            } catch (Exception e1) {
                log.info("Cookie加载失败！");
            }
            log.info("URL加载失败！");
        }

        return ipList;
    }

    public static Document getDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .header("accept", "*/*")
                .header("connection", "Keep-Alive")
                .header("Cookie", Constant.COOKIE)
                .header("user-agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:62.0) Gecko/20100101 Firefox/62.0")
                .timeout(5000)
                .get();
    }
}
