package com.yeastar.swebtest.tools.pjsip;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface CLibrary extends Library {

    CLibrary INSTANCE = (CLibrary) Native.loadLibrary(("add"), CLibrary.class);
    int add(int a, int b);
}
