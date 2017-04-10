package chao.app.refreshrecyclerview.recycleview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by k.huang on 2017/3/16.
 */

public interface OnItemClickListener {
    void onItemClickListener(RecyclerView recyclerView, View view, int position);
}
