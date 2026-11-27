package jogo;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import banco.Conexao;
import banco.EventoPuzzle;
import banco.Partida;
import banco.TentativaFase;
import banco.TentativaPuzzle;
import banco.dao.ConsultaProgressoDAO;
import banco.dao.EventoPuzzleDAO;
import banco.dao.PartidaDAO;
import banco.dao.TentativaFaseDAO;
import banco.dao.TentativaPuzzleDAO;

/**
 * Ponte entre o Progresso do jogo e o MySQL (usa os DAOs do pacote banco).
 *
 * Correspondência jogo -> banco (ids começam em 1, na ordem dos scripts de dados):
 *   fase 0..2   -> fases.id_fase 1..3
 *   peça 0..5   -> pecas_nave.id_peca 1..6
 *   puzzle 0..5 -> tipos_puzzle.id_tipo_puzzle 1..6 (Quiz 1, Quiz 2, Luzes, Ordem, Rodas, Labirinto)
 *
 * Valores de texto usados:
 *   partida:          em_andamento | concluida | abandonada
 *   tentativa_fase:   em_andamento | game_over | concluida
 *   tentativa_puzzle: em_andamento | concluido
 *   evento_puzzle.tipo_evento: erro (custo 1) | resposta_quiz | dica | migracao
 *   tentativa_puzzle.conteudo (JSON): {"acertadas":N,"dicasUsadas":N}
 *
 * O oxigênio e as dicas NÃO têm coluna: são calculados pelos eventos (oxigenio_inicial - soma dos custos).
 * Os métodos daqui são síncronos e lançam exceção se algo falhar; quem decide o que fazer é o Progresso.
 */
public class RepositorioMySQL {

    // Enquanto não houver login, o jogo usa sempre o usuário 1 (combinado com a equipe do banco)
    public static final int ID_USUARIO_PADRAO = 1;

    private static final Pattern ACERTADAS = Pattern.compile("\"acertadas\"\\s*:\\s*(\\d+)");
    private static final Pattern DICAS = Pattern.compile("\"dicasUsadas\"\\s*:\\s*(\\d+)");

    /** Estado recuperado do banco (o mesmo que o Progresso guarda na memória). */
    public static class Estado {
        public int pecas;
        public int oxigenio = Progresso.OXIGENIO_MAXIMO;
        public int dicas = Progresso.DICAS_MAXIMAS;
        public final int[][] sorteio = new int[3][2];
        public final long[] semente = new long[6];
        public final int[] tentativa = { 1, 1, 1 };
        public final int[] quiz = new int[6];
        public final int[] dicasUsadas = new int[6];
        /** true se a fase já tem tentativa em andamento no banco com puzzles gravados */
        public final boolean[] faseNoBanco = new boolean[3];
        /** true se a última tentativa da fase atual terminou em game over (a fase deve recomeçar) */
        public boolean gameOver;
    }

    private final PartidaDAO partidaDAO;
    private final TentativaFaseDAO faseDAO;
    private final TentativaPuzzleDAO puzzleDAO;
    private final EventoPuzzleDAO eventoDAO;
    private final ConsultaProgressoDAO consultaDAO;
    private final int idUsuario;

    private int idPartida = 0;
    private final int[] idTentativaFase = new int[3];   // tentativa em andamento de cada fase (0 = nenhuma)
    private final int[] idPuzzle = new int[6];          // tentativa_puzzle de cada peça na tentativa atual

    public RepositorioMySQL(PartidaDAO p, TentativaFaseDAO f, TentativaPuzzleDAO t, EventoPuzzleDAO e,
            ConsultaProgressoDAO c, int idUsuario) {
        this.partidaDAO = p;
        this.faseDAO = f;
        this.puzzleDAO = t;
        this.eventoDAO = e;
        this.consultaDAO = c;
        this.idUsuario = idUsuario;
    }

    /** Cria o repositório de verdade. Devolve null se não for possível conectar (jogo usa só o arquivo). */
    public static RepositorioMySQL criarPadrao() {
        try {
            java.sql.DriverManager.setLoginTimeout(3);
            java.sql.Connection teste = Conexao.conectar();
            if (teste == null) {
                return null;
            }
            teste.close();
            return new RepositorioMySQL(new PartidaDAO(), new TentativaFaseDAO(), new TentativaPuzzleDAO(),
                    new EventoPuzzleDAO(), new ConsultaProgressoDAO(), ID_USUARIO_PADRAO);
        } catch (Throwable e) {
            System.out.println("Banco indisponível, usando só o arquivo local: " + e);
            return null;
        }
    }

