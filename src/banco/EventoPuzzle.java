
package banco;

import java.sql.Timestamp;

public class EventoPuzzle {

    // Dados do evento
    private int idEvento;
    private int idTentativaPuzzle;
    private String tipoEvento;
    private String valor;
    private Boolean correto;
    private int custoOxigenio;
    private Integer oxigenioDepois;
    private Timestamp criadoEm;

    // Construtor
    public EventoPuzzle(int idEvento, int idTentativaPuzzle,
            String tipoEvento, String valor, Boolean correto,
            int custoOxigenio, Integer oxigenioDepois,
            Timestamp criadoEm) {

        this.idEvento = idEvento;
        this.idTentativaPuzzle = idTentativaPuzzle;
        this.tipoEvento = tipoEvento;
        this.valor = valor;
        this.correto = correto;
        this.custoOxigenio = custoOxigenio;
        this.oxigenioDepois = oxigenioDepois;
        this.criadoEm = criadoEm;
    }

    // Getters
    public int getIdEvento() { return idEvento; }
    public int getIdTentativaPuzzle() { return idTentativaPuzzle; }
    public String getTipoEvento() { return tipoEvento; }
    public String getValor() { return valor; }
    public Boolean getCorreto() { return correto; }
    public int getCustoOxigenio() { return custoOxigenio; }
    public Integer getOxigenioDepois() { return oxigenioDepois; }
    public Timestamp getCriadoEm() { return criadoEm; }
}
