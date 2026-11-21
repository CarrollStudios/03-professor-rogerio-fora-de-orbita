package jogo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Cutscene da decolagem, sem ação do jogador.
 * Parte 1 (na sala): a nave entra voando pela porta, pousa no chão ao lado da aluna,
 * puxa a aluna para dentro e decola pelo teto.
 * Parte 2 (no céu): a nave sobe, o céu escurece e vira o espaço
 * (o topo do céu é igual ao fundo da CenaMeteoro, por isso as cenas se emendam).
 * Feita por Gih (Front-end) - Carroll Studios.
 */
public class CenaDecolagem extends JPanel {

    // ===== Tamanho da tela =====
    private static final int LARGURA = 1280;
    private static final int ALTURA = 720;
    private static final int TIQUE = 30;   // o relógio bate a cada 30 milissegundos

    // ===== Posições na sala (as mesmas da CenaSala) =====
    private static final int CHAO = 460;
    private static final int X_ALUNA = 520;
    private static final int X_ROGER = 1010;
    private static final int X_NAVE = 320;      // a nave pousa à esquerda da aluna
    private static final int Y_NAVE_NO_CHAO = CHAO - 140;   // centro da nave quando ela está no chão

    // ===== Etapas da cena =====
    private enum Etapa { NAVE_ENTRANDO, POUSANDO, PUXANDO, DECOLANDO, CEU }

    private Etapa etapa = Etapa.NAVE_ENTRANDO;
    private int tempoNaEtapa = 0;

    // ===== Imagens =====
    private BufferedImage fundoSala, colegas, roger, alunaFrente;
    private BufferedImage nave, naveComAluna, naveLado;
    private BufferedImage[] fogo = new BufferedImage[2];
    private BufferedImage ceu;

    // ===== Estado =====
    private double naveX = -300, naveY = 290;   // centro da nave
    private double velocidade = 0;              // velocidade da subida
    private double puxada = 0;                  // 0 = aluna no lugar dela, 1 = aluna dentro da nave
    private boolean alunaNaNave = false;
    private double cameraCeu;                   // parte do céu que aparece na tela

    private TelaJogo janela;
    private Timer relogio;

