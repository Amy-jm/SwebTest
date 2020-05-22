package com.yeastar.swebtest.driver;

import com.yeastar.swebtest.driver.YSMethod.*;
import com.yeastar.swebtest.pobject.CDRandRecordings.CDRandRecordings;
import com.yeastar.swebtest.pobject.CDRandRecordings.Edit_List_Options.Edit_List_Options;
import com.yeastar.swebtest.pobject.Maintenance.BackupandRestore.Backup_Schedule.Backup_Schedule;
import com.yeastar.swebtest.pobject.Maintenance.BackupandRestore.BackupandRestore;
import com.yeastar.swebtest.pobject.Maintenance.BackupandRestore.Create_New_Backup_File.Create_New_Backup_File;
import com.yeastar.swebtest.pobject.Maintenance.BackupandRestore.Upload_a_Backup_File.Upload_a_Backup_File;
import com.yeastar.swebtest.pobject.Maintenance.Maintenance;
import com.yeastar.swebtest.pobject.Maintenance.OperationLog.OperationLog;
import com.yeastar.swebtest.pobject.Maintenance.Reboot.Reboot;
import com.yeastar.swebtest.pobject.Maintenance.Reset.Reset;
import com.yeastar.swebtest.pobject.Maintenance.SystemLog.SystemLog;
import com.yeastar.swebtest.pobject.Maintenance.Troubleshooting.EthernetCaptureTool.EthernetCaptureTool;
import com.yeastar.swebtest.pobject.Maintenance.Troubleshooting.IPPing.IPPing;
import com.yeastar.swebtest.pobject.Maintenance.Troubleshooting.PortMonitorTool.PortMonitorTool;
import com.yeastar.swebtest.pobject.Maintenance.Troubleshooting.Traceroute.Traceroute;
import com.yeastar.swebtest.pobject.Maintenance.Upgrade.Upgrade;
import com.yeastar.swebtest.pobject.Me.Me;
import com.yeastar.swebtest.pobject.Me.Me_Blacklist_Whitelist.Me_Blacklist.Me_Add_Blacklist.Me_Add_Blacklist;
import com.yeastar.swebtest.pobject.Me.Me_Blacklist_Whitelist.Me_Blacklist.Me_Blacklist;
import com.yeastar.swebtest.pobject.Me.Me_Blacklist_Whitelist.Me_Blacklist.Me_Import_Blacklist.Me_Import_Blacklist;
import com.yeastar.swebtest.pobject.Me.Me_Blacklist_Whitelist.Me_Whitelist.Me_Add_Whitelist.Me_Add_Whitelist;
import com.yeastar.swebtest.pobject.Me.Me_Blacklist_Whitelist.Me_Whitelist.Me_Import_Whitelist.Me_Import_Whitelist;
import com.yeastar.swebtest.pobject.Me.Me_Blacklist_Whitelist.Me_Whitelist.Me_Whitelist;
import com.yeastar.swebtest.pobject.Me.Me_CDRandRecording.Me_CDRandRecording;
import com.yeastar.swebtest.pobject.Me.Me_ExtensionSettings.Me_ExtensionSettings;
import com.yeastar.swebtest.pobject.Me.Me_PasswordSettings.Me_PasswordSettings;
import com.yeastar.swebtest.pobject.Me.Me_RoutePermission.Me_RoutePermission;
import com.yeastar.swebtest.pobject.Me.Me_Voicemail.Me_Voicemail;
import com.yeastar.swebtest.pobject.MySettings;
import com.yeastar.swebtest.pobject.PageDeskTop;
import com.yeastar.swebtest.pobject.PageLogin;
import com.yeastar.swebtest.pobject.PbxMonitor;
import com.yeastar.swebtest.pobject.Settings.EventCenter.EventLog.EventLog;
import com.yeastar.swebtest.pobject.Settings.EventCenter.EventSettings.EventSetting.EventSetting;
import com.yeastar.swebtest.pobject.Settings.EventCenter.EventSettings.NotificationContacts.Add_Contact.Add_Contact;
import com.yeastar.swebtest.pobject.Settings.EventCenter.EventSettings.NotificationContacts.NotificationContacts;
import com.yeastar.swebtest.pobject.Settings.PBX.CallControl.AutoCLIPRoutes.AutoCLIPRoutes;
import com.yeastar.swebtest.pobject.Settings.PBX.CallControl.InboundRoutes.Add_Inbound_Route.Add_Inbound_Route;
import com.yeastar.swebtest.pobject.Settings.PBX.CallControl.InboundRoutes.Import_InboundRoutes.Import_InboundRoutes;
import com.yeastar.swebtest.pobject.Settings.PBX.CallControl.InboundRoutes.InboundRoutes;
import com.yeastar.swebtest.pobject.Settings.PBX.CallControl.OutboundRestriction.Add_Outbound_Restriction.Add_Outbound_Restriction;
import com.yeastar.swebtest.pobject.Settings.PBX.CallControl.OutboundRestriction.OutboundRestriction;
import com.yeastar.swebtest.pobject.Settings.PBX.CallControl.OutboundRoutes.Add_Outbound_Routes.Add_Outbound_Routes;
import com.yeastar.swebtest.pobject.Settings.PBX.CallControl.OutboundRoutes.Import_OutboundRoutes.Import_OutboundRoutes;
import com.yeastar.swebtest.pobject.Settings.PBX.CallControl.OutboundRoutes.OutboundRoutes;
import com.yeastar.swebtest.pobject.Settings.PBX.CallControl.SLA.Add_SLA_Station.Add_SLA_Station;
import com.yeastar.swebtest.pobject.Settings.PBX.CallControl.SLA.SLA;
import com.yeastar.swebtest.pobject.Settings.PBX.CallControl.Time_Conditions.Holiday.Add_Holiday.Add_Holiday;
import com.yeastar.swebtest.pobject.Settings.PBX.CallControl.Time_Conditions.Holiday.Holiday;
import com.yeastar.swebtest.pobject.Settings.PBX.CallControl.Time_Conditions.TimeConditions.Add_Time_Condition.Add_Time_Condition;
import com.yeastar.swebtest.pobject.Settings.PBX.CallControl.Time_Conditions.TimeConditions.TimeConditions;
import com.yeastar.swebtest.pobject.Settings.PBX.CallControl.Time_Conditions.Time_Conditions;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Blacklist_Whitelist.Blacklist.Add_Blacklist.Add_Blacklist;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Blacklist_Whitelist.Blacklist.Blacklist;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Blacklist_Whitelist.Blacklist.Import_Blacklist.Import_Blacklist;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Blacklist_Whitelist.Blacklist_Whitelist;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Blacklist_Whitelist.Whitelist.Add_Whitelist.Add_Whitelist;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Blacklist_Whitelist.Whitelist.Import_Whitelist.Import_Whitelist;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Blacklist_Whitelist.Whitelist.Whitelist;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.CallFeatures;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Callback.Add_Callback.Add_Callback;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Callback.Callback;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Conference.Add_Conference.Add_Conference;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Conference.Conference;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.DISA.Add_DISA.Add_DISA;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.DISA.DISA;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.IVR.Add_IVR_Basic.Add_IVR_Basic;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.IVR.Add_IVR_KeyPressEvent.Add_IVR_KeyPressEvent;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.IVR.IVR;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.PINList.Add_PIN_List.Add_PIN_List;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.PINList.PINList;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Paging_Intercom.Add_Paging_Intercom.Add_Paging_Intercom;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Paging_Intercom.Paging_Intercom;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.PickupGroup.Add_Pickup_Group.Add_Pickup_Group;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.PickupGroup.PickupGroup;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Queue.Add_Queue_Basic.Add_Queue_Basic;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Queue.Add_Queue_CallerExperienceSettings.Add_Queue_CallerExperienceSettings;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.Queue.Queue;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.RingGroup.Add_Ring_Group.Add_Ring_Group;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.RingGroup.RingGroup;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.SMS.SMS;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.SpeedDial.Add_Speed_Dial.Add_Speed_Dial;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.SpeedDial.Import_Speed_Dial_Number.Import_Speed_Dial_Number;
import com.yeastar.swebtest.pobject.Settings.PBX.CallFeatures.SpeedDial.SpeedDial;
import com.yeastar.swebtest.pobject.Settings.PBX.EmergencyNumber.Add_Emergency_Number.Add_Emergency_Number;
import com.yeastar.swebtest.pobject.Settings.PBX.EmergencyNumber.EmergencyNumber;
import com.yeastar.swebtest.pobject.Settings.PBX.Extensions.ExtensionGroup.Add_Extension_Group.Add_Extension_Group;
import com.yeastar.swebtest.pobject.Settings.PBX.Extensions.ExtensionGroup.ExtensionGroup;
import com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.Add_Bulk_Extensions.Add_Bulk_Extensions_Advanced.Add_Bulk_Extensions_Advanced;
import com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.Add_Bulk_Extensions.Add_Bulk_Extensions_Basic.Add_Bulk_Extensions_Basic;
import com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.Add_Bulk_Extensions.Add_Bulk_Extensions_CallPermission.Add_Bulk_Extensions_CallPermission;
import com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.Add_Bulk_Extensions.Add_Bulk_Extensions_Features.Add_Bulk_Extensions_Features;
import com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.Add_Extension.Add_Extension_Advanced.Add_Extension_Advanced;
import com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.Add_Extension.Add_Extension_Basic.Add_Extension_Basic;
import com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.Add_Extension.Add_Extension_CallPermission.Add_Extension_CallPermission;
import com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.Add_Extension.Add_Extension_Features.Add_Extension_Features;
import com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.Edit_Selected_Extensions.Edit_Selected_Extensions_Advanced.Edit_Selected_Extensions_Advanced;
import com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.Edit_Selected_Extensions.Edit_Selected_Extensions_Basic.Edit_Selected_Extensions_Basic;
import com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.Edit_Selected_Extensions.Edit_Selected_Extensions_CallPermission.Edit_Selected_Extensions_CallPermission;
import com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.Edit_Selected_Extensions.Edit_Selected_Extensions_Features.Edit_Selected_Extensions_Features;
import com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.Extensions;
import com.yeastar.swebtest.pobject.Settings.PBX.Extensions.Extensions.Import_Extension.Import_Extension;
import com.yeastar.swebtest.pobject.Settings.PBX.General.FeatureCode.FeatureCode;
import com.yeastar.swebtest.pobject.Settings.PBX.General.IAX.IAX;
import com.yeastar.swebtest.pobject.Settings.PBX.General.Preferences.Preferences;
import com.yeastar.swebtest.pobject.Settings.PBX.General.SIP.Advanced.Advanced;
import com.yeastar.swebtest.pobject.Settings.PBX.General.SIP.Codec.Codec;
import com.yeastar.swebtest.pobject.Settings.PBX.General.SIP.General.General;
import com.yeastar.swebtest.pobject.Settings.PBX.General.SIP.JitterBuffer.JitterBuffer;
import com.yeastar.swebtest.pobject.Settings.PBX.General.SIP.NAT.NAT;
import com.yeastar.swebtest.pobject.Settings.PBX.General.SIP.QoS.QoS;
import com.yeastar.swebtest.pobject.Settings.PBX.General.SIP.SIP;
import com.yeastar.swebtest.pobject.Settings.PBX.General.SIP.SessionTimer.SessionTimer;
import com.yeastar.swebtest.pobject.Settings.PBX.General.SIP.TLS.TLS;
import com.yeastar.swebtest.pobject.Settings.PBX.General.SIP.T_38.T_38;
import com.yeastar.swebtest.pobject.Settings.PBX.General.Voicemail.Voicemail;
import com.yeastar.swebtest.pobject.Settings.PBX.MultisiteInterconnect.BranchOffice.BranchOffice_AdaptCallerID.BranchOffice_AdaptCallerID;
import com.yeastar.swebtest.pobject.Settings.PBX.MultisiteInterconnect.BranchOffice.BranchOffice_Advanced.BranchOffice_Advanced;
import com.yeastar.swebtest.pobject.Settings.PBX.MultisiteInterconnect.BranchOffice.BranchOffice_Basic.Add_Branch_Office.Add_Branch_Office;
import com.yeastar.swebtest.pobject.Settings.PBX.MultisiteInterconnect.BranchOffice.BranchOffice_Basic.BranchOffice_Basic;
import com.yeastar.swebtest.pobject.Settings.PBX.MultisiteInterconnect.Headquerter.Headquarter_Basic.Add_HeadquerterBranch_Office.Add_HeadquerterBranch_Office;
import com.yeastar.swebtest.pobject.Settings.PBX.MultisiteInterconnect.Headquerter.Headquarter_Basic.Headquerter_Basic;
import com.yeastar.swebtest.pobject.Settings.PBX.MultisiteInterconnect.Headquerter.Headquerter_AdaptCallerID.Headquerter_AdaptCallerID;
import com.yeastar.swebtest.pobject.Settings.PBX.MultisiteInterconnect.Headquerter.Headquerter_Advanced.Headquerter_Advanced;
import com.yeastar.swebtest.pobject.Settings.PBX.Recording.Recording;
import com.yeastar.swebtest.pobject.Settings.PBX.Trunks.Add_VoIP_Trunk.Add_VoIP_Trunk_AdaptCallerID.Add_VoIP_Trunk_AdaptCallerID;
import com.yeastar.swebtest.pobject.Settings.PBX.Trunks.Add_VoIP_Trunk.Add_VoIP_Trunk_Advanced.Add_VoIP_Trunk_Advanced;
import com.yeastar.swebtest.pobject.Settings.PBX.Trunks.Add_VoIP_Trunk.Add_VoIP_Trunk_Basic.Add_VoIP_Trunk_Basic;
import com.yeastar.swebtest.pobject.Settings.PBX.Trunks.Add_VoIP_Trunk.Add_VoIP_Trunk_Codec.Add_VoIP_Trunk_Codec;
import com.yeastar.swebtest.pobject.Settings.PBX.Trunks.Add_VoIP_Trunk.Add_VoIP_Trunk_DOD.Add_VoIP_Trunk.Add_VoIP_Trunk;
import com.yeastar.swebtest.pobject.Settings.PBX.Trunks.Add_VoIP_Trunk.Add_VoIP_Trunk_DOD.Add_VoIP_Trunk_DOD;
import com.yeastar.swebtest.pobject.Settings.PBX.Trunks.Add_VoIP_Trunk.Edit_Bri_Trunk.Edit_Bri_Trunk;
import com.yeastar.swebtest.pobject.Settings.PBX.Trunks.Import_Trunks.Import_trunks;
import com.yeastar.swebtest.pobject.Settings.PBX.Trunks.Trunks;
import com.yeastar.swebtest.pobject.Settings.PBX.VoicePrompts.CustomPrompts.CustomPrompts;
import com.yeastar.swebtest.pobject.Settings.PBX.VoicePrompts.CustomPrompts.Record_New_Prompt.Record_New_Prompt;
import com.yeastar.swebtest.pobject.Settings.PBX.VoicePrompts.CustomPrompts.Upload_a_Prompt.Upload_a_Prompt;
import com.yeastar.swebtest.pobject.Settings.PBX.VoicePrompts.MusicOnHold.Add_MOH_Playlist.Add_MOH_Playlist;
import com.yeastar.swebtest.pobject.Settings.PBX.VoicePrompts.MusicOnHold.MusicOnHold;
import com.yeastar.swebtest.pobject.Settings.PBX.VoicePrompts.PromptPreference.PromptPreference;
import com.yeastar.swebtest.pobject.Settings.PBX.VoicePrompts.SystemPrompt.SystemPrompt;
import com.yeastar.swebtest.pobject.Settings.Settings;
import com.yeastar.swebtest.pobject.Settings.System.DateTime.DateTime;
import com.yeastar.swebtest.pobject.Settings.System.Email.Email;
import com.yeastar.swebtest.pobject.Settings.System.HotStandby.HotStandby;
import com.yeastar.swebtest.pobject.Settings.System.Network.BasicSettings.BasicSettings;
import com.yeastar.swebtest.pobject.Settings.System.Network.CellularNetwork.CellularNetwork;
import com.yeastar.swebtest.pobject.Settings.System.Network.DDNSSettings.DDNSSettings;
import com.yeastar.swebtest.pobject.Settings.System.Network.ICMPDetection.ICMPDetection;
import com.yeastar.swebtest.pobject.Settings.System.Network.OpenVPN.OpenVPN;
import com.yeastar.swebtest.pobject.Settings.System.Network.StaticRoutes.RoutingTable.RoutingTable;
import com.yeastar.swebtest.pobject.Settings.System.Network.StaticRoutes.StaticRoute.StaticRoute;
import com.yeastar.swebtest.pobject.Settings.System.Network.StaticRoutes.StaticRoutes;
import com.yeastar.swebtest.pobject.Settings.System.Security.Certificate.Certificate;
import com.yeastar.swebtest.pobject.Settings.System.Security.DatabaseGrant.DatabaseGrant;
import com.yeastar.swebtest.pobject.Settings.System.Security.FirewallRules.FirewallRules;
import com.yeastar.swebtest.pobject.Settings.System.Security.IPAutoDefense.IPAutoDefense;
import com.yeastar.swebtest.pobject.Settings.System.Security.Service.Service;
import com.yeastar.swebtest.pobject.Settings.System.Storage.AutoCleanup.AutoCleanup;
import com.yeastar.swebtest.pobject.Settings.System.Storage.FileShare.FileShare;
import com.yeastar.swebtest.pobject.Settings.System.Storage.Preference.Preference;
import com.yeastar.swebtest.pobject.Settings.System.UserPermission.Grant_Privilege_Application.Grant_Privilege_Application;
import com.yeastar.swebtest.pobject.Settings.System.UserPermission.Grant_Privilege_CDRandRecordings.Grant_Privilege_CDRandRecordings;
import com.yeastar.swebtest.pobject.Settings.System.UserPermission.Grant_Privilege_Monitor.Grant_Privilege_Monitor;
import com.yeastar.swebtest.pobject.Settings.System.UserPermission.Grant_Privilege_Others.Grant_Privilege_Others;
import com.yeastar.swebtest.pobject.Settings.System.UserPermission.Grant_Privilege_Settings.Grant_Privilege_Settings;
import com.yeastar.swebtest.pobject.Settings.System.UserPermission.UserPermission;
import com.yeastar.swebtest.tools.TcpSocket.TcpSocket;
import com.yeastar.swebtest.tools.pjsip.PjsipApp;
import com.yeastar.swebtest.tools.ssh.SSHApp;


