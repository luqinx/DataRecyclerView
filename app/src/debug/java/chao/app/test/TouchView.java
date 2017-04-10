package chao.app.test;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author chao.qin
 * @since 2017/3/30
 */

public class TouchView extends View {
    private static final java.lang.String TAG = TouchView.class.getSimpleName();

    public TouchView(Context context) {
        super(context);
    }

    public TouchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Util.printTouchEvent(this.getClass(),event.getAction());
//        return super.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return true;
        }
        return false;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Util.printDispatchTouchEvent(this.getClass(),ev.getAction());
        return super.dispatchTouchEvent(ev);
//        return true;
    }
}
