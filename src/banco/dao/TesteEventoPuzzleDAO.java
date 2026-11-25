
package banco.dao;

import banco.EventoPuzzle;

public class TesteEventoPuzzleDAO {

    public static void main(String[] args) {

        EventoPuzzleDAO eventoDAO = new EventoPuzzleDAO();

        // Cadastra um evento
        int idGerado = eventoDAO.criarEvento(
                2, "resposta", "alternativa A",
                true, 0, 4
        );

        if (idGerado != -1) {

            System.out.println("Evento cadastrado com sucesso!");
            System.out.println("ID do evento: " + idGerado);

            // Busca o evento cadastrado
            EventoPuzzle evento =
                    eventoDAO.buscarEventoPorId(idGerado);

            if (evento != null) {

                System.out.println("\nDados do evento:");
                System.out.println("ID: " + evento.getIdEvento());
                System.out.println("Tentativa de puzzle: "
                        + evento.getIdTentativaPuzzle());
                System.out.println("Tipo: " + evento.getTipoEvento());
                System.out.println("Valor: " + evento.getValor());
                System.out.println("Correto: " + evento.getCorreto());
                System.out.println("Custo de oxigênio: "
                        + evento.getCustoOxigenio());
                System.out.println("Oxigênio depois: "
                        + evento.getOxigenioDepois());
                System.out.println("Criado em: " + evento.getCriadoEm());
            }
        }
    }
}