import java.io.File;
import java.util.SimpleTimeZone;

/**
 * Created by GaGa on 2017-05-19.
 */
public class ConfigP extends DataReader2 {

    public static long TEST_TIMEOUT = 3000;  //测试过程的时间差
    public static long FINDELEMENT_TIMEOUT = 10000;  //元素查找的时间差
//    public static String CHROME = "chrome";
//    public static String FIREFOX = "firefox";
//    public static String IE = "ie";


    public static String S20 = "S20";
    public static String S50 = "S50";
    public static String S100 ="S100";
    public static String S300 ="S300";
    public static String S20P ="S20P";
    public static String S412 = "S412";
    public static String PC = "PC";
    public static String CLOUD_PBX= "CloudPBX";
    public static String EXTENSION_PASSWORD = "Yeastar202Yeastar202";
    public static String currentPath = System.getProperty("user.dir")+ File.separator;

    public static boolean Single_Init = true;
    public static boolean Single_Device_Test = false;
//    public static boolean Single_Device_Test = true;
//    public static boolean Single_Init = false;


    //通讯类型定义
    public static String communication_outRoute = "Outbound";
    public static String communication_inbound = "Inbound";
    public static String communication_internal = "Internal";
    public static String communication_callback = "Callback";
    public static String communication_transfer = "Transfer";
    public static String communication_warning = "Warning";

