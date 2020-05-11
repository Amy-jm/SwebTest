package com.yeastar.swebtest.testcase.RegressionCase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import com.yeastar.untils.*;
import io.qameta.allure.Description;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.Platform;
import org.testng.annotations.*;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static com.codeborne.selenide.Selenide.refresh;
import static com.codeborne.selenide.Selenide.sleep;

/**
 * Created by xlq on 2017/9/26.
 * 功能：执行PBXcore测试的前置设置
 */
@Log4j2
@Listeners({AllureReporterListener.class, RetryListener.class, TestNGListener.class})
public class BeforeTest extends SwebDriver{
    String[] version = DEVICE_VERSION.split("\\.");
    @BeforeClass
    public void BeforeClass() {
        pjsip.Pj_Init();
        Reporter.infoBeforeClass("开始执行：======前置环境设置—BeforeTest======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && !PRODUCT.equals(PC) && Integer.valueOf(VERSION_SPLIT[1]) <= 9){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();

    }

    @Test
    public void A0_setPasswordType(){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.general_panel.click();
        m_general.setExPreferencesDefault();
        closeSetting();

		if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.security_panel.click();
        service.service.click();
        ys_waitingTime(5000);
        setCheckBox(service.weakPassword,true);
        service.save.click();
        ys_apply();
        closeSetting();
    }
//    创建分机1000、1100~1105
    @Test
    public void A1_addExtension() throws InterruptedException {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.extensions_panel.click();
        settings.extensions_tree.click();
        deletes(" 删除所有分机",extensions.grid,extensions.delete,extensions.delete_yes,extensions.grid_Mask);
        Reporter.infoExec(" 添加分机1000");
        m_extension.addSipExtension(1000, EXTENSION_PASSWORD);
        if (PRODUCT.equals(CLOUD_PBX) && Integer.valueOf(VERSION_SPLIT[1]) >= 7){
            m_extension.addBulkExtensions_cloud(1100,6);
            Reporter.infoExec(" 编辑分机1100的注册密码为Yeastar202Yeastar202"); //执行操作
            gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Extensions,"1100",sort_ascendingOrder),extensions.gridEdit);
            ys_waitingMask();
            addExtensionBasic.registrationPassword.setValue(EXTENSION_PASSWORD);
            addExtensionBasic.save.click();
            ys_waitingLoading(extensions.grid_Mask);

            Reporter.infoExec(" 编辑分机1101的注册密码为Yeastar202Yeastar202"); //执行操作
            gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Extensions,"1101",sort_ascendingOrder),extensions.gridEdit);
            ys_waitingMask();
            addExtensionBasic.registrationPassword.setValue(EXTENSION_PASSWORD);
            addExtensionBasic.save.click();
            ys_waitingLoading(extensions.grid_Mask);

            Reporter.infoExec(" 编辑分机1102的注册密码为Yeastar202Yeastar202"); //执行操作
            gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Extensions,"1102",sort_ascendingOrder),extensions.gridEdit);
            ys_waitingMask();
            addExtensionBasic.registrationPassword.setValue(EXTENSION_PASSWORD);
            addExtensionBasic.save.click();
            ys_waitingLoading(extensions.grid_Mask);

            Reporter.infoExec(" 编辑分机1103的注册密码为Yeastar202Yeastar202"); //执行操作
            gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Extensions,"1103",sort_ascendingOrder),extensions.gridEdit);
            ys_waitingMask();
            addExtensionBasic.registrationPassword.setValue(EXTENSION_PASSWORD);
            addExtensionBasic.save.click();
            ys_waitingLoading(extensions.grid_Mask);

            Reporter.infoExec(" 编辑分机1104的注册密码为Yeastar202Yeastar202"); //执行操作
            gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Extensions,"1104",sort_ascendingOrder),extensions.gridEdit);
            ys_waitingMask();
            addExtensionBasic.registrationPassword.setValue(EXTENSION_PASSWORD);
            addExtensionBasic.save.click();
            ys_waitingLoading(extensions.grid_Mask);

            Reporter.infoExec(" 编辑分机1105的注册密码为Yeastar202Yeastar202"); //执行操作
            gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Extensions,"1105",sort_ascendingOrder),extensions.gridEdit);
            ys_waitingMask();
            addExtensionBasic.registrationPassword.setValue(EXTENSION_PASSWORD);
            addExtensionBasic.userPassword.setValue(EXTENSION_PASSWORD);
            addExtensionBasic.save.click();
            ys_waitingLoading(extensions.grid_Mask);
        }else {
            Reporter.infoExec(" 批量创建分机1100~1105");
            m_extension.addBulkExtensions(1100, 6, 2, EXTENSION_PASSWORD, 2, EXTENSION_PASSWORD);
        }
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if (!FXS_1.equals("null")) {
            Reporter.infoExec("创建FXS分机1106");
            m_extension.addFxsExtension(1106, EXTENSION_PASSWORD, FXS_1);
        }
    }

    @Test
    public void A2_editExtenName1() throws InterruptedException {
        extensions.Extensions.click();
        Reporter.infoExec(" 编辑分机1103的名字为xlq"); //执行操作
        gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Extensions,"1103",sort_ascendingOrder),extensions.gridEdit);
        ys_waitingMask();
        addExtensionBasic.name.setValue("xlq");
        addExtensionBasic.save.click();
        ys_waitingLoading(extensions.grid_Mask);

    }

    @Test
    public void A3_editExtenName2() throws InterruptedException {
        Reporter.infoExec(" 编辑分机1104的名字为xll"); //执行操作
        gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Extensions,"1104",sort_ascendingOrder),extensions.gridEdit);
        ys_waitingMask();
        addExtensionBasic.name.setValue("xll");
        addExtensionBasic.save.click();
        ys_waitingLoading(extensions.grid_Mask);

    }

