package com.champion.dance;

import com.google.common.collect.Lists;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

import java.util.ArrayList;

/**
 * Created with dance
 * Author: jiangping.li
 * Date: 2018/3/9 15:22
 * Description：
 */
public class HelloWorld {
    public interface CLibrary extends Library {
        CLibrary INSTANCE = (CLibrary)
                Native.loadLibrary((Platform.isWindows() ? "msvcrt" : "c"),
                        CLibrary.class);
        void printf(String format, Object... args);
    }
    public static void main(String[] args) {
//        CLibrary.INSTANCE.printf("Hello, World\n");
//        System.out.println(Runtime.getRuntime().maxMemory());
        int DEFAULT_INITIAL_CAPACITY = 1 << 4;
        System.out.println(DEFAULT_INITIAL_CAPACITY);
    }
}
