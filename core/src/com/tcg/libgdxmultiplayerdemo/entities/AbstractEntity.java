package com.tcg.libgdxmultiplayerdemo.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.tcg.libgdxmultiplayerdemo.MyHelpers;

public abstract class AbstractEntity {

    public static final String TAG = AbstractEntity.class.getSimpleName();

    private static int entitiesCreated;

    private Vector2[] vertices;
    private Vector2 position;
    private Vector2 velocity;
    private int id;
    private float radius;
    protected float angle;

    public AbstractEntity() {
        position = new Vector2();
        velocity = new Vector2();
        this.radius = 0;
        this.angle = 0;
        id = ++AbstractEntity.entitiesCreated;
        Gdx.app.debug(TAG, "Created Entity: " + this);
    }

    protected void initializeVertices(int numPoints) {
        vertices = new Vector2[numPoints];
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = new Vector2();
        }
    }

    protected abstract void setShape();

    public abstract void update(float dt);

    public abstract void draw(float dt, ShapeRenderer sr);

    protected void drawPoints(ShapeRenderer sr) {
        AbstractEntity.drawVector(this.vertices, sr, true);
    }

    protected static void drawVector(Vector2[] vertices, ShapeRenderer sr, boolean connectEnds) {
        for (int i = 0; i < vertices.length - 1; i++) {
            sr.line(vertices[i], vertices[i + 1]);
        }
        if(connectEnds) {
            sr.line(vertices[vertices.length - 1], vertices[0]);
        }
    }

    protected void setPointRadians(int point, float length, float angleOffset) {
        Vector2 offset = MyHelpers.polarVectorRadians(length, angle + angleOffset);
        setPointOffset(point, offset);
    }

    protected void setPointDegrees(int point, float length, float angleOffset) {
        setPointRadians(point, length, angleOffset * MathUtils.degreesToRadians);
    }

    protected void setPointOffset(int point, Vector2 offset) {
        setPointOffset(point, offset.x, offset.y);
    }

    protected void setPointOffset(int point, float xOffset, float yOffset) {
        this.vertices[point].set(getX() + xOffset, getY() + yOffset);
    }

    protected void setSquare() {
        setPointOffset(0, getRadius(), -getRadius());
        setPointOffset(1, getRadius(), getRadius());
        setPointOffset(2, -getRadius(), getRadius());
        setPointOffset(3, -getRadius(), -getRadius());
    }

    protected float getRadius() {
        return radius;
    }

    protected void setRadius(float radius) {
        this.radius = radius;
    }

    public float getAngle() {
        return this.angle;
    }

    public float getX() {
        return this.position.x;
    }

    public void setX(float x) {
        this.position.x = x;
    }

    public float getY() {
        return this.position.y;
    }

    public void setY(float y) {
        this.position.y = y;
    }

    public Vector2 getPosition() {
        return new Vector2(position);
    }

    public void setPosition(float x, float y) {
        setX(x);
        setY(y);
    }

    public void setPosition(Vector2 point) {
        setPosition(point.x, point.y);
    }

    protected void wrapPosition(float xMin, float xMax, float yMin, float yMax) {
        setX(MyHelpers.wrap(getX(), xMin, xMax));
        setY(MyHelpers.wrap(getY(), yMin, yMax));
    }

    public float getVelocityX() {
        return this.velocity.x;
    }

    public void setVelocityX(float x) {
        this.velocity.x = x;
    }

    public float getVelocityY() {
        return this.velocity.y;
    }

    public void setVelocityY(float y) {
        this.velocity.y = y;
    }

    public void setVelocity(float x, float y) {
        setVelocityX(x);
        setVelocityY(y);
    }

    public void setVelocity(Vector2 velocity) {
        setVelocity(velocity.x, velocity.y);
    }

    public Vector2 getVelocity() {
        return new Vector2(this.velocity);
    }

    public void setVelocityPolar(float speed, float angle) {
        setVelocity(MyHelpers.polarVectorRadians(speed, angle));
    }

    public void setVelocityPolarDegrees(float speed, float angle) {
        setVelocity(MyHelpers.polarVectorDegrees(speed, angle));
    }

    public float getDirection() {
        return MathUtils.atan2(getVelocityY(), getVelocityX());
    }

    public float getDirectionDegrees() {
        return getDirection() * MathUtils.degreesToRadians;
    }

    public void setDirection(float direction) {
        float speed = MyHelpers.mag(getVelocity());
        setVelocityPolar(speed, direction);
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public void setDirectionDegrees(float direction) {
        setDirection(direction * MathUtils.degreesToRadians);
    }

    public float getSpeed() {
        return MyHelpers.mag(getVelocity());
    }

    public void setSpeed(float speed) {
        float angle = getDirection();
        setVelocityPolar(speed, angle);
    }

    public void applyVelocity(float dt) {
        this.position.x += this.velocity.x * dt;
        this.position.y += this.velocity.y * dt;
    }

    public boolean collidingWith(AbstractEntity e) {
        return collidingWith(e, true);
    }

    private boolean collidingWith(AbstractEntity e, boolean reflexive) {
        boolean colliding = false;
        Vector2[] other = e.vertices;
        for (int i = 0; i < other.length && !colliding; i++) {
            if(contains(other[i])) {
                colliding = true;
            }
        }
        if(!colliding && reflexive) {
            colliding = e.collidingWith(this, false);
        }
        return colliding;
    }

    public boolean contains(Vector2 point) {
        return contains(point.x, point.y);
    }

    public boolean contains(float x, float y) {
        boolean b = false;
        for (int i = 0, j = vertices.length - 1;
             i < vertices.length;
             j = i++) {
            if ((vertices[i].y > y) != (vertices[j].y > y) &&
                    (x < (vertices[j].x - vertices[i].x) *
                            (y - vertices[i].y) / (vertices[j].y - vertices[i].y)
                            + vertices[i].x)) {
                b = !b;
            }
        }
        return b;
    }

    protected Vector2 getVertex(int index) {
        return new Vector2(this.vertices[index]);
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj == null || obj.getClass() != this.getClass()) {
            result = true;
        } else {
            AbstractEntity other = (AbstractEntity) obj;
            result = this.id == other.id;
        }
        return result;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@id=" + this.id;
    }

}
