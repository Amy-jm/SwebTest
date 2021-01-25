package com.yeastar.swebtest.tools.pjsip;

import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import io.qameta.allure.Step;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.yeastar.swebtest.driver.Config.*;
import static com.yeastar.swebtest.driver.SwebDriver.gridExtensonStatus;
import static com.yeastar.swebtest.driver.SwebDriver.ys_waitingTime;

/**
 * Created by Yeastar on 2017/6/7.
 */
@Log4j2
public class PjsipApp extends PjsipDll {

    final static int PJSIP_INV_STATE_NULL = 0;
    /**
     * < Before INVITE is sent or received  0
     */
    final static int PJSIP_INV_STATE_CALLING = 1;
    /**
     * < After INVITE is sent		    1
     */
    final static int PJSIP_INV_STATE_INCOMING = 2;
    /**
     * < After INVITE is received.	    2
     */
    final static int PJSIP_INV_STATE_EARLY = 3;
    /**
     * < After response with To tag.	    3
     */
    final static int PJSIP_INV_STATE_CONNECTING = 4;
    /**
     * < After 2xx is sent/received.	4
     */
    final static int PJSIP_INV_STATE_CONFIRMED = 5;
    /**
     * < After ACK is sent/received.	5
     */
    final static int PJSIP_INV_STATE_DISCONNECTED = 6;
    /**
     * < Session is terminated.		    6
     */

    private static List<UserAccount> accounts;
    //======================================Call back==============================//
    //注册回调
    public pjsipdll.RegisterCallBack registerCallBack = new pjsipdll.RegisterCallBack() {
        @Override
        public int fptr_regstate(int id, int registerCode) {
            try {
                log.debug("RegisterCallBack :" + id + " code:" + registerCode);
                if (registerCode == 200) {
                    for (int i = 0; i < accounts.size(); i++) {
                        if (accounts.get(i).accId == id)
                            accounts.get(i).status = IDLE;
                    }
                } else {
                    for (int i = 0; i < accounts.size(); i++) {
                        if (accounts.get(i).accId == id)
                            accounts.get(i).status = INVALID;
                    }
                    switch (registerCode) {
                        case 503://服务器未相应
                            break;
                        case 408://超时
                            break;
                        //....
                    }
                }
            } catch (Throwable ex) {
                log.error("【PjsipException fptr_regstate】" + ex);
            }
            return 0;
        }
    };
    //拨号回调
    public pjsipdll.IncomingCallBack incomingcallback = new pjsipdll.IncomingCallBack() {
        @Override
        /**
         * number  对方送来的callid（来显？）
         */
        public int fptr_callincoming(int id, String number, int accid) {
            log.debug("incomingcallback " + number + "callid: " + id + "accid: " + accid + "accounts.size(): " + accounts.size());

            for (int i = 0; i < accounts.size(); i++) {
                if (accounts.get(i).accId == accid) {
                    accounts.get(i).callId = id;
                    accounts.get(i).status = RING;
                    accounts.get(i).callerId = number.substring(number.indexOf("\"")+1, number.indexOf("\"",2));

                }
            }
            return 0;
        }
    };
    //通话回调
    public pjsipdll.CallstateCallBack callstateCallBack = new pjsipdll.CallstateCallBack() {
        @Override
        public int fptr_callstate(int id, int accId, int callCode) {
            try {
                log.debug("1.CallstateCallBack callid:" + id + " accId:" + accId + " code:" + callCode);
                for (int i = 0; i < accounts.size(); i++) {
                    if (accounts.get(i).accId == accId) {
                        log.debug("2.CallstateCallBack : username :" + accounts.get(i).username);
                        switch (callCode) {
                            case PJSIP_INV_STATE_NULL:
                                accounts.get(i).callId = id;
                                break;
                            case PJSIP_INV_STATE_CALLING:
                                accounts.get(i).callId = id;
                                accounts.get(i).status = CALLING;
                                break;
                            case PJSIP_INV_STATE_INCOMING:
                                accounts.get(i).callId = id;
                                break;
                            case PJSIP_INV_STATE_EARLY:
                                accounts.get(i).callId = id;
                                accounts.get(i).status = RING;
                                break;
                            case PJSIP_INV_STATE_CONNECTING:
                                accounts.get(i).callId = id;
                                break;
                            case PJSIP_INV_STATE_CONFIRMED://5
                                accounts.get(i).callId = id;
                                accounts.get(i).status = TALKING;
                                break;
                            case PJSIP_INV_STATE_DISCONNECTED:
                                accounts.get(i).callId = id;
                                accounts.get(i).status = HUNGUP;
                                break;
                            default:
                                break;
                        }
                    }
                }
            } catch (Throwable ex) {
                log.error("【PjsipException fptr_callstate】" + ex);
            }
            return 0;
        }
    };
    //DTMF回调
    public pjsipdll.DtmfCallBack dtmfCallBack = new pjsipdll.DtmfCallBack() {
        @Override
        public int fptr_dtmfdigit(int id, int dtmf) {
            log.debug("DtmfCallBack id:" + id + "dtmf:" + dtmf);
            return 0;
        }
    };

