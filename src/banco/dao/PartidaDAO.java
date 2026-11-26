
package banco.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import banco.Conexao;
import banco.Partida;

public class PartidaDAO {

    // Cadastra uma nova partida
    public int criarPartida(int idUsuario) {

        String sql = "INSERT INTO partida (id_usuario) VALUES (?)";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            comando.setInt(1, idUsuario);
            comando.executeUpdate();

            try (ResultSet resultado = comando.getGeneratedKeys()) {
                if (resultado.next()) {
                    return resultado.getInt(1);
                }
            }

        } catch (SQLException erro) {
            System.out.println("Erro ao cadastrar partida: "
                    + erro.getMessage());
        }

        return -1;
    }

    // Busca uma partida pelo ID
    public Partida buscarPartidaPorId(int idPartida) {

        String sql = "SELECT * FROM partida WHERE id_partida = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, idPartida);

            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return new Partida(
                            resultado.getInt("id_partida"),
                            resultado.getInt("id_usuario"),
                            resultado.getString("status"),
                            resultado.getTimestamp("criada_em"),
                            resultado.getTimestamp("atualizada_em")
                    );
                }
            }

        } catch (SQLException erro) {
            System.out.println("Erro ao buscar partida: "
                    + erro.getMessage());
        }

        return null;
    }

    // Busca a partida ativa de um usuário
    public Partida buscarPartidaAtivaPorUsuario(int idUsuario) {

        String sql = "SELECT * FROM partida "
                + "WHERE id_usuario = ? AND status = 'em_andamento' "
                + "ORDER BY atualizada_em DESC LIMIT 1";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, idUsuario);

            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return new Partida(
                            resultado.getInt("id_partida"),
                            resultado.getInt("id_usuario"),
                            resultado.getString("status"),
                            resultado.getTimestamp("criada_em"),
                            resultado.getTimestamp("atualizada_em")
                    );
                }
            }

        } catch (SQLException erro) {
            System.out.println("Erro ao buscar partida ativa: "
                    + erro.getMessage());
        }

        return null;
    }

    // Atualiza o status de uma partida
    public boolean atualizarStatus(int idPartida, String novoStatus) {

        String sql = "UPDATE partida SET status = ? WHERE id_partida = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, novoStatus);
            comando.setInt(2, idPartida);

            return comando.executeUpdate() > 0;

        } catch (SQLException erro) {
            System.out.println("Erro ao atualizar partida: "
                    + erro.getMessage());
        }

        return false;
    }
}
