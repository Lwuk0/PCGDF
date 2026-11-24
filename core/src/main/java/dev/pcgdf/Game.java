package dev.pcgdf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dev.pcgdf.entities.Car;
import dev.pcgdf.entities.Player;
import dev.pcgdf.fase.Decoration;

import java.util.stream.IntStream;

public class Game extends ScreenAdapter {
    private static final float GRID_SIZE = 50f;

    private final ShapeRenderer shapeRender;
    private final SpriteBatch batch;
    private final Viewport viewport;
    private final Player player;
    private final Array<Entidade> entidades;
    private final Array<Decoration> decorations = new Array<>();
    private final FloatArray lanes;
    private float[] laneSpeeds;
    private float[] laneSpawnTimers;
    private final float goalY;

    public Game() {
        shapeRender = new ShapeRenderer();
        batch = new SpriteBatch();
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        lanes = new FloatArray();

        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();
        int totalRows = (int) (worldH / GRID_SIZE);

        goalY = (totalRows - 1) * GRID_SIZE;
        player = new Player(worldW / 2f, 0f, GRID_SIZE, worldW, worldH);
        entidades = new Array<>();

        entidades.add(player);

        setupLanes();
        setupDecorations();
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

        shapeRender.setColor(0.95f, 0.75f, 0.15f, 1f);
        shapeRender.rect(0, goalY, viewport.getWorldWidth(), GRID_SIZE);

        for (int i = 0; i < entidades.size; i++) entidades.get(i).render(shapeRender);

        shapeRender.end();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        for (Decoration decoration : decorations) decoration.render(batch);
        for (int i = 0; i < entidades.size; i++) if (entidades.get(i) instanceof Car car) car.render(batch);

        player.render(batch);
        batch.end();

        Main.getInstance().batch.begin();
        Main.getInstance().font.getData().setScale(1.0f);
        Main.getInstance().font.setColor(Color.WHITE);
        Main.getInstance().font.draw(Main.getInstance().batch, "VIDAS: " + player.getVidas(), 10, 15);
        Main.getInstance().batch.end();
    }

    private void handleInput() {
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
        IntStream.range(0, entidades.size).forEach(i -> entidades.get(i).update(delta));

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

    private void setupLanes() {
        IntStream.iterate(entidades.size - 1, i -> i >= 0, i -> i - 1).filter(i -> entidades.get(i) instanceof Car).forEach(entidades::removeIndex);
        lanes.clear();

        float worldH = viewport.getWorldHeight();
        int totalRows = (int) (worldH / GRID_SIZE);
        int currentRow = 2;
        int maxRoadRow = totalRows - 3;

        while (currentRow <= maxRoadRow) {
            int clusterSize = MathUtils.random(1, 3);

            for (int j = 0; j < clusterSize && currentRow <= maxRoadRow; j++) {
                lanes.add(currentRow * GRID_SIZE);
                currentRow++;
            }

            currentRow += MathUtils.random(1, 2);
        }

        laneSpeeds = new float[lanes.size];
        laneSpawnTimers = new float[lanes.size];

        for (int i = 0; i < lanes.size; i++) {
            float direction = (i % 2 == 0) ? 1f : -1f;
            int ongoing = MathUtils.random(1, 3);

            laneSpeeds[i] = direction * MathUtils.random(120f, 260f);
            laneSpawnTimers[i] = MathUtils.random(0.2f, 1.5f);

            for (int c = 0; c < ongoing; c++) spawnCar(i, MathUtils.random(0f, viewport.getWorldWidth()));
        }
    }

    private void spawnCar(int idx, float startX) {
        float height = GRID_SIZE - 10f;
        float width = height * 2f;
        float y = lanes.get(idx) + 5f;

        for (Entidade e : entidades) {
            if (e instanceof Car car && Math.abs(car.getY() - y) < 1f && startX < car.getX() + car.bounds.width + 30f && startX + width > car.getX() - 30f) return;
        }

        entidades.add(new Car(startX, y, width, height, laneSpeeds[idx]));
    }

    private void spawnCar(int idx) {
        float startX = (laneSpeeds[idx] > 0) ? - (GRID_SIZE - 10f) * 2f : viewport.getWorldWidth();

        spawnCar(idx, startX);
    }

    private void respawn() {
        if (player.getVidas() <= 0) Main.getInstance().setScreen(new TitleScreen());

        player.reset(viewport.getWorldWidth() / 2f, 0f);
        setupLanes();
        setupDecorations();
    }

    private void setupDecorations() {
        decorations.clear();

        for (int row = 0; row < (int)(viewport.getWorldHeight() / GRID_SIZE); row++) {
            float y = row * GRID_SIZE;

            boolean isRoad = false;

            for (int i = 0; i < lanes.size; i++) {
                if (lanes.get(i) == y) {
                    isRoad = true;
                    break;
                }
            }

            if (isRoad) continue;

            for (int side = 0; side < 2; side++) {
                if (MathUtils.randomBoolean(0.7f)) {
                    float x = side == 0 ? MathUtils.random(0, 2) * GRID_SIZE : viewport.getWorldWidth() - GRID_SIZE * MathUtils.random(1, 3);

                    decorations.add(new Decoration(x, y, GRID_SIZE, GRID_SIZE));
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        shapeRender.dispose();
        batch.dispose();
        player.dispose();
        Decoration.dispose();
    }
}
