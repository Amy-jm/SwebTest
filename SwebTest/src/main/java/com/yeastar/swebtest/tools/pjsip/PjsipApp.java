package com.yeastar.swebtest.tools.pjsip;

import com.yeastar.swebtest.tools.reporter.Reporter;
//import com.yeastar.swebtest.tools.reporter.Reporter;
import com.yeastar.swebtest.tools.ysassert.YsAssert;


import java.util.ArrayList;
import java.util.List;

import static com.yeastar.swebtest.driver.Config.*;
import static com.yeastar.swebtest.driver.SwebDriver.*;

/**
 * Created by Yeastar on 2017/6/7.
 */
public class PjsipApp extends PjsipDll{

    final static int PJSIP_INV_STATE_NULL=0;	        /**< Before INVITE is sent or received  0*/
    final static int PJSIP_INV_STATE_CALLING=1;	    /**< After INVITE is sent		    1*/
    final static int PJSIP_INV_STATE_INCOMING=2;	    /**< After INVITE is received.	    2*/
    final static int PJSIP_INV_STATE_EARLY=3	  ;     /**< After response with To tag.	    3*/
    final static int PJSIP_INV_STATE_CONNECTING=4;	/**< After 2xx is sent/received.	4   */
    final static int PJSIP_INV_STATE_CONFIRMED=5;	    /**< After ACK is sent/received.	5   */
    final static int PJSIP_INV_STATE_DISCONNECTED=6; /**< Session is terminated.		    6*/

    private static List<UserAccount> accounts;

    public List<UserAccount> get_account() {
        return accounts;
    }

    public void setAccounts(List<UserAccount> accounts) {
        this.accounts = accounts;
    }
    //初始化PJSIP
    public  void Pj_Init(){
        accounts = new ArrayList<UserAccount>();
        Reporter.infoExec("pjsip init "+pjsipdll.instance.ys_init());
        Reporter.infoExec("pjisp main " +pjsipdll.instance.ys_main());
        pjsipdll.instance.onRegStateCallback(registerCallBack);
        pjsipdll.instance.onCallIncoming(incomingcallback);
        pjsipdll.instance.onCallStateCallback(callstateCallBack);
        pjsipdll.instance.onDtmfDigitCallback(dtmfCallBack);
//        pjsipdll.instance.ys_printlog();
        Reporter.infoExec("pjs_init done");

    }
    //释放PJSIP
    public void Pj_Destory(){
        pjsipdll.instance.ys_destroy_pjsua();
    }
    //在Accout数组中创建分机
    public void Pj_CreateAccount(int username, String password,String type,int pos){
        Pj_CreateAccount(type,username,password,pos,"",5060);
    }
    public void Pj_CreateAccount(int username, String password,String type,int port,int pos){
        Pj_CreateAccount(type,username,password,pos,"",port);
    }
    public void Pj_CreateAccount(String type,int username, String password,int pos,String ip,int port){
        UserAccount account = new UserAccount();
        account.username = String.valueOf(username);
        account.password = password;
        account.ip = ip;
        account.type = type;
        account.pos = pos;
        account.callId = -1;
        account.accId = -1;
        account.port = String.valueOf(port);
        if(type == "UDP" || type == "udp"){
            account.uriHead = "sip:";
        }
        else if(type == "TLS" || type == "tls"){
            account.uriHead = "sips:";
        }

        this.accounts.add(account);
    }

    //注册分机
    public UserAccount Pj_Register_Account(String type,int username, String ip,String password,int port,boolean isAsserst) {

        UserAccount account ;
        account = findAccountByUsername(String.valueOf(username));
        account.ip = ip;
//        account.port = String.valueOf(port);
        account.uriHead = "sip:";
//        account.accId  = pjsipdll.instance.ys_registerAccount(account.uriHead+String.valueOf(username)+"@"+ip+":"+String.valueOf(port), "sip:"+ip+":"+account.port, "*", String.valueOf(username), account.password, "", true);
        account.accId  = pjsipdll.instance.ys_registerAccount(account.uriHead+String.valueOf(username)+"@"+ip+":"+account.port, "sip:"+ip+":"+account.port, "*", String.valueOf(username), account.password, "", true,99999);

        System.out.println("sip: ."+account.uriHead+String.valueOf(username)+"@"+ip+":"+account.port+"......." + account.accId);
        System.out.println("sip register: "+"sip:"+ip+":"+account.port);
        System.out.println("username:"+String.valueOf(username));
        System.out.println("pwd :" +account.password);
        if(isAsserst){
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            ys_waitingTime(3000);
            int time=16;
            String call_status = null;
            while (time>0){
                ys_waitingTime(1000);
                call_status = String.valueOf(gridExtensonStatus(extensions.grid_status,account.pos,0));
                if(call_status.equals("Registered")){
                    break;
                }
                time--;
            }
//            YsAssert.assertEquals(call_status,"Registered",String.valueOf(username)+"分机注册");
            if(time>0){
                updateAccountByUsername(username);
            }
        }
        return account;
    }

