package com.github.noahshen;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: noahshen
 * Date: 14-3-22
 * Time: 21:28
 * To change this template use File | Settings | File Templates.
 */
public class FieldRead {

    public static void main(String[] args) throws IOException {
        ClassReader reader = new ClassReader("com.github.noahshen.ForReadClass");
        ClassNode cn = new ClassNode();
        reader.accept(cn, 0);
        System.out.println(cn.name);
        List<FieldNode> fieldList = cn.fields;
        for (FieldNode fieldNode : fieldList) {
            System.out.printf("Field name: %s, desc: %s, value: %s, access: %s, signature: %s\n",
                    fieldNode.name, fieldNode.desc, fieldNode.value, fieldNode.access, fieldNode.signature);
        }

    }
}
