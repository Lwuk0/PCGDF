package dev.pcgdf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;

public class TitleScreen extends ScreenAdapter {
    private final Main game;
    private Texture image2;
    private float tempo = 0f;
    private final float duracao = 2f;

    public TitleScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        image2 = new Texture("sinal de transito.png");
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        tempo += delta;

        float ciclo = tempo % (duracao * 2);
        boolean primeira = ciclo < duracao;
        float progresso = primeira ? ciclo / duracao : (ciclo - duracao) / duracao;
        float alpha = 1f - Math.abs(progresso * 2f - 1f);

        game.batch.begin();
        game.font.draw(game.batch, "OII OII OI OI OI OI", 100, 300);

        game.batch.setColor(1f, 1f, 1f, alpha);
        if (primeira) {
            game.batch.draw(game.image, 140, 210);
        } else {
            game.batch.draw(image2, 0, 0);
        }
        game.batch.setColor(Color.WHITE);

        game.batch.end();
    }

    @Override
    public void dispose() {
        image2.dispose();
    }
}
