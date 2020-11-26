package com.yeastar.testcase.pseries;

import com.jcraft.jsch.JSchException;
import com.yeastar.page.pseries.CallFeatures.IIVRPageElement;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.OperatorPanel.OperatorPanelPage;
import com.yeastar.page.pseries.OperatorPanel.OperatorPanelPage.RECORD_DETAILS;
import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.*;
import com.yeastar.untils.APIObject.IVRObject.PressKey;
import com.yeastar.untils.APIObject.IVRObject.PressKeyObject;
import io.qameta.allure.*;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.htmlunit.HtmlUnitAlert;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static com.yeastar.page.pseries.CallFeatures.IIVRPageElement.ele_ivr_basic_dial_outb_routes_checkbox;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.xbill.DNS.Type.CNAME;

import com.yeastar.untils.CDRObject.*;

/**
 * @program: SwebTest
 * @description: Operator Panel Test Case
 * @author: huangjx@yeastar.com
 * @create: 2020/07/30
 */

/********* 用例与 prompt的时长密切相关*********
 *  prompt1.wav   9 秒
 *  prompt2.wav   2 秒
 *  prompt3.wav   3 秒
 *  prompt4.wav   4 秒
 *  prompt5.wav   3 秒
 */
@Log4j2
public class TestIVR extends TestCaseBaseNew{
    private boolean isRunRecoveryEnvFlag = true;
    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;
    private boolean isReConfigIVR6201PressKeyFlag = true;
    private boolean isReConfigIVR6201Default = true;

    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();
    ArrayList<PressKeyObject> pressKeyObjects_0 = new ArrayList<>();
    ArrayList<PressKeyObject> pressKeyObjects_1 = new ArrayList<>();

    private String IVR_GREETING_DIAL_EXT= "ivr-greeting-dial-ext.slin";
    private String DIR_USINGKEYPAD = "dir-usingkeypad.gsm";
    private String PROMPT_1 = "prompt1.slin";
    private String PROMPT_2 = "prompt2.slin";

    APIUtil apiUtil = new APIUtil();

    String cdrIVR1 ="IVR IVR1<6201>";//6201
    String  invalidKey = "Invalid key";
    Object[][] routes = new Object[][]{

            {"99", 2000, "1000", DEVICE_ASSIST_2, "2000 [2000]", RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "SPS"},//sps   前缀 替换
            {"88", 2000, "1000", DEVICE_ASSIST_2, "2000 [2000]", RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "BRI"},//BRI   前缀 替换
            {"",   2000, "2005", DEVICE_ASSIST_2, "2000 [2000]", RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "FXO"},//FXO --77 不输   2005（FXS）
            {"66", 2000, "1000", DEVICE_ASSIST_2, "2000 [2000]", RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "E1"},//E1     前缀 替换
            {"",   3001, "3000", DEVICE_ASSIST_1, "3001 [3001]", RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "SIP_REGISTER"},//SIP  --55 REGISTER
            {"44", 4000, "1000", DEVICE_ASSIST_3, "4000 [4000]", RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "SIP_ACCOUNT"},
            {"33", 2000,DEVICE_TEST_GSM,DEVICE_ASSIST_2,DEVICE_ASSIST_GSM+" ["+DEVICE_ASSIST_GSM+"]",RECORD_DETAILS.EXTERNAL.getAlias(),"GSM"}
    };

