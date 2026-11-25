package dev.pcgdf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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

import java.util.Arrays;
import java.util.stream.IntStream;

public class Game extends ScreenAdapter {
    private static final float GRID_SIZE = 50f;

    // Zoom: quanto menor o mundo, maior fica tudo na tela (mantenha 16:9)
    private static final float WORLD_LARGURA = 1280f;
    private static final float WORLD_ALTURA = 720f;

    private final ShapeRenderer shapeRender;
    private final SpriteBatch batch;
    private final Viewport viewport;
    private final Player player;
    private final Array<Entidade> entidades;
    private final Array<Decoration> decorations = new Array<>();
    private final FloatArray lanes;
    private float[] laneSpeeds;
    private float[] laneSpawnTimers;

    // Sem final: agora a meta muda de lugar a cada fase (calculada em setupLanes)
    private float goalY;

    private static final float FAIXA_LARGURA = GRID_SIZE * 2f;
    private static final float CALCADA_ALTURA = 14f;
    private static final float TEMPO_FAIXA = 4f;
    private static final int MAX_RUAS = 5;
    private static final int PISTAS_MAX = 2;

    private static final String[] DICAS = {
        "Dica: use a faixa de pedestre!",
        "Dica: os carros azuis param na faixa!",
        "Dica: espere o sinal vermelho!",
        "Dica: olhe antes de atravessar!"
    };

    private final Array<Car> carrosAzuis = new Array<>();

    private boolean sinalVermelho = false;
    private float sinalTimer = 0f;

    private float[] faixaTimers = new float[0];
    private boolean[] jogadorNaFaixaAntes = new boolean[0];
    private boolean usouFaixa = false;

    private int pontos = 0;
    private int fase = 1;
    private boolean gameOver = false;

    private String mensagem = "";
    private float mensagemTimer = 0f;

    public Game() {
        shapeRender = new ShapeRenderer();
        batch = new SpriteBatch();
        viewport = new FitViewport(WORLD_LARGURA, WORLD_ALTURA);
        lanes = new FloatArray();

        float worldW = viewport.getWorldWidth();
        float worldH = viewport.getWorldHeight();

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

        desenharCalcadas();
        desenharFaixasDePedestre();

        for (int i = 0; i < entidades.size; i++) entidades.get(i).render(shapeRender);

        shapeRender.end();

        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        for (Decoration decoration : decorations) decoration.render(batch);
        for (int i = 0; i < entidades.size; i++) if (entidades.get(i) instanceof Car car) car.render(batch);

        player.render(batch);
        batch.end();

        shapeRender.begin(ShapeRenderer.ShapeType.Filled);
        desenharSinais();
        desenharBarraDeAviso();
        desenharPainelFimDeJogo();
        shapeRender.end();

        Main.getInstance().batch.setProjectionMatrix(viewport.getCamera().combined);
        Main.getInstance().batch.begin();
        Main.getInstance().font.getData().setScale(1.0f);
        Main.getInstance().font.setColor(Color.WHITE);
        Main.getInstance().font.draw(Main.getInstance().batch, "VIDAS: " + player.getVidas() + "   PONTOS: " + pontos + "   FASE: " + fase, 10, 15);

        String aviso = getAviso();

        if (aviso != null) {
            Main.getInstance().font.setColor(getFaixaTimerMax() > 0f ? Color.YELLOW : Color.WHITE);
            desenharTextoCentralizado(Main.getInstance().font, aviso, viewport.getWorldHeight() - 7f);
            Main.getInstance().font.setColor(Color.WHITE);
        }

        if (gameOver) desenharTextoFimDeJogo();

        Main.getInstance().batch.end();
    }

