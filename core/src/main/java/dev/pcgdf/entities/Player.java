package dev.pcgdf.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import dev.pcgdf.Entidade;

public class Player extends Entidade {
    public static final int VIDAS_INICIAIS = 5;

    private final float gridSize;
    private final float worldWidth;
    private final float worldHeight;
    private final Texture textura;
    private int vidas = VIDAS_INICIAIS;

    public Player(float startX, float startY, float gridSize, float worldWidth, float worldHeight) {
        super(startX + gridSize * .15f, startY + gridSize * .15f, gridSize * .70f, gridSize * .70f);

        this.gridSize = gridSize;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;

        textura = new Texture("protagonista.png");
        textura.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
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
    }

    @Override
    public void render(ShapeRenderer renderer) {
    }

    public void render(SpriteBatch batch) {
        if (!ativa) return;

        batch.draw(textura, bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void reset(float startX, float startY) {
        float padding = gridSize * .15f;

        setPosition(startX + padding, startY + padding);

        if (vidas > 0) iniciar();
    }

    public void dispose() {
        textura.dispose();
    }
}