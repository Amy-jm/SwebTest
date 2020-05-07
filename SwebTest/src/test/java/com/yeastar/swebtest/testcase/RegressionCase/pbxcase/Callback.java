package com.yeastar.swebtest.testcase.RegressionCase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.*;

/**
 * Created by AutoTest on 2017/10/24.
 */
public class Callback extends SwebDriver {
    @BeforeClass
    public void BeforeClass() {

        Reporter.infoBeforeClass("开始执行：======  Callback  ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && !PRODUCT.equals(PC) && Integer.valueOf(VERSION_SPLIT[1]) <= 9){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
    }
    
    @Test(priority =0)
    public void A0_Register() throws InterruptedException {
        pjsip.Pj_Init();
        Reporter.infoExec(" 被测设备注册分机1000，辅助1：分机3001，辅助2：分机2000"); //执行操作
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,1);
        pjsip.Pj_CreateAccount(3001,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_1,-1);
        pjsip.Pj_CreateAccount(2000,EXTENSION_PASSWORD,"UDP",UDP_PORT_ASSIST_2,-1);
        pjsip.Pj_Register_Account(1000,DEVICE_IP_LAN);
        pjsip.Pj_Register_Account_WithoutAssist(3001,DEVICE_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(2000,DEVICE_ASSIST_2);
    }

    @Test(priority =1)
    public void A1_add_callback() throws InterruptedException {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        if(!PRODUCT.equals(CLOUD_PBX)){
            ys_waitingMask();
        }else{
            ys_waitingTime(3000);
        }
        callFeatures.more.click();
        callback.callback.click();
        callback.add.shouldBe(Condition.exist);
        deletes(" 删除所有Callback",callback.grid,callback.delete,callback.delete_yes,callback.grid_Mask);

        Reporter.infoExec(" 创建Callback1，Destination：分机1000，其它默认");
        m_callFeature.addCallBack("Callback1",s_extensin,"1000");
        Reporter.infoExec(" 创建Callback2，CallbackThrough："+SPS+"，DelayBeforeCallback：10，Strip：1，Prepend：311，Destination：IVR1（6500）");
        callback.add.click();
        ys_waitingMask();
        ys_waitingTime(2000);
        add_callback.name.setValue("Callback2");
        comboboxSet(add_callback.callbackThrough,trunkList,SPS);
        add_callback.delayBeforeCallback.setValue("10");
        add_callback.strip.setValue("1");
        add_callback.prepend.setValue("311");
        comboboxSelect(add_callback.destination,s_ivr);
        comboboxSet(add_callback.destinationDest,"name","IVR1");
        add_callback.save.click();
        ys_waitingTime(3000);
    }

    @Test(priority =2)
    public void B_editInbound1() throws InterruptedException {
        Reporter.infoExec(" 编辑呼入路由Inbound1到Callback1"); //执行操作
        settings.callControl_tree.click();
        inboundRoutes.inboundRoutes.shouldBe(Condition.exist);
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        comboboxSelect(add_inbound_route.destinationType,s_callback);
        comboboxSet(add_inbound_route.destination,"name","Callback1");
        add_inbound_route.save.click();
        ys_waitingTime(1000);
        ys_apply();
    }

    @Test(priority =3)
    public void C_calldefault1_sip() throws InterruptedException {
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入，等待2秒挂断通话"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1);
        ys_waitingTime(2000);
        pjsip.Pj_Hangup_All();
        ysAssertWithHangup(getExtensionStatus(3001,RING,20),RING,"预期3001会响铃");
        pjsip.Pj_Answer_Call(3001,false);
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("callback3001","1000 <1000>","Answered",SIPTrunk,"",communication_callback);
    }

    @Test(priority =4)
    public void C_calldefault2_sps() throws InterruptedException {
        Reporter.infoExec(" 2000拨打99999通过sps外线呼入，等待2秒挂断通话"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(2000,"99999",DEVICE_ASSIST_2);
        ys_waitingTime(2000);
        pjsip.Pj_Hangup_All();
        ysAssertWithHangup(getExtensionStatus(2000,RING,20),RING,"预期2000会响铃");
        pjsip.Pj_Answer_Call(2000,false);
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000会响铃");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("callback2000","1000 <1000>","Answered",SPS,"",communication_callback);
    }



    @Test(priority =5)
    public void D_editInbound1() throws InterruptedException {
        Reporter.infoExec(" 编辑呼入路由Inbound1到Callback2"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_tree.click();
        inboundRoutes.inboundRoutes.shouldBe(Condition.exist);
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        comboboxSelect(add_inbound_route.destinationType,s_callback);
        comboboxSet(add_inbound_route.destination,"name","Callback2");
        add_inbound_route.save.click();
        ys_waitingTime(1000);
        ys_apply();
    }

    @Test(priority =6)
    public void E_callbackThroughSPS() throws InterruptedException {
        Reporter.infoExec(" 3001拨打3000通过sip外线呼入,等待2秒挂断通话--预期通过sps外线回拨到分机2000"); //执行操作
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        ys_waitingTime(2000);
        pjsip.Pj_Hangup_All();
        ysAssertWithHangup(getExtensionStatus(2000,RING,20),RING,"预期辅助2的2000分机响铃");
        pjsip.Pj_Answer_Call(2000,false);
        ys_waitingTime(3000);
        Reporter.infoExec(" 回拨到IVR1，按1到分机1000"); //执行操作
        pjsip.Pj_Send_Dtmf(2000,"1");
        ysAssertWithHangup(getExtensionStatus(1000,RING,20),RING,"预期1000分机响铃");
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(5000);
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("callback311001","1000 <6500(1000)>","Answered",SPS,"",communication_callback);
    }
    
//    删除
    @Test(priority =7)
    public void F_delete() throws InterruptedException {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_tree.click();
        callback.callback.click();
        callback.add.shouldBe(Condition.exist);
        Reporter.infoExec(" 表格删除：Callback1-取消删除"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(callback.grid,callback.gridcolumn_Name,"Callback1",sort_ascendingOrder)));
        int rows=Integer.parseInt(String.valueOf(gridLineNum(callback.grid)));
        gridClick(callback.grid,row,callback.gridDelete);
        callback.delete_no.click();
        ys_waitingLoading(callback.grid_Mask);
        int row1 =Integer.parseInt(String.valueOf(gridLineNum(callback.grid)));
        System.out.println("row1:"+row1);
        YsAssert.assertEquals(rows,row1,"表格删除：Callback1-取消删除");

        Reporter.infoExec(" 表格删除：Callback1-确定删除"); //执行操作
        gridClick(callback.grid,row,callback.gridDelete);
        callback.delete_yes.click();
        ys_waitingLoading(callback.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(callback.grid)));
        System.out.println("row2:"+row2);
        int row3=row2+1;
        System.out.println("row3:"+row3);
        YsAssert.assertEquals(row3,row1,"表格删除：Callback1-确定删除");

        Reporter.infoExec(" 删除：Callback2-取消删除"); //执行操作
        int row8 = Integer.parseInt(String.valueOf(gridLineNum(callback.grid)));
        int row4 = Integer.parseInt(String.valueOf(gridFindRowByColumn(callback.grid,callback.gridcolumn_Name,"Callback2",sort_ascendingOrder)));
        gridCheck(callback.grid,row4,callback.gridcolumn_Check);
        callback.delete.click();
        callback.delete_no.click();
        ys_waitingLoading(callback.grid_Mask);
        int row5 = Integer.parseInt(String.valueOf(gridLineNum(callback.grid)));
        System.out.println("row5:"+row5);
        YsAssert.assertEquals(row8,row5,"删除：Callback2-取消删除");

        Reporter.infoExec(" 删除：Callback2-确定删除"); //执行操作
        callback.delete.click();
        callback.delete_yes.click();
        ys_waitingLoading(callback.grid_Mask);
        int row6 = Integer.parseInt(String.valueOf(gridLineNum(callback.grid)));
        System.out.println("row6:"+row6);
        int row7=row6+1;
        System.out.println("row7:"+row7);
        YsAssert.assertEquals(row5,row7,"删除：Callback2-确定删除");
    }
    
    @Test(priority =8)
    public void G_Recovery() throws InterruptedException {
        Reporter.infoExec(" 恢复呼入路由InRoute1到分机1000");
        settings.callControl_tree.click();
        inboundRoutes.inboundRoutes.click();
        inboundRoutes.add.shouldBe(Condition.exist);
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        comboboxSelect(add_inbound_route.destinationType,s_extensin);
        comboboxSet(add_inbound_route.destination,extensionList,"1000");
        add_inbound_route.save.click();
        ys_waitingTime(1000);
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
        Reporter.infoAfterClass("执行完毕：======  Callback  ======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        ys_waitingTime(10000);
        killChromePid();

    }
}
