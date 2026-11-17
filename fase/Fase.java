package dev.pcgdf.fase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import dev.pcgdf.entities.Player;

// Coordena jogador, ruas, colisao e objetivo.
public class Fase {
    private static final float GRID_SIZE = 50f;
    private final float larguraMundo;
    private final float alturaMundo;
    private final Array<Rua> ruas = new Array<>();
    private final GerenciadorColisao colisoes = new GerenciadorColisao();
    private Player player;
    private float goalY;
    private boolean carregada;

    public Fase(float larguraMundo, float alturaMundo) {
        this.larguraMundo = larguraMundo;
        this.alturaMundo = alturaMundo;
    }

    //Duas ruas a cada tres linhas.
    public void carregar() {
        if (carregada) return;
        int totalRows = (int) (alturaMundo / GRID_SIZE);
        int maxRoadRow = totalRows - 3;
        int indiceRua = 0;
        for (int row = 2; row <= maxRoadRow; row++) {
            if ((row - 2) % 3 == 2) continue;
            float direcao = (indiceRua % 2 == 0) ? 1f : -1f;
            float velocidade = direcao * MathUtils.random(120f, 260f);
            ruas.add(new Rua(row * GRID_SIZE, velocidade, larguraMundo, GRID_SIZE));
            indiceRua++;
        }
        goalY = (totalRows - 1) * GRID_SIZE;
        player = new Player(larguraMundo / 2f, 0f, GRID_SIZE, larguraMundo, alturaMundo);
        carregada = true;
    }

    public EventoFase atualizar(float delta) {
        if (!carregada) return EventoFase.NENHUM;
        player.update(delta);
        for (Rua rua : ruas) rua.atualizar(delta);
        if (colisoes.verificar(player, ruas)) return EventoFase.ATROPELAMENTO;
        if (player.getY() >= goalY) return EventoFase.TRAVESSIA;
        return EventoFase.NENHUM;
    }

    public void reposicionarJogador() {
        if (carregada && player != null && player.getVidas() > 0) {
            player.reset(larguraMundo / 2f, 0f);
        }
    }

    public void render(ShapeRenderer renderer) {
        if (!carregada) return;
        for (Rua rua : ruas) rua.render(renderer);
        player.render(renderer);
    }

    public Player getPlayer() { return player; }
    public boolean isCarregada() { return carregada; }

    public void descarregar() {
        if (!carregada) return;
        for (Rua rua : ruas) rua.descarregar();
        ruas.clear();
        player.finalizar();
        player = null;
        carregada = false;
    }
}