    public List<UserAccount> get_accounts() {
        return accounts;
    }

    public UserAccount getAccount(int num){

        for (int i=0; i<accounts.size(); i++){
            if(accounts.get(i).username.equals(String.valueOf(num))){
                return accounts.get(i);
            }
        }
        return null;
    }


    public void setAccounts(List<UserAccount> accounts) {
        this.accounts = accounts;
    }

    //初始化PJSIP
    @Step("【pjsip】初始化PJSIP")
    public void Pj_Init() {
        try {
            accounts = new ArrayList<UserAccount>();
            pjsipdll.instance.ys_init();
            pjsipdll.instance.ys_main();
//        Reporter.infoExec("pjsip init "+ pjsipdll.instance.ys_init());
//        Reporter.infoExec("pjisp main " +pjsipdll.instance.ys_main());
            pjsipdll.instance.onRegStateCallback(registerCallBack);
            pjsipdll.instance.onCallIncoming(incomingcallback);
            pjsipdll.instance.onCallStateCallback(callstateCallBack);
            pjsipdll.instance.onDtmfDigitCallback(dtmfCallBack);

            String m_os = System.getProperty("os.name");
            log.debug("[System Property:]" + m_os);
            if (m_os.toLowerCase().startsWith("win")) {
                log.debug("[System is Windows]");
                pjsipdll.instance.ys_log_set_level(1);
            } else {
            }
//
            Reporter.infoExec("pjs_init done");
        } catch (Throwable ex) {
            log.error("【PjsipException Init】" + ex);
        }
    }

    //释放PJSIP
    @Step("【pjsip】释放PJSIP")
    public void Pj_Destory() {
        try {
            pjsipdll.instance.ys_destroy_pjsua();
        } catch (Throwable ex) {
            log.error("【PjsipException Destory】" + ex);
        }
    }

    //在Account数组中创建分机
    @Step("【pjsip】在Account数组中创建分机")
    public void Pj_CreateAccount(int username, String password, String type, int pos) {
        try {
            Pj_CreateAccount(type, username, password, pos, "", 5060);
        } catch (Throwable ex) {
            log.error("【PjsipException CreateAccount】" + ex);
        }
    }

    @Step("【pjsip】创建分机, username：{0},  password:{1},  type:{2}, port:{3}, pos:{4} ")
    public void Pj_CreateAccount(int username, String password, String type, int port, int pos) {
        try {
            Pj_CreateAccount(type, username, password, pos, "", port);
        } catch (Throwable ex) {
            log.error("【PjsipException CreateAccount】" + ex);
        }
    }

