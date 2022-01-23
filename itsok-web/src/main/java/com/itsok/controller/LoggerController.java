package com.itsok.controller;


import com.itsok.bean.PageBean;
import com.itsok.bean.ResultBean;
import com.itsok.util.LoggerUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/itsok/logger")
public class LoggerController {

    @RequestMapping("/list")
    @ResponseBody
    public ResultBean getLoggerList(Integer currentPage, Integer pageSize, String searchName) {
        PageBean pageBean = LoggerUtils.getLoggerList(currentPage, pageSize, searchName);
        if(pageBean.getData().size() == 0) {
            return ResultBean.error().setMessage("未查询到相关数据,请检查该类使用的日志框架是否是" + LoggerUtils.getLogType()).setData(pageBean);
        }
        return ResultBean.ok().setData(pageBean);
    }

    @RequestMapping("/changeLevel")
    @ResponseBody
    public ResultBean changeLevel(String name, String level) {
        LoggerUtils.changeLevel(name, level);
        return ResultBean.ok();
    }

}
