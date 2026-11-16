package dev.pcgdf.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Car {
    public final Rectangle bounds;
    private final float speed;

    public Car(float x, float y, float width, float height, float speed) {
        this.bounds = new Rectangle(x, y, width, height);
        this.speed = speed;
    }

    public void update(float delta) {
        bounds.x += speed * delta;
    }

    public void render(ShapeRenderer renderer) {
        renderer.setColor(Color.GRAY);
        renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public boolean isOffScreen(float width) {
        return (speed > 0 && bounds.x > width) || (speed < 0 && bounds.x + bounds.width < 0);
    }
}
