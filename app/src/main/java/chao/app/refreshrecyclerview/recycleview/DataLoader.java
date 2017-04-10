package chao.app.refreshrecyclerview.recycleview;


import chao.app.refreshrecyclerview.recycleview.data.DataItemResult;

/**
 * DataListView 网络数据加载约定
 */
public abstract class DataLoader {

    public void onPreFetch() {}

    public abstract DataItemResult fetchData(DataRecyclerAdapter adapter, int pageAt, int pageSize);

    public void onFetchDone(DataItemResult result) {}
}
