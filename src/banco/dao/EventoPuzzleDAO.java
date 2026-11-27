
package banco.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
             PreparedStatement comando = conexao.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            comando.setInt(1, idTentativaPuzzle);
            comando.setString(2, tipoEvento);
            comando.setString(3, valor);
            comando.setObject(4, correto);
            comando.setInt(5, custoOxigenio);
            comando.setObject(6, oxigenioDepois);

            comando.executeUpdate();

            try (ResultSet resultado = comando.getGeneratedKeys()) {
                if (resultado.next()) {
                    return resultado.getInt(1);
                }
            }

        } catch (SQLException erro) {
            System.out.println("Erro ao cadastrar evento: "
                    + erro.getMessage());
        }

        return -1;
    }

    // Busca um evento pelo ID
    public EventoPuzzle buscarEventoPorId(int idEvento) {

        String sql = "SELECT * FROM evento_puzzle WHERE id_evento = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, idEvento);

            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return criarObjetoEvento(resultado);
                }
            }

        } catch (SQLException erro) {
            System.out.println("Erro ao buscar evento: "
                    + erro.getMessage());
        }

        return null;
    }

    // Lista os eventos de uma tentativa de puzzle
    public List<EventoPuzzle> listarEventosPorTentativaPuzzle(
            int idTentativaPuzzle) {

        List<EventoPuzzle> eventos = new ArrayList<>();

        String sql = "SELECT * FROM evento_puzzle "
                + "WHERE id_tentativa_puzzle = ? "
                + "ORDER BY criado_em, id_evento";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, idTentativaPuzzle);

            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    eventos.add(criarObjetoEvento(resultado));
                }
            }

        } catch (SQLException erro) {
            System.out.println("Erro ao listar eventos: "
                    + erro.getMessage());
        }

        return eventos;
    }

    // Converte uma linha do banco em objeto EventoPuzzle
    private EventoPuzzle criarObjetoEvento(ResultSet resultado)
            throws SQLException {

        return new EventoPuzzle(
                resultado.getInt("id_evento"),
                resultado.getInt("id_tentativa_puzzle"),
                resultado.getString("tipo_evento"),
                resultado.getString("valor"),
                resultado.getObject("correto", Boolean.class),
                resultado.getInt("custo_oxigenio"),
                (Integer) resultado.getObject("oxigenio_depois"),
                resultado.getTimestamp("criado_em")
        );
    }
}
