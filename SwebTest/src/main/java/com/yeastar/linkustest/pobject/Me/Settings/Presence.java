package com.yeastar.linkustest.pobject.Me.Settings;

import org.openqa.selenium.By;

public class Presence {

    public static By back = By.id("com.yeastar.linkus:id/actionbar_left_iv");
    public static By ok = By.id("com.yeastar.linkus:id/actionbar_right_iv");


    //Available
    public static By available = By.xpath("//*[@resource-id='com.yeastar.linkus:id/presence_lv']/android.widget.LinearLayout[1]");
    //离开
    public static By away = By.xpath("//*[@resource-id='com.yeastar.linkus:id/presence_lv']/android.widget.LinearLayout[2]");
    //午饭
    public static By lunchBreak = By.xpath("//*[@resource-id='com.yeastar.linkus:id/presence_lv']/android.widget.LinearLayout[3]");
    //出差
    public static By businessTrip = By.xpath("//*[@resource-id='com.yeastar.linkus:id/presence_lv']/android.widget.LinearLayout[4]");



    public static By presenceInfo = By.id("com.yeastar.linkus:id/et_message_status");
    public static By aways = By.id("com.yeastar.linkus:id/cb_always");
    public static By awaysVoicemail = By.id("com.yeastar.linkus:id/ll_always_voice_mail");
    public static By awaysExtension = By.id("com.yeastar.linkus:id/tv_aways_extension");
    public static By awaysRingGroup = By.id("com.yeastar.linkus:id/tv_aways_ring_group");
    public static By awaysQueue = By.id("com.yeastar.linkus:id/tv_aways_queue");
    public static By awaysIvr = By.id("com.yeastar.linkus:id/tv_aways_ivr");
    public static By awaysuserMobile = By.id("com.yeastar.linkus:id/tv_aways_user_mobile");
    public static By awayscustomNumber = By.id("com.yeastar.linkus:id/tv_aways_custom_number");
    public static By awayshangup = By.id("com.yeastar.linkus:id/ll_aways_hangup");

    public static By noAnswer = By.id("com.yeastar.linkus:id/cb_no_answer");
    public static By noAnswerVoicemail = By.id("com.yeastar.linkus:id/ll_no_answer_voice_mail");
    public static By noAnswerExtension = By.id("com.yeastar.linkus:id/tv_no_answer_extension");
    public static By noAnswerRingGroup = By.id("com.yeastar.linkus:id/tv_no_answer_ring_group");
    public static By noAnswerQueue = By.id("com.yeastar.linkus:id/tv_no_answer_queue");
    public static By noAnswerIvr = By.id("com.yeastar.linkus:id/tv_no_answer_ivr");
    public static By noAnsweruserMobile = By.id("com.yeastar.linkus:id/tv_no_answer_user_mobile");
    public static By noAnswercustomNumber = By.id("com.yeastar.linkus:id/tv_no_answer_custom_number");
    public static By noAnswerhangup = By.id("com.yeastar.linkus:id/ll_no_answer_hangup");

    public static By whenBusy = By.id("com.yeastar.linkus:id/cb_when_busy");
    public static By whenBusyVoicemail = By.id("com.yeastar.linkus:id/ll_when_busy_voice_mail");
    public static By whenBusyExtension = By.id("com.yeastar.linkus:id/tv_when_busy_extension");
    public static By whenBusyRingGroup = By.id("com.yeastar.linkus:id/tv_when_busy_ring_group");
    public static By whenBusyQueue = By.id("com.yeastar.linkus:id/tv_when_busy_queue");
    public static By whenBusyIvr = By.id("com.yeastar.linkus:id/tv_when_busy_ivr");
    public static By whenBusyuserMobile = By.id("com.yeastar.linkus:id/tv_when_busy_user_mobile");
    public static By whenBusycustomNumber = By.id("com.yeastar.linkus:id/tv_when_busy_custom_number");
    public static By whenBusyhangup = By.id("com.yeastar.linkus:id/ll_when_busy_hangup");


    //先响铃
    public static By ringFirstPc = By.id("com.yeastar.linkus:id/pc_client_container");
    public static By ringFirstMobile = By.id("com.yeastar.linkus:id/mobile_container");
    public static By ringFirstPhone = By.id("com.yeastar.linkus:id/extension_container");
    //选中时的勾
    public static By ringFirstPcRb = By.id("com.yeastar.linkus:id/rb_pc_client");
    public static By ringFirstMobileRb = By.id("com.yeastar.linkus:id/rb_mobile_client");
    public static By ringFirstPhoneRb = By.id("com.yeastar.linkus:id/rb_extension");
    //后响铃
    public static By ringSecondlyPc =By.id("com.yeastar.linkus:id/pc_client_container_");
    public static By ringSecondlyMobile=By.id("com.yeastar.linkus:id/mobile_container_");
    public static By ringSecondlyPhone=By.id("com.yeastar.linkus:id/extension_container_");
    //选中时的勾
    public static By ringSecondlyPcRb = By.id("com.yeastar.linkus:id/rb_pc_client_");
    public static By ringSecondlyMobileRb = By.id("com.yeastar.linkus:id/rb_mobile_client_");
    public static By ringSecondlyPhoneRb = By.id("com.yeastar.linkus:id/rb_extension_");
}