    private void handleInput() {
        if (gameOver) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) Main.getInstance().setScreen(new Game());
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) Main.getInstance().setScreen(new TitleScreen());

            return;
        }

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
        if (gameOver) return;

        if (mensagemTimer > 0f) mensagemTimer -= delta;

        atualizarSinal(delta);
        atualizarFaixas(delta);
        atualizarVelocidadeDosCarros();

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
                    mostrarMensagem("Ops! Olhe os carros antes de atravessar!");
                    respawn();

                    return;
                }

                if (car.isOffScreen(viewport.getWorldWidth())) {
                    carrosAzuis.removeValue(car, true);
                    entidades.removeIndex(i);
                }
            }
        }

        if (player.getY() >= goalY) {
            Gdx.app.log("EVENT", "nice");

            int ganho = usouFaixa ? 15 : 10;
            pontos += ganho;
            fase++;

            String extra;

            if (fase <= MAX_RUAS) extra = "Nova rua e carros mais rapidos!";
            else if (usouFaixa) extra = "Usou a faixa, muito bem!";
            else extra = DICAS[MathUtils.random(DICAS.length - 1)];

            mostrarMensagem("+" + ganho + " pontos - " + extra);

            respawn();
        }
    }

    private void setupLanes() {
        IntStream.iterate(entidades.size - 1, i -> i >= 0, i -> i - 1).filter(i -> entidades.get(i) instanceof Car).forEach(entidades::removeIndex);
        lanes.clear();

        carrosAzuis.clear();

        float worldH = viewport.getWorldHeight();
        int totalRows = (int) (worldH / GRID_SIZE);
        int maxRoadRow = totalRows - 3;
        int linhasDisponiveis = maxRoadRow - 1;

        int ruasDesejadas = Math.min(fase, MAX_RUAS);
        int[] pistasDaRua = new int[ruasDesejadas];
        int totalDePistas = 0;

        for (int r = 0; r < ruasDesejadas; r++) {
            pistasDaRua[r] = MathUtils.random(1, PISTAS_MAX);
            totalDePistas += pistasDaRua[r];
        }

        int espacoLivre = linhasDisponiveis - totalDePistas;
        int espaco = MathUtils.clamp(espacoLivre / ruasDesejadas, 1, 3);

        int linhaAtual = 2;

        for (int rua = 0; rua < ruasDesejadas; rua++) {
            if (linhaAtual + pistasDaRua[rua] - 1 > maxRoadRow) break;

            for (int p = 0; p < pistasDaRua[rua]; p++) {
                lanes.add((linhaAtual + p) * GRID_SIZE);
            }

            linhaAtual += pistasDaRua[rua] + espaco;
        }

        // A meta fica 3 linhas acima da ultima pista
        float ultimaPista = lanes.size > 0 ? lanes.peek() : 0f;
        float metaMaxima = (totalRows - 1) * GRID_SIZE; // nunca passa do topo da tela
        goalY = Math.min(ultimaPista + GRID_SIZE * 3f, metaMaxima);

        laneSpeeds = new float[lanes.size];
        laneSpawnTimers = new float[lanes.size];

        faixaTimers = new float[getTotalRuas()];
        jogadorNaFaixaAntes = new boolean[getTotalRuas()];

        for (int i = 0; i < lanes.size; i++) {
            float direction = (i % 2 == 0) ? 1f : -1f;
            int ongoing = MathUtils.random(1, 3);

            laneSpeeds[i] = direction * MathUtils.random(120f, 260f) * getMultiplicadorDeVelocidade();
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

        Car novoCarro = new Car(startX, y, width, height, laneSpeeds[idx]);
        entidades.add(novoCarro);

        if (MathUtils.randomBoolean(0.5f)) carrosAzuis.add(novoCarro);
    }

    private void spawnCar(int idx) {
        float startX = (laneSpeeds[idx] > 0) ? - (GRID_SIZE - 10f) * 2f : viewport.getWorldWidth();

        spawnCar(idx, startX);
    }

    private void respawn() {
        Arrays.fill(faixaTimers, 0f);
        Arrays.fill(jogadorNaFaixaAntes, false);
        usouFaixa = false;

        if (player.getVidas() <= 0) {
            gameOver = true;
            return;
        }

        player.reset(viewport.getWorldWidth() / 2f, 0f);
        setupLanes();
        setupDecorations();
    }

    private void setupDecorations() {
        decorations.clear();

        int colunas = (int) (viewport.getWorldWidth() / GRID_SIZE);

        for (int row = 0; row < (int)(viewport.getWorldHeight() / GRID_SIZE); row++) {
            float y = row * GRID_SIZE;

            if (y > goalY) continue; // nao decora acima da meta

            boolean isRoad = false;

            for (int i = 0; i < lanes.size; i++) {
                if (lanes.get(i) == y) {
                    isRoad = true;
                    break;
                }
            }

            if (isRoad) continue;

            for (int col = 0; col < colunas; col++) {
                if (MathUtils.randomBoolean(0.25f)) {
                    decorations.add(new Decoration(col * GRID_SIZE, y, GRID_SIZE, GRID_SIZE));
                }
            }
        }
    }

    private float getMultiplicadorDeVelocidade() {
        return Math.min(1f + (fase - 1) * 0.3f, 4f);
    }
    private void mostrarMensagem(String texto) {
        mensagem = texto;
        mensagemTimer = 3.5f;
    }

    private float getFaixaTimerMax() {
        float maior = 0f;

        for (int i = 0; i < faixaTimers.length; i++) {
            if (faixaTimers[i] > maior) maior = faixaTimers[i];
        }

        return maior;
    }

    private String getAviso() {
        float faixaTimerMax = getFaixaTimerMax();

        if (faixaTimerMax > 0f) return "Carros azuis parados: " + (int) Math.ceil(faixaTimerMax) + "s";
        if (mensagemTimer > 0f) return mensagem;
        return null;
    }

    private int getRuaDaFaixa(int faixa) {
        int rua = 0;

        for (int i = 1; i <= faixa; i++) {
            if (lanes.get(i) - lanes.get(i - 1) > GRID_SIZE + 1f) rua++;
        }

        return rua;
    }

    private int getTotalRuas() {
        if (lanes.size == 0) return 0;

        return getRuaDaFaixa(lanes.size - 1) + 1;
    }

    private float getRuaBaseY(int rua) {
        for (int i = 0; i < lanes.size; i++) {
            if (getRuaDaFaixa(i) == rua) return lanes.get(i);
        }

        return 0f;
    }

    private float getRuaTopoY(int rua) {
        float topo = 0f;

        for (int i = 0; i < lanes.size; i++) {
            if (getRuaDaFaixa(i) == rua) topo = lanes.get(i) + GRID_SIZE;
        }

        return topo;
    }

    private boolean temFaixa(int rua) {
        return rua % 2 == 0;
    }

    private boolean temSinal(int rua) {
        return rua % 2 == 1;
    }

    private void atualizarSinal(float delta) {
        sinalTimer += delta;

        float tempoDoSinal = sinalVermelho ? 3f : 5f;

        if (sinalTimer >= tempoDoSinal) {
            sinalTimer = 0f;
            sinalVermelho = !sinalVermelho;
        }
    }

    private void atualizarFaixas(float delta) {
        for (int rua = 0; rua < getTotalRuas(); rua++) {
            if (!temFaixa(rua)) continue;

            boolean naFaixa = jogadorNaFaixa(rua);

            if (naFaixa && !jogadorNaFaixaAntes[rua]) faixaTimers[rua] = TEMPO_FAIXA;
            if (jogadorAtravessandoNaFaixa(rua)) usouFaixa = true;

            jogadorNaFaixaAntes[rua] = naFaixa;

            if (faixaTimers[rua] > 0f) faixaTimers[rua] -= delta;
        }
    }

    private void atualizarVelocidadeDosCarros() {
        for (int i = 0; i < entidades.size; i++) {
            if (!(entidades.get(i) instanceof Car car)) continue;

            int faixa = getFaixaDoCarro(car);
            if (faixa < 0) continue;

            int rua = getRuaDaFaixa(faixa);
            float velocidadeNormal = laneSpeeds[faixa];
            boolean deveParar = false;

            if (temFaixa(rua) && carrosAzuis.contains(car, true) && faixaTimers[rua] > 0f) {
                deveParar = estaChegandoNaFaixa(car, velocidadeNormal);
            }

            if (temSinal(rua) && sinalVermelho) {
                deveParar = true;
            }

            car.velocidade.x = deveParar ? 0f : velocidadeNormal;
        }
    }

    private int getFaixaDoCarro(Car car) {
        for (int i = 0; i < lanes.size; i++) {
            if (Math.abs(car.getY() - (lanes.get(i) + 5f)) < 1f) return i;
        }
        return -1;
    }

    private boolean jogadorNaColunaDaFaixa() {
        float centroDoJogador = player.getX() + GRID_SIZE / 2f;
        float faixaX = getFaixaX();

        return centroDoJogador >= faixaX && centroDoJogador <= faixaX + FAIXA_LARGURA;
    }

    private boolean jogadorNaFaixa(int rua) {
        float base = getRuaBaseY(rua);
        float topo = getRuaTopoY(rua);

        boolean pertoDaRuaNoY = player.getY() >= base - GRID_SIZE - 1f && player.getY() <= topo - GRID_SIZE + 1f;

        return jogadorNaColunaDaFaixa() && pertoDaRuaNoY;
    }

    private boolean jogadorAtravessandoNaFaixa(int rua) {
        float base = getRuaBaseY(rua);
        float topo = getRuaTopoY(rua);

        boolean naRuaNoY = player.getY() >= base - 1f && player.getY() <= topo - GRID_SIZE + 1f;

        return jogadorNaColunaDaFaixa() && naRuaNoY;
    }

    private boolean estaChegandoNaFaixa(Car car, float velocidadeNormal) {
        float faixaX = getFaixaX();

        if (velocidadeNormal > 0) {
            float frente = car.getX() + car.bounds.width;
            return frente >= faixaX - 30f && frente <= faixaX;
        } else {
            float frente = car.getX();
            float fimDaFaixa = faixaX + FAIXA_LARGURA;
            return frente <= fimDaFaixa + 30f && frente >= fimDaFaixa;
        }
    }

    private float getFaixaX() {
        return viewport.getWorldWidth() / 2f - FAIXA_LARGURA / 2f;
    }

    private void desenharCalcadas() {
        float largura = viewport.getWorldWidth();

        for (int rua = 0; rua < getTotalRuas(); rua++) {
            float base = getRuaBaseY(rua);
            float topo = getRuaTopoY(rua);

            shapeRender.setColor(0.78f, 0.78f, 0.75f, 1f);
            shapeRender.rect(0, base - CALCADA_ALTURA, largura, CALCADA_ALTURA);
            shapeRender.rect(0, topo, largura, CALCADA_ALTURA);

            shapeRender.setColor(0.55f, 0.55f, 0.55f, 1f);
            shapeRender.rect(0, base - 2f, largura, 2f);
            shapeRender.rect(0, topo, largura, 2f);
        }
    }

    private void desenharFaixasDePedestre() {
        float x = getFaixaX();

        shapeRender.setColor(0.93f, 0.93f, 0.93f, 1f);

        for (int rua = 0; rua < getTotalRuas(); rua++) {
            if (!temFaixa(rua)) continue;

            float base = getRuaBaseY(rua);
            float topo = getRuaTopoY(rua);
            int listras = (int) ((topo - base) / 10f);

            for (int i = 0; i < listras; i++) {
                shapeRender.rect(x, base + 2f + i * 10f, FAIXA_LARGURA, 6f);
            }
        }
    }

    private void desenharSinais() {
        for (int rua = 0; rua < getTotalRuas(); rua++) {
            if (!temSinal(rua)) continue;

            float x = 14f;
            float y = getRuaTopoY(rua) + 4f;
            float cx = x + 11f;

            shapeRender.setColor(0.08f, 0.08f, 0.08f, 1f);
            shapeRender.rect(x, y, 22f, 38f);

            if (sinalVermelho) shapeRender.setColor(1f, 0.15f, 0.15f, 1f);
            else shapeRender.setColor(0.3f, 0.05f, 0.05f, 1f);
            shapeRender.circle(cx, y + 30f, 5f);

            shapeRender.setColor(0.3f, 0.25f, 0.05f, 1f);
            shapeRender.circle(cx, y + 19f, 5f);

            if (sinalVermelho) shapeRender.setColor(0.05f, 0.3f, 0.08f, 1f);
            else shapeRender.setColor(0.2f, 1f, 0.3f, 1f);
            shapeRender.circle(cx, y + 8f, 5f);
        }
    }

    private void desenharBarraDeAviso() {
        if (getAviso() == null) return;

        shapeRender.setColor(0.05f, 0.05f, 0.1f, 1f);
        shapeRender.rect(0, viewport.getWorldHeight() - 28f, viewport.getWorldWidth(), 28f);
    }

    private void desenharPainelFimDeJogo() {
        if (!gameOver) return;

        float largura = Math.min(viewport.getWorldWidth() - 40f, 520f);
        float cx = viewport.getWorldWidth() / 2f;
        float cy = viewport.getWorldHeight() / 2f;

        shapeRender.setColor(0.05f, 0.05f, 0.1f, 1f);
        shapeRender.rect(cx - largura / 2f, cy - 90f, largura, 190f);
    }

    private void desenharTextoFimDeJogo() {
        float cy = viewport.getWorldHeight() / 2f;

        Main.getInstance().fonteGrande.setColor(1f, 0.3f, 0.3f, 1f);
        desenharTextoCentralizado(Main.getInstance().fonteGrande, "FIM DE JOGO", cy + 80f);

        Main.getInstance().fonteMedia.setColor(Color.WHITE);
        desenharTextoCentralizado(Main.getInstance().fonteMedia, "Pontos: " + pontos + "   Fase: " + fase, cy + 10f);

        Main.getInstance().font.setColor(Color.YELLOW);
        desenharTextoCentralizado(Main.getInstance().font, "ENTER: jogar de novo   ESC: menu", cy - 45f);
        Main.getInstance().font.setColor(Color.WHITE);
    }

    private void desenharTextoCentralizado(BitmapFont fonte, String texto, float y) {
        GlyphLayout layout = new GlyphLayout(fonte, texto);

        fonte.draw(Main.getInstance().batch, layout, (viewport.getWorldWidth() - layout.width) / 2f, y);
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
