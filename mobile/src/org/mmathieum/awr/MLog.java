package org.mmathieum.awr;

import android.util.Log;

/**
 * This class is used to customize and optimize the log.
 *
 * @author Mathieu Méa
 */
public class MLog {

    /**
     * Use this boolean to enable full LOG (VERBOSE)
     */
    public static boolean DEBUG = false;

    /**
     * The log tag for all the logs from the app.
     */
    public static final String MAIN_TAG = "AWR";

    /**
     * @param level the level to check
     * @return true if the level is loggable
     */
    public static boolean isLoggable(int level) {
        return DEBUG || Log.isLoggable(MAIN_TAG, level);
    }

    /**
     * @param tag the class tag
     * @param msg the message
     * @see Log#v(String, String)
     */
    public static void v(String tag, String msg) {
        if (isLoggable(Log.VERBOSE)) {
            Log.v(MAIN_TAG, String.format("%s>%s", tag, msg));
        }
    }

    /**
     * @param tag  the class tag
     * @param msg  the message
     * @param args the message arguments
     * @see Log#v(String, String)
     */
    public static void v(String tag, String msg, Object... args) {
        if (isLoggable(Log.VERBOSE)) {
            Log.v(MAIN_TAG, String.format("%s>%s", tag, String.format(msg, args)));
        }
    }

    /**
     * @param tag the class tag
     * @param msg the message
     * @see Log#d(String, String)
     */
    public static void d(String tag, String msg) {
        if (isLoggable(Log.DEBUG)) {
            Log.d(MAIN_TAG, String.format("%s>%s", tag, msg));
        }
    }

    /**
     * @param tag  the class tag
     * @param msg  the message
     * @param args the message arguments
     * @see Log#d(String, String)
     */
    public static void d(String tag, String msg, Object... args) {
        if (isLoggable(Log.DEBUG)) {
            Log.d(MAIN_TAG, String.format("%s>%s", tag, String.format(msg, args)));
        }
    }

    /**
     * @param tag  the class tag
     * @param t    the error
     * @param msg  the message
     * @param args the message arguments
     * @see Log#d(String, String, Throwable)
     */
    public static void d(String tag, Throwable t, String msg, Object... args) {
        if (isLoggable(Log.DEBUG)) {
            Log.d(MAIN_TAG, String.format("%s>%s", tag, String.format(msg, args)), t);
        }
    }

    /**
     * @param tag the class log
     * @param msg the message
     * @see Log#i(String, String)
     */
    public static void i(String tag, String msg) {
        if (isLoggable(Log.INFO)) {
            Log.i(MAIN_TAG, String.format("%s>%s", tag, msg));
        }
    }

    /**
     * @param tag  the class log
     * @param msg  the message
     * @param args the message arguments
     * @see Log#i(String, String)
     */
    public static void i(String tag, String msg, Object... args) {
        if (isLoggable(Log.INFO)) {
            Log.i(MAIN_TAG, String.format("%s>%s", tag, String.format(msg, args)));
        }
    }

    /**
     * @param tag  the class tag
     * @param msg  the message
     * @param args the message arguments
     * @see Log#w(String, String)
     */
    public static void w(String tag, String msg, Object... args) {
        if (isLoggable(Log.WARN)) {
            Log.w(MAIN_TAG, String.format("%s>%s", tag, String.format(msg, args)));
        }
    }

    /**
     * @param tag  the class tag
     * @param t    the error
     * @param msg  the message
     * @param args the message arguments
     * @see Log#w(String, String, Throwable)
     */
    public static void w(String tag, Throwable t, String msg, Object... args) {
        if (isLoggable(Log.WARN)) {
            Log.w(MAIN_TAG, String.format("%s>%s", tag, String.format(msg, args)), t);
        }
    }

    /**
     * @param tag the class tag
     * @param t   the error
     * @param msg the message
     * @see Log#e(String, String, Throwable)
     */
    public static void e(String tag, Throwable t, String msg) {
        if (isLoggable(Log.ERROR)) {
            Log.e(MAIN_TAG, String.format("%s>%s", tag, msg), t);
        }
    }
}
