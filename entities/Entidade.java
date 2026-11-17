
package dev.pcgdf.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Entidade {

    protected final Rectangle bounds;

    protected final Vector2 velocidade;

    protected boolean ativa;

    protected Entidade(
        float x,
        float y,
        float width,
        float height
    ) {
        bounds = new Rectangle(x, y, width, height);
        velocidade = new Vector2(0, 0);
        ativa = true;
    }

    public void iniciar() {
        ativa = true;
    }

    //@param delta Tempo decorrido desde o último quadro.
    public abstract void update(float delta);

    public abstract void render(ShapeRenderer renderer);

    // Verifica colisão com outra entidade.
    public boolean isCollidingWith(Entidade outra) {
        return ativa
            && outra != null
            && outra.ativa
            && bounds.overlaps(outra.bounds);
    }


    public void finalizar() {
        ativa = false;
    }

    // Métodos de acesso aos atributos compartilhados.

    public Rectangle getBounds() {
        return bounds;
    }

    public Vector2 getVelocidade() {
        return velocidade;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public float getX() {
        return bounds.x;
    }

    public float getY() {
        return bounds.y;
    }

    public void setPosition(float x, float y) {
        bounds.setPosition(x, y);
    }
}