//    编辑分机1105的邮箱为autotest@yeastar.com
    @Test
    public void A3_editExtenEmail() throws InterruptedException {
        Reporter.infoExec(" 编辑分机1105的邮箱为autotest@yeastar.com"); //执行操作
        gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Extensions,"1105",sort_ascendingOrder),extensions.gridEdit);
        ys_waitingMask();
        addExtensionBasic.email.setValue("autotest@yeastar.com");
        addExtensionBasic.save.click();
        ys_waitingLoading(extensions.grid_Mask);

    }


//      创建分机组
    @Test
    public void A4_addExtensionGroup() throws InterruptedException {
        extensionGroup.extensionGroup.click();
        deletes(" 删除所有分机组",extensionGroup.grid,extensionGroup.delete,extensionGroup.delete_yes,extensionGroup.grid_Mask);
        Reporter.infoExec(" 添加分机组：ExtensionGroup1:1000,1100,1101,1105"); //执行操作
        m_extension.addExtensionGroup("ExtensionGroup1",1000,1100,1101,1105);
    }

    //    创建外线
//TODO 0424 ignore assert exception
    @Test
    public void B_addtrunk() throws InterruptedException {
        //DEBUG
//        pageDeskTop.taskBar_Main.click();
//        pageDeskTop.settingShortcut.click();
//        settings.trunks_panel.click();
        //
        settings.trunks_tree.click();
        Reporter.infoExec(" 删除所有VoIP外线"); //执行操作
        setPageShowNum(trunks.grid, 100);
        gridSeleteAll(trunks.grid);
        trunks.delete.click();
        if (trunks.delete_yes.isDisplayed()){
            trunks.delete_yes.click();
        }
        if (trunks.delete_ok.isDisplayed()){
            trunks.delete_ok.click();
        }

        ys_waitingLoading(trunks.grid_Mask);
        Reporter.infoExec(" 添加sip外线"+SIPTrunk); //执行操作
        m_trunks.addTrunk("SIP",add_voIP_trunk_basic.VoipTrunk,SIPTrunk,DEVICE_ASSIST_1,String.valueOf(UDP_PORT_ASSIST_1),DEVICE_ASSIST_1,"3000","3000","3000",EXTENSION_PASSWORD,"3000");
    }

    //TODO 0424 ignore assert exception
    @Test
    public void C_addtrunk() throws InterruptedException {
        Reporter.infoExec(" 添加iax外线"+IAXTrunk);
        m_trunks.addTrunk("IAX",add_voIP_trunk_basic.VoipTrunk,IAXTrunk,DEVICE_ASSIST_1,"4569","","3100","","",EXTENSION_PASSWORD,"");
    }
    @Test
    public void D_addtrunk() throws InterruptedException {
        Reporter.infoExec(" 添加sps外线"+SPS);
        m_trunks.addTrunk("SIP",add_voIP_trunk_basic.PeerToPeer,SPS,DEVICE_ASSIST_2,String.valueOf(UDP_PORT_ASSIST_2),DEVICE_ASSIST_2,
                "","","","","");
    }
    //TODO 0424 ignore assert exception
    @Test
    public void E1_addtrunk() throws InterruptedException {
        Reporter.infoExec(" 添加spx外线"+SPX);
        m_trunks.addTrunk("IAX",add_voIP_trunk_basic.PeerToPeer,SPX,DEVICE_ASSIST_2,"4569",DEVICE_ASSIST_2,
                "","","","","");
    }
    //TODO 0424 ignore assert exception
    @Test
    public void E2_addtrunk() {
        if (!DEVICE_ASSIST_3.equals("null")) {
            Reporter.infoExec(" 添加Account外线" + ACCOUNTTRUNK); //执行操作
            m_trunks.addAccountTrunk(ACCOUNTTRUNK,"6100","6100", EXTENSION_PASSWORD);
        }
    }
//    创建呼入路由
    @Test
    public void F_addInRoute() throws InterruptedException {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_tree.click();
        inboundRoutes.inboundRoutes.click();
        deletes(" 删除所有呼入路由",inboundRoutes.grid,inboundRoutes.delete,inboundRoutes.delete_yes,inboundRoutes.grid_Mask);
        Reporter.infoExec(" 添加呼入路由InRoute1"); //执行操作
        ArrayList<String> arraytrunk1 = new ArrayList<>();
        arraytrunk1.add("all");
        m_callcontrol.addInboundRoutes("InRoute1","","",add_inbound_route.s_extensin,"1000",arraytrunk1);
    }


