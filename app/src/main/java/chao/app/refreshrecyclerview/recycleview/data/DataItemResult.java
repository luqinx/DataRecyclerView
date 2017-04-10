package chao.app.refreshrecyclerview.recycleview.data;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 列表数据容器
 *
 * @author solomon.wen
 * @since 2011-12-6
 * 1.支持序列化和反序列化
 * 2.属性 resultCode 和 statusCode，对应51job接口XML节点的result值和status值
 * 3.属性 maxCount 对应51job接口XML节点的jobcount或maxcount值
 * 4.属性hasError表示是否出现错误
 * 5.属性parseError表示是否本地数据解析出错
 * 6.属性errorMessage存放可能的出错信息，这个信息可能是本地信息，也可能是服务器上返回的信息
 * 7.警告：不要随意给本类新增成员变量，否则会影响数据的兼容性；
 * 8.警告：不要随意修改本类成员方法的返回值，否则会引起一系列出错的连锁反应
 */
@SuppressLint("UseSparseArrays")
public class DataItemResult implements Parcelable, Iterable<DataItemDetail> {
    private static final String TAG = DataItemResult.class.getSimpleName();

    private List<DataItemDetail> dataList;
    private Map<Integer, String> adapterSetting;
    private int resultCode = 0; // 根据新版接口特性：把 resultCode
    // 置成私有，此值将不再被外部使用，也不会用于判断，目前仅作兼容旧版序列化和反序列化所用 /
    // 2011.12.27 by solomon.wen

    private String itemUniqueKeyName = "";
    private String debugInfo = "";
    private int index = 0;


    public DataItemDetail detailInfo;
    public int statusCode = 0;
    public int maxCount = 0;

    public boolean parseError = false;
    public boolean hasError = false;
    public boolean localError = false; //未请求到接口数据时设置为true

    public String message = "";

    /**
     * 构造函数，初始化列表容器，详细信息容器，数据适配器容器
     */
    public DataItemResult() {
        this.dataList = new ArrayList<>();
        this.detailInfo = new DataItemDetail();
        this.adapterSetting = new HashMap<>();
    }

    public DataItemResult Copy() {
        DataItemResult result = new DataItemResult();

        result.resultCode = resultCode;
        result.itemUniqueKeyName = itemUniqueKeyName;
        result.debugInfo = debugInfo;
        result.detailInfo = detailInfo.Copy();
        result.statusCode = statusCode;
        result.maxCount = maxCount;
        result.parseError = parseError;
        result.hasError = hasError;
        result.message = message;

        result.dataList = new ArrayList<>();
        result.adapterSetting = new HashMap<>();

        for (Integer adapterKey : adapterSetting.keySet()) {
            result.adapterSetting.put(adapterKey, adapterSetting.get(adapterKey));
        }

        for (DataItemDetail dataItem : dataList) {
            result.dataList.add(dataItem.Copy());
        }

        return result;
    }


    /**
     * 获取adapter绑定的哈希表
     *
     * @return Map<Integer,String>
     */
    public Map<Integer, String> getAdaperSetting() {
        return this.adapterSetting;
    }


    /**
     * 绑定item的对应键的值到layout的ID
     *
     * @return String
     */
    public String bindItemKey(int layoutItemElementID, String itemKey) {
        return this.adapterSetting.put(layoutItemElementID, itemKey);
    }

    /**
     * 绑定item的对应键数组的值到layout的ID列表
     */
    public void bindItemKeys(int[] layoutItemElementIDList, String[] itemKeys) {
        for (int i = 0; i < itemKeys.length; i++) {
            this.adapterSetting.put(layoutItemElementIDList[i], itemKeys[i]);
        }
    }

    /**
     * 获取item的唯一值
     */
    public int getItemID(int index) {
        if (null == itemUniqueKeyName || itemUniqueKeyName.length() < 1) {
            return index;
        }

        DataItemDetail item = getItem(index);

        if (item == null) {
            return index;
        }

        return item.getInt(itemUniqueKeyName);
    }

