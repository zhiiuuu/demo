package com.zjq.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.websocket.server.ServerEndpoint;

@Controller
public class IndexController {

    @RequestMapping({"/index","/"})
    public String index(){
        return "redirect:/chat.html";
    }

}
