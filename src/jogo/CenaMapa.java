package jogo;

import java.awt.BasicStroke;
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
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Tela de entrada das fases (o mapa da missão): Lua -> planeta 1 -> planeta 2 -> planeta 3 -> Terra.
 * Mostra quantas peças da nave já foram recuperadas e leva o jogador para a próxima fase.
 * Depois da cutscene do meteoro, o botão "Continuar" chega aqui.
 */
public class CenaMapa extends JPanel {

    private static final int LARGURA = 1280;
    private static final int ALTURA = 720;
    private static final int TIQUE = 30;

    // Centro de cada ponto do mapa: 0 = Lua, 1 a 3 = planetas das fases, 4 = Terra
    private static final double[][] NOS = { { 200, 530 }, { 430, 440 }, { 660, 580 }, { 900, 390 }, { 1140, 215 } };
    private static final Color[] COR_PLANETA = { new Color(255, 150, 50), new Color(235, 80, 170), new Color(40, 200, 220) };
    private static final double[] RAIO = { 40, 36, 40 };

    private static final double ESCALA_ALUNA = 0.32;

    private BufferedImage fundo, aluna;
    private TelaJogo janela;
    private Timer relogio;
    private int tempo = 0;

    private final Rectangle botao = new Rectangle(1040, 618, 190, 58);
    private int mouseX, mouseY;

    private final int no;            // onde a aluna está agora (= fases concluídas)
    private boolean viajando = false;
    private boolean enviado = false;
    private int tempoViagem = 0;
    private boolean confirmarReiniciar = false;

