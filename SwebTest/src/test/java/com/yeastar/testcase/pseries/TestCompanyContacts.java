package com.yeastar.testcase.pseries;

import com.yeastar.page.pseries.HomePage;
import com.yeastar.page.pseries.TestCaseBaseNew;
import com.yeastar.untils.*;
import com.yeastar.untils.APIObject.CallLogObject;
import io.qameta.allure.*;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.sleep;
import static org.assertj.core.api.Assertions.tuple;

/**
 * company_contacts的测试用例脚本
 * @author huangst
 */
@Log4j2
public class TestCompanyContacts extends TestCaseBaseNew {

    private boolean isRunRecoveryEnvFlag = true;
    private boolean isDebugInitExtensionFlag = !isRunRecoveryEnvFlag;
    //启动子线程，监控asterisk log
    List<AsteriskObject> asteriskObjectList = new ArrayList<AsteriskObject>();

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
            initTrunk();
            initOutbound();
            initInbound();
            isRunRecoveryEnvFlag = registerAllExtensions();
        }
        step("=========== init before class  end =========");
        log.debug("[prerequisite time]:" + (System.currentTimeMillis() - startTime) / 1000 + " Seconds");
    }

    @AfterMethod
    public void afterMethodRecover(){
        log.info("api恢复默认用admin登录");
        apiUtil.loginWeb(LOGIN_USERNAME,LOGIN_PASSWORD);
        apiUtil.deleteAllCompanyContacts();
        log.info("contacts匹配恢复默认");
        apiUtil.contactsOptionsUpdate("{\"match_model\":\"fuzzy_match\",\"fuzzy_count\":7}");
        log.info("分机1000恢复mobile设置为空");
        apiUtil.editExtension("1000","\"mobile_number\":\"\"").apply();
    }

    @DataProvider(name = "route")
    public Object[][] Route(Method method){
        Object[][] ob = null;
        String methodName = method.getName();
        if (methodName == "testContacts_01_AddCompanyContactsSucc"){
            ob = new Object[][]{
//                    呼入路由前缀，主叫号码 ，被叫号码 ，admin-cdr匹配结果 ，采用中继 ，联系人名称
                    {"99", 2000, "1000", "mytestadmincontacts<2000>", SPS, "mytestadmincontacts"}
            };
        }else if(methodName=="testContacts_02_AddPersonalContactsSucc"){
            ob = new Object[][]{
//                    呼入路由前缀，主叫号码，被叫号码 ，admin-cdr匹配结果，分机-cdr匹配结果 ，采用中继，联系人名称
                    {"99", 2000, "1000", "2000<2000>", "mytestpercontacts",SPS, "mytestpercontacts"}
            };
        }else if(methodName == "testContacts_03_PersonalAndCompanyContactsSucc"){
            ob = new Object[][]{
//                    呼入路由前缀，主叫号码，被叫号码 ，，admin-cdr匹配结果，分机-cdr匹配结果，采用中继，个人联系人名称，企业联系人名称
                    {"99", 2000, "1000", "mytestadmincontacts<2000>", "mytestpercontacts",SPS, "mytestpercontacts","mytestadmincontacts"}
            };
        }else if(methodName == "testContacts_04_ExtensionMobileSucc"){
            ob = new Object[][]{
//                    呼入路由前缀，主叫号码 ，被叫号码，，admin-cdr匹配结果，分机-cdr匹配结果，采用中继，个人联系人名称，企业联系人名称
                    {"99", 2000, "1000", "test A<2000>", "test A",SPS, "mytestpercontacts","mytestadmincontacts"}
            };
        }else if(methodName == "testContacts_05_OptionSetNoMatchSucc"){
            ob = new Object[][]{
//                    呼入路由前缀，主叫号码，被叫号码，admin-cdr匹配结果，采用中继，个人联系人名称，企业联系人名称
                    {"99", 2000, "1000", "2000<2000>", SPS, "mytestpercontacts","mytestadmincontacts"}
            };
        }else if(methodName == "testContacts_06_SameNumberDostNotHaveTheSameName"|| methodName == "testContacts_07_SameNumberAndSameName"){
            ob = new Object[][]{
//                    呼入路由前缀，主叫号码 ，被叫号码，admin-cdr匹配结果，分机-cdr匹配结果，采用中继，个人联系人1，企业联系人1，个人联系人2，企业联系人2
                    {"99", 2000, "1000", "testcompanyone<2000>","testpersonalone",SPS,"testpersonalone","testcompanyone","testpersonaltwo","testcompanytwo"}
            };
        }else if(methodName == "testContacts_08_CallOut"){
            ob = new Object[][]{
                    {1000, "2000", "testcompany<2000>","testpersonal",SPS,"testpersonal","testcompany"}
            };
        }
        return ob;
    }

    @Epic("CompanyContacts")
    @Feature("新增企业联系人，无对应个人联系人，然后该联系人呼入到Pbx，进行企业联系人匹配")
    @Description("1:login PBX->2:创建联系人->3:联系人外呼进来，呼入到分机1000->4:admin界面进行cdr记录联系人匹配校验")
    @Severity(SeverityLevel.NORMAL)
    @Test(dataProvider = "route", groups = {"PSeries", "Cloud", "K2","P2","","Contacts"})
    public void testContacts_01_AddCompanyContactsSucc(String routePrefix, int caller, String callee, String cdrCaller, String trunk, String contactsName){
        prerequisite();
        log.info("操作前删除所有联系人");
        apiUtil.deleteAllCompanyContacts().apply();

        log.info("设置Name Display Format为first name在前，last name在后");
        apiUtil.preferencesUpdate("{\"name_disp_fmt\":\"first_last\"}").apply();

        step("1:login with admin");
        auto.loginPage().loginWithAdmin();

        step("2:创建联系人");
        auto.homePage().intoPage(HomePage.Menu_Level_1.company_contacts);
        ArrayList<String> phoneList = new ArrayList<>(2);
        phoneList.add("2000");
        auto.contactsPage().createCompanyContacts(contactsName,"","S32.138的分机","hst@qq.com",phoneList);

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, DEVICE_ASSIST_2, false);
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
        apiUtil.loginWeb(LOGIN_USERNAME,LOGIN_PASSWORD);
        List<CDRObject> resultCDR = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(cdrCaller, CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), ""+cdrCaller+" hung up", trunk, "", "Inbound"));

        softAssertPlus.assertAll();
    }

    @Epic("PersonalContacts")
    @Feature("新增个人联系人，无对应企业联系人，然后该联系人呼入到Pbx，进行个人联系人匹配")
    @Description("1:删除联系人记录->2:创建联系人->3:联系人外呼进来，呼入到分机1000->4：webclient-》call log界面进行cdr记录联系人匹配校验->5:admin进行cdr校验")
    @Severity(SeverityLevel.NORMAL)
    @Test(dataProvider = "route", groups = {"PSeries", "Cloud", "K2","P2","","Contacts"})
    public void testContacts_02_AddPersonalContactsSucc(String routePrefix, int caller, String callee, String adminCdrCaller, String perCdrCaller, String trunk, String perContactsName){
        prerequisite();

        log.info("操作前删除所有联系人");
        apiUtil.deleteAllCompanyContacts();

        log.info("设置Name Display Format为first name在前，last name在后");
        apiUtil.preferencesUpdate("{\"name_disp_fmt\":\"first_last\"}").apply();

        step("删除个人联系人记录，然后分机创建个人联系人");
        apiUtil.loginWeb(callee,EXTENSION_PASSWORD_NEW);
        apiUtil.delPerContacts().apply();

        auto.loginPage().loginWithExtension(callee, EXTENSION_PASSWORD_NEW);
        auto.homePage().intoPage(HomePage.Menu_Level_1.personal_contacts);
        ArrayList<String> phoneList = new ArrayList<>(2);
        phoneList.add("2000");

        auto.contactsPage().createPersonalContacts(perContactsName,"","S32.138分机1000的联系人","hst@qq.com",phoneList);
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_log);

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[Call log校验]");
        apiUtil.loginWeb(callee,EXTENSION_PASSWORD_NEW);
        List<CallLogObject> resultCDR = apiUtil.getPerCdrRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[Call log校验] Time：" + DataUtils.getCurrentTime()).extracting("perCallType","perCallerId","perContactName","perDisposition","perNumber","perNumberType").contains(tuple(CDRObject.COMMUNICATION_TYPE.INBOUND.toString(),String.valueOf(caller),perCdrCaller,CDRObject.STATUS.ANSWER.toString(),String.valueOf(caller), CallLogObject.NUMBER_TYPE.Personal.toString()));

        assertStep("[CDR校验]");
        apiUtil.loginWeb(LOGIN_USERNAME,LOGIN_PASSWORD);
        List<CDRObject> getCDRRecord = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(getCDRRecord).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(String.valueOf(adminCdrCaller), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), ""+adminCdrCaller+" hung up", trunk, "", "Inbound"));
        softAssertPlus.assertAll();
    }

    @Epic("Personal And CompanyContacts")
    @Feature("新增个人联系人和企业联系人，然后该联系人呼入到Pbx，进行个人/企业联系人匹配")
    @Description("1:login webclient->2:创建联系人->3:联系人外呼进来，呼入到分机1001->4：webclient-》call log界面进行cdr记录联系人匹配校验->5:admin进行cdr校验")
    @Severity(SeverityLevel.NORMAL)
    @Test(dataProvider = "route", groups = {"PSeries", "Cloud", "K2","P2","","Contacts"})
    public void testContacts_03_PersonalAndCompanyContactsSucc(String routePrefix, int caller, String callee, String adminCdrCaller, String perCdrCaller, String trunk, String perContactsName,String comContactsName){
        prerequisite();

        log.info("操作前删除所有联系人");
        apiUtil.deleteAllCompanyContacts();

        log.info("设置Name Display Format为first name在前，last name在后");
        apiUtil.preferencesUpdate("{\"name_disp_fmt\":\"first_last\"}").apply();

        step("创建企业联系人");
        apiUtil.createCompanyContacts("{\"first_name\":\""+comContactsName+"\",\"last_name\":\"\",\"company\":\"\",\"email\":\"\",\"zip_code\":\"\",\"street\":\"\",\"city\":\"\",\"state\":\"\",\"country\":\"\",\"number_list\":[{\"new\":true,\"id\":1616583707962,\"num_type\":\"business_number\",\"number\":\""+caller+"\"}]}");

        step("删除个人联系人记录，然后分机创建个人联系人");
        apiUtil.loginWeb(callee,EXTENSION_PASSWORD_NEW);
        apiUtil.delPerContacts().apply();

        auto.loginPage().loginWithExtension(callee, EXTENSION_PASSWORD_NEW);
        auto.homePage().intoPage(HomePage.Menu_Level_1.personal_contacts);
        ArrayList<String> perPhoneList = new ArrayList<>(2);
        perPhoneList.add(String.valueOf(caller));

        auto.contactsPage().createPersonalContacts(perContactsName,"","S32.138分机1000的联系人","hst@qq.com",perPhoneList);
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_log);

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[Call log校验]");
        apiUtil.loginWeb(callee,EXTENSION_PASSWORD_NEW);
        List<CallLogObject> resultCDR = apiUtil.getPerCdrRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[Call log校验] Time：" + DataUtils.getCurrentTime()).extracting("perCallType","perCallerId","perContactName","perDisposition","perNumber","perNumberType").contains(tuple(CDRObject.COMMUNICATION_TYPE.INBOUND.toString(),String.valueOf(caller),perCdrCaller,CDRObject.STATUS.ANSWER.toString(),String.valueOf(caller),CallLogObject.NUMBER_TYPE.Personal.toString()));

        assertStep("[CDR校验]");
        apiUtil.loginWeb(LOGIN_USERNAME,LOGIN_PASSWORD);
        List<CDRObject> getCDRRecord = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(getCDRRecord).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(String.valueOf(adminCdrCaller), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), ""+adminCdrCaller+" hung up", trunk, "", "Inbound"));
        softAssertPlus.assertAll();
    }

    @Epic("ExtensionMobile")
    @Feature("2000为分机1000的跟随mobile")
    @Description("1->删除联系人记录，然后进行新增企业联系人;2->修改分机1000的mobile为外线号码2000;3->分机登录webclient进行新增个人联系人记录;4->外线号码2000进行呼入到分机1000;5->call log进行联系人匹配校验;6->admin cdr进行联系人匹配校验;7->恢复分机2000的mobile为空")
    @Severity(SeverityLevel.NORMAL)
    @Test(dataProvider = "route", groups = {"PSeries", "Cloud", "K2","P2","","Contacts"})
    public void testContacts_04_ExtensionMobileSucc(String routePrefix, int caller, String callee, String adminCdrCaller, String perCdrCaller, String trunk, String perContactsName,String comContactsName){
        prerequisite();

        log.info("操作前删除所有联系人");
        apiUtil.deleteAllCompanyContacts();

        log.info("设置Name Display Format为first name在前，last name在后");
        apiUtil.preferencesUpdate("{\"name_disp_fmt\":\"first_last\"}");

        step("创建企业联系人");
        apiUtil.createCompanyContacts("{\"first_name\":\""+comContactsName+"\",\"last_name\":\"\",\"company\":\"\",\"email\":\"\",\"zip_code\":\"\",\"street\":\"\",\"city\":\"\",\"state\":\"\",\"country\":\"\",\"number_list\":[{\"new\":true,\"id\":1616583707962,\"num_type\":\"business_number\",\"number\":\""+caller+"\"}]}");

        step("修改分机1000，进行赋予跟随mobile");
        apiUtil.editExtension("1000","\"mobile_number\":\""+caller+"\"").apply();

        step("删除个人联系人记录，然后分机创建个人联系人");
        apiUtil.loginWeb(callee,EXTENSION_PASSWORD_NEW);
        apiUtil.delPerContacts().apply();

        auto.loginPage().loginWithExtension(callee, EXTENSION_PASSWORD_NEW);
        auto.homePage().intoPage(HomePage.Menu_Level_1.personal_contacts);
        ArrayList<String> perPhoneList = new ArrayList<>(2);
        perPhoneList.add(String.valueOf(caller));
        auto.contactsPage().createPersonalContacts(perContactsName,"","S32.138分机1000的联系人","hst@qq.com",perPhoneList);
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_log);

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[Call log校验]");
        List<CallLogObject> resultCDR = apiUtil.getPerCdrRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[Call log校验] Time：" + DataUtils.getCurrentTime()).extracting("perCallType","perCallerId","perContactName","perDisposition","perNumber","perNumberType").contains(tuple(CDRObject.COMMUNICATION_TYPE.INBOUND.toString(),String.valueOf(caller),perCdrCaller,CDRObject.STATUS.ANSWER.toString(),String.valueOf(caller),CallLogObject.NUMBER_TYPE.Mobile.toString()));

        assertStep("[CDR校验]");
        apiUtil.loginWeb(LOGIN_USERNAME,LOGIN_PASSWORD);
        List<CDRObject> getCDRRecord = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(getCDRRecord).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(String.valueOf(adminCdrCaller), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), ""+adminCdrCaller+" hung up", trunk, "", "Inbound"));
        softAssertPlus.assertAll();
    }

    @Epic("option set no match")
    @Feature("匹配模式当前设置不匹配")
    @Description("1->删除联系人记录，进行新增企业联系人，且设置匹配模式为不匹配;2->新增个人联系人记录;3->外线号码2000进行呼入到分机1000;4->call log进行联系人匹配校验;5->admin cdr进行联系人匹配校验;6->恢复匹配模式为模糊匹配7位")
    @Severity(SeverityLevel.NORMAL)
    @Test(dataProvider = "route", groups = {"PSeries", "Cloud", "K2","P2","","Contacts"})
    public void testContacts_05_OptionSetNoMatchSucc(String routePrefix, int caller, String callee, String adminCdrCaller,String trunk, String perContactsName,String comContactsName){
        prerequisite();

        log.info("操作前删除所有联系人");
        apiUtil.deleteAllCompanyContacts();

        log.info("设置Name Display Format为first name在前，last name在后");
        apiUtil.preferencesUpdate("{\"name_disp_fmt\":\"first_last\"}");

        step("创建企业联系人");
        apiUtil.createCompanyContacts("{\"first_name\":\""+comContactsName+"\",\"last_name\":\"\",\"company\":\"\",\"email\":\"\",\"zip_code\":\"\",\"street\":\"\",\"city\":\"\",\"state\":\"\",\"country\":\"\",\"number_list\":[{\"new\":true,\"id\":1616583707962,\"num_type\":\"business_number\",\"number\":\""+caller+"\"}]}");

        step("设置为不匹配");
        apiUtil.contactsOptionsUpdate("{\"match_model\":\"not_match\"}").apply();

        step("操作前删除个人联系人，然后分机创建个人联系人");
        apiUtil.loginWeb(callee,EXTENSION_PASSWORD_NEW);
        apiUtil.delPerContacts().apply();

        auto.loginPage().loginWithExtension(callee, EXTENSION_PASSWORD_NEW);
        auto.homePage().intoPage(HomePage.Menu_Level_1.personal_contacts);
        ArrayList<String> perPhoneList = new ArrayList<>(2);
        perPhoneList.add(String.valueOf(caller));
        auto.contactsPage().createPersonalContacts(perContactsName,"","S32.138分机1000的联系人","hst@qq.com",perPhoneList);
        auto.homePage().intoPage(HomePage.Menu_Level_1.call_log);

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[Call log校验]");
        List<CallLogObject> resultCDR = apiUtil.getPerCdrRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[Call log校验] Time：" + DataUtils.getCurrentTime()).extracting("perCallType","perCallerId","perDisposition","perNumber","perNumberType").contains(tuple(CDRObject.COMMUNICATION_TYPE.INBOUND.toString(),String.valueOf(caller),CDRObject.STATUS.ANSWER.toString(),String.valueOf(caller),CallLogObject.NUMBER_TYPE.External.toString()));

        assertStep("[CDR校验]");
        apiUtil.loginWeb(LOGIN_USERNAME,LOGIN_PASSWORD);
        List<CDRObject> getCDRRecord = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(getCDRRecord).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(String.valueOf(adminCdrCaller), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), ""+adminCdrCaller+" hung up", trunk, "", "Inbound"));
        softAssertPlus.assertAll();
    }

    @Epic("The same number does not have the same name")
    @Feature("同号不同名")
    @Description("1->删除联系人记录，然后进行新增联系人;2->2000外线号码进行呼入到分机1000;3->call log进行联系人匹配校验;4->admin cdr进行联系人匹配校验")
    @Severity(SeverityLevel.NORMAL)
    @Test(dataProvider = "route", groups = {"PSeries", "Cloud", "K2","P2","","Contacts"})
    public void testContacts_06_SameNumberDostNotHaveTheSameName(String routePrefix, int caller, String callee, String adminCdrCaller,String perCdrCaller,String trunk, String perContactsNameOne,String comContactsNameOne, String perContactsNameTwo,String comContactsNameTwo){
        prerequisite();

        log.info("操作前删除所有联系人");
        apiUtil.deleteAllCompanyContacts();

        log.info("设置Name Display Format为first name在前，last name在后");
        apiUtil.preferencesUpdate("{\"name_disp_fmt\":\"first_last\"}");

        step("创建企业联系人");
        apiUtil.createCompanyContacts("{\"first_name\":\""+comContactsNameOne+"\",\"last_name\":\"\",\"company\":\"\",\"email\":\"\",\"zip_code\":\"\",\"street\":\"\",\"city\":\"\",\"state\":\"\",\"country\":\"\",\"number_list\":[{\"new\":true,\"id\":1616583707962,\"num_type\":\"business_number\",\"number\":\""+caller+"\"}]}");
        apiUtil.createCompanyContacts("{\"first_name\":\""+comContactsNameTwo+"\",\"last_name\":\"\",\"company\":\"\",\"email\":\"\",\"zip_code\":\"\",\"street\":\"\",\"city\":\"\",\"state\":\"\",\"country\":\"\",\"number_list\":[{\"new\":true,\"id\":1616583707962,\"num_type\":\"business_number\",\"number\":\"1539888"+caller+"\"}]}").apply();

        step("操作前删除个人联系人，然后分机创建个人联系人");
        apiUtil.loginWeb(callee,EXTENSION_PASSWORD_NEW);
        apiUtil.delPerContacts().apply();

        apiUtil.createPersonalContacts("{\"first_name\":\""+perContactsNameOne+"\",\"last_name\":\"\",\"company\":\"\",\"email\":\"\",\"zip_code\":\"\",\"street\":\"\",\"city\":\"\",\"state\":\"\",\"country\":\"\",\"number_list\":[{\"new\":true,\"id\":1616981912633,\"num_type\":\"business_number\",\"number\":\"1597222"+caller+"\"}],\"type\":\"personal\"}");
        apiUtil.createPersonalContacts("{\"first_name\":\""+perContactsNameTwo+"\",\"last_name\":\"\",\"company\":\"\",\"email\":\"\",\"zip_code\":\"\",\"street\":\"\",\"city\":\"\",\"state\":\"\",\"country\":\"\",\"number_list\":[{\"new\":true,\"id\":1616981912633,\"num_type\":\"business_number\",\"number\":\""+caller+"\"}],\"type\":\"personal\"}");

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[Call log校验]");
        List<CallLogObject> resultCDR = apiUtil.getPerCdrRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[Call log校验] Time：" + DataUtils.getCurrentTime()).extracting("perCallType","perCallerId","perContactName", "perDisposition","perNumber","perNumberType").contains(tuple(CDRObject.COMMUNICATION_TYPE.INBOUND.toString(),String.valueOf(caller),perCdrCaller,CDRObject.STATUS.ANSWER.toString(),String.valueOf(caller),CallLogObject.NUMBER_TYPE.Personal.toString()));

        assertStep("[CDR校验]");
        apiUtil.loginWeb(LOGIN_USERNAME,LOGIN_PASSWORD);
        List<CDRObject> getCDRRecord = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(getCDRRecord).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(String.valueOf(adminCdrCaller), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), ""+adminCdrCaller+" hung up", trunk, "", "Inbound"));
        softAssertPlus.assertAll();
    }

    @Epic("The same number and same name")
    @Feature("同号又同名")
    @Description("1->删除联系人记录，然后进行新增联系人;2->2000外线号码进行呼入到分机1000;3->call log进行联系人匹配校验;4->admin cdr进行联系人匹配校验")
    @Severity(SeverityLevel.NORMAL)
    @Test(dataProvider = "route", groups = {"PSeries", "Cloud", "K2","P2","","Contacts"})
    public void testContacts_07_SameNumberAndSameName(String routePrefix, int caller, String callee, String adminCdrCaller,String perCdrCaller,String trunk, String perContactsNameOne,String comContactsNameOne, String perContactsNameTwo,String comContactsNameTwo){
        prerequisite();

        log.info("操作前删除所有联系人");
        apiUtil.deleteAllCompanyContacts();

        log.info("设置Name Display Format为first name在前，last name在后");
        apiUtil.preferencesUpdate("{\"name_disp_fmt\":\"first_last\"}");

        step("创建企业联系人");
        apiUtil.createCompanyContacts("{\"first_name\":\""+comContactsNameOne+"\",\"last_name\":\"\",\"company\":\"\",\"email\":\"\",\"zip_code\":\"\",\"street\":\"\",\"city\":\"\",\"state\":\"\",\"country\":\"\",\"number_list\":[{\"new\":true,\"id\":1616583707962,\"num_type\":\"business_number\",\"number\":\""+caller+"\"}]}");
        apiUtil.createCompanyContacts("{\"first_name\":\""+comContactsNameTwo+"\",\"last_name\":\"\",\"company\":\"\",\"email\":\"\",\"zip_code\":\"\",\"street\":\"\",\"city\":\"\",\"state\":\"\",\"country\":\"\",\"number_list\":[{\"new\":true,\"id\":1616583707962,\"num_type\":\"business_number\",\"number\":\""+caller+"\"}]}").apply();

        step("操作前删除个人联系人，然后分机创建个人联系人");
        apiUtil.loginWeb(callee,EXTENSION_PASSWORD_NEW);
        apiUtil.delPerContacts().apply();

        apiUtil.createPersonalContacts("{\"first_name\":\""+perContactsNameOne+"\",\"last_name\":\"\",\"company\":\"\",\"email\":\"\",\"zip_code\":\"\",\"street\":\"\",\"city\":\"\",\"state\":\"\",\"country\":\"\",\"number_list\":[{\"new\":true,\"id\":1616981912633,\"num_type\":\"business_number\",\"number\":\""+caller+"\"}],\"type\":\"personal\"}");
        apiUtil.createPersonalContacts("{\"first_name\":\""+perContactsNameTwo+"\",\"last_name\":\"\",\"company\":\"\",\"email\":\"\",\"zip_code\":\"\",\"street\":\"\",\"city\":\"\",\"state\":\"\",\"country\":\"\",\"number_list\":[{\"new\":true,\"id\":1616981912633,\"num_type\":\"business_number\",\"number\":\""+caller+"\"}],\"type\":\"personal\"}");

        step("2:[caller] " + caller + ",[callee] " + routePrefix + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, routePrefix + callee, DEVICE_ASSIST_2, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(1000, RING, 30), RING);
        pjsip.Pj_Answer_Call(1000, false);
        Assert.assertEquals(getExtensionStatus(1000, TALKING, 30), TALKING);
        sleep(WaitUntils.SHORT_WAIT);

        step("[主叫挂断]");
        pjsip.Pj_hangupCall(2000);

        assertStep("[Call log校验]");
        List<CallLogObject> resultCDR = apiUtil.getPerCdrRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[Call log校验] Time：" + DataUtils.getCurrentTime()).extracting("perCallType","perCallerId","perContactName", "perDisposition","perNumber","perNumberType").contains(tuple(CDRObject.COMMUNICATION_TYPE.INBOUND.toString(),String.valueOf(caller),perCdrCaller,CDRObject.STATUS.ANSWER.toString(),String.valueOf(caller),CallLogObject.NUMBER_TYPE.Personal.toString()));

        assertStep("[CDR校验]");
        apiUtil.loginWeb(LOGIN_USERNAME,LOGIN_PASSWORD);
        List<CDRObject> getCDRRecord = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(getCDRRecord).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(String.valueOf(adminCdrCaller), CDRObject.CDRNAME.Extension_1000.toString(), CDRObject.STATUS.ANSWER.toString(), ""+adminCdrCaller+" hung up", trunk, "", "Inbound"));
        softAssertPlus.assertAll();
    }

    @Epic("1000 callout ")
    @Feature("1000外呼2000")
    @Description("1->删除联系人记录，然后进行新增联系人;2->1000外呼到2000;3->call log进行联系人匹配校验;4->admin cdr进行联系人匹配校验")
    @Severity(SeverityLevel.NORMAL)
    @Test(dataProvider = "route",groups = {"PSeries", "Cloud", "K2","P2","","Contacts"})
    public void testContacts_08_CallOut(int caller, String callee, String adminCdrCaller,String perCdrCaller,String trunk, String perContactsName,String comContactsName){
        prerequisite();

        log.info("操作前删除所有联系人");
        apiUtil.deleteAllCompanyContacts();

        log.info("设置Name Display Format为first name在前，last name在后");
        apiUtil.preferencesUpdate("{\"name_disp_fmt\":\"first_last\"}");

        step("创建企业联系人");
        apiUtil.createCompanyContacts("{\"first_name\":\""+comContactsName+"\",\"last_name\":\"\",\"company\":\"\",\"email\":\"\",\"zip_code\":\"\",\"street\":\"\",\"city\":\"\",\"state\":\"\",\"country\":\"\",\"number_list\":[{\"new\":true,\"id\":1616583707962,\"num_type\":\"business_number\",\"number\":\""+callee+"\"}]}").apply();

        step("操作前删除个人联系人，然后分机创建个人联系人");
        apiUtil.loginWeb(String.valueOf(caller),EXTENSION_PASSWORD_NEW);
        apiUtil.delPerContacts().apply();

        apiUtil.createPersonalContacts("{\"first_name\":\""+perContactsName+"\",\"last_name\":\"\",\"company\":\"\",\"email\":\"\",\"zip_code\":\"\",\"street\":\"\",\"city\":\"\",\"state\":\"\",\"country\":\"\",\"number_list\":[{\"new\":true,\"id\":1616981912633,\"num_type\":\"business_number\",\"number\":\""+callee+"\"}],\"type\":\"personal\"}");

        step("2:[caller] " + caller + ",[callee] " + callee + ",[trunk] " + trunk);
        pjsip.Pj_Make_Call_No_Answer(caller, callee, DEVICE_IP_LAN, false);
        sleep(WaitUntils.SHORT_WAIT * 2);

        step("[通话状态校验]");
        Assert.assertEquals(getExtensionStatus(Integer.parseInt(callee), RING, 30), RING);
        pjsip.Pj_Answer_Call(Integer.parseInt(callee), false);
        Assert.assertEquals(getExtensionStatus(Integer.parseInt(callee), TALKING, 30), TALKING);
        sleep(WaitUntils.SHORT_WAIT);

        step("[被叫挂断]");
        pjsip.Pj_hangupCall(Integer.parseInt(callee));

        assertStep("[Call log校验]");
        List<CallLogObject> resultCDR = apiUtil.getPerCdrRecord(1);
        softAssertPlus.assertThat(resultCDR).as("[Call log校验] Time：" + DataUtils.getCurrentTime()).extracting("perCallType","perCallerId","perContactName", "perDisposition","perNumber","perNumberType").contains(tuple(CDRObject.COMMUNICATION_TYPE.OUTBOUND.toString(),callee,perCdrCaller,CDRObject.STATUS.ANSWER.toString(),callee,CallLogObject.NUMBER_TYPE.Personal.toString()));

        assertStep("[CDR校验]");
        apiUtil.loginWeb(LOGIN_USERNAME,LOGIN_PASSWORD);
        List<CDRObject> getCDRRecord = apiUtil.getCDRRecord(1);
        softAssertPlus.assertThat(getCDRRecord).as("[CDR校验] Time：" + DataUtils.getCurrentTime()).extracting("callFrom", "callTo", "status", "reason", "sourceTrunk", "destinationTrunk", "communicatonType")
                .contains(tuple(CDRObject.CDRNAME.Extension_1000.toString(),adminCdrCaller, CDRObject.STATUS.ANSWER.toString(), ""+adminCdrCaller+" hung up", "", trunk, "Outbound"));
        softAssertPlus.assertAll();
    }
}