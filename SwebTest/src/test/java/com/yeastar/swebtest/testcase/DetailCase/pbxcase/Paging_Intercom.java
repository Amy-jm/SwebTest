package com.yeastar.swebtest.testcase.DetailCase.pbxcase;

import java.util.ArrayList;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import org.testng.annotations.*;

/**
 * Created by AutoTest on 2017/12/29.
 */
public class Paging_Intercom extends SwebDriver {
    @BeforeClass
    public void BeforeClass() {

        Reporter.infoBeforeClass("开始执行：=======  PagingGroup & Intercom  ======="); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes") && Integer.valueOf(VERSION_SPLIT[1]) <= 9){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
    }

    @BeforeClass
    public void Register() {
        Reporter.infoExec(" 注册分机1000、1100、1103、1105、2000"); //执行操作
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,1);
        pjsip.Pj_CreateAccount(1100,EXTENSION_PASSWORD,"UDP",UDP_PORT,2);
        pjsip.Pj_CreateAccount(1101,EXTENSION_PASSWORD,"UDP",UDP_PORT,3);
        pjsip.Pj_CreateAccount(1103,EXTENSION_PASSWORD,"UDP",UDP_PORT,5);
        pjsip.Pj_CreateAccount(1105,EXTENSION_PASSWORD,"UDP",UDP_PORT,7);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_Register_Account(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1100,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1101,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1103,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1105,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
     }

    @Test
    public void A1_InitIntercom() {
         Reporter.infoExec(" 设置对讲的特征码为*5"); //执行操作
         pageDeskTop.taskBar_Main.click();
         pageDeskTop.settingShortcut.click();
         settings.general_panel.click();
         if(!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes")){
             ys_waitingMask();
         }else{
             ys_waitingTime(3000);
         }
         featureCode.featureCode.click();
         featureCode.oneTouchRecord.shouldBe(Condition.exist);
         featureCode.intercom.should(Condition.enabled);
         setCheckBox(featureCode.intercom_check,true);
         featureCode.intercom.clear();
         featureCode.intercom.setValue("*5");
         featureCode.save.click();
         ys_waitingTime(3000);
    }

    @Test
    public void A2_InitPaging() {
        Reporter.infoExec(" 初始化广播组设置：删除所有广播组"); //执行操作
        settings.callFeatures_tree.click();
        paging_intercom.paging_Intercom.click();
        ys_waitingMask();
        deletes(" 删除所有广播组",paging_intercom.grid,paging_intercom.delete,paging_intercom.delete_yes,paging_intercom.grid_Mask);
        ys_waitingTime(2000);
    }

    @Test
    public void B1_add_Paging6300() {
        Reporter.infoExec(" 新建广播组Paging6300--单工、不启用*Answer、成员分机：1100、1105"); //执行操作
        m_callFeature.addPagingIntercom("Paging6300",6300,add_paging_intercom.paging1way,false,1100,1105);
        ys_waitingLoading(paging_intercom.grid_Mask);
    }

    @Test
    public void B2_add_AutoPag6301() {
        Reporter.infoExec(" 新建广播组AutoPag6301--双工、启用*Answer、成员：ExtensionGroup1"); //执行操作
        m_callFeature.addPagingIntercom("AutoPag6301",6301,add_paging_intercom.paging2way,true,"ExtensionGroup1");
        ys_waitingLoading(paging_intercom.grid_Mask);
    }

    @Test
    public void B3_add_NewPage6302() {
        Reporter.infoExec(" 新建广播组NewPage6302--双工、不启用*Answer、成员分机：1100、1103、1105"); //执行操作
        m_callFeature.addPagingIntercom("NewPage6302",6302,add_paging_intercom.paging2way,false,1100,1103,1105);
        ys_waitingLoading(paging_intercom.grid_Mask);
    }

