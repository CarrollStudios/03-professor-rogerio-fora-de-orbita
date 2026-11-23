package jogo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Cutscene do meteoro, no espaço.
 * A nave voa, a aluna vê um asteroide, ele bate na nave
 * e a nave quebra em 6 peças, que se espalham pelo espaço.
 * No final, a aluna pede ajuda ao jogador (botões Ver de novo e Continuar).
 * Feita por Gih (Front-end) - Carroll Studios.
 */
public class CenaMeteoro extends JPanel {

    // ===== Tamanho da tela e pixel art =====
    private static final int LARGURA = 1280;
    private static final int ALTURA = 720;
    private static final int ESCALA = 4;
    private static final int TIQUE = 30;

    // ===== Nave =====
    private static final int NAVE_X = 640, NAVE_Y = 360;   // o mesmo lugar onde a nave termina na CenaDecolagem   // centro da nave

    // As 6 peças: nome do arquivo, onde ficam dentro da nave e para onde voam
    private static final String[] NOMES_PECAS = { "bico", "janela", "corpo", "asa-esquerda", "asa-direita", "motor" };
    private static final int[][] LUGAR_NA_NAVE = { { 48, 0 }, { 60, 100 }, { 48, 80 }, { 8, 160 }, { 128, 160 }, { 56, 236 } };
    private static final int[][] DESTINO = { { 250, 150 }, { 1010, 150 }, { 190, 380 }, { 440, 330 }, { 880, 300 }, { 1100, 380 } };
    private static final double[] GIRO = { -0.12, 0.15, 0.06, -0.18, 0.2, -0.14 };

    // ===== Etapas da cena =====
    private enum Etapa { VOANDO, ALERTA, BATIDA, EXPLOSAO, FALAS, BOTOES }

    private Etapa etapa = Etapa.VOANDO;
    private int tempoNaEtapa = 0;
    private int tempoTotal = 0;

    // ===== Imagens =====
    private BufferedImage fundo, nave, asteroide, alunaAstronauta;
    private BufferedImage[] fogo = new BufferedImage[2];

    // ===== Estado =====
    private double astX = 1450, astY = -150;   // centro do asteroide
    private double astVX = 0, astVY = 0;
    private List<PecaNave> pecas = new ArrayList<>();   // fica vazia até a nave quebrar
    private float clarao = 0f;

    // ===== Falas =====
    private Fala alerta = new Fala("Zoe", "Cuidado! Um asteroide!");
    private List<Fala> falas = new ArrayList<>();
    private Fala falaNaTela = null;
    private int falaAtual = 0;
    private int letrasVisiveis = 0;
    private CaixaDeFala caixa = new CaixaDeFala();

    private JButton botaoVerDeNovo, botaoContinuar;
    private TelaJogo janela;
    private Timer relogio;

