
package banco;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Conexao {

    public static Connection conectar() {

        Properties propriedades = new Properties();

        try (FileInputStream arquivo =
                new FileInputStream("config.properties")) {

            propriedades.load(arquivo);

        } catch (IOException e) {
            System.out.println("Erro ao carregar as configuracoes: "
                    + e.getMessage());
            return null;
        }

        String url = propriedades.getProperty("URL");
        String usuario = propriedades.getProperty("USUARIO");
        String senha = propriedades.getProperty("SENHA");

        if (url == null || usuario == null || senha == null) {
            System.out.println("Configuracoes do banco incompletas.");
            return null;
        }

        try {

            Connection conexao =
                    DriverManager.getConnection(url, usuario, senha);

            System.out.println("Conexao realizada com sucesso!");

            return conexao;

        } catch (SQLException e) {
            System.out.println("Erro ao conectar: " + e.getMessage());
            return null;
        }
    }
}
