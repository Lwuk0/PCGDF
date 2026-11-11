package dev.pcgdf;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;

public class TitleScreen extends ScreenAdapter {
    private final Main game;
    private Texture image2;
    private float tempo = 0f;
    private final float duracao = 2f;

    public TitleScreen(Main game) {
        this.game = game;
    }

    @Override
    public void show() {
        image2 = new Texture("sinal de transito.png");
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        tempo += delta;

        int mouseX = Gdx.input.getX();
        int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

        boolean hoverJogar = mouseX >= 90 && mouseX <= 210 && mouseY >= 280 && mouseY <= 320;
        boolean hoverSair = mouseX >= 90 && mouseX <= 210 && mouseY >= 230 && mouseY <= 270;
        boolean hoverCreditos = mouseX >= 90 && mouseX <= 210 && mouseY >= 180 && mouseY <= 220;
        game.batch.begin();

        if (tempo < duracao) {
            game.batch.draw(game.image, 140, 210);
        } else {
            game.batch.draw(image2, 0, 0);
        }

        if (hoverJogar) {
            game.font.setColor(Color.GOLD);
            game.font.draw(game.batch, "> JOGAR <", 90, 300);
        } else {
            game.font.setColor(Color.WHITE);
            game.font.draw(game.batch, "  JOGAR  ", 100, 300);
        }

        if (hoverSair) {
            game.font.setColor(Color.GOLD);
            game.font.draw(game.batch, "> SAIR <", 95, 250);
        } else {
            game.font.setColor(Color.WHITE);
            game.font.draw(game.batch, "  SAIR  ", 100, 250);
        }

        if (hoverCreditos){
            game.font.setColor(Color.GOLD);
            game.font.draw(game.batch, "> CREDITOS <", 100, 200);
        }else {
            game.font.setColor(Color.WHITE);
            game.font.draw(game.batch, "  CREDITOS  ", 100, 200);
        }

        game.font.setColor(Color.WHITE);

        game.batch.end();

        if (Gdx.input.justTouched()) {
            if (hoverJogar) {
                System.out.println("Botão Jogar clicado!");
            }

            if (hoverSair) {
                Gdx.app.exit();
            }

            if(hoverCreditos){
                System.out.println("Botão creditos clicado!");
            }
        }
    }

    @Override
    public void dispose() {
        image2.dispose();
    }
}
