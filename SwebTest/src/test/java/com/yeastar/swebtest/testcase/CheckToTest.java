package com.yeastar.swebtest.testcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.confirm;

/**
 * Created by GaGa on 2017-05-12.
 */
public class CheckToTest extends SwebDriver {
    /**
     * data.properties里面的数据是否正确
     * 辅助设备的sip线路是否正确
     * 确定可测试后再将测试内容填充进来（xml文件连接进来）
     */
    @Test
    public void CleanExtensions() throws InterruptedException {
        Reporter.infoBeforeClass("清空分机"); //执行操作

        initialDriver(CHROME,"https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
//        initialDriver("chrome","https://"+ "192.168.7.154" +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.settings.click();
        settings.extensions_panel.click();
        deleteTrunk();
        deleteAllExtensions();
        initCallControl();
        setAMI();
        closeFirewall();
        initCDR();
        if(pageDeskTop.apply.isDisplayed()) {
            ys_apply();
        }


        quitDriver();
        Thread.sleep(5000);
    }

    public static void deleteAllExtensions() throws InterruptedException {
        boolean a;
        settings.extensions_tree.click();
//        $(By.xpath(".//div[starts-with(@id,'mypagingtoolbar')]")).shouldBe(Condition.exist);
//        String id = (String) executeJs("return Ext.getCmp('control-panel').down('extension').down('mypagingtoolbar').items.items[13].id");
//        $(By.id(id + "-trigger-picker")).click();
//        $(By.xpath(".//li[@data-recordindex='3' and text()='100']")).click();
        //a = $(By.xpath(".//*[starts-with(@id,'extension-1') and @data-ref='body']//*[starts-with(@id,'ext-element') and @class='x-grid-item-container']")).exists();
        Thread.sleep(3000);
        String count = String.valueOf(executeJs("return Ext.getCmp('control-panel').down('extension').getStore().getCount()"));
        if(Integer.parseInt(count) != 0){
            String pageCountId = String.valueOf(executeJs("return Ext.query('#control-panel #control-panel-body .x-toolbar-text-default')[0].id"));
            String page = String.valueOf(executeJs("return document.getElementById(\"" +pageCountId+ "\").innerText"));
            int pageCount = Integer.parseInt(String.valueOf(page.charAt(2)));
            System.out.println("page is "+ page + pageCount);
            for (int i = 0 ; i< pageCount; i++ ) {
                executeJs("Ext.getCmp('control-panel').down('extension').getSelectionModel().selectAll()");
                Number lineAfterAdd = (Number) gridLineNum(extensions.grid);
                System.out.println("After add extension Lines Num = " + lineAfterAdd);
                extensions.delete.click();
                extensions.delete_yes.shouldBe(Condition.exist).click();
                extensions.delete.shouldBe(Condition.exist);
                //a = $(By.xpath(".//*[starts-with(@id,'extension-1')]//*[starts-with(@id,'loadmask') and text()='Loading...']")).exists();
                System.out.println("index = "+ i  +"  "+lineAfterAdd);
                while(true){
                    String pleaseWaitCount = String.valueOf(Integer.parseInt(String.valueOf(executeJs("return Ext.query('#control-panel #control-panel-body .x-mask').length")))-1);
//                    System.out.println("please wait.."+pleaseWaitCount+ ("return Ext.query('#control-panel #control-panel-body .x-mask')["+pleaseWaitCount+"].style.display"));
                    if(executeJs("return Ext.query('#control-panel #control-panel-body .x-mask')["+pleaseWaitCount+"].style.display").toString().equals("none")){
                        break;
                    }
                    Thread.sleep(10);
                }
                Thread.sleep(1000);
            }
        }
        extensionGroup.extensionGroup.click();
        if(String.valueOf(gridLineNum(extensionGroup.grid)).equals("0")){

        }else {
            gridSeleteAll(extensionGroup.grid);
            extensionGroup.delete.click();
            extensionGroup.delete_yes.click();
        }


    }
    public static void deleteTrunk() throws InterruptedException {
        settings.trunks_tree.click();
        ys_waitingTime(10000);
        m_trunks.showTrunkNum(100);
        if(String.valueOf(gridLineNum(trunks.grid)).equals("0")){

        }else {
            gridSeleteAll(trunks.grid);
            trunks.delete.click();
            trunks.delete_yes.click();
        }

    }
    public static void initCallControl(){
        settings.callControl_tree.click();
        inboundRoutes.inboundRoutes.click();
        ys_waitingLoading(inboundRoutes.gridLoading);
        if(Integer.parseInt(String.valueOf(gridLineNum(inboundRoutes.grid))) != 0){
            gridSeleteAll(inboundRoutes.grid);
            inboundRoutes.delete.click();
            inboundRoutes.delete_yes.click();
        }


        outboundRoutes.outboundRoutes.click();
        ys_waitingLoading(outboundRoutes.grid_Mask);
        if(Integer.parseInt(String.valueOf(gridLineNum(outboundRoutes.grid))) != 0) {
            gridSeleteAll(outboundRoutes.grid);
            outboundRoutes.delete.click();
            outboundRoutes.delete_yes.click();
        }

        outboundRestriction.outboundRestriction.click();
        ys_waitingLoading(outboundRestriction.grid_Mask);
        if(Integer.parseInt(String.valueOf(gridLineNum(outboundRestriction.grid))) != 0) {
            gridSeleteAll(outboundRestriction.grid);
            outboundRestriction.delete.click();
            outboundRestriction.delete_yes.click();
        }

        autoCLIPRoutes.autoCLIPRoutes.click();
        ys_waitingTime(3000);
        autoCLIPRoutes.mt_RemoveAllFromSelect.click();
        autoCLIPRoutes.save.click();

        time_conditions.timeConditions.click();
        timeConditions.timeConditions.click();
        ys_waitingTime(2000);
        if(Integer.parseInt(String.valueOf(gridLineNum(time_conditions.grid))) != 0){
            gridSeleteAll(time_conditions.grid);
            timeConditions.delete.click();
            timeConditions.delete_yes.click();
        }


        holiday.holiday.click();
        ys_waitingTime(2000);
        if(Integer.parseInt(String.valueOf(gridLineNum(holiday.grid))) != 0) {
            gridSeleteAll(holiday.grid);
            holiday.delete.click();
            holiday.delete_yes.click();
        }

        settings.callFeatures_tree.click();
        ivr.IVR.click();
        ys_waitingLoading(ivr.grid_Mask);
        if(Integer.parseInt(String.valueOf(gridLineNum(ivr.grid))) != 0) {
            gridSeleteAll(ivr.grid);
            ivr.delete.click();
            ivr.delete_yes.click();
        }

        ringGroup.ringGroup.click();
        ys_waitingLoading(ringGroup.grid_Mask);
//        ys_waitingTime(3000);
        if(Integer.parseInt(String.valueOf(gridLineNum(ringGroup.grid))) != 0) {
            gridSeleteAll(ringGroup.grid);
            ringGroup.delete.click();
            ringGroup.delete_yes.click();
        }

        queue.queue.click();
        ys_waitingLoading(queue.grid_Mask);
//        ys_waitingTime(3000);
        if(Integer.parseInt(String.valueOf(gridLineNum(queue.grid))) != 0) {
            gridSeleteAll(queue.grid);
            queue.delete.click();
            queue.delete_yes.click();
        }
        conference.conference.click();
        ys_waitingLoading(conference.grid_Mask);
//        ys_waitingTime(3000);
        if(Integer.parseInt(String.valueOf(gridLineNum(conference.grid))) != 0) {
            gridSeleteAll(conference.grid);
            conference.delete.click();
            conference.delete_yes.click();
        }
        pickupGroup.pickupGroup.click();
        ys_waitingLoading(pickupGroup.grid_Mask);
//        ys_waitingTime(3000);
        if(Integer.parseInt(String.valueOf(gridLineNum(pickupGroup.grid))) != 0) {
            gridSeleteAll(pickupGroup.grid);
            pickupGroup.delete.click();
            pickupGroup.delete_yes.click();
        }

        paging_intercom.paging_Intercom.click();
        ys_waitingLoading(paging_intercom.grid_Mask);
//        ys_waitingTime(3000);
        if(Integer.parseInt(String.valueOf(gridLineNum(paging_intercom.grid))) != 0) {
            gridSeleteAll(paging_intercom.grid);
            paging_intercom.delete.click();
            paging_intercom.delete_yes.click();
        }

        callFeatures.more.click();
        callback.callback.click();
        ys_waitingLoading(callback.grid_Mask);
//        ys_waitingTime(3000);
        if(Integer.parseInt(String.valueOf(gridLineNum(callback.grid))) != 0) {
            gridSeleteAll(callback.grid);
            callback.delete.click();
            callback.delete_yes.click();
        }

        disa.DISA.click();
        ys_waitingLoading(disa.grid_Mask);
//        ys_waitingTime(3000);
        if(Integer.parseInt(String.valueOf(gridLineNum(disa.grid))) != 0) {
            gridSeleteAll(disa.grid);
            disa.delete.click();
            disa.delete_yes.click();
        }

        blacklist_whitelist.blacklist_Whitelist.click();
        blacklist.blacklist.click();
        ys_waitingLoading(blacklist.grid_loadMask);
        if(Integer.parseInt(String.valueOf(gridLineNum(blacklist.grid))) != 0) {
            gridSeleteAll(blacklist.grid);
            blacklist.delete.click();
            blacklist.delete_yes.click();
        }

        whitelist.whitelist.click();
        ys_waitingLoading(whitelist.grid_loadMask);
        if(Integer.parseInt(String.valueOf(gridLineNum(whitelist.grid))) != 0) {
            gridSeleteAll(whitelist.grid);
            whitelist.delete.click();
            whitelist.delete_yes.click();
        }


        pinList.PINList.click();
        ys_waitingLoading(pinList.grid_Mask);
//        ys_waitingTime(3000);
        if(Integer.parseInt(String.valueOf(gridLineNum(pinList.grid))) != 0) {
            gridSeleteAll(pinList.grid);
            pinList.delete.click();
            pinList.delete_yes.click();
        }

        speedDial.speedDial.click();
        ys_waitingLoading(speedDial.grid_Mask);
        ys_waitingTime(1000);
        if(Integer.parseInt(String.valueOf(gridLineNum(speedDial.grid))) != 0) {
            gridSeleteAll(speedDial.grid);
            speedDial.delete.click();
            speedDial.delete_yes.click();
        }
    }
    public static void setAMI(){
        settings.system_tree.doubleClick();
        settings.security_tree.click();
        service.service.click();
        ys_waitingTime(5000);
        setCheckBox(service.enableAMI_id,true);
        ys_waitingTime(5000);
        service.enableAMI_Name.setValue("admin");
        service.enableAMI_Password.setValue("password");
        service.enableAMI_Permitted.setValue("0.0.0.0");
        service.enableAMI_Subnet.setValue("0.0.0.0");
        service.save.click();

    }
    public static void closeFirewall(){
        firewallRules.firewallRules.click();
        ys_waitingTime(3000);
        executeJs("Ext.getCmp('st-fwrules-enable').setValue('false')");
//        firewallRules.enableFirewall.click();
//        firewallRules.enableFirewall_Yes.click();
        firewallRules.save.click();
    }
    public static void initCDR(){

        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();
        ys_waitingTime(2000);
        setPageShowNum(cdRandRecordings.grid,100);
        if(Integer.parseInt(String.valueOf(gridLineNum(cdRandRecordings.grid))) != 0) {
            cdRandRecordings.deleteCDR.click();
            cdRandRecordings.delete_yes.click();
        }
    }

}
