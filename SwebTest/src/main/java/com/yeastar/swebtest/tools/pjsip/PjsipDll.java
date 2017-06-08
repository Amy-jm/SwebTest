package com.yeastar.swebtest.tools.pjsip;
import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary.*;

/**
 * Created by GaGa on 2017-04-16.
 */
public class PjsipDll {

    public interface pjsipdll extends Library {
        pjsipdll instance = (pjsipdll)Native.loadLibrary("pjsip/pjsipdll", pjsipdll.class);

        //定义回调函数
        interface IncomingCallBack extends Callback {
            int fptr_callincoming(int id,String number);
        }

        //PjsipDll.dll的通用API
        int dll_registerAccount(String uri, String reguri, String name, String username,
                                       String password, String proxy, Boolean isdefault);
        int dll_main();
        int dll_makeCall(int accountId, String uri);
        void dll_printlog();
        int dll_init();
        int dll_shutdown();
        int dll_releaseCall(int accountId);
        int dll_removeAccounts();
        int dll_answerCall(int callId, int code);

        //注册回调函数
        int onCallIncoming(IncomingCallBack cb);

    }

}
