package com.jobalistudios.codigoprocesalcivilpe.favoritos;

import org.json.JSONException;
import org.json.JSONObject;

public class FavoriteItem {
    private final String activityClassName;
    private final String title;
    private final String type;

    public FavoriteItem(String activityClassName, String title, String type) {
        this.activityClassName = activityClassName;
        this.title = title;
        this.type = type;
    }

    public String getActivityClassName() {
        return activityClassName;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String toJson() {
        JSONObject object = new JSONObject();
        try {
            object.put("activityClassName", activityClassName);
            object.put("title", title);
            object.put("type", type);
        } catch (JSONException ignored) {
        }
        return object.toString();
    }

    public static FavoriteItem fromJson(String json) {
        try {
            JSONObject object = new JSONObject(json);
            return new FavoriteItem(
                    object.optString("activityClassName"),
                    object.optString("title"),
                    object.optString("type")
            );
        } catch (JSONException exception) {
            return null;
        }
    }
}
