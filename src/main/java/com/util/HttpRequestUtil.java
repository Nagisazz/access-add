package com.util;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class HttpRequestUtil {

    /**
     * 向指定URL发送GET请求
     *
     * @param url   发送的请求 URL
     * @param param 请求参数
     * @return 请求的返回结果
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String realURL;
            if (param.isEmpty()) {
                realURL = url;
            } else {
                realURL = url + "?" + param;
            }

            URL urlClient = new URL(realURL);
            //打开和URL的连接
            URLConnection connection = urlClient.openConnection();
            //设置请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            //设置请求和返回超时时间
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(6000);
            //进行连接
            connection.connect();

//            //遍历输出所有响应头字段
//            Map<String, List<String>> map = connection.getHeaderFields();
//            for (String key : map.keySet()) {
//                System.out.println(key + "-------" + map.get(key));
//            }

            //输出URL响应
            String line;
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
//            System.out.println("GET请求发送异常！" + e);
//            e.printStackTrace();
            return "false";
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }

        }
        return result;
    }

    /**
     * 向指定URL发送POST请求
     *
     * @param url   发送的请求 URL
     * @param param 请求参数
     * @return 请求的返回结果
     */
    public static String sendPost(String url, String param) {
        String result = "";
        BufferedReader in = null;
        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            URL urlClient = new URL(url);
            //打开和URL的连接
            URLConnection connection = urlClient.openConnection();
            //设置请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            //设置请求和返回超时时间
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(6000);
            //Post请求需设置这个属性
            connection.setDoOutput(true);

//            //进行连接，getOutputStream()会调用connect，所以可以不用写
//            connection.connect();

            //写入请求参数
            outputStream = connection.getOutputStream();
            outputStreamWriter = new OutputStreamWriter(outputStream);
            bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.write(param);
            bufferedWriter.flush();

            //输出URL响应
            String line;
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
//            System.out.println("POST请求发送异常！" + e);
//            e.printStackTrace();
            return "false";
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }

//    public static void main(String[] args) {
//        System.out.println(sendProxyGet("109.197.184.81",8080,"https://h5.ele.me/hongbao/#hardware_id=&is_lucky_group=True&lucky_number=6&track_id=&platform=4&sn=2a00244d8632b85b&theme_id=2841&device_id=&refer_user_id=1",""));
//    }

    /**
     * 运用代理向指定URL发送GET请求
     *
     * @param address 代理IP
     * @param port    代理端口
     * @param url     请求的URL
     * @param param   请求参数
     * @return        返回的结果
     */
    public static String sendProxyGet(String address, int port, String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String realURL;
            if (param==null||param.isEmpty()) {
                realURL = url;
            } else {
                realURL = url + "?" + param;
            }

            URL urlClient = new URL(realURL);
            //设置代理
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(address, port));
            //设置代理打开和URL的连接
            URLConnection connection = urlClient.openConnection(proxy);
            //设置请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            //设置请求和返回超时时间
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(6000);
            //进行连接
            connection.connect();

            //遍历输出所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            for (String key : map.keySet()) {
                System.out.println(key + "-------" + map.get(key));
            }

            //输出URL响应
            String line;
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
//            System.out.println("代理GET请求发送异常！" + e);
//            System.out.println("代理IP：" + address + "   端口：" + port);
//            e.printStackTrace();
            return "false";
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }

        }
        return result;
    }

    /**
     * 运用代理向指定URL发送POST请求
     *
     * @param address 代理IP
     * @param port    代理端口
     * @param url     请求的URL
     * @param param   请求参数
     * @return        返回的结果
     */
    public static String sendProxyPost(String address, int port,String url, String param) {
        String result = "";
        BufferedReader in = null;
        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            URL urlClient = new URL(url);
            //设置代理
            Proxy proxy = new Proxy(Proxy.Type.HTTP,new InetSocketAddress(address,port));
            //设置代理打开和URL的连接
            URLConnection connection = urlClient.openConnection(proxy);
            //设置请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            //设置请求和返回超时时间
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(6000);
            //Post请求需设置这个属性
            connection.setDoOutput(true);

//            //进行连接，getOutputStream()会调用connect，所以可以不用写
//            connection.connect();

            //写入请求参数
            outputStream = connection.getOutputStream();
            outputStreamWriter = new OutputStreamWriter(outputStream);
            bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.write(param);
            bufferedWriter.flush();

            //输出URL响应
            String line;
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
//            System.out.println("代理POST请求发送异常！" + e);
//            System.out.println("代理IP：" + address + "   端口：" + port);
//            e.printStackTrace();
            return "false";
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (outputStreamWriter != null) {
                    outputStreamWriter.close();
                }
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
}
