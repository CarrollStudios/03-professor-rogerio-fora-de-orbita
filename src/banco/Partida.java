
package banco;

import java.sql.Timestamp;

public class Partida {

    // Dados da partida
    private int idPartida;
    private int idUsuario;
    private String status;
    private Timestamp criadaEm;
    private Timestamp atualizadaEm;

    // Construtor
    public Partida(int idPartida, int idUsuario, String status,
            Timestamp criadaEm, Timestamp atualizadaEm) {

        this.idPartida = idPartida;
        this.idUsuario = idUsuario;
        this.status = status;
        this.criadaEm = criadaEm;
        this.atualizadaEm = atualizadaEm;
    }

    // Retorna os dados da partida
    public int getIdPartida() {
        return idPartida;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getStatus() {
        return status;
    }

    public Timestamp getCriadaEm() {
        return criadaEm;
    }

    public Timestamp getAtualizadaEm() {
        return atualizadaEm;
    }
}
