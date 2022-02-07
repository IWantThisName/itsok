package com.itsok.asm;

import com.itsok.utils.AsmContext;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ItsokAgent {

    /**
     * agent模式
     *
     * @param featureString
     * @param inst
     */
    public static void premain(String featureString, Instrumentation inst) {
    }

    /**
     * attach模式
     *
     * @param featureString
     * @param inst
     */
    public static void agentmain(String featureString, Instrumentation inst) throws UnmodifiableClassException {
        try {
            for (Class allLoadedClass : inst.getAllLoadedClasses()) {
                if (AsmContext.class.getName().equals(allLoadedClass.getName())) {
                    Method method = allLoadedClass.getDeclaredMethod("getInstance");
                    Object obj = method.invoke(allLoadedClass);
                    Field field = allLoadedClass.getDeclaredField("inst");
                    field.setAccessible(true);
                    field.set(obj, inst);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("代理加载失败!");
        }
    }

}
