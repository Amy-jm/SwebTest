package com.yeastar.swebtest.driver.YSMethod;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.openqa.selenium.By;
import org.testng.Assert;


import static com.codeborne.selenide.Selenide.$;
import static com.yeastar.swebtest.driver.Config.*;
import static com.yeastar.swebtest.driver.SwebDriver.*;

/**
 * Created by Yeastar on 2017/7/14.
 */
public class YS_Trunk {

    /**
     * 退出turnk
     */
    public void closeSettingWindow(){
        mySettings.close.click();
        pageDeskTop.boxSettings.shouldBe(Condition.disappear);
    }

    /**
     * 修改Trunks页面显示数量
     * @param nums
     */
    public void showTrunkNum(int nums) {
        switch (100){
            case 10:{
                $(By.xpath(".//div[starts-with(@id,'mypagingtoolbar')]")).shouldBe(Condition.exist);
                String id = (String) executeJs("return Ext.getCmp('control-panel').down('trunk').down('mypagingtoolbar').items.items[13].id");
                $(By.id(id + "-trigger-picker")).click();
                $(By.xpath(".//li[@data-recordindex='0' and text()='10']")).click();
            }
                break;
            case 25:{
                $(By.xpath(".//div[starts-with(@id,'mypagingtoolbar')]")).shouldBe(Condition.exist);
                String id = (String) executeJs("return Ext.getCmp('control-panel').down('trunk').down('mypagingtoolbar').items.items[13].id");
                $(By.id(id + "-trigger-picker")).click();
                $(By.xpath(".//li[@data-recordindex='1' and text()='20']")).click();
            }
                break;
            case 50:{
                $(By.xpath(".//div[starts-with(@id,'mypagingtoolbar')]")).shouldBe(Condition.exist);
                String id = (String) executeJs("return Ext.getCmp('control-panel').down('trunk').down('mypagingtoolbar').items.items[13].id");
                $(By.id(id + "-trigger-picker")).click();
                $(By.xpath(".//li[@data-recordindex='2' and text()='50']")).click();
            }
                break;
            case 100:{
                $(By.xpath(".//div[starts-with(@id,'mypagingtoolbar')]")).shouldBe(Condition.exist);
                String id = (String) executeJs("return Ext.getCmp('control-panel').down('trunk').down('mypagingtoolbar').items.items[13].id");
                $(By.id(id + "-trigger-picker")).click();
                $(By.xpath(".//li[@data-recordindex='3' and text()='100']")).click();
            }
                break;

        }

    }
//    /**
//     * 删除所有trunk
//     */
//    public void deleteTrunks() {
//        Reporter.infoExec(" 删除所有VoIP外线");
//        pageDeskTop.taskBar_Main.click();
//        pageDeskTop.settingShortcut.click();
//        setPageShowNum(trunks.grid,100);
//        Thread.sleep(3000);
//        String count= String.valueOf(gridLineNum(trunks.grid));
////        String count = String.valueOf(executeJs("return Ext.getCmp('control-panel').down('trunk').down('trunklist').getStore().getCount()"));
//        if(Integer.parseInt(count) >0){
//            String pageCount = (String) executeJs("return document.getElementById(Ext.getCmp('control-panel').down('trunk').down('mypagingtoolbar').down('tbtext').id).innerText");
//            for(int i=0; i<Integer.parseInt(String.valueOf(pageCount.charAt(2))); i++){
//                executeJs("Ext.getCmp('control-panel').down('trunk').down('trunklist').getSelectionModel().selectAll()");
//                trunks.delete.click();
//                trunks.delete_yes.shouldBe(Condition.exist).click();
//                trunks.delete.shouldBe(Condition.exist);
////                while(true){
////                    String pleaseWaitCount = String.valueOf(Integer.parseInt(String.valueOf(executeJs("return Ext.query('#control-panel #control-panel-body .x-mask').length")))-1);
////                    if(executeJs("return Ext.query('#control-panel #control-panel-body .x-mask')["+pleaseWaitCount+"].style.display").toString().equals("none")){
////                        break;
////                    }
////                    Thread.sleep(10);
////                }
//                Thread.sleep(1000);
//            }
//        }
//        Thread.sleep(500);
////        ys_apply();
////        closeSettingWindow();
//    }

