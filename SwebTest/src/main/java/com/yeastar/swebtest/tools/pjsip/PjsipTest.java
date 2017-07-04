package com.yeastar.swebtest.testcase.LhrTest;

import com.yeastar.swebtest.tools.pjsip.PjsipApp;
import com.yeastar.swebtest.tools.pjsip.PjsipDll;
import com.yeastar.swebtest.tools.pjsip.UserAccount;
import org.apache.xerces.xs.StringList;
import org.junit.After;
import org.junit.Before;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.testng.annotations.*;

import javax.jws.soap.SOAPBinding;
import java.util.*;

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
        for(int i=0; i<accountNum; i++){
            account = new UserAccount();
            account.accId = i;
            account.username = String.valueOf(3004+i);
            accounts.add(account);
        }
        for (int i=0; i<accountNum; i++){
            System.out.println("userName "+ accounts.get(i).username);
            app.Pj_Register_Account(accounts.get(i).username,"192.168.7.151","Yeastar202","5060");
        }

        Thread.sleep(10000);
        app.Pj_Make_Call_No_Answer(3004,3005);
    }

    @Test
    public void makeCall() throws InterruptedException {
        System.out.println("is make call ");
    }
    @AfterMethod
    public  void Aftermetod(){
        System.out.println("...is after method");
    }
    @AfterTest
    public void Aftertest() throws InterruptedException {
        System.out.println("..is after test ");
        Thread.sleep(5000);

        List<String> aa= new ArrayList();
        aa.add("1");
        aa.add("2");
        aa.add("3");
        aa.add("4");
        aa.add("5");
        app.Pj_Send_Dtmf("3001",aa);

        Thread.sleep(10000);
        app.Pj_Hangup_All();

        Thread.sleep(10000);
        app.Pj_Unregister_Accounts();

        Thread.sleep(10000);
        app.Pj_Destory();
    }
}
