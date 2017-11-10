package com.yeastar.swebtest.driver.YSMethod;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.apache.regexp.RE;
import org.openqa.selenium.By;

import java.util.ArrayList;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.page;

/**
 * Created by Yeastar on 2017/7/11.
 */
public class YS_Extension extends SwebDriver{
    /*
    * 调整分机页面显示数量
    */
    public void changeExtensionDisplaying(int num) throws InterruptedException {
        switch (num){
            case 10:{
                $(By.xpath(".//div[starts-with(@id,'mypagingtoolbar')]")).shouldBe(Condition.exist);
                String id = (String) executeJs("return Ext.getCmp('control-panel').down('extension').down('mypagingtoolbar').items.items[13].id");
                $(By.id(id + "-trigger-picker")).click();
                $(By.xpath(".//li[@data-recordindex='0' and text()='10']")).click();
            }
            break;
            case 25:{
                $(By.xpath(".//div[starts-with(@id,'mypagingtoolbar')]")).shouldBe(Condition.exist);
                String id = (String) executeJs("return Ext.getCmp('control-panel').down('extension').down('mypagingtoolbar').items.items[13].id");
                $(By.id(id + "-trigger-picker")).click();
                $(By.xpath(".//li[@data-recordindex='1' and text()='25']")).click();
            }
            break;
            case 50:{
                $(By.xpath(".//div[starts-with(@id,'mypagingtoolbar')]")).shouldBe(Condition.exist);
                String id = (String) executeJs("return Ext.getCmp('control-panel').down('extension').down('mypagingtoolbar').items.items[13].id");
                $(By.id(id + "-trigger-picker")).click();
                $(By.xpath(".//li[@data-recordindex='2' and text()='50']")).click();
            }
            break;
            case 100:{
                $(By.xpath(".//div[starts-with(@id,'mypagingtoolbar')]")).shouldBe(Condition.exist);
                String id = (String) executeJs("return Ext.getCmp('control-panel').down('extension').down('mypagingtoolbar').items.items[13].id");
                $(By.id(id + "-trigger-picker")).click();
                $(By.xpath(".//li[@data-recordindex='3' and text()='100']")).click();
            }
            break;
            default:
                break;
        }
    }

    /**
     * 统一设置分机号 密码等
     */
    public void setGeneralInfo(int username,String password,String port) {
        if(null!=port){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            addExtensionBasic.FXS.click();
            executeJs("Ext.getCmp('pbxport').setValue('"+port+"')");

        }
        addExtensionBasic.extensions.setValue(String.valueOf(username));
        addExtensionBasic.callerID.setValue(String.valueOf(username));
        addExtensionBasic.registrationName.setValue(String.valueOf(username));
        addExtensionBasic.registrationPassword.setValue(password);
        addExtensionBasic.name.setValue(String.valueOf(username));
        addExtensionBasic.userPassword.setValue(password);
    }

    /*
    *  批量添加分机 常用信息添加
    * @param: RegistPwdWay 0:默认   1:Random  2:Fixed  3:Prefix + Extension
    * @param: UserPwdWay   0:默认   1:Fixed   2:Prefix + Extension
    */
    public void setBulkGeneralInfo(int StartExension, int CreatNum, int RegistPwdWay, String R_pwd, int UserPwdWay, String U_Pwd){

        addBulkExtensionsBasic.startExtension.setValue(String.valueOf(StartExension));
        addBulkExtensionsBasic.createNumber.setValue(String.valueOf(CreatNum));
        switch (RegistPwdWay){
            case 0:{

            }
            break;
            case 1:{
                executeJs("Ext.getCmp('regpasstype').setValue('random')");
            }
            break;
            case 2:{
                executeJs("Ext.getCmp('regpasstype').setValue('fixed')");
                addBulkExtensionsBasic.registerationFixPassword.setValue(R_pwd);
            }
            break;
            case 3:{
                executeJs("Ext.getCmp('regpasstype').setValue('prefixext')");
                addBulkExtensionsBasic.registerationFixPassword.setValue(R_pwd);
            }
            break;
        }

        switch (UserPwdWay){
            case 0:{
                executeJs("Ext.getCmp('loginpasstype').setValue('random')");
            }
            break;
            case 2:{
                executeJs("Ext.getCmp('loginpasstype').setValue('fixed')");
                if(!U_Pwd.isEmpty()){
                    addBulkExtensionsBasic.prefixPassword.setValue(U_Pwd);
                }
            }
            break;
            case 3:{
                executeJs("Ext.getCmp('loginpasstype').setValue('prefixext')");
                if(!U_Pwd.isEmpty()){
                    addBulkExtensionsBasic.prefixPassword.setValue(U_Pwd);
                }
            }
            break;
        }
    }

