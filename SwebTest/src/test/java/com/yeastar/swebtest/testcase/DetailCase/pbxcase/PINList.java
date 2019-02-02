package com.yeastar.swebtest.testcase.DetailCase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Selenide.screenshot;

/**
 * Created by Caroline on 2018/1/3.
 * 因为在呼出呼入路由、DISA部分有对PINList进行部分测试，所以PINList详细用例中的一些测试点在该份代码中不再重复编写
 * 这里只对未测试过的点进行补充
 */
public class PINList extends SwebDriver {
    @BeforeClass
    public void BeforeClass() {

        Reporter.infoBeforeClass("开始执行：====== PINList ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes") && Integer.valueOf(VERSION_SPLIT[1]) <= 9){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
    }
    @Test
    public void A0_Init1() {
//        resetoreBeforetest("BeforeTest_Local.bak");
    }
    @Test
    public void A1_addExtension(){
        pjsip.Pj_Init();
        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, 1);
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 辅助设备1注册分机3001"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 3001, EXTENSION_PASSWORD, -1, DEVICE_ASSIST_1, UDP_PORT_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(3001, DEVICE_ASSIST_1);

        Reporter.infoExec(" 辅助设备2注册分机2001"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 2001, EXTENSION_PASSWORD, -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2001, DEVICE_ASSIST_2);
        closePbxMonitor();
    }
//    新增PINList
    @Test
    public void B_addPin() {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callFeatures_panel.click();
        callFeatures.more.click();
        pinList.PINList.click();
        m_callFeature.addPinList("ABCDE~*_-+.?123456789012345678901234567890~~~》《》？、“：+-——）（*……）”","12,123,456,12345678,********");
    }
//    编辑DISA
    @Test
    public void C_editDISA(){
        Reporter.infoExec("编辑DISA1：选择pin码——ABCDE~*_-+.?123456789012345678901234567890~~~》《》？、“：+-——）（*……）”");
        disa.DISA.click();
        disa.add.shouldBe(Condition.exist);
        ys_waitingLoading(disa.grid_Mask);
        ys_waitingTime(2000);
        gridClick(disa.grid,gridFindRowByColumn(disa.grid,disa.gridcolumn_Name,"DISA1",sort_ascendingOrder),disa.gridEdit);
        comboboxSelect(add_disa.password,add_disa.passwordType_Pinset);
        ys_waitingTime(2000);
        comboboxSet(add_disa.password_Pinset,nameList,"ABCDE~*_-+.?123456789012345678901234567890~~~》《》？、“：+-——）（*……）”");
        add_disa.save.click();
        ys_waitingLoading(disa.grid_Mask);
    }
//    编辑呼入路由
    @Test
    public void D_editInRoute(){
        settings.callControl_tree.click();
        inboundRoutes.inboundRoutes.click();
        Reporter.infoExec("修改呼入路由的目的地到DISA");
        gridClick(inboundRoutes.grid,gridFindRowByColumn(inboundRoutes.grid,inboundRoutes.gridcolumn_Name,"InRoute1",sort_ascendingOrder),inboundRoutes.gridEdit);
        ys_waitingMask();
        comboboxSelect(add_inbound_route.destinationType,add_inbound_route.s_disa);
        ys_waitingTime(1000);
        //        保存编辑页面
        add_inbound_route.save.click();
        ys_waitingLoading(inboundRoutes.grid_Mask);
        ys_apply();
    }
