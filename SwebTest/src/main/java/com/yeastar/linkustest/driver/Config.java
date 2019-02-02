package com.yeastar.linkustest.driver;

import com.yeastar.linkustest.pobject.Calling;
import com.yeastar.linkustest.pobject.Contacts;
import com.yeastar.linkustest.pobject.Dialpad;
import com.yeastar.linkustest.pobject.Login;
import com.yeastar.linkustest.pobject.Me.*;
import com.yeastar.linkustest.pobject.Me.Settings.*;
import com.yeastar.linkustest.tools.pjsip.PjsipApp;
import com.yeastar.swebtest.tools.DateFormate;

public class Config extends DataReader {


	public static int INVALID    = -1;
	public static int IDLE       = 0;
	public static int CALLING    = 1;
	public static int RING       = 2;
	public static int TALKING    = 3;
	public static int HUNGUP     = 4;
	public static int TIMEOUT    = 5;
	public static int rejectCode = 486;

	public static int AD_SUCCESS = 1;
	public static int AD_FAIL    = 0;

	public static String currentPath = System.getProperty("user.dir")+"\\";
//	对象库初始化
	public static Contacts contacts = new Contacts();
	public static Dialpad dialpad = new Dialpad();
	public static PresenceStatus presenceStatus = new PresenceStatus();
	public static Calling calling = new Calling();
	public static Login login = new Login();
	public static Me me = new Me();
	public static Recording recording = new Recording();
	public static Settings settings = new Settings();
	public static Voicemail voicemail = new Voicemail();
	public static PersonalInformation personalInformation = new PersonalInformation();
	public static About about = new About();
	public static ReportProblem reportProblem = new ReportProblem();
	public static AdvancedOptions advancedOptions = new AdvancedOptions();
	public static Conference conference = new Conference();

	public static PjsipApp pjsip = new PjsipApp();
	public static DateFormate dateFormate = new DateFormate();

	public static final String extensionStatusIdle ="extensionStatusIdle";
	public static final String extensionStautsBusy= "extensionStautsBusy";
	public static final String extensionStatusOffline = "extensionStatusOffline";
	public static final String conferenceStatusRinging = "conferenceStatusRinging";
	public static final String conferenceStatusTalking = "conferenceStatusTalking";
	public static final String conferenceStatusUnavail = "conferenceStatusUnavail";
	public static final String historyUnread = "historyUnread";
	public static final String historyReaded = "historyReaded";
	public static final String S0 = "S0";
	public static final String S1 = "S1";
	public static final String S2 = "S2";

	//属性值判断
	public static String attribute_checked = "checked";
	public static String attribute_editable = "focusable";
	public static String attribute_enable = "enabled";
	//等待页面元素值
	public static String waitText_login = "Logging in…";

	//通话时分机状态
	public static String calling_ringing = "Ringing…";
	public static String calling_hold = "Hold";
	//分机当前状态
	public static String presence_available = "Available";
	public static String presence_away = "Away";
	public static String presence_dnd = "DND";
	public static String presence_dnd2 = "Do Not Disturb";
	public static String presence_lunchBreak="Lunch Break";
	public static String presence_businessTrip = "Business Trip";
	public static String presence_busy = "Busy";
	public static String presence_offline = "Offline";

	//CDR分机类型
	public static String cdr_extension = "Extension";
	public static String cdr_externalNumber = "External number";
	public static String cdr_NoAnswer = "No Answer";
	public static String cdr_missCall = "Missed";
	public static String cdr_secs = "secs";


	public static String transer_attended = "Attended";
	public static String transer_blind = "Blind";

	public static String connecting = "正在连接服务器…";
	public static String extensionsTitleInCall = "分机";
	public static String contactsTitleInCall = "通讯录";
	public static String titleAll ="所有";
	public static String titleFavorite = "收藏";
	public static String titleMissed = "未接";
	public static String extensionsDetailTitle = "分机详细信息";
	public static String contactsDetailTitle = "联系人详细信息";
	public static String transferAttended = "咨询转";
	public static String transferBlind = "盲转移";
	public static String conferenceDefaultName = "会议室";
	public static String stillInConferenceStatus = "会议正在进行...";
	public static String defaultRingTimeout = "30";
	public static String inConferenceMuteIcon = "ff18a7d2";
	public static String loginLocal = "local";
	public static String loginExternal = "external";
	public static String loginBoth = "both";
	public static String CloudPBX = "CloudPBX";
	public static String PC = "PC";



}








