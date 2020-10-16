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
import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static com.codeborne.selenide.Selenide.*;
import static com.yeastar.controllers.WebDriverFactory.getDriver;
import static com.yeastar.swebtest.driver.DataReader2.UI_MAP;

/**
 * @program: SwebTest
 * @description: Operator Panel Page
 * @author: huangjx@yeastar.com
 * @create: 2020/07/27
 */
@Log4j2
public class OperatorPanelPage extends BasePage {

    public String TABLE_INBOUND_XPATH = "//*[@id=\"table-inbound\"]//div[contains(@class,'ant-table-body')]//table";
    public String TABLE_OUTBOUND_XPATH = "//*[@id=\"table-outbound\"]//div[contains(@class,'ant-table-body')]//table";
    private String ACTION_XPATH = "//span[contains(text(),'%s')]";
    private String WHISPER_ACTION_XPATH = "//li[contains(text(),'%s')]";
    private String RIGHT_ACTION_XPATH = "//table/tbody//td[contains(text(),\"%s\")]";

    public SelenideElement transferElementInput = $(By.id("modalIpt"));//transfer 输入框
    public SelenideElement transferElementOKBtn = $(By.xpath("//div[@id=\"dial-transfer-call-panel\"]//button"));//transfer 确定按钮
    public SelenideElement redirectElementInput = $(By.id("modalIpt"));//redirect input
    public SelenideElement voiceMailImage = $(By.xpath("//i[contains(@class,'anticon anticon-customer-service')]"));//voice mail 图标

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
        REDIRECT( UI_MAP.getString("web_client.redirect")),
        HANG_UP(UI_MAP.getString("web_client.hangup")),
        PICK_UP(UI_MAP.getString("web_client.pickup")),
        TRANSFER(UI_MAP.getString("web_client.transfer")),
        PAUSE_RECORD(UI_MAP.getString("web_client.pause_recording")),
        Resume_RECORD(UI_MAP.getString("web_client.resume_recording")),
        PARKED(UI_MAP.getString("web_client.parked")),
        RETRIEVE(UI_MAP.getString("web_client.unparked")),
        Barge_IN(UI_MAP.getString("web_client.barge_in")),
        LISTEN(UI_MAP.getString("web_client.listen")),
        WHISPER(UI_MAP.getString("web_client.whisper"));

        private final String alias;

