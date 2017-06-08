package com.yeastar.swebtest.tools.reporter;

import org.apache.log4j.PropertyConfigurator;

import java.io.*;
import java.util.Properties;

/**
 * Created by GaGa on 2017-06-05.
 */
public class Reporter  {

    public static void setOutPutFile(String filename) throws IOException {
        String filePath = System.getProperty("user.dir")

                + "/libs/output.properties";

        Properties properties = new Properties();
        //InputStream inputStream = new BufferedInputStream(new FileInputStream(filePath));
        //properties.load(inputStream);
        OutputStream outputStream = new FileOutputStream(filePath);
        properties.setProperty("OUTPUT_FILE",filename);
        properties.store(outputStream,"Update OUTPUT_FILE name");

    }

    //将图片或链接加到TestNG报告里面去
    public static void sendReport(String typeSend, String message, String file) {
        org.testng.Reporter.setEscapeHtml(false);
        String sreenShotLink = "<a href=\"file:///"+ file + "\" target=\"_blank\">" + message + "</a>";
        String sreenShotImg = "<p>截图:<img id=\"img\" src=\"file:///" + file + "\" alt=\"Smiley face\" width=\"200\" height=\"200\"></p>";
        String reportStr = file;
        switch (typeSend) {
            case "link":
                reporterOut("  ",sreenShotLink,2);
                break;
            case "img":
                reporterOut("  ",sreenShotImg,2);
                break;
            case "txt":
                reporterOut("  ",reportStr,2);
                break;
            default:
                break;
        }
    }
    public static void infoBeforeSuite(String message) {
        String tmpname = "【Suite场景初始化】";
        reporterOut(tmpname,message,1);
    }

    public static void infoAfterSuite(String message) {
        String tmpname = "【Suite场景恢复】 ";
        reporterOut(tmpname,message,1);
    }

    public static void infoBeforeTest(String message) {
        String tmpname = "【场景初始化】";
        reporterOut(tmpname,message,1);
    }

    public static void infoAfterTest(String message) {
        String tmpname = "【场景恢复】";
        reporterOut(tmpname,message,1);
    }

    public static void infoBeforeClass(String message) {
        String tmpname = "【测试类场景初始化】";
        reporterOut(tmpname,message,1);
    }

    public static void infoAfterClass(String message) {
        String tmpname = "【测试类场景恢复】";
        reporterOut(tmpname,message,1);
    }

    public static void infoBeforeGroups(String message) {
        String tmpname = "【用例组场景初始化】";
        reporterOut(tmpname,message,1);
    }

    public static void infoAfterGroups(String message) {
        String tmpname = "【用例组场景恢复】";
        reporterOut(tmpname,message,1);
    }

    public static void infoBeforeMethod(String message) {
        String tmpname = "【测试方法场景初始化】";
        reporterOut(tmpname,message,2);
    }

    public static void infoAfterMethod(String message) {
        String tmpname = "【测试方法场景恢复】";
        reporterOut(tmpname,message,2);
    }

    public static void infoExec(String message) {
        String tmpname = "【执行操作】";
        reporterOut(tmpname,message,2);
    }

    public static void infoEndExec(String message) {
        String tmpname = "【结束操作】";
        reporterOut(tmpname,message,2);
    }

    public static void infoCheck(String message) {
        String tmpname = "【执行检查】";
        reporterOut(tmpname,message,2);
    }

    public static void infoEndCheck(String message) {
        String tmpname = "【结束检查】";
        reporterOut(tmpname,message,2);
    }

    public static void infoConfig(String message) {
        String tmpname = "【测试环境配置检查】";
        reporterOut(tmpname,message,2);
    }

    public static void error(String message) {
        reporterOut("Error: ",message,3);
    }

    public static void pass(String message) {
        reporterOut("Pass: ",message,3);
    }

    private static void reporterOut(String tmpname, String message, int layer) {
        String emptynum = "";
        switch (layer) {
            case 1:
                emptynum = "";
                break;
            case 2:
                emptynum = "  ";
                break;
            case 3:
                emptynum = "    ";
                break;
            case 4:
                emptynum = "      ";
                break;
            case 5:
                emptynum = "        ";
                break;
            default:
        }
        org.testng.Reporter.log(emptynum+tmpname+message);
    }
}
