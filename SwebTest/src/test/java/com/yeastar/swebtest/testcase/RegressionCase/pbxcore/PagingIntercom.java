package com.yeastar.swebtest.testcase.RegressionCase.pbxcore;

import com.yeastar.swebtest.driver.SwebDriver;
import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.*;

/**
 * Created by AutoTest on 2017/10/17.
 * 广播组 & 对讲
 * ONISPAGING=6300 不启用*Answer
 * ISPAGING=6300 启用*Answer
 */
public class PagingIntercom extends SwebDriver{
    @BeforeClass
    public void BeforeClass() throws InterruptedException {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：======  PagingIntercom  ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);

        if(!PRODUCT.equals(CLOUD_PBX)){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.general_panel.click();
        if(!PRODUCT.equals(CLOUD_PBX)){
            ys_waitingMask();
        }
        m_general.setIntercom(true,"*5");

        //        被测设备注册分机1000，1100,1101,1105
        pjsip.Pj_CreateAccount(1000,"Yeastar202","UDP",UDP_PORT,1);
        pjsip.Pj_CreateAccount(1100,"Yeastar202","UDP",UDP_PORT,2);
        pjsip.Pj_CreateAccount(1101,"Yeastar202","UDP",UDP_PORT,3);
        pjsip.Pj_CreateAccount(1105,"Yeastar202","UDP",UDP_PORT,7);
        pjsip.Pj_Register_Account(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1100,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1101,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account(1105,DEVICE_IP_LAN);

    }

//    新建广播组Paging6300：单工，不启用*Answer，成员：1100,1105
    @Test
    public void A_add_paging1() throws InterruptedException {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_tree.click();
        paging_intercom.paging_Intercom.click();
        ys_waitingMask();
        deletes(" 删除所有广播组",paging_intercom.grid,paging_intercom.delete,paging_intercom.delete_yes,paging_intercom.grid_Mask);
        Reporter.infoExec(" 新建广播组Paging6300：单工，不启用*Answer，成员：1100,1105"); //执行操作
        m_callFeature.addPagingIntercom("Paging6300",6300,add_paging_intercom.paging1way,false,1100,1105);
        ys_waitingLoading(paging_intercom.grid_Mask);
        ys_apply();

//       通话测试
        Reporter.infoExec(" 1000拨打6300，预期分机1100、1105自动应答");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"6300",DEVICE_IP_LAN);
       if(getExtensionStatus(1100,TALKING,10)==TALKING && getExtensionStatus(1105,TALKING,10)==TALKING){
           Reporter.pass(" 1100--Talking");
           Reporter.pass(" 1105--Talking");
       }else{
           Reporter.error(" 1100、1105自动应答失败");
       }
       pjsip.Pj_Hangup_All();

//        pageDeskTop.taskBar_Main.click();
//        pageDeskTop.CDRandRecordShortcut.click();
//        cdRandRecordings.search.click();
//        ys_waitingLoading(cdRandRecordings.grid_Mask);
//        String info1 = String.valueOf(gridContent(extensions.grid_CDR,1,cdRandRecordings.gridColumn_CallTo)).trim();
//        String info2 = String.valueOf(gridContent(extensions.grid_CDR,2,cdRandRecordings.gridColumn_CallTo)).trim();
//       if(info1.equals("1100 <6300(1100)>") || info2.equals("1100 <6300(1100)>")){
//           Reporter.pass(" cdr正确");
//       }else{
//           YsAssert.fail(" cdr错误");
//       }
//       closeCDRRecord();
        m_extension.checkCDR("1000 <1000>","1105 <6300(1105)>","Answered","","",communication_internal,1,2);

    }

//    新建广播组Paging6301:双工，启用*Answer，成员：ExtensionGroup1
    @Test
    public void A_add_paging2() throws InterruptedException {
        Reporter.infoExec(" 新建广播组Paging6301：双工，启用*Answer成员：ExtensionGroup1"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        paging_intercom.paging_Intercom.click();
        m_callFeature.addPagingIntercom("Paging6301",6301,add_paging_intercom.paging2way,true,"ExtensionGroup1");
        ys_waitingLoading(paging_intercom.grid_Mask);
        ys_apply();

//        通话测试
        Reporter.infoExec(" 1000拨打6301，预期分机1100、1101、1105自动应答");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"6301",DEVICE_IP_LAN);
        if(getExtensionStatus(1100,TALKING,10)==TALKING &&
                getExtensionStatus(1101,TALKING,10)==TALKING &&
                getExtensionStatus(1105,TALKING,10)==TALKING){
            Reporter.pass(" 1100--Talking");
            Reporter.pass(" 1101--Talking");
            Reporter.pass(" 1105--Talking");

//            分机1101按*Answer
            Reporter.infoExec(" 1101按*Answer--预期1100、1105会挂断通话");
            pjsip.Pj_Send_Dtmf(1101,"*");
            ys_waitingTime(3000);
            if(getExtensionStatus(1100,HUNGUP,10)==HUNGUP &&
                    getExtensionStatus(1105,HUNGUP,10)==HUNGUP &&
                    getExtensionStatus(1101,TALKING,10)==TALKING){
                Reporter.pass(" 1100--Hungup");
                Reporter.pass(" 1101--Talking");
                Reporter.pass(" 1105--Hungup");
            }else {
                Reporter.error(" 1101*answer失败");
            }
        }else {
            Reporter.error(" 1100、1101、1105自动应答失败");
        }
        pjsip.Pj_Hangup_All();

//        pageDeskTop.taskBar_Main.click();
//        pageDeskTop.CDRandRecordShortcut.click();
//        cdRandRecordings.search.click();
//        ys_waitingLoading(cdRandRecordings.grid_Mask);
//        String info1 = String.valueOf(gridContent(extensions.grid_CDR,1,cdRandRecordings.gridColumn_CallTo)).trim();
//        String info2 = String.valueOf(gridContent(extensions.grid_CDR,2,cdRandRecordings.gridColumn_CallTo)).trim();
//        String info3 = String.valueOf(gridContent(extensions.grid_CDR,3,cdRandRecordings.gridColumn_CallTo)).trim();
//        if(info1.equals("1101 <6301(1101)>") || info2.equals("1101 <6301(1101)>") || info3.equals("1101 <6301(1101)>")){
//            Reporter.pass(" cdr正确");
//        }else{
//            YsAssert.fail(" cdr错误");
//        }
//        closeCDRRecord();

        m_extension.checkCDR("1000 <1000>","1101 <6301(1101)>","Answered","","",communication_internal,1,2,3);
    }

//    对讲
    @Test
    public void B_intercom() throws InterruptedException {
        Reporter.infoExec(" 1000拨打*51105"); //执行操作
        pjsip.Pj_Make_Call_Auto_Answer(1000,"*51105",DEVICE_IP_LAN);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","1105 <*51105>","Answered","","",communication_internal);
    }

//    删除
    @Test
    public void C_delete() throws InterruptedException {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        paging_intercom.paging_Intercom.click();
       paging_intercom.add.shouldBe(Condition.exist);
        setPageShowNum(paging_intercom.grid,25);
        Reporter.infoExec(" 表格删除：Paging6300-取消删除"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(paging_intercom.grid,paging_intercom.gridcolumn_Name,"Paging6300",sort_ascendingOrder)));
        int rows=Integer.parseInt(String.valueOf(gridLineNum(paging_intercom.grid)));
        gridClick(paging_intercom.grid,row,paging_intercom.gridDelete);
        paging_intercom.delete_no.click();
        ys_waitingLoading(paging_intercom.grid_Mask);
        int row1 =Integer.parseInt(String.valueOf(gridLineNum(paging_intercom.grid)));
        System.out.println("row1:"+row1);
        YsAssert.assertEquals(rows,row1,"表格删除：Paging6300-取消删除");

