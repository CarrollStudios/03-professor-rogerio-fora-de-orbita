package jogo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Tela de fim de jogo: o oxigênio da Zoe acabou.
 * O botão Recomeçar zera o progresso e o jogo volta para o começo (corredor da escola).
 */
public class CenaGameOver extends JPanel {

    private static final int LARGURA = 1280;
    private static final int ALTURA = 720;
    private static final int TIQUE = 30;

    private final Rectangle botao = new Rectangle(LARGURA / 2 - 130, 548, 260, 66);
    private BufferedImage fundo, aluna;
    private int tempo = 0;
    private int mouseX, mouseY;
    private boolean saindo = false;

    private TelaJogo janela;
    private Timer relogio;

    public CenaGameOver(TelaJogo janela) {

        this.janela = janela;
        setPreferredSize(new Dimension(LARGURA, ALTURA));
        setBackground(Color.BLACK);
        setFocusable(true);

        fundo = Imagens.carregar("/images/meteoro/espaco-fundo.png");
        aluna = Imagens.carregar("/images/personagens/aluna-astronauta.png");

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
                    recomecar();
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (botao.contains(e.getX(), e.getY())) {
                    recomecar();
                }
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });

        relogio = new Timer(TIQUE, e -> {
            tempo += TIQUE;
            repaint();
        });
        relogio.start();
    }

    private void recomecar() {
        if (saindo) {
            return;
        }
        saindo = true;
        Sons.tocar("/sons/efeitos/clique.wav");
        relogio.stop();
        Progresso.zerar();       // tudo volta ao começo: peças, oxigênio e dicas
        janela.iniciarJogo();    // sem progresso, começa pelo corredor
    }

    @Override
    protected void paintComponent(Graphics g0) {

        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        Desenho.suavizar(g);

        g.drawImage(fundo, 0, 0, null);
        g.setColor(new Color(70, 0, 20, 150));
        g.fillRect(0, 0, LARGURA, ALTURA);

        // a Zoe à deriva, girando devagar
        int w = (int) (aluna.getWidth() * 0.6), h = (int) (aluna.getHeight() * 0.6);
        double ang = Math.sin(tempo / 900.0) * 0.35 + 0.5;
        AffineTransform original = g.getTransform();
        g.translate(LARGURA / 2.0, 330 + Math.sin(tempo / 700.0) * 8);
        g.rotate(ang);
        g.drawImage(aluna, -w / 2, -h / 2, w, h, null);
        g.setTransform(original);

        g.setFont(new Font("SansSerif", Font.BOLD, 84));
        Desenho.textoCentro(g, "GAME OVER", LARGURA / 2, 150, new Color(255, 110, 110));

        // fala da Zoe
        Desenho.caixa(g, 290, 440, 700, 76, new Color(26, 23, 48, 245), Desenho.ROXO);
        g.setColor(new Color(0x6D28D9));
        g.fillRoundRect(308, 428, 76, 24, 12, 12);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        Desenho.texto(g, "Zoe", 322, 446, Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 26));
        Desenho.texto(g, "Não deu dessa vez… Vamos recomeçar?", 316, 490, Desenho.TEXTO);

        // botão Recomeçar
        boolean sobre = botao.contains(mouseX, mouseY);
        Desenho.caixa(g, botao.x, botao.y, botao.width, botao.height,
                sobre ? new Color(120, 70, 220) : new Color(90, 50, 180), Desenho.AMARELO);
        g.setFont(new Font("SansSerif", Font.BOLD, 30));
        Desenho.textoCentro(g, "Recomeçar", botao.x + botao.width / 2, botao.y + 44, Color.WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        Desenho.textoCentro(g, "ou aperte ENTER", LARGURA / 2, botao.y + botao.height + 28, new Color(230, 215, 235));
    }
}
