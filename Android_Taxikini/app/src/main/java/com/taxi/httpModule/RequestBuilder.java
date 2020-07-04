package com.taxi.httpModule;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class RequestBuilder {
    HashMap<String, String> params;
    MultipartEntity entity;
    String serverUrl;
    String method;
    Context context;

    public RequestBuilder(String url, String _method)
    {
        params = new HashMap<>();
        entity = new MultipartEntity();
        serverUrl = url;
        method = _method;
    }

    public RequestBuilder addParam(String name, String value) {
        params.put(name, value);
        return this;
    }

    /******************************************************************************************************************************
     *                                               AsynTask send request(params)                                                  *
     ******************************************************************************************************************************/

    public void sendRequest(final RunanbleCallback callback) {
        SendRequest request = new SendRequest();
        request.setRunanbleCallback(callback);
        request.execute();
    }
    private class SendRequest extends AsyncTask<String, Void, String> {
        private RunanbleCallback callback;
        ResponseElement element;

        public void setRunanbleCallback(RunanbleCallback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(String... params1) {
            if (method.equalsIgnoreCase("GET"))
                element = new ResponseElement(HttpCommunicator.sendGetRequest(serverUrl, params));
            else {
                element = new ResponseElement(HttpCommunicator.sendPostRequest(serverUrl, params));
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            callback.finish(element);
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    /******************************************************************************************************************************
     *                                               File Upload Part                                                              *
     ******************************************************************************************************************************/
    public RequestBuilder addEntityParam(String name, String value){
        try {
            entity.addPart(name, new StringBody(value));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return this;
    }
    public RequestBuilder addMultiEntityParam(String key, String fileName){
        entity.addPart(key, new FileBody(new File(fileName), "image/jpeg"));
        return this;
    }
    public ResponseElement sendMultiRequest(){
        return new ResponseElement(HttpCommunicator.sendMultiPostRequest(serverUrl, entity));
    }
    public MultipartEntity getEntity(){
        return entity;
    }

    /******************************************************************************************************************************
     *                                               AsynTask send request(params)                                                 *
     ******************************************************************************************************************************/

    public void sendMultiRequest(final RunanbleCallback callback) {
        SendRequest request = new SendRequest();
        request.setRunanbleCallback(callback);
        request.execute();
    }
    private class SendMultiRequest extends AsyncTask<String, Void, String> {
        private RunanbleCallback callback;
        ResponseElement element;

        public void setRunanbleCallback(RunanbleCallback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(String... params1) {
            element = new ResponseElement(HttpCommunicator.sendMultiPostRequest(serverUrl, entity));
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            callback.finish(element);
        }
        @Override
        protected void onProgressUpdate(Void... values) {}
    }

}
