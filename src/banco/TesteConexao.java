package banco;

import java.sql.Connection;
import java.sql.SQLException;

public class TesteConexao {

    public static void main(String[] args) {

        // Testa a conexão com o banco
        try (Connection conexao = Conexao.conectar()) {

            // Verifica se a conexão foi realizada
            if (conexao != null) {
                System.out.println("Teste concluido!");
            } else {
                System.out.println("Falha no teste!");
            }

        } catch (SQLException e) {

            // Exibe possíveis erros
            System.out.println("Erro ao fechar a conexao: " + e.getMessage());
        }
    }
}