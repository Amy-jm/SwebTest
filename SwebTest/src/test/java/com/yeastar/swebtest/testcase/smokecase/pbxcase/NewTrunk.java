package com.yeastar.swebtest.testcase.smokecase.pbxcase;

import com.codeborne.selenide.Condition;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.*;

/**
 * Created by GaGa on 2017-05-15.
 */

public class NewTrunk extends SwebDriver {
    @BeforeClass
    public void BeforeClass() {
        Reporter.infoBeforeClass("打开游览器并登录设备_NewTrunkTest"); //执行操作
        initialDriver("chrome","https://"+ DEVICE_IP_LAN +":"+DEVICE_PORT+"/");
        login(LOGIN_USERNAME,LOGIN_PASSWORD);
        pageDeskTop.settings.shouldBe(Condition.exist);
        pageDeskTop.CDRandRecording.shouldBe(Condition.exist);
        pageDeskTop.maintenance.shouldBe(Condition.exist);
        mySettings.close.click();
    }

    @Test
    public void A_AddTrunk() throws InterruptedException {
        Reporter.infoExec("添加SIPTrunk");
        pageDeskTop.settings.click();
        settings.trunks_panel.click();
        setPageShowNum(trunks.grid,100);
        m_trunks.addTrunk("SIP",add_voIP_trunk_basic.VoipTrunk,"SIPTrunk",DEVICE_ASSIST_1,"",DEVICE_ASSIST_1
                ,"3030","3030","3030","Yeastar202");
    }
    @Test
    public void B_AddTrunkIAXTrunk() throws InterruptedException {
        Reporter.infoExec("添加IAXTrunk");
        m_trunks.addTrunk("IAX",add_voIP_trunk_basic.VoipTrunk,"IAXTrunk",DEVICE_ASSIST_1,"","",
                "3034","","","Yeastar202");
    }
    @Test
    public void C_AddSPX() throws InterruptedException {
        Reporter.infoExec("添加SPX");
        m_trunks.addTrunk("IAX",add_voIP_trunk_basic.PeerToPeer,"SPX",DEVICE_ASSIST_2,"",DEVICE_ASSIST_2,
                "","","","");
    }
    @Test
    public void D_AddSPS() throws InterruptedException {
        Reporter.infoExec("添加SPS");
        m_trunks.addTrunk("SIP",add_voIP_trunk_basic.PeerToPeer,"SPS",DEVICE_ASSIST_2,"",DEVICE_ASSIST_2,
                "","","","");
    }
    //改成辅助设备设置成network模式，减少测试时间
//    @Test
    public void E_SetBriTrunk() throws InterruptedException {
        if(Single_Device_Test){
            pageDeskTop.settings.click();
            settings.trunks_panel.click();
        }
        ys_waitingTime(5000);
        String lineNum =String.valueOf(gridLineNum(trunks.grid)) ;
        int row =0;
        for(row = 1;row<=Integer.parseInt(lineNum);row++){
            String trunkname= String.valueOf(gridContent(trunks.grid,row,trunks.gridcolumn_TrunkName));
            if(trunkname.equals(BRI_1)){
                System.out.println("find bri "+BRI_1+" row="+row);
                break;
            }
        }
        gridClick(trunks.grid,row,trunks.gridEdit);
        ys_waitingMask();

        if(String.valueOf(return_executeJs("Ext.getCmp('switchside').getValue()")).equals("user")){
            executeJs("Ext.getCmp('switchside').setValue('network')");
            edit_bri_trunk.save.click();
            pageDeskTop.reboot_Yes.shouldBe(Condition.exist).click();
            waitReboot();
            login(LOGIN_USERNAME,LOGIN_PASSWORD);
            mySettings.close.click();
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            pbxMonitor.trunks.click();
            String trunkStatus= String.valueOf(gridExtensonStatus(pbxMonitor.grid_Trunks,row,pbxMonitor.gridTrunks_Status));
            YsAssert.assertEquals(trunkStatus,pbxMonitor.Status_UP);
        }else {
            System.out.println("current bri Role " + String.valueOf(return_executeJs("Ext.getCmp('switchside').getValue()")));
        }
    }

    @AfterClass
    public void AfterClass() throws InterruptedException {
        Thread.sleep(10000);
        Reporter.infoAfterClass("关闭游览器_NewTrunk"); //执行操作
//        pjsip.Pj_Destory();
//        ssh.Close();
        quitDriver();
        Thread.sleep(5000);
    }
}