    /**
     *
     * @param protocol
     * @param type
     * @param providerName
     * @param hostname
     * @param hostport
     * @param domain
     * @param username
     * @param authenticationName
     * @param fromUser
     * @param password
     * @throws InterruptedException
     */
    public void addTrunk(String protocol,int type,String providerName,String hostname,String hostport,
                            String domain,String username,String authenticationName,String fromUser,String password,String did) {
        if(protocol.equals("IAX") && PRODUCT.equals(CLOUD_PBX)){
            System.out.println("Cloud PBX no support IAX extension");
            Reporter.infoExec("Cloud PBX no support IAX extension");
            return;
        }
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        trunks.add.click();
        ys_waitingMask();
        String typeName = null;
        if(protocol.equals("IAX")){
            executeJs("Ext.getCmp('type').setValue('IAX')");
        }else{
            executeJs("Ext.getCmp('type').setValue('SIP')");
        }

        //
        if(type == 2){
            typeName = "SIP-Peer";
            executeJs("Ext.getCmp('trunktype').setValue('peertopeer')");
            if(protocol.equals(CLOUD_PBX)){
                add_voIP_trunk_basic.didNumber.setValue("584321");
            }
        }else {

            executeJs("Ext.getCmp('trunktype').setValue('voiptrunk')");
        }
        if(type == 1 && protocol.equals("SIP")){
            typeName = "SIP-Register";
        }else if(type == 2 && protocol.equals("SIP")){
            typeName = "SIP-Peer";
        }else if(type == 1 && protocol.equals("IAX")){
            typeName = "IAX-Register";
        }else if(type == 2 && protocol.equals("IAX")){
            typeName = "IAX-Peer";
        }
        //
        ys_waitingTime(2000);
        add_voIP_trunk_basic.providerName.setValue(providerName);
        add_voIP_trunk_basic.hostname.setValue(hostname);

        if(!domain.isEmpty()){
            add_voIP_trunk_basic.domain.setValue(domain);
        }


        if(!hostport.isEmpty()){
            add_voIP_trunk_basic.hostnamePort.setValue(hostport);
        }
        if(!username.isEmpty()){
            add_voIP_trunk_basic.username.setValue(username);
        }

        if(!password.isEmpty()){
            add_voIP_trunk_basic.password.setValue(password);
        }
        if(!authenticationName.isEmpty()){
            add_voIP_trunk_basic.authenticationName.setValue(authenticationName);
        }
        if(!fromUser.isEmpty()){
            add_voIP_trunk_basic.fromUser.setValue(fromUser);
        }
        if (!did.isEmpty()) {
            if (PRODUCT.equals(CLOUD_PBX)) {
                add_voIP_trunk_basic.didNumber.setValue(did);
            }
        }

        if (!(PRODUCT.equals(CLOUD_PBX) && Integer.valueOf(VERSION_SPLIT[1]) >= 7)){
//        选择编码
        add_voIP_trunk_codec.codec.click();
//        listSelectAllbyValue(add_voIP_trunk_codec.list);
            executeJs("Ext.getCmp('allowcodec').setValue('alaw,ulaw,g729,ilbc')");
        }
        add_voIP_trunk_basic.save.click();
        ys_apply();
        ys_waitingLoading(trunks.grid_Mask);

        String lineNum = String.valueOf(gridLineNum(trunks.grid));
        int row=0;
        for(row = Integer.parseInt(lineNum) ; row>0 ; row--){
            String actTrunkName = String.valueOf(gridContent(trunks.grid,row,trunks.gridcolumn_TrunkName));
            if(actTrunkName.equals(providerName)){
                break;
            }
        }
//        m_trunks.assertTrunkGrid("IAXTrunk","IAX-Register","192.168.7.151","3040",row);
        assertTrunkGrid(providerName,typeName,hostname,username,row);
        ys_waitingTime(10000);
        assertTrunkStatus(providerName);
    }
    /**
     * trunk不作状态判断
     * @param protocol
     * @param type
     * @param providerName
     * @param hostname
     * @param hostport
     * @param domain
     * @param username
     * @param authenticationName
     * @param fromUser
     * @param password
     * @throws InterruptedException
     */
    public void addUnavailTrunk(String protocol,int type,String providerName,String hostname,String hostport,
                         String domain,String username,String authenticationName,String fromUser,String password,boolean Assert,String did) {
        if(protocol.equals("IAX") && PRODUCT.equals(CLOUD_PBX)){
            System.out.println("Cloud PBX no support IAX extension");
            Reporter.infoExec("Cloud PBX no support IAX extension");
            return;
        }
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        trunks.add.click();
        ys_waitingMask();
        String typeName = null;
        if(protocol.equals("IAX")){
            executeJs("Ext.getCmp('type').setValue('IAX')");
        }else{
            executeJs("Ext.getCmp('type').setValue('SIP')");
        }

        //
        if(type == 2){
            typeName = "SIP-Peer";
            executeJs("Ext.getCmp('trunktype').setValue('peertopeer')");
        }else {

            executeJs("Ext.getCmp('trunktype').setValue('voiptrunk')");
        }
        if(type == 1 && protocol.equals("SIP")){
            typeName = "SIP-Register";
        }else if(type == 2 && protocol.equals("SIP")){
            typeName = "SIP-Peer";
        }else if(type == 1 && protocol.equals("IAX")){
            typeName = "IAX-Register";
        }else if(type == 2 && protocol.equals("IAX")){
            typeName = "IAX-Peer";
        }
        //
        ys_waitingTime(2000);
        add_voIP_trunk_basic.providerName.setValue(providerName);
        add_voIP_trunk_basic.hostname.setValue(hostname);

        if(!domain.isEmpty()){
            add_voIP_trunk_basic.domain.setValue(domain);
        }


        if(!hostport.isEmpty()){
            add_voIP_trunk_basic.hostnamePort.setValue(hostport);
        }
        if(!username.isEmpty()){
            add_voIP_trunk_basic.username.setValue(username);
        }

        if(!password.isEmpty()){
            add_voIP_trunk_basic.password.setValue(password);
        }
        if(!authenticationName.isEmpty()){
            add_voIP_trunk_basic.authenticationName.setValue(authenticationName);
        }
        if(!fromUser.isEmpty()){
            add_voIP_trunk_basic.fromUser.setValue(fromUser);
        }
        if (!did.isEmpty()) {
            if (PRODUCT.equals(CLOUD_PBX)) {
                add_voIP_trunk_basic.didNumber.setValue(did);
            }
        }
        if (!(PRODUCT.equals(CLOUD_PBX) && Integer.valueOf(VERSION_SPLIT[1]) >= 7)) {
//        选择编码
            add_voIP_trunk_codec.codec.click();
//        listSelectAllbyValue(add_voIP_trunk_codec.list);
            executeJs("Ext.getCmp('allowcodec').setValue('alaw,ulaw,g729,ilbc')");
        }
        add_voIP_trunk_basic.save.click();
        ys_apply();
        ys_waitingLoading(trunks.grid_Mask);

        String lineNum = String.valueOf(gridLineNum(trunks.grid));
        int row=0;
        for(row = Integer.parseInt(lineNum) ; row>0 ; row--){
            String actTrunkName = String.valueOf(gridContent(trunks.grid,row,trunks.gridcolumn_TrunkName));
            if(actTrunkName.equals(providerName)){
                break;
            }
        }
        if(Assert){
//        m_trunks.assertTrunkGrid("IAXTrunk","IAX-Register","192.168.7.151","3040",row);
         assertTrunkGrid(providerName,typeName,hostname,username,row);
         ys_waitingTime(10000);
         assertTrunkStatus(providerName);
        }
    }


