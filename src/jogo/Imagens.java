package jogo;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Ajudante para carregar imagens da pasta resources/images.
 * Assim cada tela não precisa repetir o mesmo código.
 * Feita por Gih (Front-end) - Carroll Studios.
 */
public class Imagens {

    // Exemplo de caminho: "/images/sala/sala-fundo.png"
    public static BufferedImage carregar(String caminho) {

        try {
            BufferedImage imagem = ImageIO.read(Imagens.class.getResource(caminho));
            System.out.println("Imagem carregada: " + caminho);
            return imagem;

        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Erro ao carregar: " + caminho);
            e.printStackTrace();
            return null;
        }
    }
}
