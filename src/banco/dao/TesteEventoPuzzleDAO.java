
package banco.dao;

import banco.EventoPuzzle;
import java.util.List;

public class TesteEventoPuzzleDAO {

    public static void main(String[] args) {

        EventoPuzzleDAO eventoDAO = new EventoPuzzleDAO();

        int idEvento = 2;
        int idTentativaPuzzle = 2;

        // Busca um evento pelo ID
        EventoPuzzle evento = eventoDAO.buscarEventoPorId(idEvento);

        if (evento != null) {

            System.out.println("Evento encontrado!");
            System.out.println("ID: " + evento.getIdEvento());
            System.out.println("Tentativa de puzzle: " + evento.getIdTentativaPuzzle());
            System.out.println("Tipo: " + evento.getTipoEvento());
            System.out.println("Valor: " + evento.getValor());
            System.out.println("Correto: " + evento.getCorreto());
            System.out.println("Custo de oxigenio: " + evento.getCustoOxigenio());
            System.out.println("Oxigenio depois: " + evento.getOxigenioDepois());
            System.out.println("Criado em: " + evento.getCriadoEm());

        } else {
            System.out.println("Evento nao encontrado.");
        }

        // Lista os eventos da tentativa de puzzle
        List<EventoPuzzle> eventos =
                eventoDAO.listarEventosPorTentativaPuzzle(idTentativaPuzzle);

        System.out.println("\nQuantidade de eventos encontrados: " + eventos.size());

        for (EventoPuzzle e : eventos) {
            System.out.println("Evento ID: " + e.getIdEvento());
            System.out.println("Tipo: " + e.getTipoEvento());
            System.out.println("Valor: " + e.getValor());
            System.out.println("----------------------------");
        }
    }
}