    // ================================================================ leitura

    /** Lê a partida ativa do usuário. Devolve null se ele ainda não tem partida no banco. */
    public Estado carregar() throws Exception {
        Partida partida = partidaDAO.buscarPartidaAtivaPorUsuario(idUsuario);
        if (partida == null) {
            return null;
        }
        idPartida = partida.getIdPartida();
        java.util.Arrays.fill(idTentativaFase, 0);
        java.util.Arrays.fill(idPuzzle, 0);

        Estado e = new Estado();
        TentativaFase[] ultima = new TentativaFase[3];
        for (TentativaFase t : consultaDAO.listarTentativasPorPartida(idPartida)) {
            int f = t.getIdFase() - 1;
            if (f >= 0 && f < 3 && (ultima[f] == null || t.getNumeroTentativa() >= ultima[f].getNumeroTentativa())) {
                ultima[f] = t;
            }
        }
        for (int f = 0; f < 3; f++) {
            if (ultima[f] != null) {
                e.tentativa[f] = ultima[f].getNumeroTentativa();
            }
        }

        // a fase atual é a primeira que ainda não foi concluída
        int atual = 0;
        while (atual < 3 && ultima[atual] != null && "concluida".equals(ultima[atual].getStatus())) {
            atual++;
        }
        if (atual == 3) {
            e.pecas = 6;
            return e;
        }
        e.pecas = atual * 2;
        TentativaFase t = ultima[atual];
        if (t == null) {
            return e;                       // fase ainda não começou no banco
        }
        if ("game_over".equals(t.getStatus())) {
            e.gameOver = true;
            e.oxigenio = 0;
            return e;
        }

        // tentativa em andamento: recupera puzzles, quiz, dicas e oxigênio
        idTentativaFase[atual] = t.getIdTentativaFase();
        List<TentativaPuzzle> puzzles = puzzleDAO.listarPuzzlesPorTentativaFase(t.getIdTentativaFase());
        if (puzzles.size() < 2) {
            return e;                       // tentativa incompleta: o Progresso sorteia de novo
        }
        int custo = 0;
        int concluidos = 0;
        int dicasGastas = 0;
        for (TentativaPuzzle p : puzzles) {
            int j = p.getOrdem() - 1;
            int peca = atual * 2 + j;
            if (j < 0 || j > 1 || p.getSemente() == null) {
                return e;
            }
            e.sorteio[atual][j] = p.getIdTipoPuzzle() - 1;
            e.semente[peca] = p.getSemente();
            e.quiz[peca] = numero(ACERTADAS, p.getConteudo());
            e.dicasUsadas[peca] = numero(DICAS, p.getConteudo());
            dicasGastas += e.dicasUsadas[peca];
            idPuzzle[peca] = p.getIdTentativaPuzzle();
            if ("concluido".equals(p.getStatus())) {
                concluidos++;
            }
            for (EventoPuzzle ev : eventoDAO.listarEventosPorTentativaPuzzle(p.getIdTentativaPuzzle())) {
                custo += ev.getCustoOxigenio();
            }
        }
        e.faseNoBanco[atual] = true;
        e.pecas = atual * 2 + Math.min(2, concluidos);
        e.oxigenio = Math.max(0, t.getOxigenioInicial() - custo);
        e.dicas = Math.max(0, Progresso.DICAS_MAXIMAS - dicasGastas);
        return e;
    }

    private static int numero(Pattern p, String json) {
        if (json == null) {
            return 0;
        }
        Matcher m = p.matcher(json);
        return m.find() ? Integer.parseInt(m.group(1)) : 0;
    }

    // ================================================================ escrita

    /** Começa uma partida nova. A anterior (se houver) fica no histórico com o status informado. */
    public void novaPartida(String statusDaAnterior) throws Exception {
        if (idPartida > 0 && statusDaAnterior != null) {
            partidaDAO.atualizarStatus(idPartida, statusDaAnterior);
        }
        int id = partidaDAO.criarPartida(idUsuario);
        if (id <= 0) {
            throw new IllegalStateException("Não foi possível criar a partida.");
        }
        idPartida = id;
        java.util.Arrays.fill(idTentativaFase, 0);
        java.util.Arrays.fill(idPuzzle, 0);
    }

