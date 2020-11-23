package com.yeastar.testcase.pseries;

import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.AsteriskObject;
import com.yeastar.untils.CDRObject.CDRNAME;
import com.yeastar.untils.CDRObject.STATUS;
import com.yeastar.untils.DataUtils;
import com.yeastar.untils.SSHLinuxUntils;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * @program: SwebTest
 * @description: test out bound route caller basic
 * @author: huangjx@yeastar.com
 * @create: 2020/11/04
 */
@Log4j2
public class TestOutboundBasic extends TestCaseBaseNew {
    List<String> trunk9 = new ArrayList<>();
    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();

    private boolean isRunRecoveryEnvFlag = false;
    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;

    TestOutboundBasic() {
        trunk9.add(SPS);
        trunk9.add(BRI_1);
        trunk9.add(FXO_1);
        trunk9.add(E1);
        trunk9.add(SIPTrunk);
        trunk9.add(ACCOUNTTRUNK);
        trunk9.add(GSM);
    }

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
//            initOutbound();
            initFeatureCode();

            isRunRecoveryEnvFlag = registerAllExtensions();
        }
        step("=========== init before class  end =========");

        List<String> officeTimes = new ArrayList<>();
        List<String> resetTimes = new ArrayList<>();
        officeTimes.add("00:00-00:00");
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().createOfficeTime("sun mon tue wed thu fri sat", officeTimes, resetTimes).apply();

        log.debug("[prerequisite time]:" + (System.currentTimeMillis() - startTime) / 1000 + " Seconds");
    }

    /**
     * Business Hours and Holidays->Business Hours 删除所有；添加一条
     * 00:00-00:00，	Sunday.Monday.Tuesday.Wednesday.Thursday.Friday.Saturday
     * Business Hours and Holidays->Holidays 删除所有；
     * *
     */
    public void initBusinessHours() {
        List<String> officeTimes = new ArrayList<>();
        List<String> resetTimes = new ArrayList<>();
        officeTimes.add("00:00-00:00");
        apiUtil.deleteAllHoliday().deleteAllOfficeTime().createOfficeTime("sun mon tue wed thu fri sat", officeTimes, resetTimes).apply();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("basic,Trunk")
    @Description("1.新建呼出路由OR1,Dial Pattern: 11. ,Strip:2，选择sip外线，选择Default_Extension_Group、分机A-1000，其它默认保存\n" +
            "\t分机1000拨打113001呼出\n" +
            "\t\t辅助1的分机3001响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Basic","Trunk","SIPREGISTER","P1"})
    public void testOR_01_Basic_Trunk() {
        prerequisite();
        step("新建呼出路由OR1,Dial Pattern: 11. ,Strip:2，选择sip外线，选择Default_Extension_Group、分机A-1000，其它默认保存");
        apiUtil.deleteAllOutbound().createOutbound("OR1", asList(SIPTrunk), asList("Default_Extension_Group", "1000"), "11.", 2).apply();

        step("1:login with admin,trunk: " + SIPTrunk);
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 113001");
        pjsip.Pj_Make_Call_No_Answer(1000, "113001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(3001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "113001 hung up","", SIPTrunk,"Outbound"));

        step("################[被叫挂断]##############");
        step("2:[caller] 1000" + ",[callee] 113001");
        pjsip.Pj_Make_Call_No_Answer(1000, "113001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[被叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "113001", STATUS.ANSWER.toString(), "test A<1000> hung up","", SIPTrunk,"Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("basic,Trunk")
    @Description("2.新建呼出路由OR2,Dial Pattern: 12. ,Strip:2，选择sps外线，选择Default_Extension_Group、分机A-1000，其它默认保存\n" +
            "\t分机1002拨打122222呼出\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Basic","Trunk","SPS","P1"})
    public void testOR_02_Basic_Trunk() {
        prerequisite();
        step("新建呼出路由OR2,Dial Pattern: 12. ,Strip:2，选择sps外线，选择Default_Extension_Group、分机A-1000，其它默认保存");
        apiUtil.deleteAllOutbound().createOutbound("OR2", asList(SPS), asList("Default_Extension_Group", "1000"), "12.", 2).apply();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 122222");
        pjsip.Pj_Make_Call_No_Answer(1002, "122222", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1002);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1002.toString(), "122222", STATUS.ANSWER.toString(), "testta C<1002> hung up",  "", SPS,"Outbound"));

        step("################[被叫挂断]##############");
        step("2:[caller] 3001" + ",[callee] 122222");
        pjsip.Pj_Make_Call_No_Answer(1002, "122222", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1002.toString(), "122222", STATUS.ANSWER.toString(), "122222 hung up",  "", SPS,"Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("basic,Trunk")
    @Description("3.新建呼出路由OR3,Dial Pattern: 13. ,Strip:2，选择account外线，选择Default_Extension_Group、分机A-1000，其它默认保存\n" +
            "\t分机1003拨打133333呼出\n" +
            "\t\t辅助3的分机4000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Basic","Trunk","ACCOUNTTRUNK","P1"})
    public void testOR_03_Basic_Trunk() {
        prerequisite();
        step("新建呼出路由OR3,Dial Pattern: 13. ,Strip:2，选择account外线，选择Default_Extension_Group、分机A-1000，其它默认保存");
        apiUtil.deleteAllOutbound().createOutbound("OR3", asList(ACCOUNTTRUNK), asList("Default_Extension_Group", "1000"), "13.", 2).apply();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1003" + ",[callee] 133333");
        pjsip.Pj_Make_Call_No_Answer(1003, "133333", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(4000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(4000, false);
        assertThat(getExtensionStatus(4000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1003);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple( CDRNAME.Extension_1003.toString(),"133333", STATUS.ANSWER.toString(), "testa D<1003> hung up", "",ACCOUNTTRUNK, "Outbound"));

        step("################[被叫挂断]##############");
        step("2:[caller] 1003" + ",[callee] 133333");
        pjsip.Pj_Make_Call_No_Answer(1003, "133333", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(4000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(4000, false);
        assertThat(getExtensionStatus(4000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(4000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple( CDRNAME.Extension_1003.toString(),"133333", STATUS.ANSWER.toString(), "133333 hung up", "",ACCOUNTTRUNK, "Outbound"));
        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("basic,Trunk")
    @Description("4.新建呼出路由OR4,Dial Pattern: 14. ,Strip:2，选择FXO外线，选择Default_Extension_Group、分机A-1000，其它默认保存\n" +
            "\t分机1000拨打142000呼出\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Basic","Trunk","FXO","P2"})
    public void testOR_04_Basic_Trunk() {
        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXO线路 不测试！");
        }
        prerequisite();
        step("新建呼出路由OR4,Dial Pattern: 14. ,Strip:2，选择FXO外线，选择Default_Extension_Group、分机A-1000，其它默认保存");
        apiUtil.deleteAllOutbound().createOutbound("OR4", asList(FXO_1), asList("Default_Extension_Group", "1000"), "14.", 2).apply();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 142000");
        pjsip.Pj_Make_Call_No_Answer(1000, "142000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(),"142000",  STATUS.ANSWER.toString(), "test A<1000> hung up", "", FXO_1,"Outbound"));

        step("################[被叫挂断]##############");
        step("2:[caller] 1000" + ",[callee] 142000");
        pjsip.Pj_Make_Call_No_Answer(1000, "142000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(),"142000",  STATUS.ANSWER.toString(), "142000 hung up", "", FXO_1,"Outbound"));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("basic,Trunk")
    @Description("5.新建呼出路由OR5,Dial Pattern: 15. ,Strip:2，选择GSM外线，选择Default_Extension_Group、分机A-1000，其它默认保存\n" +
            "\t分机1000拨打15+辅助2gsm号码呼出\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Basic","Trunk","GSM","P2"})
    public void testOR_05_Basic_Trunk() {
        if(GSM.trim().equalsIgnoreCase("null") || GSM.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"GSM线路 不测试！");
        }
        prerequisite();
        step("新建呼出路由OR5,Dial Pattern: 15. ,Strip:2，选择GSM外线，选择Default_Extension_Group、分机A-1000，其它默认保存");
        apiUtil.deleteAllOutbound().createOutbound("OR5", asList(GSM), asList("Default_Extension_Group", "1000"), "15.", 2).apply();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee]"+DEVICE_ASSIST_GSM);
        pjsip.Pj_Make_Call_No_Answer(1000, "15"+DEVICE_ASSIST_GSM, DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        sleep(WaitUntils.SHORT_WAIT*30);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple( CDRNAME.Extension_1000.toString(), "15"+DEVICE_ASSIST_GSM, STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString()+" hung up", "",GSM, "Outbound"));

        step("################[被叫挂断]##############");
        step("2:[caller] 1000" + ",[callee]"+DEVICE_ASSIST_GSM);
        pjsip.Pj_Make_Call_No_Answer(1000, "15"+DEVICE_ASSIST_GSM, DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        sleep(WaitUntils.SHORT_WAIT*30);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple( CDRNAME.Extension_1000.toString(), "15"+DEVICE_ASSIST_GSM, STATUS.ANSWER.toString(), "15"+DEVICE_ASSIST_GSM + " hung up", "",GSM, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("basic,Trunk")
    @Description("6.新建呼出路由OR6,Dial Pattern: 16. ,Strip:2，选择BRI外线，选择Default_Extension_Group、分机A-1000，其它默认保存\n" +
            "\t分机1000拨打166666呼出\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【ID1036056】\n" +
            "【P系列】【自动化测试】 呼出路由 E1/BRI CDR 被叫显示异常")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Basic","Trunk","BRI","P2"})
    public void testOR_06_Basic_Trunk() {
        prerequisite();
        step("新建呼出路由OR6,Dial Pattern: 16. ,Strip:2，选择BRI外线，选择Default_Extension_Group、分机A-1000，其它默认保存");
        apiUtil.deleteAllOutbound().createOutbound("OR5", asList(BRI_1), asList("Default_Extension_Group", "1000"), "16.", 2).apply();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 166666");
        pjsip.Pj_Make_Call_No_Answer(1000, "166666", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(),"166666" , STATUS.ANSWER.toString(),  CDRNAME.Extension_1000.toString()+" hung up", BRI_1, "", "Outbound"));

        step("################[被叫挂断]##############");
        step("2:[caller] 1000" + ",[callee] 166666");
        pjsip.Pj_Make_Call_No_Answer(1000, "166666", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(),"166666" , STATUS.ANSWER.toString(),  "166666 hung up", BRI_1, "", "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("basic,Trunk")
    @Description("7.新建呼出路由OR7,Dial Pattern: 17. ,Strip:2，选择E1外线，选择Default_Extension_Group、分机A-1000，其它默认保存\n" +
            "\t分机1000拨打177777呼出\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【ID1036056】\n" +
            "【P系列】【自动化测试】 呼出路由 E1/BRI CDR 被叫显示异常")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Basic","Trunk","P2"})
    public void testOR_07_Basic_Trunk() {
        prerequisite();
        step("新建呼出路由OR7,Dial Pattern: 17. ,Strip:2，选择E1外线，选择Default_Extension_Group、分机A-1000，其它默认保存");
        apiUtil.deleteAllOutbound().createOutbound("OR5", asList(E1), asList("Default_Extension_Group", "1000"), "17.", 2).apply();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1000" + ",[callee] 177777");
        pjsip.Pj_Make_Call_No_Answer(1000, "177777", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple( CDRNAME.Extension_1000.toString(), "177777", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString()+" hung up", "",E1, "Outbound"));

        step("################[被叫挂断]##############");

        step("2:[caller] 1000" + ",[callee] 177777");
        pjsip.Pj_Make_Call_No_Answer(1000, "177777", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple( CDRNAME.Extension_1000.toString(), "177777", STATUS.ANSWER.toString(),"2000<2000> hung up", "",E1, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("Extension,Role,ExtensionGroup")
    @Description("新建呼出路由OR8,Dial Pattern: 21. ,Strip:2，选择sps外线，选择Default_Extension_Group，其它默认保存\n" +
            "\t8.分机1000拨打211111呼出\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Extension","Role","ExtensionGroup","P2"})
    public void testOR_08_Extension_Role_ExtensionGroup() {
        prerequisite();
        step("新建呼出路由OR8,Dial Pattern: 21. ,Strip:2，选择sps外线，选择Default_Extension_Group，其它默认保存");
        apiUtil.deleteAllOutbound().createOutbound("OR8", asList(SPS), asList("Default_Extension_Group"), "21.", 2).apply();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3211111000");
        pjsip.Pj_Make_Call_No_Answer(1000, "211111", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "211111", STATUS.ANSWER.toString(), "211111 hung up",  "",SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("Extension,Role,ExtensionGroup")
    @Description("新建呼出路由OR8,Dial Pattern: 21. ,Strip:2，选择sps外线，选择Default_Extension_Group，其它默认保存\n" +
            "\t9.分机1001拨打211111呼出\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Extension","Role","ExtensionGroup","P3"})
    public void testOR_09_Extension_Role_ExtensionGroup() {
        prerequisite();
        step("新建呼出路由OR8,Dial Pattern: 21. ,Strip:2，选择sps外线，选择Default_Extension_Group，其它默认保存");
        apiUtil.deleteAllOutbound().createOutbound("OR8", asList(SPS), asList("Default_Extension_Group"), "21.", 2).apply();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 211111");
        pjsip.Pj_Make_Call_No_Answer(1001, "211111", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(),"211111", STATUS.ANSWER.toString(), "211111 hung up", "",SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("Extension,Role,ExtensionGroup")
    @Description("新建呼出路由OR8,Dial Pattern: 21. ,Strip:2，选择sps外线，选择Default_Extension_Group，其它默认保存\n" +
            "\t10.FXS分机1020拨打211111呼出\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Extension","Role","ExtensionGroup","P3"})
    public void testOR_10_Extension_Role_ExtensionGroup() {
        prerequisite();
        step("新建呼出路由OR8,Dial Pattern: 21. ,Strip:2，选择sps外线，选择Default_Extension_Group，其它默认保存");
        apiUtil.deleteAllOutbound().createOutbound("OR8", asList(SPS), asList("Default_Extension_Group"), "21.", 2).apply();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1020" + ",[callee] 211111");
        pjsip.Pj_Make_Call_No_Answer(2001, "211111", DEVICE_ASSIST_2, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1020.toString(),"211111", STATUS.ANSWER.toString(), "211111 hung up", "",SPS, "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("Extension,Role,ExtensionGroup")
    @Description("新建呼出路由OR9,Dial Pattern: 22. ,Strip:2，选择sps外线，选择ExGroup1、分机1003、1004\n" +
            "\t11.分机1001拨打221111呼出\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Extension","Role","ExtensionGroup","P3"})
    public void testOR_11_Extension_Role_ExtensionGroup() {
        prerequisite();
        step("新建呼出路由OR9,Dial Pattern: 22. ,Strip:2，选择sps外线，选择ExGroup1、分机1003、1004");
        apiUtil.deleteAllOutbound().createOutbound("OR9", asList(SPS), asList("Default_Extension_Group","1003","1004"), "22.", 2).apply();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1001" + ",[callee] 221111");
        pjsip.Pj_Make_Call_No_Answer(1001, "221111", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "221111", STATUS.ANSWER.toString(), "221111 hung up", "",SPS, "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("Extension,Role,ExtensionGroup")
    @Description("新建呼出路由OR9,Dial Pattern: 22. ,Strip:2，选择sps外线，选择ExGroup1、分机1003、1004\n" +
            "\t12.分机1003拨打222222呼出\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n" +
            "\t\t\t编辑OR9，删除分机1003、ExGroup1；\n" +
            "分机1001、1003分别拨打22222呼出\n" +
            "\t\t\t\t呼出失败，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Extension","Role","ExtensionGroup","P3"})
    public void testOR_12_Extension_Role_ExtensionGroup() {
        prerequisite();
        step("新建呼出路由OR9,Dial Pattern: 22. ,Strip:2，选择sps外线，选择ExGroup1、分机1003、1004");
        apiUtil.deleteAllOutbound().createOutbound("OR9", asList(SPS), asList("Default_Extension_Group","1003","1004"), "22.", 2).apply();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 1003 " + ",[callee] 222222");
        pjsip.Pj_Make_Call_No_Answer(1003, "222222", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRNAME.Extension_1003.toString(), "222222", STATUS.ANSWER.toString(), "222222 hung up", "",SPS, "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("Extension,Role,ExtensionGroup")
    @Description("新建呼出路由OR11,Dial Pattern: 24. ,Strip:2，选择sps外线，Role选\tSupervisor，不选任意分机\n" +
            "\t13.分机1001拨打244444呼出\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Extension","Role","ExtensionGroup","P2"})
    public void testOR_13_Extension_Role_ExtensionGroup() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("Extension,Role,ExtensionGroup")
    @Description("新建呼出路由OR11,Dial Pattern: 24. ,Strip:2，选择sps外线，Role选\tSupervisor，不选任意分机\n" +
            "\t14.分机1002拨打244444呼出\n" +
            "\t\t呼出失败，主叫被挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Extension","Role","ExtensionGroup","P3"})
    public void testOR_14_Extension_Role_ExtensionGroup() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("Extension,Role,ExtensionGroup")
    @Description("新建呼出路由OR12,Dial Pattern: 25. ,Strip:2，选择sps外线，Role选\tOperator，不选任意分机\n" +
            "\t15.分机1002拨打25555呼出\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Extension","Role","ExtensionGroup","P2"})
    public void testOR_15_Extension_Role_ExtensionGroup() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("Extension,Role,ExtensionGroup")
    @Description("新建呼出路由OR12,Dial Pattern: 25. ,Strip:2，选择sps外线，Role选\tOperator，不选任意分机\n" +
            "\t16.分机1003拨打25555呼出\n" +
            "\t\t呼出失败，主叫被挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Extension","Role","ExtensionGroup","P3"})
    public void testOR_16_Extension_Role_ExtensionGroup() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("Extension,Role,ExtensionGroup")
    @Description("新建呼出路由OR13,Dial Pattern: 26. ,Strip:2，选择sps外线，Role选\tEmployee，不选任意分机\n" +
            "\t17.分机1003拨打26666呼出\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Extension","Role","ExtensionGroup","P2"})
    public void testOR_17_Extension_Role_ExtensionGroup() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("Extension,Role,ExtensionGroup")
    @Description("新建呼出路由OR13,Dial Pattern: 26. ,Strip:2，选择sps外线，Role选\tEmployee，不选任意分机\n" +
            "\t18.分机1004拨打26666呼出\n" +
            "\t\t呼出失败，主叫被挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Extension","Role","ExtensionGroup","P3"})
    public void testOR_18_Extension_Role_ExtensionGroup() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("Extension,Role,ExtensionGroup")
    @Description("新建呼出路由OR14,Dial Pattern: 27. ,Strip:2，选择sps外线，Role选Human Resource，不选任意分机\n" +
            "\t19.分机1004拨打27777呼出\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Extension","Role","ExtensionGroup","P2"})
    public void testOR_19_Extension_Role_ExtensionGroup() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("Extension,Role,ExtensionGroup")
    @Description("新建呼出路由OR14,Dial Pattern: 27. ,Strip:2，选择sps外线，Role选Human Resource，不选任意分机\n" +
            "\t20.分机1005拨打27777呼出\n" +
            "\t\t呼出失败，主叫被挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Extension","Role","ExtensionGroup","P3"})
    public void testOR_20_Extension_Role_ExtensionGroup() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("Extension,Role,ExtensionGroup")
    @Description("新建呼出路由OR15,Dial Pattern: 28. ,Strip:2，选择sps外线，Role选Accounting，不选任意分机\n" +
            "\t21.分机1005拨打28888呼出\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Extension","Role","ExtensionGroup","P2"})
    public void testOR_21_Extension_Role_ExtensionGroup() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("Extension,Role,ExtensionGroup")
    @Description("新建呼出路由OR15,Dial Pattern: 28. ,Strip:2，选择sps外线，Role选Accounting，不选任意分机\n" +
            "\t22.分机1000拨打28888呼出\n" +
            "\t\t呼出失败，主叫被挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Extension","Role","ExtensionGroup","P3"})
    public void testOR_22_Extension_Role_ExtensionGroup() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("Extension,Role,ExtensionGroup")
    @Description("新建呼出路由OR16,Dial Pattern: 29. ,Strip:2，选择sps外线，Role选\tAccounting，分机选择ExGroup1,1003\n" +
            "\t23.分机1000拨打29999呼出\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Extension","Role","ExtensionGroup","P2"})
    public void testOR_23_Extension_Role_ExtensionGroup() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("Extension,Role,ExtensionGroup")
    @Description("新建呼出路由OR16,Dial Pattern: 29. ,Strip:2，选择sps外线，Role选\tAccounting，分机选择ExGroup1,1003\n" +
            "\t24.分机1003拨打2999呼出\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n" +
            "\t25.分机1005拨打29999呼出\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n" +
            "\t\t\t修改分机1005的Role为Administrator；\n" +
            "分机1005拨打29999呼出\n" +
            "\t\t\t\t呼出失败，主叫被挂断；检查cdr\n" +
            "\t\t\t\t\t修改分机1005的Role为Accounting；\n" +
            "分机1005拨打29999呼出\n" +
            "\t\t\t\t\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n" +
            "\t26.分机1002拨打2999呼出\n" +
            "\t\t呼出失败，主叫被挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Extension","Role","ExtensionGroup","P3"})
    public void testOR_24_Extension_Role_ExtensionGroup() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("Extension,Role,ExtensionGroup")
    @Description("新建呼出路由OR16,Dial Pattern: 29. ,Strip:2，选择sps外线，Role选\tAccounting，分机选择ExGroup1,1003\n" +
            "\t25.分机1005拨打29999呼出\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n" +
            "\t\t\t修改分机1005的Role为Administrator；\n" +
            "分机1005拨打29999呼出\n" +
            "\t\t\t\t呼出失败，主叫被挂断；检查cdr\n" +
            "\t\t\t\t\t修改分机1005的Role为Accounting；\n" +
            "分机1005拨打29999呼出\n" +
            "\t\t\t\t\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Extension","Role","ExtensionGroup","P3"})
    public void testOR_25_Extension_Role_ExtensionGroup() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("Extension,Role,ExtensionGroup")
    @Description("新建呼出路由OR16,Dial Pattern: 29. ,Strip:2，选择sps外线，Role选\tAccounting，分机选择ExGroup1,1003\n" +
            "\t26.分机1002拨打2999呼出\n" +
            "\t\t呼出失败，主叫被挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Extension","Role","ExtensionGroup","P3"})
    public void testOR_26_Extension_Role_ExtensionGroup() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("OutboundRoutePassword")
    @Description("新建呼出路由OR17,Dial Pattern: 31. ,Strip:2，选择sps外线，分机选择ExGroup1,1003,Outbound Route Password 选择single PIN，密码设置为123\n" +
            "\t27.分机1000拨打3111111呼出\n" +
            "\t\tasterisk后台检测到提示音enter-password.gsm时，输入密码123# ；\n" +
            "辅助2的分机2000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","OutboundRoutePassword","P2"})
    public void testOR_27_OutboundRoutePassword() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("OutboundRoutePassword")
    @Description("新建呼出路由OR17,Dial Pattern: 31. ,Strip:2，选择sps外线，分机选择ExGroup1,1003,Outbound Route Password 选择single PIN，密码设置为123\n" +
            "\t28.分机1000拨打3111111呼出\n" +
            "\t\tasterisk后台每次检测到提示音enter-password.gsm时，输入密码122#；三次后主叫被挂断，呼出失败；检查cdr\n" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","OutboundRoutePassword","P2"})
    public void testOR_28_OutboundRoutePassword() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("OutboundRoutePassword")
    @Description("新建呼出路由OR17,Dial Pattern: 31. ,Strip:2，选择sps外线，分机选择ExGroup1,1003,Outbound Route Password 选择single PIN，密码设置为123\n" +
            "\t29.分机1003拨打31111呼出\n" +
            "\t\tasterisk后台第1次检测到提示音enter-password.gsm时，输入密码456#；第二次检测到enter-password.gsm时，输入密码789#；第三次检测到提示音enter-password.gsm时，输入密码123#；\n" +
            "\n" +
            "辅助2的分机2000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","OutboundRoutePassword","P3"})
    public void testOR_29_OutboundRoutePassword() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("OutboundRoutePassword")
    @Description("新建呼出路由OR17,Dial Pattern: 31. ,Strip:2，选择sps外线，分机选择ExGroup1,1003,Outbound Route Password 选择single PIN，密码设置为123\n" +
            "\t30.编辑呼出路由OR17,Outbound Route Password密码修改为123456789012345\n" +
            "\t\t分机1003拨打31111呼出\n" +
            "\t\t\tasterisk后台检测到提示音enter-password.gsm时，输入密码123456789012345 ；\n" +
            "辅助2的分机2000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","OutboundRoutePassword","P3"})
    public void testOR_30_OutboundRoutePassword() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("OutboundRoutePassword")
    @Description("新建呼出路由OR17,Dial Pattern: 31. ,Strip:2，选择sps外线，分机选择ExGroup1,1003,Outbound Route Password 选择single PIN，密码设置为123\n" +
            "\t31.编辑呼出路由OR17,Outbound Route Password选择为disable\n" +
            "\t\t分机1003拨打31111呼出\n" +
            "\t\t\t直接呼出成功，辅助2的分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","OutboundRoutePassword","P3"})
    public void testOR_31_OutboundRoutePassword() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("RrmemoryHunt")
    @Description("新建呼出路由OR18,Dial Pattern: 41. ,Strip:2，选择sps外线、sip外线、Account外线，分机选择ExGroup1,1003,勾选Rrmemory Hunt\n" +
            "\t32.分机1000拨打413001呼出\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n" +
            "\t\t\t分机1000拨打413001呼出\n" +
            "\t\t\t\t辅助1的分机3001响铃，接听，挂断；检查cdr\n" +
            "\t\t\t\t\t分机1000拨打413001呼出\n" +
            "\t\t\t\t\t\t辅助3的分机4000响铃，接听，挂断；检查cdr\n" +
            "\t\t\t\t\t\t\t分机1000拨打413001呼出\n" +
            "\t\t\t\t\t\t\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","RrmemoryHunt","P2"})
    public void testOR_32_RrmemoryHunt() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("RrmemoryHunt")
    @Description("新建呼出路由OR18,Dial Pattern: 41. ,Strip:2，选择sps外线、sip外线、Account外线，分机选择ExGroup1,1003,勾选Rrmemory Hunt\n" +
            "\t\t\t\t\t\t\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n" +
            "\t33.编辑OR18，选择外线sps外线、sip外线、sip2外线（不可用）、Account外线\n" +
            "\t\t分机1000拨打413001呼出\n" +
            "\t\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n" +
            "\t\t\t\t分机1000拨打413001呼出\n" +
            "\t\t\t\t\t辅助1的分机3001响铃，接听，挂断；检查cdr\n" +
            "\t\t\t\t\t\t分机1000拨打413001呼出\n" +
            "\t\t\t\t\t\t\t辅助3的分机4000响铃，接听，挂断；检查cdr\n" )
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","RrmemoryHunt","P3"})
    public void testOR_33_RrmemoryHunt() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("RrmemoryHunt")
    @Description("新建呼出路由OR18,Dial Pattern: 41. ,Strip:2，选择sps外线、sip外线、Account外线，分机选择ExGroup1,1003,勾选Rrmemory Hunt\n" +
            "\t34.编辑OR18，调整外线的顺序sip外线、Account外线、sps外线\n" +
            "\t\t分机1000拨打413001呼出\n" +
            "\t\t\t辅助1的分机3001响铃，接听，挂断；检查cdr\n" +
            "\t\t\t\t分机1000拨打413001呼出\n" +
            "\t\t\t\t\t辅助3的分机4000响铃，接听，挂断；检查cdr\n" +
            "\t\t\t\t\t\t分机1000拨打413001呼出\n" +
            "\t\t\t\t\t\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n" +
            "\t物理外线自动化不测")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","RrmemoryHunt","P3"})
    public void testOR_34_RrmemoryHunt() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("RrmemoryHunt")
    @Description("35.新建呼出路由OR19,Dial Pattern: 42. ,Strip:2，选择sps外线、sip外线、Account外线，分机选择ExGroup1,1003,不勾选Rrmemory Hunt\n" +
            "\t分机1000拨打413001呼出\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n" +
            "\t\t\t分机1000拨打413001呼出\n" +
            "\t\t\t\t辅助2的分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","RrmemoryHunt","P2"})
    public void testOR_35_RrmemoryHunt() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("Priority")
    @Description("新建呼出路由OR20,Dial Pattern: 51. ,Strip:2，选择sps外线，分机选择ExGroup1；\n" +
            "新建呼出路由OR21,Dial Pattern: 51. ,Strip:2，选择sip外线，分机选择ExGroup1;\n" +
            "新建呼出路由OR22,Dial Pattern: 51. ,Strip:2，选择account外线，分机选择ExGroup1\n" +
            "\t36.分机1000拨打513001呼出\n" +
            "\t\t辅助2的分机2000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Priority","P2"})
    public void testOR_36_Priority() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("Priority")
    @Description("新建呼出路由OR20,Dial Pattern: 51. ,Strip:2，选择sps外线，分机选择ExGroup1；\n" +
            "新建呼出路由OR21,Dial Pattern: 51. ,Strip:2，选择sip外线，分机选择ExGroup1;\n" +
            "新建呼出路由OR22,Dial Pattern: 51. ,Strip:2，选择account外线，分机选择ExGroup1\n" +
            "\t37.调整呼出路由的优先级OR21调整到第一条\n" +
            "\t\t分机1000拨打513001呼出\n" +
            "\t\t\t辅助1的分机3001响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Priority","P2"})
    public void testOR_37_Priority() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("Priority")
    @Description("新建呼出路由OR20,Dial Pattern: 51. ,Strip:2，选择sps外线，分机选择ExGroup1；\n" +
            "新建呼出路由OR21,Dial Pattern: 51. ,Strip:2，选择sip外线，分机选择ExGroup1;\n" +
            "新建呼出路由OR22,Dial Pattern: 51. ,Strip:2，选择account外线，分机选择ExGroup1\n" +
            "\t38.调整呼出路由的优先级OR22调整到第一条\n" +
            "\t\t分机1000拨打513001呼出\n" +
            "\t\t\t辅助3的分机4000响铃，接听，挂断；检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Priority","P2"})
    public void testOR_38_Priority() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("Priority")
    @Description("新建呼出路由OR20,Dial Pattern: 51. ,Strip:2，选择sps外线，分机选择ExGroup1；\n" +
            "新建呼出路由OR21,Dial Pattern: 51. ,Strip:2，选择sip外线，分机选择ExGroup1;\n" +
            "新建呼出路由OR22,Dial Pattern: 51. ,Strip:2，选择account外线，分机选择ExGroup1\n" +
            "\t39.调整呼出路由的优先级OR21调整到第一条\n" +
            "\t\t分机1000拨打513001呼出\n" +
            "\t\t\t辅助2的分机2000响铃，接听，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Priority","P2"})
    public void testOR_39_Priority() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("Delete")
    @Description("40.单条删除OR1\n" +
            "\t检查列表OR1被删除成功")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Delete","P2"})
    public void testOR_40_Delete() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("OutboundRoute-Basic")
    @Story("Delete")
    @Description("41.批量删除所有呼出路由\n" +
            "\t检查列表呼出路由全部删除成功")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "SPS", "OutboundRoute-Basic","Delete","P2"})
    public void testOR_41_Delete() {
        prerequisite();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:[caller] 3001" + ",[callee] 3000");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("2000<2000>", CDRNAME.Extension_1000.toString(), STATUS.ANSWER.toString(), "2000<2000> hung up", SIPTrunk, "", "Outbound"));

        softAssertPlus.assertAll();
    }
}
