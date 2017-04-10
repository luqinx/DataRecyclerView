package chao.app.refreshrecyclerview.recycleview;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import chao.app.protocol.LogHelper;
import chao.app.refreshrecyclerview.BuildConfig;
import chao.app.refreshrecyclerview.R;

import static chao.app.refreshrecyclerview.recycleview.DataRecyclerAdapter.statusText;

/**
 * @author chao.qin
 * @since 2017/3/28.
 */
public class DataRecyclerHeaderCell extends DataRecyclerCell {

    private static final String TAG = DataRecyclerHeaderCell.class.getSimpleName();

    private static final boolean DEBUG = false && BuildConfig.DEBUG;


    private static final int HEADER_POSITION = 0;

    private static final int REFRESH_IDLE = DataRecyclerAdapter.REFRESH_IDLE;        //下拉状态但还没到达刷新或者没有下拉
    private static final int REFRESH_REFRESHING = DataRecyclerAdapter.REFRESH_REFRESHING;  // 正在刷新
    private static final int REFRESH_FAILED = DataRecyclerAdapter.REFRESH_FAILED;  //刷新失败
    private static final int REFRESH_DONE = DataRecyclerAdapter.REFRESH_DONE;    //刷新完成
    private static final int REFRESH_EMPTY = DataRecyclerAdapter.REFRESH_EMPTY;   //数据为空
    private static final int REFRESH_PULL = DataRecyclerAdapter.REFRESH_PULL;   //数据为空
    private static final int REFRESH_CANCEL = DataRecyclerAdapter.REFRESH_CANCEL;   //数据为空
    private static final int REFRESH_PREPARE_REFRESHING = DataRecyclerAdapter.REFRESH_PREPARE_REFRESHING;


    private static final int REFRESH_STATUS_DELAY = 500;

    private TextView mText = null;
    private ProgressBar mProgressBar;

    private int mScrollY;

    private DataRecyclerView mDataRecyclerView;
    private int mStatus = REFRESH_IDLE;

    private HeaderHandler mHandler = new HeaderHandler();

    void detach() {
        mHandler.removeCallbacksAndMessages(null);
    }

    private int deltaY;
    private final Object deltaLock = new Object();

    public  int computeScaledDyLock(int dy) {
        synchronized (deltaLock) {
            if (isStatus(REFRESH_PULL) && dy < 0) {
                deltaY += (int) (dy / 2.5);
            } else {
                deltaY += dy;
            }
            return deltaY;
        }
    }

    public  int computeScaledDy(int dy) {
        deltaY = dy;
        if (isStatus(REFRESH_PULL) && dy < 0) {
            deltaY = (int) (dy / 2.5);
//            deltaY = dy;
        }
        return deltaY;
    }

    @SuppressLint("HandlerLeak")
    private class HeaderHandler extends Handler {

        private static final int WHAT_REFRESH_STARTED = 1;
        private static final int WHAT_REFRESH_PULL = 2;
        private static final int WHAT_REFRESH_FAILED = 3;
        private static final int WHAT_REFRESH_DONE = 4;
        private static final int WHAT_REFRESH_EMPTY = 5;
        private static final int WHAT_REFRESH_READY_REFRESH = 6;
        private static final int WHAT_REFRESH_IDLE = 7;


        private static final int WHAT_CLOSE_HEADER = 10;

        private void sendHeaderMessage(int what) {
            Message message = obtainMessage();
            message.what = what;
            message.sendToTarget();
        }

