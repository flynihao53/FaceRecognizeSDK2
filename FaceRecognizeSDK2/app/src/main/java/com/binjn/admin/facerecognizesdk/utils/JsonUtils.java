package com.binjn.admin.facerecognizesdk.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by angdajian on 2017/11/27.
 */

public class JsonUtils {

    public static String getJsonObjectParam(Map<String, String> map) {
        JSONObject object = new JSONObject();
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            try {
                object.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return object.toString();
    }

    public static String getJsFromHashMap(HashMap<String, String> hashMap) {
        StringBuilder builder = new StringBuilder();
        Iterator it = hashMap.entrySet().iterator();
        builder.append("{");
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            if(!it.hasNext()){
                builder.append("\"").append(key).append("\"").append(":").append("\"").append(value).append("\"");
            }else {
                builder.append("\"").append(key).append("\"").append(":").append("\"").append(value).append("\"").append(",");
            }
        }
        builder.append("}");
        return builder.toString();
    }
}