    @Step("【pjsip】创建分机,type:{0}, username：{1},  password:{2},  pos:{3}, ip:{4}, port:{5} ")
    public void Pj_CreateAccount(String type, int username, String password, int pos, String ip, int port) {
        UserAccount account = new UserAccount();
        try {
            account.username = String.valueOf(username);
            account.password = password;
            account.ip = ip;
            account.type = type;
            account.pos = pos;
            account.callId = -1;
            account.accId = -1;
            account.port = String.valueOf(port);
            if (type == "UDP" || type == "udp") {
                account.uriHead = "sip:";
            } else if (type == "TLS" || type == "tls") {
                account.uriHead = "sips:";
            }
        } catch (Throwable ex) {
            log.error("【PjsipException Pj_CreateAccount】" + ex);
        }
        this.accounts.add(account);
    }

    //注册分机
    @Step("【pjsip】注册分机, type:{0}, username:{1},  ip:{2}, password:{3}, port:{4}, isAsserst:{5}")
    public UserAccount Pj_Register_Account_For_PSeries(String type, int username, String ip, String password, int port, boolean isAsserst) {

        UserAccount account;
        account = findAccountByUsername(String.valueOf(username));
        try {
            account.ip = ip;
//        account.port = String.valueOf(port);
            account.uriHead = "sip:";
//        account.accId  = pjsipdll.instance.ys_registerAccount(account.uriHead+String.valueOf(username)+"@"+ip+":"+String.valueOf(port), "sip:"+ip+":"+account.port, "*", String.valueOf(username), account.password, "", true);
            account.accId = pjsipdll.instance.ys_registerAccount(account.uriHead + String.valueOf(username) + "@" + ip + ":" + account.port, "sip:" + ip + ":" + account.port, "*", String.valueOf(username), account.password, "", true, 99999);

            log.debug("sip: ." + account.uriHead + String.valueOf(username) + "@" + ip + ":" + account.port + "......." + account.accId);
            log.debug("sip register: " + "sip:" + ip + ":" + account.port);
            log.debug("username:" + String.valueOf(username));
            log.debug("pwd :" + account.password);
            if (isAsserst) {
                pageDeskTop.taskBar_Main.click();
                pageDeskTop.pbxmonitorShortcut.click();
                ys_waitingTime(3000);
                int time = 16;
                String call_status = null;
                while (time > 0) {
                    ys_waitingTime(1000);
                    call_status = String.valueOf(gridExtensonStatus(extensions.grid_status, account.pos, 0));
                    if (call_status.equals("Registered")) {
                        break;
                    }
                    time--;
                }
//            YsAssert.assertEquals(call_status,"Registered",String.valueOf(username)+"分机注册");
                if (time > 0) {
                    updateAccountByUsername(username);
                }
            }
        } catch (Throwable ex) {
            log.error("【PjsipException Pj_Register_Account_For_PSeries】" + ex);
        }
        return account;
    }

    //注册分机
    @Step("【pjsip】注册分机, type:{0}, username:{1},  ip:{2}, password:{3}, port:{4}, isAsserst:{5}")
    public UserAccount Pj_Register_Account(String type, int username, String ip, String password, int port, boolean isAsserst) {

        UserAccount account;
        account = findAccountByUsername(String.valueOf(username));
        try {
            account.ip = ip;
//        account.port = String.valueOf(port);
            account.uriHead = "sip:";
//        account.accId  = pjsipdll.instance.ys_registerAccount(account.uriHead+String.valueOf(username)+"@"+ip+":"+String.valueOf(port), "sip:"+ip+":"+account.port, "*", String.valueOf(username), account.password, "", true);
            account.accId = pjsipdll.instance.ys_registerAccount(account.uriHead + String.valueOf(username) + "@" + ip + ":" + account.port, "sip:" + ip + ":" + account.port, "*", String.valueOf(username), account.password, "", true, 99999);

//        log.debug("sip: ."+account.uriHead+String.valueOf(username)+"@"+ip+":"+account.port+"......." + account.accId);
//        log.debug("sip register: "+"sip:"+ip+":"+account.port);
//        log.debug("username:"+String.valueOf(username));
//        log.debug("pwd :" +account.password);
            if (false) {
                pageDeskTop.taskBar_Main.click();
                pageDeskTop.pbxmonitorShortcut.click();
                ys_waitingTime(3000);
                int time = 16;
                String call_status = null;
                while (time > 0) {
                    ys_waitingTime(1000);
                    call_status = String.valueOf(gridExtensonStatus(extensions.grid_status, account.pos, 0));
                    if (call_status.equals("Registered")) {
                        break;
                    }
                    time--;
                }
//            YsAssert.assertEquals(call_status,"Registered",String.valueOf(username)+"分机注册");
                if (time > 0) {
                    updateAccountByUsername(username);
                }
            }
        } catch (Throwable ex) {
            log.error("【PjsipException Pj_Register_Account】" + ex);
        }
        return account;
    }

