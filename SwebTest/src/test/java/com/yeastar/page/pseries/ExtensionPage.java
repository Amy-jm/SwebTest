package com.yeastar.page.pseries;

import co.boorse.seleniumtable.SeleniumTable;
import co.boorse.seleniumtable.SeleniumTableCell;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.thoughtworks.selenium.Selenium;
import com.yeastar.swebtest.testcase.smokecase.pbxcase.Extension;
import com.yeastar.untils.WaitUntils;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.yeastar.untils.TableUtils.strTableXPATH;

/**
 * @program: P Series
 * @description: 分机配置页面
 * @author: huangjx@yeastar.com
 * @create: 2020/05/27
 */
@Log4j2
public class ExtensionPage extends BasePage implements IExtensionPageElement {


    /**
     * @param extensionName
     * 点击表格，选择分机状态 Available\Away\Business Trip\Do Not Disturb\Lunch Break\Off Work
     * @param status
     */
    public ExtensionPage selectExtensionPresence(String extensionName, String status){
        WebElement tableElement = getWebDriver().findElement(By.xpath(strTableXPATH));
        SeleniumTable table = SeleniumTable.getInstance(tableElement);
        if (table.hasColumn("Extension Number")) {
            List<SeleniumTableCell> header1Cells = table.getColumn("Extension Number");
            for(int row=0; row<header1Cells.size(); row++){
                if(header1Cells.get(row).getText().equals(extensionName)){
                    log.debug("[selectExtensionPresence :find table data,row=] "+row);
                    $(By.xpath("//table/tbody/tr["+(row+1)+"]//div[contains(@class,'editable-cell-value-wrap')]")).click();
                    sleep(500);
                    $(By.xpath("//table/tbody/tr["+(row+1)+"]//div[contains(@class,'ant-form-item')]")).click();
                    sleep(500);
                    $(By.xpath(String.format(SELECT_COMM_XPATH,status))).click();
                    $(By.xpath("//table/tbody/tr["+(row+1)+"]//div[contains(@class,'ant-form-item')]")).click();
                    searchIpt.click();
                }
            }
        }
        return this;
    }

    /**
     * 密码强度不够提示框，选择OK
     * @return
     */
    public ExtensionPage registration_Password_Alert_Exist_And_GoOn(){
        ele_registration_password_not_strong_alert.shouldBe(Condition.exist);
        OKAlertBtn.shouldBe(Condition.enabled).click();
        return this;
    }

    /**
     * 创建SIP extensionNumber分机，密码为 userPassword，
     * @param extensionNumber 分机号，同时默认修改 FirstName,ExtensionNumber,Caller ID(Internal)字段为分机号
     * @param userPassword 用户密码
     * @return 返回分机页面实例，ExtensionPage
     */
    @Step("extensionNumber:{0},userPassword:{1}")
    public ExtensionPage createSipExtension(String extensionNumber, String userPassword) {
        addBtn.shouldBe(Condition.enabled).click();
        ele_add_DropDown_add_Btn.shouldBe(Condition.enabled).click();
        ele_extension_user_first_name.setValue(extensionNumber);
        ele_extension_user_user_password.setValue(userPassword);
        ele_extension_user_number.setValue(extensionNumber);
        ele_extension_user_caller_id.setValue(extensionNumber);
        ele_extension_user_reg_password.setValue("Yeastar202Yeastar202");
        saveBtn.click();
        clickApply();
        return this;
    }

    /**
     * 创建SIP extensionNumber分机，密码为 userPassword，
     * @param extensionNumber 分机号，同时默认修改 FirstName,ExtensionNumber,Caller ID(Internal)字段为分机号
     * @param userPassword 用户密码
     * @return 返回分机页面实例，ExtensionPage
     */

    /**
     * 创建SIP 分机
     * @param firstName
     * @param lastName
     * @param calledID
     * @param registerName
     * @param userPassword
     * @return
     */
    @Step("extensionNumber:{0},firstName:{1},lastName:{2},calledID:{3},registerName:{4},registerPassword:{5}")
    public ExtensionPage createSipExtension(String extensionNumber,String firstName, String lastName,String calledID,String registerName,String userPassword) {
        addBtn.shouldBe(Condition.enabled).click();
        ele_add_DropDown_add_Btn.shouldBe(Condition.enabled).click();
        ele_extension_user_first_name.setValue(firstName);
        ele_extension_user_last_name.setValue(lastName);
        ele_extension_user_caller_id.setValue(calledID);
        ele_extension_user_reg_name.setValue(registerName);
        ele_extension_user_user_password.setValue(userPassword);
        ele_extension_user_number.setValue(extensionNumber);
        ele_extension_user_reg_password.setValue("Yeastar202Yeastar202");
        return this;
    }

