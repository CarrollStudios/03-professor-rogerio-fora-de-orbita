package jogo;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/**
 * Ajudante para tocar os sons da pasta resources/sons (arquivos .wav).
 * - Música de fundo: toca em loop (recomeça sozinha quando acaba).
 * - Efeito sonoro: toca uma vez (clique, porta, batida...).
 * Assim cada cena só pede "toca tal som", sem repetir código.
 * Feita por Gih (Front-end) - Carroll Studios.
 */
public class Sons {

    // Volume em decibéis: 0 = normal, números negativos = mais baixo
    private static final float VOLUME_MUSICA = -10f;
    private static final float VOLUME_EFEITO = -3f;

    // Cada som é carregado uma vez só e guardado aqui (mais rápido na próxima vez)
    private static Map<String, Clip> guardados = new HashMap<>();

    private static Clip musicaAtual;
    private static String nomeMusicaAtual = "";
    private static boolean mudo = false;   // true = som desligado pelo botão

    // Exemplo: Sons.tocar("/sons/efeitos/porta.wav");
    public static void tocar(String caminho) {
        if (mudo) {
            return;   // som desligado: não toca nada
        }
        Clip som = carregar(caminho, VOLUME_EFEITO);
        if (som != null) {
            som.stop();
            som.setFramePosition(0);   // volta para o começo do som
            som.start();
        }
    }

    // Exemplo: Sons.tocarMusica("/sons/musicas/musica-escola.wav");
    // Se essa música já estiver tocando, ela continua (não recomeça).
    public static void tocarMusica(String caminho) {
        if (caminho.equals(nomeMusicaAtual) && musicaAtual != null && (musicaAtual.isRunning() || mudo)) {
            return;
        }
        pararMusica();
        musicaAtual = carregar(caminho, VOLUME_MUSICA);
        if (musicaAtual != null) {
            nomeMusicaAtual = caminho;
            musicaAtual.setFramePosition(0);
            if (!mudo) {
                musicaAtual.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
    }

    // Botão de som: desliga tudo ou liga de novo (a música volta de onde parou)
    public static void alternarMudo() {
        mudo = !mudo;
        if (musicaAtual != null) {
            if (mudo) {
                musicaAtual.stop();
            } else {
                musicaAtual.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
    }

    public static boolean estaMudo() {
        return mudo;
    }

    public static void pararMusica() {
        if (musicaAtual != null) {
            musicaAtual.stop();
        }
        nomeMusicaAtual = "";
    }

    // Abre o arquivo de som e deixa pronto para tocar
    private static Clip carregar(String caminho, float volume) {

        if (guardados.containsKey(caminho)) {
            return guardados.get(caminho);
        }
        try {
            URL arquivo = Sons.class.getResource(caminho);
            AudioInputStream audio = AudioSystem.getAudioInputStream(arquivo);
            Clip som = AudioSystem.getClip();
            som.open(audio);
            if (som.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl controle = (FloatControl) som.getControl(FloatControl.Type.MASTER_GAIN);
                controle.setValue(Math.max(controle.getMinimum(), volume));
            }
            guardados.put(caminho, som);
            return som;

        } catch (Exception e) {
            // se o computador não tiver som, o jogo continua funcionando (só fica mudo)
            System.out.println("Erro ao carregar som: " + caminho);
            guardados.put(caminho, null);
            return null;
        }
    }
}
