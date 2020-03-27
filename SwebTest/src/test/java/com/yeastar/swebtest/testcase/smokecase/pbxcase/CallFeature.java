package com.yeastar.swebtest.testcase.smokecase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.yeastar.swebtest.driver.Config.*;
import static com.yeastar.swebtest.driver.SwebDriver.*;

/**
 * Created by Yeastar on 2017/7/17.
 */
public class CallFeature extends SwebDriver {
    @BeforeClass
    public void BeforeClass() {
        Reporter.infoBeforeClass("打开游览器并登录设备_CallFeatureTest"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.settingShortcut.shouldBe(Condition.exist);
        if(!PRODUCT.equals(CLOUD_PBX)){
            mySettings.close.click();
        }
//        m_extension.showCDRClounm();
    }
    @BeforeClass
    public void InitCallFeature(){
        pageDeskTop.taskBar_Main.click();        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        ringGroup.ringGroup.click();
        ys_waitingLoading(ringGroup.grid_Mask);
        if(Integer.parseInt(String.valueOf(gridLineNum(ringGroup.grid))) != 0) {
            gridSeleteAll(ringGroup.grid);
            ringGroup.delete.click();
            ringGroup.delete_yes.click();
        }

        queue.queue.click();
        ys_waitingLoading(queue.grid_Mask);
        if(Integer.parseInt(String.valueOf(gridLineNum(queue.grid))) != 0) {
            gridSeleteAll(queue.grid);
            queue.delete.click();
            queue.delete_yes.click();
        }
        conference.conference.click();
        ys_waitingLoading(conference.grid_Mask);
        if(Integer.parseInt(String.valueOf(gridLineNum(conference.grid))) != 0) {
            gridSeleteAll(conference.grid);
            conference.delete.click();
            conference.delete_yes.click();
        }
        pickupGroup.pickupGroup.click();
        ys_waitingLoading(pickupGroup.grid_Mask);
        if(Integer.parseInt(String.valueOf(gridLineNum(pickupGroup.grid))) != 0) {
            gridSeleteAll(pickupGroup.grid);
            pickupGroup.delete.click();
            pickupGroup.delete_yes.click();
        }

        paging_intercom.paging_Intercom.click();
        ys_waitingLoading(paging_intercom.grid_Mask);
        if(Integer.parseInt(String.valueOf(gridLineNum(paging_intercom.grid))) != 0) {
            gridSeleteAll(paging_intercom.grid);
            paging_intercom.delete.click();
            paging_intercom.delete_yes.click();
        }

        callFeatures.more.click();
        callback.callback.click();
        ys_waitingLoading(callback.grid_Mask);
        if(Integer.parseInt(String.valueOf(gridLineNum(callback.grid))) != 0) {
            gridSeleteAll(callback.grid);
            callback.delete.click();
            callback.delete_yes.click();
        }
        pinList.PINList.click();
        ys_waitingLoading(pinList.grid_Mask);
        if(Integer.parseInt(String.valueOf(gridLineNum(pinList.grid))) != 0) {
            gridSeleteAll(pinList.grid);
            pinList.delete.click();
            pinList.delete_yes.click();
        }
        closeSetting();
    }
    @Test
    public void A_RingGroup() throws InterruptedException {
        Reporter.infoExec("添加RingGroup");
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        ringGroup.ringGroup.click();
        m_callFeature.addRingGroup("ringgoup1","","0",1100,1102   );
    }

    @Test
    public void B_Queue() throws InterruptedException {
        Reporter.infoExec("添加Queue");
        queue.queue.click();
        m_callFeature.addQueue("queue","",1101,1102);
    }

    @Test
    public void C_Conference() throws InterruptedException {
//        pageDeskTop.taskBar_Main.click();        pageDeskTop.settingShortcut.click();
//        settings.callFeatures_panel.click();
        Reporter.infoExec("添加conference");
        conference.conference.click();
        m_callFeature.addConference("6400","meet1");

    }

    @Test
    public void D_PickupGroup() throws InterruptedException {
//        pageDeskTop.taskBar_Main.click();        pageDeskTop.settingShortcut.click();
//        settings.callFeatures_panel.click();
        Reporter.infoExec("添加pickupGroup");
        pickupGroup.pickupGroup.click();
        m_callFeature.addPickupGroup("pickupgroup1",1100,1101);
        //assert
        String actualName = null;
        actualName = (String) gridContent(pickupGroup.grid,Integer.parseInt(String.valueOf(gridLineNum(pickupGroup.grid))),pickupGroup.gridcolumn_Name);
        YsAssert.assertEquals(actualName,"pickupgroup1");
    }

    @Test
    public void E_PagingIntercom() throws InterruptedException {
        Reporter.infoExec("添加paging_Intercom");
        paging_intercom.paging_Intercom.click();
        m_callFeature.addPagingIntercom("paging_Intercom",6300,"",false,1100,1102);
    }

    @Test
    public void F_CallBack() throws InterruptedException {
        Reporter.infoExec("添加callback");
//        pageDeskTop.taskBar_Main.click();        pageDeskTop.settingShortcut.click();
//        settings.callFeatures_panel.click();
        callFeatures.more.click();
        callback.callback.click();
        m_callFeature.addCallBack("callback1",add_callback.destination_Extension,"1100");

        //assert

    }
    @Test
    public void F_PINList() throws InterruptedException {
//        pageDeskTop.taskBar_Main.click();        pageDeskTop.settingShortcut.click();
//        settings.callFeatures_panel.click();
//        callFeatures.more.click();
        Reporter.infoExec("添加PINList");
        pinList.PINList.click();
        m_callFeature.addPinList("Pin1","123");

        ys_apply();
    }
    @AfterClass
    public void AfterClass() throws InterruptedException {
        Reporter.infoAfterClass("关闭游览器_CallFeatureTest"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
    }

}
