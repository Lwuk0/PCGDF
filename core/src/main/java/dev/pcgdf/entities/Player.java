package dev.pcgdf.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Player {
    public final Rectangle bounds;
    private final float gridSize;
    private final float worldWidth;
    private final float worldHeight;

    public Player(float startX, float startY, float gridSize, float worldWidth, float worldHeight) {
        this.gridSize = gridSize;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;

        float padding = gridSize * .15f;

        bounds = new Rectangle(startX + padding, startY + padding, gridSize - (padding * 2), gridSize - (padding * 2));
    }

    public void move(int dirX, int dirY) {
        float newX = bounds.x + (dirX * gridSize);
        float newY = bounds.y + (dirY * gridSize);

        bounds.x = MathUtils.clamp(newX, 0, worldWidth - bounds.width);
        bounds.y = MathUtils.clamp(newY, 0, worldHeight - bounds.height);
    }

    public void render(ShapeRenderer renderer) {
        renderer.setColor(Color.WHITE);
        renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void reset(float startX, float startY) {
        float padding = gridSize * .15f;

        bounds.setPosition(startX + padding, startY + padding);
    }
}
