package chao.app.test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import chao.app.refreshrecyclerview.R;

/**
 * @author chao.qin
 * @since 2017/3/30
 */

public class FatherLayout extends RelativeLayout {

    private String mFatherName = "second";

    public FatherLayout(Context context) {
        super(context);
    }

    public FatherLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        int id = getId();
        if (id == R.id.first_father) {
            mFatherName = "first";
        }
    }

    public FatherLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        int id = getId();
        if (id == R.id.first_father) {
            mFatherName = "first";
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Util.printInterceptTouchEvent(this.getClass(),ev.getAction(),mFatherName);
//        return super.onInterceptTouchEvent(ev);
//        return true;
        if (isFirst()) {
            return true;
        }
        return false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Util.printTouchEvent(this.getClass(),event.getAction(),mFatherName);
        return super.onTouchEvent(event);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Util.printDispatchTouchEvent(this.getClass(),ev.getAction(),mFatherName);
        return super.dispatchTouchEvent(ev);
    }

    boolean isFirst() {
        return getId() == R.id.first_father;
    }
}
