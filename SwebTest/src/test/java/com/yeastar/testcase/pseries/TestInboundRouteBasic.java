package com.yeastar.testcase.pseries;

import com.jcraft.jsch.JSchException;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.OperatorPanel.OperatorPanelPage;
import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.*;
import com.yeastar.untils.CDRObject.CDRNAME;
import com.yeastar.untils.CDRObject.STATUS;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static org.assertj.core.api.Assertions.tuple;

/**
 * @program: SwebTest
 * @description: test conference
 * @author: huangjx@yeastar.com
 * @create: 2020/11/04
 */
@Log4j2
public class TestInboundRouteBasic extends TestCaseBaseNew {
    List<String> trunk9 = new ArrayList<>();
    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();

    private boolean isRunRecoveryEnvFlag = true;
    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;
    private String EXTENSION_1000_HUNGUP = "test A<1000> hung up";

    TestInboundRouteBasic() {
        trunk9.add(SPS);
        trunk9.add(BRI_1);
        trunk9.add(FXO_1);
        trunk9.add(E1);
        trunk9.add(SIPTrunk);
        trunk9.add(ACCOUNTTRUNK);
        trunk9.add(GSM);
    }


    //############### dataProvider #########################

    public void prerequisite() {
        long startTime = System.currentTimeMillis();
        if (isDebugInitExtensionFlag) {
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
            initIVR();
            initInbound();
            initOutbound();

            isRunRecoveryEnvFlag = registerAllExtensions();
        }
        step("=========== init before class  end =========");
        log.debug("[prerequisite time]:" + (System.currentTimeMillis() - startTime) / 1000 + " Seconds");
    }

