package com.nagisazz.accessadd.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nagisazz.accessadd.dao.base.RecordMapper;
import com.nagisazz.accessadd.entity.Record;
import com.nagisazz.accessadd.task.ProxyRequestTask;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RecordService {

    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private ProxyRequestTask proxyRequestTask;

    public List<Record> selectByUrl(String url) {
        return recordMapper.selectList(Record.builder().url(url).build());
    }

    public String startAdd(Integer number, String url) {

        Record nowRecord = recordMapper.selectOne(Record.builder().url(url).status(2).build());
        if (nowRecord != null) {
            return "存在正在访问的url，开始时间：" + nowRecord.getCreateTime();
        }
        for (int i = 0; i < number; i++) {
            proxyRequestTask.start(url);
        }

        recordMapper.insertSelective(Record.builder().url(url).number(number).status(2).reality(0).createTime(LocalDateTime.now()).build());
        return "开始访问";
    }
}
