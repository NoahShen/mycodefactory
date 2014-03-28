package com.github.mycodefactory.templatemessage.directive;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.TimeUnit;

public class TimeAfterNowDirective extends Directive {

    // 指定指令的名称
    @Override
    public String getName() {
        return "timeAfterNow";
    }

    // 指定指令类型为块指令
    @Override
    public int getType() {
        return LINE;
    }

    // 指令内容操作
    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        SimpleNode snOffset = (SimpleNode) node.jjtGetChild(0);
        long offset = (Integer)snOffset.value(context);

        SimpleNode snUnit = (SimpleNode) node.jjtGetChild(1);
        String snUnitStr = (String) snUnit.value(context);
        TimeUnit unit = getUnit(snUnitStr);

        writer.write(String.valueOf(System.currentTimeMillis() + unit.toMillis(offset)));
        return true;
    }

    private TimeUnit getUnit(String unit) {
        if ("sec".equals(unit) || "second".equals(unit)) {
            return TimeUnit.SECONDS;
        } else if ("min".equals(unit) || "minute".equals(unit)) {
            return TimeUnit.MINUTES;
        } else if ("hour".equals(unit)) {
            return TimeUnit.HOURS;
        } else if ("day".equals(unit)) {
            return TimeUnit.DAYS;
        } else if ("mill".equals(unit) || "millisecond".equals(unit)) {
            return TimeUnit.MILLISECONDS;
        }
        return TimeUnit.MILLISECONDS;
    }
}