//    @Test
    public void D1_backup(){
        backupEnviroment(this.getClass().getName());
    }
    @Test
    public void E_checkPin(){
        //        SPS呼入，DISA走SIP
        Reporter.infoExec("2001拨打99999通过SPS外线呼入，进入DISA后正确的Pin码，再进行二次拨号");
        tcpSocket.connectToDevice(100000);
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        boolean showKeyWord= tcpSocket.getAsteriskInfo("DISA1");
        tcpSocket.closeTcpSocket();
        YsAssert.assertEquals(showKeyWord,true,"外线呼出进入pinList，AMI未打印关键字");
        pjsip.Pj_Send_Dtmf(2001,"*","*","*","*","*","*","*","*","#");
        pjsip.Pj_Send_Dtmf(2001,"1","3","0","0","1","#");
        ys_waitingTime(5000);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","13001","Answered",SPS,SIPTrunk,communication_outRoute);
        m_extension.checkCDR_OtherInfo(cdRandRecordings.gridColumn_PinCode,"********",1);

    }
    @Test
    public void F_deletePinNumber(){
        settings.callFeatures_tree.click();
        pinList.PINList.click();
        Reporter.infoExec("修改ABCDE~*_-+.?123456789012345678901234567890~~~》《》？、“：+-——）（*……）”的PIN_Value为12,123,456,12345678");
        gridClick(pinList.grid,gridFindRowByColumn(pinList.grid,pinList.gridcolumn_Name,"ABCDE~*_-+.?123456789012345678901234567890~~~》《》？、“：+-——）（*……）”",sort_ascendingOrder),pinList.gridEdit);
        add_pin_list.PINList.setValue("12,123,456,12345678");
        add_pin_list.save.click();
        ys_apply();
    }
    @Test
    public void G_checkPin(){
        //        SPS呼入，DISA走SIP
        Reporter.infoExec("2001拨打99999通过SPS外线呼入，进入DISA后先输入错误pin码，接着输入正确Pin码，再进行二次拨号");
        tcpSocket.connectToDevice(70000);
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        boolean showKeyWord= tcpSocket.getAsteriskInfo("DISA1");
        tcpSocket.closeTcpSocket();
        YsAssert.assertEquals(showKeyWord,true,"外线呼出进入pinList，AMI未打印关键字");
        pjsip.Pj_Send_Dtmf(2001,"*","*","*","*","*","*","*","*","#");
        pjsip.Pj_Send_Dtmf(2001,"1","2","3","#");
        pjsip.Pj_Send_Dtmf(2001,"1","3","0","0","1","#");
        ys_waitingTime(5000);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();

        m_extension.checkCDR("2001 <2001>","13001","Answered",SPS,SIPTrunk,communication_outRoute);
        m_extension.checkCDR_OtherInfo(cdRandRecordings.gridColumn_PinCode,"123",1);

    }
    @Test
    public void H1_deleteOnePin_no(){
        Reporter.infoExec(" 删除单个PIN——选择no"); //执行操作
//       定位要删除的那条DISA
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(pinList.grid, pinList.gridcolumn_Name, "ABCDE~*_-+.?123456789012345678901234567890~~~》《》？、“：+-——）（*……）”", sort_ascendingOrder)));
        //还未操作前呼出路由的所有条数
        int rows = Integer.parseInt(String.valueOf(gridLineNum(pinList.grid)));
        System.out.println("预期值:" + rows);
        gridClick(pinList.grid, row, pinList.gridDelete);
        pinList.delete_no.click();
        ys_waitingLoading(pinList.grid_Mask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(pinList.grid)));
        System.out.println("实际值:" + row1);
        YsAssert.assertEquals(row1, rows, "删除DISA1-取消删除");
    }
    @Test
    public void H2_deleteOnePin_yes(){
        Reporter.infoExec(" 删除PIN——选择yes"); //执行操作
        int rows = Integer.parseInt(String.valueOf(gridLineNum(pinList.grid)));
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(pinList.grid, pinList.gridcolumn_Name, "ABCDE~*_-+.?123456789012345678901234567890~~~》《》？、“：+-——）（*……）”", sort_ascendingOrder)));
        gridClick(pinList.grid, row, pinList.gridDelete);
        pinList.delete_yes.click();
        ys_waitingLoading(pinList.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(pinList.grid)));
        System.out.println("实际值row2:" + row2);
        int row3 = rows - 1;
        System.out.println("期望值row3:" + row3);
        ys_apply();
        YsAssert.assertEquals(row2, row3, "删除DISA1——确定删除");
    }
//    删除Pin后，呼入到DISA无需再输入Pin码，可直接二次拨号呼出
    @Test
    public void I_checkPin(){
        //        SPS呼入，DISA走SIP
        Reporter.infoExec("2001拨打99999通过SPS外线呼入，进行二次拨号");
        pjsip.Pj_Make_Call_Auto_Answer(2001,"99999",DEVICE_ASSIST_2,false);
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(2001,"1","3","0","0","1","#");
        ys_waitingTime(5000);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("2001 <2001>","13001","Answered",SPS,SIPTrunk,communication_outRoute);
    }
    @Test
    public void J1_deletePartPin_no(){
        Reporter.infoExec(" 全部勾选，再取消某条的勾选后-取消删除"); //执行操作
//        还未删除前的表格行数
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(pinList.grid)));
        System.out.println("期望值row1:" + row1);
        int row2 = Integer.parseInt(String.valueOf(gridFindRowByColumn(pinList.grid, pinList.gridcolumn_Name, "test1", sort_ascendingOrder)));
//        全部勾选
        gridSeleteAll(pinList.grid);
        ys_waitingTime(1000);
