package chao.app.test;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.MotionEvent;

import chao.app.protocol.LogHelper;

/**
 * @author chao.qin
 * @since 2017/3/30
 */

public class Util {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void printTouchEvent(Class clazz, int action) {
        LogHelper.i(clazz.getSimpleName(),"touch", MotionEvent.actionToString(action));
    }

    public static void printInterceptTouchEvent(Class clazz, int action) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            LogHelper.i(clazz.getSimpleName(), "intercept",MotionEvent.actionToString(action));
        }
    }

    public static void printDispatchTouchEvent(Class clazz, int action) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            LogHelper.i(clazz.getSimpleName(),"dispatch", MotionEvent.actionToString(action));
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void printTouchEvent(Class clazz, int action,String name) {
        LogHelper.i(clazz.getSimpleName(),"touch",name, MotionEvent.actionToString(action));
    }

    public static void printInterceptTouchEvent(Class clazz, int action,String name) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            LogHelper.i(clazz.getSimpleName(), "intercept",name,MotionEvent.actionToString(action));
        }
    }

    public static void printDispatchTouchEvent(Class clazz, int action,String name) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            LogHelper.i(clazz.getSimpleName(),"dispatch",name, MotionEvent.actionToString(action));
        }
    }
}
