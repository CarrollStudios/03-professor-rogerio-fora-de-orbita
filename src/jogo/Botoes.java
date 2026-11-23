package jogo;

import java.awt.Cursor;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * Ajudante para criar botões em pixel art.
 * Cada botão usa duas imagens: uma normal e outra para quando
 * o mouse passa por cima (hover). Assim as cenas não repetem esse código.
 * Feita por Gih (Front-end) - Carroll Studios.
 */
public class Botoes {

    // Exemplo: Botoes.criar("/images/botoes/botao-pular.png", "/images/botoes/botao-pular-hover.png");
    public static JButton criar(String imagemNormal, String imagemHover) {

        BufferedImage normal = Imagens.carregar(imagemNormal);
        BufferedImage hover = Imagens.carregar(imagemHover);

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
