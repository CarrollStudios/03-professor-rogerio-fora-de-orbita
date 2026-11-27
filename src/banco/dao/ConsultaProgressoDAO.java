package banco.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import banco.Conexao;
import banco.TentativaFase;

/**
 * Consulta extra usada pelo jogo para recuperar o progresso salvo.
 * (Arquivo NOVO: não altera nenhum DAO existente. Sugestão: a Catitinha revisar e, se quiser,
 * mover este método para o TentativaFaseDAO.)
 * Diferente dos outros DAOs, este NÃO engole o erro: se o banco falhar, a exceção sobe e o jogo
 * continua usando o arquivo local.
 */
public class ConsultaProgressoDAO {

    // Todas as tentativas de fase de uma partida, da fase 1 em diante, da mais antiga para a mais nova
    public List<TentativaFase> listarTentativasPorPartida(int idPartida) throws SQLException {

        String sql = "SELECT * FROM tentativa_fase WHERE id_partida = ? "
                + "ORDER BY id_fase, numero_tentativa";

        Connection conexao = Conexao.conectar();
        if (conexao == null) {
            throw new SQLException("Sem conexao com o banco.");
        }

        try (Connection c = conexao;
             PreparedStatement comando = c.prepareStatement(sql)) {

            comando.setInt(1, idPartida);

            List<TentativaFase> lista = new ArrayList<>();
            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    lista.add(new TentativaFase(
                            resultado.getInt("id_tentativa_fase"),
                            resultado.getInt("id_partida"),
                            resultado.getInt("id_fase"),
                            resultado.getInt("numero_tentativa"),
                            resultado.getString("status"),
                            resultado.getInt("oxigenio_inicial"),
                            resultado.getTimestamp("iniciada_em"),
                            resultado.getTimestamp("finalizada_em")));
                }
            }
            return lista;
        }
    }
}
