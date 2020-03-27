package com.yeastar.linkusActivationCode.TestCase;

import com.codeborne.selenide.SelenideElement;
import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.driver.SwebDriver;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import org.testng.annotations.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.codeborne.selenide.Selenide.*;

public class KeyCode extends SwebDriver{
    public SelenideElement username_autho2 = $(By.id("username"));
    public SelenideElement password_autho2 = $(By.id("pwd"));
    public SelenideElement ver = $(By.id("verify"));
    public SelenideElement login_autho = $(By.xpath(".//*[@id='login']//button[@type='button']/span"));

    public SelenideElement Linkus_tree = $(By.xpath(".//*[@class='menu_tree']//a[text()='S Series Linkus']"));
    public SelenideElement activa_tree = $(By.id("linkus_manage"));
    public SelenideElement sn = $(By.xpath(".//*[@class='main_body']//input[@id='SN']"));
    public SelenideElement search = $(By.xpath(".//*[text()='Search']"));
    public SelenideElement update = $(By.xpath(".//*[text()='Update']"));
    public SelenideElement extension_auth = $(By.id("Extension"));
    public SelenideElement generate = $(By.id("modalgeneral"));
    public SelenideElement copy = $(By.id("copycode"));


    public SelenideElement linkus_Client_Setting = $(By.xpath(".//*[@id='linkus-body']//span[text()='Linkus Client Settings']"));
    public SelenideElement linkus_Client_Setting_expand = $(By.xpath(".//*[@id='linkus-body']//span[text()='Expand']"));
    public SelenideElement authorizationCode_Input = $(By.id("linkus-activecode-inputEl"));
    public SelenideElement authorizationCode_Active = $(By.xpath(".//*[starts-with(@id,'linkus-licenseactive')]//span[text()='Expand']"));
    public SelenideElement authorizationCode = $(By.id("code"));
    public SelenideElement autthorizationMegBox_ok = $(By.xpath(".//*[starts-with(@id,'messagebox')]//span[text()='OK']"));


    public int time=1500;
    public int extension=17;
    public String code="";

    public  void login_auth(String username,String password) {
        username_autho2.setValue(username);
        password_autho2.setValue(password);
        ver.setValue("Yeastarautoverify");

        login_autho.click();
    }

    public  void update(String sn1,String s2){
        Reporter.infoExec("Manager Center Extension:"+s2);
        Linkus_tree.click();
        activa_tree.click();


        ys_waitingTime(3000);
        sn.setValue(sn1);
        search.click();

        ys_waitingTime(2000);
        update.click();
        extension_auth.setValue(s2);
        screenshot("MC Extension_"+extension);
        generate.click();

        ys_waitingTime(2000);
        String m_code;
        m_code = authorizationCode.getText();
        screenshot("MC authorization_code"+extension);
        copy.click();

        if(m_code.isEmpty()){
            Reporter.infoExec("激活码为空");
            ys_waitingTime(5000);
            screenshot("MC authorization_code01 "+extension);
            update.click();
            screenshot("MC authorization_code02 "+extension);
            extension_auth.setValue(s2);
            screenshot("MC authorization_code03 "+extension);
            generate.click();
            screenshot("MC authorization_code04 "+extension);
            ys_waitingTime(10000);

            m_code = authorizationCode.getText();
            screenshot("MC authorization_code05 "+extension);
            copy.click();
        }
        if(!code.equals(m_code)){
            code = m_code;
        }else{
            Reporter.infoExec("第二次生成...");
            update.click();
            generate.click();
            ys_waitingTime(2000);
            code = authorizationCode.getText();
        }
        Reporter.infoExec("激活码:\n");
        Reporter.infoExec(code);
    }

