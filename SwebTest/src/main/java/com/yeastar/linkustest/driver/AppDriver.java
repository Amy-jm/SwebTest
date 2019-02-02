package com.yeastar.linkustest.driver;

import com.yeastar.linkustest.tools.reporter.Reporter;
import com.yeastar.linkustest.tools.ysassert.YsAssert;
import com.yeastar.linkustest.tools.pjsip.UserAccount;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.Connection;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AppDriver extends Config{

    public static AndroidDriver driver1;
    public static AndroidDriver driver2;
    public int initialDriver1() throws MalformedURLException {

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("deviceName", linkusA_udid);
        capabilities.setCapability("udid", linkusA_udid);// 测试机adb devices获取
        capabilities.setCapability("automationName", "Appium");
        capabilities.setCapability("platformName", "Android");
        capabilities.setCapability("platformVersion", linkusA_androidVer);
        capabilities.setCapability("appPackage", "com.yeastar.linkus");
        capabilities.setCapability("appActivity", "com.yeastar.linkus.business.WelcomeActivity");
        capabilities.setCapability("newCommandTimeout", "180");
//        capabilities.setCapability("unicodeKeyboard", true);//使用 Unicode 输入法
        capabilities.setCapability("resetKeyboard", true);  //重置输入法到原有状态
        capabilities.setCapability("noReset", true);//防止重安装app

        driver1 = new AndroidDriver(new URL("http://127.0.0.1:" + linkusA_appiumPort + "/wd/hub"), capabilities);
        driver1.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS); //隐式等待10s，10s内有找到就直接跳转，10s后还未找到才报错
//        driver1.setConnection(Connection.WIFI);
        return AD_SUCCESS;
    }

    public int initialDriver2() throws MalformedURLException {

        DesiredCapabilities capabilities2 = new DesiredCapabilities();
        capabilities2.setCapability("deviceName", linkusB_udid);
        capabilities2.setCapability("udid", linkusB_udid);// 测试机adb devices获取
        capabilities2.setCapability("automationName", "Appium");
        capabilities2.setCapability("platformName", "Android");
        capabilities2.setCapability("platformVersion", linkusB_androidVer);
        capabilities2.setCapability("appPackage", "com.yeastar.linkus");
        capabilities2.setCapability("appActivity", "com.yeastar.linkus.business.WelcomeActivity");
        capabilities2.setCapability("unicodeKeyboard", true);//使用 Unicode 输入法
        capabilities2.setCapability("resetKeyboard", true);  //重置输入法到原有状态
        capabilities2.setCapability("noReset", true);//防止重安装app
        capabilities2.setCapability("newCommandTimeout", "180");
        driver2 = new AndroidDriver(new URL("http://127.0.0.1:" + linkusB_appiumPort + "/wd/hub"), capabilities2);
//        driver2.setConnection(Connection.WIFI);
        return AD_SUCCESS;
    }
    /**
     * 获取对象
     * @param obj
     * @return
     */
    public WebElement findElement1(By obj) {
        return findElement(driver1,obj);
    }
    public WebElement findElement2(By obj) {
        return findElement(driver2,obj);
    }
    /**
     * 根据driver 和obj 查找元素
     * @param driver
     * @param obj
     * @return
     */
    public WebElement findElement(AndroidDriver driver, By obj) {
        try {
            int i = 0;
            while (i < 3) {
                ys_waitingTime(800);
                if (driver.findElement(obj).isDisplayed()) {
                    break;
                } else{
                    i++;
                }
            }
        } catch (Exception e) {
            System.out.println(obj + "控件未找到！" );
            return null;
        }
        return driver.findElement(obj);
    }
    /**

     * 等待延时
     * @param msec
     */
    public void ys_waitingTime(int msec) {
        try {
            Thread.sleep(msec);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void ys_waitingFor(WebElement e){
        ys_waitingTime(1500);
        int i=0;
        while(true){
            if(e.isDisplayed()){
                break;
            }
            ys_waitingTime(500);
            i++;
            if(i>20)
                break;
        }
        ys_waitingTime(1500);
    }

    public String executedCmd(String cmd)  {
        System.out.println("cmd = "+cmd);
        String ret = null;
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            InputStream is = p.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            while (reader.readLine()!= null){
                ret = ret + reader.readLine();
            }
            p.waitFor();
            is.close();
            reader.close();
            p.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void logout(AndroidDriver driver){
        Reporter.infoExec("登出");
        findElement(driver,me.me_tab).click();
        findElement(driver,me.settings).click();
        findElement(driver,settings.logout).click();
        YsAssert.assertEquals(findElement(driver,login.login).isDisplayed(),true,"登出成功");
    }

    public void checkCDR(AndroidDriver driver, String name,String type,String status, int row){
        checkCDR(driver,name,"",type,status,row);
    }
    public void checkCDR(AndroidDriver driver, String name,String type,String status){
        checkCDR(driver,name,"",type,status,1);
    }
    public void checkCDR(AndroidDriver driver, String name ,String count,String type,String status,int row){
        ys_waitingTime(3000);
        String str="";
        if(driver == driver1){
            str = "设备1";
        }else {
            str = "设备2";
        }
        findElement(driver,dialpad.dialpad_tab).click();
        findElement(driver,dialpad.all_page).click();

        //实际CDR
        if(row == 1){
            String m_name = findElement(driver,dialpad.historyName1).getText();
            String m_type = findElement(driver,dialpad.historyType1).getText();
            YsAssert.assertEquals(m_name,name,str+"CDR分机名称错误为"+m_name);
            YsAssert.assertEquals(m_type,type,str+"CDR分机类型错误"+m_type);
            if(!count.isEmpty()){
                String m_count = findElement(driver,dialpad.historyCount1).getText();
                YsAssert.assertEquals(m_count,name,str+"CDR分机名称错误");
            }
            findElement(driver,dialpad.historyDetail1).click();
            String m_status = findElement(driver,dialpad.historyDetailType1).getText();
            findElement(driver,dialpad.historyDetailBack).click();
            YsAssert.assertInclude(m_status,status,str+"CDR详细信息呼叫类型错误为"+m_status);
        }else if(row == 2){
            String m_name = findElement(driver,dialpad.historyName2).getText();
            String m_type = findElement(driver,dialpad.historyType2).getText();
            YsAssert.assertEquals(m_name,name,str+"CDR分机名称错误为"+m_name);
            YsAssert.assertEquals(m_type,type,str+"CDR分机类型错误"+m_type);
            if(!count.isEmpty()){
                String m_count = findElement(driver,dialpad.historyCount2).getText();
                YsAssert.assertEquals(m_count,name,str+"CDR分机名称错误");
            }
            findElement(driver,dialpad.historyDetail2).click();
            String m_status = findElement(driver,dialpad.historyDetailType1).getText();
            findElement(driver,dialpad.historyDetailBack).click();
            YsAssert.assertInclude(m_status,status,str+"CDR详细信息呼叫类型错误为"+m_status);

        }

    }

    public void toCall(AndroidDriver driver, String... num){
        ArrayList<String> numList = new ArrayList<>();
        for (String index:num){
            numList.add(index);
        }
        for (int i=0 ; i<numList.size(); i++){
            if(numList.get(i).equals("*")){
                findElement(driver,dialpad.numStar).click();
            }else if(numList.get(i).equals("#")){
                findElement(driver,dialpad.numPound).click();
            }else {
                switch (Integer.parseInt(numList.get(i))){
                    case 0:findElement(driver,dialpad.num0).click();break;
                    case 1:findElement(driver,dialpad.num1).click();break;
                    case 2:findElement(driver,dialpad.num2).click();break;
                    case 3:findElement(driver,dialpad.num3).click();break;
                    case 4:findElement(driver,dialpad.num4).click();break;
                    case 5:findElement(driver,dialpad.num5).click();break;
                    case 6:findElement(driver,dialpad.num6).click();break;
                    case 7:findElement(driver,dialpad.num7).click();break;
                    case 8:findElement(driver,dialpad.num8).click();break;
                    case 9:findElement(driver,dialpad.num9).click();break;
                }
            }
        }
        findElement(driver,dialpad.dial).click();
    }
    public void toCall(AndroidDriver driver,ArrayList<String>  numList){
        for (int i=0 ; i<numList.size(); i++){
            if(numList.get(i).equals("*")){
                findElement(driver,dialpad.numStar).click();
            }else if(numList.get(i).equals("#")){
                findElement(driver,dialpad.numPound).click();
            }else {
                switch (Integer.parseInt(numList.get(i))){
                    case 0:findElement(driver,dialpad.num0).click();break;
                    case 1:findElement(driver,dialpad.num1).click();break;
                    case 2:findElement(driver,dialpad.num2).click();break;
                    case 3:findElement(driver,dialpad.num3).click();break;
                    case 4:findElement(driver,dialpad.num4).click();break;
                    case 5:findElement(driver,dialpad.num5).click();break;
                    case 6:findElement(driver,dialpad.num6).click();break;
                    case 7:findElement(driver,dialpad.num7).click();break;
                    case 8:findElement(driver,dialpad.num8).click();break;
                    case 9:findElement(driver,dialpad.num9).click();break;
                }
            }
        }
        findElement(driver,dialpad.dial).click();
    }
    public void toCall(AndroidDriver driver, String num){
        ArrayList<String> numList=new ArrayList<>();
        for (int i=0; i<num.length(); i++){
            numList.add(String.valueOf(num.charAt(i)));
        }
        toCall(driver,numList);
    }

    public void toCallingCall(AndroidDriver driver, String num){
        ArrayList<String> numList=new ArrayList<>();
        for (int i=0; i<num.length(); i++){
            numList.add(String.valueOf(num.charAt(i)));
        }
        for (int i=0 ; i<numList.size(); i++){
            if(numList.get(i).equals("*")){
                findElement(driver,calling.numStar).click();
            }else if(numList.get(i).equals("#")){
                findElement(driver,calling.numPound).click();
            }else {
                switch (Integer.parseInt(numList.get(i))){
                    case 0:findElement(driver,calling.num0).click();break;
                    case 1:findElement(driver,calling.num1).click();break;
                    case 2:findElement(driver,calling.num2).click();break;
                    case 3:findElement(driver,calling.num3).click();break;
                    case 4:findElement(driver,calling.num4).click();break;
                    case 5:findElement(driver,calling.num5).click();break;
                    case 6:findElement(driver,calling.num6).click();break;
                    case 7:findElement(driver,calling.num7).click();break;
                    case 8:findElement(driver,calling.num8).click();break;
                    case 9:findElement(driver,calling.num9).click();break;
                }
            }
        }
        findElement(driver,calling.dial).click();
    }

    /**
     * 获取分机通话状态
     * @param username getExtensionStatus(1101, TALKING, 8)
     */
    public int getExtensionStatus(String username, int expectStatus, int timeout) {
        UserAccount account;
        int time = 0;
        int status = -1;
        while (time<=timeout) {
            ys_waitingTime(1000);
            account = pjsip.getUserAccountInfo(Integer.parseInt(username));
            if (account == null) {
                status=-1;
                System.out.println("first--------account is null----------");
            }
            if (account.status == expectStatus) {
                status=account.status;
                System.out.println("Second-------get status succ--------------");
                return status;
            }
            if (time == timeout) {
                status=account.status;
                System.out.println("third-------get status Timeout----------");
            }
            time++;
        }
        System.out.println("分机通话状态："+status);
        return status;
    }
}
