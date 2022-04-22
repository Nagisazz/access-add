package com.nagisazz.accessadd.task;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.nagisazz.accessadd.util.HttpProxyUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ProxyRequestTask {

    @Autowired
    private HttpProxyUtil httpProxyUtil;

    private final ExecutorService threadPool = Executors.newFixedThreadPool(16);

    public void start(String url) {
        threadPool.execute(new ProxyRequestThread(url));
    }

    public void stop() {
        threadPool.shutdown();
    }

    class ProxyRequestThread implements Runnable {

        private final String url;

        public ProxyRequestThread(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            httpProxyUtil.sendGet(url, true);
        }
    }

}

