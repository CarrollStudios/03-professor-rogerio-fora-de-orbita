package jogo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Final do jogo: as 6 peças voltam para a nave (de onde elas se espalharam na CenaMeteoro),
 * a nave decola e a aluna volta para a Terra.
 */
public class CenaFinal extends JPanel {

    private static final int LARGURA = 1280;
    private static final int ALTURA = 720;
    private static final int TIQUE = 30;

    // Mesmas peças e posições da CenaMeteoro (agora no caminho de volta)
    private static final String[] NOMES_PECAS = { "bico", "janela", "corpo", "asa-esquerda", "asa-direita", "motor" };
    private static final int[][] LUGAR_NA_NAVE = { { 48, 0 }, { 60, 100 }, { 48, 80 }, { 8, 160 }, { 128, 160 }, { 56, 236 } };
    private static final int[][] ORIGEM = { { 250, 150 }, { 1010, 150 }, { 190, 380 }, { 440, 330 }, { 880, 300 }, { 1100, 380 } };
    private static final int NAVE_X = 640, NAVE_Y = 360;

    // Linha do tempo (em ms)
    private static final int INICIO_PECAS = 600, INTERVALO = 650, VOO_PECA = 900;
    private static final int T_COMPLETA = INICIO_PECAS + 5 * INTERVALO + VOO_PECA;   // todas encaixadas
    private static final int T_DECOLA = T_COMPLETA + 2200;
    private static final int T_FIM = T_DECOLA + 3200;

    private BufferedImage fundo, nave;
    private BufferedImage[] fogo = new BufferedImage[2];
    private BufferedImage[] pecas = new BufferedImage[6];
    private boolean[] chegou = new boolean[6];
    private boolean decolou = false;

    private int tempo = 0;
    private TelaJogo janela;
    private Timer relogio;

