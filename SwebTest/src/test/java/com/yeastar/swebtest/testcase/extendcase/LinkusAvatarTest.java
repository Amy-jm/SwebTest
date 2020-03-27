package com.yeastar.swebtest.testcase.extendcase;

import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.pobject.PageDeskTop;
//import com.yeastar.swebtest.pobject.PageMeSetting;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Random;

/**
 * Created by GAGA on 2017/4/26.
 * Linkus分机批量创建头像
 */
public class LinkusAvatarTest extends SwebDriver {
//    static PageMeSetting pageMeSetting = new PageMeSetting();
    static PageDeskTop pageDeskTop = new PageDeskTop();

    public static void changeInformation(String path, String name, String email, String mobile) {
//        pageDeskTop.mesetting.click();
        //pageMeSetting.name.setValue(name);
        //pageMeSetting.email.setValue(email);
        //pageMeSetting.mobile.setValue(mobile);
//        pageMeSetting.clicktochange.click();

        try {
            Thread.sleep(500);

            Runtime.getRuntime().exec("C:\\Users\\Yeastar\\Desktop\\Import.exe "+path);

            Thread.sleep(500);
            Thread.sleep(1000);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        pageMeSetting.save.click();
    }
    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "yeastarYEASTAR6205";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static String getRandomNumber(int length) { //length表示生成字符串的长度
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    @BeforeTest
    public void Init() {
        initialDriver("chrome","https://192.168.2.192:8088");
    }
    @Test
    public void inforTest() {
//        for (int i = 0;i < 190; i++)
//        {
//            int num = 1000 + i;
//            login(String.valueOf(num),"123456");
//            String name = getRandomString(7);
//            changeInformation("C:\\Users\\GaGa\\Desktop\\1\\f-"+i+".png",name,name+"@yeastar.com",getRandomNumber(11));
//            logout();
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        for (int i = 0;i < 190; i++)
//        {
//            int num = 1190 + i;
//            login(String.valueOf(num),"123456");
//            String name = getRandomString(7);
//            changeInformation("C:\\Users\\GaGa\\Desktop\\2\\f-"+i+".png",name,name+"@yeastar.com",getRandomNumber(11));
//            logout();
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
        for (int i = 0;i < 190; i++)
        {
            int num = 1380 + i;
            login(String.valueOf(num),"123456");
            String name = getRandomString(7);
            changeInformation("C:\\Users\\GaGa\\Desktop\\3\\f-"+i+".png",name,name+"@yeastar.com",getRandomNumber(11));
            logout();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    @AfterTest
    public void end() {
        //logout();
    }
}
