package jogo;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Font;

public class PainelJogo extends JPanel implements KeyListener {

    private Personagem personagem;
    private Professor professor;

    // Imagens dos cenários
    private BufferedImage corredor;
    private BufferedImage corredorAberto;
    private BufferedImage salaInformatica;

    // Controle das cenas
    private GerenciadorCenas gerenciadorCenas;

    // Porta do laboratório 3
    private Porta porta;

    private static final int LARGURA_TELA = 1200;
    private static final int ALTURA_TELA = 600;

    // Controle das teclas
    private boolean direitaPressionada = false;
    private boolean esquerdaPressionada = false;

    // Temporizador da movimentação
    private Timer temporizador;

    // Temporizador da entrada na sala
    private Timer temporizadorEntrada;

    private boolean entrandoNaSala = false;

    public PainelJogo() {

        personagem = new Personagem(100, 0);

        gerenciadorCenas = new GerenciadorCenas();
        
        professor = new Professor(600, 260, 500, 500);

        // Área da terceira porta na imagem original
        porta = new Porta(1240, 245, 220, 430);

        setPreferredSize(
            new java.awt.Dimension(LARGURA_TELA, ALTURA_TELA)
        );

        carregarCenarios();

        addKeyListener(this);

        setFocusable(true);

        // Clique do mouse
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                verificarCliqueNaPorta(e.getX(), e.getY());

            }
        });

        // Movimento da personagem
        temporizador = new Timer(80, e -> atualizarMovimento());

        // Animação de entrada na sala
        temporizadorEntrada = new Timer(40, e -> animarEntrada());

    }

    // Carrega as imagens dos cenários
    private void carregarCenarios() {

        try {

            corredor = ImageIO.read(
                getClass().getResource("/sprites/corredorInicial.jpeg")
            );

            corredorAberto = ImageIO.read(
                getClass().getResource("/sprites/corredorPortaAberta.jpeg")
            );

            salaInformatica = ImageIO.read(
                getClass().getResource("/sprites/sala/salaInformatica.jpeg")
            );

            System.out.println("Todos os cenários foram carregados!");

        } catch (IOException | IllegalArgumentException e) {

            System.out.println("Erro ao carregar os cenários!");
            e.printStackTrace();

        }
    }

    // Atualiza a movimentação
    private void atualizarMovimento() {

        if (entrandoNaSala) {
            return;
        }

        if (direitaPressionada) {

            personagem.moverDireita(getWidth());

        } else if (esquerdaPressionada) {

            personagem.moverEsquerda();

        } else {

            personagem.parar();

        }

        repaint();
    }

    // Verifica se o jogador clicou na terceira porta
    private void verificarCliqueNaPorta(int mouseX, int mouseY) {

        if (gerenciadorCenas.getCenaAtual()
                != GerenciadorCenas.Cena.CORREDOR) {
            return;
        }

        if (entrandoNaSala || porta.estaAberta()) {
            return;
        }

        // Converte o clique para as coordenadas da imagem original
        int imagemX = mouseX * corredor.getWidth() / getWidth();

        int imagemY = mouseY * corredor.getHeight() / getHeight();

        if (porta.recebeuClique(imagemX, imagemY)) {

            // Exige que a personagem esteja próxima da porta
            if (!podeEntrarNaSala()) {

                System.out.println("Chegue mais perto da porta!");

                return;
            }

            abrirPorta();

        }

        requestFocusInWindow();

    }

    // Abre a porta
    private void abrirPorta() {

        porta.abrir();

        direitaPressionada = false;
        esquerdaPressionada = false;

        temporizador.stop();

        personagem.parar();

        // Começa a animação de entrada
        entrandoNaSala = true;

        temporizadorEntrada.start();

        repaint();

    }

    // Faz a personagem entrar na sala
    private void animarEntrada() {

        personagem.setX(personagem.getX() + 30);

        repaint();

        // Quando sair do corredor, muda para a sala
        if (personagem.getX() > getWidth()) {

            temporizadorEntrada.stop();

            entrandoNaSala = false;

            gerenciadorCenas.mudarCena(
                GerenciadorCenas.Cena.SALA
            );

            repaint();

        }

    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        // CENA DO CORREDOR
        if (gerenciadorCenas.getCenaAtual()
                == GerenciadorCenas.Cena.CORREDOR) {

            BufferedImage imagemAtual;

            if (porta.estaAberta()) {
                imagemAtual = corredorAberto;
            } else {
                imagemAtual = corredor;
            }

            if (imagemAtual != null) {

                g.drawImage(
                    imagemAtual,
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    this
                );

            }

            desenharPersonagem(g);
            desenharAvisoEntrada(g);

        }

        // CENA DA SALA DE INFORMÁTICA
        else if (gerenciadorCenas.getCenaAtual()
                == GerenciadorCenas.Cena.SALA) {

            if (salaInformatica != null) {

                g.drawImage(
                    salaInformatica,
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    this
                );

            }

            // Desenha o professor na sala
            professor.desenhar(g);

        }

    }

    // Desenha a personagem
    private void desenharPersonagem(Graphics g) {

        int alturaPainel = getHeight();

        int tamanhoPersonagem;

        if (alturaPainel >= 550) {

            tamanhoPersonagem = 450;

        } else {

            tamanhoPersonagem = (int) (alturaPainel * 0.25);

        }

        personagem.setLargura(tamanhoPersonagem);

        int linhaDoChao = (int) (alturaPainel * 0.92);

        int posicaoY = linhaDoChao - tamanhoPersonagem;

        personagem.setY(Math.max(0, posicaoY));

        BufferedImage sprite = personagem.getSpriteAtual();

        if (sprite != null) {

            g.drawImage(
                sprite,
                personagem.getX(),
                personagem.getY(),
                tamanhoPersonagem,
                tamanhoPersonagem,
                this
            );

        }

    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (entrandoNaSala ||
            gerenciadorCenas.getCenaAtual()
                != GerenciadorCenas.Cena.CORREDOR) {
            return;
        }
        
        if (e.getKeyCode() == KeyEvent.VK_E) {
        	
        	if (podeEntrarNaSala() && ! porta.estaAberta()) {
        		abrirPorta();
        	}
        	
        	return;
        }

        if (e.getKeyCode() == KeyEvent.VK_D) {

            boolean primeiroToque = !direitaPressionada;

            direitaPressionada = true;
            esquerdaPressionada = false;

            if (primeiroToque) {
                atualizarMovimento();
            }

            if (!temporizador.isRunning()) {
                temporizador.start();
            }

        }

        if (e.getKeyCode() == KeyEvent.VK_A) {

            boolean primeiroToque = !esquerdaPressionada;

            esquerdaPressionada = true;
            direitaPressionada = false;

            if (primeiroToque) {
                atualizarMovimento();
            }

            if (!temporizador.isRunning()) {
                temporizador.start();
            }

        }

    }

    @Override
    public void keyReleased(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_D) {
            direitaPressionada = false;
        }

        if (e.getKeyCode() == KeyEvent.VK_A) {
            esquerdaPressionada = false;
        }

        if (!direitaPressionada && !esquerdaPressionada) {

            temporizador.stop();

            personagem.parar();

            repaint();

        }

    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
    
    // Verifica se a personagem está perto da porta
    private boolean podeEntrarNaSala() {

        // Posição inicial da terceira porta na imagem original
        int inicioPorta = (int) (1240.0 * getWidth() / corredor.getWidth());

        // Extremidade direita da personagem
        int fimPersonagem = personagem.getX() + personagem.getLargura();

        // O aviso aparece quando ela estiver a até 80 pixels da porta
        return fimPersonagem >= inicioPorta - 80;

    }

    // Desenha o aviso para entrar na sala
    private void desenharAvisoEntrada(Graphics g) {

        if (podeEntrarNaSala()
                && !porta.estaAberta()
                && !entrandoNaSala) {

            int largura = 260;
            int altura = 65;

            int x = (getWidth() - largura) / 2;
            int y = 40;

            // Fundo do aviso
            g.setColor(new Color(30, 25, 40, 220));

            g.fillRoundRect(x, y, largura, altura, 20, 20);

            // Texto
            g.setColor(Color.WHITE);

            g.setFont(new Font("Arial", Font.BOLD, 19));

            g.drawString("ENTRAR NA SALA", x + 43, y + 27);

            g.setFont(new Font("Arial", Font.PLAIN, 15));

            g.drawString("Pressione E ou clique na porta", x + 25, y + 50);

        }

    }

}