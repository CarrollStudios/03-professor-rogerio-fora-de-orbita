package jogo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Cutscene da sala de aula (LAB 3), sem ação do jogador.
 * A aluna entra, o Professor Roger se assusta com a porta aberta
 * e manda a aluna para o espaço. Em cada fala, a câmera dá zoom
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
    private enum Etapa { ALUNA_ENTRANDO, DIALOGO, FLASH, ESCURECENDO }

    private Etapa etapa = Etapa.ALUNA_ENTRANDO;   // começa com a aluna já entrando (sem tela preta)
    private int tempoNaEtapa = 0;

    // ===== Imagens =====
    private BufferedImage fundo, colegas, roger, alunaFrente;
    private BufferedImage[] passos = new BufferedImage[6];
    private BufferedImage cena = new BufferedImage(LARGURA, ALTURA, BufferedImage.TYPE_INT_ARGB);

    // ===== Estado =====
    private double alunaX = X_PORTA;
    private BufferedImage spriteAluna;
    private float escuro = 0f;     // 0 = normal, 1 = tela preta
    private float clarao = 0f;     // o "flash" quando o professor aperta Enter

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
        for (int i = 0; i < passos.length; i++) {
            passos[i] = Imagens.carregar("/images/personagens/aluna-lado-andando" + (i + 1) + ".png");
        }
        spriteAluna = passos[0];

        falas.add(new Fala("Professor Roger", "Quem deixou a porta aberta... de novo?"));
        falas.add(new Fala("Aluna", "Foi o vento, professor."));
        falas.add(new Fala("Professor Roger", "O vento? Então vai explicar isso lá no espaço."));
        falas.add(new Fala("Aluna", "Pera, como assim?!"));
        falas.add(new Fala("Professor Roger", "Boa viagem!"));

        relogio = new Timer(TIQUE, e -> {
            atualizar();
            repaint();
        });
        relogio.start();
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

    void atualizar() {

        tempoNaEtapa += TIQUE;

        switch (etapa) {

            case ALUNA_ENTRANDO:
                // a aluna entra pela porta e anda até a frente da lousa
                alunaX += 5;
                spriteAluna = passos[(tempoNaEtapa / 110) % passos.length];
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
                        irPara(Etapa.FLASH);
                    }
                }
                break;

            case FLASH:
                // clarão rápido: o professor apertou Enter no notebook
                clarao = tempoNaEtapa < 150 ? tempoNaEtapa / 150f : Math.max(0f, 1f - (tempoNaEtapa - 150) / 450f);
                if (tempoNaEtapa >= 600) {
                    clarao = 0f;
                    irPara(Etapa.ESCURECENDO);
                }
                break;

            case ESCURECENDO:
                escuro = Math.min(1f, tempoNaEtapa / 800f);
                if (escuro >= 1f) {
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
            caixa.desenhar(gp, fala.getNome(), fala.getTexto().substring(0, letrasVisiveis));
            gp.dispose();
            g2.drawImage(pequena, 0, 0, LARGURA, ALTURA, null);
        }

        // 3. clarão e escurecer
        if (clarao > 0) {
            g2.setColor(new Color(1f, 1f, 1f, clarao));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        if (escuro > 0) {
            g2.setColor(new Color(0f, 0f, 0f, escuro));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
