package com.yeastar.swebtest.testcase.RegressionCase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.*;
import java.util.ArrayList;


/**

/**
 * Created by AutoTest on 2017/10/9.
 */
public class Inbound extends SwebDriver {
    @BeforeClass
    public void BeforeClass() throws InterruptedException {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：======  Inbound  ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        System.out.println("after. initialDriver.");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        System.out.println("after. login(LOGIN_USERNAME,LOGIN_PASSWORD)..");
        if(!PRODUCT.equals(CLOUD_PBX)){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();

//        被测设备注册分机1000，辅助1：分机3001，辅助2：分机2001
        pjsip.Pj_CreateAccount(1000,"Yeastar202","UDP",UDP_PORT,1);
        pjsip.Pj_CreateAccount(1101,"Yeastar202","UDP",UDP_PORT,3);
        pjsip.Pj_CreateAccount(1105,"Yeastar202","UDP",UDP_PORT,7);
        pjsip.Pj_CreateAccount(3001,"Yeastar202","UDP",UDP_PORT_ASSIST_1,-1);
        pjsip.Pj_CreateAccount(2001,"Yeastar202","UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_CreateAccount(2002,"Yeastar202","UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_Register_Account(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1101,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1105,DEVICE_IP_LAN);
        System.out.println("after. createAccount..");
        pjsip.Pj_Register_Account_WithoutAssist(3001,DEVICE_ASSIST_1,UDP_PORT_ASSIST_1);
        System.out.println("after. Pj_Register_Account  1000..");
        pjsip.Pj_Register_Account_WithoutAssist(2001,DEVICE_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2002,DEVICE_ASSIST_2);
    }

//    验证各种外线都能正常呼入到分机1000
    @Test
    public void A_callfrom1_sip() throws InterruptedException {
//        ys_waitingTime(10000);
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到分机1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1);
        ys_waitingTime(10000);
        pjsip.Pj_hangupCall(3001,1000);
        System.out.println("SIP--通话完成");
        m_extension.checkCDR("3001 <3001>","1000 <1000>","Answered",SIPTrunk," ",communication_inbound);

//        检查正常录音
        if(!NETWORK_DEVICE_NAME.equals("null")) {
            if(!PRODUCT.equals(CLOUD_PBX)){
                pageDeskTop.taskBar_Main.click();
                pageDeskTop.CDRandRecordShortcut.click();
                cdRandRecordings.search.click();
                ys_waitingLoading(cdRandRecordings.grid_Mask);
                if (gridPicColor(cdRandRecordings.grid, 1, cdRandRecordings.gridPlay).contains(cdRandRecordings.gridColumnColor_Gray)) {
                    YsAssert.fail(" 录音失败");
                } else {
                    Reporter.pass(" 正确检测到录音文件");
                }
                closeCDRRecord();
            }
        }
    }

    @Test
    public void A_callfrom2_iax() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        Reporter.infoExec(" 3001拨打3100通过iax外线呼入到分机1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3100",DEVICE_ASSIST_1);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1000 <1000>","Answered",IAXTrunk," ",communication_inbound);

    }

    @Test
    public void A_callfrom3_sps() throws InterruptedException {
        Reporter.infoExec(" 2001拨打99999通过sps外线呼入到分机1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1000 <1000>","Answered",SPS," ",communication_inbound);
    }

