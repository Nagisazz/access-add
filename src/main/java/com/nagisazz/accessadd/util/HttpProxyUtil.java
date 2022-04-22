package com.nagisazz.accessadd.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.nagisazz.accessadd.vo.ProxyIpLog;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class HttpProxyUtil {

    private static final RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(3000)
            .setConnectTimeout(3000).setConnectionRequestTimeout(3000).build();

    private static final SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(3000).build();

    private List<String> ipList = new ArrayList<String>();

    @Value("${proxy.url.list}")
    private String listUrl;

    @Value("${proxy.url.remove}")
    private String removeUrl;

    @PostConstruct
    public void initIpList() {
        log.info("初始化ipList");
        HttpGet get = new HttpGet(listUrl);
        get.setConfig(defaultRequestConfig);
        get.setHeader("Accept", "*/*");
        get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:62.0) Gecko/20100101 Firefox/62.0");
        try (CloseableHttpClient client = HttpClients.custom()
                .setDefaultRequestConfig(defaultRequestConfig)
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false)).build();
             CloseableHttpResponse response = client.execute(get)) {
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "utf-8");
            List<ProxyIpLog> proxyIpLogs = JSON.parseArray(result, ProxyIpLog.class);
            ipList.addAll(proxyIpLogs.stream().map(ProxyIpLog::getIp).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("初始化ipList失败", e);
        }
        try {
            get.releaseConnection();
        } catch (Exception e) {
            log.error("HttpGet释放失败", e);
        }
        log.info("初始化ipList完成，共有：{}个", ipList.size());
    }

    /**
     * 运用代理向指定URL发送GET请求
     *
     * @param url     请求的URL
     * @return 返回的结果
     */
    public String sendGet(String url, boolean isProxy) {
        String result = "";
        BufferedReader in = null;

        Random random = new Random();
        String ip = "";
        if (isProxy) {
            ip = ipList.get(random.nextInt(ipList.size()));
            log.info("开始访问：{}，ip：{}", url, ip);
        } else {
            log.info("开始访问：{}，本机ip", url);
        }

        try {
            URL urlClient = new URL(url);
            //设置代理
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip.split(":")[0], Integer.parseInt(ip.split(":")[1])));
            //设置代理打开和URL的连接
            URLConnection connection = urlClient.openConnection(proxy);
            //设置请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            //设置请求和返回超时时间
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            //进行连接
            connection.connect();
            //输出URL响应
            String line;
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
//            remove(ip);
            log.error("{}已经失效，移除此ip，还有{}个ip可用", ip, ipList.size(), e);
            return "";
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }

        }
        log.info("访问成功：{}，ip：{}", url, ip);
        return result;
    }

    private void remove(String ipSrc) {
        ipList.remove(ipSrc);
        Runnable runnable = () -> {
            log.info("发送请求清除无用ip，ipSrc：{}", ipSrc);
            String ip = ipSrc.split(":")[0];
            String port = ipSrc.split(":")[1];
            HttpGet get = new HttpGet(removeUrl + ip + "/" + port);
            get.setHeader("Accept", "*/*");
            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:62.0) Gecko/20100101 Firefox/62.0");
            try (CloseableHttpClient client = HttpClients.custom()
                    .setDefaultRequestConfig(defaultRequestConfig)
                    .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
                    .build()) {
                client.execute(get);
            } catch (Exception e) {
                log.error("发送请求清除无用ip失败，ipSrc：{}", ipSrc, e);
            }
            log.info("发送请求清除无用ip完成，ipSrc：{}", ipSrc);
            try {
                get.releaseConnection();
            } catch (Exception e) {
                log.error("HttpGet释放失败", e);
            }

            log.info("检查ip数量：{}", ipList.size());
            synchronized (this){
                if (ipList.size() < 20) {
                    log.info("ip数量小于20，当前数量：{}，开始重新获取50个ip", ipList.size());
                    initIpList();
                }
            }
        };
        runnable.run();
    }

    public String getListUrl() {
        return listUrl;
    }

    public void setListUrl(String listUrl) {
        this.listUrl = listUrl;
    }

    public String getRemoveUrl() {
        return removeUrl;
    }

    public void setRemoveUrl(String removeUrl) {
        this.removeUrl = removeUrl;
    }

//    public static void main(String[] args) {
//        try (CloseableHttpClient client = HttpClients.custom()
//                .setProxy(new HttpHost("58.220.95.44", 10174))
//                .setDefaultRequestConfig(defaultRequestConfig).build();) {
//            HttpGet get = new HttpGet("http://1.15.87.105:12100/proxy/ip/list/50");
//            get.setHeader("Accept", "*/*");
//            get.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:62.0) Gecko/20100101 Firefox/62.0");
//            CloseableHttpResponse response = client.execute(get);
//            HttpEntity entity = response.getEntity();
//            String result = EntityUtils.toString(entity, "utf-8");
//            List<ProxyIpLog> proxyIpLogs = JSON.parseArray(result, ProxyIpLog.class);
////            ipList.addAll(proxyIpLogs.stream().map(ProxyIpLog::getIp).collect(Collectors.toList()));
//        } catch (Exception e) {
//            log.error("初始化ipList失败", e);
//        }
//    }
}
