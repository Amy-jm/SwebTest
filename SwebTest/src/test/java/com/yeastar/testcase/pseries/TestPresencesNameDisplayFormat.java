package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.org.glassfish.gmbal.Description;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.APIObject.WebclientExtensionObject;
import com.yeastar.untils.AsteriskObject;
import com.yeastar.untils.CDRObject;
import com.yeastar.untils.DataUtils;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;
import top.jfunc.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.yeastar.swebtest.driver.DataReader2.*;
//import static com.yeastar.untils.APIObject.Import.readCsvFile;
import static io.qameta.allure.Allure.step;
import static java.lang.Thread.sleep;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

/**
 * Created by Yeastar on 2021/2/2.
 */
@Log4j2
public class TestPresencesNameDisplayFormat extends TestCaseBaseNew {
    List<String> trunk9 = new ArrayList<>();
    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();
    List<String> officeTimes = new ArrayList<>();
    List<String> resetTimes = new ArrayList<>();
    private boolean isRunRecoveryEnvFlag = true;
    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;

    TestPresencesNameDisplayFormat() {
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
            initTestNameDisplayFormat();
            //            initTestEnv();//
            isDebugInitExtensionFlag = registerAllExtensions();
            isRunRecoveryEnvFlag = false;
        }

        if (isRunRecoveryEnvFlag) {
            step("=========== init before class  start =========");
            initExtension();
            initExtensionGroup();
            initTrunk();
//            initRingGroup();
//            initQueue();
//            initConference();
//            initOutbound();
//            initIVR();
            initInbound();
//            initFeatureCode();
            isRunRecoveryEnvFlag = registerAllExtensions();

            //上面默认的初始化内容，initTestNameDisplayFormat是执行这个用例需要的初始化

            initTestNameDisplayFormat();
//            initTestContacts();
            step("=========== init before class  end =========");
        }
        log.debug("[prerequisite time]:" + (System.currentTimeMillis() - startTime) / 1000 + " Seconds");
    }

    public void initTestNameDisplayFormat() {
        log.debug("######### 初始化测试环境\n" +
                "设置NameDisplayFormat初始值为First Name Last Name with Space Inbetween"
        );
        apiUtil.preferencesUpdate("{\"name_disp_fmt\":\"first_last\"}").apply();

    }
    /**
     * 读取CSV文件内容
     */
//    public void initTestContacts() {
//        log.debug("######### 初始化测试环境\n" +"admin导入企业联系人");
//        String filePath = "F:\\swebtest\\SwebTest\\exportFile\\P570-company_contact-37.2.0.81-export-企业联系人.csv";
//        ArrayList<String[]> a = readCsvFile(filePath);
//        System.out.print(a);

