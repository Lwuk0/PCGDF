package dev.pcgdf;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends Game {
    public SpriteBatch batch;
    public BitmapFont font;

    private static Main instance;

    @Override
    public void create() {
        instance = this;

        batch = new SpriteBatch();
        font = new BitmapFont();

        setScreen(new TitleScreen());
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();

        instance = null;
    }

    public static Main getInstance() {
        return instance;
    }
}
