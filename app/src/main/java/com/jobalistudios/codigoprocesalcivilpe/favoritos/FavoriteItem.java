package com.jobalistudios.codigoprocesalcivilpe.favoritos;

import org.json.JSONException;
import org.json.JSONObject;

public class FavoriteItem {
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_SUBTITLE = "subtitle";
    private static final String KEY_TYPE = "type";
    private static final String KEY_DESTINATION = "destination_id";
    private static final String KEY_ACTIVITY = "activity";
    private static final String KEY_ADDED_AT = "addedAt";

    private final String id;
    private final String title;
    private final String subtitle;
    private final String type;
    private final String destinationId;
    private final long addedAt;

    public FavoriteItem(String id, String title, String subtitle, String type, String destinationId) {
        this(id, title, subtitle, type, destinationId, 0L);
    }

    public FavoriteItem(
            String id,
            String title,
            String subtitle,
            String type,
            String destinationId,
            long addedAt
    ) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.type = type;
        this.destinationId = FavoriteDestinationMapper.normalizeDestinationId(destinationId);
        this.addedAt = Math.max(0L, addedAt);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getType() {
        return type;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public long getAddedAt() {
        return addedAt;
    }

    FavoriteItem withAddedAt(long value) {
        return new FavoriteItem(id, title, subtitle, type, destinationId, value);
    }

    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put(KEY_ID, id);
        object.put(KEY_TITLE, title);
        object.put(KEY_SUBTITLE, subtitle);
        object.put(KEY_TYPE, type);
        object.put(KEY_DESTINATION, destinationId);
        object.put(KEY_ADDED_AT, addedAt);
        return object;
    }

    public static FavoriteItem fromJson(JSONObject object) {
        if (object == null) {
            return new FavoriteItem("", "", "", "", "");
        }

        String rawDestination = object.optString(KEY_DESTINATION);
        if (rawDestination.isEmpty()) {
            rawDestination = object.optString(KEY_ACTIVITY);
        }

        return new FavoriteItem(
                object.optString(KEY_ID),
                object.optString(KEY_TITLE),
                object.optString(KEY_SUBTITLE),
                object.optString(KEY_TYPE),
                rawDestination,
                object.optLong(KEY_ADDED_AT, 0L)
        );
    }
}
