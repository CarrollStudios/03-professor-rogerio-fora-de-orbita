
package banco.dao;

public class TestePartidaDAO {

    public static void main(String[] args) {

        // Cria um objeto para acessar os métodos de partida
        PartidaDAO partidaDAO = new PartidaDAO();

        // Testa o cadastro de uma nova partida
        int idPartida = partidaDAO.criarPartida(1);

        // Exibe o resultado do teste
        if (idPartida != -1) {
            System.out.println("Partida cadastrada com sucesso!");
            System.out.println("ID da partida: " + idPartida);
        } else {
            System.out.println("Não foi possível cadastrar a partida.");
        }
    }
}