//    创建呼出路由
//    添加sip的呼出路由
    @Test
    public void G_addOutRoute1() throws InterruptedException {
        outboundRoutes.outboundRoutes.click();
        deletes(" 删除所有呼出路由",outboundRoutes.grid,outboundRoutes.delete,outboundRoutes.delete_yes,outboundRoutes.grid_Mask);
        Reporter.infoExec(" 添加sip的呼出路由OutRoute1_sip"); //执行操作
        ArrayList<String> arrayex = new ArrayList<>();
        arrayex.add("all");
        ArrayList<String> arraytrunk = new ArrayList<>();
        arraytrunk.add(SIPTrunk);
        m_callcontrol.addOutboundRoute("OutRoute1_sip","1.","1","",arrayex,arraytrunk);
    }

    //添加IAX的呼出路由
    @Test
    public void G_addOutRoute2() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        Reporter.infoExec(" 添加iax的呼出路由OutRoute2_iax"); //执行操作
        ArrayList<String> arrayex = new ArrayList<>();
        arrayex.add("all");
        ArrayList<String> arraytrunk = new ArrayList<>();
        arraytrunk.add(IAXTrunk);
        m_callcontrol.addOutboundRoute("OutRoute2_iax","2.","1","",arrayex,arraytrunk);
    }

    //添加SPS的呼出路由
    @Test
    public void G_addOutRoute3() throws InterruptedException {
        Reporter.infoExec(" 添加sps的呼出路由OutRoute3_sps"); //执行操作
        ArrayList<String> arrayex = new ArrayList<>();
        arrayex.add("all");
        ArrayList<String> arraytrunk = new ArrayList<>();
        arraytrunk.add(SPS);
        m_callcontrol.addOutboundRoute("OutRoute3_sps","3.","1","",arrayex,arraytrunk);
    }

//添加SPX的呼出路由
    @Test
    public void G_addOutRoute4() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        Reporter.infoExec(" 添加spx的呼出路由OutRoute4_spx"); //执行操作
        ArrayList<String> arrayex = new ArrayList<>();
        arrayex.add("all");
        ArrayList<String> arraytrunk = new ArrayList<>();
        arraytrunk.add(SPX);
        m_callcontrol.addOutboundRoute("OutRoute4_spx","4.","1","",arrayex,arraytrunk);
    }

//    添加FXO呼出路由
    @Test
    public void G_addOutRoute5() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(!FXO_1.equals("null")){
            Reporter.infoExec(" 添加fxo的呼出路由OutRoute5_fxo"); //执行操作
            ArrayList<String> arrayex = new ArrayList<>();
            arrayex.add("all");
            ArrayList<String> arraytrunk = new ArrayList<>();
            arraytrunk.add(FXO_1);
            m_callcontrol.addOutboundRoute("OutRoute5_fxo","5.","1","",arrayex,arraytrunk);
        }
    }

//添加BRI的呼出路由
    @Test
    public void G_addOutRoute6() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(!BRI_1.equals("null")){
            Reporter.infoExec(" 添加bri的呼出路由OutRoute6_bri"); //执行操作
            ArrayList<String> arrayex = new ArrayList<>();
            arrayex.add("all");
            ArrayList<String> arraytrunk = new ArrayList<>();
            arraytrunk.add(BRI_1);
            m_callcontrol.addOutboundRoute("OutRoute6_bri","6.","1","",arrayex,arraytrunk);
        }
    }

//    添加E1的呼出路由
    @Test
    public void G_addOutRoute7() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(!E1.equals("null")){
            Reporter.infoExec(" 添加E1的呼出路由OutRoute7_e1"); //执行操作
            ArrayList<String> arrayex = new ArrayList<>();
            arrayex.add("all");
            ArrayList<String> arraytrunk = new ArrayList<>();
            arraytrunk.add(E1);
            m_callcontrol.addOutboundRoute("OutRoute7_e1","7.","1","",arrayex,arraytrunk);
        }
    }

//    添加GSM的呼出路由
    @Test
    public void G_addOutRoute8() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX) || PRODUCT.equals(PC)){
            return;
        }
        if(!GSM.equals("null")){
            Reporter.infoExec(" 添加GSM的呼出路由OutRoute8_gsm"); //执行操作
            ArrayList<String> arrayex = new ArrayList<>();
            arrayex.add("all");
            ArrayList<String> arraytrunk = new ArrayList<>();
            arraytrunk.add(GSM);
            m_callcontrol.addOutboundRoute("OutRoute8_gsm","8.","1","",arrayex,arraytrunk);
        }
    }

