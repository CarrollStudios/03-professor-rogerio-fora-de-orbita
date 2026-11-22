package jogo;

import java.awt.image.BufferedImage;

/**
 * Os 6 sistemas da nave, na ordem em que a aluna os recupera.
 * Cada sistema usa um dos desenhos de peça que já existem (os mesmos que
 * quebram na CenaMeteoro).
 *   Fase 1: ESTRUTURA e PROPULSAO
 *   Fase 2: ENERGIA e TERMICO
 *   Fase 3: ATITUDE e NAVEGACAO
 */
public enum SistemaNave {

    ESTRUTURA("Estrutura", "Casco e chassi", "corpo",
            "Suporte mecânico que protege a nave contra o vácuo, a radiação e os micrometeoroides."),
    PROPULSAO("Propulsão", "Motores, tanques e propulsores", "motor",
            "Gera o empuxo para a nave se mover e mudar de órbita."),
    ENERGIA("Energia elétrica", "Painéis solares e baterias", "asa-esquerda",
            "Alimenta todos os sistemas da nave."),
    TERMICO("Controle térmico", "Radiadores e aquecedores", "asa-direita",
            "Mantém a temperatura da nave dentro dos limites."),
    ATITUDE("Controle de atitude (ADCS)", "Rodas de reação e sensores", "janela",
            "Orienta a nave no espaço, apontando para o lado certo."),
    NAVEGACAO("Navegação (GNC)", "Guiagem, navegação e controle", "bico",
            "Descobre a posição da nave e direciona a sua trajetória.");

    public final String nome;
    public final String subtitulo;
    public final String descricao;
    private final String arquivo;

    private BufferedImage imagem;
    private BufferedImage sombra;

    SistemaNave(String nome, String subtitulo, String arquivo, String descricao) {
        this.nome = nome;
        this.subtitulo = subtitulo;
        this.arquivo = arquivo;
        this.descricao = descricao;
    }

    // Desenho da peça (carrega só na primeira vez)
    public BufferedImage imagem() {
        if (imagem == null) {
            imagem = Imagens.carregar("/images/nave/nave-peca-" + arquivo + ".png");
        }
        return imagem;
    }

    // Versão escura da peça: aparece no lugar da peça que ainda não foi recuperada
    public BufferedImage sombra() {
        if (sombra == null && imagem() != null) {
            BufferedImage original = imagem();
            sombra = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < original.getHeight(); y++) {
                for (int x = 0; x < original.getWidth(); x++) {
                    int alfa = (original.getRGB(x, y) >>> 24) & 0xFF;
                    if (alfa > 0) {
                        sombra.setRGB(x, y, (alfa << 24) | 0x3A405A);
                    }
                }
            }
        }
        return sombra;
    }
}
