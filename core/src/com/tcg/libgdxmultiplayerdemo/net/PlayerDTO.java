package com.tcg.libgdxmultiplayerdemo.net;

import com.tcg.libgdxmultiplayerdemo.entities.Ship;
import org.json.JSONException;
import org.json.JSONObject;

public final class PlayerDTO {

    public final String id;
    public final float x;
    public final float y;
    public final float angle;
    public final float velX;
    public final float velY;

    public PlayerDTO(String id, Ship ship) {
        this.id = id;
        this.x = ship.getX();
        this.y = ship.getY();
        this.angle = ship.getAngle();
        this.velX = ship.getVelocityX();
        this.velY = ship.getVelocityY();
    }

    public PlayerDTO(JSONObject data) throws JSONException {
        this.id = data.getString("id");
        this.x = (float) data.getDouble("x");
        this.y = (float) data.getDouble("y");
        this.angle = (float) data.getDouble("angle");
        this.velX = (float) data.getDouble("velX");
        this.velY = (float) data.getDouble("velY");
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject data = new JSONObject();
        data.put("id", this.id);
        data.put("x", this.x);
        data.put("y", this.y);
        data.put("angle", this.angle);
        data.put("velX", this.velX);
        data.put("velY", this.velY);
        return data;
    }

}
