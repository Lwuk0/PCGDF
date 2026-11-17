package dev.pcgdf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.pcgdf.entities.Player;
import dev.pcgdf.fase.Fase;

// Tela LibGDX: entradas, viewport e renderizacao.
public class Game extends ScreenAdapter {
    private final ShapeRenderer shapeRender;
    private final Viewport viewport;
    private final Fase fase;

    public Game() {
        shapeRender = new ShapeRenderer();
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        fase = new Fase(viewport.getWorldWidth(), viewport.getWorldHeight());
        fase.carregar();
    }

    @Override public void render(float delta) {
        if (handleInput()) return;
        fase.atualizar(delta);
        ScreenUtils.clear(.18f, .55f, .22f, 1f);
        viewport.apply();
        shapeRender.setProjectionMatrix(viewport.getCamera().combined);
        shapeRender.begin(ShapeRenderer.ShapeType.Filled);
        fase.render(shapeRender);
        shapeRender.end();
    }

    // Retorna true quando a tela foi substituida.
    private boolean handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Main.getInstance().setScreen(new TitleScreen());
            return true;
        }
        boolean up = Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W);
        boolean down = Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S);
        boolean left = Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A);
        boolean right = Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D);
        int dx = (right ? 1 : 0) - (left ? 1 : 0);
        int dy = (up ? 1 : 0) - (down ? 1 : 0);
        if (dx != 0) dy = 0;
        Player player = fase.getPlayer();
        if (player != null && (dx != 0 || dy != 0)) player.move(dx, dy);
        return false;
    }

    @Override public void resize(int width, int height) { viewport.update(width, height, true); }
    @Override public void dispose() {
        fase.descarregar();
        shapeRender.dispose();
    }
}
