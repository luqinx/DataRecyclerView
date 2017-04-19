package chao.app.refreshrecyclerview;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import chao.app.protocol.UIDebugHelper;
import chao.app.refreshrecyclerview.recycleview.DataRecyclerCell;
import chao.app.refreshrecyclerview.recycleview.DataRecyclerView;
import chao.app.refreshrecyclerview.recycleview.OnItemClickListener;
import chao.app.refreshrecyclerview.recycleview.data.DataItemDetail;
import chao.app.refreshrecyclerview.recycleview.data.DataItemResult;

/**
 *
 * 不使用上拉加载和下拉刷新
 *
 * @author chao.qin
 * @since 2017/4/19
 */

public class ForbiddenModeFragment extends Fragment implements OnItemClickListener {
    private DataRecyclerView mRecyclerView;

    public static void show(Context context) {
        UIDebugHelper.showUI(context, ForbiddenModeFragment.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_fragment, container, false);
        mRecyclerView = (DataRecyclerView) view.findViewById(R.id.recycler_view);
//        mRecyclerView.setDataRecyclerCell(StandardModeCell.class); 如果是内部类，使用下面带第二个参数的setDataRecyclerCell方法，指定创建它的外部类对象
        mRecyclerView.setDataRecyclerCell(StandardModeCell.class, this);

        mRecyclerView.setOnItemClickListener(this);
        mRecyclerView.appendData(recyclerData());
        return view;
    }

    //构建一个DataItemResult
    private DataItemResult recyclerData() {
        DataItemDetail detail = new DataItemDetail();//相当于map
        DataItemResult result = new DataItemResult();//相当于一个map集合


        for (int i = 0; i < 80 ; i++) {
            DataItemDetail itemDetail = detail.Copy();
            itemDetail.setIntValue("No.",  + i);
            itemDetail.setStringValue("content", "This is a Recycler Test.");
            result.addItem(itemDetail);
        }
        return result;
    }


    @Override
    public void onItemClickListener(RecyclerView recyclerView, View view, int position) {
        DataItemResult result = mRecyclerView.getDataList();
        DataItemDetail detail = result.getItem(position);

        Toast.makeText(getActivity(), position + ". " + detail.getString("content"), Toast.LENGTH_SHORT).show();
    }

    public class StandardModeCell extends DataRecyclerCell {
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
