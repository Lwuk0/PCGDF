package dev.pcgdf.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import dev.pcgdf.Entidade;

public class Player extends Entidade {
    public static final int VIDAS_INICIAIS = 5;

    private final float gridSize;
    private final float worldWidth;
    private final float worldHeight;
    private int vidas = VIDAS_INICIAIS;

    public Player(float startX, float startY, float gridSize, float worldWidth, float worldHeight) {
        super(startX + gridSize * .15f, startY + gridSize * .15f, gridSize * .70f, gridSize * .70f);

        this.gridSize = gridSize;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public void move(int dirX, int dirY) {
        if (!ativa) return;

        bounds.x = MathUtils.clamp(bounds.x + dirX * gridSize, 0, worldWidth - bounds.width);
        bounds.y = MathUtils.clamp(bounds.y + dirY * gridSize, 0, worldHeight - bounds.height);
    }

    public void perderVida() {
        if (vidas > 0) vidas--;
        if (vidas == 0) finalizar();
    }

    public int getVidas() {
        return vidas;
    }

    @Override
    public void update(float delta) {
        // Movimento comandado por Game, por passos discretos.
    }

    @Override
    public void render(ShapeRenderer renderer) {
        if (!ativa) return;

        renderer.setColor(Color.WHITE);
        renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void reset(float startX, float startY) {
        float padding = gridSize * .15f;

        setPosition(startX + padding, startY + padding);

        if (vidas > 0) iniciar();
    }
}
