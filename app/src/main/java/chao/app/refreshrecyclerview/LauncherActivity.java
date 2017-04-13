package chao.app.refreshrecyclerview;

import chao.app.uidebug.UIDebugLauncherActivity;
import chao.app.uidebug.annotations.DebugClass;
import chao.app.uidebug.annotations.DebugSwitchON;
import chao.app.uidebug.annotations.MainClass;

@DebugSwitchON(true)
@DebugClass(RefreshRecyclerViewTestFragment.class)
@MainClass(MainFragment.class)
public class LauncherActivity extends UIDebugLauncherActivity {

//    private static final Class DEBUG_CLASS = RefreshRecyclerViewTestFragment.class;
//    private static final boolean DEBUG_ENABLED = true;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        UIDebugHelper.DebugInfo info = new UIDebugHelper.DebugInfo()
//                .debugClass(DEBUG_CLASS)
//                .fromActivity(this)
//                .mainClass(MainActivity.class);
//
//        if (DEBUG_ENABLED) {
//            UIDebugHelper.enterDebugMode(info);
//        }
//    }
}
