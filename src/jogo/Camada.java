package jogo;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Uma camada do cenário que pode flutuar para cima e para baixo.
 * Exemplo: um planeta do menu que sobe e desce devagarinho.
 * Feita por Gih (Front-end) - Carroll Studios.
 */
public class Camada {

    private BufferedImage imagem;
    private int amplitude;   // quantos pixels ela sobe e desce (0 = parada)
    private int periodo;     // quanto tempo (ms) leva para subir e descer uma vez
    private double fase;     // para cada camada não mexer igual às outras

    public Camada(BufferedImage imagem, int amplitude, int periodo, double fase) {
        this.imagem = imagem;
        this.amplitude = amplitude;
        this.periodo = periodo;
        this.fase = fase;
    }

    // Desenha a camada no lugar certo para aquele momento (tempo em ms)
    public void desenhar(Graphics2D g, int tempo) {

        if (imagem == null) {
            return;
        }
        int deslocamento = 0;
        if (amplitude > 0) {
            double onda = Math.sin(2 * Math.PI * tempo / periodo + fase);
            // arredonda para múltiplos de 4: anda "de quadradinho em quadradinho", como pixel art
            deslocamento = (int) Math.round(onda * amplitude / 4.0) * 4;
        }
        g.drawImage(imagem, 0, deslocamento, null);
    }
}
