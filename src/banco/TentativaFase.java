
package banco;

import java.sql.Timestamp;

public class TentativaFase {

    // Dados da tentativa de fase
    private int idTentativaFase;
    private int idPartida;
    private int idFase;
    private int numeroTentativa;
    private String status;
    private int oxigenioInicial;
    private Timestamp iniciadaEm;
    private Timestamp finalizadaEm;

    // Construtor
    public TentativaFase(int idTentativaFase, int idPartida, int idFase,
            int numeroTentativa, String status, int oxigenioInicial,
            Timestamp iniciadaEm, Timestamp finalizadaEm) {

        this.idTentativaFase = idTentativaFase;
        this.idPartida = idPartida;
        this.idFase = idFase;
        this.numeroTentativa = numeroTentativa;
        this.status = status;
        this.oxigenioInicial = oxigenioInicial;
        this.iniciadaEm = iniciadaEm;
        this.finalizadaEm = finalizadaEm;
    }

    // Retorna os dados da tentativa
    public int getIdTentativaFase() {
        return idTentativaFase;
    }

    public int getIdPartida() {
        return idPartida;
    }

    public int getIdFase() {
        return idFase;
    }

    public int getNumeroTentativa() {
        return numeroTentativa;
    }

    public String getStatus() {
        return status;
    }

    public int getOxigenioInicial() {
        return oxigenioInicial;
    }

    public Timestamp getIniciadaEm() {
        return iniciadaEm;
    }

    public Timestamp getFinalizadaEm() {
        return finalizadaEm;
    }
}