    /**
     * 多线路测试数据
     * routePrefix（路由前缀） + caller（主叫） + callee（被叫） + device_assist（主叫所在的设置ip） + vcpCaller（VCP列表中显示的主叫名称） + vcpDetail（VCP中显示的Detail信息） + testRouteTypeMessage（路由类型）
     *
     * @return
     */
    @DataProvider(name = "routes")
    public Object[][] Routes(ITestContext c, Method method) {
        Object[][] group = null;
        String methodName = method.getName();
        //sip_register 呼入
        if(methodName.equals("testIVR01_trunk") || methodName.equals("testIVR04_PPRD_1") || methodName.equals("testIVR06_PPRD_2") || methodName.equals("testIVR07_PPRD_3") || methodName.equals("testIVR08_PPRD_4") || methodName.equals("testIVR09_PPRD_5") ||
                methodName.equals("testIVR01_trunk") || methodName.equals("testIVR05_PPRD_1_2") || methodName.equals("testIVR34_KeyPressEvent_1") || methodName.equals("testIVR10_DialExtension_1")){
            return  new Object[][]{{"", 3001, "3000", DEVICE_ASSIST_1, "3001 [3001]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(),SIPTrunk,"SIP_REGISTER"}};
        }
        //sps 呼入
        if (methodName.equals("testIVR02_trunk") || methodName.equals("testIVR11_DialExtension_2") || methodName.equals("testIVR12_DialExtension_3") || methodName.equals("testIVR13_DialExtension_4") || methodName.equals("testIVR14_DialExtension_5") || methodName.equals("testIVR18_DialExtension_9") ||
                methodName.equals("testIVR16_DialExtension_7") ||methodName.equals("testIVR26_DialExtension_17")|| methodName.equals("testIVR19_DialExtension_10") || methodName.equals("testIVR20_DialExtension_11") || methodName.equals("testIVR22_DialExtension_13") || methodName.equals("testIVR24_DialExtension_15") || methodName.equals("testIVR25_DialExtension_16")){
            return  new Object[][]{{"99", 2000, "6201", DEVICE_ASSIST_2, "2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(),SPS, "SPS"}};
        }
        if(methodName.equals("testIVR03_trunk")){
            Object[][] routes = new Object[][]{
                    {"88", 2000, "1000", DEVICE_ASSIST_2, "2000 [2000]", RECORD_DETAILS.EXTERNAL_IVR.getAlias(),BRI_1, "BRI"},//BRI   前缀 替换
                    {"",   2000, "2005", DEVICE_ASSIST_2, "2000 [2000]", RECORD_DETAILS.EXTERNAL_IVR.getAlias(),FXO_1,"FXO"},//FXO --77 不输   2005（FXS）
                    {"66", 2000, "1000", DEVICE_ASSIST_2, "2000 [2000]", RECORD_DETAILS.EXTERNAL_IVR.getAlias(),E1, "E1"},//E1     前缀 替换
                    {"44", 4000, "1000", DEVICE_ASSIST_3, "4000 [4000]", RECORD_DETAILS.EXTERNAL_IVR.getAlias(),ACCOUNTTRUNK ,"SIP_ACCOUNT"},
                    {"33", 2000,DEVICE_TEST_GSM,DEVICE_ASSIST_2,DEVICE_ASSIST_GSM+" ["+DEVICE_ASSIST_GSM+"]",RECORD_DETAILS.EXTERNAL.getAlias(),GSM,"GSM"}
            };
            return routes;
        }
        else {
            for (String groups : c.getIncludedGroups()) {
                for (int i = 0; i < routes.length; i++) {
                    for (int j = 0; j < routes[i].length; j++) {
                        if (groups.equalsIgnoreCase("SPS")) {
                            group = new Object[][]{{"99", 2000, "6200", DEVICE_ASSIST_2, "2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "SPS"}};
                        } else if (groups.equalsIgnoreCase("BRI")) {
                            group = new Object[][]{{"88", 2000, "6200", DEVICE_ASSIST_2, "2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "BRI"}};
                        } else if (groups.equalsIgnoreCase("FXO")) {
                            group = new Object[][]{{"", 2000, "2005", DEVICE_ASSIST_2, "2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "FXO"}};
                        } else if (groups.equalsIgnoreCase("E1")) {
                            group = new Object[][]{{"66", 2000, "6200", DEVICE_ASSIST_2, "2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "E1"}};
                        } else if (groups.equalsIgnoreCase("SIP_REGISTER")) {
                            group = new Object[][]{{"", 3001, "3000", DEVICE_ASSIST_1, "3001 [3001]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "SIP_REGISTER"}};
                        } else if (groups.equalsIgnoreCase("SIP_ACCOUNT")) {
                            group = new Object[][]{{"44", 4000, "6200", DEVICE_ASSIST_3, "2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "SIP_ACCOUNT"}};
                        } else if (groups.equalsIgnoreCase("GSM")) {
                            group = new Object[][]{{"33", 2000, DEVICE_TEST_GSM, DEVICE_ASSIST_2, DEVICE_ASSIST_GSM + " [" + DEVICE_ASSIST_GSM + "]", RECORD_DETAILS.EXTERNAL.getAlias(), "GSM"}};
                        } else {
                            group = routes;
                        }
                    }
                }
            }
        }
        //jenkins  run with xml and ITestContext c will be null
        if (group == null) {
            group = routes; //default run all routes
        }
        return group;
    }


    Object[][] routesOutbound = new Object[][]{
            //routePrefix（路由前缀） + caller（主叫） + callee（被叫） + device_assist（主叫所在的设置ip） + prefix + callee + RingExtension + InBoundTrunk(呼入线路) + OutBoundTrunk(呼出线路)
            {"", 3001, "3000", DEVICE_ASSIST_1, "2",2222,"RingExtension","SIP_REGISTER", "SPS"},//SIP  --55 REGISTER
            {"99", 2000, "1000", DEVICE_ASSIST_2,"3",2001,"RingExtension","SPS", "SIP_ACCOUNT"},
            {"", 3001, "3000", DEVICE_ASSIST_1, "4",2000,"RingExtension","SIP_REGISTER", "SPS", "FXO"},
            {"", 3001, "3000", DEVICE_ASSIST_1, "5",5555,"RingExtension","SIP_REGISTER", "SPS", "BRI"},
            {"", 3001, "3000", DEVICE_ASSIST_1, "6",6666,"RingExtension","SIP_REGISTER", "SPS", "EI"},
            {"", 3001, "3000", DEVICE_ASSIST_1, "7",DEVICE_ASSIST_GSM,"RingExtension" ,"SIP_REGISTER", "SPS", "EI"}
    };


    /**
     * @return
     */
    @DataProvider(name = "routesOutbound")
    public Object[][] RoutesOutbound(ITestContext c, Method method) {
        Object[][] group = null;
        String methodName = method.getName();
        //sps呼入--sip呼出
        if(methodName.equals("testIVR27_DialOutboundRoutes_1") || methodName.equals("testIVR29_DialOutboundRoutes_8")){
            return  new Object[][]{ {"99", 2000, "1000", DEVICE_ASSIST_2,"1",3001,3001, SPS, SIPTrunk}};//SIP  --55 R
        }
        //
        if(methodName.equals("testIVR28_DialOutboundRoutes_2To7")){
            Object[][] routes = new Object[][]{
                    //routePrefix（路由前缀） + caller（主叫） + callee（被叫） + device_assist（主叫所在的设置ip） + DTMF + RingExtension + InBoundTrunk(呼入线路) + OutBoundTrunk(呼出线路)
                    {"", 3001, "3000", DEVICE_ASSIST_1, "2",2000,2000,SIPTrunk, SPS},//SIP  --55 REGISTER
                    {"99", 2000, "1000", DEVICE_ASSIST_2,"3",4000,4000,SPS, ACCOUNTTRUNK},
                    {"", 3001, "3000", DEVICE_ASSIST_1, "4",2000,2000,SIPTrunk, FXO_1},
                    {"", 3001, "3000", DEVICE_ASSIST_1, "5",2000,2000,SIPTrunk, BRI_1},
                    {"", 3001, "3000", DEVICE_ASSIST_1, "6",2000,2000,SIPTrunk,  E1},
//                    {"", 3001, "3000", DEVICE_ASSIST_1, "7",DEVICE_ASSIST_GSM, "2000","SIP_REGISTER", "GSM"}
            };
            return routes;
        }
        //sip呼入--sps呼出
        if(methodName.equals("testIVR32_DialOutboundRoutes_11") || methodName.equals("testIVR33_DialOutboundRoutes_12")  || methodName.equals("testIVR30_DialOutboundRoutes_9")|| methodName.equals("testIVR31_DialOutboundRoutes_10"))
            return  new Object[][]{{"", 3001, "3000", DEVICE_ASSIST_1, "2",2000,2000,SIPTrunk, SPS}};//SIP  --55 REGISTER
        //jenkins  run with xml and ITestContext c will be null
        if (group == null) {
            group = routesOutbound; //default run all routes
        }
        return group;
    }



    //############### press key event data #################

    Object[][] routesKeyPressEvent = new Object[][]{
            {"",3001, "3000", DEVICE_ASSIST_1, "5","通过sip外线呼入到IVR1，按5","SIP_REGISTER"},//SIP  --55 REGISTER
            {"",3001, "3000", DEVICE_ASSIST_1, "*","通过sip外线呼入到IVR1，按*","SIP_REGISTER"},//SIP  --55 REGISTER
            {"",3001, "3000", DEVICE_ASSIST_1, "A","通过sip外线呼入到IVR1，按A","SIP_REGISTER"},//SIP  --55 REGISTER
    };
    /**
     * 多线路测试数据
     * routePrefix（路由前缀） + caller（主叫） + callee（被叫） + device_assist（主叫所在的设置ip） + vcpCaller（VCP列表中显示的主叫名称） + vcpDetail（VCP中显示的Detail信息） + testRouteTypeMessage（路由类型）
     *
     * @return
     */
    @DataProvider(name = "routesKeyPressEvent")
    public Object[][] routesKeyPressEvent(ITestContext c, Method method) {
        Object[][] group = null;
        String methodName = method.getName();
        //sip_register 呼入
        if(methodName.equals("testIVR35_KeyPressEvent_2To4")){
            Object[][] routesKeyPressEvent = new Object[][]{
                    {"",3001, "3000", DEVICE_ASSIST_1, "5","通过sip外线呼入到IVR1，按5",SIPTrunk,"SIP_REGISTER"},//SIP  --55 REGISTER
                    {"",3001, "3000", DEVICE_ASSIST_1, "*","通过sip外线呼入到IVR1，按*",SIPTrunk,"SIP_REGISTER"},//SIP  --55 REGISTER
                    {"",3001, "3000", DEVICE_ASSIST_1, "A","通过sip外线呼入到IVR1，按A",SIPTrunk,"SIP_REGISTER"},//SIP  --55 REGISTER
            };
            return routesKeyPressEvent;
        }
        if(methodName.equals("testIVR36_KeyPressEvent_5")){
            Object[][] routesKeyPressEvent = new Object[][]{
                    {"",3001, "3000", DEVICE_ASSIST_1, "2","5.通过sip外线呼入到IVR1，按2\n 分机1001响铃可正常接听，挂断",SIPTrunk,"SIP_REGISTER"},//SIP  --55 REGISTER
            };
            return routesKeyPressEvent;
        }
        if(methodName.equals("testIVR37_KeyPressEvent_6")){
            Object[][] routesKeyPressEvent = new Object[][]{
                    {"",3001, "3000", DEVICE_ASSIST_1, "1","6.通过sip外线呼入到IVR1，按1\n 通话被挂断",SIPTrunk,"SIP_REGISTER"},//SIP  --55 REGISTER
            };
            return routesKeyPressEvent;
        }
        if(methodName.equals("testIVR38_KeyPressEvent_7")){
            Object[][] routesKeyPressEvent = new Object[][]{
                    {"",3001, "3000", DEVICE_ASSIST_1, "3","7.通过sip外线呼入到IVR1，按3\n 分机1001登录查看新增1条语音留言，Name显示外部号码",SIPTrunk,"SIP_REGISTER"},//SIP  --55 REGISTER
            };
            return routesKeyPressEvent;
        }
        if(methodName.equals("testIVR39_KeyPressEvent_8")){
            Object[][] routesKeyPressEvent = new Object[][]{
                    {"",3001, "3000", DEVICE_ASSIST_1, "4","8.听到提示音后再按0\n 分机1000响铃可正常接听，挂断",SIPTrunk,"SIP_REGISTER"},//SIP  --55 REGISTER
            };
            return routesKeyPressEvent;
        }
        if(methodName.equals("testIVR40_KeyPressEvent_9")){
            Object[][] routesKeyPressEvent = new Object[][]{
                    {"",3001, "3000", DEVICE_ASSIST_1, "5","9.通过sip外线呼入到IVR1，按5\n 分机1000、1001、1003 同时响铃，分机1001接听后，1000、1003停止响铃；可正常挂断，检查cdr\n",SIPTrunk,"SIP_REGISTER"},//SIP  --55 REGISTER
            };
            return routesKeyPressEvent;
        }
        if(methodName.equals("testIVR41_KeyPressEvent_10")){
            Object[][] routesKeyPressEvent = new Object[][]{
                    {"",3001, "3000", DEVICE_ASSIST_1, "5","10.通过sip外线呼入到IVR1，按5\n 10.1000、1001、1003 响铃后都不接听，10s后只有分机1000再次响铃，分机1000可正常接听，检查cdr;",SIPTrunk,"SIP_REGISTER"},//SIP  --55 REGISTER
            };
            return routesKeyPressEvent;
        }
        if(methodName.equals("testIVR42_KeyPressEvent_11")){
            Object[][] routesKeyPressEvent = new Object[][]{
                    {"",3001, "3000", DEVICE_ASSIST_1, "6","11.通过sip外线呼入到IVR1，按6\n 分机1000、1001、1003、1004同时响铃，1004接听后停止响铃；可正常挂断，检查cdr",SIPTrunk,"SIP_REGISTER"},//SIP  --55 REGISTER
            };
            return routesKeyPressEvent;
        }
        if(methodName.equals("testIVR43_KeyPressEvent_12")){
            Object[][] routesKeyPressEvent = new Object[][]{
                    {"",3001, "3000", DEVICE_ASSIST_1, "6","12.通过sip外线呼入到IVR1，按6\n 分机1000、1001、1003、1004同时响铃，1001接听后停止响铃；可正常挂断，检查cdr",SIPTrunk,"SIP_REGISTER"},//SIP  --55 REGISTER
            };
            return routesKeyPressEvent;
        }
        if(methodName.equals("testIVR44_KeyPressEvent_13")){
            Object[][] routesKeyPressEvent = new Object[][]{
                    {"",3001, "3000", DEVICE_ASSIST_1, "6","13.通过sip外线呼入到IVR1，按6\n 分机1000、1001、1003、1004同时响铃，等待超时",SIPTrunk,"SIP_REGISTER"},//SIP  --55 REGISTER
            };
            return routesKeyPressEvent;
        }
        if(methodName.equals("testIVR45_KeyPressEvent_14")){
            Object[][] routesKeyPressEvent = new Object[][]{
                    {"",3001, "3000", DEVICE_ASSIST_1, "6","14.通过sip外线呼入到IVR1，按6\n 14.等待10s后，主叫按0\n 只有分机1001正常响铃、接听；检查cdr",SIPTrunk,"SIP_REGISTER"},//SIP  --55 REGISTER
            };
            return routesKeyPressEvent;
        }
        if(methodName.equals("testIVR46_KeyPressEvent_15")){
            Object[][] routesKeyPressEvent = new Object[][]{
                    {"",3001, "3000", DEVICE_ASSIST_1, "7","15.通过sip外线呼入到IVR1，按7\n 进入asterisk检查播放dir-usingkeypad.gsm时\n .按8378 然后按1\n 分机1004响铃，可正常接听\n",SIPTrunk,"SIP_REGISTER"},//SIP  --55 REGISTER
            };
            return routesKeyPressEvent;
        }
        if(methodName.equals("testIVR47_KeyPressEvent_16")){
            Object[][] routesKeyPressEvent = new Object[][]{
                    {"",3001, "3000", DEVICE_ASSIST_1, "7","16.通过sip外线呼入到IVR1，按7\n 进入asterisk检查播放dir-usingkeypad.gsm时\n 按8378 然后按*，再按1\n 分机1000响铃，可正常接听",SIPTrunk,"SIP_REGISTER"},//SIP  --55 REGISTER
            };
            return routesKeyPressEvent;
        }
        if(methodName.equals("testIVR48_KeyPressEvent_17")){
            Object[][] routesKeyPressEvent = new Object[][]{
                    {"",3001, "3000", DEVICE_ASSIST_1, "8","17.通过sip外线呼入到IVR1，按8\n 辅助2分机2000可正常响铃、接听；查看cdr正确",SIPTrunk,"SIP_REGISTER"},//SIP  --55 REGISTER
            };
            return routesKeyPressEvent;
        }
        if(methodName.equals("testIVR49_KeyPressEvent_18")){
            Object[][] routesKeyPressEvent = new Object[][]{
                    {"",3001, "3000", DEVICE_ASSIST_1, "9","18.通过sip外线呼入到IVR1，按9\n 进入asterisk查看播放提示音prompt1一遍后，通话挂断；查看cdr正确",SIPTrunk,"SIP_REGISTER"},//SIP  --55 REGISTER
            };
            return routesKeyPressEvent;
        }
        if(methodName.equals("testIVR50_KeyPressEvent_19")){
            Object[][] routesKeyPressEvent = new Object[][]{
                    {"",3001, "3000", DEVICE_ASSIST_1, "#","19.通过sip外线呼入到IVR1，按#\n 分机C-1002响铃，可正常接听，cdr正确",SIPTrunk,"SIP_REGISTER"},//SIP  --55 REGISTER
            };
            return routesKeyPressEvent;
        }
        if(methodName.equals("testIVR51_KeyPressEvent_20")){
            Object[][] routesKeyPressEvent = new Object[][]{
                    {"",3001, "3000", DEVICE_ASSIST_1, "*","20.通过sip外线呼入到IVR1，按*\n 进入asterisk查看播放提示音prompt2 五遍后，通话挂断；查看cdr正确",SIPTrunk,"SIP_REGISTER"},//SIP  --55 REGISTER
            };
            return routesKeyPressEvent;
        }
        if(methodName.equals("testIVR52_KeyPressEvent_21")){
            Object[][] routesKeyPressEvent = new Object[][]{
                    {"",3001, "3000", DEVICE_ASSIST_1, "","21.通过sip外线呼入到IVR1，不按键\n 判断一段时间后（最大100s ，IVR1的按键时长在不同步骤下可能被编辑设置为不同时长)1003分机响铃，可正常接听，cdr正确",SIPTrunk,"SIP_REGISTER"},//SIP  --55 REGISTER
            };
            return routesKeyPressEvent;
        }
        if(methodName.equals("testIVR53_KeyPressEvent_22")){
            Object[][] routesKeyPressEvent = new Object[][]{
                    {"",3001, "3000", DEVICE_ASSIST_1, "A","22.通过sip外线呼入到IVR1，按A\n 分机E-1004可正常响铃、接听；cdr正确",SIPTrunk,"SIP_REGISTER"},//SIP  --55 REGISTER
            };
            return routesKeyPressEvent;
        }
        if(methodName.equals("testIVR54_KeyPressEvent_23")){
            Object[][] routesKeyPressEvent = new Object[][]{
                    {"",3001, "3000", DEVICE_ASSIST_1, "0","23.通过sip外线呼入到IVR1，按0\n 分机FXS-1020可正常响铃、接听‘cdr正确",SIPTrunk,"SIP_REGISTER"},//SIP  --55 REGISTER
            };
            return routesKeyPressEvent;
        }
        //jenkins  run with xml and ITestContext c will be null
        if (group == null) {
            group = routesKeyPressEvent; //default run all routes
        }
        return group;
    }


    private boolean runRecoveryEnvFlag = true;
    private ArrayList<String> IVRMember = new ArrayList<>();
    private String reqDataCreateExtension = String.format("" +
                    "{\"type\":\"SIP\",\"first_name\":\"EXTENSIONFIRSTNAME\",\"last_name\":\"EXTENSIONLASTNAME\",\"email_addr\":\"\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":0,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"MTAwMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"reg_name\":\"EXTENSIONNUM\",\"reg_password\":\"%s\",\"allow_reg_remotely\":1,\"enb_user_agent_ident\":0,\"enb_ip_rstr\":0}"
            , enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)), enBase64(EXTENSION_PASSWORD));

    private String reqDataCreateExtensionWithRoleAdmin = String.format("" +
                    "{\"type\":\"SIP\",\"first_name\":\"EXTENSIONFIRSTNAME\",\"last_name\":\"EXTENSIONLASTNAME\",\"email_addr\":\"\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":1,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"MTAwMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"reg_name\":\"EXTENSIONNUM\",\"reg_password\":\"%s\",\"allow_reg_remotely\":1,\"enb_user_agent_ident\":0,\"enb_ip_rstr\":0}"
            , enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)), enBase64(EXTENSION_PASSWORD));

    private String reqDataCreateSPS_2 = String.format("" +
                    "{\"name\":\"%s\",\"enable\":1,\"country\":\"general\",\"itsp\":\"\",\"type\":\"peer\",\"transport\":\"udp\",\"codec_sel\":\"ulaw,alaw,g729\",\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_srtp\":0,\"enb_t38_support\":0,\"enb_inband_progress\":0,\"max_call_chans\":0,\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"%s\",\"from_user\":\"\",\"from_user_part\":\"default\",\"from_disp_name\":\"\",\"from_disp_name_part\":\"default\",\"from_host\":\"\",\"from_host_part\":\"domain\",\"diversion_user\":\"\",\"diversion_user_part\":\"\",\"rpid_user\":\"\",\"rpid_user_part\":\"\",\"pai_user\":\"\",\"pai_user_part\":\"\",\"ppi_user\":\"\",\"ppi_user_part\":\"\",\"enb_privacy_id\":0,\"enb_user_phone\":0,\"caller_id_from\":\"follow_system\",\"did_from\":\"follow_system\",\"user_agent\":\"\",\"enb_100rel\":0,\"max_ptime\":\"default\",\"rtp_reinvite\":\"\",\"enb_guest_auth\":0,\"enb_early_media\":0,\"enb_message\":0,\"did_list\":[],\"inbound_cid_list\":[],\"outbound_cid_list\":[],\"hostname\":\"%s\",\"port\":5060,\"domain\":\"%s\"}"
            , SPS, "DOD", DEVICE_ASSIST_2, DEVICE_ASSIST_2);
    private String reqDataCreateExtensionFXS = String.format("" +
                    "{\"type\":\"FXS\",\"first_name\":\"EXTENSIONFIRSTNAME\",\"last_name\":\"EXTENSIONLASTNAME\",\"mobile_number\":\"\",\"user_password\":\"%s\",\"role_id\":0,\"number\":\"EXTENSIONNUM\",\"caller_id\":\"EXTENSIONNUM\",\"emergency_caller_id\":\"\",\"trunk_caller_id_list\":[],\"presence_status\":\"available\",\"presence_status_text\":\"\",\"enb_vm\":1,\"enb_vm_pin\":1,\"vm_pin\":\"OTkxMA==\",\"new_vm_notify\":\"no\",\"after_vm_notify\":\"no\",\"enb_vm_play_datetime\":0,\"enb_vm_play_caller_id\":0,\"enb_vm_play_duration\":0,\"vm_greeting\":\"follow_system\",\"enb_email_pwd_chg\":1,\"enb_email_miss_call\":0,\"enb_ctl_record\":0,\"presence_list\":[{\"status\":\"available\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"away\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"do_not_disturb\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"launch\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"business_trip\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"},{\"status\":\"off_work\",\"information\":\"\",\"enb_in_always_forward\":0,\"in_always_forward_dest\":\"ext_vm\",\"in_always_forward_prefix\":\"\",\"in_always_forward_num\":\"\",\"in_always_forward_value\":\"\",\"enb_in_no_answer_forward\":1,\"in_no_answer_forward_dest\":\"ext_vm\",\"in_no_answer_forward_prefix\":\"\",\"in_no_answer_forward_num\":\"\",\"in_no_answer_forward_value\":\"\",\"enb_in_busy_forward\":1,\"in_busy_forward_dest\":\"ext_vm\",\"in_busy_forward_prefix\":\"\",\"in_busy_forward_num\":\"\",\"in_busy_forward_value\":\"\",\"enb_ex_always_forward\":0,\"ex_always_forward_dest\":\"ext_vm\",\"ex_always_forward_prefix\":\"\",\"ex_always_forward_num\":\"\",\"ex_always_forward_value\":\"\",\"enb_ex_no_answer_forward\":1,\"ex_no_answer_forward_dest\":\"ext_vm\",\"ex_no_answer_forward_prefix\":\"\",\"ex_no_answer_forward_num\":\"\",\"ex_no_answer_forward_value\":\"\",\"enb_ex_busy_forward\":1,\"ex_busy_forward_dest\":\"ext_vm\",\"ex_busy_forward_prefix\":\"\",\"ex_busy_forward_num\":\"\",\"ex_busy_forward_value\":\"\",\"enb_ring1_endpoints\":1,\"enb_ring1_mobile_client\":1,\"enb_ring1_desktop_client\":1,\"enb_ring1_web_client\":1,\"enb_ring2_endpoints\":0,\"enb_ring2_mobile_client\":0,\"enb_ring2_desktop_client\":0,\"enb_ring2_web_client\":0,\"enb_ring_mobile\":0,\"mobile_prefix\":\"\",\"mobile_number\":\"\",\"dynamic_agent_action\":\"no_action\",\"ring_timeout\":30,\"vm_greeting\":\"\"}],\"disable_outb_call\":0,\"disable_office_time_outb_call\":0,\"max_outb_call_duration\":-1,\"enb_mobile_client\":1,\"enb_desktop_client\":1,\"enb_web_client\":1,\"group_list\":GROUPLIST,\"dtmf_mode\":\"rfc4733\",\"enb_qualify\":1,\"enb_t38_support\":0,\"transport\":\"udp\",\"enb_nat\":1,\"enb_srtp\":0,\"fxs_port\":\"FXSPORT\",\"enb_hotline\":0,\"hotline_number\":\"\",\"delay_dial\":2,\"min_flash_detect\":300,\"max_flash_detect\":1000,\"rx_volume\":\"0\",\"rx_gain\":0,\"tx_volume\":\"0\",\"tx_gain\":0,\"enb_call_waiting\":0,\"enb_dtmf_passthrough\":0,\"enb_echo_cancel\":1}"
            , enBase64(DigestUtils.md5Hex(EXTENSION_PASSWORD)), enBase64(EXTENSION_PASSWORD));


    public void prerequisite(boolean isRestIVR6201ToDefault) {
        long startTime=System.currentTimeMillis();
        if(isDebugInitExtensionFlag){
            isDebugInitExtensionFlag = registerAllExtensions();
            isRunRecoveryEnvFlag = false;
        }
        if (isRunRecoveryEnvFlag) {
            step("=========== init before class  start =========");
            initExtension();
            initExtensionGroup();
            initTrunk();
            initRingGroup();
            initQueue();
            initConference();
            initOutbound();
            initIVR();
            initInbound();
            initFeatureCode();
            isRunRecoveryEnvFlag = registerAllExtensions();
        }

        if(isRestIVR6201ToDefault){
            restIVR6201ToDefault();
        }else{
            configIVR6201PressKey();
        }

        step("=========== init before class  end =========");
        log.debug("[prerequisite time]:"+(System.currentTimeMillis()-startTime)/1000+" Seconds");
    }

    /**
     * 设置IVR6201 PressKey
     */
    public void configIVR6201PressKey(){
        if(isReConfigIVR6201PressKeyFlag){
            step("=========== config ivr 6201  press key start =========");
            pressKeyObjects_1.clear();
            pressKeyObjects_1.add(new PressKeyObject(PressKey.press0, "extension", "", "1020", 0));
            pressKeyObjects_1.add(new PressKeyObject(PressKey.press1, "end_call","", "1000", 0));
            pressKeyObjects_1.add(new PressKeyObject(PressKey.press2, "extension","", "1001", 0));
            pressKeyObjects_1.add(new PressKeyObject(PressKey.press3, "ext_vm","", "1001", 0));
            pressKeyObjects_1.add(new PressKeyObject(PressKey.press4, "ivr","", "6200", 0));
            pressKeyObjects_1.add(new PressKeyObject(PressKey.press5, "ring_group","", "6300", 0));
            pressKeyObjects_1.add(new PressKeyObject(PressKey.press6, "queue","", "6400", 0));
            pressKeyObjects_1.add(new PressKeyObject(PressKey.press7, "dial_by_name","", "", 0));
            pressKeyObjects_1.add(new PressKeyObject(PressKey.press8, "external_num","", "2000", 0));
            pressKeyObjects_1.add(new PressKeyObject(PressKey.press9, "play_greeting","1", "prompt1.wav", 0));
            pressKeyObjects_1.add(new PressKeyObject(PressKey.press_hash, "extension", "", "1002", 0));
            pressKeyObjects_1.add(new PressKeyObject(PressKey.press_star, "play_greeting", "5", "prompt2.wav", 0));
            pressKeyObjects_1.add(new PressKeyObject(PressKey.timeout, "extension", "", "1003", 0));
            pressKeyObjects_1.add(new PressKeyObject(PressKey.invaild, "extension", "", "1004", 0));

            apiUtil.deleteIVR("IVR1").createIVR("6201", "IVR1", pressKeyObjects_1).apply();
            step("创建呼入路由InRoute3,目的地到IVR 6200");
            List<String> trunk9 = new ArrayList<>();
            trunk9.add(SPS);
            trunk9.add(BRI_1);
            trunk9.add(FXO_1);
            trunk9.add(E1);
            trunk9.add(SIPTrunk);
            trunk9.add(ACCOUNTTRUNK);
            trunk9.add(GSM);
            apiUtil.deleteAllInbound().createInbound("Inbound1", trunk9, "IVR", "6201").apply();

            step("=========== config ivr 6201  press key  end =========");
            isReConfigIVR6201PressKeyFlag = false;
            isReConfigIVR6201Default=true;
        }
    }


    /**
     * 重置ivr6201to default，只有按0到分机A
     */
    public void restIVR6201ToDefault(){
        if(isReConfigIVR6201Default) {
        step("=========== rest ivr 6201 to default start =========");
        pressKeyObjects_0.clear();
        pressKeyObjects_0.add(new PressKeyObject(PressKey.press0, "extension", "", "1000", 0));
        apiUtil.deleteIVR("IVR1").createIVR("6201", "IVR1", pressKeyObjects_0).apply();
        step("创建呼入路由InRoute3,目的地到IVR 6200");
        List<String> trunk9 = new ArrayList<>();
        trunk9.add(SPS);
        trunk9.add(BRI_1);
        trunk9.add(FXO_1);
        trunk9.add(E1);
        trunk9.add(SIPTrunk);
        trunk9.add(ACCOUNTTRUNK);
        trunk9.add(GSM);
        apiUtil.deleteAllInbound().createInbound("Inbound1", trunk9, "IVR", "6201").apply();

        step("=========== rest ivr 6201 to default   end =========");
            isReConfigIVR6201Default = false;
        isReConfigIVR6201PressKeyFlag = true;
        }
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("Trunk")
    @Description("1.通过外线呼入到IVR1按0到分机A-1000\n" +
            "2.检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "","IVR","Trunk", "InboundRoute","testIVR03_trunk", "GSM", "BRI", "FXO",  "E1","ACCOUNT","PSeries","Cloud","K2"}, dataProvider = "routes")
    public void testIVR03_trunk(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2:【2000 呼叫 6201】，press 0 ->分机1000 为Ringing状态，接听，挂断;[caller：]"+caller+",[callee：] "+routePrefix + callee+",[deviceAssist：] "+deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*4);

        pjsip.Pj_Send_Dtmf(caller, "0");
        assertStep("[通话状态校验：分机1000为Ring]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING);
        pjsip.Pj_Answer_Call(1000,false);
        assertStep("[通话状态校验：分机1000为Talking]");
        Assert.assertEquals(getExtensionStatus(1000,TALKING,30),TALKING);
//        GSM 通话太短，卡会被锁住，故通话时长久一点
        if(message.equals("GSM")){
            sleep(80000);
        }
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        if(message.equals("SPS")){
            softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                    .contains(tuple("2000<2000>".replace("2000",caller+""), CDRNAME.IVR1_6201, STATUS.ANSWER, "2000<2000> called Extension".replace("2000",caller+""),trunk,"","Inbound"))
                    .contains(tuple("2000<2000>".replace("2000",caller+""), CDRNAME.Extension_1000, STATUS.ANSWER, "test A<1000> hung up",trunk,"","Inbound"));

            softAssertPlus.assertAll();
        }
    }


    @Epic("P_Series")
    @Feature("IVR")
    @Story("Basic_Trunk")
    @Description("1.通过sip外线呼入到IVR1按0到分机A")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P1", "IVR", "InboundRoute","Basic", "Trunk", "testIVR01_trunk","SIP_REGISTER","PSeries","Cloud","K2"}, dataProvider = "routes")
    public void testIVR01_trunk(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2:通过sip外线呼入到IVR1按0到分机A,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);

        pjsip.Pj_Send_Dtmf(caller, "0");
        sleep(WaitUntils.SHORT_WAIT * 2);
        assertStep("[通话状态校验：分机1000为Ring]");
        Assert.assertEquals(getExtensionStatus(1000,RING,20),RING);
        pjsip.Pj_Answer_Call(1000,false);
        sleep(WaitUntils.SHORT_WAIT );
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING,5)).as("[通话状态校验]").isEqualTo(TALKING);

        pjsip.Pj_hangupCall(1000);

        assertStep("9:[CDR显示]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1, "ANSWERED", "2000<2000> called Extension".replace("2000",caller+""),trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "test A<1000> hung up",trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("Basic_Trunk")
    @Description("通过sps外线呼入到IVR1,直播分机号分机B-1001")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P1", "IVR", "InboundRoute","Basic", "Trunk", "testIVR02_trunk","SPS","PSeries","Cloud","K2"}, dataProvider = "routes")
    public void testIVR02_trunk(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.通过sps外线呼入到IVR1按分机号1001到分机B,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(3000);
        pjsip.Pj_Send_Dtmf(caller, "1","0","0","1");
        sleep(WaitUntils.SHORT_WAIT);
        assertStep("[通话状态校验：分机1001为Ring]");
        Assert.assertEquals(getExtensionStatus(1001,RING,20),RING);
        pjsip.Pj_Answer_Call(1001,false);
        sleep(WaitUntils.SHORT_WAIT );
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING,20)).as("[通话状态校验]").isEqualTo(TALKING);

        pjsip.Pj_hangupCall(1001);

        assertStep("9:[CDR显示]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验_2] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1, "ANSWERED", "2000<2000> dialed Extension".replace("2000",caller+""),trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "test2 B<1001>", "ANSWERED", "test2 B<1001> hung up",trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @SneakyThrows
    @Epic("P_Series")
    @Feature("IVR")
    @Story("Prompt，PromptRepeatCount，ResponseTimeout，DigitTimeout")
    @Description("通过sip外线呼入到IVR1\n" +
            "1.进入asterisk检查播放提示音文件ivr-greeting-dial-ext.slin\n" +
            "2.进入asterisk检查播放提示音文件的间隔为3秒\n" +
            "3.进入asterisk检查播放3遍后再等待3秒通话被自动挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P2", "IVR", "Prompt","testIVR04_PPRD_1","PromptRepeatCount","ResponseTimeout","DigitTimeout","PSeries","Cloud","K2"}, dataProvider = "routes")
    public void testIVR04_PPRD_1(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite(true);
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread = new SSHLinuxUntils.AsteriskThread(asteriskObjectList, IVR_GREETING_DIAL_EXT);
        thread.start();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();
        asteriskObjectList.clear();
        step("2.通过sip外线呼入到IVR1,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        int tmp = 0;
        while (asteriskObjectList.size() !=3 && tmp <= 800){
            sleep(50);
            tmp++;
            log.debug("[tmp]_"+tmp);
        }
        if(tmp == 801){
            for(int i = 0 ; i < asteriskObjectList.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());
        }
        thread.flag =  false;
        sleep(3000);

        assertStep("[Asterisk校验]进入asterisk检查播放提示音文件ivr-greeting-dial-ext.slin 播放3遍提示音后挂断");
        softAssertPlus.assertThat(getExtensionStatus(caller,HUNGUP,60)).as("[Asterisk校验] 播放3遍提示音后挂断 "+ DataUtils.getCurrentTime()).isEqualTo(4);

        assertStep("[Asterisk校验]进入asterisk检查播放提示音文件ivr-greeting-dial-ext.slin");
        softAssertPlus.assertThat(asteriskObjectList.get(0).getName()).as("[Asterisk校验] Time："+ DataUtils.getCurrentTime()).contains("ivr-greeting-dial-ext.slin");

        log.debug("[asterisk object size] "+ asteriskObjectList.size());
        for(int i = 0 ; i < asteriskObjectList.size() ; i++){
            log.debug(i+"_[asterisk object name] "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
        }
//
        assertStep("[Asterisk校验]进入asterisk检查播放提示音文件ivr-greeting-dial-ext.slin 3遍");
        softAssertPlus.assertThat(asteriskObjectList.size()).as("[Asterisk校验] Time："+ DataUtils.getCurrentTime()).isEqualTo(3);
        softAssertPlus.assertAll();
    }

    @SneakyThrows
    @Epic("P_Series")
    @Feature("IVR")
    @Story("Prompt，PromptRepeatCount，ResponseTimeout，DigitTimeout")
    @Description("通过sip外线呼入到IVR1\n" +
            "进入asterisk检查播放提示音文件ivr-greeting-dial-ext.slin第2遍时，按0到分机A" +
            "检查通话正常建立，cdr正常")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P2", "IVR", "Prompt","testIVR06_PPRD_2","PromptRepeatCount","ResponseTimeout","DigitTimeout","PSeries","Cloud","K2"}, dataProvider = "routes")
    public void testIVR05_PPRD_1_2(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite(true);//启动子线程，监控asterisk log
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread = new SSHLinuxUntils.AsteriskThread(asteriskObjectList, IVR_GREETING_DIAL_EXT);
        thread.start();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.通过sps外线呼入到IVR1,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 2 && tmp++ < 800){
            sleep(50);
        }
        if(tmp == 801){
            for(int i = 0 ; i < asteriskObjectList.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(caller,"0");
        assertStep("[Asterisk校验]");
        softAssertPlus.assertThat(getExtensionStatus(1000,RING,30)).as("[Asterisk校验] 播放2 遍提示音后，按0，转分机 "+ DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000,false);
        sleep(3000);
        pjsip.Pj_hangupCall(1000);


        log.debug("[asterisk object size] "+ asteriskObjectList.size());
        for(int i = 0 ; i < asteriskObjectList.size() ; i++){
            log.debug(i+"_[asterisk object name] "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
        }

        assertStep("[CDR显示]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1, "ANSWERED", "2000<2000> called Extension".replace("2000",caller+""),trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "test A<1000> hung up",trunk,"","Inbound"));

        softAssertPlus.assertAll();

    }

    @SneakyThrows
    @Epic("P_Series")
    @Feature("IVR")
    @Story("Prompt，PromptRepeatCount，ResponseTimeout，DigitTimeout")
    @Description("1.通过sip外线呼入到IVR1\n" +
            "2.编辑IVR1,Prompt选择【None】，PromptRepeatCount 选择1，ResponseTimeout选择1，DigitTimeout选择1" +
            "通过sip外线呼入到IVR1\n" +
            "\t通话1秒就被挂断，cdr正常")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P3", "IVR", "Prompt","testIVR06_PPRD_2","PromptRepeatCount","ResponseTimeout","DigitTimeout","PSeries","Cloud","K2"}, dataProvider = "routes")
    public void testIVR06_PPRD_2(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite(true);
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread = new SSHLinuxUntils.AsteriskThread(asteriskObjectList, IVR_GREETING_DIAL_EXT);
//        thread.start();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1,Prompt选择【None】，PromptRepeatCount 选择1，ResponseTimeout选择1，DigitTimeout选择1");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);

        auto.ivrPage().editIVR("IVR1").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_extra_prompt,"None").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_prompt_repeat,"1").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_resp_timeout,"1").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_digit_timeout,"1").
                clickSaveAndApply();

        step("2.通过sps外线呼入到IVR1,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        //todo 时间太短无法校验，后续通过解析asterisk进行校验
        //softAssertPlus.assertThat(getExtensionStatus(1000,TALKING,3)).as("[通话状态校验] Time："+ DataUtils.getCurrentTime()).isEqualTo(3);
        sleep(WaitUntils.SHORT_WAIT);
        assertStep("[CDR显示]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","callDuration","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1, "1","ANSWERED", "IVR IVR1<6201> timed out, hung up",trunk,"","Inbound"));

        softAssertPlus.assertAll();

    }
    @SneakyThrows
    @Epic("P_Series")
    @Feature("IVR")
    @Story("Prompt，PromptRepeatCount，ResponseTimeout，DigitTimeout")
    @Description("1.通过sip外线呼入到IVR1\n" +
            "3.编辑IVR1,Prompt选择prompt1，PromptRepeatCount 选择2，ResponseTimeout选择5，DigitTimeout选择1" +
            "通过sip外线呼入到IVR1\n" +
            "\t进入asterisk检查播放提示音文件prompt1\n" +
            "\t播放第2遍prompt1时，等3秒按0\n" +
            "\t\t检查通话正常建立，cdr正常")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P3", "IVR", "Prompt","testIVR07_PPRD_3","PromptRepeatCount","ResponseTimeout","DigitTimeout","PSeries","Cloud","K2"}, dataProvider = "routes")
    public void testIVR07_PPRD_3(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite(true);
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread = new SSHLinuxUntils.AsteriskThread(asteriskObjectList, PROMPT_1);
        thread.start();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.进入IVR");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);

        step("3.编辑IVR1,Prompt选择prompt1，PromptRepeatCount 选择2，ResponseTimeout选择5，DigitTimeout选择1");
        auto.ivrPage().editIVR("IVR1").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_extra_prompt,"prompt1.wav").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_prompt_repeat,"2").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_resp_timeout,"5").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_digit_timeout,"1").
                clickSaveAndApply();

        step("2.通过sps外线呼入到IVR1,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        assertStep("[asterisk]播放第2遍prompt1时，等3秒按0,[caller]:"+caller);
        int tmp = 0;
        while (asteriskObjectList.size() != 2 && tmp++ < 600){//30s
            sleep(50);
        }
        if(tmp == 601){
            for(int i = 0 ; i < asteriskObjectList.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(caller,"0");//dtmf默认等待3s

        assertStep("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,30),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,5),TALKING,"[通话状态校验_通话] Time："+ DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_hangupCall(1000);

        assertStep("[asterisk]检查播放提示音文件prompt1");
        softAssertPlus.assertThat(asteriskObjectList.get(0).getName()).as("[检查播放提示音文件prompt1] Time："+ DataUtils.getCurrentTime()).contains("prompt1.slin");

        assertStep("[CDR显示]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1,"ANSWERED", "2000<2000> called Extension".replace("2000",caller+""),trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "test A<1000> hung up",trunk,"","Inbound"));
//
//        log.debug("[asterisk object size 第二遍] " + tmp + "----------" + asteriskObjectList.size());
//        for(int i = 0 ; i < asteriskObjectList.size() ; i++){
//            log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
//        }
        softAssertPlus.assertAll();
    }

    @SneakyThrows
    @Epic("P_Series")
    @Feature("IVR")
    @Story("Prompt，PromptRepeatCount，ResponseTimeout，DigitTimeout")
    @Description("1.通过sip外线呼入到IVR1\n" +
            "4.编辑IVR1,Prompt选择prompt1~prompt5，PromptRepeatCount 选择5，ResponseTimeout设置10，DigitTimeout设置10" +
            "通过sip外线呼入到IVR1\n" +
            "\t进入asterisk检查播放提示音文件prompt1~prompt5\n" +
            "\t播放第5遍时按分机1002，每发送一个dtmf时间隔8秒\n" +
            "\t\t分机1002响铃，通话正常建立，cdr正常")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P3", "IVR", "Prompt","testIVR08_PPRD_4","PromptRepeatCount","ResponseTimeout","DigitTimeout","PSeries","Cloud","K2"}, dataProvider = "routes")
    public void testIVR08_PPRD_4(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite(true);
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread = new SSHLinuxUntils.AsteriskThread(asteriskObjectList, "Playing 'record/prompt");
        thread.start();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.进入IVR");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);

        step("3.编辑IVR1,Prompt选择prompt1~prompt5，PromptRepeatCount 选择5，ResponseTimeout设置10，DigitTimeout设置10");
        auto.ivrPage().editIVR("IVR1").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_extra_prompt,"prompt1.wav,prompt2.wav,prompt3.wav,prompt4.wav,prompt5.wav").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_prompt_repeat,"5").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_resp_timeout,"10").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_digit_timeout,"10").
                clickSaveAndApply();

        step("2.通过sps外线呼入到IVR1,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        int tmp = 0;
        while (asteriskObjectList.size() != 5 && tmp++ < 600){
            sleep(50);
        }
        if(tmp == 601){
            for(int i = 0 ; i < asteriskObjectList.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());
        }
        thread.flag = false;

        step("播放第5遍时按分机1002，每发送一个dtmf时间隔8秒");//send dtmf 5s
        pjsip.Pj_Send_Dtmf(caller,"1");
        sleep(8000);
        pjsip.Pj_Send_Dtmf(caller,"0");
        sleep(8000);
        pjsip.Pj_Send_Dtmf(caller,"0");
        sleep(8000);
        pjsip.Pj_Send_Dtmf(caller,"2");

        assertStep("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1002,RING,60),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1002,false);
        Assert.assertEquals(getExtensionStatus(1002,TALKING,5),TALKING,"[通话状态校验_通话] Time："+ DataUtils.getCurrentTime());
        sleep(3000);
        pjsip.Pj_hangupCall(caller);

        for(int i = 0 ; i < asteriskObjectList.size() ; i++){
            log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
        }

        assertStep("[Asterisk校验] 检查播放提示音文件prompt1~prompt5");
        softAssertPlus.assertThat(asteriskObjectList).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("keyword")
                .contains("record/prompt1.slin", "record/prompt2.slin", "record/prompt3.slin", "record/prompt4.slin", "record/prompt5.slin");

        assertStep("[CDR显示]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), CDRNAME.IVR1_6201.toString(), "ANSWERED", "2000<2000> dialed Extension".replace("2000",caller+""),trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "testta C<1002>", "ANSWERED", "3001<3001> hung up",trunk,"","Inbound"));
        softAssertPlus.assertAll();
    }
    @SneakyThrows
    @Epic("P_Series")
    @Feature("IVR")
    @Story("Prompt，PromptRepeatCount，ResponseTimeout，DigitTimeout")
    @Description("通过sip外线呼入到IVR1\n" +
            "5.编辑IVR1，Prompt选择【Default】,PromptRepeatCount 选择3，ResponseTimeout设置3，DigitTimeout设置3" +
            "通过sip外线呼入到IVR1\n" +
            "\t进入asterisk检查播放提示音文件ivr-greeting-dial-ext.slin\n" +
            "\t\t检查通话正常建立，cdr正常")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Test(groups = {"P3", "IVR", "Prompt","testIVR09_PPRD_5","PromptRepeatCount","ResponseTimeout","DigitTimeout","PSeries","Cloud","K2"}, dataProvider = "routes")
    public void testIVR09_PPRD_5(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite(true);
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread = new SSHLinuxUntils.AsteriskThread(asteriskObjectList, IVR_GREETING_DIAL_EXT);
        thread.start();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.进入IVR");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);

        step("3.编辑IVR1，Prompt选择【Default】,PromptRepeatCount 选择3，ResponseTimeout设置3，DigitTimeout设置3");
        auto.ivrPage().editIVR("IVR1").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_extra_prompt,"Default").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_prompt_repeat,"5").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_resp_timeout,"3").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_digit_timeout,"3").
                clickSaveAndApply();

        step("2.通过sps外线呼入到IVR1,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);
        int tmp = 0;
        while (asteriskObjectList.size() !=1 && tmp++ < 600){
            sleep(50);
        }
        if(tmp == 601){
            for(int i = 0 ; i < asteriskObjectList.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());
        }
        thread.flag = false;
        step("[asteriskObjectList] "+asteriskObjectList.size() +"【tmp】"+tmp);
        for(int i = 0 ; i < asteriskObjectList.size() ; i++){
            log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
        }

        assertStep("[通话状态校验]");
        pjsip.Pj_Send_Dtmf(caller,"0");
        Assert.assertEquals(getExtensionStatus(1000,RING,60),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,20),TALKING,"[通话状态校验_通话] Time："+ DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR显示]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), CDRNAME.IVR1_6201.toString(), "ANSWERED", "2000<2000> called Extension".replace("2000",caller+""),trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "test A<1000> hung up",trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.通过sip外线呼入到IVR1按1020到分机FXS\n" +
            "2.预期辅助2的分机2000响铃，检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","DialExtension", "testIVR10_DialExtension_1","FXS","PSeries"}, dataProvider = "routes")
    public void testIVR10_DialExtension_1(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2:通过sip外线呼入到IVR1按1020到分机FXS");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);
        //1020--2005
        pjsip.Pj_Send_Dtmf(caller, "1","0","2","0");
        assertStep("[通话状态校验：辅助2的2000分机响铃]");
        softAssertPlus.assertThat(getExtensionStatus(2000,RING,30)).as("[通话校验]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000,false);
        softAssertPlus.assertThat(getExtensionStatus(2000,TALKING,30)).as("[通话校验]").isEqualTo(TALKING);
        pjsip.Pj_hangupCall(caller);

//       assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
//        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
//        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime())
//                .extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
//                .contains(tuple(CALLFROM.SIP, CDRNAME.IVR1, STATUS.ANSWER, REASON.SIP_DIALED_EXTENSION,trunk,"", COMMUNICATION_TYPE.INBOUND))
//                .contains(tuple(CALLFROM.SIP, CDRNAME.Extension_1020, STATUS.ANSWER, REASON.SIP_HUNGUP,trunk,"", COMMUNICATION_TYPE.INBOUND));

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("3001<3001>", "IVR IVR1<6201>", "ANSWERED", "3001<3001> dialed Extension",trunk, "", "Inbound"))
                .contains(tuple("3001<3001>", "1020 1020<1020>", "ANSWERED", "3001<3001> hung up", trunk, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，禁用Dial Extensions，选择disable\n" +
            "2.通过sps外线呼入到IVR1按分机号1001到分机B\n" +
            "3.分机B不会响铃，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2", "IVR","DialExtension", "testIVR11_DialExtension_2"}, dataProvider = "routes")
    public void testIVR11_DialExtension_2(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，禁用Dial Extensions，选择disable");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
        auto.ivrPage().editIVR("IVR1").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_dial_ext_option, IIVRPageElement.DIAL_EXTENSIONS.DISABLE.getAlias()).
                clickSaveAndApply();

        step("3:通过sps外线呼入到IVR1按分机号1001到分机B");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);

        pjsip.Pj_Send_Dtmf(caller, "1","0","0","1");
        step("[通话状态校验]分机B不会响铃，通话被挂断");
        int result =getExtensionStatus(1001,RING,10);
        Assert.assertTrue((result ==HUNGUP) ||  (result ==IDLE));
//        Assert.assertEquals(getExtensionStatus(1001, RING,20),0,"[通话校验]");
        sleep(10000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1, "ANSWERED", invalidKey ,trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tAllowed Extensions，分机选择ExGroup1、1001、1003\n" +
            "2.通过sps外线呼入到IVR1，按分机号1000\n" +
            "3.检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2", "IVR","DialExtension", "testIVR12_DialExtension_3","PSeries","Cloud","K2"}, dataProvider = "routes")
    public void testIVR12_DialExtension_3(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，选择\tAllowed Extensions，分机选择ExGroup1、1001、1003");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
        auto.ivrPage().editIVR("IVR1").setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_dial_ext_option, IIVRPageElement.DIAL_EXTENSIONS.ALLOWED_EXTENSIONS.getAlias()).
                selectAllowExtensionsToRight("ExGroup1","1001","1003").
                clickSaveAndApply();

        step("3:通过sps外线呼入到IVR1，按分机号1000");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);
        pjsip.Pj_Send_Dtmf(caller, "1","0","0","0");
        step("[通话状态校验] 分机A响铃，可正常接听，分机1000挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1000,RING,10)).as("[通话校验]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000,false);
        softAssertPlus.assertThat(getExtensionStatus(1000,TALKING,5)).as("[通话校验]").isEqualTo(TALKING);
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1, "ANSWERED", "2000<2000> dialed Extension".replace("2000",caller+""),trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "test A<1000> hung up",trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tAllowed Extensions，分机选择ExGroup1、1001、1003\n" +
            "2.通过sps外线呼入到IVR1，按分机号1002\n" +
            "3.分机1002不会响铃，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2", "IVR","DialExtension", "testIVR13_DialExtension_4","PSeries","Cloud","K2"}, dataProvider = "routes")
    public void testIVR13_DialExtension_4(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，选择\tAllowed Extensions，分机选择ExGroup1、1001、1003");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
        auto.ivrPage().editIVR("IVR1").setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_dial_ext_option, IIVRPageElement.DIAL_EXTENSIONS.ALLOWED_EXTENSIONS.getAlias()).
                selectAllowExtensionsToRight("ExGroup1","1001","1003").
                clickSaveAndApply();

        step("3:通过sps外线呼入到IVR1，按分机号1002");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);

        pjsip.Pj_Send_Dtmf(caller, "1","0","0","2");
        step("[通话状态校验] 分机1002不会响铃，通话被挂断");
        int result =getExtensionStatus(1002,RING,10);
        Assert.assertTrue((result ==HUNGUP) ||  (result ==IDLE));
//        softAssertPlus.assertThat(getExtensionStatus(1002,IDLE,20)).as("[通话校验]").isEqualTo(IDLE);
        sleep(10000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1, "ANSWERED", invalidKey ,trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tAllowed Extensions，分机选择ExGroup1、1001、1003\n" +
            "2.通过sps外线呼入到IVR1，按分机号1001\n" +
            "3.分机1001响铃，接听，外线主叫挂断，检查cdr.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","DialExtension", "testIVR14_DialExtension_5","PSeries","Cloud","K2"}, dataProvider = "routes")
    public void testIVR14_DialExtension_5(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，选择\tAllowed Extensions，分机选择ExGroup1、1001、1003");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
        auto.ivrPage().editIVR("IVR1").setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_dial_ext_option, IIVRPageElement.DIAL_EXTENSIONS.ALLOWED_EXTENSIONS.getAlias()).
                selectAllowExtensionsToRight("ExGroup1","1001","1003").
                clickSaveAndApply();

        step("3:通过sps外线呼入到IVR1按分机号1001到分机B");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);

        pjsip.Pj_Send_Dtmf(caller, "1","0","0","1");
        step("[通话状态校验] 分机1001响铃，接听，外线主叫挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1001,RING,20)).as("[通话校验]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001,false);
        softAssertPlus.assertThat(getExtensionStatus(1001,TALKING,20)).as("[通话校验]").isEqualTo(TALKING);
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1, "ANSWERED", "2000<2000> dialed Extension".replace("2000",caller+""),trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "test2 B<1001>", "ANSWERED", "2000<2000> hung up".replace("2000",caller+""),trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tAllowed Extensions，分机选择ExGroup1、1001、1003\n" +
            "2.内部分机1004呼入到IVR1，按分机号1003\n" +
            "3.分机1003响铃，接听，1004主叫挂断，检查cdr.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","DialExtension", "testIVR15_DialExtension_6","PSeries","Cloud","K2"})
    public void testIVR15_DialExtension_6() {
        prerequisite(true);

        step("1:login with admin,trunk: 内部分机");
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，选择\tAllowed Extensions，分机选择ExGroup1、1001、1003");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
        auto.ivrPage().editIVR("IVR1").setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_dial_ext_option, IIVRPageElement.DIAL_EXTENSIONS.ALLOWED_EXTENSIONS.getAlias()).
                selectAllowExtensionsToRight("ExGroup1","1001","1003").
                clickSaveAndApply();

        step("3:内部分机1004呼入到IVR1，按分机号1003");
        pjsip.Pj_Make_Call_No_Answer(1004, "6201", DEVICE_IP_LAN, false);
        sleep(WaitUntils.SHORT_WAIT*3);

        pjsip.Pj_Send_Dtmf(1004, "1","0","0","3");
        step("[通话状态校验] 分机1003响铃，接听，1004主叫挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1003,RING,5)).as("[通话校验]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1003,false);
        softAssertPlus.assertThat(getExtensionStatus(1003,TALKING,5)).as("[通话校验]").isEqualTo(TALKING);
        pjsip.Pj_hangupCall(1004);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("t estX<1004>", cdrIVR1, "ANSWERED", "t estX<1004> dialed Extension","","","Internal"))
                .contains(tuple("t estX<1004>", "testa D<1003>", "ANSWERED", "t estX<1004> hung up","","","Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tAllowed Extensions，分机选择ExGroup1、1001、1003\n" +
            "编辑分机选择去掉1001、1003"+
            "2.7.通过sps外线呼入到IVR1，按分机号1001\n" +
            "3.分机B不会响铃，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","DialExtension", "testIVR16_DialExtension_7","PSeries","Cloud","K2"}, dataProvider = "routes")
    public void testIVR16_DialExtension_7(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        step("2.编辑IVR1，选择\tAllowed Extensions，分机选择ExGroup1、1001、1003,编辑分机选择去掉1001、1003");
        apiUtil.editIVR("6201",String.format("\"dial_ext_option\":\"allow\",\"dial_ext_list\":[{\"text\":\"ExGroup1\",\"text2\":\"ExGroup1\",\"value\":\"%s\",\"type\":\"ext_group\"}]",apiUtil.getExtensionGroupSummary("ExGroup1").id)).apply();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("3:7.通过sps外线呼入到IVR1，按分机号1001");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);

