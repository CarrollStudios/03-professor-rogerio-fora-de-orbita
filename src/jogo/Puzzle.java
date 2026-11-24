package jogo;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

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

    // Erros cometidos (cada um gasta 1 de oxigênio; quem cuida disso é a CenaFase)
    private int errosPendentes = 0;

    // Dica do ET (só existe na última fase)
    private boolean dicasAtivas = false;
    private boolean dicaAberta = false;
    private int dicasUsadasAqui;      // começa com o que já foi usado nesta peça (o jogo lembra)
    private int numeroDica = 0;
    private String textoDica = "";
    private static final Rectangle BOTAO_DICA = new Rectangle(PX + PW - 262, PY + PH - 82, 232, 40);

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
        if (!dicaAberta) {      // enquanto o ET fala, o puzzle espera
            andar(dt);
        }
    }

    protected abstract void andar(double dt);

    protected abstract String instrucao();

    protected abstract void desenharConteudo(Graphics2D g);

    public void tecla(int codigo) {
    }

    public void clique(int x, int y) {
    }

    // ---- erros e oxigênio ----

    // Chame isto quando o jogador errar: a CenaFase tira 1 de oxigênio
    protected void errou() {
        errosPendentes++;
    }

    // A CenaFase pergunta quantos erros novos aconteceram (e zera a contagem)
    public final int coletarErros() {
        int n = errosPendentes;
        errosPendentes = 0;
        return n;
    }

    // ---- dicas do ET ----

    // Textos das dicas, da primeira para a última. Sem dicas = o puzzle não tem ajuda do ET.
    protected String[] dicas() {
        return new String[0];
    }

    // Efeito visual extra de cada dica (n = 0 para a primeira). Ex.: marcar o caminho no labirinto.
    protected void aplicarDica(int n) {
    }

    public void ativarDicas() {
        dicasAtivas = true;
        dicasUsadasAqui = Math.min(Progresso.getDicasUsadas(sistema.ordinal()), dicas().length);
    }

    private void pedirDica() {
        String[] d = dicas();
        if (!dicasAtivas || d.length == 0 || dicaAberta || resolvido) {
            return;
        }
        if (dicasUsadasAqui < d.length) {
            if (Progresso.getDicas() <= 0) {
                dizer("O ET não tem mais dicas!", VERMELHO);
                return;
            }
            Progresso.usouDica(sistema.ordinal());
            numeroDica = dicasUsadasAqui;
            dicasUsadasAqui++;
            aplicarDica(numeroDica);
        } else {
            numeroDica = d.length - 1;    // já mostrou tudo: repete a última, sem gastar
        }
        textoDica = d[numeroDica];
        dicaAberta = true;
        som();
    }

    // A CenaFase chama estes dois (e eles chamam tecla/clique do puzzle quando não é o ET falando)
    public final void teclaGeral(int codigo) {
        if (dicaAberta) {
            dicaAberta = false;
        } else if (dicasAtivas && codigo == KeyEvent.VK_H) {
            pedirDica();
        } else {
            tecla(codigo);
        }
    }

    public final void cliqueGeral(int x, int y) {
        if (dicaAberta) {
            dicaAberta = false;
        } else if (dicasAtivas && BOTAO_DICA.contains(x, y)) {
            pedirDica();
        } else {
            clique(x, y);
        }
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

        // oxigênio no canto de cima
        g.setFont(F_PEQUENA_B);
        Desenho.texto(g, "OXIGÊNIO", PX + PW - 150, PY + 26, new Color(170, 225, 255));
        Desenho.oxigenio(g, PX + PW - 150, PY + 38, Progresso.getOxigenio(), Progresso.OXIGENIO_MAXIMO, (int) (tempo * 1000));

        // botão de dica do ET
        if (dicasAtivas && dicas().length > 0) {
            boolean sobre = BOTAO_DICA.contains(mouseX, mouseY) && !dicaAberta;
            boolean acabou = Progresso.getDicas() <= 0 && dicasUsadasAqui < dicas().length;
            Desenho.caixa(g, BOTAO_DICA.x, BOTAO_DICA.y, BOTAO_DICA.width, BOTAO_DICA.height,
                    acabou ? new Color(40, 42, 60) : (sobre ? new Color(40, 120, 80) : new Color(26, 80, 60)),
                    acabou ? new Color(100, 100, 130) : new Color(120, 255, 160));
            Desenho.alien(g, BOTAO_DICA.x + 22, BOTAO_DICA.y + 36, 1);
            g.setFont(F_PEQUENA_B);
            Desenho.texto(g, "Dica do ET (H)  x" + Progresso.getDicas(), BOTAO_DICA.x + 42, BOTAO_DICA.y + 26,
                    acabou ? new Color(150, 150, 170) : Color.WHITE);
        }

        g.setFont(F_PEQUENA_B);
        if (tempoMensagem > 0) {
            Desenho.texto(g, mensagem, PX + 30, PY + PH - 20, corMensagem);
        }
        g.setFont(F_PEQUENA);
        Desenho.texto(g, "ESC: sair do puzzle", PX + PW - 190, PY + PH - 20, new Color(160, 160, 190));

        if (dicaAberta) {
            desenharDica(g);
        }
    }

    // O ET aparece com um balão de fala por cima do puzzle
    private void desenharDica(Graphics2D g) {
        int bx = PX + 60, by = PY + 150, bw = PW - 120, bh = 250;
        g.setColor(new Color(0, 0, 10, 150));
        g.fillRect(PX, PY, PW, PH);
        Desenho.caixa(g, bx, by, bw, bh, new Color(14, 30, 34, 252), new Color(120, 255, 160));
        Desenho.alien(g, bx + 90, by + bh - 24, 6);
        g.setFont(F_MEDIA);
        Desenho.texto(g, "ET diz: (dica " + (numeroDica + 1) + " de " + dicas().length + ")", bx + 190, by + 44,
                new Color(150, 255, 180));
        g.setFont(F_TEXTO);
        Desenho.paragrafo(g, textoDica, bx + 190, by + 88, bw - 220, 30, Color.WHITE);
        g.setFont(F_PEQUENA);
        Desenho.texto(g, "Clique ou aperte qualquer tecla para voltar ao desafio", bx + 190, by + bh - 20,
                new Color(190, 220, 200));
    }
}
