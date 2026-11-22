package jogo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.Random;

/**
 * Rodas de reação: todas as setas precisam apontar para CIMA.
 * Cada clique gira a roda clicada E a vizinha da direita (a última gira a primeira).
 * A bagunça inicial é feita com cliques de verdade, então SEMPRE tem solução.
 */
public class PuzzleRodas extends Puzzle {

    private static final int N = 4;
    private final int[] giros = new int[N];       // quartos de volta (só aumenta, para animar)
    private final double[] angulo = new double[N]; // ângulo mostrado, em graus
    private int cliques = 0;
    private double fim = -1;

    public PuzzleRodas(SistemaNave sistema) {
        super(sistema);
        Random sorteio = new Random();
        do {
            for (int i = 0; i < N; i++) {
                giros[i] = 0;
            }
            for (int k = 0; k < 8; k++) {
                girar(sorteio.nextInt(N));
            }
        } while (tudoAlinhado());
        for (int i = 0; i < N; i++) {
            angulo[i] = giros[i] * 90.0;
        }
    }

    @Override
    protected String instrucao() {
        return "Aponte TODAS as rodas para cima. Cada clique gira a roda e também a vizinha da direita.";
    }

    private void girar(int i) {
        giros[i]++;
        giros[(i + 1) % N]++;
    }

    private boolean tudoAlinhado() {
        for (int i = 0; i < N; i++) {
            if (giros[i] % 4 != 0) {
                return false;
            }
        }
        return true;
    }

    private int centroX(int i) {
        return CX + 105 + i * 210;
    }

    private int centroY() {
        return CY + 110;
    }

    @Override
    protected void andar(double dt) {
        for (int i = 0; i < N; i++) {
            double alvo = giros[i] * 90.0;
            if (angulo[i] < alvo) {
                angulo[i] = Math.min(alvo, angulo[i] + 600 * dt);
            }
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
        for (int i = 0; i < N; i++) {
            int cx = centroX(i), cy = centroY(), r = 66;
            boolean certa = giros[i] % 4 == 0;
            boolean sobre = fim < 0 && Math.hypot(mouseX - cx, mouseY - cy) < r;

            g.setColor(certa ? new Color(80, 255, 140) : new Color(255, 90, 90));
            g.fillOval(cx - 8, cy - r - 30, 16, 16);

            g.setColor(sobre ? new Color(75, 85, 130) : new Color(50, 56, 92));
            g.fillOval(cx - r, cy - r, 2 * r, 2 * r);
            g.setColor(new Color(150, 160, 200));
            g.setStroke(new BasicStroke(4f));
            g.drawOval(cx - r, cy - r, 2 * r, 2 * r);
            g.setStroke(new BasicStroke(1f));
            g.setColor(new Color(255, 255, 255, 120));
            g.fillRect(cx - 2, cy - r - 2, 4, 10);

            AffineTransform original = g.getTransform();
            g.translate(cx, cy);
            g.rotate(Math.toRadians(angulo[i]));
            g.setColor(new Color(255, 205, 70));
            g.fillPolygon(new int[] { 0, 20, -20 }, new int[] { -52, -14, -14 }, 3);
            g.fillRect(-7, -16, 14, 54);
            g.setTransform(original);

            g.setFont(F_PEQUENA_B);
            Desenho.textoCentro(g, String.valueOf(i + 1), cx, cy + r + 28, new Color(200, 200, 230));
            if (i < N - 1) {
                g.setFont(F_MEDIA);
                Desenho.textoCentro(g, ">", cx + 105, cy + 8, new Color(150, 150, 190));
            }
        }
        g.setFont(F_MEDIA);
        Desenho.textoCentro(g, "Cliques: " + cliques + "   (aperte 1-4 ou clique nas rodas)", CX + CW / 2, CY + 270,
                Color.WHITE);
    }

    @Override
    public void tecla(int codigo) {
        if (codigo >= KeyEvent.VK_1 && codigo <= KeyEvent.VK_4) {
            apertar(codigo - KeyEvent.VK_1);
        }
    }

    @Override
    public void clique(int x, int y) {
        for (int i = 0; i < N; i++) {
            if (Math.hypot(x - centroX(i), y - centroY()) < 66) {
                apertar(i);
            }
        }
    }

    private void apertar(int i) {
        if (fim >= 0) {
            return;
        }
        girar(i);
        cliques++;
        som();
        if (tudoAlinhado()) {
            fim = 0;
            dizer("Nave alinhada! Peça encaixada!", VERDE);
        }
    }
}
