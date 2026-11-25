
package banco.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import banco.Conexao;
import banco.EventoPuzzle;

public class EventoPuzzleDAO {

    // Cadastra um evento de puzzle
    public int criarEvento(int idTentativaPuzzle, String tipoEvento,
            String valor, Boolean correto, int custoOxigenio,
            Integer oxigenioDepois) {

        String sql = "INSERT INTO evento_puzzle "
                + "(id_tentativa_puzzle, tipo_evento, valor, correto, "
                + "custo_oxigenio, oxigenio_depois) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, idTentativaPuzzle);
            stmt.setString(2, tipoEvento);
            stmt.setString(3, valor);
            stmt.setObject(4, correto);
            stmt.setInt(5, custoOxigenio);
            stmt.setObject(6, oxigenioDepois);

            stmt.executeUpdate();

            try (ResultSet resultado = stmt.getGeneratedKeys()) {
                if (resultado.next()) {
                    return resultado.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar evento: "
                    + e.getMessage());
        }

        return -1;
    }

    // Busca um evento pelo ID
    public EventoPuzzle buscarEventoPorId(int idEvento) {

        String sql = "SELECT * FROM evento_puzzle "
                + "WHERE id_evento = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idEvento);

            try (ResultSet resultado = stmt.executeQuery()) {

                if (resultado.next()) {

                    return new EventoPuzzle(
                            resultado.getInt("id_evento"),
                            resultado.getInt("id_tentativa_puzzle"),
                            resultado.getString("tipo_evento"),
                            resultado.getString("valor"),
                            (Boolean) resultado.getObject("correto"),
                            resultado.getInt("custo_oxigenio"),
                            (Integer) resultado.getObject("oxigenio_depois"),
                            resultado.getTimestamp("criado_em")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar evento: "
                    + e.getMessage());
        }

        return null;
    }
}