    @DataProvider(name = "routes")
    public Object[][] Routes(ITestContext c, Method method) {
        Object[][] group = null;
        String methodName = method.getName();
        //SIP_REGISTER,SPS,ACCOUNT
        if (methodName.contains("_01_03")) {
            return new Object[][]{
                    //routePrefix（路由前缀） + caller（主叫） + callee（被叫） + device_assist（主叫所在的设置ip）+ cdrCaller(CDR caller显示) + trunk(路由)
                    {"", 3001, "3000", DEVICE_ASSIST_1, "3001<3001>", SIPTrunk},//SIP  --55 REGISTER
                    {"99", 2000, "1000", DEVICE_ASSIST_2, "2000<2000>", SPS},//sps   前缀 替换
                    {"44", 4000, "1000", DEVICE_ASSIST_3, "4000<4000>", ACCOUNTTRUNK},
            };
        }
        //BRI,E1
        if (methodName.contains("_04_05") || methodName.contains("_10_11") || methodName.contains("_17_18") || methodName.contains("_24_25") ||
                methodName.contains("_31_32")) {
            return new Object[][]{
                    //routePrefix（路由前缀） + caller（主叫） + callee（被叫） + device_assist（主叫所在的设置ip）+ cdrCaller(CDR caller显示) + trunk(路由)
                    {"88", 2000, "1000", DEVICE_ASSIST_2, "2000<2000>", BRI_1},//BRI   前缀 替换
                    {"66", 2000, "1000", DEVICE_ASSIST_2, "2000<2000>", E1}
            };
        }
        //FXO,GSM
        if (methodName.contains("_06_07") || methodName.contains("_12_13") || methodName.contains("_19_20") || methodName.contains("_26_27") ||
                methodName.contains("_33_34") || methodName.contains("_36_37")) {
            return new Object[][]{
                    //routePrefix（路由前缀） + caller（主叫） + callee（被叫） + device_assist（主叫所在的设置ip）+ cdrCaller(CDR caller显示) + trunk(路由)
                    {"", 2000, "2005", DEVICE_ASSIST_2, "2000<2000>", FXO_1},//FXO --77 不输   2005（FXS）
                    {"33", 2000, DEVICE_TEST_GSM, DEVICE_ASSIST_2, DEVICE_ASSIST_GSM + "<" + DEVICE_ASSIST_GSM + ">", GSM}
            };
        }

        //SIP_REGEIST
        if (methodName.contains("_08") || methodName.contains("_14") || methodName.contains("_21") || methodName.contains("_28") ||
                methodName.contains("_35") || methodName.contains("_45") || methodName.contains("_46")) {
            return new Object[][]{
                    //routePrefix（路由前缀） + caller（主叫） + callee（被叫） + device_assist（主叫所在的设置ip）+ cdrCaller(CDR caller显示) + trunk(路由)
                    {"", 3001, "3000", DEVICE_ASSIST_1, "3001<3001>", SIPTrunk},//SIP  --55 REGISTER
            };
        }

        //ACCOUNT
        if (methodName.contains("_09") || methodName.contains("_38")) {
            return new Object[][]{
                    //routePrefix（路由前缀） + caller（主叫） + callee（被叫） + device_assist（主叫所在的设置ip）+ cdrCaller(CDR caller显示) + trunk(路由)
                    {"44", 4000, "1000", DEVICE_ASSIST_3, "4000<4000>", ACCOUNTTRUNK},
            };
        }

        //SPS,ACCOUNT
        if (methodName.contains("_15_16") || methodName.contains("_22_23") || methodName.contains("_29_30")) {
            return new Object[][]{
                    //routePrefix（路由前缀） + caller（主叫） + callee（被叫） + device_assist（主叫所在的设置ip）+ cdrCaller(CDR caller显示) + trunk(路由)
                    {"99", 2000, "1000", DEVICE_ASSIST_2, "2000<2000>", SPS},//sps   前缀 替换
                    {"44", 4000, "1000", DEVICE_ASSIST_3, "4000<4000>", ACCOUNTTRUNK},
            };
        }

        //SPS
        if (methodName.contains("_39_") || methodName.contains("_43") || methodName.contains("_42") || methodName.contains("_47") || methodName.contains("_44")) {
            return new Object[][]{
                    //routePrefix（路由前缀） + caller（主叫） + callee（被叫） + device_assist（主叫所在的设置ip）+ cdrCaller(CDR caller显示) + trunk(路由)
                    {"99", 2000, "1000", DEVICE_ASSIST_2, "2000<2000>", SPS},//sps   前缀 替换
            };
        }
        return null;
    }

//    @BeforeMethod(alwaysRun = true)
//    public void initEnv(){
//        step("=========== init env start ==========");
//        prerequisite();
//        step("=========== init env end ==========");
//    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Basic,Trunk,InboundRoute,Extension,SIP")
    @Description("编辑呼入路由In1的目的地为分机1000\n" +
            "\t1.通过sip外线呼入到分机1000\n" +
            "\t\t1000响铃，接听，主叫挂断；检测cdr\n" +
            "\t2.通过sps外线呼入到分机1000\n" +
            "\t\t1000响铃，接听，被叫挂断；检测cdr\n" +
            "\t3.通过Account外线呼入到分机1000\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P1", "Basic", "Trunk", "InboundRoute", "Extension", "SIP"}, dataProvider = "routes")
    public void testIRB_01_03_Basic(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) {
        prerequisite();
        step("编辑呼入路由In1的目的地为分机1000");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1000").id)).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(cdrCaller, CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), cdrCaller + " hung up", trunk, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Basic,Trunk,InboundRoute,Extension,SIP")
    @Description("编辑呼入路由In1的目的地为分机1000\n" +
            "\t4.通过BRI外线呼入到分机1000\n" +
            "\t\t1000响铃，接听，挂断；检测cdr\n" +
            "\t5.通过E1外线呼入到分机1000\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P1", "Basic", "Trunk", "InboundRoute", "Extension", "SIP"}, dataProvider = "routes")
    public void testIRB_04_05_Basic(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) {
        prerequisite();
        step("编辑呼入路由In1的目的地为分机1000");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1001").editInbound("In1", String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1000").id)).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 20), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 20), TALKING);

        step("[被叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(cdrCaller, CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), EXTENSION_1000_HUNGUP.toString(), trunk, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Basic,Trunk,InboundRoute,Extension,SIP")
    @Description("编辑呼入路由In1的目的地为分机1000\n" +
            "\t6.通过FXO外线呼入到分机1000\n" +
            "\t\t1000响铃，接听，挂断；检测cdr\n" +
            "\t7.通过GSM外线呼入到分机1000\n" +
            "\t\t1000响铃，接听，挂断；检测cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P1", "Basic", "Trunk", "InboundRoute", "Extension", "SIP"}, dataProvider = "routes")
    public void testIRB_06_07_Basic(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) throws IOException, JSchException {
        if (trunk.trim().equalsIgnoreCase("null") || trunk.trim().equalsIgnoreCase("")) {
            Assert.assertTrue(false, "FXO 线路 不通！");
        }
        if (DEVICE_TEST_GSM.trim().equalsIgnoreCase("") || DEVICE_TEST_GSM.trim().equalsIgnoreCase("null") || DEVICE_ASSIST_GSM.trim().equalsIgnoreCase("") || DEVICE_ASSIST_GSM.trim().equalsIgnoreCase("null")) {
            Assert.assertTrue(false, "GSM 线路 不通！");
        }
        prerequisite();
        step("编辑呼入路由In1的目的地为分机1000");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1000").id)).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 120), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 60), TALKING);

        //GSM 通话时长必须大于 60s
        if (trunk.trim().equalsIgnoreCase(GSM)) {
            sleep(WaitUntils.SHORT_WAIT * 30);
        }
        step("[被叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(cdrCaller, CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), EXTENSION_1000_HUNGUP.toString(), trunk, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,FXS")
    @Description("判断是否存在FXS分机1020，存在则执行编辑呼入路由In1的目的地为FXS分机1020" +
            "8.通过sip外线呼入到分机1020\n" +
            "\t辅助2对应的分机2000响铃，接听，主叫挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P2", "Trunk", "InboundRoute", "FXS", "_08"}, dataProvider = "routes")
    public void testIRB_08_FXS(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) {
        prerequisite();
        step("判断是否存在FXS分机1020，存在则执行编辑呼入路由In1的目的地为FXS分机1020");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1020").id)).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(2000, RING, 30), RING);
        pjsip.Pj_Answer_Call(2000, false);
        Assert.assertEquals(getExtensionStatus(2000, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(cdrCaller, CDRNAME.Extension_1020.toString(), STATUS.ANSWER.toString(), cdrCaller + " hung up", trunk, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,FXS")
    @Description("判断是否存在FXS分机1020，存在则执行编辑呼入路由In1的目的地为FXS分机1020\n" +
            "\t9.通过Account外线呼入到分机1020\n" +
            "\t\t辅助2对应的分机2000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "InboundRoute", "FXS"}, dataProvider = "routes")
    public void testIRB_09_FXS(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) {
        prerequisite();
        step("判断是否存在FXS分机1020，存在则执行编辑呼入路由In1的目的地为FXS分机1020");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1020").id)).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(2000, RING, 30), RING);
        pjsip.Pj_Answer_Call(2000, false);
        Assert.assertEquals(getExtensionStatus(2000, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(cdrCaller, CDRNAME.Extension_1020.toString(), STATUS.ANSWER.toString(), cdrCaller + " hung up", trunk, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,FXS")
    @Description("判断是否存在FXS分机1020，存在则执行编辑呼入路由In1的目的地为FXS分机1020\n" +
            "\t10.通过BRI外线呼入到分机1020\n" +
            "\t\t辅助2对应的分机2000响铃，接听，挂断；检查cdr\n" +
            "\t11.通过E1外线呼入到分机1020\n" +
            "\t\t辅助2对应的分机2000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "InboundRoute", "FXS"}, dataProvider = "routes")
    public void testIRB_10_11_FXS(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) {
        prerequisite();
        step("判断是否存在FXS分机1020，存在则执行编辑呼入路由In1的目的地为FXS分机1020");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1020").id)).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(2000, RING, 30), RING);
        pjsip.Pj_Answer_Call(2000, false);
        Assert.assertEquals(getExtensionStatus(2000, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(cdrCaller, CDRNAME.Extension_1020.toString(), STATUS.ANSWER.toString(), cdrCaller + " hung up", trunk, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,FXS")
    @Description("判断是否存在FXS分机1020，存在则执行编辑呼入路由In1的目的地为FXS分机1020\n" +
            "\t12.通过FXO外线呼入到分机1020\n" +
            "\t\t辅助2对应的分机2000响铃，接听，挂断；检查cdr\n" +
            "\t13.通过GSM外线呼入到分机1020\n" +
            "\t\t辅助2对应的分机2000响铃，接听，挂断；检查cdr\n" +
            "\t拨打电话时注意辅助2的主叫不能是2000分机")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "InboundRoute", "FXS"}, dataProvider = "routes")
    public void testIRB_12_13_FXS(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) {
        if (trunk.trim().equalsIgnoreCase("null") || trunk.trim().equalsIgnoreCase("")) {
            Assert.assertTrue(false, "FXO 线路 不通！");
        }
        if (DEVICE_TEST_GSM.trim().equalsIgnoreCase("") || DEVICE_TEST_GSM.trim().equalsIgnoreCase("null") || DEVICE_ASSIST_GSM.trim().equalsIgnoreCase("") || DEVICE_ASSIST_GSM.trim().equalsIgnoreCase("null")) {
            Assert.assertTrue(false, "GSM 线路 不通！");
        }
        prerequisite();
        step("编辑呼入路由In1的目的地为分机1000");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1000").id)).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 120), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 60), TALKING);

        //GSM 通话时长必须大于 60s
        if (trunk.trim().equalsIgnoreCase(GSM)) {
            sleep(WaitUntils.SHORT_WAIT * 30);
        }
        step("[被叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(cdrCaller, CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), EXTENSION_1000_HUNGUP.toString(), trunk, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,HangUp")
    @Description("编辑呼入路由In1的目的地为HangUp\n" +
            "14.通过sip外线呼入\n" +
            "\t通话被自动挂断;检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P2", "Trunk", "InboundRoute", "HangUp"}, dataProvider = "routes", enabled = true)
    public void testIRB_14_HangUp(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) {
        prerequisite();
        step("编辑呼入路由In1的目的地为HangUp");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"end_call\",\"def_dest_value\":\"\"")).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        step("[通话状态校验]");
        int result = getExtensionStatus(caller, HUNGUP, 30);
        Assert.assertTrue((result == HUNGUP) || (result == IDLE));
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,HangUp")
    @Description("编辑呼入路由In1的目的地为HangUp\n" +
            "\t15.通过sps外线呼入\n" +
            "\t\t通话被自动挂断;检查cdr\n" +
            "\t16.通过Account外线呼入\n" +
            "\t\t通话被自动挂断;检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "InboundRoute", "HangUp"}, dataProvider = "routes")
    public void testIRB_15_16_HangUp(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) {
        prerequisite();
        step("编辑呼入路由In1的目的地为HangUp");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"end_call\",\"def_dest_value\":\"\"")).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(caller, HUNGUP, 100), HUNGUP);
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,HangUp")
    @Description("编辑呼入路由In1的目的地为HangUp\n" +
            "\t17.通过BRI外线呼入\n" +
            "\t\t通话被自动挂断;检查cdr\n" +
            "\t18.通过E1外线呼入\n" +
            "\t\t通话被自动挂断;检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "InboundRoute", "HangUp"}, dataProvider = "routes")
    public void testIRB_17_18_HangUp(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) {
        prerequisite();
        step("编辑呼入路由In1的目的地为HangUp");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"end_call\",\"def_dest_value\":\"\"")).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(caller, HUNGUP, 100), HUNGUP);
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,HangUp")
    @Description("编辑呼入路由In1的目的地为HangUp\n" +
            "\t19通过FXO外线呼入\n" +
            "\t\t通话被自动挂断;检查cdr\n" +
            "\t20.通过GSM外线呼入\n" +
            "\t\t通话被自动挂断;检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "InboundRoute", "HangUp"}, dataProvider = "routes")
    public void testIRB_19_20_HangUp(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) {
        if (trunk.trim().equalsIgnoreCase("null") || trunk.trim().equalsIgnoreCase("")) {
            Assert.assertTrue(false, "FXO 线路 不通！");
        }
        if (DEVICE_TEST_GSM.trim().equalsIgnoreCase("") || DEVICE_TEST_GSM.trim().equalsIgnoreCase("null") || DEVICE_ASSIST_GSM.trim().equalsIgnoreCase("") || DEVICE_ASSIST_GSM.trim().equalsIgnoreCase("null")) {
            Assert.assertTrue(false, "GSM 线路 不通！");
        }
        prerequisite();
        step("编辑呼入路由In1的目的地为HangUp");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"end_call\",\"def_dest_value\":\"\"")).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        //FXO 线路不判断
        if (trunk.trim().equalsIgnoreCase(GSM)) {
            step("[通话状态校验]");
            Assert.assertEquals(getExtensionStatus(caller, HUNGUP, 100), HUNGUP, "[caller] " + caller);
        }
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,Voicemail")
    @Description("编辑呼入路由In1的目的地为Voicemail-分机1000\n" +
            "21.通过sip外线呼入\n" +
            "\t保持通话20s主叫挂断;检查cdr；登录分机1000查看新增一条语音留言，Name记录正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P2", "Trunk", "InboundRoute", "Voicemail"}, dataProvider = "routes")
    public void testIRB_21_Voicemail(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) {
        prerequisite();
        step("编辑呼入路由In1的目的地为Voicemail-分机1000");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"ext_vm\",\"def_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1000").id)).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT * 2);
        sleep(20 * 1000);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(caller);

        step("登录分机1000查看新增一条语音留言，Name记录正确");
        auto.homePage().logout();
        auto.loginPage().login("1000", EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT * 2);
        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        softAssertPlus.assertThat(TableUtils.getTableForHeader(getDriver(), "Name", 0)).contains(caller + "");

        String voiceMailTime = TableUtils.getTableForHeader(getDriver(), "Time", 0);
        log.debug("[callTime] " + callTime + " ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        assertStep("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(cdrCaller, CDRNAME.Extension_1000_VOICEMAIL.toString(), STATUS.VOICEMAIL.toString(), cdrCaller + " hung up", trunk, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,Voicemail")
    @Description("编辑呼入路由In1的目的地为Voicemail-分机1000\n" +
            "\t22.通过sps外线呼入\n" +
            "\t\t保持通话20s主叫挂断;检查cdr；登录分机1000查看新增一条语音留言，Name记录正确\n" +
            "\t23.通过Account外线呼入\n" +
            "\t\t保持通话20s主叫挂断;检查cdr；登录分机1000查看新增一条语音留言，Name记录正确\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "InboundRoute", "Voicemail"}, dataProvider = "routes")
    public void testIRB_22_23_Voicemail(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) {
        prerequisite();
        step("编辑呼入路由In1的目的地为Voicemail-分机1000");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"ext_vm\",\"def_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1000").id)).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT * 2);
        sleep(20 * 1000);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(caller);

        step("登录分机1000查看新增一条语音留言，Name记录正确");
        auto.homePage().logout();
        auto.loginPage().login("1000", EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT * 2);
        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);

        softAssertPlus.assertThat(TableUtils.getTableForHeader(getDriver(), "Name", 0)).contains(caller + "");

        String voiceMailTime = TableUtils.getTableForHeader(getDriver(), "Time", 0);
        log.debug("[callTime] " + callTime + " ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        assertStep("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(cdrCaller, CDRNAME.Extension_1000_VOICEMAIL.toString(), STATUS.VOICEMAIL.toString(), cdrCaller + " hung up", trunk, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,Voicemail")
    @Description("编辑呼入路由In1的目的地为Voicemail-分机1000\n" +
            "\t24.通过BRI外线呼入\n" +
            "\t\t保持通话20s主叫挂断;检查cdr；登录分机1000查看新增一条语音留言，Name记录正确\n" +
            "\t25.通过E1外线呼入\n" +
            "\t\t保持通话20s主叫挂断;检查cdr；登录分机1000查看新增一条语音留言，Name记录正确\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "InboundRoute", "Voicemail"}, dataProvider = "routes")
    public void testIRB_24_25_Voicemail(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) {
        prerequisite();
        step("编辑呼入路由In1的目的地为Voicemail-分机1000");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"ext_vm\",\"def_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1000").id)).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT * 2);
        sleep(20 * 1000);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(caller);

        step("登录分机1000查看新增一条语音留言，Name记录正确");
        auto.homePage().logout();
        auto.loginPage().login("1000", EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT * 2);
        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        softAssertPlus.assertThat(TableUtils.getTableForHeader(getDriver(), "Name", 0)).contains(caller + "");

        String voiceMailTime = TableUtils.getTableForHeader(getDriver(), "Time", 0);
        log.debug("[callTime] " + callTime + " ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        assertStep("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(cdrCaller, CDRNAME.Extension_1000_VOICEMAIL.toString(), STATUS.VOICEMAIL.toString(), cdrCaller + " hung up", trunk, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,Voicemail")
    @Description("编辑呼入路由In1的目的地为Voicemail-分机1000\n" +
            "\t26.通过FXO外线呼入\n" +
            "\t\t保持通话20s主叫挂断;检查cdr；登录分机1000查看新增一条语音留言，Name记录正确\n" +
            "\t27.通过GSM外线呼入\n" +
            "\t\t保持通话20s主叫挂断;检查cdr；登录分机1000查看新增一条语音留言，Name记录正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "InboundRoute", "Voicemail"}, dataProvider = "routes")
    public void testIRB_26_27_Voicemail(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) {
        if (trunk.trim().equalsIgnoreCase("null") || trunk.trim().equalsIgnoreCase("")) {
            Assert.assertTrue(false, "FXO 线路 不通！");
        }
        if (DEVICE_TEST_GSM.trim().equalsIgnoreCase("") || DEVICE_TEST_GSM.trim().equalsIgnoreCase("null") || DEVICE_ASSIST_GSM.trim().equalsIgnoreCase("") || DEVICE_ASSIST_GSM.trim().equalsIgnoreCase("null")) {
            Assert.assertTrue(false, "GSM 线路 不通！");
        }

        prerequisite();
        step("编辑呼入路由In1的目的地为Voicemail-分机1000");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"ext_vm\",\"def_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1000").id)).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT * 2);
        //GSM 通话时长必须大于 60s
        if (trunk.trim().equalsIgnoreCase(GSM)) {
            sleep(WaitUntils.SHORT_WAIT * 30);
        } else {
            sleep(20 * 1000);
        }
        step("[主叫挂断]");
        pjsip.Pj_hangupCall(caller);

        step("登录分机1000查看新增一条语音留言，Name记录正确");
        auto.homePage().logout();
        auto.loginPage().login("1000", EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT * 2);

        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        if (trunk.trim().equalsIgnoreCase(GSM)) {
            softAssertPlus.assertThat(TableUtils.getTableForHeader(getDriver(), "Name", 0)).contains(DEVICE_ASSIST_GSM);
        } else {
            softAssertPlus.assertThat(TableUtils.getTableForHeader(getDriver(), "Name", 0)).contains(caller + "");
        }

        String voiceMailTime = TableUtils.getTableForHeader(getDriver(), "Time", 0);
        log.debug("[callTime] " + callTime + " ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        assertStep("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(cdrCaller, CDRNAME.Extension_1000_VOICEMAIL.toString(), STATUS.VOICEMAIL.toString(), cdrCaller + " hung up", trunk, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,ExternalNumber")
    @Description("编辑呼入路由In1的目的地为ExternalNumber: Prefix为2，号码1234567890\n" +
            "\t28.通过sip外线呼入\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P2", "Trunk", "InboundRoute", "ExternalNumber"}, dataProvider = "routes")
    public void testIRB_28_ExternalNumber(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) {
        prerequisite();
        step("编辑呼入路由In1的目的地为ExternalNumber: Prefix为2，号码1234567890");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"external_num\",\"def_dest_prefix\":\"2\",\"def_dest_value\":\"1234567890\"")).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(2000, RING, 30), RING);
        pjsip.Pj_Answer_Call(2000, false);
        Assert.assertEquals(getExtensionStatus(2000, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(cdrCaller, "21234567890", STATUS.ANSWER.toString(), cdrCaller + " hung up", trunk, SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,ExternalNumber")
    @Description("编辑呼入路由In1的目的地为ExternalNumber: Prefix为1，号码3001\n" +
            "\t31.通过BRI外线呼入\n" +
            "\t\t辅助1的分机3001响铃，接听，挂断；检查cdr\n" +
            "\t32.通过E1s外线呼入\n" +
            "\t\t辅助1的分机3001响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("Bug CDR HangUp exception")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "InboundRoute", "ExternalNumber"}, dataProvider = "routes")
    public void testIRB_31_32_ExternalNumber(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) {
        prerequisite();
        step("编辑呼入路由In1的目的地为ExternalNumber: Prefix为2，号码1234567890");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"external_num\",\"def_dest_prefix\":\"1\",\"def_dest_value\":\"3001\"")).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001, RING, 30), RING);
        pjsip.Pj_Answer_Call(3001, false);
        Assert.assertEquals(getExtensionStatus(caller, 3001, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(cdrCaller, "13001", STATUS.ANSWER.toString(), cdrCaller + " hung up", trunk, SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,ExternalNumber")
    @Description("编辑呼入路由In1的目的地为ExternalNumber: Prefix为1，号码3001\n" +
            "\t\t辅助1的分机3001响铃，接听，挂断；检查cdr\n" +
            "\t33.通过FXO外线呼入\n" +
            "\t\t辅助1的分机3001响铃，接听，挂断；检查cdr\n" +
            "\t34.通过GSM外线呼入\n" +
            "\t\t辅助1的分机3001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("Bug CDR HangUp exception")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "InboundRoute", "ExternalNumber"}, dataProvider = "routes")
    public void testIRB_33_34_ExternalNumber(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) {
        if (trunk.trim().equalsIgnoreCase("null") || trunk.trim().equalsIgnoreCase("")) {
            Assert.assertTrue(false, "FXO 线路 不通！");
        }
        if (DEVICE_TEST_GSM.trim().equalsIgnoreCase("") || DEVICE_TEST_GSM.trim().equalsIgnoreCase("null") || DEVICE_ASSIST_GSM.trim().equalsIgnoreCase("") || DEVICE_ASSIST_GSM.trim().equalsIgnoreCase("null")) {
            Assert.assertTrue(false, "GSM 线路 不通！");
        }
        prerequisite();
        step("编辑呼入路由In1的目的地为ExternalNumber: Prefix为2，号码1234567890");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"external_num\",\"def_dest_prefix\":\"1\",\"def_dest_value\":\"3001\"")).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001, RING, 30), RING);
        pjsip.Pj_Answer_Call(3001, false);
        Assert.assertEquals(getExtensionStatus(3001, TALKING, 30), TALKING);

        //GSM 通话时长必须大于 60s
        if (trunk.trim().equalsIgnoreCase(GSM)) {
            sleep(WaitUntils.SHORT_WAIT * 30);
        }

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(cdrCaller, "13001", STATUS.ANSWER.toString(), cdrCaller + " hung up", trunk, SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,OutboundRoute")
    @Description("编辑呼入路由In1的目的地为OutboundRoute-Out8\n" +
            "\t35.通过sip外线呼入\n" +
            "\t辅助2的分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P2", "Trunk", "InboundRoute", "OutboundRoute"}, dataProvider = "routes")
    public void testIRB_35_OutBoundRoute(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) {
        prerequisite();
        step("编辑呼入路由In1的目的地为OutboundRoute-Out8");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"outroute\",\"def_dest_value\":\"%s\"", apiUtil.getOutBoundRouteSummary("Out8").id)).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(2000, RING, 30), RING);
        pjsip.Pj_Answer_Call(2000, false);
        Assert.assertEquals(getExtensionStatus(2000, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(cdrCaller, "3000", STATUS.ANSWER.toString(), cdrCaller + " hung up", trunk, SPS, "Outbound"));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,OutboundRoute")
    @Description("编辑呼入路由In1的目的地为OutboundRoute-Out8\n" +
            "\t36.辅助2分机2001通过GSM外线呼入\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n" +
            "\t\t未实测跑不通过时Q一下\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "InboundRoute", "OutboundRoute"})
    public void testIRB_36_OutBoundRoute() {
        if (DEVICE_TEST_GSM.trim().equalsIgnoreCase("") || DEVICE_TEST_GSM.trim().equalsIgnoreCase("null") || DEVICE_ASSIST_GSM.trim().equalsIgnoreCase("") || DEVICE_ASSIST_GSM.trim().equalsIgnoreCase("null")) {
            Assert.assertTrue(false, "GSM 线路 不通！");
        }
        prerequisite();
        step("编辑呼入路由In1的目的地为OutboundRoute-Out8");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"outroute\",\"def_dest_value\":\"%s\"", apiUtil.getOutBoundRouteSummary("Out8").id)).apply();

        step("1:login with admin,trunk: ");
        auto.loginPage().loginWithAdmin();

        step("2:辅助2分机2001通过GSM外线呼入");
        pjsip.Pj_Make_Call_No_Answer(2001, 33 + DEVICE_TEST_GSM, DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(2000, RING, 30), RING);
        pjsip.Pj_Answer_Call(2000, false);
        Assert.assertEquals(getExtensionStatus(2000, TALKING, 30), TALKING);

        sleep(WaitUntils.SHORT_WAIT * 30);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2001);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(DEVICE_ASSIST_GSM + "<" + DEVICE_ASSIST_GSM + ">", 7 + DEVICE_ASSIST_GSM, STATUS.ANSWER.toString(), "2001<2001> hung up", GSM, SPS, "Outbound"));

        softAssertPlus.assertAll();

    }


    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,OutboundRoute")
    @Description("编辑呼入路由In1的目的地为OutboundRoute-Out8\n" +
            "\t37.辅助2分机2001拨打2010通过FXO外线呼入\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n" +
            "\t\t未实测跑不通过时Q一下\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "InboundRoute", "OutboundRoute"})
    public void testIRB_37_OutBoundRoute() {
        if (FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")) {
            Assert.assertTrue(false, "FXO 线路 不通！");
        }

        prerequisite();
        step("编辑呼入路由In1的目的地为OutboundRoute-Out8");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"outroute\",\"def_dest_value\":\"%s\"", apiUtil.getOutBoundRouteSummary("Out8").id)).apply();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:辅助2分机2001拨打2010通过FXO外线呼入 ");
        pjsip.Pj_Make_Call_No_Answer(2001, "2005", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(2000, RING, 30), RING);
        pjsip.Pj_Answer_Call(2000, false);
        Assert.assertEquals(getExtensionStatus(2000, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2001);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2001<2001>", "13000", STATUS.ANSWER.toString(), "2001<2001> hung up", FXO_1, SPS, "Outbound"));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,OutboundRoute")
    @Description("编辑呼入路由In1的目的地为OutboundRoute-Out8\n" +
            "\t38.通过Account外线呼入\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n" +
            "\t\t未实测跑不通过时Q一下")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "InboundRoute", "OutboundRoute"}, dataProvider = "routes")
    public void testIRB_38_OutBoundRoute(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) {
        prerequisite();
        step("编辑呼入路由In1的目的地为OutboundRoute-Out8");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"outroute\",\"def_dest_value\":\"%s\"", apiUtil.getOutBoundRouteSummary("Out8").id)).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(2000, RING, 30), RING);
        pjsip.Pj_Answer_Call(2000, false);
        Assert.assertEquals(getExtensionStatus(2000, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(caller);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(cdrCaller, "test A<1000>", STATUS.ANSWER.toString(), cdrCaller + " hung up", trunk, SPS, "Outbound"));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,OutboundRoute")
    @Description("编辑呼入路由In1的目的地为OutboundRoute-Out1\n" +
            "\t39.通过sps外线拨打9913001呼入\n" +
            "\t\t辅助1的分机3001响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "InboundRoute", "OutboundRoute"})
    public void testIRB_39_OutBoundRoute() {
        prerequisite();
        step("编辑呼入路由In1的目的地为OutboundRoute-Out1");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"outroute\",\"def_dest_value\":\"%s\"", apiUtil.getOutBoundRouteSummary("Out1").id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001");
        pjsip.Pj_Make_Call_No_Answer(2000, "9913001", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001, RING, 30), RING);
        pjsip.Pj_Answer_Call(3001, false);
        Assert.assertEquals(getExtensionStatus(3001, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", "13001", "ANSWERED", "2000<2000> hung up", SPS, SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,OutboundRoute")
    @Description("编辑呼入路由In1的目的地为OutboundRoute-Out1\n" +
            "\t40.通过BRI外线拨打8813001呼入\n" +
            "\t\t辅助1的分机3001响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "InboundRoute", "OutboundRoute"})
    public void testIRB_40_OutBoundRoute() {
        prerequisite();
        step("编辑呼入路由In1的目的地为OutboundRoute-Out1");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"outroute\",\"def_dest_value\":\"%s\"", apiUtil.getOutBoundRouteSummary("Out1").id)).apply();

        step("1:login with admin,trunk: ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 8813001");
        pjsip.Pj_Make_Call_No_Answer(2000, "8813001", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001, RING, 30), RING);
        pjsip.Pj_Answer_Call(3001, false);
        Assert.assertEquals(getExtensionStatus(3001, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", "13001", STATUS.ANSWER.toString(), "2000<2000> hung up", BRI_1, SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();

    }


    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,OutboundRoute")
    @Description("编辑呼入路由In1的目的地为OutboundRoute-Out1\n" +
            "\t41.通过E1外线拨打6613001呼入\n" +
            "\t\t辅助1的分机3001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "InboundRoute", "OutboundRoute"})
    public void testIRB_41_OutBoundRoute() {
        prerequisite();
        step("编辑呼入路由In1的目的地为OutboundRoute-Out1");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"outroute\",\"def_dest_value\":\"%s\"", apiUtil.getOutBoundRouteSummary("Out1").id)).apply();

        step("1:login with admin ");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000,[callee] 6613001");
        pjsip.Pj_Make_Call_No_Answer(2000, "6613001", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(3001, RING, 30), RING);
        pjsip.Pj_Answer_Call(3001, false);
        Assert.assertEquals(getExtensionStatus(3001, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", "13001", STATUS.ANSWER.toString(), "2000<2000> hung up", E1, SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,PlayGreetingthenHangUp")
    @Description("编辑呼入路由In1的目的地为Play Greeting then Hang Up，选择prompt1,1遍\n" +
            "\t42.通过sps外线呼入\n" +
            "\t\tasterisk后台查看打印播放语音文件prompt1 一遍后，主叫被挂断；检查cdr\n" +
            "\t\t\t编辑呼入路由In1的目的地为分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P2", "Trunk", "InboundRoute", "OutboundRoute"}, dataProvider = "routes")
    public void testIRB_42_PlayGreetign(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) {
        prerequisite();
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"def_dest\":\"play_greeting\",\"def_dest_prefix\":\"1\",\"def_dest_value\":\"prompt1.wav\"")).
                apply();
        asteriskObjectList.clear();
        new Thread(new SSHLinuxUntils.AsteriskThread(asteriskObjectList, PROMPT_1)).start();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 301) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(cdrCaller, "play_file", STATUS.ANSWER.toString(), cdrCaller + " hung up", trunk, "", "Inbound"));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,PlayGreetingthenHangUp")
    @Description("编辑呼入路由In1的目的地为Play Greeting then Hang Up，选择prompt2,5遍\n" +
            "\t43.通过sps外线呼入\n" +
            "\t\tasterisk后台查看打印播放语音文件prompt2 五遍后，主叫被挂断；检查cdr\n" +
            "\t\t\t编辑呼入路由In1的目的地为分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "InboundRoute", "OutboundRoute"}, dataProvider = "routes", invocationCount = 1)
    public void testIRB_43_PlayGreetign(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) {
        prerequisite();
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"play_greeting\",\"def_dest_prefix\":\"5\",\"def_dest_value\":\"prompt2.wav\"")).apply();
        asteriskObjectList.clear();
        new Thread(new SSHLinuxUntils.AsteriskThread(asteriskObjectList, PROMPT_2)).start();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        int tmp = 0;
        while (asteriskObjectList.size() != 5 && tmp++ <= 600) {
            sleep(50);
        }
        for (int i = 0; i < asteriskObjectList.size(); i++) {
            log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
        }
        if (tmp == 601) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(cdrCaller, "play_file", STATUS.ANSWER.toString(), cdrCaller + " hung up", trunk, "", "Inbound"));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,FaxToEmail")
    @Description("44.编辑呼入路由In1的目的地为Fax to Email-分机1001\n" +
            "\t通过sps外线呼入\n" +
            "\t\tasterisk后台查看打印“1001@fax_to_email”挂断通话；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "InboundRoute", "OutboundRoute"}, dataProvider = "routes")
    public void testIRB_44_FaxToEmail(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) {
        prerequisite();
        step("编辑呼入路由In1的目的地为Fax to Email-分机1001");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"fax_to_email\",\"def_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1001").id)).apply();
        asteriskObjectList.clear();
        new Thread(new SSHLinuxUntils.AsteriskThread(asteriskObjectList, FAX_TO_EMAIL_1001)).start();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        int tmp = 0;
        while (asteriskObjectList.size() >= 1 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 301) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(cdrCaller, "play_file", STATUS.ANSWER.toString(), cdrCaller + " hung up", trunk, "", "Inbound"));

        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute,Move")
    @Description("45.编辑呼入路由In1的目的地为分机1000；\n" +
            "新建呼入路由InRoute2的目的地为分机1001，Trunk选择sip;\n" +
            "\t通过sip外线呼入\n" +
            "\t\t分机1000响铃\n" +
            "\t\t\t呼入路由List调整InRoute2在In1前面；通过sip外线呼入\n" +
            "\t\t\t\t分机1000响铃")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "InboundRoute", "OutboundRoute"}, dataProvider = "routes")
    public void testIRB_45_Move(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SIPTrunk);
        step("编辑呼入路由In1的目的地为分机1000 ; 新建呼入路由InRoute2的目的地为分机1001，Trunk选择sip");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").
                createInbound("InRoute2", trunk1, "Extension", "1001").
                apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);//sip
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 60), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);
        sleep(WaitUntils.SHORT_WAIT * 2);
        pjsip.Pj_hangupCall(caller);

        step("呼入路由List调整InRoute2在In1前面");//{"id":1612,"pos_from":2,"pos_to":1}
        apiUtil.editInbound("InRoute2", String.format("\"pos_from\":2,\"pos_to\":1")).apply();


        step("[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 60), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);
        sleep(WaitUntils.SHORT_WAIT * 2);
        pjsip.Pj_hangupCall(caller);

        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(cdrCaller, "test A<1000>", STATUS.ANSWER.toString(), cdrCaller + " hung up", trunk, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute")
    @Description("46.编辑呼入路由In1的目的地为分机1000，Trunk只选择sip\n" +
            "\t分别通过sip、account外线呼入\n" +
            "\t\t通过sip外线呼入时分机1000响铃，通过account外线呼入失败；检查cdr\n" +
            "\t\t\t编辑呼入路由In1的目的地为分机1000，Trunk全选")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "InboundRoute", "OutboundRoute"}, dataProvider = "routes")
    public void testIRB_46_InboundRoute(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SIPTrunk);
        step("编辑呼入路由In1的目的地为分机1000，Trunk只选择sip");
        apiUtil.deleteAllInbound().createInbound("In1", trunk1, "Extension", "1000").apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 60), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);
        sleep(WaitUntils.SHORT_WAIT * 2);
        pjsip.Pj_hangupCall(1000);

        step("通过account外线呼入失败");
        step("[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(2000, "441000", DEVICE_ASSIST_2, false);

        int result = getExtensionStatus(1000, RING, 10);
        log.debug("[result] " + result);
        Assert.assertTrue((result == HUNGUP) || (result == IDLE));

        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(cdrCaller, "test A<1000>", STATUS.ANSWER.toString(), EXTENSION_1000_HUNGUP.toString(), trunk, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Trunk,InboundRoute")
    @Description("47.编辑呼入路由In1的目的地为[None]，全选外线\n" +
            "\t通过sps外线呼入 99999\n" +
            "\t\t通话被挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P3", "Trunk", "InboundRoute", "OutboundRoute"}, dataProvider = "routes")
    public void testIRB_47_InboundRoute(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) {
        prerequisite();
        step("编辑呼入路由In1的目的地为[None]，全选外线");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").editInbound("In1", String.format("\"def_dest\":\"\"")).apply();

        step("1:login with admin,trunk: " + trunk);
        auto.loginPage().loginWithAdmin();

        step("通过account外线呼入失败");
        step("[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, "999999", DEVICE_ASSIST_2, false);

        int result = getExtensionStatus(1000, RING, 10);
        log.debug("[result] " + result);
        Assert.assertTrue((result == HUNGUP) || (result == IDLE));

    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Delete")
    @Description("48.单条删除In1\n" +
            "\t检查列表In1被删除成功\n" +
            "\t\t添加呼入路由In1，选择所有外线，分机目的地设置为分机1000，其它默认\n" +
            "\t\t\t恢复初始化环境")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P2", "Trunk", "InboundRoute", "OutboundRoute"})
    public void testIRB_48_Delete() {
        prerequisite();
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").apply();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);

        step("单条删除");
        auto.inboundRoute().deleDataByDeleImage("In1").clickSaveAndApply();

        assertStep("[删除成功]");
        List<String> list = TableUtils.getTableForHeader(getDriver(), "Name");
        softAssertPlus.assertThat(list).doesNotContain("In1");
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("InboundRoute-Basic")
    @Story("Delete")
    @Description("49.再次创建InboundRoute2,选择sip外线\n" +
            "\t批量选择删除InboundRoute2\n" +
            "\t\t检查列表InboundRoute2被删除成功")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "P2", "Trunk", "InboundRoute", "OutboundRoute"})
    public void testIRB_49_Delete() {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SIPTrunk);
        step("编辑呼入路由In1的目的地为分机1000 ; 新建呼入路由InRoute2的目的地为分机1001，Trunk选择sip");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").
                createInbound("InRoute2", trunk1, "Extension", "1001").
                apply();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_control, HomePage.Menu_Level_2.call_control_tree_inbound_routes);

        step("2:批量删除");
        auto.inboundRoute().deleteAllInboundRoutes().clickSaveAndApply();

        assertStep("[删除成功]");
        List<String> list = TableUtils.getTableForHeader(getDriver(), "Number");
        softAssertPlus.assertThat(list.size()).isEqualTo(0);
        softAssertPlus.assertAll();

    }
}