package com.yeastar.swebtest.testcase.DetailCase.pbxcase;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.yeastar.swebtest.driver.Config;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.pobject.PageDeskTop;
import com.yeastar.swebtest.pobject.Settings.PBX.General.SIP.SIP;
import com.yeastar.swebtest.testcase.RegressionCase.pbxcase.Outbound;
import com.yeastar.swebtest.testcase.DetailCase.pbxcase.BeforeTest;
import com.yeastar.swebtest.tools.file.ExcelUnit;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import jxl.read.biff.BiffException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.page;

/**
 * Created by Caroline on 2017/11/24.
 * 注意：自动化无法实现点击new来创建pin码，所以只能提前创建好pin码来选用
 * import呼出路由部分：所有的数据都是可行的，不考虑导入失败的各种情况
 */
public class OutboundRoutes extends SwebDriver {
    //    标志SIP4线路是否存在的标志变量
    boolean isTrunkExist = false;

    @BeforeClass
    public void BeforeClass() {

        Reporter.infoBeforeClass("开始执行：====== OutboundRoutes ======"); //执行操作
        initialDriver(BROWSER,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        if(!PRODUCT.equals(CLOUD_PBX) && LOGIN_ADMIN.equals("yes") && Integer.valueOf(VERSION_SPLIT[1]) <= 9){
            ys_waitingMask();
            mySettings.close.click();
        }
        m_extension.showCDRClounm();
    }
//    @Test
    public void A0_initbeforeTest(){
        //初始化beforetest
        resetoreBeforetest("BeforeTest_Local.bak");

    }
    @Test
    public void A1_init1(){
//        如果不可用的SIP3存在的话，将标志变量设置为已存在
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.trunks_panel.click();
        setPageShowNum(trunks.grid,100);
        String []name = new String[20];
        for (int i=0;i<Integer.parseInt(String.valueOf(gridLineNum(trunks.grid)));i++){
            name[i] = String.valueOf(gridContent(trunks.grid,i+1,trunks.gridcolumn_TrunkName));
            if (name[i].equals("SIP3")){
//                如果name[i]就是要找的那条中继，将标志变量 isTrunkExist 赋值为true
                isTrunkExist = true ;
                break;
            }
        }

    }
    //    添加一条不可用的路由,最后恢复BeforeTest环境时要删除
    @Test
    public void A2_addTrunk() throws InterruptedException {
//        如果不存在SIP3，那就添加
        if (!isTrunkExist) {
            Reporter.infoExec(" 添加不可用的sip外线SIP3");
            if (!PRODUCT.equals(CLOUD_PBX)) {
                m_trunks.addUnavailTrunk("SIP", add_voIP_trunk_basic.VoipTrunk, "SIP3", DEVICE_ASSIST_1, String.valueOf(UDP_PORT_ASSIST_1), DEVICE_ASSIST_1, "1", "1", "1", "Yeastar", false,"");
            }else {
                m_trunks.addUnavailTrunk("SIP", add_voIP_trunk_basic.VoipTrunk, "SIP3", DEVICE_ASSIST_1, String.valueOf(UDP_PORT_ASSIST_1), DEVICE_ASSIST_1, "1", "1", "1", "Yeastar", false,"1");
            }
        }
    }
    @Test
    public void A3_initOutRoute() throws InterruptedException {
        settings.callControl_tree.click();
        outboundRoutes.outboundRoutes.click();
        ys_waitingTime(2000);
//        判断呼出路由是否为BeforeTest的环境，如果不是，那就删除全部呼出路由，进行重新添加
        if (Integer.parseInt(String.valueOf(gridLineNum(outboundRoutes.grid))) != 9){
            deletes("删除所有呼出路由",outboundRoutes.grid,outboundRoutes.delete,outboundRoutes.delete_yes,outboundRoutes.grid_Mask);
            BeforeTest recovery = new BeforeTest();
            recovery.G_addOutRoute1();
            recovery.G_addOutRoute2();
            recovery.G_addOutRoute3();
            recovery.G_addOutRoute4();
            recovery.G_addOutRoute5();
            recovery.G_addOutRoute6();
            recovery.G_addOutRoute7();
            recovery.G_addOutRoute8();
            recovery.G_addOutRoute9();
            ys_apply();
        }
    }
//    创建注册分机
    @Test
    public void B_addExtensions() {
        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_Init();
        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, 1);
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_CreateAccount(1100, EXTENSION_PASSWORD, "UDP", UDP_PORT, 2);
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1103"); //执行操作
        pjsip.Pj_CreateAccount(1103, EXTENSION_PASSWORD, "UDP", UDP_PORT, 5);
        pjsip.Pj_Register_Account(1103, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1104"); //执行操作
        pjsip.Pj_CreateAccount(1104, EXTENSION_PASSWORD, "UDP", UDP_PORT, 6);
        pjsip.Pj_Register_Account(1104, DEVICE_IP_LAN);

        Reporter.infoExec(" 辅助设备1注册分机3001"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 3001, EXTENSION_PASSWORD, -1, DEVICE_ASSIST_1, UDP_PORT_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(3001, DEVICE_ASSIST_1);

        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 2000, EXTENSION_PASSWORD, -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备3注册分机4000"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 4000, EXTENSION_PASSWORD, -1, DEVICE_ASSIST_3, UDP_PORT_ASSIST_3);
        pjsip.Pj_Register_Account_WithoutAssist(4000, DEVICE_ASSIST_3);
        ys_waitingTime(1000);
        closePbxMonitor();
    }

    @Test
    public void C_call() throws InterruptedException {
        //        通话测试
        Outbound outbound = new Outbound();
        outbound.A_out1_sip();
        outbound.A_out2_iax();
        outbound.A_out3_sps();
        outbound.A_out4_spx();
        outbound.A_out5_fxo();
        outbound.A_out6_bri();
        outbound.A_out7_E1();
        outbound.A_out8_gsm();

//        预留测试account呼出路由
      if(!DEVICE_ASSIST_3.equals("null")) {
          Reporter.infoExec(" 1000拨打914000通过account外线呼出"); //执行操作
          pjsip.Pj_Make_Call_Auto_Answer(1000, "914000", DEVICE_IP_LAN);
          ys_waitingTime(5000);
          if (getExtensionStatus(4000, TALKING, 8) == TALKING) {
              Reporter.pass(" 分机4000状态--TALKING，通话正常建立");
          } else {
              Reporter.error(" 预期分机4000状态为TALKING，实际状态为"+getExtensionStatus(4000, TALKING, 8));
          }
          pjsip.Pj_Hangup_All();
          closePbxMonitor();
//          cloud cdr不同；Callee显示为 6100 <914000>
          m_extension.checkCDR("1000 <1000>", "914000", "Answered", " ", ACCOUNTTRUNK, communication_outRoute);
      }
    }

//  删除beforeTest所创建的呼出路由，避免之后测试呼出前缀有冲突
    @Test
    public void D_deleteOutBoundRoutes(){
//        删除beforeTest所创建的呼出路由，避免之后测试呼出前缀有冲突
        Reporter.infoExec(" 勾选全部的呼出路由"); //执行操作
        gridSeleteAll(outboundRoutes.grid);  //勾选全部
//        取消勾选OutRoute1_sip
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"OutRoute1_sip",sort_ascendingOrder)));
        gridCheck(outboundRoutes.grid,row,outboundRoutes.gridcolumn_Check);
        Reporter.infoExec(" 确定删除所有呼出路由"); //执行操作
        outboundRoutes.delete.click();
        outboundRoutes.delete_yes.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        ys_apply();
    }

