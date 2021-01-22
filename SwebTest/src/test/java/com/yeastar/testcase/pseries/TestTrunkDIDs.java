package com.yeastar.testcase.pseries;

import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.APIUtil;
import java.util.List;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.untils.*;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.annotations.Test;
import static com.yeastar.untils.CDRObject.CDRNAME.*;
import static com.yeastar.untils.CDRObject.COMMUNICATION_TYPE.*;
import static org.assertj.core.api.Assertions.tuple;

/**
 * @program: SwebTest
 * @description:
 * @author: xlq@yeastar.com
 * @create: 2020/12/16
 */

@Log4j2
public class TestTrunkDIDs extends TestCaseBaseNew {

    APIUtil apiUtil = new APIUtil();
    private boolean runRecoveryEnvFlag = true;
    private boolean isDebugInitExtensionFlag = !runRecoveryEnvFlag;
    private boolean resetdid = true;
    private boolean resetdidsip =true;
    private boolean resetdidbri = true;
    private boolean resetdide1 = true;
    /**
     * 前提环境
     */
    public void prerequisite() {

        if (isDebugInitExtensionFlag) {
            log.debug("*****************init extension************");

            apiUtil.loginWeb("0",EXTENSION_PASSWORD_NEW);
            runRecoveryEnvFlag = false;
            isDebugInitExtensionFlag = registerAllExtensions();

        }

        if (runRecoveryEnvFlag) {
            initExtension();
            initExtensionGroup();
            initTrunk();
            initRingGroup();
            initQueue();
            initConference();
            initOutbound();
            initIVR();
            initInbound();
            runRecoveryEnvFlag = registerAllExtensions();
        }
    }

    private void resetsps() {
        if(resetdid) {
            step("编辑sps外线-》DIDs/DDIs" +
                    "DID:9999 ,DID Name:Name1\n" +
                    "DID:9999,DID Name:Name2\n" +
                    "DID:9. ,DID Name:Name3\n" +
                    "DID:+5503301 ,DID Name:NameAbc1234567890\n" +
                    "DID:XZN!  ,DID Name：NameName\n" +
                    "DID:0095[13][27-9]xnz. ,DID Name: LastName\n"+
                    "DID:XXX. ,DID Name: NameNameName\n"
                    );
            log.debug("*****************init sps trunk DIDs************");
            apiUtil.editSipTrunk(SPS, "\"did_list\":[{\"did\":\"9999\",\"did_name\":\"Name1\",\"pos\":1},{\"did\":\"9999\",\"did_name\":\"Name2\",\"pos\":2},{\"did\":\"9.\",\"did_name\":\"Name3\",\"pos\":3},{\"did\":\"+5503301\",\"did_name\":\"NameAbc1234567890\",\"pos\":4},{\"did\":\"XZN!\",\"did_name\":\"NameName\",\"pos\":5},{\"did\":\"XXX.\",\"did_name\":\"NameNameName\",\"pos\":7},{\"did\":\"0095[13][27-9]xnz.\",\"did_name\":\"LastName\",\"pos\":6}]").apply();
            resetdid = false;
        }
    }

    private void resetsip(){

        if(resetdidsip) {
            step("\"编辑sip外线-》DIDs/DDIs\"" +
                    "DID:[1-4]XXX! ,DID Name: SIPRegisterTrunk"+
                    "DID:3000 ,DID Name：SIPName1");
            log.debug("*****************init SIP trunk DIDs************");
            apiUtil.editSipTrunk(SIPTrunk, "\"did_list\":[{\"did\":\"[1-4]XXX!\",\"did_name\":\"SIPRegisterTrunk\",\"pos\":1},{\"did\":\"3000\",\"did_name\":\"SIPName2\",\"pos\":2}]").apply();
            resetdidsip = false;
        }
    }

