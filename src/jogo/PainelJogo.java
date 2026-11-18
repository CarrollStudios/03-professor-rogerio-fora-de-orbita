package jogo;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class PainelJogo extends JPanel implements KeyListener {

    private Personagem personagem;
    private BufferedImage corredor;

    private static final int LARGURA_TELA = 1200;
    private static final int ALTURA_TELA = 600;

    private static final int LARGURA_PERSONAGEM = 400;
    private static final int ALTURA_PERSONAGEM = 400;

    // Controla se as teclas estão pressionadas
    private boolean direitaPressionada = false;
    private boolean esquerdaPressionada = false;

    // Timer responsável pela movimentação e animação
    private Timer temporizador;

    public PainelJogo() {

        personagem = new Personagem(100, 450);

        setPreferredSize(
            new java.awt.Dimension(LARGURA_TELA, ALTURA_TELA)
        );

        // Carrega o corredor
        try {

            corredor = ImageIO.read(
                getClass().getResource("/sprites/corredorInicial.jpeg")
            );

            System.out.println("Corredor carregado!");

        } catch (IOException | IllegalArgumentException e) {

            System.out.println("Erro ao carregar o corredor!");
            e.printStackTrace();
        }

        addKeyListener(this);

        setFocusable(true);

        // Atualiza a movimentação a cada 80 milissegundos
        temporizador = new Timer(80, e -> atualizarMovimento());

    }

    // Atualiza a posição e a animação
    private void atualizarMovimento() {

        if (direitaPressionada) {

            personagem.moverDireita(getWidth());

        } else if (esquerdaPressionada) {

            personagem.moverEsquerda();

        } else {

            personagem.parar();
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        // Desenha o corredor
        if (corredor != null) {

            g.drawImage(
                corredor,
                0,
                0,
                getWidth(),
                getHeight(),
                this
            );
        }

        // Desenha a personagem
        desenharPersonagem(g);
    }

    private void desenharPersonagem(Graphics g) {

        BufferedImage sprite = personagem.getSpriteAtual();

        if (sprite == null) {
            return;
        }

        g.drawImage(
            sprite,
            personagem.getX(),
            personagem.getY(),
            LARGURA_PERSONAGEM,
            ALTURA_PERSONAGEM,
            this
        );
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_D) {

            direitaPressionada = true;
            esquerdaPressionada = false;

            if (!temporizador.isRunning()) {
                temporizador.start();
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_A) {

            esquerdaPressionada = true;
            direitaPressionada = false;

            if (!temporizador.isRunning()) {
                temporizador.start();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_D) {
            direitaPressionada = false;
        }

        if (e.getKeyCode() == KeyEvent.VK_A) {
            esquerdaPressionada = false;
        }

        if (!direitaPressionada && !esquerdaPressionada) {

            temporizador.stop();

            personagem.parar();

            repaint();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

}