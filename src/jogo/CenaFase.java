package jogo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Uma fase no espaço. A Zoe voa até a peça solta, aperta E e resolve o puzzle.
 * Cada erro gasta 1 de oxigênio; sem oxigênio é game over.
 * Na fase 3 aparece um ET que dá até 2 dicas (tecla H dentro do puzzle).
 * Cada fase tem 2 peças:
 *   fase 1 (índice 0) = peças 1 e 2, fase 2 (índice 1) = peças 3 e 4, fase 3 (índice 2) = peças 5 e 6.
 * O progresso é salvo (classe Progresso) cada vez que uma peça é recuperada.
 */
public class CenaFase extends JPanel {

    private static final int LARGURA = 1280;
    private static final int ALTURA = 720;
    private static final int TIQUE = 30;
    private static final double DT = TIQUE / 1000.0;

    // Onde fica cada uma das 6 peças soltas
    private static final double[][] POSICAO = { { 930, 340 }, { 520, 520 }, { 330, 330 }, { 980, 520 }, { 700, 350 },
            { 1000, 390 } };
    private static final Color[] COR_PLANETA = { new Color(255, 150, 50), new Color(235, 80, 170), new Color(40, 200, 220) };
    private static final String[] NOME_PLANETA = { "Planeta Brasa", "Planeta Magenta", "Planeta Ciano" };

    private static final double ESCALA_ALUNA = 0.5;

    private enum Estado { FALANDO, VOANDO, PUZZLE, COLETANDO, CONCLUIDA, SEM_OXIGENIO }

    private final int fase;
    private Estado estado = Estado.FALANDO;

    private BufferedImage fundo, aluna;
    private double x = 200, y = 460;          // centro da aluna
    private boolean viradaParaDireita = true;
    private boolean esq, dir, cima, baixo;
    private int mouseX, mouseY;
    private int tempo = 0;
    private int tempoPeca = 0;                // para a peça "aparecer" crescendo
    private int tempoColeta = 0;
    private int tempoFim = 0;
    private Puzzle puzzle;

    // o ET da fase 3 (flutua perto do canto esquerdo)
    private static final double ET_X = 330, ET_Y = 360;

    // aviso da Zoe (balãozinho embaixo da tela)
    private String avisoTexto = "";
    private int avisoTempo = 0;

    // estrelas passando
    private final double[][] estrelas = new double[90][3];

    // falas do começo da fase
    private final List<Fala> falas = new ArrayList<>();
    private int falaAtual = 0;
    private int letras = 0;
    private final CaixaDeFala caixa = new CaixaDeFala();

    private TelaJogo janela;
    private Timer relogio;

