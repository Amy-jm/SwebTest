package com.yeastar.swebtest.tools.pjsip;
import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary.*;

import static com.yeastar.swebtest.driver.Config.currentPath;

/**
 * Created by GaGa on 2017-04-16.
 */
public class PjsipDll {

    public interface pjsipdll extends Library {

        pjsipdll instance = (pjsipdll)Native.loadLibrary(currentPath+"src\\main\\resources\\pjsip\\pjsipDll.dll",pjsipdll.class);
//        pjsipdll instance = (pjsipdll)Native.loadLibrary(currentPath+"src\\main\\resources\\pjsip\\pjsipDlld_Debug.dll",pjsipdll.class);

        //PjsipDll.dll的通用API
        int ys_registerAccount(String uri, String reguri, String name, String username,
                               String password, String proxy, Boolean isdefault,int reg_timeout);
        int ys_init();
        int ys_main();
        int ys_destroy_pjsua();
        int ys_makeCall(int accountId,String uri, boolean isAutoAnswer);
        int ys_hangup_all_call();
        int ys_setCurrentAccount();
        void ys_printlog();
        int ys_shutdown();
        int ys_releaseCall(int accountId);
        int ys_removeAccounts();
        int ys_answerCall(int callId, int code);
        int ys_unregister_account(int callId);
        int ys_dialDtmf(int callId, String  digits, int mode);
        int ys_makeConference(int callId);


        //=======================定义回调函数=====================//
        interface IncomingCallBack extends Callback {
            int fptr_callincoming(int id,String number,int accid);
        }
        interface RegisterCallBack extends Callback {
            int fptr_regstate(int id,int registerCode);
        }
        interface CallstateCallBack extends Callback {
            int fptr_callstate(int id ,int i,int callCode);
        }
        interface DtmfCallBack extends Callback{
            int fptr_dtmfdigit(int id, int dtmf);
        }

        //=======================注册回调函数=====================//
        int onCallIncoming(IncomingCallBack cb);
        int onRegStateCallback(RegisterCallBack cb);
        int onCallStateCallback(CallstateCallBack cb);
        int onDtmfDigitCallback(DtmfCallBack cb);




    }

}
