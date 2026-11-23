package jogo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Bancos de perguntas dos puzzles de quiz.
 * Cada banco tem MAIS perguntas do que o puzzle usa: a cada vez, algumas são sorteadas
 * (e as alternativas também são embaralhadas), então ao recomeçar o jogo as perguntas mudam.
 * Para acrescentar uma pergunta: é só colocar mais uma linha p(...) no banco.
 * Formato: p("pergunta", "dica do ET", "resposta CERTA", "errada 1", "errada 2", "errada 3")
 */
public class Perguntas {

    // Uma pergunta já sorteada (com as alternativas embaralhadas)
    public static class Pergunta {
        public final String texto;
        public final String dica;
        public final String[] opcoes;
        public final int certa;      // posição (0 a 3) da resposta certa

        Pergunta(String texto, String dica, String[] opcoes, int certa) {
            this.texto = texto;
            this.dica = dica;
            this.opcoes = opcoes;
            this.certa = certa;
        }
    }

    // Uma pergunta ainda não embaralhada: a resposta certa é a primeira alternativa
    private static class Modelo {
        final String texto, dica;
        final String[] alternativas;

        Modelo(String texto, String dica, String... alternativas) {
            this.texto = texto;
            this.dica = dica;
            this.alternativas = alternativas;
        }
    }

    private static Modelo p(String texto, String dica, String certa, String e1, String e2, String e3) {
        return new Modelo(texto, dica, certa, e1, e2, e3);
    }

    // Sorteia 'quantas' perguntas diferentes do banco e embaralha as alternativas de cada uma
    private static Pergunta[] sortear(Modelo[] banco, int quantas) {
        List<Modelo> lista = new ArrayList<>(java.util.Arrays.asList(banco));
        Collections.shuffle(lista);
        Pergunta[] r = new Pergunta[Math.min(quantas, lista.size())];
        for (int i = 0; i < r.length; i++) {
            Modelo m = lista.get(i);
            List<String> alt = new ArrayList<>(java.util.Arrays.asList(m.alternativas));
            String certa = alt.get(0);
            Collections.shuffle(alt);
            r[i] = new Pergunta(m.texto, m.dica, alt.toArray(new String[0]), alt.indexOf(certa));
        }
        return r;
    }

    // ============================================================ FÁCIL

    private static final Modelo[] FACIL_NUMEROS = {
            p("2, 4, 6, 8, ... qual é o próximo número?", "Veja quanto aumenta de um número para o outro.", "10", "9", "12", "14"),
            p("5, 10, 15, 20, ... qual é o próximo número?", "Está contando de 5 em 5.", "25", "22", "30", "35"),
            p("1, 3, 5, 7, ... qual é o próximo número?", "Só aparecem números ímpares.", "9", "8", "10", "11"),
            p("3, 6, 12, 24, ... qual é o próximo número?", "Cada número é o dobro do anterior.", "48", "36", "30", "27"),
            p("10, 9, 8, 7, ... qual é o próximo número?", "Os números estão diminuindo de 1 em 1.", "6", "5", "4", "7"),
            p("1, 4, 9, 16, ... qual é o próximo número?", "São os quadrados: 1x1, 2x2, 3x3, 4x4...", "25", "20", "24", "36"),
            p("2, 4, 8, 16, ... qual é o próximo número?", "Cada número é o dobro do anterior.", "32", "18", "24", "64") };

    private static final Modelo[] FACIL_JAVA = {
            p("Qual comando imprime Olá na tela em Java?", "Começa com System.out...", "System.out.println(\"Olá\");",
                    "echo(\"Olá\");", "print.Olá();", "console.log(\"Olá\");"),
            p("Qual palavra-chave declara uma variável de número inteiro?", "É a abreviação de \"integer\".", "int", "string", "float", "bool"),
            p("Qual símbolo termina uma instrução em Java?", "Fica no fim de quase toda linha de código.", "; (ponto e vírgula)",
                    ": (dois pontos)", ". (ponto)", ", (vírgula)"),
            p("Qual é a PRIMEIRA coisa a fazer ao entrar na sala do professor Rogério?", "Você já sabe... a nave que o diga.",
                    "Fechar a porta", "Deixar a porta aberta", "Ligar o ventilador", "Sentar no chão"),
            p("Qual destes é um comentário de uma linha em Java?", "Começa com duas barras.", "// comentário", "# comentário",
                    "-- comentário", "<!-- comentário -->"),
            p("Qual tipo guarda um texto em Java?", "Começa com letra maiúscula.", "String", "int", "boolean", "double"),
            p("Quais valores uma variável boolean pode ter?", "Só existem duas possibilidades.", "true ou false", "0 a 9", "sim ou talvez",
                    "qualquer texto") };

    // ============================================================ MÉDIO