    private void resetbri() {
        if(resetdidbri) {
            step("编辑bri外线-》DIDs/DDIs" +
                    "DID:9999 ,DID Name:Name1\n" +
                    "DID:9999,DID Name:Name2\n" +
                    "DID:9. ,DID Name:Name3\n" +
                    "DID:+5503301 ,DID Name:NameAbc1234567890\n" +
                    "DID:XZN!  ,DID Name：NameName\n" +
                    "DID:0095[13][27-9]xnz. ,DID Name: LastName\n"+
                            "DID:XXX. ,DID Name: NameNameName\n"
                    );
            log.debug("*****************init BRI trunk DIDs************");
            apiUtil.editBriTrunk(BRI_1, "\"did_list\":[{\"did\":\"9999\",\"did_name\":\"Name1\",\"pos\":1},{\"did\":\"9999\",\"did_name\":\"Name2\",\"pos\":2},{\"did\":\"9.\",\"did_name\":\"Name3\",\"pos\":3},{\"did\":\"+5503301\",\"did_name\":\"NameAbc1234567890\",\"pos\":4},{\"did\":\"XZN!\",\"did_name\":\"NameName\",\"pos\":5},{\"did\":\"XXX.\",\"did_name\":\"NameNameName\",\"pos\":7},{\"did\":\"0095[13][27-9]xnz.\",\"did_name\":\"LastName\",\"pos\":6}]").apply();
            resetdidbri = false;
        }
    }

    private void resete1() {
        if(resetdide1) {
            step("编辑e1外线-》DIDs/DDIs" +
                    "DID:9999 ,DID Name:Name1\n" +
                    "DID:9999,DID Name:Name2\n" +
                    "DID:9. ,DID Name:Name3\n" +
                    "DID:+5503301 ,DID Name:NameAbc1234567890\n" +
                    "DID:XZN!  ,DID Name：NameName\n" +
                    "DID:XXX. ,DID Name: NameNameName\n" +
                    "DID:0095[13][27-9]xnz. ,DID Name: LastName");
            log.debug("*****************init E1 trunk DIDs************");
            apiUtil.editDigitalTrunk(E1, "\"did_list\":[{\"did\":\"9999\",\"did_name\":\"Name1\",\"pos\":1},{\"did\":\"9999\",\"did_name\":\"Name2\",\"pos\":2},{\"did\":\"9.\",\"did_name\":\"Name3\",\"pos\":3},{\"did\":\"+5503301\",\"did_name\":\"NameAbc1234567890\",\"pos\":4},{\"did\":\"XZN!\",\"did_name\":\"NameName\",\"pos\":5},{\"did\":\"XXX.\",\"did_name\":\"NameNameName\",\"pos\":7},{\"did\":\"0095[13][27-9]xnz.\",\"did_name\":\"LastName\",\"pos\":6}]").apply();
            resetdide1 = false;
        }
    }


//    public void getcallerID(int num){
//        getExtensionStatus(num,RING,20);
//        String callid = pjsip.getAccount(num);
//        return callid;
//    }