        pjsip.Pj_Send_Dtmf(caller, "1","0","0","1");
        step("[通话状态校验] 分机1001响铃，接听，外线主叫挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1001,RING,20)).as("[通话校验]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001,false);
        softAssertPlus.assertThat(getExtensionStatus(1001,TALKING,20)).as("[通话校验]").isEqualTo(TALKING);
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1, "ANSWERED", "2000<2000> dialed Extension".replace("2000",caller+""),trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "test2 B<1001>", "ANSWERED", "2000<2000> hung up".replace("2000",caller+""),trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tAllowed Extensions，分机选择ExGroup1、1001、1003\n" +
            "编辑分机选择去掉1001、1003"+
            "2.内部分机1004呼入到IVR1，按分机号1003\n" +
            "3.分机D不会响铃，通话被挂断.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","DialExtension", "testIVR11_DialExtension_2","PSeries","Cloud","K2"})
    public void testIVR17_DialExtension_8() {
        prerequisite(true);
        step("2.编辑IVR1，选择\tAllowed Extensions，分机选择ExGroup1、1001、1003,编辑分机选择去掉1001、1003");
        apiUtil.editIVR("6201",String.format("\"dial_ext_option\":\"allow\",\"dial_ext_list\":[{\"text\":\"ExGroup1\",\"text2\":\"ExGroup1\",\"value\":\"%s\",\"type\":\"ext_group\"}]",apiUtil.getExtensionGroupSummary("ExGroup1").id)).apply();

        step("1:login with admin,trunk: 内部分机");
        auto.loginPage().loginWithAdmin();

        step("3:8.内部分机1004呼入到IVR1，按分机号1003");
        pjsip.Pj_Make_Call_No_Answer(1004, "6201", DEVICE_IP_LAN, false);
        sleep(WaitUntils.SHORT_WAIT*3);

        pjsip.Pj_Send_Dtmf(1004, "1","0","0","3");
        step("[通话状态校验] 分机D不会响铃，通话被挂断");
        int result =getExtensionStatus(1003,RING,30);
        Assert.assertTrue((result ==HUNGUP) ||  (result ==IDLE));

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("t estX<1004>", cdrIVR1, "ANSWERED", "Invalid key","","","Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tRestricted Extensions，分机选择ExGroup1、1001、1003\n" +
            "2.通过外线sps外线呼入到IVR1，按分机号1000\n" +
            "3.分机A不会响铃，通话被挂断.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2", "IVR","DialExtension", "testIVR18_DialExtension_9","PSeries","Cloud","K2"}, dataProvider = "routes")
    public void testIVR18_DialExtension_9(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，选择\tRestricted Extensions，分机选择ExGroup1、1001、1003");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
        auto.ivrPage().editIVR("IVR1").setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_dial_ext_option, IIVRPageElement.DIAL_EXTENSIONS.RESTRICTED_EXTENSIONS.getAlias()).
                selectAllowExtensionsToRight("ExGroup1","1001","1003").
                clickSaveAndApply();

        step("3:通过外线sps外线呼入到IVR1，按分机号1000");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);

        pjsip.Pj_Send_Dtmf(caller, "1","0","0","0");
        step("[通话状态校验] 分机A不会响铃，通话被挂断");
        int result =getExtensionStatus(1000,RING,10);
        Assert.assertTrue((result ==HUNGUP) ||  (result ==IDLE));
