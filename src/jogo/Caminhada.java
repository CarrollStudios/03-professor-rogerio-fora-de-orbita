package jogo;

import java.awt.image.BufferedImage;

/**
 * A caminhada da aluna de lado: os 6 desenhos de um passo completo
 * e o som dos passos. É usada no corredor e na sala, assim
 * o mesmo código não fica escrito duas vezes.
 * Feita por Gih (Front-end) - Carroll Studios.
 */
public class Caminhada {

    private static final int TEMPO_POR_DESENHO = 110;   // cada desenho fica 110 milissegundos na tela

    private BufferedImage[] desenhos = new BufferedImage[6];
    private int ultimoDesenho = -1;
    private boolean outroPe = false;   // alterna o som: um para cada pé

    public Caminhada() {
        for (int i = 0; i < desenhos.length; i++) {
            desenhos[i] = Imagens.carregar("/images/personagens/aluna-lado-andando" + (i + 1) + ".png");
        }
    }

    // tempo = há quantos milissegundos a aluna está andando
    // Devolve o desenho certo para esse momento e toca o passo quando o pé encosta no chão
    public BufferedImage desenhoAtual(int tempo) {
        int numero = (tempo / TEMPO_POR_DESENHO) % desenhos.length;
        // o pé encosta no chão no 1º e no 4º desenho do ciclo
        if (numero != ultimoDesenho && numero % 3 == 0) {
            Sons.tocar(outroPe ? "/sons/efeitos/passo-2.wav" : "/sons/efeitos/passo-1.wav");
            outroPe = !outroPe;
        }
        ultimoDesenho = numero;
        return desenhos[numero];
    }

    public BufferedImage primeiroDesenho() {
        return desenhos[0];
    }
}
