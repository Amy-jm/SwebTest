package com.yeastar.swebtest.testcase.smokecase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;

import static com.yeastar.swebtest.driver.Config.*;
import static com.yeastar.swebtest.driver.Config.mySettings;
import static com.yeastar.swebtest.driver.Config.pageDeskTop;
import static com.yeastar.swebtest.driver.SwebDriver.*;

/**
 * Created by Yeastar on 2017/7/25.
 */
public class DeleteAll {
    @BeforeClass
    public void BeforeClass() {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("打开游览器并登录设备"); //执行操作
        initialDriver(CHROME,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.settings.shouldBe(Condition.exist);
        pageDeskTop.CDRandRecording.shouldBe(Condition.exist);
        pageDeskTop.maintenance.shouldBe(Condition.exist);
        mySettings.close.click();
    }
    @BeforeMethod
    public void BeoforeTest(){
        ys_waitingTime(1500);
    }
    @Test
    public void A_IVR() throws InterruptedException {
        Reporter.infoExec("删除单个IVR");
        pageDeskTop.settings.click();
        settings.callFeatures_panel.click();
        ivr.add.shouldBe(Condition.exist);
        ys_waitingLoading(ivr.grid_Mask);
        gridClick(ivr.grid,1,ivr.gridDelete);
        ivr.delete_yes.click();
        pageDeskTop.apply.click();
    }
    @Test
    public void B_IVR() throws InterruptedException {
//        pageDeskTop.settings.click();
//        settings.callFeatures_panel.click();
        Reporter.infoExec("批量删除IVR");
        ivr.add.shouldBe(Condition.exist);
        m_callFeature.addIVR("test");
        m_callFeature.addIVR("test2");
        gridSeleteAll(ivr.grid);
        ivr.delete.click();
        ivr.delete_yes.click();
        pageDeskTop.apply.click();
        ys_waitingLoading(ivr.grid_Mask);
        String afterDeleteLine = String.valueOf(gridLineNum(ivr.grid));
        YsAssert.assertEquals(afterDeleteLine,"0");
    }
    @Test
    public void C_RingGroup() throws InterruptedException {
//        pageDeskTop.settings.click();
//        settings.callFeatures_panel.click();
        Reporter.infoExec("删除单个ringGroup");
        ringGroup.ringGroup.click();
        ringGroup.add.shouldBe(Condition.exist);
        ys_waitingLoading(ringGroup.grid_Mask);
        gridClick(ringGroup.grid,1,ringGroup.gridDelete);
        ringGroup.delete_yes.click();
        pageDeskTop.apply.click();
    }

    @Test
    public void D_RingGroup() throws InterruptedException {
//        pageDeskTop.settings.click();
//        settings.callFeatures_panel.click();
        Reporter.infoExec("批量删除ringGroup");
        ringGroup.ringGroup.click();
        m_callFeature.addRingGroup("test","",0,1000);
        m_callFeature.addRingGroup("test21","",0,1000);
        gridSeleteAll(ringGroup.grid);
        ringGroup.delete.click();
        ringGroup.delete_yes.click();
        pageDeskTop.apply.click();
        ys_waitingLoading(ringGroup.grid_Mask);
        String afterDeleteLine = String.valueOf(gridLineNum(ringGroup.grid));
        YsAssert.assertEquals(afterDeleteLine,"0");
    }

    @Test
    public void E_Queue() throws InterruptedException {
//        pageDeskTop.settings.click();
//        settings.callFeatures_panel.click();
        Reporter.infoExec("单个删除Queue");
        queue.queue.click();
        queue.add.shouldBe(Condition.exist);
        ys_waitingLoading(queue.grid_Mask);
        gridClick(queue.grid,1,queue.gridDelete);
        queue.delete_yes.click();
        pageDeskTop.apply.click();
    }
    @Test
    public void F_Queue() throws InterruptedException {
//        pageDeskTop.settings.click();
//        settings.callFeatures_panel.click();
        Reporter.infoExec("批量删除Queue");
        queue.queue.click();
        m_callFeature.addQueue("test1","",1000);
        m_callFeature.addQueue("test3","",1000);
        gridSeleteAll(queue.grid);
        queue.delete.click();
        queue.delete_yes.click();
        pageDeskTop.apply.click();
        ys_waitingLoading(queue.grid_Mask);
        String afterDeleteLine = String.valueOf(gridLineNum(queue.grid));
        YsAssert.assertEquals(afterDeleteLine,"0");
    }

    @Test
    public void G_Conference() throws InterruptedException {
//        pageDeskTop.settings.click();
//        settings.callFeatures_panel.click();
        Reporter.infoExec("删除单个Conference");
        conference.conference.click();
        conference.add.shouldBe(Condition.exist);
        ys_waitingLoading(conference.grid_Mask);
        gridClick(conference.grid,1,conference.gridDelete);
        conference.delete_yes.click();
        pageDeskTop.apply.click();
    }
    @Test
    public void H_Conferene() throws InterruptedException {
//        pageDeskTop.settings.click();
//        settings.callFeatures_panel.click();
        Reporter.infoExec("批量删除Conference");
        conference.conference.click();
        m_callFeature.addConference("test3");
        gridSeleteAll(conference.grid);
        conference.delete.click();
        conference.delete_yes.click();
        pageDeskTop.apply.click();
        ys_waitingLoading(conference.grid_Mask);
        String afterDeleteLine = String.valueOf(gridLineNum(conference.grid));
        YsAssert.assertEquals(afterDeleteLine,"0");
    }

    @Test
    public void I_PickupGroup() throws InterruptedException {
//        pageDeskTop.settings.click();
//        settings.callFeatures_panel.click();
        Reporter.infoExec("删除单个PinckupGruop");
        pickupGroup.pickupGroup.click();
        pickupGroup.add.shouldBe(Condition.exist);
        ys_waitingLoading(pickupGroup.grid_Mask);
        gridClick(pickupGroup.grid,1,pickupGroup.gridDelete);
        pickupGroup.delete_yes.click();
        pageDeskTop.apply.click();
    }

    @Test
    public void J_PickupGroup() throws InterruptedException {
//        pageDeskTop.settings.click();
//        settings.callFeatures_panel.click();
        Reporter.infoExec("批量删除PinckupGruop");
        pickupGroup.pickupGroup.click();
        m_callFeature.addPickupGroup("testpick",1000,1001);
        m_callFeature.addPickupGroup("testpick2",1000,1001);
        gridSeleteAll(pickupGroup.grid);
        pickupGroup.delete.click();
        pickupGroup.delete_yes.click();
        pageDeskTop.apply.click();
        ys_waitingLoading(pickupGroup.grid_Mask);
        String afterDeleteLine = String.valueOf(gridLineNum(pickupGroup.grid));
        YsAssert.assertEquals(afterDeleteLine,"0");
    }

    @Test
    public void K_PagingIntercom() throws InterruptedException {
//        pageDeskTop.settings.click();
//        settings.callFeatures_panel.click();
        Reporter.infoExec("删除单个PagingIntercom");
        paging_intercom.paging_Intercom.click();
        paging_intercom.add.shouldBe(Condition.exist);
        ys_waitingLoading(paging_intercom.grid_Mask);
        gridClick(paging_intercom.grid,1,paging_intercom.gridDelete);
        paging_intercom.delete_yes.click();
        pageDeskTop.apply.click();
    }

    @Test
    public void L_PagingIntercom() throws InterruptedException {
//        pageDeskTop.settings.click();
//        settings.callFeatures_panel.click();
        Reporter.infoExec("批量删除PagingIntercom");
        paging_intercom.paging_Intercom.click();
        m_callFeature.addPagingIntercom("paging_Intercom",1000,1001);
        gridSeleteAll(paging_intercom.grid);
        paging_intercom.delete.click();
        paging_intercom.delete_yes.click();
        pageDeskTop.apply.click();
        ys_waitingLoading(paging_intercom.grid_Mask);
        String afterDeleteLine = String.valueOf(gridLineNum(paging_intercom.grid));
        YsAssert.assertEquals(afterDeleteLine,"0");
    }

    @Test
    public void M_CallBack() throws InterruptedException {
//        pageDeskTop.settings.click();
//        settings.callFeatures_panel.click();
        Reporter.infoExec("删除单个Callback");
        callFeatures.more.click();
        callback.callback.click();
        callback.add.shouldBe(Condition.exist);
        ys_waitingLoading(callback.grid_Mask);
        gridClick(callback.grid,1,callback.gridDelete);
        callback.delete_yes.click();
        pageDeskTop.apply.click();
    }

    @Test
    public void N_CallBack() throws InterruptedException {
//        pageDeskTop.settings.click();
//        settings.callFeatures_panel.click();
//        callFeatures.more.click();
        Reporter.infoExec("批量删除CallBack");
        callback.callback.click();
        m_callFeature.addCallBack("callback1",add_callback.destination_Extension,"1000");
        gridSeleteAll(callback.grid);
        callback.delete.click();
        callback.delete_yes.click();
        pageDeskTop.apply.click();
        ys_waitingLoading(callback.grid_Mask);
        String afterDeleteLine = String.valueOf(gridLineNum(callback.grid));
        YsAssert.assertEquals(afterDeleteLine,"0");
    }
    @Test
    public void O_Disa() throws InterruptedException {
//        pageDeskTop.settings.click();
//        settings.callFeatures_panel.click();
//        callFeatures.more.click();
        Reporter.infoExec("删除单个Disa");
        disa.DISA.click();
        disa.add.shouldBe(Condition.exist);
        ys_waitingLoading(disa.grid_Mask);
        gridClick(disa.grid,1,disa.gridDelete);
        disa.delete_yes.click();
        pageDeskTop.apply.click();
    }

    @Test
    public void P_Disa() throws InterruptedException {
//        pageDeskTop.settings.click();
//        settings.callFeatures_panel.click();
//        callFeatures.more.click();
        Reporter.infoExec("批量删除disa1");
        disa.DISA.click();
        m_callFeature.addDISA("disa1","",0,0,"outrouter1");
        m_callFeature.addDISA("disa2","",0,0,"outrouter1");
        gridSeleteAll(disa.grid);
        disa.delete.click();
        disa.delete_yes.click();
        pageDeskTop.apply.click();
        ys_waitingLoading(disa.grid_Mask);
        String afterDeleteLine = String.valueOf(gridLineNum(disa.grid));
        YsAssert.assertEquals(afterDeleteLine,"0");
    }
    @Test
    public void R_PINList() throws InterruptedException {
//        pageDeskTop.settings.click();
//        settings.callFeatures_panel.click();
//        callFeatures.more.click();
        Reporter.infoExec("删除单个PINList");
        pinList.PINList.click();
        pinList.add.shouldBe(Condition.exist);
        ys_waitingLoading(pinList.grid_Mask);
        gridClick(pinList.grid,1,pinList.gridDelete);
        pinList.delete_yes.click();
        pageDeskTop.apply.click();
    }

    @Test
    public void S_PINList() throws InterruptedException {
//        pageDeskTop.settings.click();
//        settings.callFeatures_panel.click();
//        callFeatures.more.click();
        Reporter.infoExec("批量删除Pin1");
        pinList.PINList.click();
        m_callFeature.addPinList("Pin1","123");
        m_callFeature.addPinList("Pin2","342");
        gridSeleteAll(pinList.grid);
        pinList.delete.click();
        pinList.delete_yes.click();
        pageDeskTop.apply.click();
        ys_waitingLoading(pinList.grid_Mask);
        Thread.sleep(1000);
        String afterDeleteLine = String.valueOf(gridLineNum(pinList.grid));
        YsAssert.assertEquals(afterDeleteLine,"0");
    }
    @Test
    public void T_OutboundRoutes() throws InterruptedException {
//        pageDeskTop.settings.click();
//        settings.callControl_panel.click();
        Reporter.infoExec("删除单个OutboundRoutes");
        settings.callControl_tree.click();
        outboundRoutes.outboundRoutes.click();
        outboundRoutes.add.shouldBe(Condition.exist);
        ys_waitingLoading(outboundRoutes.grid_Mask);
        gridClick(outboundRoutes.grid,1,outboundRoutes.gridDelete);
        outboundRoutes.delete_yes.click();
        pageDeskTop.apply.click();
    }
    @Test
    public void U_OutboundRoutes() throws InterruptedException {
//        pageDeskTop.settings.click();
//        settings.callControl_panel.click();
        Reporter.infoExec("批量删除OutboundRoutes");
        outboundRoutes.outboundRoutes.click();
        outboundRoutes.add.shouldBe(Condition.exist);
        ys_waitingLoading(outboundRoutes.grid_Mask);

        ArrayList<String> arrayTrunk = new ArrayList<>();
        arrayTrunk.add("BRI2-4");

        ArrayList<String> arrayExtension = new ArrayList<>();
        arrayExtension.add("1000");
        m_callcontrol.addOutboundRoute("out","90","2","",arrayExtension,arrayTrunk);
        Thread.sleep(2000);
        gridSeleteAll(outboundRoutes.grid);
        outboundRoutes.delete.click();
        outboundRoutes.delete_yes.click();
        pageDeskTop.apply.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        Thread.sleep(1000);
        String afterDeleteLine = String.valueOf(gridLineNum(outboundRoutes.grid));
        YsAssert.assertEquals(afterDeleteLine,"0");
    }
    @Test
    public void V_InboundRoutes() throws InterruptedException {
//        pageDeskTop.settings.click();
//        settings.callControl_panel.click();
        Reporter.infoExec("删除单个inboundRoutes");
        inboundRoutes.inboundRoutes.click();
        inboundRoutes.add.shouldBe(Condition.exist);
        ys_waitingLoading(inboundRoutes.gridLoading);
        gridClick(inboundRoutes.grid,1,inboundRoutes.gridDelete);
        inboundRoutes.delete_yes.click();
        pageDeskTop.apply.click();
    }

    @Test
    public void W_InboundRoutes() throws InterruptedException {
//        pageDeskTop.settings.click();
//        settings.callControl_panel.click();
        Reporter.infoExec("批量删除inboundRoutes");
        inboundRoutes.inboundRoutes.click();
        inboundRoutes.add.shouldBe(Condition.exist);
        ys_waitingLoading(inboundRoutes.gridLoading);
        m_callcontrol.addInboundRoutes("inrouter1","","","all");
        Thread.sleep(2000);
        gridSeleteAll(inboundRoutes.grid);
        inboundRoutes.delete.click();
        inboundRoutes.delete_yes.click();
        pageDeskTop.apply.click();
        ys_waitingLoading(inboundRoutes.gridLoading);
        Thread.sleep(1000);
        String afterDeleteLine = String.valueOf(gridLineNum(inboundRoutes.grid));
        YsAssert.assertEquals(afterDeleteLine,"0");
    }
    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(10000);
        Reporter.infoAfterClass("关闭游览器"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        Thread.sleep(10000);
    }
}