//        Assert.assertEquals(getExtensionStatus(1000,RING,20),0);
        Assert.assertEquals(getExtensionStatus(caller,HUNGUP,20),4);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1, "ANSWERED", "Invalid key",trunk,"","Inbound"));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tRestricted Extensions，分机选择ExGroup1、1001、1003\n" +
            "2.通过sps外线呼入到IVR1，按分机号1002\n" +
            "3.分机C响铃，可正常接听，挂断，检查cdr.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2", "IVR","DialExtension", "testIVR19_DialExtension_10","PSeries","Cloud","K2"}, dataProvider = "routes")
    public void testIVR19_DialExtension_10(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，选择\tRestricted Extensions，分机选择ExGroup1、1001、1003");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
        auto.ivrPage().editIVR("IVR1").setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_dial_ext_option, IIVRPageElement.DIAL_EXTENSIONS.RESTRICTED_EXTENSIONS.getAlias()).
                selectAllowExtensionsToRight("ExGroup1","1001","1003").
                clickSaveAndApply();

        step("3:通过sps外线呼入到IVR1，按分机号1002,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);

        pjsip.Pj_Send_Dtmf(caller, "1","0","0","2");
        step("[通话状态校验] 分机C响铃，可正常接听，挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1002,RING,20)).as("[通话校验]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1002,false);
        softAssertPlus.assertThat(getExtensionStatus(1002,TALKING,20)).as("[通话校验]").isEqualTo(TALKING);
        pjsip.Pj_hangupCall(1002);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1, "ANSWERED", "2000<2000> dialed Extension",trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "testta C<1002>", "ANSWERED", "testta C<1002> hung up",trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tRestricted Extensions，分机选择ExGroup1、1001、1003\n" +
            "2.通过sps外线呼入到IVR1，按分机号1001\n" +
            "3.分机B不会响铃，通话被挂断.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","DialExtension", "testIVR20_DialExtension_11","PSeries","Cloud","K2"}, dataProvider = "routes")
    public void testIVR20_DialExtension_11(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，选择\tRestricted Extensions，分机选择ExGroup1、1001、1003");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
        auto.ivrPage().editIVR("IVR1").setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_dial_ext_option, IIVRPageElement.DIAL_EXTENSIONS.RESTRICTED_EXTENSIONS.getAlias()).
                selectAllowExtensionsToRight("ExGroup1","1001","1003").
                clickSaveAndApply();

        step("3:通过sps外线呼入到IVR1，按分机号1001,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);

        pjsip.Pj_Send_Dtmf(caller, "1","0","0","1");
        step("[通话状态校验] 分机B不会响铃，通话被挂断");
        int result =getExtensionStatus(1001,RING,10);
        Assert.assertTrue((result ==HUNGUP) ||  (result ==IDLE));
//        Assert.assertEquals(getExtensionStatus(1001,RING,20),0);
        Assert.assertEquals(getExtensionStatus(caller,HUNGUP,20),4);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1, "ANSWERED", "Invalid key",trunk,"","Inbound"));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tRestricted Extensions，分机选择ExGroup1、1001、1003\n" +
            "2.内部分机1004呼入到IVR1，按分机号1003\n" +
            "3.分机D不会响铃，通话被挂断.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","DialExtension", "testIVR21_DialExtension_12","PSeries","Cloud","K2"})
    public void testIVR21_DialExtension_12() {
        prerequisite(true);

        step("1:login with admin,trunk: 内部分机");
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，选择\tRestricted Extensions，分机选择ExGroup1、1001、1003");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
        auto.ivrPage().editIVR("IVR1").setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_dial_ext_option, IIVRPageElement.DIAL_EXTENSIONS.RESTRICTED_EXTENSIONS.getAlias()).
                selectAllowExtensionsToRight("ExGroup1","1001","1003").
                clickSaveAndApply();

        step("3:内部分机1004呼入到IVR1，按分机号1003");
        pjsip.Pj_Make_Call_No_Answer(1004, "6201", DEVICE_IP_LAN, false);
        sleep(WaitUntils.SHORT_WAIT*3);

        pjsip.Pj_Send_Dtmf(1004, "1","0","0","3");
        step("[通话状态校验] 分机D不会响铃，通话被挂断");
        int result =getExtensionStatus(1003,RING,10);
        Assert.assertTrue((result ==HUNGUP) ||  (result ==IDLE));
