package com.yeastar.linkustest.pobject;

import org.openqa.selenium.By;

public class Login {

//	登录主界面
    public  By userName = By.id("com.yeastar.linkus:id/login_username_et");
    public  By password = By.id("com.yeastar.linkus:id/login_password_et");
    public  By serverIp = By.id("com.yeastar.linkus:id/login_sn_et");
    public  By configure_server = By.id("com.yeastar.linkus:id/login_configure_server_tv");
    public  By login = By.id("com.yeastar.linkus:id/login_btn");
    public  By feedback = By.id("com.yeastar.linkus:id/tv_bug_feedback");
    public  By freetrial = By.id("com.yeastar.linkus:id/tv_free_trial");
    public  By qrcode = By.id("com.yeastar.linkus:id/login_qrcode_iv");

//	配置服务器界面
    public  By custom_localAddress = By.id("com.yeastar.linkus:id/login_local_address_et");
    public  By custom_localPort = By.id("com.yeastar.linkus:id/login_local_port_et");
    public  By custom_externalAddress = By.id("com.yeastar.linkus:id/login_remote_address_et");
    public  By custom_externalPort = By.id("com.yeastar.linkus:id/login_remote_port_et");
    public  By custom_enable = By.id("com.yeastar.linkus:id/custom_setting_cb");
    public  By custom_confirm = By.id("com.yeastar.linkus:id/ok_tv");

//	二维码界面
    public  By qrAlbum = By.id("com.yeastar.linkus:id/tv_qrCode_album");
    public  By qrBack = By.id("com.yeastar.linkus:id/back");

//	弹框
    public  By txtTitle = By.id("com.yeastar.linkus:id/txt_title");
    public  By txtMessage = By.id("com.yeastar.linkus:id/txt_msg");
    public  By txtOk = By.id("com.yeastar.linkus:id/btn_pos");
    public  By txtCancel = By.id("com.yeastar.linkus:id/btn_neg");
}