    public void pbx(){
        pageDeskTop.taskBar_Main.click();
        pageDeskTop.linkusShortcut.click();
        linkus_Client_Setting.click();
        linkus_Client_Setting_expand.click();
        authorizationCode_Input.sendKeys(code);

        authorizationCode_Active.click();
        ys_waitingTime(3000);

//        String msgid = webDriver.findElements(By.xpath(".//*[starts-with(@id,'messagebox-')]")).get(0).getAttribute("id");
        List<WebElement> webEs = webDriver.findElements(By.xpath(".//*[starts-with(@id,'messagebox-')]"));
        if(webEs.size() > 0){
            String msgid=webEs.get(0).getAttribute("id");
            String msgtxtid = msgid+"-msg";
            String msgtxt = webDriver.findElement(By.id(msgtxtid)).getText();

            System.out.println("msgtxt: "+msgtxt);
            Reporter.infoExec("激活提示："+msgtxt);
            YsAssert.assertInclude(msgtxt,"Expansion succeeded.","Number of current loops "+time+" Extension-"+extension);
        }


    }


//    @Test
    public  void sshOp(){

        System.out.println("SSH...Do Rest......");
        Reporter.infoExec(" SSH连接重置linkuscore"); //执行操作
        int creatconnect =ssh.CreatConnect("192.168.2.221","ls@yf","");
        System.out.println("creat connect to ssh ret ="+creatconnect);

//        WriteToSSH("tftp -gr linkuscore 192.168.11.194 -l /ysdisk/linkuscore","#",5000);

//        WriteToSSH("wget -P/ysdisk http://192.168.11.194:1234//linkuscore","#",5000);
//
//        WriteToSSH("chmod 777 /ysdisk/linkuscore","#");

        ssh.WriteToSSH("/ysdisk/linkuscore -o reset -D \"node=linkusgeneral&webusername=admin\"","#",5000);

        ssh.Close();
    }
    @Test
    public void do1()  {

        sshOp();
        while (time > 0){

            initialDriver(BROWSER, "http://192.168.2.47:80/");

            login_auth("admin","123");

            if(extension > 20){
                extension = 6;
                //执行SSH
                sshOp();
            }
            System.out.println("Extension: "+extension);
            Reporter.infoExec(" 当前Extension："+extension);

            update("3691A1062295", String.valueOf(extension));

            close();

            initialDriver(BROWSER,"http://192.168.2.221:80/");

            login(LOGIN_USERNAME,LOGIN_PASSWORD);

            pbx();


            autthorizationMegBox_ok.click();

            close();
            ys_waitingTime(3000);
            extension++;
            time --;
        }
//        Actions action = new Actions(webDriver);
//        action.keyDown(Keys.CONTROL).sendKeys("a").keyUp(Keys.CONTROL).sendKeys(Keys.NULL).perform();
//        System.out.println("DDDDDDDDDDDDDDDDD");
    }


    @Test
    public void asdfadf() {

        initialDriver(BROWSER,"https://www.tapd.cn/68117787/bugtrace/bugs/add?jump_from_shortcut=true&template_id=1168117787001006438");

        webDriver.findElement(By.id("username")).sendKeys("lhr@yeastar.com");
        webDriver.findElement(By.id("password_input")).sendKeys("Linhr6375");
        webDriver.findElement(By.id("tcloud_login_button")).click();

        sleep(5000);
        webDriver.findElement(By.xpath("//a[@class='brick-wrap my-project-list-trigger']")).click();

        webDriver.findElement(By.xpath("//div[@class='dropdown dropdown-quick-add brick']//a[@class='dropdown-toggle']")).click();

        webDriver.findElement(By.xpath("//a[contains(text(),'创建缺陷')]")).click();

        Set<String> winHandels = webDriver.getWindowHandles();
        List<String> it = new ArrayList<String>(winHandels); // 将set集合存入list对象
        webDriver.switchTo().window(it.get(1)); // 切换到弹出的新窗口
        sleep(1000);
        String url=webDriver.getCurrentUrl(); //获取新窗口的url
        System.out.println(url);
        webDriver.switchTo().window(it.get(1)); // 返回至原页面
//        pbx();
//        WebElement select = webDriver.findElement(By.id("BugVersionReport"));
        SelenideElement select = $(By.id("BugIterationId"));
//        System.out.println(select.getAttribute("name"));

        select.selectOption(3);

        ys_waitingTime(5000);
    }
    @BeforeClass
    public void beforeclass() {

    }
    @AfterClass
    public void CaseNam1e() {
        Reporter.infoAfterClass("time:"+time+"\n\n\n"); //执行操作
//        ys_waitingTime(50000);
        System.out.println("time: "+time);
        close();
    }

}