    public CenaMeteoro(TelaJogo janela) {

        this.janela = janela;
        setPreferredSize(new Dimension(LARGURA, ALTURA));
        setBackground(new Color(0x0D0B1E));
        setLayout(null);   // eu escolho a posição de cada botão

        fundo = Imagens.carregar("/images/meteoro/espaco-fundo.png");
        asteroide = Imagens.carregar("/images/meteoro/asteroide.png");
        nave = Imagens.carregar("/images/nave/nave-com-aluna.png");
        fogo[0] = Imagens.carregar("/images/nave/nave-fogo-1.png");
        fogo[1] = Imagens.carregar("/images/nave/nave-fogo-2.png");
        alunaAstronauta = Imagens.carregar("/images/personagens/aluna-astronauta.png");

        falas.add(new Fala("Zoe", "Ufa! Ainda bem que eu estava de capacete!"));
        falas.add(new Fala("Zoe", "A nave quebrou em 6 peças, e elas se espalharam pelo espaço..."));
        falas.add(new Fala("Zoe", "Me ajuda a recuperar as peças? Assim a gente volta para a Terra!"));

        // botões (aparecem só no final)
        botaoVerDeNovo = Botoes.criar("/images/meteoro/botao-ver-de-novo.png", "/images/meteoro/botao-ver-de-novo-hover.png");
        botaoVerDeNovo.setLocation(312, 460);
        botaoVerDeNovo.addActionListener(e -> {
            Sons.tocar("/sons/efeitos/clique.wav");
            relogio.stop();
            janela.mostrarSala();      // revê a história desde a aluna entrando na sala
        });
        botaoContinuar = Botoes.criar("/images/meteoro/botao-continuar.png", "/images/meteoro/botao-continuar-hover.png");
        botaoContinuar.setLocation(660, 460);
        botaoContinuar.addActionListener(e -> {
            Sons.tocar("/sons/efeitos/clique.wav");
            relogio.stop();
            janela.mostrarMapa();      // próxima tela: o mapa da missão
        });
        botaoVerDeNovo.setVisible(false);
        botaoContinuar.setVisible(false);
        add(botaoVerDeNovo);
        add(botaoContinuar);

        Sons.tocarMusica("/sons/musicas/musica-espaco.wav");   // continua a mesma da decolagem

        // clique na tela: a frase aparece inteira; outro clique: vai para a próxima
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                avancarFala();
            }
        });

        relogio = new Timer(TIQUE, e -> {
            atualizar();
            repaint();
        });
        relogio.start();
    }

    // Chamado quando o jogador clica na tela (só nas falas depois da batida)
    private void avancarFala() {
        if (etapa != Etapa.FALAS || falaNaTela == null) {
            return;
        }
        if (letrasVisiveis < falaNaTela.getTexto().length()) {
            letrasVisiveis = falaNaTela.getTexto().length();   // mostra a frase inteira
            tempoNaEtapa = 0;
        } else {
            tempoNaEtapa = tempoDeLeitura(falaNaTela);         // pula a pausa
        }
    }

    private void irPara(Etapa proxima) {
        etapa = proxima;
        tempoNaEtapa = 0;
    }

    private static int px(double valor) {
        return (int) Math.round(valor / 4.0) * 4;
    }

    // Efeito de máquina de escrever. Devolve true quando a frase terminou e já passou a pausa.
    private boolean escrever(Fala fala, int pausa) {
        if (letrasVisiveis < fala.getTexto().length()) {
            letrasVisiveis++;
            char letra = fala.getTexto().charAt(letrasVisiveis - 1);
            if (letra != ' ' && letrasVisiveis % 2 == 1) {
                Sons.tocar("/sons/efeitos/fala-letra.wav");   // "tic" das letrinhas
            }
            tempoNaEtapa = 0;
            return false;
        }
        return tempoNaEtapa >= pausa;
    }

    // Pausa depois da frase: o tempo de uma pessoa ler
    private int tempoDeLeitura(Fala fala) {
        return 500 + fala.getTexto().length() * 20;
    }

    void atualizar() {

        tempoNaEtapa += TIQUE;
        tempoTotal += TIQUE;

        switch (etapa) {

            case VOANDO:
                if (tempoNaEtapa >= 300) {
                    falaNaTela = alerta;
                    letrasVisiveis = 0;
                    irPara(Etapa.ALERTA);
                }
                break;

            case ALERTA:
                // o asteroide aparece lá em cima, à direita, enquanto ela grita
                astX += (1000 - astX) * 0.08;
                astY += (150 - astY) * 0.08;
                if (escrever(alerta, 250)) {
                    falaNaTela = null;
                    irPara(Etapa.BATIDA);
                }
                break;

            case BATIDA:
                // o asteroide vai direto na nave
                double dx = NAVE_X + 40 - astX;
                double dy = NAVE_Y - 40 - astY;
                double distancia = Math.hypot(dx, dy);
                astX += dx / distancia * 50;
                astY += dy / distancia * 50;
                if (distancia < 70) {
                    quebrarNave();
                    irPara(Etapa.EXPLOSAO);
                }
                break;

            case EXPLOSAO:
                clarao = Math.max(0f, 1f - tempoNaEtapa / 300f);
                if (tempoNaEtapa >= 1000) {
                    falaNaTela = falas.get(0);
                    letrasVisiveis = 0;
                    irPara(Etapa.FALAS);
                }
                break;

            case FALAS:
                if (escrever(falaNaTela, tempoDeLeitura(falaNaTela))) {
                    if (falaAtual < falas.size() - 1) {
                        falaAtual++;
                        falaNaTela = falas.get(falaAtual);
                        letrasVisiveis = 0;
                        tempoNaEtapa = 0;
                    } else {
                        botaoVerDeNovo.setVisible(true);
                        botaoContinuar.setVisible(true);
                        irPara(Etapa.BOTOES);
                    }
                }
                break;

            case BOTOES:
                // só espera o jogador clicar (as peças continuam flutuando)
                break;
        }

        // coisas que se mexem em todas as etapas
        astX += astVX;
        astY += astVY;
        for (PecaNave peca : pecas) {
            peca.mover();
        }
    }

    // O asteroide bate: a nave vira 6 peças, e cada uma voa para um lado
    private void quebrarNave() {
        int esquerda = NAVE_X - nave.getWidth() / 2;
        int topo = NAVE_Y - nave.getHeight() / 2;
        for (int i = 0; i < NOMES_PECAS.length; i++) {
            BufferedImage imagem = Imagens.carregar("/images/nave/nave-peca-" + NOMES_PECAS[i] + ".png");
            double inicioX = esquerda + LUGAR_NA_NAVE[i][0] + imagem.getWidth() / 2.0;
            double inicioY = topo + LUGAR_NA_NAVE[i][1] + imagem.getHeight() / 2.0;
            pecas.add(new PecaNave(imagem, inicioX, inicioY, DESTINO[i][0], DESTINO[i][1], GIRO[i]));
        }
        Sons.tocar("/sons/efeitos/batida.wav");
        Sons.tocar("/sons/efeitos/pecas.wav");
        clarao = 1f;
        astVX = 24;    // o asteroide é jogado para longe
        astVY = -18;
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        // a tela treme logo depois da batida
        int tremor = (etapa == Etapa.EXPLOSAO && tempoNaEtapa < 500) ? ((tempoNaEtapa / 60) % 2 == 0 ? -8 : 8) : 0;
        g2.translate(tremor, 0);

        g2.drawImage(fundo, 0, 0, null);

        if (pecas.isEmpty()) {
            // nave inteira, balançando, com o fogo aceso
            int x = px(NAVE_X - nave.getWidth() / 2.0 + Math.sin(tempoTotal / 250.0) * 6);
            int y = px(NAVE_Y - nave.getHeight() / 2.0);
            g2.drawImage(fogo[(tempoTotal / 90) % 2], x, y + nave.getHeight() - 8, null);
            g2.drawImage(nave, x, y, null);
        } else {
            for (PecaNave peca : pecas) {
                peca.desenhar(g2);
            }
            // a aluna de capacete flutuando
            int ax = px(NAVE_X - alunaAstronauta.getWidth() / 2.0);
            int ay = px(NAVE_Y - alunaAstronauta.getHeight() / 2.0 - 30 + Math.sin(tempoTotal / 400.0) * 8);
            g2.drawImage(alunaAstronauta, ax, ay, null);
        }

        // asteroide com um rastro
        if (astX > -200 && astX < LARGURA + 200 && astY > -200) {
            for (int i = 3; i >= 1; i--) {
                g2.setColor(new Color(0xF7, 0x90, 0x2F, 40 * (4 - i)));
                int tamanho = 72 - i * 12;
                double rx = astX - (etapa == Etapa.BATIDA ? (NAVE_X - astX) : 20) * 0.08 * i;
                double ry = astY - (etapa == Etapa.BATIDA ? (NAVE_Y - astY) : -12) * 0.08 * i;
                g2.fillRect(px(rx - tamanho / 2.0), px(ry - tamanho / 2.0), px(tamanho), px(tamanho));
            }
            g2.drawImage(asteroide, px(astX - asteroide.getWidth() / 2.0), px(astY - asteroide.getHeight() / 2.0), null);
        }

        g2.translate(-tremor, 0);

        // caixa de fala
        if (falaNaTela != null) {
            BufferedImage pequena = new BufferedImage(LARGURA / ESCALA, ALTURA / ESCALA, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gp = pequena.createGraphics();
            gp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            gp.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            // setinha ▼ só nas falas depois da batida, quando a frase já terminou
            boolean terminou = etapa == Etapa.FALAS && letrasVisiveis == falaNaTela.getTexto().length();
            caixa.desenhar(gp, falaNaTela.getNome(), falaNaTela.getTexto().substring(0, letrasVisiveis), terminou);
            gp.dispose();
            g2.drawImage(pequena, 0, 0, LARGURA, ALTURA, null);
        }

        // clarão da batida
        if (clarao > 0) {
            g2.setColor(new Color(1f, 1f, 1f, clarao));
            g2.fillRect(0, 0, LARGURA, ALTURA);
        }
    }
}