    public CenaDecolagem(TelaJogo janela) {

        this.janela = janela;
        setPreferredSize(new Dimension(LARGURA, ALTURA));
        setBackground(Color.BLACK);

        fundoSala = Imagens.carregar("/images/sala/sala-fundo.png");
        colegas = Imagens.carregar("/images/sala/sala-frente.png");
        roger = Imagens.carregar("/images/personagens/roger-frente.png");
        alunaFrente = Imagens.carregar("/images/personagens/aluna-frente.png");

        nave = Imagens.carregar("/images/nave/nave.png");
        naveComAluna = Imagens.carregar("/images/nave/nave-com-aluna.png");
        naveLado = Imagens.carregar("/images/nave/nave-lado.png");
        fogo[0] = Imagens.carregar("/images/nave/nave-fogo-1.png");
        fogo[1] = Imagens.carregar("/images/nave/nave-fogo-2.png");
        ceu = Imagens.carregar("/images/decolagem/ceu-fundo.png");

        // a música do espaço começa quando a nave aparece
        Sons.tocarMusica("/sons/musicas/musica-espaco.wav");
        Sons.tocar("/sons/efeitos/nave-voando.wav");

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

    // Arredonda para múltiplos de 4: assim os desenhos andam "de pixel em pixel" (pixel art)
    private static int px(double valor) {
        return (int) Math.round(valor / 4.0) * 4;
    }

    void atualizar() {

        tempoNaEtapa += TIQUE;

        switch (etapa) {

            case NAVE_ENTRANDO:
                // a nave entra de lado pela porta e voa até o lado da aluna
                naveX += 18;
                naveY = 290 + Math.sin(tempoNaEtapa / 120.0) * 8;
                if (naveX >= X_NAVE) {
                    naveX = X_NAVE;
                    irPara(Etapa.POUSANDO);
                }
                break;

            case POUSANDO:
                // a nave fica de pé e desce até o chão
                naveY += (Y_NAVE_NO_CHAO - naveY) * 0.3;
                if (tempoNaEtapa >= 300) {
                    naveY = Y_NAVE_NO_CHAO;
                    Sons.tocar("/sons/efeitos/raio.wav");
                    irPara(Etapa.PUXANDO);
                }
                break;

            case PUXANDO:
                // a aluna é arrastada de lado para dentro da nave
                puxada = Math.min(1.0, tempoNaEtapa / 800.0);
                if (puxada >= 1.0) {
                    alunaNaNave = true;
                    Sons.tocar("/sons/efeitos/decolagem.wav");
                    irPara(Etapa.DECOLANDO);
                }
                break;

            case DECOLANDO:
                // um tremidinho, e a nave acelera para cima e sai pelo teto
                if (tempoNaEtapa > 250) {
                    velocidade += 2.5;
                    naveY -= velocidade;
                }
                if (naveY < -200) {
                    // troca para o céu: a nave aparece saindo do telhado da escola
                    cameraCeu = ceu.getHeight() - ALTURA;
                    naveX = LARGURA / 2.0;
                    naveY = ALTURA + 160;
                    irPara(Etapa.CEU);
                }
                break;

            case CEU:
                // a nave vai até o meio da tela e o céu desce (parece que ela está subindo)
                naveY += (360 - naveY) * 0.1;
                naveX = LARGURA / 2.0 + Math.sin(tempoNaEtapa / 250.0) * 6;   // balança um pouquinho
                double duracao = 3000;
                double t = Math.min(1.0, tempoNaEtapa / duracao);
                double suave = t * t * (3 - 2 * t);   // começa devagar, acelera e termina devagar
                cameraCeu = (ceu.getHeight() - ALTURA) * (1 - suave);
                if (t >= 1.0) {
                    relogio.stop();
                    if (janela != null) {
                        janela.mostrarMeteoro();   // próxima cena (o fundo é o mesmo do topo do céu)
                    }
                }
                break;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        if (etapa == Etapa.CEU) {
            desenharCeu(g2);
        } else {
            desenharSala(g2);
        }
    }

    // ---------- Parte 1: dentro da sala ----------
    private void desenharSala(Graphics2D g2) {

        // a tela treme quando a nave liga o motor
        int tremor = (etapa == Etapa.DECOLANDO) ? ((tempoNaEtapa / 60) % 2 == 0 ? -4 : 4) : 0;
        g2.translate(tremor, 0);

        g2.drawImage(fundoSala, 0, 0, null);
        g2.drawImage(roger, X_ROGER - roger.getWidth() / 2, CHAO + 6 - roger.getHeight(), null);

        // raio que puxa a aluna (entre a nave e ela)
        if (etapa == Etapa.PUXANDO) {
            int brilho = (tempoNaEtapa / 90) % 2 == 0 ? 60 : 95;
            g2.setColor(new Color(0x8B, 0x5C, 0xF6, brilho));
            g2.fillRect(X_NAVE, 200, X_ALUNA + 60 - X_NAVE, 240);
        }

        // aluna: é arrastada até a nave e fica um pouco menor, até sumir atrás dela
        if (!alunaNaNave) {
            double escala = 1.0 - 0.15 * puxada;
            int largura = px(alunaFrente.getWidth() * escala);
            int altura = px(alunaFrente.getHeight() * escala);
            double x = X_ALUNA + (X_NAVE - X_ALUNA) * puxada * puxada;   // começa devagar e acelera
            g2.drawImage(alunaFrente, px(x - largura / 2.0), CHAO - altura, largura, altura, null);
        }

        desenharNave(g2);
        g2.drawImage(colegas, 0, 0, null);   // os colegas ficam na frente da nave

        g2.translate(-tremor, 0);
    }

    // ---------- A nave (usada nas duas partes) ----------
    private void desenharNave(Graphics2D g2) {

        if (etapa == Etapa.NAVE_ENTRANDO) {
            // de lado, com o bico para a direita
            g2.drawImage(naveLado, px(naveX - naveLado.getWidth() / 2.0), px(naveY - naveLado.getHeight() / 2.0), null);
            return;
        }

        BufferedImage imagem = alunaNaNave ? naveComAluna : nave;
        int x = px(naveX - imagem.getWidth() / 2.0);
        int y = px(naveY - imagem.getHeight() / 2.0);

        // fogo do motor (pisca entre dois desenhos)
        if (etapa == Etapa.DECOLANDO || etapa == Etapa.CEU) {
            BufferedImage chama = fogo[(tempoNaEtapa / 90) % 2];
            g2.drawImage(chama, x, y + imagem.getHeight() - 8, null);
        }
        g2.drawImage(imagem, x, y, null);
    }

    // ---------- Parte 2: o céu virando espaço ----------
    private void desenharCeu(Graphics2D g2) {

        int y0 = px(cameraCeu);
        // recorta o pedaço do céu que aparece na tela
        g2.drawImage(ceu, 0, 0, LARGURA, ALTURA, 0, y0, LARGURA, y0 + ALTURA, null);
        desenharNave(g2);
    }
}
