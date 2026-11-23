package jogo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Guarda o progresso da Zoe: quantas peças da nave ela já recuperou (de 0 a 6),
 * quanto oxigênio ainda tem no capacete e quantas dicas do ET ainda restam.
 * Tudo é salvo num arquivo na pasta do usuário, então continua valendo
 * mesmo depois de fechar o jogo.
 * Fase 1 = peças 1 e 2, fase 2 = peças 3 e 4, fase 3 = peças 5 e 6.
 *
 * OXIGÊNIO: a Zoe começa cada puzzle com OXIGENIO_MAXIMO. Cada erro gasta 1, e cada peça
 * recuperada troca o tanque (enche de novo). Se chegar a zero, é game over (o jogo recomeça do início).
 *
 * SORTEIO: cada fase tem PUZZLES_NO_SORTEIO puzzles possíveis, mas só 2 são sorteados (um por peça).
 * O sorteio é feito quando o jogo começa do zero e fica salvo, então recomeçar depois de um game over
 * traz puzzles diferentes.
 */
public class Progresso {

    // Quantos "tanques" de oxigênio a Zoe tem em cada fase (mude aqui se quiser mais ou menos)
    public static final int OXIGENIO_MAXIMO = 4;
    // Quantos puzzles existem por fase (veja Puzzles.java) e quantos são sorteados (um por peça da fase)
    public static final int PUZZLES_NO_SORTEIO = 6;

    // Quantas dicas o ET dá na última fase (no total)
    public static final int DICAS_MAXIMAS = 2;

    private static final File ARQUIVO = new File(System.getProperty("user.home"), "professor-rogerio-progresso.properties");

    private static int pecas = 0;                       // quantas peças já foram recuperadas
    private static int oxigenio = OXIGENIO_MAXIMO;      // oxigênio que sobrou
    private static int dicas = DICAS_MAXIMAS;           // dicas do ET que sobraram
    private static final int[][] sorteio = new int[3][2];   // [fase][peça da fase] = puzzle sorteado

    static {
        carregar();
        if (!sorteioValido()) {      // primeiro jogo (ou arquivo antigo): sorteia agora
            sortear();
            salvar();
        }
    }

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

    // Chamado quando uma peça é recuperada. O tanque de oxigênio é trocado (enche de novo).
    public static void recuperouPeca() {
        if (pecas < 6) {
            pecas++;
            oxigenio = OXIGENIO_MAXIMO;
            salvar();
        }
    }

    // Qual puzzle (0 a 4) foi sorteado para a peça 'j' (0 ou 1) da fase 'fase' (0 a 2)
    public static int getSorteio(int fase, int j) {
        return sorteio[fase][j];
    }

    // Sorteia 2 puzzles diferentes, entre os 5 possíveis, para cada fase
    private static void sortear() {
        for (int f = 0; f < 3; f++) {
            List<Integer> todos = new ArrayList<>();
            for (int i = 0; i < PUZZLES_NO_SORTEIO; i++) {
                todos.add(i);
            }
            Collections.shuffle(todos);
            sorteio[f][0] = todos.get(0);
            sorteio[f][1] = todos.get(1);
        }
    }

    private static boolean sorteioValido() {
        for (int f = 0; f < 3; f++) {
            int a = sorteio[f][0], b = sorteio[f][1];
            if (a < 0 || b < 0 || a >= PUZZLES_NO_SORTEIO || b >= PUZZLES_NO_SORTEIO || a == b) {
                return false;
            }
        }
        return true;
    }

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

    public static void usouDica() {
        if (dicas > 0) {
            dicas--;
            salvar();
        }
    }

    // Recomeça tudo do zero (botão Recomeçar do game over, ou R duas vezes no mapa)
    public static void zerar() {
        pecas = 0;
        oxigenio = OXIGENIO_MAXIMO;
        dicas = DICAS_MAXIMAS;
        sortear();
        salvar();
    }

    private static void carregar() {
        if (!ARQUIVO.exists()) {
            return;
        }
        try (FileInputStream entrada = new FileInputStream(ARQUIVO)) {
            Properties p = new Properties();
            p.load(entrada);
            pecas = Math.max(0, Math.min(6, Integer.parseInt(p.getProperty("pecas", "0"))));
            oxigenio = Math.max(1, Math.min(OXIGENIO_MAXIMO,
                    Integer.parseInt(p.getProperty("oxigenio", String.valueOf(OXIGENIO_MAXIMO)))));
            dicas = Math.max(0, Math.min(DICAS_MAXIMAS, Integer.parseInt(p.getProperty("dicas", String.valueOf(DICAS_MAXIMAS)))));
            for (int f = 0; f < 3; f++) {
                String[] par = p.getProperty("sorteio" + f, "-1,-1").split(",");
                sorteio[f][0] = Integer.parseInt(par[0].trim());
                sorteio[f][1] = Integer.parseInt(par[1].trim());
            }
        } catch (Exception e) {
            pecas = 0;
            oxigenio = OXIGENIO_MAXIMO;
            dicas = DICAS_MAXIMAS;
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
            }
            p.store(saida, "Professor Rogerio: Fora de Orbita");
        } catch (Exception e) {
            System.out.println("Não foi possível salvar o progresso: " + e.getMessage());
        }
    }
}