//    添加IVR1：6500,按1到分机1000
    @Test
    public void H_addivr() throws InterruptedException {
        settings.callFeatures_tree.click();
        ivr.IVR.click();
        deletes(" 删除所有IVR",ivr.grid,ivr.delete,ivr.delete_yes,ivr.grid_Mask);
        Reporter.infoExec(" 添加IVR1：6500,按1到分机1000"); //执行操作
        m_callFeature.addIVR("IVR1","6500");
        Reporter.infoExec(" 编辑IVR1:按1到分机1000"); //执行操作
        gridClick(ivr.grid,Integer.parseInt(String.valueOf(gridLineNum(ivr.grid))),ivr.gridEdit);
        comboboxSelect(add_ivr_keyPressEvent.s_press1,add_ivr_keyPressEvent.s_extensin);
        comboboxSet(add_ivr_keyPressEvent.d_press1,extensionList,"1000");
        add_ivr_keyPressEvent.save.click();
    }

//    添加RingGroup1：6200，选择分机1000,1001,1105，其它默认
    @Test
    public void I_addringgroup() throws InterruptedException {
//        settings.callFeatures_tree.click();
        ringGroup.ringGroup.click();
        deletes(" 删除所有RingGroup",ringGroup.grid,ringGroup.delete,ringGroup.delete_yes,ringGroup.grid_Mask);
        Reporter.infoExec(" 添加RingGroup1：6200，选择分机1000,1100,1105，其它默认"); //执行操作
        m_callFeature.addRingGroup("RingGroup1","6200",add_ring_group.rs_ringall,1000,1100,1105);
    }

//    添加Queue1：6700，选择分机1000、1001、1105，其它默认
    @Test
    public void J_addqueue() throws InterruptedException {
//        settings.callFeatures_tree.click();
        queue.queue.click();
        deletes(" 删除所有Queue",queue.grid,queue.delete,queue.delete_yes,queue.grid_Mask);
        Reporter.infoExec(" 添加Queue1：6700，选择分机1000、1100、1105，其它默认 "); //执行操作
        m_callFeature.addQueue("Queue1","6700",1000,1100,1105);
    }

//    添加Conference1：6400
    @Test
    public void K_addconference() throws InterruptedException {
//        settings.callFeatures_tree.click();
        conference.conference.click();
        deletes(" 删除所有Conference",conference.grid,conference.delete,conference.delete_yes,conference.grid_Mask);
        Reporter.infoExec(" 添加Conference1:6400"); //执行操作
        m_callFeature.addConference("6400","Conference1");
        closeSetting();
        ys_apply();
    }

