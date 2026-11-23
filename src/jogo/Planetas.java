package jogo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Os 3 planetas das fases, em pixel art feita em código (não precisa de imagem nova).
 * Cada planeta é "pintado" numa imagem pequena, pixel por pixel, com luz vinda de cima à esquerda,
 * e depois aumentado sem suavizar, para ficar com o mesmo visual do resto do jogo.
 *   BRASA   (fase 1): planeta de lava, com crateras e rachaduras brilhando.
 *   MAGENTA (fase 2): gigante gasoso com faixas, uma tempestade e anéis.
 *   CIANO   (fase 3): planeta de gelo e oceano, com nuvens e calotas polares.
 */
public class Planetas {

    public static final int BRASA = 0, MAGENTA = 1, CIANO = 2;

    // cor do brilho (atmosfera) de cada planeta
    private static final Color[] BRILHO = { new Color(255, 140, 60), new Color(255, 110, 200), new Color(90, 220, 255) };

    private static final Map<String, BufferedImage> GUARDADOS = new HashMap<>();

    /**
     * Desenha um planeta.
     * tipo = BRASA, MAGENTA ou CIANO; (cx, cy) = centro; r = raio do globo;
     * claridade = 1 para o planeta normal, menos que 1 para escurecer (planeta de fundo).
     */
    public static void desenhar(Graphics2D g, int tipo, double cx, double cy, double r, float claridade) {

        int ps = Math.max(2, (int) Math.round(r / 55.0));   // tamanho de cada "pixel" na tela
        BufferedImage img = imagem(tipo, r, ps, claridade);

        // brilho da atmosfera
        Color b = BRILHO[tipo];
        float raioBrilho = (float) (r * 1.28);
        g.setPaint(new RadialGradientPaint(new Point2D.Double(cx, cy), raioBrilho, new float[] { 0.72f, 1f },
                new Color[] { new Color(b.getRed(), b.getGreen(), b.getBlue(), (int) (70 * claridade)),
                        new Color(b.getRed(), b.getGreen(), b.getBlue(), 0) }));
        g.fill(new Ellipse2D.Double(cx - raioBrilho, cy - raioBrilho, raioBrilho * 2, raioBrilho * 2));

        Object antigo = g.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        int w = img.getWidth() * ps, h = img.getHeight() * ps;
        g.drawImage(img, (int) Math.round(cx - w / 2.0), (int) Math.round(cy - h / 2.0), w, h, null);
        if (antigo != null) {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, antigo);
        }
    }

    private static BufferedImage imagem(int tipo, double r, int ps, float claridade) {
        String chave = tipo + ":" + Math.round(r) + ":" + ps + ":" + claridade;
        BufferedImage img = GUARDADOS.get(chave);
        if (img == null) {
            img = pintar(tipo, r / ps, claridade);
            GUARDADOS.put(chave, img);
        }
        return img;
    }

    // ================================================================ pintura

    private static BufferedImage pintar(int tipo, double R, float claridade) {

        boolean aneis = tipo == MAGENTA;
        double inclinacao = -0.28;                 // anel inclinado (radianos)
        double Ro = R * 1.95, Ri = R * 1.42, achatado = 0.27;

        int metadeX = (int) Math.ceil(aneis ? Ro + 2 : R + 2);
        int metadeY = (int) Math.ceil(aneis ? Math.max(R + 2, Ro * (achatado * Math.cos(inclinacao) + Math.abs(Math.sin(inclinacao))) + 2) : R + 2);
        BufferedImage img = new BufferedImage(metadeX * 2, metadeY * 2, BufferedImage.TYPE_INT_ARGB);

        // luz vinda de cima, à esquerda
        double lx = -0.55, ly = -0.55, lz = 0.63;
        double tam = Math.sqrt(lx * lx + ly * ly + lz * lz);
        lx /= tam;
        ly /= tam;
        lz /= tam;

        // crateras do planeta de lava (sempre as mesmas)
        double[][] crateras = new double[7][3];
        Random sorteio = new Random(11);
        for (double[] c : crateras) {
            c[0] = (sorteio.nextDouble() - 0.5) * 2.4;
            c[1] = (sorteio.nextDouble() - 0.5) * 1.8;
            c[2] = 0.14 + sorteio.nextDouble() * 0.2;
        }

        for (int py = 0; py < img.getHeight(); py++) {
            for (int px = 0; px < img.getWidth(); px++) {

                double dx = px + 0.5 - metadeX, dy = py + 0.5 - metadeY;
                double nx = dx / R, ny = dy / R;
                double d2 = nx * nx + ny * ny;
                boolean noGlobo = d2 <= 1;

                // anel (em volta do globo, só no planeta magenta)
                int corAnel = 0;
                boolean anelNaFrente = false;
                if (aneis) {
                    double u = dx * Math.cos(inclinacao) + dy * Math.sin(inclinacao);
                    double v = -dx * Math.sin(inclinacao) + dy * Math.cos(inclinacao);
                    double e = Math.sqrt(u * u + (v / achatado) * (v / achatado));
                    if (e >= Ri && e <= Ro) {
                        double faixa = (e - Ri) / (Ro - Ri);
                        Color c = faixa < 0.38 ? new Color(255, 205, 235)
                                : faixa < 0.5 ? new Color(150, 80, 150)
                                : faixa < 0.86 ? new Color(240, 160, 215) : new Color(205, 120, 195);
                        corAnel = c.getRGB();
                        anelNaFrente = v > 0;
                    }
                }

                if (corAnel != 0 && (anelNaFrente || !noGlobo)) {
                    img.setRGB(px, py, escurecer(corAnel, claridade));
                    continue;
                }
                if (!noGlobo) {
                    continue;
                }

                // ---- o globo ----
                double nz = Math.sqrt(1 - d2);
                double luz = Math.max(0, nx * lx + ny * ly + nz * lz);
                double u = Math.atan2(nx, nz);          // "longitude"
                double v = Math.asin(Math.max(-1, Math.min(1, ny)));   // "latitude"

                int[] cor = tipo == BRASA ? lava(u, v, nx, ny, nz, crateras)
                        : tipo == MAGENTA ? gasoso(u, v)
                        : gelo(u, v);

                // sombra em 4 degraus (visual de pixel art)
                double f = luz < 0.10 ? 0.42 : luz < 0.32 ? 0.62 : luz < 0.60 ? 0.82 : 1.0;
                double r2 = cor[0] * f, g2 = cor[1] * f, b2 = cor[2] * f;
                // lado escuro puxa para o azul-roxo, e o lado claro ganha um brilho
                if (f < 0.7) {
                    r2 *= 0.9;
                    g2 *= 0.92;
                    b2 = b2 * 1.1 + 8;
                }
                if (luz > 0.9 && d2 < 0.5) {
                    r2 += 14;
                    g2 += 14;
                    b2 += 14;
                }
                // borda da atmosfera
                if (d2 > 0.86) {
                    double k = Math.min(1, (d2 - 0.86) / 0.14) * (0.25 + 0.6 * luz);
                    Color at = BRILHO[tipo];
                    r2 = r2 * (1 - k) + at.getRed() * k;
                    g2 = g2 * (1 - k) + at.getGreen() * k;
                    b2 = b2 * (1 - k) + at.getBlue() * k;
                }
                // contorno escuro
                if (d2 > 0.95) {
                    r2 *= 0.55;
                    g2 *= 0.55;
                    b2 *= 0.6;
                }
                img.setRGB(px, py, escurecer(rgb(r2, g2, b2), claridade));
            }
        }
        return img;
    }

    // ================================================================ cada planeta

    // Planeta de lava: crosta escura, rachaduras brilhantes e crateras
    private static int[] lava(double u, double v, double nx, double ny, double nz, double[][] crateras) {
        double n = ruido(u * 2.4 + 3, v * 2.4 + 1, 5);
        double rachadura = Math.abs(ruido(u * 3.1 + 9, v * 3.1 + 4, 8) - 0.5);

        int[] c;
        if (rachadura < 0.035) {
            c = new int[] { 255, 214, 90 };                       // lava brilhante
        } else if (rachadura < 0.07) {
            c = new int[] { 255, 130, 40 };
        } else if (n > 0.58) {
            c = new int[] { 150, 55, 35 };                        // crosta clara
        } else if (n > 0.42) {
            c = new int[] { 112, 38, 30 };
        } else {
            c = new int[] { 78, 28, 30 };                         // crosta escura
        }

        // crateras: buraco escuro com a borda de cima iluminada
        for (double[] k : crateras) {
            double dx = (u - k[0]) / k[2], dy = (v - k[1]) / k[2] / 0.85;
            double d = Math.sqrt(dx * dx + dy * dy);
            if (d < 0.78) {
                boolean fundoClaro = dy > 0.1 && d > 0.3;
                c = fundoClaro ? new int[] { 165, 70, 40 } : new int[] { 52, 20, 26 };
            } else if (d < 1.0) {
                c = dy < 0 ? new int[] { 190, 95, 50 } : new int[] { 70, 24, 26 };
            }
        }
        return c;
    }

    // Gigante gasoso: faixas onduladas e uma grande tempestade
    private static int[] gasoso(double u, double v) {
        double t = v * 4.2 + ruido(u * 1.4 + 2, v * 3.2, 3) * 1.5;
        int faixa = (int) Math.floor(t * 1.35);
        int[][] paleta = { { 252, 160, 205 }, { 224, 92, 170 }, { 172, 62, 150 }, { 255, 198, 228 }, { 205, 80, 160 } };
        int[] c = paleta[((faixa % paleta.length) + paleta.length) % paleta.length];

        // grande mancha (tempestade)
        double dx = (u - 0.45) / 0.34, dy = (v - 0.30) / 0.17;
        double d = Math.sqrt(dx * dx + dy * dy);
        if (d < 0.45) {
            c = new int[] { 195, 40, 95 };
        } else if (d < 0.78) {
            c = new int[] { 255, 225, 238 };
        } else if (d < 1.0) {
            c = new int[] { 240, 120, 170 };
        }
        return c;
    }

    // Planeta de gelo: oceano ciano, continentes de gelo, nuvens e calotas polares
    private static int[] gelo(double u, double v) {
        double n = ruido(u * 1.9 + 7, v * 1.9 + 2, 12);
        int[] c;
        if (n > 0.55) {
            c = n > 0.66 ? new int[] { 238, 250, 255 } : new int[] { 175, 228, 246 };   // gelo
        } else if (n > 0.47) {
            c = new int[] { 70, 205, 225 };                                              // água rasa
        } else {
            c = new int[] { 28, 128, 190 };                                              // oceano fundo
        }
        if (Math.abs(v) > 1.12) {
            c = new int[] { 245, 252, 255 };                                             // calotas polares
        }
        double nuvem = ruido(u * 2.6 + 20, v * 4.5 + 5, 21);
        if (nuvem > 0.64) {
            c = new int[] { 255, 255, 255 };
        }
        return c;
    }

    // ================================================================ utilidades

    private static int rgb(double r, double g, double b) {
        int ri = (int) Math.max(0, Math.min(255, r));
        int gi = (int) Math.max(0, Math.min(255, g));
        int bi = (int) Math.max(0, Math.min(255, b));
        return 0xFF000000 | (ri << 16) | (gi << 8) | bi;
    }

    private static int escurecer(int argb, float fator) {
        if (fator >= 0.999f) {
            return argb;
        }
        int r = (int) (((argb >> 16) & 0xFF) * fator);
        int g = (int) (((argb >> 8) & 0xFF) * fator);
        int b = (int) ((argb & 0xFF) * fator);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    // Ruído suave (parece "manchas" naturais): 4 camadas de ruído de valor
    private static double ruido(double x, double y, int semente) {
        double soma = 0, amplitude = 0.5, total = 0;
        for (int i = 0; i < 4; i++) {
            soma += amplitude * ruidoSimples(x, y, semente + i * 31);
            total += amplitude;
            x *= 2;
            y *= 2;
            amplitude /= 2;
        }
        return soma / total;
    }

    private static double ruidoSimples(double x, double y, int semente) {
        int xi = (int) Math.floor(x), yi = (int) Math.floor(y);
        double fx = x - xi, fy = y - yi;
        fx = fx * fx * (3 - 2 * fx);
        fy = fy * fy * (3 - 2 * fy);
        double a = valor(xi, yi, semente), b = valor(xi + 1, yi, semente);
        double c = valor(xi, yi + 1, semente), d = valor(xi + 1, yi + 1, semente);
        return (a + (b - a) * fx) + ((c + (d - c) * fx) - (a + (b - a) * fx)) * fy;
    }

    private static double valor(int x, int y, int semente) {
        int h = x * 374761393 + y * 668265263 + semente * 1442695041;
        h = (h ^ (h >>> 13)) * 1274126177;
        h ^= h >>> 16;
        return (h & 0xFFFFFF) / (double) 0x1000000;
    }
}