//    编辑呼出路由：修改name、增删patterns、选择多条的路由、选择分机组、勾选循环抓取
    @Test
    public void E_editOutRoute() {
        Reporter.infoExec(" 编辑呼出路由OutRoute1_sip");
        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"OutRoute1_sip",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();
        ys_waitingTime(1000);
//        对patterns部分进行操作
//        定位要删除哪一行
        int patterns_column = gridFindRowByColumn(add_outbound_routes.grid,add_outbound_routes.grid_gridcolumn_patterns,"1.",sort_ascendingOrder);
//        使用gridClick来点击表格里的删除按钮
        gridClick(add_outbound_routes.grid,patterns_column,add_outbound_routes.gridDelete);
        ys_waitingTime(1000);
//        点击pattern添加按钮——新增四个patterns，所以点击四次add_patterns按钮
        add_outbound_routes.add_patterns.click();
        add_outbound_routes.add_patterns.click();
        add_outbound_routes.add_patterns.click();
        add_outbound_routes.add_patterns.click();

//        获取patterns表格总共有多少行（打算新增多少个patterns）
        long number = (long) gridLineNum(add_outbound_routes.grid);
//        patterns数据源的准备，添加了几个patterns按钮，这里就准备几组数据
        String [][]patterns_data = {{"1.","1",""},{"90.","2","17911"},{"91.","2","17912"},{"92.","2","17913"}};
//        executeJs要对哪些元素进行操作，放到patterns_data2中，防止后面循环会造成对三者元素同时操作，那就达不到是对不同元素进行不同数据的编辑
        String []patterns_data2 = {"patterns","strip","prepend"};
        for(int i=0;i<patterns_data.length;i++) {
            for (int j=0;j<patterns_data[i].length;j++) {
                executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(" + i + ").set('"+patterns_data2[j]+"','" + patterns_data[i][j] + "')");
                ys_waitingTime(1000);
            }
        }

//        定位到编辑页面中的name元素，使用setvalue来修改值
        add_outbound_routes.name.setValue("A123*_-+.~?HKtw1234567890bcdef.");
//       选择多条呼出路由，只选择一个member
        Reporter.infoExec(" 编辑呼出路由OutRoute1_sip：选择多条呼出路由"); //执行操作
//        只选择一个分机组——ExtensionGroup1（1000,1100,1101,1105）
        ArrayList<String> arrayex = new ArrayList<>();
        arrayex.add("ExtensionGroup1");    //只选择ExtensionGroup1分机组（1000,1100,1101,1105）可使用该条呼出路由
        listSelect(add_outbound_routes.list_Extension, extensionList,arrayex);
//        新增多条选择+一条不可用的SIPTrunk2路由
        ArrayList<String> arraytrunk = new ArrayList<>();
//        这边考虑一下S系列/Cloud/PC间的差别
        arraytrunk.add(SPS);
        arraytrunk.add("SIP3"); //新增一条不可用的路由
        arraytrunk.add(SIPTrunk);
        if(!DEVICE_ASSIST_3.equals("null")) {
            arraytrunk.add(ACCOUNTTRUNK);
        }
        if(PRODUCT.equals(PC)){
            arraytrunk.add(IAXTrunk);
            arraytrunk.add(SPX);
        }else if (!PRODUCT.equals(CLOUD_PBX)){
            arraytrunk.add(IAXTrunk);
            arraytrunk.add(SPX);
            if (!FXO_1.equals("null")) {
                arraytrunk.add(FXO_1);
            }
            if (!E1.equals("null")) {
                arraytrunk.add(E1);
            }
            if (!BRI_1.equals("null")) {
                arraytrunk.add(BRI_1);
            }
            if (!GSM.equals("null")) {
                arraytrunk.add(GSM);
            }
        }
        listSelect(add_outbound_routes.list_Trunk,trunkList,arraytrunk);
        ys_waitingTime(1000);
//       勾选循环抓取
        setCheckBox(add_outbound_routes.rrmemoryHunt,true);
        ys_waitingTime(1000);
//        保存编辑页面
        add_outbound_routes.save.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        ys_apply();
    }

