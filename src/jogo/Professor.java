package jogo;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Professor {

    private int x;
    private int y;

    private int largura;
    private int altura;
    
    private Professor professor; 

    private BufferedImage imagem;

    public Professor(int x, int y, int largura, int altura) {

        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;

        carregarImagem();
    }

    // Carrega a imagem do professor
    private void carregarImagem() {

        try {

            imagem = ImageIO.read(
                getClass().getResource(
                    "/sprites/professorRogerio/professor.png"
                )
            );

            System.out.println("Professor Rogério carregado!");

        } catch (IOException | IllegalArgumentException e) {

            System.out.println("Erro ao carregar o professor!");
            e.printStackTrace();

        }
    }

    // Desenha o professor na tela
    public void desenhar(Graphics g) {

        if (imagem != null) {

            g.drawImage(
                imagem,
                x,
                y,
                largura,
                altura,
                null
            );

        }
    }

    public void setPosicao(int x, int y) {
        this.x = x;
        this.y = y;
    }

}