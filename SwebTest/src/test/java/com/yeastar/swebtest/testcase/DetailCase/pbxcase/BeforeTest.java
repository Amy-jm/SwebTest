package com.yeastar.swebtest.testcase.DetailCase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.pobject.Maintenance.Reboot.Reboot;
import com.yeastar.swebtest.pobject.Settings.PBX.General.SIP.Codec.Codec;
import com.yeastar.swebtest.tools.ScreenShot;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import com.yeastar.untils.AllureReporterListener;
import com.yeastar.untils.RetryListener;
import com.yeastar.untils.TestNGListener;
import org.testng.annotations.*;
import java.util.ArrayList;

/**
 * Created by xlq on 2017/9/26.
 * 功能：执行PBXcore测试的前置设置
 */
@Listeners({AllureReporterListener.class,
            RetryListener.class,
            TestNGListener.class})
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
    public void A0_init(){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.general_panel.click();
        m_general.setExPreferencesDefault();
    }
//    创建分机1000、1100~1105
    @Test
    public void A1_addExtension()  {
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
            m_extension.addFxsExtension(1106, "Yeastar202Yeastar202", FXS_1);
        }
    }

    @Test
    public void A2_editExtenName1()  {
//        extensions.Extensions.click();
        Reporter.infoExec(" 编辑分机1103的名字为xlq"); //执行操作
        gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Extensions,"1103",sort_ascendingOrder),extensions.gridEdit);
        ys_waitingMask();
        addExtensionBasic.name.setValue("xlq");
        addExtensionBasic.save.click();
        ys_waitingLoading(extensions.grid_Mask);

    }

    @Test
    public void A3_editExtenName2()  {
        Reporter.infoExec(" 编辑分机1104的名字为xll"); //执行操作
        gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Extensions,"1104",sort_ascendingOrder),extensions.gridEdit);
        ys_waitingMask();
        addExtensionBasic.name.setValue("xll");
        addExtensionBasic.save.click();
        ys_waitingLoading(extensions.grid_Mask);

    }
    //    编辑分机1000的邮箱为1000@yeastar.com
    @Test
    public void A3_editExtenEmail1()  {
        Reporter.infoExec(" 编辑分机1000的邮箱为1000@yeastar.com"); //执行操作
        gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Extensions,"1000",sort_ascendingOrder),extensions.gridEdit);
        ys_waitingMask();
        addExtensionBasic.email.setValue("1000@yeastar.com");
        addExtensionBasic.save.click();
        ys_waitingLoading(extensions.grid_Mask);
    }

//    编辑分机1105的邮箱为autotest@yeastar.com
    @Test
    public void A3_editExtenEmail2()  {
        Reporter.infoExec(" 编辑分机1105的邮箱为autotest@yeastar.com"); //执行操作
        gridClick(extensions.grid,gridFindRowByColumn(extensions.grid,extensions.gridcolumn_Extensions,"1105",sort_ascendingOrder),extensions.gridEdit);
        ys_waitingMask();
        addExtensionBasic.email.setValue("autotest@yeastar.com");
        addExtensionBasic.save.click();
        ys_waitingLoading(extensions.grid_Mask);

    }


    //  创建分机组
    @Test
    public void A4_addExtensionGroup()  {
        extensionGroup.extensionGroup.click();
        deletes(" 删除所有分机组",extensionGroup.grid,extensionGroup.delete,extensionGroup.delete_yes,extensionGroup.grid_Mask);
        Reporter.infoExec(" 添加分机组：ExtensionGroup1:1000,1100,1101,1105"); //执行操作
        m_extension.addExtensionGroup("ExtensionGroup1",1000,1100,1101,1105);
    }

