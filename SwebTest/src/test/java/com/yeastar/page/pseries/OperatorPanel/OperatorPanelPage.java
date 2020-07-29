package com.yeastar.page.pseries.OperatorPanel;

import co.boorse.seleniumtable.SeleniumTable;
import co.boorse.seleniumtable.SeleniumTableCell;
import co.boorse.seleniumtable.SeleniumTableRow;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.yeastar.controllers.WebDriverFactory;
import com.yeastar.page.pseries.BasePage;
import com.yeastar.untils.WaitUntils;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Selenide.*;

/**
 * @program: SwebTest
 * @description: Operator Panel Page
 * @author: huangjx@yeastar.com
 * @create: 2020/07/27
 */
@Log4j2
public class OperatorPanelPage extends BasePage {

    public String TABLE_INBOUND_XPATH = "//*[@id=\"table-inbound\"]//div[contains(@class,'ant-table-body')]//table";
    public String TABLE_OUTBOUND_XPATH = "//div[@id=\"table-outbound\"]//div[contains(@class,'ant-table-body')]//table";
    private String ACTION_XPATH = "//span[contains(text(),'%s')]";
    private String WHISPER_ACTION_XPATH = "//li[contains(text(),'%s')]";
    private String RIGHT_ACTION_XPATH = "//table/tbody//td[contains(text(),\"%s\")]";

    public SelenideElement transferElementInput = $(By.id("modalIpt"));//transfer 输入框
    public SelenideElement transferElementOKBtn = $(By.xpath("//div[@id=\"dial-transfer-call-panel\"]//button"));//transfer 确定按钮

    /**
     * 表格类型
     **/
    public enum TABLE_TYPE {
       INBOUND, OUTBOUND;
    }

    /**
     * vcp 右键事件
     **/
    public enum RIGHT_EVENT{
        REDIRECT("Redirect"),
        HANG_UP("Hang Up"),
        PICK_UP("Pick Up"),
        TRANSFER("Transfer"),
        PAUSE_RECORD("Pause"),
        Resume_RECORD("Resume"),
        PARKED("Parked"),
        Barge_IN("Barge In"),
        LISTEN("Listen"),
        WHISPER("Whisper");

        private final String alias;

        RIGHT_EVENT(String alias) {
            this.alias = alias;
        }
        public String getAlias() {
            return alias;
        }
    }
    /**
     * 右键表中的元素（tagName）
     * 匹配模式--包含匹配
     * @param tagName
     */
    public  OperatorPanelPage ringTableAction(String tagName,RIGHT_EVENT event,String eventParam){
        sleep(WaitUntils.SHORT_WAIT);
        //右键弹框
        actions().contextClick($(By.xpath(String.format(RIGHT_ACTION_XPATH,tagName)))).perform();
        eventAction(event, eventParam);
        return this;
    }

    /**
     * 右键表中的元素（tagName）
     * @param tagName
     */
    public  OperatorPanelPage ringTableAction(TABLE_TYPE tableType,String tagName,RIGHT_EVENT event,String eventParam){
        if(tableType==TABLE_TYPE.INBOUND){
            actions().contextClick(WebDriverFactory.getDriver().findElement(By.xpath(String.format(TABLE_INBOUND_XPATH+"/tbody//td[contains(text(),\"%s\")]",tagName)))).perform();
        }
        if(tableType==TABLE_TYPE.OUTBOUND){
            actions().contextClick(WebDriverFactory.getDriver().findElement(By.xpath(String.format(TABLE_OUTBOUND_XPATH+"/tbody//td[contains(text(),\"%s\")]",tagName)))).perform();
        }
        eventAction(event, eventParam);
        return this;
    }

