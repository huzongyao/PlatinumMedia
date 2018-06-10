package com.plutinosoft.platinum;

/**
 * Created by huzongyao on 2018/6/10.
 */

public class NtpResult {

    public static final int NPT_SUCCESS = 0;
    public static final int NPT_FAILURE = -1;

    public static final int NPT_ERROR_BASE = -20000;
    // error bases
    public static final int NPT_ERROR_BASE_GENERAL = NPT_ERROR_BASE;
    public static final int NPT_ERROR_BASE_LIST = NPT_ERROR_BASE - 100;
    public static final int NPT_ERROR_BASE_FILE = NPT_ERROR_BASE - 200;
    public static final int NPT_ERROR_BASE_IO = NPT_ERROR_BASE - 300;
    public static final int NPT_ERROR_BASE_SOCKET = NPT_ERROR_BASE - 400;
    public static final int NPT_ERROR_BASE_INTERFACES = NPT_ERROR_BASE - 500;
    public static final int NPT_ERROR_BASE_XML = NPT_ERROR_BASE - 600;
    public static final int NPT_ERROR_BASE_UNIX = NPT_ERROR_BASE - 700;
    public static final int NPT_ERROR_BASE_HTTP = NPT_ERROR_BASE - 800;
    public static final int NPT_ERROR_BASE_THREADS = NPT_ERROR_BASE - 900;
    public static final int NPT_ERROR_BASE_SERIAL_PORT = NPT_ERROR_BASE - 1000;
    public static final int NPT_ERROR_BASE_TLS = NPT_ERROR_BASE - 1100;

    // general errors
    public static final int NPT_ERROR_INVALID_PARAMETERS = NPT_ERROR_BASE_GENERAL;
    public static final int NPT_ERROR_PERMISSION_DENIED = NPT_ERROR_BASE_GENERAL - 1;
    public static final int NPT_ERROR_OUT_OF_MEMORY = NPT_ERROR_BASE_GENERAL - 2;
    public static final int NPT_ERROR_NO_SUCH_NAME = NPT_ERROR_BASE_GENERAL - 3;
    public static final int NPT_ERROR_NO_SUCH_PROPERTY = NPT_ERROR_BASE_GENERAL - 4;
    public static final int NPT_ERROR_NO_SUCH_ITEM = NPT_ERROR_BASE_GENERAL - 5;
    public static final int NPT_ERROR_NO_SUCH_CLASS = NPT_ERROR_BASE_GENERAL - 6;
    public static final int NPT_ERROR_OVERFLOW = NPT_ERROR_BASE_GENERAL - 7;
    public static final int NPT_ERROR_INTERNAL = NPT_ERROR_BASE_GENERAL - 8;
    public static final int NPT_ERROR_INVALID_STATE = NPT_ERROR_BASE_GENERAL - 9;
    public static final int NPT_ERROR_INVALID_FORMAT = NPT_ERROR_BASE_GENERAL - 10;
    public static final int NPT_ERROR_INVALID_SYNTAX = NPT_ERROR_BASE_GENERAL - 11;
    public static final int NPT_ERROR_NOT_IMPLEMENTED = NPT_ERROR_BASE_GENERAL - 12;
    public static final int NPT_ERROR_NOT_SUPPORTED = NPT_ERROR_BASE_GENERAL - 13;
    public static final int NPT_ERROR_TIMEOUT = NPT_ERROR_BASE_GENERAL - 14;
    public static final int NPT_ERROR_WOULD_BLOCK = NPT_ERROR_BASE_GENERAL - 15;
    public static final int NPT_ERROR_TERMINATED = NPT_ERROR_BASE_GENERAL - 16;
    public static final int NPT_ERROR_OUT_OF_RANGE = NPT_ERROR_BASE_GENERAL - 17;
    public static final int NPT_ERROR_OUT_OF_RESOURCES = NPT_ERROR_BASE_GENERAL - 18;
    public static final int NPT_ERROR_NOT_ENOUGH_SPACE = NPT_ERROR_BASE_GENERAL - 19;
    public static final int NPT_ERROR_INTERRUPTED = NPT_ERROR_BASE_GENERAL - 20;
    public static final int NPT_ERROR_CANCELLED = NPT_ERROR_BASE_GENERAL - 21;

    /* standard error codes                                  */
    /* these are special codes to convey an errno            */
    /* the error code is (SHI_ERROR_BASE_ERRNO - errno)      */
    /* where errno is the positive integer from errno.h      */
    public static final int NPT_ERROR_BASE_ERRNO = NPT_ERROR_BASE - 2000;

    public static int NPT_ERROR_ERRNO(int e) {
        return NPT_ERROR_BASE_ERRNO - e;
    }
}