// 验证member生效，这里验证  只对分机组生效
    @Test
    public void F_checkMember(){
        Reporter.infoExec(" 1104拨打13001，预期呼出失败");
        pjsip.Pj_Make_Call_Auto_Answer(1104,"13001",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1104,HUNGUP,8);
        if(state == HUNGUP){
            Reporter.infoExec(" 1104拨打13001，预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1104拨打13001，预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail();
        }
    }

//    验证循环抓取功能 + 验证member。这里要考虑CloudPBX、S系列和PC的区别
    @Test
    public void G_rrmemory_hunt(){

        Reporter.infoExec(" 1000拨打13001，第1次预期通过SPS外线呼出");
        pjsip.Pj_Make_Call_Auto_Answer(1100,"13001",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","13001","Answered"," ",SPS,communication_outRoute);

        Reporter.infoExec(" 1100拨打13001，第2次预期通过SIP外线呼出");
        pjsip.Pj_Make_Call_Auto_Answer(1100,"13001",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","13001","Answered"," ",SIPTrunk,communication_outRoute);

        if(!DEVICE_ASSIST_3.equals("null")) {
            Reporter.infoExec(" 1100拨打13001，第3次预期通过Account外线呼出");
            pjsip.Pj_Make_Call_Auto_Answer(1100, "14000", DEVICE_IP_LAN, false);
            ys_waitingTime(5000);
            if (getExtensionStatus(4000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机4000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机4000状态为TALKING，实际状态为"+getExtensionStatus(4000, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1100 <1100>", "14000", "Answered", " ", ACCOUNTTRUNK, communication_outRoute);
        }
//          第四次开始  考虑设备的不同   预期结果也会不同，这边针对CloudPBX和其他两者的不同
        if(!PRODUCT.equals(CLOUD_PBX)) {
            Reporter.infoExec(" 1100拨打13001，第4次预期通过IAX外线呼出");
            pjsip.Pj_Make_Call_Auto_Answer(1100, "13001", DEVICE_IP_LAN, false);
            ys_waitingTime(5000);
            if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1100 <1100>", "13001", "Answered", " ", IAXTrunk, communication_outRoute);

            Reporter.infoExec(" 1100拨打13001，第5次预期通过SPX外线呼出");
            pjsip.Pj_Make_Call_Auto_Answer(1100, "13001", DEVICE_IP_LAN, false);
            ys_waitingTime(5000);
            if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1100 <1100>", "13001", "Answered", " ", SPX, communication_outRoute);

//            第六次要考虑PC和S系列之间的区别
            if(!PRODUCT.equals(PC)){
                if (!FXO_1.equals("null")) {
                    Reporter.infoExec(" 1100拨打13001，第6次预期通过PSTN外线呼出");
                    pjsip.Pj_Make_Call_Auto_Answer(1100, "13001", DEVICE_IP_LAN, false);
                    ys_waitingTime(5000);
                    if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
                        Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
                    } else {
                        Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
                    }
                    pjsip.Pj_Hangup_All();
                    m_extension.checkCDR("1100 <1100>", "13001", "Answered", " ", FXO_1, communication_outRoute);
                }
                if (!E1.equals("null")) {
                    Reporter.infoExec(" 1100拨打13001，第7次预期通过E1外线呼出");
                    pjsip.Pj_Make_Call_Auto_Answer(1100, "13001", DEVICE_IP_LAN, false);
                    ys_waitingTime(5000);
                    if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
                        Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
                    } else {
                        Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
                    }
                    pjsip.Pj_Hangup_All();
                    m_extension.checkCDR("1100 <1100>", "13001", "Answered", " ", E1, communication_outRoute);
                }
                if (!BRI_1.equals("null")) {
                    Reporter.infoExec(" 1100拨打13001，第8次预期通过BRI外线呼出");
                    pjsip.Pj_Make_Call_Auto_Answer(1100, "13001", DEVICE_IP_LAN, false);
                    ys_waitingTime(5000);
                    if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
                        Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
                    } else {
                        Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
                    }
                    pjsip.Pj_Hangup_All();
                    m_extension.checkCDR("1100 <1100>", "13001", "Answered", " ", BRI_1, communication_outRoute);
                }
                if (!GSM.equals("null")) {
                    Reporter.infoExec(" 1100拨打13001，第9次预期通过GSM外线呼出");
                    pjsip.Pj_Make_Call_Auto_Answer(1100, "1" + DEVICE_ASSIST_GSM, DEVICE_IP_LAN, false);
                    ys_waitingTime(5000);
                    if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
                        Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
                    } else {
                        Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
                    }
                    pjsip.Pj_Hangup_All();
                    m_extension.checkCDR("1100 <1100>", "1" + DEVICE_ASSIST_GSM, "Answered", " ", GSM, communication_outRoute);
                }
                Reporter.infoExec(" 1000拨打13001，第10次预期通过SPS外线呼出");
                pjsip.Pj_Make_Call_Auto_Answer(1100, "13001", DEVICE_IP_LAN, false);
                ys_waitingTime(5000);
                if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
                    Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
                } else {
                    Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
                }
                pjsip.Pj_Hangup_All();
                m_extension.checkCDR("1100 <1100>", "13001", "Answered", " ", SPS, communication_outRoute);
            }else{
                Reporter.infoExec(" 针对PC：1100拨打13001，第6次预期通过SPS外线呼出");
                pjsip.Pj_Make_Call_Auto_Answer(1100,"13001",DEVICE_IP_LAN,false);
                ys_waitingTime(5000);
                if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
                    Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
                } else {
                    Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
                }
                pjsip.Pj_Hangup_All();
                m_extension.checkCDR("1100 <1100>","13001","Answered"," ",SPS,communication_outRoute);
            }
        }else{
            Reporter.infoExec("针对CloudPBX： 1100拨打13001，第4次预期通过SPS外线呼出");
            pjsip.Pj_Make_Call_Auto_Answer(1100,"13001",DEVICE_IP_LAN,false);
            ys_waitingTime(5000);
            if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
                Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
            } else {
                Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
            }
            pjsip.Pj_Hangup_All();
            m_extension.checkCDR("1100 <1100>","13001","Answered"," ",SPS,communication_outRoute);
        }
    }

//    再次编辑呼出路由：修改name、选择SPS线路、选择分机+分机组、取消勾选循环抓取
    @Test
    public void H_editSecond(){
        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"A123*_-+.~?HKtw1234567890bcdef.",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();
        ys_waitingTime(1000);
//        定位到编辑页面中的name元素，使用setvalue来修改值
        add_outbound_routes.name.setValue("OutRoute1_sip");
        ys_waitingTime(1000);
//       选择一条呼出路由，member选择分机1104+分机组
        Reporter.infoExec(" 编辑呼出路由OutRoute1_sip：选择1104+分机组"); //执行操作
//        选择分机1104+分机组
        ArrayList<String> arrayExtension = new ArrayList<>();
        arrayExtension.add("ExtensionGroup1");
        arrayExtension.add("1104");
        listSelect(add_outbound_routes.list_Extension, extensionList,arrayExtension);

//        新增一条SPS路由
        Reporter.infoExec(" 编辑呼出路由OutRoute1_sip：选择SPS呼出路由"); //执行操作
        ArrayList<String> arraytrunk = new ArrayList<>();
        arraytrunk.add(SPS);
        listSelect(add_outbound_routes.list_Trunk,trunkList,arraytrunk);
        ys_waitingTime(1000);
//        取消勾选循环抓取
        setCheckBox(add_outbound_routes.rrmemoryHunt,false);
        ys_waitingTime(1000);
//        保存编辑页面
        add_outbound_routes.save.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        ys_apply();
    }

    //    验证新增的三条90./91./92.的patterns生效