    /**
     * 给定一个页码，计算当前总页数
     *
     * @return int
     */
    public int getTotalPage(int pageSize) {
        if (pageSize < 1 || maxCount < 1) {
            return 0;
        }

        return (int) Math.ceil((float) maxCount / pageSize);
    }

    /**
     * 获取当前数据条数
     *
     * @return int
     */
    public int getDataCount() {
        return this.dataList.size();
    }

    /**
     * 是否是一个有效的详细页数据
     *
     * @return boolean
     */
    public boolean isValidDetailData() {
        if (null == detailInfo) {
            return false;
        }

        if (hasError) {
            return false;
        }

        if (detailInfo.getCount() < 1) {
            return false;
        }

        return true;

    }

    /**
     * 是否是一个有效的列表数据
     *
     * @return boolean
     */
    public boolean isValidListData() {

        if (null == dataList) {
            return false;
        }

        if (hasError) {
            return false;
        }

        if (dataList.size() < 1) {
            return false;
        }

        return true;
    }


    public boolean isEmpty() {
        return dataList == null || dataList.isEmpty();
    }

    /**
     * 从键值对map数组中导入数据
     *
     * @return boolean
     */
    public boolean importFromMapList(List<Map<String, String>> mapList) {
        boolean res = true;

        if (mapList == null || mapList.size() < 1) {
            return false;
        }

        for (int i = 0; i < mapList.size(); i++) {
            Map<String, String> map = mapList.get(i);
            DataItemDetail item = new DataItemDetail();

            if (item.importFromMap(map)) {
                res = dataListAddItem(item, -1) && res;
            }
        }

        if (maxCount < this.dataList.size()) {
            maxCount = this.dataList.size();
        }

        return res;
    }

    /**
     * 添加一个对象
     *
     * @return boolean
     */
    public boolean addItem(DataItemDetail item) {
        if (null == item) {
            return false;
        }

        if (dataListAddItem(item, -1)) {
            if (maxCount < this.dataList.size()) {
                maxCount = this.dataList.size();
            }

            return true;
        }

        return false;
    }

    /**
     * 添加一个对象
     * <p>
     * Created by janzon.tang
     *
     * @return boolean
     * @since 2014-2-26
     */
    public boolean addItem(int position, DataItemDetail item) {
        if (null == item || position >= maxCount) {
            return false;
        }
        if (dataListAddItem(item, position)) {
            if (maxCount < this.dataList.size()) {
                maxCount = this.dataList.size();
            }

            return true;
        }
        return false;
    }

    /**
     * 从另一个列表容器中创建当前容器
     *
     * @return boolean
     */
    public boolean addItemList(List<DataItemDetail> list) {
        boolean res = true;

        if (list == null || list.size() < 1) {
            return false;
        }

        for (int i = 0; i < list.size(); i++) {
            res = dataListAddItem(list.get(i), -1) && res;
        }

        if (maxCount < this.dataList.size()) {
            maxCount = this.dataList.size();
        }

        return res;
    }

    /**
     * 在指定的位置加入一个list
     * @param list
     * @param position
     * @return
     */
    public boolean addItemList(int position , List<DataItemDetail> list){
        boolean res = true;
        if (list == null || list.size() < 1){
            return false;
        }

        this.dataList.addAll(position,list);

        if (maxCount < this.dataList.size()) {
            maxCount = this.dataList.size();
        }

        return res;
    }

