package com.taxi.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.taxi.common.Utils.Util;
import com.taxi.httpModule.RequestBuilder;
import com.taxi.httpModule.ResponseElement;
import com.taxi.httpModule.RunanbleCallback;
import com.taxi.kini.AddFeedActivity;
import com.taxi.kini.HomeActivity;
import com.taxi.kini.R;

import org.json.JSONArray;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment {

    HomeActivity activity;
    ListView listView;
    NewsFragment.NewsListAdapter adapter;
    ProgressDialog progressDialog;
    JSONArray array = new JSONArray();

    public NewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_news, container, false);

        // Inflate the layout for this fragment
        listView = v.findViewById(R.id.list_news);
        adapter = new NewsFragment.NewsListAdapter(activity, array);
        listView.setAdapter(adapter);

        FloatingActionButton fab = v.findViewById(R.id.fab);

        if (Util.currentUser.type.equalsIgnoreCase(Util.DRIVER)) {
            fab.show();
        }
        else {
            fab.hide();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
        return v;
    }


    // --------- Get News Feeds from Server ---------

    private void getNewsRequest(String url) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        RequestBuilder requestBuilder = new RequestBuilder(url, "GET");
        requestBuilder
                .sendRequest(newsCallback);
    }
    RunanbleCallback newsCallback = new RunanbleCallback() {
        @Override
        public void finish(ResponseElement element) {
            progressDialog.dismiss();
            if (element.getStatus()) {
                array = element.getArray("data");
                adapter = new NewsFragment.NewsListAdapter(activity, array);
                listView.setAdapter(adapter);
            }
        }

    };


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(takePictureIntent, activity.REQUEST_IMAGE_CAPTURE);
        }
    }

    public class NewsListAdapter  extends BaseAdapter
    {
        private Context mContext;
        private JSONArray arrData;

        public NewsListAdapter(Context context, JSONArray arr)
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
            view = inflater.inflate(R.layout.cell_news, null);

            try {
                JSONObject cellObj = arrData.getJSONObject(position);

                CircleImageView ivAvatar = view.findViewById(R.id.iv_avatar);
                Picasso.get().load(Uri.parse(Util.hostUrl + cellObj.getString("photo"))).into(ivAvatar);

                ImageView newsPhoto = view.findViewById(R.id.newsPhoto);
                byte[] decodedString = Base64.decode(cellObj.getString("newsPhoto"), Base64.DEFAULT);
                Bitmap decodedNewsPhoto = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                if (cellObj.getString("newsPhoto").contains(".jpg") || cellObj.getString("newsPhoto").contains(".png")) {

                    //Picasso.get().load(Uri.parse(Util.hostUrl + cellObj.getString("newsPhoto"))).resize(100, 100).centerCrop().into(newsPhoto);
                    Picasso.get().load(Uri.parse(Util.hostUrl + cellObj.getString("newsPhoto"))).into(newsPhoto);
                } else {

                    newsPhoto.setImageBitmap(decodedNewsPhoto);
                }

                TextView tv_time = view.findViewById(R.id.tv_time);
                tv_time.setText(cellObj.getString("created_at"));

                if (cellObj.getString("title").length() == 0 || cellObj.getString("title").equals("") || cellObj.getString("title") == null) {

                    TextView tv_title = view.findViewById(R.id.tv_title);
                    tv_title.setText("No title");

                } else {
                    TextView tv_title = view.findViewById(R.id.tv_title);
                    tv_title.setText(cellObj.getString("title"));
                }

                if (cellObj.getString("content").length() == 0 || cellObj.getString("content").equals("") || cellObj.getString("content") == null) {

                    TextView tv_title = view.findViewById(R.id.tv_content);
                    tv_title.setText("No description");

                } else {

                    TextView tv_content = view.findViewById(R.id.tv_content);
                    tv_content.setText(cellObj.getString("content"));
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            return view;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String newsFeedUrl = Util.serverUrl + "newsFeed/";

        getNewsRequest(newsFeedUrl);
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (HomeActivity) context;
    }

}
