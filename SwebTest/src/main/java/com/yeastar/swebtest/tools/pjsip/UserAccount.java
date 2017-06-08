package com.yeastar.swebtest.tools.pjsip;

import com.yeastar.swebtest.tools.pjsip.PjsipDll;

/**
 * Created by GaGa on 2017-04-18.
 */
public class UserAccount extends PjsipDll {
    public String ip = null;
    public String caller = null;
    public String callee = null;
    public String caller_pass = null;
    public String username = null;
    public String password = null;
    public int RegistId = -1;
    public int CallerId = -1;
    public int CallId = -1;

    public static pjsipdll.IncomingCallBack incomingcallback = new pjsipdll.IncomingCallBack() {
        @Override
        public int fptr_callincoming(int id, String number) {
            System.out.println(number);
            System.out.println(id);
            return 0;
        }
    };
    public static void ys_Init() {
        //pjsipdll.instance.dll_printlog();
        pjsipdll.instance.dll_init();
        pjsipdll.instance.dll_main();
        pjsipdll.instance.onCallIncoming(incomingcallback);
    }
    public int ys_Regist() {
        if (username != null && password != null & ip != null) {
            RegistId = pjsipdll.instance.dll_registerAccount("sip:"+username+"@"+ip+"", "sip:"+ip, "YSAsterisk", username, password, "", true);
            return RegistId;
        } else { //��Ҫ�ж��Ƿ�����ͨ���С�
            return -100; //�����쳣
        }
    }
    public int ys_Call() {
        if (caller != null && callee != null)
        {
            CallerId = pjsipdll.instance.dll_registerAccount("sip:"+caller+"@"+ip+"", "sip:"+ip, "YSAsterisk", caller, caller_pass, "", true);
            CallId = pjsipdll.instance.dll_makeCall(CallerId, "sip:"+callee+"@"+ip);
            return CallId;
        } else {
            return -100; //�����쳣
        }
    }
    public int ys_HandUp() {
        if (CallId == -1) {
            return -1;
        }
        pjsipdll.instance.dll_releaseCall(CallId);
        CallId = -1;
        return 0;
    }
    public void ys_UnRegist() {
        pjsipdll.instance.dll_removeAccounts();
    }
    public static void ys_Shutdown() {
        pjsipdll.instance.dll_shutdown();
    }
}