//    设置全局录音存储
    @Test
    public void L_setRecord() throws InterruptedException {
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.storage_panel.click();
        preference.preference.click();
        ys_waitingLoading(preference.grid_Mask);
        if (!NETWORK_DEVICE_NAME.equals("null")){
            int rows=Integer.parseInt(String.valueOf(gridLineNum(preference.grid)));
            int row =gridFindRowByColumn(preference.grid,preference.gridColumn_Name,NETWORK_DEVICE_NAME,sort_ascendingOrder);
            System.out.println("rows:"+rows);
            System.out.println("row:"+row);
            if (row > rows) {
                Reporter.infoExec(" 添加网络磁盘"+NETWORK_DEVICE_NAME); //执行操作
                m_storage.AddNetworkDrive(NETWORK_DEVICE_NAME,NETWORK_DEVICE_IP,NETWORK_DEVICE_SHARE_NAME,NETWORK_DEVICE_USER_NAME,NETWORK_DEVICE_USER_PASSWORD);
            }
        }
        String value = "null";
        if (!DEVICE_RECORD_NAME.equals("null")){
            Reporter.infoExec(" 设置录音存储在："+DEVICE_RECORD_NAME);
            if(DEVICE_RECORD_NAME.equals("SD") || DEVICE_RECORD_NAME.equals("TF") || DEVICE_RECORD_NAME.equals("TF/SD")) {
                value = "tf/sd-1";
            }else if (DEVICE_RECORD_NAME.equals("HD") || DEVICE_RECORD_NAME.equals("hd") ){
                value ="hd-1";
            }else if (DEVICE_RECORD_NAME.equals("USB") || DEVICE_RECORD_NAME.equals("usb")){
                value="usb-1";
            }else if (DEVICE_RECORD_NAME.equals("Local") || DEVICE_RECORD_NAME.equals("local")){
                value="local-1";
            }else{
                value= DEVICE_RECORD_NAME;
            }
            comboboxSelect(preference.recordings,value);
            if (preference.storage_yes.isDisplayed()){
                preference.storage_yes.click();
            }
            preference.save.click();
        }
        preference.recordingSettings.click();
        ys_waitingMask();
        Reporter.infoExec(" 选择全部外线、分机、会议室进行录音");
        ArrayList<String> arrayex = new ArrayList<>();
        arrayex.add("all");
        ArrayList<String> arraytrunk = new ArrayList<>();
        arraytrunk.add("all");
        ArrayList<String> arraycon = new ArrayList<>();
        arraycon.add("all");
        m_storage.selectRecord(arraytrunk,arrayex,arraycon);
//        ys_apply();
        closeSetting();
    }

    @Test
    public void M_UserPermission() throws InterruptedException {
        Reporter.infoExec(" 添加分机1105具有管理员权限"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.userPermission_panel.click();
        ys_waitingTime(5000);
        m_userPermission.addUserPermission(1105,grant_privilege_settings.privilegeAs_Administrator);
        if(!PRODUCT.equals(CLOUD_PBX)) {
            settings.security_tree.click();
            service.service.click();
            ys_waitingTime(5000);
            setCheckBox(service.enableAMI, true);
            ys_waitingTime(5000);
            service.enableAMI_Name.setValue("admin");
            service.enableAMI_Password.setValue("password");
            service.enableAMI_Permitted.setValue("0.0.0.0");
            service.enableAMI_Subnet.setValue("0.0.0.0");
            service.save.click();
            ys_waitingMask();
            ys_apply();
        }
        closeSetting();
    }
    //    添加时间条件
    @Test
    public void N_addTimeCondition1() throws InterruptedException {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_panel.click();
        time_Conditions.timeConditions.click();
        Reporter.infoExec(" 删除所有时间条件");
        deletes("删除所有时间条件",timeConditions.grid,timeConditions.delete,timeConditions.delete_yes,timeConditions.grid_Mask);

        Reporter.infoExec(" 添加时间条件workday_24hour:每天24小时都是工作时间"); //执行操作
        m_callcontrol.addTimeContion("workday_24hour","00:00","23:59",false,"all");
        Reporter.infoExec(" 添加时间条件workday_test：每天05:05~22:39"); //执行操作
        m_callcontrol.addTimeContion("workday_test","05:05","22:39",false,"all");
        Reporter.infoExec(" 添加时间条件Outbound：00:00-00:01,周日");
        m_callcontrol.addTimeContion("Outbound","00:00","00:01",false,"sun");
    }

    @Test
    public void N_addTimeCondition2() throws InterruptedException {
        Reporter.infoExec(" 添加时间条件workday_Advanced:周日/二/四，12:30~18:45"); //执行操作
        m_callcontrol.addTimeContion("workday_Advanced","12:30","18:45",false,"sun","tue","thu");
        Reporter.infoExec(" 编辑workday_Advanced，启用高级设置，1/6/12月的1/10/20/31号");
        gridClick(timeConditions.grid,Integer.parseInt(String.valueOf(gridFindRowByColumn(timeConditions.grid,timeConditions.gridcolumn_Name,"workday_Advanced",sort_ascendingOrder))),timeConditions.gridEdit);
        add_time_condition.advancedOptions.click();
        add_time_condition.january.click();
        add_time_condition.june.click();
        add_time_condition.december.click();
        add_time_condition.day1.click();
        add_time_condition.day10.click();
        add_time_condition.day20.click();
        add_time_condition.day31.click();
        add_time_condition.save.click();
        int row = gridFindRowByColumn(timeConditions.grid,timeConditions.gridcolumn_Name,"workday_Advanced",sort_ascendingOrder);
        YsAssert.assertEquals(gridContent(timeConditions.grid,row,timeConditions.gridcolumn_Month),"Jan,Jun,Dec","月份编辑");
        YsAssert.assertEquals(gridContent(timeConditions.grid,row,timeConditions.gridcolumn_Day),"1,10,20,31","日期编辑");
    }
    @Test
    public void N_addTimeCondition3() throws InterruptedException {
        Reporter.infoExec(" 添加时间条件CheckAll"); //执行操作
        m_callcontrol.addTimeContion("CheckAll","00:00","23:59",false,"all");
        Reporter.infoExec(" 编辑CheckAl，启用高级设置，Month:All，Day:All");
        gridClick(timeConditions.grid,Integer.parseInt(String.valueOf(gridFindRowByColumn(timeConditions.grid,timeConditions.gridcolumn_Name,"CheckAll",sort_ascendingOrder))),timeConditions.gridEdit);
        add_time_condition.advancedOptions.click();
        add_time_condition.all_month.click();
        add_time_condition.all_day.click();
        add_time_condition.save.click();
        int row = gridFindRowByColumn(timeConditions.grid,timeConditions.gridcolumn_Name,"CheckAll",sort_ascendingOrder);
        YsAssert.assertEquals(gridContent(timeConditions.grid,row,timeConditions.gridcolumn_Dayofweek),"All","Week编辑页面显示检查");
        YsAssert.assertEquals(gridContent(timeConditions.grid,row,timeConditions.gridcolumn_Month),"All","Month编辑页面显示检查");
        YsAssert.assertEquals(gridContent(timeConditions.grid,row,timeConditions.gridcolumn_Day),"All","Day编辑页面显示检查");
    }

    //    测试删除按钮正常使用
    @Test
    public void N_addTimeCondition4() {
//        settings.callControl_panel.click();
//        time_Conditions.timeConditions.click();
        Reporter.infoExec(" 表格删除：workday_Advanced，取消删除"); //执行操作
        int rows = Integer.parseInt(String.valueOf(gridLineNum(timeConditions.grid)));
        gridClick(timeConditions.grid,Integer.parseInt(String.valueOf(gridFindRowByColumn(timeConditions.grid,timeConditions.gridcolumn_Name,"workday_Advanced",sort_ascendingOrder))),timeConditions.gridDelete);
        timeConditions.delete_no.click();
        ys_waitingLoading(timeConditions.grid_Mask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(timeConditions.grid)));
        YsAssert.assertEquals(row1,rows,"表格删除：workday_Advanced，取消删除");

        Reporter.infoExec(" 表格删除：workday_Advanced，确定删除"); //执行操作
        gridClick(timeConditions.grid,Integer.parseInt(String.valueOf(gridFindRowByColumn(timeConditions.grid,timeConditions.gridcolumn_Name,"workday_Advanced",sort_ascendingOrder))),timeConditions.gridDelete);
        timeConditions.delete_yes.click();
        ys_waitingLoading(timeConditions.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(timeConditions.grid)));
        System.out.println(row2);
        int row3=row2+1;
        YsAssert.assertEquals(row3,rows,"表格删除：workday_Advanced，确定删除");

        Reporter.infoExec(" 删除：workday_test，取消删除"); //执行操作
        int xx=Integer.parseInt(String.valueOf(gridFindRowByColumn(timeConditions.grid,timeConditions.gridcolumn_Name,"workday_test",sort_ascendingOrder)));
        System.out.println(xx);
        gridCheck(timeConditions.grid,Integer.parseInt(String.valueOf(gridFindRowByColumn(timeConditions.grid,timeConditions.gridcolumn_Name,"workday_test",sort_ascendingOrder))),timeConditions.gridcolumn_Check);

        timeConditions.delete.click();
        timeConditions.delete_no.click();
        ys_waitingLoading(timeConditions.grid_Mask);
        int row4 =Integer.parseInt(String.valueOf(gridLineNum(timeConditions.grid)));
        YsAssert.assertEquals(row4,row2,"删除：workday_test，取消删除");

        Reporter.infoExec(" 删除：workday_test，确定删除"); //执行操作
