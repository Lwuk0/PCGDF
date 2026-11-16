package dev.pcgdf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.pcgdf.entities.Car;
import dev.pcgdf.entities.Player;

import java.util.Iterator;

public class Game extends ScreenAdapter {
    private static final float GRID_SIZE = 50f;

    private final ShapeRenderer shapeRender;
    private final Viewport viewport;
    private final Player player;
    private final Array<Car> cars;
    private final float[] lanes = { 100f, 150f, 250f, 300f, 350f, 450f, 500f };
    private final float[] laneSpeeds;
    private final float[] laneSpawnTimers;

    public Game() {
        shapeRender = new ShapeRenderer();
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        player = new Player(viewport.getScreenWidth() / 2f, 0f, GRID_SIZE, viewport.getScreenWidth(), viewport.getScreenHeight());
        cars = new Array<>();
        laneSpeeds = new float[lanes.length];
        laneSpawnTimers = new float[lanes.length];

        for (int i = 0; i < lanes.length; i++) {
            float direction = (i % 2 == 0) ? 1f : -1f; // direção dos carros (deterministic)

            laneSpeeds[i] = direction * MathUtils.random(120f, 260f);
            laneSpawnTimers[i] = MathUtils.random(0.2f, 1.5f);
        }
    }

    @Override
    public void render(float delta) {
        handleInput();
        update(delta);

        ScreenUtils.clear(.18f, .55f, .22f, 1f);
        viewport.apply();
        shapeRender.setProjectionMatrix(viewport.getCamera().combined);
        shapeRender.begin(ShapeRenderer.ShapeType.Filled);
        shapeRender.setColor(0.22f, 0.22f, 0.25f, 1f);

        for (float laneY : lanes) shapeRender.rect(0, laneY, viewport.getScreenWidth(), GRID_SIZE);
        cars.forEach(c -> c.render(shapeRender));
        player.render(shapeRender);
        shapeRender.end();
    }

    private void handleInput() { // trocar pro input handler do libgdx
        boolean up = Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W);
        boolean down = Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S);
        boolean left = Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A);
        boolean right = Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D);

        int dx = (right ? 1 : 0) - (left ? 1 : 0);
        int dy = (up ? 1 : 0) - (down ? 1 : 0);

        if (dx != 0) dy = 0;
        if (dx != 0 || dy != 0) player.move(dx, dy);
    }

    private void update(float delta) {
        for (int i = 0; i < lanes.length; i++) {
            laneSpawnTimers[i] -= delta;

            if (laneSpawnTimers[i] <= 0) {
                spawnCar(i);
                laneSpawnTimers[i] = MathUtils.random(1.2f, 2.8f);
            }
        }

        Iterator<Car> iter = cars.iterator();
        while (iter.hasNext()) {
            Car car = iter.next();

            car.update(delta);

            if (car.bounds.overlaps(player.bounds)) {
                hitByCar();
                return;
            }

            if (car.isOffScreen(viewport.getScreenWidth())) iter.remove();
        }

        if (player.bounds.y >= viewport.getScreenHeight() - GRID_SIZE) goal();
    }

    private void spawnCar(int idx) {
        float y = lanes[idx] + 5f;
        float speed = laneSpeeds[idx];
        float width = MathUtils.random(60f, 100f);
        float height = GRID_SIZE - 10f;
        float startX = (speed > 0) ? -width : viewport.getScreenWidth();

        cars.add(new Car(startX, y, width, height, speed));
    }

    private void hitByCar() {
        Gdx.app.log("EVENT", "atropelado");
        respawn();
    }

    private void goal() {
        Gdx.app.log("EVENT", "nice");
        respawn();
    }

    private void respawn() {
        player.reset(viewport.getScreenWidth() / 2f, 0f);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        shapeRender.dispose();
    }
}
