package com.itsok.util;

import com.itsok.bean.LoggerBean;
import com.itsok.bean.PageBean;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.slf4j.Log4jLoggerFactory;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;

public class LoggerUtils {

    public static PageBean getLoggerList(Integer currentPage, Integer pageSize, String searchName) {
        currentPage = currentPage == null ? 1 : currentPage;
        pageSize = pageSize == null ? 8 : pageSize;
        PageBean pageBean = new PageBean();
        TreeSet<LoggerBean> resultSet = new TreeSet<>(new Comparator<LoggerBean>() {
            @Override
            public int compare(LoggerBean o1, LoggerBean o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        ILoggerFactory iLoggerFactory = LoggerFactory.getILoggerFactory();
        Class<? extends ILoggerFactory> aClass = iLoggerFactory.getClass();
        //log4j2
        if (aClass.getName().equals("org.apache.logging.slf4j.Log4jLoggerFactory")) {
            Log4jLoggerFactory log4jLoggerFactory = (Log4jLoggerFactory) iLoggerFactory;
            Set<LoggerContext> loggerContexts = log4jLoggerFactory.getLoggerContexts();
            for (LoggerContext loggerContext : loggerContexts) {
                org.apache.logging.log4j.core.LoggerContext loggerContext1 = (org.apache.logging.log4j.core.LoggerContext) loggerContext;
                Collection<Logger> loggers = loggerContext1.getLoggers();
                for (Logger logger : loggers) {
                    LoggerBean loggerBean = new LoggerBean();
                    loggerBean.setName(logger.getName());
                    loggerBean.setLevel(logger.getLevel().toString());
                    resultSet.add(loggerBean);
                }
            }
            //log4j
        } else if (aClass.getName().equals("org.slf4j.impl.Log4jLoggerFactory")) {

            //logback
        } else if (aClass.getName().equals("ch.qos.logback.classic.LoggerContext")) {

        }
        ArrayList arrayList = new ArrayList();
        if (StringUtils.isEmpty(searchName)) {
            arrayList.addAll(resultSet);
        } else {
            for (LoggerBean loggerBean : resultSet) {
                if (loggerBean.getName().contains(searchName)) {
                    arrayList.add(loggerBean);
                }
            }
        }
        int totalPage = arrayList.size() % pageSize == 0 ? arrayList.size() / pageSize : arrayList.size() / pageSize + 1;
        int start = (currentPage - 1) * pageSize;
        int end = start + pageSize < arrayList.size() ? start + pageSize : arrayList.size();
        pageBean.setTotalSize(arrayList.size());
        pageBean.setTotalPage(totalPage);
        pageBean.setCurrentPage(currentPage);
        pageBean.setPageSize(pageSize);
        pageBean.setData(arrayList.subList(start, end));
        return pageBean;
    }

    public static void changeLevel(String name, String level) {
        ILoggerFactory iLoggerFactory = LoggerFactory.getILoggerFactory();
        Class<? extends ILoggerFactory> aClass = iLoggerFactory.getClass();
        //log4j2
        if (aClass.getName().equals("org.apache.logging.slf4j.Log4jLoggerFactory")) {
            Log4jLoggerFactory log4jLoggerFactory = (Log4jLoggerFactory) iLoggerFactory;
            Set<LoggerContext> loggerContexts = log4jLoggerFactory.getLoggerContexts();
            for (LoggerContext loggerContext : loggerContexts) {
                org.apache.logging.log4j.core.LoggerContext loggerContext1 = (org.apache.logging.log4j.core.LoggerContext) loggerContext;
                if (loggerContext1.hasLogger(name)) {
                    Logger logger = loggerContext1.getLogger(name);
                    logger.setLevel(Level.valueOf(level));
                }
            }
            //log4j
        } else if (aClass.getName().equals("org.slf4j.impl.Log4jLoggerFactory")) {

            //logback
        } else if (aClass.getName().equals("ch.qos.logback.classic.LoggerContext")) {

        }
    }

    public static String getLogType() {
        ILoggerFactory iLoggerFactory = LoggerFactory.getILoggerFactory();
        Class<? extends ILoggerFactory> aClass = iLoggerFactory.getClass();
        //log4j2
        if (aClass.getName().equals("org.apache.logging.slf4j.Log4jLoggerFactory")) {
            return "log4j2";
            //log4j
        } else if (aClass.getName().equals("org.slf4j.impl.Log4jLoggerFactory")) {
            return "log4j";
            //logback
        } else if (aClass.getName().equals("ch.qos.logback.classic.LoggerContext")) {
            return "logback";
        }
        return "未知";
    }
}
