
package banco.dao;

import banco.TentativaFase;

public class TesteGameOverDAO {

    public static void main(String[] args) {

        TentativaFaseDAO tentativaDAO = new TentativaFaseDAO();

        // Cria uma tentativa de teste na fase 2
        int idGerado = tentativaDAO.criarTentativa(2, 2, 1);

        if (idGerado != -1) {

            System.out.println("Tentativa de teste cadastrada!");
            System.out.println("ID: " + idGerado);

            // Finaliza a tentativa com Game Over
            boolean finalizada =
                    tentativaDAO.finalizarTentativa(idGerado, "game_over");

            if (finalizada) {
                System.out.println("Tentativa finalizada com game_over!");
            } else {
                System.out.println("Falha ao finalizar a tentativa.");
            }

            // Busca para conferir o resultado
            TentativaFase tentativa =
                    tentativaDAO.buscarTentativaPorId(idGerado);

            if (tentativa != null) {
                System.out.println("Status final: " + tentativa.getStatus());
                System.out.println("Finalizada em: " + tentativa.getFinalizadaEm());
            }

        } else {
            System.out.println("Nao foi possivel cadastrar a tentativa.");
        }
    }
}
