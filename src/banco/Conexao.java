package banco;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    // Dados do banco de dados
    private static final String URL = "jdbc:mysql://localhost:3306/professor_rogerio";
    private static final String USUARIO = "jogo_app";
    private static final String SENHA = "roger4@3";

    // Realiza a conexão com o banco
    public static Connection conectar() {

        try {
            Connection conexao = DriverManager.getConnection(URL, USUARIO, SENHA);

            System.out.println("Conexao realizada com sucesso!");

            return conexao;

        } catch (SQLException e) {
            System.out.println("Erro ao conectar: " + e.getMessage());
            return null;
        }
    }
}