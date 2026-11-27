
package banco.dao;

import banco.TentativaFase;

public class TesteTentativaFaseDAO {

    public static void main(String[] args) {

        TentativaFaseDAO tentativaDAO = new TentativaFaseDAO();

        // Busca a tentativa em andamento da fase 1
        TentativaFase tentativaEmAndamento =
                tentativaDAO.buscarTentativaEmAndamento(2, 1);

        if (tentativaEmAndamento != null) {
            System.out.println("Ja existe uma tentativa em andamento.");
            System.out.println("ID: " + tentativaEmAndamento.getIdTentativaFase());
            System.out.println("Numero: " + tentativaEmAndamento.getNumeroTentativa());
            System.out.println("Status: " + tentativaEmAndamento.getStatus());
            return;
        }

        // Consulta as tentativas existentes antes de cadastrar outra
        int numeroTentativa = 3;

        int idGerado = tentativaDAO.criarTentativa(2, 1, numeroTentativa);

        if (idGerado != -1) {

            System.out.println("Tentativa cadastrada com sucesso!");
            System.out.println("ID da tentativa: " + idGerado);

            TentativaFase tentativa =
                    tentativaDAO.buscarTentativaPorId(idGerado);

            if (tentativa != null) {
                System.out.println("\nDados da tentativa:");
                System.out.println("ID: " + tentativa.getIdTentativaFase());
                System.out.println("Partida: " + tentativa.getIdPartida());
                System.out.println("Fase: " + tentativa.getIdFase());
                System.out.println("Numero da tentativa: " + tentativa.getNumeroTentativa());
                System.out.println("Status: " + tentativa.getStatus());
                System.out.println("Oxigenio inicial: " + tentativa.getOxigenioInicial());
                System.out.println("Iniciada em: " + tentativa.getIniciadaEm());
                System.out.println("Finalizada em: " + tentativa.getFinalizadaEm());
            }

            // Finaliza a tentativa cadastrada
            boolean finalizada =
                    tentativaDAO.finalizarTentativa(idGerado, "concluida");

            if (finalizada) {
                System.out.println("\nTentativa finalizada com sucesso!");
            } else {
                System.out.println("\nNao foi possivel finalizar a tentativa.");
            }

            // Confere a alteração
            TentativaFase tentativaFinalizada =
                    tentativaDAO.buscarTentativaPorId(idGerado);

            if (tentativaFinalizada != null) {
                System.out.println("\nDados apos a finalizacao:");
                System.out.println("Status final: " + tentativaFinalizada.getStatus());
                System.out.println("Finalizada em: " + tentativaFinalizada.getFinalizadaEm());
            }

        } else {
            System.out.println("Nao foi possivel cadastrar a tentativa.");
        }
    }
}