    //升序降序定义
    public static int sort_ascendingOrder=0;
    public static int sort_descendingOrder=1;

    public static int REGIST_EXTENSION_PWD_INIT = 0;
    public static int REGIST_EXTENSION_PWD_ROM = 1;
    public static int REGIST_EXTENSION_PWD_FIX= 2;
    public static int REGIST_EXTENSION_PWD_FIXEXTS= 3;

//    下拉框设置选择的属性值
    public static String extensionList = "username";//"fullname"
    public static String nameList = "name";
    public static String trunkList = "trunkname";
    public static String value ="value";

    public static int INVALID = -1;
    public static int IDLE = 0;
    public static int CALLING = 1;
    public static int RING = 2;
    public static int TALKING = 3;
    public static int HUNGUP = 4;
    public static int TIMEOUT = 5;


    public String s_extensin = "e";
    public String s_extension_range ="E";
    public String s_hangup = "h";
    public String s_voicemail = "v";
    public String s_ringGroup = "r";
    public String s_queue = "q";
    public String s_conference = "c";
    public String s_disa = "d";
    public String s_callback = "C";
    public String s_outboundRoute ="o";
    public String s_faxToMail = "F";
    public String s_ivr = "i";
    public String s_selectanOption ="u";
    public String s_dialByName = "D";
    public String s_customPrompt = "p";
    /**
     * 初始化对象库的对象
     */
    public static PageLogin pageLogin = new PageLogin();
    public static PageDeskTop pageDeskTop = new PageDeskTop();
    public static Settings settings = new Settings();
    public static MySettings mySettings = new MySettings();
    public static PbxMonitor pbxMonitor = new PbxMonitor();


