package com.dream.fmodl;

/**
 * Created by dream on 2017/5/27.
 */

public class FmodUtils {
    public static final int MODE_NORMAL = 0;
    public static final int MODE_LUOLI = 1;
    public static final int MODE_DASHU = 2;
    public static final int MODE_JINGSONG = 3;
    public static final int MODE_GAOGUAI = 4;
    public static final int MODE_KONGLING = 5;

    /**
     *
     *
     * @param path
     * @param type
     */
    public native static void fix(String path, int type);
    public native static void record(String output);

    static {
        System.loadLibrary("fmodL");
        System.loadLibrary("fmod");
        System.loadLibrary("dream");
    }

}
