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
public class TestInboundRouteDIDPattern extends TestCaseBaseNew {
    List<String> trunk9 = new ArrayList<>();
    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();
    Object[][] routes = new Object[][]{
            //routePrefix（路由前缀） + caller（主叫） + callee（被叫） + device_assist（主叫所在的设置ip）   + InBoundTrunk(呼入线路) + OutBoundTrunk(呼出线路)


            {"99", 2000, "1000", DEVICE_ASSIST_2, "2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), SPS},//sps   前缀 替换
            {"88", 2000, "1000", DEVICE_ASSIST_2, "2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), BRI_1},//BRI   前缀 替换
            {"", 2000, "2005", DEVICE_ASSIST_2, "2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), FXO_1},//FXO --77 不输   2005（FXS）
            {"66", 2000, "1000", DEVICE_ASSIST_2, "2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), E1},//E1     前缀 替换
            {"", 3001, "3000", DEVICE_ASSIST_1, "3001 [3001]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), SIPTrunk},//SIP  --55 REGISTER
            {"44", 4000, "1000", DEVICE_ASSIST_3, "4000 [4000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), ACCOUNTTRUNK},
//            {"33", 2000,DEVICE_TEST_GSM,DEVICE_ASSIST_2,DEVICE_ASSIST_GSM+" ["+DEVICE_ASSIST_GSM+"]",RECORD_DETAILS.EXTERNAL.getAlias(),"GSM"}
    };
    private boolean isRunRecoveryEnvFlag = true;
    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;
    private String EXTENSION_1000_HUNGUP = "test A<1000> hung up";


    //############### dataProvider #########################

    TestInboundRouteDIDPattern() {
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
            initOutbound();
            initRingGroup();
            initQueue();
            initConference();
            initIVR();
            initInbound();


            isRunRecoveryEnvFlag = registerAllExtensions();
        }
        step("=========== init before class  end =========");
        log.debug("[prerequisite time]:" + (System.currentTimeMillis() - startTime) / 1000 + " Seconds");
    }

    @DataProvider(name = "routes")
    public Object[][] Routes(ITestContext c, Method method) {
        Object[][] group = null;
        String methodName = method.getName();

        //#### ci base on keyword
//        for (String groups : c.getIncludedGroups()) {
//           if (groups.equalsIgnoreCase("SPS")) {
//               group = new Object[][]{{"99", 2000, "6200", DEVICE_ASSIST_2, "2000 [2000]", OperatorPanelPage.RECORD_DETAILS.EXTERNAL_IVR.getAlias(), "SPS"}};
//           }
//        }

        //#### local base on test name###########
        if (methodName.contains("testIRDID_01_05_DIDPattern")) {
            return new Object[][]{
                    //routePrefix（路由前缀） + caller（主叫） + callee（被叫） + device_assist（主叫所在的设置ip）+ cdrCaller(CDR caller显示) + trunk(路由)
                    {"99", 2000, "0123456789", DEVICE_ASSIST_2, "2000<2000>", SPS},//sps   前缀 替换
                    {"99", 2000, "+123456", DEVICE_ASSIST_2, "2000<2000>", SPS},//sps   前缀 替换
                    {"99", 2000, "s", DEVICE_ASSIST_2, "2000<2000>", SPS},//sps   前缀 替换
                    {"99", 2000, "abcdefghijklmno", DEVICE_ASSIST_2, "2000<2000>", SPS},//sps   前缀 替换
                    {"99", 2000, "056789+", DEVICE_ASSIST_2, "2000<2000>", SPS},//sps   前缀 替换
            };
        }
        if (methodName.contains("testIRDID_08_10_DIDPattern")) {
            return new Object[][]{
                    //routePrefix（路由前缀） + caller（主叫） + callee（被叫） + device_assist（主叫所在的设置ip）+ cdrCaller(CDR caller显示) + trunk(路由)
                    {"99", 2000, "s", DEVICE_ASSIST_2, "2000<2000>", SPS},//sps   前缀 替换
                    {"99", 2000, "abcdefghijklmno", DEVICE_ASSIST_2, "2000<2000>", SPS},//sps   前缀 替换
                    {"99", 2000, "056789+", DEVICE_ASSIST_2, "2000<2000>", SPS},//sps   前缀 替换
            };
        }
        if (methodName.contains("testIRDID_18_22_DIDPattern")) {
            return new Object[][]{
                    //routePrefix（路由前缀） + caller（主叫） + callee（被叫） + device_assist（主叫所在的设置ip）+ cdrCaller(CDR caller显示) + trunk(路由)
                    {"99", 2000, "+059209129921", DEVICE_ASSIST_2, "2000<2000>", SPS},//sps   前缀 替换
                    {"99", 2000, "+059100029955", DEVICE_ASSIST_2, "2000<2000>", SPS},//sps   前缀 替换
                    {"99", 2000, "+059100219988", DEVICE_ASSIST_2, "2000<2000>", SPS},//sps   前缀 替换
                    {"99", 2000, "+059109129999", DEVICE_ASSIST_2, "2000<2000>", SPS},//sps   前缀 替换
                    {"99", 2000, "+05910912992", DEVICE_ASSIST_2, "2000<2000>", SPS},//sps   前缀 替换

            };
        }


        return group;
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("1.通过sps外线拨打990123456789\n" +
            "\t分机1000响铃，接听，挂断；检查cdr\n" +
            "2.通过sps外线拨打99+123456\n" +
            "\t分机1000响铃，接听，挂断；检查cdr\n" +
            "3.通过sps外线拨打99s\n" +
            "\t分机1000响铃，接听，挂断；检查cdr\n" +
            "4.通过sps外线拨打99abcdefghijklmno\n" +
            "\t分机1000响铃，接听，挂断；检查cdr\n" +
            "5.通过sps外线拨打99056789+\n" +
            "\t分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "SPS", "P3","testIRDID_01_05_DIDPattern"}, dataProvider = "routes")
    public void testIRDID_01_05_DIDPattern(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) {
        prerequisite();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(cdrCaller, CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", trunk, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("6.新建呼入路由Inbound1，DID Pattern选择“DID Pattern”，添加规则0123456789；Trunk选择sps外线，呼入目的地为分机B-1001\n" +
            "\t通过sps外线拨打990123456789\n" +
            "\t\t分机1001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "SPS", "P2","testIRDID_06_DIDPattern"})
    public void testIRDID_06_DIDPattern() {
        prerequisite();
        List<String> trunk = new ArrayList<>();
        trunk.add(SPS);
        step("新建呼入路由Inbound1，DID Pattern选择“DID Pattern”，添加规则0123456789；Trunk选择sps外线，呼入目的地为分机B-1001");
        apiUtil.deleteInbound("Inbound1").createInbound("Inbound1", trunk, "Extension", "1001").
                editInbound("Inbound1", String.format("\"did_pattern_list\":[{\"did_pattern\":\"0123456789\"}]")).apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 990123456789" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "990123456789", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1001, RING, 30), RING);
        pjsip.Pj_Answer_Call(1001, false);
        Assert.assertEquals(getExtensionStatus(1001, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("新建呼入路由Inbound1,DID Pattern选择“DID Pattern\",添加规则1：+123456 ，规则2：s ,规则3：abcdefghijklmno，规则4：99056789+\n" +
            "\t7.通过sps外线拨打99+123456\n" +
            "\t\t分机1001响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "SPS", "P2"})
    public void testIRDID_07_DIDPattern() {
        prerequisite();
        List<String> trunk = new ArrayList<>();
        trunk.add(SPS);
        step("新建呼入路由Inbound1,DID Pattern选择“DID Pattern\",添加规则1：+123456 ，规则2：s ,规则3：abcdefghijklmno，规则4：99056789+");
        apiUtil.deleteInbound("Inbound1").createInbound("Inbound1", trunk, "Extension", "1001").
                editInbound("Inbound1", String.format("\"did_pattern_list\":[{\"did_pattern\":\"+123456\"},{\"did_pattern\":\"s\"},{\"did_pattern\":\"abcdefghijklmno\"},{\"did_pattern\":\"99056789+\"}]")).apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 99+123456" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "99+123456", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1001, RING, 30), RING);
        pjsip.Pj_Answer_Call(1001, false);
        Assert.assertEquals(getExtensionStatus(1001, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("新建呼入路由Inbound1,DID Pattern选择“DID Pattern\",添加规则1：+123456 ，规则2：s ,规则3：abcdefghijklmno，规则4：99056789+\n" +
            "8.通过sps外线拨打99s\n" +
            "\t分机1001响铃，接听，挂断；检查cdr\n" +
            "9.通过sps外线拨打99abcdefghijklmno\n" +
            "\t分机1001响铃，接听，挂断；检查cdr\n" +
            "10.通过sps外线拨打99056789+\n" +
            "\t分机1001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "SPS", "P3"}, dataProvider = "routes")
    public void testIRDID_08_10_DIDPattern(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("新建呼入路由Inbound1,DID Pattern选择“DID Pattern\",添加规则1：+123456 ，规则2：s ,规则3：abcdefghijklmno，规则4：99056789+");
        apiUtil.deleteAllInbound().createInbound("Inbound1", trunk1, "Extension", "1001").
                editInbound("Inbound1", String.format("\"did_pattern_list\":[{\"did_pattern\":\"+123456\"},{\"did_pattern\":\"s\"},{\"did_pattern\":\"abcdefghijklmno\"},{\"did_pattern\":\"056789+\"}]")).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1001, RING, 30), RING);
        pjsip.Pj_Answer_Call(1001, false);
        Assert.assertEquals(getExtensionStatus(1001, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("新建呼入路由Inbound1,DID Pattern选择“DID Pattern\",添加规则1：+123456 ，规则2：s ,规则3：abcdefghijklmno，规则4：99056789+\n" +
            "11.通过sps外线拨打99123456\n" +
            "\t分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "Extension", "P3"})
    public void testIRDID_11_DIDPattern() {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("新建呼入路由Inbound1，DID Pattern选择“DID Pattern”，添加规则0123456789；Trunk选择sps外线，呼入目的地为分机B-1001");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000")
                .createInbound("Inbound1", trunk1, "Extension", "1001").
                editInbound("Inbound1", String.format("\"did_pattern_list\":[{\"did_pattern\":\"+123456\"},{\"did_pattern\":\"s\"},{\"did_pattern\":\"abcdefghijklmno\"},{\"did_pattern\":\"056789+\"}]")).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee]99123456 " + ",[trunk] ");
        pjsip.Pj_Make_Call_No_Answer(2000, "99123456", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("编辑呼入路由In1,DID Pattern选择“DID Pattern\",添加规则： .\n" +
            "\t12.通过sps外线拨打99123abc\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "SPS", "P3"})
    public void testIRDID_12_DIDPattern() {
        prerequisite();
        step("编辑呼入路由In1,DID Pattern选择“DID Pattern\",添加规则： .\n");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_pattern_list\":[{\"did_pattern\":\".\"}]")).apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 99123abc" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "99123abc", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("新建呼入路由Inbound2，DID Pattern选择“DID Pattern\"，添加规则：123! ，呼入目的地为IVR-IVR0\n" +
            "\t13.通过sps外线拨打99123\n" +
            "\t\tasterisk后台检测到播放提示音“ivr-greeting-dial-ext”，按0，分机1000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "IVR", "P3"})
    public void testIRDID_13_DIDPattern() {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("新建呼入路由Inbound2，DID Pattern选择“DID Pattern\"，添加规则：123! ，呼入目的地为IVR-IVR0");
        apiUtil.deleteAllInbound().createInbound("Inbound2", trunk1, "Extension", "1000").
                editInbound("Inbound2", String.format("\"def_dest\":\"ivr\",\"def_dest_value\":\"%s\"", apiUtil.getIVRSummary("6200").id)).
                editInbound("Inbound2", String.format("\"did_pattern_list\":[{\"did_pattern\":\"123!\"}]")).apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread = new SSHLinuxUntils.AsteriskThread(asteriskObjectList, IVR_GREETING_DIAL_EXT);
        thread.start();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 99123" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "99123", DEVICE_ASSIST_2, false);
        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("【asteriskObjectList.size()】"+asteriskObjectList.size());
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 301) {
            log.debug("【asteriskObjectList.size()】"+asteriskObjectList.size());
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000, "0");

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("新建呼入路由Inbound2，DID Pattern选择“DID Pattern\"，添加规则：123! ，呼入目的地为IVR-IVR0\n" +
            "\t14.通过sps外线拨打991234abc\n" +
            "\t\tasterisk后台检测到播放提示音“ivr-greeting-dial-ext”，按0，分机1000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "IVR", "P3"})
    public void testIRDID_14_DIDPattern() {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("新建呼入路由Inbound2，DID Pattern选择“DID Pattern\"，添加规则：123! ，呼入目的地为IVR-IVR0");
        apiUtil.deleteAllInbound().createInbound("Inbound2", trunk1, "Extension", "1000").
                editInbound("Inbound2", String.format("\"def_dest\":\"ivr\",\"def_dest_value\":\"%s\"", apiUtil.getIVRSummary("6200").id)).
                editInbound("Inbound2", String.format("\"did_pattern_list\":[{\"did_pattern\":\"123!\"}]")).apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread = new SSHLinuxUntils.AsteriskThread(asteriskObjectList, IVR_GREETING_DIAL_EXT);
        thread.start();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991234abc" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991234abc", DEVICE_ASSIST_2, false);
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
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000, "0");

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("新建呼入路由Inbound2，DID Pattern选择“DID Pattern\"，添加规则：123! ，呼入目的地为IVR-IVR0\n" +
            "\t15.通过sps外线拨打9912\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "InboundRoute", "DIDPattern", "IVR", "P3"})
    public void testIRDID_15_DIDPattern() {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("新建呼入路由Inbound2，DID Pattern选择“DID Pattern\"，添加规则：123! ，呼入目的地为IVR-IVR0");
        apiUtil.deleteAllInbound().createInbound("In1", trunk9, "Extension", "1000")
                .createInbound("Inbound2", trunk1, "Extension", "1000").
                editInbound("Inbound2", String.format("\"def_dest\":\"ivr\",\"def_dest_value\":\"%s\"", apiUtil.getIVRSummary("6200").id)).
                editInbound("Inbound2", String.format("\"did_pattern_list\":[{\"did_pattern\":\"123!\"}]")).apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9912" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "9912", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("新建呼入路由Inbound3,DID Pattern选择“DID Pattern\"，添加规则： +0591XXZNZN[25-8].  呼入目的地为RingGroup-RingGroup0\n" +
            "\t16.通过sps外线拨打99+059109129921\n" +
            "\t\t呼入到RingGroup0，分机1000、1001、1003同时响铃，1003接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "InboundRoute", "DIDPattern", "SPS", "P2"})
    public void testIRDID_16_DIDPattern() {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("新建呼入路由Inbound3,DID Pattern选择“DID Pattern\"，添加规则： +0591XXZNZN[25-8].  呼入目的地为RingGroup-RingGroup0");
        apiUtil.deleteAllInbound().createInbound("Inbound3", trunk1, "Extension", "1000").
                editInbound("Inbound3", String.format("\"def_dest\":\"ring_group\",\"def_dest_value\":\"%s\"", apiUtil.getRingGroupSummary("6300").id)).
                editInbound("Inbound3", String.format("\"did_pattern_list\":[{\"did_pattern\":\"+0591XXZNZN[25-8].\"}]")).apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 99+059109129921" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "99+059109129921", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 5), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1001, RING, 5), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1003, RING, 5), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());

        pjsip.Pj_Answer_Call(1003, false);

        Assert.assertEquals(getExtensionStatus(1003, TALKING, 5), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_hangupCall(1003);


        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.RINGGROUP0_6300.toString(), STATUS.ANSWER.toString(), "RingGroup RingGroup0<6300> connected", SPS, "", "Inbound"))
                .contains(tuple("2000<2000>", CDRNAME.Extension_1003.toString(), STATUS.ANSWER.toString(), "testa D<1003> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("新建呼入路由Inbound3,DID Pattern选择“DID Pattern\"，添加规则： +0591XXZNZN[25-8].  呼入目的地为RingGroup-RingGroup0\n" +
            "\t17.通过sps外线拨打99+059118237877\n" +
            "\t\t呼入到RingGroup0，分机1000、1001、1003同时响铃，1003接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "RingGroup", "P3"})
    public void testIRDID_17_DIDPattern() {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("新建呼入路由Inbound3,DID Pattern选择“DID Pattern\"，添加规则： +0591XXZNZN[25-8].  呼入目的地为RingGroup-RingGroup0");
        apiUtil.deleteAllInbound().createInbound("Inbound3", trunk1, "Extension", "1000").
                editInbound("Inbound3", String.format("\"def_dest\":\"ring_group\",\"def_dest_value\":\"%s\"", apiUtil.getRingGroupSummary("6300").id)).
                editInbound("Inbound3", String.format("\"did_pattern_list\":[{\"did_pattern\":\"+0591XXZNZN[25-8].\"}]")).apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 99+059118237877" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "99+059118237877", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 5), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1001, RING, 5), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1003, RING, 5), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());

        pjsip.Pj_Answer_Call(1003, false);

        Assert.assertEquals(getExtensionStatus(1003, TALKING, 5), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_hangupCall(1003);


        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.RINGGROUP0_6300.toString(), STATUS.ANSWER.toString(), "RingGroup RingGroup0<6300> connected", SPS, "", "Inbound"))
                .contains(tuple("2000<2000>", CDRNAME.Extension_1003.toString(), STATUS.ANSWER.toString(), "testa D<1003> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("新建呼入路由Inbound3,DID Pattern选择“DID Pattern\"，添加规则： +0591XXZNZN[25-8].  呼入目的地为RingGroup-RingGroup0\n" +
            "18.通过sps外线拨打99+059209129921\n" +
            "\t分机1000响铃，接听，挂断；检查cdr\n" +
            "19.通过sps外线拨打99+059100029955\n" +
            "\t分机1000响铃，接听，挂断；检查cdr\n" +
            "20.通过sps外线拨打99+059100219988\n" +
            "\t分机1000响铃，接听，挂断；检查cdr\n" +
            "21.通过sps外线拨打99+059109129999\n" +
            "\t分机1000响铃，接听，挂断；检查cdr\n" +
            "22.通过sps外线拨打99+05910912992\n" +
            "\t分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "InboundRoute", "DIDPattern", "RingGroup", "P3"}, dataProvider = "routes")
    public void testIRDID_18_22_DIDPattern(String routePrefix, int caller, String callee, String deviceAssist, String cdrCaller, String trunk) {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("新建呼入路由Inbound3,DID Pattern选择“DID Pattern\"，添加规则： +0591XXZNZN[25-8].  呼入目的地为RingGroup-RingGroup0");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                createInbound("Inbound3", trunk1, "Extension", "1000").
                editInbound("Inbound3", String.format("\"def_dest\":\"ring_group\",\"def_dest_value\":\"%s\"", apiUtil.getRingGroupSummary("6300").id)).
                editInbound("Inbound3", String.format("\"did_pattern_list\":[{\"did_pattern\":\"+0591XXZNZN[25-8].\"}]")).apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, deviceAssist, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 5), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 5), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);

        step("[1000挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "test A<1000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("新建呼入路由Inbound4，DID Pattern选择“DID Pattern\"，添加规则： 13001，呼入目的地HangUp\n" +
            "\t23.通过sps外线拨打9913001\n" +
            "\t\t呼入失败，通话被直接挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "InboundRoute", "DIDPattern", "HangUp", "P3"})
    public void testIRDID_23_DIDPattern() {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("新建呼入路由Inbound4，DID Pattern选择“DID Pattern\"，添加规则： 13001，呼入目的地HangUp\n");
        apiUtil.deleteAllInbound().createInbound("Inbound4", trunk1, "Extension", "1000").
                editInbound("Inbound4", String.format("\"def_dest\":\"end_call\",\"def_dest_value\":\"\"")).
                editInbound("Inbound4", String.format("\"did_pattern_list\":[{\"did_pattern\":\"13001\"}]"))
                .apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "9913001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        int result = getExtensionStatus(2000, HUNGUP, 30);
        Assert.assertTrue((result == HUNGUP) || (result == IDLE));
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("编辑呼入路由Inbound4，DID Pattern选择“DID Pattern\"，添加规则： 13001，呼入目的地Extension Voicemail-分机1001\n" +
            "\t24.通过sps外线拨打9913001\n" +
            "\t\t进入到分机1001的语音留言，通话15s后挂断；登录1001查看新增一条语音留言；Name正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "Voicemail", "P3"})
    public void testIRDID_24_DIDPattern() {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("编辑呼入路由Inbound4，DID Pattern选择“DID Pattern\"，添加规则： 13001，呼入目的地Extension Voicemail-分机1001\n");
        apiUtil.deleteAllInbound().createInbound("Inbound4", trunk1, "Extension", "1000").
                editInbound("Inbound4", String.format("\"def_dest\":\"ext_vm\",\"def_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1001").id)).
                editInbound("Inbound4", String.format("\"did_pattern_list\":[{\"did_pattern\":\"13001\"}]"))
                .apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(2000, "9913001", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        sleep(WaitUntils.SHORT_WAIT * 5);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        step("登录分机1000查看新增一条语音留言，Name记录正确");
        auto.homePage().logout();
        auto.loginPage().login("1001", EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT * 2);
        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        sleep(1000);
        softAssertPlus.assertThat(TableUtils.getTableForHeader(getDriver(), "Name", 0)).contains("2000");

        String voiceMailTime = TableUtils.getTableForHeader(getDriver(), "Time", 0);
        log.debug("[callTime] " + callTime + " ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        assertStep("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", "Voicemail test2 B<1001>", "VOICEMAIL", "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("编辑呼入路由Inbound4，DID Pattern选择“DID Pattern\"，添加规则： 13001，呼入目的地Conference-Conference0\n" +
            "\t25.通过sps外线拨打9913001\n" +
            "\t\t进入到Conference0，通话10s,挂断；检查cdr;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "Conference", "P3"})
    public void testIRDID_25_DIDPattern() {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("新建呼入路由Inbound4，DID Pattern选择“DID Pattern\"，添加规则： 13001，呼入目的地HangUp\n");
        apiUtil.deleteAllInbound().createInbound("Inbound4", trunk1, "Extension", "1000").
                editInbound("Inbound4", String.format("\"def_dest\":\"ext_vm\",\"def_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1001").id)).
                editInbound("Inbound4", String.format("\"did_pattern_list\":[{\"did_pattern\":\"13001\"}]"))
                .apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("编辑呼入路由Inbound4，DID Pattern选择“DID Pattern\"，添加规则： 13001，呼入目的地Queue-Queue0\n" +
            "\t26.通过sps外线拨打通过sps外线拨打9913001\n" +
            "\t\t进入到Queue0，坐席1000、1001、1003、1004同时响铃，分机1004接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "Queue", "P3"})
    public void testIRDID_26_DIDPattern() {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("编辑呼入路由Inbound4，DID Pattern选择“DID Pattern\"，添加规则： 13001，呼入目的地Queue-Queue0\n");
        apiUtil.deleteAllInbound().createInbound("Inbound4", trunk1, "Extension", "1000").
                editInbound("Inbound4", String.format("\"def_dest\":\"queue\",\"def_dest_value\":\"%s\"", apiUtil.getQueueSummary("6400").id)).
                editInbound("Inbound4", String.format("\"did_pattern_list\":[{\"did_pattern\":\"13001\"}]")).apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 通过sps外线拨打9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "9913001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 5), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1001, RING, 5), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1003, RING, 5), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1004, RING, 5), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());

        pjsip.Pj_Answer_Call(1004, false);

        Assert.assertEquals(getExtensionStatus(1004, TALKING, 5), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_hangupCall(1004);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.QUEUE0_6400.toString(), STATUS.ANSWER.toString(), "Queue Queue0<6400> connected", SPS, "", "Inbound"))
                .contains(tuple("2000<2000>", CDRNAME.Extension_1004.toString(), STATUS.ANSWER.toString(), "t estX<1004> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("编辑呼入路由Inbound4，DID Pattern选择“DID Pattern\"，添加规则： 13001，呼入目的地External Number：prefix : 1 ,号码：3001\n" +
            "\t27.通过sps外线拨打9913001\n" +
            "\t\t拨打外部号码13001，辅助1的分机3001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "ExternalNumber", "P3"})
    public void testIRDID_27_DIDPattern() {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("编辑呼入路由Inbound4，DID Pattern选择“DID Pattern\"，添加规则： 13001，呼入目的地External Number：prefix : 1 ,号码：3001");
        apiUtil.deleteAllInbound().createInbound("Inbound4", trunk1, "Extension", "1000").
                editInbound("Inbound4", String.format("\"def_dest\":\"external_num\",\"def_dest_prefix\":\"1\",\"def_dest_value\":\"3001\"")).
                editInbound("Inbound4", String.format("\"did_pattern_list\":[{\"did_pattern\":\"13001\"}]"))
                .apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "9913001", DEVICE_ASSIST_2, false);

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
                .contains(tuple("2000<2000>", "13001", STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("编辑呼入路由Inbound4，DID Pattern选择“DID Pattern\"，添加规则： 13001，呼入目的地Out1\n" +
            "\t28.通过sps外线拨打9913001\n" +
            "\t\t转到呼出路由Out1，辅助1的分机3001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "OutboundRoute", "P3"})
    public void testIRDID_28_DIDPattern() {
        prerequisite();
        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("编辑呼入路由Inbound4，DID Pattern选择“DID Pattern\"，添加规则： 13001，呼入目的地External Number：prefix : 1 ,号码：3001");
        apiUtil.deleteAllInbound().createInbound("Inbound4", trunk1, "Extension", "1000").
                editInbound("Inbound4", String.format("\"def_dest\":\"outroute\",\"def_dest_value\":\"%s\"", apiUtil.getOutBoundRouteSummary("Out1").id)).
                editInbound("Inbound4", String.format("\"did_pattern_list\":[{\"did_pattern\":\"13001\"}]"))
                .apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "9913001", DEVICE_ASSIST_2, false);

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
                .contains(tuple("2000<2000>", "13001", STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("InboundRoute,DIDPattern")
    @Description("编辑呼入路由Inbound4，DID Pattern选择“DID Pattern\"，添加规则： 13001，呼入目的地Play Greeting then HangUp 选择prompt1，播放1遍\n" +
            "\t29.通过sps外线拨打9913001\n" +
            "\t\tasterisk后台查看播放提示音文件prompt1,挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "PlayGreetingthenHangUp", "P3"})
    public void testIRDID_29_DIDPattern() {
        prerequisite();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread = new SSHLinuxUntils.AsteriskThread(asteriskObjectList, PROMPT_1);
        thread.start();

        List<String> trunk1 = new ArrayList<>();
        trunk1.add(SPS);
        step("编辑呼入路由Inbound4，DID Pattern选择“DID Pattern\"，添加规则： 13001，呼入目的地Play Greeting then HangUp 选择prompt1，播放1遍");
        apiUtil.deleteAllInbound().createInbound("Inbound4", trunk1, "Extension", "1000").
                editInbound("Inbound4", String.format("\"def_dest\":\"play_greeting\",\"def_dest_prefix\":\"1\",\"def_dest_value\":\"prompt1.wav\"")).
                editInbound("Inbound4", String.format("\"did_pattern_list\":[{\"did_pattern\":\"13001\"}]"))
                .apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);

        pjsip.Pj_Make_Call_No_Answer(2000, "9913001", DEVICE_ASSIST_2, false);
        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 301) {
            log.debug("【asteriskObjectList.size()】"+asteriskObjectList.size());
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }

        thread.flag = false;
        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", "play_file", STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Match Selected Extensions-Default_All_Extensions\n" +
            "\t30.通过sps外线拨打991001\n" +
            "\t\t分机1001响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDPatterntoExtensions", "MatchSelectedExtensions", "P2"})
    public void testIRDID_30_MatchDIDToExt() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Match Selected Extensions-Default_All_Extensions\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"{{.Ext}}\",\"def_dest\":\"pattern_to_ext\",\"def_dest_ext_list\":[{\"value\":\"%s\"}]", apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1001, RING, 30), RING);
        pjsip.Pj_Answer_Call(1001, false);
        Assert.assertEquals(getExtensionStatus(1001, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Match Selected Extensions-Default_All_Extensions\n" +
            "\t31.通过sps外线拨打991002\n" +
            "\t\t分机1002响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDPatterntoExtensions", "MatchSelectedExtensions", "P2"})
    public void testIRDID_31_MatchDIDToExt() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Match Selected Extensions-Default_All_Extensions\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"{{.Ext}}\",\"def_dest\":\"pattern_to_ext\",\"def_dest_ext_list\":[{\"value\":\"%s\"}]", apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991002" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991002", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1002, RING, 30), RING);
        pjsip.Pj_Answer_Call(1002, false);
        Assert.assertEquals(getExtensionStatus(1002, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1002.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Match Selected Extensions-Default_All_Extensions\n" +
            "\t32.通过sps外线拨打991020\n" +
            "\t\t辅助2的2000分机响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","FXS", "InboundRoute","DIDPattern", "MatchDIDPatterntoExtensions", "MatchSelectedExtensions", "P3"})
    public void testIRDID_32_MatchDIDToExt() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Match Selected Extensions-Default_All_Extensions\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"{{.Ext}}\",\"def_dest\":\"pattern_to_ext\",\"def_dest_ext_list\":[{\"value\":\"%s\"}]", apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2001" + ",[callee] 991020" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2001, "991020", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(2000, RING, 45), RING);
        pjsip.Pj_Answer_Call(2000, false);
        Assert.assertEquals(getExtensionStatus(2000, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1020.toString(), STATUS.ANSWER.toString(), "1020 1020<1020> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Match Selected Extensions-Default_All_Extensions\n" +
            "\t33.通过BRI外线拨打881003\n" +
            "\t\t分机1003响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "BRI", "InboundRoute","DIDPattern", "MatchDIDPatterntoExtensions","MatchSelectedExtensions", "P3"})
    public void testIRDID_33_MatchDIDToExt() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"BRI线路未配置！");
        }
        prerequisite();
        step("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Match Selected Extensions-Default_All_Extensions\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"{{.Ext}}\",\"def_dest\":\"pattern_to_ext\",\"def_dest_ext_list\":[{\"value\":\"%s\"}]", apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 881003" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "881003", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1003, RING, 30), RING);
        pjsip.Pj_Answer_Call(1003, false);
        Assert.assertEquals(getExtensionStatus(1003, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1003.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", BRI_1, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Match Selected Extensions-Default_All_Extensions\n" +
            "\t34.通过E1外线拨打661004\n" +
            "\t\t分机1004响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "E1", "InboundRoute","DIDPattern", "MatchDIDPatterntoExtensions","MatchSelectedExtensions", "P3"})
    public void testIRDID_34_MatchDIDToExt() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"E1线路未配置！");
        }
        prerequisite();
        step("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Match Selected Extensions-Default_All_Extensions\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"{{.Ext}}\",\"def_dest\":\"pattern_to_ext\",\"def_dest_ext_list\":[{\"value\":\"%s\"}]", apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).
                apply();

        step("1:login with admin,trunk: " + E1);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 661004" + ",[trunk] " + E1);
        pjsip.Pj_Make_Call_No_Answer(2000, "661004", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1004, RING, 30), RING);
        pjsip.Pj_Answer_Call(1004, false);
        Assert.assertEquals(getExtensionStatus(1004, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1004.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", E1, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Match Selected Extensions-Default_All_Extensions\n" +
            "\t35.通过Account外线拨打441000\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDPatterntoExtensions", "SIP_ACCOUNT","MatchSelectedExtensions", "P3"})
    public void testIRDID_35_MatchDIDToExt() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Match Selected Extensions-Default_All_Extensions\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"{{.Ext}}\",\"def_dest\":\"pattern_to_ext\",\"def_dest_ext_list\":[{\"value\":\"%s\"}]", apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 4000" + ",[callee] 441000" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(4000, "441000", DEVICE_ASSIST_3, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(4000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("4000<4000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "4000<4000> hung up", ACCOUNTTRUNK, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：123{{.Ext}}0591XZN，选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1001,1003" +
            "\t36.通过sps外线拨打99+12310010591012\n" +
            "\t\t分机1001响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDPatterntoExtensions","MatchSelectedExtensions", "P3"})
    public void testIRDID_36_MatchDIDToExt() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\\\" ,值为：123{{.Ext}}0591XZN，选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1001,1003\\n\"");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"123{{.Ext}}0591XZN\",\"def_dest\":\"pattern_to_ext\",\"def_dest_ext_list\":[{\"value\":\"%s\"},{\"value\":\"%s\"}]", apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1003").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9912310010591012" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "9912310010591012", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1001, RING, 30), RING);
        pjsip.Pj_Answer_Call(1001, false);
        Assert.assertEquals(getExtensionStatus(1001, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：123{{.Ext}}0591XZN，选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1001,1003" +
            "\t37.通过sps外线拨打9912310030591999\n" +
            "\t\t分机1003响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDPatterntoExtensions","MatchSelectedExtensions", "P3"})
    public void testIRDID_37_MatchDIDToExt() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：123{{.Ext}}0591XZN，选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1001,1003");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"123{{.Ext}}0591XZN\",\"def_dest\":\"pattern_to_ext\",\"def_dest_ext_list\":[{\"value\":\"%s\"},{\"value\":\"%s\"}]", apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1003").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9912310030591999" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "9912310030591999", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1003, RING, 30), RING);
        pjsip.Pj_Answer_Call(1003, false);
        Assert.assertEquals(getExtensionStatus(1003, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1003.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：123{{.Ext}}0591XZN，选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1001,1003" +
            "\t38.通过sps外线拨打9912310020591012\n" +
            "\t\t打不通，通话被自动挂断\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDPatterntoExtensions","MatchSelectedExtensions", "P3"})
    public void testIRDID_38_MatchDIDToExt() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\\\" ,值为：123{{.Ext}}0591XZN，选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1001,1003\\n\"");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"123{{.Ext}}0591XZN\",\"def_dest\":\"pattern_to_ext\",\"def_dest_ext_list\":[{\"value\":\"%s\"},{\"value\":\"%s\"}]", apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1003").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9912310020591012" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "9912310020591012", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        int result = getExtensionStatus(2000, HUNGUP, 30);
        Assert.assertTrue((result == HUNGUP) || (result == IDLE));
    }


    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：123{{.Ext}}0591XZN，选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1001,1003" +
            "\t39.通过sps外线拨打9912310010592012\n" +
            "\t\t打不通，通话被自动挂断\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDPatterntoExtensions","MatchSelectedExtensions", "P3"})
    public void testIRDID_39_MatchDIDToExt() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\\\" ,值为：123{{.Ext}}0591XZN，选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1001,1003\\n\"");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"123{{.Ext}}0591XZN\",\"def_dest\":\"pattern_to_ext\",\"def_dest_ext_list\":[{\"value\":\"%s\"},{\"value\":\"%s\"}]", apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1003").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9912310010592012" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "9912310010592012", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        int result = getExtensionStatus(2000, HUNGUP, 30);
        Assert.assertTrue((result == HUNGUP) || (result == IDLE));
    }


    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：123{{.Ext}}0591XZN，选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1001,1003" +
            "\t40.通过sps外线拨打9912310010591002\n" +
            "\t\t打不通，通话被自动挂断\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDPatterntoExtensions", "MatchSelectedExtensions","P3"})
    public void testIRDID_40_MatchDIDToExt() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\\\" ,值为：123{{.Ext}}0591XZN，选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1001,1003\\n\"");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"123{{.Ext}}0591XZN\",\"def_dest\":\"pattern_to_ext\",\"def_dest_ext_list\":[{\"value\":\"%s\"},{\"value\":\"%s\"}]", apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1003").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9912310010591002" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "9912310010591002", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        int result = getExtensionStatus(2000, HUNGUP, 30);
        Assert.assertTrue((result == HUNGUP) || (result == IDLE));
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：123{{.Ext}}0591XZN，选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1001,1003" +
            "\t41.通过sps外线拨打9912310010591011\n" +
            "\t\t打不通，通话被自动挂断\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDPatterntoExtensions","MatchSelectedExtensions", "P3"})
    public void testIRDID_41_MatchDIDToExt() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\\\" ,值为：123{{.Ext}}0591XZN，选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1001,1003\\n\"");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"123{{.Ext}}0591XZN\",\"def_dest\":\"pattern_to_ext\",\"def_dest_ext_list\":[{\"value\":\"%s\"},{\"value\":\"%s\"}]", apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1003").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9912310010591011" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "9912310010591011", DEVICE_ASSIST_2, false);
        step("[通话状态校验]");
        int result = getExtensionStatus(2000, HUNGUP, 30);
        Assert.assertTrue((result == HUNGUP) || (result == IDLE));
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：+1230591XZN，选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1001,1003\n" +
            "\t42-1.通过sps外线拨打99+1230591012\n" +
            "\t\t打不通，通话被自动挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDPatterntoExtensions","MatchSelectedExtensions", "P3"})
    public void testIRDID_42_MatchDIDToExt() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\\\" ,值为：+123{{.Ext}}0591XZN！ ，选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1001,1003\\n\"");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"+1230591XZN\",\"def_dest\":\"pattern_to_ext\",\"def_dest_ext_list\":[{\"value\":\"%s\"},{\"value\":\"%s\"}]", apiUtil.getExtensionSummary("1001").id, apiUtil.getExtensionSummary("1003").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 99+1230591012" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "99+1230591012", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        int result = getExtensionStatus(2000, HUNGUP, 30);
        Assert.assertTrue((result == HUNGUP) || (result == IDLE));

    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：+1230591XZN，选择所有外线；呼入目的地选择Extension-分机1001\n" +
            "\t43.通过sps外线拨打99+1230591012\n" +
            "\t分机1001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDPatterntoExtensions","MatchSelectedExtensions", "P3"})
    public void testIRDID_43_MatchDIDToExt() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：+1230591XZN，选择所有外线；呼入目的地选择Extension-分机1001\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1001").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"+1230591XZN\",\"def_dest\":\"extension\",\"def_dest_ext_list\":[{\"value\":\"%s\"}]", apiUtil.getExtensionSummary("1001").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 99+1230591012" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "99+1230591012", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1001, RING, 30), RING);
        pjsip.Pj_Answer_Call(1001, false);
        Assert.assertEquals(getExtensionStatus(1001, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1002,1004\n" +
            "\t43.通过sps外线拨打991002a\n" +
            "\t\t分机1002响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDPatterntoExtensions","MatchSelectedExtensions", "P3"})
    public void testIRDID_44_MatchDIDToExt() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}.选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1002,1004");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"{{.Ext}}\",\"def_dest\":\"pattern_to_ext\",\"def_dest_ext_list\":[{\"value\":\"%s\"},{\"value\":\"%s\"}]", apiUtil.getExtensionSummary("1002").id, apiUtil.getExtensionSummary("1004").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991002a" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991002a", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        int result = getExtensionStatus(2000, HUNGUP, 30);
        Assert.assertTrue((result == HUNGUP) || (result == IDLE));
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1002,1004\n" +
            "\t45.通过sps外线拨打991002\n" +
            "\t分机1002响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDPatterntoExtensions", "MatchSelectedExtensions", "P3"})
    public void testIRDID_45_MatchDIDToExt() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}.选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1002,1004");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"{{.Ext}}\",\"def_dest\":\"pattern_to_ext\",\"def_dest_ext_list\":[{\"value\":\"%s\"},{\"value\":\"%s\"}]", apiUtil.getExtensionSummary("1002").id, apiUtil.getExtensionSummary("1004").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991002" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991002", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1002, RING, 30), RING);
        pjsip.Pj_Answer_Call(1002, false);
        Assert.assertEquals(getExtensionStatus(1002, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1002.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1002,1004\n" +
            "\t46.通过sps外线拨打991003\n" +
            "\t\t打不通，通话被自动挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDPatterntoExtensions","MatchSelectedExtensions", "P3"})
    public void testIRDID_46_MatchDIDToExt() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}.选择所有外线；呼入目的地选择Match Selected Extensions-选择分机1002,1004");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"{{.Ext}}\",\"def_dest\":\"pattern_to_ext\",\"def_dest_ext_list\":[{\"value\":\"%s\"},{\"value\":\"%s\"}]", apiUtil.getExtensionSummary("1002").id, apiUtil.getExtensionSummary("1004").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991003" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991003", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        int result = getExtensionStatus(2000, HUNGUP, 30);
        Assert.assertTrue((result == HUNGUP) || (result == IDLE));
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Extension-分机1000\n" +
            "\t47.通过sps外线拨打991003\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDPatterntoExtensions","Extension", "P3"})
    public void testIRDID_47_MatchDIDToExt() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Extension-分机1000\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"{{.Ext}}\",\"def_dest\":\"extension\",\"def_dest_ext_list\":[{\"value\":\"%s\"}]", apiUtil.getExtensionSummary("1000").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991003" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991003", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Extension-分机1000\n" +
            "\t48.通过sps外线拨打99999999\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDPatterntoExtensions", "Extension", "P3"})
    public void testIRDID_48_MatchDIDToExt() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Extension-分机1000\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"{{.Ext}}\",\"def_dest\":\"extension\",\"def_dest_ext_list\":[{\"value\":\"%s\"}]", apiUtil.getExtensionSummary("1000").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 99999999" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "99999999", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择RingGroup-RingGroup0\n" +
            "\t49.通过sps外线拨打991000\n" +
            "\t\t呼入到RingGroup0，分机1000、1001、1003同时响铃，1003接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDPatterntoExtensions", "RingGroup", "P3"})
    public void testIRDID_49_MatchDIDToExt() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Extension-分机1000\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"{{.Ext}}\",\"def_dest\":\"ring_group\",\"def_dest_value\":\"%s\"", apiUtil.getRingGroupSummary("6300").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        Assert.assertEquals(getExtensionStatus(1001, RING, 30), RING);
        Assert.assertEquals(getExtensionStatus(1003, RING, 30), RING);
        pjsip.Pj_Answer_Call(1003, false);
        Assert.assertEquals(getExtensionStatus(1003, TALKING, 30), TALKING);
        sleep(WaitUntils.SHORT_WAIT * 2);
        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.RINGGROUP0_6300.toString(), STATUS.ANSWER.toString(), "RingGroup RingGroup0<6300> connected", SPS, "", "Inbound"))
                .contains(tuple("2000<2000>", CDRNAME.Extension_1003.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择HangUp\n" +
            "\t50.通过sps外线拨打991000\n" +
            "\t\t呼入失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDPatterntoExtensions", "HangUp", "P3"})
    public void testIRDID_50_MatchDIDToExt() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择HangUp\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"{{.Ext}}\",\"def_dest\":\"end_call\",\"def_dest_value\":\"\"")).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        int result = getExtensionStatus(2000, HUNGUP, 30);
        Assert.assertTrue((result == HUNGUP) || (result == IDLE));
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Extension Voicemail-分机1000\n" +
            "\t51.通过sps外线拨打991001\n" +
            "\t\t进入到分机1000的语音留言，通话15s后挂断；登录1000查看新增一条语音留言；Name正确")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDPatterntoExtensions", "Voicemail", "P3"})
    public void testIRDID_51_MatchDIDToExt() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Extension-分机1000\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"{{.Ext}}\",\"def_dest\":\"ext_vm\",\"def_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1000").id)).
                apply();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991001" + ",[trunk] " + SPS);
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(2000, "991001", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 2);
        sleep(15 * 1000);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        step("登录分机1000查看新增一条语音留言，Name记录正确");
        auto.homePage().logout();
        auto.loginPage().login("1000", EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT * 2);
        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        sleep(1000);
        Assert.assertTrue(TableUtils.getTableForHeader(getDriver(), "Name", 0).contains("2000"), "没有检测到录音文件！");

        String voiceMailTime = TableUtils.getTableForHeader(getDriver(), "Time", 0);
        log.debug("[callTime] " + callTime + " ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        assertStep("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000_VOICEMAIL.toString(), STATUS.VOICEMAIL.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择IVR-IVR0\n" +
            "\t52.通过sps外线拨打991002\n" +
            "\t\t进入到IVR0，asterisk后台检测到播放提示音“ivr-greeting-dial-ext”，按0，分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDPatterntoExtensions", "IVR", "P3"})
    public void testIRDID_52_MatchDIDToExt() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择IVR-IVR0\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"{{.Ext}}\",\"def_dest\":\"ivr\",\"def_dest_value\":\"%s\"", apiUtil.getIVRSummary("6200").id)).
                apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread = new SSHLinuxUntils.AsteriskThread(asteriskObjectList, IVR_GREETING_DIAL_EXT);
        thread.start();
        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991002" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991002", DEVICE_ASSIST_2, false);
        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
            log.debug("【asteriskObjectList.size()】"+asteriskObjectList.size());
        }
        if (tmp == 301) {
            log.debug("【asteriskObjectList.size()】"+asteriskObjectList.size());
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000, "0");

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Conference-Conference0\n" +
            "\t53.通过sps外线拨打991003\n" +
            "\t\t进入到Conference0，通话10s,挂断；检查cdr;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDPatterntoExtensions", "Conference", "P3"})
    public void testIRDID_53_MatchDIDToExt() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Conference-Conference0\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"{{.Ext}}\",\"def_dest\":\"conference\",\"def_dest_value\":\"%s\"", apiUtil.getConferenceSummary("6500").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991003" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991003", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 5);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Conference0_6500.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Queue-Queue0\n" +
            "\t54.通过sps外线拨打991003\n" +
            "\t\t进入到Queue0，坐席1000、1001、1003、1004同时响铃，分机1004接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDPatterntoExtensions", "Queue", "P3"})
    public void testIRDID_54_MatchDIDToExt() throws IOException, JSchException {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Queue-Queue0\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"{{.Ext}}\",\"def_dest\":\"queue\",\"def_dest_value\":\"%s\"", apiUtil.getQueueSummary("6400").id)).
                apply();

        step("新增动态坐席：1003\\1004分别拨打*76400 加入到Queue0");
        String result = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "queue show queue-6400"));
        log.debug("[queue show queue-6400] " + result);
        if (!result.contains("1003")) {
            pjsip.Pj_Make_Call_Auto_Answer(1003, "*76400", DEVICE_IP_LAN);
        }
        if (!result.contains("1004")) {
            pjsip.Pj_Make_Call_Auto_Answer(1004, "*76400", DEVICE_IP_LAN);
        }
        String resultAfter = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "queue show queue-6400"));
        log.debug("[queue show queue-6400 resultAfter] " + resultAfter);

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991003" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991003", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1001, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1003, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1004, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());

        pjsip.Pj_Answer_Call(1004, false);

        Assert.assertEquals(getExtensionStatus(1004, TALKING, 5), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_hangupCall(1004);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.QUEUE0_6400.toString(), STATUS.ANSWER.toString(), "Queue Queue0<6400> connected", SPS, "", "Inbound"))
                .contains(tuple("2000<2000>", CDRNAME.Extension_1004.toString(), STATUS.ANSWER.toString(), "t estX<1004> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择External Number：prefix : 1 ,号码：3001\n" +
            "\t55.通过sps外线拨打991004\n" +
            "\t\t拨打外部号码13001，辅助1的分机3001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDPatterntoExtensions", "ExternalNumber", "P3"})
    public void testIRDID_55_MatchDIDToExt() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择External Number：prefix : 1 ,号码：3001");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"range_to_ext\",\"did_to_ext_start\":\"5503300\",\"did_to_ext_end\":\"5503304\",\"def_dest\":\"external_num\",\"def_dest_prefix\":\"1\",\"def_dest_value\":\"3001\"")).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Out1\n" +
            "\t56.通过sps外线拨打9913001\n" +
            "\t\t转到呼出路由Out1，辅助1的分机3001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDPatterntoExtensions", "OutboundRoute", "P3"})
    public void testIRDID_56_MatchDIDToExt() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择Extension-分机1000\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"{{.Ext}}\",\"def_dest\":\"outroute\",\"def_dest_value\":\"%s\"", apiUtil.getOutBoundRouteSummary("Out1").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
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
                .contains(tuple("2000<2000>", "13001", STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDPatterntoExtensions")
    @Description("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；Play Greeting then HangUp 选择prompt2，播放1遍\n" +
            "\t57.通过sps外线拨打9913001\n" +
            "\t\tasterisk后台查看播放提示音文件prompt2,挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDPatterntoExtensions", "PlayGreetingthenHangUp", "P3"})
    public void testIRDID_57_MatchDIDToExt() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern 选择“Match DID Pattern to Extensions\" ,值为：{{.Ext}}，选择所有外线；呼入目的地选择IVR-IVR0\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"pattern_to_ext\",\"did_pattern_to_ext\":\"{{.Ext}}\",\"def_dest\":\"play_greeting\",\"def_dest_prefix\":\"1\",\"def_dest_value\":\"prompt2.wav\"")).
                apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread = new SSHLinuxUntils.AsteriskThread(asteriskObjectList, PROMPT_2);
        thread.start();
        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "9913001", DEVICE_ASSIST_2, false);
        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
            log.debug("【asteriskObjectList.size()】"+asteriskObjectList.size());

        }
        if (tmp == 301) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", "play_file", STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Match Extension Range：1000-1004\n" +
            "\t58.通过sps外线拨打995503300\n" +
            "\t\t分机1000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDRangetoExtensionRange", "MatchExtensionRange", "P2"})
    public void testIRDID_58_MatchDIDToExtRange() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Match Extension Range：1000-1004\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", "\"did_option\":\"range_to_ext\",\"did_to_ext_start\":\"5503300\",\"did_to_ext_end\":\"5503304\",\"def_dest\":\"range_to_ext\",\"def_dest_value\":\"1000-1004\"").
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 995503300" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "995503300", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Match Extension Range：1000-1004\n" +
            "\t59.通过sps外线拨打995503304\n" +
            "\t\t分机1004响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDRangetoExtensionRange", "MatchExtensionRange", "P2"})
    public void testIRDID_59_MatchDIDToExtRange() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Match Extension Range：1000-1004\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", "\"did_option\":\"range_to_ext\",\"did_to_ext_start\":\"5503300\",\"did_to_ext_end\":\"5503304\",\"def_dest\":\"range_to_ext\",\"def_dest_value\":\"1000-1004\"").
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 995503304" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "995503304", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1004, RING, 30), RING);
        pjsip.Pj_Answer_Call(1004, false);
        Assert.assertEquals(getExtensionStatus(1004, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1004.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Match Extension Range：1000-1004\n" +
            "\t60.通过bri外线拨打885503301\n" +
            "\t\t分机1001响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDRangetoExtensionRange", "MatchExtensionRange", "P3"})
    public void testIRDID_60_MatchDIDToExtRange() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Match Extension Range：1000-1004\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", "\"did_option\":\"range_to_ext\",\"did_to_ext_start\":\"5503300\",\"did_to_ext_end\":\"5503304\",\"def_dest\":\"range_to_ext\",\"def_dest_value\":\"1000-1004\"").
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 885503301" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "885503301", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1001, RING, 30), RING);
        pjsip.Pj_Answer_Call(1001, false);
        Assert.assertEquals(getExtensionStatus(1001, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1001.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", BRI_1, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Match Extension Range：1000-1004\n" +
            "\t61.通过E1外线拨打665503302\n" +
            "\t\t分机1002响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDRangetoExtensionRange", "MatchExtensionRange", "P3"})
    public void testIRDID_61_MatchDIDToExtRange() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Match Extension Range：1000-1004\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", "\"did_option\":\"range_to_ext\",\"did_to_ext_start\":\"5503300\",\"did_to_ext_end\":\"5503304\",\"def_dest\":\"range_to_ext\",\"def_dest_value\":\"1000-1004\"").
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 665503302" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "665503302", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1002, RING, 30), RING);
        pjsip.Pj_Answer_Call(1002, false);
        Assert.assertEquals(getExtensionStatus(1002, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1002.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", E1, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Match Extension Range：1000-1004\n" +
            "\t62.通过sps外线拨打995503305\n" +
            "\t\t呼入失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDRangetoExtensionRange", "MatchExtensionRange", "P3"})
    public void testIRDID_62_MatchDIDToExtRange() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Match Extension Range：1000-1004\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", "\"did_option\":\"range_to_ext\",\"did_to_ext_start\":\"5503300\",\"did_to_ext_end\":\"5503304\",\"def_dest\":\"range_to_ext\",\"def_dest_value\":\"1000-1004\"").
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 995503305" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "995503305", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        int result = getExtensionStatus(2000, HUNGUP, 30);
        Assert.assertTrue((result == HUNGUP) || (result == IDLE));
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Hang up\n" +
            "\t63.通过sps外线拨打995503300\n" +
            "\t\t呼入失败，通话被挂断")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDRangetoExtensionRange", "HangUp", "P3"})
    public void testIRDID_63_MatchDIDToExtRange() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Hang up\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", "\"did_option\":\"range_to_ext\",\"did_to_ext_start\":\"5503300\",\"did_to_ext_end\":\"5503304\",\"def_dest\":\"end_call\",\"def_dest_value\":\"\"").
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 995503300" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "995503300", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        int result = getExtensionStatus(2000, HUNGUP, 30);
        Assert.assertTrue((result == HUNGUP) || (result == IDLE));
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Extension-分机1002\n" +
            "\t64.通过sps外线拨打995503300\n" +
            "\t\t分机1002响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDRangetoExtensionRange", "Extension", "P3"})
    public void testIRDID_64_MatchDIDToExtRange() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Extension-分机1002\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"range_to_ext\",\"did_to_ext_start\":\"5503300\",\"did_to_ext_end\":\"5503304\",\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1002").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 995503300" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "995503300", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1002, RING, 30), RING);
        pjsip.Pj_Answer_Call(1002, false);
        Assert.assertEquals(getExtensionStatus(1002, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1002.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Extension Voicemail-分机1000\n" +
            "\t65.通过sps外线拨打995503300\n" +
            "\t\t进入到分机1000的语音留言，通话15s，挂断；分机1000登录查看新增一条语音留言；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDRangetoExtensionRange", "Voicemail", "P3"})
    public void testIRDID_65_MatchDIDToExtRange() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Extension Voicemail-分机1000\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"range_to_ext\",\"did_to_ext_start\":\"5503300\",\"did_to_ext_end\":\"5503304\",\"def_dest\":\"ext_vm\",\"def_dest_value\":\"%s\"", apiUtil.getExtensionSummary("1000").id)).
                apply();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 995503300" + ",[trunk] " + SPS);
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(2000, "995503300", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 2);
        sleep(15 * 1000);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        step("登录分机1000查看新增一条语音留言，Name记录正确");
        auto.homePage().logout();
        auto.loginPage().login("1000", EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT * 2);
        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        sleep(1000);
        Assert.assertTrue(TableUtils.getTableForHeader(getDriver(), "Name", 0).contains("2000"), "没有检测到录音文件！");

        String voiceMailTime = TableUtils.getTableForHeader(getDriver(), "Time", 0);
        log.debug("[callTime] " + callTime + " ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        assertStep("[CDR校验]");
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000_VOICEMAIL.toString(), STATUS.VOICEMAIL.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择IVR-IVR0\n" +
            "\t66.通过sps外线拨打995503300\n" +
            "\t\t进入到IVR0，asterisk后台检测到播放提示音“ivr-greeting-dial-ext”，按0，分机1000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDRangetoExtensionRange", "IVR", "P3"})
    public void testIRDID_66_MatchDIDToExtRange() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Extension Voicemail-分机1000\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"range_to_ext\",\"did_to_ext_start\":\"5503300\",\"did_to_ext_end\":\"5503304\",\"def_dest\":\"ivr\",\"def_dest_value\":\"%s\"", apiUtil.getIVRSummary("6200").id)).
                apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread = new SSHLinuxUntils.AsteriskThread(asteriskObjectList, IVR_GREETING_DIAL_EXT);
        thread.start();
        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 991000" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "991000", DEVICE_ASSIST_2, false);
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
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        pjsip.Pj_Send_Dtmf(2000, "0");

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择RingGroup-RingGroup0\n" +
            "\t67.通过sps外线拨打995503300\n" +
            "\t\t呼入到RingGroup0，分机1000、1001、1003同时响铃，1003接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDRangetoExtensionRange", "RingGroup", "P3"})
    public void testIRDID_67_MatchDIDToExtRange() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择RingGroup-RingGroup0\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"range_to_ext\",\"did_to_ext_start\":\"5503300\",\"did_to_ext_end\":\"5503304\",\"def_dest\":\"ring_group\",\"def_dest_value\":\"%s\"", apiUtil.getRingGroupSummary("6300").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 995503300" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "995503300", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        Assert.assertEquals(getExtensionStatus(1001, RING, 30), RING);
        Assert.assertEquals(getExtensionStatus(1003, RING, 30), RING);
        pjsip.Pj_Answer_Call(1003, false);
        Assert.assertEquals(getExtensionStatus(1003, TALKING, 30), TALKING);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.RINGGROUP0_6300.toString(), STATUS.ANSWER.toString(), "RingGroup RingGroup0<6300> connected", SPS, "", "Inbound"))
                .contains(tuple("2000<2000>", CDRNAME.Extension_1003.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Queue-Queue0\n" +
            "\t68.通过sps外线拨打995503300\n" +
            "\t\t呼入到Queue0，坐席1000、1001、1003、1004同时响铃，1004接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDRangetoExtensionRange", "Queue", "P3"})
    public void testIRDID_68_MatchDIDToExtRange() throws IOException, JSchException {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择RingGroup-RingGroup0\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"range_to_ext\",\"did_to_ext_start\":\"5503300\",\"did_to_ext_end\":\"5503304\",\"def_dest\":\"queue\",\"def_dest_value\":\"%s\"", apiUtil.getQueueSummary("6400").id)).
                apply();

        step("新增动态坐席：1003\\1004分别拨打*76400 加入到Queue0");
        String result = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "queue show queue-6400"));
        log.debug("[queue show queue-6400] " + result);
        if (!result.contains("1003")) {
            pjsip.Pj_Make_Call_Auto_Answer(1003, "*76400", DEVICE_IP_LAN);
        }
        if (!result.contains("1004")) {
            pjsip.Pj_Make_Call_Auto_Answer(1004, "*76400", DEVICE_IP_LAN);
        }
        String resultAfter = SSHLinuxUntils.exePjsip(DEVICE_IP_LAN, PJSIP_TCP_PORT, PJSIP_SSH_USER, PJSIP_SSH_PASSWORD, String.format(ASTERISK_CLI, "queue show queue-6400"));
        log.debug("[queue show queue-6400 resultAfter] " + resultAfter);

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 995503300" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "995503300", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1001, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1003, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        Assert.assertEquals(getExtensionStatus(1004, RING, 30), RING, "[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());

        pjsip.Pj_Answer_Call(1004, false);

        Assert.assertEquals(getExtensionStatus(1004, TALKING, 5), TALKING, "[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());
        sleep(WaitUntils.SHORT_WAIT);
        pjsip.Pj_hangupCall(1004);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(2);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.QUEUE0_6400.toString(), STATUS.ANSWER.toString(), "Queue Queue0<6400> connected", SPS, "", "Inbound"))
                .contains(tuple("2000<2000>", CDRNAME.Extension_1004.toString(), STATUS.ANSWER.toString(), "t estX<1004> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Conference-Conference0\n" +
            "\t69.通过sps外线拨打995503300\n" +
            "\t\t进入到Conference0，通话10s,挂断；检查cdr;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDRangetoExtensionRange", "Conference", "P3"})
    public void testIRDID_69_MatchDIDToExtRange() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择RingGroup-RingGroup0\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"range_to_ext\",\"did_to_ext_start\":\"5503300\",\"did_to_ext_end\":\"5503304\",\"def_dest\":\"conference\",\"def_dest_value\":\"%s\"", apiUtil.getConferenceSummary("6500").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 995503300" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "995503300", DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 5);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Conference0_6500.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Internal"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择External Number ,prefix : 1 ,号码：3001\n" +
            "\t70.通过sps外线拨打995503300\n" +
            "\t\t拨打外部号码13001，辅助1的分机3001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDRangetoExtensionRange", "ExternalNumber", "P3"})
    public void testIRDID_70_MatchDIDToExtRange() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择External Number ,prefix : 1 ,号码：3001\n\n");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"range_to_ext\",\"did_to_ext_start\":\"5503300\",\"did_to_ext_end\":\"5503304\",\"def_dest\":\"external_num\",\"def_dest_prefix\":\"1\",\"def_dest_value\":\"3001\"")).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 995503300" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "995503300", DEVICE_ASSIST_2, false);

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
                .contains(tuple("2000<2000>", "13001", STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 13001-13004，呼入目的地选择Outbound Route-Out1\n" +
            "\t71.通过sps外线拨打995503300\n" +
            "\t\t转到呼出路由Out1，辅助1的分机3001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDRangetoExtensionRange", "OutboundRoute", "P3"})
    public void testIRDID_71_MatchDIDToExtRange() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Outbound Route-Out1");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"range_to_ext\",\"did_to_ext_start\":\"13001\",\"did_to_ext_end\":\"13004\",\"def_dest\":\"outroute\",\"def_dest_value\":\"%s\"", apiUtil.getOutBoundRouteSummary("Out1").id)).
                apply();

        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 9913001" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "9913001", DEVICE_ASSIST_2, false);

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
    @Feature("InboundRoute-DIDPattern")
    @Story("MatchDIDRangetoExtensionRange")
    @Description("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Play Greeting then Hang Up选择prompt3，播放1遍\n" +
            "\t72.通过sps外线拨打995503300\n" +
            "\t\tasterisk后台查看播放提示音文件prompt3,挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "InboundRoute","DIDPattern", "MatchDIDRangetoExtensionRange", "PlayGreetingthenHangUp", "P3"})
    public void testIRDID_72_MatchDIDToExtRange() {
        prerequisite();
        step("编辑呼入路由In1，DID Pattern选择Match DID Range to Extension Range，DID Range: 5503300-5503304，呼入目的地选择Play Greeting then Hang Up选择prompt3，播放1遍");
        apiUtil.deleteAllInbound().
                createInbound("In1", trunk9, "Extension", "1000").
                editInbound("In1", String.format("\"did_option\":\"range_to_ext\",\"did_to_ext_start\":\"5503300\",\"did_to_ext_end\":\"5503304\",\"def_dest\":\"play_greeting\",\"def_dest_prefix\":\"1\",\"def_dest_value\":\"prompt3.wav\"")).
                apply();
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread = new SSHLinuxUntils.AsteriskThread(asteriskObjectList, PROMPT_3);
        thread.start();
        step("1:login with admin,trunk: " + SPS);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 2000" + ",[callee] 995503300" + ",[trunk] " + SPS);
        pjsip.Pj_Make_Call_No_Answer(2000, "995503300", DEVICE_ASSIST_2, false);
        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <= 300) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
            log.debug("【asteriskObjectList.size()】"+asteriskObjectList.size());
        }
        if (tmp == 301) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;
        assertStep("[CDR校验]");
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", "play_file", STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
}