package com.itsok.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * 操作工具类,根据传入的参数值打印相关信息
 */
public class PrintUtils {

    private static Logger logger = LoggerFactory.getLogger(PrintUtils.class);


    /**
     * 打印方法入参
     */
    public static void printMethodParams(Object[] argumentArray, String targetClassName, String methodDesc) {
        logger.info(targetClassName + "." + methodDesc + "入参:" + Arrays.toString(argumentArray));
    }

    /**
     * 打印方法返回值
     */
    public static void printMethodReturn(Object returnValue, String targetClassName, String methodDesc) {
        logger.info(targetClassName + "." + methodDesc + "执行完毕返回:" + returnValue);
    }

    /**
     * 打印异常信息
     */
    public static void printException() {

    }

    /**
     * 打印耗时方法信息
     */
    public static void printLongCostMethod(long mills, String targetClassName, String methodDesc) {
        logger.info(targetClassName + "." + methodDesc + "方法执行耗时:" + mills);
    }


    public static String toJavaClassName(String internalClassName) {
        if (StringUtils.isEmpty(internalClassName)) {
            return internalClassName;
        }
        return internalClassName.replace('/', '.');
    }
}