    /**
     *
     * @param username
     * @param ip
     * @
     */
    public void Pj_Register_Account_WithoutAssist(int username , String ip){
        Pj_Register_Account("UDP",username,ip, "",5060,false);
        ys_waitingTime(2000);
    }
    public void Pj_Register_Account_WithoutAssist(int username , String ip,int port){
        Pj_Register_Account("UDP",username,ip, "",port,false);
    }

    /**
     *
     * @param username
     * @param ip
     * @param password
     * @param port
     * @
     */
    public void Pj_Register_Account(int username , String ip,String password,int port)  {
        Pj_Register_Account("UDP",username,ip, password,port,true);
    }

    /**
     *
     * @param type
     * @param username
     * @param ip
     * @param password
     * @param port
     * @
     */
    public void Pj_Register_Account(String type,int username , String ip,String password,int port)  {
        Pj_Register_Account(type,username,ip, password,port,true);
    }

    /**
     * @param username
     * @param ip
     * @
     */
    public void Pj_Register_Account(int username,String ip) {
        Pj_Register_Account("UDP",username,ip, "",5060,true);
    }

    public void Pj_Register_Account(int username,String ip,int port) {
        Pj_Register_Account("UDP",username,ip, "",port,true);
    }
    /**
     * 取消注册
     * @param username
     * @return
     */
    public int Pj_Unregister_Account(int username){
        int suc = -1;
        suc = pjsipdll.instance.ys_unregister_account(findAccountByUsername(String.valueOf(username)).accId);
//        if(suc == 0)
//            removeAccountByUsername(String.valueOf(username));
        return suc;
    }
    /**
     * 取消全部分机注册
     * @return
     */
    public int Pj_Unregister_Accounts(){
        int suc = -1;
        suc = pjsipdll.instance.ys_removeAccounts();
        if(suc == 0){
//            accounts.removeAll(accounts);
        }
        return suc;
    }

    //拨号 不会自动应答  特殊呼出号码
    public String Pj_Make_Call_No_Answer(int CallerNum, String Callee,String ServerIp,boolean Assert){
        UserAccount CallerAccount ;
        CallerAccount = findAccountByUsername(String.valueOf(CallerNum));
        String uri  = "";
        uri = "sip:"+Callee+"@"+ ServerIp+":"+CallerAccount.port;
        System.out.println("uri: "+ uri);
        if(CallerAccount.accId != -1){
            System.out.println("make call no answer:"+pjsipdll.instance.ys_makeCall(CallerAccount.accId, uri, false));
        }

        return "";
    }
    public String Pj_Make_Call_No_Answer(int CallerNum,String Callee,String ServerIp ){
        return Pj_Make_Call_No_Answer(CallerNum,Callee,ServerIp,true);
    }
    //   //被叫方手动接听 默认 code =  200
    public int Pj_Answer_Call(int CalleeNum ,boolean Assert){
        return Pj_Answer_Call(CalleeNum,200,Assert);
    }
    //被叫方手动接听 可以自定义 code   486 （Busy )  200(ok)  180(ring)
    public int Pj_Answer_Call(int CalleeNum, int code,boolean Assert) {
        int suc=-1;
        UserAccount CalleeAccount ;
        UserAccount CallerAccount = null;
        String caller_status = null;
        String callee_status = null;
        CalleeAccount = findAccountByUsername(String.valueOf(CalleeNum));
        Reporter.infoExec("Answer Call  "+CalleeAccount.callId+"  "+CalleeAccount.username);
        if(CalleeAccount.callId != -1){
            pjsipdll.instance.ys_answerCall(CalleeAccount.callId,code);
        }else {

        }
        if(Assert){
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            if(CalleeAccount.pos  != -1){
                int timeed=5;
                while (timeed>0){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    callee_status = String.valueOf(gridExtensonStatus(extensions.grid_status,CalleeAccount.pos,0));
                    if(callee_status.equals("Busy")){
                        break;
                    }
                    timeed--;
                }
            }
        }
        return suc ;
    }

