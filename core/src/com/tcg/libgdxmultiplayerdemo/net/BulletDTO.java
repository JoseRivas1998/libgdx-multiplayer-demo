package com.tcg.libgdxmultiplayerdemo.net;

import org.json.JSONException;
import org.json.JSONObject;

public class BulletDTO implements JSONAble {

    public final String bulletId;
    public final String playerId;
    public final float x;
    public final float y;
    public final float direction;

    public BulletDTO(String bulletId, String playerId, float x, float y, float direction) {
        this.bulletId = bulletId;
        this.playerId = playerId;
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    public BulletDTO(JSONObject data) throws JSONException {
        this.bulletId = data.getString("bulletId");
        this.playerId = data.getString("playerId");
        this.x = (float) data.getDouble("x");
        this.y = (float) data.getDouble("y");
        this.direction = (float) data.getDouble("direction");
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("bulletId", this.bulletId);
        json.put("playerId", this.playerId);
        json.put("x", this.x);
        json.put("y", this.y);
        json.put("direction", this.direction);
        return json;
    }

    @Override
    public String toString() {
        return "BulletDTO{" +
                "bulletId='" + bulletId + '\'' +
                ", playerId='" + playerId + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", direction=" + direction +
                '}';
    }
}
