package com.yeastar.linkustest.tools.reporter;

import org.apache.log4j.PropertyConfigurator;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import static com.yeastar.swebtest.driver.Config.currentPath;
import static com.yeastar.swebtest.driver.DataReader.LOCAL_LOG_FILE;
import static com.yeastar.swebtest.driver.DataReader.SCREENSHOT_PATH;

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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        String currentTime = String.valueOf(sdf.format(new Date()));
        String tmpname = "【测试类场景初始化】_"+currentTime;
        reporterOut(tmpname,message,1);
    }

    public static void infoAfterClass(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        String currentTime = String.valueOf(sdf.format(new Date()));
        String tmpname = "【测试类场景恢复】_"+currentTime;
        message = message+"\n\n";
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
        SimpleDateFormat sdf = new SimpleDateFormat("HH时mm分ss秒");
        String currentTime = String.valueOf(sdf.format(new Date()));
        String tmpname = "【"+currentTime+" 执行操作】";

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
//        System.out.println("LOCAL_LOG_PATH="+currentPath+"localLog\\"+LOCAL_LOG_FILE);

        System.out.println(tmpname+": "+message);
//        if(String(emptynum+tmpname+message))
        String toDelPic = emptynum+tmpname+message;
        if(toDelPic.contains("BeforeTest")){//
            clearDirFiles(SCREENSHOT_PATH,500);
//            clearDirFiles(currentPath+"build\\reports\\tests",300);
            clearDirFiles(currentPath+"localLog",500);
        }
        WriteStringToFilePath(currentPath+"localLog\\"+LOCAL_LOG_FILE,emptynum+tmpname+message+"\r\n");
        org.testng.Reporter.log(emptynum+tmpname+message);
    }

    /**
     * //文件数量超过100，清空这个文件夹
     * @param path
     */
    public static void clearDirFiles(String path,int maxNum){
        System.out.println("clearDirFiles Path :"+path);
        File file = new File(path);
        int fileNum=0;
        if(file.isDirectory()){
            File []files = file.listFiles();
            System.out.println("file count "+files.length);
            fileNum = files.length;
        }
        if(fileNum >maxNum){
            System.out.println("to delete, "+fileNum);
            File []files = file.listFiles();
            for(int i=0; i<files.length; i++){
                files[i].delete();
            }
        }
    }
    public static void WriteStringToFilePath(String filePath,String content) {

        try {
            File f = new File(filePath);
            if (f.exists()) {
            } else {
//                System.out.print("文件不存在");
                f.createNewFile();// 不存在则创建
            }
            PrintStream ps = new PrintStream(new FileOutputStream(f,true));
//            ps.println(content);// 往文件里写入字符串
            ps.append(content);// 在已有的基础上添加字符串
            ps.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

