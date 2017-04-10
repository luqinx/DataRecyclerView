package chao.app.refreshrecyclerview.recycleview;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jobs.lib_v1.app.AppUtil;
import com.jobs.lib_v1.data.DataItemDetail;
import com.jobs.lib_v1.device.DeviceUtil;

/**
 * 单元格复用抽象类：视图、数据和ViewHolder的合集。
 *
 * 1.继承和使用时的重要约束：
 * 这个类的子类的构造方法不允许带参数，否则将不会被调用。
 * 其子类可以是独立类、静态化类或在 DataListView/DataListAdapter/Activity/DataListCellSelector 对象中写的内部类。
 * 其他对象中的单元格内部类在指定时需要把对象作为默认实例化参数传进去，详见 DataListView 的 setMoreCellClass/setDataCellSelector/setDataCellClass/setErrorCellClass/setEmptyCellClass 等方法的说明。
 *
 * 2.子类中需要实现的方法为：
 * (1) createCellView 或 getCellViewLayoutID (只会调用一次)
 * (2) bindView (只会调用一次)
 * (3) bindData
 *
 * 2.创建时方法的调用顺序：
 * initAdapterAndCellViewForOnce
 * ->updateCellData
 * ->createCellView/getCellViewLayoutID
 * ->bindView
 * ->bindData
 *
 * 3.复用时方法的调用顺序：
 * updateCellData
 * ->bindData
 *
 * @author solomon.wen
 * @since 2013-12-18
 */
public abstract class DataRecyclerCell {
    /**
     * 单元格所在 DataListView 对应的 adapter
     * 这个值在 bindData 和  bindView 方法调用时，是不可能为空的
     */
    protected DataRecyclerAdapter mAdapter;

    /**
     * 单元格对应的View
     * 这个值在  bindData 和  bindView 方法调用时，是不可能为空的。
     *
     * 这个变量我设成私有的了，禁止子类直接访问；findViewById 方法可以直接用本类的
     */
    private View mCellView;

    /**
     * 单元格对应的位置。
     * 这个值因为单元格复用的关系，其值会变化。
     * 但是在 bindData 调用前，其值会被预先设置好，所以在 bindData 方法中它可以放心使用。
     */
    protected int mPosition;

    /**
     * 单元格对应的数据。
     * 这个值因为单元格复用的关系，其值会变化。
     * 但是在 bindData 调用前，其值会被预先设置好，所以在 bindData 方法中它可以放心使用。
     */
    protected DataItemDetail mDetail;

    /**
     * 初始化单元格和 adapter 的关系，创建单元格视图
     *
     * @param adapter 单元格对应的 adapter
     */
    public final void initAdapterAndCellViewForOnce(DataRecyclerAdapter adapter){
        // 初始化 adapter 和数据
        mAdapter = adapter;


        // 创建单元格视图
        try {
            int cellID = getCellViewLayoutID();
            if(cellID != 0){
                mCellView = LayoutInflater.from(adapter.getContext()).inflate(cellID, adapter.getRecyclerView(),false);
            } else {
                mCellView = createCellView();
            }
        } catch (Throwable e) {
            AppUtil.print(e);
        }

        // 创建单元格视图失败则会再创建一个默认单元格视图
        if(null == mCellView){
            mCellView = createDefaultCellView();
        }

        // 设置单元格关系
        mCellView.setTag(this);
    }

    /**
     * 更新单元格对应的数据
     *
     * @param position 单元格的位置
     */
    public final void updateCellData(int position){
        mPosition = position;
        mDetail = mAdapter.getItem(position);
    }

    /**
     * 创建默认单元格视图
     *
     * @return View 返回一个默认单元格
     */
    private final View createDefaultCellView(){
        LinearLayout rootLayout = new LinearLayout(mAdapter.getContext());

        ListView.LayoutParams rootParams = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT);
        rootLayout.setLayoutParams(rootParams);
        rootLayout.setGravity(Gravity.TOP);
        rootLayout.setBackgroundColor(Color.parseColor("#E5E5E5"));

        TextView textViewLayout = new TextView(mAdapter.getContext());
        ViewGroup.LayoutParams textViewParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int textViewPadding = DeviceUtil.dip2px(20);
        textViewLayout.setLayoutParams(textViewParams);
        textViewLayout.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        textViewLayout.setPadding(textViewPadding, textViewPadding, textViewPadding, textViewPadding);
        textViewLayout.setTextColor(ColorStateList.valueOf(Color.parseColor("#FF0000")));
        textViewLayout.setTextSize(14);
        textViewLayout.setText("Default cell view.");

        rootLayout.addView(textViewLayout);

        return rootLayout;
    }

    /**
     * 返回单元格视图
     *
     * @return View
     */
    public View getCellView(){
        return mCellView;
    }

    /**
     * 通过控件ID来查找单元格上的控件
     *
     * @param id 控件ID
     * @return View
     */
    public final <T extends View> T findViewById(int id){
        return (T)mCellView.findViewById(id);
    }

    /**
     * 创建单元格视图
     * 该方法由子类实现；createCellView 和 getCellViewLayoutID 必须实现一个
     * 只有在 getCellViewLayoutID 方法返回0时，才会调用 createCellView
     */
    public View createCellView(){
        return null;
    }

    /**
     * 获取单元格对应的 layoutID
     * 该方法由子类实现；createCellView 和 getCellViewLayoutID 必须实现一个
     * getCellViewLayoutID 方法返回0时会调用 createCellView
     */
    public int getCellViewLayoutID() {
        return 0;
    }

    /**
     * 绑定单元格视图中的控件到变量
     * 该方法由子类实现
     */
    public abstract void bindView();

    /**
     * 绑定单元格数据到控件
     * 该方法由子类实现
     */
    public abstract void bindData();

    /**
     * 获取cell高度
     */
    public int getHeight() {
//        if (mCellView == null) {
//            return 0;
//        }
        return mCellView.getHeight();
    }

    void setHeight(int height) {
        ViewGroup.LayoutParams lp = mCellView.getLayoutParams();
        lp.height = height;
//        mCellView.requestLayout();
        mCellView.invalidate();
    }
}

