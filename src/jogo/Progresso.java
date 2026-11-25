package jogo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * Guarda o progresso da Zoe. Tudo é salvo num arquivo na pasta do usuário, então continua
 * valendo mesmo depois de fechar o jogo. Fase 1 = peças 1 e 2, fase 2 = peças 3 e 4, fase 3 = peças 5 e 6.
 *
 * REGRAS:
 *  - OXIGÊNIO: a Zoe começa CADA FASE com OXIGENIO_MAXIMO. Cada erro gasta 1 e o oxigênio perdido
 *    não volta durante a fase. Chegou a zero: game over.
 *  - GAME OVER: reinicia SÓ A FASE em que o jogador perdeu (reiniciarFase). As fases anteriores e as
 *    peças já conquistadas nelas continuam. A fase recomeça com oxigênio cheio, dicas do ET cheias
 *    e puzzles NOVOS (novo sorteio, novas sementes). Cada recomeço conta como uma nova "tentativa".
 *  - SORTEIO: cada fase tem PUZZLES_NO_SORTEIO puzzles possíveis e só 2 são usados (um por peça).
 *    Cada peça também tem uma SEMENTE: com a mesma semente o puzzle sai sempre igual (mesmas perguntas,
 *    mesma sequência de luzes...). Por isso sair e voltar não troca os desafios.
 *  - QUIZ: o jogo lembra quantas perguntas do quiz já foram acertadas e retoma na pergunta certa.
 *  - Os outros puzzles recomeçam do início (com a mesma semente), mas o oxigênio perdido e as dicas
 *    do ET já usadas continuam gastos.
 *
 * Tudo que está aqui (peças, oxigênio, dicas, sorteio, sementes, tentativa, progresso do quiz e dicas
 * usadas por peça) é o que o back-end precisa guardar quando o jogo for ligado ao banco de dados.
 */
public class Progresso {

    // Quantos "tanques" de oxigênio a Zoe tem em cada fase (mude aqui se quiser mais ou menos)
    public static final int OXIGENIO_MAXIMO = 4;
    // Quantas dicas o ET dá na última fase (no total)
    public static final int DICAS_MAXIMAS = 2;
    // Quantos puzzles existem por fase (veja Puzzles.java); em cada fase são sorteados 2 (um por peça)
    public static final int PUZZLES_NO_SORTEIO = 6;

    private static final File ARQUIVO = new File(System.getProperty("user.home"), "professor-rogerio-progresso.properties");

    private static int pecas = 0;                       // quantas peças já foram recuperadas
    private static int oxigenio = OXIGENIO_MAXIMO;      // oxigênio que sobrou
    private static int dicas = DICAS_MAXIMAS;           // dicas do ET que sobraram
    private static final int[][] sorteio = new int[3][2];   // [fase][peça da fase] = puzzle sorteado
    private static final long[] semente = new long[6];      // [peça] = semente do conteúdo do puzzle
    private static final int[] tentativa = { 1, 1, 1 };     // [fase] = em qual tentativa está (1, 2, 3...)
    private static final int[] progressoQuiz = new int[6];  // [peça] = perguntas do quiz já acertadas
    private static final int[] dicasUsadas = new int[6];    // [peça] = dicas do ET já usadas nela

    static {
        carregar();
    }

    // ================================================================ peças e fases

    public static int getPecas() {
        return pecas;
    }

    // Em qual fase o jogador está (0, 1 ou 2). Com as 6 peças, devolve 3 (jogo completo).
    public static int getFase() {
        return pecas / 2;
    }

    public static boolean terminou() {
        return pecas >= 6;
    }

    // Chamado quando uma peça é recuperada. Ao terminar uma fase, a próxima começa com oxigênio cheio.
    public static void recuperouPeca() {
        if (pecas < 6) {
            pecas++;
            if (pecas % 2 == 0) {
                oxigenio = OXIGENIO_MAXIMO;
            }
            salvar();
        }
    }

    // Em qual tentativa da fase 'fase' (0 a 2) o jogador está: 1 = primeira vez, 2 = depois de um game over...
    public static int getTentativa(int fase) {
        return tentativa[fase];
    }

    // GAME OVER: recomeça só a fase atual. As fases anteriores e as peças delas ficam como estão.
    public static void reiniciarFase() {
        int f = getFase();
        if (f > 2) {
            return;
        }
        pecas = f * 2;
        oxigenio = OXIGENIO_MAXIMO;
        dicas = DICAS_MAXIMAS;
        tentativa[f]++;
        sortearFase(f);
        salvar();
    }

    // Recomeça o jogo todo do zero (R duas vezes no mapa)
    public static void zerar() {
        pecas = 0;
        oxigenio = OXIGENIO_MAXIMO;
        dicas = DICAS_MAXIMAS;
        for (int f = 0; f < 3; f++) {
            tentativa[f] = 1;
            sortearFase(f);
        }
        salvar();
    }

    // ================================================================ sorteio e sementes

    // Qual puzzle (0 a 5) foi sorteado para a peça 'j' (0 ou 1) da fase 'fase' (0 a 2)
    public static int getSorteio(int fase, int j) {
        return sorteio[fase][j];
    }

    // Semente do conteúdo do puzzle da peça 'peca' (0 a 5). Mesma semente = mesmo conteúdo.
    public static long getSemente(int peca) {
        return semente[peca];
    }

