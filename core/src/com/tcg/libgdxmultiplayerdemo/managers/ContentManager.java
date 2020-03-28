package com.tcg.libgdxmultiplayerdemo.managers;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.Map;

public class ContentManager implements Disposable {

    public enum Font {
        MAIN("fnt/hyperspace_bold.ttf", 24, Color.WHITE),
        GAME_OVER("fnt/hyperspace_bold.ttf", 56, Color.RED),
        WIN("fnt/hyperspace_bold.ttf", 56, Color.LIME),
        TITLE("fnt/hyperspace_bold.ttf", 56, Color.YELLOW);;
        public final String path;
        public final int fontSize;
        public final Color fontColor;

        Font(String path, int fontSize, Color fontColor) {
            this.path = path;
            this.fontSize = fontSize;
            this.fontColor = fontColor;
        }

        public FreeTypeFontGenerator.FreeTypeFontParameter toParam() {
            FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
            param.size = this.fontSize;
            param.color = this.fontColor;
            return param;
        }

    }

    public enum SoundEffect {
        EXPLODE("snd/explode.ogg"),
        LARGE_ENEMY("snd/largeEnemy.ogg"),
        PULSE_HIGH("snd/pulsehigh.ogg"),
        PULSE_LOW("snd/pulselow.ogg"),
        SAUCER_SHOOT("snd/saucershoot.ogg"),
        SHOOT("snd/shoot.ogg"),
        SMALL_ENEMY("snd/smallEnemy.ogg"),
        THRUSTER("snd/thruster.ogg");
        public final String path;

        SoundEffect(String path) {
            this.path = path;
        }
    }

    public enum Image {
        ;
        public final String path;

        Image(String path) {
            this.path = path;
        }
    }

    private Map<Font, BitmapFont> fonts;
    private Map<SoundEffect, Sound> sounds;
    private Map<Image, Texture> textures;

    private GlyphLayout gl;

    public ContentManager() {
        loadFonts();
        loadSounds();
        loadTextures();
    }

    private void loadTextures() {
        textures = new HashMap<Image, Texture>();
        for (Image value : Image.values()) {
            textures.put(value, new Texture(value.path));
        }
    }

    private void loadSounds() {
        sounds = new HashMap<SoundEffect, Sound>();
        for (SoundEffect value : SoundEffect.values()) {
            sounds.put(value, Gdx.audio.newSound(Gdx.files.internal(value.path)));
        }
    }

    private void loadFonts() {
        fonts = new HashMap<Font, BitmapFont>();
        for (Font value : Font.values()) {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(value.path));
            FreeTypeFontGenerator.FreeTypeFontParameter param = value.toParam();
            param.borderStraight = true;
            fonts.put(value, generator.generateFont(param));
            generator.dispose();
        }
        gl = new GlyphLayout();
    }

    public BitmapFont getFont(Font font) {
        return fonts.get(font);
    }

    public float getWidth(Font font, String s) {
        gl.setText(getFont(font), s);
        return gl.width;
    }

    public float getWidth(Font font, String s, float targetWidth, int halign, boolean wrap) {
        gl.setText(getFont(font), s, getFont(font).getColor(), targetWidth, halign, wrap);
        return gl.width;
    }

    public float getHeight(Font font, String s) {
        gl.setText(getFont(font), s);
        return gl.height;
    }

    public float getHeight(Font font, String s, float targetWidth, int halign, boolean wrap) {
        gl.setText(getFont(font), s, getFont(font).getColor(), targetWidth, halign, wrap);
        return gl.height;
    }

    public void playSound(SoundEffect soundEffect) {
        sounds.get(soundEffect).play();
    }

    public void playSound(SoundEffect soundEffect, float volume) {
        sounds.get(soundEffect).play(volume);
    }

    public void playSound(SoundEffect soundEffect, float volume, float pitch, float pan) {
        sounds.get(soundEffect).play(volume, pitch, pan);
    }

    public void loopSound(SoundEffect soundEffect) {
        sounds.get(soundEffect).loop();
    }

    public void loopSound(SoundEffect soundEffect, float volume) {
        sounds.get(soundEffect).loop(volume);
    }

    public void loopSound(SoundEffect soundEffect, float volume, float pitch, float pan) {
        sounds.get(soundEffect).loop(volume, pitch, pan);
    }

    public void stopSound(SoundEffect soundEffect) {
        sounds.get(soundEffect).stop();
    }

    public void stopAllSound() {
        for (SoundEffect value : SoundEffect.values()) {
            stopSound(value);
        }
    }

    public Texture getTexture(Image image) {
        return textures.get(image);
    }

    public TextureRegion getTextureRegion(Image image) {
        return new TextureRegion(getTexture(image));
    }

    @Override
    public void dispose() {
        for (Font value : Font.values()) {
            fonts.get(value).dispose();
        }
        for (SoundEffect value : SoundEffect.values()) {
            sounds.get(value).dispose();
        }
        for (Image value : Image.values()) {
            textures.get(value).dispose();
        }
    }
}
