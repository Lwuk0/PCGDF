package dev.pcgdf.controls;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.pcgdf.entities.Player;

public class Touch extends InputAdapter {
    private final Viewport viewport;
    private final Player player;
    private final Vector2 touchPoint = new Vector2();

    private final Rectangle upBtn;
    private final Rectangle downBtn;
    private final Rectangle leftBtn;
    private final Rectangle rightBtn;

    private Rectangle pressedBtn = null;

    private final Color idleColor = new Color(1f, 1f, 1f, .25f);
    private final Color pressedColor = new Color(1f, 1f, 1f, .55f);

    public Touch(Viewport viewport, Player player) {
        this.viewport = viewport;
        this.player = player;

        float size = 56f;
        float gap = 8f;
        float baseX = viewport.getWorldWidth() - (size * 3) - (gap * 2) - 20f;
        float baseY = 20f;

        leftBtn = new Rectangle(baseX, baseY + size + gap, size, size);
        rightBtn = new Rectangle(baseX + (size + gap) * 2, baseY + size + gap, size, size);
        downBtn = new Rectangle(baseX + size + gap, baseY, size, size);
        upBtn = new Rectangle(baseX + size + gap, baseY + (size + gap) * 2, size, size);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touchPoint.set(screenX, screenY);
        viewport.unproject(touchPoint);

        if (upBtn.contains(touchPoint)) {
            pressedBtn = upBtn;
            player.move(0, 1);

            return true;
        }

        if (downBtn.contains(touchPoint)) {
            pressedBtn = downBtn;
            player.move(0, -1);

            return true;
        }

        if (leftBtn.contains(touchPoint)) {
            pressedBtn = leftBtn;
            player.move(-1, 0);

            return true;
        }

        if (rightBtn.contains(touchPoint)) {
            pressedBtn = rightBtn;
            player.move(1, 0);

            return true;
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        pressedBtn = null;

        return false;
    }

    public void render(ShapeRenderer renderer) {
        drawButton(renderer, upBtn);
        drawButton(renderer, downBtn);
        drawButton(renderer, leftBtn);
        drawButton(renderer, rightBtn);
    }

    private void drawButton(ShapeRenderer renderer, Rectangle btn) {
        renderer.setColor(btn == pressedBtn ? pressedColor : idleColor);
        renderer.rect(btn.x, btn.y, btn.width, btn.height);
    }
}
