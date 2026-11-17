package dev.pcgdf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.pcgdf.entities.Car;
import dev.pcgdf.entities.Player;

public class Game extends ScreenAdapter {
    private static final float GRID_SIZE = 50f;

    private final ShapeRenderer shapeRender;
    private final Viewport viewport;
    private final Player player;
    private final Array<Entidade> entidades; // Não irei utilizar uma lista, pq no contexto de uma framework, um array usa menos recurso doq uma lista, ainda mais qnd os elementos executam funções de renderizar. (garbage collector do java).
    private final FloatArray lanes;
    private final float[] laneSpeeds;
    private final float[] laneSpawnTimers;
    private final float goalY;

    public Game() {
        shapeRender = new ShapeRenderer();
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        lanes = new FloatArray();

        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();
        int totalRows = (int) (worldH / GRID_SIZE);
        int maxRoadRow = totalRows - 3;

        for (int row = 2; row <= maxRoadRow; row++) if ((row - 2) % 3 != 2) lanes.add(row * GRID_SIZE);

        goalY = (totalRows - 1) * GRID_SIZE;
        player = new Player(worldW / 2f, 0f, GRID_SIZE, worldW, worldH);

        entidades = new Array<>();
        entidades.add(player);

        laneSpeeds = new float[lanes.size];
        laneSpawnTimers = new float[lanes.size];

        for (int i = 0; i < lanes.size; i++) {
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
        for (int i = 0; i < lanes.size; i++) shapeRender.rect(0, lanes.get(i), viewport.getWorldWidth(), GRID_SIZE);

        entidades.forEach(i -> i.render(shapeRender));
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

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) Main.getInstance().setScreen(new TitleScreen());
    }

    private void update(float delta) {
        entidades.forEach(i -> i.update(delta));

        for (int i = 0; i < lanes.size; i++) {
            laneSpawnTimers[i] -= delta;

            if (laneSpawnTimers[i] <= 0) {
                spawnCar(i);
                laneSpawnTimers[i] = MathUtils.random(1.2f, 2.8f);
            }
        }

        for (int i = entidades.size - 1; i >= 0; i--) {
            Entidade e = entidades.get(i);

            if (e instanceof Car car) {
                if (car.isCollidingWith(player)) {
                    Gdx.app.log("EVENT", "atropelado. Vidas restantes: " + (player.getVidas() - 1));
                    player.perderVida();
                    respawn();

                    return;
                }

                if (car.isOffScreen(viewport.getWorldWidth())) entidades.removeIndex(i);
            }
        }

        if (player.getY() >= goalY) {
            Gdx.app.log("EVENT", "nice");
            respawn();
        }
    }

    private void spawnCar(int idx) {
        float y = lanes.get(idx) + 5f;
        float speed = laneSpeeds[idx];
        float width = MathUtils.random(60f, 100f);
        float height = GRID_SIZE - 10f;
        float startX = (speed > 0) ? -width : viewport.getWorldWidth();

        entidades.add(new Car(startX, y, width, height, speed));
    }

    private void respawn() {
        player.reset(viewport.getWorldWidth() / 2f, 0f);
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
