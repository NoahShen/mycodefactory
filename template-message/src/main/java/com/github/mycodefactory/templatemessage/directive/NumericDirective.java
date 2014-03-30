package com.github.mycodefactory.templatemessage.directive;

import com.github.mycodefactory.templatemessage.utils.RandomUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;

import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;

public class NumericDirective extends Directive {
    // 指定指令的名称
    @Override
    public String getName() {
        return "numeric";
    }

    // 指定指令类型为块指令
    @Override
    public int getType() {
        return LINE;
    }

    // 指令内容操作
    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        SimpleNode snMin = (SimpleNode) node.jjtGetChild(0);
        SimpleNode snMax = (SimpleNode) node.jjtGetChild(1);

        Object minObj = snMin.value(context);
        Object maxObj = snMax.value(context);
        if (minObj instanceof Integer
                && maxObj instanceof Integer) {
            int value = RandomUtils.getRandom((Integer) minObj, (Integer) maxObj);
            writer.write(String.valueOf(value));
        } else if (minObj instanceof Double
                && maxObj instanceof Double){
            double min = (Double) minObj;
            double max = (Double) maxObj;
            double randValue = RandomUtils.getRandomDouble(min, max);
            String v = "";
            if (node.jjtGetNumChildren() == 3) {
                SimpleNode snFormat = (SimpleNode) node.jjtGetChild(2);
                String format = (String) snFormat.value(context);
                DecimalFormat df = new DecimalFormat(format);
                v = df.format(randValue);
            } else {
                v = String.valueOf(randValue);
            }
            writer.write(v);
        }

        return true;
    }
}