    /**
     * 右键事件
     * @param event 右键面板上动作事件
     * @param eventParam event 事件后，所有操作的参数
     */
    private void eventAction(RIGHT_EVENT event,String eventParam){
        //1.类型一,Transfer
        if(event==RIGHT_EVENT.TRANSFER){
            SelenideElement transferElement = $(By.xpath(String.format(ACTION_XPATH,event.alias)));
            $(By.xpath(String.format(ACTION_XPATH,event.alias))).hover();
            sleep(WaitUntils.RETRY_WAIT);
            actions().moveToElement(transferElement,220,0).click().perform();
            if(eventParam != ""){
                transferElementInput.shouldBe(Condition.visible).setValue(eventParam);
            }else{
                log.error("[Transfer eventParam 不能为空！]");
            }
            transferElementOKBtn.click();
            //2.类型二, Parked / Whisper
        }else if(event == RIGHT_EVENT.WHISPER || event == RIGHT_EVENT.PARKED){
            $(By.xpath(String.format(ACTION_XPATH,event.alias))).hover();
            sleep(WaitUntils.RETRY_WAIT);
            $(By.xpath(String.format(WHISPER_ACTION_XPATH,eventParam))).click();
            //3.类型三，直接单击
        }else{
            $(By.xpath(String.format(ACTION_XPATH,event.alias))).click();
        }
    }

    /**
     * 获取表格中记录条数
     * @param tableType 表格类型  OperatorPanelPage.TABLE_TYPE.INBOUND 或是 OperatorPanelPage.TABLE_TYPE.OUTBOUND
     * @return 记录条数
     */
    public  int getTableInboundCounts(TABLE_TYPE tableType){
        int rowCount =  0;
        sleep(2000);
        if(tableType == TABLE_TYPE.INBOUND){
            WebElement tableElement = WebDriverFactory.getDriver().findElement(By.xpath(TABLE_INBOUND_XPATH));
            SeleniumTable table = SeleniumTable.getInstance(tableElement);
            rowCount= table.rowCount();
        }else{
            WebElement tableElement = WebDriverFactory.getDriver().findElement(By.xpath(TABLE_OUTBOUND_XPATH));
            SeleniumTable table = SeleniumTable.getInstance(tableElement);
            rowCount= table.rowCount();
        }
        return rowCount;
    }


    /**
     * 元素拖动，拖动 sourceElement 到 targetElement 上
     * @param sourceElement 源元素
     * @param targetElement 目标元素
     */
    public void dragAndDrop(WebElement sourceElement,WebElement targetElement){
        JavascriptExecutor js = (JavascriptExecutor)WebDriverFactory.getDriver();
        js.executeScript("function createEvent(typeOfEvent) {\n" + "var event =document.createEvent(\"CustomEvent\");\n"
                + "event.initCustomEvent(typeOfEvent,true, true, null);\n" + "event.dataTransfer = {\n" + "data: {},\n"
                + "setData: function (key, value) {\n" + "this.data[key] = value;\n" + "},\n"
                + "getData: function (key) {\n" + "return this.data[key];\n" + "}\n" + "};\n" + "return event;\n"
                + "}\n" + "\n" + "function dispatchEvent(element, event,transferData) {\n"
                + "if (transferData !== undefined) {\n" + "event.dataTransfer = transferData;\n" + "}\n"
                + "if (element.dispatchEvent) {\n" + "element.dispatchEvent(event);\n"
                + "} else if (element.fireEvent) {\n" + "element.fireEvent(\"on\" + event.type, event);\n" + "}\n"
                + "}\n" + "\n" + "function simulateHTML5DragAndDrop(element, destination) {\n"
                + "var dragStartEvent =createEvent('dragstart');\n" + "dispatchEvent(element, dragStartEvent);\n"
                + "var dropEvent = createEvent('drop');\n"
                + "dispatchEvent(destination, dropEvent,dragStartEvent.dataTransfer);\n"
                + "var dragEndEvent = createEvent('dragend');\n"
                + "dispatchEvent(element, dragEndEvent,dropEvent.dataTransfer);\n" + "}\n" + "\n"
                + "var source = arguments[0];\n" + "var destination = arguments[1];\n"
                + "simulateHTML5DragAndDrop(source,destination);", sourceElement, targetElement);
    }

