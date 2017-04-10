package chao.app.refreshrecyclerview.recycleview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * @author chao.qin
 * @since 2017/4/7.
 */

public class DataLinearLayoutManager extends LinearLayoutManager {

    private final boolean use = false;

    private DataRecyclerHeaderCell mHeaderCell;

    void setHeaderCell(DataRecyclerHeaderCell cell) {
        mHeaderCell = cell;
    }


    public DataLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (!use) {
            return super.scrollVerticallyBy(dy,recycler,state);
        }
        int scaledDy = mHeaderCell.computeScaledDy(dy);
        return super.scrollVerticallyBy(scaledDy,recycler,state) + dy - scaledDy;
    }
}