//    创建外线
    @Test
    public void B1_addtrunk()  {
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

    @Test
    public void B_addtrunk()  {
        Reporter.infoExec(" 添加sip2外线"+SIPTrunk2); //执行操作
        m_trunks.addTrunk("SIP",add_voIP_trunk_basic.VoipTrunk,SIPTrunk2,DEVICE_ASSIST_1,String.valueOf(UDP_PORT_ASSIST_1),DEVICE_ASSIST_1,"3002","3002","3002",EXTENSION_PASSWORD,"3002");
    }

    @Test
    public void C1_addtrunk() {
        if (!DEVICE_ASSIST_3.equals("null")) {
            Reporter.infoExec(" 添加Account外线" + ACCOUNTTRUNK); //执行操作
            m_trunks.addAccountTrunk(ACCOUNTTRUNK,"6100","6100", EXTENSION_PASSWORD);
        }
    }

    @Test
    public void C_addtrunk()  {
        Reporter.infoExec(" 添加iax外线"+IAXTrunk);
        m_trunks.addTrunk("IAX",add_voIP_trunk_basic.VoipTrunk,IAXTrunk,DEVICE_ASSIST_1,"4569","","3100","","",EXTENSION_PASSWORD,"");
    }
    @Test
    public void D_addtrunk()  {
        Reporter.infoExec(" 添加sps外线"+SPS);
        m_trunks.addTrunk("SIP",add_voIP_trunk_basic.PeerToPeer,SPS,DEVICE_ASSIST_2,String.valueOf(UDP_PORT_ASSIST_2),DEVICE_ASSIST_2,
                "","","","","");
    }
    @Test
    public void E_addtrunk()  {
        Reporter.infoExec(" 添加spx外线"+SPX);
        m_trunks.addTrunk("IAX",add_voIP_trunk_basic.PeerToPeer,SPX,DEVICE_ASSIST_2,"4569",DEVICE_ASSIST_2,
                "","","","","");
    }

//    创建呼入路由
    @Test
    public void F_addInRoute()  {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();

//         settings.callControl_panel.click();
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
    public void G_addOutRoute1()  {
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
    public void G_addOutRoute2()  {
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
    public void G_addOutRoute3()  {
        Reporter.infoExec(" 添加sps的呼出路由OutRoute3_sps"); //执行操作
        ArrayList<String> arrayex = new ArrayList<>();
        arrayex.add("all");
        ArrayList<String> arraytrunk = new ArrayList<>();
        arraytrunk.add(SPS);
        m_callcontrol.addOutboundRoute("OutRoute3_sps","3.","1","",arrayex,arraytrunk);
    }

//添加SPX的呼出路由
    @Test
    public void G_addOutRoute4()  {
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
    public void G_addOutRoute5()  {
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
    public void G_addOutRoute6()  {
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
    public void G_addOutRoute7()  {
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
    public void G_addOutRoute8()  {
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

//    添加Account的呼出路由
    @Test
    public void G_addOutRoute9() {
        if (!DEVICE_ASSIST_3.equals("null")) {
            Reporter.infoExec(" 添加account的呼出路由OutRoute9_account"); //执行操作
            ArrayList<String> arrayex = new ArrayList<>();
            arrayex.add("all");
            ArrayList<String> arraytrunk = new ArrayList<>();
            arraytrunk.add(ACCOUNTTRUNK);
            m_callcontrol.addOutboundRoute("OutRoute9_account", "91.", "2", "", arrayex, arraytrunk);
        }
    }

//    添加IVR1：6500,按1到分机1000
    @Test
    public void H_addivr()  {
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
    public void I_addringgroup()  {
//        settings.callFeatures_tree.click();
        ringGroup.ringGroup.click();
        deletes(" 删除所有RingGroup",ringGroup.grid,ringGroup.delete,ringGroup.delete_yes,ringGroup.grid_Mask);
        Reporter.infoExec(" 添加RingGroup1：6200，选择分机1000,1100,1105，其它默认"); //执行操作
        m_callFeature.addRingGroup("RingGroup1","6200",add_ring_group.rs_ringall,1000,1100,1105);
    }

//    添加Queue1：6700，选择分机1000、1001、1105，其它默认
    @Test
    public void J_addqueue()  {
//        settings.callFeatures_tree.click();
        queue.queue.click();
        deletes(" 删除所有Queue",queue.grid,queue.delete,queue.delete_yes,queue.grid_Mask);
        Reporter.infoExec(" 添加Queue1：6700，选择分机1000、1100、1105，其它默认 "); //执行操作
        m_callFeature.addQueue("Queue1","6700",1000,1100,1105);
    }

//    添加Conference1：6400
    @Test
    public void K_addconference()  {
//        settings.callFeatures_tree.click();
        conference.conference.click();
        deletes(" 删除所有Conference",conference.grid,conference.delete,conference.delete_yes,conference.grid_Mask);
        Reporter.infoExec(" 添加Conference1:6400"); //执行操作
        m_callFeature.addConference("6400","Conference1");
    }

    //    添加Callback1
    @Test
    public void L_addcallback()  {
        callFeatures.more.click();
        callback.callback.click();
        callback.add.shouldBe(Condition.exist);
        deletes(" 删除所有Callback",callback.grid,callback.delete,callback.delete_yes,callback.grid_Mask);
        Reporter.infoExec(" 创建Callback1，Destination：分机1000，其它默认");
        m_callFeature.addCallBack("Callback1",s_extensin,"1000");
    }

    //    添加DISA
    @Test
    public void M_adddisa()  {
        disa.DISA.click();
        disa.add.shouldBe(Condition.exist);
        deletes("删除所有DISA",disa.grid,disa.delete,disa.delete_yes,disa.grid_Mask);
        disa.add.click();
        ys_waitingTime(8000);
        add_disa.name.setValue("DISA1");
        listSelect(add_disa.list,nameList,"OutRoute1_sip","OutRoute3_sps");
        add_disa.save.click();
        ys_waitingLoading(disa.grid_Mask);
        String actName = String.valueOf(gridContent(disa.grid,gridFindRowByColumn(disa.grid,disa.gridcolumn_Name,"DISA1",sort_ascendingOrder),disa.gridcolumn_Name));
        YsAssert.assertEquals(actName,"DISA1");
    }

//    添加PINList
    @Test
    public void N_addPin()  {
        pinList.PINList.click();
        pinList.add.shouldBe(Condition.exist);
        deletes("删除所有PIN码",pinList.grid,pinList.delete,pinList.delete_yes,pinList.grid_Mask);

        m_callFeature.addPinList("test1","12345678");
        ys_waitingTime(1000);
        pinList.add.click();
        add_pin_list.name.setValue("test2");
        add_pin_list.PINList.setValue("123456");
        add_pin_list.save.click();

        ys_waitingLoading(pinList.grid_Mask);
        int row= gridFindRowByColumn(pinList.grid,pinList.gridcolumn_Name,"test2",sort_ascendingOrder);
        String actualName = (String) gridContent(pinList.grid,row,pinList.gridcolumn_Name);
        YsAssert.assertEquals(actualName,"test2");

        closeSetting();
        ys_apply();
    }

//    设置全局录音存储
    @Test
    public void O1_setRecord()  {
        if (PRODUCT.equals(CLOUD_PBX)) {
            return;
        }
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.storage_panel.click();
        preference.preference.click();
        ys_waitingLoading(preference.grid_Mask);
        if (!NETWORK_DEVICE_NAME.equals("null")) {
            int rows = Integer.parseInt(String.valueOf(gridLineNum(preference.grid)));
            int row = gridFindRowByColumn(preference.grid, preference.gridColumn_Name, NETWORK_DEVICE_NAME, sort_ascendingOrder);
            System.out.println("rows:" + rows);
            System.out.println("row:" + row);
            if (row > rows) {
                Reporter.infoExec(" 添加网络磁盘" + NETWORK_DEVICE_NAME); //执行操作
                m_storage.AddNetworkDrive(NETWORK_DEVICE_NAME, NETWORK_DEVICE_IP, NETWORK_DEVICE_SHARE_NAME, NETWORK_DEVICE_USER_NAME, NETWORK_DEVICE_USER_PASSWORD);
            }
        }
    }
    @Test
    public void O2_setRecord()  {
        if (PRODUCT.equals(CLOUD_PBX)) {
            return;
        }
        String value = "null";
        if (!DEVICE_RECORD_NAME.equals("null")) {
            Reporter.infoExec(" 设置录音存储在：" + DEVICE_RECORD_NAME);
            if (DEVICE_RECORD_NAME.equals("SD") || DEVICE_RECORD_NAME.equals("TF") || DEVICE_RECORD_NAME.equals("TF/SD")) {
                value = "tf/sd-1";
            } else if (DEVICE_RECORD_NAME.equals("HD") || DEVICE_RECORD_NAME.equals("hd")) {
                value = "hd-1";
            } else if (DEVICE_RECORD_NAME.equals("USB") || DEVICE_RECORD_NAME.equals("usb")) {
                value = "usb-1";
            } else if (DEVICE_RECORD_NAME.equals("Local") || DEVICE_RECORD_NAME.equals("local")) {
                value = "local-1";
            } else {
                value = DEVICE_RECORD_NAME;
            }
            comboboxSelect(preference.recordings, value);
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
        m_storage.selectRecord(arraytrunk, arrayex, arraycon);
        closeSetting();
    }
    @Test
    public void P1_setRegistrationTime(){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.general_panel.click();
        sip.SIP.click();
        general.general.click();ys_waitingTime(3000);
        general.maxRegistrationTime.clear();
        general.maxRegistrationTime.setValue("99999");
        general.minRegistrationTime.setValue("99999");
        general.save.click();
    }
    @Test
    public void P2_setCode(){
        ys_waitingTime(1000);
        codec.codec.click();
        ys_waitingTime(1000);
        executeJs("Ext.getCmp('st-sip-allow').setValue('alaw,ulaw,g729,ilbc')");
        ys_waitingTime(1000);
        general.save.click();
        ys_waitingTime(1000);
        if (codec.saveCodec_ok.isDisplayed()) {
            codec.saveCodec_ok.click();
        }
        closeSetting();
    }
    @Test
    public static void Q_setAMI(){
        if(PRODUCT.equals(CLOUD_PBX)){
            return;
        }
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.security_panel.click();
        service.service.click();
        ys_waitingTime(5000);
        setCheckBox(service.enableAMI,true);
        ys_waitingTime(5000);
        service.enableAMI_Name.setValue("admin");
        service.enableAMI_Password.setValue("password");
        service.enableAMI_Permitted.setValue("0.0.0.0");
        service.enableAMI_Subnet.setValue("0.0.0.0");
        service.save.click();
        ys_waitingMask();
        closeSetting();
    }

    @Test
    public void R_UserPermission()  {
        Reporter.infoExec(" 添加分机1105具有管理员权限"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.userPermission_panel.click();
        ys_waitingTime(5000);
        m_userPermission.addUserPermission(1105,grant_privilege_settings.privilegeAs_Administrator);
        m_userPermission.addUserPermission(1000,grant_privilege_settings.privilegeAs_Administrator);
        ys_apply();
    }

    @Test
    public void S_deleteVoicePrompts(){
        //        被测设备注册分机1000
        pjsip.Pj_CreateAccount(1000,EXTENSION_PASSWORD,"UDP",UDP_PORT,1);
        pjsip.Pj_Register_Account(1000,DEVICE_IP_LAN);

        closeSetting();
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.voicePrompts_panel.click();
        if(!PRODUCT.equals(CLOUD_PBX) && !PRODUCT.equals(PC) && LOGIN_ADMIN.equals("yes")){
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
    public void T1_reRecord() {
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
    public void T2_reRecord() {
        Reporter.infoExec(" 分机1000重新录制提示音prompt1"); //执行操作
        gridClick(customPrompts.grid,1,customPrompts.gridRecord);
        comboboxSet(customPrompts.playToExtension,extensionList,"1000");
        customPrompts.record_play.click();
        getExtensionStatus(1000,RING,10);
        pjsip.Pj_Answer_Call(1000,true);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        if (!PRODUCT.equals(CLOUD_PBX) && Integer.valueOf(version[1]) <= 8) {
            m_extension.checkCDR("1000 <RecordFile>","1000","Answered","","",communication_internal);
        }else{
            m_extension.checkCDR("RecordFile","1000","Answered","","",communication_internal);
        }
    }

    @Test
    public void T3_deleteAllRecord(){
        deletes(" 删除所有提示音",customPrompts.grid,customPrompts.delete,customPrompts.delete_yes,customPrompts.grid_Mask);
    }

    @Test
    public void T4_upload_prompt1() {
        Reporter.infoExec(" 上传提示音prompt1"); //执行操作
        customPrompts.upload.click();
        upload_a_prompt.broese.click();
        ys_waitingTime(2000);
        importFile(EXPORT_PATH +"prompt1.wav");
        ys_waitingTime(2000);
        upload_a_prompt.upload.click();
        ys_waitingTime(2000);
    }

    //    播放
    @Test
    public void T5_play() {
        Reporter.infoExec(" 分机1000播放提示音prompt1"); //执行操作
        gridClick(customPrompts.grid,1,customPrompts.gridPlay);
        comboboxSet(customPrompts.playToExtension,extensionList,"1000");
        customPrompts.play.click();
        getExtensionStatus(1000,RING,10);
        pjsip.Pj_Answer_Call(1000,200,true);
        ys_waitingTime(8000);
        pjsip.Pj_Hangup_All();
        if (!PRODUCT.equals(CLOUD_PBX) && Integer.valueOf(version[1]) <= 8) {
            m_extension.checkCDR("play_file","1000 <1000>","Answered","","",communication_internal);
        }else{
            m_extension.checkCDR("play_file","1000","Answered","","",communication_internal);
        }
    }

    @Test
    public void U1_upload_satisfaction() {
        Reporter.infoExec(" 上传提示音satisfaction"); //执行操作
        customPrompts.upload.click();
        upload_a_prompt.broese.click();
        ys_waitingTime(2000);
        importFile(EXPORT_PATH +"satisfaction.wav");
        ys_waitingTime(2000);
        upload_a_prompt.upload.click();
        ys_waitingTime(2000);
    }

    @Test
    public void U2_upload_agentid() {
        Reporter.infoExec(" 上传提示音agentid"); //执行操作
        customPrompts.upload.click();
        upload_a_prompt.broese.click();
        ys_waitingTime(2000);
        importFile(EXPORT_PATH +"agentid.wav");
        ys_waitingTime(2000);
        upload_a_prompt.upload.click();
        ys_waitingTime(2000);
    }

    @Test
    public void U3_upload_Join() {
        Reporter.infoExec(" 上传提示音Join"); //执行操作
        customPrompts.upload.click();
        upload_a_prompt.broese.click();
        ys_waitingTime(2000);
        importFile(EXPORT_PATH +"Join.wav");
        ys_waitingTime(2000);
        upload_a_prompt.upload.click();
        ys_waitingTime(2000);
    }

    @Test
    public void U4_upload_AgentAnnouncement() {
        Reporter.infoExec(" 上传提示音AgentAnnouncement"); //执行操作
        customPrompts.upload.click();
        upload_a_prompt.broese.click();
        ys_waitingTime(2000);
        importFile(EXPORT_PATH +"AgentAnnouncement.wav");
        ys_waitingTime(2000);
        upload_a_prompt.upload.click();
        ys_waitingTime(2000);
    }

    //    上传
    @Test
    public void U5_upload_autotestprompt() {
        Reporter.infoExec(" 上传提示音autotestprompt"); //执行操作
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        customPrompts.recordNew.shouldBe(Condition.exist);
        customPrompts.upload.click();
        upload_a_prompt.broese.click();
        ys_waitingTime(2000);
        importFile(EXPORT_PATH +"autotestprompt.wav");
        ys_waitingTime(2000);
        upload_a_prompt.upload.click();
        ys_waitingTime(2000);
        YsAssert.assertEquals(String.valueOf(gridLineNum(customPrompts.grid)),"6","导入提示音autotestprompt");

    }
//    删除--后续IVR等需要用到提示音，暂不删除


    //    music on hold
    @Test
    public void U6_MusicOnHold() {
        Reporter.infoExec("选择Music on Hold提示音列表的第一个提示音，点击“Play”在1000播放");
        musicOnHold.musicOnHold.click();
        ys_waitingTime(2000);
        comboboxSelect(musicOnHold.chooseMOHPlaylist_id,"default");
        if (!PRODUCT.equals(CLOUD_PBX)) {
            ys_waitingMask();
        }
        ys_waitingLoading(musicOnHold.grid_Mask);
        gridClick(musicOnHold.grid,1,musicOnHold.gridPlay);
        ys_waitingTime(3000);
        comboboxSet(musicOnHold.plauToExtension,extensionList,"1000");
        ys_waitingTime(1000);
        tcpSocket.connectToDevice();
        musicOnHold.play.click();
        boolean tcpInfo = tcpSocket.getAsteriskInfo("macroformcold_day");
        tcpSocket.closeTcpSocket();
        pjsip.Pj_Hangup_All();
        YsAssert.assertEquals(tcpInfo,true,"等待音乐播放");
    }

    @Test
    public void U7_CreateNewPlaylist() {
        Reporter.infoExec("创建一个新的Autotest");
        musicOnHold.musicOnHold.click();
        ys_waitingMask();
        ys_waitingLoading(musicOnHold.grid_Mask);
        System.out.println("nnnnnnnnn："+return_executeJs("Ext.getCmp('st-moh-choosefolder').store.getCount()"));
        if(!String.valueOf(return_executeJs("Ext.getCmp('st-moh-choosefolder').store.getCount()")).equals("1")){
            executeJs("Ext.getCmp('st-moh-choosefolder').setValue('Autotest')");
            musicOnHold.chooseMOHPlaylist_delete.click();
            if(musicOnHold.delete_yes.isDisplayed()){
                musicOnHold.delete_yes.click();
            }
        }
        musicOnHold.createNewPlaylist.click();
        m_voicePrompts.addMOHPlaylist("Autotest",add_moh_playlist.playlistOrder_Random);
        ys_apply();
        String plist =musicOnHold.chooseMOHPlaylist_input.getValue();
        String gridLine = String.valueOf(gridLineNum(musicOnHold.grid)) ;
        Reporter.infoExec("Autotest line="+gridLine+" "+plist);
//        YsAssert.assertEquals(plist,"Autotest","Autotest未自动显示");
//        YsAssert.assertEquals(gridLine,"0","Autotest新增的等待提示音列表为空");
    }

    @Test
    public void U8_UploadMusicOnHold(){
        //上传提示音为xxx
        Reporter.infoExec("新的Autotest上传音乐molihua.wav");
        executeJs("Ext.getCmp('st-moh-choosefolder').setValue('Autotest')");
        ys_waitingTime(3000);
        musicOnHold.browse.click();
        ys_waitingTime(2000);
        importFile(EXPORT_PATH +"molihua.wav");
        ys_waitingTime(2000);
        musicOnHold.upload.click();
        ys_waitingTime(5000);

        gridClick(musicOnHold.grid,1,musicOnHold.gridPlay);
        ys_waitingTime(2000);
        comboboxSet(musicOnHold.plauToExtension,extensionList,"1000");
        ys_waitingTime(1000);
        tcpSocket.connectToDevice();
        musicOnHold.play.click();
        boolean tcpInfo = tcpSocket.getAsteriskInfo("molihua");
        tcpSocket.closeTcpSocket();
        pjsip.Pj_Hangup_All();
    }

    @Test
    public void V_setSystemTime(){
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
        if(pageDeskTop.reboot_Yes.isDisplayed()){
            System.out.println("datetime go to reboot ");
            pageDeskTop.reboot_Yes.click();
            System.out.println("datetime wait reboot ");
            waitReboot();
        }else if(pageDeskTop.loginout_OK.isDisplayed()){
            System.out.println("datetime go to logout ");
            pageDeskTop.loginout_OK.click();
            System.out.println("datetime wait logout ");
            ys_waitingTime(10000);
        }else{
            ys_waitingTime(10000);
        }
        System.out.println(pageDeskTop.reboot_No.isDisplayed());
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
    }

    @Test
    public void W_backup(){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.maintanceShortcut.click();
        maintenance.backupandRestore.click();
        deletes("删除所有备份记录",backupandRestore.grid,backupandRestore.delete,backupandRestore.delete_yes,backupandRestore.grid_Mask);
        backupEnviroment(this.getClass().getSimpleName());
    }
//    AfterMethod是在每个Test执行后都要来执行的方法
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
        Reporter.infoAfterClass("执行完毕：======前置环境设置—BeforeTest======"); //执行操作
        pjsip.Pj_Destory();
        quitDriver();
        killChromePid();
    }
}