        RIGHT_EVENT(String alias) {
            this.alias = alias;
        }
        public String getAlias() {
            return alias;
        }
    }

    /**
     * 获取 右键 菜单明细
     * @param tableType
     * @param tagName
     * @return
     */
    public List<String> getRightEvent(TABLE_TYPE tableType,String tagName){
        sleep(WaitUntils.SHORT_WAIT);
        refresh();//todo  for linux 必须刷新后才能进行邮件操作 chrome 84
        sleep(WaitUntils.SHORT_WAIT);
        List<String> listEvent = new ArrayList<>();
        if(tableType==TABLE_TYPE.INBOUND){
            actions().contextClick(WebDriverFactory.getDriver().findElement(By.xpath(String.format(TABLE_INBOUND_XPATH+"/tbody//td[contains(text(),\"%s\")]",tagName)))).perform();
        }
        if(tableType==TABLE_TYPE.OUTBOUND){
            actions().contextClick(WebDriverFactory.getDriver().findElement(By.xpath(String.format(TABLE_OUTBOUND_XPATH+"/tbody//td[contains(text(),\"%s\")]",tagName)))).perform();
        }
        for (RIGHT_EVENT e : RIGHT_EVENT.values()) {
            System.out.println(e.getAlias().toString());
            if($(By.xpath(String.format(ACTION_XPATH,e.getAlias()))).exists()){
                listEvent.add(e.alias);
            }
        }
        return listEvent;
    }

    /**
     * 右键表中的元素（tagName）
     * 匹配模式--包含匹配
     * @param tagName
     */
    public  OperatorPanelPage rightTableAction(String tagName,RIGHT_EVENT event,String eventParam){
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
    public  OperatorPanelPage rightTableAction(TABLE_TYPE tableType,String tagName,RIGHT_EVENT event,String eventParam){
        refresh();//todo  for linux 必须刷新后才能进行邮件操作 chrome 84
        sleep(WaitUntils.SHORT_WAIT);
        if(tableType==TABLE_TYPE.INBOUND){
            actions().contextClick(WebDriverFactory.getDriver().findElement(By.xpath(String.format(TABLE_INBOUND_XPATH+"/tbody//td[contains(text(),\"%s\")]",tagName)))).perform();
        }
        if(tableType==TABLE_TYPE.OUTBOUND){
            actions().contextClick(WebDriverFactory.getDriver().findElement(By.xpath(String.format(TABLE_OUTBOUND_XPATH+"/tbody//td[contains(text(),\"%s\")]",tagName)))).perform();
        }
        sleep(1000);
        eventAction(event, eventParam);
        return this;
    }

    /**
     * 右键表中的元素（tagName）
     * @param tagName
     */
    public  OperatorPanelPage rightTableAction(TABLE_TYPE tableType,String tagName,RIGHT_EVENT event){
        refresh();//todo  for linux 必须刷新后才能进行邮件操作 chrome 84
        sleep(WaitUntils.SHORT_WAIT);
        if(tableType==TABLE_TYPE.INBOUND){
            actions().contextClick(WebDriverFactory.getDriver().findElement(By.xpath(String.format(TABLE_INBOUND_XPATH+"/tbody//td[contains(text(),\"%s\")]",tagName)))).perform();
        }
        if(tableType==TABLE_TYPE.OUTBOUND){
            actions().contextClick(WebDriverFactory.getDriver().findElement(By.xpath(String.format(TABLE_OUTBOUND_XPATH+"/tbody//td[contains(text(),\"%s\")]",tagName)))).perform();
        }
        eventAction(event, "");
        return this;
    }

    /**
     * 右键表中的元素（tagName）
     * @param tagName
     */
    public  OperatorPanelPage rightTableActionMouserHover(TABLE_TYPE tableType,String tagName,RIGHT_EVENT event){
        refresh();//todo  for linux 必须刷新后才能进行邮件操作 chrome 84
        sleep(WaitUntils.SHORT_WAIT);
        if(tableType==TABLE_TYPE.INBOUND){
            actions().contextClick(WebDriverFactory.getDriver().findElement(By.xpath(String.format(TABLE_INBOUND_XPATH+"/tbody//td[contains(text(),\"%s\")]",tagName)))).perform();
        }
        if(tableType==TABLE_TYPE.OUTBOUND){
            actions().contextClick(WebDriverFactory.getDriver().findElement(By.xpath(String.format(TABLE_OUTBOUND_XPATH+"/tbody//td[contains(text(),\"%s\")]",tagName)))).perform();
        }
        eventActionWithHover(event);
        return this;
    }

    /**
     *
     * @param tableType 源元素 所在区域
     * @param tagName   源元素 名称
     * @param event     右键事件类型
     * @param eventParam  右键事件类型后，所要带的参数
     * @param isClickVoiceMailImage 是否点击VoiceMail图标   redirect--搜索 eventParam，点击eventParam 右边小图标（VoiceMail）
     * @return
     */
    public  OperatorPanelPage rightTableAction(TABLE_TYPE tableType,String tagName,RIGHT_EVENT event,String eventParam,boolean isClickVoiceMailImage){
        refresh();//todo  for linux 必须刷新后才能进行邮件操作 chrome 84
        sleep(WaitUntils.SHORT_WAIT);
        if(tableType==TABLE_TYPE.INBOUND){
            actions().contextClick(WebDriverFactory.getDriver().findElement(By.xpath(String.format(TABLE_INBOUND_XPATH+"/tbody//td[contains(text(),\"%s\")]",tagName)))).perform();
        }
        if(tableType==TABLE_TYPE.OUTBOUND){
            actions().contextClick(WebDriverFactory.getDriver().findElement(By.xpath(String.format(TABLE_OUTBOUND_XPATH+"/tbody//td[contains(text(),\"%s\")]",tagName)))).perform();
        }
        eventAction(event, eventParam,isClickVoiceMailImage);
        return this;
    }


    /**
     * 右键表中的元素（tagName）
     * @param tagName
     */
    public  OperatorPanelPage rightTableAction(TABLE_TYPE tableType,String tagName){
        refresh();//todo  for linux 必须刷新后才能进行邮件操作 chrome 84
        sleep(WaitUntils.SHORT_WAIT);
        if(tableType==TABLE_TYPE.INBOUND){
            actions().contextClick(WebDriverFactory.getDriver().findElement(By.xpath(String.format(TABLE_INBOUND_XPATH+"/tbody//td[contains(text(),\"%s\")]",tagName)))).perform();
        }
        if(tableType==TABLE_TYPE.OUTBOUND){
            actions().contextClick(WebDriverFactory.getDriver().findElement(By.xpath(String.format(TABLE_OUTBOUND_XPATH+"/tbody//td[contains(text(),\"%s\")]",tagName)))).perform();
        }
        return this;
    }

    /**
     * 右键事件
     * @param event 右键面板上动作事件
     * @param eventParam event 事件后，所有操作的参数
     */
    private OperatorPanelPage eventAction(RIGHT_EVENT event,String eventParam){
        //1.类型一,Transfer
        if(event==RIGHT_EVENT.TRANSFER){
            SelenideElement transferElement = $(By.xpath(String.format(ACTION_XPATH,event.alias)));
            $(By.xpath(String.format(ACTION_XPATH,event.alias))).hover();
            sleep(WaitUntils.RETRY_WAIT);
            try{
                actions().moveToElement(transferElement,220,0).click().perform();
            } catch (org.openqa.selenium.WebDriverException ex) {
                log.info("[OperatorPanel eventAction exception And will retry] " + ex);
                new Actions(getDriver()).moveToElement(transferElement,220,0).click().perform();
            }
            if(eventParam != ""){
                transferElementInput.shouldBe(Condition.visible).setValue(eventParam);
            }else{
                log.error("[Transfer eventParam 不能为空！]");
            }
            sleep(1000);
            transferElementOKBtn.click();
            //2.类型二, Parked / Whisper
        }else if(event == RIGHT_EVENT.WHISPER || event == RIGHT_EVENT.PARKED){
            $(By.xpath(String.format(ACTION_XPATH,event.alias))).hover();
            sleep(WaitUntils.RETRY_WAIT);
            $(By.xpath(String.format(WHISPER_ACTION_XPATH,eventParam))).click();
            //3.类型三，直接单击
        }else{
            $(By.xpath(String.format(ACTION_XPATH,event.alias))).click();
            if(event == RIGHT_EVENT.REDIRECT){
                redirectElementInput.setValue(eventParam);
                 actions().sendKeys(Keys.ENTER).perform();
                }
        }
        return this;
    }


    /**
     * 右键事件
     * @param event 右键面板上动作事件
//     * @param eventParam event 事件后，所有操作的参数
     */
    private OperatorPanelPage eventActionWithHover(RIGHT_EVENT event){
//        refresh();//todo  for linux 必须刷新后才能进行邮件操作 chrome 84
        sleep(WaitUntils.SHORT_WAIT);
        if(event==RIGHT_EVENT.TRANSFER){
            SelenideElement transferElement = $(By.xpath(String.format(ACTION_XPATH,event.alias)));
            $(By.xpath(String.format(ACTION_XPATH,event.alias))).hover();
            sleep(WaitUntils.RETRY_WAIT);
        }else{
            $(By.xpath(String.format(ACTION_XPATH,event.alias))).hover();
        }
        return this;
    }

    /**
     * 右键事件
     * @param event 右键面板上动作事件
     * @param eventParam event 事件后，所有操作的参数
     * @param isClickVoiceMailImage 是否单击第一个录音图标
     */
    private OperatorPanelPage eventAction(RIGHT_EVENT event,String eventParam,boolean isClickVoiceMailImage){
        //1.类型一,Transfer
        if(event==RIGHT_EVENT.TRANSFER){
            SelenideElement transferElement = $(By.xpath(String.format(ACTION_XPATH,event.alias)));
            $(By.xpath(String.format(ACTION_XPATH,event.alias))).hover();
            sleep(WaitUntils.RETRY_WAIT);
            try{
                actions().moveToElement(transferElement,220,0).click().perform();
            } catch (org.openqa.selenium.WebDriverException ex) {
                log.info("[OperatorPanel eventAction exception And will retry] " + ex);
                new Actions(getDriver()).moveToElement(transferElement,220,0).click().perform();
        }
            if(eventParam != ""){
                transferElementInput.shouldBe(Condition.visible).setValue(eventParam);
                if(isClickVoiceMailImage){
                    voiceMailImage.click();
                }else{
                    transferElementOKBtn.click();
                }
            }else{
                log.error("[Transfer eventParam 不能为空！]");
            }

            //2.类型二, Parked / Whisper
        }else if(event == RIGHT_EVENT.WHISPER || event == RIGHT_EVENT.PARKED){
            $(By.xpath(String.format(ACTION_XPATH,event.alias))).hover();
            sleep(WaitUntils.RETRY_WAIT);
            $(By.xpath(String.format(WHISPER_ACTION_XPATH,eventParam))).click();
            //3.类型三，直接单击
        }else{
            $(By.xpath(String.format(ACTION_XPATH,event.alias))).click();
            if(event == RIGHT_EVENT.REDIRECT){
                redirectElementInput.setValue(eventParam);
                if(isClickVoiceMailImage){
                    voiceMailImage.click();
                }else{
                    actions().sendKeys(Keys.ENTER).perform();
                }
            }
        }
        return this;
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
    public OperatorPanelPage dragAndDrop(WebElement sourceElement,WebElement targetElement){
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
        return this;
    }


    String ELEMENT_TABLE_XPATH = "//*[@id=\"%s\"]//table/tbody//td[contains(text(),'%s')]";
    String ELEMENT_XPATH = "//*[@id=\"%s\"]//span[contains(text(),'%s')]";

    /**
     * VCP 区域
     * 目前分为5个区域：Inbound（table），OutBound（table）,RingGroup,Queue,Parking，Extension
     */
    public enum DOMAIN{
        INBOUND("table-inbound"),
        OUTBOUND("table-outbound"),
        RINGGROUP("table-ring-group"),
        QUEUE("table-queue"),
        PARKING("table-parking"),
        EXTENSION("table-extension");
        private final String alias;

        DOMAIN(String alias) {
            this.alias = alias;
        }

        public String getAlias() {
            return alias;
        }
    }

    /**
     * 元素 拖动
     * @param sourceDomain 源元素 所在区域
     * @param sourceName 源元素的Text名称
     * @param targetDomain 目标元素，所在区域
     * @param targetName 目标元素名称
     * @return
     */
    public OperatorPanelPage dragAndDrop(DOMAIN sourceDomain,String sourceName,DOMAIN targetDomain,String targetName){
        refresh();//todo  for linux 必须刷新后才能进行邮件操作 chrome 84
        sleep(WaitUntils.SHORT_WAIT);
        SelenideElement sourceElement = null;
        SelenideElement targetElement = null;
        //sourceElement
        if(sourceDomain == DOMAIN.INBOUND || sourceDomain == DOMAIN.OUTBOUND){
             sourceElement = $(By.xpath(String.format(ELEMENT_TABLE_XPATH,sourceDomain.getAlias(),sourceName)));
            log.debug("[sourceName  ] "+String.format(ELEMENT_TABLE_XPATH,sourceDomain.getAlias(),sourceName));
        }else{
             sourceElement = $(By.xpath(String.format(ELEMENT_XPATH,sourceDomain.getAlias(),sourceName)));
        }
        //targetElement
        if(targetDomain == DOMAIN.INBOUND || targetDomain == DOMAIN.OUTBOUND){
            targetElement =  $(By.xpath(String.format(ELEMENT_TABLE_XPATH,targetDomain.getAlias(),targetName)));
        }else{
            targetElement = $(By.xpath(String.format(ELEMENT_XPATH,targetDomain.getAlias(),targetName)));
            log.debug("[target  ] "+String.format(ELEMENT_XPATH,targetDomain.getAlias(),targetName));
        }
        dragAndDrop(sourceElement,targetElement);

//        Mouse mouse = ((HasInputDevices)WebDriverFactory.getDriver()).getMouse();
//
//        mouse.mouseDown((Coordinates) targetElement.getLocation());
//        mouse.mouseUp((Coordinates) targetElement.getLocation());
//        actions().moveToElement(targetElement,2,2).click().perform();
        return this;
    }



    /**
     * 获取表格中的所有记录
     * timeout  30s
     * @param tableType 表格类型  OperatorPanelPage.TABLE_TYPE.INBOUND 或是 OperatorPanelPage.TABLE_TYPE.OUTBOUND
     * @return
     */
    public List<Record> getAllRecord(TABLE_TYPE tableType){
        int timeout = 30;//默认超时时间 Seconds
        return getTableData(tableType,timeout);
    }

    /**
     * 获取表格中的所有记录, 0的时候 throw NoSuchElementException
     * timeout  30s
     * @param tableType 表格类型  OperatorPanelPage.TABLE_TYPE.INBOUND 或是 OperatorPanelPage.TABLE_TYPE.OUTBOUND
     * @return
     */
    public List<Record> getAllRecordNotEquals0(TABLE_TYPE tableType){
        List<Record> records = new ArrayList();
        int timeout = 30;//默认超时时间 Seconds
        records = getTableData(tableType,timeout);
        if(records.size() == 0){
            throw new NoSuchElementException("[表格无数据！！！]");
        }
        return  records;
    }
    /**
     * 获取表格中的所有记录, 0的时候 throw NoSuchElementException
     * timeout seconds
     * @param tableType 表格类型  OperatorPanelPage.TABLE_TYPE.INBOUND 或是 OperatorPanelPage.TABLE_TYPE.OUTBOUND
     * @return
     */
    public List<Record> getAllRecordNotEquals0(TABLE_TYPE tableType,int timeout){
        long startTime = System.currentTimeMillis();
        List<Record> records = new ArrayList();
        records = getTableData(tableType,timeout);
        if(records.size() == 0){
            throw new NoSuchElementException("[表格无数据！！！]");
        }
        log.debug("[Wait table appear] " + (System.currentTimeMillis() - startTime) / 1000 + " Seconds");
        return  records;
    }
    /**
     * 获取表格中的所有记录
     * timeout  30s
     * @param tableType 表格类型  OperatorPanelPage.TABLE_TYPE.INBOUND 或是 OperatorPanelPage.TABLE_TYPE.OUTBOUND
     * @return
     */
    public List<Record> getAllRecord(TABLE_TYPE tableType,int timeout){
        return getTableData(tableType,timeout);
    }

    /**
     * 获取表格中的所有记录,
     * timeout  30s
     * @param tableType 表格类型  OperatorPanelPage.TABLE_TYPE.INBOUND 或是 OperatorPanelPage.TABLE_TYPE.OUTBOUND
     * @return
     */
    private List<Record> getTableData(TABLE_TYPE tableType ,int timeout){
        long startTime = System.currentTimeMillis();
        List<Record> records = new ArrayList();
        int retry = 0;
        do {
            List<WebElement> tableListElement;
            if (tableType == TABLE_TYPE.INBOUND) {
                tableListElement = WebDriverFactory.getDriver().findElements(By.xpath(TABLE_INBOUND_XPATH));
            } else {
                tableListElement = WebDriverFactory.getDriver().findElements(By.xpath(TABLE_OUTBOUND_XPATH));
            }
            WebElement tableElement = tableListElement.get(tableListElement.size() - 1);
            SeleniumTable table = SeleniumTable.getInstance(tableElement);
            //获取总行数
            int sumRow = table.rowCount();
            try {
                for (int i = 0; i < table.rowCount(); i++) {
                    SeleniumTableRow row = table.get(i);
                    Record record = new Record();
                    for (int j = 0; j < row.cellCount(); j++) {
                        SeleniumTableCell cell = row.get(j);
                        switch (j) {
                            case 0:
                                if (tableType == TABLE_TYPE.INBOUND) {
                                    if (waitElementDisplay($(By.xpath(TABLE_INBOUND_XPATH + "//tr[" + i + "]//td[1]//span[contains(@class,\"dot\")]")), WaitUntils.RETRY_WAIT)) {
                                        record.setRecordStatus("true");
                                    } else {
                                        record.setRecordStatus("false");
                                    }
                                } else {
                                    if (waitElementDisplay($(By.xpath(TABLE_OUTBOUND_XPATH + "//tr[" + i + "]//td[1]//span[contains(@class,\"dot\")]")), WaitUntils.RETRY_WAIT)) {
                                        record.setRecordStatus("true");
                                    } else {
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
            } catch (org.openqa.selenium.StaleElementReferenceException ex) {
                log.error("[读取表中数据异常，可能表中已无数据]" + ex);
            }
            log.debug("[Record list count] " + records.size() +" retry-->"+retry);
            sleep(WaitUntils.RETRY_WAIT);
            retry++;
        } while (records.size() == 0 && retry <= timeout);
        log.debug("[Wait table appear] " + (System.currentTimeMillis() - startTime) / 1000 + " Seconds");
        return records;
    }

    /**
     * 获取表格中的所有记录,
     * timeout  30s
     * @param tableType 表格类型  OperatorPanelPage.TABLE_TYPE.INBOUND 或是 OperatorPanelPage.TABLE_TYPE.OUTBOUND
     * @return
     */
    public List<Record> getTableData_(TABLE_TYPE tableType ,int timeout){
        long startTime = System.currentTimeMillis();
        List<Record> records = null;
        int retry = 0;
        do {
            List<WebElement> tableListElement;
            if (tableType == TABLE_TYPE.INBOUND) {
                tableListElement = WebDriverFactory.getDriver().findElements(By.xpath(TABLE_INBOUND_XPATH));
            } else {
                tableListElement = WebDriverFactory.getDriver().findElements(By.xpath(TABLE_OUTBOUND_XPATH));
            }
            WebElement tableElement = tableListElement.get(tableListElement.size() - 1);
            SeleniumTable table = SeleniumTable.getInstance(tableElement);
            //获取总行数
            int sumRow = table.rowCount();
            try {
                for (int i = 0; i < table.rowCount(); i++) {
                    SeleniumTableRow row = table.get(i);
                    Record record = new Record();
                    for (int j = 0; j < row.cellCount(); j++) {
                        SeleniumTableCell cell = row.get(j);
                        switch (j) {
                            case 0:
                                if (tableType == TABLE_TYPE.INBOUND) {
                                    if (waitElementDisplay($(By.xpath(TABLE_INBOUND_XPATH + "//tr[" + i + "]//td[1]//span[contains(@class,\"dot\")]")), WaitUntils.RETRY_WAIT)) {
                                        record.setRecordStatus("true");
                                    } else {
                                        record.setRecordStatus("false");
                                    }
                                } else {
                                    if (waitElementDisplay($(By.xpath(TABLE_OUTBOUND_XPATH + "//tr[" + i + "]//td[1]//span[contains(@class,\"dot\")]")), WaitUntils.RETRY_WAIT)) {
                                        record.setRecordStatus("true");
                                    } else {
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
            } catch (org.openqa.selenium.StaleElementReferenceException ex) {
                log.error("[读取表中数据异常，可能表中已无数据]" + ex);
            }
            log.debug("[Record list count] " + records.size() +" retry-->"+retry);
            sleep(WaitUntils.RETRY_WAIT);
            retry++;
        } while (records.size() == 0 && retry <= timeout);
        log.debug("[Wait table appear] " + (System.currentTimeMillis() - startTime) / 1000 + " Seconds");
        return records;
    }

    /**
     * 获取表格中的所有记录
     * timeout  30s
     * @param tableType 表格类型  OperatorPanelPage.TABLE_TYPE.INBOUND 或是 OperatorPanelPage.TABLE_TYPE.OUTBOUND
     * @return
     */
    public List<Record> waitTableRecordAppear(TABLE_TYPE tableType,int timeout){
        List<Record> records=null;
        records = getTableData(tableType,timeout);
        if(records.size()==0){
            throw new NoSuchElementException("[表格无数据！！！]");
        }
        return  records;
    }

    /**
     * 记录枚举
     */
    public enum  RECORD{
//        RecordStatus,
        Caller,
        Callee,
        Status,
        StrTime,
        Details;
    }

    public enum RECORD_DETAILS{
        QUEUE_RING(UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing").trim()),
        QUEUE_WAITING(UI_MAP.getString("web_client.queue_live.queue_panel.table_desc.ringing").trim()),
        INTERNAL("Internal"),
        INTERNAL_AGENT_RING("Internal, Ringing Agent"),
        INTERNAL_QUEUE("Internal, Queue"),
        INTERNAL_PARKED("Internal, Parked"),
        INTERNAL_RING_GROUP("Internal, Ring Group"),
        INTERNAL_IVR("Internal, IVR"),
        INTERNAL_CONFERENCE("Internal, Conference"),
        INTERNAL_PAGING("Internal, Paging"),
        INTERNAL_VOICEMAIL("Internal, Voicemail"),
        EXTERNAL("External"),
        EXTERNAL_IVR("External, IVR"),
        EXTERNAL_PARKED("External, Parked"),
        EXTERNAL_WAITING_IN_QUEUE("External, Waiting in Queue"),
        EXTERNAL_AGENT_RING("External, Ringing Agent"),
        EXTERNAL_QUEUE("External, Queue"),
        EXTERNAL_RING_GROUP("External, Ring Group"),
        EXTERNAL_CONFERENCE("External, Conference"),
        EXTERNAL_VOICEMAIL("External, Voicemail");

        private final String alias;
        RECORD_DETAILS(String alias) {
            this.alias = alias;
        }
        public String getAlias() {
            return alias;
        }
    }

    public void assertRecordValue(SoftAssertions softAssertPlus, TABLE_TYPE tableType, RECORD recordType, String recordTypeValue, String caller, String callee, String status, String details){
        assertRecordValue(softAssertPlus,tableType,recordType,recordTypeValue, caller,  callee,  status,details,"" );
    }
    /**
     * 断言操作面板某一行内容
     * @param softAssertPlus  传入case的AssertPlus
     * @param tableType       指定表格  OperatorPanelPage.TABLE_TYPE.INBOUND 或是 OperatorPanelPage.TABLE_TYPE.OUTBOUND 用于找到要校验的那一行
     * @param recordType      记录类型  用于找到要校验的那一行
     * @param recordTypeValue 记录类型的值 用于找到要校验的那一行
     * @param caller          预期值
     * @param callee          预期值
     * @param status          预期值
     * @param details         预期值
     */
    public void assertRecordValue(SoftAssertions softAssertPlus, TABLE_TYPE tableType, RECORD recordType, String recordTypeValue, String caller, String callee, String status, String details,String desc){

        sleep(WaitUntils.SHORT_WAIT);

        List<Record> records = getAllRecord(tableType);
        int targetInt = 0;

        if(records.size()!=0){
            for (int i = 0; i < records.size(); i++) {
                if (
//                        (recordType == RECORD.RecordStatus && records.get(i).getRecordStatus() == recordTypeValue) ||
                        recordType == RECORD.Caller && records.get(i).getCaller().contains(recordTypeValue) ||
                                recordType == RECORD.Callee && records.get(i).getCallee().contains(recordTypeValue) ||
                                recordType == RECORD.Status && records.get(i).getStatus().contains(recordTypeValue) ||
                                recordType == RECORD.StrTime && records.get(i).getStrTime().contains(recordTypeValue) ||
                                recordType == RECORD.Details && records.get(i).getDetails().contains(recordTypeValue)
                ) {
                    targetInt = i;
                }
            }

            softAssertPlus.assertThat(records.get(targetInt).getCaller()).as("验证_Caller_"+desc).contains(caller);
            softAssertPlus.assertThat(records.get(targetInt).getCallee()).as("验证_Callee_"+desc).contains(callee);
            softAssertPlus.assertThat(records.get(targetInt).getStatus()).as("验证_Status_"+desc).contains(status);
            softAssertPlus.assertThat(records.get(targetInt).getDetails()).as("验证_Details_"+desc).contains(details);

        }else{
            reportMessage("[没有找到有效记录！！！]");
            softAssertPlus.assertThat(records.size()).as("[没有找到有效记录！！！]").isEqualTo(-1);
        }

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
        if(records.size()!=0){
            for (int i = 0; i < records.size(); i++) {
                if (
//                        (recordType == RECORD.RecordStatus && records.get(i).getRecordStatus() == recordTypeValue) ||
                        recordType == RECORD.Caller && records.get(i).getCaller().contains(recordTypeValue) ||
                        recordType == RECORD.Callee && records.get(i).getCallee().contains(recordTypeValue) ||
                        recordType == RECORD.Status && records.get(i).getStatus().contains(recordTypeValue) ||
                        recordType == RECORD.StrTime && records.get(i).getStrTime().contains(recordTypeValue) ||
                        recordType == RECORD.Details && records.get(i).getDetails().contains(recordTypeValue)
                ) {
                    targetInt = i;
                }
            }
//            if (getRecordType == RECORD.RecordStatus) {
//                result = records.get(targetInt).getRecordStatus();
//            }else
            if (getRecordType == RECORD.Caller) {
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
        }else{
            reportMessage("[没有找到有效记录！！！]");
        }
    return result;
    }

    /**
     * 返回对象记录
     * @param records
     * @param recordType
     * @param recordTypeValue
     * @return
     */
    public Record getRecord(List<Record> records,RECORD recordType,String recordTypeValue) {
        int targetInt = 0;
        if (records.size() != 0) {
            for (int i = 0; i < records.size(); i++) {
                if (
                      recordType == RECORD.Caller && records.get(i).getCaller().contains(recordTypeValue) ||
                        recordType == RECORD.Callee && records.get(i).getCallee().contains(recordTypeValue) ||
                        recordType == RECORD.Status && records.get(i).getStatus().contains(recordTypeValue) ||
                        recordType == RECORD.StrTime && records.get(i).getStrTime().contains(recordTypeValue) ||
                        recordType == RECORD.Details && records.get(i).getDetails().contains(recordTypeValue)
                ) {
                    targetInt = i;
                }
            }

        }else{
            reportMessage("[没有找到有效记录！！！]");
        }
        return records.get(targetInt);
    }
}
