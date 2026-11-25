
package banco.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import banco.Conexao;
import banco.TentativaPuzzle;

public class TentativaPuzzleDAO {

    // Cadastra uma tentativa de puzzle
    public int criarTentativa(int idTentativaFase, int idTipoPuzzle,
            int idPeca, int ordem, Integer semente,
            Integer versaoConteudo, String conteudo) {

        String sql = "INSERT INTO tentativa_puzzle "
                + "(id_tentativa_fase, id_tipo_puzzle, id_peca, "
                + "ordem, semente, versao_conteudo, conteudo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, idTentativaFase);
            stmt.setInt(2, idTipoPuzzle);
            stmt.setInt(3, idPeca);
            stmt.setInt(4, ordem);
            stmt.setObject(5, semente);
            stmt.setObject(6, versaoConteudo);
            stmt.setString(7, conteudo);

            stmt.executeUpdate();

            try (ResultSet resultado = stmt.getGeneratedKeys()) {
                if (resultado.next()) {
                    return resultado.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar tentativa de puzzle: "
                    + e.getMessage());
        }

        return -1;
    }

    // Busca uma tentativa de puzzle pelo ID
    public TentativaPuzzle buscarTentativaPorId(int idTentativaPuzzle) {

        String sql = "SELECT * FROM tentativa_puzzle "
                + "WHERE id_tentativa_puzzle = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idTentativaPuzzle);

            try (ResultSet resultado = stmt.executeQuery()) {

                if (resultado.next()) {

                    return new TentativaPuzzle(
                            resultado.getInt("id_tentativa_puzzle"),
                            resultado.getInt("id_tentativa_fase"),
                            resultado.getInt("id_tipo_puzzle"),
                            resultado.getInt("id_peca"),
                            resultado.getInt("ordem"),
                            (Integer) resultado.getObject("semente"),
                            (Integer) resultado.getObject("versao_conteudo"),
                            resultado.getString("status"),
                            resultado.getString("conteudo")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar tentativa de puzzle: "
                    + e.getMessage());
        }

        return null;
    }

    // Atualiza o status de uma tentativa de puzzle
    public boolean atualizarStatus(int idTentativaPuzzle, String novoStatus) {

        String sql = "UPDATE tentativa_puzzle "
                + "SET status = ? WHERE id_tentativa_puzzle = ?";

        try (Connection conexao = Conexao.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, novoStatus);
            stmt.setInt(2, idTentativaPuzzle);

            int linhasAfetadas = stmt.executeUpdate();

            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar status: " + e.getMessage());
        }

        return false;
    }
}
