package dev.pcgdf.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class MenuButton {
    private final Rectangle bounds;
    private final String text;
    private final Color color;
    private final Color hoverColor = new Color(1f, 0.82f, 0.12f, 1f);
    private final Color textColor;
    private final Color textHoverColor = new Color(0.1f, 0.1f, 0.12f, 1f);

    private final GlyphLayout layout = new GlyphLayout();

    public MenuButton(float x, float y, float width, float height, String text, Color color, Color textColor) {
        this.bounds = new Rectangle(x, y, width, height);
        this.text = text;
        this.color = color;
        this.textColor = textColor;
    }

    public boolean isHovered(Vector2 mouse) {
        return bounds.contains(mouse.x, mouse.y);
    }

    public void render(ShapeRenderer shape, Vector2 mouse) {
        boolean hover = isHovered(mouse);
        float aumento = hover ? 6f : 0f;

        shape.setColor(0f, 0f, 0f, 0.4f);
        shape.rect(bounds.x - aumento + 4, bounds.y - aumento - 4, bounds.width + aumento * 2, bounds.height + aumento * 2);

        shape.setColor(hover ? hoverColor : color);
        shape.rect(bounds.x - aumento, bounds.y - aumento, bounds.width + aumento * 2, bounds.height + aumento * 2);
    }

    public void renderText(SpriteBatch batch, BitmapFont font, float fontScale, Vector2 mouse) {
        boolean hover = isHovered(mouse);

        font.getData().setScale(fontScale);
        font.setColor(hover ? textHoverColor : textColor);

        layout.setText(font, text);
        font.draw(batch, text, bounds.x + (bounds.width - layout.width) / 2f, bounds.y + (bounds.height + layout.height) / 2f);
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
