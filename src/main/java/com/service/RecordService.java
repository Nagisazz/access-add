package com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.common.Constant;
import com.task.ProxyRequestTask;

@Service
public class RecordService {

//    @Autowired
//    private RecordDao recordDao;

    @Autowired
    private ProxyRequestTask proxyRequestTask;

//    public List<Record> selectByUrl(String url){
//        return recordDao.selectByUrl(url);
//    }

    public void startAdd(String url, Integer number) {
        proxyRequestTask.start(url, Constant.proxyUrl, number);
    }
}
