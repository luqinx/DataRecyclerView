package chao.app.refreshrecyclerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import chao.app.protocol.UIDebugHelper;

public class MainActivity extends AppCompatActivity {

    private static final Class DEBUG_CLASS = RefreshRecyclerViewTestFragment.class;
    private static final boolean DEBUG_ENABLED = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UIDebugHelper.DebugInfo info = new UIDebugHelper.DebugInfo()
                .debugClass(DEBUG_CLASS)
                .fromActivity(this)
                .mainClass(MainActivity.class);

        if (DEBUG_ENABLED) {
            UIDebugHelper.enterDebugMode(info);
        }
    }
}
