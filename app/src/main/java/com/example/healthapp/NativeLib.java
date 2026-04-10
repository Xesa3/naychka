package com.example.healthapp;

public class NativeLib {

    static {
        System.loadLibrary("native-lib");
    }

    public static native int test();

    public static native void reliefFilter(
            byte[] input,
            byte[] output,
            int width,
            int height
    );
}
