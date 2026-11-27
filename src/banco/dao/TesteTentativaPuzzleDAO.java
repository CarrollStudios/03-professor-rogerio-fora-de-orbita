
package banco.dao;

import banco.TentativaPuzzle;
import java.util.List;

public class TesteTentativaPuzzleDAO {

    public static void main(String[] args) {

        TentativaPuzzleDAO tentativaDAO = new TentativaPuzzleDAO();

        int idTentativaPuzzle = 2;
        int idTentativaFase = 2;

        // Busca a tentativa de puzzle
        TentativaPuzzle tentativa =
                tentativaDAO.buscarTentativaPorId(idTentativaPuzzle);

        if (tentativa != null) {

            System.out.println("Tentativa de puzzle encontrada!");
            System.out.println("ID: " + tentativa.getIdTentativaPuzzle());
            System.out.println("Tentativa de fase: " + tentativa.getIdTentativaFase());
            System.out.println("Tipo de puzzle: " + tentativa.getIdTipoPuzzle());
            System.out.println("Peca: " + tentativa.getIdPeca());
            System.out.println("Ordem: " + tentativa.getOrdem());
            System.out.println("Status: " + tentativa.getStatus());

            // Atualiza o conteudo
            boolean conteudoAtualizado =
                    tentativaDAO.atualizarConteudo(
                            idTentativaPuzzle,
                            "{\"pergunta\":\"Teste de puzzle\"}"
                    );

            if (conteudoAtualizado) {
                System.out.println("\nConteudo atualizado com sucesso!");
            }

            // Busca novamente para conferir a atualizacao
            tentativa = tentativaDAO.buscarTentativaPorId(idTentativaPuzzle);

            if (tentativa != null) {
                System.out.println("Conteudo atual: " + tentativa.getConteudo());
            }

        } else {
            System.out.println("Tentativa de puzzle nao encontrada.");
        }

        // Lista os puzzles da tentativa de fase
        List<TentativaPuzzle> puzzles =
                tentativaDAO.listarPuzzlesPorTentativaFase(idTentativaFase);

        System.out.println("\nQuantidade de puzzles encontrados: " + puzzles.size());

        for (TentativaPuzzle puzzle : puzzles) {
            System.out.println("ID: " + puzzle.getIdTentativaPuzzle());
            System.out.println("Ordem: " + puzzle.getOrdem());
            System.out.println("Status: " + puzzle.getStatus());
        }
    }
}
