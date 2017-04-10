package chao.app.refreshrecyclerview.recycleview;

/**
 * @author chao.qin
 * @since 2017/3/7.
 */

public abstract class DataRecyclerCellSelector {
    /**
     * 当前单元格选择器中所有的类名清单
     */
    public final Class<?> mCellClasses[] = getCellClasses();

    /**
     * 当前单元格选择器中单元格类型总数
     * @return int
     */
    public final int getCellTypeCount(){
        return mCellClasses.length;
    }

    /**
     * 获取指定位置的单元格对应的类型编号
     *
     * @param adapter 单元格对应的数据适配器
     * @param position 单元格在 ListView 中的位置
     * @return int 单元格对应的类型编号
     */
    public final int getCellType(DataRecyclerAdapter adapter, int position){
        Class<?> cls = getCellClass(adapter, position);

        for(int i=0; i<mCellClasses.length;i++){
            if(mCellClasses[i].equals(cls)){
                return i;
            }
        }

        return 0;
    }

    /**
     * 返回单元格选择器中所有的类名清单 (由子类是实现)
     * 该方法只会在单元格类名选择器初始化时被调用一次。
     *
     * @return Class<>[] 单元格选择器中所有的类名清单
     */
    protected abstract Class<?>[] getCellClasses();

    /**
     * 返回指定位置单元格对应的类名
     * 返回的类名必须在 mCellClasses 中有对应的值
     *
     * @param adapter 单元格对应的数据适配器
     * @return Class<> 单元格对应的类名
     */
    public abstract Class<?> getCellClass(DataRecyclerAdapter adapter, int position);
}