    // Sorteia 2 puzzles diferentes para a fase e cria uma semente nova para cada peça. Zera o progresso dessas peças.
    private static void sortearFase(int f) {
        List<Integer> todos = new ArrayList<>();
        for (int i = 0; i < PUZZLES_NO_SORTEIO; i++) {
            todos.add(i);
        }
        Collections.shuffle(todos);
        sorteio[f][0] = todos.get(0);
        sorteio[f][1] = todos.get(1);
        Random r = new Random();
        for (int j = 0; j < 2; j++) {
            int peca = f * 2 + j;
            long s;
            do {
                s = r.nextLong();
            } while (s == 0);
            semente[peca] = s;
            progressoQuiz[peca] = 0;
            dicasUsadas[peca] = 0;
        }
    }

    private static boolean faseValida(int f) {
        int a = sorteio[f][0], b = sorteio[f][1];
        return a >= 0 && b >= 0 && a < PUZZLES_NO_SORTEIO && b < PUZZLES_NO_SORTEIO && a != b
                && semente[f * 2] != 0 && semente[f * 2 + 1] != 0;
    }

    // ================================================================ oxigênio e dicas

    public static int getOxigenio() {
        return oxigenio;
    }

    // Gasta 1 de oxigênio. Devolve true se acabou (game over).
    public static boolean perderOxigenio() {
        if (oxigenio > 0) {
            oxigenio--;
            salvar();
        }
        return oxigenio <= 0;
    }

    public static int getDicas() {
        return dicas;
    }

    // O ET deu uma dica na peça 'peca'
    public static void usouDica(int peca) {
        if (dicas > 0) {
            dicas--;
            dicasUsadas[peca]++;
            salvar();
        }
    }

    // Quantas dicas do ET já foram usadas nesta peça
    public static int getDicasUsadas(int peca) {
        return dicasUsadas[peca];
    }

    // ================================================================ quiz

    // Quantas perguntas do quiz desta peça já foram acertadas (o quiz retoma na próxima)
    public static int getProgressoQuiz(int peca) {
        return progressoQuiz[peca];
    }

    public static void salvarProgressoQuiz(int peca, int acertadas) {
        progressoQuiz[peca] = acertadas;
        salvar();
    }

    // ================================================================ arquivo

    private static int inteiro(Properties p, String chave, int padrao) {
        try {
            return Integer.parseInt(p.getProperty(chave, String.valueOf(padrao)).trim());
        } catch (NumberFormatException e) {
            return padrao;
        }
    }

    private static void carregar() {
        boolean existe = ARQUIVO.exists();
        if (existe) {
            try (FileInputStream entrada = new FileInputStream(ARQUIVO)) {
                Properties p = new Properties();
                p.load(entrada);
                pecas = Math.max(0, Math.min(6, inteiro(p, "pecas", 0)));
                oxigenio = Math.max(0, Math.min(OXIGENIO_MAXIMO, inteiro(p, "oxigenio", OXIGENIO_MAXIMO)));
                dicas = Math.max(0, Math.min(DICAS_MAXIMAS, inteiro(p, "dicas", DICAS_MAXIMAS)));
                for (int f = 0; f < 3; f++) {
                    String[] par = p.getProperty("sorteio" + f, "-1,-1").split(",");
                    sorteio[f][0] = Integer.parseInt(par[0].trim());
                    sorteio[f][1] = Integer.parseInt(par[1].trim());
                    tentativa[f] = Math.max(1, inteiro(p, "tentativa" + f, 1));
                }
                for (int i = 0; i < 6; i++) {
                    semente[i] = Long.parseLong(p.getProperty("semente" + i, "0").trim());
                    progressoQuiz[i] = Math.max(0, inteiro(p, "quiz" + i, 0));
                    dicasUsadas[i] = Math.max(0, inteiro(p, "dicasUsadas" + i, 0));
                }
            } catch (Exception e) {
                pecas = 0;
                oxigenio = OXIGENIO_MAXIMO;
                dicas = DICAS_MAXIMAS;
            }
        }
        // primeiro jogo, arquivo antigo ou arquivo com defeito: sorteia o que estiver faltando
        boolean mudou = !existe;
        for (int f = 0; f < 3; f++) {
            if (!faseValida(f)) {
                sortearFase(f);
                mudou = true;
            }
        }
        // o jogador fechou o jogo na tela de game over (oxigênio zerado): a fase já volta recomeçada
        if (oxigenio <= 0) {
            if (getFase() <= 2) {
                reiniciarFase();
            } else {
                oxigenio = OXIGENIO_MAXIMO;
            }
            mudou = true;
        }
        if (mudou) {
            salvar();
        }
    }

    private static void salvar() {
        try (FileOutputStream saida = new FileOutputStream(ARQUIVO)) {
            Properties p = new Properties();
            p.setProperty("pecas", String.valueOf(pecas));
            p.setProperty("oxigenio", String.valueOf(oxigenio));
            p.setProperty("dicas", String.valueOf(dicas));
            for (int f = 0; f < 3; f++) {
                p.setProperty("sorteio" + f, sorteio[f][0] + "," + sorteio[f][1]);
                p.setProperty("tentativa" + f, String.valueOf(tentativa[f]));
            }
            for (int i = 0; i < 6; i++) {
                p.setProperty("semente" + i, String.valueOf(semente[i]));
                p.setProperty("quiz" + i, String.valueOf(progressoQuiz[i]));
                p.setProperty("dicasUsadas" + i, String.valueOf(dicasUsadas[i]));
            }
            p.store(saida, "Professor Rogerio: Fora de Orbita");
        } catch (Exception e) {
            System.out.println("Não foi possível salvar o progresso: " + e.getMessage());
        }
    }
}