    @Test
    public void B4_add_Paging6303() {
        Reporter.infoExec(" 新建广播组Paging6303--单工、启用*Answer、成员1100、1105"); //执行操作
        m_callFeature.addPagingIntercom("Paging6303",6303,add_paging_intercom.paging1way,true,1100,1105);
        ys_waitingLoading(paging_intercom.grid_Mask);
    }

    @Test
    public void B5_Apply() {
        ys_apply();
    }

//    @Test
    public void B6_backup(){
        backupEnviroment(this.getClass().getName());
    }
//    分机1000启用总是转移到6300
    @Test
    public void C1_callforward() {
        Reporter.infoExec(" 1000拨打*716300将通话总是转移到广播组6300"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1000, "*716300", DEVICE_IP_LAN, false);
        ys_waitingTime(4000);
        pjsip.Pj_Hangup_All();
    }

//    Type：单双工---查看设备是否会打印,d--打印双工
    @Test
    public void D1_1way() {
        Reporter.infoExec(" 1000拨打6300--预期分机1100、1105自动应答，AMI不会打印,d"); //执行操作
        tcpSocket.connectToDevice(20000);
        pjsip.Pj_Make_Call_Auto_Answer(1000,"6300",DEVICE_IP_LAN);
        boolean showKeyWord= tcpSocket.getAsteriskInfo(",d");
        System.out.println("Q_Paging1way TcpSocket return: "+showKeyWord);
        tcpSocket.closeTcpSocket();
        YsAssert.assertEquals(showKeyWord,false,"预期：AMI不会打印,d");
        if(getExtensionStatus(1100,TALKING,10)==TALKING && getExtensionStatus(1105,TALKING,10)==TALKING){
            Reporter.pass(" 1100--Talking");
            Reporter.pass(" 1105--Talking");
        }else{
            Reporter.error(" 1100、1105自动应答失败");
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1105 <6300(1105)>","Answered","","",communication_internal,1,2);
    }

    @Test
    public void D2_2way() {
        Reporter.infoExec(" 1000拨打6301--预期分机1100、1101、1105自动应答，AMI打印,d"); //执行操作
        tcpSocket.connectToDevice(20000);
        pjsip.Pj_Make_Call_Auto_Answer(1000,"6301",DEVICE_IP_LAN);
        boolean showKeyWord= tcpSocket.getAsteriskInfo(",d");
        System.out.println("Q_Paging1way TcpSocket return: "+showKeyWord);
        tcpSocket.closeTcpSocket();
        YsAssert.assertEquals(showKeyWord,true,"预期：AMI会打印,d");
        if(getExtensionStatus(1100,TALKING,10)==TALKING && getExtensionStatus(1101,TALKING,10)==TALKING && getExtensionStatus(1105,TALKING,10)==TALKING){
            Reporter.pass(" 1100--Talking");
            Reporter.pass(" 1101--Talking");
            Reporter.pass(" 1105--Talking");
        }else{
            Reporter.error(" 1100、1101、1105自动应答失败");
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1105 <6301(1105)>","Answered","","",communication_internal,1,2,3);
    }

//    *Answer
    @Test
    public void E1_EnableAnswer() {
        Reporter.infoExec(" 1000拨打6301--预期分机1100、1101、1105自动应答后，1105按*Answer，1100、1101自动被挂断"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"6301",DEVICE_IP_LAN);
        if(getExtensionStatus(1100,TALKING,10)==TALKING && getExtensionStatus(1101,TALKING,10)==TALKING && getExtensionStatus(1105,TALKING,10)==TALKING){
            Reporter.pass(" 1100--Talking");
            Reporter.pass(" 1101--Talking");
            Reporter.pass(" 1105--Talking");

            Reporter.infoExec(" 1105按*Answer--预期1100、1101会挂断通话");
            pjsip.Pj_Send_Dtmf(1105,"*");
            ys_waitingTime(3000);
            if(getExtensionStatus(1100,HUNGUP,10)==HUNGUP &&
                    getExtensionStatus(1101,HUNGUP,10)==HUNGUP &&
                    getExtensionStatus(1105,TALKING,10)==TALKING){
                Reporter.pass(" 1100--Hungup");
                Reporter.pass(" 1101--Hungup");
                Reporter.pass(" 1105--Talking");
            }else {
                Reporter.error(" 1105*answer失败");
            }
        }else{
            Reporter.error(" 1100、1101、1105自动应答失败");
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1105 <6301(1105)>","Answered","","",communication_internal,1,2,3);
    }

    @Test
    public void E2_DisableAnswer() {
        Reporter.infoExec(" 1000拨打6302--预期分机1100、1103、1105自动应答，1105按*，分机1100、1103不会被挂断"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"6302",DEVICE_IP_LAN,false);
        if(getExtensionStatus(1100,TALKING,10)==TALKING && getExtensionStatus(1103,TALKING,10)==TALKING && getExtensionStatus(1105,TALKING,10)==TALKING){
            Reporter.pass(" 1100--Talking");
            Reporter.pass(" 1103--Talking");
            Reporter.pass(" 1105--Talking");

            Reporter.infoExec(" 1105按*Answer--预期1100、1103不会挂断通话");
            pjsip.Pj_Send_Dtmf(1105,"*");
            ys_waitingTime(3000);
            if(getExtensionStatus(1100,HUNGUP,10)==TALKING &&
                    getExtensionStatus(1103,HUNGUP,10)==TALKING &&
                    getExtensionStatus(1105,TALKING,10)==TALKING){
                Reporter.pass(" 1100--Talking");
                Reporter.pass(" 1103--Talking");
                Reporter.pass(" 1105--Talking");
            }else {
                Reporter.infoExec(" 1105按*Answer--预期1100、1103不会挂断通话");
            }
        }else{
            Reporter.error(" 1100、1103、1105自动应答失败");
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1105 <6302(1105)>","Answered","","",communication_internal,1,2,3);
    }

//    外线呼入到分机xx,follow me 到Paging
    @Test
    public void F1_followToPaging() {
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入到分机1000，总是转移到广播组6300，预期：分机1100、1105自动应答"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(2000,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(20000);
        if(getExtensionStatus(1100,TALKING,10)==TALKING && getExtensionStatus(1105,TALKING,10)==TALKING){
            Reporter.pass(" 1100--Talking");
            Reporter.pass(" 1105--Talking");
        }else{
            Reporter.infoExec(" 1100、1105自动应答失败");
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000","1105 <6300(1105)>","Answered","","",communication_internal,1,2);
    }

//    对讲Intercom
    @Test
    public void G1_intercom() {
        Reporter.infoExec(" 1000拨打*51105，1105自动应答"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000, "*51105", DEVICE_IP_LAN);
        ys_waitingTime(8000);
        int status = getExtensionStatus(1105,TALKING,10);
//        YsAssert.assertEquals(status,TALKING,"1105预期：自动应答");
        if(status != TALKING){
            Reporter.error("预期通话状态为"+TALKING+",实际为"+status);
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>", "1105 <*51105>", "Answered", "", "", communication_internal);
    }

//    修改特征码
    @Test
    public void G2_EditFeatureCode() {
        Reporter.infoExec(" 编辑对讲的特征码为*055"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.general_tree.click();
        ys_waitingTime(3000);
        featureCode.featureCode.click();
        featureCode.oneTouchRecord.shouldBe(Condition.exist);
        setCheckBox(featureCode.intercom_check,true);
        featureCode.intercom.clear();
        featureCode.intercom.setValue("*055");
        featureCode.save.click();
        ys_waitingTime(3000);
        ys_apply();
    }

    @Test
    public void G3_newIntercom() {
        Reporter.infoExec(" 1000拨打*0551105，预期1105自动应答"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000, "*0551105", DEVICE_IP_LAN);
        ys_waitingTime(10000);
        YsAssert.assertEquals(getExtensionStatus(1105,TALKING,10),TALKING,"1105预期：自动应答");
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>", "1105 <*0551105>", "Answered", "", "", communication_internal);
    }

//    删除
    @Test
    public void H_delete() {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_tree.click();
        paging_intercom.paging_Intercom.click();
        paging_intercom.add.shouldBe(Condition.exist);
        setPageShowNum(paging_intercom.grid, 25);
        Reporter.infoExec(" 表格删除：Paging6300-取消删除"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(paging_intercom.grid, paging_intercom.gridcolumn_Name, "Paging6300", sort_ascendingOrder)));
        int rows = Integer.parseInt(String.valueOf(gridLineNum(paging_intercom.grid)));
        gridClick(paging_intercom.grid, row, paging_intercom.gridDelete);
        paging_intercom.delete_no.click();
        ys_waitingLoading(paging_intercom.grid_Mask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(paging_intercom.grid)));
        System.out.println("row1:" + row1);
        YsAssert.assertEquals(rows, row1, "表格删除：Paging6300-取消删除");

        Reporter.infoExec(" 表格删除：Paging6300-确定删除"); //执行操作
        gridClick(paging_intercom.grid, row, paging_intercom.gridDelete);
        paging_intercom.delete_yes.click();
        ys_waitingLoading(paging_intercom.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(paging_intercom.grid)));
        System.out.println("row2:" + row2);
        int row3 = row2 + 1;
        System.out.println("row3:" + row3);
        YsAssert.assertEquals(row3, row1, "表格删除：Paging6300-确定删除");

        Reporter.infoExec(" 删除：AutoPag6301-取消删除"); //执行操作
        int row8 = Integer.parseInt(String.valueOf(gridLineNum(paging_intercom.grid)));
        int row4 = Integer.parseInt(String.valueOf(gridFindRowByColumn(paging_intercom.grid, paging_intercom.gridcolumn_Name, "AutoPag6301", sort_ascendingOrder)));
        gridCheck(paging_intercom.grid, row4, paging_intercom.gridcolumn_Check);
        paging_intercom.delete.click();
        paging_intercom.delete_no.click();
        ys_waitingLoading(paging_intercom.grid_Mask);
        int row5 = Integer.parseInt(String.valueOf(gridLineNum(paging_intercom.grid)));
        System.out.println("row5:" + row5);
        YsAssert.assertEquals(row8, row5, "删除：AutoPag6301-取消删除");

        Reporter.infoExec(" 删除：AutoPag6301-确定删除"); //执行操作
        paging_intercom.delete.click();
        paging_intercom.delete_yes.click();
        ys_waitingLoading(paging_intercom.grid_Mask);
        int row6 = Integer.parseInt(String.valueOf(gridLineNum(paging_intercom.grid)));
        System.out.println("row6:" + row6);
        int row7 = row6 + 1;
        System.out.println("row7:" + row7);
        YsAssert.assertEquals(row5, row7, "删除：AutoPag6301-确定删除");
        ys_apply();
    }

//    恢复设置
    @Test
    public void I_Recovery() {
        Reporter.infoExec(" 1000拨打*071将通话总是转移到广播组6300"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(1000, "*071", DEVICE_IP_LAN, false);
        ys_waitingTime(4000);
        pjsip.Pj_Hangup_All();
    }

    @AfterMethod
    public void AfterMethod(){
        if(cdRandRecordings.deleteCDR.isDisplayed()){
            closeCDRRecord();
        }
    }

    @AfterClass
    public void AfterClass() {
        Reporter.infoAfterClass("执行完毕：=======  PagingGroup & Intercom  ======="); //执行操作
        quitDriver();
        pjsip.Pj_Destory();

        ys_waitingTime(10000);
        killChromePid();
    }

}
