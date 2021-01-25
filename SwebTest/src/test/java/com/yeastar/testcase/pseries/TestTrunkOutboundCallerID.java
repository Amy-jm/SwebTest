package com.yeastar.testcase.pseries;

import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.AsteriskObject;
import com.yeastar.untils.CDRObject.CDRNAME;
import com.yeastar.untils.CDRObject.STATUS;
import com.yeastar.untils.DataUtils;
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
 * @description: test out bound callerID
 * @author: huangjx@yeastar.com
 * @create: 2020/11/04
 */
@Log4j2
public class TestTrunkOutboundCallerID extends TestCaseBaseNew {
    List<String> trunk9 = new ArrayList<>();
    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();

    private boolean isRunRecoveryEnvFlag = true;
    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;

    TestTrunkOutboundCallerID() {
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
            initOutbound();
            initIVR();
            initInbound();
            initFeatureCode();

            isRunRecoveryEnvFlag = registerAllExtensions();
        }
        step("=========== init before class  end =========");

        log.debug("[prerequisite time]:" + (System.currentTimeMillis() - startTime) / 1000 + " Seconds");
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("SPS")
    @Description("编辑sps外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,Outbound Caller ID List 为空\n" +
            "\t1.分机1000通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t\t检查来电显示为DOD，接听，挂断；检查cdr,Outbound Caller ID: spsOuntCid\n" +
            "\t前置条件的默认值设置为这个，跑完其它的修改回去")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","SPS"})
    public void testTrunkOutCallerID_01_SPS() {
        prerequisite();
        apiUtil.editSpsTrunk(SPS,"\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[]").apply();

        step("[caller] 1000" + ",[callee] 22000");
        pjsip.Pj_Make_Call_No_Answer(1000, "22000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "22000", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound", "spsOuntCid"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("SPS")
    @Description("编辑sps外线-》Outbound Caller ID" +
            "Outbound Caller ID: 空 ,Outbound Caller ID Name:空,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",Outbound Caller ID: SharedCallerID123,Outbound Caller ID Name:SharedCallerName, 分机选择：Default_Extension_Group\n" +
            "\t2.分机1000通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t\t检查来电显示为SharedCallerName，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID123\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","SPS"})
    public void testTrunkOutCallerID_02_SPS() {
        prerequisite();
        apiUtil.editSpsTrunk(SPS,String.format("\"def_outbound_cid\":\"\",\"def_outbound_cid_name\":\"\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName\",\"outbound_cid\":\"SharedCallerID123\",\"assoc_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"}]",apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).apply();

        step("[caller] 1000" + ",[callee] 22000");
        pjsip.Pj_Make_Call_No_Answer(1000, "22000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("SharedCallerName");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "22000", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound", "SharedCallerID123"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("SPS")
    @Description("编辑sps外线-》Outbound Caller ID" +
            "Outbound Caller ID: 空 ,Outbound Caller ID Name:空,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",Outbound Caller ID: SharedCallerID123,Outbound Caller ID Name:SharedCallerName, 分机选择：Default_Extension_Group\n" +
            "\t3.分机1001通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t\t检查来电显示为SharedCallerName，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID123")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","SPS"})
    public void testTrunkOutCallerID_03_SPS() {
        prerequisite();
        apiUtil.editSpsTrunk(SPS,String.format("\"def_outbound_cid\":\"\",\"def_outbound_cid_name\":\"\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName\",\"outbound_cid\":\"SharedCallerID123\",\"assoc_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"}]",apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).apply();

        step("[caller] 1001" + ",[callee] 22000");
        pjsip.Pj_Make_Call_No_Answer(1001, "22000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("SharedCallerName");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "22000", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", SPS, "Outbound", "SharedCallerID123"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("SPS")
    @Description("编辑sps外线-》Outbound Caller ID" +
            "Outbound Caller ID: 空 ,Outbound Caller ID Name：空,\n" +
            "Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1001-1002\n" +
            "Outbound Caller ID Name：RanggeName1\n" +
            "\t4.分机1001通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t\t检查来电显示为RanggeName1，接听，挂断；检查cdr,Outbound Caller ID: +5503301\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","SPS"})
    public void testTrunkOutCallerID_04_SPS() {
        prerequisite();
        apiUtil.editSpsTrunk(SPS,"\"def_outbound_cid\":\"\",\"def_outbound_cid_name\":\"\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeName1\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1001-1002\",\"pos\":1,\"outbound_cid\":\"\"}]").apply();

        step("[caller] 1001" + ",[callee] 22000");
        pjsip.Pj_Make_Call_No_Answer(1001, "22000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("RangeName1");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "22000", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", SPS, "Outbound", "+5503301"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("SPS")
    @Description("编辑sps外线-》Outbound Caller ID" +
            "Outbound Caller ID: 空 ,Outbound Caller ID Name：空,\n" +
            "Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1001-1002\n" +
            "Outbound Caller ID Name：RanggeName1\n" +
            "\t5.分机1002通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t\t检查来电显示为RanggeName1，接听，挂断；检查cdr,Outbound Caller ID: +5503302")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","SPS"})
    public void testTrunkOutCallerID_05_SPS() {
        prerequisite();
        apiUtil.editSpsTrunk(SPS,"\"def_outbound_cid\":\"\",\"def_outbound_cid_name\":\"\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeName1\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1001-1002\",\"pos\":1,\"outbound_cid\":\"\"}]").apply();

        step("[caller] 1002" + ",[callee] 22000");
        pjsip.Pj_Make_Call_No_Answer(1002, "22000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("RangeName1");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1002);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1002.toString(), "22000", STATUS.ANSWER.toString(), CDRNAME.Extension_1002.toString() + " hung up", "", SPS, "Outbound", "+5503302"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("SPS")
    @Description("编辑sps外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "6.分机1000通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t检查来电显示为SharedCallerName1，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID1")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","SPS"})
    public void testTrunkOutCallerID_06_SPS() {
        prerequisite();
        apiUtil.editSpsTrunk(SPS,String.format("\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName1\",\"outbound_cid\":\"SharedCallerID1\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName3\",\"outbound_cid\":\"SharedCallerID3\",\"assoc_ext_list\":[{\"text\":\"testa D\",\"text2\":\"1003\",\"value\":\"%s\",\"type\":\"extension\"},{\"text\":\"t estX\",\"text2\":\"1004\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":2,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName4\",\"outbound_cid\":\"SharedCallerID4\",\"assoc_ext_list\":[{\"text\":\"ExGroup2\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":3,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName5\",\"outbound_cid\":\"SharedCallerID5\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":4,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeNameXXX\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1000-1001\",\"pos\":5,\"outbound_cid\":\"\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionSummary("1004").id,apiUtil.getExtensionGroupSummary("ExGroup2").id,apiUtil.getExtensionSummary("1000").id)).apply();

        step("[caller] 1000" + ",[callee] 22000");
        pjsip.Pj_Make_Call_No_Answer(1000, "22000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("SharedCallerName1");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "22000", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound", "SharedCallerID1"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("SPS")
    @Description("编辑sps外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "7.分机1001通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t检查来电显示为RangeNameXXX，接听，挂断；检查cdr,Outbound Caller ID: +5503302")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","SPS"})
    public void testTrunkOutCallerID_07_SPS() {
        prerequisite();
        apiUtil.editSpsTrunk(SPS,String.format("\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName1\",\"outbound_cid\":\"SharedCallerID1\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName3\",\"outbound_cid\":\"SharedCallerID3\",\"assoc_ext_list\":[{\"text\":\"testa D\",\"text2\":\"1003\",\"value\":\"%s\",\"type\":\"extension\"},{\"text\":\"t estX\",\"text2\":\"1004\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":2,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName4\",\"outbound_cid\":\"SharedCallerID4\",\"assoc_ext_list\":[{\"text\":\"ExGroup2\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":3,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName5\",\"outbound_cid\":\"SharedCallerID5\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":4,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeNameXXX\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1000-1001\",\"pos\":5,\"outbound_cid\":\"\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionSummary("1004").id,apiUtil.getExtensionGroupSummary("ExGroup2").id,apiUtil.getExtensionSummary("1000").id)).apply();

        step("[caller] 1001" + ",[callee] 22000");
        pjsip.Pj_Make_Call_No_Answer(1001, "22000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("RangeNameXXX");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "22000", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", SPS, "Outbound", "+5503302"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("SPS")
    @Description("编辑sps外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "8.分机1002通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t检查来电显示为SharedCallerName4，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID4")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","SPS"})
    public void testTrunkOutCallerID_08_SPS() {
        prerequisite();
        apiUtil.editSpsTrunk(SPS,String.format("\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName1\",\"outbound_cid\":\"SharedCallerID1\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName3\",\"outbound_cid\":\"SharedCallerID3\",\"assoc_ext_list\":[{\"text\":\"testa D\",\"text2\":\"1003\",\"value\":\"%s\",\"type\":\"extension\"},{\"text\":\"t estX\",\"text2\":\"1004\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":2,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName4\",\"outbound_cid\":\"SharedCallerID4\",\"assoc_ext_list\":[{\"text\":\"ExGroup2\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":3,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName5\",\"outbound_cid\":\"SharedCallerID5\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":4,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeNameXXX\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1000-1001\",\"pos\":5,\"outbound_cid\":\"\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionSummary("1004").id,apiUtil.getExtensionGroupSummary("ExGroup2").id,apiUtil.getExtensionSummary("1000").id)).apply();

        step("[caller] 1002" + ",[callee] 22000");
        pjsip.Pj_Make_Call_No_Answer(1002, "22000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("SharedCallerName4");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1002);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1002.toString(), "22000", STATUS.ANSWER.toString(), CDRNAME.Extension_1002.toString() + " hung up", "", SPS, "Outbound", "SharedCallerID4"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("SPS")
    @Description("编辑sps外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "9.分机1003通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t检查来电显示为SharedCallerName3，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID3")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","SPS"})
    public void testTrunkOutCallerID_09_SPS() {
        prerequisite();
        apiUtil.editSpsTrunk(SPS,String.format("\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName1\",\"outbound_cid\":\"SharedCallerID1\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName3\",\"outbound_cid\":\"SharedCallerID3\",\"assoc_ext_list\":[{\"text\":\"testa D\",\"text2\":\"1003\",\"value\":\"%s\",\"type\":\"extension\"},{\"text\":\"t estX\",\"text2\":\"1004\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":2,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName4\",\"outbound_cid\":\"SharedCallerID4\",\"assoc_ext_list\":[{\"text\":\"ExGroup2\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":3,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName5\",\"outbound_cid\":\"SharedCallerID5\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":4,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeNameXXX\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1000-1001\",\"pos\":5,\"outbound_cid\":\"\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionSummary("1004").id,apiUtil.getExtensionGroupSummary("ExGroup2").id,apiUtil.getExtensionSummary("1000").id)).apply();

        step("[caller] 1003" + ",[callee] 22000");
        pjsip.Pj_Make_Call_No_Answer(1003, "22000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("SharedCallerName3");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1003);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1003.toString(), "22000", STATUS.ANSWER.toString(), CDRNAME.Extension_1003.toString() + " hung up", "", SPS, "Outbound", "SharedCallerID3"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("SPS")
    @Description("编辑sps外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "10.分机1004通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t检查来电显示为SharedCallerName3，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID3")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","SPS"})
    public void testTrunkOutCallerID_10_SPS() {
        prerequisite();
        apiUtil.editSpsTrunk(SPS,String.format("\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName1\",\"outbound_cid\":\"SharedCallerID1\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName3\",\"outbound_cid\":\"SharedCallerID3\",\"assoc_ext_list\":[{\"text\":\"testa D\",\"text2\":\"1003\",\"value\":\"%s\",\"type\":\"extension\"},{\"text\":\"t estX\",\"text2\":\"1004\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":2,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName4\",\"outbound_cid\":\"SharedCallerID4\",\"assoc_ext_list\":[{\"text\":\"ExGroup2\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":3,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName5\",\"outbound_cid\":\"SharedCallerID5\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":4,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeNameXXX\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1000-1001\",\"pos\":5,\"outbound_cid\":\"\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionSummary("1004").id,apiUtil.getExtensionGroupSummary("ExGroup2").id,apiUtil.getExtensionSummary("1000").id)).apply();

        step("[caller] 1004" + ",[callee] 22000");
        pjsip.Pj_Make_Call_No_Answer(1004, "22000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("SharedCallerName3");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1004);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1004.toString(), "22000", STATUS.ANSWER.toString(), CDRNAME.Extension_1004.toString() + " hung up", "", SPS, "Outbound", "SharedCallerID3"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("SPS")
    @Description("编辑sps外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "11.分机1005通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t检查来电显示为DOD，接听，挂断；检查cdr,Outbound Caller ID: spsOuntCid ")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","SPS"})
    public void testTrunkOutCallerID_11_SPS() {
        prerequisite();
        apiUtil.editSpsTrunk(SPS,String.format("\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName1\",\"outbound_cid\":\"SharedCallerID1\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName3\",\"outbound_cid\":\"SharedCallerID3\",\"assoc_ext_list\":[{\"text\":\"testa D\",\"text2\":\"1003\",\"value\":\"%s\",\"type\":\"extension\"},{\"text\":\"t estX\",\"text2\":\"1004\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":2,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName4\",\"outbound_cid\":\"SharedCallerID4\",\"assoc_ext_list\":[{\"text\":\"ExGroup2\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":3,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName5\",\"outbound_cid\":\"SharedCallerID5\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":4,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeNameXXX\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1000-1001\",\"pos\":5,\"outbound_cid\":\"\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionSummary("1004").id,apiUtil.getExtensionGroupSummary("ExGroup2").id,apiUtil.getExtensionSummary("1000").id)).apply();

        step("[caller] 1005" + ",[callee] 22000");
        pjsip.Pj_Make_Call_No_Answer(1005, "22000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("DOD");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1005);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1005.toString(), "22000", STATUS.ANSWER.toString(), CDRNAME.Extension_1005.toString() + " hung up", "", SPS, "Outbound", "spsOuntCid"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("SPS")
    @Description("编辑sps外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "12.编辑Outbound Caller ID List 的顺序：Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000 调整到第一条\n" +
            "\t分机1000通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t\t检查来电显示为SharedCallerName5，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID5")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","SPS"})
    public void testTrunkOutCallerID_12_SPS() {
        prerequisite();
        apiUtil.editSpsTrunk(SPS,String.format("\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName1\",\"outbound_cid\":\"SharedCallerID1\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":4,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName3\",\"outbound_cid\":\"SharedCallerID3\",\"assoc_ext_list\":[{\"text\":\"testa D\",\"text2\":\"1003\",\"value\":\"%s\",\"type\":\"extension\"},{\"text\":\"t estX\",\"text2\":\"1004\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":2,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName4\",\"outbound_cid\":\"SharedCallerID4\",\"assoc_ext_list\":[{\"text\":\"ExGroup2\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":3,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName5\",\"outbound_cid\":\"SharedCallerID5\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeNameXXX\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1000-1001\",\"pos\":5,\"outbound_cid\":\"\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionSummary("1004").id,apiUtil.getExtensionGroupSummary("ExGroup2").id,apiUtil.getExtensionSummary("1000").id)).apply();

        step("[caller] 1000" + ",[callee] 22000");
        pjsip.Pj_Make_Call_No_Answer(1000, "22000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("SharedCallerName5");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "22000", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound", "SharedCallerID5"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("SPS")
    @Description("编辑sps外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "13.编辑Outbound Caller ID List 的顺序：,Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；调整到第一条\n" +
            "\t分机1000通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t\t检查来电显示为SharedCallerName4，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID4")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","SPS"})
    public void testTrunkOutCallerID_13_SPS() {
        prerequisite();
        apiUtil.editSpsTrunk(SPS,String.format("\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName1\",\"outbound_cid\":\"SharedCallerID1\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":3,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName3\",\"outbound_cid\":\"SharedCallerID3\",\"assoc_ext_list\":[{\"text\":\"testa D\",\"text2\":\"1003\",\"value\":\"%s\",\"type\":\"extension\"},{\"text\":\"t estX\",\"text2\":\"1004\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":2,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName4\",\"outbound_cid\":\"SharedCallerID4\",\"assoc_ext_list\":[{\"text\":\"ExGroup2\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName5\",\"outbound_cid\":\"SharedCallerID5\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":4,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeNameXXX\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1000-1001\",\"pos\":5,\"outbound_cid\":\"\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionSummary("1004").id,apiUtil.getExtensionGroupSummary("ExGroup2").id,apiUtil.getExtensionSummary("1000").id)).apply();

        step("[caller] 1000" + ",[callee] 22000");
        pjsip.Pj_Make_Call_No_Answer(1000, "22000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("SharedCallerName4");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "22000", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound", "SharedCallerID4"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("SPS")
    @Description("编辑sps外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "14.编辑Outbound Caller ID List 的顺序：5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX  调整到第一条\n" +
            "\t分机1000通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t\t检查来电显示为RangeNameXXX，接听，挂断；检查cdr,Outbound Caller ID: +5503301")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","SPS"})
    public void testTrunkOutCallerID_14_SPS() {
        prerequisite();
        apiUtil.editSpsTrunk(SPS,String.format("\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName1\",\"outbound_cid\":\"SharedCallerID1\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":5,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName3\",\"outbound_cid\":\"SharedCallerID3\",\"assoc_ext_list\":[{\"text\":\"testa D\",\"text2\":\"1003\",\"value\":\"%s\",\"type\":\"extension\"},{\"text\":\"t estX\",\"text2\":\"1004\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":2,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName4\",\"outbound_cid\":\"SharedCallerID4\",\"assoc_ext_list\":[{\"text\":\"ExGroup2\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":3,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName5\",\"outbound_cid\":\"SharedCallerID5\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":4,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeNameXXX\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1000-1001\",\"pos\":1,\"outbound_cid\":\"\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionSummary("1004").id,apiUtil.getExtensionGroupSummary("ExGroup2").id,apiUtil.getExtensionSummary("1000").id)).apply();

        step("[caller] 1000" + ",[callee] 22000");
        pjsip.Pj_Make_Call_No_Answer(1000, "22000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("RangeNameXXX");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "22000", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", SPS, "Outbound", "+5503301"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("BRI")
    @Description("Outbound Caller ID: briOuntCid ,Outbound Caller ID Name:DOD,Outbound Caller ID List 为空\n" +
            "\t15.分机1000通过bri外线呼出，辅助2分机2000响铃\n" +
            "\t\t检查来电显示为DOD，接听，挂断；检查cdr,Outbound Caller ID: briOuntCid")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【ID1036056】【P系列】【自动化】 呼出路由 E1/BRI CDR 被叫显示异常")
    @Test(groups = {"PSeries","P2",  "Trunk-OutboundCallerID","BRI"})
    public void testTrunkOutCallerID_15_BRI() {
        prerequisite();
        apiUtil.editBriTrunk(BRI_1,"\"def_outbound_cid\":\"briOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[]").apply();

        step("[caller] 1000" + ",[callee] 52000");
        pjsip.Pj_Make_Call_No_Answer(1000, "52000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "52000", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", BRI_1, "Outbound", "briOuntCid"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("BRI")
    @Description("编辑BRI外线-》Outbound Caller ID" +
            "Outbound Caller ID: 空 ,Outbound Caller ID Name:空,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",Outbound Caller ID: SharedCallerID123,Outbound Caller ID Name:SharedCallerName, 分机选择：Default_Extension_Group\n" +
            "\t2.分机1000通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t\t检查来电显示为SharedCallerName，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID123\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【ID1036056】【P系列】【自动化】 呼出路由 E1/BRI CDR 被叫显示异常")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","BRI"})
    public void testTrunkOutCallerID_16_BRI() {
        prerequisite();
        apiUtil.editBriTrunk(BRI_1,String.format("\"def_outbound_cid\":\"\",\"def_outbound_cid_name\":\"\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName\",\"outbound_cid\":\"SharedCallerID123\",\"assoc_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"}]",apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).apply();

        step("[caller] 1000" + ",[callee] 52000");
        pjsip.Pj_Make_Call_No_Answer(1000, "52000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("SharedCallerName");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "22000", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", BRI_1, "Outbound", "SharedCallerID123"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("BRI")
    @Description("编辑BRI外线-》Outbound Caller ID" +
            "Outbound Caller ID: 空 ,Outbound Caller ID Name:空,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",Outbound Caller ID: SharedCallerID123,Outbound Caller ID Name:SharedCallerName, 分机选择：Default_Extension_Group\n" +
            "\t3.分机1001通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t\t检查来电显示为SharedCallerName，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID123")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【ID1036056】【P系列】【自动化】 呼出路由 E1/BRI CDR 被叫显示异常")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","BRI"})
    public void testTrunkOutCallerID_17_BRI() {
        prerequisite();
        apiUtil.editBriTrunk(BRI_1,String.format("\"def_outbound_cid\":\"\",\"def_outbound_cid_name\":\"\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName\",\"outbound_cid\":\"SharedCallerID123\",\"assoc_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"}]",apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).apply();

        step("[caller] 1001" + ",[callee] 52000");
        pjsip.Pj_Make_Call_No_Answer(1001, "52000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("SharedCallerName");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "22000", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", BRI_1, "Outbound", "SharedCallerID123"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("BRI")
    @Description("编辑BRI外线-》Outbound Caller ID" +
            "Outbound Caller ID: 空 ,Outbound Caller ID Name：空,\n" +
            "Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1001-1002\n" +
            "Outbound Caller ID Name：RanggeName1\n" +
            "\t4.分机1001通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t\t检查来电显示为RanggeName1，接听，挂断；检查cdr,Outbound Caller ID: +5503301\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【ID1036056】【P系列】【自动化】 呼出路由 E1/BRI CDR 被叫显示异常")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","BRI"})
    public void testTrunkOutCallerID_18_BRI() {
        prerequisite();
        apiUtil.editBriTrunk(BRI_1,"\"def_outbound_cid\":\"\",\"def_outbound_cid_name\":\"\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeName1\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1001-1002\",\"pos\":1,\"outbound_cid\":\"\"}]").apply();

        step("[caller] 1001" + ",[callee] 52000");
        pjsip.Pj_Make_Call_No_Answer(1001, "52000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("RangeName1");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "22000", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", BRI_1, "Outbound", "+5503301"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("BRI")
    @Description("编辑BRI外线-》Outbound Caller ID" +
            "Outbound Caller ID: 空 ,Outbound Caller ID Name：空,\n" +
            "Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1001-1002\n" +
            "Outbound Caller ID Name：RanggeName1\n" +
            "\t5.分机1002通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t\t检查来电显示为RanggeName1，接听，挂断；检查cdr,Outbound Caller ID: +5503302")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【ID1036056】【P系列】【自动化】 呼出路由 E1/BRI CDR 被叫显示异常")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","BRI"})
    public void testTrunkOutCallerID_19_BRI() {
        prerequisite();
        apiUtil.editBriTrunk(BRI_1,"\"def_outbound_cid\":\"\",\"def_outbound_cid_name\":\"\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeName1\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1001-1002\",\"pos\":1,\"outbound_cid\":\"\"}]").apply();

        step("[caller] 1002" + ",[callee] 52000");
        pjsip.Pj_Make_Call_No_Answer(1002, "52000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("RangeName1");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1002);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1002.toString(), "22000", STATUS.ANSWER.toString(), CDRNAME.Extension_1002.toString() + " hung up", "", BRI_1, "Outbound", "+5503302"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("BRI")
    @Description("编辑BRI外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "6.分机1000通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t检查来电显示为SharedCallerName1，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID1")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","BRI"})
    public void testTrunkOutCallerID_20_BRI() {
        prerequisite();
        apiUtil.editBriTrunk(BRI_1,String.format("\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName1\",\"outbound_cid\":\"SharedCallerID1\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName3\",\"outbound_cid\":\"SharedCallerID3\",\"assoc_ext_list\":[{\"text\":\"testa D\",\"text2\":\"1003\",\"value\":\"%s\",\"type\":\"extension\"},{\"text\":\"t estX\",\"text2\":\"1004\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":2,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName4\",\"outbound_cid\":\"SharedCallerID4\",\"assoc_ext_list\":[{\"text\":\"ExGroup2\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":3,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName5\",\"outbound_cid\":\"SharedCallerID5\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":4,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeNameXXX\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1000-1001\",\"pos\":5,\"outbound_cid\":\"\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionSummary("1004").id,apiUtil.getExtensionGroupSummary("ExGroup2").id,apiUtil.getExtensionSummary("1000").id)).apply();

        step("[caller] 1000" + ",[callee] 52000");
        pjsip.Pj_Make_Call_No_Answer(1000, "52000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("SharedCallerName1");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "22000", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", BRI_1, "Outbound", "SharedCallerID1"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("BRI")
    @Description("编辑BRI外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "7.分机1001通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t检查来电显示为RangeNameXXX，接听，挂断；检查cdr,Outbound Caller ID: +5503302")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","BRI"})
    public void testTrunkOutCallerID_21_BRI() {
        prerequisite();
        apiUtil.editBriTrunk(BRI_1,String.format("\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName1\",\"outbound_cid\":\"SharedCallerID1\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName3\",\"outbound_cid\":\"SharedCallerID3\",\"assoc_ext_list\":[{\"text\":\"testa D\",\"text2\":\"1003\",\"value\":\"%s\",\"type\":\"extension\"},{\"text\":\"t estX\",\"text2\":\"1004\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":2,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName4\",\"outbound_cid\":\"SharedCallerID4\",\"assoc_ext_list\":[{\"text\":\"ExGroup2\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":3,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName5\",\"outbound_cid\":\"SharedCallerID5\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":4,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeNameXXX\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1000-1001\",\"pos\":5,\"outbound_cid\":\"\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionSummary("1004").id,apiUtil.getExtensionGroupSummary("ExGroup2").id,apiUtil.getExtensionSummary("1000").id)).apply();

        step("[caller] 1001" + ",[callee] 52000");
        pjsip.Pj_Make_Call_No_Answer(1001, "52000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("RangeNameXXX");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "22000", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", BRI_1, "Outbound", "+5503302"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("BRI")
    @Description("编辑BRI外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "8.分机1002通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t检查来电显示为SharedCallerName4，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID4")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","BRI"})
    public void testTrunkOutCallerID_22_BRI() {
        prerequisite();
        apiUtil.editBriTrunk(BRI_1,String.format("\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName1\",\"outbound_cid\":\"SharedCallerID1\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName3\",\"outbound_cid\":\"SharedCallerID3\",\"assoc_ext_list\":[{\"text\":\"testa D\",\"text2\":\"1003\",\"value\":\"%s\",\"type\":\"extension\"},{\"text\":\"t estX\",\"text2\":\"1004\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":2,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName4\",\"outbound_cid\":\"SharedCallerID4\",\"assoc_ext_list\":[{\"text\":\"ExGroup2\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":3,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName5\",\"outbound_cid\":\"SharedCallerID5\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":4,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeNameXXX\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1000-1001\",\"pos\":5,\"outbound_cid\":\"\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionSummary("1004").id,apiUtil.getExtensionGroupSummary("ExGroup2").id,apiUtil.getExtensionSummary("1000").id)).apply();

        step("[caller] 1002" + ",[callee] 52000");
        pjsip.Pj_Make_Call_No_Answer(1002, "52000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("SharedCallerName4");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1002);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1002.toString(), "22000", STATUS.ANSWER.toString(), CDRNAME.Extension_1002.toString() + " hung up", "", BRI_1, "Outbound", "SharedCallerID4"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("BRI")
    @Description("编辑BRI外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "9.分机1003通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t检查来电显示为SharedCallerName3，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID3")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","BRI"})
    public void testTrunkOutCallerID_23_BRI() {
        prerequisite();
        apiUtil.editBriTrunk(BRI_1,String.format("\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName1\",\"outbound_cid\":\"SharedCallerID1\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName3\",\"outbound_cid\":\"SharedCallerID3\",\"assoc_ext_list\":[{\"text\":\"testa D\",\"text2\":\"1003\",\"value\":\"%s\",\"type\":\"extension\"},{\"text\":\"t estX\",\"text2\":\"1004\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":2,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName4\",\"outbound_cid\":\"SharedCallerID4\",\"assoc_ext_list\":[{\"text\":\"ExGroup2\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":3,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName5\",\"outbound_cid\":\"SharedCallerID5\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":4,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeNameXXX\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1000-1001\",\"pos\":5,\"outbound_cid\":\"\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionSummary("1004").id,apiUtil.getExtensionGroupSummary("ExGroup2").id,apiUtil.getExtensionSummary("1000").id)).apply();

        step("[caller] 1003" + ",[callee] 52000");
        pjsip.Pj_Make_Call_No_Answer(1003, "52000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("SharedCallerName3");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1003);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1003.toString(), "22000", STATUS.ANSWER.toString(), CDRNAME.Extension_1003.toString() + " hung up", "", BRI_1, "Outbound", "SharedCallerID3"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("BRI")
    @Description("编辑BRI外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "10.分机1004通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t检查来电显示为SharedCallerName3，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID3")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","BRI"})
    public void testTrunkOutCallerID_24_BRI() {
        prerequisite();
        apiUtil.editBriTrunk(BRI_1,String.format("\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName1\",\"outbound_cid\":\"SharedCallerID1\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName3\",\"outbound_cid\":\"SharedCallerID3\",\"assoc_ext_list\":[{\"text\":\"testa D\",\"text2\":\"1003\",\"value\":\"%s\",\"type\":\"extension\"},{\"text\":\"t estX\",\"text2\":\"1004\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":2,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName4\",\"outbound_cid\":\"SharedCallerID4\",\"assoc_ext_list\":[{\"text\":\"ExGroup2\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":3,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName5\",\"outbound_cid\":\"SharedCallerID5\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":4,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeNameXXX\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1000-1001\",\"pos\":5,\"outbound_cid\":\"\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionSummary("1004").id,apiUtil.getExtensionGroupSummary("ExGroup2").id,apiUtil.getExtensionSummary("1000").id)).apply();

        step("[caller] 1004" + ",[callee] 52000");
        pjsip.Pj_Make_Call_No_Answer(1004, "52000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("SharedCallerName3");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1004);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1004.toString(), "22000", STATUS.ANSWER.toString(), CDRNAME.Extension_1004.toString() + " hung up", "", BRI_1, "Outbound", "SharedCallerID3"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("BRI")
    @Description("编辑BRI外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "11.分机1005通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t检查来电显示为DOD，接听，挂断；检查cdr,Outbound Caller ID: spsOuntCid ")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","BRI"})
    public void testTrunkOutCallerID_25_BRI() {
        prerequisite();
        apiUtil.editBriTrunk(BRI_1,String.format("\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName1\",\"outbound_cid\":\"SharedCallerID1\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName3\",\"outbound_cid\":\"SharedCallerID3\",\"assoc_ext_list\":[{\"text\":\"testa D\",\"text2\":\"1003\",\"value\":\"%s\",\"type\":\"extension\"},{\"text\":\"t estX\",\"text2\":\"1004\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":2,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName4\",\"outbound_cid\":\"SharedCallerID4\",\"assoc_ext_list\":[{\"text\":\"ExGroup2\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":3,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName5\",\"outbound_cid\":\"SharedCallerID5\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":4,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeNameXXX\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1000-1001\",\"pos\":5,\"outbound_cid\":\"\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionSummary("1004").id,apiUtil.getExtensionGroupSummary("ExGroup2").id,apiUtil.getExtensionSummary("1000").id)).apply();

        step("[caller] 1005" + ",[callee] 52000");
        pjsip.Pj_Make_Call_No_Answer(1005, "52000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("DOD");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1005);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1005.toString(), "22000", STATUS.ANSWER.toString(), CDRNAME.Extension_1005.toString() + " hung up", "", BRI_1, "Outbound", "spsOuntCid"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("BRI")
    @Description("编辑BRI外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "12.编辑Outbound Caller ID List 的顺序：Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000 调整到第一条\n" +
            "\t分机1000通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t\t检查来电显示为SharedCallerName5，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID5")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","BRI"})
    public void testTrunkOutCallerID_26_BRI() {
        prerequisite();
        apiUtil.editBriTrunk(BRI_1,String.format("\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName1\",\"outbound_cid\":\"SharedCallerID1\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":4,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName3\",\"outbound_cid\":\"SharedCallerID3\",\"assoc_ext_list\":[{\"text\":\"testa D\",\"text2\":\"1003\",\"value\":\"%s\",\"type\":\"extension\"},{\"text\":\"t estX\",\"text2\":\"1004\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":2,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName4\",\"outbound_cid\":\"SharedCallerID4\",\"assoc_ext_list\":[{\"text\":\"ExGroup2\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":3,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName5\",\"outbound_cid\":\"SharedCallerID5\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeNameXXX\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1000-1001\",\"pos\":5,\"outbound_cid\":\"\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionSummary("1004").id,apiUtil.getExtensionGroupSummary("ExGroup2").id,apiUtil.getExtensionSummary("1000").id)).apply();

        step("[caller] 1000" + ",[callee] 52000");
        pjsip.Pj_Make_Call_No_Answer(1000, "52000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("SharedCallerName5");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "22000", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", BRI_1, "Outbound", "SharedCallerID5"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("BRI")
    @Description("编辑BRI外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "13.编辑Outbound Caller ID List 的顺序：,Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；调整到第一条\n" +
            "\t分机1000通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t\t检查来电显示为SharedCallerName4，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID4")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "Trunk-OutboundCallerID","BRI"})
    public void testTrunkOutCallerID_27_BRI() {
        prerequisite();
        apiUtil.editBriTrunk(BRI_1,String.format("\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName1\",\"outbound_cid\":\"SharedCallerID1\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":3,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName3\",\"outbound_cid\":\"SharedCallerID3\",\"assoc_ext_list\":[{\"text\":\"testa D\",\"text2\":\"1003\",\"value\":\"%s\",\"type\":\"extension\"},{\"text\":\"t estX\",\"text2\":\"1004\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":2,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName4\",\"outbound_cid\":\"SharedCallerID4\",\"assoc_ext_list\":[{\"text\":\"ExGroup2\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName5\",\"outbound_cid\":\"SharedCallerID5\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":4,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeNameXXX\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1000-1001\",\"pos\":5,\"outbound_cid\":\"\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionSummary("1004").id,apiUtil.getExtensionGroupSummary("ExGroup2").id,apiUtil.getExtensionSummary("1000").id)).apply();

        step("[caller] 1000" + ",[callee] 52000");
        pjsip.Pj_Make_Call_No_Answer(1000, "52000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("SharedCallerName4");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "22000", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", BRI_1, "Outbound", "SharedCallerID4"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("BRI")
    @Description("编辑BRI外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "14.编辑Outbound Caller ID List 的顺序：5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX  调整到第一条\n" +
            "\t分机1000通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t\t检查来电显示为RangeNameXXX，接听，挂断；检查cdr,Outbound Caller ID: +5503301")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3", "Trunk-OutboundCallerID","BRI"})
    public void testTrunkOutCallerID_28_BRI() {
        prerequisite();
        apiUtil.editBriTrunk(BRI_1,String.format("\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName1\",\"outbound_cid\":\"SharedCallerID1\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":5,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName3\",\"outbound_cid\":\"SharedCallerID3\",\"assoc_ext_list\":[{\"text\":\"testa D\",\"text2\":\"1003\",\"value\":\"%s\",\"type\":\"extension\"},{\"text\":\"t estX\",\"text2\":\"1004\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":2,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName4\",\"outbound_cid\":\"SharedCallerID4\",\"assoc_ext_list\":[{\"text\":\"ExGroup2\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":3,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName5\",\"outbound_cid\":\"SharedCallerID5\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":4,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeNameXXX\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1000-1001\",\"pos\":1,\"outbound_cid\":\"\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionSummary("1004").id,apiUtil.getExtensionGroupSummary("ExGroup2").id,apiUtil.getExtensionSummary("1000").id)).apply();

        step("[caller] 1000" + ",[callee] 52000");
        pjsip.Pj_Make_Call_No_Answer(1000, "52000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("RangeNameXXX");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "22000", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", BRI_1, "Outbound", "+5503301"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("E1")
    @Description("编辑E1外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,Outbound Caller ID List 为空\n" +
            "\t1.分机1000通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t\t检查来电显示为DOD，接听，挂断；检查cdr,Outbound Caller ID: spsOuntCid\n" +
            "\t前置条件的默认值设置为这个，跑完其它的修改回去")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","E1"})
    public void testTrunkOutCallerID_29_E1() {
        prerequisite();
        apiUtil.editDigitalTrunk(E1,"\"def_outbound_cid\":\"E1OuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[]").apply();

        step("[caller] 1000" + ",[callee] 62000");
        pjsip.Pj_Make_Call_No_Answer(1000, "62000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "62000", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "",E1, "Outbound", "E1OuntCid"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("E1")
    @Description("编辑E1外线-》Outbound Caller ID" +
            "Outbound Caller ID: 空 ,Outbound Caller ID Name:空,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",Outbound Caller ID: SharedCallerID123,Outbound Caller ID Name:SharedCallerName, 分机选择：Default_Extension_Group\n" +
            "\t2.分机1000通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t\t检查来电显示为SharedCallerName，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID123\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【ID1036056】【P系列】【自动化】 呼出路由 E1/BRI CDR 被叫显示异常")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","E1"})
    public void testTrunkOutCallerID_30_E1() {
        prerequisite();
        apiUtil.editDigitalTrunk(E1,String.format("\"def_outbound_cid\":\"\",\"def_outbound_cid_name\":\"\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName\",\"outbound_cid\":\"SharedCallerID123\",\"assoc_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"}]",apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).apply();

        step("[caller] 1000" + ",[callee] 62000");
        pjsip.Pj_Make_Call_No_Answer(1000, "62000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("SharedCallerName");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "62000", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", E1, "Outbound", "SharedCallerID123"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("E1")
    @Description("编辑E1外线-》Outbound Caller ID" +
            "Outbound Caller ID: 空 ,Outbound Caller ID Name:空,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",Outbound Caller ID: SharedCallerID123,Outbound Caller ID Name:SharedCallerName, 分机选择：Default_Extension_Group\n" +
            "\t3.分机1001通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t\t检查来电显示为SharedCallerName，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID123")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【ID1036056】【P系列】【自动化】 呼出路由 E1/BRI CDR 被叫显示异常")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","E1"})
    public void testTrunkOutCallerID_31_E1() {
        prerequisite();
        apiUtil.editDigitalTrunk(E1,String.format("\"def_outbound_cid\":\"\",\"def_outbound_cid_name\":\"\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName\",\"outbound_cid\":\"SharedCallerID123\",\"assoc_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"}]",apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).apply();

        step("[caller] 1001" + ",[callee] 62000");
        pjsip.Pj_Make_Call_No_Answer(1001, "62000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("SharedCallerName");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "62000", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", E1, "Outbound", "SharedCallerID123"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("E1")
    @Description("编辑E1外线-》Outbound Caller ID" +
            "Outbound Caller ID: 空 ,Outbound Caller ID Name：空,\n" +
            "Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1001-1002\n" +
            "Outbound Caller ID Name：RanggeName1\n" +
            "\t4.分机1001通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t\t检查来电显示为RanggeName1，接听，挂断；检查cdr,Outbound Caller ID: +5503301\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【ID1036056】【P系列】【自动化】 呼出路由 E1/BRI CDR 被叫显示异常")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","E1"})
    public void testTrunkOutCallerID_32_E1() {
        prerequisite();
        apiUtil.editDigitalTrunk(E1,"\"def_outbound_cid\":\"\",\"def_outbound_cid_name\":\"\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeName1\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1001-1002\",\"pos\":1,\"outbound_cid\":\"\"}]").apply();

        step("[caller] 1001" + ",[callee] 62000");
        pjsip.Pj_Make_Call_No_Answer(1001, "62000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("RangeName1");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "62000", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", E1, "Outbound", "+5503301"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("E1")
    @Description("编辑E1外线-》Outbound Caller ID" +
            "Outbound Caller ID: 空 ,Outbound Caller ID Name：空,\n" +
            "Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1001-1002\n" +
            "Outbound Caller ID Name：RanggeName1\n" +
            "\t5.分机1002通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t\t检查来电显示为RanggeName1，接听，挂断；检查cdr,Outbound Caller ID: +5503302")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【ID1036056】【P系列】【自动化】 呼出路由 E1/BRI CDR 被叫显示异常")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","E1"})
    public void testTrunkOutCallerID_33_E1() {
        prerequisite();
        apiUtil.editDigitalTrunk(E1,"\"def_outbound_cid\":\"\",\"def_outbound_cid_name\":\"\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeName1\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1001-1002\",\"pos\":1,\"outbound_cid\":\"\"}]").apply();

        step("[caller] 1002" + ",[callee] 62000");
        pjsip.Pj_Make_Call_No_Answer(1002, "62000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("RangeName1");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1002);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1002.toString(), "62000", STATUS.ANSWER.toString(), CDRNAME.Extension_1002.toString() + " hung up", "", E1, "Outbound", "+5503302"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("E1")
    @Description("编辑E1外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "6.分机1000通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t检查来电显示为SharedCallerName1，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID1")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【ID1036056】【P系列】【自动化】 呼出路由 E1/BRI CDR 被叫显示异常")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","E1"})
    public void testTrunkOutCallerID_34_E1() {
        prerequisite();
        apiUtil.editDigitalTrunk(E1,String.format("\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName1\",\"outbound_cid\":\"SharedCallerID1\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName3\",\"outbound_cid\":\"SharedCallerID3\",\"assoc_ext_list\":[{\"text\":\"testa D\",\"text2\":\"1003\",\"value\":\"%s\",\"type\":\"extension\"},{\"text\":\"t estX\",\"text2\":\"1004\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":2,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName4\",\"outbound_cid\":\"SharedCallerID4\",\"assoc_ext_list\":[{\"text\":\"ExGroup2\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":3,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName5\",\"outbound_cid\":\"SharedCallerID5\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":4,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeNameXXX\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1000-1001\",\"pos\":5,\"outbound_cid\":\"\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionSummary("1004").id,apiUtil.getExtensionGroupSummary("ExGroup2").id,apiUtil.getExtensionSummary("1000").id)).apply();

        step("[caller] 1000" + ",[callee] 62000");
        pjsip.Pj_Make_Call_No_Answer(1000, "62000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("SharedCallerName1");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "62000", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", E1, "Outbound", "SharedCallerID1"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("E1")
    @Description("编辑E1外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "7.分机1001通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t检查来电显示为RangeNameXXX，接听，挂断；检查cdr,Outbound Caller ID: +5503302")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【ID1036056】【P系列】【自动化】 呼出路由 E1/BRI CDR 被叫显示异常")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","E1"})
    public void testTrunkOutCallerID_35_E1() {
        prerequisite();
        apiUtil.editDigitalTrunk(E1,String.format("\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName1\",\"outbound_cid\":\"SharedCallerID1\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName3\",\"outbound_cid\":\"SharedCallerID3\",\"assoc_ext_list\":[{\"text\":\"testa D\",\"text2\":\"1003\",\"value\":\"%s\",\"type\":\"extension\"},{\"text\":\"t estX\",\"text2\":\"1004\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":2,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName4\",\"outbound_cid\":\"SharedCallerID4\",\"assoc_ext_list\":[{\"text\":\"ExGroup2\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":3,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName5\",\"outbound_cid\":\"SharedCallerID5\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":4,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeNameXXX\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1000-1001\",\"pos\":5,\"outbound_cid\":\"\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionSummary("1004").id,apiUtil.getExtensionGroupSummary("ExGroup2").id,apiUtil.getExtensionSummary("1000").id)).apply();

        step("[caller] 1001" + ",[callee] 62000");
        pjsip.Pj_Make_Call_No_Answer(1001, "62000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("RangeNameXXX");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "62000", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", E1, "Outbound", "+5503302"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("E1")
    @Description("编辑E1外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "8.分机1002通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t检查来电显示为SharedCallerName4，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID4")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【ID1036056】【P系列】【自动化】 呼出路由 E1/BRI CDR 被叫显示异常")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","E1"})
    public void testTrunkOutCallerID_36_E1() {
        prerequisite();
        apiUtil.editDigitalTrunk(E1,String.format("\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName1\",\"outbound_cid\":\"SharedCallerID1\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName3\",\"outbound_cid\":\"SharedCallerID3\",\"assoc_ext_list\":[{\"text\":\"testa D\",\"text2\":\"1003\",\"value\":\"%s\",\"type\":\"extension\"},{\"text\":\"t estX\",\"text2\":\"1004\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":2,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName4\",\"outbound_cid\":\"SharedCallerID4\",\"assoc_ext_list\":[{\"text\":\"ExGroup2\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":3,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName5\",\"outbound_cid\":\"SharedCallerID5\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":4,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeNameXXX\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1000-1001\",\"pos\":5,\"outbound_cid\":\"\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionSummary("1004").id,apiUtil.getExtensionGroupSummary("ExGroup2").id,apiUtil.getExtensionSummary("1000").id)).apply();

        step("[caller] 1002" + ",[callee] 62000");
        pjsip.Pj_Make_Call_No_Answer(1002, "62000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("SharedCallerName4");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1002);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1002.toString(), "62000", STATUS.ANSWER.toString(), CDRNAME.Extension_1002.toString() + " hung up", "", E1, "Outbound", "SharedCallerID4"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("E1")
    @Description("编辑E1外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "9.分机1003通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t检查来电显示为SharedCallerName3，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID3")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【ID1036056】【P系列】【自动化】 呼出路由 E1/BRI CDR 被叫显示异常")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","E1"})
    public void testTrunkOutCallerID_37_E1() {
        prerequisite();
        apiUtil.editDigitalTrunk(E1,String.format("\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName1\",\"outbound_cid\":\"SharedCallerID1\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName3\",\"outbound_cid\":\"SharedCallerID3\",\"assoc_ext_list\":[{\"text\":\"testa D\",\"text2\":\"1003\",\"value\":\"%s\",\"type\":\"extension\"},{\"text\":\"t estX\",\"text2\":\"1004\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":2,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName4\",\"outbound_cid\":\"SharedCallerID4\",\"assoc_ext_list\":[{\"text\":\"ExGroup2\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":3,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName5\",\"outbound_cid\":\"SharedCallerID5\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":4,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeNameXXX\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1000-1001\",\"pos\":5,\"outbound_cid\":\"\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionSummary("1004").id,apiUtil.getExtensionGroupSummary("ExGroup2").id,apiUtil.getExtensionSummary("1000").id)).apply();

        step("[caller] 1003" + ",[callee] 62000");
        pjsip.Pj_Make_Call_No_Answer(1003, "62000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("SharedCallerName3");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1003);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1003.toString(), "62000", STATUS.ANSWER.toString(), CDRNAME.Extension_1003.toString() + " hung up", "", E1, "Outbound", "SharedCallerID3"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("E1")
    @Description("编辑E1外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "10.分机1004通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t检查来电显示为SharedCallerName3，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID3")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【ID1036056】【P系列】【自动化】 呼出路由 E1/BRI CDR 被叫显示异常")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","E1"})
    public void testTrunkOutCallerID_38_E1() {
        prerequisite();
        apiUtil.editDigitalTrunk(E1,String.format("\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName1\",\"outbound_cid\":\"SharedCallerID1\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName3\",\"outbound_cid\":\"SharedCallerID3\",\"assoc_ext_list\":[{\"text\":\"testa D\",\"text2\":\"1003\",\"value\":\"%s\",\"type\":\"extension\"},{\"text\":\"t estX\",\"text2\":\"1004\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":2,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName4\",\"outbound_cid\":\"SharedCallerID4\",\"assoc_ext_list\":[{\"text\":\"ExGroup2\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":3,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName5\",\"outbound_cid\":\"SharedCallerID5\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":4,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeNameXXX\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1000-1001\",\"pos\":5,\"outbound_cid\":\"\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionSummary("1004").id,apiUtil.getExtensionGroupSummary("ExGroup2").id,apiUtil.getExtensionSummary("1000").id)).apply();

        step("[caller] 1004" + ",[callee] 62000");
        pjsip.Pj_Make_Call_No_Answer(1004, "62000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("SharedCallerName3");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1004);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1004.toString(), "62000", STATUS.ANSWER.toString(), CDRNAME.Extension_1004.toString() + " hung up", "", E1, "Outbound", "SharedCallerID3"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("E1")
    @Description("编辑E1外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "11.分机1005通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t检查来电显示为DOD，接听，挂断；检查cdr,Outbound Caller ID: spsOuntCid ")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【ID1036056】【P系列】【自动化】 呼出路由 E1/BRI CDR 被叫显示异常")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","E1"})
    public void testTrunkOutCallerID_39_E1() {
        prerequisite();
        apiUtil.editDigitalTrunk(E1,String.format("\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName1\",\"outbound_cid\":\"SharedCallerID1\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName3\",\"outbound_cid\":\"SharedCallerID3\",\"assoc_ext_list\":[{\"text\":\"testa D\",\"text2\":\"1003\",\"value\":\"%s\",\"type\":\"extension\"},{\"text\":\"t estX\",\"text2\":\"1004\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":2,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName4\",\"outbound_cid\":\"SharedCallerID4\",\"assoc_ext_list\":[{\"text\":\"ExGroup2\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":3,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName5\",\"outbound_cid\":\"SharedCallerID5\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":4,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeNameXXX\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1000-1001\",\"pos\":5,\"outbound_cid\":\"\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionSummary("1004").id,apiUtil.getExtensionGroupSummary("ExGroup2").id,apiUtil.getExtensionSummary("1000").id)).apply();

        step("[caller] 1005" + ",[callee] 62000");
        pjsip.Pj_Make_Call_No_Answer(1005, "62000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("DOD");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1005);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1005.toString(), "62000", STATUS.ANSWER.toString(), CDRNAME.Extension_1005.toString() + " hung up", "", E1, "Outbound", "spsOuntCid"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("E1")
    @Description("编辑E1外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "12.编辑Outbound Caller ID List 的顺序：Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000 调整到第一条\n" +
            "\t分机1000通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t\t检查来电显示为SharedCallerName5，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID5")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【ID1036056】【P系列】【自动化】 呼出路由 E1/BRI CDR 被叫显示异常")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","E1"})
    public void testTrunkOutCallerID_40_E1() {
        prerequisite();
        apiUtil.editDigitalTrunk(E1,String.format("\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName1\",\"outbound_cid\":\"SharedCallerID1\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":4,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName3\",\"outbound_cid\":\"SharedCallerID3\",\"assoc_ext_list\":[{\"text\":\"testa D\",\"text2\":\"1003\",\"value\":\"%s\",\"type\":\"extension\"},{\"text\":\"t estX\",\"text2\":\"1004\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":2,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName4\",\"outbound_cid\":\"SharedCallerID4\",\"assoc_ext_list\":[{\"text\":\"ExGroup2\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":3,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName5\",\"outbound_cid\":\"SharedCallerID5\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeNameXXX\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1000-1001\",\"pos\":5,\"outbound_cid\":\"\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionSummary("1004").id,apiUtil.getExtensionGroupSummary("ExGroup2").id,apiUtil.getExtensionSummary("1000").id)).apply();

        step("[caller] 1000" + ",[callee] 62000");
        pjsip.Pj_Make_Call_No_Answer(1000, "62000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("SharedCallerName5");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "62000", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", E1, "Outbound", "SharedCallerID5"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("E1")
    @Description("编辑E1外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "13.编辑Outbound Caller ID List 的顺序：,Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；调整到第一条\n" +
            "\t分机1000通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t\t检查来电显示为SharedCallerName4，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID4")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【ID1036056】【P系列】【自动化】 呼出路由 E1/BRI CDR 被叫显示异常")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","E1"})
    public void testTrunkOutCallerID_41_E1() {
        prerequisite();
        apiUtil.editDigitalTrunk(E1,String.format("\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName1\",\"outbound_cid\":\"SharedCallerID1\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":3,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName3\",\"outbound_cid\":\"SharedCallerID3\",\"assoc_ext_list\":[{\"text\":\"testa D\",\"text2\":\"1003\",\"value\":\"%s\",\"type\":\"extension\"},{\"text\":\"t estX\",\"text2\":\"1004\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":2,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName4\",\"outbound_cid\":\"SharedCallerID4\",\"assoc_ext_list\":[{\"text\":\"ExGroup2\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName5\",\"outbound_cid\":\"SharedCallerID5\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":4,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeNameXXX\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1000-1001\",\"pos\":5,\"outbound_cid\":\"\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionSummary("1004").id,apiUtil.getExtensionGroupSummary("ExGroup2").id,apiUtil.getExtensionSummary("1000").id)).apply();

        step("[caller] 1000" + ",[callee] 62000");
        pjsip.Pj_Make_Call_No_Answer(1000, "62000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("SharedCallerName4");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "62000", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", E1, "Outbound", "SharedCallerID4"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("E1")
    @Description("编辑E1外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "14.编辑Outbound Caller ID List 的顺序：5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX  调整到第一条\n" +
            "\t分机1000通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t\t检查来电显示为RangeNameXXX，接听，挂断；检查cdr,Outbound Caller ID: +5503301")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("【ID1036056】【P系列】【自动化】 呼出路由 E1/BRI CDR 被叫显示异常")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","E1"})
    public void testTrunkOutCallerID_42_E1() {
        prerequisite();
        apiUtil.editDigitalTrunk(E1,String.format("\"def_outbound_cid\":\"spsOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName1\",\"outbound_cid\":\"SharedCallerID1\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":5,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName3\",\"outbound_cid\":\"SharedCallerID3\",\"assoc_ext_list\":[{\"text\":\"testa D\",\"text2\":\"1003\",\"value\":\"%s\",\"type\":\"extension\"},{\"text\":\"t estX\",\"text2\":\"1004\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":2,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName4\",\"outbound_cid\":\"SharedCallerID4\",\"assoc_ext_list\":[{\"text\":\"ExGroup2\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":3,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName5\",\"outbound_cid\":\"SharedCallerID5\",\"assoc_ext_list\":[{\"text\":\"test A\",\"text2\":\"1000\",\"value\":\"%s\",\"type\":\"extension\"}],\"pos\":4,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"},{\"outbound_cid_type\":\"range\",\"outbound_cid_name\":\"RangeNameXXX\",\"outbound_cid_start\":\"+5503301\",\"outbound_cid_end\":\"+5503302\",\"assoc_exts\":\"1000-1001\",\"pos\":1,\"outbound_cid\":\"\"}]",apiUtil.getExtensionSummary("1000").id,apiUtil.getExtensionSummary("1003").id,apiUtil.getExtensionSummary("1004").id,apiUtil.getExtensionGroupSummary("ExGroup2").id,apiUtil.getExtensionSummary("1000").id)).apply();

        step("[caller] 1000" + ",[callee] 62000");
        pjsip.Pj_Make_Call_No_Answer(1000, "62000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(2000, false);
        softAssertPlus.assertThat(pjsip.getAccount(2000).callerId).as("来显检查").isEqualTo("RangeNameXXX");
        assertThat(getExtensionStatus(2000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "62000", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", E1, "Outbound", "+5503301"));

        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("Account")
    @Description("编辑Account外线-》Outbound Caller ID" +
            "Outbound Caller ID: AccountOuntCid ,Outbound Caller ID Name:DOD,Outbound Caller ID List 为空\n" +
            "\t1.分机1000通过Account外线呼出，辅助2分机2000响铃\n" +
            "\t\t检查来电显示为DOD，接听，挂断；检查cdr,Outbound Caller ID: AccountOuntCid\n" +
            "\t前置条件的默认值设置为这个，跑完其它的修改回去")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P2",  "Trunk-OutboundCallerID","Account"})
    public void testTrunkOutCallerID_43_Account() {
        prerequisite();
        apiUtil.editSpsTrunk(ACCOUNTTRUNK,"\"def_outbound_cid\":\"AccountOuntCid\",\"def_outbound_cid_name\":\"DOD\",\"outbound_cid_list\":[]").apply();

        step("[caller] 1000" + ",[callee] 34000");
        pjsip.Pj_Make_Call_No_Answer(1000, "34000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(4000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(4000, false);
        assertThat(getExtensionStatus(4000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "34000", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", ACCOUNTTRUNK, "Outbound", "AccountOuntCid"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("Account")
    @Description("编辑sps外线-》Outbound Caller ID" +
            "Outbound Caller ID: 空 ,Outbound Caller ID Name:空,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",Outbound Caller ID: SharedCallerID123,Outbound Caller ID Name:SharedCallerName, 分机选择：Default_Extension_Group\n" +
            "\t2.分机1000通过sps外线呼出，辅助2分机2000响铃\n" +
            "\t\t检查来电显示为SharedCallerName，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID123\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","Account"})
    public void testTrunkOutCallerID_44_Account() {
        prerequisite();
        apiUtil.editAccountTrunk(ACCOUNTTRUNK,String.format("\"def_outbound_cid\":\"\",\"def_outbound_cid_name\":\"\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName\",\"outbound_cid\":\"SharedCallerID123\",\"assoc_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"}]",apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).apply();

        step("[caller] 1000" + ",[callee] 34000");
        pjsip.Pj_Make_Call_No_Answer(1000, "34000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(4000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(4000, false);
        softAssertPlus.assertThat(pjsip.getAccount(4000).callerId).as("来显检查").isEqualTo("SharedCallerName");
        assertThat(getExtensionStatus(4000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "34000", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", ACCOUNTTRUNK, "Outbound", "SharedCallerID123"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("Account")
    @Description("编辑sps外线-》Outbound Caller ID" +
            "Outbound Caller ID: 空 ,Outbound Caller ID Name:空,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",Outbound Caller ID: SharedCallerID123,Outbound Caller ID Name:SharedCallerName, 分机选择：Default_Extension_Group\n" +
            "\t3.分机1001通过sps外线呼出，辅助3分机4000响铃\n" +
            "\t\t检查来电显示为SharedCallerName，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID123")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","Account"})
    public void testTrunkOutCallerID_45_Account() {
        prerequisite();
        apiUtil.editAccountTrunk(ACCOUNTTRUNK,String.format("\"def_outbound_cid\":\"\",\"def_outbound_cid_name\":\"\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName\",\"outbound_cid\":\"SharedCallerID123\",\"assoc_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"}]",apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).apply();

        step("[caller] 1001" + ",[callee] 34000");
        pjsip.Pj_Make_Call_No_Answer(1001, "34000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(4000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(4000, false);
        softAssertPlus.assertThat(pjsip.getAccount(4000).callerId).as("来显检查").isEqualTo("SharedCallerName");
        assertThat(getExtensionStatus(4000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "34000", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", ACCOUNTTRUNK, "Outbound", "SharedCallerID123"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("Account")
    @Description("编辑sps外线-》Outbound Caller ID" +
            "Outbound Caller ID: 空 ,Outbound Caller ID Name：空,\n" +
            "Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1001-1002\n" +
            "Outbound Caller ID Name：RanggeName1\n" +
            "\t4.分机1001通过sps外线呼出，辅助3分机4000响铃\n" +
            "\t\t检查来电显示为RanggeName1，接听，挂断；检查cdr,Outbound Caller ID: +5503301\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","Account"})
    public void testTrunkOutCallerID_46_Account() {
        prerequisite();
        apiUtil.editAccountTrunk(ACCOUNTTRUNK,String.format("\"def_outbound_cid\":\"\",\"def_outbound_cid_name\":\"\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName\",\"outbound_cid\":\"SharedCallerID123\",\"assoc_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"}]",apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).apply();

        step("[caller] 1001" + ",[callee] 34000");
        pjsip.Pj_Make_Call_No_Answer(1001, "34000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(4000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(4000, false);
        softAssertPlus.assertThat(pjsip.getAccount(4000).callerId).as("来显检查").isEqualTo("RangeName1");
        assertThat(getExtensionStatus(4000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "34000", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", ACCOUNTTRUNK, "Outbound", "+5503301"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("Account")
    @Description("编辑sps外线-》Outbound Caller ID" +
            "Outbound Caller ID: 空 ,Outbound Caller ID Name：空,\n" +
            "Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1001-1002\n" +
            "Outbound Caller ID Name：RanggeName1\n" +
            "\t5.分机1002通过sps外线呼出，辅助3分机4000响铃\n" +
            "\t\t检查来电显示为RanggeName1，接听，挂断；检查cdr,Outbound Caller ID: +5503302")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","Account"})
    public void testTrunkOutCallerID_47_Account() {
        prerequisite();
        apiUtil.editAccountTrunk(ACCOUNTTRUNK,String.format("\"def_outbound_cid\":\"\",\"def_outbound_cid_name\":\"\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName\",\"outbound_cid\":\"SharedCallerID123\",\"assoc_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"}]",apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).apply();

        step("[caller] 1002" + ",[callee] 34000");
        pjsip.Pj_Make_Call_No_Answer(1002, "34000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(4000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(4000, false);
        softAssertPlus.assertThat(pjsip.getAccount(4000).callerId).as("来显检查").isEqualTo("RangeName1");
        assertThat(getExtensionStatus(4000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1002);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1002.toString(), "34000", STATUS.ANSWER.toString(), CDRNAME.Extension_1002.toString() + " hung up", "", ACCOUNTTRUNK, "Outbound", "+5503302"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("Account")
    @Description("编辑sps外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "6.分机1000通过sps外线呼出，辅助3分机4000响铃\n" +
            "\t检查来电显示为SharedCallerName1，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID1")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","Account"})
    public void testTrunkOutCallerID_48_Account() {
        prerequisite();
        apiUtil.editAccountTrunk(ACCOUNTTRUNK,String.format("\"def_outbound_cid\":\"\",\"def_outbound_cid_name\":\"\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName\",\"outbound_cid\":\"SharedCallerID123\",\"assoc_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"}]",apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).apply();

        step("[caller] 1000" + ",[callee] 34000");
        pjsip.Pj_Make_Call_No_Answer(1000, "34000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(4000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(4000, false);
        softAssertPlus.assertThat(pjsip.getAccount(4000).callerId).as("来显检查").isEqualTo("SharedCallerName1");
        assertThat(getExtensionStatus(4000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "34000", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", ACCOUNTTRUNK, "Outbound", "SharedCallerID1"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("Account")
    @Description("编辑sps外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "7.分机1001通过sps外线呼出，辅助3分机4000响铃\n" +
            "\t检查来电显示为RangeNameXXX，接听，挂断；检查cdr,Outbound Caller ID: +5503302")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","Account"})
    public void testTrunkOutCallerID_49_Account() {
        prerequisite();
        apiUtil.editAccountTrunk(ACCOUNTTRUNK,String.format("\"def_outbound_cid\":\"\",\"def_outbound_cid_name\":\"\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName\",\"outbound_cid\":\"SharedCallerID123\",\"assoc_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"}]",apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).apply();

        step("[caller] 1001" + ",[callee] 34000");
        pjsip.Pj_Make_Call_No_Answer(1001, "34000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(4000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(4000, false);
        softAssertPlus.assertThat(pjsip.getAccount(4000).callerId).as("来显检查").isEqualTo("RangeNameXXX");
        assertThat(getExtensionStatus(4000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1001.toString(), "34000", STATUS.ANSWER.toString(), CDRNAME.Extension_1001.toString() + " hung up", "", ACCOUNTTRUNK, "Outbound", "+5503302"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("Account")
    @Description("编辑sps外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "8.分机1002通过sps外线呼出，辅助3分机4000响铃\n" +
            "\t检查来电显示为SharedCallerName4，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID4")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","Account"})
    public void testTrunkOutCallerID_50_Account() {
        prerequisite();
        apiUtil.editAccountTrunk(ACCOUNTTRUNK,String.format("\"def_outbound_cid\":\"\",\"def_outbound_cid_name\":\"\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName\",\"outbound_cid\":\"SharedCallerID123\",\"assoc_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"}]",apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).apply();

        step("[caller] 1002" + ",[callee] 34000");
        pjsip.Pj_Make_Call_No_Answer(1002, "34000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(4000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(4000, false);
        softAssertPlus.assertThat(pjsip.getAccount(4000).callerId).as("来显检查").isEqualTo("SharedCallerName4");
        assertThat(getExtensionStatus(4000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1002);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1002.toString(), "34000", STATUS.ANSWER.toString(), CDRNAME.Extension_1002.toString() + " hung up", "", ACCOUNTTRUNK, "Outbound", "SharedCallerID4"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("Account")
    @Description("编辑sps外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "9.分机1003通过sps外线呼出，辅助3分机4000响铃\n" +
            "\t检查来电显示为SharedCallerName3，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID3")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","Account"})
    public void testTrunkOutCallerID_51_Account() {
        prerequisite();
        apiUtil.editAccountTrunk(ACCOUNTTRUNK,String.format("\"def_outbound_cid\":\"\",\"def_outbound_cid_name\":\"\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName\",\"outbound_cid\":\"SharedCallerID123\",\"assoc_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"}]",apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).apply();

        step("[caller] 1003" + ",[callee] 34000");
        pjsip.Pj_Make_Call_No_Answer(1003, "34000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(4000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(4000, false);
        softAssertPlus.assertThat(pjsip.getAccount(4000).callerId).as("来显检查").isEqualTo("SharedCallerName3");
        assertThat(getExtensionStatus(4000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1003);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1003.toString(), "34000", STATUS.ANSWER.toString(), CDRNAME.Extension_1003.toString() + " hung up", "", ACCOUNTTRUNK, "Outbound", "SharedCallerID3"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("Account")
    @Description("编辑sps外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "10.分机1004通过sps外线呼出，辅助3分机4000响铃\n" +
            "\t检查来电显示为SharedCallerName3，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID3")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","Account"})
    public void testTrunkOutCallerID_52_Account() {
        prerequisite();
        apiUtil.editAccountTrunk(ACCOUNTTRUNK,String.format("\"def_outbound_cid\":\"\",\"def_outbound_cid_name\":\"\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName\",\"outbound_cid\":\"SharedCallerID123\",\"assoc_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"}]",apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).apply();

        step("[caller] 1004" + ",[callee] 34000");
        pjsip.Pj_Make_Call_No_Answer(1004, "34000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(4000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(4000, false);
        softAssertPlus.assertThat(pjsip.getAccount(4000).callerId).as("来显检查").isEqualTo("SharedCallerName3");
        assertThat(getExtensionStatus(4000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1004);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1004.toString(), "34000", STATUS.ANSWER.toString(), CDRNAME.Extension_1004.toString() + " hung up", "", ACCOUNTTRUNK, "Outbound", "SharedCallerID3"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("Account")
    @Description("编辑sps外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "11.分机1005通过sps外线呼出，辅助3分机4000响铃\n" +
            "\t检查来电显示为DOD，接听，挂断；检查cdr,Outbound Caller ID: spsOuntCid ")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","Account"})
    public void testTrunkOutCallerID_53_Account() {
        prerequisite();
        apiUtil.editAccountTrunk(ACCOUNTTRUNK,String.format("\"def_outbound_cid\":\"\",\"def_outbound_cid_name\":\"\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName\",\"outbound_cid\":\"SharedCallerID123\",\"assoc_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"}]",apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).apply();

        step("[caller] 1005" + ",[callee] 34000");
        pjsip.Pj_Make_Call_No_Answer(1005, "34000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(4000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(4000, false);
        softAssertPlus.assertThat(pjsip.getAccount(4000).callerId).as("来显检查").isEqualTo("DOD");
        assertThat(getExtensionStatus(4000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1005);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1005.toString(), "34000", STATUS.ANSWER.toString(), CDRNAME.Extension_1005.toString() + " hung up", "", ACCOUNTTRUNK, "Outbound", "spsOuntCid"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("Account")
    @Description("编辑sps外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "12.编辑Outbound Caller ID List 的顺序：Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000 调整到第一条\n" +
            "\t分机1000通过sps外线呼出，辅助3分机4000响铃\n" +
            "\t\t检查来电显示为SharedCallerName5，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID5")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","Account"})
    public void testTrunkOutCallerID_54_Account() {
        prerequisite();
        apiUtil.editAccountTrunk(ACCOUNTTRUNK,String.format("\"def_outbound_cid\":\"\",\"def_outbound_cid_name\":\"\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName\",\"outbound_cid\":\"SharedCallerID123\",\"assoc_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"}]",apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).apply();

        step("[caller] 1000" + ",[callee] 34000");
        pjsip.Pj_Make_Call_No_Answer(1000, "34000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(4000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(4000, false);
        softAssertPlus.assertThat(pjsip.getAccount(4000).callerId).as("来显检查").isEqualTo("SharedCallerName5");
        assertThat(getExtensionStatus(4000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "34000", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", ACCOUNTTRUNK, "Outbound", "SharedCallerID5"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("Account")
    @Description("编辑sps外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "13.编辑Outbound Caller ID List 的顺序：,Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；调整到第一条\n" +
            "\t分机1000通过sps外线呼出，辅助3分机4000响铃\n" +
            "\t\t检查来电显示为SharedCallerName4，接听，挂断；检查cdr,Outbound Caller ID: SharedCallerID4")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","Account"})
    public void testTrunkOutCallerID_55_Account() {
        prerequisite();
        apiUtil.editAccountTrunk(ACCOUNTTRUNK,String.format("\"def_outbound_cid\":\"\",\"def_outbound_cid_name\":\"\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName\",\"outbound_cid\":\"SharedCallerID123\",\"assoc_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"}]",apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).apply();

        step("[caller] 1000" + ",[callee] 34000");
        pjsip.Pj_Make_Call_No_Answer(1000, "34000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(4000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(4000, false);
        softAssertPlus.assertThat(pjsip.getAccount(4000).callerId).as("来显检查").isEqualTo("SharedCallerName4");
        assertThat(getExtensionStatus(4000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "34000", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", ACCOUNTTRUNK, "Outbound", "SharedCallerID4"));

        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk-OutboundCallerID")
    @Story("Account")
    @Description("编辑sps外线-》Outbound Caller ID" +
            "Outbound Caller ID: spsOuntCid ,Outbound Caller ID Name:DOD,\n" +
            "Outbound Caller ID List : Create Method 选择“Shared Outbound Caller ID\",\n" +
            "分别添加\n" +
            "1)Outbound Caller ID: SharedCallerID1,Outbound Caller ID Name:SharedCallerName1, 分机选择：分机A-1000；\n" +
            "2)Outbound Caller ID: SharedCallerID3,Outbound Caller ID Name:SharedCallerName3, 分机选择：分机D-1003、分机X-1004；\n" +
            "3)Outbound Caller ID: SharedCallerID4,Outbound Caller ID Name:SharedCallerName4, 分机选择：ExGroup2；\n" +
            "4)Outbound Caller ID: SharedCallerID5,Outbound Caller ID Name:SharedCallerName5, 分机选择：分机A-1000；\n" +
            "5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX" +
            "14.编辑Outbound Caller ID List 的顺序：5)Outbound Caller ID List : Create Method 选择“Outbound Caller ID Range”；\n" +
            "Outbound Caller ID Range：+5503301-+5503302\n" +
            "Extension Range：1000-1001\n" +
            "Outbound Caller ID Name：RangeNameXXX  调整到第一条\n" +
            "\t分机1000通过sps外线呼出，辅助3分机4000响铃\n" +
            "\t\t检查来电显示为RangeNameXXX，接听，挂断；检查cdr,Outbound Caller ID: +5503301")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","P3",  "Trunk-OutboundCallerID","Account"})
    public void testTrunkOutCallerID_56_Account() {
        prerequisite();
        apiUtil.editAccountTrunk(ACCOUNTTRUNK,String.format("\"def_outbound_cid\":\"\",\"def_outbound_cid_name\":\"\",\"outbound_cid_list\":[{\"outbound_cid_type\":\"single\",\"outbound_cid_name\":\"SharedCallerName\",\"outbound_cid\":\"SharedCallerID123\",\"assoc_ext_list\":[{\"text\":\"Default_Extension_Group\",\"text2\":\"Extension Group\",\"value\":\"%s\",\"type\":\"ext_group\"}],\"pos\":1,\"outbound_cid_start\":\"\",\"outbound_cid_end\":\"\"}]",apiUtil.getExtensionGroupSummary("Default_Extension_Group").id)).apply();

        step("[caller] 1000" + ",[callee] 34000");
        pjsip.Pj_Make_Call_No_Answer(1000, "34000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(4000, RING, 30)).isEqualTo(RING).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime());
        pjsip.Pj_Answer_Call(4000, false);
        softAssertPlus.assertThat(pjsip.getAccount(4000).callerId).as("来显检查").isEqualTo("RangeNameXXX");
        assertThat(getExtensionStatus(4000, TALKING, 30)).isEqualTo(TALKING).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime());

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType", "dod")
                .contains(tuple(CDRNAME.Extension_1000.toString(), "34000", STATUS.ANSWER.toString(), CDRNAME.Extension_1000.toString() + " hung up", "", ACCOUNTTRUNK, "Outbound", "+5503301"));

        softAssertPlus.assertAll();
    }



}