    /**
     * @param username
     * @param ip
     * @
     */
    public void Pj_Register_Account_WithoutAssist(int username, String ip) {
        try {
            Pj_Register_Account("UDP", username, ip, "", 5060, false);
            ys_waitingTime(2000);
        } catch (Throwable ex) {
            log.error("【PjsipException Pj_Register_Account_WithoutAssist]" + ex);
        }
    }

    public void Pj_Register_Account_WithoutAssist(int username, String ip, int port) {
        try {
            Pj_Register_Account("UDP", username, ip, "", port, false);
        } catch (Throwable ex) {
            log.error("【PjsipException Pj_Register_Account_WithoutAssist]" + ex);
        }
    }

    public void Pj_Register_Account_WithoutAssist_For_PSeries(int username, String ip) {
        try {
            Pj_Register_Account("UDP", username, ip, "", 5060, false);
            ys_waitingTime(2000);
        } catch (Throwable ex) {
            log.error("【PjsipException Pj_Register_Account_WithoutAssist_For_PSeries]" + ex);
        }
    }

    public void Pj_Register_Account_WithoutAssist_For_PSeries(int username, String ip, int port) {
        try {
            Pj_Register_Account("UDP", username, ip, "", port, false);
        } catch (Throwable ex) {
            log.error("【PjsipException Pj_Register_Account_WithoutAssist_For_PSeries]" + ex);
        }
    }

    /**
     * @param username
     * @param ip
     * @param password
     * @param port
     * @
     */
    @Step("【pjsip】账号注册 username：{0} , ip：{1},password：{2},port：{3} ")
    public void Pj_Register_Account(int username, String ip, String password, int port) {
        Pj_Register_Account("UDP", username, ip, password, port, true);
    }

    /**
     * @param type
     * @param username
     * @param ip
     * @param password
     * @param port
     * @
     */
    @Step("【pjsip】账号注册 type：{0}, username：{1} , ip：{2},password：{3},port：{4} ")
    public void Pj_Register_Account(String type, int username, String ip, String password, int port) {
        Pj_Register_Account(type, username, ip, password, port, true);
    }

    /**
     * @param username
     * @param ip
     * @
     */
    @Step("【pjsip】账号注册  username：{0} , ip：{1}")
    public void Pj_Register_Account(int username, String ip) {
        Pj_Register_Account("UDP", username, ip, "", 5060, true);
    }

    @Step("【pjsip】账号注册  username：{0} , ip：{1}")
    public void Pj_Register_Account_For_PSeries(int username, String ip) {
        Pj_Register_Account_For_PSeries("UDP", username, ip, "", 5060, true);
    }

    @Step("【pjsip】账号注册  username：{0} , ip：{1} ,port:{2}")
    public void Pj_Register_Account(int username, String ip, int port) {
        Pj_Register_Account("UDP", username, ip, "", port, true);
    }

    /**
     * 取消注册
     *
     * @param username
     * @return
     */
    @Step("【pjsip】取消注册：{0}")
    public int Pj_Unregister_Account(int username) {
        int suc = -1;
        try {
            suc = pjsipdll.instance.ys_unregister_account(findAccountByUsername(String.valueOf(username)).accId);
//        if(suc == 0)
//            removeAccountByUsername(String.valueOf(username));
        } catch (Throwable ex) {
            log.error("【PjsipException Pj_Unregister_Account】" + ex);
        }
        return suc;
    }