    /**
     * 私有函数： 往当前列表指定位置添加一个item值（position小于0或者大于列表长度插入到list末端）
     * <p>
     * 带主键去重功能； 如遇重复主键值，则直接返回true
     * <p>
     * Created by janzon.tang
     *
     * @param item     DataItemDetail
     * @param position 希望插入到list中的位置，如果position值小于0或者大于等于列表长度那么直接添加到list的末尾位置
     * @return boolean
     * @since 2014-2-26
     */
    private boolean dataListAddItem(DataItemDetail item, int position) {
        if (item == null) {
            return false;
        }

        if (itemUniqueKeyName.length() > 0) {
            int maxCnt = this.dataList.size();
            String checkValue = item.getString(itemUniqueKeyName);

            for (int i = 0; i < maxCnt; i++) {
                String tempValue = this.dataList.get(i).getString(itemUniqueKeyName);

                if (tempValue.equals(checkValue)) {
                    return true;
                }
            }
        }

        if (position < 0 || position >= this.dataList.size()) {
            return this.dataList.add(item);
        } else {
            this.dataList.add(position, item);
            return true;
        }
    }

    /**
     * 统计包含指定布尔键值对的 item 个数
     *
     * @return int
     */
    public int countItemsWithBooleanValue(String key, boolean value) {
        if (this.dataList.size() < 1) {
            return 0;
        }

        int result = 0;
        int startIndex = dataList.size() - 1;
        for (int i = startIndex; i > -1; i--) {
            DataItemDetail item = getItem(i);
            if (item == null) {
                continue;
            }

            if (item.getBoolean(key) == value) {
                result++;
            }
        }

        return result;
    }

    /**
     * 统计包含指定字符键值对的 item 个数
     * <p>
     * Created by eric.huang
     *
     * @return int
     * @since 2012-7-17
     */
    public int countItemsWithStringValue(String key, String value) {
        if (this.dataList.size() < 1) {
            return 0;
        }

        int result = 0;
        int startIndex = dataList.size() - 1;
        for (int i = startIndex; i > -1; i--) {
            DataItemDetail item = getItem(i);
            if (item == null) {
                continue;
            }

            if (item.getString(key).equals(value)) {
                result++;
            }
        }

        return result;
    }

    /**
     * 获取包含某个布尔键值对的item对应的主键列表，主键名为空时返回空
     *
     * @return String
     */
    public String getItemsIDWithBooleanValue(String key, boolean value) {
        if (itemUniqueKeyName.length() < 1) {
            return "";
        }

        if (this.dataList.size() < 1) {
            return "";
        }

        String result = "";
        int startIndex = dataList.size() - 1;
        for (int i = startIndex; i > -1; i--) {
            DataItemDetail item = getItem(i);
            if (item == null) {
                continue;
            }

            if (item.getBoolean(key) == value) {
                String ID = item.getString(itemUniqueKeyName);

                if (ID.length() > 0) {
                    if (result.length() > 0) {
                        result += ",";
                    }
                    result += ID;
                }
            }
        }

        return result;
    }


    /**
     * 获取包含某个布尔键值对的item对应的主键列表，主键名为空时返回空
     * 同时在主键名后拼接，所需的键名
     *
     * @return String
     */
    public String getItemsIDWithBooleanValue(String key, boolean value, String addKey) {
        if (itemUniqueKeyName.length() < 1) {
            return "";
        }

        if (this.dataList.size() < 1) {
            return "";
        }

        String result = "";
        int startIndex = dataList.size() - 1;
        for (int i = startIndex; i > -1; i--) {
            DataItemDetail item = getItem(i);
            if (item == null) {
                continue;
            }

            if (item.getBoolean(key) == value) {
                String ID = item.getString(itemUniqueKeyName);
                String addVar = item.getString(addKey);

                if (ID.length() > 0) {
                    if (result.length() > 0) {
                        result += ",";
                    }
                    result += ID;

                    if (addVar.length() > 0) {
                        result += ":" + addVar;
                    }

                }
            }
        }

        return result;
    }


