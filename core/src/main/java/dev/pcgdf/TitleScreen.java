package dev.pcgdf;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.utils.ScreenUtils;

public class TitleScreen extends ScreenAdapter {
    private final Main game;

    public TitleScreen(Main game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        game.batch.begin();
        game.font.draw(game.batch, "OII OII OI OI OI OI", 100, 300);
        game.batch.draw(game.image, 140, 210);
        game.batch.end();
    }
}
