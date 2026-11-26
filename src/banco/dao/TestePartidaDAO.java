
package banco.dao;

import banco.Partida;

public class TestePartidaDAO {

    public static void main(String[] args) {

        PartidaDAO partidaDAO = new PartidaDAO();

        // Busca a partida ativa do usuario de teste
        Partida partida =
                partidaDAO.buscarPartidaAtivaPorUsuario(1);

        if (partida != null) {

            System.out.println("Partida ativa encontrada!");
            System.out.println("ID: " + partida.getIdPartida());
            System.out.println("Usuario: " + partida.getIdUsuario());
            System.out.println("Status: " + partida.getStatus());

        } else {

            System.out.println("Nenhuma partida ativa encontrada para esse usuario.");

        }
    }
}