        Reporter.infoExec(" 表格删除：Paging6300-确定删除"); //执行操作
        gridClick(paging_intercom.grid,row,paging_intercom.gridDelete);
        paging_intercom.delete_yes.click();
        ys_waitingLoading(paging_intercom.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(paging_intercom.grid)));
        System.out.println("row2:"+row2);
        int row3=row2+1;
        System.out.println("row3:"+row3);
        YsAssert.assertEquals(row3,row1,"表格删除：Paging6300-确定删除");

        Reporter.infoExec(" 删除：Paging6301-取消删除"); //执行操作
        int row8 = Integer.parseInt(String.valueOf(gridLineNum(paging_intercom.grid)));
        int row4 = Integer.parseInt(String.valueOf(gridFindRowByColumn(paging_intercom.grid,paging_intercom.gridcolumn_Name,"Paging6301",sort_ascendingOrder)));
        gridCheck(paging_intercom.grid,row4,paging_intercom.gridcolumn_Check);
        paging_intercom.delete.click();
        paging_intercom.delete_no.click();
        ys_waitingLoading(paging_intercom.grid_Mask);
        int row5 = Integer.parseInt(String.valueOf(gridLineNum(paging_intercom.grid)));
        System.out.println("row5:"+row5);
        YsAssert.assertEquals(row8,row5,"删除：Paging6301-取消删除");

        Reporter.infoExec(" 删除：Paging6301-确定删除"); //执行操作
        paging_intercom.delete.click();
        paging_intercom.delete_yes.click();
        ys_waitingLoading(paging_intercom.grid_Mask);
        int row6 = Integer.parseInt(String.valueOf(gridLineNum(paging_intercom.grid)));
        System.out.println("row6:"+row6);
        int row7=row6+1;
        System.out.println("row7:"+row7);
        YsAssert.assertEquals(row5,row7,"删除：Paging6301-确定删除");
        ys_apply();

    }

    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(5000);
        Reporter.infoAfterClass("执行完毕：======  PagingIntercom  ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(5000);

    }
}
