package dev.pcgdf;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.Color;

public class Main extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
    public BitmapFont fonteGrande;
    public BitmapFont fonteMedia;

    private static Main instance;

    @Override
    public void create() {
        instance = this;

        batch = new SpriteBatch();
        font = new BitmapFont();

        setScreen(new TitleScreen());
        FreeTypeFontGenerator generatorTitle =
            new FreeTypeFontGenerator(
                Gdx.files.internal("Montserrat-Bold.ttf")
            );

        FreeTypeFontParameter titleParameter =
            new FreeTypeFontParameter();

        titleParameter.size = 64;

        fonteGrande = generatorTitle.generateFont(titleParameter);
        generatorTitle.dispose();


        FreeTypeFontGenerator generatorSubtitle =
            new FreeTypeFontGenerator(
                Gdx.files.internal("Montserrat-Medium.ttf")
            );

        FreeTypeFontParameter subtitleParameter =
            new FreeTypeFontParameter();

        subtitleParameter.size = 28;

        fonteMedia = generatorSubtitle.generateFont(subtitleParameter);
        generatorSubtitle.dispose();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();

        fonteGrande.dispose();
        fonteMedia.dispose();

        instance = null;
    }

    public static Main getInstance() {
        return instance;
    }
}
