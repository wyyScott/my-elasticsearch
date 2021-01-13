package com.bp.esjd.controller;

import com.bp.esjd.service.ContentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class ContentController {
    @Resource
    private ContentService contentService;
    @GetMapping("/parse/{keyword}")
    public Boolean parse(@PathVariable("keyword") String keyword) throws IOException {
        return contentService.parseContent(keyword);
    }


    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String,Object>> search(
            @PathVariable("keyword") String keyword,
            @PathVariable("pageNo")int pageNo,
            @PathVariable("pageSize")int pageSize) throws IOException {
        if (pageNo==0){
            pageNo=1;
        }
        return contentService.searchPage(keyword,pageNo,pageSize);
    }
}
