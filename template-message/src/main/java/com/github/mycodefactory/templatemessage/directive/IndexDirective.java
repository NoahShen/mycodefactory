package com.github.mycodefactory.templatemessage.directive;

import com.github.mycodefactory.templatemessage.utils.RandomUtils;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

import java.io.IOException;
import java.io.Writer;

public class IndexDirective extends Directive {

    private static long id = 0;
    // 指定指令的名称
    @Override
    public String getName() {
        return "index";
    }

    // 指定指令类型为块指令
    @Override
    public int getType() {
        return LINE;
    }

    // 指令内容操作
    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        writer.write(String.valueOf(id++));
        return true;
    }
}