    /**
     * Extension  分机页面对象库初始化
     */
    public static Extensions extensions = new Extensions();

    //Add_Bulk_Extensions
    public static Add_Bulk_Extensions_Advanced addBulkExtensionsAdvanced = new Add_Bulk_Extensions_Advanced();
    public static Add_Bulk_Extensions_Basic addBulkExtensionsBasic = new Add_Bulk_Extensions_Basic();
    public static Add_Bulk_Extensions_CallPermission addBulkExtensionsCallPermission = new Add_Bulk_Extensions_CallPermission();
    public static Add_Bulk_Extensions_Features addBulkExtensionsFeatures = new Add_Bulk_Extensions_Features();
    //Add_Extension
    public static Add_Extension_Basic addExtensionBasic = new Add_Extension_Basic();
    public static Add_Extension_CallPermission addExtensionCallPermission = new Add_Extension_CallPermission();
    public static Add_Extension_Advanced addExtensionAdvanced = new Add_Extension_Advanced();
    public static Add_Extension_Features addExtensionFeatures = new Add_Extension_Features();
    //Edit_Selected_Extensions
    public static Edit_Selected_Extensions_Advanced editSelectedExtensionsAdvanced = new Edit_Selected_Extensions_Advanced();
    public static Edit_Selected_Extensions_Basic editSelectedExtensionsBasic = new Edit_Selected_Extensions_Basic();
    public static Edit_Selected_Extensions_CallPermission editSelectedExtensionsCallPermission = new Edit_Selected_Extensions_CallPermission();
    public static Edit_Selected_Extensions_Features editSelectedExtensionsFeatures = new Edit_Selected_Extensions_Features();
    //Import_Extension
    public static Import_Extension importExtension = new Import_Extension();