    /**
     * 创建SIP 分机
     * @param extensionNumber
     * @param userPassword
     * @param registrationPassword
     * @return
     */
    @Step("extensionNumber:{0},UserPassword:{1},registrationPassword:{2}")
    public ExtensionPage createSipExtension(String extensionNumber, String userPassword,String registrationPassword) {
        addBtn.shouldBe(Condition.enabled).click();
        ele_add_DropDown_add_Btn.shouldBe(Condition.enabled).click();
        ele_extension_user_first_name.setValue(extensionNumber);
        ele_extension_user_user_password.setValue(userPassword);
        ele_extension_user_number.setValue(extensionNumber);
        ele_extension_user_caller_id.setValue(extensionNumber);
        ele_extension_user_reg_password.setValue(registrationPassword);
        saveBtn.click();
        return this;
    }


    @Step("extensionNumber:{0},UserPassword:{1},registrationPassword:{2}")
    public ExtensionPage createSipExtensionAndConf(String extensionNumber, String UserPassword,String registrationPassword) {
        addBtn.shouldBe(Condition.enabled).click();
        ele_add_DropDown_add_Btn.shouldBe(Condition.enabled).click();
        inputComm("First Name", extensionNumber);
        inputComm("User Password", UserPassword);
        inputComm("Extension Number", extensionNumber);
        inputComm("Caller ID (Internal)", extensionNumber);
        inputComm("Registration Password", registrationPassword);
        return this;
    }


    @Step("extensionNumber:{0},UserPassword:{1},registrationPassword:{2}")
    public ExtensionPage configPresence() {
        switchToTab(TABLE_MENU.PRESENCE.getAlias());
        isCheckBoxForSwitch( ele_extension_presence_forward_enb_in_always_forward_checkBox,true);
        return this;
    }

    /**
     * 删除所有分机
     * @return
     */
    @Step("删除所有分机")
    public ExtensionPage deleAllExtension() {
        if (ele_delete_all_checkbox.isEnabled()) {
            Selenide.actions().click(ele_delete_all_checkbox).perform();
            deleteBtn.shouldBe(Condition.visible).click();
            OKAlertBtn.shouldBe(Condition.visible).click();
            sleep(WaitUntils.RETRY_WAIT);
//            applyBtn.shouldBe(Condition.visible).click();
//            sleep(WaitUntils.SHORT_WAIT*3);
        }

        return this;
    }



    /** Tab 菜单切换 **/
    public ExtensionPage switchToTab(String enumTabMenu){
        $(By.xpath(String.format(TAB_COMM_XPATH,enumTabMenu))).shouldBe(Condition.visible).click();
        return this;
    }


    /**
     * 选择表中的第一条数据，编辑
     * @return
     */
    public ExtensionPage editFirstData(){
        ele_editImageForTableFirstTr.shouldBe(Condition.enabled).click();
        return this;
    }

    /**
     * 选择DTMF_MODE
     * @param dtmf_mode
     * @return
     */
    public ExtensionPage select_DTMF_Mode(DTMF_MODE dtmf_mode){
        $(By.xpath(String.format(SELECT_COMM_XPATH,dtmf_mode.getAlias()))).click();
        return this;
    }




    /**
     * 选择Transport
     * @param transport
     * @return
     */
    public ExtensionPage select_Transport(TRANSPORT transport){
        $(By.xpath(String.format(SELECT_COMM_XPATH,transport.getAlias()))).click();
        return this;
    }

    public ExtensionPage selectCombobox(String arg){
        selectComm(arg);
        return this;
    }

    public ExtensionPage isCheckBoxForSwitch(SelenideElement elementCheckBox,Boolean isChecked){

        elementCheckBox.shouldBe(Condition.visible);
        if(!isChecked && elementCheckBox.getAttribute("aria-checked").equals("true")){
            Selenide.actions().click(elementCheckBox).perform();
        }else if(isChecked && elementCheckBox.getAttribute("aria-checked").equals("false")){
            Selenide.actions().click(elementCheckBox).perform();
        }
        return this;
    }

    /**
     * checkbox 设置
     * @param element
     * @param isChecked
     * @return
     */
    public ExtensionPage isCheckbox(SelenideElement element,boolean isChecked){

        if(element.getAttribute("checked")==null){
            if(isChecked){
                new Actions(getWebDriver()).moveToElement(element,2,2).click().build().perform();
            }
        }else{
            if(!isChecked){
                new Actions(getWebDriver()).moveToElement(element,2,2).click().build().perform();
            }
        }

        return this;
    }


}