    /**
     * 关闭Setting窗口
     */
    public void closeSettingWindow(){
        mySettings.close.click();
        pageDeskTop.boxSettings.shouldBe(Condition.disappear);
    }
    /**
     * 关闭Monitor界面
     */
    public void closeMonitorWindow(){
        mySettings.close.click();
        pageDeskTop.boxCDRandRecordings.shouldBe(Condition.disappear);
    }
    /**
     * 创建SIP分机
     */
    public void addSipExtension(int username, String password) {

        extensions.add.shouldBe(Condition.exist).click();
        ys_waitingTime(3000);
        ys_waitingMask();

        setGeneralInfo(username,password, null);
        if(!PRODUCT.equals(CLOUD_PBX)){
            addExtensionAdvanced.advanced.click();
            setCheckBox(addExtensionAdvanced.registerRemotely,true);
        }
        addExtensionBasic.save.click();
        ys_waitingLoading(extensions.grid_Mask);
    }
    /**
     * 创建FXS分机
     */
    public void addFxsExtension(int username, String password, String port) throws InterruptedException {

        extensions.add.shouldBe(Condition.exist).click();

        setGeneralInfo(username,password,port);

        addExtensionAdvanced.advanced.click();
        setCheckBox(addExtensionAdvanced.registerRemotely,true);
        addExtensionBasic.save.click();
        ys_waitingLoading(extensions.grid_Mask);

        Thread.sleep(1000);

    }
    /**
     * 批量创建分机
     */
    public void addBulkExtensions(int startExension, int createNum, int registPwdWay, String registPwd, int userPwdWay, String userPwd) throws InterruptedException {

        extensions.bulkAdd.click();
        ys_waitingMask();
        setBulkGeneralInfo(startExension,createNum,registPwdWay,registPwd,userPwdWay,userPwd);
        addBulkExtensionsAdvanced.advanced.click();
        setCheckBox(addExtensionAdvanced.registerRemotely,true);
//        addExtensionAdvanced.advanced.click();
//        addExtensionAdvanced.registerRemotely.click();
        addBulkExtensionsAdvanced.save.click();

        ys_waitingLoading(extensions.grid_Mask);
        Thread.sleep(1000);
        String lineAfterAdd = String.valueOf(gridLineNum(extensions.grid)) ;
        System.out.println("哈哈哈"+lineAfterAdd);
        String actual = (String) gridContent(extensions.grid,Integer.parseInt(lineAfterAdd),extensions.gridcolumn_Extensions);
        System.out.println("分机值："+actual);
        for(int i=0; i<10 ; i++){
            pjsip.Pj_CreateAccount(startExension+i,registPwd,"UDP",Integer.valueOf(lineAfterAdd));
        }
        Thread.sleep(1000);
        YsAssert.assertEquals(actual,String.valueOf(startExension+createNum-1),"批量创建分机");
    }
    /**
     * 删除分机
     */
    public void deleteExtension(int username) throws InterruptedException {

        System.out.println("delelte extension pos "+pjsip.getUserAccountInfo(username).pos);
        gridClick(extensions.grid,pjsip.getUserAccountInfo(username).pos,extensions.gridDelete);
        extensions.delete_yes.shouldBe(Condition.exist).click();
//        pjsip.removeAccountByUsername(String.valueOf(username));

    }

