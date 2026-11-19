package jogo;

public class GerenciadorCenas {

    // Identifica as partes da história
    public enum Cena {
        CORREDOR,
        SALA,
        ESPACO,
        FASES,
        FINAL
    }

    // A primeira cena do jogo será o corredor
    private Cena cenaAtual = Cena.CORREDOR;

    // Retorna a cena atual
    public Cena getCenaAtual() {
        return cenaAtual;
    }

    // Muda para outra cena
    public void mudarCena(Cena novaCena) {
        cenaAtual = novaCena;

        System.out.println("Cena atual: " + cenaAtual);
    }

}