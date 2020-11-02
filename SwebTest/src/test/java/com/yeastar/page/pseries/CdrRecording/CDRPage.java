package com.yeastar.page.pseries.CdrRecording;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.yeastar.controllers.WebDriverFactory;
import com.yeastar.page.pseries.BasePage;
import com.yeastar.untils.TableUtils;
import com.yeastar.untils.WaitUntils;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebDriver;
import org.testng.asserts.SoftAssert;

import static com.codeborne.selenide.Selenide.*;
import static com.yeastar.swebtest.driver.ConfigP.*;
//import static com.yeastar.swebtest.driver.DataReader2.*;

/**
 * @program: SwebTest
 * @description: CDR page
 * @author: huangjx@yeastar.com
 * @create: 2020/06/16
 */
@Log4j2
public class CDRPage extends BasePage implements ICdrPageElement {


    /**
     * 断言CDR记录
     * @param webDriver
     * @param row   在表格中的行数，从0开始计数
     * @param callFrom
     * @param callTo
     * @param status
     * @param reason
     * @param sourceTrunk
     * @param destTrunk
     * @param communicationType
     * @return
     */
    public CDRPage assertCDRRecord(WebDriver webDriver,int row,String callFrom,String callTo,String status,String reason,String communicationType,String sourceTrunk,String destTrunk){

        //todo cdr 显示全选  刷新按键
        SoftAssert softAssert = new SoftAssert();
        ele_download_cdr_btn.waitUntil(Condition.visible,WaitUntils.SHORT_WAIT);
        ele_refresh_cdr_btn.click();
        sleep(WaitUntils.SHORT_WAIT);
        softAssert.assertEquals(TableUtils.getTableForHeader(WebDriverFactory.getDriver(),CDR_HEADER.Call_From.getAlias(),row),callFrom);
        softAssert.assertEquals(TableUtils.getTableForHeader(WebDriverFactory.getDriver(),CDR_HEADER.Call_To.getAlias(),row),callTo);
        softAssert.assertEquals(TableUtils.getTableForHeader(WebDriverFactory.getDriver(),CDR_HEADER.Status.getAlias(),row),status);
        softAssert.assertEquals(TableUtils.getTableForHeader(WebDriverFactory.getDriver(),CDR_HEADER.Reason.getAlias(),row),reason);
        softAssert.assertEquals(TableUtils.getTableForHeader(WebDriverFactory.getDriver(),CDR_HEADER.Communication_Type.getAlias(),row),communicationType);
        softAssert.assertEquals(TableUtils.getTableForHeader(WebDriverFactory.getDriver(),CDR_HEADER.Source_Trunk.getAlias(),row),sourceTrunk);
        softAssert.assertEquals(TableUtils.getTableForHeader(WebDriverFactory.getDriver(),CDR_HEADER.Destination_Trunk.getAlias(),row),destTrunk);
        softAssert.assertAll();

        return this;
    }

    public CDRPage assertCDRRecord(WebDriver webDriver,int row,String callFrom,String callTo,String status,String reason,String communicationType) {
        assertCDRRecord(webDriver,row,callFrom,callTo,status,reason,communicationType,"","");
        return this;
    }

    /**
     * 删除当前页所有CDR
     * @return
     */
    public CDRPage deleteAllCDR(){
        if (ele_delete_all_checkbox.isEnabled()) {
            Selenide.actions().click(ele_delete_all_checkbox).perform();
            deleteBtn.shouldBe(Condition.visible).click();
            OKAlertBtn.shouldBe(Condition.visible).click();
            sleep(WaitUntils.RETRY_WAIT);
        }
        return  this;
    }


    /**
     * cdr field call from
     */
    public enum CALLFROM {
        SIP("3001<3001>"),
        SPS("2000<2000>"),
        FXO("2000<2000>"),
        GSM("GSM<GSM>"),
        ACCOUNT("4000<4000>"),
        E1("2000<2000>"),
        BRI("2000<2000>");

        private final String alias;
        CALLFROM(String alias) {
            this.alias = alias;
        }
        @Override
        public String toString() {
            return this.alias;
        }
    }

    /**
     * cdr field call to
     */
    public enum CALLTO {
        Extension_1000("test A<1000>"),
        Extension_1001("test2 B<1001>"),
        Extension_1002("testta C<1002>"),
        Extension_1003("testa D<1003>"),
        Extension_1004("t estX<1004>"),
        Extension_1020("1020 1020<1020>"),

        IVR0_6200("IVR IVR0_6200<6200>"),
        IVR1_6201("IVR IVR1_6201<6201>"),

        RINGGROUP0_6300("RingGroup RingGroup0<6300>"),
        RINGGROUP1_6301("RingGroup RingGroup1<6301>"),

        QUEUE0_6400("Queue Queue0<6400>"),
        QUEUE1_6401("Queue Queue1<6401>");


        private final String alias;
        CALLTO(String alias) {
            this.alias = alias;
        }
        @Override
        public String toString() {
            return this.alias;
        }
    }

    /**
     * cdr field status
     */
    public enum STATUS {
        ANSWER("ANSWERED"),
        NO_ANSWER("NO ANSWER"),
        BUSY("BUSY"),
        VOICEMAIL("VOICEMAIL");


        private final String alias;
        STATUS(String alias) {
            this.alias = alias;
        }
        @Override
        public String toString() {
            return this.alias;
        }
    }

    /**
     * cdr field source trunk
     */
    public enum SOURCE_TRUNK {
        SIP(SIPTrunk),
        sps(SPS),
        ACCOUNT(ACCOUNTTRUNK),
        BRI(BRI_1),
        ASSIST_GSM(DEVICE_ASSIST_GSM),
        DEVICE_GSM(GSM),
        E_1(E1);

        private final String alias;
        SOURCE_TRUNK(String alias) {
            this.alias = alias;
        }
        @Override
        public String toString() {
            return this.alias;
        }
    }

    /**
     * cdr field COMMUNICATION TYPE
     */
    public enum COMMUNICATION_TYPE {
        INBOUND("Inbound"),
        OUTBOUND("Outbound");

        private final String alias;
        COMMUNICATION_TYPE(String alias) {
            this.alias = alias;
        }
        @Override
        public String toString() {
            return this.alias;
        }
    }

    /**
     * cdr field REASON
     */
    public enum REASON {
        EXTENSION_0_HUNGUP("0<0> hung up"),

        EXTENSION_1000_HUNGUP("test A<1000> hung up"),

        EXTENSION_1001_HUNGUP("1001 B<1001> hung up"),
        REDIRECTED_TO_1000("Redirected to 1000 A<1000>"),

        EXTENSION_2000_HUNGUP("2000<2000> hung up"),
        EXTENSION_2000_CALLED_EXTENSION("2000<2000> called Extension"),

        EXTENSION_3001_CALLED_EXTENSION("3001<3001> called Extension"),
        EXTENSION_3001_DIALED_EXTENSION("3001<3001> dialed Extension"),
        EXTENSION_3001_HUNGUP("3001<3001> hung up"),
        SIP_CALLED_EXTENSION("3001<3001> called Extension"),
        SIP_DIALED_EXTENSION("3001<3001> dialed Extension"),
        SIP_HUNGUP("3001<3001> hung up"),

        EXTENSION_4000_HUNGUP("4000<4000> hung up"),
        EXTENSION_4000_CALLED_EXTENSION("4000<4000> called Extension");

        private final String alias;
        REASON(String alias) {
            this.alias = alias;
        }
        @Override
        public String toString() {
            return this.alias;
        }
    }





}