    /**
     * 导入分机
     */
    public void ImportExtensions(String file){

        extensions.Import.click();
        ys_waitingTime(1000);
        importExtension.browse.click();
        System.out.println(EXPORT_PATH +file);
        importFile(EXPORT_PATH +file);
        importExtension.Import.click();
        importExtension.ImportOK.click();
    }
    /**
     * CDR通话判断
     */
    //显示Source Trunk   Destination Truenk
    public void showCDRClounm(){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();
        cdRandRecordings.CDR_set.click();
//        String id = String.valueOf(return_executeJs("Ext.query('#'+Ext.query('#'+Ext.getCmp(\"cdr-record\").down('cdrandrecord-edit').down('checkboxgroup').id +' tr td')["+r+"].id + ' div')["+c+"].id"));
//        System.out.println("aaaaa id "+id +"Ext.query('#'+Ext.query('#'+Ext.getCmp(\"cdr-record\").down('cdrandrecord-edit').down('checkboxgroup').id +' tr td')["+r+"].id + ' div')["+c+"].id");
//        setCheckBox(id,b);
        executeJs("Ext.getCmp('cdr-record').down('cdrandrecord-edit').lookupReference('showncol').setValue('0,1,2,6,4,5,6,7,8,9,10')");
        closeCDREditListOptions();
        closeCDRRecord();
    }

