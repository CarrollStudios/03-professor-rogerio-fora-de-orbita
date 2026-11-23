package jogo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Ajudante de desenho usado pelas cenas novas (mapa, fases, puzzles e final):
 * textos com sombra, caixas, planetas e imagens ajustadas.
 */
public class Desenho {

    public static final Color ROXO = new Color(0x8B5CF6);
    public static final Color FUNDO_CAIXA = new Color(0x1A, 0x17, 0x30, 240);
    public static final Color TEXTO = new Color(0xE9E4FF);
    public static final Color AMARELO = new Color(255, 230, 140);

    // Liga o suavizado só para o texto e as formas (as imagens continuam em pixel art)
    public static void suavizar(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    }

    // Texto com sombrinha (usa a fonte que já estiver no pincel)
    public static void texto(Graphics2D g, String s, int x, int y, Color cor) {
        g.setColor(new Color(0, 0, 0, 170));
        g.drawString(s, x + 2, y + 2);
        g.setColor(cor);
        g.drawString(s, x, y);
    }

    public static void textoCentro(Graphics2D g, String s, int centroX, int y, Color cor) {
        texto(g, s, centroX - g.getFontMetrics().stringWidth(s) / 2, y, cor);
    }

    // Quebra um texto grande em linhas que cabem na largura
    public static List<String> quebrar(FontMetrics fm, String texto, int larguraMax) {
        List<String> linhas = new ArrayList<>();
        StringBuilder atual = new StringBuilder();
        for (String palavra : texto.split(" ")) {
            String teste = atual.length() == 0 ? palavra : atual + " " + palavra;
            if (fm.stringWidth(teste) > larguraMax && atual.length() > 0) {
                linhas.add(atual.toString());
                atual = new StringBuilder(palavra);
            } else {
                atual = new StringBuilder(teste);
            }
        }
        if (atual.length() > 0) {
            linhas.add(atual.toString());
        }
        return linhas;
    }

    // Parágrafo com várias linhas
    public static int paragrafo(Graphics2D g, String texto, int x, int y, int larguraMax, int alturaLinha, Color cor) {
        List<String> linhas = quebrar(g.getFontMetrics(), texto, larguraMax);
        for (int i = 0; i < linhas.size(); i++) {
            texto(g, linhas.get(i), x, y + i * alturaLinha, cor);
        }
        return linhas.size();
    }

    public static void caixa(Graphics2D g, int x, int y, int w, int h, Color fundo, Color borda) {
        g.setColor(fundo);
        g.fillRoundRect(x, y, w, h, 18, 18);
        g.setColor(borda);
        g.setStroke(new BasicStroke(3f));
        g.drawRoundRect(x, y, w, h, 18, 18);
        g.setStroke(new BasicStroke(1f));
    }

    public static Color escurecer(Color c, float fator) {
        return new Color(Math.min(255, (int) (c.getRed() * fator)), Math.min(255, (int) (c.getGreen() * fator)),
                Math.min(255, (int) (c.getBlue() * fator)), c.getAlpha());
    }

    // Desenha uma imagem centralizada em (cx, cy), com no máximo "tamanho" pixels de lado
    public static void imagemAjustada(Graphics2D g, BufferedImage img, double cx, double cy, int tamanho) {
        if (img == null) {
            return;
        }
        double fator = (double) tamanho / Math.max(img.getWidth(), img.getHeight());
        int w = (int) Math.round(img.getWidth() * fator);
        int h = (int) Math.round(img.getHeight() * fator);
        g.drawImage(img, (int) Math.round(cx - w / 2.0), (int) Math.round(cy - h / 2.0), w, h, null);
    }

    // ===== ET (pixel art feita em código) =====

    // '.' = vazio, o = contorno, g = verde, l = verde claro, e = olho, w = brilho do olho, y = amarelo, m = boca
    private static final String[] ET_DESENHO = {
            "..yy........yy..",
            "..yy........yy..",
            "...o........o...",
            "....o......o....",
            ".....oooooooo...",
            "...ooggggggggoo.",
            ".oogglllgggggooo",
            ".ogggggggggggggo",
            ".ogeeeeggggeeeeo",
            ".ogewweggggewweo",
            ".ogeeeeggggeeeeo",
            ".ogggeeggggeegoo",
            ".oggggggggggggo.",
            "..ooggggmmggoo..",
            "....ooggggoo....",
            ".....oggggo.....",
            "..ooggggggggoo..",
            "..oggggggggggo..",
            "...oggggggggo...",
            "....oggo..oggo..",
            "....oooo..oooo.." };

    private static BufferedImage etImagem;