    /**
     * 创建Account中继,Caroline新增
     * @param providerName
     * @param username
     * @param authenticationName
     * @param password
     * @throws InterruptedException
     */
    public void addAccountTrunk(String providerName, String username,String authenticationName,String password) {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.settingShortcut.click();
        trunks.add.click();
        ys_waitingMask();
        String typeName = "SIP-Account";
        executeJs("Ext.getCmp('trunktype').setValue('account')");
        ys_waitingTime(2000);
        add_voIP_trunk_basic.providerName.setValue(providerName);
        add_voIP_trunk_basic.username.setValue(username);
        add_voIP_trunk_basic.authenticationName.setValue(authenticationName);
        add_voIP_trunk_basic.password.setValue(password);

        if (!(PRODUCT.equals(CLOUD_PBX) && Integer.valueOf(VERSION_SPLIT[1]) >= 7)) {
//        选择编码
            add_voIP_trunk_codec.codec.click();
//        listSelectAllbyValue(add_voIP_trunk_codec.list);
            executeJs("Ext.getCmp('allowcodec').setValue('alaw,ulaw,g729,ilbc')");
        }
        add_voIP_trunk_basic.save.click();
        ys_apply();
        ys_waitingLoading(trunks.grid_Mask);

        String lineNum = String.valueOf(gridLineNum(trunks.grid));
        int row=0;
        for(row = Integer.parseInt(lineNum) ; row>0 ; row--){
            String actTrunkName = String.valueOf(gridContent(trunks.grid,row,trunks.gridcolumn_TrunkName));
            if(actTrunkName.equals(providerName)){
                break;
            }
        }
        String actualName = (String) gridContent(trunks.grid, row, trunks.gridcolumn_TrunkName);
        YsAssert.assertEquals(actualName,providerName);

        String actualType = (String) gridContent(trunks.grid, row, trunks.gridcolumn_Type);
        YsAssert.assertEquals(actualType,typeName);
        ys_waitingTime(10000);
        assertTrunkStatus(providerName);
    }