//    验证member为分机+分机组时生效
    @Test
    public void I_checkPatterns(){
//        验证member里的分机1104是可允许呼出的 + 验证90.
        Reporter.infoExec(" 1104拨打902000，预期呼出成功");
        pjsip.Pj_Make_Call_Auto_Answer(1104,"902000",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("xll <1104>","902000","Answered"," ",SPS,communication_outRoute);
//        验证分机组里的分机1100是可允许呼出的 + 验证91.
        Reporter.infoExec(" 1100拨打912000，预期呼出成功");
        pjsip.Pj_Make_Call_Auto_Answer(1100,"912000",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","912000","Answered"," ",SPS,communication_outRoute);
//        验证92.
        Reporter.infoExec(" 1100拨打922000，预期呼出成功");
        pjsip.Pj_Make_Call_Auto_Answer(1100,"922000",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","922000","Answered"," ",SPS,communication_outRoute);
//        验证members：除了选中的分机+分机组，其他分机（1103）呼出失败
        Reporter.infoExec(" 1103拨打922000，预期呼出失败");
        pjsip.Pj_Make_Call_Auto_Answer(1103,"922000",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        int state = getExtensionStatus(1103,HUNGUP,8);
        if(state == HUNGUP){
            Reporter.infoExec(" 1103拨打922000，预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1103拨打922000，预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail();
        }
    }

//    编辑呼出路由：删除patterns
    @Test
    public void J_editOutBound(){
        Reporter.infoExec(" 编辑呼出路由：删除patterns");
        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"OutRoute1_sip",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();
        ys_waitingTime(1000);
//        删除90./91./92.的patterns; 定位要删除哪一行
        int column1 = gridFindRowByColumn(add_outbound_routes.grid,add_outbound_routes.grid_gridcolumn_patterns,"90.",sort_ascendingOrder);
//           使用gridClick来点击表格里的删除按钮，每删除一个，表格的column就会发生变化，所以删除一个后再去获取另一个的column
        gridClick(add_outbound_routes.grid,column1,add_outbound_routes.gridDelete);
        ys_waitingTime(1000);
        int column2 = gridFindRowByColumn(add_outbound_routes.grid,add_outbound_routes.grid_gridcolumn_patterns,"91.",sort_ascendingOrder);
        gridClick(add_outbound_routes.grid,column2,add_outbound_routes.gridDelete);
        ys_waitingTime(1000);
        int column3 = gridFindRowByColumn(add_outbound_routes.grid,add_outbound_routes.grid_gridcolumn_patterns,"92.",sort_ascendingOrder);
        gridClick(add_outbound_routes.grid,column3,add_outbound_routes.gridDelete);
        ys_waitingTime(1000);
        //        保存编辑页面
        add_outbound_routes.save.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        ys_apply();
    }

//    验证patterns && 成功呼出的例子——从表格中获取
//        添加pattern的数据
    @DataProvider(name="success")
    public Object[][] SuccessNumbers() throws BiffException, IOException {
        ExcelUnit e=new ExcelUnit("OutboundRoutes", "success");
        return e.getExcelData();
    }

    @Test(dataProvider="success")
    public void K1_editPatterns(HashMap<String, String> data) {
        Reporter.infoExec("修改呼出路由的pattern、strip和prepend为："+data.get("patterns")+"、"+data.get("strip")+"、"+data.get("prepend"));
//        System.out.println(data.toString());

        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"OutRoute1_sip",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();

        executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('patterns','"+data.get("patterns")+"')");
        executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('strip','"+data.get("strip")+"')");
        executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('prepend','"+data.get("prepend")+"')");

        //        保存编辑页面
        add_outbound_routes.save.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        ys_apply();
        Reporter.infoExec(" 1100拨打"+data.get("number"));
        pjsip.Pj_Make_Call_Auto_Answer(1100,data.get("number"),DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>",data.get("number"),"Answered"," ",SPS,communication_outRoute);
    }

    //    验证patterns && 呼出失败的例子——从表格中获取
    //        添加pattern的数据
    @DataProvider(name="fail")
    public Object[][] FailNumbers() throws BiffException, IOException {
        ExcelUnit e=new ExcelUnit("OutboundRoutes", "fail");
        return e.getExcelData();
    }

    @Test(dataProvider="fail")
    public void K2_editPatterns(HashMap<String, String> data) {
        Reporter.infoExec("修改呼出路由的pattern、strip和prepend为："+data.get("patterns")+"、"+data.get("strip")+"、"+data.get("prepend"));

        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"OutRoute1_sip",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();

        executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('patterns','"+data.get("patterns")+"')");
        executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('strip','"+data.get("strip")+"')");
        executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('prepend','"+data.get("prepend")+"')");

        //        保存编辑页面
        add_outbound_routes.save.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        ys_apply();
        Reporter.infoExec(" 1100拨打"+data.get("number")+",预期呼出失败");
        pjsip.Pj_Make_Call_Auto_Answer(1100,data.get("number"),DEVICE_IP_LAN,false);
        ys_waitingTime(10000);
        int state = getExtensionStatus(1100,HUNGUP,8);
        if(state == HUNGUP || state == IDLE){
            Reporter.infoExec(" 1100拨打"+data.get("number")+"，预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1100拨打"+data.get("number")+"，预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail();
        }
    }

  /*以下为验证password部分
    包括PIN list——记录到CDR与未记录到CDR
    Single Pin——最短长度和最长长度的验证
    注意：自动化无法实现点击new来创建pin码，所以只能提前创建好pin码来选用*/
//    编辑呼出路由：Password——Pin List、patterns改为1.
    @Test
    public void L1_editPassword(){
        pjsip.Pj_Unregister_Account(1000);
        pjsip.Pj_Unregister_Account(1100);
        pjsip.Pj_Unregister_Account(1103);
        pjsip.Pj_Unregister_Account(1104);
        pjsip.Pj_Unregister_Account(3001);
        pjsip.Pj_Unregister_Account(2000);
        pjsip.Pj_Unregister_Account(4000);

        Reporter.infoExec(" 主测设备注册分机1000"); //执行操作
        pjsip.Pj_CreateAccount(1000, EXTENSION_PASSWORD, "UDP", UDP_PORT, 1);
        pjsip.Pj_Register_Account(1000, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1100"); //执行操作
        pjsip.Pj_CreateAccount(1100, EXTENSION_PASSWORD, "UDP", UDP_PORT, 2);
        pjsip.Pj_Register_Account(1100, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1103"); //执行操作
        pjsip.Pj_CreateAccount(1103, EXTENSION_PASSWORD, "UDP", UDP_PORT, 5);
        pjsip.Pj_Register_Account(1103, DEVICE_IP_LAN);

        Reporter.infoExec(" 主测设备注册分机1104"); //执行操作
        pjsip.Pj_CreateAccount(1104, EXTENSION_PASSWORD, "UDP", UDP_PORT, 6);
        pjsip.Pj_Register_Account(1104, DEVICE_IP_LAN);

        Reporter.infoExec(" 辅助设备1注册分机3001"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 3001, EXTENSION_PASSWORD, -1, DEVICE_ASSIST_1, UDP_PORT_ASSIST_1);
        pjsip.Pj_Register_Account_WithoutAssist(3001, DEVICE_ASSIST_1);

        Reporter.infoExec(" 辅助设备2注册分机2000"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 2000, EXTENSION_PASSWORD, -1, DEVICE_ASSIST_2, UDP_PORT_ASSIST_2);
        pjsip.Pj_Register_Account_WithoutAssist(2000, DEVICE_ASSIST_2);

        Reporter.infoExec(" 辅助设备3注册分机4000"); //执行操作
        pjsip.Pj_CreateAccount("UDP", 4000, EXTENSION_PASSWORD, -1, DEVICE_ASSIST_3, UDP_PORT_ASSIST_3);
        pjsip.Pj_Register_Account_WithoutAssist(4000, DEVICE_ASSIST_3);
        closePbxMonitor();

        Reporter.infoExec(" 编辑呼出路由Password，Passwoed：PIN List--test1");
        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"OutRoute1_sip",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();
        ys_waitingTime(1000);
//        修改patterns
        executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('patterns','1.')");
        executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('strip','1')");
        executeJs("Ext.getCmp('control-panel').down('outrouter-edit').down('grid').store.getAt(0).set('prepend','')");
        ys_waitingTime(1000);
//        选择Password——Pin List
        comboboxSelect(add_outbound_routes.Password,add_outbound_routes.Password_Pinset);
        ys_waitingTime(1000);
        comboboxSet(add_outbound_routes.combobox_PinsetPassword,"name","test1");
        ys_waitingTime(1000);
//        保存编辑页面
        add_outbound_routes.save.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        ys_apply();
    }

//    验证pin码
    @Test
    public void L2_checkPassword(){
        Reporter.infoExec(" 1000拨打128888通过SPS外线呼出");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"128888", DEVICE_IP_LAN,false);
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(1000,"1","2","3","4","5","#");
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(1000,"1","2","3","4","5","6","7","#");
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(1000,"1","2","3","4","5","6","7","8","#");
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","128888","Answered"," ",SPS,communication_outRoute);
        m_extension.checkCDR_OtherInfo(cdRandRecordings.gridColumn_PinCode,"12345678",1);
    }

    //    编辑呼出路由：Password——Pin List
    @Test
    public void L3_editPassword2(){
        Reporter.infoExec(" 编辑呼出路由Password，Passwoed：PIN List--test2");
        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"OutRoute1_sip",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();
        ys_waitingTime(1000);
//        选择Password——Pin List
        comboboxSelect(add_outbound_routes.Password,add_outbound_routes.Password_Pinset);
        ys_waitingTime(1000);
        comboboxSet(add_outbound_routes.combobox_PinsetPassword,"name","test2");
        ys_waitingTime(1000);
//        保存编辑页面
        add_outbound_routes.save.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        ys_apply();
    }

//    验证pin码——验证的确未记录到CDR中
    @Test
    public void L4_checkPassword2(){
        Reporter.infoExec(" 1000拨打128888通过SPS外线呼出，预期呼出成功");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"128888", DEVICE_IP_LAN,false);
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(1000,"1","2","3","4","5","6","#");
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","128888","Answered"," ",SPS,communication_outRoute);
        m_extension.checkCDR_OtherInfo(cdRandRecordings.gridColumn_PinCode," ",1);
    }
//    编辑呼出路由：Password——Single Pin
    @Test
    public void L5_editPassword3(){
        Reporter.infoExec(" 编辑呼出路由Password，Passwoed：Single Pin");
        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"OutRoute1_sip",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();
        ys_waitingTime(1000);
//        选择Password——Pin List
        comboboxSelect(add_outbound_routes.Password,add_outbound_routes.Password_Singlepin);
        ys_waitingTime(1000);
        add_outbound_routes.singlepin_edit.setValue("0");
        ys_waitingTime(1000);
//        保存编辑页面
        add_outbound_routes.save.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        ys_apply();
    }
//       验证single pin——记录到CDR中
    @Test
    public void L6_checkPassword3(){
        Reporter.infoExec(" 1000拨打128888通过SPS外线呼出");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"128888", DEVICE_IP_LAN,false);
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(1000,"0","#");
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","128888","Answered"," ",SPS,communication_outRoute);
        m_extension.checkCDR_OtherInfo(cdRandRecordings.gridColumn_PinCode,"0",1);
    }
//    编辑呼出路由：Password——Single Pin
    @Test
    public void L7_editPassword4(){
        Reporter.infoExec(" 编辑呼出路由Password，Passwoed：Single Pin");
        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"OutRoute1_sip",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();
        ys_waitingTime(1000);
//        选择Password——Pin List
        comboboxSelect(add_outbound_routes.Password,add_outbound_routes.Password_Singlepin);
        ys_waitingTime(1000);
        add_outbound_routes.singlepin_edit.setValue("1234567890123456789012345678901");
        ys_waitingTime(1000);
//        保存编辑页面
        add_outbound_routes.save.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        ys_apply();
    }
//    验证single pin——记录到CDR中
    @Test
    public void L8_checkPassword4(){
        Reporter.infoExec(" 1000拨打128888通过SPS外线呼出");
        pjsip.Pj_Make_Call_Auto_Answer(1000,"128888", DEVICE_IP_LAN,false);
        ys_waitingTime(2000);
        pjsip.Pj_Send_Dtmf(1000,"1","2","3","4","5","6","7","8","9","0","1","2","3","4","5","6","7","8","9","0","1","2","3","4","5","6","7","8","9","0","1","#");
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1000 <1000>","128888","Answered"," ",SPS,communication_outRoute);
        m_extension.checkCDR_OtherInfo(cdRandRecordings.gridColumn_PinCode,"1234567890123456789012345678901",1);
    }

//    添加time condition
  @Test
  public void M1_addTimeCondition() throws InterruptedException {
      time_Conditions.timeConditions.click();
      Reporter.infoExec(" 删除所有时间条件");
      deletes("删除所有时间条件",timeConditions.grid,timeConditions.delete,timeConditions.delete_yes,timeConditions.grid_Mask);

      Reporter.infoExec(" 添加时间条件workday_24hour:每天24小时都是工作时间"); //执行操作
      m_callcontrol.addTimeContion("workday_24hour","00:00","23:59",false,"all");
      Reporter.infoExec(" NotInWorkday：00:00-00:01,周日");
      m_callcontrol.addTimeContion("NotInWorkday","00:00","00:01",false,"sun");
  }

//    编辑呼出路由：勾选time condition、取消Password
    @Test
    public void M2_timeCondition1() {
        outboundRoutes.outboundRoutes.click();
        Reporter.infoExec(" 编辑呼出路由：勾选time condition——验证在time condition时间内能够呼出成功，取消password");
        gridClick(outboundRoutes.grid, gridFindRowByColumn(outboundRoutes.grid, outboundRoutes.gridcolumn_Name, "OutRoute1_sip", sort_ascendingOrder), outboundRoutes.gridEdit);
        ys_waitingMask();
        Reporter.infoExec("进入TimeCondition——workday_24hour的勾选");
//        勾选time condition
        $$("#st-or-timecondition span").findBy(text("workday_24hour")).click();
        ys_waitingTime(1000);
        Reporter.infoExec("进入Password——None的选择");
//        选择Password——None
        comboboxSelect(add_outbound_routes.Password, add_outbound_routes.Password_None);
        ys_waitingTime(1000);
        /*
        * 加一个cdr的开关操作，是为了让TimeCondition的小气泡消失
        * */
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();
        closeCDRRecord();

        add_outbound_routes.save.click();
        ys_waitingTime(3000);
//        等待呼出路由页面的表格正确加载
        ys_waitingLoading(outboundRoutes.grid_Mask);
        ys_apply();

//        通话验证
        Reporter.infoExec(" 1100拨打1111");
        pjsip.Pj_Make_Call_Auto_Answer(1100, "1111", DEVICE_IP_LAN, false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>", "1111", "Answered", " ", SPS, communication_outRoute);
    }

//    再次编辑呼出路由，勾选time condition——验证不在time condition时间内无法成功呼出
    @Test
    public void M2_timeCondition2(){
        Reporter.infoExec(" 编辑呼出路由：勾选time condition——验证不在time condition时间内无法成功呼出");
        gridClick(outboundRoutes.grid,gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"OutRoute1_sip",sort_ascendingOrder),outboundRoutes.gridEdit);
        ys_waitingMask();
//        取消勾选workday_24hour，勾选NotInWorkday
        $$("#st-or-timecondition span").findBy(text("workday_24hour")).click();
        ys_waitingTime(1000);
        $$("#st-or-timecondition span").findBy(text("NotInWorkday")).click();
        ys_waitingTime(1000);
//        选择Password——None——为了不让时间条件的小气泡挡住save按钮，所以这里对password做个选择，以防出现时间条件的小气泡
        comboboxSelect(add_outbound_routes.Password, add_outbound_routes.Password_None);
        ys_waitingTime(1000);
        /*
        * 加一个cdr的开关操作，是为了让TimeCondition的小气泡消失
        * */
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();
        closeCDRRecord();

        add_outbound_routes.save.click();
        ys_waitingTime(1000);
        ys_waitingLoading(outboundRoutes.grid_Mask);
        ys_apply();

//        通话验证
        Reporter.infoExec(" 1100拨打1111，预期呼出失败");
        pjsip.Pj_Make_Call_Auto_Answer(1100,"1111",DEVICE_IP_LAN,false);
        ys_waitingTime(8000);
        int state = getExtensionStatus(1100,HUNGUP,8);
        if(state == HUNGUP){
            Reporter.infoExec(" 1100拨打1111，预期呼出失败,实际呼出失败");
            pjsip.Pj_Hangup_All();
        }else{
            Reporter.infoExec(" 1100拨打1111，预期呼出失败,实际呼出成功");
            pjsip.Pj_Hangup_All();
            YsAssert.fail();
        }
    }

//    导入呼出路由，注意不同设备的区别，所以代码中的导入文件只含SIP 和 SPS
    @Test
    public void N_import(){
        ys_waitingTime(3000);

        outboundRoutes.outRoute_import.click();
        ys_waitingTime(1000);
        outboundRoutes.browse.click();
        System.out.println(EXPORT_PATH +"OutboundRoute.csv");
        ys_waitingTime(1000);
        importFile(EXPORT_PATH +"OutboundRoute.csv");
        outboundRoutes.import_import.click();
        ys_waitingTime(2000);
        outboundRoutes.import_OK.click();
        ys_apply();
    }

//    验证patterns的优先级
    @Test
    public void O_checkPriority1() {

//        未变换呼出路由位置时
        Reporter.infoExec(" 1100拨打903001，预期呼出成功");
        pjsip.Pj_Make_Call_Auto_Answer(1100, "903001", DEVICE_IP_LAN, false);
        ys_waitingTime(5000);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>", "903001", "Answered", " ", SIPTrunk, communication_outRoute);
    }
    @Test
    public void O_checkPriority2() {
//        第一次变换呼出路由位置，定位要移动哪条呼出路由
        int patterns_column = gridFindRowByColumn(outboundRoutes.grid, outboundRoutes.gridcolumn_Name, "OutboundRoute2", sort_ascendingOrder);
        System.out.println("第一次变换呼出路由位置，定位要移动哪条呼出路由，column=：" + patterns_column);
//        使用gridClick来点击表格里的up按钮
        gridClick(outboundRoutes.grid, patterns_column, outboundRoutes.gridUp);
        ys_apply();

//        通话验证
        Reporter.infoExec(" 1100拨打903001，预期呼出成功");
        pjsip.Pj_Make_Call_Auto_Answer(1100, "903001", DEVICE_IP_LAN, false);
        ys_waitingTime(5000);
        if (getExtensionStatus(3001, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机3001状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机3001状态为TALKING，实际状态为"+getExtensionStatus(3001, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>", "903001", "Answered", " ", SIPTrunk2, communication_outRoute);
    }
    @Test
    public void O_checkPriority3(){
//        第二次变换位置
        int patterns_column2 = gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"OutboundRoute3",sort_ascendingOrder);
//        使用gridClick来点击表格里的up按钮
        gridClick(outboundRoutes.grid,patterns_column2,outboundRoutes.gridUp);
        int patterns_column3 = gridFindRowByColumn(outboundRoutes.grid,outboundRoutes.gridcolumn_Name,"OutboundRoute3",sort_ascendingOrder);
//        使用gridClick来点击表格里的up按钮
        gridClick(outboundRoutes.grid,patterns_column3,outboundRoutes.gridUp);
        ys_apply();

//        通话验证
        Reporter.infoExec(" 1100拨打903001，预期呼出成功");
        pjsip.Pj_Make_Call_Auto_Answer(1100,"903001",DEVICE_IP_LAN,false);
        ys_waitingTime(5000);
        if (getExtensionStatus(2000, TALKING, 8) == TALKING) {
            Reporter.pass(" 分机2000状态--TALKING，通话正常建立");
        } else {
            Reporter.error(" 预期分机2000状态为TALKING，实际状态为"+getExtensionStatus(2000, TALKING, 8));
        }
        pjsip.Pj_Hangup_All();
        m_extension.checkCDR("1100 <1100>","903001","Answered"," ",SPS,communication_outRoute);
    }

    @Test
    public void P1_deleteOne_no() {
        setPageShowNum(outboundRoutes.grid, 100);
        Reporter.infoExec(" 删除单个呼出路由OutboundRoute1——选择no"); //执行操作
//       定位要删除的那条呼出路由
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(outboundRoutes.grid, outboundRoutes.gridcolumn_Name, "OutboundRoute3", sort_ascendingOrder)));
        //还未操作前呼出路由的所有条数
        int rows = Integer.parseInt(String.valueOf(gridLineNum(outboundRoutes.grid)));
        System.out.println("预期值:" + rows);
        gridClick(outboundRoutes.grid, row, outboundRoutes.gridDelete);
        ys_waitingTime(1000);
        outboundRoutes.delete_no.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(outboundRoutes.grid)));
        System.out.println("实际值:" + row1);
        YsAssert.assertEquals(row1, rows, "删除呼出路由OutboundRoute1-取消删除");
    }
    @Test
    public void P2_deleteOne_yes() {
        Reporter.infoExec(" 删除单个呼出路由OutboundRoute1——选择yes"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridLineNum(outboundRoutes.grid)));
        //       定位要删除的那条呼出路由
        int row1 = Integer.parseInt(String.valueOf(gridFindRowByColumn(outboundRoutes.grid, outboundRoutes.gridcolumn_Name, "OutboundRoute3", sort_ascendingOrder)));
        gridClick(outboundRoutes.grid, row1, outboundRoutes.gridDelete);
        ys_waitingTime(1000);
        outboundRoutes.delete_yes.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(outboundRoutes.grid)));
        System.out.println("实际值row2:" + row2);
        int row3 = row - 1;
        System.out.println("期望值row3:" + row3);
        ys_apply();
        YsAssert.assertEquals(row2, row3, "删除单个呼出路由OutboundRoute1——确定删除");
    }
    @Test
    public void P3_deletePart_no() {
        Reporter.infoExec(" 全部勾选，再取消某条的勾选后-取消删除"); //执行操作
//        还未删除前的表格行数
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(outboundRoutes.grid)));
        System.out.println("期望值row1:" + row1);
        int row2 = Integer.parseInt(String.valueOf(gridFindRowByColumn(outboundRoutes.grid, outboundRoutes.gridcolumn_Name, "OutboundRoute2", sort_ascendingOrder)));
        int row3 = Integer.parseInt(String.valueOf(gridFindRowByColumn(outboundRoutes.grid, outboundRoutes.gridcolumn_Name, "OutboundRoute1", sort_ascendingOrder)));