    public void checkCDR(String caller, String callee, String status)  {
        checkCDR(caller,callee,status,"","","",1);
    }
    public void checkCDR(String caller, String callee, String status,String source,String destination ,String communition ){
        checkCDR(caller,callee,status,source,destination,communition,1);
    }
    public void checkCDR(String caller, String callee, String status,String source,String destination ,String communition ,int row)  {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR,row,1)).trim(),caller,"CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR,row,2)).trim(),callee,"CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR,row,5)).trim(),status,"CDR_Status检测");
        if(!source.isEmpty()){
            YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR,row,cdRandRecordings.gridColumn_SourceTrunk)).trim(),source,"CDR源中继检测");
        }
        if(!destination.isEmpty()){
            YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR,row,cdRandRecordings.gridColumn_DestinationTrunk)).trim(),destination,"CDR目的中继检测");
        }
        if(!communition.isEmpty()){
            YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR,row,cdRandRecordings.gridColumn_CommunicationTrunk)).trim(),communition,"CDR通讯类型检测");
        }
        closeCDRRecord();
    }
    public void checkCDR_OtherInfo(int col, String info ,int row){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR,row,col)).trim(),info,"CDR检测");
        closeCDRRecord();
    }


    public void checkCDR(String caller,String callee, String status, int... rowList) {
        checkCDR(caller,callee,status,"","","",rowList);
    }
    public void checkCDR(String caller,String callee, String status,String source,String destination ,String communition, int... rowList) {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        boolean findCDR = false;
        for(int row:rowList){
            if(caller.equals(String.valueOf(gridContent(extensions.grid_CDR,row,1)).trim()) ){
                if( callee.equals(String.valueOf(gridContent(extensions.grid_CDR,row,2)).trim())){
                    findCDR = true;
                    YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR,row,1)).trim(),caller,"CDR呼叫方检测");
                    YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR,row,2)).trim(),callee,"CDR被叫方检测");
                    YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR,row,5)).trim(),status,"CDR_Status检测");
                    if(!source.isEmpty()){
                        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR,row,cdRandRecordings.gridColumn_SourceTrunk)).trim(),source,"CDR源中继检测");
                    }
                    if(!destination.isEmpty()){
                        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR,row,cdRandRecordings.gridColumn_DestinationTrunk)).trim(),destination,"CDR目的中继检测");
                    }
                    if(!communition.isEmpty()){
                        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR,row,cdRandRecordings.gridColumn_CommunicationTrunk)).trim(),communition,"CDR通讯类型检测");
                    }
                    break;
                }
            }
        }
        if(!findCDR){
            YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR,1,1)).trim(),caller,"CDR呼叫方检测");
            YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR,1,2)).trim(),callee,"CDR被叫方检测");
            YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR,1,5)).trim(),status,"CDR_Status检测");
            if(!source.isEmpty()){
                YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR,1,cdRandRecordings.gridColumn_SourceTrunk)).trim(),source,"CDR源中继检测");
            }
            if(!destination.isEmpty()){
                YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR,1,cdRandRecordings.gridColumn_DestinationTrunk)).trim(),destination,"CDR目的中继检测");
            }
            if(!communition.isEmpty()){
                YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR,1,cdRandRecordings.gridColumn_CommunicationTrunk)).trim(),communition,"CDR通讯类型检测");
            }
        }
        closeCDRRecord();
    }
    //s时间检查
    public void checkCDR(String caller, String callee, String status,String time,int row) throws InterruptedException {
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.CDRandRecordShortcut.click();
        cdRandRecordings.search.click();
        ys_waitingLoading(cdRandRecordings.grid_Mask);
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR,row,0)).substring(0,13),time,"CDR呼叫时间检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR,row,1)).trim(),caller,"CDR呼叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR,row,2)).trim(),callee,"CDR被叫方检测");
        YsAssert.assertEquals(String.valueOf(gridContent(extensions.grid_CDR,row,5)).trim(),status,"CDR_Status检测");
        closeCDRRecord();
    }
    /**
     * 清空分机组
     */
    public void deleteExtensionGroup() throws InterruptedException {
        pageDeskTop.settings.click();
        settings.extensions_panel.click();
        extensionGroup.extensionGroup.click();

//        $(By.xpath(".//div[starts-with(@id,'mypagingtoolbar')]")).shouldBe(Condition.exist);
//        String id = (String) executeJs("return Ext.getCmp('control-panel').down('extensiongroup').down('mypagingtoolbar').items.items[13].id");
//        $(By.id(id + "-trigger-picker")).click();
//        $(By.xpath(".//li[@data-recordindex='3' and text()='100']")).click();


        Thread.sleep(3000);
        String count = String.valueOf(executeJs("return Ext.getCmp('control-panel').down('extensiongroup').getStore().getCount()"));
        if(Integer.parseInt(count) != 0){
//            String pageCountId = String.valueOf(executeJs("return Ext.query('#control-panel #control-panel-body .x-toolbar-text-default')[0].id"));
//            String page = String.valueOf(executeJs("return document.getElementById(\"" +pageCountId+ "\").innerText"));
//            int pageCount = Integer.parseInt(String.valueOf(page.charAt(2)));
//            System.out.println("page is "+ page + pageCount);
            int pageCount = 1;
            for (int i = 0 ; i< pageCount; i++ ) {
                executeJs(extensions.grid_extensionGroup_allSelect);
//                Number lineAfterAdd = (Number) gridLineNum(extensions.grid_extensionGroup);
                extensionGroup.delete.click();
                extensionGroup.delete_yes.shouldBe(Condition.exist).click();
                extensionGroup.delete.shouldBe(Condition.exist);
//                System.out.println("index = "+ i  +"  "+lineAfterAdd);
//                while(true){
//                    String pleaseWaitCount = String.valueOf(Integer.parseInt(String.valueOf(executeJs("return Ext.query('#control-panel #control-panel-body .x-mask').length")))-1);
//                    if(executeJs("return Ext.query('#control-panel #control-panel-body .x-mask')["+pleaseWaitCount+"].style.display").toString().equals("none")){
//                        break;
//                    }
//                    Thread.sleep(10);
//                }
                Thread.sleep(1000);
            }

        }
    }


    public void addExtensionGroup(String name, int... member) throws InterruptedException {
        ArrayList<String> memberList = new ArrayList();
        for (int item:member){
            System.out.println(item);
            memberList.add(String.valueOf(item));
        }

        extensionGroup.add.click();
        Thread.sleep(5000);
        add_extension_group.name.setValue(name);

        listSelect(add_extension_group.list_ExtensionGroup,extensionList,memberList);
        add_extension_group.save.click();

//        ys_waitingLoading(extensionGroup.grid);
        Thread.sleep(2000);
        String lineNum = String.valueOf(gridLineNum(extensionGroup.grid_Mask)) ;
        Thread.sleep(1000);
        m_extension.assertExtensionGroup(Integer.parseInt(lineNum),name,memberList);

    }

    public void assertExtensionGroup(int line, String name ,ArrayList<String> memberList) throws InterruptedException {

        String actualName = null;
        String actualmember;
        if(!name.isEmpty()){
            actualName = (String) gridContent(extensionGroup.grid,line,extensionGroup.gridcolumn_Name);
            YsAssert.assertEquals(actualName,name);
        }

    }
}
