package com.github.noahshen;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: noahshen
 * Date: 14-3-22
 * Time: 21:41
 * To change this template use File | Settings | File Templates.
 */
public class MethodRead {

    public static void main(String[] args) throws IOException {
        ClassReader reader = new ClassReader("com.github.noahshen.ForReadClass");
        ClassNode cn = new ClassNode();
        reader.accept(cn, 0);
        List<MethodNode> methodList = cn.methods;
        for (MethodNode md : methodList) {
            if (!"methodA".equals(md.name)) {
                continue;
            }
            System.out.printf("methodName: %s, access: %s, desc: %s, sig: %s\n", md.name, md.access, md.desc, md.signature);
            List<LocalVariableNode> lvNodeList = md.localVariables;
            for (LocalVariableNode lvn : lvNodeList) {
                System.out.printf("\tLocal name: %s, startLabel:%s, desc: %s, sig:%s\n",
                        lvn.name, lvn.start.getLabel(), lvn.desc, lvn.signature);
            }
            Iterator<AbstractInsnNode> instraIter = md.instructions.iterator();
            while (instraIter.hasNext()) {
                AbstractInsnNode aNode = instraIter.next();
                if (aNode instanceof MethodInsnNode) {
                    MethodInsnNode mNode = (MethodInsnNode) aNode;
                    if ("org/apache/commons/lang/StringUtils".equals(mNode.owner)
                            && "lastIndexOf".equals(mNode.name)) {
                        AbstractInsnNode temp = mNode.getPrevious();
                        while (temp != null && !(temp instanceof LineNumberNode)) {
                            if (temp instanceof LdcInsnNode) {
                                LdcInsnNode paramNode = (LdcInsnNode) temp;
                                System.out.printf("\tparamNode value: %s\n", paramNode.cst);
                            }
                            temp = temp.getPrevious();
                        }
                    }
//                    System.out.printf("\tmNode value[owner=%s, name=%s, desc=%s\n", mNode.owner, mNode.name, mNode.desc);

                }
            }
        }


//        MethodVisitor mv = cn.visitMethod(Opcodes.AALOAD, "<init>", Type
//                .getType(String.class).toString(), null, null);
//        mv.visitFieldInsn(Opcodes.GETFIELD, Type.getInternalName(String.class), "str", Type
//                .getType(String.class).toString());
//        System.out.println(cn.name);
//        List<FieldNode> fieldList = cn.fields;
//        for (FieldNode fieldNode : fieldList) {
//            System.out.println("Field name: " + fieldNode.name);
//            System.out.println("Field desc: " + fieldNode.desc);
//            System.out.println("Filed value: " + fieldNode.value);
//            System.out.println("Filed access: " + fieldNode.access);
//            if (fieldNode.visibleAnnotations != null) {
//                for (Object anNodeObj : fieldNode.visibleAnnotations) {
//                    System.out.println(((AnnotationNode) anNodeObj).desc);
//                }
//            }
//        }

    }
}