    // Desenha o ET com o centro da base em (cx, base). escala = tamanho de cada "pixel" do desenho.
    public static void alien(Graphics2D g, double cx, double base, int escala) {
        if (etImagem == null) {
            int w = ET_DESENHO[0].length(), h = ET_DESENHO.length;
            etImagem = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < h; y++) {
                String linha = ET_DESENHO[y];
                for (int x = 0; x < w && x < linha.length(); x++) {
                    int cor;
                    switch (linha.charAt(x)) {
                        case 'o': case 'm': cor = 0xFF1E5A32; break;
                        case 'g': cor = 0xFF6EDC6E; break;
                        case 'l': cor = 0xFFB4F5A8; break;
                        case 'e': cor = 0xFF10101C; break;
                        case 'w': cor = 0xFFFFFFFF; break;
                        case 'y': cor = 0xFFFFE066; break;
                        default: cor = 0; break;
                    }
                    etImagem.setRGB(x, y, cor);
                }
            }
        }
        int w = etImagem.getWidth() * escala, h = etImagem.getHeight() * escala;
        g.drawImage(etImagem, (int) Math.round(cx - w / 2.0), (int) Math.round(base - h), w, h, null);
    }

    // ===== oxigênio =====

    // Os tanques de oxigênio: cheios em azul, vazios apagados. Com pouco oxigênio, piscam em vermelho.
    public static void oxigenio(Graphics2D g, int x, int y, int quantos, int maximo, int tempoMs) {
        boolean pouco = quantos <= 1;
        boolean piscaVermelho = pouco && (tempoMs / 300) % 2 == 0;
        for (int i = 0; i < maximo; i++) {
            int tx = x + i * 30;
            boolean cheio = i < quantos;
            g.setColor(new Color(10, 12, 30, 230));
            g.fillRoundRect(tx, y, 24, 38, 10, 10);
            g.setColor(cheio ? (piscaVermelho ? new Color(255, 110, 110) : new Color(110, 215, 255)) : new Color(90, 96, 130));
            g.drawRoundRect(tx, y, 24, 38, 10, 10);
            g.fillRect(tx + 8, y - 5, 8, 5);
            if (cheio) {
                g.setColor(piscaVermelho ? new Color(235, 70, 80) : new Color(60, 170, 240));
                g.fillRoundRect(tx + 4, y + 4, 16, 30, 6, 6);
                g.setColor(new Color(255, 255, 255, 140));
                g.fillRect(tx + 7, y + 8, 3, 14);
            }
        }
    }

    // ===== astros =====

    public static void planeta(Graphics2D g, double cx, double cy, double r, Color base) {
        RadialGradientPaint tinta = new RadialGradientPaint(new Point2D.Double(cx - r * 0.35, cy - r * 0.35),
                (float) (r * 1.6), new float[] { 0f, 1f },
                new Color[] { escurecer(base, 1.3f), escurecer(base, 0.35f) });
        g.setPaint(tinta);
        g.fill(new Ellipse2D.Double(cx - r, cy - r, 2 * r, 2 * r));
    }

    public static void terra(Graphics2D g, double cx, double cy, double r) {
        planeta(g, cx, cy, r, new Color(50, 120, 230));
        Shape antigo = g.getClip();
        g.clip(new Ellipse2D.Double(cx - r, cy - r, 2 * r, 2 * r));
        g.setColor(new Color(70, 190, 90));
        g.fill(new Ellipse2D.Double(cx - r * 0.6, cy - r * 0.5, r * 0.65, r * 0.45));
        g.fill(new Ellipse2D.Double(cx + r * 0.1, cy + r * 0.05, r * 0.6, r * 0.5));
        g.fill(new Ellipse2D.Double(cx - r * 0.25, cy - r * 0.9, r * 0.45, r * 0.28));
        g.fill(new Ellipse2D.Double(cx - r * 0.7, cy + r * 0.3, r * 0.4, r * 0.3));
        g.setClip(antigo);
        g.setColor(new Color(160, 210, 255, 110));
        g.setStroke(new BasicStroke(3f));
        g.draw(new Ellipse2D.Double(cx - r, cy - r, 2 * r, 2 * r));
        g.setStroke(new BasicStroke(1f));
    }

    public static void lua(Graphics2D g, double cx, double cy, double r) {
        planeta(g, cx, cy, r, new Color(190, 192, 215));
        Shape antigo = g.getClip();
        g.clip(new Ellipse2D.Double(cx - r, cy - r, 2 * r, 2 * r));
        g.setColor(new Color(120, 122, 160, 150));
        g.fill(new Ellipse2D.Double(cx - r * 0.55, cy - r * 0.55, r * 0.3, r * 0.2));
        g.fill(new Ellipse2D.Double(cx - r * 0.1, cy - r * 0.35, r * 0.2, r * 0.13));
        g.fill(new Ellipse2D.Double(cx + r * 0.25, cy - r * 0.6, r * 0.14, r * 0.1));
        g.fill(new Ellipse2D.Double(cx - r * 0.3, cy - r * 0.15, r * 0.12, r * 0.09));
        g.setClip(antigo);
    }
}
