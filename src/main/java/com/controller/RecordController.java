package com.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.service.RecordService;

@RestController
public class RecordController {

    @Autowired
    private RecordService recordService;

//    @RequestMapping(value = "/find", method = RequestMethod.POST)
//    public List<Record> selectByUrl(@RequestBody String url) {
////        return recordService.selectByUrl(url);
//
//    }

    @GetMapping(value = "/start")
    public void startAdd(@RequestParam("url") String url,@RequestParam("number") Integer number) {
        try {
            url = URLDecoder.decode(url,"UTF-8");
        } catch (UnsupportedEncodingException e) {
        }
        recordService.startAdd(url,number);
    }
}
