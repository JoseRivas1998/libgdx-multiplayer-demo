package com.tcg.libgdxmultiplayerdemo.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Bullet extends AbstractEntity {

    private static final float BULLET_SPEED = 500f;
    private static final float BULLET_RADIUS = 1;

    public final boolean friendly;

    public Bullet(boolean friendly, Vector2 position, float direction) {
        super();
        initializeVertices(4);
        this.friendly = friendly;
        this.setRadius(BULLET_RADIUS);
        this.setSpeed(BULLET_SPEED);
        this.setDirection(direction);
        this.setPosition(position);
        this.setShape();
    }

    public Bullet(boolean friendly, float x, float y, float direction) {
        this(friendly, new Vector2(x, y), direction);
    }

    @Override
    protected void setShape() {
        setSquare();
    }

    @Override
    public void update(float dt) {
        this.applyVelocity(dt);
        this.setShape();
    }

    @Override
    public void draw(float dt, ShapeRenderer sr) {
        Color original = new Color(sr.getColor());
        sr.setColor(this.friendly ? Color.GREEN : Color.RED);
        this.drawPoints(sr);
        sr.setColor(original);
    }
}
