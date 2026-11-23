package jogo;

/**
 * Fábrica de puzzles. Cada fase tem um NÍVEL:
 *   fase 1 = fácil, fase 2 = médio, fase 3 = difícil.
 * Cada fase tem 6 puzzles possíveis (veja a lista abaixo), mas o jogo só usa 2 por fase (um para cada peça).
 * Quais são sorteados fica por conta do Progresso (o sorteio acontece quando o jogo começa do zero),
 * então ao recomeçar depois de um game over os desafios mudam. E os puzzles de pergunta e os de
 * "bagunça" (rodas, ordem, luzes) também sorteiam o conteúdo de novo a cada vez.
 *
 * Os 6 puzzles possíveis de cada fase:
 *   0 = quiz 1      (fácil: sequências de números | médio e difícil: Java)
 *   1 = quiz 2      (fácil: Java                   | médio e difícil: lógica)
 *   2 = luzes       (memorize a sequência)
 *   3 = ordem       (fácil: temperaturas, médio: planetas, difícil: passos de um programa Java)
 *   4 = rodas       (fácil: 3 rodas, médio: 4, difícil: 5)
 *   5 = labirinto   (mapa pequeno, médio e grande)
 * Para mudar a dificuldade de algum, é só editar a linha correspondente aqui (e as classes Puzzle...).
 * Na fase 3 (peças 5 e 6) o ET ajuda com até 2 dicas, em qualquer um dos puzzles.
 */
public class Puzzles {

    public static Puzzle criar(int peca) {
        int fase = peca / 2;
        int sorteado = Progresso.getSorteio(fase, peca % 2);
        Puzzle p = novo(SistemaNave.values()[peca], fase, sorteado);
        if (fase == 2) {
            p.ativarDicas();
        }
        return p;
    }

    private static Puzzle novo(SistemaNave s, int nivel, int qual) {

        switch (qual) {

            case 0:
                if (nivel == 0) {
                    return new PuzzleQuiz(s, "Complete as sequências de números.", Perguntas.numeros(nivel, 3));
                }
                return new PuzzleQuiz(s, "Responda sobre Java para religar os sistemas.", Perguntas.java(nivel, 3));

            case 1:
                if (nivel == 0) {
                    return new PuzzleQuiz(s, "Responda sobre Java para religar os sistemas.", Perguntas.java(nivel, 3));
                }
                return new PuzzleQuiz(s, "Resolva os desafios de lógica do computador de bordo.", Perguntas.logica(nivel, 3));

            case 2:
                return new PuzzleSequencia(s, nivel);

            case 3:
                return nivel == 0 ? PuzzleOrdem.temperaturas(s) : nivel == 1 ? PuzzleOrdem.planetas(s) : PuzzleOrdem.programa(s);

            case 4:
                return new PuzzleRodas(s, nivel + 3);

            default:
                return new PuzzleLabirinto(s, nivel);
        }
    }
}