//        全部勾选
        gridSeleteAll(outboundRoutes.grid);
//        取消勾选OutboundRoute2和OutboundRoute3
        gridCheck(outboundRoutes.grid, row2, outboundRoutes.gridcolumn_Check);
        gridCheck(outboundRoutes.grid, row3, outboundRoutes.gridcolumn_Check);
//        点击删除按钮
        outboundRoutes.delete.click();
        outboundRoutes.delete_no.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        int row4 = Integer.parseInt(String.valueOf(gridLineNum(outboundRoutes.grid)));
        System.out.println("实际值row4:" + row4);
        YsAssert.assertEquals(row4, row1, "全部勾选，再取消某条的勾选后-取消删除");
    }
    @Test
    public void P4_deletePart_yes() {
        Reporter.infoExec(" 全部勾选，再取消某条的勾选后-确定删除"); //执行操作
        outboundRoutes.delete.click();
        outboundRoutes.delete_yes.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(outboundRoutes.grid)));
        System.out.println("实际row6:" + row1);
        ys_apply();
        YsAssert.assertEquals(row1, 2, "全部勾选，再取消某条的勾选后-确定删除");
    }
    @Test
    public void P5_deleteAll_no() {
//        勾选全部进行删除
        Reporter.infoExec(" 全部勾选-取消删除"); //执行操作
//        还未删除前的表格行数
        int row1 = Integer.parseInt(String.valueOf(gridLineNum(outboundRoutes.grid)));
//        全部勾选
        gridSeleteAll(outboundRoutes.grid);
//        点击删除按钮
        outboundRoutes.delete.click();
        outboundRoutes.delete_no.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(outboundRoutes.grid)));
        System.out.println("实际row2:" + row2);
        YsAssert.assertEquals(row1, row2, "全部勾选-取消删除");
    }
    @Test
    public void P6_deleteAll_yes() {
        Reporter.infoExec(" 全部勾选-确定删除"); //执行操作
        outboundRoutes.delete.click();
        outboundRoutes.delete_yes.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        int row = Integer.parseInt(String.valueOf(gridLineNum(outboundRoutes.grid)));
        System.out.println("实际row:" + row);
        ys_apply();
        YsAssert.assertEquals(row, 0, "全部勾选-确定删除");
    }

