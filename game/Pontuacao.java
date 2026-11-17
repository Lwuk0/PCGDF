package dev.pcgdf.game;

import dev.pcgdf.entities.Player;
import dev.pcgdf.fase.EventoFase;
import dev.pcgdf.fase.Fase;

// Gerencia pontuacao, vidas (via Player) e estado da partida.
public class Pontuacao {
    public static final int PONTOS_POR_TRAVESSIA = 100;
    public static final int TRAVESSIAS_PARA_VENCER = 5;
    private final Fase fase;
    private int pontos;
    private int travessias;
    private EstadoJogo estado;

    public Pontuacao(float larguraMundo, float alturaMundo) {
        fase = new Fase(larguraMundo, alturaMundo);
        iniciar();
    }

    public void iniciar() {
        fase.descarregar();
        fase.carregar();
        pontos = 0;
        travessias = 0;
        estado = EstadoJogo.JOGANDO;
    }

    public void atualizar(float delta) {
        if (estado != EstadoJogo.JOGANDO) return;
        EventoFase evento = fase.atualizar(delta);
        if (evento == EventoFase.ATROPELAMENTO) {
            Player player = fase.getPlayer();
            player.perderVida();
            if (player.getVidas() == 0) estado = EstadoJogo.DERROTA;
            else fase.reposicionarJogador();
        } else if (evento == EventoFase.TRAVESSIA) {
            pontos += PONTOS_POR_TRAVESSIA;
            travessias++;
            if (travessias >= TRAVESSIAS_PARA_VENCER) estado = EstadoJogo.VITORIA;
            else fase.reposicionarJogador();
        }
    }

    public void finalizar() { fase.descarregar(); }
    public Fase getFase() { return fase; }
    public EstadoJogo getEstado() { return estado; }
    public int getPontos() { return pontos; }
    public int getTravessias() { return travessias; }
    public int getVidas() {
        Player player = fase.getPlayer();
        return player == null ? 0 : player.getVidas();
    }
}
