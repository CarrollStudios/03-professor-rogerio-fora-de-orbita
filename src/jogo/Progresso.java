package jogo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Guarda o progresso do jogador: quantas peças da nave ele já recuperou (de 0 a 6).
 * O progresso é salvo num arquivo na pasta do usuário, então continua valendo
 * mesmo depois de fechar o jogo.
 * Fase 1 = peças 1 e 2, fase 2 = peças 3 e 4, fase 3 = peças 5 e 6.
 */
public class Progresso {

    private static final File ARQUIVO = new File(System.getProperty("user.home"), "professor-rogerio-progresso.properties");

    // quantas peças já foram recuperadas
    private static int pecas = 0;

    static {
        carregar();
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

    // Chamado quando uma peça é recuperada
    public static void recuperouPeca() {
        if (pecas < 6) {
            pecas++;
            salvar();
        }
    }

    public static void zerar() {
        pecas = 0;
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
        } catch (Exception e) {
            pecas = 0;
        }
    }

    private static void salvar() {
        try (FileOutputStream saida = new FileOutputStream(ARQUIVO)) {
            Properties p = new Properties();
            p.setProperty("pecas", String.valueOf(pecas));
            p.store(saida, "Professor Rogerio: Fora de Orbita");
        } catch (Exception e) {
            System.out.println("Não foi possível salvar o progresso: " + e.getMessage());
        }
    }
}
