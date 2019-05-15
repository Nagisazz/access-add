package com.task;

import com.entity.ProxyIP;
import com.util.HttpRequestUtil;
import com.util.ProxyGetUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class ProxyRequestTask {

    private ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public void start(String url, String proxyUrl, int number) {
        threadPool.execute(new ProxyRequestThread(url, proxyUrl, number));
    }

    public void stop() {
        threadPool.shutdown();
    }

    class ProxyRequestThread implements Runnable {

        private String url;

        private String proxyUrl;

        private int number;

        public ProxyRequestThread(String url, String proxyUrl, int number) {
            this.url = url;
            this.proxyUrl = proxyUrl;
            this.number = number;
        }

        @Override
        public void run() {
            visit(url, proxyUrl, number);
        }

        public void visit(String url, String proxyUrl, int number) {
            String oriProxyUrl = proxyUrl;
            List<ProxyIP> ipList = ProxyGetUtil.getProxyIP(proxyUrl);

            //当前访问总次数
            int now = 0;
            //访问成功次数
            int count = 0;
            //状态标识 1:开始,0:结束
            int status = 1;

            for (int i = 1; ; i++) {
                log.info("--------第" + i + "批代理IP访问开始--------\n");
                for (ProxyIP proxyIP : ipList) {
                    if (count < number) {
                        now++;
                        log.info("现在是"+url+"第 " + now + " 次访问");
                        log.info("使用的代理为------" + proxyIP.getAddress() + ":" + proxyIP.getPort());
                        try {
                            String result = HttpRequestUtil.sendProxyGet(proxyIP.getAddress(), Integer.parseInt(proxyIP.getPort()), url, "");
                            if (!result.equals("false")) {
                                count++;
                                log.info(url+"成功访问次数: " + count);
                                log.info("代理IP：" + proxyIP.getAddress() + "   端口：" + proxyIP.getPort());
                            } else {
                                log.info("代理GET请求发送异常！");
                                log.info("代理IP：" + proxyIP.getAddress() + "   端口：" + proxyIP.getPort());
                            }
                        } catch (Exception e) {

                        }
                        log.info("--------本次访问结束--------\n");
                    }else {
                        status = 0;
                        break;
                    }
                }
                log.info("--------第" + i + "批代理IP访问结束--------\n");
                if (status == 0){
                    break;
                }
                ipList = ProxyGetUtil.getProxyIP(proxyUrl);
            }

            log.info("--------"+url+"访问结束，共访问了"+count+"次--------\n");
        }
    }

}
