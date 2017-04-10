package chao.app.refreshrecyclerview;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.data.DataItemResult;

import chao.app.protocol.LogHelper;
import chao.app.refreshrecyclerview.recycleview.DataLoader;
import chao.app.refreshrecyclerview.recycleview.DataRecyclerAdapter;
import chao.app.refreshrecyclerview.recycleview.DataRecyclerCell;
import chao.app.refreshrecyclerview.recycleview.DataRecyclerView;
import chao.app.refreshrecyclerview.recycleview.OnItemClickListener;

/**
 * @author chao.qin
 * @since 2017/3/29.
 */

public class RefreshRecyclerViewTestFragment extends Fragment {

    private static final long NETWORK_DELAY = 500;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.refresh_recyclerview_fragment,container,false);
        final DataRecyclerView recyclerView = (DataRecyclerView) view.findViewById(R.id.data_recycler_view);
        recyclerView.setDataRecyclerCell(TestCell.class,this);
        DataItemDetail detail = new DataItemDetail();
        DataItemResult result = new DataItemResult();

//        for (int i = 0; i < 10 ; i++) {
//            DataItemDetail itemDetail = detail.Copy();
//            itemDetail.setIntValue("No.", i);
//            itemDetail.setStringValue("content", "This is a Recycler Test.");
//            result.addItem(itemDetail);
//        }
//
//        recyclerView.appendData(result);
        recyclerView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClickListener(RecyclerView recyclerView, View view, int position) {
                LogHelper.i("chao.qin","position : "+position);
            }
        });
        recyclerView.setDataLoader(new DataLoader() {
            @Override
            public DataItemResult fetchData(DataRecyclerAdapter adapter, int pageAt, int pageSize) {
                SystemClock.sleep(NETWORK_DELAY);
                DataItemDetail detail = new DataItemDetail();
                DataItemResult result = new DataItemResult();
                result.maxCount = 4;
                pageSize = 30;

//                int randNumber = (int) (3 * Math.random());
//                if (randNumber % 3 == 0) {
//                    result.hasError = true;
//                } else if (randNumber % 3 == 1) {
//                    return result;
//                }

                for (int i = 0; i < Math.min(result.maxCount,pageSize); i++) {
                    DataItemDetail itemDetail = detail.Copy();
                    itemDetail.setIntValue("No.", ((pageAt - 1) * pageSize) + i);
                    itemDetail.setStringValue("content", "This is a Recycler Test.");
                    result.addItem(itemDetail);
                }
                return result;
            }
        });
        return view;
    }

    private class TestCell extends DataRecyclerCell {
        TextView textView;
        @Override
        public void bindView() {
            textView = findViewById(R.id.content);
        }

        @Override
        public void bindData() {
            int no = mDetail.getInt("No.");
            String content = mDetail.getString("content");
            textView.setText(no + ". " + content);
        }

        @Override
        public int getCellViewLayoutID() {
            return R.layout.test_cell;
        }
    }
}
