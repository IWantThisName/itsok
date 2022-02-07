package com.itsok.asm;

import com.itsok.utils.AsmContext;
import com.itsok.utils.AsmMethods;
import com.itsok.utils.PrintUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

public class CostVisitor extends ClassVisitor {

    private String targetClassName;

    public CostVisitor(int api, ClassVisitor classVisitor, String targetClassName) {
        super(api, classVisitor);
        this.targetClassName = PrintUtils.toJavaClassName(targetClassName);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (mv != null && !"<init>".equals(name)  && AsmContext.getInstance().isRetransformClass(targetClassName)) {
            boolean isAbstractMethod = (access & Opcodes.ACC_ABSTRACT) != 0;
            boolean isNativeMethod = (access & Opcodes.ACC_NATIVE) != 0;
            if (!isAbstractMethod && !isNativeMethod) {
                mv = new CostAdapter(api, mv, access, name, desc, targetClassName);
            }
        }
        return mv;
    }

    private static class CostAdapter extends AdviceAdapter {
        private String targetClassName;

        protected CostAdapter(int api, MethodVisitor mv, int access, String name, String desc, String targetClassName) {
            super(api, mv, access, name, desc);
            this.targetClassName = targetClassName;
        }

        /**
         * 增加秒数
         */
        @Override
        protected void onMethodEnter() {
            visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            visitVarInsn(Opcodes.LSTORE, getArgumentTypes().length + 1);
        }

        @Override
        protected void onMethodExit(int opcode) {
            visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            visitVarInsn(Opcodes.LLOAD, getArgumentTypes().length + 1);
            visitInsn(Opcodes.LSUB);
            push(targetClassName);
            push(getName());
            invokeStatic(Type.getType(PrintUtils.class), AsmMethods.ASM_METHOD_PrintUtils$printLongCostMethod);
        }
    }

}
