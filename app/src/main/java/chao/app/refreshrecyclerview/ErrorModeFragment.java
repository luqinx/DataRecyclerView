package chao.app.refreshrecyclerview;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import chao.app.protocol.UIDebugHelper;
import chao.app.refreshrecyclerview.recycleview.DataLoader;
import chao.app.refreshrecyclerview.recycleview.DataRecyclerAdapter;
import chao.app.refreshrecyclerview.recycleview.DataRecyclerCell;
import chao.app.refreshrecyclerview.recycleview.DataRecyclerView;
import chao.app.refreshrecyclerview.recycleview.OnItemClickListener;
import chao.app.refreshrecyclerview.recycleview.data.DataItemDetail;
import chao.app.refreshrecyclerview.recycleview.data.DataItemResult;

/**
 * @author chao.qin
 * @since 2017/4/19
 */

public class ErrorModeFragment extends Fragment implements OnItemClickListener {
    private static final long NETWORK_DELAY = 3000;
    private DataRecyclerView mRecyclerView;

    public static void show(Context context) {
        UIDebugHelper.showUI(context,ErrorModeFragment.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_fragment,container,false);
        mRecyclerView = (DataRecyclerView) view.findViewById(R.id.recycler_view);
//        mRecyclerView.setDataRecyclerCell(StandardModeCell.class); 如果是内部类，使用下面带第二个参数的setDataRecyclerCell方法，指定创建它的外部类对象
        mRecyclerView.setDataRecyclerCell(StandardModeCell.class,this);
        mRecyclerView.setDataLoader(new DataLoader() {
            @Override
            public DataItemResult fetchData(DataRecyclerAdapter adapter, int pageAt, int pageSize) {
                SystemClock.sleep(NETWORK_DELAY);

                //以下是模拟网络、数据库或其他方式获取数据并以DataItemResult返回
                DataItemDetail detail = new DataItemDetail();//相当于map
                DataItemResult result = new DataItemResult();//相当于一个map集合
                result.maxCount = 400;
                pageSize = 6;

                int random = (int) (Math.random() * 10);
                if (random % 3 == 0) {
                    result.hasError = true;
                    result.message = "This is a error test.";
                    return result;
                }


                for (int i = 0; i < Math.min(result.maxCount,pageSize); i++) {
                    DataItemDetail itemDetail = detail.Copy();
                    itemDetail.setIntValue("No.", ((pageAt - 1) * pageSize) + i);
                    itemDetail.setStringValue("content", "This is a Recycler Test.");
                    result.addItem(itemDetail);
                }
                return result;
            }
        });
        mRecyclerView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onItemClickListener(RecyclerView recyclerView, View view, int position) {
        DataItemResult result  = mRecyclerView.getDataList();
        DataItemDetail detail = result.getItem(position);

        Toast.makeText(getActivity(),position + ". " + detail.getString("content"),Toast.LENGTH_SHORT).show();
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