    /**
     * 获取表格中的所有记录
     * @param tableType 表格类型  OperatorPanelPage.TABLE_TYPE.INBOUND 或是 OperatorPanelPage.TABLE_TYPE.OUTBOUND
     * @return
     */
    public List getAllRecord(TABLE_TYPE tableType){
        List<Record> records  = new ArrayList();
        List<WebElement> tableListElement = WebDriverFactory.getDriver().findElements(By.xpath(TABLE_INBOUND_XPATH));
        WebElement tableElement = tableListElement.get(tableListElement.size()-1);
        SeleniumTable table = SeleniumTable.getInstance(tableElement);
        //获取总行数
        int sumRow = table.rowCount();
        for (int i = 0; i < table.rowCount(); i++) {
            SeleniumTableRow row = table.get(i);
            Record record = new Record();
            for (int j = 0; j <row.cellCount(); j++) {
                SeleniumTableCell cell = row.get(j);
                switch (j){
                    case 0:
                        if(tableType == TABLE_TYPE.INBOUND){
                            if(waitElementDisplay($(By.xpath(TABLE_INBOUND_XPATH+"//tr["+ i +"]//td[1]//span[contains(@class,\"dot\")]")),WaitUntils.RETRY_WAIT)){
                                record.setRecordStatus("true");
                            }else{
                                record.setRecordStatus("false");
                            }
                        }else{
                            if(waitElementDisplay($(By.xpath(TABLE_OUTBOUND_XPATH+"//tr["+ i +"]//td[1]//span[contains(@class,\"dot\")]")),WaitUntils.RETRY_WAIT)){
                                record.setRecordStatus("true");
                            }else{
                                record.setRecordStatus("false");
                            }
                        }
                        break;
                    case 1:
                        record.setCaller(cell.getText());
                        break;
                    case 2:
                        record.setCallee(cell.getText());
                        break;
                    case 3:
                        record.setStatus(cell.getText());
                        break;
                    case 4:
                        record.setStrTime(cell.getText());
                        break;
                    case 5:
                        record.setDetails(cell.getText());
                        break;
                    default:
                        log.debug("[getAllRecord default]");
                }
            }
            records.add(record);
        }
        log.debug("[Record list count] "+records.size());
        return records;
    }

    /**
     * 记录枚举
     */
    public enum  RECORD{
        RecordStatus,
        Caller,
        Callee,
        Status,
        StrTime,
        Details;
    }

    /**
     *
     * @param tableType 指定表格  OperatorPanelPage.TABLE_TYPE.INBOUND 或是 OperatorPanelPage.TABLE_TYPE.OUTBOUND
     * @param recordType 记录类型
     * @param recordTypeValue 对应记录类型的值
     * @param getRecordType 获取该类型所在记录的值（getRecordType）
     * @return 返回 getRecordType 对应值
     */
    public String getRecordValue(TABLE_TYPE tableType,RECORD recordType,String recordTypeValue,RECORD getRecordType) {
        List<Record> records = getAllRecord(tableType);
        int targetInt = 0;
        String result = "";
        for (int i = 0; i < records.size(); i++) {
            if ((recordType == RECORD.RecordStatus && records.get(i).getRecordStatus() == recordTypeValue) ||
                    (recordType == RECORD.Caller && records.get(i).getCaller() == recordTypeValue) ||
                    (recordType == RECORD.Callee && records.get(i).getCallee() == recordTypeValue) ||
                    (recordType == RECORD.Status && records.get(i).getStatus() == recordTypeValue) ||
                    (recordType == RECORD.StrTime && records.get(i).getStrTime() == recordTypeValue) ||
                    (recordType == RECORD.Details && records.get(i).getDetails() == recordTypeValue)
            ) {
                targetInt = i;
            }
        }
        if (getRecordType == RECORD.RecordStatus) {
            result = records.get(targetInt).getRecordStatus();
        }else if (getRecordType == RECORD.Caller) {
            result =  records.get(targetInt).getCaller();
        }else if (getRecordType == RECORD.Callee) {
            result =  records.get(targetInt).getCallee();
        }else if (getRecordType == RECORD.Status) {
            result =  records.get(targetInt).getStatus();
        }else if (getRecordType == RECORD.StrTime) {
            result =  records.get(targetInt).getStrTime();
        }else if (getRecordType == RECORD.Details) {
            result =  records.get(targetInt).getDetails();
        }else{
            log.error("[not found "+getRecordType+" value] -->>[TableType] "+tableType+"[recordType:recordTypeValue]-->>"+recordType+recordTypeValue);
        }
    return result;
    }
}