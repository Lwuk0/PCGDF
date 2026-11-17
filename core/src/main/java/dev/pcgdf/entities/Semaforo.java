package dev.pcgdf.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import dev.pcgdf.Entidade;

public class Semaforo extends Entidade {
    private boolean estado = true;
    private float tempo;
    private final float intervalo;

    public Semaforo(float x, float y, float largura, float altura, float intervalo) {
        super(x, y, largura, altura);

        this.intervalo = intervalo;
    }

    public boolean getEstado() {
        return estado;
    }

    public void trocar() {
        estado = !estado;
    }

    @Override public void update(float delta) {
        if (!ativa || intervalo <= 0) return;

        tempo += delta;

        while (tempo >= intervalo) {
            trocar();

            tempo -= intervalo;
        }
    }

    @Override
    public void render(ShapeRenderer renderer) {
        if (!ativa) return;

        renderer.setColor(Color.DARK_GRAY);
        renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);

        float raio = Math.min(bounds.width * .35f, bounds.height / 6f);
        float centroX = bounds.x + bounds.width / 2f;

        renderer.setColor(estado ? Color.DARK_GRAY : Color.RED);
        renderer.circle(centroX, bounds.y + bounds.height * .7f, raio);
        renderer.setColor(estado ? Color.GREEN : Color.DARK_GRAY);
        renderer.circle(centroX, bounds.y + bounds.height * .3f, raio);
    }
}
