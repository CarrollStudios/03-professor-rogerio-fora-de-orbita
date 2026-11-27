
package banco.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import banco.Conexao;
import banco.TentativaFase;

public class TentativaFaseDAO {

    // Cadastra uma nova tentativa de fase
    public int criarTentativa(int idPartida, int idFase, int numeroTentativa) {

        String sql = "INSERT INTO tentativa_fase "
                + "(id_partida, id_fase, numero_tentativa, oxigenio_inicial) "
                + "VALUES (?, ?, ?, 4)";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            comando.setInt(1, idPartida);
            comando.setInt(2, idFase);
            comando.setInt(3, numeroTentativa);

            comando.executeUpdate();

            try (ResultSet resultado = comando.getGeneratedKeys()) {
                if (resultado.next()) {
                    return resultado.getInt(1);
                }
            }

        } catch (SQLException erro) {
            System.out.println("Erro ao cadastrar tentativa: "
                    + erro.getMessage());
        }

        return -1;
    }

    // Busca uma tentativa pelo ID
    public TentativaFase buscarTentativaPorId(int idTentativaFase) {

        String sql = "SELECT * FROM tentativa_fase "
                + "WHERE id_tentativa_fase = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, idTentativaFase);

            try (ResultSet resultado = comando.executeQuery()) {

                if (resultado.next()) {
                    return new TentativaFase(
                            resultado.getInt("id_tentativa_fase"),
                            resultado.getInt("id_partida"),
                            resultado.getInt("id_fase"),
                            resultado.getInt("numero_tentativa"),
                            resultado.getString("status"),
                            resultado.getInt("oxigenio_inicial"),
                            resultado.getTimestamp("iniciada_em"),
                            resultado.getTimestamp("finalizada_em")
                    );
                }
            }

        } catch (SQLException erro) {
            System.out.println("Erro ao buscar tentativa: "
                    + erro.getMessage());
        }

        return null;
    }

    // Busca uma tentativa de fase em andamento
    public TentativaFase buscarTentativaEmAndamento(int idPartida, int idFase) {

        String sql = "SELECT * FROM tentativa_fase "
                + "WHERE id_partida = ? AND id_fase = ? "
                + "AND status = 'em_andamento' "
                + "ORDER BY numero_tentativa DESC LIMIT 1";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, idPartida);
            comando.setInt(2, idFase);

            try (ResultSet resultado = comando.executeQuery()) {

                if (resultado.next()) {
                    return new TentativaFase(
                            resultado.getInt("id_tentativa_fase"),
                            resultado.getInt("id_partida"),
                            resultado.getInt("id_fase"),
                            resultado.getInt("numero_tentativa"),
                            resultado.getString("status"),
                            resultado.getInt("oxigenio_inicial"),
                            resultado.getTimestamp("iniciada_em"),
                            resultado.getTimestamp("finalizada_em")
                    );
                }
            }

        } catch (SQLException erro) {
            System.out.println("Erro ao buscar tentativa em andamento: "
                    + erro.getMessage());
        }

        return null;
    }

    // Finaliza uma tentativa com o status informado
    public boolean finalizarTentativa(int idTentativaFase, String status) {

        if (!"concluida".equals(status) && !"game_over".equals(status)) {
            System.out.println("Status de finalizacao invalido.");
            return false;
        }

        String sql = "UPDATE tentativa_fase "
                + "SET status = ?, finalizada_em = CURRENT_TIMESTAMP "
                + "WHERE id_tentativa_fase = ? "
                + "AND status = 'em_andamento'";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, status);
            comando.setInt(2, idTentativaFase);

            return comando.executeUpdate() > 0;

        } catch (SQLException erro) {
            System.out.println("Erro ao finalizar tentativa: "
                    + erro.getMessage());
        }

        return false;
    }
}
