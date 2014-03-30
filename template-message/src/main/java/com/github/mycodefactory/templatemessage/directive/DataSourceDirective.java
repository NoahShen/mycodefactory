package com.github.mycodefactory.templatemessage.directive;

import com.github.mycodefactory.templatemessage.utils.ExcelUtils;
import com.github.mycodefactory.templatemessage.utils.RandomUtils;
import jxl.read.biff.BiffException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.runtime.parser.node.SimpleNode;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DataSourceDirective extends Directive {

    // 指定指令的名称
    @Override
    public String getName() {
        return "dataSource";
    }

    // 指定指令类型为块指令
    @Override
    public int getType() {
        return LINE;
    }

    // 指令内容操作
    @Override
    public boolean render(InternalContextAdapter context, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        SimpleNode sn = (SimpleNode) node.jjtGetChild(0);
        String field = (String) sn.value(context);

        SimpleNode snDsType = (SimpleNode) node.jjtGetChild(1);
        String dataSourceType = (String) snDsType.value(context);

        List<String> values = getValueFromDS(field, dataSourceType);
        if (CollectionUtils.isNotEmpty(values)) {
            writer.write(values.get(RandomUtils.getRandom(values.size())));
        }
        return true;
    }

    private List<String> getValueFromDS(String field, String dsType) {
        if ("excel".equalsIgnoreCase(dsType)) {
            try {
                String resourcePath = System.getProperty("resourcePath");
                File f = new File(resourcePath + field + ".xls");
                List<Map<String, String>> dealIdList = ExcelUtils.readFromExcel(f);
                List<String> values = new ArrayList<String>(dealIdList.size());
                for (Map<String, String> dealInfoMap : dealIdList) {
                    values.add(dealInfoMap.get(field));
                }
                return values;
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return Collections.emptyList();
    }
}