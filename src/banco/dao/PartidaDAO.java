
package banco.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import banco.Conexao;
import banco.Partida;

public class PartidaDAO {

    // Registra uma nova partida no banco de dados
    public int criarPartida(int idUsuario) {

        String sql = "INSERT INTO partida (id_usuario) VALUES (?)";

        try (Connection conexao = Conexao.conectar()) {

            // Verifica a conexão
            if (conexao == null) {
                return -1;
            }

            // Prepara o comando SQL
            try (PreparedStatement comando = conexao.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)) {

                comando.setInt(1, idUsuario);

                // Executa o cadastro
                comando.executeUpdate();

                // Recupera o ID gerado pelo banco
                try (ResultSet resultado = comando.getGeneratedKeys()) {

                    if (resultado.next()) {
                        return resultado.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {

            // Exibe possíveis erros
            System.out.println("Erro ao criar partida: " + e.getMessage());
        }

        return -1;
    }

    // Busca uma partida pelo identificador
    public Partida buscarPartidaPorId(int idPartida) {

        String sql = "SELECT * FROM partida WHERE id_partida = ?";

        try (Connection conexao = Conexao.conectar()) {

            // Verifica a conexão
            if (conexao == null) {
                return null;
            }

            // Prepara a consulta
            try (PreparedStatement comando = conexao.prepareStatement(sql)) {

                comando.setInt(1, idPartida);

                // Executa a consulta
                try (ResultSet resultado = comando.executeQuery()) {

                    if (resultado.next()) {

                        // Cria a partida com os dados encontrados
                        return new Partida(
                            resultado.getInt("id_partida"),
                            resultado.getInt("id_usuario"),
                            resultado.getString("status"),
                            resultado.getTimestamp("criada_em"),
                            resultado.getTimestamp("atualizada_em")
                        );
                    }
                }
            }

        } catch (SQLException e) {

            // Exibe possíveis erros
            System.out.println("Erro ao buscar partida: " + e.getMessage());
        }

        return null;
    }
}