//        gridCheck(timeConditions.grid,Integer.parseInt(String.valueOf(gridFindRowByColumn(timeConditions.grid,timeConditions.gridcolumn_Name,"workday_test",sort_ascendingOrder))),timeConditions.gridcolumn_Check);
        timeConditions.delete.click();
        timeConditions.delete_yes.click();
        ys_waitingLoading(timeConditions.grid_Mask);
        int row5 = Integer.parseInt(String.valueOf(gridLineNum(timeConditions.grid)));
        int row6=row5+1;
        YsAssert.assertEquals(row6,row2,"表格删除：workday_Advanced，确定删除");
        closeSetting();
    }
    //    添加Holiday
    @Test
    public void O_addHoliday1() throws InterruptedException {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_panel.click();
        time_Conditions.timeConditions.click();
        holiday.holiday.click();
        Reporter.infoExec(" 删除所有Holiday"); //执行操作
        deletes("删除所有Holiday",holiday.grid,holiday.delete,holiday.delete_yes,holiday.grid_Mask);

        Reporter.infoExec(" 新建HolidayByDay：2017-10-01~2025-12-31"); //执行操作
        m_callcontrol.addHolidayByDay("HolidayByDay","2017-10-01","2025-12-31");
        Reporter.infoExec(" 新建HolidayByMonth：1月1号-12月31号"); //执行操作
        m_callcontrol.addHolidayByMonth("HolidayByMonth",add_holiday.january,"1",add_holiday.december,"31");
        Reporter.infoExec(" 新建HolidayByWeek：5月第3个周四"); //执行操作
        m_callcontrol.addHolidayByWeek("HolidayByWeek",add_holiday.may,add_holiday.third,add_holiday.thursday);
        Reporter.infoExec(" 新建HolidayByWeek2：12月最后1个周日"); //执行操作
        m_callcontrol.addHolidayByWeek("HolidayByWeek2",add_holiday.december,add_holiday.last,add_holiday.sunday);

    }

    //    编辑Holiday
    @Test
    public void P_editHoliday() throws InterruptedException {
        Reporter.infoExec(" 编辑HolidayByWeek：8月第4个周六"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(holiday.grid,holiday.gridcolumn_Name,"HolidayByWeek",sort_ascendingOrder)));
        gridClick(holiday.grid,row,holiday.gridEdit);
        comboboxSelect(add_holiday.weekMonth,add_holiday.august);
        comboboxSelect(add_holiday.weeknum,add_holiday.fourth);
        comboboxSelect(add_holiday.weekday,add_holiday.saturday);
        add_holiday.save.click();
        YsAssert.assertEquals(gridContent(holiday.grid,row,holiday.gridcolumn_Date),"4th Sat in Aug","编辑Holiday");
    }

    //    删除Holiday
    @Test
    public void Q_deleteHoliday() throws InterruptedException {
        Reporter.infoExec(" 表格删除：HolidayByWeek-取消删除"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(holiday.grid,holiday.gridcolumn_Name,"HolidayByWeek",sort_ascendingOrder)));
        int rows=Integer.parseInt(String.valueOf(gridLineNum(holiday.grid)));
        gridClick(holiday.grid,row,holiday.gridDelete);
        holiday.delete_no.click();
        ys_waitingLoading(holiday.grid_Mask);
        int row1 =Integer.parseInt(String.valueOf(gridLineNum(holiday.grid)));
        System.out.println("row1:"+row1);
        YsAssert.assertEquals(rows,row1,"表格删除：HolidayByWeek-取消删除");

        Reporter.infoExec(" 表格删除：HolidayByWeek-确定删除"); //执行操作
        gridClick(holiday.grid,row,holiday.gridDelete);
        holiday.delete_yes.click();
        ys_waitingLoading(holiday.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(holiday.grid)));
        System.out.println("row2:"+row2);
        int row3=row2+1;
        System.out.println("row3:"+row3);
        YsAssert.assertEquals(row3,row1,"表格删除：HolidayByWeek-确定删除");

        Reporter.infoExec(" 删除：HolidayByWeek2-取消删除"); //执行操作
        int row4 = Integer.parseInt(String.valueOf(gridFindRowByColumn(holiday.grid,holiday.gridcolumn_Name,"HolidayByWeek2",sort_ascendingOrder)));
        gridCheck(holiday.grid,row4,holiday.gridcolumn_Check);
        holiday.delete.click();
        holiday.delete_no.click();
        ys_waitingLoading(holiday.grid_Mask);
        int row5 = Integer.parseInt(String.valueOf(gridLineNum(holiday.grid)));
        System.out.println("row5:"+row5);
        YsAssert.assertEquals(row2,row5,"删除：HolidayByWeek2-取消删除");

        Reporter.infoExec(" 删除：HolidayByWeek2-确定删除"); //执行操作