        private void sendHeaderMessageDelay(int what) {
            Message message = obtainMessage();
            message.what = what;
            sendEmptyMessageDelayed(what,REFRESH_STATUS_DELAY);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_REFRESH_STARTED:
                    refreshStarted();
                    break;
                case WHAT_REFRESH_PULL:
                    refreshPulling();
                    break;
                case WHAT_REFRESH_FAILED:
                    refreshFailed();
                    break;
                case WHAT_REFRESH_EMPTY:
                    refreshDataEmpty();
                    break;
                case WHAT_REFRESH_DONE:
                    refreshDone();
                    break;
                case WHAT_CLOSE_HEADER:
                    shrinkHeader(true);
                    break;
                case WHAT_REFRESH_READY_REFRESH:
                    refreshReady();
                    break;
                case WHAT_REFRESH_IDLE:
                    refreshIdle();
                    break;
            }

        }
    }

    private void shrinkHeader(boolean animation) {
        mAdapter.shrinkHeader(animation);
    }

    int idlePosition() {
        return getHeight() + 1;
    }

    int getScrollY() {
        return mScrollY;
    }

    private void refreshIdle() {
        if (idlePosition() == mScrollY) {
            return;
        }
//        mDataRecyclerView.smoothScrollBy(0,mScrollY - getHeight());
    }

    private void refreshReady() {
        mText.setText(R.string.recycler_view_ready_text);
        mProgressBar.setVisibility(View.GONE);
    }

    private void refreshStarted() {
        mText.setText(R.string.recycler_view_refreshing_text);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void refreshPulling() {
        mText.setText(R.string.recycler_view_pre_refresh_text);
        mProgressBar.setVisibility(View.INVISIBLE);
    }


    private void refreshDone() {
        mText.setText(R.string.recycler_view_refreshed_text);
        mProgressBar.setVisibility(View.INVISIBLE);
        mHandler.sendHeaderMessageDelay(HeaderHandler.WHAT_CLOSE_HEADER);
    }

    private void refreshFailed() {
        mText.setText(R.string.recycler_view_refresh_failed_text);
        mProgressBar.setVisibility(View.INVISIBLE);
        mHandler.sendHeaderMessageDelay(HeaderHandler.WHAT_CLOSE_HEADER);
        mAdapter.onRefreshFailed();
    }

    private void refreshDataEmpty() {
        mText.setText(R.string.recycler_view_refresh_empty_text);
        mProgressBar.setVisibility(View.INVISIBLE);
        mHandler.sendHeaderMessageDelay(HeaderHandler.WHAT_CLOSE_HEADER);
    }

    void setRecyclerView(DataRecyclerView recyclerView) {
        mDataRecyclerView = recyclerView;
    }

    public boolean overHeader() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mDataRecyclerView.getLayoutManager();
        int firstPosition = layoutManager.findFirstVisibleItemPosition();
        if (firstPosition == HEADER_POSITION) {
            return true;
        }
        return false;
    }

    //松手滑动到达的可滑动的最大阈值，松手后的滑动超过头部线的阈值就不再允许继续向上滑
    public boolean overFling() {
        if (overHeader() && mScrollY >= getHeight() * 3 / 4) {
            return true;
        }
        return false;

    }

    //到达头部刷线，到达这个线或再往上进入刷新状态
    public boolean overHeaderRefresh() {
        if (overHeader() && mScrollY <= getHeight() / 5) {
            return true;
        }
        return false;
    }


    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        mScrollY += dy;
//        mScrollY += deltaY;
        if (dy > 0 && mScrollY == idlePosition()) {
            onHeaderShrank();
        }
    }

    public void onScrolledLock(RecyclerView recyclerView, int dx, int dy) {
//        mScrollY += dy;
        synchronized (deltaLock) {
            mScrollY += deltaY;
            deltaY = 0;
            if (dy > 0 && mScrollY == idlePosition()) {
                onHeaderShrank();
            }
        }
    }

    private void onHeaderShrank() {
        mAdapter.resizeFooterView();
    }

    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if (!overHeader()) {
            return;
        }
        switch (newState) {
            case RecyclerView.SCROLL_STATE_IDLE:
                if (isStatus(REFRESH_PULL | REFRESH_DONE | REFRESH_FAILED | REFRESH_EMPTY | REFRESH_CANCEL)) {
                    mHandler.sendHeaderMessage(HeaderHandler.WHAT_CLOSE_HEADER);
                } else if (isStatus(REFRESH_PREPARE_REFRESHING)) {
                    mAdapter.readyForRefresh(true);
                }
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
                break;
        }

    }

    private boolean isStatus(int status) {
        if (DEBUG) {
            LogHelper.d(TAG, "current status : " + statusText(mStatus));
        }
        return (mStatus & status) != 0;
    }

    public void refreshStatusChanged(int newStatus) {
        mStatus = newStatus;
        switch (newStatus) {
            case REFRESH_PULL:
                mHandler.sendHeaderMessage(HeaderHandler.WHAT_REFRESH_PULL);
                break;
            case REFRESH_REFRESHING:
                mHandler.sendHeaderMessage(HeaderHandler.WHAT_REFRESH_STARTED);
                break;
            case REFRESH_DONE:
                mHandler.sendHeaderMessage(HeaderHandler.WHAT_REFRESH_DONE);
                break;
            case REFRESH_FAILED:
                mHandler.sendHeaderMessage(HeaderHandler.WHAT_REFRESH_FAILED);
                break;
            case REFRESH_EMPTY:
                mHandler.sendHeaderMessage(HeaderHandler.WHAT_REFRESH_EMPTY);
                break;
            case REFRESH_PREPARE_REFRESHING:
                mHandler.sendHeaderMessage(HeaderHandler.WHAT_REFRESH_READY_REFRESH);
                break;
            case REFRESH_IDLE:
                mHandler.sendHeaderMessage(HeaderHandler.WHAT_REFRESH_IDLE);
                break;
            case REFRESH_CANCEL:
                break;
            default:
                throw new IllegalStateException("unknown refresh status. " + statusText(newStatus));
        }
    }

    public boolean atTop(){
        if (mScrollY <= 0) {
            mScrollY = 0;
            return true;
        }
        return false;
    }



    @Override
    public final int getCellViewLayoutID() {
        return R.layout.recycler_view_refresh_header;
    }

    @Override
    public final void bindView() {
        mText = findViewById(R.id.progress_message);
        mProgressBar = findViewById(R.id.progress_bar);
    }

    @Override
    public final void bindData() {
        refreshPulling();
    }
}