    /**
     * 取消全部分机注册
     *
     * @return
     */
    @Step("【pjsip】取消全部分机注册：{0}")
    public int Pj_Unregister_Accounts() {
        int suc = -1;
        try {
            suc = pjsipdll.instance.ys_removeAccounts();
            if (suc == 0) {
//            accounts.removeAll(accounts);
            }
        } catch (Throwable ex) {
            log.error("【PjsipException Pj_Unregister_Accounts】" + ex);
        }
        return suc;
    }

    //拨号 不会自动应答  特殊呼出号码
    @Step("【pjsip】拨号 不会自动应答 特殊呼出号码： callerNum：{0} , Callee：{1} , ServerIp：{2} , Assert：{3}")
    public String Pj_Make_Call_No_Answer(int CallerNum, String Callee, String ServerIp, boolean Assert) {
        try {
            UserAccount CallerAccount;
            CallerAccount = findAccountByUsername(String.valueOf(CallerNum));
            String uri = "";
            if (ServerIp.isEmpty())
                uri = "sip:" + Callee + "@" + CallerAccount.ip + ":" + CallerAccount.port;
            else
                uri = "sip:" + Callee + "@" + ServerIp + ":" + CallerAccount.port;

            log.debug("Pj_Make_Call_No_Answer: "+ uri);//todo 待后续按日志等级输出
            if (CallerAccount.accId != -1) {
                pjsipdll.instance.ys_makeCall(CallerAccount.accId, uri, false); //todo 待后续按日志等级输出

            }
            ys_waitingTime(1000);
        } catch (java.lang.NullPointerException ex) {
            log.error("【Pjsip NullPointerException  Pj_Make_Call_No_Answer】" + ex);
            throw new NullPointerException();
        } catch (Throwable ex){
            log.error("【Pjsip exception  Pj_Make_Call_No_Answer】" + ex);
        }
        return "";
    }

    @Step("【pjsip】拨号 不会自动应答： callerNum：{0} , Callee：{1} , ServerIp：{2} ")
    public String Pj_Make_Call_No_Answer(int CallerNum, String Callee, String ServerIp) {
        return Pj_Make_Call_No_Answer(CallerNum, Callee, ServerIp, false);
    }

    @Step("【pjsip】拨号 不会自动应答： callerNum：{0} , Callee：{1} , ServerIp：{2} ")
    public String Pj_Make_Call_No_Answer(int CallerNum, String Callee) {
        return Pj_Make_Call_No_Answer(CallerNum, Callee, "", false);
    }

    @Step("【pjsip】被叫方手动接听 默认 code =  200： callerNum：{0} , Assert：{1")
    public int Pj_Answer_Call(int CalleeNum) {
        return Pj_Answer_Call(CalleeNum, 200, false);
    }

    //   //被叫方手动接听 默认 code =  200
    @Step("【pjsip】被叫方手动接听 默认 code =  200： callerNum：{0} , Assert：{1")
    public int Pj_Answer_Call(int CalleeNum, boolean Assert) {
        return Pj_Answer_Call(CalleeNum, 200, Assert);
    }

    @Step("【pjsip】被叫方手动接听 默认 code =  200： callerNum：{0} , Assert：{1")
    public int Pj_Answer_Call(int CalleeNum, int code) {
        return Pj_Answer_Call(CalleeNum, code, false);
    }

