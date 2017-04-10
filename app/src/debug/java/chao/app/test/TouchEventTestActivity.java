package chao.app.test;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;

import chao.app.refreshrecyclerview.R;

/**
 * @author chao.qin
 * @since 2017/3/30
 */

public class TouchEventTestActivity extends Activity {

    private static final java.lang.String TAG = TouchEventTestActivity.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.touch_test_layout);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Util.printTouchEvent(this.getClass(),event.getAction());
        return super.onTouchEvent(event);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Util.printDispatchTouchEvent(this.getClass(),ev.getAction());
        return super.dispatchTouchEvent(ev);
//        return true;
    }
}