//        Assert.assertEquals(getExtensionStatus(1003,RING,20),0);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("t estX<1004>", cdrIVR1, "ANSWERED", "Invalid key","","","Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tRestricted Extensions，分机选择ExGroup1、1001、1003;编辑分机选择去掉1001、1003\n" +
            "13.通过sps外线呼入到IVR1，按分机号1001\n" +
            "\t分机1001不会响铃，外线主叫挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","DialExtension", "testIVR22_DialExtension_13","PSeries","Cloud","K2"}, dataProvider = "routes")
    public void testIVR22_DialExtension_13(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite(true);
        step("编辑IVR1，选择\tRestricted Extensions，分机选择ExGroup1、1001、1003;编辑分机选择去掉1001、1003");
        apiUtil.editIVR("6201",String.format("\"dial_ext_option\":\"restrict\",\"restrict_dial_ext_list\":[{\"text\":\"ExGroup1\",\"text2\":\"ExGroup1\",\"value\":\"%s\",\"type\":\"ext_group\"}]",apiUtil.getExtensionGroupSummary("ExGroup1").id)).apply();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("3:通过sps外线呼入到IVR1，按分机号1001,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);

        pjsip.Pj_Send_Dtmf(caller, "1","0","0","1");
        step("[通话状态校验] 分机1001不会响铃，外线主叫挂断，检查cdr");
        int result =getExtensionStatus(1001,RING,10);
        Assert.assertTrue((result ==HUNGUP) ||  (result ==IDLE));
        int result1 =getExtensionStatus(caller,HUNGUP,10);
        Assert.assertTrue((result1 ==HUNGUP) ||  (result1 ==IDLE));
        sleep(5000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1, "ANSWERED", "Invalid key",trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tRestricted Extensions，分机选择ExGroup1、1001、1003;编辑分机选择去掉1001、1003\n" +
            "2.内部分机1004呼入到IVR1，按分机号1003\n" +
            "3.分机1003响铃，接听，1004主叫挂断，检查cdr.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","DialExtension", "testIVR23_DialExtension_14","PSeries","Cloud","K2"})
    public void testIVR23_DialExtension_14() {
        prerequisite(true);
        step("编辑IVR1，选择\tRestricted Extensions，分机选择ExGroup1、1001、1003;编辑分机选择去掉1001、1003");
        apiUtil.editIVR("6201",String.format("\"dial_ext_option\":\"restrict\",\"restrict_dial_ext_list\":[{\"text\":\"ExGroup1\",\"text2\":\"ExGroup1\",\"value\":\"%s\",\"type\":\"ext_group\"}]",apiUtil.getExtensionGroupSummary("ExGroup1").id)).apply();

        step("1:login with admin,trunk: 内部分机 ");
        auto.loginPage().loginWithAdmin();

        step("3:内部分机1004呼入到IVR1，按分机号1003");
        pjsip.Pj_Make_Call_No_Answer(1004, "6201", DEVICE_IP_LAN, false);
        sleep(WaitUntils.SHORT_WAIT*3);

        pjsip.Pj_Send_Dtmf(1004, "1","0","0","3");
        step("[通话状态校验] 分机1003响铃，接听，1004主叫挂断，检查cdr ");
        softAssertPlus.assertThat(getExtensionStatus(1003,RING,30)).as("[通话校验]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1003,false);
        softAssertPlus.assertThat(getExtensionStatus(1003,TALKING,20)).as("[通话校验]").isEqualTo(TALKING);
        pjsip.Pj_hangupCall(1004);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("t estX<1004>", cdrIVR1, "ANSWERED", "t estX<1004> dialed Extension","","","Internal"))
                .contains(tuple("t estX<1004>", "testa D<1003>", "ANSWERED", "t estX<1004> hung up","","","Internal"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tAll Extensions\n" +
            "2.通过sps外线呼入到IVR1按分机号1001到分机B\n" +
            "3.检查通话正常建立，cdr正常.")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P1", "IVR","DialExtension", "testIVR24_DialExtension_15","Basic","Trunk","PSeries","Cloud","K2"}, dataProvider = "routes")
    public void testIVR24_DialExtension_15(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，选择\tRestricted Extensions，分机选择ExGroup1");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
        auto.ivrPage().editIVR("IVR1").setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_dial_ext_option, IIVRPageElement.DIAL_EXTENSIONS.ALL_EXTENSIONS.getAlias()).
                clickSaveAndApply();

        step("3:通过sps外线呼入到IVR1按分机号1001到分机B,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);

        pjsip.Pj_Send_Dtmf(caller, "1001");

        assertStep("[通话状态校验]");
        softAssertPlus.assertThat(getExtensionStatus(1001,RING,20)).as("[通话校验]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001,false);
        softAssertPlus.assertThat(getExtensionStatus(1001,TALKING,20)).as("[通话校验]").isEqualTo(TALKING);
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1, "ANSWERED", "2000<2000> dialed Extension",trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "test2 B<1001>", "ANSWERED", "test2 B<1001> hung up",trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tAll Extensions\n" +
            "16.通过sps外线呼入到IVR1按RingGroup0的号码6300\n" +
            "通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","DialExtension", "testIVR11_DialExtension_2","PSeries","Cloud","K2"}, dataProvider = "routes")
    public void testIVR25_DialExtension_16(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) throws IOException, JSchException {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，选择\tRestricted Extensions，分机选择ExGroup1");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
        auto.ivrPage().editIVR("IVR1").setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_dial_ext_option, IIVRPageElement.DIAL_EXTENSIONS.ALL_EXTENSIONS.getAlias()).
                clickSaveAndApply();

        step("新增动态坐席：1003\\1004分别拨打*76400 加入到Queue0");
        String result = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI,"queue show queue-6400"));
        log.debug("[queue show queue-6400] "+result);
        if(!result.contains("1003")){
            pjsip.Pj_Make_Call_Auto_Answer(1003,"*76400", DEVICE_IP_LAN);
        }
        if(!result.contains("1004")){
            pjsip.Pj_Make_Call_Auto_Answer(1004,"*76400", DEVICE_IP_LAN);
        }
        String resultAfter = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI,"queue show queue-6400"));
        log.debug("[queue show queue-6400 resultAfter] "+resultAfter);

        step("3:16.通过sps外线呼入到IVR1按RingGroup0的号码6300，[caller]:"+caller+" ,[callee]:"+routePrefix + callee);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);
        pjsip.Pj_Send_Dtmf(caller, "6","3","0","0");

        assertStep(caller + "[通话状态校验]");
