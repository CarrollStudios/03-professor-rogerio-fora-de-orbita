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
 * Cutscene da sala de aula (LAB 3), sem ação do jogador.
 * A aluna entra, o Professor Roger se assusta com a porta aberta
 * e manda a aluna para o espaço (a nave vem buscar na CenaDecolagem). Em cada fala, a câmera dá zoom
 * em quem está falando (os personagens não se mexem).
 * Feita por Gih (Front-end) - Carroll Studios.
 */
public class CenaSala extends JPanel {

    // ===== Tamanho da tela e pixel art =====
    private static final int LARGURA = 1280;
    private static final int ALTURA = 720;
    private static final int ESCALA = 4;   // a caixa de fala é desenhada pequena e aumentada 4 vezes
    private static final int TIQUE = 30;

    // ===== Posições (na frente da lousa) =====
    private static final int CHAO = 460;       // pés da aluna
    private static final int X_PORTA = 150;    // de onde a aluna entra
    private static final int X_ALUNA = 520;
    private static final int X_ROGER = 1010;

    // ===== Zoom da câmera =====
    private static final double ZOOM_FALA = 2.5;   // quanto a câmera aproxima
    private double zoom = 1.0, cameraX = LARGURA / 2.0, cameraY = ALTURA / 2.0;   // como está agora
    private double alvoZoom = 1.0, alvoX = LARGURA / 2.0, alvoY = ALTURA / 2.0;   // para onde está indo

    // ===== Etapas da cena =====
    private enum Etapa { ALUNA_ENTRANDO, DIALOGO, FIM }

    private Etapa etapa = Etapa.ALUNA_ENTRANDO;   // começa com a aluna já entrando (sem tela preta)
    private int tempoNaEtapa = 0;

    // ===== Imagens =====
    private BufferedImage fundo, colegas, roger, alunaFrente;
    private Caminhada caminhada = new Caminhada();   // os 6 desenhos de um passo e o som dos passos
    private BufferedImage cena = new BufferedImage(LARGURA, ALTURA, BufferedImage.TYPE_INT_ARGB);

    // ===== Estado =====
    private double alunaX = X_PORTA;
    private BufferedImage spriteAluna;

    // ===== Diálogo (roteiro da equipe) =====
    private List<Fala> falas = new ArrayList<>();
    private int falaAtual = 0;
    private int letrasVisiveis = 0;          // efeito de máquina de escrever
    private CaixaDeFala caixa = new CaixaDeFala();

    private TelaJogo janela;
    private Timer relogio;

