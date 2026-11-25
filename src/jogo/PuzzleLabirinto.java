package jogo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * Navegação (3 níveis: o mapa cresce com a fase): guie a nave pelo campo de asteroides até a Terra
 * (setas ou WASD; R volta para o início). A nave tem combustível limitado:
 * se acabar, ela volta ao início e a Zoe perde 1 de oxigênio.
 * O ET pode ajudar (2 dicas na fase 3, tecla H): ele ilumina a rota.
 */
public class PuzzleLabirinto extends Puzzle {

    // # = asteroide, S = saída, G = destino (Terra). Um mapa por nível: 0 = fácil, 1 = médio, 2 = difícil.
    // Dá para editar os mapas à vontade, só lembre de deixar um caminho livre e todas as linhas do mesmo tamanho.
    private static final String[][] MAPAS = {
            { "S...#.....",
              "...#..#...",
              "#.##......",
              "#.#.#.....",
              "......#..G" },
            { "S...##.......",
              "...#.#.#.....",
              "..#.#..##....",
              "..#...#.#....",
              "#.#..###.#...",
              "#...##..###.G" },
            { "S.#...##......#",
              "..#....#...#..#",
              "..#..#...#.#..#",
              "#..#..#..#.#..#",
              ".#.##..#####...",
              "##...#..##.#...",
              "..........#...G" } };
    private static final int[] TAMANHO_CASA = { 52, 46, 40 };
    private static final int[] FOLGA = { 16, 14, 14 };      // movimentos de combustível além do caminho mais curto

    private final String[] mapa;
    private final int casa;
    private final int linhas;
    private final int colunas;
    private int nx, ny;       // casa da nave
    private double direcao = 90;   // 0 = cima, 90 = direita, 180 = baixo, 270 = esquerda
    private double fim = -1;
    private int combustivel;
    private final int combustivelTotal;
    private List<int[]> rota = new ArrayList<>();   // casas que o ET está iluminando
    private double tempoRota = 0;

    // nivel: 0 = fácil (mapa pequeno), 1 = médio, 2 = difícil (mapa grande e cheio de desvios)
    public PuzzleLabirinto(SistemaNave sistema, int nivel) {
        super(sistema);
        mapa = MAPAS[nivel];
        casa = TAMANHO_CASA[nivel];
        linhas = mapa.length;
        colunas = mapa[0].length();
        recomecar();
        combustivelTotal = caminho().size() + FOLGA[nivel];
        combustivel = combustivelTotal;
    }

