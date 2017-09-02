package com.yeastar.swebtest.tools.pjsip;

import com.sun.jna.platform.win32.Netapi32Util;
import com.yeastar.swebtest.tools.ysassert.YsAssert;
import cucumber.api.java.eo.Se;
import org.apache.bcel.generic.SWITCH;
import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

import static com.yeastar.swebtest.driver.Config.extensions;
import static com.yeastar.swebtest.driver.Config.m_extension;
import static com.yeastar.swebtest.driver.Config.pageDeskTop;
import static com.yeastar.swebtest.driver.SwebDriver.*;

/**
 * Created by Yeastar on 2017/6/7.
 */
public class PjsipApp extends PjsipDll{


    final static int INVALID = -1;
    final static int IDLE = 0;
    final static int CALLING = 1;
    final static int TALKING = 2;
    final static int HUNGUP = 3;

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
        System.out.println("pjsip init "+pjsipdll.instance.ys_init());
        System.out.println("pjisp main " +pjsipdll.instance.ys_main());
        pjsipdll.instance.onRegStateCallback(registerCallBack);
        pjsipdll.instance.onCallIncoming(incomingcallback);
        pjsipdll.instance.onCallStateCallback(callstateCallBack);
        pjsipdll.instance.onDtmfDigitCallback(dtmfCallBack);
//        pjsipdll.instance.ys_printlog();

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
    public UserAccount Pj_Register_Account(String type,int username, String ip,String password,int port,boolean isAsserst) throws InterruptedException {

        UserAccount account ;
        account = findAccountByUsername(String.valueOf(username));
        account.ip = ip;
        account.port = String.valueOf(port);
        account.uriHead = "sip:";
        account.accId  = pjsipdll.instance.ys_registerAccount(account.uriHead+String.valueOf(username)+"@"+ip+":"+String.valueOf(port), "sip:"+ip+":"+String.valueOf(port), "*", String.valueOf(username), account.password, "", true);

        System.out.println("sip::::::......."+account.uriHead+String.valueOf(username)+"@"+ip+":"+String.valueOf(port)+"......." + account.accId);
        if(isAsserst){
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            int time=16;
            String call_status = null;
            while (time>0){
                Thread.sleep(1000);
                call_status = String.valueOf(gridExtensonStatus(extensions.grid_status,account.pos,0));
                if(call_status.equals("Registered")){
                    break;
                }
                time--;
            }
            YsAssert.assertEquals(call_status,"Registered",String.valueOf(username)+"分机注册");
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
     * @throws InterruptedException
     */
    public void Pj_Register_Account_WithoutAssist(int username , String ip) throws InterruptedException {
        Pj_Register_Account("UDP",username,ip, "",5060,false);
    }

    /**
     *
     * @param username
     * @param ip
     * @param password
     * @param port
     * @throws InterruptedException
     */
    public void Pj_Register_Account(int username , String ip,String password,int port) throws InterruptedException {
        Pj_Register_Account("UDP",username,ip, password,port,true);
    }

    /**
     *
     * @param type
     * @param username
     * @param ip
     * @param password
     * @param port
     * @throws InterruptedException
     */
    public void Pj_Register_Account(String type,int username , String ip,String password,int port) throws InterruptedException {
        Pj_Register_Account(type,username,ip, password,port,true);
    }

    /**
     *
     * @param username
     * @param ip
     * @throws InterruptedException
     */
    public void Pj_Register_Account(int username,String ip) throws InterruptedException {
        Pj_Register_Account("UDP",username,ip, "",5060,true);
    }
    /**
     *
     * @param type
     * @param username
     * @param ip
     * @param password
     * @param port
     * @return

    public UserAccount Pj_Register_Account(String type,int username, String ip, String password,int port,boolean isAsserst) throws InterruptedException {
        UserAccount account = new UserAccount();
        account.username =String.valueOf(username) ;
        account.ip = ip;
        account.port = String.valueOf(port);
        account.password = password;
        account.type = type;
        if(type == "UDP"){
            account.uriHead = "sip:";
            account.accId  = pjsipdll.instance.ys_registerAccount("sip:"+username+"@"+ip+":"+String.valueOf(port), "sip:"+ip+":"+String.valueOf(port), "*", String.valueOf(username), password, "", true);
        }else if(type == "TLS"){
            account.uriHead = "sips";
            account.accId  = pjsipdll.instance.ys_registerAccount("sips:"+username+"@"+ip+":"+String.valueOf(port), "sip:"+ip+":"+String.valueOf(port), "*", String.valueOf(username), password, "", true);
        }else if(type == "TCP"){

        }else{
            account.uriHead = "sip:";
        }


        return account;
    }
     */
    //取消注册
    public int Pj_Unregister_Account(int username){
        int suc = -1;
        suc = pjsipdll.instance.ys_unregister_account(findAccountByUsername(String.valueOf(username)).accId);
//        if(suc == 0)
//            removeAccountByUsername(String.valueOf(username));

        return suc;
    }
    //取消全部注册
    public int Pj_Unregister_Accounts(){
        int suc = -1;
        suc = pjsipdll.instance.ys_removeAccounts();
        if(suc == 0){
//            accounts.removeAll(accounts);
        }
        return suc;
    }
    //拨号 无自动应答
    public String Pj_Make_Call_No_Answer(int CallerNum, int CalleeNum,String ServerIp) throws InterruptedException {
        return Pj_Make_Call_No_Answer(CallerNum,CalleeNum,"",ServerIp,true);
    }
    public String Pj_Make_Call_No_Answer(int CallerNum, String CalleeNum,String ServerIp) throws InterruptedException {
        return Pj_Make_Call_No_Answer(CallerNum,CalleeNum,ServerIp,true);
    }
    public String Pj_Make_Call_No_Answer(int CallerNum, int CalleeNum,String DailPatterns,String ServerIp,boolean Assert){
        UserAccount CallerAccount ;
        UserAccount CalleeAccount ;
        CallerAccount = findAccountByUsername(String.valueOf(CallerNum));
        CalleeAccount = findAccountByUsername(String.valueOf(CalleeNum));
        String uri  = "";
        if(DailPatterns.isEmpty()){
            //+":"+CalleeAccount.port
            uri = CalleeAccount.uriHead+CalleeAccount.username+"@"+ServerIp+":"+CalleeAccount.port;
        }
        else {
            //+":"+CalleeAccount.port
            uri = CalleeAccount.uriHead+DailPatterns+CalleeAccount.username+"@"+ServerIp+":"+CalleeAccount.port;
        }
        System.out.println("uri: "+ uri);
        if(CallerAccount.accId != -1)
            CalleeAccount.callId = pjsipdll.instance.ys_makeCall(CallerAccount.accId,uri,false);

        System.out.println("Call id... "+  CalleeAccount.callId);
        return "";
    }
    //拨号 不会自动应答  特殊呼出号码
    public String Pj_Make_Call_No_Answer(int CallerNum, String Callee,String ServerIp,boolean Assert){
        UserAccount CallerAccount ;
//        UserAccount CalleeAccount ;
        CallerAccount = findAccountByUsername(String.valueOf(CallerNum));
//        CalleeAccount = findAccountByUsername((CalleeNum));
        String uri  = "";
        //+":"+CalleeAccount.port
        uri = "sip:"+Callee+"@"+ ServerIp;
//      uri = CalleeAccount.uriHead+CalleeAccount.username+"@"+ServerIp+":"+CalleeAccount.port;
        System.out.println("uri: "+ uri);
        if(CallerAccount.accId != -1)
            pjsipdll.instance.ys_makeCall(CallerAccount.accId,uri,false);

        return "";
    }
    //被叫方手动接听
    public int Pj_Answer_Call(int CalleeNum,boolean Assert) {
        int suc=-1;
        UserAccount CalleeAccount ;
        UserAccount CallerAccount = null;
        String caller_status = null;
        String callee_status = null;
        CalleeAccount = findAccountByUsername(String.valueOf(CalleeNum));
        System.out.println("Answer Call  "+CalleeAccount.callId+"  "+CalleeAccount.username);
        if(CalleeAccount.callId != -1){
            pjsipdll.instance.ys_answerCall(CalleeAccount.callId,200);
        }else {

        }


        if(Assert){
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
//            int timeer=5;
//            while (timeer>0){
//                Thread.sleep(1000);
//                caller_status = String.valueOf(gridExtensonStatus(extensions.grid_status,CallerAccount.pos,0));
//                if(caller_status.equals("Busy")){
//                    break;
//                }
//                timeer--;
//            }
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
    //拨号 自动应答
    public String Pj_Make_Call_Auto_Answer(int CallerNum, int CalleeNum,String DailPatterns,String ServerIp,boolean Assert) throws InterruptedException {
        UserAccount CallerAccount ;
        UserAccount CalleeAccount ;
        CallerAccount = findAccountByUsername(String.valueOf(CallerNum));
        CalleeAccount = findAccountByUsername(String.valueOf(CalleeNum));
        String uri  = "";
        String caller_status = null;
        String callee_status = null;
        if(DailPatterns.isEmpty()){
            //+":"+CalleeAccount.port
            uri = CalleeAccount.uriHead+CalleeAccount.username+"@"+ServerIp;
        }
        else {
            //+":"+CalleeAccount.port
            uri = CalleeAccount.uriHead+DailPatterns+CalleeAccount.username+"@"+ServerIp;
        }
        System.out.println("uri/./.........."+uri+"  "+CallerAccount.accId);
        CalleeAccount.callId = pjsipdll.instance.ys_makeCall(CallerAccount.accId,uri,true);
        if(Assert){
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            if(CallerAccount.pos  != -1){
                int timeer=5;
                while (timeer>0){
                    Thread.sleep(1000);
                    caller_status = String.valueOf(gridExtensonStatus(extensions.grid_status,CallerAccount.pos,0));
                    if(caller_status.equals("Busy")){
                        break;
                    }
                    timeer--;
                }
            }

            if(CalleeAccount.pos  != -1){
                int timeed=10;
                while (timeed>0){
                    Thread.sleep(1000);
                    callee_status = String.valueOf(gridExtensonStatus(extensions.grid_status,CalleeAccount.pos,0));
                    if(callee_status.equals("Busy")){
                        break;
                    }
                    timeed--;
                }
            }
//            YsAssert.assertEquals(caller_status,"Busy");
//            YsAssert.assertEquals(callee_status,"Busy");
        }
//        m_extension.closeMonitorWindow();
//        pageDeskTop.CDRandRecording.click();



        return callee_status;
    }
    public String  Pj_Make_Call_Auto_Answer(int CallerNum, int CalleeNum,String ServerIp) throws InterruptedException {
        return Pj_Make_Call_Auto_Answer(CallerNum,CalleeNum,"",ServerIp,true);
    }
    public String  Pj_Make_Call_Auto_Answer(int CallerNum, int CalleeNum,String prefix,String ServerIp) throws InterruptedException {
        return Pj_Make_Call_Auto_Answer(CallerNum,CalleeNum,prefix,ServerIp,true);
    }
    public String Pj_Make_Call_Auto_Answer(int CallerNum, int CalleeNum,String ServerIp,boolean Assert) throws InterruptedException {
        return Pj_Make_Call_Auto_Answer(CallerNum,CalleeNum,"",ServerIp,Assert);
    }

    //拨号 自动应答  特殊呼出号码
    public String Pj_Make_Call_Auto_Answer(int CallerNum, String Callee,String ServerIp,boolean Assert) throws InterruptedException {
        UserAccount CallerAccount ;

        CallerAccount = findAccountByUsername(String.valueOf(CallerNum));
        String uri  = "";
        String caller_status = null;
        String callee_status = null;

        //+":"+CalleeAccount.port
//        uri = CalleeAccount.uriHead+CalleeAccount.username+"@"+ServerIp;
        uri = "sip:"+Callee+"@"+ ServerIp;

        System.out.println("uri/./.........."+uri+"  "+CallerAccount.accId);
        pjsipdll.instance.ys_makeCall(CallerAccount.accId,uri,true);
        if(Assert){
            pageDeskTop.taskBar_Main.click();
            pageDeskTop.pbxmonitorShortcut.click();
            if(CallerAccount.pos  != -1){
                int timeer=5;
                while (timeer>0){
                    Thread.sleep(1000);
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
    public String Pj_Make_Call_Auto_Answer(int CallerNum, String CalleeNum,String ServerIp) throws InterruptedException {
        return Pj_Make_Call_Auto_Answer(CallerNum,CalleeNum,ServerIp,true);
    }

    //通话全部挂断
    public int Pj_Hangup_All(){
        int suc=-1;
        suc = pjsipdll.instance.ys_hangup_all_call();
        ys_waitingTime(5000);
        return suc;
    }
    //挂断指定通话
    public int Pj_hangupCall(int caller,int callee) throws InterruptedException {
        int suc=-1;
        ys_waitingTime(3000);
        UserAccount HangupAccont = findAccountByUsername(String.valueOf(caller));
        suc = pjsipdll.instance.ys_releaseCall(HangupAccont.callId);
        HangupAccont = findAccountByUsername(String.valueOf(callee));
        suc = pjsipdll.instance.ys_releaseCall(HangupAccont.callId);
        ys_waitingTime(5000);
        return suc;
    }
    //发送DTMF
    public int Pj_Send_Dtmf(int username, String... dtmf) throws InterruptedException {
        ys_waitingTime(6000);//等待输入DTMF的提示出现
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

            Thread.sleep(500);
        }
        return suc;
    }
    public int Pj_Send_Dtmf(int username,int callid ,String... dtmf) throws InterruptedException {
        ys_waitingTime(6000);//等待输入DTMF的提示出现
        ArrayList<String> dtmfList = new ArrayList<>();
        for (String index:dtmf){
            dtmfList.add(index);
        }
        int suc = -1;
        UserAccount CallerAccont = findAccountByUsername(String.valueOf(username));
        for (int i=0 ; i<dtmfList.size(); i++){
            System.out.println("callId: "+callid+"send dtmf :"+dtmfList.get(i)+" num:");
            suc = pjsipdll.instance.ys_dialDtmf(callid,dtmfList.get(i),1);
            System.out.println("send dtmf end ...");
            Thread.sleep(500);
        }
        return suc;
    }
    public int Pj_Send_Dtmf(int username, List<String> dtmf) throws InterruptedException {
        ys_waitingTime(6000);//等待输入DTMF的提示出现
        int suc = -1;
        UserAccount CallerAccont = findAccountByUsername(String.valueOf(username));
        for (int i=0 ; i<dtmf.size(); i++){
            System.out.println("callId: "+CallerAccont.callId+"send dtmf :"+dtmf.get(i)+" num:");
            suc = pjsipdll.instance.ys_dialDtmf(CallerAccont.callId,dtmf.get(i),1);
            Thread.sleep(1000);
        }
        return suc;
    }


    //======================================Call back=============================//
    //注册回调
    public  pjsipdll.RegisterCallBack registerCallBack = new pjsipdll.RegisterCallBack() {
        @Override
        public int fptr_regstate(int id, int registerCode) {
//            System.out.println("RegisterCallBack :"+id +" code:"+ registerCode);
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
            System.out.println("incomingcallback"+number+"callid:"+id+"accid:"+accid+"accounts.size()："+accounts.size());

            for(int i=0; i<accounts.size(); i++){
//                System.out.println("aaaaaaaaaaaaaaaaaaaaaaaa+"+accounts.get(i).username+" "+accounts.get(i).callId);
                if(accounts.get(i).accId == accid){
                    System.out.println("bbbbbbbbbbbbbbbbbbbbbbbb+"+accounts.get(i).username+" "+accounts.get(i).callId);
                    accounts.get(i).callId = id;
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
                            accounts.get(i).status = CALLING;
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
        System.out.println("ffffffffffff "+accounts.size());
        for(int i=0; i<accounts.size(); i++){
            System.out.println("aaaaaaaa"+accounts.get(i).username+accounts.get(i).pos+" "+String.valueOf(username));
            if(accounts.get(i).username.equals( String.valueOf(username)))
                return accounts.get(i);
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
