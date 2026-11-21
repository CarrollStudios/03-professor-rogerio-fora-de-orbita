package jogo;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Caixa de fala no estilo da Carroll Studios, em pixel art.
 * Ela é desenhada numa "tela pequena" (320 x 180), que depois é aumentada 4 vezes.
 * Pode ser usada em qualquer cena: na sala, nas fases, no final...
 * Feita por Gih (Front-end) - Carroll Studios.
 */
public class CaixaDeFala {

    // Cores da identidade visual
    private static final Color FUNDO = new Color(0x1A, 0x17, 0x30, 235);
    private static final Color BORDA = new Color(0x8B5CF6);
    private static final Color ETIQUETA = new Color(0x6D28D9);
    private static final Color TEXTO = new Color(0xE9E4FF);

    // Posição e tamanho da caixa na tela pequena (320 x 180)
    private static final int X = 10;
    private int y = 138;   // altura da caixa (138 = embaixo da tela)
    private static final int LARGURA = 300;
    private static final int ALTURA = 37;
    private static final int ESPACO = 4;   // espaço entre as palavras

    // Caixa embaixo da tela (posição padrão)
    public CaixaDeFala() {
    }

    // Caixa numa altura escolhida (ex.: 4 = em cima da tela)
    public CaixaDeFala(int y) {
        this.y = y;
    }

    // g = pincel da tela pequena; nome = quem fala; textoVisivel = o pedaço do texto que já apareceu
    public void desenhar(Graphics2D g, String nome, String textoVisivel) {

        // caixa
        g.setColor(FUNDO);
        g.fillRoundRect(X, y, LARGURA, ALTURA, 6, 6);
        g.setColor(BORDA);
        g.drawRoundRect(X, y, LARGURA, ALTURA, 6, 6);

        // etiqueta com o nome
        g.setFont(new Font("SansSerif", Font.BOLD, 9));
        FontMetrics m = g.getFontMetrics();
        int larguraNome = m.stringWidth(nome) + 10;
        g.setColor(ETIQUETA);
        g.fillRoundRect(X + 6, y - 5, larguraNome, 12, 6, 6);
        g.setColor(Color.WHITE);
        g.drawString(nome, X + 11, y + 4);

        // texto, quebrado em linhas para caber na caixa
        g.setFont(new Font("SansSerif", Font.BOLD, 10));
        g.setColor(TEXTO);
        int linhaY = y + 20;
        FontMetrics mt = g.getFontMetrics();
        for (String linha : quebrarEmLinhas(textoVisivel, mt, LARGURA - 20)) {
            // escreve palavra por palavra, com um espaço fixo entre elas
            // (em letras pequenas o espaço normal fica apertado demais)
            int x = X + 10;
            for (String palavra : linha.split(" ")) {
                g.drawString(palavra, x, linhaY);
                x += mt.stringWidth(palavra) + ESPACO;
            }
            linhaY += 11;
        }
    }

    // Divide o texto em linhas que caibam na largura máxima
    private List<String> quebrarEmLinhas(String texto, FontMetrics m, int larguraMaxima) {

        List<String> linhas = new ArrayList<>();
        String linhaAtual = "";

        for (String palavra : texto.split(" ")) {
            String teste = linhaAtual.isEmpty() ? palavra : linhaAtual + " " + palavra;
            if (largura(teste, m) > larguraMaxima && !linhaAtual.isEmpty()) {
                linhas.add(linhaAtual);
                linhaAtual = palavra;
            } else {
                linhaAtual = teste;
            }
        }
        linhas.add(linhaAtual);
        return linhas;
    }

    // Largura de uma linha usando o nosso espaço fixo entre as palavras
    private int largura(String linha, FontMetrics m) {
        int total = 0;
        for (String palavra : linha.split(" ")) {
            total += m.stringWidth(palavra) + ESPACO;
        }
        return total - ESPACO;
    }
}