    //ExtensionGroup
    public static ExtensionGroup extensionGroup = new ExtensionGroup();
    public static Add_Extension_Group add_extension_group = new Add_Extension_Group();




    /**
     * trunk 初始化
     */
    public static Trunks trunks = new Trunks();
    public static Add_VoIP_Trunk add_voIP_trunk = new Add_VoIP_Trunk();
    public static Add_VoIP_Trunk_AdaptCallerID add_voIP_trunk_adaptCallerID = new Add_VoIP_Trunk_AdaptCallerID();
    public static Add_VoIP_Trunk_Advanced add_voIP_trunk_advanced = new Add_VoIP_Trunk_Advanced();
    public static Add_VoIP_Trunk_DOD add_voIP_trunk_dod = new Add_VoIP_Trunk_DOD();
    public static Add_VoIP_Trunk_Basic add_voIP_trunk_basic = new Add_VoIP_Trunk_Basic();
    public static Add_VoIP_Trunk_Codec add_voIP_trunk_codec = new Add_VoIP_Trunk_Codec();
    public static Edit_Bri_Trunk edit_bri_trunk = new Edit_Bri_Trunk();
    public static Import_trunks import_trunks = new Import_trunks();
    /**
     * CallFeature
     */
    public static CallFeatures callFeatures = new CallFeatures();
    public static Blacklist_Whitelist blacklist_whitelist = new Blacklist_Whitelist();
    public static Add_Blacklist add_blacklist = new Add_Blacklist();
    public static Import_Blacklist import_blacklist = new Import_Blacklist();
    public static Blacklist blacklist = new Blacklist();
    public static Whitelist whitelist = new Whitelist();
    public static Add_Whitelist add_whitelist = new Add_Whitelist();
    public static Import_Whitelist import_whitelist = new Import_Whitelist();
    public static RingGroup ringGroup = new RingGroup();
    public static Add_Ring_Group add_ring_group = new Add_Ring_Group();
    public static Queue queue = new Queue();
    public static Add_Queue_Basic add_queue_basic = new Add_Queue_Basic();
    public static Add_Queue_CallerExperienceSettings add_queue_callerExperienceSettings = new Add_Queue_CallerExperienceSettings();
    public static Conference conference = new Conference();
    public static Add_Conference add_conference = new Add_Conference();
    public static PickupGroup pickupGroup = new PickupGroup();
    public static Add_Pickup_Group add_pickup_group = new Add_Pickup_Group();
    public static Paging_Intercom paging_intercom = new Paging_Intercom();
    public static Add_Paging_Intercom add_paging_intercom = new Add_Paging_Intercom();
    public static Callback callback =new Callback();
    public static Add_Callback add_callback = new Add_Callback();
    public static DISA disa = new DISA();
    public static Add_DISA add_disa = new Add_DISA();
    public static PINList pinList = new PINList();
    public static Add_PIN_List add_pin_list = new Add_PIN_List();
    public static IVR ivr = new IVR();
    public static Add_IVR_Basic add_ivr_basic = new Add_IVR_Basic();
    public static Add_IVR_KeyPressEvent add_ivr_keyPressEvent = new Add_IVR_KeyPressEvent();
    public static SpeedDial speedDial = new SpeedDial();
    public static Add_Speed_Dial add_speed_dial = new Add_Speed_Dial();
    public static Import_Speed_Dial_Number import_speed_dial_number = new Import_Speed_Dial_Number();
    public static SMS sms = new SMS();

