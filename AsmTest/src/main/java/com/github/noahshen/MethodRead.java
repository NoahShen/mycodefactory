package com.github.noahshen;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
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
        List<Class<?>> classList = ClassUtils.getClassList("com.github", true);
        for (Class<?> clazz : classList) {
            ClassReader reader = new ClassReader(clazz.getName());
            ClassNode cn = new ClassNode();
            reader.accept(cn, 0);
            String owner = "org/apache/commons/lang/StringUtils";
            String name = "lastIndexOf";
            MethodInsnNode mNode = getInvokeMethodNode(cn, owner, name);
            if (mNode == null) {
                continue;
            }
            List<AbstractInsnNode> paramNodes = getParamNodes(mNode);
            System.out.println(paramNodes);
        }
    }

    private static MethodInsnNode getInvokeMethodNode(ClassNode cn, String owner, String name) {
        List<MethodNode> methodList = cn.methods;
        for (MethodNode md : methodList) {
            Iterator<AbstractInsnNode> it = md.instructions.iterator();
            while (it.hasNext()) {
                AbstractInsnNode aNode = it.next();
                if (!(aNode instanceof MethodInsnNode)) {
                    continue;
                }
                MethodInsnNode mNode = (MethodInsnNode) aNode;
                if (owner.equals(mNode.owner)
                        && name.equals(mNode.name)) {
                    return mNode;
                }
            }
        }
        return null;
    }

    private static List<AbstractInsnNode> getParamNodes(MethodInsnNode mNode) {
        List<AbstractInsnNode> paramNodes = new LinkedList<AbstractInsnNode>();
        AbstractInsnNode temp = mNode.getPrevious();
        while (temp != null && !(temp instanceof LineNumberNode)) {
//            if (temp instanceof LdcInsnNode) {
//                LdcInsnNode paramNode = (LdcInsnNode) temp;
//                System.out.printf("\tparamNode value: %s\n", paramNode.cst);
//            }
            paramNodes.add(0, temp);
            temp = temp.getPrevious();
        }
        return paramNodes;
    }
}
