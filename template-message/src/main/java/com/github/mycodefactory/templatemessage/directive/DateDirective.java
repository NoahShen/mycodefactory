package com.github.mycodefactory.templatemessage.directive;

import org.apache.commons.lang.time.DateUtils;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateDirective extends Directive {

    // 指定指令的名称
    @Override
    public String getName() {
        return "date";
    }

    // 指定指令类型为块指令
    @Override
    public int getType() {
        return LINE;
    }

    // 指令内容操作
    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        SimpleNode snFormat = (SimpleNode) node.jjtGetChild(0);
        String format = (String) snFormat.value(context);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        writer.write(sdf.format(new Date()));
        return true;
    }
}