    //拨号 自动应答  特殊呼出号码
    public String Pj_Make_Call_Auto_Answer(int CallerNum, String Callee,String ServerIp,boolean Assert)  {
        UserAccount CallerAccount ;

        CallerAccount = findAccountByUsername(String.valueOf(CallerNum));
        String uri  = "";
        String caller_status = null;
        String callee_status = null;

        //+":"+CalleeAccount.port
//        uri = CalleeAccount.uriHead+CalleeAccount.username+"@"+ServerIp;
        uri = "sip:"+Callee+"@"+ ServerIp+":"+CallerAccount.port;

        System.out.println("uri/./.........."+uri+"  "+CallerAccount.accId);
        int retMakecall= pjsipdll.instance.ys_makeCall(CallerAccount.accId,uri,true);
        System.out.println("Make call Auto "+ retMakecall);
        if(Assert){
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            if(CallerAccount.pos  != -1){
                int timeer=5;
                while (timeer>0){
                    ys_waitingTime(1000);
                    caller_status = String.valueOf(gridExtensonStatus(extensions.grid_status,CallerAccount.pos,0));
                    if(caller_status.equals("Busy")){
                        break;
                    }
                    timeer--;
                }
            }

//            YsAssert.assertEquals(caller_status,"Busy");
//            YsAssert.assertEquals(callee_status,"Busy");
        }
//        m_extension.closeMonitorWindow();
//        pageDeskTop.CDRandRecording.click();
        return callee_status;
    }
    public String Pj_Make_Call_Auto_Answer(int CallerNum, String CalleeNum,String ServerIp)  {
        return Pj_Make_Call_Auto_Answer(CallerNum,CalleeNum,ServerIp,true);
    }

    //通话全部挂断
    public int Pj_Hangup_All(){
        int suc=-1;
        suc = pjsipdll.instance.ys_hangup_all_call();
        ys_waitingTime(2000);
        return suc;
    }
    //挂断指定通话
    public int Pj_hangupCall(int caller,int callee)  {
        int suc = -1;
        UserAccount account = null;
        for (int i = 0; i < accounts.size(); i++) {
            account = accounts.get(i);
            if (account.username.equals(String.valueOf(caller))) {
                if (account.status == HUNGUP) {
                    Reporter.infoCheck("分机:" + account.username + "处于hungup");
                    YsAssert.assertEquals(account.status,TALKING,"分机"+account.username+"处于hungup");
                }
            }
        }
        UserAccount HangupAccont = findAccountByUsername(String.valueOf(caller));
        suc = pjsipdll.instance.ys_releaseCall(HangupAccont.callId);

        if (caller != callee){
        for (int i = 0; i < accounts.size(); i++) {
            account = accounts.get(i);
            if (account.username.equals(String.valueOf(callee))) {
                if (account.status == HUNGUP) {
                    Reporter.error("分机:" + account.username + "处于hungup");
                    YsAssert.assertEquals(account.status,TALKING,"分机"+account.username+"处于hungup");
                }
            }
        }
        HangupAccont = findAccountByUsername(String.valueOf(callee));
        suc = pjsipdll.instance.ys_releaseCall(HangupAccont.callId);
    }
        ys_waitingTime(5000);
        return suc;
    }

    //发送DTMF
    public int Pj_Send_Dtmf(int username, String... dtmf) {
        ys_waitingTime(3000);//等待输入DTMF的提示出现
        ArrayList<String> dtmfList = new ArrayList<>();
        for (String index:dtmf){
            dtmfList.add(index);
        }
        int suc = -1;
        UserAccount CallerAccont = findAccountByUsername(String.valueOf(username));
        for (int i=0 ; i<dtmfList.size(); i++){
            System.out.println("callId: "+CallerAccont.callId+"send dtmf :"+dtmfList.get(i)+" num:");
            if(CallerAccont.callId != -1){
                suc = pjsipdll.instance.ys_dialDtmf(CallerAccont.callId,dtmfList.get(i),1);
                System.out.println("send dtmf end ...");
            }
           ys_waitingTime(500);
        }
        return suc;
    }
    public int Pj_Send_Dtmf(int username,int callid ,String... dtmf)  {
        ys_waitingTime(6000);//等待输入DTMF的提示出现
        ArrayList<String> dtmfList = new ArrayList<>();
        for (String index:dtmf){
            dtmfList.add(index);
        }
        int suc = -1;
        for (int i=0 ; i<dtmfList.size(); i++){
            System.out.println("callId: "+callid+"send dtmf :"+dtmfList.get(i)+" num:");
            suc = pjsipdll.instance.ys_dialDtmf(callid,dtmfList.get(i),1);
            System.out.println("send dtmf end ...");
            ys_waitingTime(500);
        }
        return suc;
    }
    public int Pj_Send_Dtmf(int username, List<String> dtmf)  {
        ys_waitingTime(6000);//等待输入DTMF的提示出现
        int suc = -1;
        UserAccount CallerAccont = findAccountByUsername(String.valueOf(username));
        for (int i=0 ; i<dtmf.size(); i++){
            System.out.println("callId: "+CallerAccont.callId+"send dtmf :"+dtmf.get(i)+" num:");
            suc = pjsipdll.instance.ys_dialDtmf(CallerAccont.callId,dtmf.get(i),1);
            ys_waitingTime(1000);
        }
        return suc;
    }


