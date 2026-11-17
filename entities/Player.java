package dev.pcgdf.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

/** Personagem controlado em passos discretos na grade. Data: 17/11/2026. */
public class Player extends Entidade {
    private final float gridSize;
    private final float worldWidth;
    private final float worldHeight;

    public Player(float startX, float startY, float gridSize, float worldWidth, float worldHeight) {
        super(startX + gridSize * .15f, startY + gridSize * .15f,
            gridSize * .70f, gridSize * .70f);
        this.gridSize = gridSize;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    /** Move uma celula por comando, respeitando os limites do mundo. */
    public void move(int dirX, int dirY) {
        if (!ativa) return;
        bounds.x = MathUtils.clamp(bounds.x + dirX * gridSize, 0, worldWidth - bounds.width);
        bounds.y = MathUtils.clamp(bounds.y + dirY * gridSize, 0, worldHeight - bounds.height);
    }

    @Override public void update(float delta) {
        // O deslocamento do jogador depende da entrada, processada por Game.
    }

    @Override public void render(ShapeRenderer renderer) {
        if (!ativa) return;
        renderer.setColor(Color.WHITE);
        renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    /** Reposiciona o personagem no centro da celula inicial. */
    public void reset(float startX, float startY) {
        float padding = gridSize * .15f;
        setPosition(startX + padding, startY + padding);
        iniciar();
    }
}
