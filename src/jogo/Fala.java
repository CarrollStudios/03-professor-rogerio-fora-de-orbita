package jogo;

/**
 * Uma fala do diálogo: quem fala e o que fala.
 * Exemplo: new Fala("Roger", "Quem deixou a porta aberta... de novo?")
 * Feita por Gih (Front-end) - Carroll Studios.
 */
public class Fala {

    private String nome;   // quem está falando
    private String texto;  // o que a pessoa fala

    public Fala(String nome, String texto) {
        this.nome = nome;
        this.texto = texto;
    }

    public String getNome() {
        return nome;
    }

    public String getTexto() {
        return texto;
    }
}