    @Test
    public void A_callfrom4_spx() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        Reporter.infoExec(" 2001拨打88888通过spx外线呼入到分机1000"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"88888",DEVICE_ASSIST_2);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1000 <1000>","Answered",SPX," ",communication_inbound);
    }

    @Test
    public void A_callfrom5_fxo() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX) ||PRODUCT.equals(PC) ){
            return;
        }
        if(!FXO_1.equals("null")){
            Reporter.infoExec(" 2001拨打2010通过fxo外线呼入到分机1000"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(2001,"2010",DEVICE_ASSIST_2);
            ys_waitingTime(10000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2001 <2001>","1000 <1000>","Answered",FXO_1," ",communication_inbound);
        }
    }

    @Test
    public void A_callfrom6_bri() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX) ||PRODUCT.equals(PC) ){
            return;
        }
        if(!BRI_1.equals("null")){
            Reporter.infoExec(" 2001拨打66666通过bri外线呼入到分机1000"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(2001,"66666",DEVICE_ASSIST_2);
            ys_waitingTime(10000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2001 <2001>","1000 <1000>","Answered",BRI_1," ",communication_inbound);

        }
    }

    @Test
    public void A_callfrom7_e1() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX) ||PRODUCT.equals(PC) ){
            return;
        }
        if(!E1.equals("null")){
            Reporter.infoExec(" 2001拨打77777通过E1外线呼入到分机1000"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(2001,"77777",DEVICE_ASSIST_2);
            ys_waitingTime(10000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("2001 <2001>","1000 <1000>","Answered",E1," ",communication_inbound);
        }
    }

    @Test
    public void A_callfrom8_gsm() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX) ||PRODUCT.equals(PC) ){
            return;
        }
        if(!GSM.equals("null")){
            Reporter.infoExec(" 2001拨打被测设备的gsm号码呼入到分机1000"); //执行操作
            pjsip.Pj_Make_Call_Auto_Answer(2001,DEVICE_TEST_GSM,DEVICE_ASSIST_2);
            ys_waitingTime(15000);
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR(DEVICE_ASSIST_GSM +" <"+DEVICE_ASSIST_GSM+">","1000 <1000>","Answered",GSM," ",communication_inbound);
        }
    }