    /**
     * Call Control
     */
    public static YS_CallControl m_callcontrol = new YS_CallControl();
    public static AutoCLIPRoutes autoCLIPRoutes = new AutoCLIPRoutes();
    public static InboundRoutes inboundRoutes = new InboundRoutes();
    public static Add_Inbound_Route add_inbound_route = new Add_Inbound_Route();
    public static OutboundRestriction outboundRestriction = new OutboundRestriction();
    public static Add_Outbound_Restriction add_outbound_restriction = new Add_Outbound_Restriction();
    public static OutboundRoutes outboundRoutes = new OutboundRoutes();
    public static Add_Outbound_Routes add_outbound_routes = new Add_Outbound_Routes();
    public static SLA sla = new SLA();
    public static Add_SLA_Station add_sla_station = new Add_SLA_Station();
    public static Time_Conditions time_Conditions = new Time_Conditions();
    public static Holiday holiday = new Holiday();
    public static Add_Holiday add_holiday = new Add_Holiday();
    public static TimeConditions timeConditions = new TimeConditions();
    public static Add_Time_Condition add_time_condition =new Add_Time_Condition();
    public static Import_InboundRoutes import_inboundRoutes = new Import_InboundRoutes();
    public static Import_OutboundRoutes import_outboundRoutes = new Import_OutboundRoutes();

