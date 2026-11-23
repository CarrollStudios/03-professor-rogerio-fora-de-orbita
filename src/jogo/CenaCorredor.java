package jogo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Cutscene do corredor (cena sem ação do jogador).
 * A aluna anda sozinha pelo corredor, a câmera acompanha,
 * a porta do LAB 3 abre e ela entra na sala.
 * Assim que ela entra, o jogo já mostra a cena da sala.
 * Feita por Gih (Front-end) - Carroll Studios.
 */
public class CenaCorredor extends JPanel {

    // ===== Tamanho da tela =====
    private static final int LARGURA = 1280;
    private static final int ALTURA = 720;

    // ===== Porta do LAB 3 (posição dentro da imagem do corredor) =====
    private static final int PORTA_X1 = 1196;
    private static final int PORTA_Y1 = 252;
    private static final int PORTA_X2 = 1352;
    private static final int CENTRO_PORTA = (PORTA_X1 + PORTA_X2) / 2;

    // ===== Movimento =====
    private static final int CHAO = 640;        // altura dos pés da aluna
    private static final int VELOCIDADE = 5;    // pixels a cada "tique" do relógio
    private static final int TIQUE = 30;        // o relógio bate a cada 30 milissegundos

    // ===== As etapas da cena, na ordem =====
    private enum Etapa { ANDANDO, PARADA, PORTA_ABRINDO, VIRANDO, ENTRANDO }

    private Etapa etapa = Etapa.ANDANDO;
    private int tempoNaEtapa = 0;   // quantos milissegundos estamos na etapa atual

    // ===== Imagens =====
    private BufferedImage corredor, portaAberta;
    private BufferedImage parada, costas;
    private Caminhada caminhada = new Caminhada();   // os 6 desenhos de um passo e o som dos passos

    // ===== Estado da aluna e da câmera =====
    private double alunaX = 200;    // posição da aluna no corredor (centro dela)
    private double pesY = CHAO;
    private double escala = 1.0;    // 1.0 = tamanho normal; menor = mais longe
    private BufferedImage spriteAtual;
    private int cameraX = 0;        // quanto o corredor está "deslizado" para a esquerda
    private boolean mostrarPortaAberta = false;

    private TelaJogo janela;
    private Timer relogio;

    public CenaCorredor(TelaJogo janela) {

        this.janela = janela;
        setPreferredSize(new Dimension(LARGURA, ALTURA));
        setBackground(Color.BLACK);

        corredor = Imagens.carregar("/images/corredor/corredor-fundo.png");
        portaAberta = Imagens.carregar("/images/corredor/corredor-porta-aberta.png");
        parada = Imagens.carregar("/images/personagens/aluna-lado-parada.png");
        costas = Imagens.carregar("/images/personagens/aluna-costas.png");
        spriteAtual = parada;

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

        // O Timer é o "relógio" da cena: a cada tique, atualiza e redesenha
        relogio = new Timer(TIQUE, e -> {
            atualizar();
            repaint();
        });
        relogio.start();
    }

    // Muda para a próxima etapa e zera o tempo
    private void irPara(Etapa proxima) {
        etapa = proxima;
        tempoNaEtapa = 0;
    }

    // Chamado a cada tique: decide o que acontece em cada etapa
    void atualizar() {

        tempoNaEtapa += TIQUE;

        switch (etapa) {

            case ANDANDO:
                alunaX += VELOCIDADE;
                // troca o desenho do passo a cada 110 milissegundos (6 desenhos = 1 passo completo)
                spriteAtual = caminhada.desenhoAtual(tempoNaEtapa);
                if (alunaX >= CENTRO_PORTA) {
                    alunaX = CENTRO_PORTA;
                    spriteAtual = parada;
                    irPara(Etapa.PARADA);
                }
                break;

            case PARADA:
                if (tempoNaEtapa >= 200) {
                    mostrarPortaAberta = true;   // a porta abre
                    Sons.tocar("/sons/efeitos/porta.wav");
                    irPara(Etapa.PORTA_ABRINDO);
                }
                break;

            case PORTA_ABRINDO:
                if (tempoNaEtapa >= 350) {
                    spriteAtual = costas;        // ela se vira para a porta
                    irPara(Etapa.VIRANDO);
                }
                break;

            case VIRANDO:
                if (tempoNaEtapa >= 300) {
                    irPara(Etapa.ENTRANDO);
                }
                break;

            case ENTRANDO:
                // vai para dentro da sala: fica um pouco menor e mais para cima
                double p = Math.min(1.0, tempoNaEtapa / 800.0);
                escala = 1.0 - 0.2 * p;
                pesY = CHAO - 40 * p;
                if (p >= 1.0) {
                    // assim que ela entra, a cena já troca para dentro da sala (sem tela preta)
                    relogio.stop();
                    if (janela != null) {
                        janela.mostrarSala();
                    }
                }
                break;

        }

        // a câmera segue a aluna, sem passar das pontas do corredor
        if (corredor != null && etapa == Etapa.ANDANDO) {
            int maximo = corredor.getWidth() - LARGURA;
            cameraX = (int) Math.max(0, Math.min(maximo, alunaX - LARGURA / 2));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // 1. o corredor (deslizado pela câmera)
        if (corredor != null) {
            g2.drawImage(corredor, -cameraX, 0, null);
        }

        // 2. a porta aberta, por cima da porta fechada
        if (mostrarPortaAberta && portaAberta != null) {
            g2.drawImage(portaAberta, -cameraX, 0, null);
        }

        // 3. a aluna (some atrás da parede quando entra na sala)
        if (spriteAtual != null) {

            int largura = (int) (spriteAtual.getWidth() * escala);
            int altura = (int) (spriteAtual.getHeight() * escala);
            int x = (int) alunaX - largura / 2 - cameraX;
            int y = (int) pesY - altura;

            Shape recorteAntigo = g2.getClip();
            boolean dentroDaPorta = etapa == Etapa.ENTRANDO;
            if (dentroDaPorta) {
                // só aparece o que está dentro do vão da porta
                g2.clipRect(PORTA_X1 - cameraX, PORTA_Y1, PORTA_X2 - PORTA_X1, ALTURA - PORTA_Y1);
            }
            g2.drawImage(spriteAtual, x, y, largura, altura, null);
            g2.setClip(recorteAntigo);
        }

    }
}