//    Caller ID测试
    @Test
    public void B1_callerid() throws InterruptedException {
        Reporter.infoExec(" 编辑呼入路由InRoute1，Caller ID：2002"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_panel.click();
        inboundRoutes.inboundRoutes.click();
        ys_waitingLoading(inboundRoutes.grid_Mask);
        inboundRoutes.add.shouldBe(Condition.exist);
        ys_waitingTime(5000);
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        add_inbound_route.callIDPattem.setValue("2002");
//        please wait 刚加载完就点击save,页面容易卡在Please wait页面，一姐暂时没有好的优化方法，先等待2秒再点击save
        ys_waitingTime(2000);
        add_inbound_route.save.click();
        if(PRODUCT.equals(CLOUD_PBX)){
            ys_waitingMask();
        }
        ys_waitingTime(5000);
        ys_apply();
    }

    @Test
    public void B2_callerid() throws InterruptedException {
        ys_waitingTime(5000);
//        Caller ID通话测试
        Reporter.infoExec(" 2002拨打99999通过sps外线呼入到分机1000");
        pjsip.Pj_Make_Call_Auto_Answer(2002,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(12000);
//        YsAssert.assertEquals(getExtensionStatus(1000,TALKING,10),TALKING,"预期1000为Talking");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2002 <2002>","1000 <1000>","Answered",SPS," ",communication_inbound);

        Reporter.infoExec(" 2001拨打99999通过sps外线呼入--预期：无法呼入，CDR不会生成");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2002 <2002>","1000 <1000>","Answered",SPS," ",communication_inbound);

    }

    //    DID Pattern：SPS外线-DID：5503301-5503305，Extension Range：1100-1105

    @Test
    public void C1_did1_sps() throws InterruptedException {
        Reporter.infoExec(" 新建呼入路由DIDtest，DID Pattern：5503301-5503305，选择SPS外线，Extension Range：1101-1105"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
//        settings.callControl_panel.click();
//        inboundRoutes.inboundRoutes.click();
//        ys_waitingLoading(inboundRoutes.grid_Mask);
        ArrayList<String> arraytrunk1 = new ArrayList<>();
        arraytrunk1.add(SPS);
        m_callcontrol.addInboundRoutes("DIDtest","5503301-5503305","",add_inbound_route.s_extension_range,"1101-1105",arraytrunk1);
        ys_apply();

    }

    //      DID 通话测试
    @Test
    public void C2_did1_sps() throws InterruptedException {
//        注意CDR的正确与否！！！
        Reporter.infoExec(" 2001拨打995503301通过sps外线呼入到分机1101"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"995503301",DEVICE_ASSIST_2);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1101 <1101>","Answered",SPS," ",communication_inbound);

        Reporter.infoExec(" 2001拨打995503305通过sps外线呼入到分机1105"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2001,"995503305",DEVICE_ASSIST_2);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","1105 <1105>","Answered",SPS," ",communication_inbound);

    }


    //    TimeCondition测试
    @Test
    public void F_timecondition1() throws InterruptedException {
        Reporter.infoExec(" 设置分机1000具有拨打时间特征码的权限"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.general_tree.click();
        m_general.setExtensionPermission(true,"*8","1000");
}

    @Test
    public void F_timecondition2() throws InterruptedException {
        Reporter.infoExec(" 编辑InRoute1,启用时间条件，workday_24hour到分机1101"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_tree.click();
        inboundRoutes.add.should(Condition.exist);
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        ys_waitingTime(3000);
        add_inbound_route.callIDPattem.clear();
        setCheckBox(add_inbound_route.enableTimeCondition,true);
        ys_waitingTime(1000);
        add_inbound_route.addTimeCondition.click();
        add_inbound_route.SetTimeConditionTableviewDestition(1,1,"workday_24hour");
        add_inbound_route.SetTimeConditionTableviewDestination(1,2,add_inbound_route.s_extensin);
        add_inbound_route.SetTimeConditionTableviewDestitionEx(1,3,"1101");
        add_inbound_route.save.click();
        ys_apply();

//      workday通话验证
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入1101");
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1101 <1101>","Answered",SIPTrunk," ",communication_inbound);
    }

    @Test
    public void F_timecondition3() throws InterruptedException {
        Reporter.infoExec(" 编辑InRoute1，添加[Holiday]到会议室Conference1");
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_tree.click();
        inboundRoutes.add.should(Condition.exist);
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        ys_waitingTime(1000);
        add_inbound_route.addTimeCondition.click();
        ys_waitingTime(1000);
        add_inbound_route.SetTimeConditionTableviewDestition(2,1,"[Holiday]");
        add_inbound_route.SetTimeConditionTableviewDestination(2,2,add_inbound_route.s_conference);
        add_inbound_route.SetTimeConditionTableviewDestition(2,3,"Conference1");
        add_inbound_route.save.click();
        ys_apply();
        ys_waitingTime(5000);

//        Holiday通话验证
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入到会议室6400");
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","6400","Answered",SIPTrunk," ",communication_inbound);
    }

    @Test
    public void F_timecondition4() throws InterruptedException {
        Reporter.infoExec(" 分机1000拨打特征码*802强制启用工作时间"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"*802",DEVICE_IP_LAN);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();

//        建立通话验证特征码生效
        Reporter.infoExec(" 建立通话验证特征码启用生效：3001拨打3000通过sip外线呼入，预期呼入到1101");
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1101 <1101>","Answered",SIPTrunk," ",communication_inbound);
    }

    @Test
    public void F_timecondition5() throws InterruptedException {
        Reporter.infoExec(" 编辑InRoute1禁用时间条件"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_tree.click();
        inboundRoutes.add.should(Condition.exist);
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        setCheckBox(add_inbound_route.enableTimeCondition,false);
        add_inbound_route.save.click();
        ys_apply();

//        通话验证时间条件禁用成功
        Reporter.infoExec(" 建立通话验证禁用时间条件：3001拨打3000通过sip外线呼入，预期呼入到1000");
        pjsip.Pj_Make_Call_Auto_Answer(3001,"3000",DEVICE_ASSIST_1);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("3001 <3001>","1000 <1000>","Answered",SIPTrunk," ",communication_inbound);

    }

//    删除操作
    @Test
    public void G_delete1() throws InterruptedException {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_tree.click();
        inboundRoutes.add.should(Condition.exist);
        Reporter.infoExec(" 表格删除：DIDtest-取消删除"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"DIDtest",sort_ascendingOrder)));
        int rows=Integer.parseInt(String.valueOf(gridLineNum(inboundRoutes.grid)));
        gridClick(inboundRoutes.grid,row,inboundRoutes.gridDelete);
        inboundRoutes.delete_no.click();
        ys_waitingLoading(inboundRoutes.grid_Mask);
        int row1 =Integer.parseInt(String.valueOf(gridLineNum(inboundRoutes.grid)));
        System.out.println("row1:"+row1);
        YsAssert.assertEquals(rows,row1,"表格删除：DIDtest-取消删除");

        Reporter.infoExec(" 表格删除：DIDtest-确定删除"); //执行操作
        gridClick(inboundRoutes.grid,row,inboundRoutes.gridDelete);
        inboundRoutes.delete_yes.click();
        ys_waitingLoading(inboundRoutes.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(inboundRoutes.grid)));
        System.out.println("row2:"+row2);
        int row3=row2+1;
        System.out.println("row3:"+row3);
        YsAssert.assertEquals(row3,row1,"表格删除：DIDtest-确定删除");

//        ArrayList<String> arraytrunk1 = new ArrayList<>();
//        arraytrunk1.add("all");
//        m_callcontrol.addInboundRoutes("DeleteTest","","","","",arraytrunk1);
        Reporter.infoExec(" 删除：InRoute1-取消删除"); //执行操作
        int row8 = Integer.parseInt(String.valueOf(gridLineNum(inboundRoutes.grid)));
        int row4 = Integer.parseInt(String.valueOf(gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1",sort_ascendingOrder)));
        gridCheck(inboundRoutes.grid,row4,inboundRoutes.gridcolumn_Check);
        inboundRoutes.delete.click();
        inboundRoutes.delete_no.click();
        ys_waitingLoading(inboundRoutes.grid_Mask);
        int row5 = Integer.parseInt(String.valueOf(gridLineNum(inboundRoutes.grid)));
        System.out.println("row5:"+row5);
        YsAssert.assertEquals(row8,row5,"删除：InRoute1-取消删除");

        Reporter.infoExec(" 删除：InRoute1-确定删除"); //执行操作
        inboundRoutes.delete.click();
        inboundRoutes.delete_yes.click();
        ys_waitingLoading(inboundRoutes.grid_Mask);
        int row6 = Integer.parseInt(String.valueOf(gridLineNum(inboundRoutes.grid)));
        System.out.println("row6:"+row6);
        int row7=row6+1;
        System.out.println("row7:"+row7);
        YsAssert.assertEquals(row5,row7,"删除：InRoute1-确定删除");
    }

    @Test
    public void H_recovery() throws InterruptedException {
        deletes(" 删除所有呼入路由",inboundRoutes.grid,inboundRoutes.delete,inboundRoutes.delete_yes,inboundRoutes.grid_Mask);
        Reporter.infoExec(" 添加呼入路由InRoute1"); //执行操作
        ArrayList<String> arraytrunk1 = new ArrayList<>();
        arraytrunk1.add("all");
        m_callcontrol.addInboundRoutes("InRoute1","","",add_inbound_route.s_extensin,"1000",arraytrunk1);
        ys_waitingTime(2000);
        ys_apply();
    }

    @AfterMethod
    public void AfterMethod(){
        if(cdRandRecordings.deleteCDR.isDisplayed()){
            closeCDRRecord();
        }
    }

    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
        Reporter.infoAfterClass("执行完毕：======  Inbound  ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);

    }
}
