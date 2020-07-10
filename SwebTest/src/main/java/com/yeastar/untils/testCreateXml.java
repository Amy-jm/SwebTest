package com.yeastar.untils;

import java.io.File;
import java.util.Set;

public class testCreateXml {

    public static void main(String[] args) throws Exception {
        File file=new File(System.getProperty("user.dir")+File.separator+"SwebTest"+File.separator+"suite"+File.separator+"defaultGroup.xml");

        String packageName = "com.yeastar.testcase.pseries";
        String filepath = System.getProperty("user.dir")+File.separator+"SwebTest"+File
        .separator+"src"+File.separator+"test"+File.separator+"java"+File.separator+"com"+File.separator+"yeastar"+File.separator+"testcase"+File.separator+"pseries";
        System.out.println("[filepath] "+ filepath);
        System.out.println("[packageName] "+ packageName);

        Set<String> classNames = new PackageUtils().getClassNameFromDir(filepath,packageName, false);
        if (classNames != null) {
            for (String className : classNames) {
                System.out.println("[1]"+className);
            }
        }

        XMLUtils xmlUtils = new XMLUtils();
       // xmlUtils.CreateXMLByDOM4J(file,args[0],args[1],classNames);
        System.out.println("[keyword] "+ args[0]);
        xmlUtils.CreateXMLByDOM4J(file,args[0],classNames);


    }
}