    /**
     * 清除包含指定键值对的元素
     *
     * @return boolean
     */
    public boolean removeItemsWithStringValue(String key, String value) {
        if (this.dataList.size() < 1) {
            return true;
        }

        int startIndex = dataList.size() - 1;
        for (int i = startIndex; i > -1; i--) {
            DataItemDetail item = getItem(i);
            if (item == null) {
                // 2012-5-31 release version Log.e("DataItemResult", "Can not find index:" + i + " to remove!");
                return false;
            }

            if (item.hasKeyValue(key, value)) {
                if (null == this.removeByIndex(i)) {
                    // 2012-5-31 release version Log.e("DataItemResult", "Remove item at index:" + i + " failed!");
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 把所有元素的指定键名的值都置成指定字符串值
     *
     * @return boolean
     */
    public boolean setAllItemsToStringValue(String key, String value) {
        if (this.dataList.size() < 1) {
            return true;
        }

        for (int i = 0; i < dataList.size(); i++) {
            DataItemDetail item = getItem(i);

            if (item == null) {
                // 2012-5-31 release version Log.e("DataItemResult", "Can not find index:" + i + " to change key-value paire!");
                return false;
            }

            if (!value.equals(item.setStringValue(key, value))) {
                // 2012-5-31 release version Log.e("DataItemResult", "change key-value paire for item at index:" + i + " failed!");
                return false;
            }
        }

        return true;
    }

    /**
     * 把所有元素的指定键名的值都置成布尔值
     *
     * @return boolean
     */
    public boolean setAllItemsToBooleanValue(String key, Boolean value) {
        if (this.dataList.size() < 1) {
            return true;
        }

        for (int i = 0; i < dataList.size(); i++) {
            DataItemDetail item = getItem(i);

            if (item == null) {
                // 2012-5-31 release version Log.e("DataItemResult", "Can not find index:" + i + " to change key-value paire!");
                return false;
            }

            if (value != item.setBooleanValue(key, value)) {
                // 2012-5-31 release version Log.e("DataItemResult", "change key-value paire for item at index:" + i + " failed!");
                return false;
            }
        }

        return true;
    }

    /**
     * 清除对应键名为true的元素
     *
     * @return boolean
     */
    public boolean removeItemsWithTrueValue(String key) {
        if (this.dataList.size() < 1) {
            return true;
        }

        int startIndex = dataList.size() - 1;
        for (int i = startIndex; i > -1; i--) {
            DataItemDetail item = getItem(i);
            if (item == null) {
                // 2012-5-31 release version Log.e("DataItemResult", "Can not find index:" + i + " to remove!");
                return false;
            }

            if (item.getBoolean(key)) {
                if (null == this.removeByIndex(i)) {
                    // 2012-5-31 release version Log.e("DataItemResult", "Remove item at index:" + i + " failed!");
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 清除所有元素，不包括数据适配器容器中的数据
     */
    public DataItemResult clear() {
        dataList.clear();
        detailInfo.clear();

        debugInfo = "";
        resultCode = 0;
        statusCode = 0;
        maxCount = 0;
        parseError = false;
        hasError = false;
        message = "";

        return this;
    }

    /**
     * 往当前列表容器的后端追加另一个列表容器所有的数据
     *
     * @return boolean
     */
    public boolean appendItems(DataItemResult items) {
        boolean res = true;

        if (null == items || items.hasError) {
            return false;
        }

        this.maxCount = items.maxCount;
        this.statusCode = items.statusCode;

        Map<String, String> itemInfo = items.detailInfo.getAllData();

        for (String key : itemInfo.keySet()) {
            String value = (String) itemInfo.get(key);
            detailInfo.setStringValue(key, value);
        }

        for (int i = 0; i < items.getDataCount(); i++) {
            res = dataListAddItem(items.getItem(i), -1) && res;
        }

        if (maxCount < this.dataList.size()) {
            maxCount = this.dataList.size();
        }

        return res;
    }

    /**
     * 通过索引删除一个对象
     *
     * @return DataItemDetail
     */
    public DataItemDetail removeByIndex(int index) {
        if (index < 0 || index >= dataList.size()) {
            return null;
        }

        DataItemDetail item = this.dataList.remove(index);

        if (null != item) {
            maxCount--;
        }

        return item;
    }

    /**
     * 删除一个对象
     *
     * @return boolean
     */
    public boolean removeItem(DataItemDetail item) {
        if (this.dataList.remove(item)) {
            maxCount--;
            return true;
        }

        return false;
    }

    /**
     * 通过索引取得一个对象
     *
     * @return DataItemDetail
     */
    public DataItemDetail getItem(int index) {
        if (index < 0 || index >= this.dataList.size()) {
            return null;
        }

        return this.dataList.get(index);
    }


    /**
     * 设置出错的堆栈信息
     *
     * @since 2012-12-09
     */
    public void setErrorStack(String str) {
        this.debugInfo = (null == str) ? "" : str;
    }

    /**
     * 获取出错的堆栈信息
     *
     * @since 2012-12-09
     */
    public String getErrorStack() {
        if (null == this.debugInfo) {
            return this.debugInfo;
        }
        return this.debugInfo;
    }

    /**
     * 调试用，输出列表中所有元素
     *
     * @return void
     */
    public void Dump() {
        Log.v("Dump", "==========  [basicInfo] ==========");
        Log.v("Dump", "  .statusCode: " + this.statusCode);
        Log.v("Dump", "  .maxCount: " + this.maxCount);
        Log.v("Dump", "  .hasError: " + this.hasError);
        Log.v("Dump", "  .parseError: " + this.parseError);
        Log.v("Dump", "  .errorMessage: " + this.message);
        Log.v("Dump", "  .debugInfo: " + this.debugInfo);

        if (this.detailInfo.getCount() > 0) {
            Log.v("Dump", "==========  [detailInfo] ==========");
            this.detailInfo.Dump();
        }

        if (this.dataList.size() > 0) {
            Log.v("Dump", "==========  [dataList] ==========");

            for (int i = 0; i < this.dataList.size(); i++) {
                Log.v("Dump", "----------  [item:" + (i + 1) + "] ----------");
                this.dataList.get(i).Dump();
            }
        }
    }

    /**
     * 把对象数据转为字节数组
     *
     * @return byte[]
     * @since 2011-12-1
     */
    public byte[] toBytes() {
        byte[] result = null;

        try {
            Parcel out = Parcel.obtain();
            writeToParcel(out, 0);
            out.setDataPosition(0);
            result = out.marshall();
            out.recycle();
        } catch (Throwable e) {
            Log.e(TAG,"toBytes error",e);
        }

        return result;
    }

    /**
     * 把字节数据转换为 DataItemDetail 对象
     *
     * @param bytesData 字节数据
     * @return DataItemDetail 返回对象
     * @since 2011-12-1
     */
    public static DataItemResult fromBytes(byte[] bytesData) {
        if (null == bytesData) {
            return new DataItemResult();
        }

        DataItemResult result = null;

        try {
            Parcel in = Parcel.obtain();

            in.unmarshall(bytesData, 0, bytesData.length);
            in.setDataPosition(0);

            result = DataItemResult.CREATOR.createFromParcel(in);

            in.recycle();
        } catch (Throwable e) {
            Log.e(TAG,"toBytes error",e);
        }

        if (null != result) {
            return result;
        }

        return new DataItemResult();
    }

    /**
     * 序列化描述符，默认为0
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * 判断当前对象是否和另一个对象相同
     * <p>
     * Created by yuye.zou
     *
     * @return boolean
     * @since 2012-11-29
     */
    @Override
    public boolean equals(Object o) {
        if (null == o) {
            return false;
        }

        if (!(o instanceof DataItemResult)) {
            return false;
        }

        DataItemResult pO = (DataItemResult) o;

        // 判断hasError是否相等
        if (pO.hasError != (hasError)) {
            return false;
        }

        // 判断parseError是否相等
        if (pO.parseError != (parseError)) {
            return false;
        }

        // 判断message是否相等
        if (!pO.message.equals(message)) {
            return false;
        }

        // 判断maxCount是否相等
        if (pO.maxCount != maxCount) {
            return false;
        }

        // 判断statusCode是否相等
        if (pO.statusCode != statusCode) {
            return false;
        }

        // 判断resultCode是否相等
        if (pO.resultCode != resultCode) {
            return false;
        }

        // 判断debugInfo是否相等
        if (!pO.debugInfo.equals(debugInfo)) {
            return false;
        }

        // 判断detailInfo对象是否相等
        if (!pO.detailInfo.equals(detailInfo)) {
            return false;
        }
        //判断dataList对象是否相等
        if (pO.dataList.size() != this.dataList.size()) {
            return false;
        }
        int itemCount = this.getDataCount();
        for (int i = 0; i < itemCount; i++) {
            if (!pO.dataList.get(i).equals(this.dataList.get(i))) {
                return false;
            }
        }
        //判断adapterSetting对象是否相等
        if (pO.adapterSetting.size() != this.adapterSetting.size()) {
            return false;
        }
        for (int key : adapterSetting.keySet()) {
            if (!pO.adapterSetting.containsKey(key)) {
                return false;
            }
            if (!pO.adapterSetting.get(key).equals(adapterSetting.get(key))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 对象序列化函数
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(debugInfo);
        dest.writeString(message);
        dest.writeString(itemUniqueKeyName);

        dest.writeInt(resultCode);
        dest.writeInt(statusCode);
        dest.writeInt(maxCount);

        dest.writeInt(parseError ? 1 : 0);
        dest.writeInt(hasError ? 1 : 0);

        detailInfo.writeToParcel(dest, flags);

        int itemCount = this.getDataCount();

        dest.writeInt(itemCount);

        for (int i = 0; i < itemCount; i++) {
            this.getItem(i).writeToParcel(dest, flags);
        }

        itemCount = adapterSetting.size();

        dest.writeInt(itemCount);

        for (Integer key : adapterSetting.keySet()) {
            dest.writeInt(key);
            dest.writeString(adapterSetting.get(key));
        }
    }

    /**
     * 构造容器
     */
    public static final Creator<DataItemResult> CREATOR = new Creator<DataItemResult>() {
        public DataItemResult createFromParcel(Parcel in) {
            return new DataItemResult(in);
        }

        public DataItemResult[] newArray(int size) {
            return new DataItemResult[size];
        }
    };

    /**
     * 对象反序列化函数
     */
    public DataItemResult(Parcel in) {
        debugInfo = in.readString();
        message = in.readString();
        itemUniqueKeyName = in.readString();

        resultCode = in.readInt();
        statusCode = in.readInt();
        maxCount = in.readInt();

        parseError = in.readInt() == 1 ? true : false;
        hasError = in.readInt() == 1 ? true : false;

        detailInfo = new DataItemDetail(in);
        dataList = new ArrayList<DataItemDetail>();
        adapterSetting = new HashMap<Integer, String>();

        int itemCount = in.readInt();

        for (int i = 0; i < itemCount; i++) {
            DataItemDetail item = new DataItemDetail(in);
            this.addItem(item);
        }

        itemCount = in.readInt();

        for (int i = 0; i < itemCount; i++) {
            int key = in.readInt();
            String value = in.readString();
            adapterSetting.put(key, value);
        }
    }

    /**
     * 实现foreach迭代方式实现的接口
     * <p>
     * Created by shuai.yang
     *
     * @since 2016/4/15
     */
    @Override
    public Iterator<DataItemDetail> iterator() {
        return new Iterator<DataItemDetail>() {
            @Override
            public boolean hasNext() {
                if (index < dataList.size()) {
                    return true;
                } else {
                    index = 0;
                    return false;
                }
            }

            @Override
            public DataItemDetail next() {
                return dataList.get(index++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}