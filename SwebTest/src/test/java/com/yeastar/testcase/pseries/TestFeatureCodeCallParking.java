package com.yeastar.testcase.pseries;

import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.*;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * @program: SwebTest
 * @description: test feature code call parking
 * @author: huangjx@yeastar.com
 * @create: 2021/01/27
 */
@Log4j2
public class TestFeatureCodeCallParking extends TestCaseBaseNew {
    List<String> trunk9 = new ArrayList<>();
    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();
    List<AsteriskObject> asteriskObjectListSecond = new ArrayList<AsteriskObject>();
    List<AsteriskObject> asteriskObjectListExten = new ArrayList<AsteriskObject>();
    List<AsteriskObject> asteriskObjectListOperator = new ArrayList<AsteriskObject>();
    List<AsteriskObject> asteriskObjectListFailed = new ArrayList<AsteriskObject>();
    List<AsteriskObject> asteriskObjectListGSM = new ArrayList<AsteriskObject>();
    List<AsteriskObject> asteriskObjectListGoogbye = new ArrayList<AsteriskObject>();
    List<String> officeTimes = new ArrayList<>();
    List<String> resetTimes = new ArrayList<>();
    private boolean isRunRecoveryEnvFlag = false;
    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;


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
            initPrompt();
            isRunRecoveryEnvFlag = registerAllExtensions();
            step("=========== init before class  end =========");
        }
        log.debug("[prerequisite time]:" + (System.currentTimeMillis() - startTime) / 1000 + " Seconds");
        //reset test env
        apiUtil.editFeatureCode("\"digit_timeout\": 4000,\"enb_park\": 1,\"park\":\"*5\",\"park_start\":\"6000\",\"park_end\":\"6099\",\"park_timeout\":60,\"enb_park_on_slots\":1,\"park_on_slots\":\"*05\",\"park_timeout_dest\": \"original\"").apply();
        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();
    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("CallParking")
