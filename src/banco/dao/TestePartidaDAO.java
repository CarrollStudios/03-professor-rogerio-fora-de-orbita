
package banco.dao;

import banco.Partida;

public class TestePartidaDAO {

    public static void main(String[] args) {

        // Cria o objeto responsável pelas operações
        PartidaDAO partidaDAO = new PartidaDAO();

        // Atualiza o status da partida de ID 2
        boolean atualizado = partidaDAO.atualizarStatus(2, "concluida");

        if (atualizado) {
            System.out.println("Status atualizado com sucesso!");
        } else {
            System.out.println("Nao foi possivel atualizar o status.");
        }

        // Busca a partida para conferir a alteração
        Partida partida = partidaDAO.buscarPartidaPorId(2);

        if (partida != null) {
            System.out.println("ID: " + partida.getIdPartida());
            System.out.println("Novo status: " + partida.getStatus());
        }
    }
}
