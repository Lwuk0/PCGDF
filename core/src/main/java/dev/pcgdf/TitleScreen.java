package dev.pcgdf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TitleScreen extends ScreenAdapter {
    private final Main game;
    private final ShapeRenderer shape = new ShapeRenderer();
    private final GlyphLayout layout = new GlyphLayout();
    private final Viewport viewport = new ExtendViewport(640, 480);
    private final Vector2 mouse = new Vector2();

    private Rectangle botaoJogar;
    private Rectangle botaoCreditos;
    private Rectangle botaoSair;
    private Rectangle botaoVoltar;

    private boolean mostrarCreditos = false;

    private final String[] nomes = {
        "Lucas Santos do Carmo",
        "Guilherme Maia Matos",
        "Anthony Raphael Bery Cruz",
        "João Gabriel Marinho",
        "Humberto Santos do Nascimento",
        "Natanael dos Santos",
        "Lucas Gabriel Aguiar Monteiro",
        "Victor Campos Mansilla",
        "Lucas Gabriel Soares do Nascimento"
    };

    private final Color amarelo = new Color(1f, 0.82f, 0.12f, 1f);
    private final Color asfaltoCima = new Color(0.17f, 0.18f, 0.21f, 1f);
    private final Color asfaltoBaixo = new Color(0.06f, 0.06f, 0.08f, 1f);
    private final Color verdeBotao = new Color(0.18f, 0.80f, 0.44f, 1f);
    private final Color cinzaBotao = new Color(0.20f, 0.23f, 0.29f, 1f);
    private final Color textoEscuro = new Color(0.1f, 0.1f, 0.12f, 1f);
    private final Color vermelhoApagado = new Color(0.3f, 0.1f, 0.1f, 1f);
    private final Color amareloApagado = new Color(0.35f, 0.30f, 0.08f, 1f);
    private final Color verdeApagado = new Color(0.1f, 0.3f, 0.1f, 1f);
    private final Color cinzaClaro = new Color(0.85f, 0.85f, 0.85f, 1f);

    private float tempo = 0f;

    public TitleScreen() {
        game = Main.getInstance();
    }

    @Override
    public void show() {
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        montarBotoes();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        montarBotoes();
    }

    private void montarBotoes() {
        float largura = 260;
        float altura = 54;
        float espaco = 16;
        float x = (viewport.getWorldWidth() - largura) / 2f;
        float y = viewport.getWorldHeight() * 0.50f;

        botaoJogar = new Rectangle(x, y, largura, altura);
        botaoCreditos = new Rectangle(x, y - (altura + espaco), largura, altura);
        botaoSair = new Rectangle(x, y - 2 * (altura + espaco), largura, altura);
        botaoVoltar = new Rectangle(x, 20, largura, 44);
    }

    @Override
    public void render(float delta) {
        tempo += delta;
        ScreenUtils.clear(0, 0, 0, 1);

        viewport.apply();
        shape.setProjectionMatrix(viewport.getCamera().combined);
        game.batch.setProjectionMatrix(viewport.getCamera().combined);

        mouse.set(Gdx.input.getX(), Gdx.input.getY());
        viewport.unproject(mouse);

        if (mostrarCreditos) {
            renderCreditos();
            return;
        }

        boolean hoverJogar = botaoJogar.contains(mouse.x, mouse.y);
        boolean hoverCreditos = botaoCreditos.contains(mouse.x, mouse.y);
        boolean hoverSair = botaoSair.contains(mouse.x, mouse.y);

        shape.begin(ShapeType.Filled);
        desenharFundo();
        desenharFaixa();
        desenharSemaforo();
        desenharFormaBotao(botaoJogar, verdeBotao, hoverJogar);
        desenharFormaBotao(botaoCreditos, cinzaBotao, hoverCreditos);
        desenharFormaBotao(botaoSair, cinzaBotao, hoverSair);
        shape.end();

        game.batch.begin();
        desenharTitulo();
        desenharFrase();
        desenharTextoBotao(botaoJogar, "JOGAR", hoverJogar);
        desenharTextoBotao(botaoCreditos, "CREDITOS", hoverCreditos);
        desenharTextoBotao(botaoSair, "SAIR", hoverSair);
        game.font.getData().setScale(1f);
        game.font.setColor(Color.WHITE);
        game.batch.end();

        if (Gdx.input.justTouched()) {
            if (hoverJogar) {
                game.setScreen(new Game());
            }
            if (hoverCreditos) {
                mostrarCreditos = true;
            }
            if (hoverSair) {
                Gdx.app.exit();
            }
        }
    }

    private void renderCreditos() {
        boolean hoverVoltar = botaoVoltar.contains(mouse.x, mouse.y);

        shape.begin(ShapeType.Filled);
        desenharFundo();
        desenharFaixa();
        desenharFormaBotao(botaoVoltar, cinzaBotao, hoverVoltar);
        shape.end();

        game.batch.begin();
        desenharTituloCreditos();
        desenharNomes();
        desenharTextoBotao(botaoVoltar, "VOLTAR", hoverVoltar);
        game.font.getData().setScale(1f);
        game.font.setColor(Color.WHITE);
        game.batch.end();

        if (Gdx.input.justTouched() && hoverVoltar) {
            mostrarCreditos = false;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            mostrarCreditos = false;
        }
    }

    private void desenharFundo() {
        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();
        shape.rect(0, 0, w, h, asfaltoBaixo, asfaltoBaixo, asfaltoCima, asfaltoCima);
    }

    private void desenharFaixa() {
        float w = viewport.getWorldWidth();
        float larguraFaixa = 40;
        float espaco = 30;
        float passo = larguraFaixa + espaco;
        float deslocamento = (tempo * 40f) % passo;

        shape.setColor(Color.WHITE);
        for (float x = -passo + deslocamento; x < w; x += passo) {
            shape.rect(x, 0, larguraFaixa, 36);
        }
    }

    private void desenharSemaforo() {
        float x = viewport.getWorldWidth() - 70;
        float y = viewport.getWorldHeight() - 130;
        boolean verde = ((int) (tempo / 2f)) % 2 == 1;

        shape.setColor(0.10f, 0.10f, 0.12f, 1f);
        shape.rect(x - 18, y - 4, 36, 100);

        if (verde) {
            shape.setColor(vermelhoApagado);
        } else {
            shape.setColor(Color.RED);
        }
        shape.circle(x, y + 72, 11);

        shape.setColor(amareloApagado);
        shape.circle(x, y + 40, 11);

        if (verde) {
            shape.setColor(Color.GREEN);
        } else {
            shape.setColor(verdeApagado);
        }
        shape.circle(x, y + 8, 11);
    }

    private void desenharFormaBotao(Rectangle botao, Color cor, boolean hover) {
        float aumento = 0;
        if (hover) {
            aumento = 6;
        }

        shape.setColor(0f, 0f, 0f, 0.4f);
        shape.rect(botao.x - aumento + 4, botao.y - aumento - 4,
            botao.width + aumento * 2, botao.height + aumento * 2);

        if (hover) {
            shape.setColor(amarelo);
        } else {
            shape.setColor(cor);
        }
        shape.rect(botao.x - aumento, botao.y - aumento,
            botao.width + aumento * 2, botao.height + aumento * 2);
    }

    private void desenharTitulo() {
        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();

        game.font.getData().setScale(2.6f);
        game.font.setColor(amarelo);
        layout.setText(game.font, "THE RIGHT STEP");
        game.font.draw(game.batch, "THE RIGHT STEP", (w - layout.width) / 2f, h - 50);

        game.font.getData().setScale(1.2f);
        game.font.setColor(Color.WHITE);
        layout.setText(game.font, "Guardião da Faixa");
        game.font.draw(game.batch, "Guardião da Faixa", (w - layout.width) / 2f, h - 105);
    }

    private void desenharTituloCreditos() {
        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();

        game.font.getData().setScale(2f);
        game.font.setColor(amarelo);
        layout.setText(game.font, "CREDITOS");
        game.font.draw(game.batch, "CREDITOS", (w - layout.width) / 2f, h - 40);
    }

    private void desenharNomes() {
        float w = viewport.getWorldWidth();
        float h = viewport.getWorldHeight();
        float y = h - 100;

        game.font.getData().setScale(1f);
        game.font.setColor(Color.WHITE);
        for (int i = 0; i < nomes.length; i++) {
            layout.setText(game.font, nomes[i]);
            game.font.draw(game.batch, nomes[i], (w - layout.width) / 2f, y);
            y -= 28;
        }
    }

    private void desenharFrase() {
        String frase = "Ter consciência é o primeiro passo para ser um bom cidadão.";
        float w = viewport.getWorldWidth();

        game.font.getData().setScale(0.9f);
        game.font.setColor(cinzaClaro);
        layout.setText(game.font, frase);
        game.font.draw(game.batch, frase, (w - layout.width) / 2f, 70);
    }

    private void desenharTextoBotao(Rectangle botao, String texto, boolean hover) {
        game.font.getData().setScale(1.3f);
        if (hover) {
            game.font.setColor(textoEscuro);
        } else {
            game.font.setColor(Color.WHITE);
        }
        layout.setText(game.font, texto);
        game.font.draw(game.batch, texto,
            botao.x + (botao.width - layout.width) / 2f,
            botao.y + (botao.height + layout.height) / 2f);
    }

    @Override
    public void dispose() {
        shape.dispose();
    }
}
