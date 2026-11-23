package jogo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Arrays;

/**
 * Ligar os radiadores em ordem crescente de temperatura (do mais frio ao mais quente).
 * Errou a ordem? Tudo recomeça.
 */
public class PuzzleOrdem extends Puzzle {

    private final int[] valores = { 40, -10, 120, 0, -50, 15 };
    private final int[] ordenados;
    private final boolean[] ligado = new boolean[6];
    private int proximo = 0;
    private double fim = -1;
    private double tempoErro = 0;

    public PuzzleOrdem(SistemaNave sistema) {
        super(sistema);
        ordenados = valores.clone();
        Arrays.sort(ordenados);
    }

    @Override
    protected String instrucao() {
        return "Ligue os radiadores do mais FRIO para o mais QUENTE, clicando na ordem certa.";
    }

    private Rectangle quadro(int i) {
        return new Rectangle(CX + 35 + (i % 3) * 270, CY + 20 + (i / 3) * 125, 230, 100);
    }

    @Override
    protected void andar(double dt) {
        if (tempoErro > 0) {
            tempoErro -= dt;
        }
        if (fim >= 0) {
            fim += dt;
            if (fim > 1.1) {
                resolvido = true;
            }
        }
    }

    @Override
    protected void desenharConteudo(Graphics2D g) {
        for (int i = 0; i < 6; i++) {
            Rectangle r = quadro(i);
            boolean sobre = !ligado[i] && fim < 0 && r.contains(mouseX, mouseY);
            Color fundo = ligado[i] ? new Color(35, 135, 80) : (sobre ? new Color(60, 80, 145) : new Color(30, 40, 85));
            if (tempoErro > 0 && !ligado[i]) {
                fundo = new Color(150, 50, 60);
            }
            Desenho.caixa(g, r.x, r.y, r.width, r.height, fundo, new Color(120, 200, 255));
            Desenho.imagemAjustada(g, sistema.imagem(), r.x + 42, r.y + 50, 56);
            g.setFont(F_GRANDE);
            Desenho.texto(g, valores[i] + " °C", r.x + 90, r.y + 62, Color.WHITE);
            if (ligado[i]) {
                g.setFont(F_PEQUENA_B);
                Desenho.texto(g, "OK", r.x + r.width - 38, r.y + 26, new Color(190, 255, 200));
            }
        }
        g.setFont(F_MEDIA);
        Desenho.textoCentro(g, "Ligados: " + proximo + " de 6", CX + CW / 2, CY + 270, Color.WHITE);
    }

    @Override
    public void clique(int x, int y) {
        if (fim >= 0) {
            return;
        }
        for (int i = 0; i < 6; i++) {
            if (!ligado[i] && quadro(i).contains(x, y)) {
                som();
                if (valores[i] == ordenados[proximo]) {
                    ligado[i] = true;
                    proximo++;
                    if (proximo >= 6) {
                        fim = 0;
                        dizer("Temperatura estabilizada! Peça encaixada!", VERDE);
                    }
                } else {
                    Arrays.fill(ligado, false);
                    proximo = 0;
                    tempoErro = 0.5;
                    dizer("Fora de ordem! Os radiadores reiniciaram.", VERMELHO);
                }
            }
        }
    }
}
