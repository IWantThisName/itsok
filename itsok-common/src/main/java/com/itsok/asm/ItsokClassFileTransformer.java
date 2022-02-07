package com.itsok.asm;

import com.itsok.utils.AsmContext;
import com.itsok.utils.PrintUtils;
import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;


public class ItsokClassFileTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
        CostVisitor costVisitor = new CostVisitor(Opcodes.ASM4, cw, cr.getClassName());
        ParamVisitor paramVisitor = new ParamVisitor(Opcodes.ASM4, costVisitor, cr.getClassName());
        cr.accept(
                paramVisitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES
        );
        if(AsmContext.getInstance().isRetransformClass(PrintUtils.toJavaClassName(cr.getClassName()))) {
            try {
                IOUtils.write(cw.toByteArray(), new FileOutputStream("E:\\"+PrintUtils.toJavaClassName(cr.getClassName())+"111.class"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cw.toByteArray();
    }

}
