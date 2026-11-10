package jogo;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;

public class PainelJogo extends JPanel implements KeyListener {

    private Personagem personagem;

    public PainelJogo() {
        personagem = new Personagem(100, 340);

        addKeyListener(this);
        setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Parede cinza
        g.setColor(new Color(155, 155, 160));
        g.fillRect(0, 0, 800, 400);

        // Faixa superior
        g.setColor(new Color(125, 125, 132));
        g.fillRect(0, 0, 800, 22);

        // Portas
        desenharPorta(g, 25);
        desenharPorta(g, 305);
        desenharPorta(g, 585);

        // Placas dos laboratorios
        desenharPlaca(g, 125, "LAB 1");
        desenharPlaca(g, 405, "LAB 2");
        desenharPlaca(g, 685, "LAB 3");

        // Armarios
        desenharArmarios(g, 200, 150);
        desenharArmarios(g, 480, 150);

        // Rodape
        g.setColor(new Color(65, 65, 72));
        g.fillRect(0, 390, 800, 10);

        // Piso
        g.setColor(new Color(155, 155, 165));
        g.fillRect(0, 400, 800, 200);

        // Linhas do piso
        g.setColor(new Color(120, 120, 132));

        for (int x = 0; x <= 800; x += 100) {
            g.drawLine(x, 400, x, 600);
        }

        for (int y = 450; y <= 600; y += 50) {
            g.drawLine(0, y, 800, y);
        }

        // Personagem em pixel art
        desenharPersonagem(g);
    }

    private void desenharPorta(Graphics g, int x) {

        g.setColor(new Color(75, 75, 80));
        g.fillRect(x - 5, 85, 100, 315);

        g.setColor(new Color(220, 220, 215));
        g.fillRect(x, 90, 90, 310);

        g.setColor(new Color(245, 245, 238));
        g.fillRect(x + 7, 97, 76, 303);

        g.setColor(new Color(215, 215, 210));
        g.fillRect(x + 15, 112, 60, 72);

        g.setColor(new Color(55, 70, 75));
        g.fillRect(x + 20, 117, 50, 62);

        g.setColor(new Color(130, 150, 150));
        g.drawLine(x + 24, 123, x + 45, 123);

        g.setColor(new Color(225, 225, 220));
        g.drawRect(x + 15, 200, 60, 180);

        // Macaneta cinza
        g.setColor(new Color(115, 115, 115));
        g.fillOval(x + 65, 250, 9, 9);
    }

    private void desenharPlaca(
        Graphics g, int x, String texto
    ) {

        g.setColor(new Color(65, 65, 65));
        g.fillRect(x, 205, 62, 31);

        g.setColor(new Color(45, 130, 75));
        g.fillRect(x + 2, 207, 58, 27);

        g.setColor(new Color(130, 190, 125));
        g.drawRect(x + 2, 207, 58, 27);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        g.drawString(texto, x + 6, 224);
    }

    private void desenharArmarios(
        Graphics g, int inicioX, int inicioY
    ) {

        int largura = 27;
        int altura = 78;
        int espaco = 3;

        g.setColor(new Color(55, 57, 62));
        g.fillRect(
            inicioX - 4,
            inicioY - 4,
            3 * largura + 2 * espaco + 8,
            3 * altura + 2 * espaco + 8
        );

        for (int linha = 0; linha < 3; linha++) {
            for (int coluna = 0; coluna < 3; coluna++) {

                int x = inicioX + coluna * (largura + espaco);
                int y = inicioY + linha * (altura + espaco);

                g.setColor(new Color(165, 168, 175));
                g.fillRect(x, y, largura, altura);

                g.setColor(new Color(205, 207, 212));
                g.drawLine(
                    x + 3, y + 3,
                    x + largura - 4, y + 3
                );

                g.setColor(new Color(85, 88, 95));
                g.drawRect(x, y, largura - 1, altura - 1);

                // Ventilacao
                g.drawLine(x + 5, y + 9, x + 21, y + 9);
                g.drawLine(x + 5, y + 13, x + 21, y + 13);

                // Macaneta
                g.setColor(new Color(70, 72, 78));
                g.fillOval(x + 19, y + 35, 4, 5);
            }
        }

        // Adesivos coloridos
        desenharAdesivo(g, inicioX + 5, inicioY + 25, 0);
        desenharAdesivo(g, inicioX + 32, inicioY + 105, 1);
        desenharAdesivo(g, inicioX + 62, inicioY + 185, 2);
        desenharAdesivo(g, inicioX + 32, inicioY + 25, 3);
        desenharAdesivo(g, inicioX + 62, inicioY + 105, 0);
    }

