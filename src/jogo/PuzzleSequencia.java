package jogo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Random;

/**
 * Puzzle de memória (estilo "Genius"): a nave acende uma sequência de células de energia
 * e o jogador repete. A sequência cresce a cada rodada (4 rodadas).
 */
public class PuzzleSequencia extends Puzzle {

    private static final int RODADAS = 4;
    private static final Color[] CORES = { new Color(70, 130, 255), new Color(70, 205, 110),
            new Color(250, 210, 60), new Color(240, 85, 85) };

    private final int[] sequencia = new int[RODADAS];
    private int rodada = 1;
    // 0 = esperando para mostrar, 1 = mostrando, 2 = jogador repete, 3 = venceu
    private int estado = 0;
    private double espera = 0.9;
    private int mostrando;
    private int acertos;
    private int acesa = -1;
    private double tempoAcesa;

    public PuzzleSequencia(SistemaNave sistema) {
        super(sistema);
        Random sorteio = new Random();
        for (int i = 0; i < RODADAS; i++) {
            sequencia[i] = sorteio.nextInt(4);
        }
    }

    @Override
    protected String instrucao() {
        return "Memorize a sequência de luzes e repita (clique ou aperte 1-4). Rodada " + rodada + " de " + RODADAS + ".";
    }

    private Rectangle botao(int i) {
        return new Rectangle(CX + 45 + i * 200, CY + 40, 150, 150);
    }

    @Override
    protected void andar(double dt) {
        if (tempoAcesa > 0) {
            tempoAcesa -= dt;
            if (tempoAcesa <= 0) {
                acesa = -1;
            }
        }
        if (estado == 0) {
            espera -= dt;
            if (espera <= 0) {
                estado = 1;
                mostrando = 0;
                espera = 0;
            }
        } else if (estado == 1) {
            espera -= dt;
            if (espera <= 0) {
                if (mostrando >= rodada) {
                    estado = 2;
                    acertos = 0;
                    acesa = -1;
                    dizer("Sua vez!", Desenho.AMARELO);
                } else {
                    acesa = sequencia[mostrando];
                    tempoAcesa = 0.45;
                    mostrando++;
                    espera = 0.75;
                }
            }
        } else if (estado == 3) {
            espera -= dt;
            if (espera <= 0) {
                resolvido = true;
            }
        }
    }

    @Override
    protected void desenharConteudo(Graphics2D g) {
        for (int i = 0; i < 4; i++) {
            Rectangle r = botao(i);
            boolean acendeu = (acesa == i);
            boolean sobre = estado == 2 && r.contains(mouseX, mouseY);
            g.setColor(acendeu ? CORES[i] : Desenho.escurecer(CORES[i], sobre ? 0.55f : 0.35f));
            g.fillRoundRect(r.x, r.y, r.width, r.height, 24, 24);
            g.setColor(acendeu ? Color.WHITE : new Color(255, 255, 255, 90));
            g.drawRoundRect(r.x, r.y, r.width, r.height, 24, 24);
            g.setFont(F_GRANDE);
            Desenho.textoCentro(g, String.valueOf(i + 1), r.x + r.width / 2, r.y + r.height / 2 + 12,
                    acendeu ? Color.BLACK : new Color(255, 255, 255, 200));
        }
        g.setFont(F_MEDIA);
        String s = estado < 2 ? "Observe..." : (estado == 2 ? "Repita a sequência!" : "Energia restaurada!");
        Desenho.textoCentro(g, s, CX + CW / 2, CY + 235, Color.WHITE);
    }

    @Override
    public void tecla(int codigo) {
        if (codigo >= KeyEvent.VK_1 && codigo <= KeyEvent.VK_4) {
            apertar(codigo - KeyEvent.VK_1);
        }
    }

    @Override
    public void clique(int x, int y) {
        for (int i = 0; i < 4; i++) {
            if (botao(i).contains(x, y)) {
                apertar(i);
            }
        }
    }

    private void apertar(int i) {
        if (estado != 2) {
            return;
        }
        acesa = i;
        tempoAcesa = 0.25;
        if (i == sequencia[acertos]) {
            som();
            acertos++;
            if (acertos >= rodada) {
                if (rodada >= RODADAS) {
                    estado = 3;
                    espera = 1.1;
                    dizer("Energia restaurada! Peça encaixada!", VERDE);
                } else {
                    rodada++;
                    estado = 0;
                    espera = 0.9;
                    dizer("Muito bem! A sequência vai crescer...", VERDE);
                }
            }
        } else {
            dizer("Sequência errada! Observe de novo.", VERMELHO);
            estado = 0;
            espera = 1.0;
        }
    }
}
