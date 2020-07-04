package com.taxi.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.taxi.common.Utils.Util;
import com.taxi.httpModule.RequestBuilder;
import com.taxi.httpModule.ResponseElement;
import com.taxi.httpModule.RunanbleCallback;
import com.taxi.kini.HomeActivity;
import com.taxi.kini.R;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    HomeActivity activity;
    ListView listView;
    HistoryFragment.HistoryListAdapter adapter;
    ProgressDialog progressDialog;
    JSONArray array = new JSONArray();

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);

        // Inflate the layout for this fragment

        String newsFeedUrl = Util.serverUrl + "history/";
        getHistoryRequest(newsFeedUrl);

        listView = v.findViewById(R.id.list_history);
        adapter = new HistoryFragment.HistoryListAdapter(activity, array);
        listView.setAdapter(adapter);
        return v;
    }
    private void getHistoryRequest(String url) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        RequestBuilder requestBuilder = new RequestBuilder(url, "GET");
        requestBuilder
                .addParam("type", "driver")
                .addParam("phone", "8613042661581")
                .sendRequest(newsCallback);
    }
    RunanbleCallback newsCallback = new RunanbleCallback() {
        @Override
        public void finish(ResponseElement element) {
            progressDialog.dismiss();
            if (element.getStatus()) {
                array = element.getArray("data");
                adapter = new HistoryFragment.HistoryListAdapter(activity, array);
                listView.setAdapter(adapter);
            }
        }

    };
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (HomeActivity) context;
    }
    public class HistoryListAdapter  extends BaseAdapter
    {
        private Context mContext;
        private JSONArray arrData;

        public HistoryListAdapter(Context context, JSONArray arr)
        {
            super();
            mContext=context;
            arrData = arr;
        }

        public int getCount()
        {
            // return the number of records in cursor
            return arrData == null ? 0 : arrData.length();
            // return 10;
        }

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }


        // getView method is called for each item of ListView

        public View getView(int position, View view, ViewGroup parent)
        {
            // inflate the layout for each item of listView

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.cell_history, null);
            TextView txt_date = view.findViewById(R.id.txt_date);
            TextView txt_pickup = view.findViewById(R.id.txt_pickup);
            TextView txt_dest = view.findViewById(R.id.txt_dest);

            try {
                JSONObject cellObj = arrData.getJSONObject(position);


                txt_date.setText(cellObj.getString("created_at"));

                txt_pickup.setText(cellObj.getString("departure"));
                txt_dest.setText(cellObj.getString("destination"));

            } catch (Exception e) {
                e.printStackTrace();
            }

            return view;
        }
    }
}
