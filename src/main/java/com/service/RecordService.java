package com.service;

import com.command.UrlCommand;
import com.common.Constant;
import com.dao.RecordDao;
import com.entity.Record;
import com.task.ProxyRequestTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordService {

    @Autowired
    private RecordDao recordDao;

    @Autowired
    private ProxyRequestTask proxyRequestTask;

    public List<Record> selectByUrl(String url){
        return recordDao.selectByUrl(url);
    }

    public void startAdd(UrlCommand command) {

        proxyRequestTask.start(command.getUrl(),Constant.proxyUrl,command.getNumber());

        Record record = new Record();
        record.setUrl(command.getUrl());
        record.setNumber(command.getNumber());
        recordDao.insert(record);
    }
}
