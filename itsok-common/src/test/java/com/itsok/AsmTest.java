package com.itsok;

import com.itsok.asm.CostVisitor;
import com.itsok.asm.ParamVisitor;
import com.itsok.utils.AsmContext;
import com.itsok.utils.PrintUtils;
import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.objectweb.asm.*;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Method;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class AsmTest {

    @Test
    public void testAsm1() throws IOException, ClassNotFoundException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        //起一个定时任务,定时执行线程的方法
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(new Thread(), 1, 1, TimeUnit.SECONDS);
        //attach当前jvm,并且增强指定类
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        String pid = runtime.getName().split("@")[0];
        System.out.println(pid);
        VirtualMachine virtualMachine = VirtualMachine.attach(pid);
        virtualMachine.loadAgent("E:\\sourcecode\\itsok\\itsok-agent\\target\\sandbox-agent-1.0-SNAPSHOT-jar-with-dependencies.jar");
        LockSupport.park();
    }

    @Test
    public void testAsm2() throws AgentLoadException, IOException, AgentInitializationException, AttachNotSupportedException {
        VirtualMachine virtualMachine = VirtualMachine.attach("23180");
        virtualMachine.loadAgent("E:\\sourcecode\\itsok\\itsok-agent\\target\\sandbox-agent-1.0-SNAPSHOT-jar-with-dependencies.jar");
    }


    /**
     * 打印类结构
     *
     * @throws IOException
     */
    @Test
    public void testAsm3() throws IOException {
        String className = AsmContext.class.getName();
        int parsingOptions = ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG;
        boolean asmCode = true;

        Printer printer = asmCode ? new ASMifier() : new Textifier();
        PrintWriter printWriter = new PrintWriter(System.out, true);
        TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, printer, printWriter);
        new ClassReader(className).accept(traceClassVisitor, parsingOptions);
    }

    @Test
    public void testAsm4() throws IOException {
        ClassReader cr = new ClassReader(AsmContext.class.getName());
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
        cr.accept(
                new ParamVisitor(Opcodes.ASM4, cw, cr.getClassName()), ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES
        );
        IOUtils.write(cw.toByteArray(), new FileOutputStream("MyRunnable.class"));
    }

    @Test
    public void testAsm5() throws Exception {
        String relative_path = "HelloWorld.class";

        // (1) 生成byte[]内容
        byte[] bytes = dump();

        IOUtils.write(bytes, new FileOutputStream(relative_path));
    }

    public static byte[] dump() throws Exception {
        // (1) 创建ClassWriter对象
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        // (2) 调用visitXxx()方法
        cw.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER, "sample/HelloWorld",
                null, "java/lang/Object", null);

        {
            MethodVisitor mv1 = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
            mv1.visitCode();
            mv1.visitVarInsn(Opcodes.ALOAD, 0);
            mv1.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv1.visitInsn(Opcodes.RETURN);
            mv1.visitMaxs(0, 0);
            mv1.visitEnd();
        }

        {
            MethodVisitor mv2 = cw.visitMethod(Opcodes.ACC_PUBLIC, "test", "(I)V", null, null);
            Label elseLabel = new Label();
            Label returnLabel = new Label();

            // 第1段
            mv2.visitCode();
            mv2.visitVarInsn(Opcodes.ILOAD, 1);
            mv2.visitJumpInsn(Opcodes.IFNE, elseLabel);
            mv2.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv2.visitLdcInsn("value is 0");
            mv2.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            mv2.visitJumpInsn(Opcodes.GOTO, returnLabel);

            // 第2段
            mv2.visitLabel(elseLabel);
            mv2.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv2.visitLdcInsn("value is not 0");
            mv2.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

            // 第3段
            mv2.visitLabel(returnLabel);
            mv2.visitInsn(Opcodes.RETURN);
            mv2.visitMaxs(0, 0);
            mv2.visitEnd();
        }

        cw.visitEnd();

        // (3) 调用toByteArray()方法
        return cw.toByteArray();
    }


    @Test
    public void testAsm6() throws IOException {
        ASMifier.main(new String[]{AsmContext.class.getName()});
        System.out.println("---------------------------");
        Textifier.main(new String[]{AsmContext.class.getName()});
        System.out.println(Type.getDescriptor(PrintUtils.class));
    }

    @Test
    public void testAsm7() throws IOException {
        ClassReader cr = new ClassReader(ManagementFactory.class.getName());
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
        CostVisitor costVisitor = new CostVisitor(Opcodes.ASM4, cw, PrintUtils.toJavaClassName(cr.getClassName()));
        ParamVisitor paramVisitor = new ParamVisitor(Opcodes.ASM4, costVisitor, PrintUtils.toJavaClassName(cr.getClassName()));
        cr.accept(
                paramVisitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES
        );
        IOUtils.write(cw.toByteArray(), new FileOutputStream("ttt.class"));
    }

    @Test
    public void testAsm8() throws NoSuchMethodException {
        Method[] declaredMethods = PrintUtils.class.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            System.out.println(declaredMethod);
        }
    }

}