//    @Description("1.通过sps外线呼入到分机A-1000,1000接听\n" +
//            "\t分机A-1000按*5将通话停泊\n" +
//            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；\n" +
//            "\t\t\t分机C-1002拨打6000接回通话\n" +
//            "\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "CallParking",""})
//    public void testCallParking_01_CallParking() {
//        prerequisite();
//
//        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 2000" + ",[callee] 991000");
//        pjsip.Pj_Make_Call_No_Answer(2000, "991000");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        sleep(5000);
//        step("分机A-1000按*5将通话停泊");
//        pjsip.Pj_Send_Dtmf(1000,"*","5");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("[caller] 1002" + ",[callee] 6000");
//        pjsip.Pj_Make_Call_No_Answer(1002, "6000", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(3000);
//        pjsip.Pj_hangupCall(1002);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6000 , testta C<1002> hung up", SPS, "", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at 6000", SPS, "", "Inbound"));
//
//        softAssertPlus.assertAll();
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("CallParking")
//    @Description("2.修改In1的目的地为IVR0\n" +
//            "\t通过SIP外线呼入到IVR0，按0，分机1000响铃，接听\n" +
//            "\t\t分机A-1000按*5将通话停泊\n" +
//            "\t\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；\n" +
//            "\t\t\t\t分机C-1002拨打6000接回通话\n" +
//            "\t\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "CallParking",""})
//    public void testCallParking_02_CallParking() {
//        prerequisite();
//
//        step("修改In1的目的地为IVR0");
//        apiUtil.editInbound("In1",String.format("\"def_dest\":\"ivr\",\"def_dest_value\":\"%s\",\"enb_time_condition\":0",apiUtil.getIVRSummary("6200").id)).apply();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 3001" + ",[callee] 3000");
//        pjsip.Pj_Make_Call_No_Answer(3001, "3000", DEVICE_ASSIST_1, false);
//
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(3001,"0");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机A-1000按*5将通话停泊");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(1000,"*","5");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("[caller] 1002" + ",[callee] 6000");
//        pjsip.Pj_Make_Call_No_Answer(1002, "6000", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(3000);
//        pjsip.Pj_hangupCall(1002);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_3001.toString(), CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6000 , testta C<1002> hung up", SIPTrunk, "", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_3001.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "3001<3001> parked at 6000", SIPTrunk, "", "Inbound"));
//
//        softAssertPlus.assertAll();
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("CallParking")
//    @Description("3.修改In1的目的地为RingGroup0\n" +
//            "\t通过Account外线呼入到RingGroup0,1000、1001、1003同时响铃，1003接听\n" +
//            "\t\t分机1003按*5将通话停泊\n" +
//            "\t\t\tasterisk播放提示音call-parked-at.slin，一会儿后1003自动挂断，外线保持通话状态；\n" +
//            "\t\t\t\t分机D-1003拨打6000接回通话\n" +
//            "\t\t\t\t\t分机D与外线正常通话，外线挂断，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "CallParking",""})
//    public void testCallParking_03_CallParking() {
//        prerequisite();
//
//        step("修改In1的目的地为RingGroup0");
//        apiUtil.editInbound("In1",String.format("\"def_dest\":\"ring_group\",\"def_dest_value\":\"%s\",\"enb_time_condition\":0",apiUtil.getRingGroupSummary("6300").id)).apply();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 4000" + ",[callee] 441000");
//        pjsip.Pj_Make_Call_No_Answer(4000, "441000", DEVICE_ASSIST_3, false);
//
//        step("[通话状态校验]");
//        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
//        Assert.assertEquals(getExtensionStatus(1001, RING, 30), RING);
//        Assert.assertEquals(getExtensionStatus(1003, RING, 30), RING);
//        pjsip.Pj_Answer_Call(1003, false);
//        Assert.assertEquals(getExtensionStatus(1003, TALKING, 30), TALKING);
//
//        step("分机A-1000按*5将通话停泊");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(1003,"*","5");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1003, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(4000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("[caller] 1003" + ",[callee] 6000");
//        pjsip.Pj_Make_Call_No_Answer(1003, "6000", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1003, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(3000);
//        pjsip.Pj_hangupCall(1003);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_4000.toString(), CDRObject.CDRNAME.Extension_1003.toString(), CDRObject.STATUS.ANSWER.toString(), "testa D<1003> retrieved from 6000 , testa D<1003> hung up", ACCOUNTTRUNK, "", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_4000.toString(), CDRObject.CDRNAME.RINGGROUP0_6300.toString(), CDRObject.STATUS.ANSWER.toString(), "RingGroup RingGroup0<6300> connected", ACCOUNTTRUNK, "", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_4000.toString(), CDRObject.CDRNAME.Extension_1003.toString(), CDRObject.STATUS.ANSWER.toString(), "4000<4000> parked at 6000", ACCOUNTTRUNK, "", "Inbound"));
//
//        softAssertPlus.assertAll();
//
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("CallParking")
//    @Description("4.修改In1的目的地为Queue0\n" +
//            "\t通过sps外线呼入到Queue0,1000、1001、1003、1004同时响铃，1004接听\n" +
//            "\t\t分机1004按*5将通话停泊\n" +
//            "\t\t\tasterisk播放提示音call-parked-at.slin，一会儿后1004自动挂断，外线保持通话状态；\n" +
//            "\t\t\t\t分机D-1003拨打6000接回通话\n" +
//            "\t\t\t\t\t分机D与外线正常通话，外线挂断，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "CallParking",""})
//    public void testCallParking_04_CallParking() {
//        prerequisite();
//
//        step("修改In1的目的地为Queue0");
//        apiUtil.editInbound("In1",String.format("\"def_dest\":\"queue\",\"def_dest_value\":\"%s\",\"enb_time_condition\":0",apiUtil.getQueueSummary("6400").id)).apply();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 2000" + ",[callee] 991000");
//        pjsip.Pj_Make_Call_No_Answer(2000, "991000");
//
//        step("[通话状态校验]");
//        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
//        Assert.assertEquals(getExtensionStatus(1001, RING, 30), RING);
//        Assert.assertEquals(getExtensionStatus(1003, RING, 30), RING);
//        Assert.assertEquals(getExtensionStatus(1004, RING, 30), RING);
//        pjsip.Pj_Answer_Call(1004, false);
//        Assert.assertEquals(getExtensionStatus(1004, TALKING, 30), TALKING);
//
//        step("分机A-1000按*5将通话停泊");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(1004,"*","5");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("[caller] 1003" + ",[callee] 6000");
//        pjsip.Pj_Make_Call_No_Answer(1003, "6000", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1003, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(3000);
//        pjsip.Pj_hangupCall(1003);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1003.toString(), CDRObject.STATUS.ANSWER.toString(), "testa D<1003> retrieved from 6000 , testa D<1003> hung up", SPS, "", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.QUEUE0_6400.toString(), CDRObject.STATUS.ANSWER.toString(), "Queue Queue0<6400> connected", SPS, "", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1004.toString(), CDRObject.STATUS.ANSWER.toString(),"2000<2000> parked at 6000", SPS, "", "Inbound"));
//
//        softAssertPlus.assertAll();
//
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("CallParking")
//    @Description("5.通过FXO外线呼入到分机A-1000,1000接听\n" +
//            "\t分机A-1000按*5将通话停泊\n" +
//            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；\n" +
//            "\t\t\t分机C-1002拨打6000接回通话\n" +
//            "\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries","FeatureCode-CallParking","P3", "CallParking",""})
//    public void testCallParking_05_CallParking() {
//        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
//            Assert.assertTrue(false,"FXO 线路 不测！");
//        }
//        prerequisite();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 2000" + ",[callee] 2005");
//        pjsip.Pj_Make_Call_No_Answer(2000, "2005");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机A-1000按*5将通话停泊");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(1000,"*","5");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("[caller] 1002" + ",[callee] 6000");
//        pjsip.Pj_Make_Call_No_Answer(1002, "6000", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(3000);
//        pjsip.Pj_hangupCall(2000);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6000 , 2000<2000> hung up", FXO_1, "", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at 6000", FXO_1, "", "Inbound"));
//
//        softAssertPlus.assertAll();
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("CallParking")
//    @Description("6.通过BRI外线呼入到分机A-1000,1000接听\n" +
//            "\t分机A-1000按*5将通话停泊\n" +
//            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；\n" +
//            "\t\t\t分机C-1002拨打6000接回通话\n" +
//            "\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr")
//    @Severity(SeverityLevel.NORMAL)
//    @TmsLink(value = "")
//    @Issue("BRI 主叫显示异常")
//    @Test(groups = {"PSeries","FeatureCode-CallParking","P3", "CallParking",""})
//    public void testCallParking_06_CallParking() {
//        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
//            Assert.assertTrue(false,"BRI_1 线路 不测！");
//        }
//        prerequisite();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 2000" + ",[callee] 881000");
//        pjsip.Pj_Make_Call_No_Answer(2000, "881000");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机A-1000按*5将通话停泊");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(1000,"*","5");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("[caller] 1002" + ",[callee] 6000");
//        pjsip.Pj_Make_Call_No_Answer(1002, "6000", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(3000);
//        pjsip.Pj_hangupCall(1002);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(),CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(),"testta C<1002> retrieved from 6000 , testta C<1002> hung up", BRI_1,"", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(),CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(),"2000 parked at 6000",BRI_1,"", "Inbound"));
//
//        softAssertPlus.assertAll();
//
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("CallParking")
//    @Description("7.通过E1外线呼入到分机A-1000,1000接听\n" +
//            "\t分机A-1000按*5将通话停泊\n" +
//            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；\n" +
//            "\t\t\t分机C-1002拨打6000接回通话\n" +
//            "\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "FeatureCode-CallParking","P3", "CallParking",""})
//    public void testCallParking_07_CallParking() {
//        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
//            Assert.assertTrue(false,"E1 线路 不测！");
//        }
//        prerequisite();
//
//        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 2000" + ",[callee] 661000");
//        pjsip.Pj_Make_Call_No_Answer(2000, "661000");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机A-1000按*5将通话停泊");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(1000,"*","5");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("[caller] 1002" + ",[callee] 6000");
//        pjsip.Pj_Make_Call_No_Answer(1002, "6000", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(3000);
//        pjsip.Pj_hangupCall(1002);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(),"testta C<1002> retrieved from 6000 , testta C<1002> hung up", E1,"", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(),"2000<2000> parked at 6000",  E1,"", "Inbound"));
//
//        softAssertPlus.assertAll();
//
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("CallParking")
//    @Description("8.通过GSM外线呼入到分机A-1000,1000接听\n" +
//            "\t分机A-1000按*5将通话停泊\n" +
//            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；\n" +
//            "\t\t\t分机C-1002拨打6000接回通话\n" +
//            "\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "CallParking",""})
//    public void testCallParking_08_CallParking() {
//        if(GSM.trim().equalsIgnoreCase("null") || GSM.trim().equalsIgnoreCase("")){
//            Assert.assertTrue(false,"GSM线路 不测试！");
//        }
//        prerequisite();
//
//        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 2000" + ",[callee] 33"+DEVICE_TEST_GSM);
//        pjsip.Pj_Make_Call_No_Answer(2000 ,"33"+DEVICE_TEST_GSM);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, RING, 150)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机A-1000按*5将通话停泊");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(1000,"*","5");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("[caller] 1002" + ",[callee] 6000");
//        pjsip.Pj_Make_Call_No_Answer(1002, "6000", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(90*1000);
//        pjsip.Pj_hangupCall(1002);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(),"33"+DEVICE_TEST_GSM, CDRObject.STATUS.ANSWER.toString(), CDRObject.CDRNAME.Extension_1000.toString() + " hung up", "", GSM, "Outbound"));
//
//        softAssertPlus.assertAll();
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("CallParking")
//    @Description("分机A-1000拨打分机B-1001，1001接听\n" +
//            "\t9.分机A-1000按*5将通话停泊\n" +
//            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，分机B保持通话状态；\n" +
//            "\t\t\t分机C-1002拨打6000接回通话\n" +
//            "\t\t\t\t分机BC正常通话，分机C挂断，检查cdr\n")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "CallParking",""})
//    public void testCallParking_09_CallParking() {
//        prerequisite();
//
//        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 1000" + ",[callee] 1001");
//        pjsip.Pj_Make_Call_No_Answer(1000 ,"1001");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1001, RING, 150)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1001, false);
//        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机A-1000按*5将通话停泊");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(1000,"*","5");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("[caller] 1002" + ",[callee] 6000");
//        pjsip.Pj_Make_Call_No_Answer(1002, "6000", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(90*1000);
//        pjsip.Pj_hangupCall(1002);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_1001.toString(), CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6000 , testta C<1002> hung up", "", "", "Internal"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.CDRNAME.Extension_1001.toString(), CDRObject.STATUS.ANSWER.toString(), "test2 B<1001> parked at 6000", "", "", "Internal"));
//
//        softAssertPlus.assertAll();
//
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("CallParking")
//    @Description("分机A-1000拨打分机B-1001，1001接听\n" +
//            "\t10.分机B-1001按*5将通话停泊\n" +
//            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后1001自动挂断，分机1000保持通话状态；\n" +
//            "\t\t\t分机B-1001拨打6000接回通话\n" +
//            "\t\t\t\t分机AB正常通话，分机A挂断，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "CallParking",""})
//    public void testCallParking_10_CallParking() {
//        prerequisite();
//
//        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 1000" + ",[callee] 1001");
//        pjsip.Pj_Make_Call_No_Answer(1000 ,"1001", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1001, RING, 30)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1001, false);
//        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机A-1000按*5将通话停泊");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(1001,"*","5");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1001, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("[caller] 1001" + ",[callee] 6000");
//        pjsip.Pj_Make_Call_No_Answer(1001, "6000", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(3*1000);
//        pjsip.Pj_hangupCall(1000);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.CDRNAME.Extension_1001.toString(), CDRObject.STATUS.ANSWER.toString(), "test2 B<1001> retrieved from 6000 , test A<1000> hung up", "", "", "Internal"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.CDRNAME.Extension_1001.toString(), CDRObject.STATUS.ANSWER.toString(), "test A<1000> parked at 6000", "", "", "Internal"));
//
//        softAssertPlus.assertAll();
//
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("CallParking")
//    @Description("11.分机1020拨打分机A-1000,1000接听\n" +
//            "\t分机1020按*5将通话停泊\n" +
//            "（辅助2分机2000按77*5）\n" +
//            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后2000自动挂断，分机1000保持通话状态；\n" +
//            "\t\t\t分机2000拨打776000接回通话\n" +
//            "\t\t\t\t分机2000与分机1000正常通话，分机2000挂断，检查cdr\n" +
//            "\tFXS分机需判断")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "CallParking",""})
//    public void testCallParking_11_CallParking() {
//        prerequisite();
//
//        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 2000" + ",[callee] 2005");//todo call failed
//        pjsip.Pj_Make_Call_No_Answer(2000 ,"2005");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, RING, 30)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机1020按*5将通话停泊（辅助2分机2000按77*5）");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(2000,"7","7","*","5");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(2000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("[caller] 2000" + ",[callee] 776000");
//        pjsip.Pj_Make_Call_No_Answer(2000, "776000");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(3*1000);
//        pjsip.Pj_hangupCall(2000);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_1020.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), CDRObject.CDRNAME.Extension_2000.toString() + " hung up", SPS, "", "Inbound"));
//
//        softAssertPlus.assertAll();
//
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("CallParking")
//    @Description("12.分机1001拨打13001通过SIP外线呼出，被叫应答\n" +
//            "\t分机1001按*5将通话停泊\n" +
//            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后1001自动挂断，外线保持通话状态；\n" +
//            "\t\t\t分机C-1002拨打6000接回通话\n" +
//            "\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "CallParking",""})
//    public void testCallParking_12_CallParking() {
//        prerequisite();
//
//        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 1001" + ",[callee] 13001");
//        pjsip.Pj_Make_Call_No_Answer(1001 ,"13001", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(3001, RING, 30)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(3001, false);
//        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机1001按*5将通话停泊");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(1001,"*","5");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1001, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("[caller] 1002" + ",[callee] 6000");
//        pjsip.Pj_Make_Call_No_Answer(1002, "6000", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(3*1000);
//        pjsip.Pj_hangupCall(1002);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple( "13001",CDRObject.CDRNAME.Extension_1002.toString(),CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6000 , testta C<1002> hung up",SIPTrunk,"", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_1001.toString(), "13001", CDRObject.STATUS.ANSWER.toString(), "13001 parked at 6000", "", SIPTrunk, "Outbound"));
//
//        softAssertPlus.assertAll();
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("CallParking")
//    @Description("13.分机1001拨打22001通过SPS外线呼出，被叫应答\n" +
//            "\t分机1001按*5将通话停泊\n" +
//            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后1001自动挂断，外线保持通话状态；\n" +
//            "\t\t\t分机C-1002拨打6000接回通话\n" +
//            "\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "CallParking",""})
//    public void testCallParking_13_CallParking() {
//        prerequisite();
//
//        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 1001" + ",[callee] 22001");
//        pjsip.Pj_Make_Call_No_Answer(1001 ,"22001");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(2000, RING, 30)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(2000, false);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机1001按*5将通话停泊");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(1001,"*","5");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1001, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("[caller] 1002" + ",[callee] 6000");
//        pjsip.Pj_Make_Call_No_Answer(1002, "6000", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(3*1000);
//        pjsip.Pj_hangupCall(1002);
//
//        assertStep("[CDR校验]");//todo check ivr
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple("22001", CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6000 , testta C<1002> hung up",SPS,"", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_1001.toString(), "22001", CDRObject.STATUS.ANSWER.toString(), "22001 parked at 6000", "", SPS, "Outbound"));
//
//        softAssertPlus.assertAll();
//
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("CallParking")
//    @Description("14.分机1001拨打33001通过Account外线呼出，被叫应答\n" +
//            "\t分机1001按*5将通话停泊\n" +
//            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后1001自动挂断，外线保持通话状态；\n" +
//            "\t\t\t分机C-1002拨打6000接回通话\n" +
//            "\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "CallParking",""})
//    public void testCallParking_14_CallParking() {
//        prerequisite();
//
//        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 1001" + ",[callee] 33001");
//        pjsip.Pj_Make_Call_No_Answer(1001 ,"33001");
//
//        step("[通话状态校验]");//todo call failed
//        assertThat(getExtensionStatus(3001, RING, 30)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(3001, false);
//        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机1001按*5将通话停泊");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(1001,"*","5");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1001, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(3000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("[caller] 1002" + ",[callee] 6000");
//        pjsip.Pj_Make_Call_No_Answer(1002, "6000", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(3*1000);
//        pjsip.Pj_hangupCall(1002);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), CDRObject.CDRNAME.Extension_1002.toString() + " hung up", "", ACCOUNTTRUNK, "Outbound"));
//
//        softAssertPlus.assertAll();
//
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("CallParking")
//    @Description("15.分机1001拨打42000通过FXO外线呼出，被叫应答\n" +
//            "\t分机1001按*5将通话停泊\n" +
//            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后1001自动挂断，外线保持通话状态；\n" +
//            "\t\t\t分机C-1002拨打6000接回通话\n" +
//            "\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "CallParking",""})
//    public void testCallParking_15_CallParking() {
//        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
//            Assert.assertTrue(false,"FXO 线路 不测！");
//        }
//        prerequisite();
//
//        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 1001" + ",[callee] 42000");
//        pjsip.Pj_Make_Call_No_Answer(1001 ,"42000");
//
//        step("[通话状态校验]");//todo call failed
//        assertThat(getExtensionStatus(4000, RING, 30)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(4000, false);
//        assertThat(getExtensionStatus(4000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机1001按*5将通话停泊");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(1001,"*","5");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1001, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(4000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("[caller] 1002" + ",[callee] 6000");
//        pjsip.Pj_Make_Call_No_Answer(1002, "6000", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(3*1000);
//        pjsip.Pj_hangupCall(1002);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_1001.toString(), "33001", CDRObject.STATUS.ANSWER.toString(), CDRObject.CDRNAME.Extension_1002.toString() + " hung up", "", ACCOUNTTRUNK, "Outbound"));
//
//        softAssertPlus.assertAll();
//
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("CallParking")
//    @Description("16.分机1001拨打5555通过BRI外线呼出，被叫应答\n" +
//            "\t分机1001按*5将通话停泊\n" +
//            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后1001自动挂断，外线保持通话状态；\n" +
//            "\t\t\t分机C-1002拨打6000接回通话\n" +
//            "\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "CallParking",""})
//    public void testCallParking_16_CallParking() {
//        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
//            Assert.assertTrue(false,"BRI_1 线路 不测！");
//        }
//        prerequisite();
//
//        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 1001" + ",[callee] 5555");
//        pjsip.Pj_Make_Call_No_Answer(1001 ,"5555", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(2000, RING, 30)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(2000, false);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机1001按*5将通话停泊");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(1001,"*","5");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 2 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1001, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("[caller] 1002" + ",[callee] 6000");
//        pjsip.Pj_Make_Call_No_Answer(1002, "6000", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(3*1000);
//        pjsip.Pj_hangupCall(1002);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(),  "testta C<1002> retrieved from 6000 , testta C<1002> hung up", BRI_1,"", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_1001.toString(), "2000", CDRObject.STATUS.ANSWER.toString(), "2000 parked at 6000", "", BRI_1, "Outbound"));
//
//        softAssertPlus.assertAll();
//
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("CallParking")
//    @Description("17.分机1001拨打6666通过E1外线呼出，被叫应答\n" +
//            "\t分机1001按*5将通话停泊\n" +
//            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后1001自动挂断，外线保持通话状态；\n" +
//            "\t\t\t分机C-1002拨打6000接回通话\n" +
//            "\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("E1,callto 显示异常")
//    @Test(groups = {"PSeries", "FeatureCode-CallParking","P3", "CallParking",""})
//    public void testCallParking_17_CallParking() {
//        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
//            Assert.assertTrue(false,"E1 线路 不测！");
//        }
//        prerequisite();
//
//        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 1001" + ",[callee] 6666");
//        pjsip.Pj_Make_Call_No_Answer(1001 ,"6666", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(2000, RING, 30)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(2000, false);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机1001按*5将通话停泊");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(1001,"*","5");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1001, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("[caller] 1002" + ",[callee] 6000");
//        pjsip.Pj_Make_Call_No_Answer(1002, "6000", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(3*1000);
//        pjsip.Pj_hangupCall(1002);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(),CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6000 , testta C<1002> hung up",E1,"", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_1001.toString(),CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.STATUS.ANSWER.toString(), "2000 parked at 6000","",E1, "Outbound"));
//
//        softAssertPlus.assertAll();
//
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("CallParking")
//    @Description("18.分机1001拨打7+辅助2的GSM号码通过GSM外线呼出，被叫应答\n" +
//            "\t分机1001按*5将通话停泊\n" +
//            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后1001自动挂断，外线保持通话状态；\n" +
//            "\t\t\t分机C-1002拨打6000接回通话\n" +
//            "\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "CallParking",""})
//    public void testCallParking_18_CallParking() {
//        if(GSM.trim().equalsIgnoreCase("null") || GSM.trim().equalsIgnoreCase("")){
//            Assert.assertTrue(false,"GSM线路 不测试！");
//        }
//        prerequisite();
//
//        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 1001" + ",[callee] 7"+DEVICE_TEST_GSM);
//        pjsip.Pj_Make_Call_No_Answer(1001 ,"7"+DEVICE_TEST_GSM);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(2000, RING, 150)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(2000, false);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机A-1000按*5将通话停泊");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(1001,"*","5");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1001, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("[caller] 1002" + ",[callee] 6000");
//        pjsip.Pj_Make_Call_No_Answer(1002, "6000", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(90*1000);
//        pjsip.Pj_hangupCall(1002);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(),"33"+DEVICE_TEST_GSM, CDRObject.STATUS.ANSWER.toString(), CDRObject.CDRNAME.Extension_1000.toString() + " hung up", "", GSM, "Outbound"));
//
//        softAssertPlus.assertAll();
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("CallParking")
//    @Description("19.通过sps外线呼入到分机A-1000,1000接听;\n" +
//            "分机B-1001拨打分机C-1002,1002接听；\n" +
//            "\t分机1000先按*5将通话停泊；\n" +
//            "分机1002也按*5将通话停泊；\n" +
//            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000、1002自动挂断，外线保持通话状态；\n" +
//            "\t\t\t分机1000拨打6001接回通话；\n" +
//            "分机1005拨打6000接回通话；\n" +
//            "\t\t\t\t分机1000与分机1001正常通话；分机1005与外线正常通话；主叫挂断，检查cdr\n" +
//            "\t\t\t\t判断对应的分机1000、1005、1001、外线分机为Talking状态即可，无法判断谁和谁真正在通话；看cdr正确就行")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "CallParking",""})
//    public void testCallParking_19_CallParking() {
//        prerequisite();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 2000" + ",[callee] 991000");
//        pjsip.Pj_Make_Call_No_Answer(2000 ,"991000");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, RING, 30)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("[caller] 1001" + ",[callee] 1002");
//        pjsip.Pj_Make_Call_No_Answer(1001 ,"1002", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1002, RING, 30)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1002, false);
//        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机1000先按*5将通话停泊,分机1002先按*5将通话停泊");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(1000,"*","5");
//        sleep(2000);
//        pjsip.Pj_Send_Dtmf(1002,"*","5");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 4 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");//todo auto hungup failed
//        assertThat(getExtensionStatus(1001, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("[caller] 1000" + ",[callee] 6001");
//        pjsip.Pj_Make_Call_No_Answer(1000, "6001", DEVICE_IP_LAN, false);
//        step("[caller] 1005" + ",[callee] 6000");
//        pjsip.Pj_Make_Call_No_Answer(1005, "6000", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//        assertThat(getExtensionStatus(1005, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(3*1000);
//        pjsip.Pj_hangupCall(1000);
//        pjsip.Pj_hangupCall(1005);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(5)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.CDRNAME.Extension_1001.toString(), CDRObject.STATUS.ANSWER.toString(), CDRObject.CDRNAME.Extension_1000.toString() + " hung up", "", "", "Inbound"));
//
//        softAssertPlus.assertAll();
//
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("CallParking")
//    @Description("编辑Feature->Call Parking 禁用" +
//            "20.通过sps外线呼入到分机A-1000,1000接听；\n" +
//            "分机1000按*5将通话停泊；\n" +
//            "\t停泊失败，分机1000与外线保持通话，挂断；检查cdr\n" +
//            "\t\t编辑Feature->Call Parking 启用；\n" +
//            "通过sps外线呼入到分机A-1000,1000接听；\n" +
//            "分机1000按*5将通话停泊；\n" +
//            "\t\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；\n" +
//            "\t\t\t\t分机C-1002拨打6000接回通话\n" +
//            "\t\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "CallParking",""})
//    public void testCallParking_20_CallParking() {
//        prerequisite();
//        apiUtil.editFeatureCode("\"enb_park\": 0").apply();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 2000" + ",[callee] 991000");
//        pjsip.Pj_Make_Call_No_Answer(2000, "991000");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机A-1000按*5将通话停泊");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(1000,"*","5");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 2 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("[caller] 1002" + ",[callee] 6000");
//        pjsip.Pj_Make_Call_No_Answer(1002, "6000", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(3000);
//        pjsip.Pj_hangupCall(1002);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6000 , testta C<1002> hung up", SPS, "", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at 6000", SPS, "", "Inbound"));
//
//        softAssertPlus.assertAll();
//
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("CallParking")
//    @Description("编辑Feature->Call Parking 禁用" +
//            "21.分机1001拨打22001通过SPS外线呼出，被叫应答；\n" +
//            "分机1001按*5将通话停泊；\n" +
//            "\t停泊失败，分机1001与外线保持通话，挂断；检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "CallParking",""})
//    public void testCallParking_21_CallParking() {
//        prerequisite();
//        apiUtil.editFeatureCode("\"enb_park\": 0").apply();
//
//        step("[caller] 1001" + ",[callee] 22001");
//        pjsip.Pj_Make_Call_No_Answer(1001, "22001", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(2000, false);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机A-1001按*5将通话停泊");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(1001,"*","5");
//
//        assertThat(getExtensionStatus(1001, TALKING, 30)).as("通话状态校验 失败!").isIn(TALKING);
//
//        step("挂断");
//        pjsip.Pj_hangupCall(1001);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_1001.toString(), "22001", CDRObject.STATUS.ANSWER.toString(), "22001 parked at 6000",  "",SPS, "Outbound"));
//        softAssertPlus.assertAll();
//
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("CallParking")
//    @Description("22.编辑Feature->Call Parking 修改特征码为*******\n" +
//            "\t通过sps外线呼入到分机A-1000,1000接听；\n" +
//            "分机1000按*5将通话停泊；\n" +
//            "\t\t停泊失败，分机1000与外线保持通话，挂断；检查cdr\n" +
//            "\t\t\t分机1000按*******将通话停泊；\n" +
//            "\t\t\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；\n" +
//            "\t\t\t\t\t分机C-1002拨打6000接回通话\n" +
//            "\t\t\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr\n" +
//            "\t\t\t\t\t\t\t编辑Feature->Call Parking 修改特征码为*5\n" +
//            "通过sps外线呼入到分机A-1000,1000接听；\n" +
//            "分机1000按*5将通话停泊；\n" +
//            "\t\t\t\t\t\t\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；主叫挂断，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "CallParking",""})
//    public void testCallParking_22_CallParking() {
//        prerequisite();
//        apiUtil.editFeatureCode("\"enb_park\": 1,\"park\":\"*******\"").apply();
//
//        asteriskObjectList.clear();
//        asteriskObjectListSecond.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        SSHLinuxUntils.AsteriskThread threadSecond=new SSHLinuxUntils.AsteriskThread(asteriskObjectListSecond,CALL_PARKED_AT);
//        thread.start();
//        threadSecond.start();
//
//        step("[caller] 2000" + ",[callee] 991000");
//        pjsip.Pj_Make_Call_No_Answer(2000, "991000");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机A-1000按*5将通话停泊");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(1000,"*","5");
//
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("通话状态校验 失败!").isIn(TALKING);
//
//        step("挂断");
//        pjsip.Pj_hangupCall(1000);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), CDRObject.CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));
//
//        step("[caller] 2000" + ",[callee] 991000");
//        pjsip.Pj_Make_Call_No_Answer(2000, "991000");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机1000按*******将通话停泊");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(1000,"*","*","*","*","*","*","*");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("[caller] 1002" + ",[callee] 6000");
//        pjsip.Pj_Make_Call_No_Answer(1002, "6000", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(3000);
//        pjsip.Pj_hangupCall(1002);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), CDRObject.CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));
//
//        apiUtil.editFeatureCode("\"enb_park\": 1,\"park\":\"*5\"").apply();
//
//        step("[caller] 2000" + ",[callee] 991000");
//        pjsip.Pj_Make_Call_No_Answer(2000, "991000");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        sleep(5000);
//        step("分机A-1000按*5将通话停泊");
//        pjsip.Pj_Send_Dtmf(1000,"*","5");
//
//        tmp = 0;
//        while (asteriskObjectListSecond.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectListSecond.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectListSecond.get(i).getName() + " [asterisk object time] " + asteriskObjectListSecond.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListSecond.get(i).getTag());
//            }
//            threadSecond.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListSecond.size());
//        }
//        threadSecond.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        pjsip.Pj_hangupCall(1001);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6000 , testta C<1002> hung up", SPS, "", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at 6000", SPS, "", "Inbound"));
//
//        softAssertPlus.assertAll();
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("ParkingNumberRange")
//    @Description("编辑Feature->Parking Number Range 为6050-6059" +
//            "23.通过sps外线呼入到分机A-1000,1000接听；\n" +
//            "分机1000按*5将通话停泊；\n" +
//            "\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；\n" +
//            "\t\t分机B-1001拨打6050接回通话，挂断；检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "ParkingNumberRange",""})
//    public void testCallParking_23_ParkingNumberRange() {
//        prerequisite();
//        apiUtil.editFeatureCode("\"enb_park\":1,\"park_start\":\"6050\",\"park_end\":\"6059\"").apply();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 2000" + ",[callee] 991000");
//        pjsip.Pj_Make_Call_No_Answer(2000, "991000");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        sleep(5000);
//        step("分机A-1000按*5将通话停泊");
//        pjsip.Pj_Send_Dtmf(1000,"*","5");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("[caller] 1001" + ",[callee] 6050");
//        pjsip.Pj_Make_Call_No_Answer(1001, "6050", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(3000);
//        pjsip.Pj_hangupCall(1001);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1001.toString(), CDRObject.STATUS.ANSWER.toString(), "test2 B<1001> retrieved from 6050 , test2 B<1001> hung up", SPS, "", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at 6050", SPS, "", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), "*", CDRObject.STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"));
//
//        softAssertPlus.assertAll();
//    }
//
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("ParkingNumberRange")
//    @Description("编辑Feature->Parking Number Range 为6050-6059" +
//            "24.分机A-1000按*056060将通话停泊\n" +
//            "\tasterisk播放提示音call-parked-failed-slotused.slin，停泊失败分机A与外线继续保持通话；主叫挂断，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "ParkingNumberRange",""})
//    public void testCallParking_24_ParkingNumberRange() {
//        prerequisite();
//        apiUtil.editFeatureCode("\"enb_park\":1,\"park_start\":\"6050\",\"park_end\":\"6059\"").apply();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,"call-parked-failed-slotused.slin");
//        thread.start();
//
//        step("[caller] 2000" + ",[callee] 991000");
//        pjsip.Pj_Make_Call_No_Answer(2000, "991000");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        sleep(5000);
//        step("分机A-1000按*056060将通话停泊");
//        pjsip.Pj_Send_Dtmf(1000,"*","0","5","6","0","6","0");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("通话状态校验 自动挂断失败!").isIn(TALKING);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        pjsip.Pj_hangupCall(2000);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at", SPS, "", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1001.toString(), CDRObject.STATUS.ANSWER.toString(), "test2 B<1001> retrieved from 6050 , test2 B<1001> hung up", SPS, "", "Inbound"));
//
//        softAssertPlus.assertAll();
//    }
//
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("ParkingNumberRange")
//    @Description("编辑Feature->Parking Number Range 为6050-6059" +
//            "25.分机A-1000按*056059将通话停泊\n" +
//            "\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；\n" +
//            "\t\t分机C-1002拨打6059接回通话\n" +
//            "\t\t\t分机C与外线正常通话，分机C挂断，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "ParkingNumberRange",""})
//    public void testCallParking_25_ParkingNumberRange() {
//        prerequisite();
//        apiUtil.editFeatureCode("\"enb_park\":1,\"park_start\":\"6050\",\"park_end\":\"6059\"").apply();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 2000" + ",[callee] 991000");
//        pjsip.Pj_Make_Call_No_Answer(2000, "991000");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        sleep(5000);
//        step("分机A-1000按*056059将通话停泊");
//        pjsip.Pj_Send_Dtmf(1000,"*","0","5","6","0","5","9");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("[caller] 1002" + ",[callee] 6059");
//        pjsip.Pj_Make_Call_No_Answer(1002, "6059", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(3000);
//        pjsip.Pj_hangupCall(1001);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "2000<2000> hung up", SPS, "", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at", SPS, "", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1001.toString(), CDRObject.STATUS.ANSWER.toString(), "test2 B<1001> retrieved from 6050 , test2 B<1001> hung up", SPS, "", "Inbound"));
//
//        softAssertPlus.assertAll();
//    }
//
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("ParkingNumberRange")
//    @Description("26.编辑Feature->Parking Number Range 为6050-6050\n" +
//            "\t通过sps外线呼入到分机A-1000,1000接听；\n" +
//            "分机1000按*5将通话停泊；\n" +
//            "\t\tasterisk播放提示音call-parked-at.slin，分机B-1001拨打1002,1002接听，1002按*5将通话停泊，asterisk 播放提示音call-parked-failed-noslot.slin，分机1001、1002保持正常通话；挂断所有通话，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "ParkingNumberRange",""})
//    public void testCallParking_26_ParkingNumberRange() {
//        prerequisite();
//        apiUtil.editFeatureCode("\"enb_park\":1,\"park_start\":\"6050\",\"park_end\":\"6050\"").apply();
//
//        asteriskObjectList.clear();
//        asteriskObjectListSecond.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        SSHLinuxUntils.AsteriskThread threadSecond=new SSHLinuxUntils.AsteriskThread(asteriskObjectListSecond,"call-parked-failed-noslot.slin");
//        thread.start();
//        threadSecond.start();
//
//        step("[caller] 2000" + ",[callee] 991000");
//        pjsip.Pj_Make_Call_No_Answer(2000, "991000");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        sleep(5000);
//        step("分机1000按*5将通话停泊");
//        pjsip.Pj_Send_Dtmf(1000,"*","5");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("[caller] 1001" + ",[callee] 1002");
//        pjsip.Pj_Make_Call_No_Answer(1001, "1002", DEVICE_IP_LAN, false);
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1002, RING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1002);
//        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//
//        sleep(5000);
//        step("分机1002按*5将通话停泊");
//        pjsip.Pj_Send_Dtmf(1002,"*","5");
//
//        tmp = 0;
//        while (asteriskObjectListSecond.size() != 2 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectListSecond.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectListSecond.get(i).getName() + " [asterisk object time] " + asteriskObjectListSecond.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListSecond.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListSecond.size());
//        }
//        threadSecond.flag = false;
//
//        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//
//        step("挂断");
//        sleep(3000);
//        pjsip.Pj_hangupCall(2000);
//        pjsip.Pj_hangupCall(1001);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_1001.toString(), CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "test2 B<1001> hung up", "", "", "Internal"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_1001.toString(), CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "test2 B<1001> parked at", "", "", "Internal"));
//
//        softAssertPlus.assertAll();
//
//    }
//
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("CallParkingInitiator")
//    @Description("编辑Feature->ParkingTimeout 为10s\n" +
//            "\t27.通过sps外线呼入到分机A-1000,1000接听；\n" +
//            "分机1000按*5将通话停泊；\n" +
//            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断；等待10s后，分机1000响铃，接听，分机1000与外线正常通话，挂断，检查cdr\n" +
//            "\t\t\t编辑Feature->ParkingTimeout 为60s;\n" +
//            "通过sps外线呼入到分机A-1000,1000接听；\n" +
//            "分机1000按*5将通话停泊；\n" +
//            "\t\t\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断；等待60s后，分机1000响铃，接听，分机1000与外线正常通话，挂断，检查cdr\n")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "ParkingTimeout","CallParkingInitiator"})
//    public void testCallParking_27_ParkingTimeout() {
//        prerequisite();
//        apiUtil.editFeatureCode("\"park_timeout\":10").apply();
//
//        asteriskObjectList.clear();
//        asteriskObjectListSecond.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        SSHLinuxUntils.AsteriskThread threadSecond=new SSHLinuxUntils.AsteriskThread(asteriskObjectListSecond,"call-parked-failed-noslot.slin");
//        thread.start();
//        threadSecond.start();
//
//        step("[caller] 2000" + ",[callee] 991000");
//        pjsip.Pj_Make_Call_No_Answer(2000, "991000");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        sleep(5000);
//        step("分机1000按*5将通话停泊");
//        pjsip.Pj_Send_Dtmf(1000,"*","5");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        sleep(10*1000);
//        assertThat(getExtensionStatus(1000, RING, 5)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 10)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        pjsip.Pj_hangupCall(1000);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "test A<1000> hung up", SPS, "", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at 6000 , timed out", SPS, "", "Inbound"));
//
//        step("编辑Feature->ParkingTimeout 为60s");
//        apiUtil.editFeatureCode("\"park_timeout\":60").apply();
//
//        step("[caller] 2000" + ",[callee] 991000");
//        pjsip.Pj_Make_Call_No_Answer(2000, "991000");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        sleep(5000);
//        step("分机1000按*5将通话停泊");
//        pjsip.Pj_Send_Dtmf(1000,"*","5");
//
//        tmp = 0;
//        while (asteriskObjectListSecond.size() >= 2 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectListSecond.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectListSecond.get(i).getName() + " [asterisk object time] " + asteriskObjectListSecond.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListSecond.get(i).getTag());
//            }
//            threadSecond.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        threadSecond.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        sleep(60*1000);
//
//        assertThat(getExtensionStatus(1000, RING, 5)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 10)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        pjsip.Pj_hangupCall(1000);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "test A<1000> hung up", SPS, "", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at 6000 , timed out", SPS, "", "Inbound"));
//
//        softAssertPlus.assertAll();
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("CallParkingInitiator")
//    @Description("编辑Feature->ParkingTimeout 为10s\n" +
//            "\t28.通过sps外线呼入到分机A-1000,1000接听；\n" +
//            "分机1000按*056000将通话停泊；\n" +
//            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断；等待10s后，分机1000响铃，接听，分机1000与外线正常通话，挂断，检查cdr\n" +
//            "\t\t\t编辑Feature->ParkingTimeout 为60s;\n" +
//            "通过sps外线呼入到分机A-1000,1000接听；\n" +
//            "分机1000按*5将通话停泊；\n" +
//            "\t\t\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断；等待60s后，分机1000响铃，接听，分机1000与外线正常通话，挂断，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "ParkingTimeout","CallParkingInitiator"})
//    public void testCallParking_28_CallParkingInitiator() {
//        prerequisite();
//        apiUtil.editFeatureCode("\"park_timeout\":10").apply();
//
//        asteriskObjectList.clear();
//        asteriskObjectListSecond.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        SSHLinuxUntils.AsteriskThread threadSecond=new SSHLinuxUntils.AsteriskThread(asteriskObjectListSecond,"call-parked-failed-noslot.slin");
//        thread.start();
//        threadSecond.start();
//
//        step("[caller] 2000" + ",[callee] 991000");
//        pjsip.Pj_Make_Call_No_Answer(2000, "991000");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        sleep(5000);
//        step("分机1000按*056000将通话停泊");
//        pjsip.Pj_Send_Dtmf(1000,"*","0","5","6","0","0","0");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        sleep(10*1000);
//        assertThat(getExtensionStatus(1000, RING, 5)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 10)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        pjsip.Pj_hangupCall(1000);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "test A<1000> hung up", SPS, "", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at 6000 , timed out", SPS, "", "Inbound"));
//
//        step("编辑Feature->ParkingTimeout 为60s");
//        apiUtil.editFeatureCode("\"park_timeout\":60").apply();
//
//        step("[caller] 2000" + ",[callee] 991000");
//        pjsip.Pj_Make_Call_No_Answer(2000, "991000");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        sleep(5000);
//        step("分机1000按*5将通话停泊");
//        pjsip.Pj_Send_Dtmf(1000,"*","5");
//
//        tmp = 0;
//        while (asteriskObjectListSecond.size() >= 2 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectListSecond.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectListSecond.get(i).getName() + " [asterisk object time] " + asteriskObjectListSecond.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListSecond.get(i).getTag());
//            }
//            threadSecond.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        threadSecond.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        sleep(60*1000);
//
//        assertThat(getExtensionStatus(1000, RING, 5)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 10)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        pjsip.Pj_hangupCall(1000);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "test A<1000> hung up", SPS, "", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at 6000 , timed out", SPS, "", "Inbound"));
//
//        softAssertPlus.assertAll();
//    }
//
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("DirectedCallParking")
//    @Description("通过sps外线呼入到分机A-1000,1000接听" +
//            "29.分机A-1000按*056060将通话停泊\n" +
//            "\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；\n" +
//            "\t\t分机C-1002拨打6060接回通话\n" +
//            "\t\t\t分机C与外线正常通话，分机C挂断，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "DirectedCallParking"})
//    public void testCallParking_29_DirectedCallParking() {
//        prerequisite();
//
//        apiUtil.editInbound("In1",String.format("\"def_dest\":\"extension\",\"def_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1000").id)).apply();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 2000" + ",[callee] 991000");
//        pjsip.Pj_Make_Call_No_Answer(2000, "991000");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        sleep(5000);
//        step("分机A-1000按*056060将通话停泊");
//        pjsip.Pj_Send_Dtmf(1000,"*","0","5","6","0","6","0");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机C-1002拨打6060接回通话");
//        pjsip.Pj_Make_Call_No_Answer(1002, "6060");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(3000);
//        pjsip.Pj_hangupCall(1002);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(2)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6060 , testta C<1002> hung up", SPS, "", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at 6060", SPS, "", "Inbound"));
//
//        softAssertPlus.assertAll();
//    }
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("DirectedCallParking")
//    @Description("通过sps外线呼入到分机A-1000,1000接听" +
//            "30.分机A-1000按*056060将通话停泊\n" +
//            "\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；\n" +
//            "分机1001拨打1002,1002接听，1002拨打*056060将通话停泊\n" +
//            "\t\tasterisk 播放提示音call-parked-failed-slotused.slin，分机1001、1002保持通话；挂断所有通话，检查cdr ")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "DirectedCallParking"})
//    public void testCallParking_30_DirectedCallParking() {
//        prerequisite();
//        apiUtil.editFeatureCode("\"enb_park\": 1,\"park\":\"*******\"").apply();
//
//        asteriskObjectList.clear();
//        asteriskObjectListSecond.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        SSHLinuxUntils.AsteriskThread threadSecond=new SSHLinuxUntils.AsteriskThread(asteriskObjectListSecond,"call-parked-failed-slotused.slin");
//        thread.start();
//        threadSecond.start();
//
//        step("[caller] 2000" + ",[callee] 991000");
//        pjsip.Pj_Make_Call_No_Answer(2000, "991000");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机A-1000按*056060将通话停泊");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(1000,"*","0","5","6","0","6","0");
//
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("通话状态校验 失败!").isIn(TALKING);
//
//        step("挂断");
//        pjsip.Pj_hangupCall(1000);
//
//        step("分机1001拨打1002,1002接听，1002拨打*056060将通话停泊");
//        pjsip.Pj_Make_Call_No_Answer(1001, "1002");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1002, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1002, false);
//        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("1002拨打*056060将通话停泊");
//        sleep(2000);
//        pjsip.Pj_Send_Dtmf(1002,"*","0","5","6","0","6","0");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1001, TALKING, 30)).as("通话状态校验 自动挂断失败!").isIn(TALKING);
//        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        pjsip.Pj_hangupCall(2000);
//        pjsip.Pj_hangupCall(1001);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(5)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_1002.toString(), "*", CDRObject.STATUS.ANSWER.toString(), "test2 B<1001> hung up", "", "", "Internal"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "test2 B<1001> parked at 6060", "", "", "Internal"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at", SPS, "", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6060 , testta C<1002> hung up", SPS, "", "Inbound"));
//
//        softAssertPlus.assertAll();
//    }
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("DirectedCallParking")
//    @Description("31.修改In1的目的地为IVR0\n" +
//            "\t通过SIP外线呼入到IVR0，按0，分机1000响铃，接听\n" +
//            "\t\t分机A-1000按*056001将通话停泊\n" +
//            "\t\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；\n" +
//            "\t\t\t\t分机C-1002拨打6001接回通话\n" +
//            "\t\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "DirectedCallParking"})
//    public void testCallParking_31_DirectedCallParking() {
//        prerequisite();
//
//        apiUtil.editInbound("In1",String.format("\"def_dest\":\"ivr\",\"def_dest_value\":\"%s\"",apiUtil.getIVRSummary("6200").id)).apply();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 3001" + ",[callee] 3000");
//        pjsip.Pj_Make_Call_No_Answer(3001 ,"3000");
//
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(3001,"0");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, RING, 30)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机A-1000按*056001将通话停泊");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(1000,"*","0","5","6","0","0","1");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("[caller] 1002" + ",[callee] 6001");
//        pjsip.Pj_Make_Call_No_Answer(1002, "6001");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(3*1000);
//        pjsip.Pj_hangupCall(1002);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple( CDRObject.CDRNAME.Extension_3001.toString(),CDRObject.CDRNAME.Extension_1002.toString(),CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6001 , testta C<1002> hung up",SIPTrunk,"", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_3001.toString(), "13001", CDRObject.STATUS.ANSWER.toString(), "13001 parked at 6000", "", SIPTrunk, "Outbound"));
//
//        softAssertPlus.assertAll();
//    }
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("DirectedCallParking")
//    @Description("32.修改In1的目的地为RingGroup0\n" +
//            "\t通过Account外线呼入到RingGroup0,1000、1001、1003同时响铃，1003接听\n" +
//            "\t\t分机1003按*056002将通话停泊\n" +
//            "\t\t\tasterisk播放提示音call-parked-at.slin，一会儿后1003自动挂断，外线保持通话状态；\n" +
//            "\t\t\t\t分机D-1003拨打6002接回通话\n" +
//            "\t\t\t\t\t分机D与外线正常通话，外线挂断，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "DirectedCallParking"})
//    public void testCallParking_32_DirectedCallParking() {
//        prerequisite();
//
//        apiUtil.editInbound("In1",String.format("\"def_dest\":\"ring_group\",\"def_dest_value\":\"%s\"",apiUtil.getRingGroupSummary("6300").id)).apply();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 4000" + ",[callee] 441000");
//        pjsip.Pj_Make_Call_No_Answer(4000 ,"441000");
//
//        step("[通话状态校验]");
//        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
//        Assert.assertEquals(getExtensionStatus(1001, RING, 30), RING);
//        Assert.assertEquals(getExtensionStatus(1003, RING, 30), RING);
//        pjsip.Pj_Answer_Call(1003, false);
//        Assert.assertEquals(getExtensionStatus(1003, TALKING, 30), TALKING);
//
//        step("分机1003按*056002将通话停泊");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(1003,"*","0","5","6","0","0","2");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1003, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(4000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机D-1003拨打6002接回通话");
//        pjsip.Pj_Make_Call_No_Answer(1003, "6002");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1003, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(3*1000);
//        pjsip.Pj_hangupCall(1003);
//        pjsip.Pj_hangupCall(4000);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple( CDRObject.CDRNAME.Extension_4000.toString(),CDRObject.CDRNAME.Extension_1003.toString(),CDRObject.STATUS.ANSWER.toString(), "testa D<1003> retrieved from 6002 , testa D<1003> hung up",ACCOUNTTRUNK,"", "Inbound"))
//                .contains(tuple( CDRObject.CDRNAME.Extension_4000.toString(),CDRObject.CDRNAME.RINGGROUP0_6300.toString(),CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6000 , testta C<1002> hung up",ACCOUNTTRUNK,"", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_4000.toString(),CDRObject.CDRNAME.Extension_1003.toString(), CDRObject.STATUS.ANSWER.toString(), "4000<4000> parked at 6002", ACCOUNTTRUNK, "", "Inbound"));
//
//        softAssertPlus.assertAll();
//    }
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("DirectedCallParking")
//    @Description("33.修改In1的目的地为Queue0\n" +
//            "\t通过sps外线呼入到Queue0,1000、1001、1003、1004同时响铃，1004接听\n" +
//            "\t\t分机1004按*056003将通话停泊\n" +
//            "\t\t\tasterisk播放提示音call-parked-at.slin，一会儿后1004自动挂断，外线保持通话状态；\n" +
//            "\t\t\t\t分机D-1003拨打6003接回通话\n" +
//            "\t\t\t\t\t分机D与外线正常通话，外线挂断，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "DirectedCallParking"})
//    public void testCallParking_33_DirectedCallParking() {
//        prerequisite();
//
//        apiUtil.editInbound("In1",String.format("\"def_dest\":\"queue\",\"def_dest_value\":\"%s\"",apiUtil.getQueueSummary("6400").id)).apply();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 2000" + ",[callee] 991000");
//        pjsip.Pj_Make_Call_No_Answer(2000 ,"991000");
//
//        step("[通话状态校验]");
//        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
//        Assert.assertEquals(getExtensionStatus(1001, RING, 30), RING);
//        Assert.assertEquals(getExtensionStatus(1003, RING, 30), RING);
//        Assert.assertEquals(getExtensionStatus(1004, RING, 30), RING);
//        pjsip.Pj_Answer_Call(1004, false);
//        Assert.assertEquals(getExtensionStatus(1004, TALKING, 30), TALKING);
//
//        step("分机1004按*056003将通话停泊");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(1004,"*","0","5","6","0","0","3");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1004, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机D-1003拨打6003接回通话");
//        pjsip.Pj_Make_Call_No_Answer(1003, "6003");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1003, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(3*1000);
//        pjsip.Pj_hangupCall(1003);
//        pjsip.Pj_hangupCall(2000);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple( CDRObject.CDRNAME.Extension_2000.toString(),CDRObject.CDRNAME.Extension_1003.toString(),CDRObject.STATUS.ANSWER.toString(), "testa D<1003> retrieved from 6003 , testa D<1003> hung up",SPS,"", "Inbound"))
//                .contains(tuple( CDRObject.CDRNAME.Extension_2000.toString(),CDRObject.CDRNAME.QUEUE0_6400.toString(),CDRObject.STATUS.ANSWER.toString(), "Queue Queue0<6400> connected",SPS,"", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1004.toString(), CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at 6003",SPS, "", "Inbound"));
//
//        softAssertPlus.assertAll();
//    }
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("DirectedCallParking")
//    @Description("34.通过FXO外线呼入到分机A-1000,1000接听\n" +
//            "\t分机A-1000按*056010将通话停泊\n" +
//            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；\n" +
//            "\t\t\t分机C-1002拨打6010接回通话\n" +
//            "\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "DirectedCallParking"})
//    public void testCallParking_34_DirectedCallParking() {
//        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
//            Assert.assertTrue(false,"FXO 线路 不测！");
//        }
//        prerequisite();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 2000" + ",[callee] 2005");
//        pjsip.Pj_Make_Call_No_Answer(2000, "2005");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机A-1000按*056010将通话停泊");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(1000,"*","0","5","6","0","1","0");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机C-1002拨打6010接回通话");
//        pjsip.Pj_Make_Call_No_Answer(1002, "6010");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(3000);
//        pjsip.Pj_hangupCall(1002);
//        pjsip.Pj_hangupCall(2000);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6010 , testta C<1002> hung up", FXO_1, "", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at 6010", FXO_1, "", "Inbound"));
//        softAssertPlus.assertAll();
//    }
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("DirectedCallParking")
//    @Description("35.通过BRI外线呼入到分机A-1000,1000接听\n" +
//            "\t分机A-1000按*056020将通话停泊\n" +
//            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；\n" +
//            "\t\t\t分机C-1002拨打6020接回通话\n" +
//            "\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("BRI call From 显示异常")
//    @Test(groups = {"PSeries", "FeatureCode-CallParking","P3", "DirectedCallParking"})
//    public void testCallParking_35_DirectedCallParking() {
//        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
//            Assert.assertTrue(false,"BRI_1 线路 不测！");
//        }
//        prerequisite();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 2000" + ",[callee] 881000");
//        pjsip.Pj_Make_Call_No_Answer(2000, "881000");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机A-1000按*056020将通话停泊");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(1000,"*","0","5","6","0","2","0");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机C-1002拨打6020接回通话");
//        pjsip.Pj_Make_Call_No_Answer(1002, "6020");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(3000);
//        pjsip.Pj_hangupCall(1002);
//        pjsip.Pj_hangupCall(2000);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6020 , testta C<1002> hung up", BRI_1, "", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at 6020", BRI_1, "", "Inbound"));
//
//        softAssertPlus.assertAll();
//    }
//    @Epic("P_Series")
//    @Feature("FeatureCode-CallParking")
//    @Story("DirectedCallParking")
//    @Description("36.通过E1外线呼入到分机A-1000,1000接听\n" +
//            "\t分机A-1000按*056030将通话停泊\n" +
//            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；\n" +
//            "\t\t\t分机C-1002拨打6030接回通话\n" +
//            "\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr")
//    @Severity(SeverityLevel.BLOCKER)
//    @TmsLink(value = "")
//    @Issue("")
//    @Test(groups = {"PSeries", "FeatureCode-CallParking","P3", "DirectedCallParking"})
//    public void testCallParking_36_DirectedCallParking() {
//        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
//            Assert.assertTrue(false,"E1 线路 不测！");
//        }
//        prerequisite();
//
//        asteriskObjectList.clear();
//        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
//        thread.start();
//
//        step("[caller] 2000" + ",[callee] 661000");
//        pjsip.Pj_Make_Call_No_Answer(2000, "661000");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
//        pjsip.Pj_Answer_Call(1000, false);
//        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机A-1000按*056030将通话停泊");
//        sleep(5000);
//        pjsip.Pj_Send_Dtmf(1000,"*","0","5","6","0","3","0");
//
//        int tmp = 0;
//        while (asteriskObjectList.size() != 1 && tmp <=800) {
//            sleep(50);
//            tmp++;
//            log.debug("[tmp]_" + tmp);
//        }
//        if (tmp == 801) {
//            for (int i = 0; i < asteriskObjectList.size(); i++) {
//                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
//            }
//            thread.flag = false;
//            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
//        }
//        thread.flag = false;
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
//        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("分机C-1002拨打6020接回通话");
//        pjsip.Pj_Make_Call_No_Answer(1002, "6030");
//
//        step("[通话状态校验]");
//        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);
//
//        step("挂断");
//        sleep(3000);
//        pjsip.Pj_hangupCall(1002);
//        pjsip.Pj_hangupCall(2000);
//
//        assertStep("[CDR校验]");
//        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6030 , testta C<1002> hung up", E1, "", "Inbound"))
//                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at 6030", E1, "", "Inbound"));
//
//        softAssertPlus.assertAll();
//    }
//
    @Epic("P_Series")
    @Feature("FeatureCode-CallParking")
    @Story("DirectedCallParking")
    @Description("37.通过GSM外线呼入到分机A-1000,1000接听\n" +
            "\t分机A-1000按*056040将通话停泊\n" +
            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；\n" +
            "\t\t\t分机C-1002拨打6040接回通话\n" +
            "\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "DirectedCallParking"})
    public void testCallParking_37_DirectedCallParking() {
        if((GSM.trim().equalsIgnoreCase("null") || GSM.trim().equalsIgnoreCase("")) && !DEVICE_TEST_GSM.equals("") && !DEVICE_ASSIST_GSM.equals("")){
            Assert.assertTrue(false,"GSM线路 不测试！");
        }
        prerequisite();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
        thread.start();

        step("[caller] 2000" + ",[callee] 33"+DEVICE_TEST_GSM);//TODO NO answer
        pjsip.Pj_Make_Call_No_Answer(2000, "33"+DEVICE_TEST_GSM);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 150)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机A-1000按*056040将通话停泊");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1000,"*","0","5","6","0","4","0");

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=800) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机C-1002拨打6040接回通话");
        pjsip.Pj_Make_Call_No_Answer(1002, "6040");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("挂断");
        sleep(90*1000);
        pjsip.Pj_hangupCall(1002);
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(),  CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6040 , testta C<1002> hung up", GSM, "", "Inbound"))
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at 6000", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("FeatureCode-CallParking")
    @Story("DirectedCallParking")
    @Description("分机A-1000拨打分机B-1001，1001接听" +
            "38.分机A-1000按*056099将通话停泊\n" +
            "\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，分机B保持通话状态；\n" +
            "\t\t分机C-1002拨打6099接回通话\n" +
            "\t\t\t分机BC正常通话，分机C挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "DirectedCallParking"})
    public void testCallParking_38_DirectedCallParking() {
        prerequisite();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
        thread.start();

        step("[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001");

        step("[通话状态校验]");//todo no answer
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机A-1000按*056099将通话停泊");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1000,"*","0","5","6","0","9","9");

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=800) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机C-1002拨打6099接回通话");
        pjsip.Pj_Make_Call_No_Answer(1002, "6099");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("挂断");
        sleep(3000);
        pjsip.Pj_hangupCall(1000);
        pjsip.Pj_hangupCall(1002);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6000 , testta C<1002> hung up", SPS, "", "Inbound"))
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at 6000", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("FeatureCode-CallParking")
    @Story("DirectedCallParking")
    @Description("分机A-1000拨打分机B-1001，1001接听" +
            "39.分机B-1001按*056098将通话停泊\n" +
            "\tasterisk播放提示音call-parked-at.slin，一会儿后1001自动挂断，分机1000保持通话状态；\n" +
            "\t\t分机B-1001拨打6098接回通话\n" +
            "\t\t\t分机AB正常通话，分机A挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "DirectedCallParking"})
    public void testCallParking_39_DirectedCallParking() {
        prerequisite();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
        thread.start();

        step("[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1001, false);
        assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机A-1000按*056098将通话停泊");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1000,"*","0","5","6","0","9","8");

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=800) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);//todo failed
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机C-1002拨打6098接回通话");
        pjsip.Pj_Make_Call_No_Answer(1002, "6098");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("挂断");
        sleep(3000);
        pjsip.Pj_hangupCall(1000);
        pjsip.Pj_hangupCall(1002);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6000 , testta C<1002> hung up", SPS, "", "Inbound"))
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at 6000", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("FeatureCode-CallParking")
    @Story("DirectedCallParking")
    @Description("40.分机1001拨打13001通过SIP外线呼出，被叫应答\n" +
            "\t分机1001按*056099将通话停泊\n" +
            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后1001自动挂断，外线保持通话状态；\n" +
            "\t\t\t分机C-1002拨打6099接回通话\n" +
            "\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "DirectedCallParking"})
    public void testCallParking_40_DirectedCallParking() {
        prerequisite();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
        thread.start();

        step("[caller] 1001" + ",[callee] 13001");
        pjsip.Pj_Make_Call_No_Answer(1001, "13001");

        step("[通话状态校验]");//todo NullPointerException
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机A-1000按*056099将通话停泊");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1001,"*","0","5","6","0","9","9");

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=800) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机C-1002拨打6099接回通话");
        pjsip.Pj_Make_Call_No_Answer(1002, "6099");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("挂断");
        sleep(3000);
        pjsip.Pj_hangupCall(1002);
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6000 , testta C<1002> hung up", SPS, "", "Inbound"))
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at 6000", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("FeatureCode-CallParking")
    @Story("DirectedCallParking")
    @Description("41.分机1001拨打22001通过SPS外线呼出，被叫应答\n" +
            "\t分机1001按*056000将通话停泊\n" +
            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后1001自动挂断，外线保持通话状态；\n" +
            "\t\t\t分机C-1002拨打6000接回通话\n" +
            "\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "DirectedCallParking"})
    public void testCallParking_41_DirectedCallParking() {
        prerequisite();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
        thread.start();

        step("[caller] 1001" + ",[callee] 22001");
        pjsip.Pj_Make_Call_No_Answer(1001, "22001");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机A-1000按*056000将通话停泊");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1001,"*","0","5","6","0","0","0");

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=800) {//todo no found
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机C-1002拨打6000接回通话");
        pjsip.Pj_Make_Call_No_Answer(1002, "6000");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("挂断");
        sleep(3000);
        pjsip.Pj_hangupCall(1002);
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("22001", CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6000 , testta C<1002> hung up", SPS, "", "Inbound"))
                .contains(tuple(CDRObject.CDRNAME.Extension_1001.toString(), "22001", CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at 6000", SPS, "", "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("FeatureCode-CallParking")
    @Story("DirectedCallParking")
    @Description("42.分机1001拨打33001通过Account外线呼出，被叫应答\n" +
            "\t分机1001按*056088将通话停泊\n" +
            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后1001自动挂断，外线保持通话状态；\n" +
            "\t\t\t分机C-1002拨打6088接回通话\n" +
            "\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "DirectedCallParking"})
    public void testCallParking_42_DirectedCallParking() {
        prerequisite();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
        thread.start();

        step("[caller] 1001" + ",[callee] 33001");
        pjsip.Pj_Make_Call_No_Answer(1001, "33001");

        step("[通话状态校验]");//todo NullPointerException
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机A-1000按*056088将通话停泊");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1001,"*","0","5","6","0","8","8");

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=800) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机C-1002拨打6088接回通话");
        pjsip.Pj_Make_Call_No_Answer(1002, "6088");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("挂断");
        sleep(3000);
        pjsip.Pj_hangupCall(1002);
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("22001", CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6000 , testta C<1002> hung up", SPS, "", "Inbound"))
                .contains(tuple(CDRObject.CDRNAME.Extension_1001.toString(), "22001", CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at 6000", SPS, "", "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("FeatureCode-CallParking")
    @Story("DirectedCallParking")
    @Description("43.分机1001拨打42000通过FXO外线呼出，被叫应答\n" +
            "\t分机1001按*056066将通话停泊\n" +
            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后1001自动挂断，外线保持通话状态；\n" +
            "\t\t\t分机C-1002拨打6066接回通话\n" +
            "\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "DirectedCallParking"})
    public void testCallParking_43_DirectedCallParking() {
        if(FXO_1.trim().equalsIgnoreCase("null") || FXO_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"FXO 线路 不测！");
        }
        prerequisite();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
        thread.start();

        step("[caller] 1001" + ",[callee] 33001");
        pjsip.Pj_Make_Call_No_Answer(1001, "42000");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机A-1000按*056066将通话停泊");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1001,"*","0","5","6","0","6","6");

        int tmp = 0;//todo nofound
        while (asteriskObjectList.size() != 1 && tmp <=800) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机C-1002拨打6066接回通话");
        pjsip.Pj_Make_Call_No_Answer(1002, "6066");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("挂断");
        sleep(3000);
        pjsip.Pj_hangupCall(1002);
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple("42000", CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6066 , testta C<1002> hung up", FXO_1, "", "Inbound"))
                .contains(tuple(CDRObject.CDRNAME.Extension_1001.toString(), "42000", CDRObject.STATUS.ANSWER.toString(), "42000 parked at 6066", "", FXO_1, "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("FeatureCode-CallParking")
    @Story("DirectedCallParking")
    @Description("44.分机1001拨打5555通过BRI外线呼出，被叫应答\n" +
            "\t分机1001按*056055将通话停泊\n" +
            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后1001自动挂断，外线保持通话状态；\n" +
            "\t\t\t分机C-1002拨打6055接回通话\n" +
            "\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("BRI callto 异常")
    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "DirectedCallParking"})
    public void testCallParking_44_DirectedCallParking() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"BRI_1 线路 不测！");
        }
        prerequisite();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
        thread.start();

        step("[caller] 1001" + ",[callee] 5555");
        pjsip.Pj_Make_Call_No_Answer(1001, "5555");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机A-1000按*056055将通话停泊");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1001,"*","0","5","6","0","5","5");

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=800) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机C-1002拨打6055接回通话");
        pjsip.Pj_Make_Call_No_Answer(1002, "6055");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("挂断");
        sleep(3000);
        pjsip.Pj_hangupCall(1002);
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6055 , testta C<1002> hung up", BRI_1, "", "Inbound"))
                .contains(tuple(CDRObject.CDRNAME.Extension_1001.toString(), "2000", CDRObject.STATUS.ANSWER.toString(), "2000 parked at 6055", "", BRI_1, "Outbound"));

        softAssertPlus.assertAll();

    }
    @Epic("P_Series")
    @Feature("FeatureCode-CallParking")
    @Story("DirectedCallParking")
    @Description("45.分机1001拨打6666通过E1外线呼出，被叫应答\n" +
            "\t分机1001按*056049将通话停泊\n" +
            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后1001自动挂断，外线保持通话状态；\n" +
            "\t\t\t分机C-1002拨打6049接回通话\n" +
            "\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("E1,callfrom 异常")
    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "DirectedCallParking"})
    public void testCallParking_45_DirectedCallParking() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"E1 线路 不测！");
        }
        prerequisite();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
        thread.start();

        step("[caller] 1001" + ",[callee] 6666");
        pjsip.Pj_Make_Call_No_Answer(1001, "6666");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机A-1000按*056049将通话停泊");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1001,"*","0","5","6","0","4","9");

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=800) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机C-1002拨打6049接回通话");
        pjsip.Pj_Make_Call_No_Answer(1002, "6049");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("挂断");
        sleep(3000);
        pjsip.Pj_hangupCall(1002);
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6049 , testta C<1002> hung up", E1, "", "Inbound"))
                .contains(tuple(CDRObject.CDRNAME.Extension_1001.toString(), "2000", CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at 6049", "", E1, "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("FeatureCode-CallParking")
    @Story("DirectedCallParking")
    @Description("46.分机1001拨打7+辅助2的GSM号码通过GSM外线呼出，被叫应答\n" +
            "\t分机1001按*056038将通话停泊\n" +
            "\t\tasterisk播放提示音call-parked-at.slin，一会儿后1001自动挂断，外线保持通话状态；\n" +
            "\t\t\t分机C-1002拨打6038接回通话\n" +
            "\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "DirectedCallParking"})
    public void testCallParking_46_DirectedCallParking() {
        if(GSM.trim().equalsIgnoreCase("null") || GSM.trim().equalsIgnoreCase("")){
            Assert.assertTrue(false,"GSM 线路 不测！");
        }
        prerequisite();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
        thread.start();

        step("[caller] 1001" + ",[callee] 7"+DEVICE_ASSIST_GSM);
        pjsip.Pj_Make_Call_No_Answer(1001, "7"+DEVICE_ASSIST_GSM);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(2000, RING, 200)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(2000, false);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机A-1000按*056038将通话停泊");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1001,"*","0","5","6","0","3","8");

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=800) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1001, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机C-1002拨打6038接回通话");
        pjsip.Pj_Make_Call_No_Answer(1002, "6038");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("挂断");
        sleep(3000);
        pjsip.Pj_hangupCall(1002);
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "", SPS, "", "Inbound"))
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at 6000", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("FeatureCode-CallParking")
    @Story("DirectedCallParking")
    @Description("编辑Feature->Directed CallParking 禁用\n" +
            "\t47.通过sps外线呼入到分机A-1000,1000接听；\n" +
            "分机1000按*056000将通话停泊；\n" +
            "\t\t停泊失败，分机1000与外线保持通话，挂断；检查cdr\n" +
            "\t\t\t编辑Feature->Directed Call Parking启用；\n" +
            "通过sps外线呼入到分机A-1000,1000接听；\n" +
            "分机1000按*056000将通话停泊；\n" +
            "\t\t\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；\n" +
            "\t\t\t\t\t分机C-1002拨打6000接回通话\n" +
            "\t\t\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "DirectedCallParking"})
    public void testCallParking_47_DirectedCallParking() {
        prerequisite();
        apiUtil.editFeatureCode("\"enb_park_on_slots\":0").apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
        thread.start();

        step("[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000");

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);

        step("分机1000按*056000将通话停泊");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1000,"*","0","5","6","0","0","0");

        assertThat(getExtensionStatus(1000, TALKING, 30)).as("通话状态校验 自动挂断失败!").isIn(TALKING);

        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1003.toString(), CDRObject.STATUS.ANSWER.toString(), "testa D<1003> retrieved from 6000 , testa D<1003> hung up", SPS, "", "Inbound"))
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.QUEUE0_6400.toString(), CDRObject.STATUS.ANSWER.toString(), "Queue Queue0<6400> connected", SPS, "", "Inbound"))
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1004.toString(), CDRObject.STATUS.ANSWER.toString(),"2000<2000> parked at 6000", SPS, "", "Inbound"));

        step("编辑Feature->Directed Call Parking启用");
        apiUtil.editFeatureCode("\"enb_park_on_slots\":1,\"park_on_slots\":\"*05\"").apply();

        step("[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000");

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);

        step("分机1000按*056000将通话停泊");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1000,"*","0","5","6","0","0","0");

        int tmp = 0;//todo no found
        while (asteriskObjectList.size() != 1 && tmp <=800) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("[caller] 1002" + ",[callee] 6000");
        pjsip.Pj_Make_Call_No_Answer(1002, "6000");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("挂断");
        sleep(2000);
        pjsip.Pj_hangupCall(1002);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1003.toString(), CDRObject.STATUS.ANSWER.toString(), "testa D<1003> retrieved from 6000 , testa D<1003> hung up", SPS, "", "Inbound"))
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.QUEUE0_6400.toString(), CDRObject.STATUS.ANSWER.toString(), "Queue Queue0<6400> connected", SPS, "", "Inbound"))
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1004.toString(), CDRObject.STATUS.ANSWER.toString(),"2000<2000> parked at 6000", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("FeatureCode-CallParking")
    @Story("DirectedCallParking")
    @Description("编辑Feature->Directed CallParking 禁用" +
            "48.分机1001拨打22001通过SPS外线呼出，被叫应答；\n" +
            "分机1001按*056001将通话停泊；\n" +
            "\t停泊失败，分机1001与外线保持通话，挂断；检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "DirectedCallParking"})
    public void testCallParking_48_DirectedCallParking() {
        prerequisite();
        apiUtil.editFeatureCode("\"enb_park_on_slots\":0").apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
        thread.start();

        step("[caller] 1001" + ",[callee] 22001");
        pjsip.Pj_Make_Call_No_Answer(1001, "22001");

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(2000, RING, 30), RING);
        pjsip.Pj_Answer_Call(2000, false);
        Assert.assertEquals(getExtensionStatus(2000, TALKING, 30), TALKING);

        step("分机1001按*056001将通话停泊");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1001,"*","0","5","6","0","0","1");

        assertThat(getExtensionStatus(1000, TALKING, 30)).as("通话状态校验 自动挂断失败!").isIn(TALKING);//todo failed

        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1003.toString(), CDRObject.STATUS.ANSWER.toString(), "testa D<1003> retrieved from 6000 , testa D<1003> hung up", SPS, "", "Inbound"))
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.QUEUE0_6400.toString(), CDRObject.STATUS.ANSWER.toString(), "Queue Queue0<6400> connected", SPS, "", "Inbound"))
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1004.toString(), CDRObject.STATUS.ANSWER.toString(),"2000<2000> parked at 6000", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("FeatureCode-CallParking")
    @Story("DirectedCallParking")
    @Description("49.编辑Feature->Directed CallParking 修改特征码为#******\n" +
            "\t通过sps外线呼入到分机A-1000,1000接听；\n" +
            "分机1000按*056000将通话停泊；\n" +
            "\t\t停泊失败，分机1000与外线保持通话，挂断；检查cdr\n" +
            "\t\t\t分机1000按#******6000将通话停泊；\n" +
            "\t\t\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；\n" +
            "\t\t\t\t\t分机C-1002拨打6000接回通话\n" +
            "\t\t\t\t\t\t分机C与外线正常通话，分机C挂断，检查cdr\n" +
            "\t\t\t\t\t\t\t编辑Feature->Directed Call Parking 修改特征码为*05\n" +
            "通过sps外线呼入到分机A-1000,1000接听；\n" +
            "分机1000按*056000将通话停泊；\n" +
            "\t\t\t\t\t\t\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；主叫挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "DirectedCallParking"})
    public void testCallParking_49_DirectedCallParking() {
        prerequisite();
        apiUtil.editFeatureCode("\"enb_park\": 1,\"park\":\"#******\"").apply();

        asteriskObjectList.clear();
        asteriskObjectListSecond.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
        SSHLinuxUntils.AsteriskThread threadSecond=new SSHLinuxUntils.AsteriskThread(asteriskObjectListSecond,CALL_PARKED_AT);
        thread.start();
        threadSecond.start();

        step("[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机1000按*056000将通话停泊");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1000,"*","0","5","6","0","0","0");

        assertThat(getExtensionStatus(1000, TALKING, 30)).as("通话状态校验 失败!").isIn(TALKING);

        step("挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), CDRObject.CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        step("[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机1000按#******6000将通话停泊");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1000,"#","*","*","*","*","*","*","6","0","0","0");

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=800) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("[caller] 1002" + ",[callee] 6000");
        pjsip.Pj_Make_Call_No_Answer(1002, "6000", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1002, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("挂断");
        sleep(3000);
        pjsip.Pj_hangupCall(1002);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), CDRObject.CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        apiUtil.editFeatureCode("\"enb_park\": 1,\"park\":\"*5\"").apply();

        step("[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(5000);
        step("分机1000按*056000将通话停泊");
        pjsip.Pj_Send_Dtmf(1000,"*","0","5","6","0","0","0");

        tmp = 0;
        while (asteriskObjectListSecond.size() != 2 && tmp <=800) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectListSecond.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectListSecond.get(i).getName() + " [asterisk object time] " + asteriskObjectListSecond.get(i).getTime() + "[asterisk object tag] " + asteriskObjectListSecond.get(i).getTag());
            }
            threadSecond.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectListSecond.size());
        }
        threadSecond.flag = false;

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6000 , testta C<1002> hung up", SPS, "", "Inbound"))
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at 6000", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("FeatureCode-CallParking")
    @Story("TimeoutDestination")
    @Description("编辑Feature Code->Call Parking->Timeout Destination 选择分机1005；\n" +
            "Parking Timeout (s) 设置10s" +
            "通过sps外线呼入到分机A-1000,1000接听" +
            "50.分机A-1000按*056077将通话停泊\n" +
            "\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；\n" +
            "10s之后分机1005响铃，接听，分机1005与外线正常通话，挂断，检查cdr;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "TimeoutDestination","Extension"})
    public void testCallParking_50_TimeoutDestination() {
        prerequisite();
        apiUtil.editFeatureCode(String.format("\"park_timeout\":10,\"park_timeout_dest\":\"extension\",\"park_timeout_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
        thread.start();

        step("[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机A-1000按*056077将通话停泊");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1000,"*","0","5","6","0","7","7");

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=800) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;

        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 失败!").isIn(HUNGUP,IDLE);

        sleep(10*1000);
        assertThat(getExtensionStatus(1005, RING, 5)).as("通话状态校验 失败!").isIn(RING);
        pjsip.Pj_Answer_Call(1005);
        assertThat(getExtensionStatus(1005,TALKING , 5)).as("通话状态校验 失败!").isIn(TALKING);
        sleep(5*1000);

        step("挂断");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1005.toString(), CDRObject.STATUS.ANSWER.toString(), CDRObject.CDRNAME.Extension_2000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("FeatureCode-CallParking")
    @Story("TimeoutDestination")
    @Description("编辑Feature Code->Call Parking->Timeout Destination 选择分机1005；\n" +
            "Parking Timeout (s) 设置10s" +
            "通过sps外线呼入到分机A-1000,1000接听" +
            "51.分机A-1000按*5将通话停泊\n" +
            "\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；\n" +
            "10s之后分机1005响铃，接听，分机1005与外线正常通话，挂断，检查cdr;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "TimeoutDestination","Extension"})
    public void testCallParking_51_TimeoutDestination() {
        prerequisite();
        apiUtil.editFeatureCode(String.format("\"park_timeout\":10,\"park_timeout_dest\":\"extension\",\"park_timeout_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
        thread.start();

        step("[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机A-1000按*5将通话停泊");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1000,"*","5");

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=800) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;

        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 失败!").isIn(HUNGUP,IDLE);

        sleep(10*1000);
        assertThat(getExtensionStatus(1005, RING, 5)).as("通话状态校验 失败!").isIn(RING);
        pjsip.Pj_Answer_Call(1005);
        assertThat(getExtensionStatus(1005,TALKING , 5)).as("通话状态校验 失败!").isIn(TALKING);
        sleep(5*1000);

        step("挂断");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1005.toString(), CDRObject.STATUS.ANSWER.toString(), CDRObject.CDRNAME.Extension_2000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("FeatureCode-CallParking")
    @Story("TimeoutDestination")
    @Description("编辑Feature Code->Call Parking->Timeout Destination 选择分机1005；\n" +
            "Parking Timeout (s) 设置10s" +
            "分机1001拨打13001通过SIP外线呼出，被叫应答" +
            "52.分机1001按*056078将通话停泊\n" +
            "\tasterisk播放提示音call-parked-at.slin，一会儿后1001自动挂断，外线保持通话状态；\n" +
            "10s之后分机1005响铃，接听，分机1005与外线正常通话，挂断，检查cdr;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "TimeoutDestination","Extension"})
    public void testCallParking_52_TimeoutDestination() {
        prerequisite();
        apiUtil.editFeatureCode(String.format("\"park_timeout\":10,\"park_timeout_dest\":\"extension\",\"park_timeout_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
        thread.start();

        step("[caller] 1001" + ",[callee] 13001");
        pjsip.Pj_Make_Call_No_Answer(1001, "13001");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机1001按*056078将通话停泊");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1001,"*","0","5","6","0","7","8");

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=800) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;

        assertThat(getExtensionStatus(1001, HUNGUP, 30)).as("通话状态校验 失败!").isIn(HUNGUP,IDLE);

        sleep(10*1000);
        assertThat(getExtensionStatus(1005, RING, 5)).as("通话状态校验 失败!").isIn(RING);
        pjsip.Pj_Answer_Call(1005);
        assertThat(getExtensionStatus(1005,TALKING , 5)).as("通话状态校验 失败!").isIn(TALKING);
        sleep(5*1000);

        step("挂断");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_1001.toString(), "13001", CDRObject.STATUS.ANSWER.toString(),  "13001 parked at 6078", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("FeatureCode-CallParking")
    @Story("TimeoutDestination")
    @Description("编辑Feature Code->Call Parking->Timeout Destination 选择分机1005；\n" +
            "Parking Timeout (s) 设置10s" +
            "分机1001拨打13001通过SIP外线呼出，被叫应答" +
            "53.分机1001按*5将通话停泊\n" +
            "\tasterisk播放提示音call-parked-at.slin，一会儿后1001自动挂断，外线保持通话状态；\n" +
            "10s之后分机1005响铃，接听，分机1005与外线正常通话，挂断，检查cdr;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "TimeoutDestination","Extension"})
    public void testCallParking_53_TimeoutDestination() {
        prerequisite();
        apiUtil.editFeatureCode(String.format("\"park_timeout\":10,\"park_timeout_dest\":\"extension\",\"park_timeout_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
        thread.start();

        step("[caller] 1001" + ",[callee] 13001");
        pjsip.Pj_Make_Call_No_Answer(1001, "13001");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(3001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(3001, false);
        assertThat(getExtensionStatus(3001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机1001按*5将通话停泊");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1001,"*","5");

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=800) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;

        assertThat(getExtensionStatus(1001, HUNGUP, 30)).as("通话状态校验 失败!").isIn(HUNGUP,IDLE);

        sleep(10*1000);
        assertThat(getExtensionStatus(1005, RING, 5)).as("通话状态校验 失败!").isIn(RING);
        pjsip.Pj_Answer_Call(1005);
        assertThat(getExtensionStatus(1005,TALKING , 5)).as("通话状态校验 失败!").isIn(TALKING);
        sleep(5*1000);

        step("挂断");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_1001.toString(), "13001", CDRObject.STATUS.ANSWER.toString(), "13001 parked at 6000", "", SIPTrunk, "Outbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("FeatureCode-CallParking")
    @Story("TimeoutDestination")
    @Description("编辑Feature Code->Call Parking->Timeout Destination 选择Voicemail-1005；\n" +
            "Parking Timeout (s) 设置10s\n" +
            "\t通过sps外线呼入到分机A-1000,1000接听\n" +
            "\t\t54.分机A-1000按*056077将通话停泊\n" +
            "\t\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；\n" +
            "10s之后进入到分机1005的语音留言，通话15s，挂断，登录分机1005查看新增一条语音留言;\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "TimeoutDestination","Voicemail"})
    public void testCallParking_54_TimeoutDestination() {
        prerequisite();
        apiUtil.editFeatureCode(String.format("\"park_timeout\":10,\"park_timeout_dest\":\"ext_vm\",\"park_timeout_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
        thread.start();

        step("[caller] 2000" + ",[callee] 991000");
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机A-1000按*056077将通话停泊");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1000,"*","0","5","6","0","7","7");

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=800) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;

        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 失败!").isIn(HUNGUP,IDLE);

        sleep(10*1000);

        assertThat(getExtensionStatus(2000,TALKING , 5)).as("通话状态校验 失败!").isIn(TALKING);
        sleep(15*1000);

        step("挂断");
        pjsip.Pj_hangupCall(2000);

        auto.loginPage().login("1005",EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT*2);
        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        sleep(3000);
        Assert.assertTrue(TableUtils.getTableForHeader(getDriver(),"Name",0).contains("2000"),"没有生成语音留言");

        sleep(3000);
        String voiceMailTime =TableUtils.getTableForHeader(getDriver(),"Time",0);
        log.debug("[callTime] " + callTime+" ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), CDRObject.CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("FeatureCode-CallParking")
    @Story("TimeoutDestination")
    @Description("编辑Feature Code->Call Parking->Timeout Destination 选择Voicemail-1005；\n" +
            "Parking Timeout (s) 设置10s\n" +
            "\t通过sps外线呼入到分机A-1000,1000接听\n" +
            "\t\t55.分机A-1000按*5将通话停泊\n" +
            "\t\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；\n" +
            "10s之后进入到分机1005的语音留言，通话15s，挂断，登录分机1005查看新增一条语音留言;\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "TimeoutDestination","Voicemail"})
    public void testCallParking_55_TimeoutDestination() {
        prerequisite();
        apiUtil.editFeatureCode(String.format("\"park_timeout\":10,\"park_timeout_dest\":\"ext_vm\",\"park_timeout_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
        thread.start();

        step("[caller] 2000" + ",[callee] 991000");
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机A-1000按*056077将通话停泊");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1000,"*","5");

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=800) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;

        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 失败!").isIn(HUNGUP,IDLE);

        sleep(10*1000);
        assertThat(getExtensionStatus(2000, RING, 5)).as("通话状态校验 失败!").isIn(RING);
        pjsip.Pj_Answer_Call(2000);
        assertThat(getExtensionStatus(2000,TALKING , 5)).as("通话状态校验 失败!").isIn(TALKING);
        sleep(15*1000);

        step("挂断");
        pjsip.Pj_hangupCall(2000);

        auto.loginPage().login("1005",EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT*2);
        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        sleep(3000);
        Assert.assertTrue(TableUtils.getTableForHeader(getDriver(),"Name",0).contains("2000"),"没有生成语音留言");

        sleep(3000);
        String voiceMailTime =TableUtils.getTableForHeader(getDriver(),"Time",0);
        log.debug("[callTime] " + callTime+" ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), CDRObject.CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("FeatureCode-CallParking")
    @Story("TimeoutDestination")
    @Description("编辑Feature Code->Call Parking->Timeout Destination 选择Voicemail-1005；\n" +
            "Parking Timeout (s) 设置10s\n" +
            "\t通过sps外线呼入到分机A-1000,1000接听\n" +
            "\t\t56.分机1001按*056078将通话停泊\n" +
            "\t\t\tasterisk播放提示音call-parked-at.slin，一会儿后1001自动挂断，外线保持通话状态；\n" +
            "10s之后进入到分机1005的语音留言，通话15s，挂断，登录分机1005查看新增一条语音留言;\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "TimeoutDestination","Voicemail"})
    public void testCallParking_56_TimeoutDestination() {
        prerequisite();
        apiUtil.editFeatureCode(String.format("\"park_timeout\":10,\"park_timeout_dest\":\"ext_vm\",\"park_timeout_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
        thread.start();

        step("[caller] 2000" + ",[callee] 991000");
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机1001按*056078将通话停泊");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1001,"*","0","5","6","0","7","8");

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=800) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;

        assertThat(getExtensionStatus(1001, HUNGUP, 30)).as("通话状态校验 失败!").isIn(HUNGUP,IDLE);

        sleep(10*1000);
        assertThat(getExtensionStatus(2000,TALKING , 5)).as("通话状态校验 失败!").isIn(TALKING);
        sleep(15*1000);

        step("挂断");
        pjsip.Pj_hangupCall(2000);

        auto.loginPage().login("1005",EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT*2);
        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        sleep(3000);
        Assert.assertTrue(TableUtils.getTableForHeader(getDriver(),"Name",0).contains("2000"),"没有生成语音留言");

        sleep(3000);
        String voiceMailTime =TableUtils.getTableForHeader(getDriver(),"Time",0);
        log.debug("[callTime] " + callTime+" ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), CDRObject.CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("FeatureCode-CallParking")
    @Story("TimeoutDestination")
    @Description("编辑Feature Code->Call Parking->Timeout Destination 选择Voicemail-1005；\n" +
            "Parking Timeout (s) 设置10s\n" +
            "\t通过sps外线呼入到分机A-1000,1000接听\n" +
            "\t\t57.分机1001按*5将通话停泊\n" +
            "\t\t\tasterisk播放提示音call-parked-at.slin，一会儿后1001自动挂断，外线保持通话状态；\n" +
            "10s之后进入到分机1005的语音留言，通话15s，挂断，登录分机1005查看新增一条语音留言;")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "TimeoutDestination","Voicemail"})
    public void testCallParking_57_TimeoutDestination() {
        prerequisite();
        apiUtil.editFeatureCode(String.format("\"park_timeout\":10,\"park_timeout_dest\":\"ext_vm\",\"park_timeout_dest_value\":\"%s\"",apiUtil.getExtensionSummary("1005").id)).apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
        thread.start();

        step("[caller] 2000" + ",[callee] 991000");
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机1001按*5将通话停泊");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1001,"*","5");

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=800) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;

        assertThat(getExtensionStatus(1001, HUNGUP, 30)).as("通话状态校验 失败!").isIn(HUNGUP,IDLE);

        sleep(10*1000);
        assertThat(getExtensionStatus(2000,TALKING , 5)).as("通话状态校验 失败!").isIn(TALKING);
        sleep(15*1000);

        step("挂断");
        pjsip.Pj_hangupCall(2000);

        auto.loginPage().login("1005",EXTENSION_PASSWORD_NEW);
        sleep(WaitUntils.SHORT_WAIT*2);
        auto.homePage().intoPage(HomePage.Menu_Level_1.voicemails);
        sleep(3000);
        Assert.assertTrue(TableUtils.getTableForHeader(getDriver(),"Name",0).contains("2000"),"没有生成语音留言");

        sleep(3000);
        String voiceMailTime =TableUtils.getTableForHeader(getDriver(),"Time",0);
        log.debug("[callTime] " + callTime+" ,[voiceMailTime] " + voiceMailTime);
        softAssertPlus.assertThat(LocalTime.parse(voiceMailTime)).isAfter(callTime);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), CDRObject.CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("FeatureCode-CallParking")
    @Story("TimeoutDestination")
    @Description("编辑Feature Code->Call Parking->Timeout Destination 选择外部号码，前缀2 号码2222；\n" +
            "Parking Timeout (s) 设置10s\n" +
            "\t通过sip外线呼入到分机A-1000,1000接听\n" +
            "\t\t58.分机A-1000按*056077将通话停泊\n" +
            "\t\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；\n" +
            "10s之后呼叫外部号码22222，辅助2分机2000响铃，接听，挂断，检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "TimeoutDestination","External Number"})
    public void testCallParking_58_TimeoutDestination() {
        prerequisite();
        apiUtil.editFeatureCode("\"park_timeout\":10,\"park_timeout_dest\":\"external_num\",\"park_timeout_dest_value\":\"2222\",\"park_timeout_dest_prefix\":\"2\"").apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
        thread.start();

        step("[caller] 3001" + ",[callee] 3000");
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机A-1000按*056077将通话停泊");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1000,"*","0","5","6","0","7","7");

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=800) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;

        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 失败!").isIn(HUNGUP,IDLE);

        sleep(15*1000);
        assertThat(getExtensionStatus(2000, RING, 5)).as("通话状态校验 失败!").isIn(RING);
        pjsip.Pj_Answer_Call(2000);
        assertThat(getExtensionStatus(2000,TALKING , 5)).as("通话状态校验 失败!").isIn(TALKING);
        sleep(5*1000);

        step("挂断");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), CDRObject.CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("FeatureCode-CallParking")
    @Story("TimeoutDestination")
    @Description("编辑Feature Code->Call Parking->Timeout Destination 选择外部号码，前缀2 号码2222；\n" +
            "Parking Timeout (s) 设置10s\n" +
            "\t通过sip外线呼入到分机A-1000,1000接听\n" +
            "\t\t59.分机A-1000按*5将通话停泊\n" +
            "\t\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；\n" +
            "10s之后呼叫外部号码22222，辅助2分机2000响铃，接听，挂断，检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "TimeoutDestination","External Number"})
    public void testCallParking_59_TimeoutDestination() {
        prerequisite();
        apiUtil.editFeatureCode("\"park_timeout\":10,\"park_timeout_dest\":\"external_num\",\"park_timeout_dest_value\":\"2222\",\"park_timeout_dest_prefix\":\"2\"").apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
        thread.start();

        step("[caller] 3001" + ",[callee] 3000");
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机A-1000按*5将通话停泊");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1000,"*","5");

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=800) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;

        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 失败!").isIn(HUNGUP,IDLE);

        sleep(15*1000);
        assertThat(getExtensionStatus(2000, RING, 5)).as("通话状态校验 失败!").isIn(RING);
        pjsip.Pj_Answer_Call(2000);
        assertThat(getExtensionStatus(2000,TALKING , 5)).as("通话状态校验 失败!").isIn(TALKING);
        sleep(5*1000);

        step("挂断");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_3001.toString(), "22222", CDRObject.STATUS.ANSWER.toString(),"22222 hung up", SIPTrunk,SPS, "Outbound"))
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), "22222", CDRObject.STATUS.ANSWER.toString(),"3001<3001> parked at 6000 , timed out", SIPTrunk, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("FeatureCode-CallParking")
    @Story("TimeoutDestination")
    @Description("编辑Feature Code->Call Parking->Timeout Destination 选择外部号码，前缀2 号码2222；\n" +
            "Parking Timeout (s) 设置10s\n" +
            "\t通过sip外线呼入到分机A-1000,1000接听\n" +
            "\t\t60.分机1001按*056078将通话停泊\n" +
            "\t\t\tasterisk播放提示音call-parked-at.slin，一会儿后1001自动挂断，外线保持通话状态；\n" +
            "10s之后呼叫外部号码22222，辅助2分机2000响铃，接听，挂断，检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "TimeoutDestination","External Number"})
    public void testCallParking_60_TimeoutDestination() {
        prerequisite();
        apiUtil.editFeatureCode("\"park_timeout\":10,\"park_timeout_dest\":\"external_num\",\"park_timeout_dest_value\":\"2222\",\"park_timeout_dest_prefix\":\"2\"").apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
        thread.start();

        step("[caller] 3001" + ",[callee] 3000");
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机1001按*056078将通话停泊");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1001,"*","0","5","6","0","7","8");

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=800) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;

        assertThat(getExtensionStatus(1001, HUNGUP, 30)).as("通话状态校验 失败!").isIn(HUNGUP,IDLE);

        sleep(15*1000);
        assertThat(getExtensionStatus(2000, RING, 5)).as("通话状态校验 失败!").isIn(RING);
        pjsip.Pj_Answer_Call(2000);
        assertThat(getExtensionStatus(2000,TALKING , 5)).as("通话状态校验 失败!").isIn(TALKING);
        sleep(5*1000);

        step("挂断");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), CDRObject.CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("FeatureCode-CallParking")
    @Story("TimeoutDestination")
    @Description("编辑Feature Code->Call Parking->Timeout Destination 选择外部号码，前缀2 号码2222；\n" +
            "Parking Timeout (s) 设置10s\n" +
            "\t通过sip外线呼入到分机A-1000,1000接听\n" +
            "\t\t61.分机1001按*5将通话停泊\n" +
            "\t\t\tasterisk播放提示音call-parked-at.slin，一会儿后1001自动挂断，外线保持通话状态；\n" +
            "10s之后呼叫外部号码22222，辅助2分机2000响铃，接听，挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "TimeoutDestination","External Number"})
    public void testCallParking_61_TimeoutDestination() {
        prerequisite();
        apiUtil.editFeatureCode("\"park_timeout\":10,\"park_timeout_dest\":\"external_num\",\"park_timeout_dest_value\":\"2222\",\"park_timeout_dest_prefix\":\"2\"").apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
        thread.start();

        step("[caller] 3001" + ",[callee] 3000");
        String callTime = DataUtils.getCurrentTime("HH:mm:ss");
        pjsip.Pj_Make_Call_No_Answer(3001, "3000");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("分机1001按*5将通话停泊");
        sleep(5000);
        pjsip.Pj_Send_Dtmf(1001,"*","5");

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=800) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;

        assertThat(getExtensionStatus(1001, HUNGUP, 30)).as("通话状态校验 失败!").isIn(HUNGUP,IDLE);

        sleep(10*1000);
        assertThat(getExtensionStatus(2000, RING, 10)).as("通话状态校验 失败!").isIn(RING);
        pjsip.Pj_Answer_Call(2000);
        assertThat(getExtensionStatus(2000,TALKING , 10)).as("通话状态校验 失败!").isIn(TALKING);
        sleep(5*1000);

        step("挂断");
        pjsip.Pj_hangupCall(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), CDRObject.CDRNAME.Extension_1000.toString() + " hung up", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("FeatureCode-CallParking")
    @Story("TimeoutDestination")
    @Description("默认值4000ms\n" +
            "\t通过sps外线呼入到分机A-1000,1000接听\n" +
            "\t\t62.分机1000按*056010 将通话停泊，每按一个数字中间间隔3s\n" +
            "\t\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；挂断通话，检查cdr\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "FeatureCodeDigitTimeout",""})
    public void testCallParking_62_FeatureCodeDigitTimeout() {
        prerequisite();
        
        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
        thread.start();

        step("[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(5000);
        step("分机1000按*056010 将通话停泊，每按一个数字中间间隔3s");
        pjsip.Pj_Send_Dtmf(1000,"*");
        sleep(3*1000);
        pjsip.Pj_Send_Dtmf(1000,"0");
        sleep(3*1000);
        pjsip.Pj_Send_Dtmf(1000,"5");
        sleep(3*1000);
        pjsip.Pj_Send_Dtmf(1000,"6");
        sleep(3*1000);
        pjsip.Pj_Send_Dtmf(1000,"0");
        sleep(3*1000);
        pjsip.Pj_Send_Dtmf(1000,"1");
        sleep(3*1000);
        pjsip.Pj_Send_Dtmf(1000,"0");

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=800) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("挂断");
        sleep(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6000 , testta C<1002> hung up", SPS, "", "Inbound"))
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at 6000", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
    @Epic("P_Series")
    @Feature("FeatureCode-CallParking")
    @Story("TimeoutDestination")
    @Description("默认值4000ms\n" +
            "\t通过sps外线呼入到分机A-1000,1000接听\n" +
            "\t\t63.分机1000按*056010 将通话停泊，每按一个数字中间间隔5s\n" +
            "\t\t\t分机1000与外线保持正常通话，挂断，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "FeatureCodeDigitTimeout",""})
    public void testCallParking_63_FeatureCodeDigitTimeout() {
        prerequisite();

        step("[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(5000);
        step("分机1000按*056010 将通话停泊，每按一个数字中间间隔5s");
        pjsip.Pj_Send_Dtmf(1000,"*");
        sleep(5*1000);
        pjsip.Pj_Send_Dtmf(1000,"0");
        sleep(5*1000);
        pjsip.Pj_Send_Dtmf(1000,"5");
        sleep(5*1000);
        pjsip.Pj_Send_Dtmf(1000,"6");
        sleep(5*1000);
        pjsip.Pj_Send_Dtmf(1000,"0");
        sleep(5*1000);
        pjsip.Pj_Send_Dtmf(1000,"1");
        sleep(5*1000);
        pjsip.Pj_Send_Dtmf(1000,"0");


        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("通话状态校验 自动挂断失败!").isIn(TALKING);

        step("挂断");
        sleep(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6000 , testta C<1002> hung up", SPS, "", "Inbound"))
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at 6000", SPS, "", "Inbound"));

        softAssertPlus.assertAll();

    }
    @Epic("P_Series")
    @Feature("FeatureCode-CallParking")
    @Story("TimeoutDestination")
    @Description("64.编辑Feature Code ->Feature Code Digit Timeout 为6000\n" +
            "\t通过sps外线呼入到分机A-1000,1000接听\n" +
            "\t\t分机1000按*056010 将通话停泊，每按一个数字中间间隔5s\n" +
            "\t\t\tasterisk播放提示音call-parked-at.slin，一会儿后1000自动挂断，外线保持通话状态；挂断通话，检查cdr")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2", "FeatureCode-CallParking","P3", "FeatureCodeDigitTimeout",""})
    public void testCallParking_64_FeatureCodeDigitTimeout() {
        prerequisite();
        apiUtil.editFeatureCode("\"digit_timeout\": 6000").apply();

        asteriskObjectList.clear();
        SSHLinuxUntils.AsteriskThread thread=new SSHLinuxUntils.AsteriskThread(asteriskObjectList,CALL_PARKED_AT);
        thread.start();

        step("[caller] 2000" + ",[callee] 991000");
        pjsip.Pj_Make_Call_No_Answer(2000, "991000");

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        pjsip.Pj_Answer_Call(1000, false);
        assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        sleep(5000);
        step("分机1000按*056010 将通话停泊，每按一个数字中间间隔3s");
        pjsip.Pj_Send_Dtmf(1000,"*");
        sleep(5*1000);
        pjsip.Pj_Send_Dtmf(1000,"0");
        sleep(5*1000);
        pjsip.Pj_Send_Dtmf(1000,"5");
        sleep(5*1000);
        pjsip.Pj_Send_Dtmf(1000,"6");
        sleep(5*1000);
        pjsip.Pj_Send_Dtmf(1000,"0");
        sleep(5*1000);
        pjsip.Pj_Send_Dtmf(1000,"1");
        sleep(5*1000);
        pjsip.Pj_Send_Dtmf(1000,"0");

        int tmp = 0;
        while (asteriskObjectList.size() != 1 && tmp <=800) {
            sleep(50);
            tmp++;
            log.debug("[tmp]_" + tmp);
        }
        if (tmp == 801) {
            for (int i = 0; i < asteriskObjectList.size(); i++) {
                log.debug(i + "_【asterisk object name】 " + asteriskObjectList.get(i).getName() + " [asterisk object time] " + asteriskObjectList.get(i).getTime() + "[asterisk object tag] " + asteriskObjectList.get(i).getTag());
            }
            thread.flag = false;
            Assert.assertTrue(false, "[没有检测到提示音文件！！！]，[size] " + asteriskObjectList.size());
        }
        thread.flag = false;

        step("[通话状态校验]");
        assertThat(getExtensionStatus(1000, HUNGUP, 30)).as("通话状态校验 自动挂断失败!").isIn(HUNGUP,IDLE);
        assertThat(getExtensionStatus(2000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("挂断");
        sleep(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(3)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1002.toString(), CDRObject.STATUS.ANSWER.toString(), "testta C<1002> retrieved from 6000 , testta C<1002> hung up", SPS, "", "Inbound"))
                .contains(tuple(CDRObject.CDRNAME.Extension_2000.toString(), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "2000<2000> parked at 6000", SPS, "", "Inbound"));

        softAssertPlus.assertAll();
    }
}
