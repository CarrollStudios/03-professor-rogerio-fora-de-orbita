package jogo;

import java.awt.Rectangle;

public class Porta {

    private Rectangle area;

    private boolean aberta = false;

    public Porta(int x, int y, int largura, int altura) {
        area = new Rectangle(x, y, largura, altura);
    }

    // Atualiza a posição da porta conforme o tamanho da janela
    public void atualizarArea(int x, int y, int largura, int altura) {
        area.setBounds(x, y, largura, altura);
    }

    // Verifica se o jogador clicou na porta
    public boolean recebeuClique(int mouseX, int mouseY) {
        return area.contains(mouseX, mouseY);
    }

    public void abrir() {
        aberta = true;
        System.out.println("A porta foi aberta!");
    }

    public boolean estaAberta() {
        return aberta;
    }

    public void fechar() {
        aberta = false;
    }
}