    public CenaMapa(TelaJogo janela) {

        this.janela = janela;
        this.no = Math.min(3, Progresso.getFase());

        setPreferredSize(new Dimension(LARGURA, ALTURA));
        setBackground(new Color(0x0D0B1E));
        setFocusable(true);

        fundo = Imagens.carregar("/images/meteoro/espaco-fundo.png");
        aluna = Imagens.carregar("/images/personagens/aluna-astronauta.png");

        Sons.tocarMusica("/sons/musicas/musica-espaco.wav");

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                clicou(e.getX(), e.getY());
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                tecla(e.getKeyCode());
            }
        });

        relogio = new Timer(TIQUE, e -> {
            atualizar();
            repaint();
        });
        relogio.start();
    }

    private void tecla(int codigo) {
        if (viajando) {
            return;
        }
        if (codigo == KeyEvent.VK_ENTER || codigo == KeyEvent.VK_SPACE || codigo == KeyEvent.VK_E) {
            seguir();
        } else if (codigo == KeyEvent.VK_ESCAPE) {
            relogio.stop();
            janela.mostrarMenu();
        } else if (codigo == KeyEvent.VK_R) {
            // recomeçar do zero: precisa apertar duas vezes, para não apagar sem querer
            if (confirmarReiniciar) {
                relogio.stop();
                Progresso.zerar();
                janela.iniciarJogo();
            } else {
                confirmarReiniciar = true;
            }
        } else {
            confirmarReiniciar = false;
        }
    }

    private void clicou(int x, int y) {
        if (viajando) {
            return;
        }
        if (botao.contains(x, y)) {
            seguir();
            return;
        }
        int alvo = no + 1;
        double raio = alvo == 4 ? 90 : RAIO[alvo - 1] + 10;
        if (Math.hypot(x - NOS[alvo][0], y - NOS[alvo][1]) < raio) {
            seguir();
        }
    }

    // A aluna viaja até o próximo ponto e depois a cena muda
    private void seguir() {
        viajando = true;
        tempoViagem = 0;
        Sons.tocar("/sons/efeitos/clique.wav");
    }

    private void atualizar() {
        tempo += TIQUE;
        if (viajando) {
            tempoViagem += TIQUE;
            if (tempoViagem >= 1400 && !enviado) {
                enviado = true;
                relogio.stop();
                if (Progresso.terminou()) {
                    janela.mostrarFinal();
                } else {
                    janela.mostrarFase(Progresso.getFase());
                }
            }
        }
    }

    private String textoBotao() {
        if (Progresso.terminou()) {
            return "Voltar para a Terra";
        }
        return Progresso.getPecas() == 0 ? "Começar" : "Continuar";
    }

    // Onde ficam os pés da aluna em cada ponto
    private double[] ponto(int k) {
        if (k == 0) {
            return new double[] { NOS[0][0], NOS[0][1] };
        }
        if (k == 4) {
            return new double[] { NOS[4][0] - 110, NOS[4][1] + 70 };
        }
        return new double[] { NOS[k][0] - RAIO[k - 1] - 32, NOS[k][1] + 20 };
    }

    @Override
    protected void paintComponent(Graphics g0) {

        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        Desenho.suavizar(g);

        g.drawImage(fundo, 0, 0, null);
        g.setColor(new Color(20, 8, 60, 110));
        g.fillRect(0, 0, LARGURA, ALTURA);

        // Lua e Terra
        Desenho.lua(g, 110, 760, 250);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        etiqueta(g, "Lua", 60, 650);
        Desenho.terra(g, NOS[4][0], NOS[4][1], 80);
        etiqueta(g, "Terra", (int) NOS[4][0] - 30, (int) NOS[4][1] + 105);

        // caminhos tracejados (os já percorridos ficam mais claros)
        float[] tracejado = { 9f, 9f };
        for (int i = 0; i < 4; i++) {
            g.setColor(i < no ? new Color(255, 255, 255, 210) : new Color(255, 255, 255, 100));
            g.setStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, tracejado, 0f));
            double[] a = (i == 0) ? new double[] { 215, 480 } : NOS[i];
            g.drawLine((int) a[0], (int) a[1], (int) NOS[i + 1][0], (int) NOS[i + 1][1]);
        }
        g.setStroke(new BasicStroke(1f));

        // planetas das fases
        for (int k = 1; k <= 3; k++) {
            double px = NOS[k][0], py = NOS[k][1], r = RAIO[k - 1];
            double balanco = Math.sin(tempo / 700.0 + k) * 3;
            Planetas.desenhar(g, k - 1, px, py + balanco, r, 1f);
            if (k == 3) {
                // o ET que ajuda na última fase
                Desenho.alien(g, px - r - 22, py + balanco + 40 + Math.sin(tempo / 400.0) * 3, 2);
            }

            // número da fase
            g.setColor(Color.WHITE);
            g.fillOval((int) (px + r * 0.6), (int) (py + r * 0.6 + balanco), 26, 26);
            g.setFont(new Font("SansSerif", Font.BOLD, 18));
            g.setColor(new Color(40, 30, 80));
            g.drawString(String.valueOf(k), (int) (px + r * 0.6) + 8, (int) (py + r * 0.6 + balanco) + 19);

            // as 2 peças que a fase tem: escura = falta recuperar, colorida = já recuperada
            for (int j = 0; j < 2; j++) {
                int indice = (k - 1) * 2 + j;
                SistemaNave s = SistemaNave.values()[indice];
                boolean tem = Progresso.getPecas() > indice;
                Desenho.imagemAjustada(g, tem ? s.imagem() : s.sombra(), px - 22 + j * 44, py - r - 34 + balanco, 38);
            }
            // visto verde: fase concluída
            if (Progresso.getPecas() >= k * 2) {
                int cx = (int) (px - r), cy = (int) (py - r + 6);
                g.setColor(new Color(60, 200, 110));
                g.fillOval(cx - 4, cy - 4, 26, 26);
                g.setColor(Color.WHITE);
                g.setStroke(new BasicStroke(3f));
                g.drawLine(cx + 3, cy + 9, cx + 8, cy + 15);
                g.drawLine(cx + 8, cy + 15, cx + 16, cy + 4);
                g.setStroke(new BasicStroke(1f));
            }
        }

        // a aluna: parada no ponto atual ou viajando para o próximo
        double[] pos;
        if (viajando) {
            double[] a = ponto(no), b = ponto(no + 1);
            double f = Math.min(1, tempoViagem / 1300.0);
            f = f * f * (3 - 2 * f);
            pos = new double[] { a[0] + (b[0] - a[0]) * f, a[1] + (b[1] - a[1]) * f - Math.sin(f * Math.PI) * 40 };
        } else {
            double[] a = ponto(no);
            pos = new double[] { a[0], a[1] + Math.sin(tempo / 300.0) * 3 };
        }
        int w = (int) (aluna.getWidth() * ESCALA_ALUNA), h = (int) (aluna.getHeight() * ESCALA_ALUNA);
        g.drawImage(aluna, (int) pos[0] - w / 2, (int) pos[1] - h, w, h, null);

        // título (o botão de som fica no canto de cima, à esquerda)
        g.setFont(new Font("SansSerif", Font.BOLD, 40));
        Desenho.texto(g, "Missão: voltar para a Terra", 110, 70, Color.WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        Desenho.paragrafo(g, "Zoe: \"Ei, você aí! Preciso da sua ajuda para consertar a nave e voltar para casa. "
                + "As 6 peças estão espalhadas por estes planetas!\"", 112, 104, 620, 24, new Color(225, 215, 255));
        if (!Progresso.terminou() && !viajando) {
            g.setFont(new Font("SansSerif", Font.BOLD, 18));
            Desenho.texto(g, "Próxima peça: " + SistemaNave.values()[Progresso.getPecas()].nome, 112, 180, Desenho.AMARELO);
        }

        // painel de progresso e botão
        Desenho.caixa(g, 730, 600, 520, 94, new Color(14, 10, 50, 230), new Color(140, 120, 255));
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        Desenho.texto(g, "Peças da nave: " + Progresso.getPecas() + " de 6", 752, 636, Color.WHITE);
        g.setColor(new Color(60, 50, 110));
        g.fillRoundRect(752, 652, 250, 14, 14, 14);
        g.setColor(new Color(255, 150, 50));
        g.fillRoundRect(752, 652, (int) (250 * Progresso.getPecas() / 6.0), 14, 14, 14);

        boolean sobre = botao.contains(mouseX, mouseY) && !viajando;
        g.setColor(sobre ? new Color(150, 95, 255) : new Color(125, 70, 235));
        g.fillRoundRect(botao.x, botao.y, botao.width, botao.height, 30, 30);
        g.setColor(new Color(255, 255, 255, 150));
        g.drawRoundRect(botao.x, botao.y, botao.width, botao.height, 30, 30);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        Desenho.textoCentro(g, textoBotao(), botao.x + botao.width / 2, botao.y + 36, Color.WHITE);

        // dicas
        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        String dica = confirmarReiniciar ? "Aperte R de novo para recomeçar do zero (apaga o progresso)"
                : "ENTER: " + textoBotao().toLowerCase() + "  |  ESC: menu  |  R: recomeçar do zero";
        Desenho.texto(g, dica, 22, ALTURA - 14, confirmarReiniciar ? new Color(255, 160, 120) : new Color(235, 235, 255));
    }

    private void etiqueta(Graphics2D g, String s, int x, int y) {
        int w = g.getFontMetrics().stringWidth(s) + 24;
        g.setColor(new Color(255, 255, 255, 40));
        g.fillRoundRect(x, y, w, 28, 28, 28);
        Desenho.texto(g, s, x + 12, y + 20, new Color(235, 235, 255));
    }
}
