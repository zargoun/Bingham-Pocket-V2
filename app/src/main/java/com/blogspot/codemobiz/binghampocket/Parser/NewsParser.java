package com.blogspot.codemobiz.binghampocket.Parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Barka on 3/23/2015.
 */
public class NewsParser {
    public static String getNewsTitle(String jsonObj, int id)throws JSONException
    {
        JSONArray jsonArray = new JSONArray(id);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        jsonObj = jsonObject.getString("title");
        return jsonObj;
    }
}
