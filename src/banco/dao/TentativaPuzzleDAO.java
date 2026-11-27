
package banco.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import banco.Conexao;
import banco.TentativaPuzzle;

public class TentativaPuzzleDAO {

    // Cadastra uma tentativa de puzzle
    public int criarTentativa(int idTentativaFase, int idTipoPuzzle,
            int idPeca, int ordem, Long semente,
            Integer versaoConteudo, String conteudo) {

        String sql = "INSERT INTO tentativa_puzzle "
                + "(id_tentativa_fase, id_tipo_puzzle, id_peca, "
                + "ordem, semente, versao_conteudo, conteudo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            comando.setInt(1, idTentativaFase);
            comando.setInt(2, idTipoPuzzle);
            comando.setInt(3, idPeca);
            comando.setInt(4, ordem);
            comando.setObject(5, semente);
            comando.setObject(6, versaoConteudo);
            comando.setString(7, conteudo);

            comando.executeUpdate();

            try (ResultSet resultado = comando.getGeneratedKeys()) {
                if (resultado.next()) {
                    return resultado.getInt(1);
                }
            }

        } catch (SQLException erro) {
            System.out.println("Erro ao cadastrar tentativa de puzzle: "
                    + erro.getMessage());
        }

        return -1;
    }

    // Busca uma tentativa de puzzle pelo ID
    public TentativaPuzzle buscarTentativaPorId(int idTentativaPuzzle) {

        String sql = "SELECT * FROM tentativa_puzzle "
                + "WHERE id_tentativa_puzzle = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, idTentativaPuzzle);

            try (ResultSet resultado = comando.executeQuery()) {
                if (resultado.next()) {
                    return criarObjetoTentativa(resultado);
                }
            }

        } catch (SQLException erro) {
            System.out.println("Erro ao buscar tentativa de puzzle: "
                    + erro.getMessage());
        }

        return null;
    }

    // Lista os puzzles de uma tentativa de fase
    public List<TentativaPuzzle> listarPuzzlesPorTentativaFase(
            int idTentativaFase) {

        List<TentativaPuzzle> puzzles = new ArrayList<>();

        String sql = "SELECT * FROM tentativa_puzzle "
                + "WHERE id_tentativa_fase = ? ORDER BY ordem";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setInt(1, idTentativaFase);

            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    puzzles.add(criarObjetoTentativa(resultado));
                }
            }

        } catch (SQLException erro) {
            System.out.println("Erro ao listar puzzles: "
                    + erro.getMessage());
        }

        return puzzles;
    }

    // Atualiza o conteúdo de um puzzle
    public boolean atualizarConteudo(int idTentativaPuzzle, String novoConteudo) {

        String sql = "UPDATE tentativa_puzzle "
                + "SET conteudo = ? WHERE id_tentativa_puzzle = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, novoConteudo);
            comando.setInt(2, idTentativaPuzzle);

            return comando.executeUpdate() > 0;

        } catch (SQLException erro) {
            System.out.println("Erro ao atualizar conteúdo do puzzle: "
                    + erro.getMessage());
        }

        return false;
    }

    // Atualiza o status de uma tentativa de puzzle
    public boolean atualizarStatus(int idTentativaPuzzle, String novoStatus) {

        String sql = "UPDATE tentativa_puzzle "
                + "SET status = ? WHERE id_tentativa_puzzle = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement comando = conexao.prepareStatement(sql)) {

            comando.setString(1, novoStatus);
            comando.setInt(2, idTentativaPuzzle);

            return comando.executeUpdate() > 0;

        } catch (SQLException erro) {
            System.out.println("Erro ao atualizar status: "
                    + erro.getMessage());
        }

        return false;
    }

    // Converte uma linha do banco em objeto TentativaPuzzle
    private TentativaPuzzle criarObjetoTentativa(ResultSet resultado)
            throws SQLException {

        return new TentativaPuzzle(
                resultado.getInt("id_tentativa_puzzle"),
                resultado.getInt("id_tentativa_fase"),
                resultado.getInt("id_tipo_puzzle"),
                resultado.getInt("id_peca"),
                resultado.getInt("ordem"),
                resultado.getObject("semente", Long.class),
                (Integer) resultado.getObject("versao_conteudo"),
                resultado.getString("status"),
                resultado.getString("conteudo")
        );
    }
}
