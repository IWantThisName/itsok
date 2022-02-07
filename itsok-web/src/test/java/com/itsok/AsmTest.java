package com.itsok;

import com.itsok.asm.CostVisitor;
import com.itsok.asm.ParamVisitor;
import com.itsok.controller.LoggerController;
import com.itsok.controller.ManagementController;
import com.itsok.utils.AsmContext;
import com.itsok.utils.PrintUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.CheckClassAdapter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class AsmTest {

    @Test
    public void testAsm() throws IOException {
        AsmContext.getInstance().addRetransformClass(ManagementController.class.getName());
        ClassReader cr = new ClassReader(ManagementController.class.getName());
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
        ParamVisitor paramVisitor = new ParamVisitor(Opcodes.ASM4, cw, PrintUtils.toJavaClassName(cr.getClassName()));
        cr.accept(
                paramVisitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES
        );
        PrintWriter printWriter = new PrintWriter(System.out);
        CheckClassAdapter.verify(new ClassReader(cw.toByteArray()), false, printWriter);
        IOUtils.write(cw.toByteArray(), new FileOutputStream("ttt.class"));
        ASMifier.main(new String[]{"ttt.class"});
    }
}