    /**
     * 断言Trunk表格
     * @param trunkName
     * @param type
     * @param hostname
     * @param username
     * @throws InterruptedException
     */
    public void assertTrunkGrid(String trunkName, String type, String hostname,String username,int row) {

        String actualName = (String) gridContent(trunks.grid, row, trunks.gridcolumn_TrunkName);
        YsAssert.assertEquals(actualName,trunkName);

        String actualType = (String) gridContent(trunks.grid, row, trunks.gridcolumn_Type);
        YsAssert.assertEquals(actualType,type);

        String actualIP = (String) gridContent(trunks.grid, row, trunks.grid_column_Hostname);
        YsAssert.assertEquals(actualIP,hostname);

//        String actualUsrname = (String) gridContent(trunks.grid, Integer.parseInt(String.valueOf(gridLineNum(trunks.grid))), trunks.grid_column_Username);
//        YsAssert.assertEquals(actualUsrname,username);

    }

    public void  assertTrunkStatus(String trunkName){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.pbxmonitorShortcut.click();
        pbxMonitor.trunks.click();
//        String lineNum =String.valueOf(gridLineNum(pbxMonitor.grid_Trunks)) ;
//        int row =0;
//        for(row = 1;row<=Integer.parseInt(lineNum);row++){
//            String trunkname= String.valueOf(gridContent(pbxMonitor.grid_Trunks,row,pbxMonitor.gridTrunks_Name));
//            if(trunkname.equals(trunkName)){
//                System.out.println("find trunk "+trunkName+" row="+row);
//                break;
//            }
//        }
        int row= gridFindRowByColumn(pbxMonitor.grid_Trunks,pbxMonitor.gridTrunks_Name,trunkName,sort_ascendingOrder);
        String status= gridExtensonStatus(pbxMonitor.grid_Trunks,row,pbxMonitor.gridTrunks_Status);
        YsAssert.assertNotEquals(status,"Registration failed",trunkName+"注册失败");
    }
}
