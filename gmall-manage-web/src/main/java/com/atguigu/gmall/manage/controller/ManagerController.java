package com.atguigu.gmall.manage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ManagerController {

    @RequestMapping("index")
    public String goIndex(){
        return "index";
    }

    @RequestMapping("spuListPage")
    public String goSupListPage(){
        return "spuListPage";
    }
}