//        ArrayList<String[]> binary = readCsvFile(filePath);
//        String s = binary.toString();
//        apiUtil.contactsImport("{\"format\": \"csv\",\"file\": " + binary +"}");
//        apiUtil.apply();
//    }



    @Epic("P_Series")
    @Feature("Preferences-Name Display Format")
    @Story("Name Display Format")
    @Description("1.分机1000呼叫分机1001，应答，预期：CDR的CallFrom分机1000的name test A，CallTO分机1001显示test2 B，reason为分机B挂断，分机B的name正确\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Preferences-Basic","Name Display Format","P1"})

    public void testPreferences_01_NameDisplayFormat() {
        prerequisite();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        Selenide.sleep(5000);
        pjsip.Pj_Answer_Call(1001, false);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("1001 被叫挂断");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(Assertions.tuple(CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.CDRNAME.Extension_1001.toString(), CDRObject.STATUS.ANSWER.toString(), "test2 B<1001> hung up", "", "", "Internal"));

        softAssertPlus.assertAll();

    }



    @Epic("P_Series")
    @Feature("Preferences-Name Display Format")
    @Story("Name Display Format")
    @Description("1.分机1000呼叫分机1001，未接，预期：CDR的CallFrom分机1000的name test A，CallTO分机1001显示test2 B，reason为分机B挂断，分机B的name正确\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Preferences-Basic","Name Display Format","P2"})

    public void testPreferences_02_NameDisplayFormat() {
        prerequisite();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        Selenide.sleep(5000);

//        softAssertPlus.assertThat(getExtensionStatus(1001, IDLE, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("1001 被叫挂断");
        pjsip.Pj_hangupCall(1000);
        Selenide.sleep(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(Assertions.tuple(CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.CDRNAME.Extension_1001.toString(), CDRObject.STATUS.NO_ANSWER.toString(), "test A<1000> hung up", "", "", "Internal"));

        softAssertPlus.assertAll();

    }
    @Epic("P_Series")
    @Feature("Preferences-Name Display Format")
    @Story("Name Display Format")
    @Description("1.3002通过SIP外线呼叫分机1000，应答，预期：CDR的CallFrom分机3002 ，CallTO分机1000显示test2 A，reason为分机1000挂断\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Preferences-Basic","Name Display Format","P2"})

    public void testPreferences_03_NameDisplayFormat() {
        prerequisite();

        step("2:[caller] 3002" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(3002, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        Selenide.sleep(5000);
        pjsip.Pj_Answer_Call(1000, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("1000 被叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(Assertions.tuple("3002<3002>", CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), "test A<1000> hung up", "sipRegister", "", "Inbound"));

        softAssertPlus.assertAll();

    }

    /**
     * admin登录
     * 	4.查看分机列表
     * 		1000的Caller ID Name显示test A
     */
    @Epic("P_Series")
    @Feature("Preferences-Name Display Format")
    @Story("Name Display Format")
    @Description("admin登录，1000的Caller ID Name显示test A\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Preferences-Basic","Name Display Format","P2"})
    public void testPreferences_04_NameDisplayFromat(){
        apiUtil.preferencesUpdate("{\"name_disp_fmt\":\"first_last\"}").apply();
        String callerIdName_1000 = apiUtil.getExtensionSummary("1000").callerIdName;
        Assert.assertEquals(callerIdName_1000,"test A");
    }

    /**
     * 分机1000登录
     * 	8.查看右上角名称
     * 		name显示test A
     */
    @Epic("P_Series")
    @Feature("Preferences-Name Display Format")
    @Story("Name Display Format")
    @Description("分机1000登录，查看右上角名称,1000的Caller ID Name显示test A\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Preferences-Basic","Name Display Format","P2"})
    public void testPreferences_08_NameDisplayFormat(){
        apiUtil.preferencesUpdate("{\"name_disp_fmt\":\"first_last\"}").apply();
        apiUtil.loginWeb("1000",EXTENSION_PASSWORD_NEW);
        String name_1000 = apiUtil.getWebclientPerson();
        System.out.print("\n" + name_1000);
        Assert.assertEquals(name_1000,"test A");



    }

    /**
     * 分机1000登录
     * 	9.查看分机列表1001
     * 		name显示test2 B

     */
    @Epic("P_Series")
    @Feature("Preferences-Name Display Format")
    @Story("Name Display Format")
    @Description("机1000登录，查看分机列表1001,1001的Caller ID Name显示test2 B\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Preferences-Basic","Name Display Format","P2"})
    public void testPreferences_09_NameDisplayFormat(){
        apiUtil.preferencesUpdate("{\"name_disp_fmt\":\"first_last\"}").apply();

        //接口写法,接口为webclient需要先登录webclient获取cookie
        apiUtil.loginWeb("1000",EXTENSION_PASSWORD_NEW);
        String callerIdName_1001 = apiUtil.getWebclientExtensionSummary("1001").caller_id_name;
        Assert.assertEquals(callerIdName_1001,"test2 B");
        //网页写法，容易误报
//        auto.loginPage().login("1000",EXTENSION_PASSWORD_NEW);
//        auto.me_homePage().ext_name.shouldHave(Condition.exactText("test B"));
    }

    @Epic("P_Series")
    @Feature("Preferences-Name Display Format")
    @Story("Name Display Format")
    @Description("admin登录，1000的Caller ID Name显示A test\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Preferences-Basic","Name Display Format","P2"})
    public void testPreferences_17_NameDisplayFromat(){
        apiUtil.preferencesUpdate("{\"name_disp_fmt\":\"last_first\"}").apply();
        String callerIdName_1000 = apiUtil.getExtensionSummary("1000").callerIdName;
        Assert.assertEquals(callerIdName_1000,"A test");
    }
    /**
     * 21.分机1000登录,查看右上角名称
     * 	name显示A test
     */

    @Epic("P_Series")
    @Feature("Preferences-Name Display Format")
    @Story("Name Display Format")
    @Description("分机1000登录，查看右上角名称,1000的Caller ID Name显示A test\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Preferences-Basic","Name Display Format","P2"})
    public void testPreferences_21_NameDisplayFormat(){
        apiUtil.preferencesUpdate("{\"name_disp_fmt\":\"last_first\"}").apply();
        apiUtil.loginWeb("1000",EXTENSION_PASSWORD_NEW);
        String caller_id_name = apiUtil.getWebclientPerson();
        Assert.assertEquals(caller_id_name,"A test");


    }

    /**
     * 22.登录分机1000,查看分机列表1001
     * 	name显示B test2
     */
    @Epic("P_Series")
    @Feature("Preferences-Name Display Format")
    @Story("Name Display Format")
    @Description("分机1000登录，查看分机列表1001,1001的Caller ID Name显示B test2\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Preferences-Basic","Name Display Format","P2"})
    public void testPreferences_22_NameDisplayFormat(){
//        网页写法，容易误报
//        auto.loginPage().login("1000",EXTENSION_PASSWORD_NEW);
//        auto.me_homePage().ext_name.shouldHave(Condition.exactText("B test2"));
        apiUtil.preferencesUpdate("{\"name_disp_fmt\":\"last_first\"}").apply();
        apiUtil.loginWeb("1000",EXTENSION_PASSWORD_NEW);
        String callerIDName_1001 = apiUtil.getWebclientExtensionSummary("1001").caller_id_name;
        Assert.assertEquals(callerIDName_1001,"B test2");

    }


    @Epic("P_Series")
        @Feature("Preferences-Name Display Format")
        @Story("Name Display Format")
        @Description("1.设置Name Display Format：Last Name First Name with Space Inbetween，" +
                "1.分机1000呼叫分机1001，预期：CDR的CallFrom分机1000的nameA test，CallTO分机1001显示B test2，reason为分机B挂断，分机B的name正确\n")
        @Severity(SeverityLevel.BLOCKER)
        @TmsLink(value = "")
        @Issue("")
        @Test(groups = {"PSeries","Preferences-Basic","Name Display Format","P2"})

        public void testPreferences_30_NameDisplayFormat() {
            prerequisite();
            apiUtil.preferencesUpdate("{\"name_disp_fmt\":\"last_first\"}").apply();

            step("2:[caller] 1000" + ",[callee] 1001");
            pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

            step("[通话状态校验]");
            softAssertPlus.assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
            Selenide.sleep(5000);
            pjsip.Pj_Answer_Call(1001, false);
            softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

            step("1001 被叫挂断");
            pjsip.Pj_hangupCall(1001);

            assertStep("[CDR校验]");
            softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                    .contains(Assertions.tuple("A test<1000>", "B test2<1001>", CDRObject.STATUS.ANSWER.toString(), "B test2<1001> hung up", "", "", "Internal"));

            softAssertPlus.assertAll();

            }
    @Epic("P_Series")
    @Feature("Preferences-Name Display Format")
    @Story("Name Display Format")
    @Description("1.分机1000呼叫分机1001，未接，预期：CDR的CallFrom分机1000的name 正确，CallTO分机1001显示 正确，reason为分机A挂断，分机B的name正确\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = "PSeries,Preferences-Basic,Name Display Format,P2")
    public void testPreferences_31_NameDisplayFormat() {
        prerequisite();
        apiUtil.preferencesUpdate("{\"name_disp_fmt\":\"last_first\"}").apply();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        Selenide.sleep(5000);

//        softAssertPlus.assertThat(getExtensionStatus(1001, IDLE, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("1001 被叫挂断");
        pjsip.Pj_hangupCall(1000);
        Selenide.sleep(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(Assertions.tuple("A test<1000>", "B test2<1001>", CDRObject.STATUS.NO_ANSWER.toString(), "A test<1000> hung up", "", "", "Internal"));

        softAssertPlus.assertAll();

    }
    @Epic("P_Series")
    @Feature("Preferences-Name Display Format")
    @Story("Name Display Format")
    @Description("1.3002通过SIP外线呼叫分机1000，应答，预期：CDR的CallFrom分机3002 ，CallTO分机1000显示A test，reason为分机1000挂断\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Preferences-Basic","Name Display Format","P2"})

    public void testPreferences_32_NameDisplayFormat() {
        prerequisite();
        apiUtil.preferencesUpdate("{\"name_disp_fmt\":\"last_first\"}").apply();

        step("2:[caller] 3002" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(3002, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        Selenide.sleep(5000);
        pjsip.Pj_Answer_Call(1000, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("1000 被叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(Assertions.tuple("3002<3002>", "A test<1000>", CDRObject.STATUS.ANSWER.toString(), "A test<1000> hung up", "sipRegister", "", "Inbound"));

        softAssertPlus.assertAll();

    }
    /**
     * admin登录
     *  33.查看分机列表
     *    1000的Caller ID Name显示Atest
     */
    @Epic("P_Series")
    @Feature("Preferences-Name Display Format")
    @Story("Name Display Format")
    @Description("admin登录，查看分机列表,1000的Caller ID Name显示Atest\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = "PSeries,Preferences-Basic,Name Display Format,P2")
    public void testPreferences_33_NameDisplayFromat(){
        apiUtil.preferencesUpdate("{\"name_disp_fmt\":\"lastfirst\"}").apply();
        String callerIdName_1000 = apiUtil.getExtensionSummary("1000").callerIdName;
        Assert.assertEquals(callerIdName_1000,"Atest");
    }
    /**
     * 分机1000登录
     * 	37.查看右上角名称
     * 		name显示Atest
     */
    @Epic("P_Series")
    @Feature("Preferences-Name Display Format")
    @Story("Name Display Format")
    @Description("分机1000登录，查看右上角名称,1000的Caller ID Name显示Atest\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Preferences-Basic","Name Display Format","P2"})
    public void testPreferences_37_NameDisplayFormat(){
        apiUtil.preferencesUpdate("{\"name_disp_fmt\":\"lastfirst\"}").apply();
        apiUtil.loginWeb("1000",EXTENSION_PASSWORD_NEW);
        String caller_id_name = apiUtil.getWebclientPerson();
        Assert.assertEquals(caller_id_name,"Atest");


    }

    /**
     * 分机1000登录
     *
     * 	38.查看分机列表1001
     * 		name显示Btest2
     */
    @Epic("P_Series")
    @Feature("Preferences-Name Display Format")
    @Story("Name Display Format")
    @Description("分机1000登录，查看分机列表1001,1000的Caller ID Name显示Btest2\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = "PSeries,Preferences-Basic,Name Display Format,P2")
    public void testPreferences_38_NameDisplayFormat(){
//        网页写法
//        auto.loginPage().login("1000",EXTENSION_PASSWORD_NEW);
//        auto.me_homePage().ext_name.shouldHave(Condition.exactText("Btest2"));
        apiUtil.preferencesUpdate("{\"name_disp_fmt\":\"lastfirst\"}").apply();
        apiUtil.loginWeb("1000",EXTENSION_PASSWORD_NEW);
        String callerIDName_1000 = apiUtil.getWebclientExtensionSummary("1001").caller_id_name;
        Assert.assertEquals(callerIDName_1000,"Btest2");
    }

        @Epic("P_Series")
        @Feature("Preferences-Name Display Format")
        @Story("Name Display Format")
        @Description("1.设置Name Display Format：Last Name First Name without Space Inbetween，" +
            "1.分机1000呼叫分机1001，预期：CDR的CallFrom分机1000的nameAtest，CallTO分机1001显示Btest2，reason为分机B挂断，分机B的name正确\n")
        @Severity(SeverityLevel.BLOCKER)
        @TmsLink(value = "")
        @Issue("")
        @Test(groups = {"PSeries","Preferences-Basic","Name Display Format","P1"})

    public void testPreferences_46_NameDisplayFormat() {
        prerequisite();
        apiUtil.preferencesUpdate("{\"name_disp_fmt\":\"lastfirst\"}").apply();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        Selenide.sleep(5000);
        pjsip.Pj_Answer_Call(1001, false);
        softAssertPlus.assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("1001 被叫挂断");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(Assertions.tuple("Atest<1000>","Btest2<1001>", CDRObject.STATUS.ANSWER.toString(), "Btest2<1001> hung up", "", "", "Internal"));

        softAssertPlus.assertAll();

    }
    @Epic("P_Series")
    @Feature("Preferences-Name Display Format")
    @Story("Name Display Format")
    @Description("1.分机1000呼叫分机1001，未接，预期：CDR的CallFrom分机1000的name 正确，CallTO分机1001显示正确，reason为分机B挂断，分机B的name正确\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Preferences-Basic","Name Display Format","P2"})

    public void testPreferences_47_NameDisplayFormat() {
        prerequisite();
        apiUtil.preferencesUpdate("{\"name_disp_fmt\":\"lastfirst\"}").apply();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        softAssertPlus.assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        Selenide.sleep(5000);

//        softAssertPlus.assertThat(getExtensionStatus(1001, IDLE, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("1001 被叫挂断");
        pjsip.Pj_hangupCall(1000);
        Selenide.sleep(2000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(Assertions.tuple("Atest<1000>", "Btest2<1001>", CDRObject.STATUS.NO_ANSWER.toString(), "Atest<1000> hung up", "", "", "Internal"));

        softAssertPlus.assertAll();

    }
    @Epic("P_Series")
    @Feature("Preferences-Name Display Format")
    @Story("Name Display Format")
    @Description("1.3002通过SIP外线呼叫分机1000，应答，预期：CDR的CallFrom分机3002 ，CallTO分机1000显示Atest，reason为分机1000挂断\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = {"PSeries","Preferences-Basic","Name Display Format","P1"})

    public void testPreferences_48_NameDisplayFormat() {
        prerequisite();
        apiUtil.preferencesUpdate("{\"name_disp_fmt\":\"lastfirst\"}").apply();

        step("2:[caller] 3002" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(3002, "3000", DEVICE_ASSIST_1, false);

        step("[通话状态校验]");
        softAssertPlus.assertThat(getExtensionStatus(1000, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        Selenide.sleep(5000);
        pjsip.Pj_Answer_Call(1000, false);
        softAssertPlus.assertThat(getExtensionStatus(1000, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("1000 被叫挂断");
        pjsip.Pj_hangupCall(1000);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(Assertions.tuple("3002<3002>", "Atest<1000>", CDRObject.STATUS.ANSWER.toString(), "Atest<1000> hung up", "sipRegister", "", "Inbound"));

        softAssertPlus.assertAll();

    }






}