    @Epic("P_Series")
    @Feature("TestTrunkDIDs")
    @Story("SPS Trunk ->DIDs/DDIs")
    @Description("编辑sps外线-》DIDs/DDIs" +
            "DID:9999 ,DID Name:Name1\n" +
            "DID:9999,DID Name:Name2\n" +
            "DID:9. ,DID Name:Name3\n" +
            "DID:+5503301 ,DID Name:NameAbc1234567890\n" +
            "DID:XZN!  ,DID Name：NameName\n" +
            "DID:0095[13][27-9]xnz. ,DID Name: LastName"+
            "DID:XXX. ,DID Name: NameNameName\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","P2","Trunk","SPS","DIDs"})
    public void testTrunk01_sps() {
        prerequisite();
        resetsps();
        step("1.辅助2分机2000通过sps外线拨打999999呼入到分机1000");
        pjsip.Pj_Make_Call_No_Answer(2000,"999999",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("1-1.来显检查").isEqualTo("Name1:2000");
        pjsip.Pj_hangupCall(2000);

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), Extension_1000.toString(),  "NO ANSWER" , Extension_2000.toString() + " hung up", SPS, "", INBOUND.toString()));

        step("编辑SPS Trunk的DID List前面2个DID的顺序为\n" +
                "DID:9999 ,DID Name:Name2\n" +
                "DID:9999,DID Name:Name1");
        apiUtil.editSipTrunk(SPS, "\"did_list\":[{\"did\":\"9999\",\"did_name\":\"Name1\",\"pos\":2},{\"did\":\"9999\",\"did_name\":\"Name2\",\"pos\":1},{\"did\":\"9.\",\"did_name\":\"Name3\",\"pos\":3},{\"did\":\"+5503301\",\"did_name\":\"NameAbc1234567890\",\"pos\":4},{\"did\":\"XZN!\",\"did_name\":\"NameName\",\"pos\":5},{\"did\":\"XXX.\",\"did_name\":\"NameNameName\",\"pos\":6},{\"did\":\"0095[13][27-9]xnz.\",\"did_name\":\"LastName\",\"pos\":7}]").apply();

        step("辅助2分机2001通过sps外线拨打999999呼入到分机1000");
        pjsip.Pj_Make_Call_No_Answer(2001,"999999",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("1-2.来显检查").isEqualTo("Name2:2001");
        pjsip.Pj_hangupCall(2001);


        List<CDRObject> resultCDR2 = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR2).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001.toString(), Extension_1000.toString(),  "NO ANSWER" , Extension_2001.toString() + " hung up", SPS, "", INBOUND.toString()));
        softAssertPlus.assertAll();
        resetdid=true;
    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("SPS Trunk ->DIDs/DDIs")
    @Description("测试步骤")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","P3","Trunk","SPS","DIDs"})
    public void testTrunk02_sps() {
        prerequisite();
        resetsps();
        step("2.辅助2分机2000通过sps外线拨打999998呼入到分机1000");
        pjsip.Pj_Make_Call_No_Answer(2000,"999998",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("2.来显检查").isEqualTo("Name3:2000");
        pjsip.Pj_hangupCall(2000);

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), Extension_1000.toString(),  "NO ANSWER" , Extension_2000.toString() + " hung up", SPS, "", INBOUND.toString()));
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("SPS Trunk ->DIDs/DDIs")
    @Description("3.辅助2分机2000通过sps外线拨打99+5503301呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","P3","Trunk","SPS","DIDs"})
    public void testTrunk03_sps() {
        prerequisite();
        resetsps();
        step("3.辅助2分机2000通过sps外线拨打99+5503301呼入到分机1000");
        pjsip.Pj_Make_Call_No_Answer(2000,"99+5503301",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("3.来显检查").isEqualTo("NameAbc1234567890:2000");
        pjsip.Pj_hangupCall(2000);
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("SPS Trunk ->DIDs/DDIs")
    @Description("4.辅助2分机2000通过sps外线拨打995503301呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","P3","Trunk","SPS","DIDs"})
    public void testTrunk04_sps() {
        prerequisite();
        resetsps();
        step("4.辅助2分机2000通过sps外线拨打995503301呼入到分机1000");
        pjsip.Pj_Make_Call_No_Answer(2000,"995503301",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("4.来显检查").isEqualTo("NameNameName:2000");
        pjsip.Pj_hangupCall(2000);
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("SPS Trunk ->DIDs/DDIs")
    @Description("5.辅助2分机2000通过sps外线拨打990123呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","P3","Trunk","SPS","DIDs"})
    public void testTrunk05_sps() {
        prerequisite();
        resetsps();
        step("5.辅助2分机2000通过sps外线拨打990123呼入到分机1000");
        pjsip.Pj_Make_Call_No_Answer(2000,"990123",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("5.来显检查").isEqualTo("NameName:2000");
        pjsip.Pj_hangupCall(2000);
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("SPS Trunk ->DIDs/DDIs")
    @Description("6.辅助2分机2000通过sps外线拨打99011呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","P3","Trunk","SPS","DIDs"})
    public void testTrunk06_sps() {
        prerequisite();
        resetsps();
        step("6.辅助2分机2000通过sps外线拨打99011呼入到分机1000");
        pjsip.Pj_Make_Call_No_Answer(2000,"99011",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("6.来显检查").isEqualTo("2000");
        pjsip.Pj_hangupCall(2000);
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("SPS Trunk ->DIDs/DDIs")
    @Description("7.辅助2分机2000通过sps外线拨打99002呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","P3","Trunk","SPS","DIDs"})
    public void testTrunk07_sps() {
        prerequisite();
        resetsps();
        step("7.辅助2分机2000通过sps外线拨打99002呼入到分机1000");
        pjsip.Pj_Make_Call_No_Answer(2000,"99002",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("7.来显检查").isEqualTo("2000");
        pjsip.Pj_hangupCall(2000);
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("SPS Trunk ->DIDs/DDIs")
    @Description("8.辅助2分机2000通过sps外线拨打990020呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","P3","Trunk","SPS","DIDs"})
    public void testTrunk08_sps() {
        prerequisite();
        resetsps();
        step("8.辅助2分机2000通过sps外线拨打990020呼入到分机1000]");
        pjsip.Pj_Make_Call_No_Answer(2000,"990020",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("8.来显检查").isEqualTo("NameNameName:2000");
        pjsip.Pj_hangupCall(2000);
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("SPS Trunk ->DIDs/DDIs")
    @Description("9.辅助2分机2001通过sps外线拨打990095120211呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","P3","Trunk","SPS","DIDs"})
    public void testTrunk09_sps() {
        prerequisite();
        resetsps();
        step("9.辅助2分机2001通过sps外线拨打990095120211呼入到分机1000");
        pjsip.Pj_Make_Call_No_Answer(2001,"990095120211",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("9.来显检查").isEqualTo("LastName:2001");
        pjsip.Pj_hangupCall(2001);
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("SPS Trunk ->DIDs/DDIs")
    @Description("10.辅助2分机2001通过sps外线拨打990095381579呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","P3","Trunk","SPS","DIDs"})
    public void testTrunk10_sps() {
        prerequisite();
        resetsps();
        step("10.辅助2分机2001通过sps外线拨打990095381579呼入到分机1000");
        pjsip.Pj_Make_Call_No_Answer(2001,"990095381579",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("10.来显检查").isEqualTo("LastName:2001");
        pjsip.Pj_hangupCall(2001);
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("SPS Trunk ->DIDs/DDIs")
    @Description("11.辅助2分机2001通过sps外线拨打990095481579呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","P3","Trunk","SPS","DIDs"})
    public void testTrunk11_sps() {
        prerequisite();
        resetsps();
        step("11.辅助2分机2001通过sps外线拨打990095481579呼入到分机1000");
        pjsip.Pj_Make_Call_No_Answer(2001,"990095481579",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("11.来显检查").isEqualTo("NameNameName:2001");
        pjsip.Pj_hangupCall(2001);
        softAssertPlus.assertAll();

    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("SIP Trunk ->DIDs/DDIs")
    @Description("编辑sip外线-》DIDs/DDIs" +
            "DID:3001 ,DID Name: Name1\n" +
            "DID:3001 ,DID Name: Name2\n" +
            "DID:[1-4]XXZ! ,DID Name: SIPRegisterTrunk" +

            "12.辅助1分机3001通过sip外线呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","P2","Trunk","SIP","DIDs"})
    public void testTrunk12_sip() {
        prerequisite();
        resetsip();
        step("12.辅助1分机3001通过sip外线呼入到分机1000");
        pjsip.Pj_Make_Call_No_Answer(3001,"3000",DEVICE_ASSIST_1,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("12.来显检查").isEqualTo("SIPRegisterTrunk:3001");
        pjsip.Pj_hangupCall(3001);
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("SIP Trunk ->DIDs/DDIs")
    @Description("13.辅助1的分机3002通过sip外线呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "Cloud", "K2","P3","Trunk","SIP","DIDs"})
    public void testTrunk13_sip() {
        prerequisite();
        resetsip();
        step("13.辅助1的分机3002通过sip外线呼入到分机1000");
        pjsip.Pj_Make_Call_No_Answer(3002,"3000",DEVICE_ASSIST_1,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("13.来显检查").isEqualTo("SIPRegisterTrunk:3002");
        pjsip.Pj_hangupCall(3002);
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("BRI Trunk ->DIDs/DDIs")
    @Description("编辑bri外线-》DIDs/DDIs" +
            "DID:9999 ,DID Name:Name1\n" +
            "DID:9999,DID Name:Name2\n" +
            "DID:9. ,DID Name:Name3\n" +
            "DID:+5503301 ,DID Name:NameAbc1234567890\n" +
            "DID:XZN!  ,DID Name：NameName\n" +
            "DID:XXX. ,DID Name: NameNameName\n" +
            "DID:0095[13][27-9]xnz. ,DID Name: LastName" +
            "14.辅助2分机2000通过bri外线拨打889999呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P2","Trunk","BRI","DIDs"})
    public void testTrunk14_bri() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            step("BRI外线未配置");
            return;
        }
        prerequisite();
        resetbri();
        step("14.辅助2分机2000通过bri外线拨打889999呼入到分机1000");
        pjsip.Pj_Make_Call_No_Answer(2000,"889999",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("14-1.来显检查").isEqualTo("Name1:2000");
        pjsip.Pj_hangupCall(2000);

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), Extension_1000.toString(),  "NO ANSWER" , Extension_2000.toString() + " hung up", BRI_1, "", INBOUND.toString()));

        step("编辑BRI Trunk的DID List前面2个DID的顺序为\n" +
                "DID:9999 ,DID Name:Name2\n" +
                "DID:9999,DID Name:Name1");
        apiUtil.editBriTrunk(BRI_1, "\"did_list\":[{\"did\":\"9999\",\"did_name\":\"Name1\",\"pos\":2},{\"did\":\"9999\",\"did_name\":\"Name2\",\"pos\":1},{\"did\":\"9.\",\"did_name\":\"Name3\",\"pos\":3},{\"did\":\"+5503301\",\"did_name\":\"NameAbc1234567890\",\"pos\":4},{\"did\":\"XZN!\",\"did_name\":\"NameName\",\"pos\":5},{\"did\":\"XXX.\",\"did_name\":\"NameNameName\",\"pos\":6},{\"did\":\"0095[13][27-9]xnz.\",\"did_name\":\"LastName\",\"pos\":7}]").apply();

        step("辅助2分机2001通过BRI外线拨打889999呼入到分机1000");
        pjsip.Pj_Make_Call_No_Answer(2001,"889999",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("14-2.来显检查").isEqualTo("Name2:2001");
        pjsip.Pj_hangupCall(2001);


        List<CDRObject> resultCDR2 = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR2).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001.toString(), Extension_1000.toString(),  "NO ANSWER" , Extension_2001.toString() + " hung up", BRI_1, "", INBOUND.toString()));
        softAssertPlus.assertAll();
        resetdidbri=true;
    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("BRI Trunk ->DIDs/DDIs")
    @Description("15.辅助2分机2000通过bri外线拨打889998呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3","Trunk","BRI","DIDs"})
    public void testTrunk15_bri() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            step("BRI外线未配置");
            return;
        }
        prerequisite();
        resetbri();

        step("15.辅助2分机2000通过bri外线拨打889998呼入到分机1000");
        pjsip.Pj_Make_Call_No_Answer(2000,"889998",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("15.来显检查").isEqualTo("Name3:2000");
        pjsip.Pj_hangupCall(2000);
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("BRI Trunk ->DIDs/DDIs")
    @Description("16.辅助2分机2000通过bri外线拨打88+5503301呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3","Trunk","BRI","DIDs"})
    public void testTrunk16_bri() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            step("BRI外线未配置");
            return;
        }
        prerequisite();
        resetbri();
        step("16.辅助2分机2000通过bri外线拨打88+5503301呼入到分机1000");
        pjsip.Pj_Make_Call_No_Answer(2000,"88+5503301",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("16.来显检查").isEqualTo("NameAbc1234567890:2000");
        pjsip.Pj_hangupCall(2000);
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("BRI Trunk ->DIDs/DDIs")
    @Description("17.辅助2分机2000通过bri外线拨打885503301呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3","Trunk","BRI","DIDs"})
    public void testTrunk17_bri() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            step("BRI外线未配置");
            return;
        }
        prerequisite();
        resetbri();
        step("17.辅助2分机2000通过bri外线拨打885503301呼入到分机1000");
        pjsip.Pj_Make_Call_No_Answer(2000,"885503301",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("17.来显检查").isEqualTo("NameNameName:2000");
        pjsip.Pj_hangupCall(2000);
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("BRI Trunk ->DIDs/DDIs")
    @Description("18.辅助2分机2000通过bri外线拨打880123呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3","Trunk","BRI","DIDs"})
    public void testTrunk18_bri() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            step("BRI外线未配置");
            return;
        }
        prerequisite();
        resetbri();
        step("18.辅助2分机2000通过bri外线拨打880123呼入到分机1000");
        pjsip.Pj_Make_Call_No_Answer(2000,"880123",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("18.来显检查").isEqualTo("NameName:2000");
        pjsip.Pj_hangupCall(2000);
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("BRI Trunk ->DIDs/DDIs")
    @Description("19.辅助2分机2000通过bri外线拨打88011呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3","Trunk","BRI","DIDs"})
    public void testTrunk19_bri() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            step("BRI外线未配置");
            return;
        }
        prerequisite();
        resetbri();
        step("19.辅助2分机2000通过bri外线拨打88011呼入到分机1000");
        pjsip.Pj_Make_Call_No_Answer(2000,"88011",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("19.来显检查").isEqualTo("2000");
        pjsip.Pj_hangupCall(2000);
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("BRI Trunk ->DIDs/DDIs")
    @Description("20.辅助2分机2000通过bri外线拨打88002呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3","Trunk","BRI","DIDs"})
    public void testTrunk20_bri() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            step("BRI外线未配置");
            return;
        }
        prerequisite();
        resetbri();
        step("[20.辅助2分机2000通过bri外线拨打88002呼入到分机1000]");
        pjsip.Pj_Make_Call_No_Answer(2000,"88002",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("20.来显检查").isEqualTo("2000");
        pjsip.Pj_hangupCall(2000);
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("BRI Trunk ->DIDs/DDIs")
    @Description("21.辅助2分机2000通过bri外线拨打880020呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3","Trunk","BRI","DIDs"})
    public void testTrunk21_bri() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            step("BRI外线未配置");
            return;
        }
        prerequisite();
        resetbri();
        step("[21.辅助2分机2000通过bri外线拨打880020呼入到分机1000]");
        pjsip.Pj_Make_Call_No_Answer(2000,"880020",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("21.来显检查").isEqualTo("NameNameName:2000");
        pjsip.Pj_hangupCall(2000);
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("BRI Trunk ->DIDs/DDIs")
    @Description("22.辅助2分机2001通过bri外线拨打880095120211呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3","Trunk","BRI","DIDs"})
    public void testTrunk22_bri() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            step("BRI外线未配置");
            return;
        }
        prerequisite();
        resetbri();
        step("[22.辅助2分机2001通过bri外线拨打880095120211呼入到分机1000]");
        pjsip.Pj_Make_Call_No_Answer(2001,"880095120211",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("22.来显检查").isEqualTo("LastName:2001");
        pjsip.Pj_hangupCall(2001);
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("BRI Trunk ->DIDs/DDIs")
    @Description("23.辅助2分机2001通过bri外线拨打880095381579呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3","Trunk","BRI","DIDs"})
    public void testTrunk23_bri() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            step("BRI外线未配置");
            return;
        }
        prerequisite();
        resetbri();
        step("[23.辅助2分机2001通过bri外线拨打880095381579呼入到分机1000]");
        pjsip.Pj_Make_Call_No_Answer(2001,"880095381579",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("23.来显检查").isEqualTo("LastName:2001");
        pjsip.Pj_hangupCall(2001);
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("BRI Trunk ->DIDs/DDIs")
    @Description("24.辅助2分机2001通过bri外线拨打880095481579呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3","Trunk","BRI","DIDs"})
    public void testTrunk24_bri() {
        if(BRI_1.trim().equalsIgnoreCase("null") || BRI_1.trim().equalsIgnoreCase("")){
            step("BRI外线未配置");
            return;
        }
        prerequisite();
        resetbri();
        step("[24.辅助2分机2001通过bri外线拨打880095481579呼入到分机1000]");
        pjsip.Pj_Make_Call_No_Answer(2001,"880095481579",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("24.来显检查").isEqualTo("NameNameName:2001");
        pjsip.Pj_hangupCall(2001);
        softAssertPlus.assertAll();
    }


    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("E1 Trunk ->DIDs/DDIs")
    @Description("25.辅助2分机2000通过e1外线拨打669999呼入到分机1000" +
            "编辑前面2个DID的顺序为\n" +
            "DID:9999 ,DID Name:Name2\n" +
            "DID:9999,DID Name:Name1\n" +
            "辅助2分机2000通过E1外线拨打669999呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P2","Trunk","E1","DIDs"})
    public void testTrunk25_e1() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            step("E1外线未配置");
            return;
        }
        prerequisite();
        resete1();
        step("[25.辅助2分机2000通过e1外线拨打669999呼入到分机1000]");
        pjsip.Pj_Make_Call_No_Answer(2000,"669999",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("25-1.来显检查").isEqualTo("Name1:2000");
        pjsip.Pj_hangupCall(2000);

        assertStep("网页admin登录 ,CDR校验");
        auto.loginPage().loginWithAdmin();
        auto.homePage().intoPage(HomePage.Menu_Level_1.cdr_recording, HomePage.Menu_Level_2.cdr_recording_tree_cdr);

        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2000.toString(), Extension_1000.toString(),  "NO ANSWER" , Extension_2000.toString() + " hung up", E1, "", INBOUND.toString()));

        step("编辑E1  Trunk的DID List前面2个DID的顺序为\n" +
                "DID:9999 ,DID Name:Name2\n" +
                "DID:9999,DID Name:Name1");
        apiUtil.editDigitalTrunk(E1, "\"did_list\":[{\"did\":\"9999\",\"did_name\":\"Name1\",\"pos\":2},{\"did\":\"9999\",\"did_name\":\"Name2\",\"pos\":1},{\"did\":\"9.\",\"did_name\":\"Name3\",\"pos\":3},{\"did\":\"+5503301\",\"did_name\":\"NameAbc1234567890\",\"pos\":4},{\"did\":\"XZN!\",\"did_name\":\"NameName\",\"pos\":5},{\"did\":\"XXX.\",\"did_name\":\"NameNameName\",\"pos\":6},{\"did\":\"0095[13][27-9]xnz.\",\"did_name\":\"LastName\",\"pos\":7}]").apply();

        step("辅助2分机2000通过E1外线拨打669999呼入到分机1000");
        pjsip.Pj_Make_Call_No_Answer(2001,"669999",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("14-2.来显检查").isEqualTo("Name2:2001");
        pjsip.Pj_hangupCall(2001);

        List<CDRObject> resultCDR2 = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR2).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(Extension_2001.toString(), Extension_1000.toString(),  "NO ANSWER" , Extension_2001.toString() + " hung up", E1, "", INBOUND.toString()));
        softAssertPlus.assertAll();
        resetdide1=true;
    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("E1 Trunk ->DIDs/DDIs")
    @Description("26.辅助2分机2000通过E1外线拨打669998呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3","Trunk","E1","DIDs"})
    public void testTrunk26_e1() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            step("E1外线未配置");
            return;
        }
        prerequisite();
        resete1();

        step("[26.辅助2分机2000通过E1外线拨打669998呼入到分机1000]");
        pjsip.Pj_Make_Call_No_Answer(2000,"669998",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("26.来显检查").isEqualTo("Name3:2000");
        pjsip.Pj_hangupCall(2000);
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("E1 Trunk ->DIDs/DDIs")
    @Description("27.辅助2分机2000通过E1外线拨打66+5503301呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3","Trunk","E1","DIDs"})
    public void testTrunk27_e1() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            step("E1外线未配置");
            return;
        }
        prerequisite();
        resete1();

        step("[27.辅助2分机2000通过E1外线拨打66+5503301呼入到分机1000]");
        pjsip.Pj_Make_Call_No_Answer(2000,"66+5503301",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("27.来显检查").isEqualTo("NameAbc1234567890:2000");
        pjsip.Pj_hangupCall(2000);
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("E1 Trunk ->DIDs/DDIs")
    @Description("28.辅助2分机2000通过E1外线拨打665503301呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3","Trunk","E1","DIDs"})
    public void testTrunk28_e1() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            step("E1外线未配置");
            return;
        }
        prerequisite();
        resete1();

        step("[28.辅助2分机2000通过E1外线拨打665503301呼入到分机1000]");
        pjsip.Pj_Make_Call_No_Answer(2000,"665503301",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("28.来显检查").isEqualTo("NameNameName:2000");
        pjsip.Pj_hangupCall(2000);
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("E1 Trunk ->DIDs/DDIs")
    @Description("29.辅助2分机2000通过E1外线拨打660123呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3","Trunk","E1","DIDs"})
    public void testTrunk29_e1() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            step("E1外线未配置");
            return;
        }
        prerequisite();
        resete1();

        step("[29.辅助2分机2000通过E1外线拨打660123呼入到分机1000]");
        pjsip.Pj_Make_Call_No_Answer(2000,"660123",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("29.来显检查").isEqualTo("NameName:2000");
        pjsip.Pj_hangupCall(2000);
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("E1 Trunk ->DIDs/DDIs")
    @Description("30.辅助2分机2000通过E1外线拨打66011呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3","Trunk","E1","DIDs"})
    public void testTrunk30_e1() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            step("E1外线未配置");
            return;
        }
        prerequisite();
        resete1();

        step("[30.辅助2分机2000通过E1外线拨打66011呼入到分机1000]");
        pjsip.Pj_Make_Call_No_Answer(2000,"66011",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("30.来显检查").isEqualTo("2000");
        pjsip.Pj_hangupCall(2000);
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("E1 Trunk ->DIDs/DDIs")
    @Description("31.辅助2分机2000通过E1外线拨打66002呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3","Trunk","E1","DIDs"})
    public void testTrunk31_e1() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            step("E1外线未配置");
            return;
        }
        prerequisite();
        resete1();

        step("[31.辅助2分机2000通过E1外线拨打66002呼入到分机1000]");
        pjsip.Pj_Make_Call_No_Answer(2000,"66002",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("31.来显检查").isEqualTo("2000");
        pjsip.Pj_hangupCall(2000);
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("E1 Trunk ->DIDs/DDIs")
    @Description("32.辅助2分机2000通过E1外线拨打660020呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3","Trunk","E1","DIDs"})
    public void testTrunk32_e1() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            step("E1外线未配置");
            return;
        }
        prerequisite();
        resete1();

        step("[32.辅助2分机2000通过E1外线拨打660020呼入到分机1000]");
        pjsip.Pj_Make_Call_No_Answer(2000,"660020",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("32.来显检查").isEqualTo("NameNameName:2000");
        pjsip.Pj_hangupCall(2000);
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("E1 Trunk ->DIDs/DDIs")
    @Description("33.辅助2分机2001通过E1外线拨打660095120211呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3","Trunk","E1","DIDs"})
    public void testTrunk33_e1() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            step("E1外线未配置");
            return;
        }
        prerequisite();
        resete1();

        step("[33.辅助2分机2001通过E1外线拨打660095120211呼入到分机1000]");
        pjsip.Pj_Make_Call_No_Answer(2001,"660095120211",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("33.来显检查").isEqualTo("LastName:2001");
        pjsip.Pj_hangupCall(2001);
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("E1 Trunk ->DIDs/DDIs")
    @Description("34.辅助2分机2001通过E1外线拨打660095381579呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3","Trunk","E1","DIDs"})
    public void testTrunk34_e1() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            step("E1外线未配置");
            return;
        }
        prerequisite();
        resete1();

        step("[34.辅助2分机2001通过E1外线拨打660095381579呼入到分机1000]");
        pjsip.Pj_Make_Call_No_Answer(2001,"660095381579",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("34.来显检查").isEqualTo("LastName:2001");
        pjsip.Pj_hangupCall(2001);
        softAssertPlus.assertAll();
    }

    @Epic("P_Series")
    @Feature("Trunk DIDs/DDIs")
    @Story("E1 Trunk ->DIDs/DDIs")
    @Description("35.辅助2分机2001通过E1外线拨打660095481579呼入到分机1000")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries", "P3","Trunk","E1","DIDs"})
    public void testTrunk35_e1() {
        if(E1.trim().equalsIgnoreCase("null") || E1.trim().equalsIgnoreCase("")){
            step("E1外线未配置");
            return;
        }
        prerequisite();
        resete1();

        step("[35.辅助2分机2001通过E1外线拨打660095481579呼入到分机1000]");
        pjsip.Pj_Make_Call_No_Answer(2001,"660095481579",DEVICE_ASSIST_2,false);
        softAssertPlus.assertThat(getExtensionStatus(1000, RING,10)).as("[来电分机响铃]:1000分机响铃").isEqualTo(RING);
        softAssertPlus.assertThat(pjsip.getAccount(1000).callerId).as("35.来显检查").isEqualTo("NameNameName:2001");
        pjsip.Pj_hangupCall(2001);
        softAssertPlus.assertAll();
    }

}