//        int result1 =getExtensionStatus(caller,HUNGUP,20);
//        Assert.assertTrue((result1 ==HUNGUP) ||  (result1 ==IDLE));
        Assert.assertEquals(getExtensionStatus(caller,HUNGUP,20),HUNGUP);
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>", "IVR IVR1<6201>", "ANSWERED", "Invalid key", trunk, "", "Inbound"));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialExtension")
    @Description("1.编辑IVR1，选择\tAllowed Extensions，分机选择ExGroup1、1001、1003\n" +
            "编辑分机选择去掉1001、1003"+
            "2.17.通过外线sps外线呼入到IVR1，按0\n" +
            "3.分机A正常响铃，接听，通话挂断，cdr正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","DialExtension", "testIVR26_DialExtension_17","PSeries","Cloud","K2"}, dataProvider = "routes")
    public void testIVR26_DialExtension_17(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite(true);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，选择\tAllowed Extensions，分机选择ExGroup1、1001、1003");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
        auto.ivrPage().editIVR("IVR1").
                setElementValueWithClean(auto.ivrPage().ele_ivr_basic_ivr_basic_dial_ext_option, IIVRPageElement.DIAL_EXTENSIONS.ALLOWED_EXTENSIONS.getAlias()).
                selectAllowExtensionsToRight("ExGroup1").
                clickSaveAndApply();

        step("3:7.通过sps外线呼入到IVR1，按分机号1001");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);

        pjsip.Pj_Send_Dtmf(caller, "0");
        step("[通话状态校验] 分机1001响铃，接听，外线主叫挂断，检查cdr");
        softAssertPlus.assertThat(getExtensionStatus(1000,RING,60)).as("[通话校验]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000,false);
        softAssertPlus.assertThat(getExtensionStatus(1000,TALKING,20)).as("[通话校验]").isEqualTo(TALKING);
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), CDRNAME.IVR1_6201.toString(), "ANSWERED", "2000<2000> called Extension".replace("2000",caller+""),trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "2000<2000> hung up".replace("2000",caller+""),trunk,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialOutboundRoutes")
    @Description("1.1.通过sps外线呼入到IVR1，按13001通过sip外线呼出到辅助1分机3001\n" +
            "2.3001正常响铃，接听，查看cdr\n" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2", "IVR","DialExtension", "testDialOutboundRoutes_","Trunk","OutboundRoute","DialOutboundRoutes","PSeries","Cloud","K2"}, dataProvider = "routesOutbound")
    public void testIVR27_DialOutboundRoutes_1(String routePrefix, int caller, String callee, String deviceAssist, String prfix, int OutCallee, int ringExtension, String inbound, String outbound) {
        isReConfigIVR6201Default = true;
        prerequisite(true);//        restIVR_6201();  //重置ivr and inbound

        step("1:login with admin,trunk: "+inbound);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，选择\tAllowed Extensions，分机选择ExGroup1、1001、1003");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
        auto.ivrPage().editIVR("IVR1").
                isCheckbox(ele_ivr_basic_dial_outb_routes_checkbox,true).
                selectAllowExtensionsToRight("Out1","Out2","Out3","Out4","Out5","Out6","Out7","Out8","Out9").
                clickSaveAndApply();

        step("2:1.通过sps外线呼入到IVR1，按13001通过sip外线呼出到辅助1分机3001,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);

        log.debug("[DTMF] " +prfix + OutCallee);
        pjsip.Pj_Send_Dtmf(caller, prfix + OutCallee);

        step("[通话状态校验] 3001正常响铃，接听，查看cdr");
        softAssertPlus.assertThat(getExtensionStatus(ringExtension,RING,60)).as("[通话校验]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(ringExtension,false);
        softAssertPlus.assertThat(getExtensionStatus(ringExtension,TALKING,20)).as("[通话校验]").isEqualTo(TALKING);
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), CDRNAME.IVR1_6201.toString(), "ANSWERED", "2000<2000> dialed Outbound",inbound,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), prfix + OutCallee + "", "ANSWERED", "2000<2000> hung up",inbound,outbound,"Outbound"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialOutboundRoutes")
    @Description("2.通过sip外线呼入到IVR1，按22222通过sps外线呼出到辅助2分机2000\n" +
            "\t正常响铃，接听，查看cdr\n" +
            "3.通过sps外线呼入到IVR1，按3xxx通过Account外线呼出到辅助3分机xxx\n" +
            "\t正常响铃，接听，查看cdr\n" +
            "4.通过sip外线呼入到IVR1，按42000通过FXO外线呼出到辅助2分机2000\n" +
            "\t正常响铃，接听，查看cdr\n" +
            "5.通过sip外线呼入到IVR1，按5xxxx通过BRI外线呼出到辅助2分机2000\n" +
            "\t正常响铃，接听，查看cdr\n" +
            "6.通过sip外线呼入到IVR1，按6xxxx通过E1外线呼出到辅助2分机2000\n" +
            "\t正常响铃，接听，查看cdr\n" +
            "7.通过sip外线呼入到IVR1，按7+辅助2的gsm号码通过gsm外线呼出到辅助2分机2000\n" +
            "\t正常响铃，接听，查看cdr" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("ID1034912 BRI/E1 exception")
    @Test(groups = {"P3", "IVR","DialExtension", "testIVR28_DialOutboundRoutes_2To7","Trunk","OutboundRoute","DialOutboundRoutes","PSeries","Cloud","K2"}, dataProvider = "routesOutbound")
    public void testIVR28_DialOutboundRoutes_2To7(String routePrefix, int caller, String callee, String deviceAssist, String prfix, int OutCallee, int ringExtesnion, String inbound, String outbound) {
        isReConfigIVR6201Default = true;
        prerequisite(true);//        restIVR_6201();  //重置ivr and inbound

        step("1:login with admin,trunk: "+inbound);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，选择\tAllowed Extensions，分机选择ExGroup1、1001、1003");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
        auto.ivrPage().editIVR("IVR1").
                isCheckbox(ele_ivr_basic_dial_outb_routes_checkbox,true).
                selectAllowExtensionsToRight("Out1","Out2","Out3","Out4","Out5","Out6","Out7","Out8","Out9").
                clickSaveAndApply();

        step("2:,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);

        log.debug("[DTMF] " +prfix + OutCallee);
        pjsip.Pj_Send_Dtmf(caller, prfix + OutCallee);

        step("[通话状态校验] 3001正常响铃，接听，查看cdr");
        softAssertPlus.assertThat(getExtensionStatus(ringExtesnion,RING,60)).as("[通话校验]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(OutCallee,false);
        softAssertPlus.assertThat(getExtensionStatus(ringExtesnion,TALKING,20)).as("[通话校验]").isEqualTo(TALKING);
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
//        if(prfix.equals("5") || prfix.equals("6")){
//            List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
//            softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
//                    .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1, "ANSWERED", "2000<2000> dialed Outbound".replace("2000",caller+""),inbound,"","Inbound"))
//                    .contains(tuple("2000<2000>".replace("2000",caller+""), OutCallee + "", "ANSWERED", "42000 hung up",inbound,outbound,"Outbound"));
//
//        }else{

        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1, "ANSWERED", "2000<2000> dialed Outbound".replace("2000",caller+""),inbound,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), prfix + OutCallee + "", "ANSWERED", "2000<2000> hung up".replace("2000",caller+""),inbound,outbound,"Outbound"));

//        }
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialOutboundRoutes")
    @Description("编辑IVR1，呼出路由只选择Out8【只允许分机1000呼出】\n" +
            "8.通过sps外线呼入到IVR1，按13001通过sip外线呼出到辅助1分机3001\n" +
            "呼出失败，通话挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2", "IVR","DialExtension", "testIVR29_DialOutboundRoutes_8","DialOutboundRoutes","PSeries","Cloud","K2"}, dataProvider = "routesOutbound")
    public void testIVR29_DialOutboundRoutes_8(String routePrefix, int caller, String callee, String deviceAssist, String prfix, int OutCallee, int ringExtension, String inbound, String outbound) {
        isReConfigIVR6201Default = true;
        prerequisite(true);//        restIVR_6201(); //重置ivr and inbound

        step("1:login with admin,trunk: "+inbound);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，呼出路由只选择Out8【只允许分机1000呼出】");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
        auto.ivrPage().editIVR("IVR1").
                isCheckbox(ele_ivr_basic_dial_outb_routes_checkbox,true).
                selectAllowExtensionsToRight("Out8").
                clickSaveAndApply();

        step("2:通过sps外线呼入到IVR1，按13001通过sip外线呼出到辅助1分机3001,caller：" + caller + " ，callee：" + routePrefix + "1002" + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + "1002", deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);

        log.debug("[DTMF] " +prfix + OutCallee);
        pjsip.Pj_Send_Dtmf(caller, prfix + OutCallee);

        step("[通话状态校验]");
        Assertions.assertThat(getExtensionStatus(caller, HUNGUP, 60)).isIn(HUNGUP,IDLE).as("通话状态校验 失败!");
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialOutboundRoutes")
    @Description("编辑IVR1，呼出路由只选择Out8【只允许分机1000呼出】\n" +
            "9.通过sip外线呼入到IVR1，按2222通过sps外线呼出到辅助2分机2000\n" +
            "\t正常响铃，接听，查看cdr\n" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","DialExtension", "testIVR30_DialOutboundRoutes_9","DialOutboundRoutes","PSeries","Cloud","K2"}, dataProvider = "routesOutbound")
    public void testIVR30_DialOutboundRoutes_9(String routePrefix, int caller, String callee, String deviceAssist, String prfix, int OutCallee, int ringExtension, String inbound, String outbound) {
        isReConfigIVR6201Default = true;
        prerequisite(true); //重置ivr and inbound

        step("1:login with admin,trunk: "+inbound);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，呼出路由只选择Out8【只允许分机1000呼出】");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
        auto.ivrPage().editIVR("IVR1").
                isCheckbox(ele_ivr_basic_dial_outb_routes_checkbox,true).
                selectAllowExtensionsToRight("Out8").
                clickSaveAndApply();

        step("3:通过sip外线呼入到IVR1，按22000通过sps外线呼出到辅助2分机2000 " + "[caller]：" + caller + ",[callee]：" + routePrefix + callee);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);

        log.debug("[DTMF] " +prfix + OutCallee);
        pjsip.Pj_Send_Dtmf(caller, prfix + OutCallee);

        assertStep("[通话状态校验]");
        softAssertPlus.assertThat(getExtensionStatus(ringExtension,RING,60)).as("[通话校验]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(ringExtension,false);
        softAssertPlus.assertThat(getExtensionStatus(ringExtension,TALKING,20)).as("[通话校验]").isEqualTo(TALKING);
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("3001<3001>", CDRNAME.IVR1_6201.toString(), "ANSWERED", "3001<3001> dialed Outbound",inbound,"","Inbound"))
                .contains(tuple("3001<3001>", prfix + OutCallee + "", "ANSWERED", "3001<3001> hung up",inbound,outbound,"Outbound"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialOutboundRoutes")
    @Description("编辑IVR1，呼出路由只选择Out8【只允许分机1000呼出】\n" +
            "10.分机1003呼入到IVR1，按2222通过sps外线呼出到辅助2分机2000\n" +
            "\t正常响铃，接听，查看cdr" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","DialExtension", "testIVR31_DialOutboundRoutes_10","DialOutboundRoutes","PSeries","Cloud","K2"}, dataProvider = "routesOutbound")
    public void testIVR31_DialOutboundRoutes_10(String routePrefix, int caller, String callee, String deviceAssist, String prfix, int OutCallee, int ringExtension, String inbound, String outbound) {
        isReConfigIVR6201Default = true;
        prerequisite(true);//        restIVR_6201(); //重置ivr and inbound

        step("1:login with admin,trunk: "+inbound);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，呼出路由只选择Out8【只允许分机1000呼出】");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
        auto.ivrPage().editIVR("IVR1").
                isCheckbox(ele_ivr_basic_dial_outb_routes_checkbox,true).
                selectAllowExtensionsToRight("Out8").
                clickSaveAndApply();

        step("2:分机1003呼入到IVR1，按22000通过sps外线呼出到辅助2分机2000,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(1003, "6201", DEVICE_IP_LAN, false);
        sleep(WaitUntils.SHORT_WAIT*3);

        log.debug("[DTMF] " +prfix + OutCallee);
        pjsip.Pj_Send_Dtmf(1003, prfix + OutCallee);

        step("[通话状态校验] ringExtension:"+ringExtension);
        softAssertPlus.assertThat(getExtensionStatus(ringExtension,RING,60)).as("[通话校验]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(ringExtension,false);
        softAssertPlus.assertThat(getExtensionStatus(ringExtension,TALKING,20)).as("[通话校验]").isEqualTo(TALKING);
        pjsip.Pj_hangupCall(1003);
        sleep(1000);
        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("testa D<1003>", "IVR IVR1<6201>", "ANSWERED", "testa D<1003> dialed Outbound", "", "", "Internal"))
                .contains(tuple("testa D<1003>", "22000", "ANSWERED", "testa D<1003> hung up", "", SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialOutboundRoutes")
    @Description("编辑IVR1，呼出路由选择Out9、Out2【Out9 呼出外线不可用】\n" +
            "\t11.通过sip外线呼入到IVR1，按22222通过sps外线呼出到辅助2分机2000\n" +
            "\t\tOut9呼出失败，通过Out2呼出成功，正常响铃，接听，查看cdr" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","DialExtension", "testIVR32_DialOutboundRoutes_11","DialOutboundRoutes","PSeries","Cloud","K2"}, dataProvider = "routesOutbound")
    public void testIVR32_DialOutboundRoutes_11(String routePrefix, int caller, String callee, String deviceAssist, String prfix, int OutCallee, int ringExtension, String inbound, String outbound) {
        isReConfigIVR6201Default = true;
        prerequisite(true); //重置ivr and inbound

        step("1:login with admin,trunk: "+inbound);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，呼出路由选择Out9、Out2【Out9 呼出外线不可用】");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
        auto.ivrPage().editIVR("IVR1").
                isCheckbox(ele_ivr_basic_dial_outb_routes_checkbox,true).
                selectAllowExtensionsToRight("Out2","Out9").
                clickSaveAndApply();

        step("2:,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);

        log.debug("[DTMF] " +prfix + OutCallee);
        pjsip.Pj_Send_Dtmf(caller, prfix + OutCallee);

        step("[通话状态校验] 3001正常响铃，接听，查看cdr");
        softAssertPlus.assertThat(getExtensionStatus(ringExtension,RING,20)).as("[通话校验]").isEqualTo(RING);
        pjsip.Pj_Answer_Call(ringExtension,false);
        softAssertPlus.assertThat(getExtensionStatus(ringExtension,TALKING,20)).as("[通话校验]").isEqualTo(TALKING);
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("3001<3001>", cdrIVR1, "ANSWERED", "3001<3001> dialed Outbound",inbound,"","Inbound"))
                .contains(tuple("3001<3001>", prfix + OutCallee + "", "ANSWERED", "3001<3001> hung up",inbound,outbound,"Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("DialOutboundRoutes")
    @Description("编辑IVR1，不勾选Dial Outbound Routes\n" +
            "\t12.通过sip外线呼入到IVR1，按22222通过sps外线呼出到辅助2分机2000\n" +
            "\t\t呼出失败，通话被挂断" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","DialExtension", "testIVR32_DialOutboundRoutes_11","DialOutboundRoutes","PSeries","Cloud","K2"}, dataProvider = "routesOutbound")
    public void testIVR33_DialOutboundRoutes_12(String routePrefix, int caller, String callee, String deviceAssist, String prfix, int OutCallee, int ringExtension, String inbound, String outbound) {
        isReConfigIVR6201Default = true;
        prerequisite(true); //重置ivr and inbound

        step("1:login with admin,trunk: "+inbound);
        auto.loginPage().loginWithAdmin();

        step("2.编辑IVR1，不勾选Dial Outbound Routes");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);
        auto.ivrPage().editIVR("IVR1").
                isCheckbox(ele_ivr_basic_dial_outb_routes_checkbox,false).
                clickSaveAndApply();

        step("3:通过sip外线呼入到IVR1，按22222通过sps外线呼出到辅助2分机2000,caller：" + caller + " ，callee：" + routePrefix + callee + "， deviceAssist：" + deviceAssist);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);

        log.debug("[DTMF] " +prfix + OutCallee);
        pjsip.Pj_Send_Dtmf(caller, prfix + OutCallee);

        step("[通话状态校验] 3001正常响铃，接听，查看cdr");
        softAssertPlus.assertThat(getExtensionStatus(caller,HUNGUP,20)).as("[通话校验]").isEqualTo(HUNGUP);


        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("3001<3001>", cdrIVR1, "ANSWERED", "Invalid key",inbound,"","Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("KeyPressEvent")
    @Description("默认Key选择[None]\n" +
            "1.1.通过sip外线呼入到IVR1，按4到IVR0,再按1\n" +
            "\t通话被挂断" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2", "IVR","KeyPressEvent", "testIVR34_KeyPressEvent_1","PSeries","Cloud","K2"}, dataProvider = "routes")
    public void testIVR34_KeyPressEvent_1(String routePrefix, int caller, String callee, String deviceAssist, String vcpCaller, String vcpDetail, String trunk, String message) {
        prerequisite(false);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2:通过sip外线呼入到IVR1，按4到IVR0,再按1" +"，[caller]:"+caller+" ,[callee]:"+routePrefix + callee+"[dtmf] 4 -->1");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);
        pjsip.Pj_Send_Dtmf(caller, "4");//转到IVR0
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Send_Dtmf(caller,"1");//转到IVR0

        assertStep(caller + "[通话状态校验] 3001正常响铃，接听，查看cdr");
        Assert.assertEquals(getExtensionStatus(caller,HUNGUP,60),HUNGUP);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("3001<3001>", CDRNAME.IVR1_6201.toString(), "ANSWERED", "3001<3001> IVR IVR1<6201>", trunk, "", "Inbound"))
                .contains(tuple("3001<3001>", CDRNAME.IVR0_6200.toString(), "ANSWERED", "Invalid key", trunk, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("KeyPressEvent")
    @Description("默认Key选择[None]\n" +
            "\t2.通过sip外线呼入到IVR1，按4到IVR0,再按5\n" +
            "\t\t通话被挂断\n" +
            "\t3.通过sip外线呼入到IVR1，按4到IVR0,再按*\n" +
            "\t\t通话被挂断\n" +
            "\t4.通过sip外线呼入到IVR1，按4到IVR0,再按A\n" +
            "\t\t通话被挂断"
    )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","KeyPressEvent", "testIVR35_KeyPressEvent_2To4","PSeries","Cloud","K2"}, dataProvider = "routesKeyPressEvent")
    public void testIVR35_KeyPressEvent_2To4(String routePrefix, int caller, String callee, String deviceAssist, String dtmf, String dtmfMessage, String trunk, String message) {
        prerequisite(false);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2:"+dtmfMessage +"，[caller]:"+caller+" ,[callee]:"+routePrefix + callee+"[dtmf] 4 -->"+dtmf);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);
        pjsip.Pj_Send_Dtmf(caller, "4");//转到IVR0
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Send_Dtmf(caller,dtmf);//转到IVR0

        assertStep(caller + "[通话状态校验] 3001正常响铃，接听，查看cdr");
        Assert.assertEquals(getExtensionStatus(caller,HUNGUP,60), HUNGUP);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("3001<3001>", CDRNAME.IVR1_6201.toString(), "ANSWERED", "3001<3001> IVR IVR1<6201>", trunk, "", "Inbound"))
                .contains(tuple("3001<3001>", CDRNAME.IVR0_6200.toString(), "ANSWERED", "Invalid key", trunk, "", "Inbound"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("IVR")
    @Story("KeyPressEvent")
    @Description("编辑IVR1，按2选择到分机B-1001\n" +
            "\t5.通过sip外线呼入到IVR1，按1\n" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P1", "IVR","KeyPressEvent", "testIVR36_KeyPressEvent_5","Basic","Trunk","PSeries","Cloud","K2","Extension"}, dataProvider = "routesKeyPressEvent")
    public void testIVR36_KeyPressEvent_5(String routePrefix, int caller, String callee, String deviceAssist, String dtmf, String dtmfMessage, String trunk, String message) {
        prerequisite(false);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2:"+dtmfMessage +"，[caller]:"+caller+" ,[callee]:"+routePrefix + callee+"[dtmf] "+dtmf);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);

        pjsip.Pj_Send_Dtmf(caller, dtmf);

        assertStep(caller + "[通话状态校验]");
        //5.通过sip外线呼入到IVR1，按1 分机1001响铃可正常接听，挂断

        Assert.assertEquals(getExtensionStatus(1001,RING,5),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());        pjsip.Pj_Answer_Call(1002,false);
        pjsip.Pj_Answer_Call(1001,false);
        Assert.assertEquals(getExtensionStatus(1001,TALKING,5),TALKING,"[通话状态校验_通话] Time："+ DataUtils.getCurrentTime());
        sleep(3000);
        pjsip.Pj_hangupCall(1001);
        assertStep("9:[CDR显示]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验_2] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1, "ANSWERED", "2000<2000> called Extension".replace("2000",caller+""),trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "test2 B<1001>", "ANSWERED", "test2 B<1001> hung up",trunk,"","Inbound"));
        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("IVR")
    @Story("KeyPressEvent")
    @Description("编辑IVR1，按1到Hang Up\n" +
            "\t6.通过sip外线呼入到IVR1，按2\n" +
            "\t\t通话被挂断" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","KeyPressEvent", "testIVR37_KeyPressEvent_6","PSeries","Cloud","K2","HangUp"}, dataProvider = "routesKeyPressEvent")
    public void testIVR37_KeyPressEvent_6(String routePrefix, int caller, String callee, String deviceAssist, String dtmf, String dtmfMessage, String trunk, String message) {
        prerequisite(false);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2:"+dtmfMessage +"，[caller]:"+caller+" ,[callee]:"+routePrefix + callee+"[dtmf] "+dtmf);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);
        pjsip.Pj_Send_Dtmf(caller, dtmf);

        assertStep(caller + "[通话状态校验]");
        //6.通过sip外线呼入到IVR1，按2 通话被挂断
        Assert.assertEquals(getExtensionStatus(caller,HUNGUP,20),HUNGUP);
        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1, "ANSWERED", "3001<3001> called Hang Up" ,trunk,"","Inbound"));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("KeyPressEvent")
    @Description("编辑IVR1，按3到ExtensionVoicemail-分机1001\n" +
            "7.通过sip外线呼入到IVR1，按3\n 分机1001登录查看新增1条语音留言，Name显示外部号码" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","KeyPressEvent", "testIVR38_KeyPressEvent_7","PSeries","Cloud","K2","Voicemail"}, dataProvider = "routesKeyPressEvent")
    public void testIVR38_KeyPressEvent_7(String routePrefix, int caller, String callee, String deviceAssist, String dtmf, String dtmfMessage, String trunk, String message) {
        prerequisite(false);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2:"+dtmfMessage +"，[caller]:"+caller+" ,[callee]:"+routePrefix + callee+"[dtmf] "+dtmf);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);
        pjsip.Pj_Send_Dtmf(caller, dtmf);

        assertStep(caller + "[通话状态校验]");
        sleep(30000);
        pjsip.Pj_hangupCall(caller);

        //loginWeb 1001
        auto.homePage().logout();
        auto.loginPage().login("1001",EXTENSION_PASSWORD_NEW);
        sleep(3000);
        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        softAssertPlus.assertThat(TableUtils.getTableForHeader(getDriver(),"Name",0)).contains("3001");

        assertStep("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1, "ANSWERED", "3001<3001> called Voicemail" ,trunk,"","Inbound"))
                .contains(tuple("3001<3001>", "Voicemail test2 B<1001>", "VOICEMAIL", "3001<3001> break_out",trunk, "", "Inbound"));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("KeyPressEvent")
    @Description("编辑IVR1，按4到IVR0\n" +
            "\t通过sip外线呼入到IVR1，按4\n" +
            "\t\t8.听到提示音后再按0\n" +
            "\t\t\t分机1000响铃可正常接听，挂断" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","KeyPressEvent", "testIVR39_KeyPressEvent_8","PSeries","Cloud","K2",}, dataProvider = "routesKeyPressEvent")
    public void testIVR39_KeyPressEvent_8(String routePrefix, int caller, String callee, String deviceAssist, String dtmf, String dtmfMessage, String trunk, String message) {
        prerequisite(false);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2:"+dtmfMessage +"，[caller]:"+caller+" ,[callee]:"+routePrefix + callee+"[dtmf] "+dtmf);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);
        pjsip.Pj_Send_Dtmf(caller, dtmf);
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_Send_Dtmf(caller, "0");

        assertStep("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,5),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,5),TALKING,"[通话状态校验_通话] Time："+ DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR显示]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1,"ANSWERED", "3001<3001> IVR IVR1<6201>",trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "IVR IVR0_6200<6200>","ANSWERED", "3001<3001> called Extension",trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "test A<1000> hung up",trunk,"","Inbound"));

    }
    @Epic("P_Series")
    @Feature("IVR")
    @Story("KeyPressEvent")
    @Description("编辑IVR1，按5到RingGroup0\n" +
            "\t9.通过sip外线呼入到IVR1，按5\n" +
            "\t\t分机1000、1001、1003 同时响铃，分机1001接听后，1000、1003停止响铃；可正常挂断，检查cdr\n" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","KeyPressEvent", "testIVR40_KeyPressEvent_9","PSeries","Cloud","K2","RingGroup"}, dataProvider = "routesKeyPressEvent")
    public void testIVR40_KeyPressEvent_9(String routePrefix, int caller, String callee, String deviceAssist, String dtmf, String dtmfMessage, String trunk, String message) {
        prerequisite(false);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2:"+dtmfMessage +"，[caller]:"+caller+" ,[callee]:"+routePrefix + callee+"[dtmf] "+dtmf);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);
        pjsip.Pj_Send_Dtmf(caller, dtmf);

        assertStep(caller + "[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,5),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1001,RING,5),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1003,RING,5),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());

        pjsip.Pj_Answer_Call(1001,false);
        Assert.assertEquals(getExtensionStatus(1001,TALKING,5),TALKING,"[通话状态校验_通话] Time："+ DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1000,HUNGUP,5),HUNGUP,"[通话状态校验_通话] Time："+ DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1003,HUNGUP,5),HUNGUP,"[通话状态校验_通话] Time："+ DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_hangupCall(1001);


        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1, "ANSWERED", "3001<3001> called Ring Group" ,trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "RingGroup RingGroup0<6300>", "ANSWERED", "RingGroup RingGroup0<6300> connected" ,trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "test2 B<1001>", "ANSWERED", "test2 B<1001> hung up" ,trunk,"","Inbound"));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("KeyPressEvent")
    @Description("编辑IVR1，按5到RingGroup0\n" +
            "\t\t10.1000、1001、1003 响铃后都不接听，10s后只有分机1000再次响铃，分机1000可正常接听，检查cdr;" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","KeyPressEvent", "testIVR41_KeyPressEvent_10","PSeries","Cloud","K2","RingGroup"}, dataProvider = "routesKeyPressEvent")
    public void testIVR41_KeyPressEvent_10(String routePrefix, int caller, String callee, String deviceAssist, String dtmf, String dtmfMessage, String trunk, String message) {
        prerequisite(false);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2:"+dtmfMessage +"，[caller]:"+caller+" ,[callee]:"+routePrefix + callee+"[dtmf] "+dtmf);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);
        pjsip.Pj_Send_Dtmf(caller, dtmf);

        assertStep(caller + "[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,5),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1001,RING,5),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1003,RING,5),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());

        // sleep(10000);
        Assert.assertEquals(getExtensionStatus(1001,HUNGUP,10),HUNGUP,"[通话状态校验_通话] Time："+ DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1003,HUNGUP,5),HUNGUP,"[通话状态校验_通话] Time："+ DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1000,RING,5),RING,"[通话状态校验_通话] Time："+ DataUtils.getCurrentTime());


        pjsip.Pj_Answer_Call(1000,false);
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_hangupCall(1000);


        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1, "ANSWERED", "3001<3001> called Ring Group" ,trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "RingGroup RingGroup0<6300>", "NO ANSWER", "RingGroup RingGroup0<6300> timed out, failover" ,trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "test A<1000> hung up" ,trunk,"","Inbound"));
        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("IVR")
    @Story("KeyPressEvent")
    @Description("编辑IVR1，按6到Queue0\n" +
            "\t11.通过sip外线呼入到IVR1，按6\n" +
            "\t\t分机1000、1001、1003、1004同时响铃，1004接听后停止响铃；可正常挂断，检查cdr\n" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","KeyPressEvent", "testIVR42_KeyPressEvent_11","PSeries","Cloud","K2","Queue"}, dataProvider = "routesKeyPressEvent")
    public void testIVR42_KeyPressEvent_11(String routePrefix, int caller, String callee, String deviceAssist, String dtmf, String dtmfMessage, String trunk, String message) throws IOException, JSchException {
        prerequisite(false);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("新增动态坐席：1003\\1004分别拨打*76400 加入到Queue0");
        String result = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI,"queue show queue-6400"));
        log.debug("[queue show queue-6400] "+result);
        if(!result.contains("1003")){
            pjsip.Pj_Make_Call_Auto_Answer(1003,"*76400", DEVICE_IP_LAN);
        }
        if(!result.contains("1004")){
            pjsip.Pj_Make_Call_Auto_Answer(1004,"*76400", DEVICE_IP_LAN);
        }
        String resultAfter = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI,"queue show queue-6400"));
        log.debug("[queue show queue-6400 resultAfter] "+resultAfter);

        step("2:"+dtmfMessage +"，[caller]:"+caller+" ,[callee]:"+routePrefix + callee+"[dtmf] "+dtmf);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);
        pjsip.Pj_Send_Dtmf(caller, dtmf);

        assertStep(caller + "[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,20),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1001,RING,20),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1003,RING,20),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1004,RING,20),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());

        pjsip.Pj_Answer_Call(1004,false);
        Assert.assertEquals(getExtensionStatus(1004,TALKING,10),TALKING,"[通话状态校验_通话] Time："+ DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_hangupCall(1004);

        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1, "ANSWERED", "3001<3001> called Queue" ,trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "Queue Queue0<6400>", "ANSWERED", "Queue Queue0<6400> connected" ,trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "t estX<1004>", "ANSWERED", "t estX<1004> hung up" ,trunk,"","Inbound"));
        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("IVR")
    @Story("KeyPressEvent")
    @Description("编辑IVR1，按6到Queue0\n" +
            "\t\t12.分机1000、1001、1003、1004同时响铃，1001接听后停止响铃；可正常挂断，检查cdr\n" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","KeyPressEvent", "testIVR43_KeyPressEvent_12","PSeries","Cloud","K2","Queue"}, dataProvider = "routesKeyPressEvent")
    public void testIVR43_KeyPressEvent_12(String routePrefix, int caller, String callee, String deviceAssist, String dtmf, String dtmfMessage, String trunk, String message) throws IOException, JSchException {
        prerequisite(false);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("新增动态坐席：1003\\1004分别拨打*76400 加入到Queue0");
        String result = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI,"queue show queue-6400"));
        log.debug("[queue show queue-6400] "+result);
        if(!result.contains("1003")){
            pjsip.Pj_Make_Call_Auto_Answer(1003,"*76400", DEVICE_IP_LAN);
        }
        if(!result.contains("1004")){
            pjsip.Pj_Make_Call_Auto_Answer(1004,"*76400", DEVICE_IP_LAN);
        }
        String resultAfter = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI,"queue show queue-6400"));
        log.debug("[queue show queue-6400 resultAfter] "+resultAfter);

        step("2:"+dtmfMessage +"，[caller]:"+caller+" ,[callee]:"+routePrefix + callee+"[dtmf] "+dtmf);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);
        pjsip.Pj_Send_Dtmf(caller, dtmf);

        assertStep(caller + "[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,5),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1001,RING,5),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1003,RING,5),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1004,RING,5),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());

        pjsip.Pj_Answer_Call(1001,false);
        Assert.assertEquals(getExtensionStatus(1001,TALKING,5),TALKING,"[通话状态校验_通话] Time："+ DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_hangupCall(1001);

        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1, "ANSWERED", "3001<3001> called Queue" ,trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "Queue Queue0<6400>", "ANSWERED", "Queue Queue0<6400> connected" ,trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "test2 B<1001>", "ANSWERED", "test2 B<1001> hung up" ,trunk,"","Inbound"));
        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("IVR")
    @Story("KeyPressEvent")
    @Description("编辑IVR1，按6到Queue0\n" +
            "\t\t13.分机1000、1001、1003、1004同时响铃，等待超时\n" +
            "\t\t\t60秒后，只有分机1000响铃，可正常接听，检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","KeyPressEvent", "testIVR44_KeyPressEvent_13","PSeries","Cloud","K2","Queue"}, dataProvider = "routesKeyPressEvent")
    public void testIVR44_KeyPressEvent_13(String routePrefix, int caller, String callee, String deviceAssist, String dtmf, String dtmfMessage, String trunk, String message) throws IOException, JSchException {
        prerequisite(false);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("新增动态坐席：1003\\1004分别拨打*76400 加入到Queue0");
        String result = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI,"queue show queue-6400"));
        log.debug("[queue show queue-6400] "+result);
        if(!result.contains("1003")){
            pjsip.Pj_Make_Call_Auto_Answer(1003,"*76400", DEVICE_IP_LAN);
        }
        if(!result.contains("1004")){
            pjsip.Pj_Make_Call_Auto_Answer(1004,"*76400", DEVICE_IP_LAN);
        }
        String resultAfter = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI,"queue show queue-6400"));
        log.debug("[queue show queue-6400 resultAfter] "+resultAfter);

        step("2:"+dtmfMessage +"，[caller]:"+caller+" ,[callee]:"+routePrefix + callee+"[dtmf] "+dtmf);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);
        pjsip.Pj_Send_Dtmf(caller, dtmf);

        assertStep(caller + "[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,5),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1001,RING,5),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1003,RING,5),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1004,RING,5),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());

        Assert.assertEquals(getExtensionStatus(1000,HUNGUP,60),HUNGUP,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1001,HUNGUP,60),HUNGUP,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1003,HUNGUP,60),HUNGUP,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1004,HUNGUP,60),HUNGUP,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());

        Assert.assertEquals(getExtensionStatus(1000,RING,60),RING,"[通话状态校验_通话] Time："+ DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,5),TALKING,"[通话状态校验_通话] Time："+ DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_hangupCall(1000);

        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1, "ANSWERED", "3001<3001> called Queue" ,trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "Queue Queue0<6400>", "NO ANSWER", "Queue Queue0<6400> timed out, failover" ,trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "test A<1000>", "ANSWERED", "test A<1000> hung up" ,trunk,"","Inbound"));
        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("IVR")
    @Story("KeyPressEvent")
    @Description("编辑IVR1，按6到Queue0\n" +
            "\t\t14.等待10s后，主叫按0\n" +
            "\t\t\t只有分机1001正常响铃、接听；检查cdr" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","KeyPressEvent", "testIVR45_KeyPressEvent_14","PSeries","Cloud","K2","Queue"}, dataProvider = "routesKeyPressEvent")
    public void testIVR45_KeyPressEvent_14(String routePrefix, int caller, String callee, String deviceAssist, String dtmf, String dtmfMessage, String trunk, String message) throws IOException, JSchException {
        prerequisite(false);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("新增动态坐席：1003\\1004分别拨打*76400 加入到Queue0");
        String result = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI,"queue show queue-6400"));
        log.debug("[queue show queue-6400] "+result);
        if(!result.contains("1003")){
            pjsip.Pj_Make_Call_Auto_Answer(1003,"*76400", DEVICE_IP_LAN);
        }
        if(!result.contains("1004")){
            pjsip.Pj_Make_Call_Auto_Answer(1004,"*76400", DEVICE_IP_LAN);
        }
        String resultAfter = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI,"queue show queue-6400"));
        log.debug("[queue show queue-6400 resultAfter] "+resultAfter);

        step("2:"+dtmfMessage +"，[caller]:"+caller+" ,[callee]:"+routePrefix + callee+"[dtmf] "+dtmf);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);
        pjsip.Pj_Send_Dtmf(caller, dtmf);

        assertStep(caller + "[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000,RING,5),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1001,RING,5),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1003,RING,5),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1004,RING,5),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());

        sleep(5000);
        pjsip.Pj_Send_Dtmf(caller, "0");
        Assert.assertEquals(getExtensionStatus(1001,RING,20),RING,"[通话状态校验_通话] Time："+ DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1000,HUNGUP,5),HUNGUP,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1003,HUNGUP,5),HUNGUP,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1004,HUNGUP,5),HUNGUP,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1001,false);
        Assert.assertEquals(getExtensionStatus(1001,TALKING,60),TALKING,"[通话状态校验_通话] Time："+ DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_hangupCall(1001);

        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1, "ANSWERED", "3001<3001> called Queue" ,trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "Queue Queue0<6400>", "NO ANSWER", "Queue Queue0<6400> connected" ,trunk,"","Inbound"))
                .contains(tuple("2000<2000>".replace("2000",caller+""), "test2 B<1001>", "ANSWERED", "test2 B<1001> hung up" ,trunk,"","Inbound"));
        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("IVR")
    @Story("KeyPressEvent")
    @Description("编辑IVR1，按7到Dial by Name\n" +
            "\t通过sip外线呼入到IVR1，按7\n" +
            "\t\t进入asterisk检查播放dir-usingkeypad.gsm时\n" +
            "\t\t\t15.按8378 然后按1\n" +
            "\t\t\t\t分机1004响铃，可正常接听\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","KeyPressEvent", "testIVR46_KeyPressEvent_15","PSeries","Cloud","K2","DialbyName"}, dataProvider = "routesKeyPressEvent")
    public void testIVR46_KeyPressEvent_15(String routePrefix, int caller, String callee, String deviceAssist, String dtmf, String dtmfMessage, String trunk, String message) {
        prerequisite(false);
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread = new SSHLinuxUntils.AsteriskThread(asteriskObjectList, DIR_USINGKEYPAD);
        thread.start();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2:"+dtmfMessage +"，[caller]:"+caller+" ,[callee]:"+routePrefix + callee+"[dtmf] "+dtmf);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);
        pjsip.Pj_Send_Dtmf(caller, dtmf);

        step("3.进入asterisk检查播放dir-usingkeypad.gsm时");
        int tmp = 0;
        while (asteriskObjectList.size() !=1 && tmp <= 800){
            sleep(50);
            tmp++;
            log.debug("[tmp]_"+tmp);
        }
        if(tmp==801){
            Assert.assertTrue(false,"[asterisk time out not found key] "+DIR_USINGKEYPAD);
        }
        step("4.按8378 然后按1");
        pjsip.Pj_Send_Dtmf(caller, "8");
        sleep(3000);
        pjsip.Pj_Send_Dtmf(caller, "3");
        sleep(3000);
        pjsip.Pj_Send_Dtmf(caller, "7");
        sleep(3000);
        pjsip.Pj_Send_Dtmf(caller, "8");
        sleep(3000);

        pjsip.Pj_Send_Dtmf(caller, "1");

        assertStep(caller + "[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1004,RING,60),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1004,false);
        Assert.assertEquals(getExtensionStatus(1004,TALKING,60),TALKING,"[通话状态校验_通话] Time："+ DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_hangupCall(1004);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("3001<3001>", cdrIVR1, "ANSWERED", "3001<3001> called Dial by Name" ,trunk,"","Inbound"))
                .contains(tuple("3001<3001>", "t estX<1004>", "ANSWERED", "t estX<1004> hung up",trunk, "", "Inbound"));
        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("IVR")
    @Story("KeyPressEvent")
    @Description("编辑IVR1，按7到Dial by Name\n" +
            "\t通过sip外线呼入到IVR1，按7\n" +
            "\t\t进入asterisk检查播放dir-usingkeypad.gsm时\n" +
            "\t\t\t16.按8378 然后按*，再按1\n" +
            "\t\t\t\t分机1000响铃，可正常接听" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","KeyPressEvent", "testIVR47_KeyPressEvent_16","DialbyName","PSeries","Cloud","K2"}, dataProvider = "routesKeyPressEvent")
    public void testIVR47_KeyPressEvent_16(String routePrefix, int caller, String callee, String deviceAssist, String dtmf, String dtmfMessage, String trunk, String message) {
        prerequisite(false);
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread = new SSHLinuxUntils.AsteriskThread(asteriskObjectList, DIR_USINGKEYPAD);
        thread.start();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2:"+dtmfMessage +"，[caller]:"+caller+" ,[callee]:"+routePrefix + callee+"[dtmf] "+dtmf);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);
        pjsip.Pj_Send_Dtmf(caller, dtmf);

        step("3.进入asterisk检查播放dir-usingkeypad.gsm时");
        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp++ < 800){
            sleep(50);
        }
        if(tmp == 801){
            for(int i = 0 ; i < asteriskObjectList.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());
        }

        step("4.按8378 然后按*，再按1");
        pjsip.Pj_Send_Dtmf(caller, "8");
        sleep(3000);
        pjsip.Pj_Send_Dtmf(caller, "3");
        sleep(3000);
        pjsip.Pj_Send_Dtmf(caller, "7");
        sleep(3000);
        pjsip.Pj_Send_Dtmf(caller, "8");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(caller,"*");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(caller,"1");


        assertStep(caller + "[通话状态校验:分机1000预期响铃]");
        Assert.assertEquals(getExtensionStatus(1000,RING,60),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000,false);
        Assert.assertEquals(getExtensionStatus(1000,TALKING,60),TALKING,"[通话状态校验_通话] Time："+ DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_hangupCall(1000);
        thread.flag = false;
        sleep(5000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(3);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("3001<3001>", CDRNAME.IVR1_6201.toString(), "ANSWERED", "3001<3001> called Dial by Name" ,trunk,"","Inbound"))
                .contains(tuple("3001<3001>", "test A<1000>", "ANSWERED", "test A<1000> hung up",trunk, "", "Inbound"));
        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("IVR")
    @Story("KeyPressEvent")
    @Description("编辑IVR1，按8到External Number，外线号码为2222\n" +
            "\t17.通过sip外线呼入到IVR1，按8\n" +
            "\t\t辅助2分机2000可正常响铃、接听；查看cdr正确" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","KeyPressEvent", "testIVR48_KeyPressEvent_17","PSeries","Cloud","K2","ExternalNumber"}, dataProvider = "routesKeyPressEvent")
    public void testIVR48_KeyPressEvent_17(String routePrefix, int caller, String callee, String deviceAssist, String dtmf, String dtmfMessage, String trunk, String message) {
        prerequisite(false);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2:"+dtmfMessage +"，[caller]:"+caller+" ,[callee]:"+routePrefix + callee+"[dtmf] "+dtmf);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);
        pjsip.Pj_Send_Dtmf(caller, dtmf);

        assertStep(caller + "[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(2000,RING,20),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000,false);
        Assert.assertEquals(getExtensionStatus(2000,TALKING,20),TALKING,"[通话状态校验_通话] Time："+ DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("2000<2000>".replace("2000",caller+""), cdrIVR1, "ANSWERED", "3001<3001> called External Number" ,trunk,"","Inbound"))
                .contains(tuple("3001<3001>", "2000", "ANSWERED", "2000 hung up",trunk,"SPS1", "Outbound"));
        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("IVR")
    @Story("KeyPressEvent")
    @Description("编辑IVR1，按9到Play Prompt and Exit 选择prompt1，播放1遍\n" +
            "\t18.通过sip外线呼入到IVR1，按9\n" +
            "\t\t进入asterisk查看播放提示音prompt1一遍后，通话挂断；查看cdr正确" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","KeyPressEvent", "testIVR49_KeyPressEvent_18","PSeries","Cloud","K2","PlayPromptandExit"}, dataProvider = "routesKeyPressEvent")
    public void testIVR49_KeyPressEvent_18(String routePrefix, int caller, String callee, String deviceAssist, String dtmf, String dtmfMessage, String trunk, String message) {
        prerequisite(false);
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread = new SSHLinuxUntils.AsteriskThread(asteriskObjectList, PROMPT_1);
        thread.start();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2:"+dtmfMessage +"，[caller]:"+caller+" ,[callee]:"+routePrefix + callee+"[dtmf] "+dtmf);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Send_Dtmf(caller, dtmf);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp++ < 900){
            sleep(50);
        }
        if(tmp == 901){
            for(int i = 0 ; i < asteriskObjectList.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());
        }
        thread.flag = false;
        assertStep(caller + "[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(caller,HUNGUP,20),HUNGUP,"[通话状态校验] Time："+ DataUtils.getCurrentTime());

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("3001<3001>", "IVR IVR1<6201>", "ANSWERED", "3001<3001> called Play Prompt and Exit" ,trunk,"","Inbound"))
                .contains(tuple("3001<3001>", "PlayFile", "ANSWERED", "3001<3001> hung up", trunk, "", "Inbound"));
        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("IVR")
    @Story("KeyPressEvent")
    @Description("编辑IVR1，按#到分机C-1002\n" +
            "\t19.通过sip外线呼入到IVR1，按#\n" +
            "\t\t分机C-1002响铃，可正常接听，cdr正确" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","KeyPressEvent", "testIVR50_KeyPressEvent_19","PSeries","Cloud","K2"}, dataProvider = "routesKeyPressEvent")
    public void testIVR50_KeyPressEvent_19(String routePrefix, int caller, String callee, String deviceAssist, String dtmf, String dtmfMessage, String trunk, String message) {
        prerequisite(false);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2:"+dtmfMessage +"，[caller]:"+caller+" ,[callee]:"+routePrefix + callee+"[dtmf] "+dtmf);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Send_Dtmf(caller, dtmf);

        assertStep(caller + "[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1002,RING,60),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1002,false);
        Assert.assertEquals(getExtensionStatus(1002,TALKING,20),TALKING,"[通话状态校验_通话] Time："+ DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_hangupCall(caller);


        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("3001<3001>", "IVR IVR1<6201>", "ANSWERED", "3001<3001> called Extension", trunk, "", "Inbound"))
                .contains(tuple("3001<3001>", "testta C<1002>", "ANSWERED", "3001<3001> hung up", trunk, "", "Inbound"));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("KeyPressEvent")
    @Description("编辑IVR1，按*到Play Prompt and Exit 选择prompt2，播放5遍\n" +
            "\t20.通过sip外线呼入到IVR1，按*\n" +
            "\t\t进入asterisk查看播放提示音prompt2 五遍后，通话挂断；查看cdr正确" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","KeyPressEvent", "testIVR51_KeyPressEvent_20","PlayPromptandExit","PSeries","Cloud","K2"}, dataProvider = "routesKeyPressEvent")
    public void testIVR51_KeyPressEvent_20(String routePrefix, int caller, String callee, String deviceAssist, String dtmf, String dtmfMessage, String trunk, String message) {
        prerequisite(false);
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread = new SSHLinuxUntils.AsteriskThread(asteriskObjectList, PROMPT_2);
        thread.start();

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2:"+dtmfMessage +"，[caller]:"+caller+" ,[callee]:"+routePrefix + callee+"[dtmf] "+dtmf);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Send_Dtmf(caller, dtmf);

        int tmp = 0;
        while (asteriskObjectList.size() != 5 && tmp++ < 600){
            sleep(50);
        }
        if(tmp == 601){
            for(int i = 0 ; i < asteriskObjectList.size() ; i++){
                log.debug(i+"_【asterisk object name】 "+asteriskObjectList.get(i).getName() +" [asterisk object time] "+asteriskObjectList.get(i).getTime()+"[asterisk object tag] "+asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false,"[没有检测到提示音文件！！！]，[size] "+asteriskObjectList.size());
        }
        thread.flag = false;

        assertStep(caller + "[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(caller,HUNGUP,20),HUNGUP,"[通话状态校验] Time："+ DataUtils.getCurrentTime());


        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("3001<3001>", "IVR 6201", "ANSWERED", "3001<3001> called Play Prompt and Exit", trunk, "", "Inbound"))
                .contains(tuple("3001<3001>", "PlayFile", "ANSWERED", "3001<3001> hung up", trunk, "", "Inbound"));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("KeyPressEvent")
    @Description("编辑IVR1，Response Timeout到分机D-1003\n" +
            "\t21.通过sip外线呼入到IVR1，不按键\n" +
            "\t\t判断一段时间后（最大100s ，IVR1的按键时长在不同步骤下可能被编辑设置为不同时长)1003分机响铃，可正常接听，cdr正确" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","KeyPressEvent", "testIVR52_KeyPressEvent_21","PSeries","Cloud","K2","ResponseTimeout"}, dataProvider = "routesKeyPressEvent")
    public void testIVR52_KeyPressEvent_21(String routePrefix, int caller, String callee, String deviceAssist, String dtmf, String dtmfMessage, String trunk, String message) {
        prerequisite(false);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2:"+dtmfMessage +"，[caller]:"+caller+" ,[callee]:"+routePrefix + callee+"[dtmf] "+dtmf);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        assertStep(caller + "[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1003,RING,100),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1003,false);
        Assert.assertEquals(getExtensionStatus(1003,TALKING,20),TALKING,"[通话状态校验_通话] Time："+ DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("3001<3001>", "IVR IVR1<6201>", "ANSWERED", "IVR IVR1<6201> timed out, failover", trunk, "", "Inbound"))
                .contains(tuple("3001<3001>", "testa D<1003>", "ANSWERED", "3001<3001> hung up", trunk, "", "Inbound"));
        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("IVR")
    @Story("KeyPressEvent")
    @Description("编辑IVR1，Invalid Input Destination到分机E1004\n" +
            "\t22.通过sip外线呼入到IVR1，按A\n" +
            "\t\t分机E-1004可正常响铃、接听；cdr正确" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","KeyPressEvent", "testIVR53_KeyPressEvent_22","Invalid","PSeries","Cloud","K2"}, dataProvider = "routesKeyPressEvent")
    public void testIVR53_KeyPressEvent_22(String routePrefix, int caller, String callee, String deviceAssist, String dtmf, String dtmfMessage, String trunk, String message) {
        prerequisite(false);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2:"+dtmfMessage +"，[caller]:"+caller+" ,[callee]:"+routePrefix + callee+"[dtmf] "+dtmf);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*2);
        pjsip.Pj_Send_Dtmf(caller, dtmf);

        assertStep(caller + "[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1004,RING,20),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1004,false);
        Assert.assertEquals(getExtensionStatus(1004,TALKING,20),TALKING,"[通话状态校验_通话] Time："+ DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("3001<3001>", "IVR IVR1<6201>", "ANSWERED", "Invalid key", trunk, "", "Inbound"))
                .contains(tuple("3001<3001>", "t estX<1004>", "ANSWERED", "3001<3001> hung up", trunk, "", "Inbound"));
        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("IVR")
    @Story("KeyPressEvent")
    @Description("编辑IVR1，按0到FXS分机1020【前提是存在1020】\n" +
            "\t23.通过sip外线呼入到IVR1，按0\n" +
            "\t\t分机FXS-1020可正常响铃、接听；即辅助2分机2000可正常响铃、接听；cdr正确" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P3", "IVR","KeyPressEvent", "testIVR54_KeyPressEvent_23","FXS","PSeries"}, dataProvider = "routesKeyPressEvent")
    public void testIVR54_KeyPressEvent_23(String routePrefix, int caller, String callee, String deviceAssist, String dtmf, String dtmfMessage, String trunk, String message) {
        prerequisite(false);

        step("1:login with admin,trunk: "+message);
        auto.loginPage().loginWithAdmin();

        step("2:"+dtmfMessage +"，[caller]:"+caller+" ,[callee]:"+routePrefix + callee+"[dtmf] "+dtmf);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT*3);
        pjsip.Pj_Send_Dtmf(caller, dtmf);

        assertStep(caller + "[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(2000,RING,20),RING,"[通话状态校验_响铃] Time："+ DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000,false);
        Assert.assertEquals(getExtensionStatus(2000,TALKING,20),TALKING,"[通话状态校验_通话] Time："+ DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording,HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time："+ DataUtils.getCurrentTime()).extracting("callFrom","callTo","status","reason","sourceTrunk","destinationTrunk","communicatonType")
                .contains(tuple("3001<3001>", "IVR IVR1<6201>", "ANSWERED", "3001<3001> called Extension", trunk, "", "Inbound"))
                .contains(tuple("3001<3001>", "1020 1020<1020>", "ANSWERED", "3001<3001> hung up", trunk, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("Delete")
    @Description("1.单条删除IVR1\n" +
            "\t检查列表IVR1被删除成功" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2", "IVR", "testIVR55_Delete_1","PSeries","Cloud","K2","Delete"})
    public void testIVR55_Delete_1() {
        prerequisite(true);
        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:进入IVR界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);

        List<String> lists = TableUtils.getTableForHeader(getDriver(),"Number");
        if(!lists.contains("6202")){
            auto.ivrPage().createIVR("6202","IVR2_6202").clickSaveAndApply();
        }
        step("3:删除IVR");
        auto.ivrPage().deleDataByDeleImage("6202").clickSaveAndApply();

        assertStep("[删除成功]");
        List<String> list = TableUtils.getTableForHeader(getDriver(),"Number");
        softAssertPlus.assertThat(list).doesNotContain("6202");
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("IVR")
    @Story("Delete")
    @Description("1.批量删除IVR1\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"P2", "IVR", "testIVR56_Delete_2","PSeries","Cloud","K2","Delete"})
    public void testIVR56_Delete_2() {
        prerequisite(true);

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:进入IVR界面");
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_feature, HomePage.Menu_Level_2.call_feature_tree_ivr);

        step("3:批量删除IVR1");
        auto.ivrPage().deleAllIVR().clickSaveAndApply();

        assertStep("[删除成功]");
        List<String> list = TableUtils.getTableForHeader(getDriver(),"Number");
        softAssertPlus.assertThat(list.size()).isEqualTo(0);
        softAssertPlus.assertAll();
    }

}

