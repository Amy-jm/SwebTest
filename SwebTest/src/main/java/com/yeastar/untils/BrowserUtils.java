package com.yeastar.untils;

import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;


/**
 * @author huangjx
 * @create 2019-10-18 9:47
 **/
@Log4j2
public class BrowserUtils {

    public static BrowserUtils instance;
    public static synchronized BrowserUtils getInstance(){
        if(instance == null)
            instance = new BrowserUtils();
        return instance;
    }

    public  void  getLogType_Browser(WebDriver driver) {
        try {
            LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
            System.out.println("\r\n===============BROWSER.start=====================");
            for (LogEntry entry : logEntries) {
                System.out.println(entry.getLevel() + " " + entry.getMessage());
            }
            System.out.println("\r\n===============BROWSER.end=====================");
        }catch(org.openqa.selenium.UnsupportedCommandException ex){
            log.error("[获取浏览器日志异常]"+ex);
        }
    }

    /**
     * 获取浏览器console
     * @param method
     * @param driver
     * @throws Exception
     */
    public  void  getLogType_Browser(Method method, WebDriver driver){

        try {
            LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
            log.fatal("\r\n===["+method.getName()+"]===BROWSER.LOG.start=====================");
            log.debug("[BrowserUtils remote session]{}",driver);
            for (LogEntry entry : logEntries) {
                log.fatal(entry.getLevel() + " " + entry.getMessage());
            }
            log.debug("[BrowserUtils remote session]{}",driver);
            log.fatal("\r\n===["+method.getName()+"]===BROWSER.LOG.end=====================");
        }catch (Exception e){
            log.error("[getLogType_Browser error]{}",e.getMessage()+e.getStackTrace());
        }
    }

    public  static void getLogType_Performance(WebDriver driver){
        //this is just to make you know user number of logs with LogType as PERFORMANCE
        List<LogEntry> entries = driver.manage().logs().get(LogType.PERFORMANCE).getAll();
        System.out.println(entries.size() + " " + LogType.PERFORMANCE + " log entries found");


        //as the request and response will consists HUGE amount of DATA so I will be write it into text file for reference
        for (LogEntry entry : entries)
        {
            try
            {
                FileWriter f = new FileWriter("Performance", true);
                BufferedWriter bufferedWriter = new BufferedWriter(f);

                String data =  (new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage()).toString();

                bufferedWriter.write(data);
                bufferedWriter.write("\n"+"@@@@@@@@@@"+"\n\n");
                bufferedWriter.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

    }


    public static void getAnalyzeLog(WebDriver driver) {
        System.out.println("=================LogType.BROWSER");
        LogEntries logEntries_Browser = driver.manage().logs().get(LogType.BROWSER);
        for (LogEntry entry : logEntries_Browser) {
            log.fatal(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
            //do something useful with the data
        }

        System.out.println("=================LogType.CLIENT");
        LogEntries logEntries_CLIENT = driver.manage().logs().get(LogType.CLIENT);
        for (LogEntry entry : logEntries_CLIENT) {
            log.fatal(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
            //do something useful with the data
        }


        System.out.println("=================LogType.SERVER");
        LogEntries logEntries_SERVER = driver.manage().logs().get(LogType.SERVER);
        for (LogEntry entry : logEntries_Browser) {
            log.fatal(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
            //do something useful with the data
        }

        System.out.println("=================LogType.PERFORMANCE");
        //this is just to make you know user number of logs with LogType as PERFORMANCE
        List<LogEntry> entries = driver.manage().logs().get(LogType.PERFORMANCE).getAll();
        System.out.println(entries.size() + " " + LogType.PERFORMANCE + " log entries found");


        //as the request and response will consists HUGE amount of DATA so I will be write it into text file for reference
        for (LogEntry entry : entries) {
            String data = (new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
            log.fatal(data);
        }
    }


    public static void getAnalyzeLog(Method method,WebDriver driver) {
        try {
            log.fatal("\r\n===[" + method.getName() + "]===LogType  start=====================");

            log.fatal("\r\n===[" + method.getName() + "]===LogType.BROWSER.start=====================");
            LogEntries logEntries_Browser = driver.manage().logs().get(LogType.BROWSER);
            for (LogEntry entry : logEntries_Browser) {
                log.fatal(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
                //do something useful with the data
            }

            log.fatal("\r\n===[" + method.getName() + "]===LogType.CLIENT.start=====================");
            LogEntries logEntries_CLIENT = driver.manage().logs().get(LogType.CLIENT);
            for (LogEntry entry : logEntries_CLIENT) {
                log.fatal(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
                //do something useful with the data
            }

            log.fatal("\r\n===[" + method.getName() + "]===LogType.PERFORMANCE.start=====================");
            //this is just to make you know user number of logs with LogType as PERFORMANCE
            String keyWord = "{\\\"errcode";
            String endKeyWord = "\"}\"},\"timestamp\":";
            HashSet hs = new HashSet();
            List<LogEntry> entries = driver.manage().logs().get(LogType.PERFORMANCE).getAll();
            log.fatal(entries.size() + " " + LogType.PERFORMANCE + " log entries found");
            //as the request and response will consists HUGE amount of DATA so I will be write it into text file for reference
            for (LogEntry entry : entries) {
                String data = (new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
                if (data.contains("/api/v") || data.contains("\"Set-Cookie\"") || data.contains("\"status\"") || data.contains("payloadData")) {
                    if(data.contains(keyWord) && data.contains(endKeyWord)){
                        int beginIndex=data.indexOf(keyWord);
                        int endIndex=data.indexOf(endKeyWord);
                        hs.add(data.substring(beginIndex,endIndex));
                    }
                    log.fatal(data);
                }
            }

            log.fatal("\r\n===[" + method.getName() + "]===LogType  end=====================");
            log.fatal("\r\n===[" + method.getName() + "]===LogType  errcode and message start=====================");
            log.error(hs);
            try{
                Cookie cookie = new Cookie("zaleniumMessage", "[errcode and message] "+hs);
                log.debug("[errcode and message] "+ method.getName() +"--->"+hs);
                getWebDriver().manage().addCookie(cookie);
            }catch (org.openqa.selenium.WebDriverException exception){
                log.error("[org.openqa.selenium.WebDriverException: unable to set cookie]");
            }catch(Exception ex){
                log.error("[BrowserUtils on LogType  errcode and message start ] "+ex);
            }
            log.fatal("\r\n===[" + method.getName() + "]===LogType  errcode and message end=====================");
        } catch (Exception e) {
            log.error("[getAnalyzeLog error]{}", e.getMessage() + e.getStackTrace());
        }
    }
}