    public CenaFinal(TelaJogo janela) {

        this.janela = janela;
        setPreferredSize(new Dimension(LARGURA, ALTURA));
        setBackground(new Color(0x0D0B1E));
        setFocusable(true);

        fundo = Imagens.carregar("/images/meteoro/espaco-fundo.png");
        nave = Imagens.carregar("/images/nave/nave-com-aluna.png");
        fogo[0] = Imagens.carregar("/images/nave/nave-fogo-1.png");
        fogo[1] = Imagens.carregar("/images/nave/nave-fogo-2.png");
        for (int i = 0; i < 6; i++) {
            pecas[i] = Imagens.carregar("/images/nave/nave-peca-" + NOMES_PECAS[i] + ".png");
        }

        Sons.tocarMusica("/sons/musicas/musica-espaco.wav");

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
                    avancar();
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                avancar();
            }
        });

        relogio = new Timer(TIQUE, e -> {
            atualizar();
            repaint();
        });
        relogio.start();
    }

    // Antes do final: pula a animação. Na tela final: volta para o menu.
    private void avancar() {
        if (tempo < T_FIM) {
            tempo = T_FIM;
            decolou = true;
            for (int i = 0; i < 6; i++) {
                chegou[i] = true;
            }
        } else if (tempo > T_FIM + 4000) {
            relogio.stop();
            janela.mostrarMenu();
        }
    }

    private void atualizar() {
        tempo += TIQUE;
        for (int i = 0; i < 6; i++) {
            if (!chegou[i] && tempo >= INICIO_PECAS + i * INTERVALO + VOO_PECA) {
                chegou[i] = true;
                Sons.tocar("/sons/efeitos/pecas.wav");
            }
        }
        if (!decolou && tempo >= T_DECOLA) {
            decolou = true;
            Sons.tocar("/sons/efeitos/decolagem.wav");
        }
    }

    @Override
    protected void paintComponent(Graphics g0) {

        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        Desenho.suavizar(g);
        g.drawImage(fundo, 0, 0, null);

        if (tempo < T_FIM) {
            desenharNave(g);
        } else {
            desenharFinal(g, tempo - T_FIM);
        }
    }

    private void desenharNave(Graphics2D g) {

        int esquerda = NAVE_X - nave.getWidth() / 2;
        int topo = NAVE_Y - nave.getHeight() / 2;
        boolean completa = tempo >= T_COMPLETA;

        // sobe depois de decolar
        double subida = 0;
        if (tempo >= T_DECOLA) {
            double f = (tempo - T_DECOLA) / 1500.0;
            subida = f * f * 900;
        }

        if (completa) {
            if (tempo >= T_DECOLA) {
                g.drawImage(fogo[(tempo / 90) % 2], esquerda, (int) (topo + nave.getHeight() - 8 - subida), null);
            }
            // brilho quando a nave fica pronta
            if (tempo < T_COMPLETA + 700) {
                g.setColor(new Color(255, 245, 170, (int) (70 * (1 - (tempo - T_COMPLETA) / 700.0))));
                g.fillOval(NAVE_X - 180, NAVE_Y - 220, 360, 440);
            }
            g.drawImage(nave, esquerda, (int) (topo - subida), null);
        } else {
            // peças voando de volta para o lugar
            for (int i = 0; i < 6; i++) {
                double inicio = INICIO_PECAS + i * INTERVALO;
                if (tempo < inicio) {
                    // ainda parada no lugar onde ficou flutuando
                    desenharPeca(g, i, ORIGEM[i][0], ORIGEM[i][1], 0.4 * Math.sin(tempo / 400.0 + i));
                    continue;
                }
                double f = Math.min(1, (tempo - inicio) / VOO_PECA);
                double e = 1 - Math.pow(1 - f, 3);
                double destinoX = esquerda + LUGAR_NA_NAVE[i][0] + pecas[i].getWidth() / 2.0;
                double destinoY = topo + LUGAR_NA_NAVE[i][1] + pecas[i].getHeight() / 2.0;
                double px = ORIGEM[i][0] + (destinoX - ORIGEM[i][0]) * e;
                double py = ORIGEM[i][1] + (destinoY - ORIGEM[i][1]) * e;
                desenharPeca(g, i, px, py, (1 - e) * 6);
            }
        }

        // textos
        String titulo, sub = "";
        if (tempo >= T_DECOLA) {
            titulo = "Rumo à Terra!";
        } else if (completa) {
            titulo = "A nave foi reconstruída!";
            sub = "Todos os sistemas estão funcionando. Hora de decolar!";
        } else {
            titulo = "As peças estão voltando para a nave...";
        }
        Desenho.caixa(g, 140, ALTURA - 130, LARGURA - 280, 90, new Color(10, 12, 30, 225), new Color(150, 130, 255));
        g.setFont(new Font("SansSerif", Font.BOLD, 26));
        Desenho.textoCentro(g, titulo, LARGURA / 2, ALTURA - 82 - (sub.isEmpty() ? 0 : 8), Color.WHITE);
        if (!sub.isEmpty()) {
            g.setFont(new Font("SansSerif", Font.PLAIN, 18));
            Desenho.textoCentro(g, sub, LARGURA / 2, ALTURA - 54, new Color(210, 210, 240));
        }
        g.setFont(new Font("SansSerif", Font.PLAIN, 15));
        Desenho.texto(g, "ENTER: pular", LARGURA - 150, 30, new Color(200, 200, 230));
    }

    private void desenharPeca(Graphics2D g, int i, double cx, double cy, double angulo) {
        AffineTransform original = g.getTransform();
        g.rotate(angulo, cx, cy);
        g.drawImage(pecas[i], (int) Math.round(cx - pecas[i].getWidth() / 2.0),
                (int) Math.round(cy - pecas[i].getHeight() / 2.0), null);
        g.setTransform(original);
    }

    private void desenharFinal(Graphics2D g, int t) {

        Desenho.terra(g, 640, 980, 520);
        g.setColor(new Color(0, 0, 20, 90));
        g.fillRect(0, 0, LARGURA, 460);

        linha(g, t, 400, "VOCÊ VOLTOU PARA A TERRA!", new Font("SansSerif", Font.BOLD, 50), 140, Desenho.AMARELO);
        linha(g, t, 2000, "Quando a aluna voltou para a aula, a primeira coisa que fez foi...",
                new Font("SansSerif", Font.BOLD, 24), 220, Color.WHITE);
        linha(g, t, 4000, "FECHAR A PORTA.", new Font("SansSerif", Font.BOLD, 44), 290, Desenho.AMARELO);
        linha(g, t, 6000, "FIM  -  Obrigado por jogar!", new Font("SansSerif", Font.BOLD, 24), 360,
                new Color(200, 190, 255));
        if (t > 7000 && (t / 500) % 2 == 0) {
            g.setFont(new Font("SansSerif", Font.BOLD, 18));
            Desenho.textoCentro(g, "ENTER ou clique: voltar ao menu", LARGURA / 2, 420, Desenho.AMARELO);
        }
    }

    // Linha de texto que aparece aos poucos
    private void linha(Graphics2D g, int t, int aparece, String texto, Font fonte, int y, Color cor) {
        if (t < aparece) {
            return;
        }
        float alfa = (float) Math.min(1, (t - aparece) / 800.0);
        g.setFont(fonte);
        Desenho.textoCentro(g, texto, LARGURA / 2, y, new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), (int) (255 * alfa)));
    }
}
