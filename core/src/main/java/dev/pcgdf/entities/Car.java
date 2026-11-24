package dev.pcgdf.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import dev.pcgdf.Entidade;

public class Car extends Entidade {
    private static Texture[] texturas;

    private final Texture textura;

    public Car(float x, float y, float width, float height, float speed) {
        super(x, y, width, height);

        velocidade.set(speed, 0f);

        if (texturas == null) {
            texturas = new Texture[] {
                new Texture("carro_lateral_vermelho.png"),
                new Texture("carro_lateral_azul.png")
            };
            for (Texture t : texturas) {
                t.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            }
        }
        textura = texturas[MathUtils.random(texturas.length - 1)];
    }

    @Override
    public void update(float delta) {
        if (ativa) bounds.x += velocidade.x * delta;
    }

    @Override
    public void render(ShapeRenderer renderer) {
    }

    public void render(SpriteBatch batch) {
        if (!ativa) return;

        boolean espelhar = velocidade.x < 0;
        batch.draw(textura,
                bounds.x, bounds.y, bounds.width, bounds.height,
                0, 0, textura.getWidth(), textura.getHeight(),
                espelhar, false);
    }

    public boolean isOffScreen(float width) {
        return (velocidade.x > 0 && bounds.x > width) || (velocidade.x < 0 && bounds.x + bounds.width < 0);
    }
}