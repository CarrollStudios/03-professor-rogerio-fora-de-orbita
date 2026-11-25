
package banco.dao;

import banco.TentativaPuzzle;

public class TesteTentativaPuzzleDAO {

    public static void main(String[] args) {

        TentativaPuzzleDAO tentativaDAO = new TentativaPuzzleDAO();

        // Cadastra uma tentativa de puzzle
        int idGerado = tentativaDAO.criarTentativa(
                2, 1, 1, 1, null, 1, null
        );

        if (idGerado != -1) {

            System.out.println("Tentativa de puzzle cadastrada!");
            System.out.println("ID da tentativa: " + idGerado);

            // Busca a tentativa cadastrada
            TentativaPuzzle tentativa =
                    tentativaDAO.buscarTentativaPorId(idGerado);

            if (tentativa != null) {

                System.out.println("\nDados da tentativa:");
                System.out.println("ID: " + tentativa.getIdTentativaPuzzle());
                System.out.println("Tentativa de fase: "
                        + tentativa.getIdTentativaFase());
                System.out.println("Tipo de puzzle: "
                        + tentativa.getIdTipoPuzzle());
                System.out.println("Peça: " + tentativa.getIdPeca());
                System.out.println("Ordem: " + tentativa.getOrdem());
                System.out.println("Semente: " + tentativa.getSemente());
                System.out.println("Versão do conteúdo: "
                        + tentativa.getVersaoConteudo());
                System.out.println("Status: " + tentativa.getStatus());
                System.out.println("Conteúdo: " + tentativa.getConteudo());
            }

            // Atualiza o status
            boolean atualizado =
                    tentativaDAO.atualizarStatus(idGerado, "concluida");

            if (atualizado) {
                System.out.println("\nStatus atualizado com sucesso!");
            }
        }
    }
}
