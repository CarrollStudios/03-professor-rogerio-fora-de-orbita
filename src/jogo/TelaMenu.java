package jogo;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Tela do menu inicial do jogo.
 * Mostra o fundo do espaço, o título e os botões Jogar e Sair.
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

    private BufferedImage fundo;    // o espaço com os planetas
    private BufferedImage titulo;   // "Professor Rogério - Fora de Órbita"
    private TelaJogo janela;        // a janela do jogo, para trocar de tela

    public TelaMenu(TelaJogo janela) {

        this.janela = janela;

        setPreferredSize(new Dimension(LARGURA, ALTURA));
        setLayout(null); // null = eu escolho a posição de cada botão

        fundo = carregarImagem("fundo-menu-hd.png");
        titulo = carregarImagem("titulo-menu.png");

        // Botão JOGAR: chama a próxima tela (o corredor da Yasmim)
        JButton botaoJogar = criarBotao("botao-jogar.png", "botao-jogar-hover.png");
        botaoJogar.setLocation(X_BOTAO, Y_JOGAR);
        botaoJogar.addActionListener(e -> janela.iniciarJogo());
        add(botaoJogar);

        // Botão SAIR: fecha o jogo
        JButton botaoSair = criarBotao("botao-sair.png", "botao-sair-hover.png");
        botaoSair.setLocation(X_BOTAO, Y_SAIR);
        botaoSair.addActionListener(e -> System.exit(0));
        add(botaoSair);
    }

    // Lê uma imagem da pasta resources/images/menu
    private BufferedImage carregarImagem(String nomeArquivo) {

        try {
            BufferedImage imagem = ImageIO.read(
                getClass().getResource(PASTA + nomeArquivo)
            );
            System.out.println("Imagem carregada: " + nomeArquivo);
            return imagem;

        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Erro ao carregar: " + nomeArquivo);
            e.printStackTrace();
            return null;
        }
    }

    // Desenha a tela: primeiro o fundo, depois o título por cima
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (fundo != null) {
            g.drawImage(fundo, 0, 0, getWidth(), getHeight(), this);
        }
        if (titulo != null) {
            g.drawImage(titulo, 0, 0, getWidth(), getHeight(), this);
        }
    }

    // Cria um botão usando duas imagens:
    // uma normal e outra para quando o mouse passa por cima (hover)
    private JButton criarBotao(String imagemNormal, String imagemHover) {

        BufferedImage normal = carregarImagem(imagemNormal);
        BufferedImage hover = carregarImagem(imagemHover);

        JButton botao = new JButton();
        if (normal != null) {
            botao.setIcon(new ImageIcon(normal));
            botao.setSize(normal.getWidth(), normal.getHeight());
        }
        if (hover != null) {
            botao.setRolloverIcon(new ImageIcon(hover));
        }

        botao.setContentAreaFilled(false); // não usa o fundo padrão do Java
        botao.setBorderPainted(false);     // não usa a borda padrão do Java
        botao.setFocusPainted(false);      // tira o quadradinho de foco
        botao.setBorder(null);             // sem espaço extra em volta
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR)); // mãozinha no mouse

        return botao;
    }
}