    /**
     * Recording
     */
    public static Recording recording = new Recording();

    /**
     * Voice Prompts
     */
    public static YS_VoicePrompts m_voicePrompts = new YS_VoicePrompts();
    public static Record_New_Prompt record_new_prompt = new Record_New_Prompt();
    public static Upload_a_Prompt upload_a_prompt = new Upload_a_Prompt();
    public static CustomPrompts customPrompts = new CustomPrompts();
    public static MusicOnHold musicOnHold = new MusicOnHold();
    public static Add_MOH_Playlist add_moh_playlist = new Add_MOH_Playlist();
    public static PromptPreference promptPreference = new PromptPreference();
    public static SystemPrompt systemPrompt = new SystemPrompt();

    /**
     * General
     */

    public static FeatureCode featureCode = new FeatureCode();
    public static IAX iax = new IAX();
    public static Preferences preferences = new Preferences();
    public static SIP sip = new SIP();
    public static Advanced advanced = new Advanced();
    public static Codec codec = new Codec();
    public static General general = new General();
    public static JitterBuffer jitterBuffer = new JitterBuffer();
    public static NAT nat = new NAT();
    public static QoS qoS = new QoS();
    public static SessionTimer sessionTimer = new SessionTimer();
    public static T_38 t_38 = new T_38();
    public static TLS tls    = new TLS();
    public static Voicemail voicemail = new Voicemail();


    /**
     * Emergency Number
     */
    public static YS_EmergencyNumber m_emergencyNumber = new YS_EmergencyNumber();
    public static EmergencyNumber emergencyNumber = new EmergencyNumber();
    public static Add_Emergency_Number add_emergency_number = new Add_Emergency_Number();

    /**
     * CDR
     */
    public static CDRandRecordings cdRandRecordings = new CDRandRecordings();
    public static Edit_List_Options edit_list_options = new Edit_List_Options();


    /**
     * DateTime
     */
    public static DateTime dateTime = new DateTime();
    /**
     * email setting
     */
    public static Email email = new Email();
    /**
     *Security
     */
    public static Certificate certificate = new Certificate();
    public static DatabaseGrant databaseGrant = new DatabaseGrant();
    public static FirewallRules firewallRules = new FirewallRules();
    public static IPAutoDefense ipAutoDefense = new IPAutoDefense();
    public static Service service = new Service();
    /**
     * HotStandby  双击热备
     */
    public static HotStandby hotStandby = new HotStandby();
    /**
     * Storage   CallFeatures
     */
    public static YS_Storage m_storage = new YS_Storage();
    public static Preference preference = new Preference();
    public static FileShare fileShare = new FileShare();
    public static AutoCleanup autoCleanup = new AutoCleanup();
    /**
     * UserPermission
     */
    public static UserPermission userPermission = new UserPermission();
    public static Grant_Privilege_Application grant_privilege_application = new  Grant_Privilege_Application();
    public static Grant_Privilege_CDRandRecordings grant_privilege_cdRandRecordings = new Grant_Privilege_CDRandRecordings();
    public static Grant_Privilege_Monitor grant_privilege_monitor = new Grant_Privilege_Monitor();
    public static Grant_Privilege_Others grant_privilege_others = new Grant_Privilege_Others();
    public static Grant_Privilege_Settings grant_privilege_settings = new Grant_Privilege_Settings();
    /**
     * Network
     */
    public static BasicSettings basicSettings = new BasicSettings();
    public static CellularNetwork cellularNetwork = new CellularNetwork();
    public static DDNSSettings ddnsSettings = new DDNSSettings();
    public static ICMPDetection icmpDetection = new ICMPDetection();
    public static OpenVPN openVPN = new OpenVPN();
    public static StaticRoutes staticRoutes = new StaticRoutes();
    public static RoutingTable routingTable = new RoutingTable();
    public static StaticRoute staticRoute = new StaticRoute();

