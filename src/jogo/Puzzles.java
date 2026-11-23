package jogo;

/**
 * Fábrica de puzzles: cada peça (0 a 5) tem o seu.
 * Para mudar as perguntas ou trocar um puzzle por outro, é só editar aqui.
 */
public class Puzzles {

    public static Puzzle criar(int peca) {

        SistemaNave s = SistemaNave.values()[peca];

        switch (peca) {

            case 0: // Estrutura: sequências de números
                return new PuzzleQuiz(s, "Complete as sequências para encaixar as placas do casco.",
                        new String[] { "2, 4, 8, 16, ... qual é o próximo número?",
                                "1, 1, 2, 3, 5, ... qual é o próximo número?" },
                        new String[][] { { "18", "24", "32", "64" }, { "7", "8", "10", "13" } },
                        new int[] { 2, 1 });

            case 1: // Propulsão: perguntas de Java
                return new PuzzleQuiz(s, "Responda sobre Java para religar o motor.",
                        new String[] { "Qual comando imprime Olá na tela em Java?",
                                "Qual palavra-chave declara uma variável de número inteiro?",
                                "Qual é a PRIMEIRA coisa a fazer ao entrar na sala do professor Rogério?" },
                        new String[][] {
                                { "echo(\"Olá\");", "System.out.println(\"Olá\");", "print.Olá();", "console.log(\"Olá\");" },
                                { "string", "float", "int", "bool" },
                                { "Fechar a porta", "Deixar a porta aberta", "Ligar o ventilador", "Sentar no chão" } },
                        new int[] { 1, 2, 0 });

            case 2: // Energia: memória
                return new PuzzleSequencia(s);

            case 3: // Controle térmico: ordenar
                return new PuzzleOrdem(s);

            case 4: // Atitude: alinhar as rodas
                return new PuzzleRodas(s);

            default: // Navegação: labirinto
                return new PuzzleLabirinto(s);
        }
    }
}
