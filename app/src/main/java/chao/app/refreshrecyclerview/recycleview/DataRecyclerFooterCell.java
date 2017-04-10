package chao.app.refreshrecyclerview.recycleview;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import chao.app.protocol.LogHelper;
import chao.app.refreshrecyclerview.R;

/**
 * @author chao.qin
 * @since 2017/3/31
 */

public class DataRecyclerFooterCell extends DataRecyclerCell {

    private static final String TAG = "DataRecyclerFooterCell";

    private static final int OVER_SCROLLER_DISTANCE = DataRecyclerAdapter.OVER_SCROLLER_DISTANCE;  //px

    private static final int IDLE = DataRecyclerAdapter.IDLE;              //  0000001
    private static final int LOADING = DataRecyclerAdapter.LOADING;     //  0000010
    private static final int EMPTY = DataRecyclerAdapter.EMPTY;       //  0000100
    private static final int ERROR = DataRecyclerAdapter.ERROR;        //  0001000
    private static final int MORE = DataRecyclerAdapter.MORE;         //  0010000

    private static final int DEFAULT_FOOTER_VIEW_HEIGHT = 40;

    private TextView mLoadMessageView;
    private ProgressBar mProgressBar;

    private View mContentView;

    private LinearLayout mContainer;

    private LoadHandler mHandler = new LoadHandler();

    public void detach() {
        mHandler.removeCallbacksAndMessages(null);
    }

    private class LoadHandler extends Handler {

        private static final int WHAT_LOAD_EMPTY = 1;
        private static final int WHAT_LOAD_ERROR = 2;
        private static final int WHAT_LOAD_MORE = 3;
        private static final int WHAT_LOAD_LOADING = 4;
        private static final int WHAT_LOAD_IDLE = 5;

        private void sendLoadMessage(int what) {
            Message msg = obtainMessage();
            msg.what = what;
            msg.sendToTarget();
        }


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_LOAD_EMPTY:
                    showLoadEmpty();
                    break;
                case WHAT_LOAD_ERROR:
                    showLoadError();
                    break;
                case WHAT_LOAD_IDLE:
                    showLoadIdle();
                    break;
                case WHAT_LOAD_LOADING:
                    showLoading();
                    break;
                case WHAT_LOAD_MORE:
                    showLoadMore();
                    break;
            }
        }
    }

    private void showLoadIdle() {
        mProgressBar.setVisibility(View.GONE);
        mLoadMessageView.setVisibility(View.GONE);
    }

    private void showLoadMore() {
        mProgressBar.setVisibility(View.GONE);
        mLoadMessageView.setVisibility(View.VISIBLE);
        mLoadMessageView.setText(R.string.recycler_view_load_more_text);
    }

    private void showLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
        mLoadMessageView.setVisibility(View.VISIBLE);
        mLoadMessageView.setText(R.string.recycler_view_loading_text);
    }

    private void showLoadError() {
        mProgressBar.setVisibility(View.GONE);
        String errorMessage = mAdapter.getRecyclerData().message;
        if (TextUtils.isEmpty(errorMessage)) {
            errorMessage = mAdapter.getContext().getString(R.string.recycler_view_load_failed_text);
        }
        mLoadMessageView.setVisibility(View.VISIBLE);
        mLoadMessageView.setText(errorMessage);
    }

    private void showLoadEmpty() {
        mProgressBar.setVisibility(View.GONE);
        mLoadMessageView.setVisibility(View.VISIBLE);
        mLoadMessageView.setText(mAdapter.getRecyclerData().message);
    }


    @Override
    public void bindView() {
        mLoadMessageView = findViewById(R.id.progress_message);
        mProgressBar = findViewById(R.id.progress_bar);
    }

    @Override
    public void bindData() {

    }

    @Override
    public int getCellViewLayoutID() {
        return R.layout.recycler_view_load_footer;
    }

    @Override
    public View getCellView() {
        mContentView = super.getCellView();
        if (mContentView == null) {
            return null;
        }
        if (mContainer != null) {
            return mContainer;
        }
        mContainer = new LinearLayout(mAdapter.getContext());
        mContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getDefaultFooterHeight()));
//        mContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
//        mContainer.setBackgroundColor(Color.RED);
        mContainer.setMinimumHeight(OVER_SCROLLER_DISTANCE);
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
//        mContentView.setBackgroundColor(Color.BLUE);
        mContainer.addView(mContentView);

        mContainer.setTag(this);
        return mContainer;
    }

    @Override
    void setHeight(int height) {
        if (mContainer.getHeight() == height) {
            return;
        }
//        LogHelper.i(TAG,"setFooterHeight : " + height);
        ViewGroup.LayoutParams lp = mContainer.getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }

    void setAdviceHeight(int height) {
        int oldHeight = mContainer.getHeight();
        int minHeight = getDefaultFooterHeight();
//        int minHeight = mContentView.getHeight() + OVER_SCROLLER_DISTANCE;
        height = Math.max(height,minHeight);
        //todo 这个地方有点奇怪，oldHeight和实际显示的值不一致。需要重新设置height
//        if (height == oldHeight) {
//            return;
//        }
        LogHelper.i(TAG,"advice setFooterHeight " + height,"oldHeight " + oldHeight);
        ViewGroup.LayoutParams lp = mContainer.getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }

    private int getDefaultFooterHeight() {
        float density = mContainer.getResources().getDisplayMetrics().density ;
        return (int) ((density * DEFAULT_FOOTER_VIEW_HEIGHT + 0.5f) + OVER_SCROLLER_DISTANCE);
    }


    public void loadStatusChanged(int newStatus) {

        switch (newStatus) {
            case IDLE:
                mHandler.sendLoadMessage(LoadHandler.WHAT_LOAD_IDLE);
                break;
            case ERROR:
                mHandler.sendLoadMessage(LoadHandler.WHAT_LOAD_ERROR);
                break;
            case EMPTY:
                mHandler.sendLoadMessage(LoadHandler.WHAT_LOAD_EMPTY);
                break;
            case LOADING:
                mHandler.sendLoadMessage(LoadHandler.WHAT_LOAD_LOADING);
                break;
            case MORE:
                mHandler.sendLoadMessage(LoadHandler.WHAT_LOAD_MORE);
                break;

        }
    }
}
