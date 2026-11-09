package jogo;

public class Personagem {

    private int x;
    private int y;

    private final int largura = 40;
    private final int altura = 60;
    private final int velocidade = 5;

    private boolean olhandoDireita = true;
    private int passo = 0;

    public Personagem(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getLargura() {
        return largura;
    }

    public int getAltura() {
        return altura;
    }

    public boolean isOlhandoDireita() {
        return olhandoDireita;
    }

    public int getPasso() {
        return passo;
    }

    public void moverDireita() {
        x = Math.min(x + velocidade, 800 - largura);
        olhandoDireita = true;
        alternarPasso();
    }

    public void moverEsquerda() {
        x = Math.max(x - velocidade, 0);
        olhandoDireita = false;
        alternarPasso();
    }

    private void alternarPasso() {
        passo = 1 - passo;
    }
}