    /**
     * EventCenter
     */
    public static EventLog eventLog = new EventLog();
    /**
     * EventSettings
     */
    public static EventSetting eventSetting = new EventSetting();
    public static Add_Contact add_contact = new Add_Contact();
    public static NotificationContacts notificationContacts =new NotificationContacts();

    /**
     * PJSIP  SSH  TCPSocket初始化
     */
    public static PjsipApp pjsip = new PjsipApp();
    public static SSHApp ssh = new SSHApp();
    public static TcpSocket tcpSocket = new TcpSocket();
    /**
     * log的配置
     */
    //public static String logFile = "config/log4j.properties";

    /**
     * Maintenance 初始化
     */
    public static BackupandRestore backupandRestore = new BackupandRestore();
    public static Backup_Schedule backup_schedule = new Backup_Schedule();
    public static Create_New_Backup_File create_new_backup_file = new Create_New_Backup_File();
    public static Upload_a_Backup_File upload_a_backup_file = new Upload_a_Backup_File();
    public static OperationLog  operationLog = new OperationLog();
    public static Upgrade upgrade = new Upgrade();
    public static Reboot reboot = new Reboot();
    public static Reset reset = new Reset();
    public static SystemLog systemLog = new SystemLog();
    public static EthernetCaptureTool ethernetCaptureTool = new EthernetCaptureTool();
    public static IPPing ipPing = new IPPing();
    public static PortMonitorTool portMonitorTool = new PortMonitorTool();
    public static Traceroute traceroute = new Traceroute();
    public static Maintenance maintenance = new Maintenance();

    /**
     * Me  分机登录
     */
    public static Me me = new Me();
    public static Me_Add_Blacklist me_add_blacklist = new Me_Add_Blacklist();
    public static Me_Import_Blacklist me_import_blacklist = new Me_Import_Blacklist();
    public static Me_Blacklist me_blacklist = new Me_Blacklist();
    public static Me_Add_Whitelist me_add_whitelist = new Me_Add_Whitelist();
    public static Me_Import_Whitelist me_import_whitelist = new Me_Import_Whitelist();
    public static Me_Whitelist me_whitelist = new Me_Whitelist();
    public static Me_CDRandRecording me_cdRandRecording = new Me_CDRandRecording();
    public static Me_ExtensionSettings me_extensionSettings = new Me_ExtensionSettings();
    public static Me_PasswordSettings me_passwordSettings = new Me_PasswordSettings();
    public static Me_Voicemail me_voicemail = new Me_Voicemail();
    public static Me_RoutePermission me_routePermission = new Me_RoutePermission();

    /**
     * Multisite Interconnect
     */
    public static BranchOffice_AdaptCallerID branchOffice_adaptCallerID = new BranchOffice_AdaptCallerID();
    public static BranchOffice_Advanced branchOffice_advanced = new BranchOffice_Advanced();
    public static Add_Branch_Office add_branch_office = new Add_Branch_Office();
    public static BranchOffice_Basic branchOffice_basic = new BranchOffice_Basic();
    public static Add_HeadquerterBranch_Office add_headquerterBranch_office = new Add_HeadquerterBranch_Office();
    public static Headquerter_Basic headquerter_basic = new Headquerter_Basic();
    public static Headquerter_AdaptCallerID headquerter_adaptCallerID = new Headquerter_AdaptCallerID();
    public static Headquerter_Advanced headquerter_advanced = new Headquerter_Advanced();

    /**
    * 方法初始化
    * */
    public static YS_Extension m_extension = new YS_Extension();
    public static YS_Trunk m_trunks = new YS_Trunk();
    public static YS_CallFeature m_callFeature = new YS_CallFeature();
    public static YS_Me m_me = new YS_Me();
    public static Ys_UserPermission m_userPermission = new Ys_UserPermission();
    public static YS_EventCenter m_eventCenter = new YS_EventCenter();
    public static YS_General m_general = new YS_General();
}