    private static final Modelo[] MEDIO_JAVA = {
            p("Qual palavra-chave cria um objeto novo?", "Aparece antes do nome da classe: ... Aluno();", "new", "make", "create", "object"),
            p("Quantas vezes roda: for (int i = 0; i < 3; i++) ?", "Conte: i vale 0, 1 e 2.", "3 vezes", "4 vezes", "2 vezes",
                    "Para sempre"),
            p("Qual é o resultado de 7 / 2 com números inteiros em Java?", "Com int, a parte decimal é descartada.", "3", "3.5", "4", "2"),
            p("Qual método inicia todo programa Java?", "O nome é uma palavra em inglês que significa \"principal\".", "main",
                    "start", "run", "init"),
            p("Como se compara o CONTEÚDO de duas Strings?", "É um método, não um operador.", "a.equals(b)", "a == b", "a = b",
                    "a.compare(b)"),
            p("Qual estrutura escolhe entre dois caminhos?", "\"Se\"... \"senão\".", "if / else", "for", "class", "import"),
            p("Qual é o índice do primeiro elemento de um array?", "Os computadores começam a contar do zero.", "0", "1", "-1", "10"),
            p("Qual palavra-chave faz uma classe herdar de outra?", "Em inglês, \"estender\".", "extends", "implements", "inherits",
                    "copy") };

    private static final Modelo[] MEDIO_LOGICA = {
            p("Quanto vale 1010 em binário, em decimal?", "Some as posições com 1: 8 + 2.", "10", "8", "12", "5"),
            p("Qual é o resultado de true && false?", "O E (&&) só dá true se os DOIS forem true.", "false", "true", "null", "erro"),
            p("Qual é o resultado de true || false?", "O OU (||) dá true se pelo menos um for true.", "true", "false", "null", "erro"),
            p("Quantos bits tem um byte?", "É um número que também é uma potência de 2.", "8", "4", "16", "32"),
            p("Qual é o resultado de 10 % 3?", "O % é o resto da divisão.", "1", "0", "3", "3.33"),
            p("Se x = 5 e y = x * 2, quanto vale y + x?", "Primeiro descubra o valor de y.", "15", "10", "20", "25"),
            p("Qual é o resultado de !false ?", "O ! inverte o valor.", "true", "false", "0", "null") };

    // ============================================================ DIFÍCIL

    private static final Modelo[] DIFICIL_JAVA = {
            p("O que imprime: System.out.println(10 + 5 + \"a\");", "A soma acontece primeiro, da esquerda para a direita.",
                    "15a", "105a", "10 5a", "dá erro"),
            p("O que imprime: System.out.println(\"a\" + 10 + 5);", "Depois de aparecer um texto, o + vira junção de texto.",
                    "a105", "a15", "15a", "dá erro"),
            p("Quantas vezes roda: for (int i = 0; i < 10; i += 3) ?", "Escreva os valores de i: 0, 3, 6, ...", "4 vezes", "3 vezes",
                    "5 vezes", "10 vezes"),
            p("O que significa static num método?", "Você consegue chamar sem criar um objeto.",
                    "Pertence à classe, não a um objeto", "O método é privado", "O método não retorna nada", "Não pode ser chamado"),
            p("int x = 5; x++; System.out.println(x * 2);  Qual é a saída?", "O x++ aumenta o x antes da multiplicação.", "12", "10",
                    "11", "14"),
            p("Qual exceção acontece ao dividir um int por zero?", "O nome lembra \"aritmética\".", "ArithmeticException",
                    "NullPointerException", "IOException", "ClassCastException"),
            p("Qual o valor de um atributo String que não foi inicializado?", "Significa \"nada\", \"ausência de objeto\".", "null",
                    "\"\" (vazio)", "0", "false"),
            p("int[] v = {4, 7, 1}; quanto vale v[v.length - 1] ?", "O último índice é o tamanho menos um.", "1", "4", "7",
                    "dá erro") };

    private static final Modelo[] DIFICIL_LOGICA = {
            p("Quanto vale 1111 em binário, em decimal?", "8 + 4 + 2 + 1.", "15", "14", "16", "31"),
            p("Qual é o resultado de !(true && false) ?", "Resolva dentro dos parênteses primeiro, depois inverta.", "true", "false",
                    "null", "erro"),
            p("Qual o próximo da sequência 2, 3, 5, 7, 11, ... ?", "São os números primos.", "13", "12", "15", "17"),
            p("Qual o próximo da sequência 1, 1, 2, 3, 5, 8, ... ?", "Cada número é a soma dos dois anteriores.", "13", "11", "12",
                    "14"),
            p("Quantos valores diferentes cabem em 3 bits?", "Cada bit dobra a quantidade: 2 x 2 x 2.", "8", "6", "9", "16"),
            p("Quanto é 2 elevado a 5 ?", "2 x 2 x 2 x 2 x 2.", "32", "10", "25", "64"),
            p("Se a = 3 e b = 4, quanto vale a*a + b*b ?", "9 + 16.", "25", "12", "49", "14") };

    // Bancos por nome (nível 0 = fácil, 1 = médio, 2 = difícil)
    public static Pergunta[] numeros(int nivel, int quantas) {
        return sortear(FACIL_NUMEROS, quantas);
    }

    public static Pergunta[] java(int nivel, int quantas) {
        return sortear(nivel == 0 ? FACIL_JAVA : nivel == 1 ? MEDIO_JAVA : DIFICIL_JAVA, quantas);
    }

    public static Pergunta[] logica(int nivel, int quantas) {
        return sortear(nivel <= 1 ? MEDIO_LOGICA : DIFICIL_LOGICA, quantas);
    }
}