    //======================================Call back==============================//
    //注册回调
    public  pjsipdll.RegisterCallBack registerCallBack = new pjsipdll.RegisterCallBack() {
        @Override
        public int fptr_regstate(int id, int registerCode) {
            System.out.println("RegisterCallBack :"+id +" code:"+ registerCode);
            if(registerCode == 200){
                for(int i=0; i<accounts.size(); i++){
                    if(accounts.get(i).accId == id)
                        accounts.get(i).status=IDLE;
                }
            }else{
                for(int i=0; i<accounts.size(); i++){
                    if(accounts.get(i).accId == id)
                        accounts.get(i).status=INVALID;
                }
                switch (registerCode){
                    case 503://服务器未相应
                        break;
                    case 408://超时
                        break;
                    //....
                }
            }
            return 0;
        }
    };
    //拨号回调
    public pjsipdll.IncomingCallBack incomingcallback = new pjsipdll.IncomingCallBack() {
        @Override
        public int fptr_callincoming(int id, String number,int accid) {
            System.out.println("incomingcallback"+number+"callid:"+id+"accid:"+accid+"accounts.size(): "+accounts.size());

            for(int i=0; i<accounts.size(); i++){
                if(accounts.get(i).accId == accid){
                    accounts.get(i).callId = id;
                    accounts.get(i).status = RING;
                }
            }
            return 0;
        }
    };
    //通话回调
    public pjsipdll.CallstateCallBack callstateCallBack = new pjsipdll.CallstateCallBack() {
        @Override
        public int fptr_callstate(int id, int accId,int callCode) {
            System.out.println("CallstateCallBack callid:"+id +" accId:" + accId+" code:"+ callCode);
            for(int i=0; i<accounts.size(); i++){
                if(accounts.get(i).accId == accId){
                    System.out.println("CallstateCallBack : username :"+accounts.get(i).username);
                    switch (callCode){
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
                        default :
                            break;
                    }
                }
            }
            return 0;
        }
    };
    //DTMF回调
    public pjsipdll.DtmfCallBack dtmfCallBack = new pjsipdll.DtmfCallBack() {
        @Override
        public int fptr_dtmfdigit(int id, int dtmf) {
            System.out.println("DtmfCallBack id:"+id + "dtmf:"+dtmf);
            return 0;
        }
    };
    //===============================提供给外部类分机信息========================//
    //获取分机信息
    public UserAccount getUserAccountInfo(int username){
        for(int i=0; i<accounts.size(); i++){
            if(accounts.get(i).username.equals( String.valueOf(username))){
                return accounts.get(i);
            }
        }
        return null;
    }
    //==============================对 UserAccount 结构体操作函数================================================//
    //查找账户
    private UserAccount findAccountByUsername(String username){
        UserAccount account = null;
        for(int i=0; i<accounts.size(); i++){
            account = accounts.get(i);
//            System.out.println("finding account "+account.username);
            if(account.username.equals(username)){
                System.out.println("find this "+account.username+" IP:"+account.ip);
                return account;
            }
        }
        return null;
    }
    //删除所有账户
    public void remvoeAccounts(){
        accounts.clear();
    }
    //删除账户 根据username查找
    public void removeAccountByUsername(String username){
        UserAccount account;
        for(int i=0; i<accounts.size(); i++){
            account = accounts.get(i);
            if(account.username.equals(username)){
                accounts.remove(i);
                break;
            }
        }
    }
    //更新账户 根据accid查找
    private void updateAccountByAccId(int accid){
        UserAccount account = new UserAccount();
        for (int i=0; i<accounts.size(); i++){
            account = accounts.get(i);
            if (account.accId == accid){

                break;
            }
        }
    }
    //更新账户 根据usename查找
    private void updateAccountByUsername(int username){
        UserAccount account = new UserAccount();
        for (int i=0; i<accounts.size(); i++){
            account = accounts.get(i);
            if (account.username == String.valueOf(username)){
                break;
            }
        }
    }
}
