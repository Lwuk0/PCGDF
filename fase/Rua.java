package dev.pcgdf.fase;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import dev.pcgdf.entities.Car;

// Faixa de transito: asfalto, veiculos e geracao periodica.
public class Rua {
    private final float posY;
    private final float velocidade;
    private final float larguraMundo;
    private final float gridSize;
    private float spawnTimer;
    private final Array<Car> veiculos = new Array<>();

    public Rua(float posY, float velocidade, float larguraMundo, float gridSize) {
        this.posY = posY;
        this.velocidade = velocidade;
        this.larguraMundo = larguraMundo;
        this.gridSize = gridSize;
        spawnTimer = MathUtils.random(.2f, 1.5f);
    }

    // Gera veiculos e atualiza os existentes sem depender de semaforos
    public void atualizar(float delta) {
        spawnTimer -= delta;
        if (spawnTimer <= 0f) {
            gerarVeiculo();
            spawnTimer = MathUtils.random(1.2f, 2.8f);
        }
        // Iteracao reversa permite remover sem pular elementos.
        for (int i = veiculos.size - 1; i >= 0; i--) {
            Car carro = veiculos.get(i);
            carro.update(delta);
            if (carro.isOffScreen(larguraMundo)) {
                carro.finalizar();
                veiculos.removeIndex(i);
            }
        }
    }

    private void gerarVeiculo() {
        float largura = MathUtils.random(60f, 100f);
        float altura = gridSize - 10f;
        float x = velocidade > 0 ? -largura : larguraMundo;
        veiculos.add(new Car(x, posY + 5f, largura, altura, velocidade));
    }

    // Desenha primeiro o asfalto e depois os veiculos.
    public void render(ShapeRenderer renderer) {
        renderer.setColor(.22f, .22f, .25f, 1f);
        renderer.rect(0f, posY, larguraMundo, gridSize);
        for (Car carro : veiculos) carro.render(renderer);
    }

    public Iterable<Car> getVeiculos() { return veiculos; }

    public void descarregar() {
        for (Car carro : veiculos) carro.finalizar();
        veiculos.clear();
    }
}
