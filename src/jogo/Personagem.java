package jogo;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Personagem {

    private int x;
    private int y;

    private int velocidade = 24;
    private int largura = 400;

    private boolean olhandoDireita = true;

    // Sprites para a direita
    private BufferedImage[] spritesDireita = new BufferedImage[3];

    // Sprites para a esquerda
    private BufferedImage[] spritesEsquerda = new BufferedImage[3];

    // Quadro atual da animação
    private int quadroAtual = 0;

    public Personagem(int x, int y) {

        this.x = x;
        this.y = y;

        carregarSprites();
    }

    // Carrega os seis sprites
    private void carregarSprites() {

        try {

            spritesDireita[0] = carregarImagem("personagemParadaDireita.png");
            spritesDireita[1] = carregarImagem("personagemAndandoDireita1.png");
            spritesDireita[2] = carregarImagem("personagemAndandoDireita2.png");

            spritesEsquerda[0] = carregarImagem("personagemParadaEsquerda.png");
            spritesEsquerda[1] = carregarImagem("personagemAndandoEsquerda1.png");
            spritesEsquerda[2] = carregarImagem("personagemAndandoEsquerda2.png");

            System.out.println("Seis sprites carregados com sucesso!");

        } catch (IOException | IllegalArgumentException e) {

            System.out.println("Erro ao carregar os sprites da personagem!");
            e.printStackTrace();
        }
    }

    // Carrega cada imagem individualmente
    private BufferedImage carregarImagem(String nomeArquivo)
            throws IOException {

        String caminho = "/sprites/personagem/" + nomeArquivo;

        System.out.println("Carregando: " + caminho);

        java.net.URL arquivo = getClass().getResource(caminho);

        if (arquivo == null) {
            throw new IOException("Arquivo não encontrado: " + caminho);
        }

        return ImageIO.read(arquivo);
    }

    // Movimenta para a direita
    public void moverDireita(int larguraPainel) {

        int limite = Math.max(0, larguraPainel - largura);

        x = Math.min(x + velocidade, limite);

        olhandoDireita = true;

        alternarAnimacao();
    }

    // Movimenta para a esquerda
    public void moverEsquerda() {

        x = Math.max(x - velocidade, 0);

        olhandoDireita = false;

        alternarAnimacao();
    }

    // Alterna entre os quadros de caminhada
    private void alternarAnimacao() {

        if (quadroAtual == 1) {
            quadroAtual = 2;
        } else {
            quadroAtual = 1;
        }
    }

    // Retorna à posição parada
    public void parar() {
        quadroAtual = 0;
    }

    // Retorna o sprite atual
    public BufferedImage getSpriteAtual() {

        if (olhandoDireita) {
            return spritesDireita[quadroAtual];
        } else {
            return spritesEsquerda[quadroAtual];
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Atualiza a posição vertical
    public void setY(int y) {
        this.y = y;
    }

    // Atualiza a largura conforme o tamanho da janela
    public void setLargura(int largura) {
        this.largura = largura;
    }

    public int getLargura() {
        return largura;
    }
    
    // Atualiza a posição horizontal da personagem
    public void setX(int x){
    	this.x = x;
    }
}