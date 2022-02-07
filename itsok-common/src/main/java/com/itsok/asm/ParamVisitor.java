package com.itsok.asm;

import com.itsok.utils.AsmContext;
import com.itsok.utils.AsmMethods;
import com.itsok.utils.PrintUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

public class ParamVisitor extends ClassVisitor {

    private String targetClassName;

    public ParamVisitor(int api, ClassVisitor cw, String targetClassName) {
        super(api, cw);
        this.targetClassName = PrintUtils.toJavaClassName(targetClassName);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (mv != null && !"<init>".equals(name) && AsmContext.getInstance().isRetransformClass(targetClassName)) {
            boolean isAbstractMethod = (access & Opcodes.ACC_ABSTRACT) != 0;
            boolean isNativeMethod = (access & Opcodes.ACC_NATIVE) != 0;
            if (!isAbstractMethod && !isNativeMethod) {
                mv = new ParamAdapter(api, mv, access, name, desc, targetClassName);
            }
        }
        return mv;
    }

    private static class ParamAdapter extends AdviceAdapter {
        private String targetClassName;

        protected ParamAdapter(int api, MethodVisitor mv, int access, String name, String desc, String targetClassName) {
            super(api, mv, access, name, desc);
            this.targetClassName = targetClassName;
        }

        /**
         * 打印入参
         */
        @Override
        protected void onMethodEnter() {
            Type[] argumentTypes = getArgumentTypes();
            if (argumentTypes.length > 0) {
                loadArgArray();
                push(targetClassName);
                push(getName());
                invokeStatic(Type.getType(PrintUtils.class), AsmMethods.ASM_METHOD_PrintUtils$printMethodParams);
            }
        }

        @Override
        protected void onMethodExit(int opcode) {
            if (opcode != Opcodes.RETURN) {
                loadReturn(opcode);
                push(targetClassName);
                push(getName());
                invokeStatic(Type.getType(PrintUtils.class), AsmMethods.ASM_METHOD_PrintUtils$printMethodReturn);
            }
        }


        /**
         * 加载返回值
         *
         * @param opcode 操作吗
         */
        private void loadReturn(int opcode) {
            switch (opcode) {
                case Opcodes.ARETURN: {
                    dup();
                    break;
                }
                case Opcodes.LRETURN:
                case Opcodes.DRETURN: {
                    dup2();
                    box(Type.getReturnType(methodDesc));
                    break;
                }
                default: {
                    dup();
                    box(Type.getReturnType(methodDesc));
                    break;
                }
            }
        }
    }
}
