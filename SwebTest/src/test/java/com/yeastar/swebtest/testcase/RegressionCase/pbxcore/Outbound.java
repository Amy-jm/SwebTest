package com.yeastar.swebtest.testcase.RegressionCase.pbxcore;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.*;
import java.util.ArrayList;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Created by AutoTest on 2017/10/12.
 */
public class Outbound extends SwebDriver{

    @BeforeClass
    public void BeforeClass() throws InterruptedException {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：======  Outbound  ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        ys_waitingMask();
        mySettings.close.click();
        m_extension.showCDRClounm();

 //        被测设备注册分机1000，辅助1：分机3001，辅助2：分机2000
        pjsip.Pj_CreateAccount(1000,"Yeastar202","UDP",1);
        pjsip.Pj_CreateAccount(3001,"Yeastar202","UDP",-1);
        pjsip.Pj_CreateAccount(2000,"Yeastar202","UDP",-1);
        pjsip.Pj_Register_Account(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(3001,DEVICE_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_panel.click();
        outboundRoutes.outboundRoutes.click();
        outboundRoutes.add.should(Condition.exist);
    }

//    测试各种外线呼出
    @Test
    public void A_out1_sip() throws InterruptedException {
        Reporter.infoExec(" 1000拨打13001通过sip外线呼出"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"13001",DEVICE_IP_LAN);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","13001","Answered"," ",SIPTrunk,communication_outRoute);

        //        检查正常录音
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        if(gridPicColor(cdRandRecordings.grid,1,cdRandRecordings.gridPlay).contains(cdRandRecordings.gridColumnColor_Gray)){
            YsAssert.fail(" 录音失败");
        }else {
            Reporter.pass(" 正确检测到录音文件");
        }
        closeCDRRecord();

    }

    @Test
    public void A_out2_iax() throws InterruptedException {
        Reporter.infoExec(" 1000拨打23001通过iax外线呼出"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"23001",DEVICE_IP_LAN);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","23001","Answered"," ",IAXTrunk,communication_outRoute);

    }

    @Test
    public void A_out3_sps() throws InterruptedException {
        Reporter.infoExec(" 1000拨打32000通过sps外线呼出"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"32000",DEVICE_IP_LAN);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","32000","Answered"," ",SPS,communication_outRoute);
    }

    @Test
    public void A_out4_sps() throws InterruptedException {
        Reporter.infoExec(" 1000拨打42000通过spx外线呼出"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"42000",DEVICE_IP_LAN);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","42000","Answered"," ",SPX,communication_outRoute);
    }

    @Test
    public void A_out5_fxo() throws InterruptedException {
        if (!FXO_1.equals("null")) {
            Reporter.infoExec(" 1000拨打52000通过PSTN外线呼出"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "52000", DEVICE_IP_LAN);
            ys_waitingTime(10000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1000 <1000>", "52000", "Answered", " ",FXO_1,  communication_outRoute);
        }
    }

    @Test
    public void A_out6_bri() throws InterruptedException {
        if (!BRI_1.equals("null")) {
            Reporter.infoExec(" 1000拨打62000通过BRI外线呼出"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "62000", DEVICE_IP_LAN);
            ys_waitingTime(10000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1000 <1000>", "62000", "Answered"," ", BRI_1,  communication_outRoute);
        }
    }

    @Test
    public void A_out7_E1() throws InterruptedException {
        if (!E1.equals("null")) {
            Reporter.infoExec(" 1000拨打72000通过E1外线呼出"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "72000", DEVICE_IP_LAN);
            ys_waitingTime(13000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1000 <1000>", "72000", "Answered"," ", E1,  communication_outRoute);
        }
    }

    @Test
    public void A_out8_gsm() throws InterruptedException {
        if (!GSM.equals("null")) {
            Reporter.infoExec(" 1000拨打辅助设备的GSM号码呼出"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(1000, "8"+DEVICE_ASSIST_GSM, DEVICE_IP_LAN);
            ys_waitingTime(15000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1000 <1000>", "8"+DEVICE_ASSIST_GSM, "Answered", " ",GSM,  communication_outRoute);
        }
    }


//  Dial Patterns 测试
    @Test
    public void B_Prepend() throws InterruptedException {
        Reporter.infoExec(" 新建呼出路由DialPattern，Pattern:91.，strip：2，Prepend：123456，选择SIP外线/所有分机"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        outboundRoutes.add.should(Condition.exist);
        ArrayList<String> extenlist =new ArrayList<>();
        extenlist.add("all");
        ArrayList<String> trunklist =new ArrayList<>();
        trunklist.add(SIPTrunk);
        m_callcontrol.addOutboundRoute("Prepend","91.","2","30",extenlist,trunklist);
        ys_apply();

//      Dial Patterns 通话测试
        Reporter.infoExec(" 1000拨打9101通过sip外线呼出");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"9101",DEVICE_IP_LAN);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","9101","Answered"," ",SIPTrunk,communication_outRoute);
    }

//    Password测试
    @Test
    public void C_password() throws InterruptedException {
        Reporter.infoExec(" 新建呼出路由Password，Pattern:92.，strip：2，外线:SPS，分机：1000"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        outboundRoutes.add.should(Condition.exist);
        ArrayList<String> extenlist =new ArrayList<>();
        extenlist.add("1000");
        ArrayList<String> trunklist =new ArrayList<>();
        trunklist.add(SPS);
        m_callcontrol.addOutboundRoute("Password","92.","2","",extenlist,trunklist);
        ys_waitingTime(2000);

        Reporter.infoExec(" 编辑呼出路由Password，Passwoed：Single Pin--123");
        outboundRoutes.add.should(Condition.exist);
        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"Password",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();
        comboboxSelect(add_outbound_routes.Password,add_outbound_routes.Password_Singlepin);
        add_outbound_routes.singlepin_edit.setValue("123");
        add_outbound_routes.save.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        ys_apply();

//       通话测试
        Reporter.infoExec(" 1000拨打928888通过SPS外线呼出");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"928888", DEVICE_IP_LAN,false);
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(1000,"1","2","3","#");
        Thread.sleep(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","928888","Answered"," ",SPS,communication_outRoute);
        m_extension.checkCDR_OtherInfo(cdRandRecordings.gridColumn_PinCode,"123",1);
    }

//    循环抓取
    @Test
    public void D_rrmemory_hunt() throws InterruptedException {
        Reporter.infoExec(" 新建呼出路由Rrmemory_hunt，Pattern:93.，strip：2，外线:SPS、SPX，分机：1000"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        outboundRoutes.add.should(Condition.exist);
        ArrayList<String> extenlist =new ArrayList<>();
        extenlist.add("1000");
        ArrayList<String> trunklist =new ArrayList<>();
        trunklist.add(SPS);
        trunklist.add(SPX);
        m_callcontrol.addOutboundRoute("Rrmemory_hunt","93.","2","",extenlist,trunklist);
        Reporter.infoExec(" 编辑呼出路由Rrmemory_hunt，启用循环抓取");
        outboundRoutes.add.should(Condition.exist);
        setPageShowNum(outboundRoutes.grid,25);
        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"Rrmemory_hunt",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();
        setCheckBox(add_outbound_routes.rrmemoryHunt,true);
        add_outbound_routes.save.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        ys_apply();

//        通话测试循环抓取
        Reporter.infoExec(" 1000拨打933333，第1次预期通过SPS外线呼出");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"933333",DEVICE_IP_LAN,false);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","933333","Answered"," ",SPS,communication_outRoute);

        Reporter.infoExec(" 1000拨打933333，第2次预期通过SPX外线呼出");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"933333",DEVICE_IP_LAN,false);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","933333","Answered"," ",SPX,communication_outRoute);

        Reporter.infoExec(" 1000拨打933333，第3次预期通过SPS外线呼出");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"933333",DEVICE_IP_LAN,false);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","933333","Answered"," ",SPS,communication_outRoute);

    }

//    TimeCondition
    @Test
    public void E_timecondition() throws InterruptedException {
        Reporter.infoExec(" 编辑呼出路由Password：取消密码设置，TimeCondition选择Outbound"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        outboundRoutes.add.should(Condition.exist);
        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"Password",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();
        comboboxSelect(add_outbound_routes.Password,add_outbound_routes.Password_None);
        $$("#st-or-timecondition span").findBy(text("Outbound")).click();;
        add_outbound_routes.save.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        ys_apply();

//        通话验证
        Reporter.infoExec(" 1000拨打929999，通过sps外线呼出，预期：呼出失败");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"9299999",DEVICE_IP_LAN,false);
        ys_waitingTime(10000);
        YsAssert.assertEquals(getExtensionStatus(1000,HUNGUP,20),HUNGUP,"预期1000为HangUp");
        pjsip.Pj_Hangup_All();
//        m_extension.checkCDR("1000 <1000>","9299999","Failed"," "," ",communication_internal);

        Reporter.infoExec(" 编辑呼出路由Password，TimeCondition选择CheckAll"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        outboundRoutes.add.should(Condition.exist);
        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"Password",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();
        $$("#st-or-timecondition span").findBy(text("CheckAll")).click();;
        add_outbound_routes.save.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        ys_apply();

//        通话验证
        Reporter.infoExec(" 1000拨打929999，通过sps外线呼出，预期：呼出成功");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"9299999",DEVICE_IP_LAN,false);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","9299999","Answered"," ",SPS,communication_outRoute);
    }
    
    @Test
    public void F_delete() throws InterruptedException {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_tree.click();
        outboundRoutes.add.should(Condition.exist);
        setPageShowNum(outboundRoutes.grid,25);
        Reporter.infoExec(" 表格删除：Rrmemory_hunt-取消删除"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"Rrmemory_hunt",sort_ascendingOrder)));
        int rows=Integer.parseInt(String.valueOf(gridLineNum(outboundRoutes.grid)));
        gridClick(outboundRoutes.grid,row,outboundRoutes.gridDelete);
        outboundRoutes.delete_no.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        int row1 =Integer.parseInt(String.valueOf(gridLineNum(outboundRoutes.grid)));
        System.out.println("row1:"+row1);
        YsAssert.assertEquals(rows,row1,"表格删除：Rrmemory_hunt-取消删除");

        Reporter.infoExec(" 表格删除：Rrmemory_hunt-确定删除"); //执行操作
        gridClick(outboundRoutes.grid,row,outboundRoutes.gridDelete);
        outboundRoutes.delete_yes.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(outboundRoutes.grid)));
        System.out.println("row2:"+row2);
        int row3=row2+1;
        System.out.println("row3:"+row3);
        YsAssert.assertEquals(row3,row1,"表格删除：Rrmemory_hunt-确定删除");

        Reporter.infoExec(" 删除：Prepend-取消删除"); //执行操作
        int row8 = Integer.parseInt(String.valueOf(gridLineNum(outboundRoutes.grid)));
        int row4 = Integer.parseInt(String.valueOf(gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"Prepend",sort_ascendingOrder)));
        gridCheck(outboundRoutes.grid,row4,outboundRoutes.gridcolumn_Check);
        outboundRoutes.delete.click();
        outboundRoutes.delete_no.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        int row5 = Integer.parseInt(String.valueOf(gridLineNum(outboundRoutes.grid)));
        System.out.println("row5:"+row5);
        YsAssert.assertEquals(row8,row5,"删除：Prepend-取消删除");

        Reporter.infoExec(" 删除：Prepend-确定删除"); //执行操作
        outboundRoutes.delete.click();
        outboundRoutes.delete_yes.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        int row6 = Integer.parseInt(String.valueOf(gridLineNum(outboundRoutes.grid)));
        System.out.println("row6:"+row6);
        int row7=row6+1;
        System.out.println("row7:"+row7);
        YsAssert.assertEquals(row5,row7,"删除：Prepend-确定删除");
        ys_apply();
        
    }

    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
        Reporter.infoAfterClass("执行完毕：======  Outbound  ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);

    }
}