//    删除time condition
    @Test
    public void P7_deleteTime(){
        time_Conditions.timeConditions.click();
        Reporter.infoExec(" 删除所有时间条件");
        deletes("删除所有时间条件",timeConditions.grid,timeConditions.delete,timeConditions.delete_yes,timeConditions.grid_Mask);
        int row = Integer.parseInt(String.valueOf(gridLineNum(timeConditions.grid)));
        System.out.println("实际row:" + row);
        ys_apply();
        YsAssert.assertEquals(row, 0, "全部勾选-确定删除");
    }
    @Test
    public void P8_addTime() throws InterruptedException {
        Reporter.infoExec(" 添加时间条件Workday:默认的工作时间"); //执行操作
        m_callcontrol.addTimeContion("Workday","08:30","12:00",false,"mon","tue","wed","thu","fri");
        Reporter.infoExec(" 编辑Workday");
        gridClick(timeConditions.grid,gridFindRowByColumn(timeConditions.grid,timeConditions.gridcolumn_Name,"Workday",sort_ascendingOrder),timeConditions.gridEdit);
        ys_waitingTime(3000);
        add_time_condition.getAddTime(1).click();
//        add_time_condition.addTime.click();
        ys_waitingTime(1000);
        ArrayList<String> arrayStartTime = new ArrayList<>();
        arrayStartTime.add("starthour");
        arrayStartTime.add("endhour");
        ArrayList<String> arrayTime = new ArrayList<>();
        arrayTime.add("14");
        arrayTime.add("18");
        add_time_condition.setTime_One(2,arrayStartTime,arrayTime);
        add_time_condition.save.click();
        ys_apply();
    }
