package jogo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Puzzle de colocar em ORDEM: o jogador clica nos itens na ordem certa.
 * Errou a ordem? Tudo recomeça (e a Zoe perde 1 de oxigênio).
 * Existem 3 versões, uma para cada nível, e os itens são sorteados a cada vez:
 *   temperaturas() - fácil:  4 radiadores, do mais frio ao mais quente
 *   planetas()     - médio:  5 planetas, do mais perto ao mais longe do Sol
 *   programa()     - difícil: 6 passos de como um programa Java roda
 */
public class PuzzleOrdem extends Puzzle {

    private final String instrucao;
    private final String[] textos;      // textos na ordem em que aparecem na tela
    private final int[] posicao;        // posicao[i] = lugar certo do item i (0 = primeiro a clicar)
    private final boolean comIcone;
    private final String[] dicasTexto;
    private final boolean[] ligado;
    private int proximo = 0;
    private int marcado = -1;           // item que o ET mostrou (seta verde)
    private double fim = -1;
    private double tempoErro = 0;

    // emOrdem = os itens já na ordem CERTA (a tela mostra embaralhados)
    public PuzzleOrdem(SistemaNave sistema, String instrucao, String[] emOrdem, boolean comIcone, String[] dicas) {
        super(sistema);
        this.instrucao = instrucao;
        this.comIcone = comIcone;
        this.dicasTexto = dicas == null ? new String[0] : dicas;
        int n = emOrdem.length;
        List<Integer> ordem = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            ordem.add(i);
        }
        do {
            Collections.shuffle(ordem);
        } while (jaEstaEmOrdem(ordem));
        textos = new String[n];
        posicao = new int[n];
        for (int i = 0; i < n; i++) {
            textos[i] = emOrdem[ordem.get(i)];
            posicao[i] = ordem.get(i);
        }
        ligado = new boolean[n];
    }

    private static boolean jaEstaEmOrdem(List<Integer> l) {
        for (int i = 0; i < l.size(); i++) {
            if (l.get(i) != i) {
                return false;
            }
        }
        return true;
    }

    // ---------------------------------------------------------------- as 3 versões

    // Nível fácil: do mais FRIO ao mais QUENTE
    public static PuzzleOrdem temperaturas(SistemaNave s) {
        Integer[] pool = { -50, -20, -10, 0, 15, 40, 85, 120, 150 };
        List<Integer> lista = new ArrayList<>(Arrays.asList(pool));
        Collections.shuffle(lista);
        List<Integer> escolhidas = new ArrayList<>(lista.subList(0, 4));
        Collections.sort(escolhidas);
        String[] itens = new String[4];
        for (int i = 0; i < 4; i++) {
            itens[i] = escolhidas.get(i) + " °C";
        }
        return new PuzzleOrdem(s, "Ligue os radiadores do mais FRIO para o mais QUENTE, clicando na ordem certa.", itens, true,
                null);
    }

    // Nível médio: do mais PERTO ao mais LONGE do Sol
    public static PuzzleOrdem planetas(SistemaNave s) {
        String[] todos = { "Mercúrio", "Vênus", "Terra", "Marte", "Júpiter", "Saturno", "Urano", "Netuno" };
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < todos.length; i++) {
            indices.add(i);
        }
        Collections.shuffle(indices);
        List<Integer> escolhidos = new ArrayList<>(indices.subList(0, 5));
        Collections.sort(escolhidos);
        String[] itens = new String[5];
        for (int i = 0; i < 5; i++) {
            itens[i] = todos[escolhidos.get(i)];
        }
        return new PuzzleOrdem(s, "Aponte o radar para os planetas, do mais PERTO para o mais LONGE do Sol.", itens, false,
                null);
    }

    // Nível difícil: a vida de um programa Java
    public static PuzzleOrdem programa(SistemaNave s) {
        String[] passos = { "Escrever o código-fonte (.java)", "Compilar com o javac", "Gerar o bytecode (.class)",
                "Carregar na JVM", "Executar o método main", "Mostrar a saída na tela" };
        return new PuzzleOrdem(s, "Coloque na ordem o que acontece desde escrever até rodar um programa Java.", passos, false,
                new String[] {
                        "Pense em como um programa Java nasce: alguém escreve, o computador traduz e só depois executa.",
                        "Vou marcar com a seta verde o próximo passo certo. Siga a partir dele!" });
    }

    // ---------------------------------------------------------------- o puzzle

    @Override
    protected String instrucao() {
        return instrucao;
    }

    @Override
    protected String[] dicas() {
        return dicasTexto;
    }

    @Override
    protected void aplicarDica(int n) {
        if (n == 1) {
            for (int i = 0; i < textos.length; i++) {
                if (!ligado[i] && posicao[i] == proximo) {
                    marcado = i;
                }
            }
        }
    }

    private Rectangle quadro(int i) {
        return new Rectangle(CX + 35 + (i % 3) * 270, CY + 30 + (i / 3) * 120, 230, 100);
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
        for (int i = 0; i < textos.length; i++) {
            Rectangle r = quadro(i);
            boolean sobre = !ligado[i] && fim < 0 && r.contains(mouseX, mouseY);
            Color fundo = ligado[i] ? new Color(35, 135, 80) : (sobre ? new Color(60, 80, 145) : new Color(30, 40, 85));
            if (tempoErro > 0 && !ligado[i]) {
                fundo = new Color(150, 50, 60);
            }
            Desenho.caixa(g, r.x, r.y, r.width, r.height, fundo, new Color(120, 200, 255));
            if (comIcone) {
                Desenho.imagemAjustada(g, sistema.imagem(), r.x + 42, r.y + 50, 56);
                g.setFont(F_GRANDE);
                Desenho.texto(g, textos[i], r.x + 90, r.y + 62, Color.WHITE);
            } else if (textos[i].length() <= 12) {
                g.setFont(F_MEDIA);
                Desenho.textoCentro(g, textos[i], r.x + r.width / 2, r.y + 60, Color.WHITE);
            } else {
                g.setFont(F_PEQUENA_B);
                Desenho.paragrafo(g, textos[i], r.x + 16, r.y + 40, r.width - 32, 24, Color.WHITE);
            }
            if (ligado[i]) {
                g.setFont(F_PEQUENA_B);
                Desenho.texto(g, (posicao[i] + 1) + "º", r.x + r.width - 38, r.y + 26, new Color(190, 255, 200));
            }
            // seta verde do ET
            if (i == marcado && !ligado[i] && fim < 0) {
                int pulo = (int) (Math.sin(tempo * 8) * 4);
                g.setColor(new Color(120, 255, 160));
                g.setStroke(new BasicStroke(4f));
                g.drawRoundRect(r.x - 5, r.y - 5, r.width + 10, r.height + 10, 14, 14);
                g.setStroke(new BasicStroke(1f));
                g.fillPolygon(new int[] { r.x + r.width / 2 - 12, r.x + r.width / 2 + 12, r.x + r.width / 2 },
                        new int[] { r.y - 22 + pulo, r.y - 22 + pulo, r.y - 6 + pulo }, 3);
            }
        }
        g.setFont(F_MEDIA);
        Desenho.textoCentro(g, "Certos: " + proximo + " de " + textos.length, CX + CW / 2, CY + 285, Color.WHITE);
    }

    @Override
    public void clique(int x, int y) {
        if (fim >= 0) {
            return;
        }
        for (int i = 0; i < textos.length; i++) {
            if (!ligado[i] && quadro(i).contains(x, y)) {
                som();
                marcado = -1;
                if (posicao[i] == proximo) {
                    ligado[i] = true;
                    proximo++;
                    if (proximo >= textos.length) {
                        fim = 0;
                        dizer("Tudo na ordem! Peça encaixada!", VERDE);
                    }
                } else {
                    Arrays.fill(ligado, false);
                    proximo = 0;
                    tempoErro = 0.5;
                    errou();
                    dizer("Fora de ordem! Tudo reiniciou.", VERMELHO);
                }
            }
        }
    }
}