    private void desenharAdesivo(
        Graphics g, int x, int y, int tipo
    ) {

        Color[] cores = {
            new Color(255, 100, 120),
            new Color(255, 210, 70),
            new Color(90, 190, 255),
            new Color(155, 105, 220)
        };

        g.setColor(cores[tipo]);

        if (tipo == 0) {
            g.fillOval(x, y, 12, 12);
        } else if (tipo == 1) {
            g.fillRect(x, y, 12, 12);
        } else if (tipo == 2) {
            g.fillOval(x, y, 13, 13);
        } else {
            int[] pontosX = {x + 6, x + 12, x + 6, x};
            int[] pontosY = {y, y + 6, y + 12, y + 6};

            g.fillPolygon(pontosX, pontosY, 4);
        }
    }

    // Desenha o estudante com formas pixeladas
    private void desenharPersonagem(Graphics g) {

        int x = personagem.getX();
        int y = personagem.getY();

        boolean direita = personagem.isOlhandoDireita();
        int passo = personagem.getPasso();

        // Sombra nos pes
        g.setColor(new Color(95, 95, 100));
        g.fillRect(x + 4, y + 56, 32, 4);

        // Cabelo
        pixel(g, x, y, 8, 4, 24, 8,
              new Color(65, 40, 30), direita);
        pixel(g, x, y, 12, 0, 16, 8,
              new Color(65, 40, 30), direita);
        pixel(g, x, y, 8, 8, 8, 8,
              new Color(65, 40, 30), direita);

        // Rosto
        pixel(g, x, y, 12, 8, 16, 16,
              new Color(235, 185, 145), direita);

        // Franja
        pixel(g, x, y, 12, 8, 16, 4,
              new Color(65, 40, 30), direita);

        // Olho
        pixel(g, x, y, 24, 16, 4, 4,
              new Color(35, 30, 30), direita);

        // Orelha
        pixel(g, x, y, 28, 16, 4, 4,
              new Color(235, 185, 145), direita);

        // Pescoco
        pixel(g, x, y, 16, 24, 8, 4,
              new Color(220, 165, 125), direita);

        // Tronco: uniforme azul
        pixel(g, x, y, 8, 28, 24, 16,
              new Color(45, 75, 145), direita);

        // Gola da camisa
        pixel(g, x, y, 16, 28, 8, 4,
              new Color(235, 235, 230), direita);

        // Manga do braco
        pixel(g, x, y, 4, 28, 8, 12,
              new Color(45, 75, 145), direita);

        pixel(g, x, y, 28, 28, 8, 12,
              new Color(45, 75, 145), direita);

        // Maos
        pixel(g, x, y, 4, 40, 8, 4,
              new Color(235, 185, 145), direita);

        pixel(g, x, y, 28, 40, 8, 4,
              new Color(235, 185, 145), direita);

        // Calca escura
        pixel(g, x, y, 12, 44, 16, 8,
              new Color(45, 45, 65), direita);

        // Pernas: alternam conforme o passo
        if (passo == 0) {

            pixel(g, x, y, 12, 52, 6, 6,
                  new Color(45, 45, 65), direita);

            pixel(g, x, y, 24, 52, 6, 6,
                  new Color(45, 45, 65), direita);

            // Sapatos
            pixel(g, x, y, 8, 56, 10, 4,
                  new Color(35, 30, 30), direita);

            pixel(g, x, y, 24, 56, 10, 4,
                  new Color(35, 30, 30), direita);

        } else {

            pixel(g, x, y, 8, 50, 6, 8,
                  new Color(45, 45, 65), direita);

            pixel(g, x, y, 26, 50, 6, 8,
                  new Color(45, 45, 65), direita);

            // Sapatos em outra posicao
            pixel(g, x, y, 4, 56, 10, 4,
                  new Color(35, 30, 30), direita);

            pixel(g, x, y, 26, 56, 10, 4,
                  new Color(35, 30, 30), direita);
        }
    }

    // Desenha um bloco do personagem.
    // Inverte a posicao horizontal quando ele olha para a esquerda.
    private void pixel(
        Graphics g,
        int baseX,
        int baseY,
        int px,
        int py,
        int largura,
        int altura,
        Color cor,
        boolean direita
    ) {

        int posicaoX;

        if (direita) {
            posicaoX = baseX + px;
        } else {
            posicaoX = baseX + personagem.getLargura()
                       - px - largura;
        }

        g.setColor(cor);
        g.fillRect(
            posicaoX,
            baseY + py,
            largura,
            altura
        );
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_D) {
            personagem.moverDireita();
            repaint();
        }

        if (e.getKeyCode() == KeyEvent.VK_A) {
            personagem.moverEsquerda();
            repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}