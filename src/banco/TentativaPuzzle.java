package banco;

public class TentativaPuzzle {

    // Dados da tentativa de puzzle
    private int idTentativaPuzzle;
    private int idTentativaFase;
    private int idTipoPuzzle;
    private int idPeca;
    private int ordem;
    private Integer semente;
    private Integer versaoConteudo;
    private String status;
    private String conteudo;

    // Construtor
    public TentativaPuzzle(int idTentativaPuzzle, int idTentativaFase,
            int idTipoPuzzle, int idPeca, int ordem, Integer semente,
            Integer versaoConteudo, String status, String conteudo) {

        this.idTentativaPuzzle = idTentativaPuzzle;
        this.idTentativaFase = idTentativaFase;
        this.idTipoPuzzle = idTipoPuzzle;
        this.idPeca = idPeca;
        this.ordem = ordem;
        this.semente = semente;
        this.versaoConteudo = versaoConteudo;
        this.status = status;
        this.conteudo = conteudo;
    }

    // Getters
    public int getIdTentativaPuzzle() { return idTentativaPuzzle; }
    public int getIdTentativaFase() { return idTentativaFase; }
    public int getIdTipoPuzzle() { return idTipoPuzzle; }
    public int getIdPeca() { return idPeca; }
    public int getOrdem() { return ordem; }
    public Integer getSemente() { return semente; }
    public Integer getVersaoConteudo() { return versaoConteudo; }
    public String getStatus() { return status; }
    public String getConteudo() { return conteudo; }
}