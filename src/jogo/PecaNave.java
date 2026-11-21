package jogo;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Uma peça da nave quebrada (bico, janela, corpo, asas ou motor).
 * Cada peça é um objeto com a sua imagem, a posição, a velocidade e o giro.
 * Na cena do meteoro, as 6 peças voam para longe e ficam flutuando.
 * Nas fases, a aluna vai recuperar essas peças.
 * Feita por Gih (Front-end) - Carroll Studios.
 */
public class PecaNave {

    private static final double ATRITO = 0.94;   // a cada tique a peça perde um pouco de velocidade

    private BufferedImage imagem;
    private double x, y;          // centro da peça
    private double vx, vy;        // velocidade
    private double angulo = 0;    // quanto a peça já girou
    private double giro;          // quanto ela gira a cada tique

    // inicioX/inicioY = onde a peça está na nave; destinoX/destinoY = onde ela vai parar
    public PecaNave(BufferedImage imagem, double inicioX, double inicioY, double destinoX, double destinoY, double giro) {
        this.imagem = imagem;
        this.x = inicioX;
        this.y = inicioY;
        // velocidade inicial calculada para a peça frear exatamente no destino
        this.vx = (destinoX - inicioX) * (1 - ATRITO);
        this.vy = (destinoY - inicioY) * (1 - ATRITO);
        this.giro = giro;
    }

    // Move a peça um pouquinho (chamado a cada tique do relógio)
    public void mover() {
        x += vx;
        y += vy;
        vx *= ATRITO;
        vy *= ATRITO;
        angulo += giro;
        // o giro diminui, mas nunca para de vez: a peça continua flutuando no espaço
        if (Math.abs(giro) > 0.01) {
            giro *= 0.97;
        }
    }

    // Desenha a peça girada em volta do próprio centro
    public void desenhar(Graphics2D g) {
        if (imagem == null) {
            return;
        }
        AffineTransform original = g.getTransform();
        g.rotate(angulo, x, y);
        g.drawImage(imagem, (int) Math.round(x - imagem.getWidth() / 2.0), (int) Math.round(y - imagem.getHeight() / 2.0), null);
        g.setTransform(original);
    }
}