//        gridCheck(holiday.grid,Integer.parseInt(String.valueOf(gridFindRowByColumn(holiday.grid,holiday.gridcolumn_Name,"HolidayByWeek2",sort_ascendingOrder))),holiday.gridcolumn_Check);
        holiday.delete.click();
        holiday.delete_yes.click();
        ys_waitingLoading(holiday.grid_Mask);
        int row6 = Integer.parseInt(String.valueOf(gridLineNum(holiday.grid)));
        System.out.println("row6:"+row6);
        int row7=row6+1;
        System.out.println("row7:"+row7);
        YsAssert.assertEquals(row5,row7,"删除：HolidayByWeek2-确定删除");

    }

    @Test
    public void S0_customPromptsInit(){

//        被测设备注册分机1000
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,1);
        pjsip.Pj_Register_Account(1000,DEVICE_IP_LAN);

        closeSetting();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.voicePrompts_panel.click();
        if(!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes")){
            ys_waitingMask();
        }else{
            ys_waitingTime(5000);
        }
        customPrompts.customPrompts.click();
        ys_waitingLoading(customPrompts.grid_Mask);
        customPrompts.recordNew.shouldBe(Condition.exist);
        deletes(" 删除所有提示音",customPrompts.grid,customPrompts.delete,customPrompts.delete_yes,customPrompts.grid_Mask);
    }
    //    录制
    @Test
    public void S1_record() throws InterruptedException {
        Reporter.infoExec(" 分机1000录制提示音prompt1"); //执行操作
        customPrompts.recordNew.click();
        record_new_prompt.name.setValue("prompt1");
        comboboxSet(record_new_prompt.recordExtension, extensionList, "1000");
        record_new_prompt.record.click();
        getExtensionStatus(1000, RING, 10);
        pjsip.Pj_Answer_Call(1000, true);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        if (!PRODUCT.equals(CLOUD_PBX) && Integer.valueOf(version[1]) <= 8) {
            m_extension.checkCDR("1000 <RecordFile>", "1000", "Answered", "", "", communication_internal);
        }else{
            m_extension.checkCDR("RecordFile", "1000", "Answered", "", "", communication_internal);
        }

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        customPrompts.refresh.click();
        YsAssert.assertEquals(gridContent(customPrompts.grid, 1, customPrompts.gridcolumn_Name), "prompt1", "生成自定义提示音");
    }

    //    重新录制
    @Test
    public void S2_reRecord() throws InterruptedException {
        Reporter.infoExec(" 分机1000录制提示音prompt1"); //执行操作
        gridClick(customPrompts.grid,1,customPrompts.gridRecord);
        comboboxSet(customPrompts.playToExtension,extensionList,"1000");
        customPrompts.record_play.click();
        getExtensionStatus(1000,RING,10);
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        if (!PRODUCT.equals(CLOUD_PBX) && Integer.valueOf(version[1]) <= 8) {
            m_extension.checkCDR("1000 <RecordFile>","1000","Answered","","",communication_internal);
        }else{
            m_extension.checkCDR("RecordFile","1000","Answered","","",communication_internal);
        }
    }

    @Test
    public void S3_deleteAllRecord(){
        deletes(" 删除所有提示音",customPrompts.grid,customPrompts.delete,customPrompts.delete_yes,customPrompts.grid_Mask);
        ys_apply();
    }

    @Description("上传自定义提示音")
    @Test
    public void S4_upload_prompt1() {
        if (Platform.getCurrent().equals(Platform.LINUX)) {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.settingShortcut.click();
            settings.voicePrompts_panel.click();
            customPrompts.customPrompts.click();
            customPrompts.upload.click();
            String PROMPT1_PATH = "c:"+ File.separator+"fakepath"+File.separator+"prompt1.wav";
            executeJs("Ext.getCmp('st-cp-choosefile').setRawValue('c:\\\\fakepath\\\\prompt1.wav')");

            ys_waitingTime(2000);
            upload_a_prompt.upload.click();

        }else{
            Reporter.infoExec(" 上传提示音prompt1"); //执行操作
            customPrompts.upload.click();
            upload_a_prompt.broese.click();
            ys_waitingTime(2000);
            importFile(EXPORT_PATH +"prompt1.wav");
            ys_waitingTime(2000);
            upload_a_prompt.upload.click();
            ys_waitingTime(2000);
        }
    }
    //    播放
    @Test
    public void S5_play() throws InterruptedException {
        Reporter.infoExec(" 分机1000播放提示音prompt1"); //执行操作
        gridClick(customPrompts.grid,1,customPrompts.gridPlay);
        comboboxSet(customPrompts.playToExtension,extensionList,"1000");
        customPrompts.play.click();
        getExtensionStatus(1000,RING,10);
        pjsip.Pj_Answer_Call(1000,200,true);
        ys_waitingTime(10000);
        pjsip.Pj_Hangup_All();
        if (!PRODUCT.equals(CLOUD_PBX) && Integer.valueOf(version[1]) <= 8) {
            m_extension.checkCDR("play_file","1000 <1000>","Answered","","",communication_internal);
        }else{
            m_extension.checkCDR("play_file","1000","Answered","","",communication_internal);
        }
    }

    //    上传
    @Description("上传提示音autotestprompt")
    @Test(enabled = false)
    public void S6_upload_autotestprompt() throws InterruptedException {
        if (Platform.getCurrent().equals(Platform.LINUX)) {
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.settingShortcut.click();
            settings.voicePrompts_panel.click();
            customPrompts.customPrompts.click();
            customPrompts.upload.click();
            executeJs("Ext.getCmp('st-cp-choosefile').setRawValue('/home/seluser/exportFile/autotestprompt.wav')");

            ys_waitingTime(2000);
            upload_a_prompt.upload.click();
        }else{
            Reporter.infoExec(" 上传提示音autotestprompt"); //执行操作
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.settingShortcut.click();
            customPrompts.recordNew.shouldBe(Condition.exist);
            log.info("[Platform] "+Platform.getCurrent());

            customPrompts.upload.click();
            upload_a_prompt.broese.click();
            ys_waitingTime(2000);
            importFile(EXPORT_PATH + "autotestprompt.wav");
            ys_waitingTime(2000);
            upload_a_prompt.upload.click();
            ys_waitingTime(2000);
            YsAssert.assertEquals(String.valueOf(gridLineNum(customPrompts.grid)), "2", "导入提示音autotestprompt");
        }
    }

    @Test
    public void S7_setSystemTime(){
        closeSetting();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.dateTime_panel.click();
        ys_waitingTime(2000);
        executeJs("Ext.getCmp('"+dateTime.timeZone +"').setValue('"+dateTime.chinaTime+"')");
        if (!PRODUCT.equals(CLOUD_PBX)) {
            dateTime.synchronizeWithNTPServer.click();
        }
        dateTime.save.click();
        ys_waitingTime(5000);
        if (pageDeskTop.reboot_Yes.isDisplayed()) {
            System.out.println("datetime go to reboot ");
            pageDeskTop.reboot_Yes.click();
            System.out.println("datetime wait reboot ");
            waitReboot();
        } else if (pageDeskTop.loginout_OK.isDisplayed()) {
            System.out.println("datetime go to logout ");
            pageDeskTop.loginout_OK.click();
            System.out.println("datetime wait logout ");
            ys_waitingTime(10000);
        } else {
            ys_waitingTime(10000);
        }
        login(LOGIN_USERNAME, LOGIN_PASSWORD);
        refresh();
        if(!PRODUCT.equals(CLOUD_PBX) && Integer.valueOf(VERSION_SPLIT[1]) <= 9){
            ys_waitingMask();
            mySettings.close.click();
        }
    }

//    删除--后续IVR等需要用到提示音，暂不删除
//    @Test
    public void  Z_backup(){
        //
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.extensions_panel.click();
        extensions.add.click();
        ys_waitingMask();
        closeSetting();
        backupEnviroment("Regression_"+this.getClass().getSimpleName()+ "_"+DataUtils.getCurrentTime());
    }

    @AfterMethod
    public void AfterMethod(){
        if (pbxMonitor.trunks.isDisplayed()){
            System.out.println("admin角色的PBXMonitor页面未关闭");
            closePbxMonitor();
            System.out.println("admin角色的PBXMonitor页面已关闭");
        }
        if(cdRandRecordings.deleteCDR.isDisplayed()){
            closeCDRRecord();
        }
    }
    @AfterClass
    public void AfterClass() throws InterruptedException {
        sleep(5000);
        Reporter.infoAfterClass("执行完毕：======前置环境设置—BeforeTest======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        killChromePid();
        sleep(5000);

    }
}
