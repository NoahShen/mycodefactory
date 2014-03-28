package com.github.mycodefactory.templatemessage;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: noahshen
 * Date: 14-3-28
 * Time: 上午9:20
 * To change this template use File | Settings | File Templates.
 */
public class TemplateMain {

    public static void main(String[] args) throws IOException {
        // 初始化Velocity模板引擎
        VelocityEngine ve = new VelocityEngine();
        Properties p = new Properties();
        p.load(TemplateMain.class.getResourceAsStream("/velocity.properties"));
        p.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, TemplateMain.class.getResource("/").getFile());

        ve.init(p);

        System.setProperty("resourcePath", TemplateMain.class.getResource("/").getFile());
        System.out.println("path:" + ve.getProperty(Velocity.FILE_RESOURCE_LOADER_PATH));
        System.out.println("resourcePath:" + System.getProperty("resourcePath"));

        // Velocity获取模板文件，得到模板引用
        Template t = ve.getTemplate("template.vm");

        // 初始化环境，并将数据放入环境
        VelocityContext context = new VelocityContext();

        // 将环境变量和输出部分结合
        StringWriter writer = new StringWriter();
        t.merge(context, writer);
        System.out.println(writer.toString());
    }
}