    // Caminho mais curto da nave até a Terra (busca em largura); a lista não inclui a casa onde a nave está
    private List<int[]> caminho() {
        int[][][] veioDe = new int[linhas][colunas][];
        ArrayDeque<int[]> fila = new ArrayDeque<>();
        fila.add(new int[] { nx, ny });
        veioDe[ny][nx] = new int[] { -1, -1 };
        int[] fimCasa = null;
        while (!fila.isEmpty()) {
            int[] c = fila.poll();
            if (mapa[c[1]].charAt(c[0]) == 'G') {
                fimCasa = c;
                break;
            }
            int[][] passos = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };
            for (int[] p : passos) {
                int x = c[0] + p[0], y = c[1] + p[1];
                if (x >= 0 && y >= 0 && x < colunas && y < linhas && mapa[y].charAt(x) != '#' && veioDe[y][x] == null) {
                    veioDe[y][x] = c;
                    fila.add(new int[] { x, y });
                }
            }
        }
        List<int[]> r = new ArrayList<>();
        int[] c = fimCasa;
        while (c != null && veioDe[c[1]][c[0]][0] != -1) {
            r.add(0, c);
            c = veioDe[c[1]][c[0]];
        }
        return r;
    }

    @Override
    protected String[] dicas() {
        return new String[] {
                "Esse campo de asteroides é uma armadilha! Vou iluminar o começo da rota para você. "
                        + "Siga as luzes e depois continue por conta própria.",
                "Ah, vocês terráqueos... Está bem! Vou iluminar a rota inteira até a Terra. "
                        + "Mas as luzes se apagam logo, então memorize!" };
    }

    @Override
    protected void aplicarDica(int n) {
        List<int[]> todo = caminho();
        rota = n == 0 ? new ArrayList<>(todo.subList(0, Math.min(8, todo.size()))) : todo;
        tempoRota = n == 0 ? 9 : 14;
    }

    private void recomecar() {
        for (int l = 0; l < linhas; l++) {
            for (int c = 0; c < colunas; c++) {
                if (mapa[l].charAt(c) == 'S') {
                    nx = c;
                    ny = l;
                }
            }
        }
        direcao = 90;
    }

    @Override
    protected String instrucao() {
        return "Guie a nave até a Terra. Setas/WASD movem, R volta ao início. Combustível limitado!";
    }

    @Override
    protected void andar(double dt) {
        if (tempoRota > 0) {
            tempoRota -= dt;
            if (tempoRota <= 0) {
                rota.clear();
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
        int ox = CX + (CW - colunas * casa) / 2;
        int oy = CY + 8;
        g.setColor(new Color(8, 10, 28));
        g.fillRect(ox - 6, oy - 6, colunas * casa + 12, linhas * casa + 12);
        for (int l = 0; l < linhas; l++) {
            for (int c = 0; c < colunas; c++) {
                int x = ox + c * casa, y = oy + l * casa;
                g.setColor(((l + c) % 2 == 0) ? new Color(22, 26, 62) : new Color(26, 30, 70));
                g.fillRect(x, y, casa, casa);
                char ch = mapa[l].charAt(c);
                if (ch == '#') {
                    g.setColor(new Color(40, 36, 50));
                    g.fillOval(x + 4, y + 5, casa - 8, casa - 10);
                    g.setColor(new Color(130, 120, 140));
                    g.fillOval(x + 6, y + 6, casa - 15, casa - 17);
                    g.setColor(new Color(90, 84, 100));
                    g.fillOval(x + 21, y + 19, 9, 7);
                    g.fillOval(x + 10, y + 24, 6, 5);
                } else if (ch == 'G') {
                    Desenho.terra(g, x + casa / 2.0, y + casa / 2.0, casa / 2.0 - 4);
                }
            }
        }
        // a rota iluminada pelo ET
        if (tempoRota > 0) {
            float brilho = tempoRota < 2 ? (float) (tempoRota / 2) : 1f;
            for (int[] c : rota) {
                g.setColor(new Color(120, 255, 160, (int) (150 * brilho)));
                g.fillRoundRect(ox + c[0] * casa + 10, oy + c[1] * casa + 10, casa - 20, casa - 20, 10, 10);
            }
        }

        // combustível
        g.setFont(F_PEQUENA_B);
        Desenho.texto(g, "Combustível: " + combustivel + " / " + combustivelTotal, ox, oy + linhas * casa + 28,
                combustivel <= 6 ? new Color(255, 160, 120) : Color.WHITE);
        g.setColor(new Color(40, 44, 80));
        g.fillRoundRect(ox + 190, oy + linhas * casa + 14, 200, 14, 8, 8);
        g.setColor(combustivel <= 6 ? new Color(255, 120, 90) : new Color(110, 215, 255));
        g.fillRoundRect(ox + 190, oy + linhas * casa + 14, (int) (200.0 * combustivel / combustivelTotal), 14, 8, 8);

        // a nave (foguetinho desenhado em código)
        AffineTransform original = g.getTransform();
        g.translate(ox + nx * casa + casa / 2.0, oy + ny * casa + casa / 2.0);
        g.scale(0.72 * casa / 40.0, 0.72 * casa / 40.0);
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
            recomecar();     // volta para o início, mas o combustível gasto continua gasto
            return;
        } else {
            return;
        }
        int x = nx + dx, y = ny + dy;
        if (x < 0 || y < 0 || x >= colunas || y >= linhas || mapa[y].charAt(x) == '#') {
            dizer("Cuidado com os asteroides!", new Color(255, 160, 120));
            return;
        }
        nx = x;
        ny = y;
        som();
        combustivel--;
        if (mapa[ny].charAt(nx) == 'G') {
            fim = 0;
            dizer("Rota traçada! Peça encaixada!", VERDE);
        } else if (combustivel <= 0) {
            errou();
            combustivel = combustivelTotal;
            recomecar();
            dizer("Sem combustível! A nave voltou ao início.", VERMELHO);
        }
    }
}
