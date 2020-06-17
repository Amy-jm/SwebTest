package com.yeastar.untils;

import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;


/**
 * @author huangjx
 * @create 2019-10-18 9:47
 **/
@Log4j2
public class BrowserUtils {
    public  void  getLogType_Browser(WebDriver driver) {
        try {
            LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
            System.out.println("===============BROWSER.start=====================");
            for (LogEntry entry : logEntries) {
                System.out.println(entry.getLevel() + " " + entry.getMessage());
            }
            System.out.println("===============BROWSER.end=====================");
        }catch(org.openqa.selenium.UnsupportedCommandException ex){
            log.error("[获取浏览器日志异常]"+ex);
        }
    }

    public  void  getLogType_Browser(Method method, WebDriver driver) throws  Exception{
        LogEntries logEntries = driver.manage().logs().get(LogType.BROWSER);
        log.fatal("===["+method.getName()+"]===BROWSER.LOG.start=====================");
        for (LogEntry entry : logEntries) {
            log.fatal(entry.getLevel() + " " + entry.getMessage());
        }
        log.fatal("===["+method.getName()+"]===BROWSER.LOG.end=====================");
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


    public static void analyzeLog(WebDriver driver) {
        System.out.println("=================LogType.BROWSER");
        LogEntries logEntries_Browser = driver.manage().logs().get(LogType.BROWSER);
        for (LogEntry entry : logEntries_Browser) {
            System.out.println(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
            //do something useful with the data
        }

        System.out.println("=================LogType.CLIENT");
        LogEntries logEntries_CLIENT = driver.manage().logs().get(LogType.CLIENT);
        for (LogEntry entry : logEntries_CLIENT) {
            System.out.println(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
            //do something useful with the data
        }


        System.out.println("=================LogType.SERVER");
        LogEntries logEntries_SERVER = driver.manage().logs().get(LogType.SERVER);
        for (LogEntry entry : logEntries_Browser) {
            System.out.println(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
            //do something useful with the data
        }

        System.out.println("=================LogType.PERFORMANCE");
        //this is just to make you know user number of logs with LogType as PERFORMANCE
        List<LogEntry> entries = driver.manage().logs().get(LogType.PERFORMANCE).getAll();
        System.out.println(entries.size() + " " + LogType.PERFORMANCE + " log entries found");


        //as the request and response will consists HUGE amount of DATA so I will be write it into text file for reference
        for (LogEntry entry : entries) {
            String data = (new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage()).toString();
            System.out.println(data);
        }
    }

    }