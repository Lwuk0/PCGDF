package dev.pcgdf.fase;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

import java.util.Arrays;

public class Decoration {
    private static final Texture[] TEXTURES = {
        new Texture("deco/bush0.png"),
        new Texture("deco/bush1.png"),
        new Texture("deco/bush2.png")
    };

    static {
        Arrays.stream(TEXTURES).forEach(texture -> texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest)); // TODO verificar se tá safe pro wasm
    }

    private final Texture texture;
    private final float x;
    private final float y;
    private final float width;
    private final float height;

    public Decoration(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        texture = TEXTURES[MathUtils.random(TEXTURES.length - 1)];
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }

    public static void dispose() {
        Arrays.stream(TEXTURES).forEach(Texture::dispose);
    }
}
