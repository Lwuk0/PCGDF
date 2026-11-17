package dev.pcgdf.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

//Base abstrata das entidades
public abstract class Entidade {
    protected final Rectangle bounds;
    protected final Vector2 velocidade = new Vector2();
    protected boolean ativa = true;

    protected Entidade(float x, float y, float largura, float altura) {
        bounds = new Rectangle(x, y, largura, altura);
    }

    public void iniciar() { ativa = true; }
    public void finalizar() { ativa = false; }
    public boolean isAtiva() { return ativa; }
    public float getX() { return bounds.x; }
    public float getY() { return bounds.y; }
    public void setPosition(float x, float y) { bounds.setPosition(x, y); }

    // Testa a sobreposicao entre entidades
    public boolean isCollidingWith(Entidade outra) {
        return ativa && outra != null && outra.ativa && bounds.overlaps(outra.bounds);
    }

    public abstract void update(float delta);
    public abstract void render(ShapeRenderer renderer);
}