    /** Abre uma tentativa da fase 'fase' (0 a 2) com os 2 puzzles sorteados e suas sementes. */
    public void iniciarTentativa(int fase, int numero, int[] sorteioDaFase, long[] sementeDaFase) throws Exception {
        garantirPartida();
        int idT = faseDAO.criarTentativa(idPartida, fase + 1, numero);
        if (idT <= 0) {
            throw new IllegalStateException("Não foi possível criar a tentativa da fase " + (fase + 1));
        }
        idTentativaFase[fase] = idT;
        for (int j = 0; j < 2; j++) {
            int peca = fase * 2 + j;
            int idP = puzzleDAO.criarTentativa(idT, sorteioDaFase[j] + 1, peca + 1, j + 1, sementeDaFase[j],
                    Perguntas.VERSAO, json(0, 0));
            if (idP <= 0) {
                throw new IllegalStateException("Não foi possível criar o puzzle da peça " + (peca + 1));
            }
            idPuzzle[peca] = idP;
        }
    }

    public void evento(int peca, String tipo, String valor, Boolean correto, int custo, int oxigenioDepois)
            throws Exception {
        if (peca < 0 || peca > 5 || idPuzzle[peca] == 0) {
            return;
        }
        if (eventoDAO.criarEvento(idPuzzle[peca], tipo, valor, correto, custo, oxigenioDepois) <= 0) {
            throw new IllegalStateException("Não foi possível registrar o evento.");
        }
    }

    public void conteudo(int peca, int acertadas, int dicasUsadas) throws Exception {
        if (peca >= 0 && peca <= 5 && idPuzzle[peca] != 0) {
            if (!puzzleDAO.atualizarConteudo(idPuzzle[peca], json(acertadas, dicasUsadas))) {
                throw new IllegalStateException("Não foi possível atualizar o conteúdo do puzzle.");
            }
        }
    }

    public void concluirPuzzle(int peca) throws Exception {
        if (peca >= 0 && peca <= 5 && idPuzzle[peca] != 0) {
            if (!puzzleDAO.atualizarStatus(idPuzzle[peca], "concluido")) {
                throw new IllegalStateException("Não foi possível concluir o puzzle.");
            }
        }
    }

    /** status: "concluida" ou "game_over". Se a tentativa já foi finalizada, não faz nada. */
    public void finalizarTentativa(int fase, String status) throws Exception {
        if (fase >= 0 && fase < 3 && idTentativaFase[fase] != 0) {
            faseDAO.finalizarTentativa(idTentativaFase[fase], status);
            idTentativaFase[fase] = 0;
        }
    }

    public boolean temTentativaAberta(int fase) {
        return idTentativaFase[fase] != 0;
    }

    /**
     * Primeira vez com o banco mas já existe progresso no arquivo local: copia o que o jogador já fez,
     * para ele não perder nada. As fases já vencidas viram tentativas concluídas.
     */
    public void importar(Estado s) throws Exception {
        novaPartida(null);
        int faseAtual = Math.min(3, s.pecas / 2);
        for (int f = 0; f <= Math.min(2, faseAtual); f++) {
            iniciarTentativa(f, s.tentativa[f], s.sorteio[f], new long[] { s.semente[f * 2], s.semente[f * 2 + 1] });
            for (int j = 0; j < 2; j++) {
                int peca = f * 2 + j;
                conteudo(peca, s.quiz[peca], s.dicasUsadas[peca]);
                if (peca < s.pecas) {
                    concluirPuzzle(peca);
                }
            }
            if (f < faseAtual) {
                finalizarTentativa(f, "concluida");
            } else if (s.oxigenio < Progresso.OXIGENIO_MAXIMO) {
                int perdido = Progresso.OXIGENIO_MAXIMO - s.oxigenio;
                evento(f * 2 + Math.min(1, s.pecas - f * 2), "migracao", "importado do arquivo local", null,
                        perdido, s.oxigenio);
            }
        }
    }

    private void garantirPartida() throws Exception {
        if (idPartida <= 0) {
            novaPartida(null);
        }
    }

    private static String json(int acertadas, int dicasUsadas) {
        return "{\"acertadas\":" + acertadas + ",\"dicasUsadas\":" + dicasUsadas + "}";
    }
}
