package jogo;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Tela do menu inicial do jogo.
 * Mostra o fundo do espaço, o título e os botões Jogar e Sair.
 * Os planetas, os asteroides e a nave flutuam, e o meteoro passa de vez em quando.
 * Todas as imagens estão em resources/images/menu, em pixel art.
 * Feita por Gih (Front-end) - Carroll Studios.
 */
public class TelaMenu extends JPanel {

    // ===== Tamanho da tela (combinado com a equipe) =====
    private static final int LARGURA = 1280;
    private static final int ALTURA = 720;

    // ===== Pasta onde ficam as imagens do menu =====
    private static final String PASTA = "/images/menu/";

    // ===== Posição dos botões =====
    private static final int X_BOTAO = 488;
    private static final int Y_JOGAR = 368;
    private static final int Y_SAIR = 456;

    // ===== Animação =====
    private static final int TIQUE = 40;              // o relógio bate a cada 40 ms
    private static final int CICLO_METEORO = 7000;    // o meteoro passa a cada 7 segundos
    private static final int DURACAO_METEORO = 1400;  // e leva 1,4 segundo para atravessar

    private BufferedImage espaco;     // fundo com as estrelas (parado)
    private BufferedImage meteoro;
    private BufferedImage superficie; // chão do planeta rosa (parado, na frente)
    private List<Camada> camadas = new ArrayList<>();   // planetas, asteroides e nave
    private BufferedImage titulo;     // "Professor Rogério - Fora de Órbita"
    private int tempo = 0;            // quanto tempo (ms) o menu está aberto
    private Timer relogio;
    private TelaJogo janela;        // a janela do jogo, para trocar de tela

    public TelaMenu(TelaJogo janela) {

        this.janela = janela;

        setPreferredSize(new Dimension(LARGURA, ALTURA));
        setLayout(null); // null = eu escolho a posição de cada botão

        espaco = carregarImagem("menu-fundo-espaco.png");
        meteoro = carregarImagem("menu-meteoro.png");
        superficie = carregarImagem("menu-planeta-rosa-superficie.png");
        titulo = carregarImagem("titulo-menu.png");

        // cada camada: imagem, quanto sobe e desce (px), tempo de uma volta (ms), fase
        camadas.add(new Camada(carregarImagem("menu-planeta-laranja.png"), 8, 6000, 0.0));
        camadas.add(new Camada(carregarImagem("menu-planeta-azul.png"), 8, 4000, 1.0));
        camadas.add(new Camada(carregarImagem("menu-planeta-verde.png"), 4, 3000, 2.0));
        camadas.add(new Camada(carregarImagem("menu-planeta-lilas.png"), 8, 5000, 3.0));
        camadas.add(new Camada(carregarImagem("menu-asteroide-1.png"), 8, 3500, 0.5));
        camadas.add(new Camada(carregarImagem("menu-asteroide-2.png"), 8, 4200, 1.5));
        camadas.add(new Camada(carregarImagem("menu-asteroide-3.png"), 4, 2800, 2.5));
        camadas.add(new Camada(carregarImagem("menu-planeta-anel.png"), 8, 5500, 4.0));
        camadas.add(new Camada(carregarImagem("menu-nave.png"), 12, 1600, 0.0));

        // Botão JOGAR: chama a próxima tela (o corredor)
        JButton botaoJogar = Botoes.criar(PASTA + "botao-jogar.png", PASTA + "botao-jogar-hover.png");
        botaoJogar.setLocation(X_BOTAO, Y_JOGAR);
        botaoJogar.addActionListener(e -> {
            Sons.tocar("/sons/efeitos/clique.wav");
            relogio.stop();          // para a animação do menu
            janela.iniciarJogo();
        });
        add(botaoJogar);

        // Botão SAIR: fecha o jogo
        JButton botaoSair = Botoes.criar(PASTA + "botao-sair.png", PASTA + "botao-sair-hover.png");
        botaoSair.setLocation(X_BOTAO, Y_SAIR);
        botaoSair.addActionListener(e -> {
            Sons.tocar("/sons/efeitos/clique.wav");
            // espera o clique tocar (0,3 segundo) e fecha o jogo
            Timer fechar = new Timer(300, f -> System.exit(0));
            fechar.setRepeats(false);
            fechar.start();
        });
        add(botaoSair);

        // relógio da animação: a cada tique, avança o tempo e redesenha
        relogio = new Timer(TIQUE, e -> {
            tempo += TIQUE;
            repaint();
        });
        relogio.start();

        // música da escola: começa no menu e continua no corredor e na sala
        Sons.tocarMusica("/sons/musicas/musica-escola.wav");
    }

    // Lê uma imagem da pasta resources/images/menu (usando a ajudante Imagens)
    private BufferedImage carregarImagem(String nomeArquivo) {
        return Imagens.carregar(PASTA + nomeArquivo);
    }

    // Desenha a tela: primeiro o fundo, depois o título por cima
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // 1. fundo com as estrelas
        if (espaco != null) {
            g2.drawImage(espaco, 0, 0, null);
        }

        // 2. meteoro: atravessa a tela na diagonal de vez em quando
        int t = tempo % CICLO_METEORO;
        if (meteoro != null && t < DURACAO_METEORO) {
            double p = t / (double) DURACAO_METEORO;   // 0 = começo, 1 = fim
            int d = (int) Math.round((-500 + 1000 * p) / 4.0) * 4;
            g2.drawImage(meteoro, d, d, null);
        }

        // 3. planetas, asteroides e nave flutuando
        for (Camada camada : camadas) {
            camada.desenhar(g2, tempo);
        }

        // 4. chão do planeta rosa
        if (superficie != null) {
            g2.drawImage(superficie, 0, 0, null);
        }

        // 5. título
        if (titulo != null) {
            g.drawImage(titulo, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
