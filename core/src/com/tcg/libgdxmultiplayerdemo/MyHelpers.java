package com.tcg.libgdxmultiplayerdemo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class MyHelpers {

    public static Vector2 polarVectorRadians(float magnitude, float angle) {
        // Calculate rectangular x and y components
        float rectX = magnitude * MathUtils.cos(angle);
        float rectY = magnitude * MathUtils.sin(angle);
        return new Vector2(rectX, rectY);
    }

    public static Vector2 polarVectorDegrees(float magnitude, float angle) {
        return polarVectorRadians(magnitude, angle * MathUtils.degreesToRadians);
    }

    public static float magSquared(float x, float y) {
        return (x * x) + (y * y);
    }

    public static float magSquared(Vector2 vector2) {
        return magSquared(vector2.x, vector2.y);
    }

    public static float mag(float x, float y) {
        return (float) Math.sqrt(magSquared(x, y));
    }

    public static float mag(Vector2 vector2) {
        return mag(vector2.x, vector2.y);
    }

    public static float distSquared(float x1, float y1, float x2, float y2) {
        return magSquared(x2 - x1, y2 - y1);
    }

    public static float distSquared(Vector2 p1, Vector2 p2) {
        return distSquared(p1.x, p1.y, p2.x, p2.y);
    }

    public static float dist(float x1, float y1, float x2, float y2) {
        return mag(x2 - x1, y2 - y1);
    }

    public static float dist(Vector2 p1, Vector2 p2) {
        return dist(p1.x, p1.y, p2.x, p2.y);
    }

    public static <T> T choose(T... choices) {
        return choices[MathUtils.random(choices.length - 1)];
    }

    public static <T extends Enum<T>> T choose(Class<T> enumClass) {
        return choose(enumClass.getEnumConstants());
    }

    public static TextureRegion[][] splitSpriteSheet(Texture texture, int rows, int cols) {
        return TextureRegion.split(texture, texture.getWidth() / cols, texture.getHeight() / rows);
    }

    public static float wrap(float n, float min, float max) {
        if(min > max) {
            return wrap(n, max, min);
        }
        if(n < min) {
            return max;
        }
        if(n > max) {
            return min;
        }
        return n;
    }

    public static Color randomColor() {
        int rgb = MathUtils.random(0xFFFFFF);
        int rgba = (rgb << 8) | 0xFF;
        return new Color(rgba);
    }

    public static Vector2 randomVector(float magnitude) {
        float angle = MathUtils.random(MathUtils.PI2);
        return polarVectorRadians(magnitude, angle);
    }

}
