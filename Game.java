package dev.pcgdf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.pcgdf.entities.Player;
import dev.pcgdf.game.EstadoJogo;
import dev.pcgdf.game.Pontuacao;
import dev.pcgdf.game.Relatorio;

/** Tela LibGDX: teclado, camera, renderizacao e HUD. */
public class Game extends ScreenAdapter {
    private final ShapeRenderer shapeRender;
    private final Viewport viewport;
    private final Pontuacao pontuacao;
    private final Relatorio relatorio;

    public Game() {
        shapeRender = new ShapeRenderer();
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        pontuacao = new Pontuacao(viewport.getWorldWidth(), viewport.getWorldHeight());
        relatorio = new Relatorio();
    }

    @Override public void render(float delta) {
        if (handleInput()) return;
        pontuacao.atualizar(delta);
        ScreenUtils.clear(.18f, .55f, .22f, 1f);
        viewport.apply();
        shapeRender.setProjectionMatrix(viewport.getCamera().combined);
        shapeRender.begin(ShapeRenderer.ShapeType.Filled);
        pontuacao.getFase().render(shapeRender);
        shapeRender.end();
        // SpriteBatch nao pode desenhar enquanto ShapeRenderer esta aberto.
        relatorio.setProjectionMatrix(viewport.getCamera().combined);
        relatorio.mostrar(pontuacao, viewport.getWorldWidth(), viewport.getWorldHeight());
    }

    private boolean handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Main.getInstance().setScreen(new TitleScreen());
            dispose();
            return true;
        }
        if (pontuacao.getEstado() != EstadoJogo.JOGANDO) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) pontuacao.iniciar();
            return false;
        }
        boolean up = Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W);
        boolean down = Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S);
        boolean left = Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A);
        boolean right = Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D);
        int dx = (right ? 1 : 0) - (left ? 1 : 0);
        int dy = (up ? 1 : 0) - (down ? 1 : 0);
        if (dx != 0) dy = 0;
        Player player = pontuacao.getFase().getPlayer();
        if (player != null && (dx != 0 || dy != 0)) player.move(dx, dy);
        return false;
    }

    @Override public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void dispose() {
        pontuacao.finalizar();
        relatorio.dispose();
        shapeRender.dispose();
    }
}