//        取消勾选
        gridCheck(pinList.grid, row2, pinList.gridcolumn_Check);
//        点击删除按钮
        pinList.delete.click();
        pinList.delete_no.click();
        ys_waitingLoading(pinList.grid_Mask);
        int row4 = Integer.parseInt(String.valueOf(gridLineNum(pinList.grid)));
        System.out.println("实际值row4:" + row4);
        YsAssert.assertEquals(row4, row1, "全部勾选，再取消某条的勾选后-取消删除");
    }
    @Test
    public void J2_deletePartPin_yes(){
        Reporter.infoExec(" 全部勾选，再取消某条的勾选后-确定删除"); //执行操作
        pinList.delete.click();
        pinList.delete_yes.click();
        ys_waitingLoading(pinList.grid_Mask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(pinList.grid)));
        System.out.println("实际row6:" + row1);
        ys_apply();
        YsAssert.assertEquals(row1, 1, "全部勾选，再取消某条的勾选后-确定删除");
    }
    @Test
    public void J3_deleteAllPin_no(){
        Reporter.infoExec(" 全部勾选-取消删除"); //执行操作
//        还未删除前的表格行数
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(pinList.grid)));
//        全部勾选
        gridSeleteAll(pinList.grid);
//        点击删除按钮
        pinList.delete.click();
        pinList.delete_no.click();
        ys_waitingLoading(pinList.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(pinList.grid)));
        System.out.println("实际row2:" + row2);
        YsAssert.assertEquals(row1, row2, "全部勾选-取消删除");
    }
    @Test
    public void J4_deleteAllPin_yes(){
        Reporter.infoExec(" 全部勾选-确定删除"); //执行操作
        pinList.delete.click();
        pinList.delete_yes.click();
        ys_waitingLoading(pinList.grid_Mask);
        int row = Integer.parseInt(String.valueOf(gridLineNum(pinList.grid)));
        System.out.println("实际row:" + row);
        ys_apply();
        YsAssert.assertEquals(row, 0, "全部勾选-确定删除");
    }
    @Test
    public void K1_recovery_InRoute() throws InterruptedException {
        settings.callControl_tree.click();
        inboundRoutes.inboundRoutes.click();
        Reporter.infoExec("修改呼入路由的目的地到DISA");
        gridClick(inboundRoutes.grid, gridFindRowByColumn(inboundRoutes.grid, inboundRoutes.gridcolumn_Name, "InRoute1", sort_ascendingOrder), inboundRoutes.gridEdit);
        ys_waitingMask();
        comboboxSelect(add_inbound_route.destinationType, add_inbound_route.s_extensin);
        comboboxSet(add_inbound_route.destination, extensionList, "1000");
        ys_waitingTime(1000);
        //        保存编辑页面
        add_inbound_route.save.click();
        ys_waitingLoading(inboundRoutes.grid_Mask);
    }
    @Test
    public void K2_recovery_Pin() throws InterruptedException {
        Reporter.infoExec("新增Pin——test1、test2");
        settings.callFeatures_tree.click();
        m_callFeature.addPinList("test1","12345678");
        pinList.add.click();
        add_pin_list.name.setValue("test2");
        add_pin_list.PINList.setValue("123456");
        add_pin_list.save.click();
    }
    @Test
    public void K3_recovery_Disa(){
        Reporter.infoExec("新增DISA——DISA1");
        disa.DISA.click();
        disa.add.shouldBe(Condition.exist);
        ys_waitingLoading(disa.grid_Mask);
        ys_waitingTime(2000);
        gridClick(disa.grid,gridFindRowByColumn(disa.grid,disa.gridcolumn_Name,"DISA1",sort_ascendingOrder),disa.gridEdit);
        comboboxSelect(add_disa.password,add_disa.passwordType_None);
        ys_waitingTime(2000);
       add_disa.save.click();
        ys_waitingLoading(disa.grid_Mask);
    }
    //    AfterMethod是在每个Test执行后都要来执行的方法
    @AfterMethod
    public void AfterMethod(){
        if (cdRandRecordings.deleteCDR.isDisplayed()){
            System.out.println("admin角色的cdr页面未关闭");
            closeCDRRecord();
            System.out.println("admin角色的cdr页面已关闭");
        }
    }
    @AfterClass
    public void AfterClass() throws InterruptedException {
        Reporter.infoAfterClass("执行完毕：======  PINList  ======"); //执行操作
        quitDriver();
        pjsip.Pj_Destory();
        ys_waitingTime(10000);
        killChromePid();
    }
}
