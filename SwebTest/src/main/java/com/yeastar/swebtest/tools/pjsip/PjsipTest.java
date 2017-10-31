package com.yeastar.swebtest.tools.pjsip;

import org.testng.annotations.*;

import java.util.*;

import static com.yeastar.swebtest.driver.Config.DEVICE_ASSIST_1;
import static com.yeastar.swebtest.driver.Config.DEVICE_IP_LAN;
import static com.yeastar.swebtest.driver.Config.pjsip;

/**
 * Created by LHR on 2017/6/7.
 */
public class PjsipTest {
    private PjsipApp app;
    private List<UserAccount> accounts ;
    final static int accountNum = 2;
    @BeforeTest
    public void setUp() throws InterruptedException {
        app = new PjsipApp();
        accounts = new ArrayList<UserAccount>();
        app.Pj_Init();
        System.out.println("..is set up");
        Thread.sleep(5000);
    }
    @BeforeMethod
    public void beforeMethod(){
        System.out.println("...is before method");
    }

    @Test
    public void addAcc() throws InterruptedException {
        UserAccount account = new UserAccount();
//        for(int i=0; i<accountNum; i++){
//            account = new UserAccount();
//            account.accId = i;
//            account.username = String.valueOf(1101+i);
//            accounts.add(account);
//        }
//        for (int i=0; i<accountNum; i++){
//            System.out.println("userName "+ accounts.get(i).username);
//            app.Pj_Register_Account(Integer.parseInt(accounts.get(i).username) ,"192.168.7.153");
//        }
        Thread.sleep(5000);
        pjsip.Pj_CreateAccount(1100,"Yeastar202","UDP",5060,-1);
        pjsip.Pj_CreateAccount(3000,"Yeastar202","UDP",5060,3);
        pjsip.Pj_CreateAccount(3030,"Yeastar202","UDP",5060,3);
        pjsip.Pj_Register_Account_WithoutAssist(1100, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(3000,DEVICE_ASSIST_1);
        System.out.println("After Register ..........");
        Thread.sleep(10000);
        app.Pj_Make_Call_No_Answer(3000,"3030",DEVICE_ASSIST_1);
//        app.Pj_Make_Call_Auto_Answer(3000,3030,DEVICE_ASSIST_1,false);
        System.out.println("After Make Call No Answer");
        Thread.sleep(3000);
        app.Pj_Send_Dtmf(3000,"0");
        Thread.sleep(5000);
        app.Pj_Answer_Call(1100,false);
        Thread.sleep(10000);
    }
    @Test
    public void makeCall() throws InterruptedException {
        System.out.println("is make call ");
        pjsip.Pj_CreateAccount(4000,"Yeastar202","UDP",5060,-1);
        pjsip.Pj_CreateAccount(4001,"Yeastar202","UDP",5060,3);
        pjsip.Pj_Register_Account_WithoutAssist(4000, DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(4001, DEVICE_IP_LAN);
        System.out.println("After Register ..........");
        Thread.sleep(5000);
        app.Pj_Make_Call_No_Answer(4000,"4001", DEVICE_IP_LAN);
        Thread.sleep(3000);
//        app.Pj_Make_Call_Auto_Answer(4000,4001,DEVICE_IP_LAN,false);
        System.out.println("After Make Call No Answer");
        app.Pj_Answer_Call(4001,false);
        Thread.sleep(100000);
    }
    @AfterMethod
    public  void Aftermetod(){
        System.out.println("...is after method");
    }
    @AfterTest
    public void Aftertest() throws InterruptedException {
        System.out.println("..is after test ");
        Thread.sleep(5000);
//        List<String> aa= new ArrayList();
//        aa.add("1");
//        aa.add("2");
//        aa.add("3");
//        aa.add("4");
//        aa.add("5");
//        app.Pj_Hangup_All();
//
//        app.Pj_Unregister_Accounts();

        Thread.sleep(10000);
        app.Pj_Destory();
    }
}
