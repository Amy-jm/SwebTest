package com.yeastar.swebtest.tools.pjsip;

import org.apache.bcel.generic.SWITCH;

import java.util.ArrayList;
import java.util.List;

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

    public  void Pj_Init(){
        accounts = new ArrayList<UserAccount>();
        System.out.println("pjsip init "+pjsipdll.instance.ys_init());
        System.out.println("pjisp main " +pjsipdll.instance.ys_main());
        pjsipdll.instance.onRegStateCallback(registerCallBack);
        pjsipdll.instance.onCallIncoming(incomingcallback);
        pjsipdll.instance.onCallStateCallback(callstateCallBack);
        pjsipdll.instance.onDtmfDigitCallback(dtmfCallBack);
    }

    public void Pj_Destory(){
        pjsipdll.instance.ys_destroy_pjsua();
    }

    public UserAccount Pj_Register_Account(String username, String ip, String password,String port){
        UserAccount account = new UserAccount();
        account.username = username;
        account.ip = ip;
        account.port = port;
        account.password = password;
        account.type = "UDP";
        account.uriHead = "sip:";
//        account.accId  = pjsipdll.instance.ys_registerAccount("sip:"+username+"@"+ip+":"+port, "sip:"+ip+":"+port, "YSAsterisk", username, password, "", true);
        pjsipdll.instance.ys_registerAccount("sip:3001@192.168.7.151:5060","sip:192.168.7.151:5060","YSASterisk","3001","Yeastar202","",true);
        this.accounts.add(account);
        return account;
    }

    public UserAccount Pj_Register_Account(String type,String username, String ip, String password,String port){
        UserAccount account = new UserAccount();
        account.username = username;
        account.ip = ip;
        account.port = port;
        account.password = password;
        account.type = type;
        if(type == "UDP"){
            account.uriHead = "sip:";
            account.accId  = pjsipdll.instance.ys_registerAccount("sip:"+username+"@"+ip+":"+port, "sip:"+ip+":"+port, "YSAsterisk", username, password, "", true);
        }else if(type == "TLS"){
            account.uriHead = "sips";
            account.accId  = pjsipdll.instance.ys_registerAccount("sips:"+username+"@"+ip+":"+port, "sip:"+ip+":"+port, "YSAsterisk", username, password, "", true);
        }else if(type == "TCP"){

        }else{
            account.uriHead = "sip:";
        }
        accounts.add(account);
        return account;
    }

    public int Pj_Unregister_Account(int username){
        int suc = -1;
        suc = pjsipdll.instance.ys_unregister_account(findAccountByUsername(String.valueOf(username)).accId);
        if(suc == 0)
            removeAccountByUsername(String.valueOf(username));
        else{
            //报错
        }
        return suc;
    }

    public int Pj_Unregister_Accounts(){
        int suc = -1;
        suc = pjsipdll.instance.ys_removeAccounts();
        if(suc == 0){
            accounts.removeAll(accounts);
        }
        return suc;
    }

    public int Pj_Make_Call_No_Answer(int CallerNum, int CalleeNum){
        UserAccount CallerAccount ;
        UserAccount CalleeAccount ;
        CallerAccount = findAccountByUsername(String.valueOf(CallerNum));
        CalleeAccount = findAccountByUsername(String.valueOf(CalleeNum));

        String uri = CalleeAccount.uriHead+CalleeAccount.username+"@"+CalleeAccount.ip+":"+CalleeAccount.port;
        CalleeAccount.callId = pjsipdll.instance.ys_makeCall(CallerAccount.accId,uri,false);
        CallerAccount.callId = CalleeAccount.callId;
        return CalleeAccount.callId;
    }
    public int Pj_Make_Call_Auto_Answer(int CallerNum, int CalleeNum){
        UserAccount CallerAccount ;
        UserAccount CalleeAccount ;
        CallerAccount = findAccountByUsername(String.valueOf(CallerNum));
        CalleeAccount = findAccountByUsername(String.valueOf(CalleeNum));
        String uri = CalleeAccount.uriHead+CalleeAccount.username+"@"+CalleeAccount.ip+":"+CalleeAccount.port;
        pjsipdll.instance.ys_makeCall(CallerAccount.accId,uri,true);
        return CalleeAccount.callId;
    }

    public int Pj_Answer_Call(String CalleeNum){
        int suc=-1;
        UserAccount CalleeAccount ;
        CalleeAccount = findAccountByUsername(String.valueOf(CalleeNum));
        pjsipdll.instance.ys_answerCall(CalleeAccount.callId,200);

        return suc ;
    }

    public int Pj_Hangup_All(){
        int suc=-1;
        suc = pjsipdll.instance.ys_hangup_all_call();
        return suc;
    }
    public int Pj_Send_Dtmf(String username, List<String> dtmf) throws InterruptedException {
        int suc = -1;
        UserAccount CallerAccont = findAccountByUsername(username);
        for (int i=0 ; i<dtmf.size(); i++){
            System.out.println("send dtmf :"+dtmf.get(i)+" num:");
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

        public int fptr_callincoming(int id, String number) {
            System.out.println("incomingcallback"+number+"id:"+id);

            return 0;
        }
    };
    //通话回调
    public pjsipdll.CallstateCallBack callstateCallBack = new pjsipdll.CallstateCallBack() {
        @Override
        public int fptr_callstate(int id, int callCode) {
            System.out.println("CallstateCallBack :"+id +" code:"+ callCode);
            for(int i=0; i<accounts.size(); i++){
                if(accounts.get(i).accId == id){
                    switch (callCode){
                        case PJSIP_INV_STATE_NULL:
                            break;
                        case PJSIP_INV_STATE_CALLING:
                            accounts.get(i).status = CALLING;
                            break;
                        case PJSIP_INV_STATE_INCOMING:
                            break;
                        case PJSIP_INV_STATE_EARLY:
                            accounts.get(i).status = CALLING;
                            break;
                        case PJSIP_INV_STATE_CONNECTING:
                            break;
                        case PJSIP_INV_STATE_CONFIRMED://5
                            accounts.get(i).status = TALKING;
                            break;
                        case PJSIP_INV_STATE_DISCONNECTED:
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

    //==============================内部函数================================================//
    //查找账户
    private UserAccount findAccountByUsername(String username){
        UserAccount account = null;
        for(int i=0; i<accounts.size(); i++){
            account = accounts.get(i);
            if(account.username.equals(username)){
                break;
            }
        }
        return account;
    }
    //删除账户
    private void removeAccountByUsername(String username){
        UserAccount account;
        for(int i=0; i<accounts.size(); i++){
            account = accounts.get(i);
            if(account.username.equals(username)){
                accounts.remove(i);
                break;
            }
        }
    }
    //更新账户
    private void updateAccountByAccId(int accid){
        UserAccount account = new UserAccount();
        for (int i=0; i<accounts.size(); i++){
            account = accounts.get(i);
            if (account.accId == accid){

                break;
            }
        }
    }
}
