package jogo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Rodas de reação (puzzle difícil): todas as setas precisam apontar para CIMA.
 * Cada clique gira a roda clicada E a vizinha da direita (a última gira a primeira).
 * A bagunça inicial é feita com cliques de verdade, então SEMPRE tem solução.
 * Atenção: existe um limite de cliques. Passou do limite? A nave sobrecarrega,
 * as rodas voltam para o começo e a Zoe perde 1 de oxigênio.
 * O ET pode ajudar (2 dicas na fase 3, tecla H).
 */
public class PuzzleRodas extends Puzzle {

    private final int N;                       // quantas rodas: 3 (fácil), 4 (médio) ou 5 (difícil)
    private final int folga;                   // cliques além do mínimo que o jogador pode usar

    private final int[] giros;                     // quartos de volta (só aumenta, para animar)
    private final int[] inicial;                   // como a bagunça começou
    private final double[] angulo;                 // ângulo mostrado, em graus
    private final int limite;
    private int cliques = 0;
    private double fim = -1;
    private int marcada = -1;                      // roda que o ET mandou clicar (-1 = nenhuma)

    // rodas: 3 = fácil, 4 = médio, 5 = difícil
    public PuzzleRodas(SistemaNave sistema, int rodas) {
        super(sistema);
        N = rodas;
        folga = rodas <= 3 ? 7 : rodas == 4 ? 6 : 5;
        giros = new int[N];
        inicial = new int[N];
        angulo = new double[N];
        Random sorteio = new Random();
        do {
            for (int i = 0; i < N; i++) {
                giros[i] = 0;
            }
            for (int k = 0; k < 12; k++) {
                girar(sorteio.nextInt(N));
            }
        } while (tudoAlinhado() || caminhoMinimo().length < N);
        for (int i = 0; i < N; i++) {
            angulo[i] = giros[i] * 90.0;
            inicial[i] = giros[i];
        }
        limite = caminhoMinimo().length + folga;
    }

    @Override
    protected String instrucao() {
        return "Aponte TODAS as rodas para cima. Cada clique gira a roda e também a vizinha da direita. "
                + "Cuidado com o limite de cliques!";
    }

    @Override
    protected String[] dicas() {
        return new String[] {
                "Cada roda gira quando você clica nela OU na roda anterior. A roda 1, por exemplo, só se mexe "
                        + "com cliques na 1 e na " + N + ". Teste uma roda de cada vez e veja quem mais se mexe!",
                "Tudo bem, vou te mostrar o melhor próximo passo: clique na roda marcada pela seta verde. "
                        + "Se pedir outra dica, eu repito esta." };
    }

    @Override
    protected void aplicarDica(int n) {
        if (n == 1) {
            marcada = proximaJogada();
        }
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

    // Menor sequência de cliques que resolve a situação atual (busca em largura)
    private int[] caminhoMinimo() {
        int[] inicio = new int[N];
        for (int i = 0; i < N; i++) {
            inicio[i] = giros[i] % 4;
        }
        Map<Integer, int[]> veioDe = new HashMap<>();   // estado -> {estado anterior, roda clicada}
        ArrayDeque<int[]> fila = new ArrayDeque<>();
        fila.add(inicio);
        veioDe.put(codigo(inicio), new int[] { -1, -1 });
        while (!fila.isEmpty()) {
            int[] e = fila.poll();
            if (codigo(e) == 0) {
                break;
            }
            for (int c = 0; c < N; c++) {
                int[] n = e.clone();
                n[c] = (n[c] + 1) % 4;
                n[(c + 1) % N] = (n[(c + 1) % N] + 1) % 4;
                if (!veioDe.containsKey(codigo(n))) {
                    veioDe.put(codigo(n), new int[] { codigo(e), c });
                    fila.add(n);
                }
            }
        }
        java.util.ArrayList<Integer> jogadas = new java.util.ArrayList<>();
        int atual = 0;
        while (veioDe.containsKey(atual) && veioDe.get(atual)[0] != -1) {
            jogadas.add(0, veioDe.get(atual)[1]);
            atual = veioDe.get(atual)[0];
        }
        int[] r = new int[jogadas.size()];
        for (int i = 0; i < r.length; i++) {
            r[i] = jogadas.get(i);
        }
        return r;
    }

    private static int codigo(int[] e) {
        int c = 0;
        for (int v : e) {
            c = c * 4 + v;
        }
        return c;
    }

    private int proximaJogada() {
        int[] caminho = caminhoMinimo();
        return caminho.length == 0 ? -1 : caminho[0];
    }

    private int raio() {
        return N <= 3 ? 64 : N == 4 ? 58 : 52;
    }

    private int passo() {
        return CW / N;
    }

    private int centroX(int i) {
        return CX + passo() / 2 + i * passo();
    }

    private int centroY() {
        return CY + 122;
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
        int r = raio();
        for (int i = 0; i < N; i++) {
            int cx = centroX(i), cy = centroY();
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
            g.fillPolygon(new int[] { 0, 16, -16 }, new int[] { -42, -12, -12 }, 3);
            g.fillRect(-6, -14, 12, 44);
            g.setTransform(original);

            g.setFont(F_PEQUENA_B);
            Desenho.textoCentro(g, String.valueOf(i + 1), cx, cy + r + 26, new Color(200, 200, 230));
            if (i < N - 1) {
                g.setFont(F_MEDIA);
                Desenho.textoCentro(g, ">", cx + passo() / 2, cy + 8, new Color(150, 150, 190));
            }

            // seta verde do ET: "clique aqui"
            if (i == marcada && fim < 0) {
                int pulo = (int) (Math.sin(tempo * 8) * 5);
                g.setColor(new Color(120, 255, 160));
                g.fillPolygon(new int[] { cx - 14, cx + 14, cx }, new int[] { cy - r - 62 + pulo, cy - r - 62 + pulo, cy - r - 40 + pulo }, 3);
                g.setStroke(new BasicStroke(4f));
                g.drawOval(cx - r - 6, cy - r - 6, 2 * r + 12, 2 * r + 12);
                g.setStroke(new BasicStroke(1f));
            }
        }
        g.setFont(F_MEDIA);
        boolean perto = limite - cliques <= 2;
        Desenho.textoCentro(g, "Cliques: " + cliques + " / " + limite + "   (aperte 1-" + N + " ou clique nas rodas)", CX + CW / 2,
                CY + 262, perto ? new Color(255, 170, 130) : Color.WHITE);
    }

    @Override
    public void tecla(int codigo) {
        if (codigo >= KeyEvent.VK_1 && codigo < KeyEvent.VK_1 + N) {
            apertar(codigo - KeyEvent.VK_1);
        }
    }

    @Override
    public void clique(int x, int y) {
        for (int i = 0; i < N; i++) {
            if (Math.hypot(x - centroX(i), y - centroY()) < raio()) {
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
        marcada = -1;
        som();
        if (tudoAlinhado()) {
            fim = 0;
            dizer("Nave alinhada! Peça encaixada!", VERDE);
        } else if (cliques >= limite) {
            // sobrecarga: as rodas voltam para o começo (sempre girando para frente, para animar)
            errou();
            for (int k = 0; k < N; k++) {
                int voltas = (int) Math.ceil((giros[k] - inicial[k]) / 4.0);
                giros[k] = inicial[k] + 4 * voltas;
            }
            cliques = 0;
            dizer("Sobrecarga! Cliques demais: as rodas voltaram ao começo.", VERMELHO);
        }
    }
}
