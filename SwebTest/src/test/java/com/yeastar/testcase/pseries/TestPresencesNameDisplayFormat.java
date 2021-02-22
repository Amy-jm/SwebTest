package com.yeastar.testcase.pseries;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.sun.org.glassfish.gmbal.Description;
import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.AsteriskObject;
import com.yeastar.untils.CDRObject;
import com.yeastar.untils.DataUtils;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.assertj.core.api.Assertions;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.yeastar.swebtest.driver.DataReader2.*;
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
            initRingGroup();
            initQueue();
            initConference();
            initOutbound();
            initIVR();
            initInbound();
            initFeatureCode();
            isRunRecoveryEnvFlag = registerAllExtensions();

            //上面默认的初始化内容，initTestNameDisplayFormat是执行这个用例需要的初始化

            initTestNameDisplayFormat();
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

    @Epic("P_Series")
    @Feature("Preferences-Name Display Format")
    @Story("Name Display Format")
    @Description("1.分机1000呼叫分机1001，预期：CDR的CallFrom分机1000的nametest A，CallTO分机1001显示test2 B，reason为分机B挂断，分机B的name正确\n")
    @Severity(SeverityLevel.BLOCKER)
    @TmsLink(value = "")
    @Issue("")
    @Test(groups = "PSeries,Cloud,K2,Preferences-Basic,Name Display Format,P1")

    public void testPreferences_01_NameDisplayFormat() {
        prerequisite();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        Assertions.assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        Selenide.sleep(5000);
        pjsip.Pj_Answer_Call(1001, false);
        Assertions.assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

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
        @Description("1.设置Name Display Format：Last Name First Name with Space Inbetween，" +
                "1.分机1000呼叫分机1001，预期：CDR的CallFrom分机1000的nameA test，CallTO分机1001显示B test2，reason为分机B挂断，分机B的name正确\n")
        @Severity(SeverityLevel.BLOCKER)
        @TmsLink(value = "")
        @Issue("")
        @Test(groups = "PSeries,Cloud,K2,Preferences-Basic,Name Display Format,P1")

        public void testPreferences_30_NameDisplayFormat() {
            prerequisite();
            apiUtil.preferencesUpdate("{\"name_disp_fmt\":\"last_first\"}").apply();

            step("2:[caller] 1000" + ",[callee] 1001");
            pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

            step("[通话状态校验]");
            Assertions.assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
            Selenide.sleep(5000);
            pjsip.Pj_Answer_Call(1001, false);
            Assertions.assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

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
        @Description("1.设置Name Display Format：Last Name First Name without Space Inbetween，" +
            "1.分机1000呼叫分机1001，预期：CDR的CallFrom分机1000的nameAtest，CallTO分机1001显示Btest2，reason为分机B挂断，分机B的name正确\n")
        @Severity(SeverityLevel.BLOCKER)
        @TmsLink(value = "")
        @Issue("")
        @Test(groups = "PSeries,Cloud,K2,Preferences-Basic,Name Display Format,P1")

    public void testPreferences_46_NameDisplayFormat() {
        prerequisite();
        apiUtil.preferencesUpdate("{\"name_disp_fmt\":\"lastfirst\"}").apply();

        step("2:[caller] 1000" + ",[callee] 1001");
        pjsip.Pj_Make_Call_No_Answer(1000, "1001", DEVICE_IP_LAN, false);

        step("[通话状态校验]");
        Assertions.assertThat(getExtensionStatus(1001, RING, 60)).as("[通话状态校验_响铃] Time：" + DataUtils.getCurrentTime()).isEqualTo(RING);
        Selenide.sleep(5000);
        pjsip.Pj_Answer_Call(1001, false);
        Assertions.assertThat(getExtensionStatus(1001, TALKING, 30)).as("[通话状态校验_通话] Time：" + DataUtils.getCurrentTime()).isEqualTo(TALKING);

        step("1001 被叫挂断");
        pjsip.Pj_hangupCall(1001);

        assertStep("[CDR校验]");
        softAssertPlus.assertThat(apiUtil.getCDRRecord(1)).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(Assertions.tuple("Atest<1000>","Btest2<1001>", CDRObject.STATUS.ANSWER.toString(), "Btest2<1001> hung up", "", "", "Internal"));

        softAssertPlus.assertAll();

    }





}