    public CenaSala(TelaJogo janela) {

        this.janela = janela;
        setPreferredSize(new Dimension(LARGURA, ALTURA));
        setBackground(Color.BLACK);

        fundo = Imagens.carregar("/images/sala/sala-fundo.png");
        colegas = Imagens.carregar("/images/sala/sala-frente.png");   // colegas e computadores
        roger = Imagens.carregar("/images/personagens/roger-frente.png");
        alunaFrente = Imagens.carregar("/images/personagens/aluna-frente.png");
        spriteAluna = caminhada.primeiroDesenho();

        falas.add(new Fala("Professor Roger", "Quem deixou a porta aberta... de novo?"));
        falas.add(new Fala("Aluna", "Foi o vento, professor."));
        falas.add(new Fala("Professor Roger", "O vento? Então vai explicar isso lá no espaço."));
        falas.add(new Fala("Aluna", "Pera, como assim?!"));
        falas.add(new Fala("Professor Roger", "Boa viagem!"));

        // botão PULAR: vai direto para a decolagem (para quem já viu a história)
        setLayout(null);   // eu escolho a posição do botão
        JButton pular = Botoes.criar("/images/botoes/botao-pular.png", "/images/botoes/botao-pular-hover.png");
        pular.setLocation(LARGURA - pular.getWidth() - 24, 24);   // canto de cima, à direita
        pular.addActionListener(e -> {
            Sons.tocar("/sons/efeitos/clique.wav");
            relogio.stop();
            if (janela != null) {
                janela.mostrarDecolagem();
            }
        });
        add(pular);

        // música da escola (se ela já estiver tocando, continua; no "Ver de novo", ela volta)
        Sons.tocarMusica("/sons/musicas/musica-escola.wav");

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

    // Chamado quando o jogador clica na tela
    private void avancarFala() {
        if (etapa != Etapa.DIALOGO) {
            return;
        }
        String texto = falas.get(falaAtual).getTexto();
        if (letrasVisiveis < texto.length()) {
            letrasVisiveis = texto.length();      // mostra a frase inteira de uma vez
            tempoNaEtapa = 0;
        } else {
            tempoNaEtapa = tempoDeLeitura(texto); // acaba a pausa: no próximo tique vem a próxima fala
        }
    }

    private void irPara(Etapa proxima) {
        etapa = proxima;
        tempoNaEtapa = 0;
    }

    // Pausa depois que a frase aparece: o tempo de uma pessoa ler
    // (meio segundo + um pouquinho para cada letra)
    private int tempoDeLeitura(String texto) {
        return 500 + texto.length() * 20;
    }

    // Diz para a câmera mirar no rosto de quem está falando
    private void mirarCamera(String nome) {
        alvoZoom = ZOOM_FALA;
        if (nome.equals("Aluna")) {
            alvoX = X_ALUNA + 20;
            alvoY = CHAO - alunaFrente.getHeight() + 95;
        } else {
            alvoX = X_ROGER - 20;
            alvoY = CHAO + 6 - roger.getHeight() + 110;
        }
    }

    // Volta a câmera para a sala inteira
    private void abrirCamera() {
        alvoZoom = 1.0;
        alvoX = LARGURA / 2.0;
        alvoY = ALTURA / 2.0;
    }

    // "Tic" das letrinhas, como nos jogos antigos (uma letra sim, outra não, e sem som no espaço)
    private void tocarLetra(char letra) {
        if (letra != ' ' && letrasVisiveis % 2 == 1) {
            Sons.tocar("/sons/efeitos/fala-letra.wav");
        }
    }

    void atualizar() {

        tempoNaEtapa += TIQUE;

        switch (etapa) {

            case ALUNA_ENTRANDO:
                // a aluna entra pela porta e anda até a frente da lousa
                alunaX += 5;
                spriteAluna = caminhada.desenhoAtual(tempoNaEtapa);
                if (alunaX >= X_ALUNA) {
                    alunaX = X_ALUNA;
                    spriteAluna = alunaFrente;   // vira de frente
                    mirarCamera(falas.get(0).getNome());
                    irPara(Etapa.DIALOGO);
                }
                break;

            case DIALOGO:
                String texto = falas.get(falaAtual).getTexto();
                // uma letra nova a cada tique (30 ms)
                if (letrasVisiveis < texto.length()) {
                    letrasVisiveis++;
                    tocarLetra(texto.charAt(letrasVisiveis - 1));
                    if (letrasVisiveis == texto.length()) {
                        tempoNaEtapa = 0;   // começa a contar a pausa depois do texto
                    }
                } else if (tempoNaEtapa >= tempoDeLeitura(texto)) {
                    // terminou a pausa: próxima fala
                    if (falaAtual < falas.size() - 1) {
                        falaAtual++;
                        letrasVisiveis = 0;
                        tempoNaEtapa = 0;
                        mirarCamera(falas.get(falaAtual).getNome());
                    } else {
                        abrirCamera();
                        irPara(Etapa.FIM);
                    }
                }
                break;

            case FIM:
                // espera a câmera voltar para a sala inteira e chama a decolagem
                // (a decolagem começa com a mesma sala: parece uma cena só)
                if (zoom < 1.01) {
                    relogio.stop();
                    if (janela != null) {
                        janela.mostrarDecolagem();   // próxima cena
                    }
                }
                break;
        }

        // a câmera chega aos poucos no alvo (movimento suave)
        zoom += (alvoZoom - zoom) * 0.15;
        cameraX += (alvoX - cameraX) * 0.15;
        cameraY += (alvoY - cameraY) * 0.15;
    }

    // Desenha a sala inteira (sem zoom) na imagem "cena"
    private void desenharSala() {

        Graphics2D g = cena.createGraphics();
        if (fundo != null) {
            g.drawImage(fundo, 0, 0, null);
        }

        // Professor Roger (treme um pouquinho quando se assusta)
        boolean assustado = etapa == Etapa.DIALOGO && falaAtual == 0;
        if (roger != null) {
            int tremor = (assustado && letrasVisiveis < 12) ? ((tempoNaEtapa / 60) % 2 == 0 ? -3 : 3) : 0;
            g.drawImage(roger, X_ROGER - roger.getWidth() / 2 + tremor, CHAO + 6 - roger.getHeight(), null);

            if (assustado) {
                // balão "!" em cima da cabeça
                int bx = X_ROGER + 36;
                int by = CHAO + 6 - roger.getHeight() - 8;
                g.setColor(new Color(0xC2410C));
                g.fillOval(bx, by + 4, 40, 40);
                g.setColor(new Color(0xF97316));
                g.fillOval(bx, by, 40, 40);
                g.setColor(Color.WHITE);
                g.fillRect(bx + 16, by + 8, 8, 16);
                g.fillRect(bx + 16, by + 28, 8, 6);
            }
        }

        // aluna
        if (spriteAluna != null) {
            g.drawImage(spriteAluna, (int) alunaX - spriteAluna.getWidth() / 2, CHAO - spriteAluna.getHeight(), null);
        }

        // colegas e computadores (na frente de todo mundo)
        if (colegas != null) {
            g.drawImage(colegas, 0, 0, null);
        }
        g.dispose();
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        desenharSala();

        // 1. "câmera": recorta um pedaço da sala e aumenta (zoom)
        double largura = LARGURA / zoom;
        double altura = ALTURA / zoom;
        int x0 = (int) Math.max(0, Math.min(LARGURA - largura, cameraX - largura / 2));
        int y0 = (int) Math.max(0, Math.min(ALTURA - altura, cameraY - altura / 2));
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.drawImage(cena, 0, 0, LARGURA, ALTURA,
                x0, y0, x0 + (int) largura, y0 + (int) altura, null);

        // 2. caixa de fala (desenhada pequena e aumentada: pixel art)
        if (etapa == Etapa.DIALOGO) {
            BufferedImage pequena = new BufferedImage(LARGURA / ESCALA, ALTURA / ESCALA, BufferedImage.TYPE_INT_ARGB);
            Graphics2D gp = pequena.createGraphics();
            gp.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            gp.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            Fala fala = falas.get(falaAtual);
            boolean terminou = letrasVisiveis == fala.getTexto().length();   // frase completa: mostra a setinha ▼
            caixa.desenhar(gp, fala.getNome(), fala.getTexto().substring(0, letrasVisiveis), terminou);
            gp.dispose();
            g2.drawImage(pequena, 0, 0, LARGURA, ALTURA, null);
        }
    }
}