//    删除不可用的trunk——SIP3
    @Test
    public void P9_deleteTrunk(){
        settings.trunks_tree.click();
        setPageShowNum(trunks.grid,100);
        Reporter.infoExec(" 删除SIP3——选择yes"); //执行操作
        int row = Integer.parseInt(String.valueOf(gridFindRowByColumn(trunks.grid,trunks.gridcolumn_TrunkName,"SIP3",sort_ascendingOrder)));
        gridClick(trunks.grid,row,trunks.gridDelete);
        trunks.delete_yes.click();
        ys_waitingLoading(trunks.grid_Mask);
        int row2 = Integer.parseInt(String.valueOf(gridLineNum(trunks.grid)));
        System.out.println("实际值row2:"+row2);
        int row3=row-1;
        System.out.println("期望值row3:"+row3);
        YsAssert.assertEquals(row2,row3,"删除SIP3——确定删除");
    }

    //    恢复到BeforeTest环境;这里要考虑设备的不同性
    @Test
    public void Q_recovery1() throws InterruptedException {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        settings.callControl_tree.click();
        BeforeTest recovery = new BeforeTest();
        recovery.G_addOutRoute1();
        recovery.G_addOutRoute2();
        recovery.G_addOutRoute3();
        recovery.G_addOutRoute4();
        recovery.G_addOutRoute5();
        recovery.G_addOutRoute6();
        recovery.G_addOutRoute7();
        recovery.G_addOutRoute8();
        recovery.G_addOutRoute9();
        ys_apply();
    }
    @Test
    public void Q_recovery2(){
//        因为前面有删除过呼出路由，所以会影响到DISA线路的选择，这里恢复一下环境
        settings.callFeatures_tree.click();
        callFeatures.more.click();
        disa.DISA.click();
        disa.add.shouldBe(Condition.exist);
        ys_waitingMask();
        ys_waitingLoading(disa.grid_Mask);
        gridClick(disa.grid,gridFindRowByColumn(disa.grid,disa.gridcolumn_Name,"DISA1",sort_ascendingOrder),disa.gridEdit);
        listSelect(add_disa.list,nameList,"OutRoute1_sip","OutRoute3_sps");
        add_disa.save.click();
        ys_waitingLoading(disa.grid_Mask);
        String actName = String.valueOf(gridContent(disa.grid,gridFindRowByColumn(disa.grid,disa.gridcolumn_Name,"DISA1",sort_ascendingOrder),disa.gridcolumn_Name));
        YsAssert.assertEquals(actName,"DISA1");

    }
    //    AfterMethod是在每个Test执行后都要来执行的方法
    @AfterMethod
    public void AfterMethod(){
        if (cdRandRecordings.deleteCDR.isDisplayed()){
            System.out.println("admin角色的cdr页面未关闭");
            closeCDRRecord();
            System.out.println("admin角色的cdr页面已关闭");
        }
        if (pbxMonitor.extension.isDisplayed()){
            Reporter.infoExec("admin角色的PBXMonitor页面未关闭");
            closePbxMonitor();
            Reporter.infoExec("admin角色的PBXMonitor页面已关闭");
        }
    }
    @AfterClass
    public void AfterClass() throws InterruptedException {
        Reporter.infoAfterClass("执行完毕：======  OutboundRoutes  ======"); //执行操作
        quitDriver();
        pjsip.Pj_Destory();

        ys_waitingTime(10000);
        killChromePid();
    }

}
