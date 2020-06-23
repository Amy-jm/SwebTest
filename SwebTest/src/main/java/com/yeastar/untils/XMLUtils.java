package com.yeastar.untils;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

public class XMLUtils {
//    public static void main(String[] args) throws Exception {
//        String packageName = "com.myProject.testcase";
//        File file=new File("E:\\defaulXml.xml");
//        CreateXMLByDOM4J(file);
//
//    }


    public static void CreateXMLByDOM4J(File dest,String groupsKey, String excludeKey, Set<String> classNames) {
        // 创建Document对象
        Document document = DocumentHelper.createDocument();
        document.addDocType("suite SYSTEM \"http://testng.org/testng-1.0.dtd\" ","","");
        // 创建根节点
        Element suite = document.addElement("suite");
        //为rss根节点添加属性
        suite.addAttribute("name", "DefaultGroupsSuite");
        suite.addAttribute("parallel", "methods");
        suite.addAttribute("thread-count", "5");

        // 创建二级子节点Listeners,test
        Element listeners = suite.addElement("listeners");
        Element listener_0 = listeners.addElement("listener");
        Element listener_1 = listeners.addElement("listener");
        Element listener_2 = listeners.addElement("listener");

        listener_0.addAttribute("class-name","org.uncommons.reportng.HTMLReporter");
        listener_1.addAttribute("class-name","org.uncommons.reportng.JUnitXMLReporter");
        listener_2.addAttribute("class-name","com.gillion.auto.untils.TestNGListener");

        Element tests = suite.addElement("test");
        tests.addAttribute("name","GroupTest");
        // 创建test子节点
        Element groups = tests.addElement("groups");
        Element classes = tests.addElement("classes");

        // 设置groups 润节点
        Element run = groups.addElement("run");

        //include node
        if (!groupsKey.equals("")){
            if(groupsKey.contains(",")){
                String[] strs=groupsKey.split(",");
                for(int i=0,len=strs.length;i<len;i++){
                    Element include = run.addElement("include");
                    include.addAttribute("name",strs[i].toString());
                }
            }else{
                Element include = run.addElement("include");
                include.addAttribute("name",groupsKey);
            }

        }

        //exclude node
        if (!excludeKey.equals("")){
            if(excludeKey.contains(",")){
                String[] strs=excludeKey.split(",");
                for(int i=0,len=strs.length;i<len;i++){
                    Element exclude = run.addElement("exclude");
                    exclude.addAttribute("name",strs[i].toString());
                }
            }else{
                Element exclude = run.addElement("exclude");
                exclude.addAttribute("name",excludeKey);
            }

        }


        //classes
        if (classNames != null) {
            for (String className : classNames) {
                Element classNode = classes.addElement("class");
                classNode.addAttribute("name",className);
                System.out.println(className);
            }
        }


        // 创建输出格式(OutputFormat对象)
        OutputFormat format = OutputFormat.createPrettyPrint();

        ///设置输出文件的编码
//      format.setEncoding("GBK");

        try {
            // 创建XMLWriter对象
            XMLWriter writer = new XMLWriter(new FileOutputStream(dest), format);

            //设置不自动进行转义
            writer.setEscapeText(false);

            // 生成XML文件
            writer.write(document);

            //关闭XMLWriter对象
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void CreateXMLByDOM4J(File dest,String groupsKey, Set<String> classNames) {
        // 创建Document对象
        Document document = DocumentHelper.createDocument();
        document.addDocType("suite SYSTEM \"http://testng.org/testng-1.0.dtd\" ","","");
        // 创建根节点
        Element suite = document.addElement("suite");
        //为rss根节点添加属性
        suite.addAttribute("name", "DefaultGroupsSuite");
//        suite.addAttribute("parallel", "methods");
//        suite.addAttribute("thread-count", "5");
        suite.addAttribute(" preserve-order", "false");

        // 创建二级子节点Listeners,test
        Element listeners = suite.addElement("listeners");
        Element listener_0 = listeners.addElement("listener");
        Element listener_1 = listeners.addElement("listener");
        Element listener_2 = listeners.addElement("listener");

        listener_0.addAttribute("class-name","com.yeastar.untils.AllureReporterListener");
        listener_1.addAttribute("class-name","com.yeastar.untils.RetryListener");
        listener_2.addAttribute("class-name","com.yeastar.untils.TestNGListener");

        Element tests = suite.addElement("test");
        tests.addAttribute("name","GroupTest");
        tests.addAttribute("allow-return-values","true");
        // 创建test子节点
        Element groups = tests.addElement("groups");
        Element classes = tests.addElement("classes");

        // 设置groups 润节点
        Element run = groups.addElement("run");

        //include node
        if (!groupsKey.equals("")){
            if(groupsKey.contains(",")){
                String[] strs=groupsKey.split(",");
                for(int i=0,len=strs.length;i<len;i++){
                    Element include = run.addElement("include");
                    include.addAttribute("name",strs[i].toString());
                }
            }else{
                Element include = run.addElement("include");
                include.addAttribute("name",groupsKey);
            }

        }
//
//        //exclude node
//        if (!excludeKey.equals("")){
//            if(excludeKey.contains(",")){
//                String[] strs=excludeKey.split(",");
//                for(int i=0,len=strs.length;i<len;i++){
//                    Element exclude = run.addElement("exclude");
//                    exclude.addAttribute("name",strs[i].toString());
//                }
//            }else{
//                Element exclude = run.addElement("exclude");
//                exclude.addAttribute("name",excludeKey);
//            }
//
//        }


        //classes
        if (classNames != null) {
            for (String className : classNames) {
                Element classNode = classes.addElement("class");
                classNode.addAttribute("name",className);
                System.out.println(className);
            }
        }


        // 创建输出格式(OutputFormat对象)
        OutputFormat format = OutputFormat.createPrettyPrint();

        ///设置输出文件的编码
//      format.setEncoding("GBK");

        try {
            // 创建XMLWriter对象
            System.out.println("[defaultGroup xml filePath] "+dest);
            XMLWriter writer = new XMLWriter(new FileOutputStream(dest), format);

            //设置不自动进行转义
            writer.setEscapeText(false);

            // 生成XML文件
            writer.write(document);

            //关闭XMLWriter对象
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
