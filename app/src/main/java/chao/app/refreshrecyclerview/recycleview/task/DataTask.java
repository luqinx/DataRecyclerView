package chao.app.refreshrecyclerview.recycleview.task;

import android.os.AsyncTask;

import chao.app.refreshrecyclerview.recycleview.data.DataItemResult;

/**
 * @author chao.qin
 * @since 2017/4/11
 */

public class DataTask extends AsyncTask<Void,Void,DataItemResult> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onPostExecute(DataItemResult dataItemDetails) {
        super.onPostExecute(dataItemDetails);
    }

    @Override
    protected DataItemResult doInBackground(Void... params) {
        return null;
    }
}