    //被叫方手动接听 可以自定义 code   486 （Busy )  200(ok)  180(ring)
    @Step("【pjsip】被叫方手动接听 可以自定义 code   486 （Busy )  200(ok)  180(ring)： callerNum：{0} , code：{1} , Assert：{2}")
    public int Pj_Answer_Call(int CalleeNum, int code, boolean Assert) {
        int suc = -1;
        try {
            UserAccount CalleeAccount;
            UserAccount CallerAccount = null;
            String caller_status = null;
            String callee_status = null;
            CalleeAccount = findAccountByUsername(String.valueOf(CalleeNum));
            log.debug("Answer Call  "+CalleeAccount.callId+"  "+CalleeAccount.username);//todo 待后续日志等级输出
            if (CalleeAccount.callId != -1) {
                int answer = pjsipdll.instance.ys_answerCall(CalleeAccount.callId, code);
                log.debug("answer return: "+answer);
            } else {

            }
            if (Assert) {
                pageDeskTop.taskBar_Main.click();
                pageDeskTop.pbxmonitorShortcut.click();
                if (CalleeAccount.pos != -1) {
                    int timeed = 5;
                    while (timeed > 0) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        callee_status = String.valueOf(gridExtensonStatus(extensions.grid_status, CalleeAccount.pos, 0));
                        if (callee_status.equals("Busy")) {
                            break;
                        }
                        timeed--;
                    }
                }
            }
        } catch (Throwable ex) {
            log.error("【PjsipException Answer_Call】" + ex);
        }
        return suc;
    }

    //拨号 自动应答  特殊呼出号码
    @Step("【pjsip】拨号 自动应答 特殊呼出号码： callerNum：{0} , Callee：{1} , ServerIp：{2} , Assert：{3}")
    public String Pj_Make_Call_Auto_Answer(int CallerNum, String Callee, String ServerIp, boolean Assert) {
        UserAccount CallerAccount;
        String caller_status = null;
        String callee_status = null;
        try {
            CallerAccount = findAccountByUsername(String.valueOf(CallerNum));
            String uri = "";

            if (ServerIp.isEmpty())
                uri = "sip:" + Callee + "@" + CallerAccount.ip + ":" + CallerAccount.port;
            else
                uri = "sip:" + Callee + "@" + ServerIp + ":" + CallerAccount.port;

            log.debug("uri/./.........." + uri + "  " + CallerAccount.accId);
            int retMakecall = pjsipdll.instance.ys_makeCall(CallerAccount.accId, uri, true);
            log.debug("[Make call Auto] " + retMakecall);



        } catch (Throwable ex) {
            log.error("【PjsipException Make_Call_Auto_Answer】" + ex);
        }
        return callee_status;
    }

    //拨号 自动应答  特殊呼出号码
    @Step("【pjsip】拨号 自动应答 特殊呼出号码： callerNum：{0} , Callee：{1} , ServerIp：{2} , Assert：{3}")
    public String Pj_Make_Call_Auto_Answer_For_PSeries(int CallerNum, String Callee, String ServerIp, boolean Assert) {
        UserAccount CallerAccount;

        CallerAccount = findAccountByUsername(String.valueOf(CallerNum));
        String uri = "";
        String caller_status = null;
        String callee_status = null;

        try {
            //+":"+CalleeAccount.port
//        uri = CalleeAccount.uriHead+CalleeAccount.username+"@"+ServerIp;
            if (ServerIp.isEmpty())
                uri = "sip:" + Callee + "@" + CallerAccount.ip + ":" + CallerAccount.port;
            else
                uri = "sip:" + Callee + "@" + ServerIp + ":" + CallerAccount.port;

            log.debug("uri/./.........." + uri + "  " + CallerAccount.accId);
            int retMakecall = pjsipdll.instance.ys_makeCall(CallerAccount.accId, uri, true);
            log.debug("[Make call Auto] " + retMakecall);
            if (Assert) {
                pageDeskTop.taskBar_Main.click();
                pageDeskTop.pbxmonitorShortcut.click();
                if (CallerAccount.pos != -1) {
                    int timeer = 5;
                    while (timeer > 0) {
                        ys_waitingTime(1000);
                        caller_status = String.valueOf(gridExtensonStatus(extensions.grid_status, CallerAccount.pos, 0));
                        if (caller_status.equals("Busy")) {
                            break;
                        }
                        timeer--;
                    }
                }
                //返回为-1时，设置没有正常注册
                if (CallerAccount.pos == -1) {
                    log.error("[CallerAccount 为-1 状态异常！！！]");
                    return null;
                }

//            YsAssert.assertEquals(caller_status,"Busy");
//            YsAssert.assertEquals(callee_status,"Busy");
            }
//        m_extension.closeMonitorWindow();
//        pageDeskTop.CDRandRecording.click();
        } catch (Throwable ex) {
            log.error("【PjsipException Make_Call_Auto_Answer】" + ex);
        }
        return callee_status;
    }

    @Step("【pjsip】拨号 自动应答： callerNum：{0} , Callee：{1} , ServerIp：{2}")
    public String Pj_Make_Call_Auto_Answer(int CallerNum, String CalleeNum, String ServerIp) {
        return Pj_Make_Call_Auto_Answer(CallerNum, CalleeNum, ServerIp, false);
    }

    @Step("【pjsip】拨号 自动应答： callerNum：{0} , Callee：{1} , ServerIp：{2}")
    public String Pj_Make_Call_Auto_Answer(int CallerNum, String CalleeNum) {
        return Pj_Make_Call_Auto_Answer(CallerNum, CalleeNum, "", false);
    }

    //通话全部挂断
    @Step("【pjsip】通话全部挂断")
    public int Pj_Hangup_All() {
        int suc = -1;
        try {
            suc = pjsipdll.instance.ys_hangup_all_call();
            ys_waitingTime(2000);
        } catch (Throwable ex) {
            log.error("【PjsipException Hangup_All】" + ex);
        }
        return suc;
    }

    //挂断指定通话
    @Step("【pjsip】挂断指定通话: caller:{0} , callee:{1}")
    public int Pj_hangupCall(int number) {
        int suc = -1;
        try {
            UserAccount account = null;
            for (int i = 0; i < accounts.size(); i++) {
                account = accounts.get(i);
                if (account.username.equals(String.valueOf(number))) {
                    if (account.status == HUNGUP) {
                        log.debug("pj_hangupCall: 分机:" + account.username + "处于hungup");
                        return suc;
                    }
                }
            }
            UserAccount HangupAccont = findAccountByUsername(String.valueOf(number));
            suc = pjsipdll.instance.ys_releaseCall(HangupAccont.callId);
            log.debug("pj_hangupCall sus: "+suc);
            ys_waitingTime(3000);

        } catch (Throwable ex) {
            log.error("【PjsipException hangupCall number】" + ex);
        }
        return suc;
    }

    //挂断指定通话
    @Step("【pjsip】挂断指定通话: caller:{0} , callee:{1}")
    public int Pj_hangupCall(int caller, int callee) {
        int suc = -1;
        UserAccount account = null;
        try {
            for (int i = 0; i < accounts.size(); i++) {
                account = accounts.get(i);
                if (account.username.equals(String.valueOf(caller))) {
                    if (account.status == HUNGUP) {
                        Reporter.infoCheck("分机:" + account.username + "处于hungup");
                        YsAssert.assertEquals(account.status, TALKING, "分机" + account.username + "处于hungup");
                    }
                }
            }
            UserAccount HangupAccont = findAccountByUsername(String.valueOf(caller));
            suc = pjsipdll.instance.ys_releaseCall(HangupAccont.callId);

            if (caller != callee) {
                for (int i = 0; i < accounts.size(); i++) {
                    account = accounts.get(i);
                    if (account.username.equals(String.valueOf(callee))) {
                        if (account.status == HUNGUP) {
                            Reporter.error("分机:" + account.username + "处于hungup");
                            YsAssert.assertEquals(account.status, TALKING, "分机" + account.username + "处于hungup");
                        }
                    }
                }
                HangupAccont = findAccountByUsername(String.valueOf(callee));
                suc = pjsipdll.instance.ys_releaseCall(HangupAccont.callId);
            }
            ys_waitingTime(5000);
        } catch (Throwable ex) {
            log.error("【PjsipException Pj_hangupCall】" + ex);
        }
        return suc;
    }

    //发送DTMF
    @Step("【pjsip】发送DTMF: username:{0} , dtmf:{1}")
    public int Pj_Send_Dtmf(int username, String... dtmf) {
        int suc = -1;
        try {
            ArrayList<String> dtmfList = new ArrayList<>();
            for (String index : dtmf) {
                dtmfList.add(index);
            }

            UserAccount CallerAccont = findAccountByUsername(String.valueOf(username));
            for (int i = 0; i < dtmfList.size(); i++) {
                log.debug("callId: " + CallerAccont.callId + "send dtmf :" + dtmfList.get(i) + " num:");
                if (CallerAccont.callId != -1) {
                    suc = pjsipdll.instance.ys_dialDtmf(CallerAccont.callId, dtmfList.get(i), 1);
                    log.debug("send dtmf end ...");
                }
                ys_waitingTime(500);
            }
        } catch (Throwable ex) {
            log.error("【PjsipException Send_Dtmf】" + ex);
        }
        return suc;
    }



    public int Pj_Send_Dtmf(int username, int callid, String... dtmf) {
        ArrayList<String> dtmfList = new ArrayList<>();
        for (String index : dtmf) {
            dtmfList.add(index);
        }
        int suc = -1;
        for (int i = 0; i < dtmfList.size(); i++) {
            log.debug("callId: " + callid + "send dtmf :" + dtmfList.get(i) + " num:");
            suc = pjsipdll.instance.ys_dialDtmf(callid, dtmfList.get(i), 1);
            log.debug("send dtmf end ...");
            ys_waitingTime(500);
        }
        return suc;
    }

    public int Pj_Send_Dtmf(int username, List<String> dtmf) {
        int suc = -1;
        try {
            UserAccount CallerAccont = findAccountByUsername(String.valueOf(username));
            for (int i = 0; i < dtmf.size(); i++) {
                log.debug("callId: " + CallerAccont.callId + "send dtmf :" + dtmf.get(i) + " num:");
                suc = pjsipdll.instance.ys_dialDtmf(CallerAccont.callId, dtmf.get(i), 1);
                ys_waitingTime(1000);
            }
        } catch (Throwable ex) {
            log.error("【PjsipException Pj_Send_Dtmf】" + ex);
        }
        return suc;
    }

    //===============================提供给外部类分机信息========================//
    //获取分机信息
    public UserAccount getUserAccountInfo(int username) {
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).username.equals(String.valueOf(username))) {
                return accounts.get(i);
            }
        }
        return null;
    }

    //==============================对 UserAccount 结构体操作函数================================================//
    //查找账户
    private UserAccount findAccountByUsername(String username) {
        UserAccount account = null;
        for (int i = 0; i < accounts.size(); i++) {
            account = accounts.get(i);
//            log.debug("finding account "+account.username);
            if (account.username.equals(username)) {
//                log.debug("find this "+account.username+" IP:"+account.ip);
                return account;
            }
        }
        return null;
    }

    //删除所有账户
    public void remvoeAccounts() {
        accounts.clear();
    }

    //删除账户 根据username查找
    public void removeAccountByUsername(String username) {
        UserAccount account;
        for (int i = 0; i < accounts.size(); i++) {
            account = accounts.get(i);
            if (account.username.equals(username)) {
                accounts.remove(i);
                break;
            }
        }
    }

    //更新账户 根据accid查找
    private void updateAccountByAccId(int accid) {
        UserAccount account = new UserAccount();
        for (int i = 0; i < accounts.size(); i++) {
            account = accounts.get(i);
            if (account.accId == accid) {

                break;
            }
        }
    }

    //更新账户 根据usename查找
    private void updateAccountByUsername(int username) {
        UserAccount account = new UserAccount();
        for (int i = 0; i < accounts.size(); i++) {
            account = accounts.get(i);
            if (account.username == String.valueOf(username)) {
                break;
            }
        }
    }
}