    public CenaFase(TelaJogo janela, int fase) {

        this.janela = janela;
        this.fase = fase;

        setPreferredSize(new Dimension(LARGURA, ALTURA));
        setBackground(new Color(0x0D0B1E));
        setFocusable(true);

        fundo = Imagens.carregar("/images/meteoro/espaco-fundo.png");
        aluna = Imagens.carregar("/images/personagens/aluna-astronauta.png");

        Random sorteio = new Random(fase + 1);
        for (double[] e : estrelas) {
            e[0] = sorteio.nextDouble() * LARGURA;
            e[1] = sorteio.nextDouble() * ALTURA;
            e[2] = 0.5 + sorteio.nextDouble() * 2.5;
        }

        // Falas do começo da fase (edite à vontade; cada fala cabe em até 2 linhas da caixa)
        if (fase == 0) {
            falas.add(new Fala("Zoe", "Ufa, que planeta é esse? Deve ter uma peça da nave por aqui!"));
            falas.add(new Fala("Zoe", "Cada fase tem 2 peças. Resolva os desafios que eu recupero as peças!"));
            falas.add(new Fala("Zoe", "Cuidado: cada erro gasta o oxigênio do meu capacete. Conto com você!"));
            falas.add(new Fala("Zoe", "Setas ou WASD para voar. Perto da peça, aperte E."));
        } else if (fase == 1) {
            falas.add(new Fala("Zoe", "Fase 2! Agora ficou mais difícil, presta atenção."));
            falas.add(new Fala("Zoe", "Troquei o tanque do capacete: oxigênio cheio de novo!"));
            falas.add(new Fala("Zoe", "Se o professor Rogério me visse agora, ia ter que engolir aquele papo de que eu não sei programar!"));
        } else {
            falas.add(new Fala("Zoe", "Última fase! Mais duas peças e a gente volta para casa."));
            falas.add(new Fala("Zoe", "Troquei o tanque de novo. Mas esses desafios parecem bem difíceis..."));
            falas.add(new Fala("Zoe", "Ei! Tem alguém ali... um ET?!"));
            falas.add(new Fala("ET", "Calma, terráquea, eu venho em paz! Vi o que o seu professor fez por causa de uma porta."));
            falas.add(new Fala("ET", "Que povo estranho! Posso te dar até 2 dicas. Dentro de um desafio, aperte H."));
            falas.add(new Fala("Zoe", "Valeu, ET! Vou guardar as dicas para quando eu realmente precisar."));
        }
        // se o jogador voltou no meio da fase (já tinha 1 peça), não repete a conversa
        if (Progresso.getPecas() % 2 == 1) {
            falas.clear();
            estado = Estado.VOANDO;
        }

        Sons.tocarMusica("/sons/musicas/musica-espaco.wav");

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                teclaApertada(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                teclaSolta(e.getKeyCode());
            }
        });
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

        relogio = new Timer(TIQUE, e -> {
            atualizar();
            repaint();
        });
        relogio.start();
    }

    // ================================================================== lógica

    private int pecaAtual() {
        return Progresso.getPecas();
    }

    private double pecaX() {
        return POSICAO[pecaAtual()][0];
    }

    private double pecaY() {
        return POSICAO[pecaAtual()][1] + Math.sin(tempo / 300.0) * 8;
    }

    private boolean pertoDaPeca() {
        return pecaAtual() < 6 && Math.hypot(x - pecaX(), y - pecaY()) < 140;
    }

    private void atualizar() {

        tempo += TIQUE;
        if (avisoTempo > 0) {
            avisoTempo -= TIQUE;
        }

        for (double[] e : estrelas) {
            e[0] -= e[2];
            if (e[0] < -4) {
                e[0] = LARGURA + 4;
            }
        }

        switch (estado) {

            case FALANDO:
                if (!falas.isEmpty() && letras < falas.get(falaAtual).getTexto().length()) {
                    letras++;
                }
                break;

            case VOANDO:
                tempoPeca += TIQUE;
                int dx = (dir ? 1 : 0) - (esq ? 1 : 0);
                int dy = (baixo ? 1 : 0) - (cima ? 1 : 0);
                double tamanho = Math.sqrt(dx * dx + dy * dy);
                if (tamanho > 0) {
                    x += dx / tamanho * 8;
                    y += dy / tamanho * 8;
                }
                if (dx != 0) {
                    viradaParaDireita = dx > 0;
                }
                x = Math.max(70, Math.min(LARGURA - 70, x));
                y = Math.max(230, Math.min(ALTURA - 100, y));
                break;

            case PUZZLE:
                puzzle.atualizar(DT);
                int erros = puzzle.coletarErros();
                if (erros > 0) {
                    errouNoPuzzle(erros);
                } else if (puzzle.estaResolvido()) {
                    estado = Estado.COLETANDO;
                    tempoColeta = 0;
                    Sons.tocar("/sons/efeitos/pecas.wav");
                    avisar("Isso! Mandou bem!");
                }
                break;

            case SEM_OXIGENIO:
                tempoFim += TIQUE;
                if (tempoFim > 1300) {
                    relogio.stop();
                    janela.mostrarGameOver();
                }
                break;

            case COLETANDO:
                tempoColeta += TIQUE;
                if (tempoColeta >= 1100) {
                    Progresso.recuperouPeca();   // salva o progresso
                    puzzle = null;
                    if (Progresso.getPecas() >= fase * 2 + 2) {
                        estado = Estado.CONCLUIDA;
                        tempoFim = 0;
                    } else {
                        estado = Estado.VOANDO;
                        tempoPeca = 0;
                    }
                }
                break;

            case CONCLUIDA:
                tempoFim += TIQUE;
                break;
        }
    }

    // Cada erro tira 1 de oxigênio. Chegou a zero: game over.
    private void errouNoPuzzle(int erros) {
        boolean acabou = false;
        for (int i = 0; i < erros && !acabou; i++) {
            acabou = Progresso.perderOxigenio();
        }
        Sons.tocar("/sons/efeitos/batida.wav");
        if (acabou) {
            estado = Estado.SEM_OXIGENIO;
            tempoFim = 0;
            avisar("Não consigo respirar... o oxigênio acabou!");
        } else if (Progresso.getOxigenio() <= 1) {
            avisar("O oxigênio está acabando, capricha nessa!");
        } else {
            avisar("Ops… lá se vai um pouco do meu oxigênio.");
        }
    }

    private void avisar(String texto) {
        avisoTexto = texto;
        avisoTempo = 3500;
    }

    private boolean pertoDoET() {
        return fase == 2 && Math.hypot(x - ET_X, y - etY()) < 150;
    }

    private double etY() {
        return ET_Y + Math.sin(tempo / 350.0) * 7;
    }

    // Apertar E perto do ET: ele lembra como pedir as dicas
    private void falarComET() {
        falas.clear();
        int n = Progresso.getDicas();
        falas.add(new Fala("ET", n > 0
                ? "Dentro de um desafio, aperte H e eu ajudo. Ainda tenho " + n + (n == 1 ? " dica!" : " dicas!")
                : "Já gastei todas as minhas dicas, terráquea. Agora é com você!"));
        falaAtual = 0;
        letras = 0;
        estado = Estado.FALANDO;
    }

    // ================================================================== entrada

    private void teclaApertada(int codigo) {

        if (codigo == KeyEvent.VK_LEFT || codigo == KeyEvent.VK_A) {
            esq = true;
        } else if (codigo == KeyEvent.VK_RIGHT || codigo == KeyEvent.VK_D) {
            dir = true;
        } else if (codigo == KeyEvent.VK_UP || codigo == KeyEvent.VK_W) {
            cima = true;
        } else if (codigo == KeyEvent.VK_DOWN || codigo == KeyEvent.VK_S) {
            baixo = true;
        }

        boolean acao = codigo == KeyEvent.VK_ENTER || codigo == KeyEvent.VK_SPACE || codigo == KeyEvent.VK_E;

        switch (estado) {
            case FALANDO:
                if (acao) {
                    avancarFala();
                }
                break;
            case VOANDO:
                if (acao && pertoDaPeca()) {
                    puzzle = Puzzles.criar(pecaAtual());
                    estado = Estado.PUZZLE;
                    Sons.tocar("/sons/efeitos/clique.wav");
                } else if (acao && pertoDoET()) {
                    falarComET();
                } else if (codigo == KeyEvent.VK_ESCAPE) {
                    voltarAoMapa();
                }
                break;
            case PUZZLE:
                if (codigo == KeyEvent.VK_ESCAPE) {
                    puzzle = null;
                    estado = Estado.VOANDO;
                } else {
                    puzzle.teclaGeral(codigo);
                }
                break;
            case CONCLUIDA:
                if (acao && tempoFim > 500) {
                    voltarAoMapa();
                }
                break;
            default:
                break;
        }
    }

    private void teclaSolta(int codigo) {
        if (codigo == KeyEvent.VK_LEFT || codigo == KeyEvent.VK_A) {
            esq = false;
        } else if (codigo == KeyEvent.VK_RIGHT || codigo == KeyEvent.VK_D) {
            dir = false;
        } else if (codigo == KeyEvent.VK_UP || codigo == KeyEvent.VK_W) {
            cima = false;
        } else if (codigo == KeyEvent.VK_DOWN || codigo == KeyEvent.VK_S) {
            baixo = false;
        }
    }

    private void clicou(int mx, int my) {
        if (estado == Estado.FALANDO) {
            avancarFala();
        } else if (estado == Estado.PUZZLE) {
            puzzle.cliqueGeral(mx, my);
        } else if (estado == Estado.CONCLUIDA && tempoFim > 500) {
            voltarAoMapa();
        } else if (estado == Estado.VOANDO && pertoDaPeca()
                && Math.hypot(mx - pecaX(), my - pecaY()) < 70) {
            puzzle = Puzzles.criar(pecaAtual());
            estado = Estado.PUZZLE;
            Sons.tocar("/sons/efeitos/clique.wav");
        } else if (estado == Estado.VOANDO && pertoDoET() && Math.hypot(mx - ET_X, my - etY()) < 80) {
            falarComET();
        }
    }

    // Primeiro clique: mostra a frase inteira. Segundo: vai para a próxima.
    private void avancarFala() {
        Fala fala = falas.get(falaAtual);
        if (letras < fala.getTexto().length()) {
            letras = fala.getTexto().length();
        } else if (falaAtual < falas.size() - 1) {
            falaAtual++;
            letras = 0;
        } else {
            estado = Estado.VOANDO;
            tempoPeca = 0;
        }
    }

    private void voltarAoMapa() {
        relogio.stop();
        janela.mostrarMapa();
    }

    // ================================================================== desenho

    private int xDoSlot(int i) {
        return LARGURA - 20 - 6 * 62 + i * 62;
    }

    @Override
    protected void paintComponent(Graphics g0) {

        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        Desenho.suavizar(g);

        g.drawImage(fundo, 0, 0, null);
        Color cor = COR_PLANETA[fase];
        g.setColor(new Color(cor.getRed(), cor.getGreen(), cor.getBlue(), 28));
        g.fillRect(0, 0, LARGURA, ALTURA);
        // o planeta da fase, grande no canto, e uma lua pequena lá em cima
        Planetas.desenhar(g, fase, 1120, 900, 380, 0.75f);

        g.setColor(new Color(255, 255, 255, 160));
        for (double[] e : estrelas) {
            g.fillRect((int) e[0], (int) e[1], e[2] > 2 ? 3 : 2, e[2] > 2 ? 3 : 2);
        }

        // a peça solta
        if (estado == Estado.VOANDO || estado == Estado.PUZZLE || estado == Estado.FALANDO) {
            if (pecaAtual() < 6) {
                double crescer = Math.min(1, tempoPeca / 500.0);
                crescer = 1 - Math.pow(1 - crescer, 3);
                double raio = (70 + Math.sin(tempo / 120.0) * 6) * crescer;
                g.setColor(new Color(255, 245, 160, 55));
                g.fillOval((int) (pecaX() - raio), (int) (pecaY() - raio), (int) (2 * raio), (int) (2 * raio));
                Desenho.imagemAjustada(g, SistemaNave.values()[pecaAtual()].imagem(), pecaX(), pecaY(), (int) (96 * crescer));
            }
        }

        // o ET da fase 3
        if (fase == 2) {
            Desenho.alien(g, ET_X, etY() + 58, 5);
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            Desenho.textoCentro(g, "ET", (int) ET_X, (int) etY() + 82, new Color(150, 255, 180));
            if (estado == Estado.VOANDO && pertoDoET() && !pertoDaPeca()) {
                g.setFont(new Font("SansSerif", Font.BOLD, 18));
                String t = "Pressione E para falar com o ET";
                int lw = g.getFontMetrics().stringWidth(t) + 30;
                int bx = (int) ET_X - lw / 2, by = (int) etY() - 100;
                Desenho.caixa(g, bx, by, lw, 36, new Color(10, 12, 30, 230), new Color(150, 255, 180));
                Desenho.texto(g, t, bx + 15, by + 25, new Color(150, 255, 180));
            }
        }

        // a aluna flutuando
        int w = (int) (aluna.getWidth() * ESCALA_ALUNA), h = (int) (aluna.getHeight() * ESCALA_ALUNA);
        int ay = (int) (y - h / 2 + Math.sin(tempo / 220.0) * 5);
        if (viradaParaDireita) {
            g.drawImage(aluna, (int) x - w / 2, ay, w, h, null);
        } else {
            g.drawImage(aluna, (int) x + w / 2, ay, -w, h, null);
        }

        // aviso de interação
        if (estado == Estado.VOANDO && pertoDaPeca()) {
            String s = "Pressione E para consertar a peça";
            g.setFont(new Font("SansSerif", Font.BOLD, 18));
            int largura = g.getFontMetrics().stringWidth(s) + 30;
            int bx = Math.max(10, Math.min(LARGURA - largura - 10, (int) pecaX() - largura / 2));
            int by = (int) pecaY() - 105;
            Desenho.caixa(g, bx, by, largura, 36, new Color(10, 12, 30, 230), Desenho.AMARELO);
            Desenho.texto(g, s, bx + 15, by + 25, Desenho.AMARELO);
        }

        // a peça voando para o painel
        if (estado == Estado.COLETANDO) {
            int i = pecaAtual();
            double f = Math.min(1, tempoColeta / 1000.0);
            f = f * f * (3 - 2 * f);
            double tx = xDoSlot(i) + 29, ty = 18 + 29;
            Desenho.imagemAjustada(g, SistemaNave.values()[i].imagem(), POSICAO[i][0] + (tx - POSICAO[i][0]) * f,
                    POSICAO[i][1] + (ty - POSICAO[i][1]) * f, (int) (96 - 44 * f));
        }

        desenharPainel(g);


        if (estado == Estado.PUZZLE) {
            g.setColor(new Color(0, 0, 10, 170));
            g.fillRect(0, 0, LARGURA, ALTURA);
            puzzle.desenhar(g, mouseX, mouseY);
        }

        // aviso da Zoe (balão embaixo da tela)
        if (avisoTempo > 0 && estado != Estado.FALANDO && estado != Estado.CONCLUIDA) {
            Desenho.caixa(g, 190, 628, 900, 58, new Color(26, 23, 48, 245), Desenho.ROXO);
            g.setColor(new Color(0x6D28D9));
            g.fillRoundRect(206, 616, 76, 24, 12, 12);
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            Desenho.texto(g, "Zoe", 220, 634, Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 22));
            Desenho.texto(g, avisoTexto, 212, 668, Desenho.TEXTO);
        }
        if (estado == Estado.FALANDO && !falas.isEmpty()) {
            desenharFala(g);
        }
        if (estado == Estado.CONCLUIDA) {
            desenharConcluida(g);
        }
        if (estado == Estado.SEM_OXIGENIO) {
            g.setColor(new Color(60, 0, 10, (int) Math.min(200, tempoFim * 0.16)));
            g.fillRect(0, 0, LARGURA, ALTURA);
        }
    }

    // Faixa de cima: nome da fase e as 6 peças
    private void desenharPainel(Graphics2D g) {

        Desenho.caixa(g, 100, 14, 560, 64, new Color(10, 12, 30, 215), COR_PLANETA[fase]);
        g.setFont(new Font("SansSerif", Font.BOLD, 24));
        Desenho.texto(g, "Fase " + (fase + 1) + " - " + NOME_PLANETA[fase], 118, 42, Color.WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        if (pecaAtual() < 6 && estado != Estado.CONCLUIDA) {
            Desenho.texto(g, "Encontre a peça: " + SistemaNave.values()[pecaAtual()].nome, 118, 66, Desenho.AMARELO);
        }

        Desenho.caixa(g, xDoSlot(0) - 10, 10, 6 * 62 + 8, 72, new Color(10, 12, 30, 215), new Color(150, 130, 255));
        for (int i = 0; i < 6; i++) {
            boolean tem = Progresso.getPecas() > i && !(estado == Estado.COLETANDO && i == pecaAtual());
            SistemaNave s = SistemaNave.values()[i];
            Desenho.imagemAjustada(g, tem ? s.imagem() : s.sombra(), xDoSlot(i) + 26, 46, 48);
        }

        // oxigênio da Zoe
        if (estado != Estado.PUZZLE) {   // dentro do puzzle, o oxigênio aparece no quadro do puzzle
            Desenho.caixa(g, 100, 88, 168, 76, new Color(10, 12, 30, 215), new Color(110, 215, 255));
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            Desenho.texto(g, "OXIGÊNIO", 112, 108, new Color(170, 225, 255));
            Desenho.oxigenio(g, 112, 118, Progresso.getOxigenio(), Progresso.OXIGENIO_MAXIMO, tempo);
        }

        if (estado == Estado.VOANDO) {
            g.setFont(new Font("SansSerif", Font.PLAIN, 16));
            Desenho.texto(g, "Setas/WASD: voar  |  E: consertar a peça  |  ESC: voltar ao mapa", 22, ALTURA - 14,
                    new Color(235, 235, 255));
        }
    }

    // Caixa de fala no mesmo estilo da CenaMeteoro (tela pequena aumentada 4 vezes)
    private void desenharFala(Graphics2D g) {
        Fala fala = falas.get(falaAtual);
        BufferedImage pequena = new BufferedImage(LARGURA / 4, ALTURA / 4, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gp = pequena.createGraphics();
        gp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        gp.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        boolean terminou = letras == fala.getTexto().length();
        caixa.desenhar(gp, fala.getNome(), fala.getTexto().substring(0, letras), terminou);
        gp.dispose();
        g.drawImage(pequena, 0, 0, LARGURA, ALTURA, null);
    }

    // Quadro "Fase concluída!" com as 2 peças recuperadas
    private void desenharConcluida(Graphics2D g) {
        g.setColor(new Color(0, 0, 10, 170));
        g.fillRect(0, 0, LARGURA, ALTURA);
        int px = 220, py = 130, pw = 840, ph = 450;
        Desenho.caixa(g, px, py, pw, ph, new Color(12, 14, 40, 245), COR_PLANETA[fase]);
        g.setFont(new Font("SansSerif", Font.BOLD, 34));
        Desenho.textoCentro(g, "Fase " + (fase + 1) + " concluída!", LARGURA / 2, py + 56, Desenho.AMARELO);
        g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        Desenho.textoCentro(g, "Você recuperou estas peças da nave:", LARGURA / 2, py + 90, new Color(220, 220, 245));
        for (int j = 0; j < 2; j++) {
            SistemaNave s = SistemaNave.values()[fase * 2 + j];
            int by = py + 115 + j * 126;
            Desenho.imagemAjustada(g, s.imagem(), px + 84, by + 52, 88);
            g.setFont(new Font("SansSerif", Font.BOLD, 24));
            Desenho.texto(g, s.nome + " - " + s.subtitulo, px + 150, by + 34, Color.WHITE);
            g.setFont(new Font("SansSerif", Font.PLAIN, 18));
            Desenho.paragrafo(g, s.descricao, px + 150, by + 64, pw - 190, 24, new Color(200, 200, 235));
        }
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        Desenho.textoCentro(g, "Zoe: \"Mais duas peças! A nave está ficando inteira.\"", LARGURA / 2, py + ph - 58,
                new Color(200, 190, 255));
        if ((tempoFim / 500) % 2 == 0) {
            g.setFont(new Font("SansSerif", Font.BOLD, 18));
            Desenho.textoCentro(g, "ENTER ou clique: voltar ao mapa", LARGURA / 2, py + ph - 22, Desenho.AMARELO);
        }
    }
}
