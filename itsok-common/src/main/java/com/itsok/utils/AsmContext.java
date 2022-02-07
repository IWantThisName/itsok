package com.itsok.utils;

import com.itsok.exception.AgentNotLoadException;

import java.lang.instrument.Instrumentation;
import java.util.HashSet;
import java.util.Set;

public class AsmContext {

    private static class InnerClass {
        private static AsmContext sington = new AsmContext();
    }

    public static AsmContext getInstance() {
        return InnerClass.sington;
    }

    /**
     * 被字节码编辑过的类
     */
    private Set<String> retransformClassSet = new HashSet<>();

    private Instrumentation inst;

    public void init(Instrumentation instrumentation) {
        this.inst = instrumentation;
    }

    public Instrumentation getInst() throws AgentNotLoadException {
        if (inst == null) {
            throw new AgentNotLoadException();
        }
        return inst;
    }

    public void addRetransformClass(String name) {
        retransformClassSet.add(name);
    }

    public boolean isRetransformClass(String name) {
        return retransformClassSet.contains(name);
    }
}
