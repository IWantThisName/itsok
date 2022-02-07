package com.itsok.controller;


import com.itsok.asm.ItsokClassFileTransformer;
import com.itsok.bean.PageBean;
import com.itsok.bean.ResultBean;
import com.itsok.exception.AgentNotLoadException;
import com.itsok.util.LoggerUtils;
import com.itsok.utils.AsmContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

@Controller
@RequestMapping("/itsok/logger")
public class LoggerController {

    private Logger logger = LoggerFactory.getLogger(LoggerController.class);

    @RequestMapping("/list")
    @ResponseBody
    public ResultBean getLoggerList(Integer currentPage, Integer pageSize, String searchName) {
        PageBean pageBean = LoggerUtils.getLoggerList(currentPage, pageSize, searchName);
        if (pageBean.getData().size() == 0) {
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

    @RequestMapping("/printParam")
    @ResponseBody
    public ResultBean printParam(String name) {
        try {
            Instrumentation inst = AsmContext.getInstance().getInst();
            inst.addTransformer(new ItsokClassFileTransformer(), true);
            Class[] allLoadedClasses = inst.getAllLoadedClasses();
            for (Class allLoadedClass : allLoadedClasses) {
                if (allLoadedClass.getName().equals(name)) {
                    AsmContext.getInstance().addRetransformClass(name);
                    inst.retransformClasses(allLoadedClass);
                }
            }
        } catch (AgentNotLoadException | UnmodifiableClassException e) {
            return ResultBean.error().setMessage(e.getMessage());
        }
        return ResultBean.ok();
    }

    @RequestMapping("/cancelPrintParam")
    @ResponseBody
    public ResultBean cancelPrintParam(String name) {
        try {
            Instrumentation inst = AsmContext.getInstance().getInst();
            if (AsmContext.getInstance().isRetransformClass(name)) {
                Class<?> aClass = Class.forName(name);
                inst.redefineClasses(new ClassDefinition(aClass, new byte[]{}));
            }
        } catch (Exception e) {
            return ResultBean.error().setMessage(e.getMessage());
        }
        return ResultBean.ok();
    }

}
