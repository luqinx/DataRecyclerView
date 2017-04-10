package chao.app.refreshrecyclerview.recycleview;

import android.view.View;

/**
 */

public interface OnItemEmptyClickListener {
    /**
     *
     * @return return true消费点击事件，false让DataRecyclerView继续处理点击事件
     */
    boolean onItemEmptyClickListener(View view);
}
