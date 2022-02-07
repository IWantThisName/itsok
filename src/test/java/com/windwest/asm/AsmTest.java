package com.windwest.asm;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import jdk.internal.org.objectweb.asm.ClassReader;
import org.junit.Test;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class AsmTest {

    @Test
    public void testAsm() throws IOException {
        ClassPrinter cp = new ClassPrinter();
        ClassReader cr = new ClassReader("java.lang.Runnable");
        cr.accept(cp, 0);
        System.out.println(this.getClass().getClassLoader());
    }

    @Test
    public void testAsm2() throws IOException, ClassNotFoundException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        //起一个定时任务,定时执行线程的方法
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(new Thread() {
            @Override
            public void run() {
                System.out.println("oooooooooo");
            }
        }, 1,1, TimeUnit.SECONDS);
        //attach当前jvm,并且增强指定类
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String pid = runtime.getName().split("@")[0];
        VirtualMachine virtualMachine = VirtualMachine.attach(pid);
        virtualMachine.loadAgent("E:\\sourcecode\\itsok\\itsok-agent\\target\\sandbox-agent-1.0-SNAPSHOT-jar-with-dependencies.jar");
        LockSupport.park();
    }

}
