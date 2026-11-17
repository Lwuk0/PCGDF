package dev.pcgdf.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


public class Car extends Entidade {
    public Car(float x, float y, float width, float height, float speed) {
        super(x, y, width, height);
        velocidade.set(speed, 0f);
    }

    @Override public void update(float delta) {
        if (ativa) bounds.x += velocidade.x * delta;
    }

    @Override public void render(ShapeRenderer renderer) {
        if (!ativa) return;
        renderer.setColor(Color.GRAY);
        renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }


    public boolean isOffScreen(float width) {
        return (velocidade.x > 0 && bounds.x > width)
            || (velocidade.x < 0 && bounds.x + bounds.width < 0);
    }
}
