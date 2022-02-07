package com.itsok.utils;

import org.objectweb.asm.commons.Method;

public class AsmMethods {

    public static Method ASM_METHOD_PrintUtils$printMethodParams = getAsmMethod(
            PrintUtils.class,
            "printMethodParams",
            Object[].class, String.class, String.class
    );


    public static Method ASM_METHOD_PrintUtils$printMethodReturn = getAsmMethod(
            PrintUtils.class,
            "printMethodReturn",
            Object.class, String.class, String.class
    );

    public static Method ASM_METHOD_PrintUtils$printLongCostMethod = getAsmMethod(
            PrintUtils.class,
            "printLongCostMethod",
            long.class, String.class, String.class
    );


    public static Method getAsmMethod(final Class<?> clazz,
                                      final String methodName,
                                      final Class<?>... parameterClassArray) {

        return Method.getMethod(unCaughtGetClassDeclaredJavaMethod(clazz, methodName, parameterClassArray));
    }

    public static java.lang.reflect.Method unCaughtGetClassDeclaredJavaMethod(final Class<?> clazz,
                                                                              final String name,
                                                                              final Class<?>... parameterClassArray) {
        try {
            return clazz.getDeclaredMethod(name, parameterClassArray);
        } catch (NoSuchMethodException e) {
            throw new UnCaughtException(e);
        }
    }

}
