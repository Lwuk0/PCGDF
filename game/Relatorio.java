package dev.pcgdf.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

// Apresenta HUD e resultado, sem modificar as regras da partida. */
public class Relatorio {
    private final BitmapFont fonte = new BitmapFont();
    private final SpriteBatch batch = new SpriteBatch();

    public void mostrar(Pontuacao pontuacao, float largura, float altura) {
        batch.begin();
        fonte.draw(batch, "Vidas: " + pontuacao.getVidas(), 16, altura - 16);
        fonte.draw(batch, "Pontos: " + pontuacao.getPontos(), 125, altura - 16);
        fonte.draw(batch, "Travessias: " + pontuacao.getTravessias() + "/5", 250, altura - 16);
        if (pontuacao.getEstado() != EstadoJogo.JOGANDO) {
            String resultado = pontuacao.getEstado() == EstadoJogo.VITORIA ? "VITORIA!" : "DERROTA!";
            fonte.draw(batch, resultado, largura / 2f - 45f, altura / 2f + 25f);
            fonte.draw(batch, "Pontos finais: " + pontuacao.getPontos(), largura / 2f - 65f, altura / 2f);
            fonte.draw(batch, "R: reiniciar | ESC: menu", largura / 2f - 95f, altura / 2f - 25f);
        }
        batch.end();
    }

    public void setProjectionMatrix(com.badlogic.gdx.math.Matrix4 matriz) {
        batch.setProjectionMatrix(matriz);
    }

    public void dispose() {
        fonte.dispose();
        batch.dispose();
    }
}
