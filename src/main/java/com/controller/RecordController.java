package com.controller;

import com.command.UrlCommand;
import com.entity.Record;
import com.service.RecordService;
import com.util.HandleCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.script.ScriptException;
import java.util.List;

@RestController
public class RecordController {

    @Autowired
    private RecordService recordService;

    @RequestMapping(value = "/find", method = RequestMethod.POST)
    public List<Record> selectByUrl(@RequestBody String url) {
        return recordService.selectByUrl(url);

    }

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public void startAdd(@RequestBody UrlCommand command) {
        recordService.startAdd(command);
    }
}
