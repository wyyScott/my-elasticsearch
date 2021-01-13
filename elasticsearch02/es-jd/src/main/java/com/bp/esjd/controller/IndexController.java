package com.bp.esjd.controller;

import com.bp.esjd.utils.HtmlParseUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;

@Controller
public class IndexController {
    @Resource
    private HtmlParseUtil parseUtil;
    @GetMapping({"/","index"})
    public String test(){
        return "index";
    }
}
