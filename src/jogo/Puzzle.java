package jogo;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * Base de todos os puzzles. Cada puzzle conserta UMA peça da nave.
 * Esta classe desenha a moldura (título, ícone, instrução e mensagem);
 * cada puzzle só desenha o seu próprio conteúdo em desenharConteudo().
 * Para criar um puzzle novo: faça uma classe que estenda Puzzle e
 * coloque ela em Puzzles.criar().
 */
public abstract class Puzzle {

    // Painel do puzzle na tela de 1280 x 720
    public static final int PX = 190, PY = 110, PW = 900, PH = 500;
    // Área onde cada puzzle desenha o conteúdo
    public static final int CX = PX + 30, CY = PY + 140, CW = PW - 60;

    protected static final Font F_GRANDE = new Font("SansSerif", Font.BOLD, 32);
    protected static final Font F_MEDIA = new Font("SansSerif", Font.BOLD, 24);
    protected static final Font F_TEXTO = new Font("SansSerif", Font.PLAIN, 22);
    protected static final Font F_PEQUENA = new Font("SansSerif", Font.PLAIN, 17);
    protected static final Font F_PEQUENA_B = new Font("SansSerif", Font.BOLD, 17);

    protected static final Color VERDE = new Color(120, 255, 160);
    protected static final Color VERMELHO = new Color(255, 120, 120);

    protected final SistemaNave sistema;
    protected boolean resolvido = false;
    protected double tempo = 0;
    protected int mouseX, mouseY;

    private String mensagem = "";
    private Color corMensagem = Color.WHITE;
    private double tempoMensagem = 0;

    protected Puzzle(SistemaNave sistema) {
        this.sistema = sistema;
    }

    // dt = segundos desde o último tique
    public final void atualizar(double dt) {
        tempo += dt;
        if (tempoMensagem > 0) {
            tempoMensagem -= dt;
        }
        andar(dt);
    }

    protected abstract void andar(double dt);

    protected abstract String instrucao();

    protected abstract void desenharConteudo(Graphics2D g);

    public void tecla(int codigo) {
    }

    public void clique(int x, int y) {
    }

    public boolean estaResolvido() {
        return resolvido;
    }

    protected void dizer(String texto, Color cor) {
        mensagem = texto;
        corMensagem = cor;
        tempoMensagem = 2.5;
    }

    protected void som() {
        Sons.tocar("/sons/efeitos/clique.wav");
    }

    public final void desenhar(Graphics2D g, int mx, int my) {
        mouseX = mx;
        mouseY = my;

        Desenho.caixa(g, PX, PY, PW, PH, new Color(12, 14, 40, 245), new Color(150, 130, 255));

        Desenho.imagemAjustada(g, sistema.imagem(), PX + 60, PY + 55, 72);
        g.setFont(F_GRANDE);
        Desenho.texto(g, "Consertar: " + sistema.nome, PX + 114, PY + 55, Color.WHITE);
        g.setFont(F_PEQUENA);
        Desenho.texto(g, sistema.subtitulo, PX + 116, PY + 82, new Color(190, 180, 255));

        g.setFont(F_PEQUENA_B);
        Desenho.paragrafo(g, instrucao(), PX + 30, PY + 118, PW - 60, 22, Desenho.AMARELO);

        desenharConteudo(g);

        g.setFont(F_PEQUENA_B);
        if (tempoMensagem > 0) {
            Desenho.texto(g, mensagem, PX + 30, PY + PH - 20, corMensagem);
        }
        g.setFont(F_PEQUENA);
        Desenho.texto(g, "ESC: sair do puzzle", PX + PW - 190, PY + PH - 20, new Color(160, 160, 190));
    }
}
