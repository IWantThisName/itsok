package com.itsok.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/itsok")
public class ItsokController {

    private Logger logger = LoggerFactory.getLogger(ItsokController.class);

    @RequestMapping("/test")
    @ResponseBody
    public String test() {
        return "test";
    }

}
