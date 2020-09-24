# SWEBTEST

- [SWEBTEST](#swebtest)
- [技术栈](#技术栈)
- [运行环境](#运行环境)
- [目录结构描述](#目录结构描述)
- [用例编写](#用例编写)
  - [用例模式 PageObject](#用例模式-pageobject)
  - [用例组成](#用例组成)
  - [用例注解](#用例注解)
  - [用例步骤](#用例步骤)
    - [步骤描述](#步骤描述)
    - [具体步骤](#具体步骤)
  - [用例验证](#用例验证)
  - [用例执行](#用例执行)
  - [报告查看](#报告查看)
    


# 技术栈
selenium + selenide + testng + +allure + zalenium + docker + maven 

# 运行环境
  1. Code (用例编写与调试)
        http://192.168.3.252:8077/

  2. Test Browser (远端运行浏览器)
        http://192.168.3.252:4444/grid/admin/live?only_active_sessions=true

  3. Record Dashboard (录屏)
        http://192.168.3.252:4444/dashboard/#

  4. CI Integration (CI 集成，S series，P series)
        http://192.168.3.252:8087/jenkins/


# 目录结构描述
```
├── Readme.md                                            
├── src                                     
│   ├── main                              //源码目录                               
│   │     ├── java                        //源码
│   │     └── resources
│   │         ├── allure.properties       //report 配置文件
│   │         ├── config.properties       //main 全局配置文件
│   │         ├── libYsAutoTestPjsua.so   //pjsip so库
│   │         └── libYsAutoTestPjsua.so.2 //pjsip so库  
│   └── test                              //测试目录  
│         ├── com.yeastar
│         │    ├── page                   //page Object  
│         │    ├── testcase               //test case  
│         └── resources                   //测试资源文件  
│             ├── p_language              //P series 文案源
│             ├── data.properties         //测试数据配置文件  
│             ├── log4j2.xml              //log 配置文件  
│             └── config.properties       //测试全局配置，eg：重跑 ,分辨率，Hub，录屏等            
├── suite                                 //xml 用例套件
└── pom.xm                                //配置文件

```
# 用例编写
## 用例模式 PageObject

![po](./.vscode/imgmd/po.png)

## 用例组成
<font color=DarkOrchi size=5>**注解**</font>+
<font color=Cyan size=5>**步骤**</font>+
<font color=#0099ff size=5>**验证**</font>

## 用例注解
|  字段           | 描述                  | 备注                             |
|  ----           | ----                 |----                              |
|Epic             |     敏捷保留字段      |report，Behaviors下级一级目录      |
|Feature          |     敏捷保留字段      |report，Behaviors下级二级目录      |
|Story            |     敏捷保留字段      |report，Behaviors下级三级目录      |
|Description      | 描述                  |
|Severity         |缺陷等级               |
|TmsLink          |关联用例               |
|Issue            |关联缺陷               |
|Test             |用例注解               |必填
|groups           |关键字段               |必填 <br>1.用例等级：P0,P1,P2 <br>2.用例名：test* <br>3.用例所属功能模块：Extension<br>4.用例所属项目：PSeries<br>5.用例所属场景：GSM,SIP


## 用例步骤
### 步骤描述
``` java
step("1:login web client");

assertStep("[VCP显示]");
```
### 具体步骤
``` java
auto.loginPage().login("0", EXTENSION_PASSWORD_NEW);

auto.extensionPage().deleAllExtension().createSipExtension("0",EXTENSION_PASSWORD).
                switchToTab("Linkus Clients").editDataByEditImage("all").editLinksClientsUserType(ExtensionPage.USER_TYPE.Manager).clickSaveAndApply();
       

```

## 用例验证
``` java

// Assert
 Assert.assertTrue(execAsterisk(PJSIP_SHOW_AOR+"1000").contains("Unable to find object 1000"));

//softAssert
softAssert.assertTrue(SSHLinuxUntils.exeCommand(DEVICE_IP_LAN, SHOW_CLI_LOG).contains("vm-duration"),"[Assert,cli确认提示音vm-duration]");
        softAssert.assertAll();

//softAssertPlus
softAssertPlus.assertThat(resultSum_before).extracting("caller", "callee", "status", "details")
                .contains(tuple(IVRListName_0 + vcpCaller, "1010 K [1010]", "Ringing", RECORD_DETAILS.EXTERNAL.getAlias()));
softAssertPlus.assertAll();
```



## 用例执行


## 报告查看


