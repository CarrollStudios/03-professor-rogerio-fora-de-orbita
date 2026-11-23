package jogo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;

/**
 * Navegação: guie a nave pelo campo de asteroides até a Terra
 * (setas ou WASD; R recomeça).
 */
public class PuzzleLabirinto extends Puzzle {

    // # = asteroide, S = saída, G = destino (Terra). Dá para editar o mapa à vontade,
    // só lembre de deixar um caminho livre e todas as linhas com o mesmo tamanho.
    private static final String[] MAPA = {
            "S.#...#....",
            "#.#.#.#.#.#",
            "....#...#..",
            ".##.#.###.#",
            "....#.#...G" };

    private static final int CASA = 56;
    private final int linhas = MAPA.length;
    private final int colunas = MAPA[0].length();
    private int nx, ny;       // casa da nave
    private double direcao = 90;   // 0 = cima, 90 = direita, 180 = baixo, 270 = esquerda
    private double fim = -1;

    public PuzzleLabirinto(SistemaNave sistema) {
        super(sistema);
        recomecar();
    }

    private void recomecar() {
        for (int l = 0; l < linhas; l++) {
            for (int c = 0; c < colunas; c++) {
                if (MAPA[l].charAt(c) == 'S') {
                    nx = c;
                    ny = l;
                }
            }
        }
        direcao = 90;
    }

    @Override
    protected String instrucao() {
        return "Guie a nave até a Terra, desviando dos asteroides. Setas/WASD movem, R recomeça.";
    }

    @Override
    protected void andar(double dt) {
        if (fim >= 0) {
            fim += dt;
            if (fim > 1.1) {
                resolvido = true;
            }
        }
    }

    @Override
    protected void desenharConteudo(Graphics2D g) {
        int ox = CX + (CW - colunas * CASA) / 2;
        int oy = CY + 5;
        g.setColor(new Color(8, 10, 28));
        g.fillRect(ox - 6, oy - 6, colunas * CASA + 12, linhas * CASA + 12);
        for (int l = 0; l < linhas; l++) {
            for (int c = 0; c < colunas; c++) {
                int x = ox + c * CASA, y = oy + l * CASA;
                g.setColor(((l + c) % 2 == 0) ? new Color(22, 26, 62) : new Color(26, 30, 70));
                g.fillRect(x, y, CASA, CASA);
                char ch = MAPA[l].charAt(c);
                if (ch == '#') {
                    g.setColor(new Color(40, 36, 50));
                    g.fillOval(x + 5, y + 6, CASA - 10, CASA - 12);
                    g.setColor(new Color(130, 120, 140));
                    g.fillOval(x + 8, y + 8, CASA - 20, CASA - 22);
                    g.setColor(new Color(90, 84, 100));
                    g.fillOval(x + 28, y + 26, 12, 9);
                    g.fillOval(x + 14, y + 32, 8, 6);
                } else if (ch == 'G') {
                    Desenho.terra(g, x + CASA / 2.0, y + CASA / 2.0, CASA / 2.0 - 4);
                }
            }
        }
        // a nave (foguetinho desenhado em código)
        AffineTransform original = g.getTransform();
        g.translate(ox + nx * CASA + CASA / 2.0, oy + ny * CASA + CASA / 2.0);
        g.rotate(Math.toRadians(direcao));
        g.setColor(new Color(230, 230, 245));
        g.fillPolygon(new int[] { 0, 12, 12, -12, -12 }, new int[] { -22, -4, 16, 16, -4 }, 5);
        g.setColor(new Color(240, 85, 85));
        g.fillPolygon(new int[] { -12, -20, -12 }, new int[] { 6, 20, 16 }, 3);
        g.fillPolygon(new int[] { 12, 20, 12 }, new int[] { 6, 20, 16 }, 3);
        g.setColor(new Color(60, 190, 230));
        g.fillOval(-5, -8, 10, 10);
        g.setTransform(original);
    }

    @Override
    public void tecla(int codigo) {
        if (fim >= 0) {
            return;
        }
        int dx = 0, dy = 0;
        if (codigo == KeyEvent.VK_LEFT || codigo == KeyEvent.VK_A) {
            dx = -1;
            direcao = 270;
        } else if (codigo == KeyEvent.VK_RIGHT || codigo == KeyEvent.VK_D) {
            dx = 1;
            direcao = 90;
        } else if (codigo == KeyEvent.VK_UP || codigo == KeyEvent.VK_W) {
            dy = -1;
            direcao = 0;
        } else if (codigo == KeyEvent.VK_DOWN || codigo == KeyEvent.VK_S) {
            dy = 1;
            direcao = 180;
        } else if (codigo == KeyEvent.VK_R) {
            recomecar();
            return;
        } else {
            return;
        }
        int x = nx + dx, y = ny + dy;
        if (x < 0 || y < 0 || x >= colunas || y >= linhas || MAPA[y].charAt(x) == '#') {
            dizer("Cuidado com os asteroides!", new Color(255, 160, 120));
            return;
        }
        nx = x;
        ny = y;
        som();
        if (MAPA[ny].charAt(nx) == 'G') {
            fim = 0;
            dizer("Rota traçada! Peça encaixada!", VERDE);
        }
    }
}
