package com.yeastar.swebtest.testcase.RegressionCase.pbxcore;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by AutoTest on 2017/10/31.
 */
public class deleteTest extends SwebDriver {
    @BeforeClass
    public void BeforeClass() throws InterruptedException {
        Reporter.infoBeforeClass("开始执行：======  Delete  ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        ys_waitingMask();
        mySettings.close.click();
        m_extension.showCDRClounm();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        ys_waitingMask();
        ringGroup.ringGroup.click();

    }


    //    新建响铃组
    @Test
    public void A_add_RingGroup1 () throws InterruptedException {
    for (int i=1; i<1000;i++) {
        System.out.println("==============第"+i+"次执行测试==========");

        ringGroup.add.shouldBe(Condition.exist);
        deletes(" 删除所有RingGroup", ringGroup.grid, ringGroup.delete, ringGroup.delete_yes, ringGroup.grid_Mask);
        Reporter.infoExec(" 添加RingGroup1：6200，选择分机1000,1100,1105，其它默认"); //执行操作
        m_callFeature.addRingGroup("RingGroup1", "6200", add_ring_group.rs_ringall, 1000, 1100, 1105);
        Reporter.infoExec(" 新建RingGroup6201,Mem:ExtensionGroup1,其它默认"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        ringGroup.add.shouldBe(Condition.exist);
        m_callFeature.addRingGroup("RingGroup6201", "6201", "", "ExtensionGroup1");
        ys_apply();

        Reporter.infoExec(" 编辑RingGroup6201，成员响铃时间：15s，Failover：分机1102"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        ringGroup.add.shouldBe(Condition.exist);
        gridClick(ringGroup.grid, gridFindRowByColumn(ringGroup.grid, ringGroup.gridcolumn_Name, "RingGroup6201", sort_ascendingOrder), ringGroup.gridEdit);
        add_ring_group.secondstoringeachmenmber.clear();
        add_ring_group.secondstoringeachmenmber.setValue("15");
        comboboxSelect(add_ring_group.failoverDestinationtype, s_extensin);
        comboboxSet(add_ring_group.failoverDestination, extensionList, "1102");
        add_ring_group.save.click();
        ys_waitingLoading(ringGroup.grid_Mask);
        ys_apply();

        Reporter.infoExec(" 新建响铃组RingSequentially6202，Sequentially,每个成员20s,成员：1000、1100、1101、1105"); //执行操作
        m_callFeature.addRingGroup("RingSequentially6202","6202",add_ring_group.rs_sequentially,1000,1100,1101,1105);
        ys_apply();

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_tree.click();
        ringGroup.ringGroup.click();
        ringGroup.add.shouldBe(Condition.exist);
        Reporter.infoExec(" 表格删除：RingGroup6201-取消删除"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(ringGroup.grid, ringGroup.gridcolumn_Name, "RingGroup6201", sort_ascendingOrder)));
        int rows = Integer.parseInt(String.valueOf(gridLineNum(ringGroup.grid)));
        gridClick(ringGroup.grid, row, ringGroup.gridDelete);
        ringGroup.delete_no.click();
        ys_waitingLoading(ringGroup.grid_Mask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(ringGroup.grid)));
        System.out.println("row1:" + row1);
        YsAssert.assertEquals(rows, row1, "表格删除：RingGroup6201-取消删除");

        Reporter.infoExec(" 表格删除：RingGroup6201-确定删除"); //执行操作
        gridClick(ringGroup.grid, row, ringGroup.gridDelete);
        ringGroup.delete_yes.click();
        ys_waitingLoading(ringGroup.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(ringGroup.grid)));
        System.out.println("row2:" + row2);
        int row3 = row2 + 1;
        System.out.println("row3:" + row3);
        YsAssert.assertEquals(row3, row1, "表格删除：RingGroup6201-确定删除");

        Reporter.infoExec(" 删除：RingSequentially6202-取消删除"); //执行操作
        int row8 = Integer.parseInt(String.valueOf(gridLineNum(ringGroup.grid)));
        int row4 = Integer.parseInt(String.valueOf(gridFindRowByColumn(ringGroup.grid, ringGroup.gridcolumn_Name, "RingSequentially6202", sort_ascendingOrder)));
        gridCheck(ringGroup.grid, row4, ringGroup.gridcolumn_Check);
        ringGroup.delete.click();
        ringGroup.delete_no.click();
        ys_waitingLoading(ringGroup.grid_Mask);
        int row5 = Integer.parseInt(String.valueOf(gridLineNum(ringGroup.grid)));
        System.out.println("row5:" + row5);
        YsAssert.assertEquals(row8, row5, "删除：RingSequentially6202-取消删除");

        Reporter.infoExec(" 删除：RingSequentially6202-确定删除"); //执行操作
        ringGroup.delete.click();
        ringGroup.delete_yes.click();
        ys_waitingLoading(ringGroup.grid_Mask);
        int row6 = Integer.parseInt(String.valueOf(gridLineNum(ringGroup.grid)));
        System.out.println("row6:" + row6);
        int row7 = row6 + 1;
        System.out.println("row7:" + row7);
        YsAssert.assertEquals(row5, row7, "删除：RingSequentially6202-确定删除");
        ys_apply();
    }
    }

    @AfterClass
    public void AfterClass() throws InterruptedException {
            Thread.sleep(5000);
            Reporter.infoExec(" 恢复呼入路由InRoute1到分机1000");
            settings.callControl_tree.click();
            inboundRoutes.inboundRoutes.click();
            inboundRoutes.add.shouldBe(Condition.exist);
            gridClick(inboundRoutes.grid, gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute1", sort_ascendingOrder), inboundRoutes.gridEdit);
            ys_waitingMask();
            comboboxSelect(add_inbound_route.destinationType, s_extensin);
            comboboxSet(add_inbound_route.destination, extensionList, "1000");
            add_inbound_route.save.click();
            ys_waitingTime(1000);
            ys_apply();
            Reporter.infoAfterClass("执行完毕：======  RingGroup  ======"); //执行操作
            pjsip.Pj_Destory();
            quitDriver();
            Thread.sleep(5000);

    }
}
