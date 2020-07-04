package com.taxi.httpModule;

import org.json.JSONArray;
import org.json.JSONObject;

public class ResponseElement {
    ResponseBuilder responseBuilder;

    public ResponseElement(String response) {
        responseBuilder = new ResponseBuilder(response);
    }

    public boolean getStatus() {
        return responseBuilder.getBoolean("status");
    }

    public int getStatusCode() {
        String code = responseBuilder.getString("status");

        if (code.equals(""))
            code = "500";
        return Integer.parseInt(code);
    }

    public JSONObject getData() {
        JSONObject data = responseBuilder.getObject("data");
        return data;
    }

    public JSONObject getJsonObject(String name) {
        JSONArray jsonArray = null;
        try {
            jsonArray = responseBuilder.jsonObject.getJSONArray(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject data = null;
        try {
            data = jsonArray.getJSONObject(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public String getData(String group, String name) {
        String data = responseBuilder.getString("data", group, name);
        return data;
    }

    public JSONArray getArray(String key) {
        JSONArray array = responseBuilder.getArray("data");
        return array;
    }

    public int getArraySize() {
        int size = responseBuilder.getArraySize("data");
        return size;
    }

    public String getDataAtIndex(String name, int index) {
        String data = responseBuilder.getStringAtIndex("data", name, index);
        return data;
    }

    public String getErrorData() {
        String data = responseBuilder.getString("data", "reason");
        return data;
    }
    public String getErrorData(String key) {
        String data = responseBuilder.getString("data", key);
        return data;
    }


    public String getResponse() {
        return responseBuilder.getResponse();
    }
}
